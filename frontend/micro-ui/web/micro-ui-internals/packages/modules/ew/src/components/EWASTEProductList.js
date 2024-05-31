import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ProductListElement from "./EWASTEProductListElement";
import ApplicationTable from "./inbox/ApplicationTable";
import { Header, Button } from "@upyog/digit-ui-react-components";
import { LinkButton, SubmitBar, DeleteIcon } from "@upyog/digit-ui-react-components";

const ProductList = ({ t, prlistName, setPrlistName, prlistQuantity, setPrlistQuantity }) => {
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
    { Header: "PRODUCT_NAME", accessor: "name" },
    { Header: "PRODUCT_QUANTITY", accessor: "quantity" },
    { Header: "PRODUCT_PRICE", accessor: "price" },
    { Header: "DELETE_KEY", accessor: "delete", Cell: ({ row }) => (
      <button onClick={() => handleDelete(row.index)}><DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} /></button>
    ), },
  ];

    const productRows = prlistName?.map((p, index) => (
        {
            name: p.code,
            quantity: prlistQuantity[index].code,
            price: p.price * prlistQuantity[index].code,
        }
    )) || [];


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
        totalRecords={productRows.length}
      />

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
