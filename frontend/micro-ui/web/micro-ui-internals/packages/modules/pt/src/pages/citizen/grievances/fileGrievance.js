import React, { useCallback, useEffect, useState, useRef, Fragment } from "react";
import { useTranslation } from "react-i18next";
import { API_ENDPOINTS } from "../../../config/apiConfig";
import { Switch, useLocation, Link, useNavigate, useHistory } from "react-router-dom";

const FileGrievance = ({
  
}) => {
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const history = useHistory();

    const { t } = useTranslation();
    const [activeIndex, setActiveIndex] = useState(null);
    const [isRecording, setIsRecording] = useState(false);
    const [recognizedText, setRecognizedText] = useState('');
    const [mediaRecorder, setMediaRecorder] = useState(null);
    const [audioChunks, setAudioChunks] = useState([]);
    const [suggestedCategories, setSuggestedCategories] = useState([]);
    const [suggestedDept, setSuggestedDept] = useState(null);
    const [field_attributes, setFieldAttributes] = useState('');
    const [categoryWithAi, setCategoryWithAi] = useState(false);
    const [nlpSearchData, setNlpSearchData] = useState({});
    const [type, setType] = useState(null); // true for suggestion, false for grievance
    const [formData, setFormData] = useState({});
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
    const [uploadedFile, setUploadedFile] = useState(null);
    const [isChecked, setIsChecked] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);
    const [isLoadingQuerySearch, setIsLoadingQuerySearch] = useState(false);
    const [isLoadingClassification, setIsLoadingClassification] = useState(false);
    const processedTextsRef = useRef(new Set()); // Track processed texts to prevent duplicates

    // Manual category selection states
    const [manualCategoryMode, setManualCategoryMode] = useState(false);
    const [manualL1Categories, setManualL1Categories] = useState([]);
    const [manualL2Categories, setManualL2Categories] = useState([]);
    const [manualL3Categories, setManualL3Categories] = useState([]);
    const [selectedManualDept, setSelectedManualDept] = useState({
        stage_1_details: {},
        stage_2_details: {},
        stage_3_details: {}
    });
    const [manualContactForm, setManualContactForm] = useState({});
    const [showConfirmationModal, setShowConfirmationModal] = useState(false);
    const [grievanceDataForSubmission, setGrievanceDataForSubmission] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Translation states (matching Angular pattern)
    const [translatedText, setTranslatedText] = useState('');
    const [recognitionOther, setRecognitionOther] = useState(''); // Dual-text for non-English
    const [detectedLanguage, setDetectedLanguage] = useState('en');
    const [isLoadingTranslation, setIsLoadingTranslation] = useState(false);
    const [showTranslation, setShowTranslation] = useState(false);
    const [englishText, setEnglishText] = useState(''); // English version for API calls (separate from textarea)
    const translateDebounceRef = useRef(null); // Debounce timer for translation API

    const handleFormChange = (fieldName, value) => {
        setFormData(prev => ({
            ...prev,
            [fieldName]: value
        }));
    };

    const handleFileUpload = (event) => {
        if (event.target.files && event.target.files[0]) {
            setUploadedFile(event.target.files[0]);
        }
    };

    const getDepartments = async (params, level) => {
        try {
            console.log(`getDepartments for level ${level} with params:`, params);
            const response = await fetch(API_ENDPOINTS.DEPT_SEARCH, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ data: params })
            });
            const res = await response.json();
            console.log(`Fetched departments for level ${level}:`, res);
            if (res && res.success && Array.isArray(res.response)) {
                console.log(`Fetched departments for level ${level}:`, res.response);
                if (level === 'l1') {
                    setManualL1Categories(res.response);
                } else if (level === 'l2') {
                    setManualL2Categories(res.response);
                } else if (level === 'l3') {
                    setManualL3Categories(res.response);
                }
            }
        } catch (err) {
            console.error('Error fetching departments:', err);
        }
    };

    const onSelectDept = (event, level) => {
        if (event && event?.id) {
            if (level === 'l1') {
                setSelectedManualDept(prev => ({
                    ...prev,
                    stage_1_details: event,
                    stage_2_details: {},
                    stage_3_details: {}
                }));
                setManualL2Categories([]);
                setManualL3Categories([]);
                getDepartments({ parent: event?.id }, 'l2');
            } else if (level === 'l2') {
                setSelectedManualDept(prev => ({
                    ...prev,
                    stage_2_details: [event],
                    stage_3_details: {}
                }));
                setManualL3Categories([]);
                getDepartments({ parent: event?.id }, 'l3');
            } else if (level === 'l3') {
                setSelectedManualDept(prev => ({
                    ...prev,
                    stage_3_details: event
                }));
            }
        }
    };

    const handleCancel = () => {
        setRecognizedText('');
        setSuggestedCategories([]);
        setSuggestedDept(null);
        setCategoryWithAi(false);
        setFormData({});
        setUploadedFile(null);
        setIsChecked(false);
        setNlpSearchData({});
        setType(null);
        setManualCategoryMode(false);
        setSelectedManualDept({ stage_1_details: {}, stage_2_details: {}, stage_3_details: {} });
        setManualContactForm({});
        setManualL1Categories([]);
        setManualL2Categories([]);
        setManualL3Categories([]);
        setTranslatedText('');
        setRecognitionOther('');
        setDetectedLanguage('en');
        setShowTranslation(false);
        setEnglishText(''); // Clear English text for API calls
        // Clear debounce timer
        if (translateDebounceRef.current) {
            clearTimeout(translateDebounceRef.current);
        }
        processedTextsRef.current.clear(); // Clear processed texts on cancel
    };

    const confirmGrievanceSubmission = async () => {
        if (!grievanceDataForSubmission) return;

        setIsSubmitting(true);
        try {
            // Remove display-only fields before sending to API
            const { uploaded_file, department_name, category_name, subcategory_name, ...payloadData } = grievanceDataForSubmission;

            // Prepare the final payload with { data: {...} } wrapper
            const finalPayload = {
                data: payloadData
            };

            console.log('Submitting grievance data:', finalPayload);

            // Using the CREATE_GRIEVANCE endpoint if available, fallback to GRIEVANCE_SEARCH
            const endpoint = API_ENDPOINTS.CREATE_GRIEVANCE || API_ENDPOINTS.GRIEVANCE_SEARCH;
            
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(finalPayload)
            });

            const result = await response.json();
            console.log('Grievance submission response:', result);

            if (result && result.success) {
                alert('Grievance submitted successfully!');
                console.log('Grievance ID:', result.response?.id || result.data?.id);
                setShowConfirmationModal(false);
                handleCancel(); // Clear the form
                // Optionally navigate to dashboard or show success message
                // router.push('/dashboard');
                history.replace("/digit-ui/citizen/pt/pt-grievance-dashboard");
            } else {
                alert(result?.message || 'Failed to submit grievance');
                console.error('API Error:', result);
            }
        } catch (error) {
            console.error('Error submitting grievance:', error);
            alert('Something went wrong while submitting your grievance!');
        } finally {
            setIsSubmitting(false);
        }
    };

    const closeConfirmationModal = () => {
        setShowConfirmationModal(false);
        setGrievanceDataForSubmission(null);
    };

    const handleSubmit = () => {
        try {
            // Validate that required fields are filled
            if (!recognizedText.trim()) {
                alert('Please describe your grievance or suggestion');
                return;
            }

            if (!suggestedDept) {
                alert('Please select a category');
                return;
            }

            if (type === null) {
                alert('Please select grievance type');
                return;
            }

            if (type === false && !isChecked) {
                alert('Please accept the declaration');
                return;
            }

            // Prepare field details from suggestedDept - maintain full structure
            let fieldDetailsArray = [];
            if (suggestedDept?.stage_3_details?.field_details && Array.isArray(suggestedDept.stage_3_details.field_details)) {
                fieldDetailsArray = suggestedDept.stage_3_details.field_details.map((field) => ({
                    name: field.model_name,
                    label: field.field_name || field.model_name,
                    type: field.field_type?.id || 'text',
                    placeholder: field.placeholder || `Enter ${field.field_name}`,
                    value: formData[field.model_name] || field.value || '',
                    validators: {
                        required: field.is_mandetory || field.required || false
                    }
                }));
            }

            // Get user ID from context (default to empty, should be set from auth context)
            const userId = '15'; // This should come from Digit.ULBService.getCurrentUser() or similar

            // Prepare the submission data object matching exact payload structure
            const submissionData = {
                // Contact information
                name: userDetails?.name || 'N/A',
                country: userDetails?.country || 'India',
                state: userDetails?.state || '',
                district: userDetails?.district || '',
                address: userDetails?.address || '',
                address2: userDetails?.address2 || null,
                pinCode: userDetails?.pinCode || '',
                emailId: userDetails?.email || 'N/A',
                mobileNo: userDetails?.mobileNo || 'N/A',
                
                // Form fields from the dynamic fields
                field_details: fieldDetailsArray,
                
                // User references
                raised_by: userId,
                assign_by: userId,
                
                // Grievance content
                description_en: englishText || recognizedText || '',
                description_other: showTranslation ? recognizedText : '',
                
                // Type classification
                type: type === true ? 'Suggestion' : 'Grievance',
                
                // Department and category details
                deptid: suggestedDept?.stage_3_details?.id?.toString() || '',
                assign_to: suggestedDept?.stage_3_details?.monitoringcode?.toString() || '',
                
                // NLP extracted data (should be arrays, not stringified)
                probable_resolution: Array.isArray(nlpSearchData?.probable_resolution) 
                    ? nlpSearchData.probable_resolution 
                    : [],
                probable_root_cause: Array.isArray(nlpSearchData?.probable_root_cause) 
                    ? nlpSearchData.probable_root_cause 
                    : [],
                sentiment: nlpSearchData?.sentiment || '',
                sentiment_percent: nlpSearchData?.sentiment_percent || 0,
                summary: nlpSearchData?.summary || '',
                
                // Metadata
                channel: 'Web',
                created_at: new Date().toISOString()
            };

            // Store display data for modal (includes extra display fields)
            const displayData = {
                ...submissionData,
                uploaded_file: uploadedFile,
                department_name: suggestedDept?.stage_1_details?.description || 'N/A',
                category_name: suggestedDept?.stage_2_details?.description || 'N/A',
                subcategory_name: suggestedDept?.stage_3_details?.description || 'N/A'
            };

            // Show confirmation modal with the prepared data
            setGrievanceDataForSubmission(displayData);
            setShowConfirmationModal(true);

        } catch(error) {
            console.error('Error in handleSubmit:', error);
            alert('Something went wrong!');
        }
    };

    const toggleRecording = async () => {
        if (isRecording) {
            mediaRecorder.stop();
            setIsRecording(false);
            return;
        }

        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        const recorder = new MediaRecorder(stream);
        setMediaRecorder(recorder);
        const chunks = [];
        setAudioChunks(chunks);

        recorder.ondataavailable = (event) => {
            chunks.push(event.data);
        };

        recorder.onstop = () => {
            const audioBlob = new Blob(chunks, { type: 'audio/webm' });
            sendToAWS(audioBlob);
        };

        recorder.start();
        setIsRecording(true);
        setRecognizedText(''); // Clear previous text
    };

    const sendToAWS = async (audioBlob) => {
        const formData = new FormData();
        formData.append('audio', audioBlob);

        try {
            console.log('Sending audio to AWS for transcription...',API_ENDPOINTS.TRANSCRIBE);
            const response = await fetch(API_ENDPOINTS.TRANSCRIBE, {
                method: 'POST',
                body: formData
            });
            const res = await response.json();
            if (res) {
                // AWS Transcribe response format:
                // - recognizedText: Original recognized text in input language
                // - recognitionOther: English translation (if not English)
                // - detectedLanguage: Detected language code (e.g., 'hi', 'en')
                
                const detectedLang = res.detectedLanguage || 'en-us';
                setDetectedLanguage(detectedLang);
                
                if (detectedLang === 'en' || detectedLang === 'en-us') {
                    // English speech
                    setRecognizedText(res.recognizedText || '');
                    setEnglishText(res.recognizedText || ''); // Same as recognized for API calls
                    setRecognitionOther(''); // No secondary text for English
                    setTranslatedText(''); // No translation needed for English
                    setShowTranslation(false);
                } else {
                    // Non-English speech (e.g., Hindi)
                    // recognizedText: Original Hindi text (shown in textarea)
                    // recognitionOther: English translation (shown in secondary display)
                    // englishText: English translation (used for API calls)
                    setRecognizedText(res.recognizedText || ''); // Original speech in textarea (e.g., Hindi)
                    setEnglishText(res.recognitionOther || res.recognizedText || ''); // English translation for APIs
                    setRecognitionOther(res.recognizedText || ''); // Keep original for Original section (e.g., Hindi)
                    setTranslatedText(res.recognitionOther || ''); // English translation for Translation section
                    setShowTranslation(true);
                }
            }
        } catch (err) {
            console.error('Error sending audio to AWS:', err);
        }
    };

    // Debounced translation API call (800ms like Angular)
    // This ensures recognizedText is ALWAYS English for querysearch and classification
    const callTranslateAPI = useCallback((text) => {
        if (!text || !text.trim()) return;

        // Clear previous debounce timer
        if (translateDebounceRef.current) {
            clearTimeout(translateDebounceRef.current);
        }

        // Set new debounce timer (1000ms)
        translateDebounceRef.current = setTimeout(() => {
            performTranslate(text);
        }, 1000); // 1000ms debounce time 
    }, []);

    const performTranslate = async (text) => {
        if (!text || !text.trim()) return;

        setIsLoadingTranslation(true);
        try {
            const response = await fetch(API_ENDPOINTS.TRANSLATE, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ text })
            });
            const res = await response.json();
            console.log('Text translation response:', res);

            if (res) {
                setDetectedLanguage(res.sourceLang || 'en');
                
                // IMPORTANT: Keep recognizedText unchanged (what user typed in textarea)
                // Only update englishText for API calls (querysearch, classification)
                if (res.sourceLang === 'en') {
                    // English input - no translation needed
                    // englishText = original text (same as recognized)
                    setEnglishText(text);
                    setRecognitionOther('');
                    setTranslatedText('');
                    setShowTranslation(false);
                } else {
                    // Non-English input - translation provided
                    // englishText = translation (for API calls)
                    // recognizedText stays unchanged (for textarea display)
                    setEnglishText(res.translatedText || text);
                    setRecognitionOther(text); // Store original as secondary
                    setTranslatedText(res.translatedText || ''); // For UI display
                    setShowTranslation(true);
                }
            }
        } catch (err) {
            console.error('Error calling translation API:', err);
        } finally {
            setIsLoadingTranslation(false);
        }
    };

    const debounceRef = useRef(null);

    const textClassification = useCallback(async (attributes, textToClassify) => {
        if (!textToClassify.trim()) return;

        setIsLoadingClassification(true);
        try {
            const response = await fetch(API_ENDPOINTS.CHECK_SUGGESTION_OR_GRIEVANCE, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    data: textToClassify,
                    attributes: attributes
                })
            });
            const data = await response.json();

            if (data && data.status === 200) {
                setNlpSearchData(data.response);

                if (data.response?.type === 'suggestion' || data.response?.type === 'Suggestion') {
                    setType(true);
                } else if (data.response?.type === 'grievance' || data.response?.type === 'Grievance') {
                    setType(false);
                }

                // Update field values from response using functional update
                setSuggestedDept(prevDept => {
                    if (prevDept?.stage_3_details?.field_details) {
                        const updatedFields = prevDept.stage_3_details.field_details.map(field => ({
                            ...field,
                            value: data.response[field.model_name] || ''
                        }));
                        return {
                            ...prevDept,
                            stage_3_details: {
                                ...prevDept.stage_3_details,
                                field_details: updatedFields
                            }
                        };
                    }
                    return prevDept;
                });

                openGrievanceForm();
            }
        } catch (err) {
            console.error('Error in textClassification:', err);
        } finally {
            setIsLoadingClassification(false);
        }
    }, []); // Empty dependencies - no external dependencies needed

    const openGrievanceForm = () => {
        // Implement form opening logic here
        console.log('Opening grievance form with type:', type);
        // You can add navigation or form display logic here
    };

    // const [isProcessing, setIsProcessing] = useState(false);

    const sendMessage = useCallback(async () => {
        // Use englishText for API calls (always English)
        // englishText is only set after translation API confirms language
        if (!englishText.trim() || isProcessing) return;

        // Check if this text has already been processed
        if (processedTextsRef.current.has(englishText)) {
            return;
        }

        // Mark this text as processed
        processedTextsRef.current.add(englishText);
        setIsProcessing(true);
        setIsLoadingQuerySearch(true);

        try {
            // IMPORTANT: Using englishText which is ALWAYS English (either original or translated)
            // Both querysearch and classification use English text only - called ONCE per input
            const response = await fetch(API_ENDPOINTS.QUERY_SEARCH, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                // Using English text for querysearch
                body: JSON.stringify({ query: englishText })
            });
            const data = await response.json();

            if (data && data.length > 0) {
                const updatedCategories = data.map((item, index) => ({
                    ...item,
                    isSelected: index === 0
                }));
                setSuggestedCategories(updatedCategories);
                setSuggestedDept(updatedCategories[0]);
                let attributes = '';
                if (updatedCategories[0]?.stage_3_details?.field_details?.length > 0) {
                    attributes = updatedCategories[0].stage_3_details.field_details
                        .map(field => field?.model_name)
                        .filter(Boolean)
                        .join(',');
                    setFieldAttributes(attributes);
                    // Using English text for classification
                    
                }
                await textClassification(attributes, englishText);
                setCategoryWithAi(true);
            }
        } catch (err) {
            console.error('Error in sendMessage:', err);
        } finally {
            setIsProcessing(false);
            setIsLoadingQuerySearch(false);
        }
    }, [englishText, isProcessing]); // Use englishText instead of recognizedText

    const sendMessageRef = useRef();

    // Update ref whenever sendMessage changes
    useEffect(() => {
        sendMessageRef.current = sendMessage;
    }, [sendMessage]);

    useEffect(() => {
        // Clear existing timeout
        if (debounceRef.current) {
            clearTimeout(debounceRef.current);
        }

        // Only trigger API calls when englishText is ready (after translation)
        // This ensures querysearch and classification are called only ONCE per language detection
        if (englishText.trim() && !isProcessing) {
            debounceRef.current = setTimeout(() => {
                sendMessageRef.current?.();
            }, 1000); // 1 second debounce
        }

        return () => {
            if (debounceRef.current) {
                clearTimeout(debounceRef.current);
            }
        };
    }, [englishText, isProcessing]); // Use englishText instead of recognizedText

    // Fetch initial L1 categories on mount
    useEffect(() => {
        getDepartments({parent: 0}, 'l1');
    }, []);

      return (
        <div style={{ width: "98%", padding: "20px" }}>
            {/* Header Section */}
            <div style={{display: "flex", justifyContent: "space-between", marginBottom: "20px", padding: "0px 10px"}}>
                <header style={{fontSize: "18px", fontWeight: "600"}}>File Your Grievance</header>
            </div>

            {/* Textarea with Mic Icon */}
            <div style={{ marginBottom: "30px", padding: "0px 10px" }}>
                <div style={{ position: 'relative', display: 'inline-block', width: '100%' }}>
                    <textarea 
                        cols="30" 
                        rows="6" 
                        placeholder="Describe your grievance or suggestion..."
                        style={{ width: '100%', padding: '10px', borderRadius: '4px', border: '1px solid #ccc', fontFamily: 'Arial' }}
                        value={recognizedText}
                        onChange={(e) => {
                            setRecognizedText(e.target.value);
                            // Call translation API when text changes
                            if (e.target.value && e.target.value.trim()) {
                                callTranslateAPI(e.target.value);
                            } else {
                                setShowTranslation(false);
                                setTranslatedText('');
                            }
                        }}
                    ></textarea>
                    <span 
                        style={{ 
                            position: 'absolute', 
                            bottom: '10px', 
                            right: '10px', 
                            cursor: 'pointer', 
                            fontSize: '22px',
                            color: isRecording ? '#d32f2f' : '#212121',
                            animation: isRecording ? 'mic-blink 1s infinite' : 'none',
                            transition: 'color 0.2s ease-in-out'
                        }} 
                        onClick={toggleRecording}
                        title={isRecording ? 'Stop Recording' : 'Start Recording'}
                    >
                        {isRecording ? '🎙️' : '🎙️'}
                    </span>
                </div>
                <style>
                    {`@keyframes mic-blink { 0%, 100% { opacity: 1; } 50% { opacity: 0.2; } }
                     @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`}
                </style>

                {/* Translation Section - Dual Text Display (matching Angular pattern) */}
                {showTranslation && recognitionOther && (
                    <div style={{
                        marginTop: '20px',
                        backgroundColor: '#f5f5f5',
                        border: '1px solid #e0e0e0',
                        borderRadius: '4px',
                        padding: '15px'
                    }}>
                        {/* <a 
                            href="#" 
                            onClick={(e) => {
                                e.preventDefault();
                                setShowTranslation(!showTranslation);
                            }}
                            style={{
                                color: '#0f4f9e',
                                textDecoration: 'underline',
                                fontSize: '13px',
                                fontWeight: '500',
                                cursor: 'pointer'
                            }}
                        >
                            English Translation
                        </a> */}
                        
                        {showTranslation && (
                            <div style={{
                                marginTop: '12px'
                            }}>
                                {/* Original Text in Other Language */}
                                {/* <div style={{
                                    backgroundColor: '#e3f2fd',
                                    padding: '12px',
                                    borderRadius: '4px',
                                    border: '1px solid #90caf9',
                                    fontSize: '13px',
                                    color: '#555',
                                    marginBottom: '10px',
                                    lineHeight: '1.5'
                                }}>
                                    <strong style={{ color: '#0f4f9e', fontSize: '12px' }}>Original ({detectedLanguage}):</strong>
                                    <div style={{ marginTop: '8px', fontStyle: 'italic' }}>
                                        {recognitionOther}
                                    </div>
                                </div> */}

                                {/* Translated Text (English) */}
                                <div style={{
                                    backgroundColor: 'white',
                                    padding: '12px',
                                    borderRadius: '4px',
                                    border: '1px solid #ddd',
                                    fontSize: '14px',
                                    color: '#333',
                                    lineHeight: '1.5'
                                }}>
                                    {isLoadingTranslation ? (
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                            <div style={{
                                                width: '14px',
                                                height: '14px',
                                                border: '2px solid #f3f3f3',
                                                borderTop: '2px solid #0f4f9e',
                                                borderRadius: '50%',
                                                animation: 'spin 1s linear infinite'
                                            }}></div>
                                            <span>Translating...</span>
                                        </div>
                                    ) : (
                                        <>
                                            <strong style={{ color: '#0f4f9e', fontSize: '12px' }}>English Translation:</strong>
                                            <div style={{ marginTop: '8px' }}>
                                                {translatedText || recognizedText}
                                            </div>
                                        </>
                                    )}
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>

            {/* Loading Spinner for Query Search */}
            {isLoadingQuerySearch && (
                <div style={{ padding: '0px 10px', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div style={{
                        width: '20px',
                        height: '20px',
                        border: '3px solid #f3f3f3',
                        borderTop: '3px solid #0f4f9e',
                        borderRadius: '50%',
                        animation: 'spin 1s linear infinite'
                    }}></div>
                    <span style={{ fontSize: '14px', color: '#666' }}>Analyzing your grievance...</span>
                </div>
            )}

            {/* Manual Category Selection Mode */}
            {manualCategoryMode && (
                <div style={{ padding: '0px 10px', marginBottom: '30px' }}>
                    {/* Navigation Link - Go back to Suggested Categories (Always Visible) */}
                    {suggestedDept && (
                        <div style={{ marginBottom: '15px', fontSize: '14px' }}>
                            <a 
                                href="#" 
                                style={{ color: '#0f4f9e', cursor: 'pointer', textDecoration: 'underline' }}
                                onClick={(e) => {
                                    e.preventDefault();
                                    setManualCategoryMode(false);
                                    setCategoryWithAi(true);
                                }}
                            >
                                ← Go with suggested categories
                            </a>
                        </div>
                    )}

                    {/* Category Selection Box */}
                    <div style={{ 
                        backgroundColor: '#f5f5f5', 
                        padding: '20px', 
                        borderRadius: '4px',
                        marginBottom: '20px'
                    }}>
                        {/* <h3 style={{ marginTop: '0', marginBottom: '15px', color: '#0f4f9e' }}>Select Category</h3> */}
                        
                        {/* Level 1 Selection - Department */}
                        <div style={{ marginBottom: '15px' }}>
                            <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: '600' }}>Department *</label>
                            <select 
                                onChange={(e) => {
                                    const selected = manualL1Categories.find(cat => cat.id === e.target.value);
                                    if (selected) onSelectDept(selected, 'l1');
                                }}
                                style={{
                                    width: '100%',
                                    padding: '10px',
                                    border: '1px solid #bbb',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    boxSizing: 'border-box'
                                }}
                            >
                                <option value="">-- Select Department --</option>
                                {manualL1Categories.map(cat => (
                                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                                ))}
                            </select>
                        </div>

                        {/* Level 2 Selection - Category */}
                        {selectedManualDept?.stage_1_details?.id && (
                            <div style={{ marginBottom: '15px' }}>
                                <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: '600' }}>Category *</label>
                                <select 
                                    onChange={(e) => {
                                        const selected = manualL2Categories.find(cat => cat.id === e.target.value);
                                        if (selected) onSelectDept(selected, 'l2');
                                    }}
                                    style={{
                                        width: '100%',
                                        padding: '10px',
                                        border: '1px solid #bbb',
                                        borderRadius: '4px',
                                        fontSize: '14px',
                                        boxSizing: 'border-box'
                                    }}
                                >
                                    <option value="">-- Select Category --</option>
                                    {manualL2Categories.map(cat => (
                                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                                    ))}
                                </select>
                            </div>
                        )}

                        {/* Level 3 Selection - Sub-Category */}
                        {selectedManualDept?.stage_2_details?.id && (
                            <div style={{ marginBottom: '15px' }}>
                                <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: '600' }}>Sub-Category *</label>
                                <select 
                                    onChange={(e) => {
                                        const selected = manualL3Categories.find(cat => cat.id === e.target.value);
                                        if (selected) onSelectDept(selected, 'l3');
                                    }}
                                    style={{
                                        width: '100%',
                                        padding: '10px',
                                        border: '1px solid #bbb',
                                        borderRadius: '4px',
                                        fontSize: '14px',
                                        boxSizing: 'border-box'
                                    }}
                                >
                                    <option value="">-- Select Sub-Category --</option>
                                    {manualL3Categories.map(cat => (
                                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                                    ))}
                                </select>
                            </div>
                        )}
                    </div>

                    {/* Show Form Fields After Sub-Category Selection */}
                    {selectedManualDept?.stage_3_details?.field_details && selectedManualDept?.stage_3_details?.field_details?.length > 0 && (
                        <>
                            {/* Form Section Header - Only for Grievance (type === false) */}
                            {type === false && (
                                <div style={{ 
                                    backgroundColor: '#e8eef7', 
                                    padding: '12px 10px', 
                                    marginBottom: '20px', 
                                    fontSize: '14px',
                                    color: '#0f4f9e',
                                    fontWeight: '600'
                                }}>
                                    Please provide below details
                                </div>
                            )}

                            {/* Dynamic Form Fields from field_details - Only for Grievance (type === false) */}
                            {type === false && (
                                <div style={{ padding: '0px 10px', marginBottom: '20px' }}>
                                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
                                        {selectedManualDept?.stage_3_details?.field_details?.map((field, index) => (
                                            <div key={field.model_name || index}>
                                                <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: '500' }}>
                                                    {field.field_name || field.model_name}
                                                    {field.is_mandetory && <span style={{ color: 'red' }}>*</span>}
                                                </label>
                                                {field.field_type?.id === 'date' ? (
                                                    <input 
                                                        type="date"
                                                        value={formData[field.model_name] || field.value || ''}
                                                        onChange={(e) => handleFormChange(field.model_name, e.target.value)}
                                                        style={{
                                                            width: '100%',
                                                            padding: '10px',
                                                            border: '1px solid #bbb',
                                                            borderBottom: '2px solid #999',
                                                            fontSize: '14px',
                                                            boxSizing: 'border-box'
                                                        }}
                                                    />
                                                ) : (
                                                    <input 
                                                        type="text"
                                                        placeholder={field.placeholder || `Enter ${field.field_name}`}
                                                        value={formData[field.model_name] || field.value || ''}
                                                        onChange={(e) => handleFormChange(field.model_name, e.target.value)}
                                                        style={{
                                                            width: '100%',
                                                            padding: '10px',
                                                            border: '1px solid #bbb',
                                                            borderBottom: '2px solid #999',
                                                            fontSize: '14px',
                                                            boxSizing: 'border-box'
                                                        }}
                                                    />
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {/* File Upload Section - Only for Grievance (type === false) */}
                            {type === false && (
                                <div style={{ padding: '0px 10px', marginBottom: '20px' }}>
                                    <label style={{ display: 'block', marginBottom: '10px', fontSize: '14px', fontWeight: '500' }}>
                                        Upload Documents(if any):
                                    </label>
                                    <input 
                                        type="file"
                                        onChange={handleFileUpload}
                                        style={{
                                            padding: '10px',
                                            border: '1px solid #bbb',
                                            borderRadius: '4px',
                                            fontSize: '14px'
                                        }}
                                    />
                                    {uploadedFile && (
                                        <div style={{ marginTop: '5px', fontSize: '12px', color: '#666' }}>
                                            Selected: {uploadedFile.name}
                                        </div>
                                    )}
                                </div>
                            )}

                            {/* Checkbox Section - Only for Grievance (type === false) */}
                            {type === false && (
                                <div style={{ padding: '0px 10px', marginBottom: '20px' }}>
                                    <label style={{ display: 'flex', alignItems: 'flex-start', gap: '10px', fontSize: '14px' }}>
                                        <input 
                                            type="checkbox"
                                            checked={isChecked}
                                            onChange={(e) => setIsChecked(e.target.checked)}
                                            style={{ marginTop: '3px', cursor: 'pointer' }}
                                        />
                                        <span>I hear by State that the facts mentioned above are true to the best of my knowledge and belief</span>
                                    </label>
                                </div>
                            )}

                            {/* Date and Buttons Section */}
                            <div style={{ 
                                display: 'flex', 
                                justifyContent: 'space-between', 
                                alignItems: 'center', 
                                padding: '20px 10px',
                                borderTop: '1px solid #ddd'
                            }}>
                                <div style={{ fontSize: '12px', color: '#666' }}>
                                    Date: {new Date().toLocaleDateString()}
                                </div>
                                <div style={{ display: 'flex', gap: '10px' }}>
                                    <button
                                        onClick={handleCancel}
                                        style={{
                                            padding: '10px 30px',
                                            backgroundColor: '#dc3545',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '4px',
                                            fontSize: '14px',
                                            cursor: 'pointer',
                                            fontWeight: '500'
                                        }}
                                    >
                                        Cancel Application
                                    </button>
                                    <button
                                        onClick={handleSubmit}
                                        disabled={type === false && !isChecked}
                                        style={{
                                            padding: '10px 30px',
                                            backgroundColor: type === true || isChecked ? '#28a745' : '#ccc',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '4px',
                                            fontSize: '14px',
                                            cursor: type === true || isChecked ? 'pointer' : 'not-allowed',
                                            fontWeight: '500'
                                        }}
                                    >
                                        Submit Grievance
                                    </button>
                                </div>
                            </div>
                        </>
                    )}
                </div>
            )}

            {/* Show Form Only When Category is Selected (AI Mode) - Only for Grievance type */}
            {categoryWithAi && suggestedDept && !manualCategoryMode && type === false && (
                <>
                    {/* Navigation Links */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', padding: '0px 10px', fontSize: '14px' }}>
                        <a href="#" style={{ color: '#0f4f9e', cursor: 'pointer' }}>Continue with suggested categories.</a>
                        <a 
                            href="#" 
                            style={{ color: '#0f4f9e', cursor: 'pointer' }}
                            onClick={(e) => {
                                e.preventDefault();
                                setManualCategoryMode(true);
                                setCategoryWithAi(false);
                            }}
                        >
                            Choose categories manually
                        </a>
                    </div>

                    {/* Loading Spinner for Classification */}
                    {isLoadingClassification && (
                        <div style={{ 
                            backgroundColor: '#fff3cd', 
                            padding: '15px', 
                            border: '1px solid #ffc107', 
                            borderRadius: '4px', 
                            marginBottom: '20px',
                            marginLeft: '10px',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '10px'
                        }}>
                            <div style={{
                                width: '16px',
                                height: '16px',
                                border: '2px solid #f3f3f3',
                                borderTop: '2px solid #0f4f9e',
                                borderRadius: '50%',
                                animation: 'spin 1s linear infinite'
                            }}></div>
                            <span style={{ fontSize: '14px', color: '#856404' }}>Classifying your grievance...</span>
                        </div>
                    )}

                    {/* Category Hierarchy Box */}
                    {!isLoadingClassification && (
                    <div style={{ 
                        backgroundColor: '#e3f2fd', 
                        padding: '15px', 
                        border: '1px solid #90caf9', 
                        borderRadius: '4px', 
                        marginBottom: '20px',
                        marginLeft: '10px'
                    }}>
                        <div style={{ marginBottom: '8px' }}>
                            <strong>Department:</strong> {suggestedDept?.stage_1_details?.description || 'N/A'}
                        </div>
                        <div style={{ marginLeft: '20px', marginBottom: '8px' }}>
                            <strong>└ Category:</strong> {suggestedDept?.stage_2_details?.description || 'N/A'}
                        </div>
                        <div style={{ marginLeft: '40px' }}>
                            <strong>└ Sub-Category:</strong> {suggestedDept?.stage_3_details?.description || 'N/A'}
                        </div>
                    </div>
                    )}

                    {/* Type Toggle - Only show when not loading */}
                    {!isLoadingClassification && (
                    <div style={{ marginBottom: '30px', padding: '0px 10px' }}>
                        <label style={{ display: 'flex', alignItems: 'center', gap: '15px', fontSize: '16px' }}>
                            <span>Type:</span>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <span style={{ fontWeight: 'normal', color: type === false ? '#212121' : '#999' }}>Grievance</span>
                                <button
                                    onClick={() => setType(!type)}
                                    style={{
                                        width: '60px',
                                        height: '20px',
                                        borderRadius: '20px',
                                        border: 'none',
                                        backgroundColor: type === true ? '#28a745' : '#8b0808',
                                        cursor: 'pointer',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: type === true ? 'flex-end' : 'flex-start',
                                        padding: '2px',
                                        transition: 'all 0.3s ease-in-out'
                                    }}
                                >
                                    <div style={{
                                        width: '20px',
                                        height: '20px',
                                        borderRadius: '50%',
                                        backgroundColor: 'white',
                                        transition: 'all 0.3s ease-in-out'
                                    }}></div>
                                </button>
                                <span style={{ fontWeight: 'normal', color: type === true ? '#212121' : '#999' }}>Suggestion</span>
                            </div>
                        </label>
                    </div>
                    )}

                    {/* Form Section Header */}
                    {!isLoadingClassification && type === false && (
                        <div style={{ 
                            backgroundColor: '#e8eef7', 
                            padding: '12px 10px', 
                            marginBottom: '20px', 
                            fontSize: '14px',
                            color: '#0f4f9e'
                        }}>
                            Please provide below details
                        </div>
                    )}

                    {/* Dynamic Form Fields - Only for Grievance (type === false) */}
                    {!isLoadingClassification && type === false && (
                        <div style={{ padding: '0px 10px', marginBottom: '20px' }}>
                            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
                                {suggestedDept?.stage_3_details?.field_details?.map((field, index) => (
                                    <div key={field.model_name || index}>
                                        <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', fontWeight: '500' }}>
                                            {field.field_name || field.model_name}
                                            {field.required && <span style={{ color: 'red' }}>*</span>}
                                        </label>
                                        {field.field_type?.id === 'date' ? (
                                            <input 
                                                type="date"
                                                value={formData[field.model_name] || field.value || ''}
                                                onChange={(e) => handleFormChange(field.model_name, e.target.value)}
                                                style={{
                                                    width: '100%',
                                                    padding: '10px',
                                                    border: '1px solid #bbb',
                                                    borderBottom: '2px solid #999',
                                                    fontSize: '14px',
                                                    boxSizing: 'border-box'
                                                }}
                                            />
                                        ) : (
                                            <input 
                                                type="text"
                                                placeholder={field.placeholder || `Enter ${field.field_name}`}
                                                value={formData[field.model_name] || field.value || ''}
                                                onChange={(e) => handleFormChange(field.model_name, e.target.value)}
                                                style={{
                                                    width: '100%',
                                                    padding: '10px',
                                                    border: '1px solid #bbb',
                                                    borderBottom: '2px solid #999',
                                                    fontSize: '14px',
                                                    boxSizing: 'border-box'
                                                }}
                                            />
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* File Upload Section - Only for Grievance (type === false) */}
                    {!isLoadingClassification && type === false && (
                        <div style={{ padding: '0px 10px', marginBottom: '20px' }}>
                            <label style={{ display: 'block', marginBottom: '10px', fontSize: '14px', fontWeight: '500' }}>
                                Upload Documents(if any):
                            </label>
                            <input 
                                type="file"
                                onChange={handleFileUpload}
                                style={{
                                    padding: '10px',
                                    border: '1px solid #bbb',
                                    borderRadius: '4px',
                                    fontSize: '14px'
                                }}
                            />
                            {uploadedFile && (
                                <div style={{ marginTop: '5px', fontSize: '12px', color: '#666' }}>
                                    Selected: {uploadedFile.name}
                                </div>
                            )}
                        </div>
                    )}

                    {/* Checkbox Section - Only for Grievance (type === false) */}
                    {!isLoadingClassification && type === false && (
                        <div style={{ padding: '0px 10px', marginBottom: '20px' }}>
                            <label style={{ display: 'flex', alignItems: 'flex-start', gap: '10px', fontSize: '14px' }}>
                                <input 
                                    type="checkbox"
                                    checked={isChecked}
                                    onChange={(e) => setIsChecked(e.target.checked)}
                                    style={{ marginTop: '3px', cursor: 'pointer' }}
                                />
                                <span>I hear by State that the facts mentioned above are true to the best of my knowledge and belief</span>
                            </label>
                        </div>
                    )}

                    {/* Date and Buttons Section */}
                    {!isLoadingClassification && (
                    <div style={{ 
                        display: 'flex', 
                        justifyContent: 'space-between', 
                        alignItems: 'center', 
                        padding: '20px 10px',
                        borderTop: '1px solid #ddd'
                    }}>
                        <div style={{ fontSize: '12px', color: '#666' }}>
                            Date: {new Date().toLocaleDateString()}
                        </div>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button
                                onClick={handleCancel}
                                style={{
                                    padding: '10px 30px',
                                    backgroundColor: '#dc3545',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    cursor: 'pointer',
                                    fontWeight: '500'
                                }}
                            >
                                Cancel Application
                            </button>
                            <button
                                onClick={handleSubmit}
                                disabled={type === false && !isChecked}
                                style={{
                                    padding: '10px 30px',
                                    backgroundColor: type === true || isChecked ? '#28a745' : '#ccc',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    cursor: type === true || isChecked ? 'pointer' : 'not-allowed',
                                    fontWeight: '500'
                                }}
                            >
                                Submit Grievance
                            </button>
                        </div>
                    </div>
                    )}
                </>
            )}

            {/* Show Type Toggle and Submit for Suggestion Type */}
            {categoryWithAi && suggestedDept && !manualCategoryMode && type === true && (
                <div style={{ padding: '0px 10px' }}>
                    {/* Category Hierarchy Box - For Info Only */}
                    {!isLoadingClassification && type === false && (
                    <div style={{ 
                        backgroundColor: '#e3f2fd', 
                        padding: '15px', 
                        border: '1px solid #90caf9', 
                        borderRadius: '4px', 
                        marginBottom: '20px',
                        marginLeft: '0px'
                    }}>
                        <div style={{ marginBottom: '8px' }}>
                            <strong>Department:</strong> {suggestedDept?.stage_1_details?.description || 'N/A'}
                        </div>
                        <div style={{ marginLeft: '20px', marginBottom: '8px' }}>
                            <strong>└ Category:</strong> {suggestedDept?.stage_2_details?.description || 'N/A'}
                        </div>
                        <div style={{ marginLeft: '40px' }}>
                            <strong>└ Sub-Category:</strong> {suggestedDept?.stage_3_details?.description || 'N/A'}
                        </div>
                    </div>
                    )}

                    {/* Type Toggle */}
                    {!isLoadingClassification && (
                    <div style={{ marginBottom: '30px' }}>
                        <label style={{ display: 'flex', alignItems: 'center', gap: '15px', fontSize: '16px' }}>
                            <span>Type:</span>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <span style={{ fontWeight: 'normal', color: type === false ? '#212121' : '#999' }}>Grievance</span>
                                <button
                                    onClick={() => setType(!type)}
                                    style={{
                                        width: '60px',
                                        height: '20px',
                                        borderRadius: '20px',
                                        border: 'none',
                                        backgroundColor: type === true ? '#28a745' : '#8b0808',
                                        cursor: 'pointer',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: type === true ? 'flex-end' : 'flex-start',
                                        padding: '2px',
                                        transition: 'all 0.3s ease-in-out'
                                    }}
                                >
                                    <div style={{
                                        width: '20px',
                                        height: '20px',
                                        borderRadius: '50%',
                                        backgroundColor: 'white',
                                        transition: 'all 0.3s ease-in-out'
                                    }}></div>
                                </button>
                                <span style={{ fontWeight: 'normal', color: type === true ? '#212121' : '#999' }}>Suggestion</span>
                            </div>
                        </label>
                    </div>
                    )}

                    {/* Date and Buttons Section for Suggestion */}
                    {!isLoadingClassification && (
                    <div style={{ 
                        display: 'flex', 
                        justifyContent: 'space-between', 
                        alignItems: 'center', 
                        padding: '20px 0px',
                        borderTop: '1px solid #ddd'
                    }}>
                        <div style={{ fontSize: '12px', color: '#666' }}>
                            Date: {new Date().toLocaleDateString()}
                        </div>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button
                                onClick={handleCancel}
                                style={{
                                    padding: '10px 30px',
                                    backgroundColor: '#dc3545',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    cursor: 'pointer',
                                    fontWeight: '500'
                                }}
                            >
                                Cancel Application
                            </button>
                            <button
                                onClick={handleSubmit}
                                style={{
                                    padding: '10px 30px',
                                    backgroundColor: '#28a745',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    fontSize: '14px',
                                    cursor: 'pointer',
                                    fontWeight: '500'
                                }}
                            >
                                Submit Suggestion
                            </button>
                        </div>
                    </div>
                    )}
                </div>
            )}

            {/* Confirmation Modal */}
            {showConfirmationModal && grievanceDataForSubmission && (
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
                        maxWidth: '900px',
                        maxHeight: '90vh',
                        overflowY: 'auto',
                        width: '95%',
                        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)'
                    }}>
                        {/* Modal Header */}
                        <div style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            padding: '10px 20px',
                            backgroundColor: '#0f9fd9',
                            color: 'white',
                            borderTopLeftRadius: '8px',
                            borderTopRightRadius: '8px'
                        }}>
                            <h2 style={{ margin: 0, fontSize: '18px' }}>Please confirm</h2>
                            <button 
                                onClick={closeConfirmationModal}
                                style={{
                                    backgroundColor: 'transparent',
                                    border: 'none',
                                    color: 'white',
                                    fontSize: '24px',
                                    cursor: 'pointer'
                                }}
                            >
                                ×
                            </button>
                        </div>

                        {/* Modal Body */}
                        <div style={{ padding: '10px 10px' }}>
                            {/* Type Section */}
                            <div style={{ marginBottom: '30px' }}>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '15px', fontSize: '16px', fontWeight: '600' }}>
                                    <span style={{ textDecoration: 'underline' }}>Type:</span>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                        <span style={{ fontWeight: 'normal', color: grievanceDataForSubmission.type === 'Grievance' ? '#212121' : '#999' }}>Grievance</span>
                                        <div style={{
                                            width: '60px',
                                            height: '20px',
                                            borderRadius: '20px',
                                            backgroundColor: grievanceDataForSubmission.type === 'Suggestion' ? '#28a745' : '#8b0808',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: grievanceDataForSubmission.type === 'Suggestion' ? 'flex-end' : 'flex-start',
                                            padding: '2px'
                                        }}>
                                            <div style={{
                                                width: '16px',
                                                height: '16px',
                                                borderRadius: '50%',
                                                backgroundColor: 'white'
                                            }}></div>
                                        </div>
                                        <span style={{ fontWeight: 'normal', color: grievanceDataForSubmission.type === 'Suggestion' ? '#212121' : '#999' }}>Suggestion</span>
                                    </div>
                                </label>
                            </div>

                            

                            {/* For Suggestion Type - Only show type and translation section */}
                            {grievanceDataForSubmission.type === 'Suggestion' && (
                                <div style={{ padding: '20px 0', textAlign: 'center', color: '#666' }}>
                                    <p>Suggestion recorded successfully!</p>
                                </div>
                            )}

                            {/* For Grievance Type - Show all details */}
                            {grievanceDataForSubmission.type === 'Grievance' && (
                                <>
                            {/* Citizen Details */}
                            <div style={{ marginBottom: '30px' }}>
                                <h3 style={{ textDecoration: 'underline', fontSize: '16px', marginBottom: '15px' }}>Citizen Details</h3>
                                <div style={{
                                    backgroundColor: '#f0f8ff',
                                    border: '1px solid #90caf9',
                                    borderRadius: '4px',
                                    padding: '15px'
                                }}>
                                    <div style={{ marginBottom: '10px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                                        <div>
                                            <strong>Name:</strong> {grievanceDataForSubmission.name}
                                        </div>
                                        <div>
                                            <strong>Country:</strong> {grievanceDataForSubmission.country}
                                        </div>
                                    </div>
                                    <div style={{ marginBottom: '10px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                                        <div>
                                            <strong>Mobile No.:</strong> {grievanceDataForSubmission.mobileNo}
                                        </div>
                                        <div></div>
                                    </div>
                                    <div>
                                        <strong>Email ID:</strong> {grievanceDataForSubmission.emailId}
                                    </div>
                                </div>
                            </div>

                            {/* Grievance Description */}
                            <div style={{ marginBottom: '30px' }}>
                                <h3 style={{ textDecoration: 'underline', fontSize: '16px', marginBottom: '15px' }}>Grievance Description</h3>
                                <div style={{
                                    backgroundColor: '#f5f5f5',
                                    border: '1px solid #ddd',
                                    borderRadius: '4px',
                                    padding: '15px',
                                    minHeight: '80px',
                                    whiteSpace: 'pre-wrap',
                                    wordWrap: 'break-word'
                                }}>
                                    {grievanceDataForSubmission.description_other && grievanceDataForSubmission.description_en && detectedLanguage !== 'en' && (
                                        <div>
                                            {grievanceDataForSubmission.description_other}
                                            <div style={{ color: '#0f4f9e', fontSize: '12px' }}>English Translation:</div>
                                            {grievanceDataForSubmission.description_en}
                                        </div>
                                    )}
                                    {grievanceDataForSubmission.description_en && detectedLanguage === 'en' && (
                                        <div>
                                            {grievanceDataForSubmission.description_en}
                                        </div>
                                    )}
                                </div>
                            </div>

                            {/* Domain & Category */}
                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px', marginBottom: '30px' }}>
                                <div>
                                    <h3 style={{ textDecoration: 'underline', fontSize: '16px', marginBottom: '15px' }}>Domain & Category</h3>
                                    <div style={{
                                        backgroundColor: '#e3f2fd',
                                        border: '1px solid #90caf9',
                                        borderRadius: '4px',
                                        padding: '15px'
                                    }}>
                                        <div style={{ marginBottom: '8px' }}>
                                            <strong>Department:</strong> {grievanceDataForSubmission.department_name}
                                        </div>
                                        <div style={{ marginLeft: '20px', marginBottom: '8px' }}>
                                            <strong>└ Level 1:</strong> {grievanceDataForSubmission.category_name}
                                        </div>
                                        <div style={{ marginLeft: '40px' }}>
                                            <strong>└ Level 2:</strong> {grievanceDataForSubmission.subcategory_name}
                                        </div>
                                    </div>
                                </div>

                                {/* Uploaded Documents */}
                                {grievanceDataForSubmission.uploaded_file && (
                                <div>
                                    <h3 style={{ textDecoration: 'underline', fontSize: '16px', marginBottom: '15px' }}>Uploaded Documents</h3>
                                    <div style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '10px',
                                        padding: '10px',
                                        backgroundColor: '#f5f5f5',
                                        borderRadius: '4px'
                                    }}>
                                        <span style={{ fontSize: '20px' }}>📄</span>
                                        <span>{grievanceDataForSubmission.uploaded_file?.name || 'File'}</span>
                                    </div>
                                </div>
                                )}
                            </div>

                            {/* Field Details */}
                            {grievanceDataForSubmission.field_details && grievanceDataForSubmission.field_details.length > 0 && (
                            <div style={{ marginBottom: '30px' }}>
                                <h3 style={{ textDecoration: 'underline', fontSize: '16px', marginBottom: '15px' }}>Field Details</h3>
                                <div style={{
                                    backgroundColor: '#f5f5f5',
                                    border: '1px solid #ddd',
                                    borderRadius: '4px',
                                    padding: '15px',
                                    display: 'grid',
                                    gridTemplateColumns: 'repeat(2, 1fr)',
                                    gap: '15px'
                                }}>
                                    {grievanceDataForSubmission.field_details.map((field, index) => (
                                        <div key={index}>
                                            <strong>{field.label}:</strong> {field.value || 'N/A'}
                                        </div>
                                    ))}
                                </div>
                            </div>
                            )}

                            {/* Additional Details */}
                            <div style={{ marginBottom: '30px' }}>
                                <div style={{
                                    backgroundColor: '#f5f5f5',
                                    border: '1px solid #ddd',
                                    borderRadius: '4px',
                                    padding: '15px',
                                    display: 'grid',
                                    gridTemplateColumns: '1fr 1fr',
                                    gap: '15px'
                                }}>
                                    <div>
                                        <strong>Expected Resolution Time:</strong> 7 days
                                    </div>
                                    <div>
                                        <strong>Submission Date:</strong> {new Date().toLocaleDateString()}
                                    </div>
                                </div>
                            </div>
                                </>
                            )}
                        </div>

                        {/* Modal Footer */}
                        <div style={{
                            display: 'flex',
                            justifyContent: 'flex-end',
                            gap: '10px',
                            padding: '20px',
                            backgroundColor: '#f5f5f5',
                            borderTop: '1px solid #ddd'
                        }}>
                            <button
                                onClick={closeConfirmationModal}
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
                                onClick={confirmGrievanceSubmission}
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
                                {isSubmitting ? 'Submitting...' : 'Confirm'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
      );
};

export default FileGrievance;
