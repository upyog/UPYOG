import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ProductListElement from "./EWASTEProductListElement";
import ApplicationTable from "./inbox/ApplicationTable";
import { Header } from "@egovernments/digit-ui-react-components";
import { LinkButton, SubmitBar } from "@upyog/digit-ui-react-components";

const ProductList = ({ t, prlistName, prlistQuantity, prlistTotalprice }) => {
  const [productList, setProductList] = useState([]);

  useEffect(() => {
    if (prlistName) {
      setProductList([...prlistName]);
    }
  }, [prlistName]);

//   console.log("this is product list state in product list component :: ", productList)
  // const handleDelete = (e) => {
  //   const updatedList = [...prlistName];
  //   if (updatedList.length != 0) {
  //     updatedList.splice(e, 1);
  //     setProductList(updatedList);
  //    // setPrlistName(updatedList);
  //   }
  // };

  // const handleDelete = (e) => {
  //   console.log("delete button clicked")
  //   // const updatedList = [...productList];
  //   // productList?.map((pr, index) => {
  //   //     if (e == pr.code){
  //   //         updatedList.splice(index, 1);
  //   //         setProductList(updatedList);
  //   //     }
  //   // })
  // };

    const columns = [
        { Header: "Product Name", accessor: "name" },
        { Header: "Product Quantity", accessor: "quantity" },
        { Header: "Product Price", accessor: "price" },

    ]

    const data = prlistName?.map((p, index) => (
        {
            name: p.code,
            quantity: prlistQuantity[index].code,
            price: prlistTotalprice[index].code,
            
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
