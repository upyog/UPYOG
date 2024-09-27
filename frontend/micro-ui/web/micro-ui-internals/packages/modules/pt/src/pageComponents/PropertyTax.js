import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { cardBodyStyle, stringReplaceAll } from "../utils";
//import { map } from "lodash-es";

const PropertyTax = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");

  const docType = config?.isMutation ? ["MutationDocuments"] : "Documents";

  const { isLoading, data: Documentsob = {} } = Digit.Hooks.pt.usePropertyMDMS(stateId, "PropertyTax", docType);

  let docs = Documentsob?.PropertyTax?.[config?.isMutation ? docType[0] : docType];
  if (!config?.isMutation) docs = docs?.filter((doc) => doc["digit-citizen"]);
  function onSave() {}

  function goNext() {
    console.log("config========next===",config)
    if(config && config?.amalgamationState && config?.amalgamationState?.action == "Amalgamation") {
      onSelect('amalgamationDetails', config?.amalgamationState);
    }else if(config && config?.amalgamationState && config?.amalgamationState?.action == "BIFURCATION") {
      onSelect('bifurcationDetails', config?.amalgamationState);
    } else {
      onSelect();
    }
    
  }
  let isAmalgamation = false;
  let amalgamationDetails = {};
  if(config && config?.amalgamationState && config?.amalgamationState?.action=='Amalgamation' && config?.amalgamationState?.propertyDetails) {
    isAmalgamation = true;
    amalgamationDetails =  config?.amalgamationState
  }
  let isBifurcation = false;
  let bifurcationDetails = {};
  if(config && config?.amalgamationState && config?.amalgamationState?.action=="BIFURCATION" && config?.amalgamationState?.propertyDetails) {
    isBifurcation = true;
    bifurcationDetails =  config?.amalgamationState
  }
  const isMobile = window.Digit.Utils.browser.isMobile();

  return (
    <React.Fragment>
      <div>
        {isAmalgamation &&
          <div style={isMobile ? {} : { maxWidth: "960px", minWidth: "640px", marginRight: "auto", padding: '15px', background: '#cde2e4',marginBottom : '5px', borderRadius: "6px", color: "#0f4f9e" }}>
          <div style={{fontWeight: 'bold', fontSize: '18px'}}>Amalgamation Property Details</div>
          {amalgamationDetails && amalgamationDetails?.propertyDetails && amalgamationDetails?.propertyDetails.length>0 && 
          amalgamationDetails.propertyDetails.map((e)=> (
          <div>
            <span style={{fontWeight: 'bold'}}>Property ID: </span><span>{e.property_id} | </span>
            <span style={{fontWeight: 'bold'}}>Owner Name: </span><span>{e.owner_name} | </span>
            <span style={{fontWeight: 'bold'}}>Owner Mobile No.: </span><span>{e.owner_mobile}</span>
          </div>
          ))}
        </div>
        }
        {isBifurcation &&
          <div style={isMobile ? {} : { maxWidth: "960px", minWidth: "640px", marginRight: "auto", padding: '15px', background: '#cde2e4',marginBottom : '5px', borderRadius: "6px", color: "#0f4f9e" }}>
          <div style={{fontWeight: 'bold', fontSize: '18px'}}>Separation of Ownership Property Details</div>
          {bifurcationDetails && bifurcationDetails?.propertyDetails && 
          (
          <div>
            <span style={{fontWeight: 'bold'}}>Property ID: </span><span>{bifurcationDetails?.propertyDetails?.propertyId} | </span>
            <span style={{fontWeight: 'bold'}}>Owner Name: </span><span>{bifurcationDetails?.propertyDetails?.owners[0]?.name} | </span>
            <span style={{fontWeight: 'bold'}}>Owner Mobile No.: </span><span>{bifurcationDetails?.propertyDetails?.owners[0]?.mobileNumber}</span>
          </div>
          )}
        </div>
        }
      </div>
      <Card>
        <CardHeader>{config.isMutation ? t("PT_REQIURED_DOC_TRANSFER_OWNERSHIP") : isAmalgamation ? 'Property Amalgamation' : t("PT_DOC_REQ_SCREEN_HEADER")}</CardHeader>
        <div>
          <CardText className={"primaryColor"}>{t("PT_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("PT_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("PT_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
          <CardSubHeader>{t("PT_DOC_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className={"primaryColor"}>{t("PT_DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
          <div>
            {isLoading && <Loader />}
            {Array.isArray(docs)
              ? config?.isMutation ?
                  docs.map(({ code, dropdownData }, index) => (
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t(code)}
                      </CardSubHeader>
                      <CardText className={"primaryColor"}>
                        {dropdownData.map((dropdownData) => (
                          t(dropdownData?.code)
                        )).join(', ')}
                      </CardText>
                      {/* <CardText>{t(`${code.split('.')[0]}.${code.split('.')[1]}.${code.split('.')[1]}_DESCRIPTION`)}</CardText> */}
                    </div>
                  )) :
                  docs.map(({ code, dropdownData }, index) => (
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t("PROPERTYTAX_" + stringReplaceAll(code, ".", "_") + "_HEADING")}
                      </CardSubHeader>
                      {dropdownData.map((dropdownData) => (
                        <CardText className={"primaryColor"}>{t("PROPERTYTAX_" + stringReplaceAll(dropdownData?.code, ".", "_") + "_LABEL")}</CardText>
                      ))}
                    </div>
                  ))
              : null}
          </div>
        </div>
        <span>
          <SubmitBar label={t("PT_COMMON_NEXT")} onSubmit={goNext} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default PropertyTax;
