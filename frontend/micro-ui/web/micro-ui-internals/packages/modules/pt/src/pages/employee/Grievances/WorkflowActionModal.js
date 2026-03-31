import React, { useState, useEffect } from 'react';
import { API_ENDPOINTS } from "../../../config/apiConfig";

const WorkflowActionModal = ({
    isOpen,
    action,
    onClose,
    onSubmit,
    assigneeOptions = [],
    isSubmitting = false,
    userDetails = null,
    currentAssignTo = null
}) => {
    const [selectedAssignee, setSelectedAssignee] = useState('');
    const [comments, setComments] = useState('');
    const [file, setFile] = useState(null);
    const [errors, setErrors] = useState({});
    const [groList, setGroList] = useState([]);
    const [isLoadingUsers, setIsLoadingUsers] = useState(false);

    const needsAssignee = () => {
        return action === 'Forward' || action === 'Forward to Sub-Appellate Officer';
    };

    // Fetch users for Forward actions (matches Angular getGroList)
    useEffect(() => {
        console.log("===",isOpen,needsAssignee(),userDetails)
        if (!isOpen || !needsAssignee() || !userDetails) return;

        const getGroList = async () => {
            setIsLoadingUsers(true);
            try {
                // Determine user_type based on logged-in user (matches Angular logic)
                const userType = userDetails.userType;
                let targetType;
                if (userType === 'Helpdesk User') {
                    targetType = 'GRO';
                } else if (userType === 'GRO') {
                    targetType = 'Nodal';
                } else if (userType === 'Appellate Officer') {
                    targetType = 'Sub-Appellate Officer';
                } else {
                    targetType = 'Nodal';
                }

                const response = await fetch(API_ENDPOINTS.FETCH_USERS_BY_TYPE, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        data: {
                            orgcode: userDetails.organization,
                            user_type: targetType
                        }
                    })
                });

                const result = await response.json();
                if (result.status === 200 && Array.isArray(result.user)) {
                    // Filter out current assignee and map display names (matches Angular logic)
                    const filtered = result.user
                        .filter(u => u.id !== currentAssignTo)
                        .map(u => ({
                            name: u.userType === 'GRO' ? `${u.name} (GO)` : `${u.name} (SO)`,
                            id: u.id,
                            userType: u.userType
                        }));
                    setGroList(filtered);
                }
            } catch (error) {
                console.error('Error fetching users:', error);
                setGroList([]);
            } finally {
                setIsLoadingUsers(false);
            }
        };

        getGroList();
    }, [isOpen, action, userDetails, currentAssignTo]);

    // Determine which options to use: fetched groList for Forward, or passed assigneeOptions
    const effectiveOptions = needsAssignee() ? groList : assigneeOptions;

    const handleFileChange = (event) => {
        setFile(event.target.files[0] || null);
    };

    const validateForm = () => {
        const newErrors = {};
        if (needsAssignee() && !selectedAssignee) {
            newErrors.assignee = 'This field is required';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = () => {
        if (!validateForm()) return;

        // Find the full assignee object from options
        const assigneeObj = effectiveOptions.find(
            opt => String(opt.id) === String(selectedAssignee)
        );

        // Build callback event matching Angular pattern:
        // { action, data: { assign_to, comments } }
        const event = {
            action,
            data: {
                assign_to: assigneeObj || null,
                comments: comments || '',
                file: file || null
            }
        };

        onSubmit(event);
        resetForm();
    };

    const resetForm = () => {
        setSelectedAssignee('');
        setComments('');
        setFile(null);
        setErrors({});
        setGroList([]);
    };

    const handleClose = () => {
        resetForm();
        onClose();
    };

    if (!isOpen) return null;

    const getActionTitle = () => {
        const titleMap = {
            'Forward': 'Forward',
            'Approve': 'Resolve',
            'Sent back to citizen': 'Send Back',
            'Reject': 'Reject',
            'Forward to Sub-Appellate Officer': 'Forward to Sub-Appellate Officer',
            'Disposed': 'Disposed',
            'Re-submit by citizen': 'Re-submit'
        };
        return titleMap[action] || action;
    };

    const getPlaceholder = () => {
        const placeholderMap = {
            'Forward': 'Select Department / Officer',
            'Approve': 'Select Resolution Type',
            'Sent back to citizen': 'Select Reason',
            'Forward to Sub-Appellate Officer': 'Select Sub-Appellate Officer',
            'Disposed': 'Select Disposition Type'
        };
        return placeholderMap[action] || 'Select Option';
    };

    return (
        <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 100000
        }}>
            <div style={{
                backgroundColor: 'white',
                borderRadius: '8px',
                width: '95%',
                maxWidth: '600px',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
                height: '400px',
                overflow: 'auto'
            }}>
                {/* Header */}
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '5px 20px',
                    backgroundColor: '#0f9fd9',
                    color: 'white'
                }}>
                    <h2 style={{ margin: 0, fontSize: '18px', fontWeight: '600' }}>
                        {getActionTitle()}
                    </h2>
                    <button
                        onClick={handleClose}
                        disabled={isSubmitting}
                        style={{
                            backgroundColor: 'transparent',
                            border: 'none',
                            color: 'white',
                            fontSize: '24px',
                            cursor: isSubmitting ? 'not-allowed' : 'pointer',
                            opacity: isSubmitting ? 0.6 : 1
                        }}
                    >
                        ×
                    </button>
                </div>

                {/* Body */}
                <div style={{ padding: '5px 20px' }}>
                    {/* Select Field - Only for Forward actions */}
                    {needsAssignee() && (
                    <div style={{ marginBottom: '25px' }}>
                        <label style={{
                            display: 'block',
                            fontSize: '14px',
                            fontWeight: '600',
                            marginBottom: '8px',
                            color: '#212121'
                        }}>
                            Select<span style={{ color: '#d32f2f' }}>*</span>
                        </label>
                        <select
                            value={selectedAssignee}
                            onChange={(e) => {
                                setSelectedAssignee(e.target.value);
                                if (errors.assignee) setErrors(prev => ({ ...prev, assignee: null }));
                            }}
                            disabled={isSubmitting || isLoadingUsers}
                            style={{
                                width: '100%',
                                padding: '10px 12px',
                                border: errors.assignee ? '2px solid #d32f2f' : '1px solid #bbb',
                                borderRadius: '4px',
                                fontSize: '14px',
                                boxSizing: 'border-box',
                                backgroundColor: 'white',
                                cursor: (isSubmitting || isLoadingUsers) ? 'not-allowed' : 'pointer',
                                opacity: (isSubmitting || isLoadingUsers) ? 0.6 : 1
                            }}
                        >
                            <option value="">{isLoadingUsers ? '-- Loading... --' : `-- ${getPlaceholder()} --`}</option>
                            {effectiveOptions.map((option) => (
                                <option key={option.id || option.value} value={option.id || option.value}>
                                    {option.name || option.label}
                                </option>
                            ))}
                        </select>
                        {errors.assignee && (
                            <div style={{
                                color: '#d32f2f',
                                fontSize: '12px',
                                marginTop: '5px',
                                fontWeight: '500'
                            }}>
                                {errors.assignee}
                            </div>
                        )}
                    </div>
                    )}

                    {/* Comments Field */}
                    <div style={{ marginBottom: '25px' }}>
                        <label style={{
                            display: 'block',
                            fontSize: '14px',
                            fontWeight: '600',
                            marginBottom: '8px',
                            color: '#212121'
                        }}>
                            Comments
                        </label>
                        <textarea
                            value={comments}
                            onChange={(e) => setComments(e.target.value)}
                            disabled={isSubmitting}
                            placeholder="Enter your comments..."
                            rows={5}
                            style={{
                                width: '100%',
                                padding: '10px 12px',
                                border: '1px solid #bbb',
                                borderRadius: '4px',
                                fontSize: '14px',
                                fontFamily: 'Arial, sans-serif',
                                boxSizing: 'border-box',
                                resize: 'vertical',
                                opacity: isSubmitting ? 0.6 : 1,
                                cursor: isSubmitting ? 'not-allowed' : 'text'
                            }}
                        />
                    </div>

                    {/* Upload Files Field */}
                    <div style={{ marginBottom: '25px' }}>
                        <label style={{
                            display: 'block',
                            fontSize: '14px',
                            fontWeight: '600',
                            marginBottom: '8px',
                            color: '#212121'
                        }}>
                            Upload Files
                        </label>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                            <label style={{
                                padding: '8px 16px',
                                backgroundColor: '#f5f5f5',
                                border: '1px solid #bbb',
                                borderRadius: '4px',
                                cursor: isSubmitting ? 'not-allowed' : 'pointer',
                                fontSize: '14px',
                                fontWeight: '500',
                                opacity: isSubmitting ? 0.6 : 1
                            }}>
                                Choose File
                                <input
                                    type="file"
                                    onChange={handleFileChange}
                                    disabled={isSubmitting}
                                    style={{ display: 'none' }}
                                />
                            </label>
                            <span style={{ fontSize: '14px', color: '#666' }}>
                                {file ? file.name : 'No file chosen'}
                            </span>
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <div style={{
                    display: 'flex',
                    justifyContent: 'flex-end',
                    gap: '10px',
                    padding: '5px 20px',
                    backgroundColor: '#f5f5f5',
                    borderTop: '1px solid #ddd'
                }}>
                    <button
                        onClick={handleClose}
                        disabled={isSubmitting}
                        style={{
                            padding: '10px 30px',
                            backgroundColor: '#dc3545',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            fontSize: '14px',
                            cursor: isSubmitting ? 'not-allowed' : 'pointer',
                            fontWeight: '500',
                            opacity: isSubmitting ? 0.6 : 1
                        }}
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={isSubmitting}
                        style={{
                            padding: '10px 30px',
                            backgroundColor: isSubmitting ? '#ccc' : '#0f9fd9',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            fontSize: '14px',
                            cursor: isSubmitting ? 'not-allowed' : 'pointer',
                            fontWeight: '500'
                        }}
                    >
                        {isSubmitting ? 'Submitting...' : getActionTitle()}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default WorkflowActionModal;
