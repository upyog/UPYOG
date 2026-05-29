import React, { useState } from "react";
import { CardLabel, CardLabelDesc, CardSubHeader, Modal, CardText, DeleteIcon } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import ApplicationTable from "./ApplicationTable";

// Close button component
import "../css/ads-inline-auto.css";
const Close = () => <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>;
const CloseBtn = ({
  onClick
}) => <div className="icon-bg-secondary" onClick={onClick}>
    <Close />
  </div>;

/**
 * ADSCartAndCancellationPolicyDetails Component
 *
 * This component displays cart details and cancellation policy information for an ADS booking.
 * It features a toggle for viewing the cart and another for displaying the terms and conditions
 * associated with the booking. The component uses modals to present detailed information and
 */

const ADSCartAndCancellationPolicyDetails = () => {
  const [showCancellationPolicy, setShowCancellationPolicy] = useState(false);
  const [showViewCart, setShowViewCart] = useState(false);
  const {
    t
  } = useTranslation();
  const [params, setParams] = Digit.Hooks.useSessionStorage("ADS_CREATE", {});
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [showdemandEstimation, setShowDemandEstimation] = useState(false);
  const [showPriceBreakup, setShowPriceBreakup] = useState(false);
  const {
    data: cancelpolicyData
  } = Digit.Hooks.useCustomMDMS("pg", "Advertisement", [{
    name: "AdvServices"
  }], {
    select: data => data?.["Advertisement"]?.["AdvServices"] || []
  });
  const handleDelete = index => {
    // Make a shallow copy of the current params state to ensure immutability
    const updatedParams = {
      ...params
    };

    // Check if adslist exists and if cartDetails is an array
    if (updatedParams?.adslist?.cartDetails) {
      // Create a new array with the item at the given index removed
      updatedParams.adslist.cartDetails = updatedParams.adslist.cartDetails.filter((_, idx) => idx !== index);
    }

    // Update the state with the modified params
    setParams(updatedParams);
  };
  const columns = [{
    Header: () => <div className="ads-auto-3">{t("S_NO")}</div>,
    // Use a function to render header with padding
    accessor: "sNo",
    Cell: ({
      row
    }) => <div className="ads-auto-4">
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
    Header: t("DELETE_KEY"),
    accessor: "delete",
    Cell: ({
      row
    }) => <button onClick={() => handleDelete(row.index)}>
          <DeleteIcon className="delete ads-auto-5" fill="#a82227" />
        </button>
  }
  // { Header: t("TOTAL_PRICE"), accessor: "price" },
  ];
  let cartDetails = params?.adslist?.cartDetails.map(details => {
    return {
      addType: details.addTypeCode,
      faceArea: details.faceAreaCode,
      location: details.locationCode,
      nightLight: details.nightLight,
      bookingDate: details.bookingDate,
      bookingFromTime: "06:00",
      bookingToTime: "05:59",
      status: "BOOKING_CREATED"
    };
  });
  let formdata = {
    tenantId: tenantId,
    cartDetails: cartDetails
  };
  let mutation = Digit.Hooks.ads.useADSDemandEstimation();
  if (showdemandEstimation === false) {
    mutation.mutate(formdata);
    setShowDemandEstimation(true);
  }
  const handleCartClick = () => {
    setShowViewCart(prev => !prev);
  };
  const handlePriceBreakupClick = () => {
    setShowPriceBreakup(!showPriceBreakup);
  };
  const handleCancellationPolicyClick = () => {
    setShowCancellationPolicy(prev => !prev);
  };
  const renderCancellationPolicy = policy => {
    return <ol className="ads-auto-6">
        {policy.split("\n").filter(line => line.trim() !== "").map((line, index) => <li key={index} className="ads-auto-7">
              <CardLabelDesc>{line.trim()}</CardLabelDesc>
            </li>)}
      </ol>;
  };
  const calculateTotalAmount = CalculationType => {
    return CalculationType.reduce((total, item) => total + item.taxAmount, 0);
  };

  // Sample total booking amount
  const totalBookingAmount = mutation.data?.demands[0] && mutation.data?.demands[0]?.additionalDetails; // Replace with actual amount

  return <div>
      <CardSubHeader className="ads-auto-8">Cart Details</CardSubHeader>

      <div className="ads-auto-9">
        <div>
          <div onClick={handleCartClick} className="ads-auto-10">
            <div class="container" className="ads-auto-11">            <div className="ads-auto-12">
              <div className="ads-auto-13">
              <div> {params?.adslist?.cartDetails.length}</div>

              </div>
            </div>
          </div> 
            View Cart
            
          </div>
           
                  <div onClick={handleCancellationPolicyClick} className="ads-auto-14">
            Terms and Conditions
          </div>
        </div>
        <div onClick={handlePriceBreakupClick} className="ads-auto-15">
          Total Booking Amount: <strong>{totalBookingAmount} INR</strong>
        </div>
      </div>
      {showCancellationPolicy && <Modal headerBarMain={<CardSubHeader className="ads-auto-17">Terms and Conditions</CardSubHeader>} headerBarEnd={<CloseBtn onClick={handleCancellationPolicyClick} />} popupStyles={{
      backgroundColor: "#fff",
      position: "relative",
      maxHeight: "90vh",
      width: "80%",
      overflowY: "auto"
    }} children={<div>
              {cancelpolicyData.length > 0 ? renderCancellationPolicy(cancelpolicyData[0].termsAndCondition) : <CardLabel className="ads-auto-18">Loading...</CardLabel>}
            </div>} actionCancelLabel={null} actionCancelOnSubmit={null} actionSaveLabel={null} actionSaveOnSubmit={null} actionSingleLabel={null} actionSingleSubmit={null} error={null} setError={() => {}} formId="modalForm" isDisabled={false} hideSubmit={true} popupModuleMianStyles={{
      padding: "10px"
    }} headerBarMainStyle={{
      position: "sticky",
      top: 0,
      backgroundColor: "#f5f5f5"
    }} isOBPSFlow={false} popupModuleActionBarStyles={{
      display: "none"
    }} isOpen={showCancellationPolicy} onClose={handleCancellationPolicyClick} className="ads-auto-16" />}
      {showViewCart && <Modal headerBarMain={<CardSubHeader className="ads-auto-19">My Cart</CardSubHeader>} headerBarEnd={<CloseBtn onClick={handleCartClick} />} popupStyles={{
      backgroundColor: "#fff",
      position: "relative",
      maxHeight: "80vh",
      width: "80%",
      overflowY: "auto"
    }} popupModuleMianStyles={{
      padding: "10px"
    }} hideSubmit={true} headerBarMainStyle={{
      position: "sticky",
      top: 0,
      backgroundColor: "#f5f5f5"
    }} formId="modal-action">
          <ApplicationTable t={t} data={params?.adslist?.cartDetails} columns={columns} getCellProps={cellInfo => ({
        style: {
          minWidth: "150px",
          padding: "20px",
          fontSize: "16px"
        }
      })} isPaginationRequired={false} totalRecords={params?.adslist?.cartDetails.length} />
        </Modal>}
      {showPriceBreakup && <Modal headerBarMain={<CardSubHeader className="ads-auto-21">Price Breakup</CardSubHeader>} headerBarEnd={<CloseBtn onClick={handlePriceBreakupClick} />} popupStyles={{
      backgroundColor: "#fff",
      position: 'relative',
      maxHeight: '90vh',
      width: '60%',
      overflowY: 'auto'
    }} children={<div>
              <CardLabelDesc className="ads-auto-22">Estimate Price Details</CardLabelDesc>
              <ul>
                {mutation.data?.demands[0]?.demandDetails && mutation.data?.demands[0]?.demandDetails.map((demands, index) => <li key={index} className="ads-auto-23">
                    <CardText>{t(`${demands.taxHeadMasterCode}`)}</CardText>
                    <CardText>Rs {demands.taxAmount}</CardText>
                  </li>)}
              </ul>
              <hr />
              <div className="ads-auto-24">
                <CardLabelDesc>Total</CardLabelDesc>
                <CardLabelDesc>Rs {mutation.data?.demands[0]?.demandDetails && calculateTotalAmount(mutation.data?.demands[0]?.demandDetails)}</CardLabelDesc>
              </div>
            </div>} actionCancelLabel={null} // Hide Cancel button
    actionCancelOnSubmit={null} // No action for Cancel
    actionSaveLabel={null} // Hide Save button
    actionSaveOnSubmit={null} // No action for Save
    actionSingleLabel={null} // Hide Submit button
    actionSingleSubmit={null} // No action for Submit
    error={null} setError={() => {}} formId="modalForm" isDisabled={false} hideSubmit={true} // Ensure submit is hidden
    // popupModuleMianStyles={{ padding: "10px" }}
    headerBarMainStyle={{
      position: "sticky",
      top: 0,
      backgroundColor: "#f5f5f5"
    }} isOBPSFlow={false} popupModuleActionBarStyles={{
      display: 'none'
    }} // Hide Action Bar
    isOpen={showPriceBreakup} // Pass isOpen prop
    onClose={handlePriceBreakupClick} // Pass onClose prop
    className="ads-auto-20" />}
    </div>;
};
export default ADSCartAndCancellationPolicyDetails;
