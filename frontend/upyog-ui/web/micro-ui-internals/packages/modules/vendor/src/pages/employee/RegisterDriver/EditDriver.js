import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { FormComposer, Loader, Toast, Header } from "@nudmcdgnpm/digit-ui-react-components";
import { useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import DriverConfig from "../../../config/DriverConfig";

const EditDriver = ({ parentUrl, heading }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  let { id: dsoId } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [canSubmit, setSubmitValve] = useState(false);
  const [defaultValues, setDefaultValues] = useState({});
  const [driverDetails, setDriverDetails] = useState({});
  const queryClient = useQueryClient();

  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("FSM_MUTATION_HAPPENED", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("FSM_ERROR_DATA", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("FSM_MUTATION_SUCCESS_DATA", false);

  const { data: driverData, isLoading: daoDataLoading, isSuccess: isDriverSuccess, error: driverError } = Digit.Hooks.fsm.useDriverDetails(
    tenantId,
    { ids: dsoId },
    { staleTime: Infinity }
  );

  const { isLoading: isLoading, isError: vendorCreateError, data: updateResponse, error: updateError, mutate } = Digit.Hooks.fsm.useDriverUpdate(
    tenantId
  );

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
  }, []);

  useEffect(() => {
    if (driverData && driverData[0]) {
      let driverDetailsObj = driverData[0];
      setDriverDetails(driverDetailsObj?.driverData);
      let values = {
        driverName: driverDetailsObj?.driverData?.name,
        license: driverDetailsObj?.driverData?.licenseNumber,
        selectGender: driverDetailsObj?.driverData?.owner?.gender
          ? { code: driverDetailsObj?.driverData?.owner?.gender, i18nKey: driverDetailsObj?.driverData?.owner?.gender }
          : null,
        dob: driverDetailsObj?.driverData?.owner?.dob && Digit.DateUtils.ConvertTimestampToDate(driverDetailsObj?.driverData?.owner?.dob, "yyyy-MM-dd"),
        emailId: driverDetailsObj?.driverData?.owner?.emailId === "abc@egov.com" ? "" : driverDetailsObj?.driverData?.owner?.emailId,
        additionalDetails: driverDetailsObj?.driverData?.additionalDetails?.serviceType
          ? {
            code: driverDetailsObj?.driverData?.additionalDetails?.serviceType,
            i18nKey: driverDetailsObj?.driverData?.additionalDetails?.serviceType,
            value: driverDetailsObj?.driverData?.additionalDetails?.serviceType,
          }
          : driverDetailsObj?.driverData?.additionalDetails?.description,
      };
      setDefaultValues(values);
    }
  }, [driverData]);

  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const Config = DriverConfig(t, true);

  const onFormValueChange = (setValue, formData) => {
    if (formData?.selectGender || formData?.driverName) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const onSubmit = (data) => {
    const license = data?.license || driverDetails?.licenseNumber;
    const gender = data?.selectGender?.code || data?.selectGender;
    const emailId = data?.emailId;
    const dob = data?.dob ? new Date(`${data.dob}`).getTime() : driverDetails?.owner?.dob;
    const additionalDetails = data?.additionalDetails?.code || data?.additionalDetails || driverDetails?.additionalDetails?.serviceType;
    const formData = {
      driver: {
        ...driverDetails,
        tenantId: driverDetails?.tenantId || tenantId,
        licenseNumber: license,
        additionalDetails: {
          ...driverDetails.additionalDetails,
          serviceType: additionalDetails,
        },
        owner: {
          ...driverDetails.owner,
          tenantId: driverDetails?.owner?.tenantId || stateId,
          name: data?.driverName || driverDetails?.owner?.name || driverDetails?.name,
          fatherOrHusbandName: data?.driverName || driverDetails?.owner?.fatherOrHusbandName || driverDetails?.name,
          relationship: driverDetails.owner?.relationship || "OTHER",
          gender: gender || driverDetails.owner?.gender || "OTHER",
          dob: dob,
          emailId: emailId || "abc@egov.com",
        },
      },
    };
    mutate(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: error });
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({ key: "success", action: "UPDATE_DRIVER" });
        setTimeout(closeToast, 5000);
        queryClient.invalidateQueries("FSM_DRIVER_SEARCH");
        setTimeout(() => {
          closeToast();
          navigate(`/upyog-ui/employee/vendor/registry/driver-details/${dsoId}`);
        }, 3000);
      },
    });
  };
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (daoDataLoading || Object.keys(defaultValues).length === 0) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <div>
        <Header>{t("ES_FSM_REGISTRY_TITLE_EDIT_DRIVER")}</Header>
      </div>
      <div style={!isMobile ? { marginLeft: "-15px" } : {}}>
        <FormComposer
          isDisabled={!canSubmit}
          label={t("ES_COMMON_APPLICATION_SUBMIT")}
          config={Config.filter((i) => !i.hideInEmployee).map((config) => {
            return {
              ...config,
              body: config.body.filter((a) => !a.hideInEmployee),
            };
          })}
          fieldStyle={{ marginRight: 0 }}
          onSubmit={onSubmit}
          defaultValues={defaultValues}
          onFormValueChange={onFormValueChange}
          noBreakLine={true}
        />
        {showToast && (
          <Toast
            error={showToast.key === "error" ? true : false}
            label={t(showToast.key === "success" ? `ES_FSM_REGISTRY_${showToast.action}_SUCCESS` : showToast.action)}
            onClose={closeToast}
          />
        )}
      </div>
    </React.Fragment>
  );
};

export default EditDriver;
