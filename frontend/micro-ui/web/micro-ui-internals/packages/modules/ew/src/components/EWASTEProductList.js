import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ProductListElement from "./EWASTEProductListElement";
import ApplicationTable from "./inbox/ApplicationTable";
import { Header, Button } from "@upyog/digit-ui-react-components";
import { LinkButton, SubmitBar, DeleteIcon } from "@upyog/digit-ui-react-components";

const ProductList = ({ t, prlistName, setPrlistName, prlistQuantity }) => {
  const handleDelete = (e) => {
    const updatedList = [...prlistName];
    if (updatedList.length != 0) {
      updatedList.splice(e, 1);
     setPrlistName(updatedList);
    }
  };

  const columns = [
    { Header: "PRODUCT_NAME", accessor: "name" },
    { Header: "PRODUCT_QUANTITY", accessor: "quantity" },
    { Header: "PRODUCT_PRICE", accessor: "price" },

    { Header: "DELETE_KEY", accessor: "delete", Cell: ({ row }) => (
      <button onClick={() => handleDelete(row.index)}><DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} /></button>
    ), },
  ];

    const data = prlistName?.map((p, index) => (
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
        data={data}
        columns={columns}
        getCellProps={(cellInfo) => ({
          style: {
            minWidth: "150px",
            padding: "20px",
            fontSize: "16px",
          },
        })}
        totalRecords={data.length}
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
