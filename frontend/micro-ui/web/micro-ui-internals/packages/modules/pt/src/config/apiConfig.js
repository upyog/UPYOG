// API Configuration
export const API_BASE_URL = "https://cpgram.pgrdigit.in/mnptb/api";

// Specific endpoints
export const API_ENDPOINTS = {
    GRIEVANCE_SEARCH: `${API_BASE_URL}/cpgram-application-service/grievance/search`,
    TRANSCRIBE: `${API_BASE_URL}/aws/transcribe`,
    TRANSLATE: `${API_BASE_URL}/aws/translate`,
    QUERY_SEARCH: `${API_BASE_URL}/cpgram-application-service/querysearch`,
    DEPT_SEARCH: `${API_BASE_URL}/cpgram-application-service/getDepartments`,
    CHECK_SUGGESTION_OR_GRIEVANCE: `${API_BASE_URL}/cpgram-application-service/grievance/checkSuggestionOrGrievance`,
    CREATE_GRIEVANCE: `${API_BASE_URL}/cpgram-application-service/grievance/create`,
    SIMILAR_GRIEVANCES: `${API_BASE_URL}/cpgram-application-service/grievance/similar`,
    GRIEVANCE_VOICECALL_HISTORY: `${API_BASE_URL}/cpgram-application-service/grievance/voicecall/history`,
    GRIEVANCE_DASHBOARD_DATA: `${API_BASE_URL}/dashboard/getDashboardData`,
    GRIEVANCE_UPDATE: `${API_BASE_URL}/cpgram-application-service/grievance/update`,
    APPEAL_UPDATE: `${API_BASE_URL}/cpgram-application-service/appeal/update`,
    FETCH_USERS_BY_TYPE: `${API_BASE_URL}/cpgram-application-service/user/fetchUsersByUserTypes`,
    CREATE_APPEAL: `${API_BASE_URL}/cpgram-application-service/appeal/create`,

  // Add other endpoints here
};