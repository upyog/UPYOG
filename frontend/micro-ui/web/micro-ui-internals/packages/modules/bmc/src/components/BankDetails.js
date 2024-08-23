import { AddIcon, RemoveIcon, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

const BankDetailsForm = ({ tenantId, onUpdate, initialRows = [], AddOption = true, AllowRemove = true, ...props }) => {
  const { t } = useTranslation();
  /*
TO DO
still branch Id is not getting populated. 
Also try to excute only when ifsc length =11. 
fix length of input for 11
*/
  const initialDefaultValues = {
    branchId: "",
    name: "",
    branchName: "",
    ifsc: "SBIN0000454",
    micr: "",
    accountNumber: "",
  };

  const {
    control,
    handleSubmit,
    reset,
    getValues,
    setValue,
    watch,
    formState: { errors },
  } = useForm({
    defaultValues: initialDefaultValues,
  });

  const headerLocale = Digit.Utils.locale.getTransformedLocale(tenantId);
  const [bankData, setBankData] = useState([]);
  const [rows, setRows] = useState([]);
  const ifsc = watch("ifsc");

  const processBankData = (data, headerLocale) => {
    return (
      data?.BankDetails?.map((item) => ({
        branchId: item.branchId,
        name: item.name,
        branchName: item.branchName,
        ifsc: item.ifsc,
        micr: item.micr,
        accountNumber: item.accountNumber,
        i18nKey: `${headerLocale}_ADMIN_${item.name}`,
      })) || []
    );
  };

  const bankFunction = (data) => {
    const BankData = processBankData(data, headerLocale);
    setBankData(BankData);
    return { BankData };
  };

  const getBank = { BankSearchCriteria: { IFSC: ifsc } };
  Digit.Hooks.bmc.useCommonGetBank(getBank, { select: bankFunction });

  useEffect(() => {
    if (ifsc) {
      const details = bankData.find((bank) => bank.ifsc === ifsc) || {};
      setValue("branchId", details.branchId || "");
      setValue("name", details.name || "");
      setValue("branchName", details.branchName || "");
      setValue("micr", details.micr || "");
    } else {
      setValue("branchId", "");
      setValue("name", "");
      setValue("branchName", "");
      setValue("micr", "");
    }
  }, [ifsc, bankData, setValue]);

  useEffect(() => {
    setRows(initialRows);
  }, [initialRows]);

  const addRow = () => {
    const formData = getValues();
    const branchId = bankData.find((item) => {
      return item.branchId;
    });
    const updatedRows = [
      ...rows,
      {
        branchId: branchId.branchId,
        name: formData.name,
        branchName: formData.branchName,
        ifsc: formData.ifsc,
        micr: formData.micr,
        accountNumber: formData.accountNumber,
      },
    ];
    setRows(updatedRows);
   
    reset(initialDefaultValues);
    onUpdate(updatedRows);
    console.log("Added Row: ", updatedRows);
  };

  const removeRow = (index) => {
    const updatedRows = rows.filter((_, i) => i !== index);
    setRows(updatedRows);
    onUpdate(updatedRows);
  };

  return (
    <React.Fragment>
      <div className="bmc-row-card-header">
        <div className="bmc-card-row">
          <div className="bmc-title">{t("BANK DETAILS")}</div>
          <div className="bmc-table-container" style={{ padding: "1rem" }}>
            <form onSubmit={handleSubmit(addRow)}>
              <table className="bmc-hover-table">
                <thead>
                  <tr>
                    <th scope="col">{t("IFSC Code")}</th>
                    <th scope="col">{t("MICR Code")}</th>
                    <th scope="col">{t("Account Number")}</th>
                    <th scope="col">{t("Bank Name")}</th>
                    <th scope="col">{t("Branch Name")}</th>
                    {AllowRemove && <th scope="col"></th>}
                  </tr>
                </thead>
                <tbody>
                  {AddOption && (
                    <tr>
                      <td data-label={t("IFSC Code")} style={{ textAlign: "left" }}>
                        <Controller
                          control={control}
                          name="ifsc"
                          rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            minLength: { value: 11, message: t("IFSC code must be 11 characters long") },
                            maxLength: { value: 11, message: t("IFSC code must be 11 characters long") },
                          }}
                          render={(props) => (
                            <div>
                              <TextInput {...props} placeholder={t("IFSC Code")} />
                              {errors.ifsc && <span style={{ color: "red" }}>{errors.ifsc.message}</span>}
                            </div>
                          )}
                        />
                      </td>
                      <td data-label={t("MICR Code")} style={{ textAlign: "left" }}>
                        <Controller
                          control={control}
                          name="micr"
                          render={(props) => (
                            <div>
                              <TextInput {...props} placeholder={t("MICR Code")} disabled />
                              {errors.micr && <span style={{ color: "red" }}>{errors.micr.message}</span>}
                            </div>
                          )}
                        />
                      </td>
                      <td data-label={t("Account Number")} style={{ textAlign: "left" }}>
                        <Controller
                          control={control}
                          name="accountNumber"
                          rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                          render={(props) => (
                            <div>
                              <TextInput {...props} placeholder={t("Account Number")} type="number" value={props.value} onChange={props.onChange} />
                              {errors.accountNumber && <span style={{ color: "red" }}>{errors.accountNumber.message}</span>}
                            </div>
                          )}
                        />
                      </td>
                      <td data-label={t("Bank Name")} style={{ textAlign: "left" }}>
                        <Controller
                          control={control}
                          name="name"
                          render={(props) => (
                            <div>
                              <TextInput {...props} placeholder={t("Bank Name")} disabled />
                            </div>
                          )}
                        />
                      </td>
                      <td data-label={t("Branch Name")} style={{ textAlign: "left" }}>
                        <Controller
                          control={control}
                          name="branchName"
                          render={(props) => (
                            <div>
                              <TextInput {...props} placeholder={t("Branch Name")} disabled />
                            </div>
                          )}
                        />
                      </td>
                      <td data-label={t("Add Row")}>
                        <button type="submit">
                          <AddIcon className="bmc-add-icon" />
                        </button>
                      </td>
                    </tr>
                  )}

                  {rows.map((row, index) => (
                    <tr key={index}>
                      <td style={{ display: "none" }}>{row.branchId || "-"}</td>
                      <td>{row.ifsc || "-"}</td>
                      <td>{row.micr || "-"}</td>
                      <td>{row.accountNumber || "-"}</td>
                      <td>{row.name || "-"}</td>
                      <td>{row.branchName || "-"}</td>
                      {AllowRemove && (
                        <td data-label={t("Remove Row")}>
                          <button type="button" onClick={() => removeRow(index)}>
                            <RemoveIcon className="bmc-remove-icon" />
                          </button>
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </form>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default BankDetailsForm;
