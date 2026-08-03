import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { FormComposer, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { updateNDCForm } from "../../../redux/actions/NDCFormActions";
import { useState } from "react";
import { Loader } from "../../../components/Loader";
import _ from "lodash";

export const NewNDCStepFormOne = ({ config, onGoNext, onBackClick, t }) => {
  const dispatch = useDispatch();
  const [showToast, setShowToast] = useState(false);
  const [error, setError] = useState("");
  const user = Digit.UserService.getUser();
  const id = window.location.pathname.split("/").pop();
  const currentStepData = useSelector((state) =>
    state.ndc.NDCForm.formData && state.ndc.NDCForm.formData[config.key] ? state.ndc.NDCForm.formData[config.key] : {}
  );
  const checkApiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.apiData);

  const checkNDCData = useSelector((state) => state.ndc.NDCForm.formData);


  const [getLoader, setLoader] = useState(false);
  const checkFormData = useSelector((state) => state.ndc.NDCForm.formData || {});

  const checkFormDatass = useSelector((state) => state.ndc.NDCForm || {});

  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  function goNext(data) {
    const missingFields = validateStepData(currentStepData);

    if (missingFields.length > 0) {
      setError(`${missingFields[0]}`);
      setShowToast(true);
      setTimeout(() => {
        setShowToast(false);
      }, 3000);
      return;
    }


    const isRealId = id && id.startsWith("NDC-");


    if (checkFormData?.apiData?.Applications?.[0]?.applicationNo || checkFormData?.responseData?.[0]?.applicationNo || isRealId) {
      // onGoNext();
      updateApplication(data);
      console.log("here bab");
    } else createApplication(data);

    // onGoNext();
  }

  const createApplication = async (data) => {
    // setLoader(true);
    const applicant = Digit.UserService.getUser()?.info || {};
    const applicantId = applicant?.uuid;

    const owners = (data?.cpt?.details?.owners || [])?.map((owner) => {
      const newOwner = JSON.parse(JSON.stringify(owner));

      delete newOwner.status;

      if (newOwner?.name?.trim()?.toLowerCase() === data?.PropertyDetails?.firstName?.trim()?.toLowerCase()) {
        newOwner.emailId = data?.PropertyDetails?.email;
        newOwner.isPrimaryOwner = true;
      }

      return newOwner;
    });


    // Prepare NdcDetails
    const ndcDetails = [];

    // Add each water connection to NdcDetails
    (data?.PropertyDetails?.waterConnection || [])?.forEach((wc) => {
      ndcDetails.push({
        uuid: wc?.billData?.id,
        applicantId: applicantId,
        businessService: "WS",
        consumerCode: wc?.connectionNo,
        additionalDetails: {
          propertyAddress: data?.PropertyDetails?.address,
          propertyType: data?.cpt?.details?.usageCategory,
          // connectionType: wc?.billData,
          // meterNumber: "NOT_AVAILABLE"
        },
        dueAmount: wc?.billData?.totalAmount || 0,
        status: wc?.billData?.status,
      });
    });

    // Add each sewerage connection to NdcDetails
    (data?.PropertyDetails?.sewerageConnection || [])?.forEach((sc) => {
      ndcDetails.push({
        uuid: sc?.billData?.id,
        applicantId: applicantId,
        businessService: "SW",
        consumerCode: sc?.connectionNo,
        additionalDetails: {
          propertyAddress: data?.PropertyDetails?.address,
          propertyType: data?.cpt?.details?.usageCategory,
        },
        dueAmount: sc?.billData?.totalAmount || 0,
        status: sc?.billData?.status,
      });
    });

    if (data?.PropertyDetails?.propertyBillData?.billData) {
      const billData = data?.PropertyDetails?.propertyBillData?.billData;
      ndcDetails.push({
        uuid: billData?.id,
        applicantId: applicantId,
        businessService: "PT",
        consumerCode: data?.cpt?.id,
        additionalDetails: {
          propertyAddress: data?.PropertyDetails?.address,
          propertyType: data?.cpt?.details?.usageCategory,
          reason: data?.NDCReason?.reason,
          remarks: data?.PropertyDetails?.remarks,
        },
        dueAmount: billData?.totalAmount || 0,
        status: billData?.status,
      });
    }

    // Final payload
    const payload = {
      Applications: [
        {
          tenantId,
          owners,
          NdcDetails: ndcDetails,
          Documents: [],
          active: true,
          reason: data?.NDCReason?.code,
          workflow: {
            action: "INITIATE",
          },
        },
      ],
    };

    try {
      const response = await Digit.NDCService.NDCcreate({ tenantId, details: payload });
      setLoader(false);
      if (response?.ResponseInfo?.status === "successful") {
        dispatch(updateNDCForm("apiData", response));
        onGoNext();
        return { isSuccess: true, response };
      } else {
        return { isSuccess: false, response };
      }
    } catch (error) {
      setLoader(false);
    }
  };

  const updateApplication = async (data) => {
    const applicant = Digit.UserService.getUser()?.info || {};
    const auditDetails = data?.cpt?.details?.auditDetails;
    const applicantId = applicant?.uuid;

    // Build owners array
    const owners = checkApiDataCheck?.Applications?.[0]?.owners || checkFormData?.responseData?.[0]?.owners;

    // Prepare NdcDetails
    let ndcDetails = [];

    if (checkFormData?.responseData?.[0]?.NdcDetails?.length > 0) {
      ndcDetails = checkFormData.responseData[0].NdcDetails;
    } else {
      // Add each water connection to NdcDetails
      (data?.PropertyDetails?.waterConnection || [])?.forEach((wc) => {
        ndcDetails.push({
          uuid: wc?.billData?.id,
          applicantId: applicantId,
          businessService: "WS",
          consumerCode: wc?.connectionNo,
          additionalDetails: {
            propertyAddress: data?.PropertyDetails?.address,
            propertyType: data?.cpt?.details?.usageCategory,
            // connectionType: wc?.billData,
            // meterNumber: "NOT_AVAILABLE"
          },
          dueAmount: wc?.billData?.totalAmount || 0,
          status: wc?.billData?.status,
        });
      });

      // Add each sewerage connection to NdcDetails
      (data?.PropertyDetails?.sewerageConnection || [])?.forEach((sc) => {
        ndcDetails.push({
          uuid: sc?.billData?.id,
          applicantId: applicantId,
          businessService: "SW",
          consumerCode: sc?.connectionNo,
          additionalDetails: {
            propertyAddress: data?.PropertyDetails?.address,
            propertyType: data?.cpt?.details?.usageCategory,
          },
          dueAmount: sc?.billData?.totalAmount || 0,
          status: sc?.billData?.status,
        });
      });

      if (data?.PropertyDetails?.propertyBillData?.billData) {
        const billData = data?.PropertyDetails?.propertyBillData?.billData;
        ndcDetails.push({
          uuid: billData?.id,
          applicantId: applicantId,
          businessService: "PT",
          consumerCode: data?.cpt?.id,
          additionalDetails: {
            propertyAddress: data?.PropertyDetails?.address,
            propertyType: data?.cpt?.details?.usageCategory,
          },
          dueAmount: billData?.totalAmount || 0,
          status: billData?.status,
        });
      }
    }
    const appNumber = checkApiDataCheck?.Applications?.[0]?.applicationNo || checkFormData?.responseData?.[0]?.applicationNo;
    const apUUid = checkApiDataCheck?.Applications?.[0]?.uuid || checkFormData?.responseData?.[0]?.uuid;

    // Final payload
    const payload = {
      Applications: [
        {
          tenantId,
          owners,
          NdcDetails: ndcDetails,
          Documents: [],
          active: true,
          reason: data?.NDCReason?.code,
          auditDetails: data?.cpt?.details?.auditDetails,
          applicationNo: appNumber,
          uuid: apUUid,
          workflow: {
            action: "DRAFT",
          },
        },
      ],
    };


    const response = await Digit.NDCService.NDCUpdate({ tenantId, details: payload });

    if (response?.ResponseInfo?.status === "successful") {
      dispatch(updateNDCForm("apiData", response));
      onGoNext();
      return { isSuccess: true, response };
    } else {
      return { isSuccess: false, response };
    }
  };

  function validateStepData(data) {
    const missingFields = [];
    const invalidFields = [];

    const cpt = data?.cpt || {};
    const cptDetails = cpt?.details || {};
    const propertyDetails = data?.PropertyDetails || {};
    const NDCReason = data?.NDCReason || {};

    if (!data?.cpt?.dues) {
      missingFields.push(`${t("NDC_MESSAGE_PLEASE_CHECK_STATUS_OF_PROPERTY_TAX")} ${cpt?.id}`);
    }
    if (data?.cpt?.dues?.totalAmount > 0) {
      missingFields.push(`${t("NDC_MESSAGE_PLEASE_PAY_DUES_OF_PROPERTY_TAX")} ${cpt?.id}`);
    }

    if (propertyDetails?.waterConnection?.length > 0) {
      propertyDetails.waterConnection?.forEach((value) => {
        if (value?.billData?.totalAmount != 0) {
          missingFields.push(`${t("NDC_MESSAGE_PLEASE_CHECK_STATUS_OF_WATER_CONNECTION")} ${value?.connectionNo}`);
        }
        if (value?.billData?.id && value?.billData?.totalAmount > 0) {
          missingFields.push(`${t("NDC_MESSAGE_PLEASE_PAY_DUES_OF_WATER_CONNECTION")} ${value?.connectionNo}`);
        }
      });
    }

    if (propertyDetails?.sewerageConnection?.length > 0) {
      propertyDetails.sewerageConnection?.forEach((value) => {
        if (value?.billData?.totalAmount != 0) {
          missingFields.push(`${t("NDC_MESSAGE_PLEASE_CHECK_STATUS_OF_SEWERAGE_CONNECTION")} ${value?.connectionNo}`);
        }
        if (value?.billData?.id && value?.billData?.totalAmount > 0) {
          missingFields.push(`${t("NDC_MESSAGE_PLEASE_PAY_DUES_OF_SEWERAGE_CONNECTION")} ${value?.connectionNo}`);
        }
      });
    }

    // Mandatory Field Checks
    if (!cpt?.id) missingFields.push(t("NDC_MESSAGE_PROPERTY_ID"));
    if (!cptDetails || Object.keys(cptDetails).length === 0) missingFields.push(t("NDC_MESSAGE_PLEASE_SEARCH_PROPERTY_ID"));
    if (!NDCReason?.code) missingFields.push(t("NDC_MESSAGE_NDC_REASON"));

    // Format Validations
    const nameRegex = /^[A-Za-z\s]+(,\s*[A-Za-z\s]+)*$/;
    // const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const emailRegex = /^(?!\.)(?!.*\.\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(\.[a-zA-Z]{2,})+$/;

    const mobileRegex = /^[6-9]\d{9}$/;

    if (propertyDetails?.firstName && !nameRegex.test(propertyDetails.firstName)) {
      invalidFields.push(t("NDC_MESSAGE_FIRST_NAME_ONLY_ALPHABETS_ALLOWED"));
    }

    if (propertyDetails?.email && !emailRegex.test(propertyDetails.email)) {
      invalidFields.push(t("NDC_MESSAGE_EMAIL_INVALID_FORMAT"));
    }

    if (propertyDetails?.mobileNumber && !mobileRegex.test(propertyDetails.mobileNumber)) {
      invalidFields.push(t("NDC_MESSAGE_MOBILE_NUMBER_MUST_BE_A_VALID_TEN_DIGIT_INDIAN_NUMBER"));
    }

    const allErrors = [...missingFields, ...invalidFields];
    return allErrors;
  }

  function onGoBack(data) {
    onBackClick(config.key, data);
  }

  const onFormValueChange = (setValue = true, data) => {
    if (!_.isEqual(data, currentStepData)) {
      dispatch(updateNDCForm(config.key, data));
    }
  };

  const closeToast = () => {
    setShowToast(false);
    setError("");
  };

  return (
    <React.Fragment>
      <FormComposer
        defaultValues={currentStepData}
        config={config.currStepConfig}
        onSubmit={goNext}
        onFormValueChange={onFormValueChange}
        label={t(`${config.texts.submitBarLabel}`)}
        currentStep={config.currStepNumber}
        onBackClick={onGoBack}
      />
      {getLoader && <Loader page={true} />}
      {showToast && <Toast isDleteBtn={true} error={true} label={error} onClose={closeToast} />}
    </React.Fragment>
  );
};
