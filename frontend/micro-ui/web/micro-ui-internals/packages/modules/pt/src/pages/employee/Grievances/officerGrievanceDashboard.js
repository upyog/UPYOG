import React, { useEffect, useState, Fragment } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast } from "@upyog/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useParams,useHistory } from "react-router-dom"
import { useTranslation } from "react-i18next";
import { API_ENDPOINTS } from "../../../config/apiConfig";
const EmployeeGrievanceDashboard = ({path}) => {
    const tenantId = Digit.ULBService.getCurrentTenantId();

    const { t } = useTranslation();
    const history = useHistory();
    const [activeIndex, setActiveIndex] = useState(null);
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
    const [isOpen, setIsOpen] = useState(false);
    const [cardDetails, setCardDetails] = useState({
        totalGrievance: 0,
        inProgress: 0,
        closed: 0,
        disposed: 0
    });
    const [dataList, setDataList] = useState([]);
    const [page, setPage] = useState(1);
    const rowsPerPage = 10;

    useEffect(() => {
        fetchCardDetails();
        // Fetch grievances data here and set it to state
        fetchGrievances();
    }, []);
    const formateDate = (date) => {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return date.toLocaleDateString(undefined, options);
    }
    const calculateResponseDays = (createdAt, responseTime) => {
        const createdDate = new Date(createdAt);
        const currentDate = new Date();
        const timeDiff = currentDate - createdDate;
        const daysDiff = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));
        const remainingDays = responseTime - daysDiff;
        return remainingDays >= 0 ? remainingDays : 0;
    }

    const fetchCardDetails = () => {
        fetch(API_ENDPOINTS.GRIEVANCE_DASHBOARD_DATA, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                "userType": userDetails?.userType || '',
                "id": userDetails?.id || ''
            })
        })
            .then(response => response.json())
            .then(data => {
                console.log("Card details data==", data);
                if (data?.response) {
                    setCardDetails({
                        totalGrievance: data?.response?.total_count || 0,
                        inProgress: data.response.pending_count || 0,
                        closed: data.response.closed_count || 0,
                        disposed: data.response.disposed_count || 0
                    });
                }
            })
            .catch(error => {
                console.error("Error fetching card details:", error);
            });
    }

    const fetchGrievances = () => {
        // Mock data for grievances

       let reqObj = {};
        if(userDetails?.userType=='Nodal') {
        reqObj = {
            "data": {
                "user_type": 'Nodal',
                orgcode: userDetails?.organization || ''
            }
        }
        }else if(userDetails?.userType=='Appellate Officer' || userDetails?.userType=='Sub-Appellate Officer') {
        reqObj = {
            "data": {
                "user_type": userDetails?.userType,
                orgcode: userDetails?.organization || '',
                "assign_to": userDetails?.id || ''
            }
        }
        } else {
        reqObj = {
            "data": {
                "assign_to": userDetails?.id || ''
            }
        }
        }
        fetch(API_ENDPOINTS.GRIEVANCE_SEARCH, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(reqObj)
        })
            .then(response => response.json())
            .then(data => {
                // Set the fetched data to state (if needed)
                console.log("Grievance data==", data);
                if(data?.response?.length > 0) {
                    
                    let dataList = data?.response || [];
                    dataList.map((data)=>{
                        let str = data?.probable_resolution || '{}';
                        str = str.replace(/^{/, "[").replace(/}$/, "]");
                        const arr = JSON.parse(str);
                        const formattedResolution = arr
                        .map((item, index) => `${index + 1}. ${item}`)
                        .join("\n");
                        data.probable_resolution = formattedResolution;
                        let str2 = data?.probable_root_cause || '{}';
                        str2 = str2.replace(/^{/, "[").replace(/}$/, "]");
                        const arr2 = JSON.parse(str2);
                        const formattedRootCause = arr2
                        .map((item, index) => `${index + 1}. ${item}`)
                        .join("\n");
                        data.probable_resolution = formattedResolution;
                        data.probable_root_cause = formattedRootCause;
                        data.raised_on = formateDate(new Date(data?.created_at))
                        if(data?.modified_date) {
                            data.action_date = formateDate(new Date(data?.modified_date))
                        }else {
                            data.action_date = 'NA'
                        }
                        
                        if (data?.created_at && data?.categoryDetails?.stage_3_details?.response_time && data?.type=='Grievance') {
                            if (data?.created_at && data?.status!='Closed') {
                            data.expectede_response_days = calculateResponseDays(data?.created_at, data?.categoryDetails?.stage_3_details?.response_time)
                            } else {
                            data.expectede_response_days = 0
                            }              
                        } else {
                            data.expectede_response_days = 'NA'
                        }
                        
                        if (data?.categoryDetails?.stage_1_details?.[0]) {
                            data.department = data?.categoryDetails?.stage_1_details[0]?.description
                        } else {
                            data.department = ''
                        }
                        if (data?.categoryDetails?.stage_3_details) {
                            data.category = data?.categoryDetails?.stage_3_details?.description
                        } else {
                            data.category = ''
                        }
                        if(data?.status=='Pending') {
                            data.action = ['Track Grievance']
                        } else if(data?.status=='Closed') {
                            data.action = ['View Details']
                        } else if(data?.status=='Approve') {
                            data.action = ['Provide Feedback','Raise Appeal']
                        } else if(data?.status=='Appeal raised') {
                            data.action = ['Track Appeal']
                        } else if(data?.status=='Disposed') {
                            data.action = ['Share Your Feedback']
                        }

                        });
                    setDataList(dataList);
                    setPage(1);
                }
                 
            
            
            });
        const grievancesData = [
            { id: 1, question: "Grievance 1", answer: "Answer to grievance 1" },
            { id: 2, question: "Grievance 2", answer: "Answer to grievance 2" },
            { id: 3, question: "Grievance 3", answer: "Answer to grievance 3" },
        ];
        // Set the fetched data to state (if needed)
    }

    const  formatCount = (value) => { 
        return value !== undefined && value !== null
        ? String(value).padStart(2, '0')
        : '00';
    }
    const gotoGrievance = (el) => {
        history.replace(`/digit-ui/employee/pt/grievance/${el?.grievance_id}`);
        // window.location.href = `https://cpgram.pgrdigit.in/grievance/${el?.id}`;
    }

    const totalPages = Math.max(1, Math.ceil(dataList.length / rowsPerPage));
    const paginatedData = dataList.slice((page - 1) * rowsPerPage, page * rowsPerPage);

    const goFirstPage = () => setPage(1);
    const goPreviousPage = () => setPage((prev) => Math.max(1, prev - 1));
    const goNextPage = () => setPage((prev) => Math.min(totalPages, prev + 1));
    const goLastPage = () => setPage(totalPages);

      return (
        <div style={{ width: '98%', minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
            {/* Welcome Header Banner */}
            <div style={{
                background: 'linear-gradient(135deg, #0f9fd9 0%, #0d7fa8 100%)',
                padding: '5px 15px',
                marginBottom: '30px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                position: 'relative',
                overflow: 'hidden'
            }}>
                {/* Wave Pattern Background */}
                <div style={{
                    position: 'absolute',
                    top: 0,
                    right: 0,
                    width: '300px',
                    height: '100%',
                    opacity: 0.1,
                    backgroundImage: `url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 280"><defs><style>.wave{stroke:white;stroke-width:2;fill:none;}</style></defs><path class="wave" d="M0,140 Q300,40 600,140 T1200,140"/></svg>')`,
                    backgroundRepeat: 'no-repeat',
                    pointerEvents: 'none'
                }} />
                
                <h1 style={{
                    margin: 0,
                    fontSize: '18px',
                    fontWeight: '600',
                    color: 'white',
                    position: 'relative',
                    zIndex: 1
                }}>
                    Grievance Dashboard
                </h1>
            </div>

            {/* Stats Cards Section */}
            <div style={{
                padding: '0 40px',
                marginBottom: '30px'
            }}>
                <div style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(4, 1fr)',
                    gap: '20px'
                }}>
                    {/* Registered Card */}
                    <div style={{
                        backgroundColor: 'white',
                        border: '2px solid #0d7fa8',
                        borderRadius: '8px',
                        padding: '20px',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '15px',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.08)'
                    }}>
                        <div style={{
                            fontSize: '40px',
                            fontWeight: '700',
                            color: '#0d7fa8',
                            minWidth: '70px'
                        }}>
                            {formatCount(cardDetails.totalGrievance)}
                        </div>
                        <div style={{
                            fontSize: '14px',
                            color: '#666',
                            fontWeight: '500'
                        }}>
                            Grievances Registered
                        </div>
                    </div>

                    {/* In Process Card */}
                    <div style={{
                        backgroundColor: 'white',
                        border: '2px solid #0f4f9e',
                        borderRadius: '8px',
                        padding: '20px',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '15px',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.08)'
                    }}>
                        <div style={{
                            fontSize: '40px',
                            fontWeight: '700',
                            color: '#0f4f9e',
                            minWidth: '70px'
                        }}>
                            {formatCount(cardDetails.inProgress)}
                        </div>
                        <div style={{
                            fontSize: '14px',
                            color: '#666',
                            fontWeight: '500'
                        }}>
                            Grievances in Process
                        </div>
                    </div>

                    {/* Closed Card */}
                    <div style={{
                        backgroundColor: 'white',
                        border: '2px solid #0a9fa8',
                        borderRadius: '8px',
                        padding: '20px',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '15px',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.08)'
                    }}>
                        <div style={{
                            fontSize: '40px',
                            fontWeight: '700',
                            color: '#0a9fa8',
                            minWidth: '70px'
                        }}>
                            {formatCount(cardDetails.closed)}
                        </div>
                        <div style={{
                            fontSize: '14px',
                            color: '#666',
                            fontWeight: '500'
                        }}>
                            Grievances Closed
                        </div>
                    </div>

                    {/* Disposed Card */}
                    <div style={{
                        backgroundColor: 'white',
                        border: '2px solid #0d5fa8',
                        borderRadius: '8px',
                        padding: '20px',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '15px',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.08)'
                    }}>
                        <div style={{
                            fontSize: '40px',
                            fontWeight: '700',
                            color: '#0d5fa8',
                            minWidth: '70px'
                        }}>
                            {formatCount(cardDetails.disposed)}
                        </div>
                        <div style={{
                            fontSize: '14px',
                            color: '#666',
                            fontWeight: '500'
                        }}>
                            Grievances Disposed
                        </div>
                    </div>
                </div>
            </div>

            {/* Table Section */}
            {dataList && dataList.length > 0 && (
                <div style={{ padding: '0 40px', marginBottom: '40px' }}>
                    <div style={{
                        backgroundColor: 'white',
                        borderRadius: '4px',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.08)',
                        overflow: 'hidden'
                    }}>
                        {/* Table */}
                        <div style={{ overflowX: 'auto' }}>
                            <table style={{
                                width: '100%',
                                borderCollapse: 'collapse',
                                fontSize: '14px'
                            }}>
                                <thead>
                                    <tr style={{
                                        backgroundColor: '#0f9fd9',
                                        color: 'white',
                                        fontWeight: '600',
                                        lineHeight: '25px'
                                    }}>
                                        <th style={{ padding: '12px 15px', textAlign: 'left' }}>Grievance No.</th>
                                        {/* <th style={{ padding: '12px 15px', textAlign: 'left' }}>Department</th> */}
                                        <th style={{ padding: '12px 15px', textAlign: 'left' }}>Category</th>
                                        <th style={{ padding: '12px 15px', textAlign: 'left' }}>Type</th>
                                        <th style={{ padding: '12px 15px', textAlign: 'left' }}>Raised On</th>
                                        <th style={{ padding: '12px 15px', textAlign: 'left' }}>Last Action Date</th>
                                        <th style={{ padding: '12px 15px', textAlign: 'left' }}>Expected Response (in days)</th>
                                        <th style={{ padding: '12px 15px', textAlign: 'left' }}>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {paginatedData.map((el, i) => {
                                        const serialNo = (page - 1) * rowsPerPage + i + 1;
                                        return (
                                            <tr
                                                key={el?.id || el?.grievance_id || serialNo}
                                                style={{
                                                    borderBottom: '1px solid #f0f0f0',
                                                    fontSize: '14px',
                                                    lineHeight: '22px'
                                                }}
                                                onMouseOver={(e) => {
                                                    e.currentTarget.style.backgroundColor = '#f9f9f9';
                                                }}
                                                onMouseOut={(e) => {
                                                    e.currentTarget.style.backgroundColor = 'white';
                                                }}
                                            >
                                                <td style={{ padding: '12px 15px' }}>
                                                    <a
                                                        onClick={() => gotoGrievance(el)}
                                                        style={{
                                                            color: '#0f4f9e',
                                                            textDecoration: 'none',
                                                            cursor: 'pointer',
                                                            fontWeight: '500'
                                                        }}
                                                    >
                                                        {el?.grievance_id}
                                                    </a>
                                                </td>
                                                {/* <td style={{ padding: '12px 15px' }}>{el?.department || 'N/A'}</td> */}
                                                <td style={{ padding: '12px 15px' }}>{el?.category || 'N/A'}</td>
                                                <td style={{ padding: '12px 15px' }}>{el?.type || 'N/A'}</td>
                                                <td style={{ padding: '12px 15px' }}>{el?.raised_on || 'N/A'}</td>
                                                <td style={{ padding: '12px 15px' }}>{el?.action_date || 'NA'}</td>
                                                <td style={{ padding: '12px 15px' }}>
                                                    {typeof el?.expectede_response_days === 'number'
                                                        ? el?.expectede_response_days
                                                        : 'N/A'}
                                                </td>
                                                <td style={{ padding: '12px 15px' }}>
                                                    <span style={{
                                                        display: 'inline-block',
                                                        padding: '4px 8px',
                                                        borderRadius: '4px',
                                                        fontSize: '12px',
                                                        fontWeight: '500',
                                                        color: el?.status === 'Closed' || el?.status === 'Dispose'
                                                            ? '#d4af37'
                                                            : el?.status === 'Approve'
                                                            ? '#4caf50'
                                                            : '#ff6b6b',
                                                        backgroundColor: el?.status === 'Closed' || el?.status === 'Dispose'
                                                            ? 'rgba(212, 175, 55, 0.1)'
                                                            : el?.status === 'Approve'
                                                            ? 'rgba(76, 175, 80, 0.1)'
                                                            : 'rgba(255, 107, 107, 0.1)'
                                                    }}>
                                                        {el?.status || 'N/A'}
                                                    </span>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>

                        {/* Pagination */}
                        <div style={{
                            padding: '15px 20px',
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            borderTop: '1px solid #f0f0f0',
                            backgroundColor: '#f9f9f9'
                        }}>
                            <div style={{ fontSize: '13px', color: '#666' }}>
                                Items per page:
                                <select
                                    value={rowsPerPage}
                                    onChange={(e) => {
                                        // Handle rows per page change if needed
                                    }}
                                    style={{
                                        marginLeft: '8px',
                                        padding: '4px 8px',
                                        border: '1px solid #ddd',
                                        borderRadius: '4px',
                                        cursor: 'pointer'
                                    }}
                                >
                                    <option value={5}>5</option>
                                    <option value={10}>10</option>
                                    <option value={20}>20</option>
                                </select>
                            </div>

                            <div style={{ fontSize: '13px', color: '#666' }}>
                                {(page - 1) * rowsPerPage + 1} − {Math.min(page * rowsPerPage, dataList.length)} of {dataList.length}
                            </div>

                            <div style={{ display: 'flex', gap: '5px' }}>
                                <button
                                    onClick={goFirstPage}
                                    disabled={page === 1}
                                    style={{
                                        padding: '6px 10px',
                                        backgroundColor: page === 1 ? '#f5f5f5' : 'white',
                                        color: page === 1 ? '#ccc' : '#666',
                                        border: '1px solid #ddd',
                                        borderRadius: '4px',
                                        cursor: page === 1 ? 'not-allowed' : 'pointer',
                                        fontSize: '12px'
                                    }}
                                >
                                    ◀◀
                                </button>
                                <button
                                    onClick={goPreviousPage}
                                    disabled={page === 1}
                                    style={{
                                        padding: '6px 10px',
                                        backgroundColor: page === 1 ? '#f5f5f5' : 'white',
                                        color: page === 1 ? '#ccc' : '#666',
                                        border: '1px solid #ddd',
                                        borderRadius: '4px',
                                        cursor: page === 1 ? 'not-allowed' : 'pointer',
                                        fontSize: '12px'
                                    }}
                                >
                                    ◀
                                </button>
                                <span style={{ padding: '6px 15px', color: '#666', fontSize: '12px' }}>
                                    Page {page} of {totalPages}
                                </span>
                                <button
                                    onClick={goNextPage}
                                    disabled={page === totalPages}
                                    style={{
                                        padding: '6px 10px',
                                        backgroundColor: page === totalPages ? '#f5f5f5' : 'white',
                                        color: page === totalPages ? '#ccc' : '#666',
                                        border: '1px solid #ddd',
                                        borderRadius: '4px',
                                        cursor: page === totalPages ? 'not-allowed' : 'pointer',
                                        fontSize: '12px'
                                    }}
                                >
                                    ▶
                                </button>
                                <button
                                    onClick={goLastPage}
                                    disabled={page === totalPages}
                                    style={{
                                        padding: '6px 10px',
                                        backgroundColor: page === totalPages ? '#f5f5f5' : 'white',
                                        color: page === totalPages ? '#ccc' : '#666',
                                        border: '1px solid #ddd',
                                        borderRadius: '4px',
                                        cursor: page === totalPages ? 'not-allowed' : 'pointer',
                                        fontSize: '12px'
                                    }}
                                >
                                    ▶▶
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
      );
};

export default EmployeeGrievanceDashboard