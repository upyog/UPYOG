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
    return () => sessionStorage.removeItem("mcollectFormData");
  }, []);

  const onFormValueChange = (setValue, formData, formState) => {
    setSubmitValve(!Object.keys(formState.errors).length);
  };

  const onSubmit = (data) => {
    const mcollectFormValue = JSON.parse(sessionStorage.getItem("mcollectFormData")) || {};
    const rhfFlat = {
      ...data?.consomerDetails1?.[0],
      ...data?.addressDetails?.[0],
      city:         data?.serviceDetails?.[0]?.city,
      category:     data?.serviceDetails?.[0]?.category,
      categoryType: data?.serviceDetails?.[0]?.categoryType,
      fromDate:     data?.serviceDetails?.[0]?.fromDate,
      toDate:       data?.serviceDetails?.[0]?.toDate,
      comments:     data?.serviceDetails?.[0]?.comments,
      ...Object.fromEntries(
        Object.entries(data?.serviceDetails?.[0] || {}).filter(([k]) =>
          !["city","category","categoryType","fromDate","toDate","comments",
            "mohalla","doorNo","building","streetName","pincode",
            "ConsumerName","mobileNumber","emailId"].includes(k)
        )
      ),
    };
    const merged = { ...mcollectFormValue, ...rhfFlat };

    const categoryPrefix = merged?.category?.code?.split(".")[0];
    const amountArray = Object.entries(merged)
      .filter(([key]) => categoryPrefix && key.startsWith(`${categoryPrefix}_`) &&
        !["category","categoryType","fromDate","toDate","city","ConsumerName",
          "mobileNumber","emailId","doorNo","building","streetName","pincode","mohalla","comments"].includes(key))
      .map(([key, val]) => ({
        taxHeadCode: key.replace("_", "."),
        amount: val ? Math.round(val) : 0,
      }));

    const Challan = isEdit
      ? {
          accountId: ChallanData[0].accountId,
          citizen: ChallanData[0].citizen,
          applicationStatus: ChallanData[0].applicationStatus,
          auditDetails: ChallanData[0].auditDetails,
          id: ChallanData[0].id,
          businessService: ChallanData[0].businessService,
          challanNo: ChallanData[0].challanNo,
          consumerType: merged?.category?.code,
          description: merged.comments,
          taxPeriodFrom: Date.parse(merged.fromDate),
          taxPeriodTo: Date.parse(merged.toDate),
          tenantId,
          address: ChallanData[0].address,
          amount: amountArray,
        }
      : {
          citizen: { name: merged.ConsumerName, mobileNumber: merged.mobileNumber, emailId: merged.emailId },
          businessService: merged?.categoryType?.code,
          consumerType: merged?.category?.code?.split(".")[0],
          description: merged?.comments,
          taxPeriodFrom: Date.parse(merged?.fromDate),
          taxPeriodTo: Date.parse(merged?.toDate),
          tenantId,
          address: {
            buildingName: merged.building,
            doorNo: merged.doorNo,
            street: merged.streetName,
            locality: { code: merged?.mohalla?.code },
            pincode: merged.pincode,
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
        .catch((e) => setShowToast({ key: "error", label: e?.response?.data?.Errors[0].message }));
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
        .catch((e) => setShowToast({ key: "error", label: e?.response?.data?.Errors[0].message }));
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