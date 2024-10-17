import { FormComposer, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams, useLocation } from "react-router-dom";
import { newConfig } from "../../../config/Create/config";

// component created for renewapplication
const RenewApplication = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const tenants = Digit.Hooks.ptr.useTenants();
  const configs = newConfig;
  const {pathname} = useLocation();
  const [canSubmit, setSubmitValve] = useState(false);
  const defaultValues = {};
  const history = useHistory();

  // applicationNumber passed from the url
  const { applicationNumber } = useParams();

  // const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("RE_EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("RE_EMPLOYEE_MUTATION_SUCCESS_DATA", {});

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);


  const onFormValueChange = (setValue, formData, formState) => {
    setSubmitValve(!Object.keys(formState.errors).length);
  };

  // Maybe needed to be used later:
  // let appType;
  // pathname && pathname.includes("renew-application") ? appType = "RENEWAPPLICATION" : appType = "NEWAPPLICATION"

  const onPetSubmit = (data) => {

    const formData = [{
      tenantId,
      ...data?.owners[0],
      petDetails: {
        ...data?.pets[0],
        petType: data?.pets[0]?.petType?.value,
        breedType: data?.pets[0]?.breedType?.value,
        petGender: data?.pets[0]?.petGender?.name,
      },

      address: {
        ...data?.address,
        city: data?.address?.city?.name,
        locality: { code: data?.address?.locality?.code, area: data?.address?.locality?.area },

      },
      // more fields added according to requirement of payload
      previousapplicationnumber: applicationNumber || null,
      documents: data?.documents?.documents,
      applicationType: "RENEWAPPLICATION",
      propertyId: data?.propertyDetails?.propertyId,


      workflow: {
        businessService: "ptr",   // required
        action: "APPLY",   //required
        moduleName: "pet-services" //required
      }
    }];
    history.replace("/digit-ui/employee/ptr/petservice/response", { PetRegistrationApplications: formData });
  };

  return ( 
    // component used for rendering screens 
    <FormComposer
      heading={t("ES_TITLE_RENEW_PET_REGISTARTION")}
      isDisabled={canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={configs.map((config) => {

        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      fieldStyle={{ marginRight: 0 }}
      onSubmit={onPetSubmit}
      defaultValues={defaultValues}
      onFormValueChange={onFormValueChange}

    />
  );
};

export default RenewApplication;