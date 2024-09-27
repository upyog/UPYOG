import {
  Card,
  CardSubHeader,
  EditIcon,
  Header,
  LinkButton,
  Loader,
  PopUp,
  Row,
  StatusTable,
  SubmitBar,
  LinkLabel
} from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useHistory, useParams } from "react-router-dom";
import PropertyDocument from "../../../pageComponents/PropertyDocument";
import { getCityLocale, getPropertyTypeLocale, stringReplaceAll } from "../../../utils";
import ActionModal from "../../../../../templates/ApplicationDetails/Modal/index"
import ArrearSummary from "../../../../../common/src/payments/citizen/bills/routes/bill-details/arrear-summary";
const setBillData = async (tenantId, propertyIds, updatefetchBillData, updateCanFetchBillData) => {
  const assessmentData = await Digit.PTService.assessmentSearch({ tenantId, filters: { propertyIds } });
  let billData = {};
  if (assessmentData?.Assessments?.length > 0) {
    billData = await Digit.PaymentService.fetchBill(tenantId, {
      businessService: "PT",
      consumerCode: propertyIds,
    });
  }
  updatefetchBillData(billData);
  updateCanFetchBillData({
    loading: false,
    loaded: true,
    canLoad: true,
  });
};

const getBillAmount = (fetchBillData = null) => {
  if (fetchBillData == null) return "CS_NA";
  return fetchBillData ? (fetchBillData?.Bill && fetchBillData.Bill[0] ? fetchBillData.Bill[0]?.totalAmount : "0") : "0";
};

const PropertyInformation = () => {
  const { t } = useTranslation();
  const { propertyIds } = useParams();
const [showModal,setshowModal] = useState(false)
  var isMobile = window.Digit.Utils.browser.isMobile();
  const [enableAudit, setEnableAudit] = useState(false);
const moduleCode="PT"
const history = useHistory();
const selectedAction =    {
  action: "ASSESS_PROPERTY",
  forcedName: "PT_ASSESS",
  showFinancialYearsModal: true,
  customFunctionToExecute: (data) => {
    //const history = useHistory();
    delete data.customFunctionToExecute;
    history.replace({ pathname: `/digit-ui/citizen/pt/assessment-details/${property.propertyId}`, state: { ...data } });
  },
  tenantId: Digit.ULBService.getStateId(),
}
const { id: applicationNumber } = useParams();
const [isEnableLoader, setIsEnableLoader] = useState(false);
const [isWarningPop, setWarningPopUp] = useState(false);
const businessService="PT"
const state = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: UpdateNumberConfig } = Digit.Hooks.useCommonMDMS(Digit.ULBService.getStateId(),"PropertyTax",["UpdateNumber"],{
    select: (data) => {
      return data?.PropertyTax?.UpdateNumber?.[0];
    },
    retry:false,
    enable:false
  });

  const { isLoading, isError, error, data } = Digit.Hooks.pt.usePropertySearch({ filters: { propertyIds, tenantId } }, { filters: { propertyIds, tenantId } });

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.pt.usePropertySearch(
    {
      tenantId,
      filters: { propertyIds, audit: true },
    },
    {
      enabled: enableAudit,
      select: (d) =>
        d.Properties.filter((e) => e.status === "ACTIVE")?.sort((a, b) => b.auditDetails.lastModifiedTime - a.auditDetails.lastModifiedTime),
    }
  );

  const [popup, showPopup] = useState(false);
  const [billData, updateCanFetchBillData] = useState({
    loading: false,
    loaded: false,
    canLoad: false,
  });

  const [fetchBillData, updatefetchBillData] = useState({});

  const [property, setProperty] = useState(() => data?.Properties[0] || " ");
  const mutation = Digit.Hooks.pt.usePropertyAPI(property?.tenantId, false);

  let specialCategoryDoc = [];
  property?.documents?.filter(ob => ob.documentType.includes("SPECIALCATEGORYPROOF")).map((doc) => {
      specialCategoryDoc.push(doc);
  })
  
  useEffect(() => {
    if (data) {
      setProperty(data?.Properties[0]);
      if (data?.Properties[0]?.status !== "ACTIVE") setEnableAudit(true);
    }
  }, [data]);

  useEffect(() => {
    if (auditData?.[0]) {
      const property = auditData?.[0] || {};
      property.owners = property?.owners?.filter((owner) => owner.status == "ACTIVE");
      setProperty(property);
    }
  }, [enableAudit, auditData]);
