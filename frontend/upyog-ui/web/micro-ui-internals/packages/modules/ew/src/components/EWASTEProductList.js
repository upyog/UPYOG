import React, { useEffect } from "react";
import ApplicationTable from "./inbox/ApplicationTable";
import { DeleteIcon, StatusTable, Row } from "@upyog/digit-ui-react-components";

const ProductList = ({ t, prlistName, setPrlistName, prlistQuantity, setPrlistQuantity, setCalculatedAmount,calculatedAmount }) => {
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

  const handleIncrement = (index) => {
    const updatedQuantities = [...prlistQuantity];
    const currentQuantity = parseInt(updatedQuantities[index].code, 10);
    updatedQuantities[index].code = currentQuantity + 1;
    setPrlistQuantity(updatedQuantities);
  };

  const handleDecrement = (index) => {
    const updatedQuantities = [...prlistQuantity];
    if (updatedQuantities[index].code > 1) {
      updatedQuantities[index].code -= 1;
      setPrlistQuantity(updatedQuantities);
    }
  };

  const productcolumns = [
    { Header: t("PRODUCT_NAME"), accessor: "name" },
    { Header: t("PRODUCT_QUANTITY"), accessor: "quantity", 
      Cell: ({ row }) => (
        <div style={{ display: "flex", alignItems: "center" }}>
          <button style={{marginRight: "5px", borderRadius: "50%", paddingLeft: "11px", paddingRight: "11px", background: "#a82227", cursor: "pointer",fontSize: "18px",  color: "white" }} onClick={() => handleDecrement(row.index)} >-</button>
          {row.original.quantity}
          <button style={{marginLeft: "5px",  borderRadius: "50%", paddingLeft: "9px", paddingRight: "9px", background: "#a82227",fontSize: "18px", cursor: "pointer", color: "white"}} onClick={() => handleIncrement(row.index)}>+</button>
        </div>
      ) 
     },
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

  // Calculate the total price
  useEffect(() => {
    const totalPrice = productRows.reduce((sum, pd) => sum + (pd.total_price || 0), 0);
    setCalculatedAmount(totalPrice);
  }, [productRows, setCalculatedAmount]);

  if(prlistName.length>0){

  return (
    <div className="card">
      <ApplicationTable
        t={t}
        data={productRows}
        columns={productcolumns}
        getCellProps={() => ({
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
          text={<div style={{ marginLeft: "37%" }}>{"₹ " + calculatedAmount}</div>}
        />
      </StatusTable>
    </div>
  )}
  return null;
};

export default ProductList;
