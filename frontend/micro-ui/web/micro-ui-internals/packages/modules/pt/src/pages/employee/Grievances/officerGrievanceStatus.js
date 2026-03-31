import React, { useCallback, useEffect, useState, Fragment, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { API_ENDPOINTS } from "../../../config/apiConfig";
import { useParams,Switch, useLocation, Link, useHistory } from "react-router-dom";
import WorkflowActionModal from './WorkflowActionModal';


const EmployeeGrievanceStatus = ({ isTracking = false }) => {
    const { grievanceId } = useParams();
    const history = useHistory();
    const { t } = useTranslation();

    // State management
    const [grievanceData, setGrievanceData] = useState(null);
    const [voicecallDetails, setVoicecallDetails] = useState([]);
    const [userDetails, setUserDetails] = useState({
        "id": "16",
        "name": "Ajay Singh",
        "mobileNo": "9999999910",
        "email": "asingh@gmail.com",
        "address": null,
        "gender": null,
        "userType": "GRO",
        "password": "123456",
        "column_1": null,
        "column_2": null,
        "column_3": null,
        "created_by": null,
        "updated_by": null,
        "createdOn": null,
        "updatedOn": "2026-02-11T05:08:53.121Z",
        "organization": "PT7448"
    });
    const [isLoading, setIsLoading] = useState(false);
    const [grievanceTableId, setGrievanceTableId] = useState(null);
    const [showActionDropdown, setShowActionDropdown] = useState(false);
    const [workflowAction, setWorkflowAction] = useState(null);
    const [approvedSteps, setApprovedSteps] = useState([]);
    const [approvedAppealSteps, setApprovedAppealSteps] = useState([]);
    const [dropdownPosition, setDropdownPosition] = useState('bottom');
    const [showWorkflowActionModal, setShowWorkflowActionModal] = useState(false);
    const [isSubmittingWorkflow, setIsSubmittingWorkflow] = useState(false);
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

    const isMountedRef = useRef(true);
    useEffect(() => {
        isMountedRef.current = true;
        return () => { isMountedRef.current = false; };
    }, []);

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
                if (!isMountedRef.current) return;
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
                if (!isMountedRef.current) return;
                setIsLoading(false);
                console.error('Error fetching grievance:', error);
            });
        } catch (error) {
            if (!isMountedRef.current) return;
            setIsLoading(false);
            console.error('Error in getGrievanceById:', error);
        }
    }, [grievanceId, userDetails]);

    // Fetch voice call details
    const getVoicecallDetails = useCallback(() => {
        if (!grievanceId) return;
        try {
            const reqObj = { grievanceId };
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
                if (!isMountedRef.current) return;
                if (data && data.success) {
                    setVoicecallDetails(data.response || []);
                }
            })
            .catch(error => {
                if (!isMountedRef.current) return;
                console.error('Error fetching voice call details:', error);
            });
        } catch (error) {
            if (!isMountedRef.current) return;
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
                if (!isMountedRef.current) return;
                if (data && data.success) {
                    setGrievanceData(prev => ({
                        ...prev,
                        similarGrievances: data.response || []
                    }));
                }
            })
            .catch(error => {
                if (!isMountedRef.current) return;
                console.error('Error finding similar grievances:', error);
            });
        } catch (error) {
            if (!isMountedRef.current) return;
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
            history.replace('/digit-ui/employee/pt/grievances');
        } else {
            history.replace('/digit-ui/employee/pt/grievances');
        }
    };

    // Handle workflow action - open modal (matches Angular onClickForward)
    const handleWorkflowAction = (action) => {
        setWorkflowAction(action);
        setShowWorkflowActionModal(true);
        setShowActionDropdown(false);
    };

    // Workflow dialog callback - matches Angular workflowDialogCallback exactly
    const workflowDialogCallback = async (event) => {
        console.log('workflowDialogCallback===', event);
        if (!event) return;

        const dataList = grievanceData ? [grievanceData] : [];
        const workflowTableLength = dataList[0]?.workflowDetails?.length || 0;

        // Determine assign_to based on action (matches Angular logic)
        let assignTo;
        if (event.action === 'Sent back to citizen') {
            assignTo = dataList[0]?.raised_by;
        } else if (event.action === 'Re-submit by citizen') {
            assignTo = dataList[0]?.assign_by;
        } else {
            assignTo = event.data?.assign_to?.id;
        }

        // Determine status based on action (matches Angular logic)
        let status;
        if (['Approve', 'Reject', 'Forward to Sub-Appellate Officer', 'Disposed'].includes(event.action)) {
            status = event.action;
        } else if (event.action === 'Sent back to citizen') {
            status = 'Pending with citizen';
        } else {
            status = 'Pending';
        }

        const obj = {
            assign_by: userDetails?.id,
            assign_to: assignTo,
            comments: event.data?.comments || '',
            status,
            action: event.action,
            grievance_table_id: grievanceTableId,
            appeal_table_id: dataList[0]?.appealDetails?.id,
            workflow_table_id: dataList[0]?.workflowDetails?.[workflowTableLength - 1]?.id
        };

        // Determine URL based on action (matches Angular logic)
        const isAppealAction = event.action === 'Forward to Sub-Appellate Officer' || event.action === 'Disposed';
        const url = isAppealAction ? API_ENDPOINTS.APPEAL_UPDATE : API_ENDPOINTS.GRIEVANCE_UPDATE;

        setIsSubmittingWorkflow(true);
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ data: obj })
            });

            const res = await response.json();

            if (!isMountedRef.current) return;

            if (res && res.success) {
                setIsSubmittingWorkflow(false);
                setShowWorkflowActionModal(false);
                setWorkflowAction(null);
                alert(res.message || 'Action submitted successfully!');
                history.replace('/digit-ui/employee/pt/grievances');
            } else {
                setIsSubmittingWorkflow(false);
                alert('Something went wrong!');
            }
        } catch (error) {
            if (!isMountedRef.current) return;
            setIsSubmittingWorkflow(false);
            alert('Something went wrong!');
            console.log('Error in workflowDialogCallback=', error);
        }
    };

    // Parse PostgreSQL array string like "{\"item1\",\"item2\"}" into an array
    const parsePgArray = (str) => {
        if (!str) return [];
        try {
            const trimmed = str.replace(/^\{|\}$/g, '');
            const items = [];
            let current = '';
            let inQuotes = false;
            for (let i = 0; i < trimmed.length; i++) {
                const ch = trimmed[i];
                if (ch === '"') {
                    inQuotes = !inQuotes;
                } else if (ch === ',' && !inQuotes) {
                    items.push(current.trim());
                    current = '';
                } else {
                    current += ch;
                }
            }
            if (current.trim()) items.push(current.trim());
            return items;
        } catch {
            return [str];
        }
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
        <div style={{ width: '100%', minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
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
            <div style={{ padding: '0 20px 20px' }}>
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
                                    <p style={{ margin: 0, fontSize: '16px' }}>{grievanceData.mobile_no}</p>
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
                                        <div style={{fontSize: '14px', color: '#0f56b3'}}>
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
                        {/* Grievance Summary */}
                        <div style={{ marginBottom: '25px' }}>
                            <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                Grievance Summary
                            </h4>
                            {grievanceData.summary && (
                                <div style={{
                                    backgroundColor: '#f9f9f9',
                                    padding: '15px',
                                    borderRadius: '4px',
                                    fontSize: '16px',
                                    color: '#333',
                                    whiteSpace: 'pre-wrap',
                                    lineHeight: '1.5'
                                }}>
                                    {grievanceData.summary}
                                </div>
                            )}
                        </div>
                        {/* Probable root cause of the problem and suggestions to resolve the issue (if any) by the citizen */}

                        <div style={{ marginBottom: '25px' }}>
                            <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                Probable Root Causes
                            </h4>
                            <div style={{
                                backgroundColor: '#f9f9f9',
                                padding: '15px',
                                borderRadius: '4px',
                                fontSize: '16px',
                                color: '#333',
                                lineHeight: '1.5'
                            }}>
                                {parsePgArray(grievanceData.probable_root_cause).length > 0 ? (
                                    <ol style={{ margin: 0, paddingLeft: '20px', listStyle: 'auto' }}>
                                        {parsePgArray(grievanceData.probable_root_cause).map((item, idx) => (
                                            <li key={idx} style={{ marginBottom: '5px' }}>{item}</li>
                                        ))}
                                    </ol>
                                ) : 'N/A'}
                            </div>
                        </div>

                        <div style={{ marginBottom: '25px' }}>
                            <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                Probable Resolutions
                            </h4>
                            <div style={{
                                backgroundColor: '#f9f9f9',
                                padding: '15px',
                                borderRadius: '4px',
                                fontSize: '16px',
                                color: '#333',
                                lineHeight: '1.5'
                            }}>
                                {parsePgArray(grievanceData.probable_resolution).length > 0 ? (
                                    <ol style={{ margin: 0, paddingLeft: '20px', listStyle: 'auto' }}>
                                        {parsePgArray(grievanceData.probable_resolution).map((item, idx) => (
                                            <li key={idx} style={{ marginBottom: '5px' }}>{item}</li>
                                        ))}
                                    </ol>
                                ) : 'N/A'}
                            </div>
                        </div>

                        {/* Department & Category and Uploaded Documents */}
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px', marginBottom: '25px' }}>
                            {/* Department & Category */}
                            <div>
                                <h4 style={{ margin: '0 0 15px 0', color: '#0f4f9e', fontSize: '16px', textDecoration: 'underline' }}>
                                    Department & Category
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
                                        <strong>└ Category:</strong> {grievanceData.category_name || grievanceData?.categoryDetails?.stage_2_details?.[0]?.description || 'N/A'}
                                    </div>
                                    <div style={{ marginLeft: '40px', fontSize: '16px' }}>
                                        <strong>└ Sub-Category:</strong> {grievanceData.subcategory_name || grievanceData?.categoryDetails?.stage_3_details?.description || 'N/A'}
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

        {/* Workflow Action Modal */}
        <WorkflowActionModal
            isOpen={showWorkflowActionModal}
            action={workflowAction}
            onClose={() => {
                setShowWorkflowActionModal(false);
                setWorkflowAction(null);
            }}
            onSubmit={workflowDialogCallback}
            assigneeOptions={grievanceData?.assigneeList || []}
            isSubmitting={isSubmittingWorkflow}
            userDetails={userDetails}
            currentAssignTo={grievanceData?.assign_to}
        />
        </Fragment>
    );
}

export default EmployeeGrievanceStatus;
