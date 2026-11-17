import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { FormComposer, Toast, Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { newConfig as newConfigMcollect } from "../../../config/config";

import { stringReplaceAll } from "../../../utils";

const getformDataforEdit = (ChallanData,fetchBillData) => {
  let defaultval = {
    ConsumerName: ChallanData[0].citizen.name,
    mobileNumber: ChallanData[0].citizen.mobileNumber,
    emailId: ChallanData[0].citizen.emailId,
    doorNo: ChallanData[0].address.doorNo,
    building: ChallanData[0].address.buildingName,
    streetName: ChallanData[0].address.street,
    pincode: ChallanData[0].address.pincode || "143001",
    mohalla: { ...ChallanData[0].address.locality, i18nkey: `${stringReplaceAll(ChallanData[0].tenantId, ".", "_").toUpperCase()}_ADMIN_${ChallanData[0]?.address?.locality?.code}` },
    category: { code: ChallanData[0].businessService, i18nkey: `BILLINGSERVICE_BUSINESSSERVICE_${ChallanData[0]?.businessService.split(".")[0].toUpperCase()}` },
    categoryType: { code: ChallanData[0].businessService, i18nkey: `BILLINGSERVICE_BUSINESSSERVICE_${stringReplaceAll(ChallanData[0].businessService, ".", "_").toUpperCase()}` },
    fromDate: ChallanData[0]
      ? new Date(ChallanData[0].taxPeriodFrom).getFullYear().toString() +
        "-" +
        `${(new Date(ChallanData[0].taxPeriodFrom).getMonth() + 1) < 10 ? "0" : ""}${new Date(ChallanData[0].taxPeriodFrom).getMonth() + 1}` +
        "-" +
        `${new Date(ChallanData[0].taxPeriodFrom).getDate() < 10 ? "0" : ""}${new Date(ChallanData[0].taxPeriodFrom).getDate()}`
      : null,
    toDate: ChallanData[0]
      ? new Date(ChallanData[0].taxPeriodTo).getFullYear().toString() +
        "-" +
        `${(new Date(ChallanData[0].taxPeriodTo).getMonth() + 1) < 10 ? "0" : ""}${new Date(ChallanData[0].taxPeriodTo).getMonth() + 1}` +
        "-" +
        `${new Date(ChallanData[0].taxPeriodTo).getDate() < 10 ? "0" : ""}${new Date(ChallanData[0].taxPeriodTo).getDate()}`
      : null,
  };
  const prefix = ChallanData[0]?.businessService.split(".")[0];
  defaultval[prefix] = {};
  if (fetchBillData.Bill[0].billDetails[0].billAccountDetails.length > 0) {
    fetchBillData.Bill[0].billDetails[0].billAccountDetails.forEach(
      (ele) => (defaultval[prefix][`${ele.taxHeadCode.split(".")[1]}`] = `${ele.amount}`)
    );
  }
  sessionStorage.setItem("InitialTaxFeilds", JSON.stringify(defaultval[prefix]));
  return defaultval;
};

const NewChallan = ({ ChallanData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const { url } = Digit.Hooks.useModuleBasePath();
  const isEdit = url.includes("modify-challan");
  const navigate = Digit.Hooks.useCustomNavigate();
  const isMobile = window.Digit.Utils.browser.isMobile();

  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", {});

  const stateId = Digit.ULBService.getStateId();
  const { data: newConfig } = Digit.Hooks.mcollect.useMcollectFormConfig.getFormConfig(stateId, {});
  const queryResult = Digit.Hooks.mcollect.useMCollectCategory(tenantId, "[?(@.type=='Adhoc' && @.isActive==true)]");
  const categoriesandTypes = queryResult?.data?.data;

  const lastModTime = ChallanData ? ChallanData[0].auditDetails.lastModifiedTime : null;
  const { data: fetchBillData } = ChallanData
    ? Digit.Hooks.useFetchBillsForBuissnessService(
        { businessService: ChallanData[0].businessService, consumerCode: ChallanData[0].challanNo },
        { lastModTime }
      )
    : {};

  // Compute edit defaults only after fetchBillData is ready — declared AFTER the hook
  let editDefaults = null;
  if (isEdit && fetchBillData && ChallanData) {
    const formdata = getformDataforEdit(ChallanData, fetchBillData);
    editDefaults = { consomerDetails1: [{ ...formdata }] };
    sessionStorage.setItem("mcollectEditObject", JSON.stringify(editDefaults));
  }

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);

  window.onunload = function () {
    sessionStorage.removeItem("mcollectFormData");
  };

  const onFormValueChange = (setValue, formData, formState) => {
    setSubmitValve(!Object.keys(formState.errors).length);
  };

  const onSubmit = (data) => {
    let mcollectFormValue = JSON.parse(sessionStorage.getItem("mcollectFormData")) || {};
    const formData = {
      ...(data?.consomerDetails1?.[0] || {}),
      ...(data?.addressDetails?.[0] || {}),
      ...(data?.serviceDetails?.[0] || {}),
      ...mcollectFormValue,
    };

    const categoryCode = typeof formData?.category === "string" ? formData.category : formData?.category?.code;
    const categoryTypeCode = typeof formData?.categoryType === "string" ? formData.categoryType : formData?.categoryType?.code;
    const prefix = categoryCode?.split(".")[0] || categoryTypeCode?.split(".")[0] || ChallanData?.[0]?.businessService?.split(".")[0];
    const bService = categoryTypeCode || ChallanData?.[0]?.businessService;

    const nestedTaxObj = mcollectFormValue?.[prefix] || formData?.[prefix] || data?.serviceDetails?.[0]?.[prefix] || {};
    const taxHeadAmounts = mcollectFormValue?.taxHeadAmounts || formData?.taxHeadAmounts || {};
    const allTaxHeads = categoriesandTypes?.MdmsRes?.BillingService?.TaxHeadMaster || [];
    const relevantTaxHeads = allTaxHeads.filter((ob) => ob.service === bService);

    let amountArray = [];
    if (relevantTaxHeads.length > 0) {
      amountArray = relevantTaxHeads.map((tax) => {
        const keySuffix = tax.code.includes(".") ? tax.code.split(".").slice(1).join(".") : tax.code;
        const safeKey = tax.code.replace(/\./g, "_");
        const rawVal =
          taxHeadAmounts?.[tax.code] ??
          nestedTaxObj?.[keySuffix] ??
          nestedTaxObj?.[tax.code] ??
          mcollectFormValue?.[tax.code] ??
          mcollectFormValue?.[safeKey] ??
          formData?.[tax.code] ??
          formData?.[safeKey] ??
          data?.serviceDetails?.[0]?.[tax.code] ??
          data?.serviceDetails?.[0]?.[safeKey] ??
          0;
        return {
          taxHeadCode: tax.code,
          amount: rawVal ? Math.round(Number(rawVal)) : 0,
        };
      });
    } else if (Object.keys(taxHeadAmounts).length > 0) {
      amountArray = Object.entries(taxHeadAmounts).map(([code, val]) => ({
        taxHeadCode: code.includes(".") ? code : `${prefix}.${code}`,
        amount: val ? Math.round(Number(val)) : 0,
      }));
    } else if (nestedTaxObj && Object.keys(nestedTaxObj).length > 0) {
      let TaxHeadMasterKeys = Object.keys(nestedTaxObj);
      let TaxHeadMasterValues = Object.values(nestedTaxObj);
      amountArray = TaxHeadMasterKeys.map((ele, index) => ({
        taxHeadCode: ele.includes(".") ? ele : `${prefix}.${ele}`,
        amount: TaxHeadMasterValues[index] ? Math.round(Number(TaxHeadMasterValues[index])) : 0,
      }));
    }

    const Challan = isEdit
      ? {
          accountId: ChallanData[0].accountId,
          citizen: ChallanData[0].citizen,
          applicationStatus: ChallanData[0].applicationStatus,
          auditDetails: ChallanData[0].auditDetails,
          id: ChallanData[0].id,
          businessService: ChallanData[0].businessService,
          challanNo: ChallanData[0].challanNo,
          consumerType: formData?.category?.code,
          description: formData.comments,
          taxPeriodFrom: Date.parse(formData.fromDate),
          taxPeriodTo: Date.parse(formData.toDate),
          tenantId,
          address: ChallanData[0].address,
          amount: amountArray,
        }
      : {
          citizen: { name: formData.ConsumerName, mobileNumber: formData.mobileNumber, emailId: formData.emailId },
          businessService: formData?.categoryType?.code,
          consumerType: formData?.category?.code?.split(".")[0],
          description: formData?.comments,
          taxPeriodFrom: Date.parse(formData?.fromDate),
          taxPeriodTo: Date.parse(formData?.toDate),
          tenantId,
          address: {
            buildingName: formData.building,
            doorNo: formData.doorNo,
            street: formData.streetName,
            locality: { code: formData?.mohalla?.code },
            pincode: formData.pincode,
          },
          amount: amountArray,
        };

    if (isEdit) {
      Digit.MCollectService.update({ Challan }, tenantId)
        .then((result) => {
          if (result.challans?.length > 0) {
            const challan = result.challans[0];
            sessionStorage.removeItem("mcollectEditObject");
            Digit.SessionStorage.set("isMcollectAppChanged", challan.auditDetails.lastModifiedTime);
            Digit.MCollectService.generateBill(challan.challanNo, tenantId, challan.businessService, "challan").then((response) => {
              if (response.Bill?.length > 0) {
                navigate(
                  `/upyog-ui/employee/mcollect/acknowledgement?purpose=challan&status=success&tenantId=${tenantId}&billNumber=${response.Bill[0].billNumber}&serviceCategory=${response.Bill[0].businessService}&challanNumber=${response.Bill[0].consumerCode}&isEdit=true`,
                  { from: url }
                );
              }
            });
          }
        })
        .catch((e) => setShowToast({ key: "error", label: e?.response?.data?.Errors?.[0]?.message || e?.response?.data?.Errors?.[0]?.code || e?.message || "Error creating challan" }));
    } else {
      Digit.MCollectService.create({ Challan }, tenantId)
        .then((result) => {
          if (result.challans?.length > 0) {
            const challan = result.challans[0];
            sessionStorage.removeItem("mcollectFormData");
            Digit.MCollectService.generateBill(challan.challanNo, tenantId, challan.businessService, "challan").then((response) => {
              if (response.Bill?.length > 0) {
                navigate(
                  `/upyog-ui/employee/mcollect/acknowledgement?purpose=challan&status=success&tenantId=${tenantId}&billNumber=${response.Bill[0].billNumber}&serviceCategory=${response.Bill[0].businessService}&challanNumber=${response.Bill[0].consumerCode}`,
                  { from: url }
                );
              }
            });
          }
        })
        .catch((e) => setShowToast({ key: "error", label: e?.response?.data?.Errors?.[0]?.message || e?.response?.data?.Errors?.[0]?.code || e?.message || "Error creating challan" }));
    }
  };

  const configs = (newConfig || newConfigMcollect || []);

  const checkHead = (head) => {
    if (head === "ES_NEW_APPLICATION_LOCATION_DETAILS") return "TL_CHECK_ADDRESS";
    if (head === "ES_NEW_APPLICATION_OWNERSHIP_DETAILS") return "TL_OWNERSHIP_DETAILS_HEADER";
    return head;
  };

  // In edit mode, don't mount FormComposer until editDefaults is ready —
  // RHF useForm only reads defaultValues once on mount, so we must wait.
  if (isEdit && !editDefaults) return <Loader />;

  return (
    <div>
      <div style={isMobile ? {} : { marginLeft: "15px" }}>
        <Header>{isEdit ? t("UC_UPDATE_CHALLAN") : t("UC_COMMON_HEADER")}</Header>
      </div>
      <FormComposer
        heading={t("")}
        label={t("ES_COMMON_APPLICATION_SUBMIT")}
        config={configs.map((config) => ({
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
          head: checkHead(config.head),
        }))}
        fieldStyle={{ marginRight: 0 }}
        onSubmit={onSubmit}
        defaultValues={isEdit ? editDefaults : {}}
        onFormValueChange={onFormValueChange}
        breaklineStyle={{ border: "0px" }}
      />
      {showToast && (
        <Toast
          error={showToast?.key === "error"}
          label={showToast?.label}
          onClose={() => setShowToast(null)}
        />
      )}
    </div>
  );
};

export default NewChallan;