import json
import logging
from typing import List, Dict, Any, Optional
from datetime import datetime, timedelta
import uuid

# We will use Qdrant for both vector-based RAG and persistent sliding window memory.
from qdrant_client import QdrantClient
from qdrant_client.http.models import Distance, VectorParams, PointStruct, Filter, FieldCondition, MatchValue, Range
from langgraph.checkpoint.memory import MemorySaver

logger = logging.getLogger(__name__)

# Global Short-Term Memory Checkpointer (RAM based, temporary during a chat)
shared_memory = MemorySaver()

# Initialize local filesystem Qdrant database with fallback for concurrent access
try:
    client = QdrantClient(path="./qdrant_storage")
except Exception as e:
    logger.warning(f"Could not open ./qdrant_storage ({e}), falling back to in-memory Qdrant client.")
    client = QdrantClient(":memory:")

LONG_TERM_MEMORY_COLLECTION = "long_term_chat_memory"
KNOWLEDGE_BASE_COLLECTION = "upyog_knowledge_base"
DRAFT_STATE_COLLECTION = "draft_states"
VECTOR_SIZE = 768  # 768 dimensions used by the embedding model

# Creates missing Qdrant database collections on startup if they don't already exist
def init_collections():
    try:
        collections = client.get_collections().collections
        collection_names = [c.name for c in collections]
        
        if LONG_TERM_MEMORY_COLLECTION not in collection_names:
            logger.info(f"Creating {LONG_TERM_MEMORY_COLLECTION} collection.")
            client.create_collection(
                collection_name=LONG_TERM_MEMORY_COLLECTION,
                vectors_config=VectorParams(size=VECTOR_SIZE, distance=Distance.COSINE),
            )
            client.create_payload_index(
                collection_name=LONG_TERM_MEMORY_COLLECTION,
                field_name="timestamp",
                field_schema="integer"
            )
            
        if KNOWLEDGE_BASE_COLLECTION not in collection_names:
            logger.info(f"Creating {KNOWLEDGE_BASE_COLLECTION} collection.")
            client.create_collection(
                collection_name=KNOWLEDGE_BASE_COLLECTION,
                vectors_config=VectorParams(size=VECTOR_SIZE, distance=Distance.COSINE),
            )
            
        if DRAFT_STATE_COLLECTION not in collection_names:
            logger.info(f"Creating {DRAFT_STATE_COLLECTION} collection.")
            client.create_collection(
                collection_name=DRAFT_STATE_COLLECTION,
                vectors_config=VectorParams(size=1, distance=Distance.COSINE),
            )
            client.create_payload_index(
                collection_name=DRAFT_STATE_COLLECTION,
                field_name="phone_number",
                field_schema="keyword"
            )
            client.create_payload_index(
                collection_name=DRAFT_STATE_COLLECTION,
                field_name="plugin_name",
                field_schema="keyword"
            )
    except Exception as e:
        logger.error(f"Error initializing Qdrant collections: {e}")

init_collections()

