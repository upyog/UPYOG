import React from "react";
import { useTranslation } from "react-i18next";
import { TickMark } from "@egovernments/digit-ui-react-components";

let actions = [];

const getAction = (flow) => {
  switch (flow) {
    case "STAKEHOLDER":
      actions = [];
      break;
    case "PT_MUTATE":
      actions = ["PT_OWNERSHIP_INFO_SUB_HEADER", "PT_MUTATION_DETAILS", "CE_DOCUMENT_DETAILS", "PROPERTY_TAX_EXEMPTION", "PT_PROPERTY_PHOTO", "PT_COMMON_SUMMARY"];
      break;
    default:
      actions = ["ES_NEW_APPLICATION_PROPERTY_DETAILS", "PT_OWNERSHIP_INFO_SUB_HEADER", "CE_DOCUMENT_DETAILS", "PROPERTY_TAX_EXEMPTION", "PT_PROPERTY_PHOTO", "PT_COMMON_SUMMARY"];
  }
};
const Timeline = ({ currentStep = 1, flow = "" }) => {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();
  getAction(flow);
  let createPropertyDetails = window.sessionStorage.getItem("Digit.PT_CREATE_PROPERTY")
  createPropertyDetails = JSON.parse(createPropertyDetails)
  let isAmalgamation = false ; 
  let amalgamationDetails = {}
  if(createPropertyDetails && createPropertyDetails?.value && createPropertyDetails?.value?.amalgamationDetails) {
    isAmalgamation = true;
    amalgamationDetails = createPropertyDetails?.value?.amalgamationDetails
  }
  return (
    <div>
      <div className="timeline-container" style={isMobile ? {} : { maxWidth: "960px", minWidth: "640px", marginRight: "auto" }}>
        {actions.map((action, index, arr) => (
          <div className="timeline-checkpoint" key={index}>
            <div className="timeline-content">
              <span className={`circle ${index <= currentStep - 1 && "active"}`}>{index < currentStep - 1 ? <TickMark /> : index + 1}</span>
              <span className="secondary-color">{t(action)}</span>
            </div>
            {index < arr.length - 1 && <span className={`line ${index < currentStep - 1 && "active"}`}></span>}
          </div>
        ))}
      </div>
      <div>
        { isAmalgamation && (
          <div>
            <div style={isMobile ? {} : { maxWidth: "960px", minWidth: "640px", marginRight: "auto", padding: '15px', background: 'rgb(245 249 255)',marginBottom : '5px', borderRadius: "6px", color: "#0f4f9e" }}>
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
          </div>

        )}
      </div>
    </div>
    
  );
};

export default Timeline;
