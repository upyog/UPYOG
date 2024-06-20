import { CardLabel, CardLabelDesc, Dropdown, FormStep, UploadFile } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { stringReplaceAll } from "../../utils";
import Timeline from "../../components/RAFB/Timeline";


const SelectProjectName  = ({ t, config, onSelect, userType, formData }) => {
  //let index = window.location.href.charAt(window.location.href.length - 1);
  const { pathname: url } = useLocation();
  const isMutation = url.includes("property-mutation");

  let index = window.location.href.split("/").pop();
  const [uploadedFile, setUploadedFile] = useState(
    !isMutation ? formData?.address?.documents?.ProofOfAddress?.fileStoreId || null : formData?.[config.key]?.fileStoreId
  );
  const [file, setFile] = useState(formData?.address?.documents?.ProofOfAddress);
  const [error, setError] = useState(null);
  const cityDetails = Digit.ULBService.getCurrentUlb();
  const isUpdateProperty = formData?.isUpdateProperty || false;
  const isEditProperty = formData?.isEditProperty || false;

  const [dropdownValue, setDropdownValue] = useState(
    !isMutation ? formData?.address?.documents?.ProofOfAddress?.documentType || null : formData?.[config.key]?.documentType
  );
  let dropdownData = [];
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const { data: Documentsob = {} } = Digit.Hooks.pt.usePropertyMDMS(stateId, "PropertyTax", "Documents");
  const docs = Documentsob?.PropertyTax?.Documents;
  console.log("docs ",docs)
  const proofOfAddress = Array.isArray(docs) && docs.filter((doc) => doc.code.includes("ADDRESSPROOF"));
  if (proofOfAddress.length > 0) {
    dropdownData = proofOfAddress[0]?.dropdownData;
    dropdownData.forEach((data) => {
      data.i18nKey = stringReplaceAll(data.code, ".", "_");
    });
  }

  function setTypeOfDropdownValue(dropdownValue) {
    setDropdownValue(dropdownValue);
  }

  const handleSubmit = () => {
    let fileStoreId = uploadedFile;
    let fileDetails = file;
    if (fileDetails) fileDetails.documentType = dropdownValue;
    if (fileDetails) fileDetails.fileStoreId = fileStoreId ? fileStoreId : null;
    let address = !isMutation ? formData?.address : {};
    if (address && address.documents) {
      address.documents["ProofOfAddress"] = fileDetails;
    } else {
      address["documents"] = [];
      address.documents["ProofOfAddress"] = fileDetails;
    }
    if (!isMutation) onSelect(config.key, address, "", index);
    else onSelect(config.key, { documentType: dropdownValue, fileStoreId }, "", index);
  };
  const onSkip = () => onSelect();

  function selectfile(e) {
    setFile(e.target.files[0]);
  }

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 2000000) {
          setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("property-upload", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("PT_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {}
        }
      }
    })();
  }, [file]);
  const checkMutatePT = window.location.href.includes("citizen/pt/property/property-mutation/") ? (
    <Timeline currentStep={3} flow="PT_MUTATE" />
  ) : (
    <Timeline currentStep={1} />
  );

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? checkMutatePT : null}
      <FormStep
        config={config}
        onSelect={handleSubmit}
        onSkip={onSkip}
        t={t}
        isDisabled={isUpdateProperty || isEditProperty ? false : !uploadedFile || !dropdownValue || error}
      >
        <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_PROJECT_NAME")}`}</CardLabel>
        <Dropdown
          t={t}
          isMandatory={false}
          option={dropdownData}
          selected={dropdownValue}
          optionKey="i18nKey"
          select={setTypeOfDropdownValue}
          placeholder={t(`PT_MUTATION_SELECT_DOC_LABEL`)}
        />
        {error ? <div style={{ height: "20px", width: "100%", fontSize: "20px", color: "red", marginTop: "5px" }}>{error}</div> : ""}
        <div style={{ disabled: "true", height: "20px", width: "100%" }}></div>
      </FormStep>
    </React.Fragment>
  );
};


export default SelectProjectName;


// import { CardLabel, CitizenInfoLabel, FormStep, Loader, TextInput } from "@egovernments/digit-ui-react-components";
// import React, { useState, useEffect } from "react";
// import Timeline from "../../components/RAFB/Timeline";
// // import { currentFinancialYear } from "../utils";

// const SelectProjectName = ({ t, config, onSelect, value, userType, formData, digitTest="testqwert" }) => {
//   console.log("Select Project Name config,formData ",{config,formData})
//   let validation = {};
//   const onSkip = () => onSelect();
//   const [ProjectName, setProjectName] = useState(formData.ProjectInfo?.ProjectName);
//   const tenantId = Digit.ULBService.getCurrentTenantId();
//   const stateId = Digit.ULBService.getStateId();
//   const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
//   const { isLoading, data: fydata = {} } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "egf-master", "FinancialYear");

//   let mdmsFinancialYear = fydata["egf-master"] ? fydata["egf-master"].FinancialYear.filter(y => y.module === "TL") : [];
//   let FY = mdmsFinancialYear && mdmsFinancialYear.length > 0 && mdmsFinancialYear.sort((x, y) => y.endingDate - x.endingDate)[0]?.code;
//   function setSelectProjectName(e) {
//     setProjectName(e.target.value);
//   }

//   useEffect(() => {
//     localStorage.setItem("TLAppSubmitEnabled", "true");
//   }, []);

//   const goNext = () => {
//     // const getCurrentFinancialYear = () => {
//     //   var today = new Date();
//     //   var curMonth = today.getMonth();
//     //   var fiscalYr = "";
//     //   if (curMonth > 3) {
//     //     var nextYr1 = (today.getFullYear() + 1).toString();
//     //     fiscalYr = today.getFullYear().toString() + "-" + nextYr1;
//     //   } else {
//     //     var nextYr2 = today.getFullYear().toString();
//     //     fiscalYr = (today.getFullYear() - 1).toString() + "-" + nextYr2.slice(-2);
//     //   }
//     //   return fiscalYr;
//     // };

//     // sessionStorage.setItem("CurrentFinancialYear", FY);
//     // sessionStorage.setItem("CurrentFinancialYear", getCurrentFinancialYear());
//     // sessionStorage.setItem("CurrentFinancialYear", currentFinancialYear());
//     onSelect(config.key, { ProjectName });
//   };
//   if (isLoading) {
//     return <Loader></Loader>
//   }

//   return (
//     <React.Fragment>
//       {window.location.href.includes("/citizen") ? <Timeline /> : null}
//       <FormStep
//         config={config}
//         onSelect={goNext}
//         onSkip={onSkip}
//         t={t}
//         isDisabled={!ProjectName}
//       >
//         <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_PROJECT_NAME")}`}</CardLabel>
//         <TextInput
//           t={t}
//           isMandatory={false}
//           type={"text"}
//           // optionKey="i18nKey"
//           name="ProjectName"
//           value={ProjectName}
//           onChange={setSelectProjectName}
//           disable={isEdit}
//           // disabled={true}
//           {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
//         />
//       </FormStep>
//       {<CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("TL_LICENSE_ISSUE_YEAR_INFO_MSG") + FY} />}
//     </React.Fragment>
//   );
// };

// export default SelectProjectName;
