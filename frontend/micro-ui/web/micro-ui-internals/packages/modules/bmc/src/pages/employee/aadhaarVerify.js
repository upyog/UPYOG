import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import SearchApplications from "../../components/SearchApplications";
import Title from "../../components/title";
import Pagination from "../../components/pagination";

const headers = ["Select", "Name", "Application Number", "Ward Name", "Gender", "Pincode"];

const AadhaarVerifyPage = (_props) => {
  const { t } = useTranslation();
  const [applications, setApplications] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(15);
  const [selectedRows, setSelectedRows] = useState([]);

  const indexOfLastRow = currentPage * rowsPerPage;
  const indexOfFirstRow = indexOfLastRow - rowsPerPage;
  const currentRows = applications.slice(indexOfFirstRow, indexOfLastRow);

  const getVerifierApplications = Digit.Hooks.bmc.useVerifierSchemeDetail();

  const handleSearchCriteria = (criteria) => {
    getVerifierApplications.mutate(
      { schemeId: criteria.schemeID, detailID: criteria.detailID, type: criteria.type },
      {
        onSuccess: (data) => {
          if (data && data.Applications) {
            setApplications(data.Applications);
          } else {
            setApplications([]);
          }
        },
        onError: (err) => {
          console.error("Error fetching applications:", err);
        },
      }
    );
  };

  const handleRowClick = (applicationNumber) => {
    window.location.href = `/digit-ui/employee/bmc/aadhaarEmployee/${applicationNumber}`;
  };

  const handleSelectAll = (e) => {
    if (e.target.checked) {
      setSelectedRows(currentRows.map((row) => row.applicationNumber));
    } else {
      setSelectedRows([]);
    }
  };

  const handleRowCheckboxChange = (applicationNumber) => {
    setSelectedRows((prevSelectedRows) =>
      prevSelectedRows.includes(applicationNumber)
        ? prevSelectedRows.filter((row) => row !== applicationNumber)
        : [...prevSelectedRows, applicationNumber]
    );
  };

  return (
    <React.Fragment>
      <Title text={t("BMC_Aadhaar_Verify")} />
      <div className="bmc-card-full">
        <SearchApplications onUpdate={handleSearchCriteria} />
        <div className="bmc-row-card-header" style={{ padding: "0" }}>
          <div className="bmc-card-row">
            <div className="bmc-table-container">
              <table className="bmc-hover-table">
                <thead>
                  <tr>
                    <th>
                      <input
                        type="checkbox"
                        onChange={handleSelectAll}
                        checked={currentRows.length > 0 && currentRows.every((row) => selectedRows.includes(row.applicationNumber))}
                      />
                    </th>
                    {headers.slice(1).map((header, index) => (
                      <th key={index} scope="col">
                        {header}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {currentRows.length > 0 ? (
                    currentRows.map((item, index) => (
                      <tr key={index} style={{ cursor: "pointer" }}>
                        <td>
                          <input
                            type="checkbox"
                            checked={selectedRows.includes(item.applicationNumber)}
                            onChange={() => handleRowCheckboxChange(item.applicationNumber)}
                          />
                        </td>
                        <td onClick={() => handleRowClick(item.applicationNumber)} style={{ color: "#F47738" }}>
                          {item.ApplicantDetails[0]?.AadharUser?.aadharName || "N/A"}
                        </td>
                        <td onClick={() => handleRowClick(item.applicationNumber)}>{item.applicationNumber || "N/A"}</td>
                        <td onClick={() => handleRowClick(item.applicationNumber)}>{item.ApplicantDetails[0]?.address?.wardName || "N/A"}</td>
                        <td onClick={() => handleRowClick(item.applicationNumber)}>{item.ApplicantDetails[0]?.AadharUser?.gender || "N/A"}</td>
                        <td onClick={() => handleRowClick(item.applicationNumber)}>{item.ApplicantDetails[0]?.address?.pinCode || "N/A"}</td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={headers.length}>No data available</td>
                    </tr>
                  )}
                </tbody>
              </table>
              <Pagination
                totalRecords={applications.length}
                rowsPerPage={rowsPerPage}
                currentPage={currentPage}
                onPageChange={setCurrentPage}
                onRowsPerPageChange={setRowsPerPage}
              />
            </div>
            <button className="bmc-card-button">Verify All</button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default AadhaarVerifyPage;
