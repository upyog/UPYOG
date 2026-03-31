import React, { useCallback, useEffect, useState, Fragment, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { API_ENDPOINTS } from "../../../config/apiConfig";
import { useParams,Switch, useLocation, Link, useHistory } from "react-router-dom";


const GrievanceStatus = ({ isTracking = false }) => {
    const { grievanceId } = useParams();
    const history = useHistory();
    const { t } = useTranslation();

    // State management
    const [grievanceData, setGrievanceData] = useState(null);
    const [voicecallDetails, setVoicecallDetails] = useState([]);
    const [userDetails, setUserDetails] = useState( {
            "id": "15",
            "name": "Robin Sharma",
            "mobileNo": "9999999999",
            "email": "Robin@gmail.com",
            "address": null,
            "gender": "Male",
            "userType": "Citizen",
            "password": "123456",
            "column_1": null,
            "column_2": null,
            "column_3": null,
            "created_by": null,
            "updated_by": null,
            "createdOn": null,
            "updatedOn": null,
            "organization": null
        });
    const [isLoading, setIsLoading] = useState(false);
    const [grievanceTableId, setGrievanceTableId] = useState(null);
    const [showActionDropdown, setShowActionDropdown] = useState(false);
    const [workflowAction, setWorkflowAction] = useState(null);
    const [approvedSteps, setApprovedSteps] = useState([]);
    const [approvedAppealSteps, setApprovedAppealSteps] = useState([]);
    const [dropdownPosition, setDropdownPosition] = useState('bottom');
    const [showFeedbackModal, setShowFeedbackModal] = useState(false);
    const dropdownRef = useRef(null);

    // Get user details from localStorage on mount
    // useEffect(() => {
    //     const userData = localStorage.getItem('userDetails');
    //     if (userData) {
    //         setUserDetails(JSON.parse(userData));
    //     }
    // }, []);

    // Handle click outside dropdown
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowActionDropdown(false);
            }
        };

        // Calculate dropdown position based on available space
        const calculateDropdownPosition = () => {
            if (dropdownRef.current && showActionDropdown) {
                const element = dropdownRef.current;
                const rect = element.getBoundingClientRect();
                const spaceBelow = window.innerHeight - rect.bottom;
                const spaceAbove = rect.top;
                
                // If more space above or not enough space below, position dropdown upward
                if (spaceAbove > spaceBelow && spaceAbove > 200) {
                    setDropdownPosition('top');
                } else {
                    setDropdownPosition('bottom');
                }
            }
        };

        if (showActionDropdown) {
            document.addEventListener('mousedown', handleClickOutside);
            setTimeout(calculateDropdownPosition, 0);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showActionDropdown]);

    // Fetch grievance details
    const getGrievanceById = useCallback(() => {
        if (!grievanceId) return;

        setIsLoading(true);
        try {
            const reqObj = {
                data: {
                    grievanceId: grievanceId
                }
            };

            fetch(API_ENDPOINTS.GRIEVANCE_SEARCH, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reqObj)
            })
            .then(res => res.json())
            .then(data => {
                setIsLoading(false);
                if (data && data.success && data.response && data.response.length > 0) {
                    const grievance = data.response[0];
                    
                    // Format data
                    grievance.field_details = grievance.field_1 || [];
                    grievance.raised_on = new Date(grievance.created_at).toDateString();
                    setGrievanceData(grievance);
                    setGrievanceTableId(grievance.id);

                    // Find similar grievances if not citizen
                    if (userDetails?.userType !== 'Citizen') {
                        findSimilarGrievances(grievance.description_en);
                    }

                    // Mark approved steps
                    markApprovedSteps(grievance);
                }
            })
            .catch(error => {
                setIsLoading(false);
                console.error('Error fetching grievance:', error);
            });
        } catch (error) {
            setIsLoading(false);
            console.error('Error in getGrievanceById:', error);
        }
    }, [grievanceId, userDetails]);

    // Fetch voice call details
    const getVoicecallDetails = useCallback(() => {
        if (!grievanceId) return;

        try {
            const reqObj = { grievanceId };
            
            // Construct URL from base endpoint
            const voiceCallUrl = API_ENDPOINTS.GRIEVANCE_VOICECALL_HISTORY;

            fetch(voiceCallUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reqObj)
            })
            .then(res => res.json())
            .then(data => {
                if (data && data.success) {
                    setVoicecallDetails(data.response || []);
                }
            })
            .catch(error => console.error('Error fetching voice call details:', error));
        } catch (error) {
            console.error('Error in getVoicecallDetails:', error);
        }
    }, [grievanceId]);

    // Find similar grievances
    const findSimilarGrievances = (description) => {
        if (!description) return;

        try {
            const reqObj = {
                description,
                grievanceId
            };

            const similarUrl = API_ENDPOINTS.SIMILAR_GRIEVANCES;

            fetch(similarUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reqObj)
            })
            .then(res => res.json())
            .then(data => {
                if (data && data.success) {
                    setGrievanceData(prev => ({
                        ...prev,
                        similarGrievances: data.response || []
                    }));
                }
            })
            .catch(error => console.error('Error finding similar grievances:', error));
        } catch (error) {
            console.error('Error in findSimilarGrievances:', error);
        }
    };

    // Mark approved workflow steps
    const markApprovedSteps = (data) => {
        if (data?.workflowDetails) {
            const approved = data.workflowDetails
                .map((step, idx) => {
                    if (step.action === 'Approve' || step.action === 'Closed') {
                        return idx;
                    }
                    return null;
                })
                .filter(idx => idx !== null);
            setApprovedSteps(approved);
        }

        if (data?.appealWorkflowDetails) {
            const approved = data.appealWorkflowDetails
                .map((step, idx) => {
                    if (step.action === 'Closed' || step.action === 'Disposed') {
                        return idx;
                    }
                    return null;
                })
                .filter(idx => idx !== null);
            setApprovedAppealSteps(approved);
        }
    };

    // Initialize on mount
    useEffect(() => {
        getGrievanceById();
        getVoicecallDetails();
    }, [getGrievanceById, getVoicecallDetails]);

    // Handle back navigation
    const handleBackToHome = () => {
        if (!isTracking) {
            history.replace('/digit-ui/citizen/pt/pt-grievance-dashboard');
        } else {
            history.replace('/digit-ui/citizen/pt/pt-grievance-dashboard');
        }
    };

    // Handle workflow action
    const handleWorkflowAction = (action) => {
        setWorkflowAction(action);
        // Show modal - to be implemented with workflow modal component
        console.log('Action selected:', action);
    };

    // Date formatter
    const dateFormat = (date) => {
        return new Date(date).toDateString();
    };

    // Get workflow label
    const getWorkflowLabel = (workflow, isAppeal = false) => {
        const { action, assign_by_employee_type, employee_type, employee_name, assign_by_employee_name } = workflow;
        const showDetails = userDetails?.userType === 'Nodal' || userDetails?.userType === 'GRO' || 
                           userDetails?.userType === 'Helpdesk User' || userDetails?.userType === 'Appellate Officer';

        const employeeDisplay = (type, name) => {
            const typeMap = {
                'Citizen': 'Citizen',
                'GRO': 'GO',
                'Nodal': 'Section Officer',
                'Appellate Officer': 'Appellate Officer',
                'Sub-Appellate Officer': 'Sub-Appellate Officer'
            };
            const typeLabel = typeMap[type] || type;
            return showDetails && name ? `${typeLabel} - ${name}` : typeLabel;
        };

        if (isAppeal) {
            switch (action) {
                case 'Appeal raised':
                    return `Appeal Assigned to ${employeeDisplay(employee_type, employee_name)} by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Forward':
                    return `Appeal Assigned to ${employeeDisplay(employee_type, employee_name)} by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Approve':
                    return `Appeal Approved by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Reject':
                    return `Appeal Rejected by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Closed':
                    return `Appeal Closed by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Forward to Sub-Appellate Officer':
                    return `Appeal Assigned to Sub-Appellate Officer by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Disposed':
                    return `Appeal Disposed by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                default:
                    return action;
            }
        } else {
            switch (action) {
                case 'Create':
                    return `${grievanceData?.type === 'Grievance' ? 'Grievance' : 'Suggestion'} Assigned to ${employeeDisplay(employee_type, employee_name)} by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Sent back to citizen':
                    return `Grievance Sent back to Citizen by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Re-submit by citizen':
                    return `Clarification Re-submitted to ${employeeDisplay(employee_type, employee_name)} by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Forward':
                    return `Grievance Assigned to ${employeeDisplay(employee_type, employee_name)} by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Approve':
                    return `Resolution provided by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Reject':
                    return `Grievance Rejected by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                case 'Closed':
                    return `Grievance Closed by ${employeeDisplay(assign_by_employee_type, assign_by_employee_name)}`;
                default:
                    return action;
            }
        }
    };

    if (isLoading && !grievanceData) {
        return (
            <div style={{ padding: '20px', textAlign: 'center' }}>
                <p>Loading grievance details...</p>
            </div>
        );
    }

    if (!grievanceData) {
        return (
            <div style={{ padding: '20px' }}>
                <p>Grievance not found</p>
                <button onClick={handleBackToHome} className="btn btn-primary">Back to Home</button>
            </div>
        );
    }

    return (
        <Fragment>
        <div style={{ width: '98%', minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
            {/* Header with Grievance Number */}
            <div style={{
                backgroundColor: 'white',
                padding: '20px',
                borderBottom: '2px solid #0f9fd9'
            }}>
                <p style={{ margin: 0, fontSize: '16px', color: '#212121' }}>
                    <strong>Grievance Number:</strong> {grievanceId}
                </p>
            </div>

            {/* Workflow Tracking Section - Horizontal */}
            {grievanceData?.workflowDetails && grievanceData.workflowDetails.length > 0 && (
                <div style={{
                    backgroundColor: 'white',
                    padding: '40px 20px',
                    marginBottom: '20px',
                    position: 'relative'
                }}>
                    {/* Connecting Line */}
                    <div style={{
                        position: 'absolute',
                        top: '60px',
                        left: '20px',
                        right: '20px',
                        height: '2px',
                        backgroundColor: '#e0e0e0',
                        zIndex: 0
                    }} />

                    {/* Steps */}
                    <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        position: 'relative',
                        zIndex: 1
                    }}>
                        {grievanceData.workflowDetails.map((workflow, index) => {
                            const isCompleted = approvedSteps.includes(index);
                            const isLast = index === grievanceData.workflowDetails.length - 1;
                            
                            return (
                                <div
                                    key={index}
                                    style={{
                                        flex: 1,
                                        display: 'flex',
                                        flexDirection: 'column',
                                        alignItems: 'center',
                                        position: 'relative'
                                    }}
                                >
                                    {/* Step Circle */}
                                    <div style={{
                                        width: '50px',
                                        height: '50px',
                                        borderRadius: '50%',
                                        backgroundColor: isCompleted ? '#4caf50' : (isLast ? '#ff9800' : '#e0e0e0'),
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        color: 'white',
                                        fontWeight: 'bold',
                                        fontSize: '20px',
                                        marginBottom: '15px',
                                        border: '3px solid white',
                                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                                        position: 'relative',
                                        zIndex: 2
                                    }}>
                                        {isCompleted ? '✓' : (isLast ? index + 1 : '✓')}
                                    </div>

                                    {/* Step Label */}
                                    <p style={{
                                        margin: '0 0 5px 0',
                                        fontSize: '13px',
                                        fontWeight: '600',
                                        color: '#212121',
                                        textAlign: 'center',
                                        maxWidth: '150px',
                                        lineHeight: '1.4'
                                    }}>
                                        {getWorkflowLabel(workflow)}
                                    </p>

                                    {/* Date */}
                                    <p style={{
                                        margin: '0 0 8px 0',
                                        fontSize: '11px',
                                        color: '#999',
                                        textAlign: 'center'
                                    }}>
                                        {dateFormat(workflow.created_date || workflow.created_at)}
                                    </p>

                                    {/* Comment Icon */}
                                    {workflow.comments && (
                                        <div
                                            style={{
                                                fontSize: '16px',
                                                cursor: 'pointer',
                                                position: 'relative'
                                            }}
                                            title={workflow.comments}
                                        >
                                            💬
                                        </div>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}

            {/* Main Container */}
            <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '0 20px 20px' }}>
                {/* Grievance Details Section */}
                <div style={{
                    backgroundColor: 'white',
                    borderRadius: '4px',
                    marginBottom: '20px',
                    boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
                }}>
                    {/* Section Header */}
                    <div style={{
                        backgroundColor: '#e3f2fd',
                        padding: '15px',
                        borderBottom: '1px solid #90caf9',
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        cursor: 'pointer'
                    }}>
                        <h3 style={{ margin: 0, color: '#0f4f9e', fontSize: '16px' }}>Grievance Details</h3>
                        <span style={{ fontSize: '18px' }}>▼</span>
                    </div>

                    {/* Type Tabs */}
                    <div style={{ padding: '15px', borderBottom: '1px solid #f0f0f0' }}>
                        <div style={{ display: 'flex', gap: '20px' }}>
                            <button
                                style={{
                                    backgroundColor: 'transparent',
                                    border: 'none',
                                    color: grievanceData.type === 'Grievance' ? '#0f4f9e' : '#999',
                                    fontSize: '16px',
                                    fontWeight: grievanceData.type === 'Grievance' ? '600' : '400',
                                    borderBottom: grievanceData.type === 'Grievance' ? '2px solid #0f4f9e' : 'none',
                                    padding: '0 0 5px 0',
                                    cursor: 'pointer'
                                }}
                            >
                                Type: Grievance
                            </button>
                            <button
                                style={{
                                    backgroundColor: 'transparent',
                                    border: 'none',
                                    color: grievanceData.type === 'Suggestion' ? '#0f4f9e' : '#999',
                                    fontSize: '16px',
                                    fontWeight: grievanceData.type === 'Suggestion' ? '600' : '400',
                                    borderBottom: grievanceData.type === 'Suggestion' ? '2px solid #0f4f9e' : 'none',
                                    padding: '0 0 5px 0',
                                    cursor: 'pointer'
                                }}
                            >
                                Suggestion
                            </button>
                        </div>
                    </div>

                    {/* Content */}
                    <div style={{ padding: '20px' }}>
                        {/* Citizen Details */}
                        <div style={{ marginBottom: '25px' }}>
                            <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                Citizen Details
                            </h4>
                            <div style={{
                                display: 'grid',
                                gridTemplateColumns: '1fr 1fr 1fr',
                                gap: '20px'
                            }}>
                                <div>
                                    <label style={{ display: 'block', fontSize: '16px', color: '#666', marginBottom: '5px' }}>
                                        <strong>Name:</strong>
                                    </label>
                                    <p style={{ margin: 0, fontSize: '16px' }}>{grievanceData.name}</p>
                                </div>
                                <div>
                                    <label style={{ display: 'block', fontSize: '16px', color: '#666', marginBottom: '5px' }}>
                                        <strong>Mobile No.:</strong>
                                    </label>
                                    <p style={{ margin: 0, fontSize: '16px' }}>{grievanceData.mobileNo}</p>
                                </div>
                                <div>
                                    <label style={{ display: 'block', fontSize: '16px', color: '#666', marginBottom: '5px' }}>
                                        <strong>Country:</strong>
                                    </label>
                                    <p style={{ margin: 0, fontSize: '16px' }}>{grievanceData.country}</p>
                                </div>
                            </div>
                        </div>

                        <hr style={{ margin: '20px 0', borderTop: '1px solid #f0f0f0' }} />

                        {/* Grievance Description */}
                        <div style={{ marginBottom: '25px' }}>
                            <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                Grievance Description
                            </h4>
                            {grievanceData.description_en && !grievanceData.description_other && (
                                <div style={{
                                    backgroundColor: '#f9f9f9',
                                    padding: '15px',
                                    borderRadius: '4px',
                                    fontSize: '16px',
                                    color: '#333',
                                    whiteSpace: 'pre-wrap',
                                    lineHeight: '1.5'
                                }}>
                                    {grievanceData.description_en}
                                </div>
                            )}
                            {grievanceData.description_en && grievanceData.description_other && (
                                <div>
                                    <div style={{
                                        backgroundColor: '#f9f9f9',
                                        padding: '15px',
                                        borderRadius: '4px',
                                        fontSize: '16px',
                                        color: '#333',
                                        whiteSpace: 'pre-wrap',
                                        lineHeight: '1.5'
                                    }}>
                                        {grievanceData.description_other}
                                        <div style={{ fontSize: '14px', color: '#0f56b3', marginTop: '10px' }}>
                                            English Translation:
                                            <hr style={{
                                                position: 'relative',
                                                top: '-10px',
                                                width: '84%',
                                                left: '12%',
                                            }}/>
                                        </div>
                                        {grievanceData.description_en}
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* Domain & Category and Uploaded Documents */}
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px', marginBottom: '25px' }}>
                            {/* Domain & Category */}
                            <div>
                                <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                    Domain & Category
                                </h4>
                                <div style={{
                                    backgroundColor: '#e3f2fd',
                                    border: '1px solid #90caf9',
                                    borderRadius: '4px',
                                    padding: '15px'
                                }}>
                                    <div style={{ marginBottom: '10px', fontSize: '16px' }}>
                                        <strong>Department:</strong> {grievanceData.department_name || grievanceData?.categoryDetails?.stage_1_details?.[0]?.description || 'N/A'}
                                    </div>
                                    <div style={{ marginLeft: '20px', marginBottom: '10px', fontSize: '16px' }}>
                                        <strong>└ Level 1:</strong> {grievanceData.category_name || grievanceData?.categoryDetails?.stage_2_details?.[0]?.description || 'N/A'}
                                    </div>
                                    <div style={{ marginLeft: '40px', fontSize: '16px' }}>
                                        <strong>└ Level 2:</strong> {grievanceData.subcategory_name || grievanceData?.categoryDetails?.stage_3_details?.description || 'N/A'}
                                    </div>
                                </div>
                            </div>

                            {/* Uploaded Documents */}
                            {grievanceData.uploaded_file && (
                                <div>
                                    <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                        Uploaded Documents
                                    </h4>
                                    <div style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '10px',
                                        padding: '15px',
                                        backgroundColor: '#f5f5f5',
                                        borderRadius: '4px'
                                    }}>
                                        <span style={{ fontSize: '18px' }}>📄</span>
                                        <span style={{ fontSize: '16px' }}>{grievanceData.uploaded_file?.name}</span>
                                        <button
                                            style={{
                                                marginLeft: 'auto',
                                                backgroundColor: '#0f9fd9',
                                                color: 'white',
                                                border: 'none',
                                                width: '30px',
                                                height: '30px',
                                                borderRadius: '50%',
                                                cursor: 'pointer',
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                                fontSize: '16px'
                                            }}
                                        >
                                            ⬇
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* Field Details */}
                        {grievanceData.field_details && grievanceData.field_details.length > 0 && (
                            <>
                                <hr style={{ margin: '20px 0', borderTop: '1px solid #f0f0f0' }} />
                                <div>
                                    <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                        Field Details
                                    </h4>
                                    <div style={{
                                        backgroundColor: '#f9f9f9',
                                        border: '1px solid #f0f0f0',
                                        borderRadius: '4px',
                                        padding: '15px',
                                        display: 'grid',
                                        gridTemplateColumns: 'repeat(2, 1fr)',
                                        gap: '20px'
                                    }}>
                                        {grievanceData.field_details.map((field, idx) => (
                                            <div key={idx}>
                                                <p style={{ margin: '0 0 5px 0', fontSize: '16px', color: '#666' }}>
                                                    <strong>{field.label || field.name}:</strong>
                                                </p>
                                                <p style={{ margin: 0, fontSize: '16px' }}>{field.value || 'N/A'}</p>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </>
                        )}

                        {/* Resolution Information */}
                        <div style={{ marginTop: '25px', paddingTop: '20px', borderTop: '1px solid #f0f0f0' }}>
                            <div style={{
                                display: 'grid',
                                gridTemplateColumns: '1fr 1fr',
                                gap: '20px'
                            }}>
                                <div>
                                    <label style={{ display: 'block', fontSize: '16px', color: '#666', marginBottom: '5px' }}>
                                        <strong>Expected Resolution Time:</strong>
                                    </label>
                                    <p style={{ margin: 0, fontSize: '16px', color: '#0f4f9e' }}>7 days</p>
                                </div>
                                <div>
                                    <label style={{ display: 'block', fontSize: '16px', color: '#666', marginBottom: '5px' }}>
                                        <strong>Submission Date:</strong>
                                    </label>
                                    <p style={{ margin: 0, fontSize: '16px', color: '#0f4f9e' }}>
                                        {new Date(grievanceData.created_at).toLocaleDateString()}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Action Button */}
                <div style={{
                    display: 'flex',
                    justifyContent: 'flex-end',
                    marginBottom: '40px'
                }}>
                    <div 
                        ref={dropdownRef}
                        style={{ position: 'relative', display: 'inline-block' }}
                    >
                        <button
                            onClick={() => setShowActionDropdown(!showActionDropdown)}
                            className="btn"
                            style={{
                                padding: '10px 20px',
                                backgroundColor: '#0f9fd9',
                                color: 'white',
                                border: 'none',
                                borderRadius: '4px',
                                cursor: 'pointer',
                                fontSize: '16px',
                                fontWeight: '600'
                            }}
                        >
                            Take Action ▼
                        </button>

                        {showActionDropdown && (
                            <div style={{
                                position: 'absolute',
                                ...( dropdownPosition === 'top' 
                                    ? { bottom: '100%', marginBottom: '5px' }
                                    : { top: '100%', marginTop: '5px' }
                                ),
                                right: 0,
                                backgroundColor: 'white',
                                border: '1px solid #ddd',
                                borderRadius: '4px',
                                boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                                minWidth: '250px',
                                zIndex: 10
                            }}>
                                <button
                                    onClick={handleBackToHome}
                                    style={{
                                        display: 'block',
                                        width: '100%',
                                        padding: '10px 15px',
                                        backgroundColor: 'transparent',
                                        border: 'none',
                                        textAlign: 'left',
                                        cursor: 'pointer',
                                        fontSize: '16px',
                                        borderBottom: '1px solid #f0f0f0'
                                    }}
                                >
                                    Back to Home
                                </button>

                                {userDetails?.userType === 'Citizen' && (grievanceData.status === 'Approve' || grievanceData.appealDetails?.status === 'Disposed') && (
                                    <button
                                        onClick={() => { setShowFeedbackModal(true); setShowActionDropdown(false); }}
                                        style={{
                                            display: 'block',
                                            width: '100%',
                                            padding: '10px 15px',
                                            backgroundColor: 'transparent',
                                            border: 'none',
                                            textAlign: 'left',
                                            cursor: 'pointer',
                                            fontSize: '16px',
                                            borderBottom: '1px solid #f0f0f0'
                                        }}
                                    >
                                        Share your feedback
                                    </button>
                                )}

                                {userDetails?.userType !== 'Citizen' && grievanceData.status !== 'Approve' && grievanceData.status !== 'Closed' && (
                                    <>
                                        <button
                                            onClick={() => handleWorkflowAction('Approve')}
                                            style={{
                                                display: 'block',
                                                width: '100%',
                                                padding: '10px 15px',
                                                backgroundColor: 'transparent',
                                                border: 'none',
                                                textAlign: 'left',
                                                cursor: 'pointer',
                                                fontSize: '16px',
                                                borderBottom: '1px solid #f0f0f0'
                                            }}
                                        >
                                            Resolve
                                        </button>

                                        <button
                                            onClick={() => handleWorkflowAction('Sent back to citizen')}
                                            style={{
                                                display: 'block',
                                                width: '100%',
                                                padding: '10px 15px',
                                                backgroundColor: 'transparent',
                                                border: 'none',
                                                textAlign: 'left',
                                                cursor: 'pointer',
                                                fontSize: '16px',
                                                borderBottom: '1px solid #f0f0f0'
                                            }}
                                        >
                                            Send back to Citizen
                                        </button>

                                        <button
                                            onClick={() => handleWorkflowAction('Forward')}
                                            style={{
                                                display: 'block',
                                                width: '100%',
                                                padding: '10px 15px',
                                                backgroundColor: 'transparent',
                                                border: 'none',
                                                textAlign: 'left',
                                                cursor: 'pointer',
                                                fontSize: '16px'
                                            }}
                                        >
                                            Forward
                                        </button>
                                    </>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>

            {/* Feedback Modal */}
            {showFeedbackModal && (
                <FeedbackModal
                    grievanceId={grievanceId}
                    grievanceTableId={grievanceTableId}
                    userDetails={userDetails}
                    grievanceData={grievanceData}
                    onClose={() => setShowFeedbackModal(false)}
                    onSuccess={() => {
                        setShowFeedbackModal(false);
                        getGrievanceById();
                    }}
                />
            )}
        </Fragment>
    );
}

/* ── Feedback Modal Component ── */
const FeedbackModal = ({ grievanceId, grievanceTableId, userDetails, grievanceData, onClose, onSuccess }) => {
    const [rating, setRating] = useState(null);
    const [wantAppeal, setWantAppeal] = useState(false);
    const [remarks, setRemarks] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [errors, setErrors] = useState({});

    const ratingColors = [
        '#e53935', '#e53935', '#ef6c00', '#ef6c00',
        '#c0ca33', '#c0ca33', '#7cb342', '#7cb342',
        '#43a047', '#43a047'
    ];

    const validate = () => {
        const newErrors = {};
        if (!rating) newErrors.rating = 'Please select a rating';
        if (wantAppeal && !remarks.trim()) newErrors.remarks = 'Remarks are mandatory when raising an appeal';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async () => {
        if (!validate()) return;
        setIsSubmitting(true);
        try {
            let url;
            let payload;

            if (wantAppeal) {
                // Appeal: call /appeal/create
                url = API_ENDPOINTS.CREATE_APPEAL;
                payload = {
                    data: {
                        assign_by: userDetails?.id,
                        assign_to: grievanceData?.assign_to || 19,
                        comments: '',
                        status: 'Appeal raised',
                        action: 'Appeal raised',
                        grievance_table_id: grievanceTableId,
                        appeal_description: remarks.trim(),
                        feedback_description: '',
                        is_appeal: true,
                        raised_by: userDetails?.id,
                        rating
                    }
                };
            } else {
                // No appeal: call /grievance/update to close
                url = API_ENDPOINTS.GRIEVANCE_UPDATE;
                payload = {
                    data: {
                        assign_by: userDetails?.id,
                        assign_to: null,
                        comments: '',
                        status: 'Closed',
                        action: 'Closed',
                        grievance_table_id: grievanceTableId,
                        appeal_description: '',
                        feedback_description: remarks.trim(),
                        is_appeal: false,
                        raised_by: userDetails?.id,
                        rating
                    }
                };
            }

            const res = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if (data && data.success) {
                alert(data.message || (wantAppeal ? 'Appeal raised successfully!' : 'Feedback submitted successfully!'));
                onSuccess();
                history.push('/digit-ui/citizen/pt/pt-grievance-dashboard');
            } else {
                alert(data?.message || 'Something went wrong!');
            }
        } catch (err) {
            console.error('Feedback submit error:', err);
            alert('Something went wrong!');
        } finally {
            setIsSubmitting(false);
        }
    };

    // Overlay styles
    const overlayStyle = {
        position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
        backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex',
        alignItems: 'center', justifyContent: 'center', zIndex: 10000
    };

    const modalStyle = {
        backgroundColor: 'white', borderRadius: '8px', width: '600px',
        maxWidth: '95vw', maxHeight: '90vh', overflow: 'auto',
        boxShadow: '0 4px 20px rgba(0,0,0,0.25)'
    };

    return (
        <div style={overlayStyle} onClick={onClose}>
            <div style={modalStyle} onClick={e => e.stopPropagation()}>
                {/* Header */}
                <div style={{
                    backgroundColor: '#0f9fd9', color: 'white', padding: '10px 20px',
                    display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                }}>
                    <h3 style={{ margin: 0, fontSize: '20px', fontWeight: '700' }}>Provide Feedback</h3>
                    <button onClick={onClose} style={{
                        background: 'none', border: 'none', color: 'white',
                        fontSize: '22px', cursor: 'pointer', fontWeight: 'bold'
                    }}>✕</button>
                </div>

                {/* Body */}
                <div style={{ padding: '25px 20px' }}>
                    {/* Satisfaction Rating */}
                    <div style={{ marginBottom: '25px' }}>
                        <p style={{ margin: '0 0 12px 0', fontWeight: '600', fontSize: '16px' }}>
                            How satisfied are you with the resolution ?
                        </p>
                        <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map(n => (
                                <button
                                    key={n}
                                    onClick={() => { setRating(n); setErrors(prev => ({ ...prev, rating: undefined })); }}
                                    style={{
                                        width: '44px', height: '44px', borderRadius: '6px',
                                        border: rating === n ? '3px solid #333' : '2px solid transparent',
                                        backgroundColor: ratingColors[n - 1], color: 'white',
                                        fontSize: '16px', fontWeight: '700', cursor: 'pointer',
                                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                                        transition: 'transform 0.1s',
                                        transform: rating === n ? 'scale(1.15)' : 'scale(1)'
                                    }}
                                >
                                    {n}
                                </button>
                            ))}
                        </div>
                        {errors.rating && <p style={{ color: 'red', fontSize: '13px', margin: '6px 0 0' }}>{errors.rating}</p>}
                    </div>

                    {/* Want to raise appeal */}
                    <div style={{ marginBottom: '25px', display: 'flex', alignItems: 'center', gap: '15px' }}>
                        <span style={{ fontWeight: '600', fontSize: '16px' }}>Want to raise appeal:</span>
                        <label style={{ display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer', fontSize: '16px' }}>
                            <input
                                type="radio" name="wantAppeal" checked={wantAppeal}
                                onChange={() => setWantAppeal(true)}
                                style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                            />
                            Yes
                        </label>
                        <label style={{ display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer', fontSize: '16px' }}>
                            <input
                                type="radio" name="wantAppeal" checked={!wantAppeal}
                                onChange={() => { setWantAppeal(false); setErrors(prev => ({ ...prev, remarks: undefined })); }}
                                style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                            />
                            No
                        </label>
                    </div>

                    {/* Remarks */}
                    <div style={{ marginBottom: '10px' }}>
                        <label style={{ display: 'block', fontWeight: '400', fontSize: '16px', marginBottom: '8px', color: '#666' }}>
                            Remarks {wantAppeal && <span style={{ color: 'red' }}>*</span>}
                        </label>
                        <textarea
                            value={remarks}
                            onChange={e => { setRemarks(e.target.value); setErrors(prev => ({ ...prev, remarks: undefined })); }}
                            rows={4}
                            style={{
                                width: '100%', padding: '10px', fontSize: '15px',
                                border: errors.remarks ? '1px solid red' : '1px solid #ccc',
                                borderRadius: '4px', resize: 'vertical', boxSizing: 'border-box'
                            }}
                        />
                        {errors.remarks && <p style={{ color: 'red', fontSize: '13px', margin: '6px 0 0' }}>{errors.remarks}</p>}
                    </div>
                </div>

                {/* Footer */}
                <div style={{
                    padding: '15px 20px', borderTop: '1px solid #eee',
                    display: 'flex', justifyContent: 'flex-end', gap: '12px'
                }}>
                    <button
                        onClick={onClose}
                        disabled={isSubmitting}
                        style={{
                            padding: '10px 28px', fontSize: '15px', fontWeight: '600',
                            backgroundColor: '#0f9fd9', color: 'white', border: 'none',
                            borderRadius: '4px', cursor: 'pointer'
                        }}
                    >Cancel</button>
                    <button
                        onClick={handleSubmit}
                        disabled={isSubmitting}
                        style={{
                            padding: '10px 28px', fontSize: '15px', fontWeight: '600',
                            backgroundColor: '#00838f', color: 'white', border: 'none',
                            borderRadius: '4px', cursor: isSubmitting ? 'not-allowed' : 'pointer',
                            opacity: isSubmitting ? 0.7 : 1
                        }}
                    >{isSubmitting ? 'Submitting...' : 'Submit'}</button>
                </div>
            </div>
        </div>
    );
};

export default GrievanceStatus;
