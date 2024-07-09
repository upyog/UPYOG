import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
// import ProductListElement from "./EWASTEProductListElement";
import ApplicationTable from "./inbox/ApplicationTable";
import { Header, Button } from "@nudmcdgnpm/digit-ui-react-components";
import { LinkButton, SubmitBar, DeleteIcon, StatusTable, Row } from "@nudmcdgnpm/digit-ui-react-components";

const ProductList = ({ t, prlistName, setPrlistName, prlistQuantity, setPrlistQuantity, setCalculatedAmount }) => {
  const handleDelete = (e) => {
    const updatedList1 = [...prlistName];
    if (updatedList1.length != 0) {
      updatedList1.splice(e, 1);
      setPrlistName(updatedList1);
    }

    const updatedList2 = [...prlistQuantity];
    if (updatedList2.length != 0) {
      updatedList2.splice(e, 1);
      setPrlistQuantity(updatedList2);
    }
  };

  const productcolumns = [
    { Header: t("PRODUCT_NAME"), accessor: "name" },
    { Header: t("PRODUCT_QUANTITY"), accessor: "quantity" },
    { Header: t("UNIT_PRICE"), accessor: "unit_price" },
    { Header: t("TOTAL_PRODUCT_PRICE"), accessor: "total_price" },
    {
      Header: t("DELETE_KEY"), accessor: "delete", Cell: ({ row }) => (
        <button onClick={() => handleDelete(row.index)}><DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} /></button>
      ),
    },
  ];

  const productRows = prlistName?.map((product, index) => (
    {
      name: product.code,
      quantity: prlistQuantity[index].code,
      unit_price: product.price,
      total_price: product.price * prlistQuantity[index].code,
    }
  )) || [];

  const totalPrice = productRows.reduce((sum, pd) => sum + (pd.total_price || 0), 0);
  setCalculatedAmount(totalPrice);

  return (
    <div className="card">
      <ApplicationTable
        t={t}
        data={productRows}
        columns={productcolumns}
        getCellProps={(cellInfo) => ({
          style: {
            minWidth: "150px",
            padding: "20px",
            fontSize: "16px",
          },
        })}
        isPaginationRequired={false}
        totalRecords={productRows.length}
      />
      <br></br>

      <StatusTable style={{marginLeft: "20px"}}>
        <Row
          label={t("EWASTE_NET_PRICE")}
          text={totalPrice}
        />
      </StatusTable>

      {/* {prlistName?.map((p, index) => (
                <ProductListElement 
                key = {index}
                p = {p}
                quantity = {prlistQuantity[index]}
                price = {prlistTotalprice[index]}
                />
            ))} */}
    </div>
  );
};

export default ProductList;
