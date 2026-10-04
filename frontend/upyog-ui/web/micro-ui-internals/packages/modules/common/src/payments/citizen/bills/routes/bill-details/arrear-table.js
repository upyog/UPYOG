import React from "react";
import { useTranslation } from "react-i18next";

const styles = {
  root: {
    width: "100%",
    marginTop: "2px",
    overflowX: "auto",
    boxShadow: "none",
  },
  table: {
    minWidth: 700,
    backgroundColor: "rgba(250, 250, 250, var(--bg-opacity))",
  },
  cell: {
    maxWidth: "7em",
    minWidth: "1em",
    border: "1px solid #e8e7e6",
    padding: "4px 5px",
    fontSize: "0.8em",
    textAlign: "left",
    lineHeight: "1.5em",
  },
  cellHeader: {
    overflow: "hidden",
    textOverflow: "ellipsis",
  },
  cellLeft: {
    // position: 'sticky',
    // backgroundColor:'rgba(250, 250, 250, var(--bg-opacity))',
    // left: 0
  },
  cellRight: {
    // position: 'sticky',
    // backgroundColor:'rgba(250, 250, 250, var(--bg-opacity))',
    // right: 0
  },
};

const ArrearTable = ({ className = "table", headers = [], values = [], arrears = 0 }) => {
  const { t } = useTranslation();
  return (
    <React.Fragment>
      <div style={styles.root}>
        <table className={"table-fixed-column-common-pay"} style={styles.table}>
          <thead>
            <tr>
              <th className="arrear-table-cell arrear-table-cell--left arrear-table-cell--header">{t("CS_BILL_PERIOD")}</th>
              {headers.map((header, ind) => {
                let styleRight = headers.length == ind + 1 ? styles.cellRight : {};
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
                  let styleRight = headers.length == i + 1 ? styles.cellRight : {};
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
                    <td className="arrear-table-cell arrear-table-cell--left" key={ind} numeric>
                      {t("COMMON_ARREARS_TOTAL")}
                    </td>
                  );
                } else {
                  return <td style={styles.cell} key={ind} numeric></td>;
                }
              })}
            </tr>
          </tbody>
        </table>
      </div>
    </React.Fragment>
  );
};

export default ArrearTable;
