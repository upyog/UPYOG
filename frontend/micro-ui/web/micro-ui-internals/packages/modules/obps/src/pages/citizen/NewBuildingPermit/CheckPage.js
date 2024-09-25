import {
  Card, CardHeader, CardSubHeader, CardText,TextInput,CardLabel,CheckBox, LabelFieldPair, UploadFile,
  CitizenInfoLabel, Header, LinkButton, Row, StatusTable, SubmitBar, Table, CardSectionHeader, EditIcon, PDFSvg, Loader,TextArea
} from "@upyog/digit-ui-react-components";
import React,{ useEffect, useMemo, useState }  from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useRouteMatch } from "react-router-dom";
import Timeline from "../../../components/Timeline";
import { convertEpochToDateDMY, stringReplaceAll, getOrderDocuments } from "../../../utils";
import DocumentsPreview from "../../../../../templates/ApplicationDetails/components/DocumentsPreview";
import Architectconcent from "./Architectconcent";

const CheckPage = ({ onSubmit, value }) => {
  const [development, setDevelopment] = useState()
  const [otherCharges, setOtherCharges] = useState()
  const [lessAdjusment, setLessAdjusment] = useState()
  const [labourCess , setLabourCess] =useState()
  const [gaushalaFees , setGaushalaFees] =useState()
  const [malbafees , setMalbafees] =useState()
  const [waterCharges , setWaterCharges] =useState()
  const { t } = useTranslation();
  const history = useHistory();
  const match = useRouteMatch();
  const user = Digit.UserService.getUser();
  
  const state = Digit.ULBService.getStateId();
  
  const tenantId = user?.info?.permanentCity || value?.tenantId ||Digit.ULBService.getCurrentTenantId() ;
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.obps.useMDMS(state, "BPA", ["GaushalaFees","MalbaCharges","LabourCess"]);
  const [otherChargesDisc, setOtherChargesDisc] = useState()
  const [uploadedFile, setUploadedFile] = useState();
  const [uploadedFileLess, setUploadedFileLess] = useState([]);
  const [file, setFile] = useState();
  const [uploadMessage, setUploadMessage] = useState("");
  const [errorFile, setError] = useState(null);
  const [docLessAdjustment, setDocuments] = useState({});
  let acceptFormat = ".pdf"
  let BusinessService;
  if(value.businessService === "BPA_LOW")
  BusinessService="BPA.LOW_RISK_PERMIT_FEE";
  else if(value.businessService === "BPA")
  BusinessService="BPA.NC_APP_FEE";

  


  
  const { data, address, owners, nocDocuments, documents, additionalDetails, subOccupancy,PrevStateDocuments,PrevStateNocDocuments,applicationNo } = value;
  const isEditApplication = window.location.href.includes("editApplication");

  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };

  const Architectvalidations = sessionStorage.getItem("ArchitectConsentdocFilestoreid") ? true : false ;
  const architectmobilenumber = user?.info?.mobileNumber
  const [showTermsPopup, setShowTermsPopup] = useState(false);
  const [showMobileInput, setShowMobileInput] = useState(false);
  const [mobileNumber, setMobileNumber] = useState(architectmobilenumber || '');
  const [showOTPInput, setShowOTPInput] = useState(false);
  const [otp, setOTP] = useState('');
  const [isOTPVerified, setIsOTPVerified] = useState(false);
  const [otpError, setOTPError] = useState("");
  const [otpVerifiedTimestamp, setOTPVerifiedTimestamp] = useState(null);
  

  const handleTermsLinkClick = () => {
    if (isOTPVerified){
      setShowTermsPopup(true);
    }
    else{
      alert("Please verify yourself")
    }
    
  }

  const checkLabels = () => {
    return (
      <div>
            {t("I_AGREE_TO_BELOW_UNDERTAKING")}
            <LinkButton
              label={t("DECLARATION_UNDER_SELF_CERTIFICATION_SCHEME")}
              onClick={handleTermsLinkClick}
            />
      </div>
    );
  };

  


  
  
  

  const handleVerifyClick = () => {
    setShowMobileInput(true);
  };

  const handleMobileNumberChange = (e) => {
    setMobileNumber(e.target.value);
  };

  const handleGetOTPClick = async () => {
    // Call the Digit.UserService.sendOtp API to send the OTP
    try {
      const response = await Digit.UserService.sendOtp({otp:{mobileNumber:mobileNumber, tenantId: user?.info?.tenantId, userType: user?.info?.type, type: "login"}});
      if (response.isSuccessful) {
        setShowOTPInput(true);
      } else {
        // Handle error case if OTP sending fails
        console.error("Error sending OTP Response is false:", response.error);
        alert("Something Went Wrong")
      }
    } catch (error) {
      console.error("Error sending OTP:", error);
      alert("Something went wrong")
    }
  };

 

  const handleOTPChange = (e) => {
    setOTP(e.target.value);
  };

  const requestData = {
    username:mobileNumber,
    password:otp,
    tenantId: user?.info?.tenantId,
    userType: user?.info?.type

  };

  const handleVerifyOTPClick = async () => {
    // Call the API to verify the OTP
    try {
      const response = await Digit.UserService.authenticate(requestData);
      if (response.ResponseInfo.status==="Access Token generated successfully") {
        setIsOTPVerified(true);
        setOTPError(t("VERIFIED"));
        const currentTimestamp = new Date();
        setOTPVerifiedTimestamp(currentTimestamp);
        sessionStorage.setItem('otpVerifiedTimestamp', currentTimestamp.toString().replace(/GMT.*\((.*)\)/, '').trim());
      } else {
        setIsOTPVerified(false);
        setOTPError(t("WRONG OTP"));
      }
    } catch (error) {
      console.error("Error verifying OTP:", error);
      alert("OTP Verification Error ")
      setIsOTPVerified(false);
      setOTPError(t("OTP Verification Error"));
    }
  };

  const isValidMobileNumber = mobileNumber.length === 10 && /^[0-9]+$/.test(mobileNumber);

  useEffect(() => {
    (async () => {
      setError(null);
      if (file&& file?.type) {
        if(!(acceptFormat?.split(",")?.includes(`.${file?.type?.split("/")?.pop()}`)))
        {
          setError(t("PT_UPLOAD_FORMAT_NOT_SUPPORTED"));
        }
        else if (file.size >= 2000000) {
          setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("property-upload", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFileLess([...uploadedFileLess,{fileStoreId:response?.data?.files[0]?.fileStoreId, time:new Date()}]);
            } else {
              setError(t("PT_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
          }
        }
      }
    })();        
  }, [file]);
  useEffect(()=>{
    sessionStorage.setItem("uploadedFileLess",JSON.stringify(uploadedFileLess));
},[uploadedFileLess])

const sitePhotographOne = value?.documents?.documents.find(doc => doc?.documentType==="SITEPHOTOGRAPH.ONE.ONE")

  useEffect(()=>{
    if (value?.additionalDetails?.lessAdjustmentFeeFiles?.length) {
      const fileStoresIds = value?.additionalDetails?.lessAdjustmentFeeFiles.map((document,index) =>(index===value?.additionalDetails?.lessAdjustmentFeeFiles?.length-1 ? value?.additionalDetails?.lessAdjustmentFeeFiles[value?.additionalDetails?.lessAdjustmentFeeFiles?.length-1]?.fileStoreId : null));
      Digit.UploadServices.Filefetch(fileStoresIds, state).then((res) => setDocuments(res?.data));
    }
    if(isEditApplication){
      setDevelopment(value?.additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES);
      sessionStorage.setItem("development",value?.additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES);
      setOtherCharges(value?.additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES);
      sessionStorage.setItem("otherCharges",value?.additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES);
      setLessAdjusment(value?.additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT);
      sessionStorage.setItem("lessAdjusment",value?.additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT);
      setOtherChargesDisc(value?.additionalDetails?.otherFeesDiscription);
      sessionStorage.setItem("otherChargesDisc",value?.additionalDetails?.otherFeesDiscription);
     setUploadedFileLess(value?.additionalDetails?.lessAdjustmentFeeFiles);
    }
let plotArea = parseInt(sessionStorage.getItem("plotArea")) || datafromAPI?.planDetail?.planInformation?.plotArea || value?.additionalDetails?.area;

    //plotArea*10.7639 conversion from sq mtrs to sq ft;
    const LabourCess = Math.round((plotArea * 10.7639) > 909 ? mdmsData?.BPA?.LabourCess[1].rate * (plotArea * 10.7639) : 0)
    const GaushalaFees = Math.round(mdmsData?.BPA?.GaushalaFees[0].rate)
    const Malbafees = Math.round(((plotArea * 10.7639) <= 500 ? mdmsData?.BPA?.MalbaCharges[0].rate : (plotArea * 10.7639) > 500 && (plotArea * 10.7639) <= 1000 ? mdmsData?.BPA?.MalbaCharges?.[1].rate : mdmsData?.BPA?.MalbaCharges[2].rate || 500))
    sessionStorage.setItem("Malbafees", Malbafees)
    sessionStorage.setItem("WaterCharges", Malbafees / 2)
    sessionStorage.setItem("GaushalaFees", GaushalaFees)
    sessionStorage.setItem("LabourCess", LabourCess)
    setGaushalaFees(GaushalaFees)
    setLabourCess(LabourCess)
    setMalbafees(Malbafees)
    setWaterCharges(Malbafees / 2)
  }, [mdmsData, value?.additionalDetails])
    // for application documents
    let improvedDoc = [];
    PrevStateDocuments?.map(preDoc => { improvedDoc.push({...preDoc, module: "OBPS"}) });
    documents?.documents?.map(appDoc => { improvedDoc.push({...appDoc, module: "OBPS"}) });

    //for NOC documents 
    PrevStateNocDocuments?.map(preNocDoc => { improvedDoc.push({...preNocDoc, module: "NOC"}) });
    nocDocuments?.nocDocuments?.map(nocDoc => { improvedDoc.push({...nocDoc, module: "NOC"}) });

    const { data: pdfDetails, isLoading:pdfLoading, error } = Digit.Hooks.useDocumentSearch( improvedDoc, { enabled: improvedDoc?.length > 0 ? true : false});
    
    let applicationDocs = [], nocAppDocs = [];
    if (pdfDetails?.pdfFiles?.length > 0) {  
      pdfDetails?.pdfFiles?.map(pdfAppDoc => {
        if (pdfAppDoc?.module == "OBPS") applicationDocs.push(pdfAppDoc);
        if (pdfAppDoc?.module == "NOC") nocAppDocs.push(pdfAppDoc);
      });
    }

  const { data:datafromAPI, isLoading, refetch } = Digit.Hooks.obps.useScrutinyDetails(tenantId,value?.data?.scrutinyNumber, {
      enabled: value?.data?.scrutinyNumber?true:false,
    })


  let consumerCode=value?.applicationNo;
  const fetchBillParams = { consumerCode };

  function getdate(date) {
    let newdate = Date.parse(date);
    return `${new Date(newdate).getDate().toString() + "/" + (new Date(newdate).getMonth() + 1).toString() + "/" + new Date(newdate).getFullYear().toString()
      }`;
  }



    const {data:paymentDetails} = Digit.Hooks.useFetchBillsForBuissnessService(
      { businessService: BusinessService, ...fetchBillParams, tenantId: tenantId },
      {
        enabled: consumerCode ? true : false,
        retry: false,
      }
    );
    if(!isEditApplication){
      value.additionalDetails.P1charges=paymentDetails?.Bill[0]?.billDetails[0]?.amount;
    }
    const sendbacktocitizenApp = window.location.href.includes("sendbacktocitizen");
    let routeLink = `/digit-ui/citizen/obps/bpa/${additionalDetails?.applicationType.toLowerCase()}/${additionalDetails?.serviceType.toLowerCase()}`;
    if (isEditApplication) routeLink = `/digit-ui/citizen/obps/editApplication/bpa/${value?.tenantId}/${value?.applicationNo}`;
    if( sendbacktocitizenApp ) routeLink = `/digit-ui/citizen/obps/sendbacktocitizen/bpa/${value?.tenantId}/${value?.applicationNo}`;

    const tableHeader = [
      {
          name:"BPA_TABLE_COL_FLOOR",
          id:"Floor",
      },
      {
          name:"BPA_TABLE_COL_LEVEL",
          id:"Level",
      },
      {
          name:"BPA_TABLE_COL_OCCUPANCY",
          id:"Occupancy",
      },
      {
          name:"BPA_TABLE_COL_BUILDUPAREA",
          id:"BuildupArea",
      },
      {
          name:"BPA_TABLE_COL_FLOORAREA",
          id:"FloorArea",
      },
      // {
      //     name:"BPA_TABLE_COL_CARPETAREA",
      //     id:"CarpetArea",
      // }
  ]

  const accessData = (plot) => {
      const name = plot;
      return (originalRow, rowIndex, columns) => { 
        return originalRow[name];
      }
    }


    const tableColumns = useMemo(
      () => {
        
        return tableHeader.map((ob)=> ({
          Header:t(`${ob.name}`),
          accessor: accessData(ob.id),
          id: ob.id,
          //symbol: plot?.symbol,
          //sortType: sortRows,
        }));
  
            
      });


    function getFloorData(block){
      let floors = [];
      block?.building?.floors.map((ob) => {
          floors.push({
              Floor:t(`BPA_FLOOR_NAME_${ob.number}`),
              Level:ob.number,
              Occupancy:t(`${ob.occupancies?.[0]?.type}`),
              BuildupArea:ob.occupancies?.[0]?.builtUpArea,
              FloorArea:ob.occupancies?.[0]?.floorArea || 0,
              CarpetArea:ob.occupancies?.[0]?.CarpetArea || 0,
              key:t(`BPA_FLOOR_NAME_${ob.number}`),
          });
      });
      return floors;
    }

    function routeTo(jumpTo) {
      location.href=jumpTo;
  }

  

  function getBlockSubOccupancy(index){
    let subOccupancyString = "";
    let returnValueArray = [];
    subOccupancy && subOccupancy[`Block_${index+1}`] && subOccupancy[`Block_${index+1}`].map((ob) => {
      // subOccupancyString += `${t(ob.i18nKey)}, `;
      returnValueArray.push(`${t(stringReplaceAll(ob?.i18nKey?.toUpperCase(), "-", "_"))}`);
    })
    return returnValueArray?.length ? returnValueArray.join(', ') : "NA"
    // return subOccupancyString;
  }

  if (pdfLoading || isLoading) {
    return <Loader />
  }

  function onSubmitCheck(){
    if(!development){
      sessionStorage.setItem("development",0)
    }
    if(!waterCharges){
      sessionStorage.setItem("WaterCharges",0)
    }
    if(!lessAdjusment){
      sessionStorage.setItem("lessAdjusment",0)
    }
    if(!otherCharges){
      sessionStorage.setItem("otherCharges",0)
    }
    if(parseInt(lessAdjusment)>(parseInt(development)+parseInt(otherCharges)+parseInt(malbafees)+parseInt(labourCess)+parseInt(waterCharges)+parseInt(gaushalaFees))){
      alert(t("Enterd Less Adjustment amount is invalid"));
    }
    else{
      onSubmit();
    }      
}

  function setOtherChargesVal(value) {
    if(/^[0-9]*$/.test(value)){
      setOtherCharges(value);
      sessionStorage.setItem("otherCharges",value)
    }
    else{
      alert(t("Please enter numbers"))
    }      
}
  function setDevelopmentVal(value) {
    if(/^\d{0,9}$/.test(value)){
      setDevelopment(value);
      sessionStorage.setItem("development",value)
    }
    else{
      alert(t("Please enter only upto 9 numbers"))
    }      
  }
  function setWaterChargesVal(value) {
    if(/^\d{0,9}$/.test(value)){
      setWaterCharges(value);
      sessionStorage.setItem("WaterCharges",value)
    }
    else{
      alert(t("Please enter only upto 9 numbers"))
    }      
  }
  function setLessAdjusmentVal(value) {
  if(/^[0-9]*$/.test(value)){
    if(parseInt(value)>((parseInt(development)?parseInt(development):0)+(parseInt(otherCharges)?parseInt(otherCharges):0)+parseInt(malbafees)+parseInt(labourCess)+parseInt(waterCharges)+parseInt(gaushalaFees))){
      alert(t("Less adjustment fees cannot be grater than Total of other P2 fees"))
    }
    else{
    setLessAdjusment(value);
    sessionStorage.setItem("lessAdjusment",value)
    }      
  }
  else{
   alert(t("Please enter numbers"))
  }      
  }
  function setOtherChargesDis(value) {
    setOtherChargesDisc(value);
    sessionStorage.setItem("otherChargesDisc",value)  ;
}

function selectfile(e) {
    setUploadedFile(e.target.files[0]);
    setFile(e.target.files[0]);
 }

  return (
  <React.Fragment>
  <Timeline currentStep={4} />
  <Header styles={{marginLeft: "10px"}}>{t("BPA_STEPPER_SUMMARY_HEADER")}</Header>
  <Card style={{paddingRight:"16px"}}>
      <StatusTable>
        <Row className="border-none" label={t(`BPA_APPLICATION_NUMBER_LABEL`)} text={applicationNo?applicationNo:""} />
      </StatusTable>
  </Card>
  <Card style={{paddingRight:"16px"}}>
  <CardHeader>{t(`BPA_BASIC_DETAILS_TITLE`)}</CardHeader>
      <StatusTable>
        <Row className="border-none" label={t(`BPA_BASIC_DETAILS_APP_DATE_LABEL`)} text={convertEpochToDateDMY(Number(data?.applicationDate))} />
        <Row className="border-none" label={t(`BPA_BASIC_DETAILS_APPLICATION_TYPE_LABEL`)} text={t(`WF_BPA_${data?.applicationType}`)}/>
        <Row className="border-none" label={t(`BPA_BASIC_DETAILS_SERVICE_TYPE_LABEL`)} text={t(data?.serviceType)} />
        <Row className="border-none" label={t(`BPA_BASIC_DETAILS_OCCUPANCY_LABEL`)} text={data?.occupancyType}/>
        <Row className="border-none" label={t(`BPA_BASIC_DETAILS_RISK_TYPE_LABEL`)} text={t(`WF_BPA_${data?.riskType}`)} />
        {/* <Row className="border-none" label={t(`BPA_BASIC_DETAILS_APPLICATION_NAME_LABEL`)} text={data?.applicantName} /> */}
      </StatusTable>
  </Card>
  <Card style={{paddingRight:"16px"}}>
  <StatusTable>
  <CardHeader>{t("BPA_PLOT_DETAILS_TITLE")}</CardHeader>
  <LinkButton
        label={<EditIcon style={{ marginTop: "-10px", float: "right", position: "relative", bottom: "32px" }} />}
        style={{ width: "100px", display:"inline" }}
        onClick={() => routeTo(`${routeLink}/plot-details`)}
      />
        <Row className="border-none" textStyle={{paddingLeft:"12px"}} label={t(`BPA_BOUNDARY_PLOT_AREA_LABEL`)} text={datafromAPI?.planDetail?.planInformation?.plotArea ? `${datafromAPI?.planDetail?.planInformation?.plotArea} ${t(`BPA_SQ_MTRS_LABEL`)}` : t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_PLOT_NUMBER_LABEL`)} text={datafromAPI?.planDetail?.planInformation?.plotNo || t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_KHATHA_NUMBER_LABEL`)} text={datafromAPI?.planDetail?.planInformation?.khataNo || t("CS_NA")}/>
        <Row className="border-none" label={t(`BPA_BOUNDARY_LAND_REG_DETAIL_LABEL`)} text={data?.registrationDetails || t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_BOUNDARY_WALL_LENGTH_LABEL`)} text={data?.boundaryWallLength|| t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_KHASRA_NUMBER_LABEL`)} text={data?.khasraNumber|| value?.additionalDetails?.khasraNumber|| t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_WARD_NUMBER_LABEL`)} text={data?.wardnumber|| value?.additionalDetails?.wardnumber || t("CS_NA")} />


  </StatusTable>
  </Card>

  <Card style={{paddingRight:"16px"}}>
  <CardHeader>{t("BPA_STEPPER_SCRUTINY_DETAILS_HEADER")}</CardHeader>
  <CardSubHeader style={{fontSize: "20px"}}>{t("BPA_EDCR_DETAILS")}</CardSubHeader>
  <StatusTable  style={{border:"none"}}>
    <Row className="border-none" label={t("BPA_EDCR_NO_LABEL")} text={data?.scrutinyNumber?.edcrNumber}></Row>
    <CardSubHeader>{t("BPA_UPLOADED_PLAN_DIAGRAM")}</CardSubHeader>
    <LinkButton
      label={ <PDFSvg /> }
        onClick={() => routeTo(datafromAPI?.updatedDxfFile)}
     />
     <p style={{ marginTop: "8px", marginBottom: "20px", textAlign:"Left", fontSize: "16px", lineHeight: "19px", color: "#505A5F", fontWeight: "400" }}>{t(`Uploaded Plan.pdf`)}</p>
    <CardSubHeader>{t("BPA_SCRUNTINY_REPORT_OUTPUT")}</CardSubHeader>
    <LinkButton
      label={ <PDFSvg /> }
        onClick={() => routeTo(datafromAPI?.planReport)}
     />
     <p style={{ marginTop: "8px", marginBottom: "20px", textAlign:"Left", fontSize: "16px", lineHeight: "19px", color: "#505A5F", fontWeight: "400" }}>{t(`BPA_SCRUTINY_REPORT_PDF`)}</p>
    </StatusTable>
    <hr style={{color:"#cccccc",backgroundColor:"#cccccc",height:"2px",marginTop:"20px",marginBottom:"20px"}}/>
    <CardSubHeader style={{fontSize: "20px"}}>{t("BPA_BUILDING_EXTRACT_HEADER")}</CardSubHeader>
    <StatusTable>
    <Row className="border-none" label={t("BPA_TOTAL_BUILT_UP_AREA_HEADER")} text={`${datafromAPI?.planDetail?.blocks?.[0]?.building?.totalBuitUpArea} ${t("BPA_SQ_MTRS_LABEL")}`}></Row>
    <Row className="border-none" label={t("BPA_SCRUTINY_DETAILS_NUMBER_OF_FLOORS_LABEL")} text={datafromAPI?.planDetail?.blocks?.[0]?.building?.totalFloors}></Row>
    <Row className="border-none" label={t("BPA_HEIGHT_FROM_GROUND_LEVEL_FROM_MUMTY")} text={`${datafromAPI?.planDetail?.blocks?.[0]?.building?.declaredBuildingHeight} ${t("BPA_MTRS_LABEL")}`}></Row>
    <Row className="border-none" label={t("BPA_SECTION_HEIGHT_EXCLUDING_MUMTY_PARAPET")} text={`${datafromAPI?.planDetail?.blocks?.[0]?.building?.buildingHeightExcludingMP} ${t("BPA_MTRS_LABEL")}`}></Row>
    </StatusTable>
    <hr style={{color:"#cccccc",backgroundColor:"#cccccc",height:"2px",marginTop:"20px",marginBottom:"20px"}}/>
    <CardSubHeader style={{fontSize: "20px"}}>{t("BPA_OCC_SUBOCC_HEADER")}</CardSubHeader>
    {datafromAPI?.planDetail?.blocks.map((block,index)=>(
    <div key={index} style={datafromAPI?.planDetail?.blocks?.length > 1 ?{ marginTop: "19px", background: "#FAFAFA", border: "1px solid #D6D5D4", borderRadius: "4px", padding: "8px", lineHeight: "19px", maxWidth: "960px", minWidth: "280px" } : {}}>
    <CardSubHeader style={{marginTop:"15px", fontSize: "18px"}}>{t("BPA_BLOCK_SUBHEADER")} {index+1}</CardSubHeader>
    <StatusTable >
    <Row className="border-none" textStyle={{wordBreak:"break-word"}} label={t("BPA_SUB_OCCUPANCY_LABEL")} text={getBlockSubOccupancy(index) === ""?t("CS_NA"):getBlockSubOccupancy(index)}></Row>
    </StatusTable>
    <div style={{overflow:"scroll"}}>
    <Table
      className="customTable table-fixed-first-column table-border-style"
      t={t}
      disableSort={false}
      autoSort={true}
      manualPagination={false}
      isPaginationRequired={false}
      initSortId="S N "
      data={getFloorData(block)}
      columns={tableColumns}
      getCellProps={(cellInfo) => {
        return {
          style: {},
        };
      }}
    />
    </div>
    </div>))}
    <hr style={{color:"#cccccc",backgroundColor:"#cccccc",height:"2px",marginTop:"20px",marginBottom:"20px"}}/>
    <CardSubHeader style={{fontSize: "20px"}}>{t("BPA_APP_DETAILS_DEMOLITION_DETAILS_LABEL")}</CardSubHeader>
    <StatusTable  style={{border:"none"}}>
    <Row className="border-none" label={t("BPA_APPLICATION_DEMOLITION_AREA_LABEL")} text={datafromAPI?.planDetail?.planInformation?.demolitionArea ? `${datafromAPI?.planDetail?.planInformation?.demolitionArea} ${t("BPA_SQ_MTRS_LABEL")}` : t("CS_NA")}></Row>
    </StatusTable>
    </Card>
    <Card style={{paddingRight:"16px"}}>
    <StatusTable>
    <CardHeader>{t("BPA_NEW_TRADE_DETAILS_HEADER_DETAILS")}</CardHeader>
        <LinkButton
          label={<EditIcon style={{ marginTop: "-10px", float: "right", position: "relative", bottom: "32px" }} />}
          style={{ width: "100px", display: "inline" }}
          onClick={() => routeTo(`${routeLink}/location`)}
        />
        <Row className="border-none" textStyle={{paddingLeft:"12px"}} label={t(`BPA_DETAILS_PIN_LABEL`)} text={address?.pincode || t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_CITY_LABEL`)} text={address?.city?.name || t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_LOC_MOHALLA_LABEL`)} text={address?.locality?.name || t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_DETAILS_SRT_NAME_LABEL`)} text={address?.street || t("CS_NA")} />
        <Row className="border-none" label={t(`ES_NEW_APPLICATION_LOCATION_LANDMARK`)} text={address?.landmark || t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_SITEPHOTOGRAPH_LATITUDE`)} text={sitePhotographOne?.additionalDetails?.latitude || t("CS_NA")} />
        <Row className="border-none" label={t(`BPA_SITEPHOTOGRAPH_LONGITUDE`)} text={sitePhotographOne?.additionalDetails?.longitude || t("CS_NA")} />

    </StatusTable>
    </Card>
    <Card style={{paddingRight:"16px"}}>
    <StatusTable>
      <CardHeader>{t("BPA_APPLICANT_DETAILS_HEADER")}</CardHeader>
        <LinkButton
          label={<EditIcon style={{ marginTop: "-10px", float: "right", position: "relative", bottom: "32px" }} />}
          style={{ width: "100px", display: "inline" }}
          onClick={() => routeTo(`${routeLink}/owner-details`)}
        />
      {owners?.owners && owners?.owners.map((ob,index) =>(
      <div key={index} style={owners?.owners?.length > 1 ?{ marginTop: "19px", background: "#FAFAFA", border: "1px solid #D6D5D4", borderRadius: "4px", padding: "8px", lineHeight: "19px", maxWidth: "960px", minWidth: "280px" } : {}}>
      {owners.owners.length > 1 && <CardSubHeader>{t("COMMON_OWNER")} {index+1}</CardSubHeader>}
      <StatusTable>
      <Row className="border-none" textStyle={index==0 && owners.owners.length == 1 ?{paddingLeft:"12px"}:{}} label={t(`CORE_COMMON_NAME`)} text={ob?.name} />
      <Row className="border-none" label={t(`BPA_APPLICANT_GENDER_LABEL`)} text={t(ob?.gender?.i18nKey)} />
      <Row className="border-none" label={t(`CORE_COMMON_MOBILE_NUMBER`)} text={ob?.mobileNumber} />
      <Row className="border-none" label={t(`CORE_COMMON_EMAIL_ID`)} text={ob?.emailId || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_IS_PRIMARY_OWNER_LABEL`)} text={`${ob?.isPrimaryOwner}`} /> 
      </StatusTable>
      </div>))}
      </StatusTable>
    </Card>

    <Card style={{paddingRight:"16px"}}>
      <StatusTable>
      <CardHeader>{t("BPA_ADDITIONAL_BUILDING_DETAILS")}</CardHeader>
      <Row className="border-none" label={t(`BPA_APPROVED_COLONY_LABEL`)} text={owners?.approvedColony?.i18nKey || value?.additionalDetails?.approvedColony || t("CS_NA")} />
      {owners?.approvedColony?.i18nKey==="YES" && (
        <React.Fragment>
          <Row className="border-none" label={t(`BPA_APPROVED_COLONY_NAME`)} text={owners?.nameofApprovedcolony || value?.additionalDetails?.nameofApprovedcolony || t("CS_NA")} />
        </React.Fragment>
      )}
      {owners?.approvedColony?.i18nKey==="NO" && (
        <React.Fragment>
          <Row className="border-none" label={t(`BPA_NOC_NUMBER`)} text={owners?.NocNumber || value?.additionalDetails?.NocNumber || t("CS_NA")} />
        </React.Fragment>
      )}
      <Row className="border-none" label={t(`BPA_ULB_TYPE_LABEL`)} text={owners?.Ulblisttype?.value || value?.additionalDetails?.Ulblisttype || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_ULB_NAME_LABEL`)} text={owners?.UlbName?.code || value?.additionalDetails?.UlbName || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_DISTRICT_LABEL`)} text={owners?.District?.code || value?.additionalDetails?.District || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_BUILDING_STATUS_LABEL`)} text={owners?.buildingStatus?.code || value?.additionalDetails?.buildingStatus || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_CORE_AREA_LABEL`)} text={datafromAPI?.planDetail?.coreArea || value?.additionalDetails?.coreArea || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_PROPOSED_SITE_LABEL`)} text={owners?.proposedSite?.code || value?.additionalDetails?.proposedSite || t("CS_NA")} />
      {owners?.schemes?.code === "SCHEME" && (
    <React.Fragment>
      <Row className="border-none" label={t(`BPA_SCHEME_TYPE_LABEL`)} text={owners?.schemesselection?.value || value?.additionalDetails?.schemesselection || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_SCHEME_NAME_LABEL`)} text={owners?.schemeName || value?.additionalDetails?.schemeName || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_TRANFERRED_SCHEME_LABEL`)} text={owners?.transferredscheme || value?.additionalDetails?.transferredscheme || t("CS_NA")} />
    </React.Fragment>
  )}
      <Row className="border-none" label={t(`BPA_PURCHASED_FAR_LABEL`)} text={owners?.purchasedFAR?.code || value?.additionalDetails?.purchasedFAR || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_MASTER_PLAN_LABEL`)} text={owners?.masterPlan?.code || value?.additionalDetails?.masterPlan || t("CS_NA")} />
      <Row className="border-none" label={t(`BPA_GREEN_BUILDING_LABEL`)} text={owners?.greenbuilding?.code || value?.additionalDetails?.greenbuilding || t("CS_NA")} />
      </StatusTable>

    </Card>
    



    <Card style={{paddingRight:"16px"}}>
    <StatusTable>
      <CardHeader>{t("BPA_DOCUMENT_DETAILS_LABEL")}</CardHeader>
        <LinkButton
          label={<EditIcon style={{ marginTop: "-10px", float: "right", position: "relative", bottom: "32px" }} />}
          style={{ width: "100px", display: "inline" }}
          onClick={() => routeTo(`${routeLink}/document-details`)}
        />
      {<DocumentsPreview documents={getOrderDocuments(applicationDocs)} svgStyles = {{}} isSendBackFlow = {false} isHrLine = {true} titleStyles ={{fontSize: "18px", lineHeight: "24px", "fontWeight": 700, marginBottom: "10px"}}/>}
      </StatusTable>
    </Card>
    {/*<Card style={{paddingRight:"16px"}}>
     <StatusTable>
    <CardHeader>{t("BPA_NOC_DETAILS_SUMMARY")}</CardHeader>
        <LinkButton
          label={<EditIcon style={{ marginTop: "-10px", float: "right", position: "relative", bottom: "32px" }} />}
          style={{ width: "100px", display: "inline" }}
          onClick={() => routeTo(`${routeLink}/noc-number`)}
        />
     <Row className="border-none" label={t(`BPA_NOC_NUMBER`)} text={value?.additionalDetails?.nocNumber} />    */}
    {/* {nocDocuments && nocDocuments?.NocDetails.map((noc, index) => (
      <div key={`noc-${index}`} style={nocDocuments?.NocDetails?.length > 1 ?{ marginTop: "19px", background: "#FAFAFA", border: "1px solid #D6D5D4", borderRadius: "4px", padding: "8px", lineHeight: "19px", maxWidth: "960px", minWidth: "280px" } : {}}>
      <CardSectionHeader style={{marginBottom: "24px"}}>{`${t(`BPA_${noc?.nocType}_HEADER`)}`}</CardSectionHeader>
      <StatusTable>
        <Row className="border-none" label={t(`BPA_${noc?.nocType}_LABEL`)} text={noc?.applicationNo} />
        <Row className="border-none" label={t(`BPA_NOC_STATUS`)} text={t(`${noc?.applicationStatus}`)} textStyle={noc?.applicationStatus == "APPROVED" || noc?.applicationStatus == "AUTO_APPROVED" ? {color : "#00703C"} : {color: "#D4351C"}} />
        {noc?.additionalDetails?.SubmittedOn ? <Row className="border-none" label={`${t("BPA_NOC_SUBMISSION_DATE")}`} text={noc?.additionalDetails?.SubmittedOn ? convertEpochToDateDMY(Number(noc?.additionalDetails?.SubmittedOn)) : "NA"} /> : null }
        {noc?.nocNo ? <Row className="border-none" label={`${t("BPA_APPROVAL_NUMBER_LABEL")}`} text={noc?.nocNo || "NA"} /> : null }
        {(noc?.applicationStatus === "APPROVED" || noc?.applicationStatus === "REJECTED" || noc?.applicationStatus === "AUTO_APPROVED" || noc?.applicationStatus === "AUTO_REJECTED") ? <Row className="border-none" label={`${t("BPA_APPROVED_REJECTED_ON_LABEL")}`} text= {convertEpochToDateDMY(Number(noc?.auditDetails?.lastModifiedTime))} /> : null }
        <Row className="border-none" label={t(`BPA_DOCUMENT_DETAILS_LABEL`)} text={""} />
        {<DocumentsPreview documents={getOrderDocuments(nocAppDocs?.filter(data => data?.documentType?.includes(noc?.nocType?.split("_")?.[0])), true)} svgStyles = {{}} isSendBackFlow = {false} isHrLine = {true} titleStyles ={{fontSize: "18px", lineHeight: "24px", "fontWeight": 700, marginBottom: "10px"}}/>}
      </StatusTable>
    </div>
    ))} */}
    {/* </StatusTable>
    </Card> */}
    <Card style={{paddingRight:"16px"}}>
    <CardSubHeader>{t("BPA_SUMMARY_FEE_EST")}</CardSubHeader> 
    <StatusTable>
    {/* {paymentDetails?.Bill[0]?.billDetails[0]?.billAccountDetails.map((bill,index)=>(
      <div key={index}>
        <Row className="border-none" label={t(`${bill.taxHeadCode}`)} text={`₹ ${bill?.amount}`} />
      </div>
    ))} */}
     {/* <Row className="border-none" label={t(`BPA_COMMON_TOTAL_AMT`)} text={`₹ ${paymentDetails?.Bill?.[0]?.billDetails[0]?.amount || "0"}`} /> */}
     <CardSubHeader>{t("BPA_P1_SUMMARY_FEE_EST")}</CardSubHeader> 
     
     <Row className="border-none" label={t(`BUILDING_APPLICATION_FEES`)} text={`₹ ${Math.round((datafromAPI?.planDetail?.blocks?.[0]?.building?.totalBuitUpArea)*10.7639*2.5)}`}></Row>
     <Row className="border-none" label={t(`BOUNDARY_WALL_FEES`)} text={`₹ ${Math.round(data?.boundaryWallLength*2.5)}`}></Row>
     <Row className="border-none" label={t(`BPA_APPL_FEES`)} text={`₹ ${value?.additionalDetails?.P1charges || paymentDetails?.Bill[0]?.billDetails[0]?.amount}`} />
     <CardSubHeader>{t("BPA_P2_SUMMARY_FEE_EST")}</CardSubHeader>        
     <Row className="border-none" label={t(`BPA_COMMON_MALBA_AMT`)} text={`₹ ${malbafees}`} />
     <Row className="border-none" label={t(`BPA_COMMON_LABOUR_AMT`)} text={`₹ ${labourCess}`} />
     {/* <Row className="border-none" label={t(`BPA_COMMON_WATER_AMT`)} text={`₹ ${waterCharges}`} /> */}
     <Row className="border-none" label={t(`BPA_COMMON_GAUSHALA_AMT`)} text={`₹ ${gaushalaFees}`} />
     <CardSubHeader>{t("BPA_P2_SUMMARY_FEE_EST_MANUAL")}</CardSubHeader>
     <CardLabel>{t("BPA_COMMON_WATER_AMT")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="waterCharges"
            defaultValue={value?.additionalDetails?.selfCertificationCharges?.BPA_WATER_CHARGES }
            value={waterCharges}
            onChange={(e) => {setWaterChargesVal(e.target.value)}}
            //disable={userInfo?.info?.emailId && !isOpenLinkFlow ? true : false}
            //disable={editScreen}
            {...{ required: true, pattern: "^[0-9]*$" }}
          />
             <CardLabel>{t("BPA_COMMON_DEVELOPMENT_AMT")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="development"
            defaultValue={value?.additionalDetails?.selfCertificationCharges?.BPA_DEVELOPMENT_CHARGES }
            value={development}
            onChange={(e) => {setDevelopmentVal(e.target.value)}}
            //disable={userInfo?.info?.emailId && !isOpenLinkFlow ? true : false}
            //disable={editScreen}
            {...{ required: true, pattern: "^[0-9]*$" }}
          />
          <CardLabel>{t("BPA_COMMON_OTHER_AMT")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="otherCharges"
            defaultValue={value?.additionalDetails?.selfCertificationCharges?.BPA_OTHER_CHARGES }
            value={otherCharges}
            onChange={(e) => {setOtherChargesVal(e.target.value)}}
            //disable={userInfo?.info?.emailId && !isOpenLinkFlow ? true : false}
            //disable={editScreen}
            {...{ required: true, pattern: /^[0-9]*$/ }}
          />
          {parseInt(otherCharges)>0?
          (
          <div>
          <CardLabel>{t("BPA_COMMON_OTHER_AMT_DISCRIPTION")}</CardLabel>
          <TextArea
            t={t}
            type={"text"}
            name="otherChargesDiscription"
            defaultValue={value?.additionalDetails?.otherFeesDiscription }
            value={otherChargesDisc}
            onChange={(e) => {setOtherChargesDis(e.target.value)}}
            {...{ required: true }}
          /></div>):null
          } 
          <CardLabel>{t("BPA_COMMON_LESS_AMT")}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="lessAdjusment"
            defaultValue={value?.additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT }
            value={lessAdjusment}
            onChange={(e) => {setLessAdjusmentVal(e.target.value)}}
            //disable={userInfo?.info?.emailId && !isOpenLinkFlow ? true : false}
            //disable={editScreen}
            {...{ required: true, pattern: "^[0-9]*$" }}
          />
          {parseInt(lessAdjusment)>0 ?(
            <div>
              <CardLabel>{t("BPA_COMMON_LESS_AMT_FILE")}</CardLabel>
              <UploadFile
              id={"noc-doc"}
              style={{marginBottom:"200px"}}
              onUpload={selectfile}
              onDelete={() => {
                  setUploadedFile(null);
                  setFile("");
              }}
              message={uploadedFile ? `1 ${t(`FILEUPLOADED`)}` : t(`ES_NO_FILE_SELECTED_LABEL`)}
              error={errorFile}
              uploadMessage={uploadMessage}
          />
            </div>
          ):null
          }
          {(docLessAdjustment?.fileStoreIds?.length && parseInt(value?.additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT)>0) &&  
          <CardLabel style={{marginTop:"15px"}}>{t("BPA_COMMON_LESS_AMT_PREVIOUS_FILE")}</CardLabel>            
          }
          {(docLessAdjustment?.fileStoreIds?.length && parseInt(value?.additionalDetails?.selfCertificationCharges?.BPA_LESS_ADJUSMENT_PLOT)>0) &&             
            <a   target="_blank" href={docLessAdjustment?.fileStoreIds[0]?.url}>
            <PDFSvg />
          </a>
          }            
          <Row className="border-none"></Row>
     <Row  className="border-none" label={t(`BPA_P2_TOTAL_FEE`)} text={`₹ ${((parseInt(development)?parseInt(development):0)+(parseInt(otherCharges)?parseInt(otherCharges):0)+parseInt(malbafees)+parseInt(labourCess)+(parseInt(waterCharges)?parseInt(waterCharges):0)+parseInt(gaushalaFees))-(parseInt(lessAdjusment)?parseInt(lessAdjusment):0)}`} />
     
     </StatusTable>
     <br></br>

     {value?.status==="INITIATED" && (
     <div>
      <CardLabel>{t("ARCHITECT_SHOULD_VERIFY_HIMSELF_BY_CLICKING_BELOW_BUTTON")}</CardLabel>
      <LinkButton label={t("BPA_VERIFY")} onClick={handleVerifyClick} />
      <br></br>
      {showMobileInput && (
        <React.Fragment>
          <br></br>
        <CardLabel>{t("BPA_MOBILE_NUMBER")}</CardLabel>
        <TextInput
          t={t}
          type="tel"
          isMandatory={true}
          optionKey="i18nKey"
          name="mobileNumber"
          value={mobileNumber}
          onChange={handleMobileNumberChange}
          {...{ required: true, pattern: "[0-9]{10}", type: "tel", title: t('CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID') }}
        />
  
        <LinkButton label={t("BPA_GET_OTP")} onClick={handleGetOTPClick} disabled={!isValidMobileNumber} />
        </React.Fragment>
        )}
        <br></br>
        {showOTPInput && (
          <React.Fragment>
            <br></br>
            <CardLabel>{t('BPA_OTP')}</CardLabel>
            <TextInput
              t={t}
              type="text"
              isMandatory={true}
              optionKey="i18nKey"
              name="otp"
              value={otp}
              onChange={handleOTPChange}
              {...{ required: true, pattern: "[0-9]{6}", type: "tel", title: t('BPA_INVALID_OTP') }}
            />

            <SubmitBar label={t("VERIFY_OTP")} onSubmit={handleVerifyOTPClick} />
            {otpError && <CardLabel style={{ color: 'red' }}>{otpError}</CardLabel>}
          </React.Fragment>
        )}
     </div>)}
     <br></br>
     <div>
     <CheckBox
        label={checkLabels()}
        onChange={setdeclarationhandler}
        styles={{ height: "auto" }}
        //disabled={!agree}
      />
      
      {showTermsPopup && (
      <Architectconcent
        showTermsPopup={showTermsPopup}
        setShowTermsPopup={setShowTermsPopup}
        otpVerifiedTimestamp={otpVerifiedTimestamp} // Pass timestamp as a prop
      />
    )}
    </div>
    <hr style={{color:"#cccccc",backgroundColor:"#cccccc",height:"2px",marginTop:"20px",marginBottom:"20px"}}/>
    {/* <CardHeader>{t("BPA_COMMON_TOTAL_AMT")}</CardHeader> 
    <CardHeader>₹ {paymentDetails?.Bill?.[0]?.billDetails[0]?.amount || "0"}</CardHeader>  */}
    <SubmitBar label={t("BPA_SEND_TO_CITIZEN_LABEL")} onSubmit={onSubmitCheck} disabled={ ( !agree || !isOTPVerified || !Architectvalidations)} id/>
    </Card>
  </React.Fragment>
  );
};

export default CheckPage;
