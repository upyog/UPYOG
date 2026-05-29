import React, { useEffect, useState } from "react";
import ApplicationTable from "./ApplicationTable";
import { CardSubHeader, DeleteIcon, Modal } from "@upyog/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
//Todo: Work in progress for cart details.
import "../css/ads-inline-auto.css";
const Close = () => <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>;
const CloseBtn = props => {
  return <div className="icon-bg-secondary" onClick={props.onClick}>
      <Close />
    </div>;
};
/**
 * ADSCartDetails Component renders a modal displaying a table of advertisement cart items, 
 * including details like ad type, area, lighting, booking date, price, and status. 
 * Users can remove items from the cart, and the total price is dynamically calculated 
 * and shown at the bottom. The modal includes a header with a title and close button.
 */

const ADSCartDetails = ({
  onClose,
  cartDetails,
  setCartDetails
}) => {
  const {
    t
  } = useTranslation();
  const {
    pathname: url
  } = useLocation();
  const handleDelete = index => {
    const updatedList = cartDetails.filter((_, idx) => idx !== index);
    setCartDetails(updatedList);
  };
  const calculateTotalPrice = () => {
    return cartDetails.reduce((total, item) => total + Number(item.price), 0);
  };
  const columns = [{
    Header: () => <div className="ads-auto-25">{t("S_NO")}</div>,
    // Use a function to render header with padding
    accessor: "sNo",
    Cell: ({
      row
    }) => <div className="ads-auto-26">
          {row.index + 1} {/* Display the row index + 1 for S.No */}
        </div>
  }, {
    Header: t("ADD_TYPE"),
    accessor: "addType"
  }, {
    Header: t("FACE_AREA"),
    accessor: "faceArea"
  }, {
    Header: t("ADS_NIGHT_LIGHT"),
    accessor: "nightLight",
    Cell: ({
      value
    }) => <div>{value ? t("Yes") : t("No")}</div>
  }, {
    Header: t("BOOKING_DATE"),
    accessor: "bookingDate"
  }, {
    Header: t("UNIT_PRICE"),
    accessor: "price"
  }, {
    Header: t("ADS_STATUS"),
    accessor: "status",
    Cell: ({
      value
    }) => <div className="sla-cell-success">{value}</div>
  }, {
    Header: t("DELETE_KEY"),
    accessor: "delete",
    Cell: ({
      row
    }) => <button onClick={() => handleDelete(row.index)}>
          <DeleteIcon className="delete ads-auto-27" fill="#a82227" />
        </button>
  }];
  return <Modal headerBarMain={<CardSubHeader className="ads-auto-28">My Cart</CardSubHeader>} headerBarEnd={<CloseBtn onClick={onClose} />} popupStyles={{
    backgroundColor: "#fff",
    position: 'relative',
    maxHeight: '80vh',
    width: '80%',
    overflowY: 'auto'
  }} popupModuleMianStyles={{
    padding: "10px"
  }} hideSubmit={true} headerBarMainStyle={{
    position: "sticky",
    top: 0,
    backgroundColor: "#f5f5f5"
  }} formId="modal-action">
      <ApplicationTable t={t} data={cartDetails} columns={columns} getCellProps={cellInfo => ({
      style: {
        minWidth: "150px",
        padding: "20px",
        fontSize: "16px"
      }
    })} isPaginationRequired={false} totalRecords={cartDetails.length} />
      <div className="ads-auto-29">
        Total Price: {calculateTotalPrice()} 
      </div>
    </Modal>;
};
export default ADSCartDetails;
