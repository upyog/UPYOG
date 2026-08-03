import { Loader, Modal, Card, CardHeader, StatusTable, Row } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Route, useLocation, Routes, Navigate } from "react-router-dom";
import { newConfig } from "../../../config/Create/config";
import { convertToProperty } from "../../../utils";

const CreateProperty = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const [showToast, setShowToast] = useState(null);
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PT_CREATE_PROPERTY", {});
  const [ackData, setAckData, clearAckData] = Digit.Hooks.useSessionStorage("PT_CREATE_PROPERTY_RESPONSE", null);
  const [ackError, setAckError, clearAckError] = Digit.Hooks.useSessionStorage("PT_CREATE_PROPERTY_RESPONSE_ERROR", null);
  const tenantId = params?.address?.city?.code || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.pt.usePropertyAPI(tenantId, true);
  let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig");  const [searchData, setSearchData] = useState({});
  const { data: propertyData, isLoading: propertyDataLoading, error, isSuccess, billData } = Digit.Hooks.pt.usePropertySearchWithDue({
    tenantId: searchData?.city,
    filters: searchData?.filters,
    auth: true /*  to enable open search set false  */,
    configs: { enabled: Object.keys(searchData).length > 0, retry: false, retryOnMount: false, staleTime: Infinity },
  });

  const goNext = (skipStep, index, isAddMultiple, key) => {
    let currentPath = pathname.split("/").pop(),
      lastchar = currentPath.charAt(currentPath.length - 1),
      isMultiple = false,
      nextPage;
      
    if (Number(parseInt(currentPath)) || currentPath == "0" || currentPath == "-1") {
      if (currentPath == "-1" || currentPath == "-2") {
        // e.g. pathname ends in /route@/-1 → get route@
        const parts = pathname.split("/");
        parts.pop(); // remove -1 or -2
        currentPath = parts.pop(); // get the route segment like number-of-basements@0
        isMultiple = true;
      } else {
        // e.g. pathname ends in /number-of-basements@0/0 → get number-of-basements@0
        const parts = pathname.split("/");
        parts.pop(); // remove trailing index (0,1,2...)
        currentPath = parts.pop(); // get the route segment
        isMultiple = true;
      }
    } else {
      isMultiple = false;
    }
    if (!isNaN(lastchar)) {
      isMultiple = true;
    }

    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);
    if (typeof nextStep == "object" && nextStep != null && isMultiple != false) {
      if (nextStep[sessionStorage.getItem("ownershipCategory")]) {
        nextStep = `${nextStep[sessionStorage.getItem("ownershipCategory")]}/${index}`;
      } else if (nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]) {
        if (`${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}` === "un-occupied-area") {
          nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}/${index}`;
        } else {
          nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}`;
        }
      } else if (nextStep[sessionStorage.getItem("subusagetypevar")]) {
        nextStep = `${nextStep[sessionStorage.getItem("subusagetypevar")]}/${index}`;
      } else if (nextStep[sessionStorage.getItem("area")]) {
        // nextStep = `${nextStep[sessionStorage.getItem("area")]}/${index}`;

        if (`${nextStep[sessionStorage.getItem("area")]}` !== "map") {
          nextStep = `${nextStep[sessionStorage.getItem("area")]}/${index}`;
        } else {
          nextStep = `${nextStep[sessionStorage.getItem("area")]}`;
        }
      } else if (nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]) {
        nextStep = `${nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]}/${index}`;
      } else {
        const resolvedNext = nextStep[sessionStorage.getItem("noOofBasements")];
        if (resolvedNext) {
          // noOofBasements routes (units) should preserve the index suffix
          nextStep = resolvedNext;
          isMultiple = true;
        } else {
          const fallback = Object.values(nextStep)[0];
          nextStep = fallback || nextStep;
          isMultiple = false;
        }
      }
    }
    if (typeof nextStep == "object" && nextStep != null && isMultiple == false) {
      if (
        nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] &&
        (nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] == "map" ||
          nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")] == "un-occupied-area")
      ) {
        nextStep = `${nextStep[sessionStorage.getItem("IsAnyPartOfThisFloorUnOccupied")]}`;
      } else if (nextStep[sessionStorage.getItem("subusagetypevar")]) {
        nextStep = `${nextStep[sessionStorage.getItem("subusagetypevar")]}`;
      } else if (nextStep[sessionStorage.getItem("area")]) {
        nextStep = `${nextStep[sessionStorage.getItem("area")]}`;
      } else if (nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]) {
        nextStep = `${nextStep[sessionStorage.getItem("IsThisFloorSelfOccupied")]}`;
      } else if (nextStep[sessionStorage.getItem("PropertyType")]) {
        nextStep = `${nextStep[sessionStorage.getItem("PropertyType")]}`;
      } else if (nextStep[sessionStorage.getItem("isResdential")]) {
        nextStep = `${nextStep[sessionStorage.getItem("isResdential")]}`;
      }
    }
    /* if (nextStep === "is-this-floor-self-occupied") {
      isMultiple = false;
    } */
    let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) => navigate(to, state != null ? { replace: true, state } : { replace: true });
    }
    if (isAddMultiple) {
      nextStep = key;
    }
    if (nextStep === null) {
      return redirectWithHistory(`check`);
    }
    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${nextStep}`;
    } else {
      nextPage = isMultiple && nextStep !== "map" && nextStep !== "pincode" ? `${nextStep}/${index}` : `${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

  if(params && Object.keys(params).length>0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      clearAckData();
      clearAckError();
      queryClient.invalidateQueries({ queryKey: ["PT_CREATE_PROPERTY"] });
    }

  const onSubmitProperty = () => {
    if (mutation.isPending) return;
    const dataForCreation = { ...params, tenantId };
    const propertyPayload = convertToProperty(dataForCreation);

    mutation.mutate(propertyPayload, {
      onSuccess: (responseData) => {
        setAckData(responseData);
        setAckError(null);
        navigate(`acknowledgement`);
      },
      onError: (err) => {
        setAckData(null);
        setAckError(err);
        navigate(`acknowledgement`);
      }
    });
  };

  const createProperty = async () => {
    let tempObject = {
      "mobileNumber": params.owners[0].mobileNumber,
      "name": params.owners[0].name,
      "doorNo": params.address.doorNo,
      "locality": params.address.locality.code,
      "isRequestForDuplicatePropertyValidation": true
    }
    setSearchData({ city: params.address.city.code, filters: tempObject });
    //navigate(`acknowledgement`);
  };
  useEffect(() => {
    if (!propertyDataLoading && propertyData?.Properties?.length > 0) {
      setShowToast(true);
    }
    else if (!propertyDataLoading && propertyData?.Properties?.length === 0) {
      setShowToast(false);
      console.log("propertyDatapropertyData", propertyData);
      onSubmitProperty();
    }
  }, [propertyData, propertyDataLoading]);

  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    if (key === "owners") {
      let owners = params.owners || [];
      owners[index] = data;
      setParams({ ...params, ...{ [key]: [...owners] } });
    } else if (key === "units") {
      let units = params.units || [];
      // if(index){units[index] = data;}else{
      units = data;

      setParams({ ...params, units });
    }
    else if(key === "propertyStructureDetails")
    {
     
      let propertyStructureDetail = params.propertyStructureDetails || {};
      // if(index){units[index] = data;}else{
        propertyStructureDetail = data;
let propertyStructureDetails ={"propertyStructureDetails":propertyStructureDetail}
      setParams({ ...params, ...propertyStructureDetails });

    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }
  const Heading = (props) => {
    return <h1 className="heading-m">{props.label}</h1>;
  };

  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
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

  const handleSkip = () => {};
  const handleMultiple = () => {};

  const onSuccess = () => {
    clearParams();
    clearAckData();
    clearAckError();
    queryClient.invalidateQueries({ queryKey: ["PT_CREATE_PROPERTY"] });
  };
  if (isLoading || mutation.isPending) {
    return <Loader />;
  }

  const closeModal =() =>{
    setShowToast(false)
  }
  const setModal=()=>{
    setShowToast(false)   
    onSubmitProperty();
  }
  // commonFields=newConfig;
  /* use newConfig instead of commonFields for local development in case needed */
  commonFields = newConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

config.indexRoute = "info";

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("PTCheckPage");
  const PTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("PTAcknowledgement");
//  console.log("configconfigconfig",config)
  return (
    <div>
      <div>
    <Routes>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key, isMandatory } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={`${routeObj.route}/*`}
            key={index}
            element={
              <Component config={{ texts, inputs, key, isMandatory }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} />
            }
          />
        );
      })}
      <Route path={`check/*`} element={<CheckPage onSubmit={createProperty} value={params} />} />
      <Route path={`acknowledgement/*`} element={<PTAcknowledgement ackData={ackData} isPending={mutation.isPending} error={ackError} onSuccess={onSuccess} />} />
      <Route path="*" element={<Navigate to={`${config.indexRoute}`} replace />} />
    </Routes>
    </div>
    <div>
      { showToast &&   <Modal
      headerBarMain={<Heading label={t("CR_PROPERTY_DUPLICATE")} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={"Cancel"}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={"Proceed"}
      actionSaveOnSubmit={setModal}
      formId="modal-action"
    >  <div className="pt-auto-105">
    <Card>
        <CardHeader>Property Details</CardHeader>
     
            <StatusTable>
                <Row label={t("CR_PROPERTY_NUMBER")} text={propertyData?.Properties?.[0]?.propertyId || "NA"} textStyle={{ whiteSpace: "pre" }} />
                <Row label={t("CR_OWNER_NAME")} text={propertyData?.Properties?.[0]?.owners?.[0].name || "NA"} />
                <Row label={t("CR_MOBILE_NUMBER")} text={propertyData?.Properties?.[0]?.owners?.[0].mobileNumber|| "NA"} />
                <Row label={t("CR_ADDRESS")}    text={( propertyData?.Properties?.[0]?.address?.doorNo +", "+ propertyData?.Properties?.[0]?.address?.locality?.name +", "+ propertyData?.Properties?.[0]?.address?.city ) || "NA"}/>
            </StatusTable>
    </Card>
</div>
      </Modal>}
    </div>
    </div>
  );
};

export default CreateProperty;
