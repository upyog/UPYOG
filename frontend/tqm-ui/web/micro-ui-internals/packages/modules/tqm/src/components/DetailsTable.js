import React, { Fragment } from "react";
import { useTable } from "react-table";
import { useTranslation } from "react-i18next";
import { CardSubHeader } from "@egovernments/digit-ui-react-components";

const DetailsTable = ({ columnsData, rowsData, summaryRows, cardHeader }) => {
  const { t } = useTranslation();

  const columns = React.useMemo(() => columnsData, [t]);

  const data = React.useMemo(() => {
    return rowsData;
  }, [rowsData]);

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } = useTable({
    columns,
    data,
  });

  return (
    <>
      {cardHeader && <CardSubHeader style={cardHeader?.inlineStyles}>{cardHeader?.value}</CardSubHeader>}
      <div className="tqm-table-test-container">
        <table className="table" {...getTableProps()}>
          <thead>
            {headerGroups.map((headerGroup) => (
              <tr {...headerGroup.getHeaderGroupProps()} className="row">
                {headerGroup.headers.map((column) => (
                  <th {...column.getHeaderProps()} className="head">
                    {column.render("Header")}
                  </th>
                ))}
              </tr>
            ))}
          </thead>

          <tbody {...getTableBodyProps()}>
            {rows.map((row) => {
              prepareRow(row);
              return (
                <tr {...row.getRowProps()} className="row">
                  {row.cells.map((cell) => (
                    <td {...cell.getCellProps()} className="data">
                      {cell.render("Cell")}
                    </td>
                  ))}
                </tr>
              );
            })}

            {summaryRows && (
              <tr className="row">
                {summaryRows.map((cell, index) => (
                  <td className="data" style={index === 3 ? { borderRight: "0.5px solid #d6d5d4", textAlign: "right", fontWeight: 700 } : {}}>
                    {index === 4 ? <span className={cell === "Pass" ? "sla-cell-success" : "sla-cell-error"}> {cell} </span> : cell}
                  </td>
                ))}
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </>
  );
};

export default DetailsTable;
