import React, { useEffect, useState } from "react";
import { Card, Row, CardHeader, StatusTable, Loader } from "@upyog/digit-ui-react-components";
import { stringReplaceAll } from "../../../../tl/src/utils";
import { useTranslation } from "react-i18next";
const AcknowledgementQRCode = ({ path }) => {
    const { t } = useTranslation();
    const [details, setDetails]=useState([]);
    const [acknowledgementNumber, setAcknowledgementNumber]=useState('');
    const [grievanceType, setGrievanceType]=useState();
    const [grievanceSubType, setGrievanceSubType]=useState();
    const [priorityLevel, setPriorityLevel]=useState();
    const [PGRAddress, setPGRAddress]=useState();
    const [fsmName, setFSMName]=useState();
    const [fsmmblNumber, setMblNumber]=useState();
    const [fsmPropertyId, setFSMPropertyId]=useState();
    const [fsmPropertyType, setFSMPropertyType]=useState();
    const [fsmPropertySubType, setfsmPropertySubType]=useState();
    const [fsmNoOfTrips, setNoOfTrips]=useState();
    const [fsmTotalAmount, setfsmTotalAmount]=useState();
    const [fsmAdvAmountDue, setFSMAdvAmountDue]=useState();
    const [ptOwnerName, setPTOwnername]=useState();
    const [ptmblNumber, setPTMblNumber]=useState();
    const [ptPropertyId, setPTPropertyId]=useState();
    const [ptPropertyType, setPTPropertyTYpe]=useState();
    const [ptArea, setPTArea]=useState();
    const [builtupArea, setPTBuildupArea]=useState();
    const [ptAddress, setPTAddress]=useState();
    const [wsOwnerName, setWSOwnerName]=useState();
    const [wsAddress, setWSAddress]=useState();
    const [noOfTaps, setNOOfTaps]=useState();
    const [connectSize, setConnectSize]=useState();
    const [swOwnerName, setSWOwnerName]=useState();
    const [swAddress, setSWAdrress]=useState();
    const [noofclosets, setNOOFClosets]=useState();
    const[noOfSeats, setNOOFSeats]=useState();
    const [TLOwnerName, setTLOwnerName]=useState();
    const [TLAddress, setTLAddress]=useState();
    const [TLPropertyID, setTLPropertyID]=useState();
    const [TLPropertyAddress, setTLPropertyAddress]=useState();
    const [structureType, setStructureType]=useState();
    const [TLMblNumber, setTLMblNumber]=useState();
    const [edcrNumber, setEdcrNumber]=useState();
    const [edcrApplicantName, setApplicantEdcr]=useState();
    const [bpaOwner, setBPAOwner]=useState();
    const [bpaMblNumber, setBPAMblNumber]=useState();
    const [applnType, setApplbType]=useState();
    const [serviceType, setServiceType]=useState();
    const [bpaAddress, setBPAAdress]=useState();

    console.log("ptaddress", ptAddress)
    useEffect(async()=>{
    const tenantId = window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[0].split("=")[1]
    const acknowledgementNumber = window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[1].split("=")[1]
    
    setAcknowledgementNumber(acknowledgementNumber);
    const fetchData=async()=>{
        const module=acknowledgementNumber.split("-")[1]
        const WandS=acknowledgementNumber.split("_")[0]
        
        console.log("wands", WandS)
        let details=[];
        if(module==="PT"){
            setPTPropertyId(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[2].split("=")[1]);
            setPTOwnername(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[3].split("=")[1]);
            setPTMblNumber(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[4].split("=")[1]);
            setPTPropertyTYpe(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[5].split("=")[1]);
            setPTArea(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[6].split("=")[1]);
            setPTBuildupArea(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[7].split("=")[1]);
            setPTAddress(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[8].split("=")[1])
        }
        else if(module==="PGR"){
            setGrievanceType(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[2].split("=")[1]);
            setGrievanceSubType(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[3].split("=")[1]);
            setPriorityLevel(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[4].split("=")[1]);
            setPGRAddress(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[5].split("=")[1])
        }
        else if(module==="FSM"){
            setFSMName(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[2].split("=")[1]);
            setMblNumber(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[3].split("=")[1]);
            setFSMPropertyId(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[4].split("=")[1]);
            setFSMPropertyType(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[5].split("=")[1]);
            setfsmPropertySubType(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[6].split("=")[1]);
            setNoOfTrips(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[7].split("=")[1]);
            setfsmTotalAmount(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[8].split("=")[1]);
            setFSMAdvAmountDue(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[9].split("=")[1])
        }
        else if(module==="BP"){
            setEdcrNumber(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[2].split("=")[1])
            setApplbType(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[3].split("=")[1]);
            setServiceType(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[4].split("=")[1]);
            setApplicantEdcr(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[5].split("=")[1])
            setBPAOwner(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[6].split("=")[1]);
            setBPAMblNumber(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[7].split("=")[1])
            setBPAAdress(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[8].split("=")[1]);

            
        }
        else if(module==="TL"){
            setStructureType(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[2].split("=")[1])
            setTLOwnerName(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[3].split("=")[1]);
            setTLMblNumber(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[4].split("=")[1])
            
            setTLPropertyID(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[5].split("=")[1]);
            setTLPropertyAddress(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[6].split("=")[1]);
        }
        else if(WandS==="WS"){
            setWSOwnerName(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[2].split("=")[1]);
            
            setWSAddress(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[3].split("=")[1]);
            setNOOfTaps(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[4].split("=")[1]);
            setConnectSize(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[5].split("=")[1]);
       // 
        }
        else if(WandS==="SW"){
           setSWOwnerName(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[2].split("=")[1]);
           setSWAdrress(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[3].split("=")[1]);
           setNOOFClosets(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[4].split("=")[1]);
           setNOOFSeats(window.location.href.split("/acknowledgement/details?")?.[1].split("&")?.[5].split("=")[1])
        }
        setDetails(details);
    };
    fetchData();   
    }, [acknowledgementNumber])
    
   console.log("details99", details)
    const convertEpochToDate = (dateEpoch) => {
        // Returning NA in else case because new Date(null) returns Current date from calender
        if (dateEpoch) {
            const dateFromApi = new Date(dateEpoch);
            let month = dateFromApi.getMonth() + 1;
            let day = dateFromApi.getDate();
            let year = dateFromApi.getFullYear();
            month = (month > 9 ? "" : "0") + month;
            day = (day > 9 ? "" : "0") + day;
            return `${day}/${month}/${year}`;
        } else {
            return "NA";
        }
    };
    const convertToLocale = (value = "", key = "") => {
        let convertedValue = convertDotValues(value);
        if (convertedValue == "NA") {
            return "PT_NA";
        }
        return `${key}_${convertedValue}`;
    };

    const convertDotValues = (value = "") => {
        return (
            (checkForNotNull(value) && ((value.replaceAll && value.replaceAll(".", "_")) || (value.replace && stringReplaceAll(value, ".", "_")))) || "NA"
        );
    };
    const checkForNotNull = (value = "") => {
        return value && value != null && value != undefined && value != "" ? true : false;
    };
    const getFinancialYears = (from, to) => {
        const fromDate = new Date(from);
        const toDate = new Date(to);
        if (toDate.getYear() - fromDate.getYear() != 0) {
            return `FY${fromDate.getYear() + 1900}-${toDate.getYear() - 100}`;
        }
        return `${fromDate.toLocaleDateString()}-${toDate.toLocaleDateString()}`;
    };
    console.log("gri", grievanceType)
    console.log("ddd", details)
    console.log("moduleeee", acknowledgementNumber)
    return (
        <React.Fragment>
            <div style={{ width: "100%" }}>
                <Card>
                    <CardHeader>{t("ACKNOWLEDGEMENT_DETAILS")}</CardHeader>
                    {acknowledgementNumber.includes("PT") ? (
                    <StatusTable>
                        <div>
                        <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("CS_TITLE_APPLICATION_DETAILS")}</h3>  
                        
                        <Row label ={t("PT_APPLICATION_NO")} text={acknowledgementNumber||"NA"} textStyle={{ whiteSpace: "pre" }} />
                        
                        <Row label={t("PT_PROPERTY_ID")} text={ptPropertyId|| "NA"} textStyle={{ whiteSpace: "pre" }} />
                        </div>
                        <div>
                        <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("PT_OWNERSHIP_INFO_SUB_HEADER")}</h3>  
                        
                            <div>
                                <h3 style={{paddingTop:"10px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"18px"}}></h3>
                            <Row label={t("PT_OWNERSHIP_INFO_NAME")} text={ ptOwnerName || "NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("PT_OWNERSHIP_INFO_MOBILE_NO")} text={ ptmblNumber|| "NA"} textStyle={{ whiteSpace: "pre" }} />
                            
                            </div>
                        
                        
                        </div>
                        <div>
                        <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("PT_ASSESMENT_INFO_SUB_HEADER")}</h3>  
                        
                        <Row label={t("PT_PROPERTY_TYPE")} text={ decodeURIComponent(ptPropertyType) || "NA"} textStyle={{ whiteSpace: "pre" }} />
                        
                        <Row label={t("PT_ASSESMENT_INFO_PLOT_SIZE")} text={ptArea || "NA"} textStyle={{ whiteSpace: "pre" }} />
                        <Row label={t("PT_ASSESMENT_INFO_BUILTUP_AREA")} text={builtupArea || "NA"} textStyle={{ whiteSpace: "pre" }} />  
                        
                        
                        </div>
                        <div>
                        <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("PT_PROPERTY_ADDRESS_SUB_HEADER")}</h3> 
                            <Row label={t("PT_PROPERTY_ADDRESS")} text={decodeURIComponent(ptAddress)} textStyle={{ whiteSpace: "pre" }} /> 
                            

                         </div> 
                    </StatusTable> 
                    ): (acknowledgementNumber.includes("PGR")) ? (
                        <StatusTable>
                            <div>
                            <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("CS_TITLE_APPLICATION_DETAILS")}</h3>
                            
                            <Row label={t("PGR_ACKNOWLEDGEMENT_NUMBER")} text={acknowledgementNumber||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            
                            <Row label={t("CS_COMPLAINT_TYPE")} text={grievanceType||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("CS_COMPLAINT_PRIORITY_LEVEL")} text={priorityLevel||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("CS_COMPLAINT_SUBTYPE")} text={grievanceSubType||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("CS_COMPLAINT_ADDRESS")} text={decodeURIComponent(PGRAddress)||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            </div>
                            
                        </StatusTable>
                    ): (acknowledgementNumber.includes("FSM")) ? (
                        <StatusTable>
                            <div>
                            <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("CS_TITLE_APPLICATION_DETAILS")}</h3>
                            
                            <Row label={t("PDF_HEADER_DESLUDGING_REQUEST_ACKNOWLEDGEMENT")} text={acknowledgementNumber|| "NA"} textStyle={{ whiteSpace: "pre" }} />
                            
                            </div>
                            <div>
                            <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("CS_APPLICATION_DETAILS_APPLICANT_DETAILS")}</h3>
                            <Row label={t("CS_APPLICATION_DETAILS_APPLICANT_NAME")} text={fsmName} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("CS_APPLICATION_DETAILS_APPLICANT_MOBILE")} text={fsmmblNumber} textStyle={{ whiteSpace: "pre" }} />
                           
                            </div>
                            <div>
                            <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder",fontSize:"20px"}}>{t("CS_APPLICATION_DETAILS_PROPERTY_DETAILS")}</h3> 
                            
                            <Row label={t("CS_APPLICATION_DETAILS_PROPERTY_ID")} text={decodeURIComponent(fsmPropertyId)||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("CS_APPLICATION_DETAILS_PROPERTY_TYPE")} text={`${decodeURIComponent(fsmPropertyType)}`} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("CS_APPLICATION_DETAILS_PROPERTY_SUB_TYPE")} text={`${decodeURIComponent(fsmPropertySubType)}`} textStyle={{ whiteSpace: "pre" }} />
                            </div>
                            
                            <div>
                            <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("CS_APPLICATION_DETAILS_PIT_DETAILS")}</h3> 
                            
                            
                            <Row label={t("CS_APPLICATION_DETAILS_TRIPS")} text={fsmNoOfTrips} textStyle={{ whiteSpace: "pre" }} />
                            
                            <Row label={t("CS_APPLICATION_DETAILS__TOTAL_AMOUNT")} text={decodeURIComponent(fsmTotalAmount)} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("CS_APPLICATION_DETAILS_ADV_AMOUNt_DUE")} text={decodeURIComponent(fsmAdvAmountDue)} textStyle={{ whiteSpace: "pre" }} />
                            </div>

                        </StatusTable>
                    ): (acknowledgementNumber.includes("WS")) ? (
                        
                            <StatusTable>
                                <div style={{paddignTop:"15px"}}></div>
                                <div>
                            <h2 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("WS_COMMON_PROPERTY_DETAILS")}</h2>
                                <Row label={t("WS_ACKNOWLEDGEMENT_NUMBER")} text={acknowledgementNumber} textStyle={{ whiteSpace: "pre" }} />
                                {/* <Row label={t("WS_PROPERTY_ID_LABEL")} text={details?.WaterConnection[0]?.propertyId} textStyle={{ whiteSpace: "pre" }} /> */}
                                <Row label={t("WS_OWN_DETAIL_NAME")} text={wsOwnerName} textStyle={{ whiteSpace: "pre" }} />
                                <Row label={t("WS_OWN_DETAIL_ADDR")} text={decodeURIComponent(wsAddress)} textStyle={{ whiteSpace: "pre" }} />
                                 {/* <Row label={t("WS_PROPERTY_ADDRESS_LABEL")} text={details?.WaterConnection[0]?.} textStyle={{ whiteSpace: "pre" }} />  */}
                                 </div>
                        <div>   
                        <h2 style={{paddingTop:"10px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("WS_COMMON_CONNECTION_DETAILS")}</h2>
                        
                                
                                    <div>
                                    {/* <Row label={t("WS_APPLY_FOR")} text={details?.WaterConnection[0]?.applicationType} textStyle={{ whiteSpace: "pre" }} /> */}
                                    <Row label={t("WS_TASK_DETAILS_CONN_DETAIL_NO_OF_TAPS_PROPOSED")} text={noOfTaps} textStyle={{ whiteSpace: "pre" }} />
                                    <Row label={t("WS_TASK_DETAILS_CONN_DETAIL_PIPE_SIZE_PROPOSED")} text={connectSize} textStyle={{ whiteSpace: "pre" }} />
                                    
                                    </div>
                                
                                </div>
                                
                                

                            </StatusTable>
                       
                    ): (acknowledgementNumber.includes("SW")) ? (
                        <StatusTable>
                            <div style={{paddignTop:"15px"}}></div>
                            <div>
                            <h2 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("WS_COMMON_PROPERTY_DETAILS")}</h2>
                            
                            <Row label={t("WS_ACKNOWLEDGEMENT_NUMBER")} text={acknowledgementNumber} textStyle={{ whiteSpace: "pre" }} />
                            
                            <Row label={t("WS_OWN_DETAIL_NAME")} text={swOwnerName} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("WS_OWN_DETAIL_ADDR")} text={decodeURIComponent(swAddress)} textStyle={{ whiteSpace: "pre" }} />
                            {/* <Row label={t("WS_OWN_DETAIL_NAME")} text={details?.WaterConnection[0]?.owners.sort((a,b)=>a?.additionalDetails?.ownerSequence-b?.additionalDetails?.ownerSequence)?.map(owner => owner.name)?.join(",")} textStyle={{ whiteSpace: "pre" }} /> */}
                            {/* <Row label={t("WS_PROPERTY_ADDRESS_LABEL")} text={details?.WaterConnection[0]} textStyle={{ whiteSpace: "pre" }} /> */}
                            </div>
                            <div>
                            <h2 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("WS_COMMON_CONNECTION_DETAILS")}</h2>
                        
                                
                            
                                <div>
                                {/* <Row label={t("WS_APPLY_FOR")} text={details?.SewerageConnections[0]?.applicationType} textStyle={{ whiteSpace: "pre" }} /> */}
                                <Row label={t("WS_TASK_DETAILS_CONN_DETAIL_NO_OF_CLOSETS_PROPOSED")} text={noOfSeats} textStyle={{ whiteSpace: "pre" }} />
                                <Row label={t("WS_TASK_DETAILS_CONN_DETAIL_PIPE_SIZE_PROPOSED")} text={noofclosets} textStyle={{ whiteSpace: "pre" }} />
                                
                                </div>
                            
                            </div>
                            

                    </StatusTable>
                    ):(acknowledgementNumber.includes("TL")) ? (
                        <StatusTable>
                            <div>
                            <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("TL_COMMON_TR_DETAILS")}</h3>
                            
                            <Row label={t("TL_NEW_TRADE_DETAILS_STRUCT_TYPE_LABEL")} text={structureType} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("TL_PROPERTY_ID")} text={TLPropertyID || "NA"} textStyle={{ whiteSpace: "pre" }} />
                           
                            </div>
                            <div>
                            <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("TL_OWNERSHIP_DETAILS_HEADER")}</h3>
                           
                                <div>
                                <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}></h3>
                                
                                <Row label={t("TL_OWNER_S_NAME_LABEL")} text={TLOwnerName||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("TL_OWNER_S_MOBILE_NUM_LABEL")} text={TLMblNumber||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            
                            </div>
                            
                            
                            </div>
                            
                            <div>
                                
                            <h3 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("PROPERTY_DETAILS")}</h3>
                            <Row label={t("CORE_COMMON_ADDR")} text={decodeURIComponent(TLPropertyAddress)} textStyle={{ whiteSpace: "pre" }} />
                            
                            </div>
                            {/* <Row label={t("")} text={} textStyle={{ whiteSpace: "pre" }} /> */}
                        </StatusTable>
                    ): (acknowledgementNumber.includes("BP")) ? (
                        <StatusTable>
                            <div style={{paddignTop:"15px"}}>
                            <h2 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("BPA_BASIC_DETAILS_TITLE")}</h2>
                            <Row label={t("BPA_APPLICATION_NUMBER")} text={acknowledgementNumber} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("BPA_EDCR_NO_LABEL")} text={edcrNumber || "NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("BPA_APPLICATION_TYPE")} text={decodeURIComponent(applnType)|| "NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("BPA_SERVICE_TYPE")} text={decodeURIComponent(serviceType)|| "NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("BPA_APPLICANT_NAME")} text={edcrApplicantName} textStyle={{ whiteSpace: "pre" }} />
                            
                            </div>
                            <div>
                            <h2 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("BPA_OWNER_DETAILS")}</h2>
                            <Row label={t("BPA_OWNER_NAME")} text={bpaOwner||"NA"} textStyle={{ whiteSpace: "pre" }} />
                            <Row label={t("BPA_OWNER_MBL_NUMBER")} text={bpaMblNumber||"NA"} textStyle={{ whiteSpace: "pre" }} />
                           
                            </div>
                            <div>
                            <h2 style={{paddingTop:"12px", paddingBottom:"10px", fontWeight:"bolder", fontSize:"20px"}}>{t("BPA_NEW_TRADE_DETAILS_HEADER_DETAILS")}</h2>
                            <Row label={t("BPA_ADDRESS")} text={decodeURIComponent(bpaAddress)||"NA"} textStyle={{ whiteSpace: "pre" }} />
                           
                            </div>
                            
                        </StatusTable>
                    ):null}

                   
                </Card>
            </div>
        </React.Fragment>
    );
};
export default AcknowledgementQRCode;