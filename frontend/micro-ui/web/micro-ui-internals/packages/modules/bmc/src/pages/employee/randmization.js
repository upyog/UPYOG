import { CardLabel, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import Pagination from "../../components/pagination";
import Title from "../../components/title";
import dropdownOptions from "../../pagecomponents/dropdownOptions.json";

const data = [
  {
    id: 1,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 1",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 2,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 3,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 1",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 4,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 5,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 6,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 1",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 7,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 8,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 1",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 9,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 10,
    name: "BalKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 11,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 1",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 12,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 13,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 1",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 14,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 15,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 16,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 1",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 17,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 18,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 1",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 19,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
  {
    id: 20,
    name: "RamKrishanaYadav",
    applicationNumber: "123456",
    wardName: "Ward 2",
    gender: "Male",
    pincode: "226022",
  },
];

const headers = ["", "Name", "Application Number", "Ward Name", "Gender", "Pincode"];

const RandmizationPage = () => {
  const { t } = useTranslation();
  const { control } = useForm();
  const history = useHistory();
  const [isChecked, setIsChecked] = useState({});
  const [isAllChecked, setIsAllChecked] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(5);

  const indexOfLastRow = currentPage * rowsPerPage;
  const indexOfFirstRow = indexOfLastRow - rowsPerPage;
  const currentRows = data.slice(indexOfFirstRow, indexOfLastRow);

  useEffect(() => {
    const initialCheckedState = data.reduce((acc, item) => {
      acc[item.id] = true;
      return acc;
    }, {});
    setIsChecked(initialCheckedState);
  }, []);

  const handleCheckboxChange = (e, id) => {
    setIsChecked((prev) => ({ ...prev, [id]: e.target.checked }));
  };

  const handleAllCheckboxChange = (e) => {
    setIsAllChecked(e.target.checked);
    const newCheckedState = data.reduce((acc, item) => {
      acc[item.id] = e.target.checked;
      return acc;
    }, {});
    setIsChecked(newCheckedState);
  };

  const verifySelectedItems = () => {
    const selectedIds = Object.entries(isChecked)
      .filter(([_, checked]) => checked)
      .map(([id, _]) => id);
    if (selectedIds.length > 0) {
      history.push(`/digit-ui/citizen/bmc/crossverify?ids=${selectedIds.join(",")}`);
    }
  };

  return (
    <React.Fragment>
      <Title text={t("BMC_Randomization")} />
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-col3-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Scheme_Type")}</CardLabel>
                <Controller
                  control={control}
                  name={"scheme"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      placeholder={t("Select Scheme Type")}
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.scheme}
                      optionKey="value"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Scheme")}</CardLabel>
                <Controller
                  control={control}
                  name={"machine"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      placeholder={t("Select Scheme")}
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.machine}
                      optionKey="value"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Scheme_Name")}</CardLabel>
                <Controller
                  control={control}
                  name={"machine"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      placeholder={t("Select Scheme Name")}
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.machine}
                      optionKey="value"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <div className="bmc-search-button" style={{ textAlign: "end" }}>
                <button
                  type="button"
                  className="bmc-card-button"
                  style={{
                    borderBottom: "3px solid black",
                  }}
                >
                  {t("BMC_Search")}
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-table-container" style={{ padding: "0" }}>
              <div className="bmc-card-row">
                <div className="bmc-col3-card">
                  <LabelFieldPair>
                    <CardLabel className="bmc-label">{t("Bmc_Randomization_Type")}</CardLabel>
                    <Controller
                      control={control}
                      name={"randomizationType"}
                      rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      }}
                      render={(props) => (
                        <Dropdown
                          placeholder={t("Select Randomization Type")}
                          selected={props.value}
                          select={(value) => {
                            props.onChange(value);
                          }}
                          onBlur={props.onBlur}
                          option={dropdownOptions.randomizationType}
                          optionKey="value"
                          t={t}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col3-card">
                  <LabelFieldPair>
                    <CardLabel className="bmc-label">{t("Bmc_No_of_Machine_per_electoral_ward_for_Randomization")}</CardLabel>
                    <Controller
                      control={control}
                      name={"machine"}
                      rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      }}
                      render={(props) => (
                        <TextInput
                          placeholder={t("Select Number")}
                          selected={props.value}
                          select={(value) => {
                            props.onChange(value);
                          }}
                          onBlur={props.onBlur}
                          option={dropdownOptions.machine}
                          optionKey="value"
                          t={t}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
              </div>
              <table className="bmc-hover-table">
                <thead>
                  <tr>
                    <th>
                      <input type="checkbox" checked={isAllChecked} onChange={handleAllCheckboxChange} />
                    </th>
                    {headers.slice(1).map((header, index) => (
                      <th key={index} scope="col">
                        {header}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {currentRows.map((row) => (
                    <tr key={row.id}>
                      <td>
                        <input type="checkbox" checked={isChecked[row.id] || false} onChange={(e) => handleCheckboxChange(e, row.id)} />
                      </td>
                      <td style={{ color: "#F47738" }} data-label="Name" scope="row">
                        {row.name}
                      </td>
                      <td data-label="Application Number" scope="row">
                        {row.applicationNumber}
                      </td>
                      <td data-label="Ward Name" scope="row">
                        {row.wardName}
                      </td>
                      <td data-label="Gender" scope="row">
                        {row.gender}
                      </td>
                      <td data-label="Pincode" scope="row">
                        {row.pincode}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <Pagination
                totalRecords={data.length}
                rowsPerPage={rowsPerPage}
                currentPage={currentPage}
                onPageChange={setCurrentPage}
                onRowsPerPageChange={setRowsPerPage}
              />
            </div>
            <div style={{ textAlign: "end", padding: "1rem" }}>
              <button className="bmc-card-button" style={{ borderBottom: "3px solid black", marginRight: "1rem" }} onClick={verifySelectedItems}>
                {t("BMC_Randomize")}
              </button>
              <button className="bmc-card-button-cancel" style={{ borderBottom: "3px solid black", outline: "none" }}>
                {t("BMC_Reject")}
              </button>
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default RandmizationPage;
