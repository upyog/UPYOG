import React, { useEffect, useState } from "react";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { newConfigAmalgamate } from "../../../config/Amalgamate/config";

import { useTranslation } from "react-i18next";
import CheckPage from "./CheckPage";
import { Card, Dropdown, FormComposer, Loader, Modal, ResponseComposer, Toast } from "@upyog/digit-ui-react-components";

const AmalgamationCitizen = (props) => {
  const { t } = useTranslation();
  const match = useRouteMatch();
  const { pathname } = useLocation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PT_MUTATE_PROPERTY", {});
  const history = useHistory();
  const [submit, setSubmit] = useState(false);
  const [formData, setFormData] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [city, setCity] = useState();
  const [propertyId, setPropertyId] = useState();
  const [showToast, setShowToast] = useState(null);
  const [showErrorToast, setShowErrorToast] = useState(null);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const allCities = Digit.Hooks.pt.useTenants()?.sort((a, b) => a?.i18nKey?.localeCompare?.(b?.i18nKey));
  let filters = {};
  const auth =true;
  filters.propertyIds=propertyId;
  const searchArgs = city ? {  filters, auth } : { filters, auth };
  const result = Digit.Hooks.pt.usePropertySearch(searchArgs);
  const template = [{"label":"PT_TOTAL_DUES","key":"total_due","noteStyle":{"fontSize":"24px","fontWeight":"bold"},"notePrefix":"₹ "},{"label":"PT_PROPERTY_PTUID","key":"property_id"},{"label":"PT_OWNERSHIP_INFO_NAME","key":"owner_name"},{"label":"PT_OWNERSHIP_INFO_GENDER","key":"owner_gender"},{"label":"PT_OWNERSHIP_INFO_MOBILE_NO","key":"owner_mobile"},{"label":"PT_OWNERSHIP_INFO_EMAIL_ID","key":"owner_email"},{"label":"PT_PROPERTY_ADDRESS_SUB_HEADER","key":"property_address"},{"label":"PT_DUE_DATE","key":"bil_due__date"}];
  const actionButtonLabel = 'Select'
  const consumerCode = propertyId && result?.data?.Properties?.map((a) => a.propertyId).join(",");

  let fetchBillParams = { consumerCode };


  const paymentDetails = Digit.Hooks.useFetchBillsForBuissnessService(
    { businessService: "PT", ...fetchBillParams, tenantId: city },
    {
      enabled: consumerCode ? true : false,
      retry: false,
    }
  );

  const payment = {};

  paymentDetails?.data?.Bill?.forEach((element) => {
    if (element?.consumerCode) {
      payment[element?.consumerCode] = {
        total_due: element?.totalAmount,
        bil_due__date: new Date(element?.billDetails?.[0]?.expiryDate).toDateString(),
      };
    }
  });

  const arr = propertyId && result?.data?.Properties;

  const searchResults = arr && arr.length>0 && arr?.map((property) => {
    let addr = property?.address || {};

    return {
      property_id: property?.propertyId,
      owner_name: (property?.owners || [])?.map( o => o?.name ). join(","),
      owner_gender: (property?.owners || [])?.map( o => o?.gender ). join(","),
      owner_mobile: (property?.owners || [])?.map( o => o?.mobileNumber ). join(",") || 'NA',
      owner_email: (property?.owners || [])?.map( o => o?.emailId ). join(",")|| 'NA',
      property_address: [addr.doorNo || "" , addr.pattaNo || "", addr.principalRoadName || "", addr.buildingName || "", addr.street || "", addr.locality?.name || "", addr.city || ""]
        .filter((a) => a)
        .join(", "),
      total_due: payment[property?.propertyId]?.total_due || 0,
      bil_due__date: payment[property?.propertyId]?.bil_due__date || t("N/A"),
      total_due: payment[property?.propertyId]?.total_due || 0,
      tenantId: property?.tenantId,
    };
  });
  console.log("searchResults==",searchResults)



  const onShowModal=(val)=>{
    setShowModal(true)
  }
  const closeModal=()=>{
    setShowModal(false)
    setPropertyId('')
  }
  const Heading = (props) => {
    return <h1 className="heading-m">{props.label}</h1>;
  };
  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="red">
      <path d="M0 0h24v24H0V0z" fill="none" />
      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>
  );

  const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };
  const onSetPropertyId=(e)=>{
    setPropertyId(e.target.value);
    closeToast();
  }
  let [propertyDetails, setPropertyDetails]=useState([]);

  const onSubmit = (data) => {
    console.log("onSubmit==",data)
    if(Number(data.total_due) > 0){
      setShowToast(true);
      return;
    } else {
      let pDetails = propertyDetails || [];
      pDetails.push(data);
      setPropertyDetails(pDetails);
      closeModal()
    }
  };
  const closeToast = () => {
    setShowToast(null);
  };
  const closeErrorToast = () => {
    setShowErrorToast(null);
  };
  setTimeout(() => {
    closeErrorToast();
  }, 10000);
  
  const onAmalgamate = () =>{
    console.log("propertyDetails.length==",propertyDetails.length,propertyDetails)
    if(propertyDetails.length>=2) {
      history.push({pathname: "/digit-ui/citizen/pt/property/new-application", state: {propertyDetails: propertyDetails, action: 'Amalgamation'}})
    }else {
      setShowErrorToast(() => ({  error: true, label: `${t("Please select atleast two properties for amalgamation")}` }))
    }
  }
  const removeProperty = (result, index)=> {
    propertyDetails.splice(index, 1);
    // setPropertyDetails(pDetails);
    console.log("propertyDetails--",propertyDetails)
  }
 
  
  return (
    <React.Fragment>
      <div style={{marginTop: '16px', marginBottom: '16px',backgroundColor: '#ffffff9e', padding: '20px', maxWidth: '960px', borderRadius: "6px"}}>
        <span style={{fontSize: '20px', fontWeight: 'bold'}}>Properties for Amalgamtion</span>
        <span style={{float: 'right'}}><button style={{textDecoration: 'underline', color: "#0f4f9e", fontStyle: 'italic'}} onClick={(e)=>onShowModal(true)}>Add Property</button></span>
        <div style={{marginTop: '20px'}}>
          {propertyDetails && propertyDetails.length>0 && 
              propertyDetails.map((result, i)=>
                (
                <Card key={i} className="" style={{boxShadow: '0 0px 2px 2px rgba(0, 0, 0, 0.16)'}}>
                  <div>
                    <svg onClick={()=>setPropertyDetails(propertyDetails.splice(i, 1))} style={{float: 'right', cursor: 'pointer'}} xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white" width="18px" height="18px" className="delete">
                      <path d="M0 0h24v24H0V0z" fill="#d4351c" />
                      <path
                        d="M18.3 5.71c-.39-.39-1.02-.39-1.41 0L12 10.59 7.11 5.7c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41L10.59 12 5.7 16.89c-.39.39-.39 1.02 0 1.41.39.39 1.02.39 1.41 0L12 13.41l4.89 4.89c.39.39 1.02.39 1.41 0 .39-.39.39-1.02 0-1.41L13.41 12l4.89-4.89c.38-.38.38-1.02 0-1.4z"
                      />
                    </svg>
                  </div>
                  {template.map((field, j) => {
                    return (
                      <div className="key-note-pair">
                          <h3>{t(field.label)} 
                          </h3>
                          <div style={{display : "inline-flex"}}>
                          {result?.privacy?.[field.key] && <p style={field.noteStyle}>
                            {field.notePrefix ? field.notePrefix + result[field.key] : result[field.key]}
                            </p>}
                          {!result?.privacy?.[field.key] && <p style={field.noteStyle}>{field.notePrefix ? field.notePrefix + result[field.key] : result[field.key]}</p>}
                          </div>
                          {/* <p className="caption">{caption}</p> */}
                        </div>
                    );
                  })}
                </Card>
              ))}
              <div style={{height: '30px', marginTop: '20px'}}>
                {propertyDetails && propertyDetails.length>0 && (<button style={{float: 'right'}} className="selector-button-primary" type="submit" form="modal-action" onClick={(e)=>onAmalgamate()}>
                  <h2 style={{width: '100%'}}>Amalgamate</h2>
                </button>)}
              </div>
        </div>
        
         {showModal && <Modal
          headerBarMain={<Heading label={t('Add Property')} />}
          headerBarEnd={<CloseBtn onClick={closeModal} />}
          actionCancelOnSubmit={closeModal}
          hideSubmit={true}
          actionSaveOnSubmit={() => {}}
          formId="modal-action"
          isDisabled={false}
          popupStyles={{width: "50%"}}
        >
          <div style={{padding: "0 20px"}}>
            {/* <div className="filter-label">{t("PT_SELECT_CITY")}</div>
            <Dropdown option={allCities} optionKey="i18nKey" value={city} select={setCity} t={t} /> */}
            <div>
              <h2 className="card-label " style={{marginBottom: '8px'}}>Property ID
              <div className="tooltip" style={{paddingLeft: '10px', marginBottom: '-3px'}}>  
              {/* <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20" fill="none"><path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM11 15H9V9H11V15ZM11 7H9V5H11V7Z" fill="#0b0c0c"></path></svg><span className="tooltiptext" style="width: 150px; left: 230%; font-size: 14px;">Property ID format must be  PG-PT-xxxx-xx-xx-xxxxxx</span> */}
              </div>
              </h2>
              <div className="field">
                <div className="field-container">
                  <div className="text-input text-input-width field">
                    <input type="text" name="propertyIds" placeholder="Enter Property Id" className="employee-card-input false focus-visible undefined" autoComplete="off" value={propertyId} onChange={(e) => {
                        onSetPropertyId(e)
                      }}/>
                  </div>
                </div>
              </div>
              <div>
              </div>
            </div>
            <div>
            {showToast && <span style={{color: 'red', fontStyle: 'italic'}}>**You can not select this property, as you have dues**</span>}
            </div>
            <div style={{boxShadow: '0px 0px 3px 3px rgba(0, 0, 0, 0.16)'}}>
            {searchResults && searchResults.length>0 && <ResponseComposer data={searchResults} template={template} actionButtonLabel={actionButtonLabel} onSubmit={onSubmit} /> }
            {(!searchResults || searchResults.length==0) && <div style={{textAlign: 'center', fontWeight: 'bold', background: '#ffebe1', padding: '5px'}}>No Property to select </div> }

            </div>
            {/* <div className="popup-module-action-bar">
              <button className="selector-button-primary" type="submit" form="modal-action" onClick={(e)=>onSearchProperty()}>
                <h2 style={{width: '100%'}}>Search</h2>
              </button>
            </div> */}
          </div>
          {/* <FormComposer config={config?.form} noBoxShadow inline disabled={true} childrenAtTheBottom onSubmit={submit} formId="modal-action" /> */}
        </Modal> }
      </div>
      {showErrorToast && <Toast error={true} label={showErrorToast?.label} onClose={closeErrorToast} />}

    </React.Fragment>
  );
};

export default AmalgamationCitizen;