const handleClick=()=>{

  setshowModal(true)
}
const onBifurcate = () =>{
  history.push({pathname: "/digit-ui/citizen/pt/property/new-application", state: {propertyDetails: property, action: 'BIFURCATION'}})
}
const onAppeal =()=>{
  history.push(`/digit-ui/citizen/pt/property/appeal/${property?.propertyId}`);

}
  sessionStorage.setItem("pt-property", JSON.stringify(property));
  let docs = [];
  docs = property?.documents;
  let units = [];
  let owners = [];
  owners = property?.owners;
  units = property?.units;
  units &&
    units.sort((x, y) => {
      let a = x.floorNo,
        b = y.floorNo;
      if (x.floorNo < 0) {
        a = x.floorNo * -20;
      }
      if (y.floorNo < 0) {
        b = y.floorNo * -20;
      }
      if (a > b) {
        return 1;
      } else {
        return -1;
      }
    });

  if (isLoading) {
    return <Loader />;
  }

  if (property?.status == "ACTIVE" && !billData.loading && !billData.loaded && !billData.canLoad) {
    updateCanFetchBillData({
      loading: false,
      loaded: false,
      canLoad: true,
    });
  }
  if (billData?.canLoad && !billData.loading && !billData.loaded) {
    updateCanFetchBillData({
      loading: true,
      loaded: false,
      canLoad: true,
    });
    setBillData(property?.tenantId || tenantId, propertyIds, updatefetchBillData, updateCanFetchBillData);
  }

  let flrno,
    i = 0;
  flrno = units && units[0]?.floorNo;
  const ActionButton = ({ jumpTo, style }) => {
    const { t } = useTranslation();
    const history = useHistory();
    function routeTo() {
      history.push(jumpTo);
    }
    return <LinkButton style={style} label={t("PT_OWNER_HISTORY")} className="check-page-link-button" onClick={routeTo} />;
  };
  const UpdatePropertyNumberComponent = Digit?.ComponentRegistryService?.getComponent("UpdateNumber");
 
  const submitAction = async (data, nocData = false, isOBPS = {}) => {

      setIsEnableLoader(true);
      if (typeof data?.customFunctionToExecute === "function") {
       
        data?.customFunctionToExecute({ ...data });
       
      }
      if (nocData !== false && nocMutation) {
        const nocPrmomises = nocData?.map((noc) => {
          return nocMutation?.mutateAsync(noc);
        });
        try {
          setIsEnableLoader(true);
          const values = await Promise.all(nocPrmomises);
          values &&
            values.map((ob) => {
              Digit.SessionStorage.del(ob?.Noc?.[0]?.nocType);
            });
        } catch (err) {
          setIsEnableLoader(false);
          let errorValue = err?.response?.data?.Errors?.[0]?.code
            ? t(err?.response?.data?.Errors?.[0]?.code)
            : err?.response?.data?.Errors?.[0]?.message || err;
          closeModal();
          setShowToast({ key: "error", error: { message: errorValue } });
          setTimeout(closeToast, 5000);
          return;
        }
      }
      // if (mutate) {
      //   setIsEnableLoader(true);
      //   mutate(data, {
      //     onError: (error, variables) => {
      //       setIsEnableLoader(false);
      //       setShowToast({ key: "error", error });
      //       setTimeout(closeToast, 5000);
      //     },
      //     onSuccess: (data, variables) => {
      //       sessionStorage.removeItem("WS_SESSION_APPLICATION_DETAILS");
      //       setIsEnableLoader(false);
      //       if (isOBPS?.bpa) {
      //         data.selectedAction = selectedAction;
      //         history.replace(`/digit-ui/employee/obps/response`, { data: data });
      //       }
      //       if (isOBPS?.isStakeholder) {
      //         data.selectedAction = selectedAction;
      //         history.push(`/digit-ui/employee/obps/stakeholder-response`, { data: data });
      //       }
      //       if (isOBPS?.isNoc) {
      //         history.push(`/digit-ui/employee/noc/response`, { data: data });
      //       }
      //       if (data?.Amendments?.length > 0 ){
      //         //RAIN-6981 instead just show a toast here with appropriate message
      //       //show toast here and return 
      //         //history.push("/digit-ui/employee/ws/response-bill-amend", { status: true, state: data?.Amendments?.[0] })
              
      //         if(variables?.AmendmentUpdate?.workflow?.action.includes("SEND_BACK")){
      //           setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_SEND_BACK_UPDATE_SUCCESS")})
      //         } else if (variables?.AmendmentUpdate?.workflow?.action.includes("RE-SUBMIT")){
      //           setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_RE_SUBMIT_UPDATE_SUCCESS") })
      //         } else if (variables?.AmendmentUpdate?.workflow?.action.includes("APPROVE")){
      //           setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_APPROVE_UPDATE_SUCCESS") })
      //         }
      //         else if (variables?.AmendmentUpdate?.workflow?.action.includes("REJECT")){
      //           setShowToast({ key: "success", label: t("ES_MODIFYWSCONNECTION_REJECT_UPDATE_SUCCESS") })
      //         }            
      //         return
      //       }
      //       setShowToast({ key: "success", action: selectedAction });
      //       clearDataDetails && setTimeout(clearDataDetails, 3000);
      //       setTimeout(closeToast, 5000);
      //       queryClient.clear();
      //       queryClient.refetchQueries("APPLICATION_SEARCH");
      //       //push false status when reject
            
      //     },
      //   });
      // }
  
      closeModal();
 
  };
  if (isLoading || isEnableLoader) {
    return <Loader />;
  }
  const closeModal = () => {
    setshowModal(false)
  };

  const closeWarningPopup = () => {
    setWarningPopUp(false);
  };
  const handleClickOnPtPgr=()=>{
  sessionStorage.setItem("type","PT" );
  sessionStorage.setItem("pincode", data.Properties[0].address.pincode);
  sessionStorage.setItem("tenantId", data.Properties[0].address.tenantId);
  sessionStorage.setItem("localityCode", data.Properties[0].address.locality.code);
  sessionStorage.setItem("landmark", data.Properties[0].address.landmark); 
  sessionStorage.setItem("propertyid",data.Properties[0].propertyId)  ;
  history.push(`/digit-ui/citizen/pgr/create-complaint/complaint-type?propertyId=${property.propertyId}`);
  }
  return (
    <React.Fragment>
      <Header>{t("PT_PROPERTY_INFORMATION")}</Header>
      <div>
        <Card>
          <StatusTable>
            <Row className="border-none" label={t("PT_PROPERTY_PTUID")} text={`${property.propertyId || t("CS_NA")}`} /* textStyle={{ whiteSpace: "pre" }} */ />
            <Row className="border-none" label={t("CS_COMMON_TOTAL_AMOUNT_DUE")} text={`₹${t(getBillAmount(fetchBillData))}`} />
            <LinkLabel
            onClick={() => history.push({ pathname: `/digit-ui/citizen/pt/payment-details/${property?.propertyId}`})}
            style={isMobile ? { marginTop: "15px", marginLeft: "0px" } : { marginTop: "15px" }}
          >
            {t("PT_VIEW_PAYMENT")}
          </LinkLabel>
          </StatusTable>
          <ArrearSummary bill={fetchBillData.Bill?.[0]} />
          <CardSubHeader>{t("PT_PROPERTY_ADDRESS_SUB_HEADER")}</CardSubHeader>
          <StatusTable>
            <Row className="border-none" label={t("PT_PROPERTY_ADDRESS_PINCODE")} text={`${property.address?.pincode || t("CS_NA")}`} />
            <Row className="border-none" label={t("PT_COMMON_CITY")} text={`${t(getCityLocale(property?.tenantId)) || t("CS_NA")}`} />
            <Row className="border-none" label={t("PT_COMMON_LOCALITY_OR_MOHALLA")} text={`${t(property?.address?.locality?.name)}` || t("CS_NA")} />
            <Row className="border-none" label={t("PT_PROPERTY_ADDRESS_STREET_NAME")} text={`${property.address?.street || t("CS_NA")}`} />
            <Row className="border-none" label={t("PT_PROPERTY_ADDR_DOOR_HOUSE_NO")} text={`${property.address?.doorNo || t("CS_NA")}`} />
          </StatusTable>
          <CardSubHeader>{t("PT_PROPERTY_ASSESSMENT_DETAILS_HEADER")}</CardSubHeader>
          <StatusTable>
            {/* <Row 
              className="border-none" 
              label={t("PT_ASSESMENT_INFO_USAGE_TYPE")}
              text={
                `${t(
                  (property.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPSUBUSGTYPE_") +
                    (property?.usageCategory?.split(".")[1] ? property?.usageCategory?.split(".")[1] : property.usageCategory)
                )}` || t("CS_NA")
              }
            /> */}
            <Row className="border-none" label={t("PT_COMMON_PROPERTY_TYPE")} text={`${t(getPropertyTypeLocale(property?.propertyType))}` || t("CS_NA")} />
            <Row className="border-none" label={t("PT_ASSESMENT1_PLOT_SIZE")} text={`${property.landArea} sq.ft` || t("CS_NA")} />
            <Row className="border-none" label={t("PT_ASSESMENT_INFO_NO_OF_FLOOR")} text={`${property.noOfFloors || t("CS_NA")}`} />
            {/* <Row className="border-none" label={t("PT_ASSESSMENT1_ELECTRICITY")} text={`${property?.additionalDetails?.electricity || t("CS_NA")}`} />
            <Row className="border-none" label={t("PT_ASSESSMENT1_UID")} text={`${property?.additionalDetails?.uid || t("CS_NA")}`} /> */}
          </StatusTable>
          <div>
            {Array.isArray(units) &&
              units.length > 0 &&
              units.map((unit, index) => (
                <div key={index}>
                  {(flrno !== unit?.floorNo ? (i = 1) : (i = i + 1)) && i === 1 && (
                    <CardSubHeader>{t(`PROPERTYTAX_FLOOR_${unit?.floorNo}`)}</CardSubHeader>
                  )}
                  <div style={{ border: "groove", marginBottom:"10px", borderRadius: "6px" }}>
                    <CardSubHeader>
                      {t("ES_APPLICATION_DETAILS_UNIT")} {i}
                    </CardSubHeader>
                    {(flrno = unit?.floorNo) > -5 && (
                      <StatusTable>
                        <Row 
                          className="border-none" 
                          label={t("PT_ASSESSMENT_UNIT_USAGE_TYPE")}
                          text={
                            `${t("COMMON_PROPUSGTYPE_NONRESIDENTIAL_"+unit?.usageCategory
                              // (property.usageCategory !== "RESIDENTIAL" ? "COMMON_PROPUSGTYPE_NONRESIDENTIAL_" : "COMMON_PROPUSGTYPE_") +
                              //   (property?.usageCategory?.split(".")[1] ? property?.usageCategory?.split(".")[1] : property.usageCategory) 
                                /* (property.usageCategory !== "RESIDENTIAL" ? "_" + unit?.usageCategory.split(".").pop() : "") */
                            )}` || t("CS_NA")
                          }
                        />
                        <Row className="border-none" label={t("PT_OCCUPANY_TYPE_LABEL")} text={`${t("PROPERTYTAX_OCCUPANCYTYPE_" + unit?.occupancyType)}` || t("CS_NA")} />
                        <Row className="border-none" label={t("PT_BUILTUP_AREA_LABEL")} text={`${`${unit?.constructionDetail?.builtUpArea} sq.ft` || t("CS_NA")}`} />
                        {unit.occupancyType == "RENTED" && (
                          <Row className="border-none" label={t("PT_FORM2_TOTAL_ANNUAL_RENT")} text={`${(unit?.arv && `₹${unit?.arv}`) || t("CS_NA")}`} />
                        )}
                      </StatusTable>
                    )}
                  </div>
                </div>
              ))}
          </div>
          <CardSubHeader>{t("PT_COMMON_PROPERTY_OWNERSHIP_DETAILS_HEADER")}</CardSubHeader>
          <div className="owner-details">
            {Array.isArray(owners) &&
              owners.sort((item,item2)=>{return item?.additionalDetails?.ownerSequence - item2?.additionalDetails?.ownerSequence}).map((owner, index) => (
                <div key={index} className="owner-details-child">
                  <CardSubHeader>
                    {owners.length != 1 && (
                      <span>
                        {t("PT_OWNER_SUB_HEADER")} - {index + 1}{" "}
                      </span>
                    )}
                  </CardSubHeader>
                  <StatusTable>
                  {property?.institution && property?.institution?.name && <Row className="border-none" label={t("PT_INSTITUTION_NAME")} text={`${property?.institution?.name || t("CS_NA")}`} />}
                  {property?.institution && property?.institution?.type && <Row className="border-none" label={t("PT_INSTITUTION_TYPE")} text={`${t(`COMMON_MASTERS_OWNERSHIPCATEGORY_${property?.institution?.type}`) || t("CS_NA")}`} />}
                    <Row 
                      className="border-none" 
                      label={t("PT_COMMON_APPLICANT_NAME_LABEL")}
                      textStyle={isMobile?{marginLeft:"27%",marginRight:"5%",wordBreak:"break-word"}:{marginLeft:"2%"}}
                      text={`${owner?.name || t("CS_NA")}`}
                      actionButtonStyle={{marginRight:"-10px"}}
                      actionButton={
                        <ActionButton style={{marginRight:"-10px"}} jumpTo={`/digit-ui/citizen/pt/property/owner-history/${property.tenantId}/${property.propertyId}`} />
                      }
                    />
                    <Row className="border-none"  label={t("PT_COMMON_GENDER_LABEL")} text={`${owner?.gender ? owner?.gender.toLowerCase() : t("CS_NA")}`} />
                    {property?.institution && <Row className="border-none" label={t("PT_LANDLINE_NUMBER_FLOATING_LABEL")} text={`${owner?.altContactNumber || t("CS_NA")}`} />}
                    <Row 
                    className="border-none" 
                    label={t("PT_FORM3_MOBILE_NUMBER")}
                    text={`${t(owner?.mobileNumber)}` || t("CS_NA")}
                    textStyle={isMobile?{marginLeft:"16%"}:{marginLeft:"0%"}}
                    actionButton={
                    property?.status === "ACTIVE"&&owner?.mobileNumber&&Digit.UserService.getUser()?.info?.mobileNumber&&owner.mobileNumber===Digit.UserService.getUser()?.info?.mobileNumber&&<div onClick={() => showPopup({ name: owner?.name, mobileNumber: owner?.mobileNumber, ownerIndex: index })}>
                    <EditIcon />
                    </div>
                    }
                    />         
                    {property?.institution && property?.institution?.designation && <Row className="border-none"  label={t("Designation")} text={`${property?.institution?.designation || t("CS_NA")}`} />}
                    <Row className="border-none" label={t("PT_FORM3_GUARDIAN_NAME")} text={`${owner?.fatherOrHusbandName || t("CS_NA")}`} />
                    <Row 
                      className="border-none" 
                      label={t("PT_FORM3_OWNERSHIP_TYPE")}
                      text={`${property?.ownershipCategory ? t(`PT_OWNERSHIP_${property?.ownershipCategory}`) : t("CS_NA")}`}
                    />
                    <Row className="border-none"  label={t("PT_FORM3_RELATIONSHIP")} text={`${owner?.relationship || t("CS_NA")}`} />
                    {specialCategoryDoc && specialCategoryDoc.length>0 && <Row className="border-none" label={t("PT_SPL_CAT_DOC_TYPE")} text={`${t(stringReplaceAll(specialCategoryDoc[index]?.documentType,".","_"))}` || t("NA")} />}
                    {specialCategoryDoc && specialCategoryDoc.length>0 && <Row className="border-none" label={t("PT_SPL_CAT_DOC_ID")} text={`${t(specialCategoryDoc[index]?.id)}` || t("CS_NA")} />}
                    <Row className="border-none" label={t("PT_MUTATION_AUTHORISED_EMAIL")} text={owner?.emailId ? owner?.emailId:`${(t("CS_NA"))}`} />
                    <Row className="border-none" label={t("PT_OWNERSHIP_INFO_CORR_ADDR")} text={`${t(owner?.permanentAddress)}` || t("CS_NA")} />
                    {specialCategoryDoc?.length == 0 && <Row className="border-none"  label={t("PT_SPL_CAT")} text={(owner?.ownerType || t("CS_NA"))} /> }
                  </StatusTable>
                </div>
              ))}
          </div>
          <CardSubHeader>{t("PT_COMMON_DOCS")}</CardSubHeader>
          <div>
            {Array.isArray(docs) ? (
              docs.length > 0 && <PropertyDocument property={property}></PropertyDocument>
            ) : (
              <StatusTable>
                <Row className="border-none" text={t("PT_NO_DOCUMENTS_MSG")} />
              </StatusTable>
            )}
          </div>
          <div>
          {/* {property?.status === "ACTIVE" && !enableAudit && (
            <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>               
            <button className="submit-bar" type="button" onClick={handleClickOnPtPgr} style={{fontFamily:"sans-serif", color:"white","fontSize":"19px"}}>{t("PT_PGR")}</button>
            </div>              
            )} */}
            {property?.status === "ACTIVE" && !enableAudit && (
              <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
                <Link to={{ pathname: `/digit-ui/citizen/pt/property/edit-application/action=UPDATE/${property.propertyId}` }}>
                  <SubmitBar label={t("PT_UPDATE_PROPERTY_BUTTON")} />
                </Link>
              </div>
            )}
            {property?.status === "ACTIVE" && !enableAudit && (
              <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
               
                  {/* <SubmitBar label="Asses Property" onClick={handleClick} /> */}
                  <button className="submit-bar" type="button" onClick={handleClick} style={{fontFamily:"sans-serif", color:"white","fontSize":"19px"}}>{t("PT_SELF_ASSES_PROPERTY")}</button>
               
              </div>
            )}
            {property?.status === "ACTIVE" && !enableAudit && getBillAmount(fetchBillData)==0 && (
              <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
               
                  {/* <SubmitBar label="Asses Property" onClick={handleClick} /> */}
                  <button className="submit-bar" type="button" onClick={onBifurcate} style={{fontFamily:"sans-serif", color:"white","fontSize":"19px"}}>{t("Separation of Ownership")}</button>
               
              </div>
            )}
            {property?.status === "ACTIVE" && !enableAudit && getBillAmount(fetchBillData)==0 && (
              <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
               
                  {/* <SubmitBar label="Asses Property" onClick={handleClick} /> */}
                  <button className="submit-bar" type="button" onClick={onAppeal} style={{fontFamily:"sans-serif", color:"white","fontSize":"19px"}}>{t("Appeal")}</button>
               
              </div>
            )}
          </div>
          {popup && (
            <PopUp className="updatenumber-warper-citizen">
              <UpdatePropertyNumberComponent
                showPopup={showPopup}
                name={popup?.name}
                UpdateNumberConfig={UpdateNumberConfig}
                mobileNumber={popup?.mobileNumber}
                t={t}
                onValidation={(data, showToast) => {
                  let newProp = { ...property };
                  newProp.owners[popup?.ownerIndex].mobileNumber = data.mobileNumber;
                  newProp.creationReason = "UPDATE";
                  newProp.workflow = null;
                  mutation.mutate(
                    {
                      Property: newProp,
                    },
                    {
                      onError: () => {},
                      onSuccess: async (successRes) => {
                        showToast();
                        setTimeout(() => {
                          window.location.reload();
                        }, 3000);
                      },
                    }
                  );
                }}
              ></UpdatePropertyNumberComponent>
            </PopUp>
          )}
           {showModal ? (
            <ActionModal
              t={t}
              action={selectedAction}
              tenantId={tenantId}
              state={state}
              id={property.propertyId}
              applicationDetails={property}
              applicationData={property}
              closeModal={closeModal}
              submitAction={submitAction}
         
              businessService={businessService}
             
              moduleCode={moduleCode}
            />
          ) : null}
        </Card>
      </div>
    </React.Fragment>
  );
};

export default PropertyInformation;
