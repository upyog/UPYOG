// import { FormComposer, Loader } from "@upyog/digit-ui-react-components";
// import React, { useEffect, useState } from "react";
// import { useTranslation } from "react-i18next";
// import { useHistory } from "react-router-dom";
// import { newConfig } from "../../../config/Create/config";
// import { convertDateToEpoch } from "../../../utils";

// const NewApplication = () => {
//   const tenantId = Digit.ULBService.getCurrentTenantId();
//   const tenants = Digit.Hooks.ptr.useTenants();

//   const { t } = useTranslation();
//   const [canSubmit, setSubmitValve] = useState(false);
//   const defaultValues = {};
//   const history = useHistory();

//   const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
//   const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
//   const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });

//   const { data: commonFields } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "PetService", [{ name: "CommonFieldsConfigEmp" }],
//   {
//     select: (data) => {
//         const formattedData = data?.["PetService"]?.["CommonFieldsConfigEmp"]
//         return formattedData;
//     },
// });  

//   useEffect(() => {
//     setMutationHappened(false);
//     clearSuccessData();
//   }, []);

  

//   const onFormValueChange = (setValue, formData, formState) => {
    
//     setSubmitValve(!Object.keys(formState.errors).length); 
//   };

//   const onPetSubmit = (data) => {
//   console.log("data in index of newaplication", data);
//     const formData = [{
//       tenantId,
//       ...data?.owners[0],
//       petDetails: {
//         ...data?.pets[0],
//         petColor: data?.pets[0]?.petDetails?.colourCode,
//         petType: data?.pets[0]?.petType?.value,
//         breedType: data?.pets[0]?.breedType?.value,
//         petGender: data?.pets[0]?.petGender?.name,
//         birthDate: data?.pets?.birthDate ? convertDateToEpoch(pets?.birthDate) : null,
//         adoptionDate: data?.pets?.adoptionDate ? convertDateToEpoch(pets?.adoptionDate) : null,
//       },

//       address: {
//         ...data?.address,
//         city: data?.address?.city?.name,
//         locality: { code: data?.address?.locality?.code, area: data?.address?.locality?.area },
//       },
//       petToken: "",
//       expireflag: false ,
//       previousapplicationnumber: null,
//       documents: data?.documents?.documents,
//       // more fields added according to requirement of payload
//       applicationType: "NEWAPPLICATION",
//       propertyId: data?.propertyDetails?.propertyId,

//       workflow : {
//         businessService: "ptr",   // required
//         action : "APPLY",   //required
//         moduleName: "pet-services" //required
//       }
        
//     }];

//     history.replace("/digit-ui/employee/ptr/petservice/response", { PetRegistrationApplications: formData }); 
    
//   };
  
//   const configs =  newConfig;


  
//   return (
//     <FormComposer
//       heading={t("ES_TITLE_NEW_PET_REGISTARTION")}
//       isDisabled={!canSubmit}
//       label={t("ES_COMMON_APPLICATION_SUBMIT")}
//       config={configs.map((config) => {
      
//         return {
//           ...config,
//           body: config.body.filter((a) => !a.hideInEmployee),
//         };
//       })}
//       fieldStyle={{ marginRight: 0 }}
//       onSubmit={onPetSubmit}
//       defaultValues={defaultValues}
//       onFormValueChange={onFormValueChange}
    
//     />
//   );
// };

// export default NewApplication;



import { Loader } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { citizenConfig } from "../../../config/Create/citizenconfig";


const NewApplication = ({ parentRoute }) => {

  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  const stateId = Digit.ULBService.getStateId();

  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PTR_CREATE_PET", {});

  let { data: commonFields, isLoading } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "PetService", [{ name: "CommonFieldsConfig" }],
    {
      select: (data) => {
        const formattedData = data?.["PetService"]?.["CommonFieldsConfigEmp"]
        return formattedData;
      },
    });

  const applicationId = sessionStorage.getItem("petId") ?sessionStorage.getItem("petId") : null
  sessionStorage.setItem("applicationType",pathname.includes("new-application") ? "NEWAPPLICATION":"RENEWAPPLICATION")
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const { isError, error, data: ApplicationData } = Digit.Hooks.ptr.usePTRSearch(
    {
      tenantId,
      filters: { applicationNumber: applicationId },
    },
  );

  let dataComingfromAPI = ApplicationData?.PetRegistrationApplications[0];


  const goNext = (skipStep, index, isAddMultiple, key) => {
    let currentPath = pathname.split("/").pop(),
      lastchar = currentPath.charAt(currentPath.length - 1),
      isMultiple = false,
      nextPage;
    if (Number(parseInt(currentPath)) || currentPath == "0" || currentPath == "-1") {
      if (currentPath == "-1" || currentPath == "-2") {
        currentPath = pathname.slice(0, -3);
        currentPath = currentPath.split("/").pop();
        isMultiple = true;
      } else {
        currentPath = pathname.slice(0, -2);
        currentPath = currentPath.split("/").pop();
        isMultiple = true;
      }
    } else {
      isMultiple = false;
    }
    if (!isNaN(lastchar)) {
      isMultiple = true;
    }
    // let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === (currentPath || '0'));



    let redirectWithHistory = history.push;
    if (skipStep) {
      redirectWithHistory = history.replace;
    }
    if (isAddMultiple) {
      nextStep = key;
    }
    if (nextStep === null) {
      return redirectWithHistory(`${match.path}/check`);
    }
    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${match.path}/${nextStep}`;
    }
    else {
      nextPage = isMultiple && nextStep !== "map" ? `${match.path}/${nextStep}/${index}` : `${match.path}/${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

 

  if(params && Object.keys(params).length>0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true")
    {
      clearParams();
      queryClient.invalidateQueries("PTR_CREATE_PET");

    }

  const ptrcreate = async () => {
    history.push(`${match.path}/acknowledgement`);
  };

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
    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }

  const handleSkip = () => { };
  const handleMultiple = () => { };

  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("PTR_CREATE_PET");
    sessionStorage.removeItem(["applicationType","petId"]);
  };
  if (isLoading) {
    return <Loader />;
  }


  commonFields = commonFields ? commonFields : citizenConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  config.indexRoute = "info";

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("PTRCheckPage");
  const PTRAcknowledgement = Digit?.ComponentRegistryService?.getComponent("PTRAcknowledgement");




  return (
    <Switch>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} renewApplication={pathname.includes("new-application") ? {} : dataComingfromAPI} />
          </Route>
        );
      })}


      <Route path={`${match.path}/check`}>
        <CheckPage onSubmit={ptrcreate} value={params} />
      </Route>
      <Route path={`${match.path}/acknowledgement`}>
        <PTRAcknowledgement data={params} onSuccess={onSuccess} />
      </Route>
      <Route>
        <Redirect to={`${match.path}/${config.indexRoute}`} />
      </Route>
    </Switch>
  );
};

export default NewApplication;