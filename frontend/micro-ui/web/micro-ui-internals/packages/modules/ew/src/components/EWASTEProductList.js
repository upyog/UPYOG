import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ProductListElement from "./EWASTEProductListElement";
import ApplicationTable from "./inbox/ApplicationTable";
import { Header } from "@egovernments/digit-ui-react-components";


const ProductList = ({ t, prlistName, prlistQuantity, prlistTotalprice }) => {

    const columns = [
        { Header: "Product Name", accessor: "name" },
        { Header: "Product Quantity", accessor: "quantity" },
        { Header: "Product Price", accessor: "price" }
    ]

    const data = prlistName?.map((p, index) => (
        {
            name: p.code,
            quantity: prlistQuantity[index].code,
            price: prlistTotalprice[index].code
        }
    )) || [];

    // const data = [
    //     {
    //         name: "pjdojaowe",
    //         quantity: 5,
    //         price: 45
    //     },
    // ]

    return (
        <div className="card">
            {/* {prlistName?.map((p, index) => (
                <ProductListElement 
                key = {index}
                p = {p}
                quantity = {prlistQuantity[index]}
                price = {prlistTotalprice[index]}
                />
            ))} */}

            
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
        </div>
    );
};

export default ProductList;