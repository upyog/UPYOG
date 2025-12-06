import { CardLabel, FormStep, Dropdown, TextInput, Toast, SearchIcon, Row, ImageViewer, StatusTable, LinkButton, Header, SubmitBar, CardHeader } from "@upyog/digit-ui-react-components";
import DisplayPhotosnew from "../../../../react-components/src/atoms/DisplayPhotosnew";
import React, { useEffect, useState } from "react";
import { PreApprovedPlanService } from "../../../../libraries/src/services/elements/PREAPPROVEDPLAN";
import  usePreApprovedSearch  from "../../../../libraries/src/hooks/obps/usePreApprovedSearch";
import { useTranslation } from "react-i18next";
import { Link, useHistory, useParams } from "react-router-dom";
import useEstimateDetails from "../../../../libraries/src/hooks/obps/useEstimateDetails";
import Timeline from "../components/Timeline";
const BuildingPlanScrutiny = ({ t, config, onSelect, formData, isShowToast, isSubmitBtnDisable, setIsShowToast }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [isPlanApproved, setIsPlanApproved] = useState(formData?.isPlanApproved || "");
  const [landStatus, setLandStatus] = useState(formData?.landStatus||"");
  const [projectComponent, setProjectComponent] = useState(formData?.projectComponent);
  const [lengthInFeet, setLengthInFeet] = useState(formData?.lengthInFeet||"");
  const [lengthInInches, setLengthInInches] = useState(formData?.lengthInInches||"");
  const [widthInFeet, setWidthInFeet] = useState(formData?.widthInFeet||"");
  const [widthInInches, setWidthInInches] = useState(formData?.widthInInches||"");
  const [abuttingRoadWidth, setAbuttingRoadWidth] = useState(formData?.abuttingRoadWidth);
  const [error, setError] = useState();
  const [imageZoom, setImageZoom] = useState(null);
  const [mandatoryFieldsError, setMandatoryFiledsError] = useState();
  const [imagesToShowBelowComplaintDetails, setImagesToShowBelowComplaintDetails] = useState();

  const [selectedPlot, setSelectedPlot] = useState();
  const [inputError, setInputError] = useState()
  
  let plotImage = "https://in-egov-assets.s3.ap-south-1.amazonaws.com/images/plotImage.png"
  
  const planArrpovedOptione = [
    { code: "YES", key: "Yes" },
    { code: "NO", key: "No" },
  ];
  
  const landStatusOptions = [
    { code: "BUILDING_CONSTRUCTED", key: "Building Constructed" },
    { code: "UNDER_CONSTRUCTION", key: "Under Construction" },
    { code: "VACANT", key: "Vacant" },
  ];

  const projectComponentOptions = [
    { code: "PRE_APPROVED_RESIDENTIAL", key: "Residential" },
    { code: "OTHERS", key: "Others" },
  ];
  const [searchData, setSearchData] = useState(null); 
  const [estimatePayload, setEstimatePayload] = useState(null); 
  const preApprovedResponse = usePreApprovedSearch(searchData, {enabled:searchData!==null});
  const estimateResponse = useEstimateDetails({CalulationCriteria:estimatePayload},estimatePayload!==null?true:false, null)
 
  const getPreApprovedPlanDetails = async() => {
    const data = { 
      plotLengthInFeet: lengthInInches ? parseInt(lengthInFeet) + parseInt((lengthInInches * (1 / 12))) : parseInt(lengthInFeet), 
      plotWidthInFeet: widthInInches ? parseInt(widthInFeet) + parseInt((widthInInches * (1 / 12))) : parseInt(widthInFeet), 
      roadWidth: abuttingRoadWidth 
    };

    if (!lengthInFeet || !widthInFeet || !abuttingRoadWidth) {
      setMandatoryFiledsError(t("PLEASE_FILL_MANDATORY_DETAILS"));
    } else {
        setSearchData(data)
        setMandatoryFiledsError("");
    }
  };
  useEffect(async () => {
    if (preApprovedResponse?.data!==undefined) {
      const fileStoreIds = preApprovedResponse?.data.flatMap(item =>
        item.documents
          .filter(doc => doc?.additionalDetails?.fileName?.includes(".jpg"))
          .map(doc => doc?.fileStoreId)
      );
       const thumbnails = fileStoreIds ? await getThumbnails(fileStoreIds, tenantId) : null;
       
      setImagesToShowBelowComplaintDetails(thumbnails);
    }
  }, [preApprovedResponse]);

  const clearForm = () => {
    setIsPlanApproved(null);
    setLandStatus(null);
    setProjectComponent(null);
    setLengthInFeet("");
    setLengthInInches("");
    setWidthInFeet("");
    setWidthInInches("");
    setAbuttingRoadWidth("");
    setError("");
  };
  useEffect(() => {
    if (isPlanApproved?.code === "NO") {
      setError(t("PRE_APPROVED_PLAN_CANNOT_CREATED_WITH_SELECTED_CRITERIA"));
    } else if (landStatus?.code === "BUILDING_CONSTRUCTED" || landStatus?.code === "UNDER_CONSTRUCTION") {
      setError(t("CANNOT_APPLY_FOR_PLOT_UNDER_CONSTRUCTION_ALREADY_CONSTRUCTED"));
    } else if  (projectComponent?.code === "OTHERS"){
      setError(t("PRE_APPROVED_PLAN_CAN_BE_CREATED_FOR_RESIDENTIAL"));
    } else {
      setError("");
    }
  }, [isPlanApproved, landStatus, projectComponent]);

  const handleInputChange = (setter, fieldName) => (e) => {
    const regex = /^[0-9]*$/;
  if (regex.test(e.target.value)) {
    setter(e.target.value);
    setInputError("");
  }
  else{
    setInputError(`${fieldName} should be in number`)
  }
  }
  const handleInchesInput=(setter) => (e)=>{
    const value = e.target.value;
  const regex = /^[0-9]*$/;
  if (regex.test(value)) {
    if (value <= 12) {
      setter(value);
      setInputError("");
    } else {
      setInputError("Inches should not be more than 12");
    }
  } else {
    setInputError("Only numbers are allowed");
  }
  }

  const handleNext = () => {
    formData.selectedPlot=selectedPlot;
    formData.estimate=estimateResponse?.data;
    formData.lengthInFeet=lengthInFeet;
    formData.widthInFeet=widthInFeet;
    formData.isPlanApproved=isPlanApproved;
    formData.landStatus=landStatus;
    formData.lengthInInches=lengthInInches;
    formData.widthInInches=widthInInches;
    formData.projectComponent=projectComponent;
    formData.abuttingRoadWidth=abuttingRoadWidth;
    onSelect("", formData);
  }
  const onSkip = () => {
    clearForm();
};
  const getThumbnails =  async(ids, tenantId) => {
    const res =  await Digit.UploadServices.Filefetch(ids, tenantId);
    if (res.data.fileStoreIds && res.data.fileStoreIds.length !== 0) {
      const fetchDrawingNo = preApprovedResponse.data.reduce((acc, plan) => {
        plan.documents.forEach(doc => {
          acc[doc.fileStoreId] = plan.preApprovedCode;
        });
        return acc;
      }, {});
      const drawingNos = res.data.fileStoreIds.map(file => {
        return fetchDrawingNo[file.id] || null; 
      });
        return {
            fileStore:res.data.fileStoreIds.map(o=>o.id),
            thumbs: res.data.fileStoreIds.map(o => o.url), 
            fullImage: res.data.fileStoreIds.map(o => Digit.Utils.getFileUrl(o.url)),
            drawingNo: drawingNos,  
        };
      } else {
        return null;
      }
  };
const getDetailsRow = (estimateDetails) => {
    return (
      <div>
      <StatusTable>
      <Header style={{marginTop:"18px", marginBottom:"10px"}}>{t("BPA_FEE_DETAILS")}</Header>
      <Row label={t("BPA_DRAWING_NUMBER")} text={selectedPlot?.preApprovedCode || "NA"} />
      {estimateDetails?.taxHeadEstimates.map((item, index) => (
        <Row key={index} label={t(`${item.taxHeadCode}`)} text={item.estimateAmount} />
      ))}  
      </StatusTable>
      </div>
    );
  };
  function onCloseImageZoom() {
    setImageZoom(null);
  }
  function zoomImage(imageSource, index) {
    setImageZoom(imageSource);
  }
  const ActionButton = ({ label, jumpTo }) => {
    const { t } = useTranslation();
    const history = useHistory();
    function routeTo() {
      location.href = jumpTo;
    }
    return <LinkButton style={{marginTop:"-0.5px" }} label={t(label)} onClick={routeTo} />;
  };
  function zoomImageWrapper(imageSource, index){
      zoomImage(imagesToShowBelowComplaintDetails?.fullImage[index]);
      const selectedObject = preApprovedResponse?.data.find(obj => {
        return obj.documents.some(doc => doc?.fileStoreId === imagesToShowBelowComplaintDetails?.fileStore[index]);
    });
    setSelectedPlot(selectedObject)
    estimateData(selectedObject); 
  }
  const estimateData=(selectedObject)=>{
    const CalulationCriteria = [
      {
          "BPA": {
              "edcrNumber": selectedObject?.drawingNo,
              "riskType": "LOW",
              "businessService": "BPA-PAP",
              "applicationType": "BUILDING_PLAN_SCRUTINY",
              "serviceType": "NEW_CONSTRUCTION",
              "tenantId": "pg.citya"
          },
          "applicationNo": "",
          "feeType": "ApplicationFee",
          "tenantId": "pg.citya",
          "applicationType": "BUILDING_PLAN_SCRUTINY",
          "serviceType": "NEW_CONSTRUCTION"
      }
  ]
  sessionStorage.setItem("CalculationCriteria", JSON.stringify(CalulationCriteria))
  setEstimatePayload(CalulationCriteria)

  }
  
  function onCloseImageZoom() {
    setImageZoom(null);
  }

  return (
    <div>
    <Timeline currentStep={1} flow="PRE_APPROVE" /> 
    <FormStep
      t={t}
      config={config}
      onSelect={handleNext}
      
      isDisabled={!isPlanApproved?.code || !landStatus?.code || !projectComponent?.code || error || !estimateResponse?.data?.Calculations}
    >
      <div style={{ marginTop: "10px", display: "flex", justifyContent: "space-between" }}>
        <div style={{ flex: 1 }}>
          <CardLabel>{t("PREAPPROVE_LAYOUT_TYPE_HEADER")}<span className="check-page-link-button"> *</span></CardLabel>
          <Dropdown t={t} optionKey="key" isMandatory={true} option={planArrpovedOptione} selected={isPlanApproved} select={setIsPlanApproved}/>
          
          <div style={{ marginTop: "10px" }}>
            <CardLabel>{t("PREAPPROVE_LAND_STATUS")}<span className="check-page-link-button"> *</span></CardLabel>
            <Dropdown t={t} optionKey="key" isMandatory={true} option={landStatusOptions} selected={landStatus} select={setLandStatus}/>
          </div>
          
          <CardLabel>{t("PREAPPROVE_PROJECT_COMPONENT")}<span className="check-page-link-button"> *</span></CardLabel>
          <Dropdown t={t} optionKey="key" isMandatory={true} option={projectComponentOptions} selected={projectComponent} select={setProjectComponent}/>
          
          {error === "" && (
            <React.Fragment>
              <CardLabel>{t("PREAPPROVE_LENGHT_OF_PLOT")}<span className="check-page-link-button"> *</span></CardLabel>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '6px', marginTop: '10px' }}>
                <div style={{ flex: 1 }}>
                  <CardLabel style={{fontSize:"18px"}}>{t("FEET")}
                    <TextInput
                      t={t}
                      // type="number"
                      isMandatory={config.isMandatory}
                      value={lengthInFeet || ''}
                      onChange={handleInputChange(setLengthInFeet, t("PREAPPROVE_LENGHT_OF_PLOT"))}
                      label={t("Length in Feet")}
                      style={{ width: '79%' }}
                    />
                  </CardLabel>
                </div>
                <div style={{ flex: 1 }}>
                  <CardLabel style={{fontSize:"18px"}}>{t("INCHES")}
                    <TextInput
                      t={t}
                      // type="number"
                      isMandatory={config.isMandatory}
                      value={lengthInInches || ''}
                      onChange={handleInchesInput(setLengthInInches)}
                      label={t("Length in Inches")}
                      style={{ width: '79%' }}
                    />
                  </CardLabel>
                  
                </div>
              </div>
  
              <CardLabel>{t("PREAPPROVE_PLOT_WITH_IN_FT")}<span className="check-page-link-button"> *</span></CardLabel>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '6px', marginTop: '10px' }}>
                <div style={{ flex: 1 }}>
                  <CardLabel style={{fontSize:"18px"}}>{t("FEET")}
                    <TextInput
                      t={t}
                      // type="number"
                      isMandatory={config.isMandatory}
                      value={widthInFeet || ''}
                      onChange={handleInputChange(setWidthInFeet,t("PREAPPROVE_PLOT_WITH_IN_FT"))}
                      label={t("Width in Feet")}
                      style={{ width: '79%' }}
                    />
                  </CardLabel>
                </div>
                <div style={{ flex: 1 }}>
                  <CardLabel style={{fontSize:"18px"}}>{t("INCHES")}
                    <TextInput
                      t={t}
                      // type="number"
                      isMandatory={config.isMandatory}
                      value={widthInInches || ''}
                      onChange={handleInchesInput(setWidthInInches)}
                      label={t("Width in Inches")}
                      style={{ width: '79%' }}
                    />
                  </CardLabel>
                </div>
              </div>
              <CardLabel>{t("PREAPPROVE_ABUTTING_ROAD")}<span className="check-page-link-button"> *</span></CardLabel>
              <TextInput
                t={t}
                //type="number"
                isMandatory={config.isMandatory}
                value={abuttingRoadWidth || ''}
                onChange={handleInputChange(setAbuttingRoadWidth, t("PREAPPROVE_ABUTTING_ROAD"))}
              />
              <div style={{ marginTop: "10px", marginBottom:"10px"}}>
               <SubmitBar label={t("SEARCH")}  onSubmit={getPreApprovedPlanDetails} disabled={!lengthInFeet || ! widthInFeet || !abuttingRoadWidth}/> 
              </div>
            </React.Fragment>
          )}
          
          {imagesToShowBelowComplaintDetails?.thumbs ? (
            <div>
              <CardLabel style={{ marginTop: '18px', fontWeight: 'bolder', marginBottom: "15px" }}>{t("")}</CardLabel>
              <DisplayPhotosnew srcs={imagesToShowBelowComplaintDetails} onClick={(source, index) => zoomImageWrapper(source, index)} />
            </div>
          ) : null}
          {preApprovedResponse?.data && preApprovedResponse?.data.length===0 ? (
            <div style={{ marginBottom: "15px", marginTop: "18px" }}>{t("PLOTS_NOT_AVAILABLE")}</div>
          ):null}
          
          {estimateResponse?.data?.Calculations ? (
            getDetailsRow(estimateResponse?.data?.Calculations[0])
          ) : null}
          
          {selectedPlot && selectedPlot?.documents?.length > 0 ? (
            <div>
            <Header>{t("PLAN_DRAWING_IMAGES")}</Header>
              <div style={{ display: "flex", alignItems: "center", marginBottom: "1rem" }}>
                <span style={{ fontWeight: "bold", marginRight: "10rem"}}>{t("BPA_UPLOADED_PDF_DIAGRAM")}</span>
                <ActionButton
                  label={t(selectedPlot?.documents.find(doc => doc?.additionalDetails?.fileName.includes("pdf"))?.additionalDetails?.fileName)}
                  jumpTo={selectedPlot?.documents.find(doc => doc?.additionalDetails?.fileName.includes("pdf"))?.additionalDetails?.fileUrl}
                />
              </div>

              <div style={{ display: "flex", alignItems: "center", marginBottom: "1rem" }}>
                <span style={{ fontWeight: "bold", marginRight: "10.2rem" }}>{t("BPA_UPLOADED_CAD_DIAGRAM")}</span>
                <ActionButton
                  label={t(selectedPlot?.documents.find(doc => doc?.additionalDetails?.fileName.includes("dxf"))?.additionalDetails?.fileName)}
                  jumpTo={selectedPlot?.documents.find(doc => doc?.additionalDetails?.fileName.includes("dxf"))?.additionalDetails?.fileUrl}
                />
              </div>

              <div style={{ display: "flex", alignItems: "center", marginBottom: "1rem" }}>
                <span style={{ fontWeight: "bold", marginRight: "9rem" }}>{t("BPA_UPLOADED_IMAGE_DIAGRAM")}</span>
                <ActionButton
                  label={t(selectedPlot?.documents.find(doc => doc?.additionalDetails?.fileName.includes("jpg"))?.additionalDetails?.fileName)}
                  jumpTo={selectedPlot?.documents.find(doc => doc?.additionalDetails?.fileName.includes("jpg"))?.additionalDetails?.fileUrl}
                />
              </div>
            </div>
        ) : null}
          
        </div>
        
        <div style={{ marginLeft: "20px", display: "flex", flexDirection: "column", alignItems: "flex-end", marginTop: "40px" }}>
          <img
            src={plotImage}
            alt="Description"
            style={{ width: "300px", height: "auto", borderRadius: "8px" }}
          />
        </div>
      </div>
    </FormStep>
  
    {imageZoom ? <ImageViewer imageSrc={imageZoom} onClose={onCloseImageZoom} /> : null}
    {error && <Toast error={error} label={error} onClose={() => setError("")} />}
    {inputError && <Toast error={inputError} label={inputError} onClose={() => setInputError("")} />}
    {mandatoryFieldsError && <Toast error={mandatoryFieldsError} label={mandatoryFieldsError} onClose={() => setError("")} />}
  </div>
  

  );
  
};

export default BuildingPlanScrutiny;