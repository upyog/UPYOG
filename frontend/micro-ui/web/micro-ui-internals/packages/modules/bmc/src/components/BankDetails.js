import { AddIcon, RemoveIcon, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

const bankDetails = {
  IFSC001: {
    bankName: "Bank A",
    branchName: "Branch A1",
    micrCode: "MICR001",
  },
  IFSC002: {
    bankName: "Bank B",
    branchName: "Branch B1",
    micrCode: "MICR002",
  },
  // Add more bank details here
};

const BankDetailsForm = ({ tenantId, initialRows = [], AddOption = true, AllowRemove = true }) => {
  const {
    control,
    setValue,
    watch,
    formState: { errors },
    handleSubmit,
  } = useForm();
  const { t } = useTranslation();
  const headerLocale = Digit.Utils.locale.getTransformedLocale(tenantId);
  const [rows, setRows] = useState([]);
  const ifscCode = watch("ifscCodes");

  useEffect(() => {
    if (ifscCode) {
      const details = bankDetails[ifscCode] || {};
      setValue("name", details.bankName || "");
      setValue("branchNames", details.branchName || "");
      setValue("micrCodes", details.micrCode || "");
    } else {
      setValue("name", "");
      setValue("branchNames", "");
      setValue("micrCodes", "");
    }
  }, [ifscCode, setValue]);

  const addRow = (data) => {
    setRows([...rows, data]);
  };

  const removeRow = (index) => {
    const updatedRows = rows.filter((_, i) => i !== index);
    setRows(updatedRows);
  };

  useEffect(() => {
    setRows(initialRows);
  }, [initialRows]);

  return (
    <div className="bmc-row-card-header">
      <div className="bmc-card-row">
        <div className="bmc-title">BANK DETAILS</div>
        <div className="bmc-table-container" style={{ padding: "1rem" }}>
          <form onSubmit={handleSubmit(addRow)}>
            <table className="bmc-hover-table">
              <thead>
                <tr>
                  <th scope="col">IFSC Code</th>
                  <th scope="col">MICR Code</th>
                  <th scope="col">Account Number</th>
                  <th scope="col">Bank Name</th>
                  <th scope="col">Branch Name</th>
                  {AllowRemove && <th scope="col"></th>}
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td data-label="IFSC Code" style={{ textAlign: "left" }}>
                    <Controller
                      control={control}
                      name="ifscCodes"
                      render={(props) => (
                        <div>
                          <TextInput {...props} placeholder="IFSC Code" />
                          {errors.ifscCodes && <span style={{ color: "red" }}>{errors.ifscCodes.message}</span>}
                        </div>
                      )}
                    />
                  </td>
                  <td data-label="MICR Code" style={{ textAlign: "left" }}>
                    <Controller
                      control={control}
                      name="micrCodes"
                      render={(props) => (
                        <div>
                          <TextInput {...props} placeholder="MICR Code" disabled />
                          {errors.micrCodes && <span style={{ color: "red" }}>{errors.micrCodes.message}</span>}
                        </div>
                      )}
                    />
                  </td>
                  <td data-label="Account Number" style={{ textAlign: "left" }}>
                    <Controller
                      control={control}
                      name="accountnumber"
                      render={(props) => (
                        <div>
                          <TextInput {...props} placeholder="Account Number" type="number" />
                          {errors.accountnumber && <span style={{ color: "red" }}>{errors.accountnumber.message}</span>}
                        </div>
                      )}
                    />
                  </td>
                  <td data-label="Bank Name" style={{ textAlign: "left" }}>
                    <Controller
                      control={control}
                      name="name"
                      render={(props) => (
                        <div>
                          <TextInput {...props} placeholder="Bank Name" disabled />
                          {/* {errors.bankName && <span style={{ color: 'red' }}>{errors.bankName.message}</span>} */}
                        </div>
                      )}
                    />
                  </td>
                  <td data-label="Branch Name" style={{ textAlign: "left" }}>
                    <Controller
                      control={control}
                      name="branchNames"
                      render={(props) => (
                        <div>
                          <TextInput {...props} placeholder="Branch Name" disabled />
                          {/* {errors.branchName && <span style={{ color: 'red' }}>{errors.branchName.message}</span>} */}
                        </div>
                      )}
                    />
                  </td>
                  <td data-label="Add Row">
                    <button type="submit">
                      <AddIcon className="bmc-add-icon" />
                    </button>
                  </td>
                </tr>
                {rows.map((row, index) => (
                  <tr key={index}>
                    <td>{row.ifscCodes || "-"}</td>
                    <td>{row.micrCodes || "-"}</td>
                    <td>{row.accountnumber || "-"}</td>
                    <td>{row.name || "-"}</td>
                    <td>{row.branchNames || "-"}</td>

                    {AllowRemove && (
                      <td data-label="Remove Row">
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
  );
};

export default BankDetailsForm;
