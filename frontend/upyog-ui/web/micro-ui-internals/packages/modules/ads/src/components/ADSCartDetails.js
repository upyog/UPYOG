import React, { useEffect, useState } from "react";
import ApplicationTable from "./ApplicationTable";
import { CardSubHeader, DeleteIcon, Modal } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
//Todo: Work in progress for cart details.

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
  console.log("cartDetailscartDetails", cartDetails)
  const calculateTotalPrice = () => {
    return (cartDetails || []).reduce((total, item) => total + (Number(item?.price) || 0), 0);
  };
  const columns = [{
    Header: () => <div className="ads-cart-details-indent-50">{t("S_NO")}</div>,
    // Use a function to render header with padding
    accessor: "sNo",
    Cell: ({
      row
    }) => <div className="ads-cart-details-indent-50">
        {row.index + 1} {/* Display the row index + 1 for S.No */}
      </div>
  }, {
    Header: t("ADD_TYPE"),
    accessor: "addType"
  }, {
    Header: t("ADS_LOCATION"),
    accessor: "location"
  }, {
    Header: t("FACE_AREA"),
    accessor: "faceArea"
  }, {
    Header: t("ADS_NIGHT_LIGHT"),
    accessor: "nightLight",
    Cell: ({
      value
    }) => <div className="ads-cart-details-badge-neutral">{value ? t("Yes") : t("No")}</div>
  }, {
    Header: t("BOOKING_DATE"),
    accessor: "bookingDate"
  }, {
    Header: t("UNIT_PRICE"),
    accessor: "price",
    Cell: ({
      value
    }) => <div className="ads-cart-details-price-cell">₹ {value !== undefined && value !== null && !Number.isNaN(Number(value)) ? value : 0}</div>
  }, {
    Header: t("ADS_STATUS"),
    accessor: "status",
    Cell: ({
      value
    }) => <div className="sla-cell-success ads-cart-details-status-pill">{value}</div>
  }, {
    Header: t("DELETE_KEY"),
    accessor: "delete",
    Cell: ({
      row
    }) => <button type="button" className="ads-cart-details-delete-btn" onClick={() => handleDelete(row.index)}>
        <DeleteIcon className="delete ads-cart-details-icon-action-ml-md" fill="#a82227" />
      </button>
  }];
  return (
    <div className="ads-cart-details-modal-root">
      <Modal
        headerBarMain={<CardSubHeader className="ads-cart-details-icon-red-margin-lg">My Cart</CardSubHeader>}
        headerBarEnd={<CloseBtn onClick={onClose} />}
        popupStyles={{
          backgroundColor: "#fff",
          position: "relative",
          maxHeight: "85vh",
          width: "92%",
          maxWidth: "1140px",
          overflowY: "auto"
        }}
        popupModuleMianStyles={{
          padding: "0px"
        }}
        hideSubmit={true}
        headerBarMainStyle={{
          position: "sticky",
          top: 0,
          backgroundColor: "#f8fafc"
        }}
        formId="modal-action"
      >
        <div className="ads-cart-details-table-container">
          <ApplicationTable
            t={t}
            data={cartDetails}
            columns={columns}
            getCellProps={() => ({
              style: {
                padding: "14px 16px",
                fontSize: "14px"
              }
            })}
            isPaginationRequired={false}
            pageSizeLimit={Math.max(cartDetails?.length || 10, 10)}
            totalRecords={cartDetails.length}
          />
        </div>
        <div className="ads-cart-details-sticky-bottom-bold">
          <span>Total Price:</span>
          <span className="ads-cart-details-total-value">₹ {calculateTotalPrice()}</span>
        </div>
      </Modal>
    </div>
  );
};
export default ADSCartDetails;
