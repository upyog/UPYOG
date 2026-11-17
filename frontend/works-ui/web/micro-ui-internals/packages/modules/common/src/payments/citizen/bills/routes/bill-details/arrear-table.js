import React from "react";
import { useTranslation } from "react-i18next";

const ArrearTable = ({ className = "table", headers = [], values = [], arrears = 0 }) => {
  const { t } = useTranslation();
  return (
    <div className="cmn-arrears-table-wrapper">
      <table className="table-fixed-column-common-pay">
        <thead>
          <tr>
            <th className="arrear-table-cell arrear-table-cell--left arrear-table-cell--header">{t("CS_BILL_PERIOD")}</th>
            {headers.map((header, ind) => {
              return (
                <th className="arrear-table-cell arrear-table-cell--right arrear-table-cell--header" key={ind}>
                  {t(header)}
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody>
          {Object.values(values).map((row, ind) => (
            <tr key={ind}>
              <td className="arrear-table-cell arrear-table-cell--left" component="th" scope="row">
                {Object.keys(values)[ind]}
              </td>
              {headers.map((header, i) => {
                return (
                  <td className="arrear-table-cell arrear-table-cell--left" key={i} numeric>
                    {i > 1 && "₹"}
                    {(row[header] && row[header]["value"]) || "0"}
                  </td>
                );
              })}
            </tr>
          ))}
          <tr>
            <td className="arrear-table-cell arrear-table-cell--left"></td>
            {headers.map((header, ind) => {
              if (ind == headers.length - 1) {
                return (
                  <td className="arrear-table-cell arrear-table-cell--left font-bold" key={ind} numeric>
                    {arrears}
                  </td>
                );
              } else if (ind == headers.length - 2) {
                return (
                  <td className="arrear-table-cell arrear-table-cell--left font-bold" key={ind} numeric>
                    {t("COMMON_ARREARS_TOTAL")}
                  </td>
                );
              } else {
                return <td className="arrear-table-cell" key={ind} numeric></td>;
              }
            })}
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default ArrearTable;

