from typing import TypedDict, Annotated, Sequence, Dict, Any
import operator
from langchain_core.messages import BaseMessage

class BaseAgentState(TypedDict):
    """
    Universal state fields required by all service workflows.
    This acts as a universal "ID Card" that travels along with every message in the system.
    Any new plugin (e.g., Property Tax, Water Bill) will automatically inherit these core fields.
    """
    # The Chat History: operator.add ensures new messages are appended to the end of the list
    messages: Annotated[Sequence[BaseMessage], operator.add]
    
    # The user's 10-digit mobile number, used to identify the user across the system
    phone_number: str
    
    # The temporary Session ID, used to track users who haven't logged in with a phone number
    session_id: str
    
    # The Active Module they are using (e.g., 'adv_booking', 'property_tax'). Used for routing.
    active_service: str
    
    # Their Login Profile & Auth Tokens, stored as a dictionary so APIs can access the UUID/auth_token instantly
    user_profile: Dict[str, Any]
