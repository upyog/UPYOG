import React, { useEffect, useState } from "react";
import ApplicationTable from "./ApplicationTable";
import { CardSubHeader, DeleteIcon, Modal } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
//Todo: Work in progress for cart details.
const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = (props) => {
  return (
    <div className="icon-bg-secondary" onClick={props.onClick}>
      <Close />
    </div>
  );
};
/**
 * ADSCartDetails component displays  a table of advertisement cart items,
 * allowing users to view, delete items, and see the total price of the cart.
 */

const ADSCartDetails = ({ onClose,cartDetails, setCartDetails }) => {
  const { t } = useTranslation();
  const { pathname: url } = useLocation();

  const handleDelete = (index) => {
    const updatedList = cartDetails.filter((_, idx) => idx !== index);
    setCartDetails(updatedList);
  };

  const calculateTotalPrice = () => {
    return cartDetails.reduce((total, item) => total + Number(item.price), 0);
  };

  const columns = [
    {
      Header: () => <div style={{ paddingLeft: "50px" }}>{t("S_NO")}</div>, // Use a function to render header with padding
      accessor: "sNo",
      Cell: ({ row }) => (
        <div style={{ paddingLeft: "50px" }}>
          {row.index + 1} {/* Display the row index + 1 for S.No */}
        </div>
      ),
    },
    { Header: t("ADD_TYPE"), accessor: "addType" },
    { Header: t("FACE_AREA"), accessor: "faceArea" },
    { Header: t("BOOKING_DATE"), accessor: "bookingDate" },
    { Header: t("TOTAL_PRICE"), accessor: "price" },
    {
      Header: t("DELETE_KEY"),
      accessor: "delete",
      Cell: ({ row }) => (
        <button onClick={() => handleDelete(row.index)}>
          <DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} />
        </button>
      ),
    },
  ];

  return (
    <Modal
      headerBarMain={<CardSubHeader style={{ color: '#a82227', margin: '25px' }}>MY_CART</CardSubHeader>}
      headerBarEnd={<CloseBtn onClick={onClose} />}
      popupStyles={{ backgroundColor: "#fff", position: 'relative', maxHeight: '80vh', width: '80%', overflowY: 'auto' }}
      popupModuleMianStyles={{ padding: "10px" }}
      hideSubmit={true} 
      headerBarMainStyle={{ position: "sticky", top: 0, backgroundColor: "#f5f5f5" }}
      formId="modal-action"
    >
      <ApplicationTable
        t={t}
        data={cartDetails}
        columns={columns}
        getCellProps={(cellInfo) => ({
          style: {
            minWidth: "150px",
            padding: "20px",
            fontSize: "16px",
          },
        })}
        isPaginationRequired={false}
        totalRecords={cartDetails.length}
      />
      <div style={{ padding: "20px", fontSize: "18px", fontWeight: "bold" }}>
        Total Price: {calculateTotalPrice()} 
      </div>
    </Modal>
  );
};

export default ADSCartDetails;