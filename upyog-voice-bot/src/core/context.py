"""
src/core/context.py — UserContext: Single Source of Truth for Conversation State
=================================================================================
Replaces: pending_interruptions (RAM dict), adv_sessions (RAM dict),
          LangGraph MemorySaver per-user state (RAM)

Storage:  Redis (key: ctx:{phone}) — survives server restarts & gunicorn workers
TTL:      1 hour (auto-cleared by Redis)

Usage:
    ctx = UserContext.load(phone)
    ctx.set_module("advertisement")
    ctx.update_draft("addType", "Hoarding")
    ctx.save()

Multi-Draft:
    ctx.set_pending_drafts(drafts)
    ctx.resolve_draft_selection(0)

Fresh Start:
    ctx.reset_workflow()    # clears module/step/draft, keeps phone/language
    ctx.clear()             # full delete (end conversation)
"""

import json
import time
import logging
from dataclasses import dataclass, field, asdict
from typing import Optional, Dict, Any, List

logger = logging.getLogger(__name__)


def _get_redis():
    from database import r_client
    return r_client


_PLUGIN_TO_MODULE = {
    "adv_booking": "advertisement",
    "grievance": "grievance",
}
_MODULE_TO_PLUGIN = {v: k for k, v in _PLUGIN_TO_MODULE.items()}


