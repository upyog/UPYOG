import json
import logging
from typing import List, Dict, Any, Optional
from datetime import datetime, timedelta
import uuid

# We will use Qdrant for both vector-based RAG and persistent sliding window memory.
# Qdrant converts text into mathematical dimensions (vectors) so the AI can perform semantic searches.
from qdrant_client import QdrantClient
from qdrant_client.http.models import Distance, VectorParams, PointStruct, Filter, FieldCondition, MatchValue, Range
from langgraph.checkpoint.memory import MemorySaver

logger = logging.getLogger(__name__)

# Global Short-Term Memory Checkpointer (RAM based, temporary during a chat)
shared_memory = MemorySaver()

# Initialize local filesystem Qdrant database for permanent storage
client = QdrantClient(path="./qdrant_storage")

LONG_TERM_MEMORY_COLLECTION = "long_term_chat_memory"
KNOWLEDGE_BASE_COLLECTION = "upyog_knowledge_base"
VECTOR_SIZE = 768  # 768 dimensions used by the embedding model to mathematically represent sentences

# Ensure collections exist
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
            # Create a payload index on timestamp for sliding window queries
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
    except Exception as e:
        logger.error(f"Error initializing Qdrant collections: {e}")

init_collections()

class MemoryManager:
    """
    ====================================================================
    [THE BRAIN ARCHIVE]
    Manages Long-Term Memory using Qdrant with a 30-Day Sliding Window.
    Instead of bloating a SQL database with chat history, we store JSON 
    payloads alongside vectors. 
    ====================================================================
    """
    
    @staticmethod
    def _enforce_sliding_window(phone_number: str):
        """
        [GARBAGE COLLECTION: 30-DAY WINDOW]
        Deletes records older than 30 days for a specific user to prevent database bloat.
        Day 31 overwrites Day 1 conceptually. This keeps the AI's memory fresh and fast.
        """
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



    
     # Saves a single conversation turn (or generated summary) into Qdrant permanently.
    # Called when 20 messages are reached in short-term memory, converted to a 2-line summary.
        
    @staticmethod
    def save_long_term_interaction(phone_number: str, role: str, content: str, embedding: Optional[List[float]] = None):
        
        if not embedding:
            # If no embedding is provided, just create a dummy vector so we can store the payload.
            # In a real scenario, you'd embed the content here if you want semantic search over history.
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
            # Enforce eviction policy
            MemoryManager._enforce_sliding_window(phone_number)
            return True
        except Exception as e:
            logger.error(f"Error saving to long term memory: {e}")
            return False

    @staticmethod
    def get_recent_history(phone_number: str, limit: int = 15) -> List[Dict[str, Any]]:
        """Retrieves recent chat history for context."""
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
            
            # Sort by timestamp ascending
            history = [r.payload for r in results]
            history.sort(key=lambda x: x.get("timestamp", 0))
            return history
        except Exception as e:
            logger.error(f"Error retrieving recent history: {e}")
            return []
        
      """
        [AI MAGIC: SEMANTIC SEARCH]
        Searches Qdrant for semantically relevant past interactions.
        Unlike SQL exact-match (`WHERE text='hotel'`), this matches mathematically 
        similar text (e.g. 'hotel ad' matches 'renew hotel ad').
        Returns results with a Cosine Similarity score > 0.4.
     """
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
            
            # Sort by timestamp ascending for chronological order
            history = [hit.payload for hit in res.points if hit.score > 0.4] # threshold for relevance
            history.sort(key=lambda x: x.get("timestamp", 0))
            return history
        except Exception as e:
            logger.error(f"Error searching long term memory: {e}")
            return []

    @staticmethod
    def save_booking_record(phone_number: str, booking_data: dict):
        """
        Saves a finalized booking record to long-term memory.
        We serialize the booking as a string to store as 'content'.
        """
        content = f"BOOKING RECORD: ID {booking_data.get('bookingNo')}, Type {booking_data.get('addType')}, Status {booking_data.get('status', 'BOOKING_CREATED')}"
        MemoryManager.save_long_term_interaction(phone_number, "system_record", content)
        # We can also keep storing it in Redis if needed for the exact JSON structure,
        # but this logs it chronologically in Qdrant.


        """
        [RAG KNOWLEDGE BASE QUERY]
        Queries the UPYOG/NUDM knowledge base (e.g., official government rules and FAQs).
        
        CRITICAL: Uses a stricter threshold (0.7) to prevent the AI from "hallucinating" 
        or guessing government policies. If the similarity is low, it means we don't 
        have the official answer, and the bot should say "I don't know".
        """

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
                if r.score > 0.7:  # similarity threshold
                    prompt = r.payload.get("prompt", "")
                    response = r.payload.get("response", "")
                    context.append(f"Q: {prompt}\nA: {response}")
            return "\n\n".join(context)
        except Exception as e:
            logger.error(f"Error searching knowledge base: {e}")
            return ""