class MemoryManager:
    """
    ====================================================================
    [THE BRAIN ARCHIVE & DRAFT MANAGER]
    Manages Long-Term Memory and Module-Isolated Draft States using Qdrant.
    Supports multi-draft storage per user anchored by composite key (phone:plugin).
    ====================================================================
    """
    
    # Automatically deletes chat history older than 30 days
    @staticmethod
    def _enforce_sliding_window(phone_number: str):
        cutoff_date = datetime.now() - timedelta(days=30)
        cutoff_timestamp = int(cutoff_date.timestamp())
        
        try:
            client.delete(
                collection_name=LONG_TERM_MEMORY_COLLECTION,
                points_selector=Filter(
                    must=[
                        FieldCondition(
                            key="phone_number",
                            match=MatchValue(value=phone_number)
                        ),
                        FieldCondition(
                            key="timestamp",
                            range=Range(lt=cutoff_timestamp)
                        )
                    ]
                )
            )
            logger.info(f"Enforced sliding window. Deleted records older than {cutoff_date} for {phone_number}.")
        except Exception as e:
            logger.error(f"Error enforcing sliding window: {e}")

    # Permanently saves a single chat message into Qdrant long-term database
    @staticmethod
    def save_long_term_interaction(phone_number: str, role: str, content: str, embedding: Optional[List[float]] = None):
        if not embedding:
            embedding = [0.0] * VECTOR_SIZE
            
        point_id = str(uuid.uuid4())
        timestamp = int(datetime.now().timestamp())
        
        point = PointStruct(
            id=point_id,
            vector=embedding,
            payload={
                "phone_number": phone_number,
                "role": role,
                "content": content,
                "timestamp": timestamp,
                "date_str": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            }
        )
        
        try:
            client.upsert(
                collection_name=LONG_TERM_MEMORY_COLLECTION,
                points=[point]
            )
            MemoryManager._enforce_sliding_window(phone_number)
            return True
        except Exception as e:
            logger.error(f"Error saving to long term memory: {e}")
            return False

    # Dynamic Multi-Draft Saving: Isolated per module (phone_number:plugin_name)
    @staticmethod
    def save_draft_state(phone_number: str, plugin_name: str, draft_data: dict) -> None:
        """
        Saves or updates a draft specifically for (phone_number, plugin_name).
        Does NOT overwrite drafts of other modules!
        """
        logger.info(f"[MemoryManager.save_draft_state] Saving draft for phone={phone_number}, plugin={plugin_name}, keys={list(draft_data.keys()) if isinstance(draft_data, dict) else []}")
        try:
            # Check for existing draft point for THIS specific phone + plugin combination
            records, _ = client.scroll(
                collection_name=DRAFT_STATE_COLLECTION,
                scroll_filter=Filter(
                    must=[
                        FieldCondition(key="phone_number", match=MatchValue(value=phone_number)),
                        FieldCondition(key="plugin_name", match=MatchValue(value=plugin_name))
                    ]
                ),
                limit=10
            )
            if records:
                point_ids = [record.id for record in records]
                client.delete(
                    collection_name=DRAFT_STATE_COLLECTION,
                    points_selector=point_ids
                )
                logger.debug(f"[MemoryManager.save_draft_state] Deleted {len(point_ids)} previous draft points for {phone_number}:{plugin_name}")
            
            point_id = str(uuid.uuid4())
            payload = {
                "draft_id": f"{phone_number}:{plugin_name}",
                "phone_number": phone_number,
                "plugin_name": plugin_name,
                "draft_data": draft_data,
                "timestamp": int(datetime.utcnow().timestamp())
            }
            
            client.upsert(
                collection_name=DRAFT_STATE_COLLECTION,
                points=[PointStruct(id=point_id, vector=[0.0], payload=payload)]
            )
            logger.info(f"[MemoryManager.save_draft_state] Successfully saved isolated draft [{plugin_name}] for user {phone_number}")
        except Exception as e:
            logger.error(f"[MemoryManager.save_draft_state] Error saving draft state for {phone_number}/{plugin_name}: {e}")

    # Retrieves all active drafts for a citizen across all modules
    @staticmethod
    def get_all_draft_states(phone_number: str) -> List[Dict[str, Any]]:
        """
        Retrieves all active drafts for a citizen to allow dynamic draft switching.
        """
        logger.info(f"[MemoryManager.get_all_draft_states] Fetching all drafts for phone={phone_number}")
        try:
            records, _ = client.scroll(
                collection_name=DRAFT_STATE_COLLECTION,
                scroll_filter=Filter(
                    must=[FieldCondition(key="phone_number", match=MatchValue(value=phone_number))]
                ),
                limit=20,
                with_payload=True
            )
            if records:
                drafts = [r.payload for r in records]
                logger.info(f"[MemoryManager.get_all_draft_states] Found {len(drafts)} drafts for {phone_number}: {[d.get('plugin_name') for d in drafts]}")
                return drafts
            logger.info(f"[MemoryManager.get_all_draft_states] No active drafts found for {phone_number}")
        except Exception as e:
            logger.error(f"[MemoryManager.get_all_draft_states] Error retrieving all draft states for {phone_number}: {e}")
        return []

    # Retrieves a specific draft form or the latest saved draft
    @staticmethod
    def get_draft_state(phone_number: str, plugin_name: Optional[str] = None) -> Optional[dict]:
        """
        Fetches the active draft for a specific module (plugin_name) or the latest draft.
        """
        logger.info(f"[MemoryManager.get_draft_state] Fetching draft for phone={phone_number}, plugin={plugin_name}")
        try:
            must_conditions = [FieldCondition(key="phone_number", match=MatchValue(value=phone_number))]
            if plugin_name:
                must_conditions.append(FieldCondition(key="plugin_name", match=MatchValue(value=plugin_name)))
                
            records, _ = client.scroll(
                collection_name=DRAFT_STATE_COLLECTION,
                scroll_filter=Filter(must=must_conditions),
                limit=1,
                with_payload=True
            )
            
            if records:
                logger.info(f"[MemoryManager.get_draft_state] Found draft for {phone_number}/{plugin_name}")
                return records[0].payload
            logger.info(f"[MemoryManager.get_draft_state] No draft found for {phone_number}/{plugin_name}")
        except Exception as e:
            logger.error(f"[MemoryManager.get_draft_state] Error retrieving draft state for {phone_number}/{plugin_name}: {e}")
        return None

    # Deletes a specific module draft after successful submission or cancellation
    @staticmethod
    def delete_draft_state(phone_number: str, plugin_name: Optional[str] = None) -> None:
        """
        Deletes the draft for a specific module or all drafts if plugin_name is None.
        """
        logger.info(f"[MemoryManager.delete_draft_state] Deleting draft(s) for phone={phone_number}, plugin={plugin_name}")
        try:
            must_conditions = [FieldCondition(key="phone_number", match=MatchValue(value=phone_number))]
            if plugin_name:
                must_conditions.append(FieldCondition(key="plugin_name", match=MatchValue(value=plugin_name)))

            records, _ = client.scroll(
                collection_name=DRAFT_STATE_COLLECTION,
                scroll_filter=Filter(must=must_conditions),
                limit=100,
                with_payload=False,
                with_vectors=False
            )
            if records:
                point_ids = [record.id for record in records]
                client.delete(
                    collection_name=DRAFT_STATE_COLLECTION,
                    points_selector=point_ids
                )
                logger.info(f"[MemoryManager.delete_draft_state] Deleted {len(point_ids)} draft point(s) [{plugin_name or 'all'}] for {phone_number}")
            else:
                logger.info(f"[MemoryManager.delete_draft_state] No draft points found to delete for {phone_number}/{plugin_name}")
        except Exception as e:
            logger.error(f"[MemoryManager.delete_draft_state] Error deleting draft state for {phone_number}/{plugin_name}: {e}")

    # Fetches recent chat history
    @staticmethod
    def get_recent_history(phone_number: str, limit: int = 15) -> List[Dict[str, Any]]:
        try:
            results, _ = client.scroll(
                collection_name=LONG_TERM_MEMORY_COLLECTION,
                scroll_filter=Filter(
                    must=[
                        FieldCondition(
                            key="phone_number",
                            match=MatchValue(value=phone_number)
                        )
                    ]
                ),
                limit=limit,
                with_payload=True,
                with_vectors=False
            )
            history = [r.payload for r in results]
            history.sort(key=lambda x: x.get("timestamp", 0))
            return history
        except Exception as e:
            logger.error(f"Error retrieving recent history: {e}")
            return []
        
    # Searches long-term vector memory
    @staticmethod
    def search_long_term_memory(phone_number: str, query_embedding: List[float], limit: int = 3) -> List[Dict[str, Any]]:
        try:
            res = client.query_points(
                collection_name=LONG_TERM_MEMORY_COLLECTION,
                query=query_embedding,
                query_filter=Filter(
                    must=[
                        FieldCondition(
                            key="phone_number",
                            match=MatchValue(value=phone_number)
                        )
                    ]
                ),
                limit=limit
            )
            history = [hit.payload for hit in res.points if hit.score > 0.4]
            history.sort(key=lambda x: x.get("timestamp", 0))
            return history
        except Exception as e:
            logger.error(f"Error searching long term memory: {e}")
            return []

    # Saves a summary of a completed booking permanently in chat history
    @staticmethod
    def save_booking_record(phone_number: str, booking_data: dict):
        content = f"BOOKING RECORD: ID {booking_data.get('bookingNo')}, Type {booking_data.get('addType')}, Status {booking_data.get('status', 'BOOKING_CREATED')}"
        MemoryManager.save_long_term_interaction(phone_number, "system_record", content)

    # Searches official UPYOG knowledge base
    @staticmethod
    def search_knowledge_base(embedding: List[float], limit: int = 3) -> str:
        try:
            results = client.search(
                collection_name=KNOWLEDGE_BASE_COLLECTION,
                query_vector=embedding,
                limit=limit
            )
            context = []
            for r in results:
                if r.score > 0.7:
                    prompt = r.payload.get("prompt", "")
                    response = r.payload.get("response", "")
                    context.append(f"Q: {prompt}\nA: {response}")
            return "\n\n".join(context)
        except Exception as e:
            logger.error(f"Error searching knowledge base: {e}")
            return ""