@dataclass
class UserContext:
    """
    Single source of truth for all per-user conversation state.
    Redis key: ctx:{phone}  |  TTL: 1 hour
    """

    # Identity
    phone: str
    session_id: str = ""

    # Active service context
    module: Optional[str] = None      # "advertisement" | "grievance" | None
    workflow: Optional[str] = None    # plugin key e.g. "adv_booking" | None
    step: Optional[str] = None        # current field step e.g. "addType" | None

    # Form draft — user data + internal keys (prefixed _)
    draft: Dict[str, Any] = field(default_factory=dict)

    # Language & intent
    language: str = "en"
    intent: Optional[str] = None

    # Timestamps
    created_at: float = field(default_factory=time.time)
    last_updated: float = field(default_factory=time.time)

    # =========================================================================
    # Redis Persistence
    # =========================================================================

    def save(self, ttl: int = 3600) -> bool:
        """Persist context to Redis with TTL (default 1 hour)."""
        try:
            self.last_updated = time.time()
            _get_redis().set(
                f"ctx:{self.phone}",
                json.dumps(asdict(self)),
                ex=ttl
            )
            logger.debug(
                f"[Context] Saved ctx:{self.phone} | "
                f"module={self.module} step={self.step} intent={self.intent}"
            )
            return True
        except Exception as e:
            logger.error(f"[Context] Failed to save for {self.phone}: {e}")
            return False

    @classmethod
    def load(cls, phone: str, session_id: str = "") -> "UserContext":
        """Load context from Redis. Returns fresh context if not found."""
        try:
            raw = _get_redis().get(f"ctx:{phone}")
            if raw:
                if isinstance(raw, bytes):
                    raw = raw.decode("utf-8")
                data = json.loads(raw)
                valid_keys = set(cls.__dataclass_fields__)
                filtered = {k: v for k, v in data.items() if k in valid_keys}
                ctx = cls(**filtered)
                logger.debug(
                    f"[Context] Loaded ctx:{phone} | "
                    f"module={ctx.module} step={ctx.step} intent={ctx.intent}"
                )
                return ctx
        except Exception as e:
            logger.warning(f"[Context] Could not load ctx:{phone}: {e}. Starting fresh.")
        return cls(phone=phone, session_id=session_id)

    def clear(self) -> bool:
        """Fully delete this context from Redis (use on end-conversation)."""
        try:
            _get_redis().delete(f"ctx:{self.phone}")
            logger.info(f"[Context] Cleared ctx:{self.phone}")
            return True
        except Exception as e:
            logger.error(f"[Context] Failed to clear for {self.phone}: {e}")
            return False

    # =========================================================================
    # Workflow State Management
    # =========================================================================

    def set_module(self, module: str, workflow: str = None):
        """Set active service module. Resets step & draft if module changed."""
        if self.module != module:
            self.step = None
            self.draft = {}
        self.module = module
        if workflow:
            self.workflow = workflow
        self.save()

    def set_plugin(self, plugin_name: str):
        """Set module from plugin_name (e.g. 'adv_booking' -> module='advertisement')."""
        module = _PLUGIN_TO_MODULE.get(plugin_name, plugin_name)
        self.module = module
        self.workflow = plugin_name
        self.save()

    @property
    def plugin_name(self) -> Optional[str]:
        """Return the LangGraph workflow key for the active module."""
        if self.workflow:
            return self.workflow
        if self.module:
            return _MODULE_TO_PLUGIN.get(self.module)
        return None

    def update_draft(self, field_name: str, value: Any):
        """Update a single draft field and persist."""
        self.draft[field_name] = value
        self.save()

    def advance_step(self, next_step: str):
        """Move to the next step in the form flow."""
        self.step = next_step
        self.save()

    def missing_fields(self, required: List[str]) -> List[str]:
        """Return which required fields are not yet in the draft."""
        return [f for f in required if not self.draft.get(f)]

    # =========================================================================
    # Multi-Draft Selection (replaces pending_interruptions dict)
    # =========================================================================

    def set_pending_drafts(self, drafts: List[Dict[str, Any]]):
        """
        Store multi-draft selection state.
        Called when user asks 'show drafts' and there are multiple active ones.
        Replaces: pending_interruptions[phone] = {"status": "awaiting_multi_draft_choice", ...}
        """
        self.intent = "awaiting_draft_selection"
        self.draft["_pending_drafts"] = drafts
        self.save()
        logger.info(f"[Context] Stored {len(drafts)} pending drafts for {self.phone}")

    @property
    def has_pending_draft_selection(self) -> bool:
        """True when multi-draft menu has been shown and user hasn't selected yet."""
        return (
            self.intent == "awaiting_draft_selection"
            and "_pending_drafts" in self.draft
        )

    @property
    def pending_drafts(self) -> List[Dict[str, Any]]:
        """Return the list of pending drafts stored for selection."""
        return self.draft.get("_pending_drafts", [])

    def resolve_draft_selection(self, selected_idx: int) -> Optional[Dict[str, Any]]:
        """
        User selected a draft by index (0-based).
        Restores that draft as the active context and clears selection state.
        Returns the selected draft dict, or None on invalid index.
        """
        drafts = self.pending_drafts
        if not drafts:
            logger.warning(f"[Context] No pending drafts to resolve for {self.phone}")
            return None

        if 0 <= selected_idx < len(drafts):
            selected = drafts[selected_idx]
            plugin = selected.get("plugin_name", "")
            draft_data = selected.get("draft_data", {})

            # Restore selected draft into active context
            self.module = _PLUGIN_TO_MODULE.get(plugin, plugin)
            self.workflow = plugin
            self.draft = dict(draft_data)   # restore form fields (no _pending_drafts)
            self.intent = None              # clear selection state
            self.save()
            logger.info(
                f"[Context] Draft restored: plugin={plugin} for {self.phone}. "
                f"Fields: {list(draft_data.keys())}"
            )
            return selected

        logger.warning(f"[Context] Invalid selection idx={selected_idx}, drafts={len(drafts)}")
        return None

    def cancel_draft_selection(self):
        """User chose 'Start New Service Request' from the draft menu."""
        self.intent = None
        self.draft.pop("_pending_drafts", None)
        self.module = None
        self.workflow = None
        self.step = None
        self.save()
        logger.info(f"[Context] Draft selection cancelled for {self.phone} — fresh start")

    # =========================================================================
    # Fresh Start
    # =========================================================================

    def reset_workflow(self):
        """
        Clear active service state — keeps phone, session_id, language.
        Use when: user says 'cancel', 'start new', 'naya form chahiye'.
        """
        self.module = None
        self.workflow = None
        self.step = None
        self.draft = {}
        self.intent = None
        self.save()
        logger.info(f"[Context] Workflow reset for {self.phone}")

    def start_fresh(self):
        """Alias for reset_workflow() with semantic clarity."""
        self.reset_workflow()

    # =========================================================================
    # Computed Properties
    # =========================================================================

    @property
    def has_active_workflow(self) -> bool:
        """True if user is mid-form with at least one non-internal field filled."""
        user_fields = {
            k: v for k, v in self.draft.items()
            if not k.startswith("_") and v is not None
        }
        return self.module is not None and bool(user_fields)

    @property
    def draft_summary(self) -> str:
        """Human-readable summary of current collected fields."""
        fields = {
            k: v for k, v in self.draft.items()
            if not k.startswith("_") and v is not None
        }
        if not fields:
            return "No fields collected yet."
        return "\n".join(
            f"• {k.replace('_', ' ').title()}: {v}" for k, v in fields.items()
        )

    def __repr__(self):
        return (
            f"UserContext(phone={self.phone!r}, module={self.module!r}, "
            f"step={self.step!r}, "
            f"draft_fields={[k for k in self.draft if not k.startswith('_')]}, "
            f"intent={self.intent!r})"
        )
