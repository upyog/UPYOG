import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import Pagination from "../../components/pagination";
import SearchApplications from "../../components/SearchApplications";
import Title from "../../components/title";

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
];

const headers = ["Name", "Application Number", "Ward Name", "Gender", "Pincode"];
const AadhaarVerifyPage = (_props) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { control } = useForm();
  const [currentPage, setCurrentPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(5);

  const indexOfLastRow = currentPage * rowsPerPage;
  const indexOfFirstRow = indexOfLastRow - rowsPerPage;
  const currentRows = data.slice(indexOfFirstRow, indexOfLastRow);
  
  const handleSearchCriteria=(critieria)=>{
    console.log(critieria)
  }
  
  return (
    <React.Fragment>
      <Title text={t("BMC_Aadhaar_Verify")} />
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <SearchApplications onUpdate={handleSearchCriteria}></SearchApplications>
          {/* <div className="bmc-card-row">
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
          </div> */}
        </div>
        <div className="bmc-row-card-header" style={{ padding: "0" }}>
          <div className="bmc-card-row">
            <div className="bmc-table-container">
              <table className="bmc-hover-table">
                <thead>
                  <tr>
                    {headers.map((header, index) => (
                      <th key={index} scope="col">
                        {header}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {currentRows.map((row, index) => (
                    <tr key={index}>
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
                      <td style={{ textAlign: "center" }}>
                        <Link to={"/digit-ui/employee/bmc/aadhaarEmployee"} style={{ textDecoration: "none" }}>
                          <button
                            className="bmc-card-view-button"
                            style={{
                              borderBottom: "3px solid black",
                            }}
                          >
                            {t("BMC_Veiw Details")}
                          </button>
                          <span className="bmc-card-link">{t("BMC_View Details")}</span>
                        </Link>
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
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default AadhaarVerifyPage;
