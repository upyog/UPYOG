import { CardLabel, Dropdown, FormStep, LinkButton, Loader, RadioButtons, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../../components/RAFB/Timeline";
import { sortDropdownNames } from "../../utils/index";
import { stringReplaceAll } from "../../utils";

const RABillsTaxDetails = ({ t, config, onSelect, userType, formData }) => {
  console.log("RABillsTaxDetails formData ", { config, formData });
  // RABillTaxDetail
  const [error, setError] = useState(null);
  // const [fields, setFeilds] = useState([formData?.RABillTaxDetail[0]?.taxcategory?.i18nKey, formData.RABillTaxDetail[0]?.remark, formData.RABillTaxDetail[0]?.amount]||[{ taxcategory: "", remark: "", amount: "" }]);
  const [fields, setFeilds] = useState(
    formData?.RABillTaxDetail?.RABillTaxDetail
      ? () => {
          const s = [];
          const ar = Object.keys(formData?.RABillTaxDetail?.RABillTaxDetail);
          for (let i = 0; i < ar.length; i++) {
            s.push(formData?.RABillTaxDetail?.RABillTaxDetail[i]);
          }
          return s;
        }
      : [{ taxcategory: "", addition_deduction: "", amount_percentage: "", percentageValue: "", amount: "", total: "" }]
  );

  function handleAdd() {
    const values = [...fields];
    values.push({ taxcategory: "", addition_deduction: "", amount_percentage: "", percentageValue: "", amount: "", total: "" });
    setFeilds(values);
  }

  function handleRemove(index) {
    const values = [...fields];
    if (values.length != 1) {
      values.splice(index, 1);
      setFeilds(values);
    }
  }
  // const { pathname: url } = useLocation();
  // const editScreen = url.includes("/modify-application/");

  const goNext = () => {
    let RABillTaxDetail = formData?.RABillTaxDetail;
    let un;

    if (!error) {
      setError(null);
      un = { ...RABillTaxDetail, RABillTaxDetail: fields };
      onSelect(config.key, un);
      // onSelect(config.key, unitsdata);
    }
  };

  //dropdown code start here
  const { pathname: url } = useLocation();
  const isMutation = url.includes("property-mutation");

  const isEditProperty = formData?.isEditProperty || false;
  // const [dropdownValue, setDropdownValue] = useState([{ taxcategory: "" }])
  // const [dropdownValue, setDropdownValue] = useState(
  //   !isMutation ? formData?.address?.documents?.ProofOfAddress?.documentType || null : formData?.[config.key]?.documentType
  // );

  const dropdownDataTaxCategory = [{ i18nKey_0: "10" }, { i18nKey_0: "20" }, { i18nKey_0: "30" }];
  const dropdownDataAdditionDeduction = [{ i18nKey_1: "40" }, { i18nKey_1: "50" }, { i18nKey_1: "60" }];
  const dropdownDataAmountPercentage = [{ i18nKey_2: "70" }, { i18nKey_2: "80" }, { i18nKey_2: "90" }];

  let dropdownData = [];
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  const { data: Documentsob = {} } = Digit.Hooks.pt.usePropertyMDMS(stateId, "PropertyTax", "Documents");
  const docs = Documentsob?.PropertyTax?.Documents;
  console.log("docs ", docs);
  const proofOfAddress = Array.isArray(docs) && docs.filter((doc) => doc.code.includes("ADDRESSPROOF"));
  if (proofOfAddress.length > 0) {
    dropdownData = proofOfAddress[0]?.dropdownData;
    dropdownData.forEach((data) => {
      data.i18nKey = stringReplaceAll(data.code, ".", "_");
    });
  }
  console.log("dropdownValue dropdownData ", dropdownData);

  function setTypeOfDropdownValue(i, e, drKey = "") {
    // return false

    console.log("dropdownValue i val ", { i, e, fields });
    let units = [...fields];
    console.log("dropdownValue i val two ", units);

    if (e.target?.name === undefined) {
      if(drKey==="taxcategory"){
        alert(drKey)
      units[i].taxcategory = e;
    }
    if(drKey==="addition_deduction"){
      alert(drKey)
      units[i].addition_deduction = e;
    }
    if(drKey==="amount_percentage"){
      alert(drKey)
      units[i].amount_percentage = e;
    }
    } 
    else {
      const { name, value } = e.target;
      console.log("dropdownValue i val name", name, value);
      units[i][name] = value;
    }

    // if (e.target?.name === undefined) {
    //   units[i].taxcategory = e;
    // } else {
    //   const { name, value } = e.target;
    //   console.log("dropdownValue i val name", name, value);
    //   units[i][name] = value;
    // }
    console.log("dropdownValue i val three ", units);
    setFeilds(units);
  }
  console.log("dropdownValue data ", fields);
  const handleSubmit = () => {
    // let fileStoreId = uploadedFile;
    // let fileDetails = file;
    if (fileDetails) fileDetails.documentType = dropdownValue;
    // if (fileDetails) fileDetails.fileStoreId = fileStoreId ? fileStoreId : null;
    let address = !isMutation ? formData?.address : {};
    if (address && address.documents) {
      address.documents["ProofOfAddress"] = fileDetails;
    } else {
      address["documents"] = [];
      address.documents["ProofOfAddress"] = fileDetails;
    }
    if (!isMutation) onSelect(config.key, address, "", index);
    // else onSelect(config.key, { documentType: dropdownValue, fileStoreId }, "", index);
  };
  //dropdown code end here

  const onSkip = () => onSelect();
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={6} /> : null}
      {false ? (
        <Loader />
      ) : (
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          forcedError={t(error)}
          // isDisabled={!fields[0].tradecategory || !fields[0].tradetype || !fields[0].tradesubtype}
        >
          {fields?.map((field, index) => {
            console.log("field field field field ", field);
            console.log("field field field field taxcategory ", field?.taxcategory);
            return (
              <div key={`${field}-${index}`}>
                <div
                  style={{
                    border: "solid",
                    borderRadius: "5px",
                    padding: "10px",
                    paddingTop: "20px",
                    marginTop: "10px",
                    borderColor: "#f3f3f3",
                    background: "#FAFAFA",
                  }}
                >
                  <LinkButton
                    label={
                      <div>
                        <span>
                          <svg
                            style={{ float: "right", position: "relative", bottom: "32px" }}
                            width="24"
                            height="24"
                            viewBox="0 0 24 24"
                            fill="none"
                            xmlns="http://www.w3.org/2000/svg"
                          >
                            <path
                              d="M1 16C1 17.1 1.9 18 3 18H11C12.1 18 13 17.1 13 16V4H1V16ZM14 1H10.5L9.5 0H4.5L3.5 1H0V3H14V1Z"
                              fill={!(fields.length == 1) ? "#494848" : "#FAFAFA"}
                            />
                          </svg>
                        </span>
                      </div>
                    }
                    style={{ width: "100px", display: "inline" }}
                    onClick={(e) => handleRemove(index)}
                  />
                  <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_TAX_CATEGORY")}`}</CardLabel>
                  <Dropdown
                    value={field?.taxcategory}
                    t={t}
                    selected={field?.taxcategory}
                    isMandatory={false}
                    // option={dropdownData}
                    option={dropdownDataTaxCategory}
                    optionKey="i18nKey_0"
                    name="taxcategory"
                    select={(e) => setTypeOfDropdownValue(index, e,"taxcategory")}
                    placeholder={t(`PT_COMMONS_SELECT_PLACEHOLDER`)}
                  />

                  <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_ADDITION_DEDUCTION")}`}</CardLabel>
                  <Dropdown
                    value={field?.addition_deduction}
                    t={t}
                    selected={field?.addition_deduction}
                    isMandatory={false}
                    // option={dropdownData}
                    option={dropdownDataAdditionDeduction}
                    optionKey="i18nKey_1"
                    name="addition_deduction"
                    select={(e) => setTypeOfDropdownValue(index, e,"addition_deduction")}
                    placeholder={t(`PT_COMMONS_SELECT_PLACEHOLDER`)}
                  />

                  <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_AMOUNT_PERCENTAGE")}`}</CardLabel>
                  <Dropdown
                    value={field?.amount_percentage}
                    t={t}
                    selected={field?.amount_percentage}
                    isMandatory={false}
                    // option={dropdownData}
                    option={dropdownDataAmountPercentage}
                    optionKey="i18nKey_2"
                    name="amount_percentage"
                    select={(e) => setTypeOfDropdownValue(index, e,"amount_percentage")}
                    placeholder={t(`PT_COMMONS_SELECT_PLACEHOLDER`)}
                  />

                  <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_PERCENTAGE_VALUE")}`}</CardLabel>
                  <TextInput
                    t={t}
                    isMandatory={false}
                    type={"text"}
                    optionKey="i18nKey"
                    name="percentageValue"
                    value={field?.percentageValue}
                    onChange={(e) => setTypeOfDropdownValue(index, e)}
                    // onChange={setSelectWorkName}
                    // disable={isEdit}
                    // {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
                  />
                  <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_WITHHELD_AMOUNT")}`}</CardLabel>
                  <TextInput
                    t={t}
                    isMandatory={false}
                    type={"text"}
                    optionKey="i18nKey"
                    name="amount"
                    value={field?.amount}
                    onChange={(e) => setTypeOfDropdownValue(index, e)}
                    // onChange={setSelectWorkName}
                    // disable={isEdit}
                    // {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
                  />
                  <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_RABILL_TOTAL")}`}</CardLabel>
                  <TextInput
                    t={t}
                    isMandatory={false}
                    type={"text"}
                    optionKey="i18nKey"
                    name="total"
                    value={field?.total}
                    onChange={(e) => setTypeOfDropdownValue(index, e)}
                    // onChange={setSelectWorkName}
                    // disable={isEdit}
                    // {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
                  />
                </div>
              </div>
            );
          })}

          <div style={{ justifyContent: "center", display: "flex", paddingBottom: "15px", color: "#FF8C00" }}>
            <button type="button" style={{ paddingTop: "10px" }} onClick={() => handleAdd()}>
              {`${t("ADD_MORE_TRADE_UNITS")}`}
            </button>
          </div>
        </FormStep>
      )}
    </React.Fragment>
  );
};
export default RABillsTaxDetails;
