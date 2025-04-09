// Importing necessary components and hooks from external libraries and local files
import React, { useEffect } from "react";
import ApplicationTable from "./inbox/ApplicationTable"; // Component for rendering a table
import { DeleteIcon, StatusTable, Row } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for delete icons, status tables, and rows

// Component to display and manage the list of products in the E-Waste module
const ProductList = ({ t, prlistName, setPrlistName, prlistQuantity, setPrlistQuantity, setCalculatedAmount, calculatedAmount }) => {
  
  // Function to handle the deletion of a product from the list
  const handleDelete = (e) => {
    const updatedList1 = [...prlistName];
    if (updatedList1.length != 0) {
      updatedList1.splice(e, 1); // Remove the product from the name list
      setPrlistName(updatedList1);
    }

    const updatedList2 = [...prlistQuantity];
    if (updatedList2.length != 0) {
      updatedList2.splice(e, 1); // Remove the product from the quantity list
      setPrlistQuantity(updatedList2);
    }
  };

  // Function to increment the quantity of a product
  const handleIncrement = (index) => {
    const updatedQuantities = [...prlistQuantity];
    const currentQuantity = parseInt(updatedQuantities[index].code, 10);
    updatedQuantities[index].code = currentQuantity + 1; // Increment the quantity
    setPrlistQuantity(updatedQuantities);
  };

  // Function to decrement the quantity of a product
  const handleDecrement = (index) => {
    const updatedQuantities = [...prlistQuantity];
    if (updatedQuantities[index].code > 1) {
      updatedQuantities[index].code -= 1; // Decrement the quantity if greater than 1
      setPrlistQuantity(updatedQuantities);
    }
  };

  // Configuration for the product table columns
  const productcolumns = [
    { Header: t("PRODUCT_NAME"), accessor: "name" }, // Column for product name
    {
      Header: t("PRODUCT_QUANTITY"), // Column for product quantity
      accessor: "quantity",
      Cell: ({ row }) => (
        <div style={{ display: "flex", alignItems: "center" }}>
          {/* Button to decrement the quantity */}
          <button
            style={{
              marginRight: "5px",
              borderRadius: "50%",
              paddingLeft: "11px",
              paddingRight: "11px",
              background: "#a82227",
              cursor: "pointer",
              fontSize: "18px",
              color: "white",
            }}
            onClick={() => handleDecrement(row.index)}
          >
            -
          </button>
          {row.original.quantity}
          {/* Button to increment the quantity */}
          <button
            style={{
              marginLeft: "5px",
              borderRadius: "50%",
              paddingLeft: "9px",
              paddingRight: "9px",
              background: "#a82227",
              fontSize: "18px",
              cursor: "pointer",
              color: "white",
            }}
            onClick={() => handleIncrement(row.index)}
          >
            +
          </button>
        </div>
      ),
    },
    { Header: t("UNIT_PRICE"), accessor: "unit_price" }, // Column for unit price
    { Header: t("TOTAL_PRODUCT_PRICE"), accessor: "total_price" }, // Column for total price
    {
      Header: t("DELETE_KEY"), // Column for delete action
      accessor: "delete",
      Cell: ({ row }) => (
        <button onClick={() => handleDelete(row.index)}>
          <DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} />
        </button>
      ),
    },
  ];

  // Mapping product data to rows for the table
  const productRows =
    prlistName?.map((product, index) => ({
      name: product.code,
      quantity: prlistQuantity[index].code,
      unit_price: product.price,
      total_price: product.price * prlistQuantity[index].code,
    })) || [];

  // Calculate the total price whenever the product rows change
  useEffect(() => {
    const totalPrice = productRows.reduce((sum, pd) => sum + (pd.total_price || 0), 0);
    setCalculatedAmount(totalPrice); // Update the calculated amount
  }, [productRows, setCalculatedAmount]);

  // Render the product list table if there are products
  if (prlistName.length > 0) {
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
        <br />

        {/* Display the total price in a status table */}
        <StatusTable style={{ marginLeft: "20px" }}>
          <Row
            label={t("EWASTE_NET_PRICE")}
            text={<div style={{ marginLeft: "37%" }}>{"₹ " + calculatedAmount}</div>}
          />
        </StatusTable>
      </div>
    );
  }

  return null; // Return null if there are no products
};

export default ProductList; // Exporting the component