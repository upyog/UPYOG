import "../../../../css/ndc.css";
import React from "react";
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
  const [getLoader, setLoader] = useState(false);

  const id = window.location.pathname.split("/").pop();

  /*
   * =========================================================
   * CURRENT STEP DATA
   * =========================================================
   */

  const currentStepData = useSelector((state) =>
    state.ndc.NDCForm.formData && state.ndc.NDCForm.formData[config.key] ? state.ndc.NDCForm.formData[config.key] : {},
  );

  /*
   * =========================================================
   * API DATA
   * =========================================================
   */

  const checkApiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.apiData);

  const checkFormData = useSelector((state) => state.ndc.NDCForm.formData || {});

  /*
   * =========================================================
   * TENANT
   * =========================================================
   */

  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  /*
   * =========================================================
   * GO NEXT
   * =========================================================
   *
   * IMPORTANT:
   * Validate the latest data received from FormComposer.
   *
   * We merge it with currentStepData because some components
   * may update only part of the step data.
   */

  function goNext(data) {
    const stepData = {
      ...currentStepData,
      ...data,
    };

    const missingFields = validateStepData(stepData);

    if (missingFields.length > 0) {
      setError(missingFields[0]);
      setShowToast(true);

      setTimeout(() => {
        setShowToast(false);
      }, 3000);

      return;
    }

    const isRealId = id && id.startsWith("NDC-");

    if (checkFormData?.apiData?.Applications?.[0]?.applicationNo || checkFormData?.responseData?.[0]?.applicationNo || isRealId) {
      updateApplication(stepData);
    } else {
      createApplication(stepData);
    }
  }

  /*
   * =========================================================
   * CREATE APPLICATION
   * =========================================================
   */

  const createApplication = async (data) => {
    const applicant = Digit.UserService.getUser()?.info || {};

    const applicantId = applicant?.uuid;

    /*
     * =======================================================
     * OWNERS
     * =======================================================
     */

    const owners = (data?.cpt?.details?.owners || []).map((owner) => {
      const newOwner = JSON.parse(JSON.stringify(owner));

      delete newOwner.status;

      if (newOwner?.name?.trim()?.toLowerCase() === data?.PropertyDetails?.firstName?.trim()?.toLowerCase()) {
        newOwner.emailId = data?.PropertyDetails?.email;

        newOwner.isPrimaryOwner = true;
      }

      return newOwner;
    });

    /*
     * =======================================================
     * NDC DETAILS
     * =======================================================
     */

    const ndcDetails = [];

    /*
     * =======================================================
     * WATER CONNECTION
     * =======================================================
     */

    (data?.PropertyDetails?.waterConnection || []).forEach((wc) => {
      ndcDetails.push({
        uuid: wc?.billData?.id,

        applicantId: applicantId,

        businessService: "WS",

        consumerCode: wc?.connectionNo,

        additionalDetails: {
          propertyAddress: data?.PropertyDetails?.address,

          propertyType: data?.cpt?.details?.usageCategory,
        },

        dueAmount: wc?.billData?.totalAmount || 0,

        status: wc?.billData?.status,
      });
    });

    /*
     * =======================================================
     * SEWERAGE CONNECTION
     * =======================================================
     */

    (data?.PropertyDetails?.sewerageConnection || []).forEach((sc) => {
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

    /*
     * =======================================================
     * PROPERTY TAX
     * =======================================================
     */

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

    /*
     * =======================================================
     * FINAL CREATE PAYLOAD
     * =======================================================
     */

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
      const response = await Digit.NDCService.NDCcreate({
        tenantId,
        details: payload,
      });

      setLoader(false);

      if (response?.ResponseInfo?.status === "successful") {
        dispatch(updateNDCForm("apiData", response));

        onGoNext();

        return {
          isSuccess: true,
          response,
        };
      }

      return {
        isSuccess: false,
        response,
      };
    } catch (error) {
      setLoader(false);

      console.error("NDC create application error:", error);

      return {
        isSuccess: false,
        error,
      };
    }
  };

  /*
   * =========================================================
   * UPDATE APPLICATION
   * =========================================================
   */

  const updateApplication = async (data) => {
    const applicant = Digit.UserService.getUser()?.info || {};

    const applicantId = applicant?.uuid;

    /*
     * =======================================================
     * OWNERS
     * =======================================================
     */

    const owners = checkApiDataCheck?.Applications?.[0]?.owners || checkFormData?.responseData?.[0]?.owners;

    /*
     * =======================================================
     * NDC DETAILS
     * =======================================================
     */

    let ndcDetails = [];

    /*
     * If NDC details already exist in API response,
     * preserve them.
     */

    if (checkFormData?.responseData?.[0]?.NdcDetails?.length > 0) {
      ndcDetails = checkFormData.responseData[0].NdcDetails;
    } else {
      /*
       * =====================================================
       * WATER CONNECTION
       * =====================================================
       */

      (data?.PropertyDetails?.waterConnection || []).forEach((wc) => {
        ndcDetails.push({
          uuid: wc?.billData?.id,

          applicantId: applicantId,

          businessService: "WS",

          consumerCode: wc?.connectionNo,

          additionalDetails: {
            propertyAddress: data?.PropertyDetails?.address,

            propertyType: data?.cpt?.details?.usageCategory,
          },

          dueAmount: wc?.billData?.totalAmount || 0,

          status: wc?.billData?.status,
        });
      });

      /*
       * =====================================================
       * SEWERAGE CONNECTION
       * =====================================================
       */

      (data?.PropertyDetails?.sewerageConnection || []).forEach((sc) => {
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

      /*
       * =====================================================
       * PROPERTY TAX
       * =====================================================
       */

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
    }

    /*
     * =======================================================
     * APPLICATION NUMBER / UUID
     * =======================================================
     */

    const appNumber = checkApiDataCheck?.Applications?.[0]?.applicationNo || checkFormData?.responseData?.[0]?.applicationNo;

    const apUUid = checkApiDataCheck?.Applications?.[0]?.uuid || checkFormData?.responseData?.[0]?.uuid;

    /*
     * =======================================================
     * FINAL UPDATE PAYLOAD
     * =======================================================
     */

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

    try {
      const response = await Digit.NDCService.NDCUpdate({
        tenantId,
        details: payload,
      });

      const responseStatus = String(response?.ResponseInfo?.status || "").toLowerCase();

      if (responseStatus === "successful") {
        dispatch(updateNDCForm("apiData", response));

        onGoNext();

        return {
          isSuccess: true,
          response,
        };
      }

      return {
        isSuccess: false,
        response,
      };
    } catch (error) {
      console.error("NDC update application error:", error);

      return {
        isSuccess: false,
        error,
      };
    }
  };

  /*
   * =========================================================
   * VALIDATE STEP DATA
   * =========================================================
   *
   * REQUIRED:
   *
   * 1. Reason for Applying
   * 2. Property ID
   * 3. Property Search / Property Details
   * 4. Full Name
   * 5. Email
   * 6. Mobile Number
   * 7. Address
   *
   * OPTIONAL:
   *
   * 1. Remarks
   * 2. Trade License Number
   */

  function validateStepData(data) {
    const missingFields = [];
    const invalidFields = [];

    const cpt = data?.cpt || {};

    const cptDetails = cpt?.details || {};

    const propertyDetails = data?.PropertyDetails || {};

    const NDCReason = data?.NDCReason || {};

    /*
     * =======================================================
     * PROPERTY TAX STATUS
     * =======================================================
     */

    if (!data?.cpt?.dues) {
      missingFields.push(`${t("NDC_MESSAGE_PLEASE_CHECK_STATUS_OF_PROPERTY_TAX")} ${cpt?.id || ""}`);
    }

    if (data?.cpt?.dues?.totalAmount > 0) {
      missingFields.push(`${t("NDC_MESSAGE_PLEASE_PAY_DUES_OF_PROPERTY_TAX")} ${cpt?.id || ""}`);
    }

    /*
     * =======================================================
     * WATER CONNECTION STATUS
     * =======================================================
     */

    if (propertyDetails?.waterConnection?.length > 0) {
      propertyDetails.waterConnection.forEach((value) => {
        if (value?.billData?.totalAmount != 0) {
          missingFields.push(`${t("NDC_MESSAGE_PLEASE_CHECK_STATUS_OF_WATER_CONNECTION")} ${value?.connectionNo || ""}`);
        }

        if (value?.billData?.id && value?.billData?.totalAmount > 0) {
          missingFields.push(`${t("NDC_MESSAGE_PLEASE_PAY_DUES_OF_WATER_CONNECTION")} ${value?.connectionNo || ""}`);
        }
      });
    }

    /*
     * =======================================================
     * SEWERAGE CONNECTION STATUS
     * =======================================================
     */

    if (propertyDetails?.sewerageConnection?.length > 0) {
      propertyDetails.sewerageConnection.forEach((value) => {
        if (value?.billData?.totalAmount != 0) {
          missingFields.push(`${t("NDC_MESSAGE_PLEASE_CHECK_STATUS_OF_SEWERAGE_CONNECTION")} ${value?.connectionNo || ""}`);
        }

        if (value?.billData?.id && value?.billData?.totalAmount > 0) {
          missingFields.push(`${t("NDC_MESSAGE_PLEASE_PAY_DUES_OF_SEWERAGE_CONNECTION")} ${value?.connectionNo || ""}`);
        }
      });
    }

    /*
     * =======================================================
     * REASON FOR APPLYING - REQUIRED
     * =======================================================
     */

    if (!NDCReason?.code) {
      missingFields.push(t("NDC_MESSAGE_NDC_REASON"));
    }

    /*
     * =======================================================
     * PROPERTY ID - REQUIRED
     * =======================================================
     */

    if (!cpt?.id) {
      missingFields.push(t("NDC_MESSAGE_PROPERTY_ID"));
    }

    /*
     * =======================================================
     * PROPERTY SEARCH - REQUIRED
     * =======================================================
     */

    if (!cptDetails || Object.keys(cptDetails).length === 0) {
      missingFields.push(t("NDC_MESSAGE_PLEASE_SEARCH_PROPERTY_ID"));
    }

    /*
     * =======================================================
     * FULL NAME - REQUIRED
     * =======================================================
     */

    if (!propertyDetails?.firstName || !String(propertyDetails.firstName).trim()) {
      missingFields.push(t("REQUIRED_FIELD"));
    }

    /*
     * =======================================================
     * EMAIL - REQUIRED
     * =======================================================
     */

    if (!propertyDetails?.email || !String(propertyDetails.email).trim()) {
      missingFields.push(t("REQUIRED_FIELD"));
    }

    /*
     * =======================================================
     * MOBILE NUMBER - REQUIRED
     * =======================================================
     */

    if (!propertyDetails?.mobileNumber || !String(propertyDetails.mobileNumber).trim()) {
      missingFields.push(t("REQUIRED_FIELD"));
    }

    /*
     * =======================================================
     * ADDRESS - REQUIRED
     * =======================================================
     */

    if (!propertyDetails?.address || !String(propertyDetails.address).trim()) {
      missingFields.push(t("REQUIRED_FIELD"));
    }

    /*
     * =======================================================
     * REMARKS
     * =======================================================
     *
     * OPTIONAL.
     *
     * DO NOT VALIDATE.
     */

    /*
     * =======================================================
     * TRADE LICENSE NUMBER
     * =======================================================
     *
     * OPTIONAL.
     *
     * DO NOT VALIDATE.
     */

    /*
     * =======================================================
     * FORMAT VALIDATIONS
     * =======================================================
     */

    const nameRegex = /^[A-Za-z\s]+(,\s*[A-Za-z\s]+)*$/;

    const emailRegex = /^(?!\.)(?!.*\.\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+(\.[a-zA-Z]{2,})+$/;

    const mobileRegex = /^[6-9]\d{9}$/;

    /*
     * =======================================================
     * NAME FORMAT
     * =======================================================
     */

    if (propertyDetails?.firstName && !nameRegex.test(String(propertyDetails.firstName).trim())) {
      invalidFields.push(t("NDC_MESSAGE_FIRST_NAME_ONLY_ALPHABETS_ALLOWED"));
    }

    /*
     * =======================================================
     * EMAIL FORMAT
     * =======================================================
     */

    if (propertyDetails?.email && !emailRegex.test(String(propertyDetails.email).trim())) {
      invalidFields.push(t("NDC_MESSAGE_EMAIL_INVALID_FORMAT"));
    }

    /*
     * =======================================================
     * MOBILE FORMAT
     * =======================================================
     */

    if (propertyDetails?.mobileNumber && !mobileRegex.test(String(propertyDetails.mobileNumber).trim())) {
      invalidFields.push(t("NDC_MESSAGE_MOBILE_NUMBER_MUST_BE_A_VALID_TEN_DIGIT_INDIAN_NUMBER"));
    }

    /*
     * =======================================================
     * RETURN ALL ERRORS
     * =======================================================
     */

    return [...missingFields, ...invalidFields];
  }

  /*
   * =========================================================
   * GO BACK
   * =========================================================
   */

  function onGoBack(data) {
    onBackClick(config.key, data);
  }

  /*
   * =========================================================
   * FORM VALUE CHANGE
   * =========================================================
   */

  const onFormValueChange = (setValue = true, data) => {
    if (!_.isEqual(data, currentStepData)) {
      dispatch(updateNDCForm(config.key, data));
    }
  };

  /*
   * =========================================================
   * CLOSE TOAST
   * =========================================================
   */

  const closeToast = () => {
    setShowToast(false);
    setError("");
  };

  /*
   * =========================================================
   * RENDER
   * =========================================================
   *
   * IMPORTANT:
   *
   * config received by this component is the step config
   * from NewNDCStepForm.js.
   *
   * The actual FormComposer configuration is:
   *
   * config.currStepConfig
   *
   * NOT:
   *
   * config
   *
   * Passing config directly causes:
   *
   * "props.config?.map is not a function"
   */

  return (
    <React.Fragment>
      <FormComposer
        defaultValues={currentStepData}
        config={config?.currStepConfig || []}
        onSubmit={goNext}
        onFormValueChange={onFormValueChange}
        label={t(`${config?.texts?.submitBarLabel || "Next"}`)}
        currentStep={config?.currStepNumber}
        onBackClick={onGoBack}
      />

      {getLoader && <Loader page={true} />}

      {showToast && <Toast isDleteBtn={true} error={true} label={error} onClose={closeToast} />}
    </React.Fragment>
  );
};

export default NewNDCStepFormOne;
