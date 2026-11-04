import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { FormComposer, Loader, Toast, Header, InfoIcon } from "@nudmcdgnpm/digit-ui-react-components";
import { useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import VehicleConfig from "../../../config/VehicleConfig";

const EditVehicle = ({ parentUrl, heading }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  let { id: dsoId } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [canSubmit, setSubmitValve] = useState(false);
  const [defaultValues, setDefaultValues] = useState({});
  const [vehicleDetails, setVehicleDetails] = useState({});
  const queryClient = useQueryClient();

  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("FSM_MUTATION_HAPPENED", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("FSM_ERROR_DATA", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("FSM_MUTATION_SUCCESS_DATA", false);

  const { data: vehicleData, isLoading: vehicleDataLoading, isSuccess: isVehicleSuccess, error: vehicleError } = Digit.Hooks.fsm.useVehicleDetails(
    tenantId,
    { registrationNumber: dsoId },
    { staleTime: Infinity }
  );

  const { isLoading: isLoading, isError: vendorCreateError, data: updateResponse, error: updateError, mutate } = Digit.Hooks.fsm.useUpdateVehicle(
    tenantId
  );

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
  }, []);

  useEffect(() => {
    if (vehicleData && vehicleData[0]) {
      let vehicleDataObj = vehicleData[0];
      setVehicleDetails(vehicleDataObj?.vehicleData);
      let values = {
        registrationNumber: vehicleDataObj?.vehicleData?.registrationNumber,
        vehicle: {
          type: vehicleDataObj?.vehicleData?.type,
          modal: vehicleDataObj?.vehicleData?.model,
          tankCapacity: vehicleDataObj?.vehicleData?.tankCapacity,
        },
        pollutionCert:
          vehicleDataObj?.vehicleData?.pollutionCertiValidTill &&
          Digit.DateUtils.ConvertTimestampToDate(vehicleDataObj?.vehicleData?.pollutionCertiValidTill, "yyyy-MM-dd"),
        insurance:
          vehicleDataObj?.vehicleData?.InsuranceCertValidTill &&
          Digit.DateUtils.ConvertTimestampToDate(vehicleDataObj?.vehicleData?.InsuranceCertValidTill, "yyyy-MM-dd"),
        roadTax:
          vehicleDataObj?.vehicleData?.roadTaxPaidTill &&
          Digit.DateUtils.ConvertTimestampToDate(vehicleDataObj?.vehicleData?.roadTaxPaidTill, "yyyy-MM-dd"),
        fitnessValidity:
          vehicleDataObj?.vehicleData?.fitnessValidTill &&
          Digit.DateUtils.ConvertTimestampToDate(vehicleDataObj?.vehicleData?.fitnessValidTill, "yyyy-MM-dd"),
        phone: vehicleDataObj?.vehicleData?.owner?.mobileNumber,
        ownerName: vehicleDataObj?.vehicleData?.owner?.name,
        selectGender: vehicleDataObj?.vehicleData?.owner?.gender
          ? { code: vehicleDataObj?.vehicleData?.owner?.gender, i18nKey: vehicleDataObj?.vehicleData?.owner?.gender }
          : null,
        dob: vehicleDataObj?.vehicleData?.owner?.dob && Digit.DateUtils.ConvertTimestampToDate(vehicleDataObj?.vehicleData?.owner?.dob, "yyyy-MM-dd"),
        emailId: vehicleDataObj?.vehicleData?.owner?.emailId === "abc@egov.com" ? "" : vehicleDataObj?.vehicleData?.owner?.emailId,
        additionalDetails: vehicleDataObj?.vehicleData?.additionalDetails?.serviceType
          ? {
            code: vehicleDataObj?.vehicleData?.additionalDetails?.serviceType,
            i18nKey: vehicleDataObj?.vehicleData?.additionalDetails?.serviceType,
            value: vehicleDataObj?.vehicleData?.additionalDetails?.serviceType,
          }
          : vehicleDataObj?.vehicleData?.additionalDetails?.description,
      };
      setDefaultValues(values);
    }
  }, [vehicleData]);

  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const Config = VehicleConfig(t, true);

  Config[0].body.forEach((item) => {
    if (item.label === "ES_FSM_REGISTRY_VEHICLE_NUMBER") {
      item.labelChildren = (
        <div className="tooltip" style={{ paddingLeft: "10px", marginBottom: "-3px" }}>
          <InfoIcon />
          <span className="tooltiptext" style={{ width: "150px", left: "230%", fontSize: "14px" }}>
            {t(item?.populators?.validation?.title)}
          </span>
        </div>
      );
    }
  });

  const onFormValueChange = (setValue, formData) => {
    if (formData?.registrationNumber && formData?.ownerName && formData?.phone && formData?.vehicle?.modal && formData?.vehicle?.type) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const onSubmit = (data) => {
    const vehicleType = data?.vehicle?.type?.code || data?.vehicle?.type || vehicleDetails?.type;
    const vehicleModal = data?.vehicle?.modal?.code || data?.vehicle?.modal || vehicleDetails?.model;
    const tankCapacity = data?.vehicle?.type?.capacity || data?.vehicle?.tankCapacity || vehicleDetails?.tankCapacity;
    const pollutionCert = data?.pollutionCert > 0 || data?.pollutionCert?.length > 0 ? new Date(`${data?.pollutionCert}`).getTime() : vehicleDetails?.pollutionCertiValidTill;
    const insurance = data?.insurance > 0 || data?.insurance?.length > 0 ? new Date(`${data?.insurance}`).getTime() : vehicleDetails?.InsuranceCertValidTill;
    const roadTax = data?.roadTax > 0 || data?.roadTax?.length > 0 ? new Date(`${data?.roadTax}`).getTime() : vehicleDetails?.roadTaxPaidTill;
    const fitnessValidity = data?.fitnessValidity > 0 || data?.fitnessValidity?.length > 0 ? new Date(`${data?.fitnessValidity}`).getTime() : vehicleDetails?.fitnessValidTill;
    const additionalDetails = data?.additionalDetails?.code || data?.additionalDetails || vehicleDetails?.additionalDetails?.serviceType;
    const phone = data?.phone || vehicleDetails?.owner?.mobileNumber;
    const vehicleOwnerName = data?.ownerName || vehicleDetails?.owner?.name;
    const gender = data?.selectGender?.code || data?.selectGender;
    const emailId = data?.emailId;
    const dob = data?.dob ? new Date(`${data.dob}`).getTime() : vehicleDetails?.owner?.dob;

    const formData = {
      vehicle: {
        ...vehicleDetails,
        tenantId: vehicleDetails?.tenantId || tenantId,
        model: vehicleModal,
        type: vehicleType,
        tankCapacity: tankCapacity,
        pollutionCertiValidTill: pollutionCert,
        InsuranceCertValidTill: insurance,
        fitnessValidTill: fitnessValidity,
        roadTaxPaidTill: roadTax,
        additionalDetails: {
          ...vehicleDetails.additionalDetails,
          serviceType: additionalDetails,
        },
        owner: {
          ...vehicleDetails.owner,
          tenantId: vehicleDetails?.owner?.tenantId || stateId,
          fatherOrHusbandName: vehicleOwnerName,
          relationship: vehicleDetails?.owner?.relationship || "OTHER",
          gender: gender || vehicleDetails.owner?.gender || "OTHER",
          dob: dob,
          emailId: emailId || "abc@egov.com",
          name: vehicleOwnerName,
          mobileNumber: phone,
        },
      },
    };
    mutate(formData, {
      onError: (error, variables) => {
        setShowToast({ key: "error", action: error });
        setTimeout(closeToast, 5000);
      },
      onSuccess: (data, variables) => {
        setShowToast({ key: "success", action: "UPDATE_VEHICLE" });
        setTimeout(closeToast, 5000);
        queryClient.invalidateQueries("FSM_VEHICLE_DETAILS");
        queryClient.invalidateQueries("FSM_VEICLES_SEARCH");
        queryClient.invalidateQueries("DSO_SEARCH");
        setTimeout(() => {
          closeToast();
          navigate(`/upyog-ui/employee/vendor/registry/vehicle-details/${dsoId}`);
        }, 2000);
      },
    });
  };
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (vehicleDataLoading || Object.keys(defaultValues).length === 0) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <div>
        <Header>{t("ES_FSM_REGISTRY_TITLE_EDIT_VEHICLE")}</Header>
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

export default EditVehicle;
