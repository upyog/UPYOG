import { CardLabel, FormStep, Dropdown, TextInput, Toast, SearchIcon, DisplayPhotos, Row, ImageViewer, StatusTable, Header } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { PreApprovedPlanService } from "../../../../libraries/src/services/elements/PREAPPROVEDPLAN";
import  usePreApprovedSearch  from "../../../../libraries/src/hooks/obps/usePreApprovedSearch";
const BuildingPlanScrutiny = ({ t, config, onSelect, formData, isShowToast, isSubmitBtnDisable, setIsShowToast }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [isPlanApproved, setIsPlanApproved] = useState();
  const [landStatus, setLandStatus] = useState();
  const [projectComponent, setProjectComponent] = useState();
  //const [lengthOfPlot, setLengthOfPlot] = useState();
  const [lengthInFeet, setLengthInFeet] = useState();
  const [lengthInInches, setLengthInInches] = useState();
  //const [widthOfPlot, setWidthOfPlot] = useState();
  const [widthInFeet, setWidthInFeet] = useState();
  const [widthInInches, setWidthInInches] = useState();
  const [abuttingRoadWidth, setAbuttingRoadWidth] = useState();
  //const [preApprovedResponse, setPreApprovedResponse] = useState();
  const [error, setError] = useState();
  const [imageZoom, setImageZoom] = useState(null);
  const [mandatoryFieldsError, setMandatoryFiledsError] = useState();
  const [imagesToShowBelowComplaintDetails, setImagesToShowBelowComplaintDetails] = useState();
  const [selectedPlot, setSelectedPlot] = useState();
  const [estimate, setEstimate] = useState();
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
    { code: "PRE_APPROVED_RESIDENTIAL", key: "PreApproved Residential" },
    { code: "OTHERS", key: "Others" },
  ];
  const [searchData, setSearchData] = useState(null); 
  console.log("searchDayta", searchData)
  //const [searchResults, setSearchResults] = useState(null); 
  const preApprovedResponse = usePreApprovedSearch(searchData, {enabled:true});
  console.log("prrrrrr", preApprovedResponse)
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
    if (isPlanApproved?.code === "NO" || projectComponent?.code === "OTHERS") {
      setError(t("PRE_APPROVED_PLAN_CANNOT_CREATED_WITH_SELECTED"));
    } else if (landStatus?.code === "BUILDING_CONSTRUCTED" || landStatus?.code === "UNDER_CONSTRUCTION") {
      setError(t("CANNOT_APPLY_FOR_PLOT_UNDER_CONSTRUCTION_ALREADY_CONSTRUCTED"));
    } else {
      setError("");
    }
  }, [isPlanApproved, landStatus, projectComponent]);

  const handleInputChange = (setter) => (e) => setter(e.target.value);
  const getPreApprovedPlanDetails = () => {
    const data = { 
      plotLengthInFeet: lengthInInches ? parseInt(lengthInFeet) + parseInt((lengthInInches * (1 / 12))) : parseInt(lengthInFeet), 
      plotWidthInFeet: widthInInches ? parseInt(widthInFeet) + parseInt((widthInInches * (1 / 12))) : parseInt(widthInFeet), 
      roadWidth: abuttingRoadWidth 
    };

    if (!lengthInFeet || !widthInFeet || !abuttingRoadWidth) {
      setMandatoryFiledsError(t("PLEASE_FILL_MANDATORY_DETAILS"));
    } else {
      setSearchData(data); 
      setMandatoryFiledsError("");
    }
  };
  useEffect(() => {
    if (preApprovedResponse?.data!==undefined) {
      const fileStoreIds = preApprovedResponse?.preapprovedPlan.flatMap(item =>
        item.documents
          .filter(doc => doc?.additionalDetails?.title?.includes("IMAGE"))
          .map(doc => doc?.fileStoreId)
      );
      const thumbnails = fileStoreIds ? getThumbnails(fileStoreIds, tenantId) : null;
      setImagesToShowBelowComplaintDetails(thumbnails);
    }
  }, [preApprovedResponse]);

  const handleNext = () => {
    formData.selectedPlot=selectedPlot;
    formData.estimate=estimate;
    formData.lengthOfPlot=parseInt(lengthInFeet)+parseInt(lengthInInches*(1/12));
    formData.widthOfPlot=parseInt(widthInFeet)+parseInt(widthInInches*(1/12));
    formData.isPlanApproved=isPlanApproved;
    formData.landStatus=landStatus;
    formData.projectComponent=projectComponent;
    formData.abuttingRoadWidth=abuttingRoadWidth;
    onSelect("", formData);
  }
  const onSkip = () => {
    clearForm();
};
  const getThumbnails =  (ids, tenantId) => {
    console.log("inside get thumb nails")
    //const res =  Digit.UploadServices.Filefetch(ids, tenantId);
    const res = {
        "5c1d7695-61d4-424e-a04a-6a1f5dd1880f": "https://sujog-dev.odisha.gov.in/filestore/v1/files/id?fileStoreId=5c1d7695-61d4-424e-a04a-6a1f5dd1880f&tenantId=od",
        "fileStoreIds": [
            {
                "id": "5c1d7695-61d4-424e-a04a-6a1f5dd1880f",
                "url": "https://sujog-dev.odisha.gov.in/filestore/v1/files/id?fileStoreId=5c1d7695-61d4-424e-a04a-6a1f5dd1880f&tenantId=od",
                "localUrl": "http://egov-filestore:8080/filestore/v1/files/id?fileStoreId=5c1d7695-61d4-424e-a04a-6a1f5dd1880f&tenantId=od"
            },
            {
                "id": "0cdc6c13-a753-4a63-a068-82d4d70d7d61",
                "url": "https://sujog-dev.odisha.gov.in/filestore/v1/files/id?fileStoreId=0cdc6c13-a753-4a63-a068-82d4d70d7d61&tenantId=od",
                "localUrl": "http://egov-filestore:8080/filestore/v1/files/id?fileStoreId=0cdc6c13-a753-4a63-a068-82d4d70d7d61&tenantId=od"
                
            }
        ]
    }
    if (res.fileStoreIds && res.fileStoreIds.length !== 0) {
        return { 
            thumbs: res.fileStoreIds.map(o => o.url), 
            fullImage: res.fileStoreIds.map(o => Digit.Utils.getFileUrl(o.url)) 
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
      <Row label={t("BPA_DRAWING_NUMBER")} text={estimateDetails?.BPA?.edcrNumber || "NA"} />
      {estimateDetails?.taxHeadEstimates.map((item, index) => (
        <Row key={index} label={t(`${item.taxHeadCode}`)} text={item.estimateAmount} />
      ))}  
      {/* <Row label={t("BPA_BUILDING_OPERATION_FEE")} text={estimateDetails?.} />  */}
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
  function zoomImageWrapper(imageSource, index){
    //if(imageSource.includes("small")){
      zoomImage(imagesToShowBelowComplaintDetails?.fullImage[index]);
      const selectedObject = preApprovedResponse?.preapprovedPlan.find(obj => {
        return obj.documents.some(doc => doc.additionalDetails?.fileUrl === imagesToShowBelowComplaintDetails?.fullImage[index]);
    });
    setSelectedPlot(selectedObject)
    estimateData(selectedObject);
      
   // }
    // else{
    //   window.open(imagesToShowBelowComplaintDetails?.fullImage[index]);
    //   setEstimate(estimateResponse);
    // }   
  }
  function estimateData(selectedObject){
    const CalulationCriteria = [
      {
          "BPA": {
              "edcrNumber": selectedObject?.drawingNo,
              "riskType": "LOW",
              "businessService": "BPA6",
              "applicationType": "BUILDING_PLAN_SCRUTINY",
              "serviceType": "NEW_CONSTRUCTION",
              "tenantId": tenantId
          },
          "applicationNo": "",
          "feeType": "ApplicationFee",
          "tenantId": tenantId,
          "applicationType": "BUILDING_PLAN_SCRUTINY",
          "serviceType": "NEW_CONSTRUCTION"
      }
  ]
  const estimateResponse = PreApprovedPlanService.estimate({CalulationCriteria:CalulationCriteria})

setEstimate(estimateResponse)
  }
  
  function onCloseImageZoom() {
    setImageZoom(null);
  }
//   const getPreApprovedPlanDetails =()=> {
//     console.log("clicked")
//     const data = { plotLengthInFeet: lengthInInches ? parseInt(lengthInFeet)+parseInt((lengthInInches*(1/12))): parseInt(lengthInFeet), plotWidthInFeet: widthInInches ? parseInt(widthInFeet)+parseInt((widthInInches*(1/12))): parseInt(widthInFeet), roadWidth: abuttingRoadWidth };
//     if(!lengthInFeet || !widthInFeet || !abuttingRoadWidth){
//       setMandatoryFiledsError(t("PLEASE_FILL_MANDATORY_DETAILS"));
//     }else{
//         setSearchData(data);
//       setMandatoryFiledsError("")
    
//     //setPreApprovedResponse(preApprovedResponses);
//     //   const filestoreids = preApprovedResponse?.preapprovedPlan.flatMap(item => 
//     //   item.documents
//     //     .filter(doc => doc?.additionalDetails?.title?.includes("IMAGE"))
//     //     .map(doc => doc?.fileStoreId)
//     //   );
//     //   console.log("fileStoreids", filestoreids)
//     //   const thumbnails = filestoreids ?  getThumbnails(filestoreids,tenantId) : null;
//     //   setImagesToShowBelowComplaintDetails(thumbnails)  
//     }
//     useEffect(() => {
//         if (preApprovedResponse) {
//           const fileStoreIds = preApprovedResponse?.preapprovedPlan.flatMap(item =>
//             item.documents
//               .filter(doc => doc?.additionalDetails?.title?.includes("IMAGE"))
//               .map(doc => doc?.fileStoreId)
//           );
    
//           const thumbnails = fileStoreIds ? getThumbnails(fileStoreIds, tenantId) : null;
//           setImagesToShowBelowComplaintDetails(thumbnails);
//         }
//       }, [preApprovedResponse]);
    
//   };

  return (
    <div>
    <FormStep
      t={t}
      config={config}
      onSelect={handleNext}
      onSkip={onSkip}
      isDisabled={!isPlanApproved?.code || !landStatus?.code || !projectComponent?.code || error || !estimate?.Calculations}
    >
      <div style={{ marginTop: "10px", display: "flex", justifyContent: "space-between" }}>
        <div style={{ flex: 1 }}>
          <CardLabel>{t("PREAPPROVE_LAYOUT_TYPE_HEADER")} *</CardLabel>
          <Dropdown t={t} optionKey="key" isMandatory={true} option={planArrpovedOptione} selected={isPlanApproved} select={setIsPlanApproved}/>
          
          <div style={{ marginTop: "10px" }}>
            <CardLabel>{t("PREAPPROVE_LAND_STATUS")} *</CardLabel>
            <Dropdown t={t} optionKey="key" isMandatory={true} option={landStatusOptions} selected={landStatus} select={setLandStatus}/>
          </div>
          
          <CardLabel>{t("PREAPPROVE_PROJECT_COMPONENT")} *</CardLabel>
          <Dropdown t={t} optionKey="key" isMandatory={true} option={projectComponentOptions} selected={projectComponent} select={setProjectComponent}/>
          
          {error === "" && (
            <React.Fragment>
              <CardLabel>{t("PREAPPROVE_LENGHT_OF_PLOT")} *</CardLabel>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '6px', marginTop: '10px' }}>
                <div style={{ flex: 1 }}>
                  <CardLabel style={{fontSize:"18px"}}>{t("FEET")}
                    <TextInput
                      t={t}
                      type="number"
                      isMandatory={config.isMandatory}
                      value={lengthInFeet || ''}
                      onChange={handleInputChange(setLengthInFeet)}
                      label={t("Length in Feet")}
                      style={{ width: '79%' }}
                    />
                  </CardLabel>
                </div>
                <div style={{ flex: 1 }}>
                  <CardLabel style={{fontSize:"18px"}}>{t("INCHES")}
                    <TextInput
                      t={t}
                      type="number"
                      isMandatory={config.isMandatory}
                      value={lengthInInches || ''}
                      onChange={handleInputChange(setLengthInInches)}
                      label={t("Length in Inches")}
                      style={{ width: '79%' }}
                    />
                  </CardLabel>
                </div>
              </div>
  
              <CardLabel>{t("PREAPPROVE_PLOT_WITH_IN_FT")} *</CardLabel>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '6px', marginTop: '10px' }}>
                <div style={{ flex: 1 }}>
                  <CardLabel style={{fontSize:"18px"}}>{t("FEET")}
                    <TextInput
                      t={t}
                      type="number"
                      isMandatory={config.isMandatory}
                      value={widthInFeet || ''}
                      onChange={handleInputChange(setWidthInFeet)}
                      label={t("Width in Feet")}
                      style={{ width: '79%' }}
                    />
                  </CardLabel>
                </div>
                <div style={{ flex: 1 }}>
                  <CardLabel style={{fontSize:"18px"}}>{t("INCHES")}
                    <TextInput
                      t={t}
                      type="number"
                      isMandatory={config.isMandatory}
                      value={widthInInches || ''}
                      onChange={handleInputChange(setWidthInInches)}
                      label={t("Width in Inches")}
                      style={{ width: '79%' }}
                    />
                  </CardLabel>
                </div>
              </div>
              <CardLabel>{t("PREAPPROVE_ABUTTING_ROAD")} *</CardLabel>
              <TextInput
                t={t}
                type="number"
                isMandatory={config.isMandatory}
                value={abuttingRoadWidth || ''}
                onChange={handleInputChange(setAbuttingRoadWidth)}
              />
              <div style={{ position: "relative", marginTop: "-40px", marginLeft: "500px" }} onClick={getPreApprovedPlanDetails}>
                <SearchIcon />
              </div>
            </React.Fragment>
          )}
          
          {imagesToShowBelowComplaintDetails?.thumbs ? (
            <div>
              <CardLabel style={{ marginTop: '18px', fontWeight: 'bolder', marginBottom: "15px" }}>{t("")}</CardLabel>
              <DisplayPhotos srcs={imagesToShowBelowComplaintDetails?.thumbs} onClick={(source, index) => zoomImageWrapper(source, index)} />
            </div>
          ) : <div style={{ marginBottom: "15px", marginTop: "18px" }}>{t("PLOTS_NOT_AVAILABLE")}</div>}
          
          {estimate?.Calculations ? (
            getDetailsRow(estimate?.Calculations[0])
          ) : null}
          
          {selectedPlot && selectedPlot?.documents?.length > 0 && (
            <div style={{ marginTop: "20px", display: "flex", gap: "10px", marginBottom: "10px" }}>
              {selectedPlot?.documents?.map((doc, index) => (
                <div
                  key={index}
                  style={{
                    padding: '10px',
                    border: '1px solid #ccc',
                    borderRadius: '8px',
                    textAlign: 'center',
                    cursor: 'pointer',
                    width: "auto",
                    display: 'flex',
                    alignItems: 'center',
                    backgroundColor: 'lightgoldenrodyellow'
                  }}
                  onClick={() => window.location.href = doc?.additionalDetails?.fileUrl}
                >
                  <CardLabel style={{ color: 'rgb(255, 133, 27)' }}>{t(`${doc?.additionalDetails?.title}`)}</CardLabel>
                </div>
              ))}
            </div>
          )}
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
    {mandatoryFieldsError && <Toast error={mandatoryFieldsError} label={mandatoryFieldsError} onClose={() => setError("")} />}
  </div>
  

  );
  
};

export default BuildingPlanScrutiny;