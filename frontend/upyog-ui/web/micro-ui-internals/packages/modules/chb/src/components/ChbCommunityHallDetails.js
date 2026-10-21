import React, { useState, useEffect } from 'react';
import { Modal, CardLabel, CardLabelDesc, CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components";

// Close button component
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
 * ChbCommunityHallDetails Component
 * 
 * This component is responsible for displaying the details of a specific community hall in the CHB (Community Hall Booking) module.
 * It fetches and displays information about the selected community hall, such as its name, description, and other metadata.
 * 
 * Props:
 * - `hallId`: The ID of the community hall whose details are to be displayed.
 * - `setShowDetails`: Function to toggle the visibility of the community hall details modal.
 * 
 * State Variables:
 * - `selectedHall`: State variable to store the details of the selected community hall.
 * - `showPopup`: State variable to manage the visibility of additional popups (if any).
 * 
 * Variables:
 * - `stateId`: The state ID fetched using the `Digit.ULBService.getStateId` function.
 * - `tenantId`: The tenant ID fetched using the `Digit.ULBService.getCitizenCurrentTenant` or `Digit.ULBService.getCurrentTenantId` function.
 * 
 * Hooks:
 * - `Digit.Hooks.useEnabledMDMS`: Custom hook to fetch MDMS (Master Data Management System) data for community halls.
 *    - Fetches the list of community halls for the given tenant ID.
 *    - Formats the fetched data for easier use in the component.
 * - `useEffect`: Fetches the details of the selected community hall when the `hallId` or `communityHalls` changes.
 * 
 * Logic:
 * - Fetches the list of community halls using the `useEnabledMDMS` hook.
 * - Filters the fetched data to find the details of the community hall with the given `hallId`.
 * - Updates the `selectedHall` state with the details of the selected community hall.
 * - Handles the visibility of the modal using the `setShowDetails` function.
 * 
 * Returns:
 * - A modal displaying the details of the selected community hall, including its name, description, and other metadata.
 * - Includes a close button to hide the modal.
 */
const ChbCommunityHallDetails = ({ hallId, setShowDetails }) => {
  const [selectedHall, setSelectedHall] = useState(null);
  const [showPopup, setShowPopup] = useState(false);
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  const { data: communityHalls } = Digit.Hooks.useEnabledMDMS(tenantId, "CHB", [{ name: "CommunityHalls" }], {
    select: (data) => {
      const formattedData = data?.["CHB"]?.["CommunityHalls"];
      return formattedData;
    },
  });

  useEffect(() => {
    let isMounted = true;
    if (hallId && communityHalls) {
      const hall = communityHalls.find(hall => hall.communityHallId === hallId);
      if (isMounted) {
        setSelectedHall(hall);
        setShowPopup(true);
      }
    }
    return () => {
      isMounted = false;
    };
  }, [hallId, communityHalls]);

  const handleClosePopup = () => {
    setShowPopup(!showPopup);
    setShowDetails(false);
  };

  const renderList = (text) => {
    return text
      .split('\n')
      .filter(line => line.trim() !== '')
      .map((line, index) => <li key={index} className="chb-chb-cancellation-policy-bottom-spacing">{line.trim()}</li>);
  };

  return (
    <div>
      {showPopup && selectedHall && (
        <Modal
          headerBarMain={<CardSubHeader className="chb-chb-community-hall-details-header">Community Hall Details</CardSubHeader>}
          headerBarEnd={<CloseBtn onClick={handleClosePopup} />}
          popupStyles={{ backgroundColor: "#fff", position: 'relative', width: '90%', maxWidth: '1200px', maxHeight: '90vh', overflowY: 'auto' }}
          children={
            <div className="chb-chb-community-hall-details-container-padding">
              <div className="chb-chb-community-hall-details-flex-container">
                <div className="chb-chb-community-hall-details-wrapper">
                  <CardLabel className="chb-chb-cancellation-policy-card">Name</CardLabel>
                  <CardLabelDesc>{selectedHall.name}</CardLabelDesc>
                </div>
                <div className="chb-chb-community-hall-details-wrapper">
                  <CardLabel className="chb-chb-cancellation-policy-card">Geo Location</CardLabel>
                  <CardLabelDesc>{selectedHall.geoLocation}</CardLabelDesc>
                </div>
                <div className="chb-chb-community-hall-details-wrapper">
                  <CardLabel className="chb-chb-cancellation-policy-card">Address</CardLabel>
                  <CardLabelDesc>{selectedHall.address}</CardLabelDesc>
                </div>
                <div className="chb-chb-community-hall-details-wrapper">
                  <CardLabel className="chb-chb-cancellation-policy-card">Contact Details</CardLabel>
                  <CardLabelDesc>{selectedHall.contactDetails}</CardLabelDesc>
                </div>
                <div className="chb-chb-community-hall-details-wrapper">
                  <CardLabel className="chb-chb-cancellation-policy-card">Description</CardLabel>
                  <CardLabelDesc>{selectedHall.hallDescription}</CardLabelDesc>
                </div>
                <div className="chb-chb-community-hall-details-wrapper">
                  <CardLabel className="chb-chb-cancellation-policy-card">Type</CardLabel>
                  <CardLabelDesc>{selectedHall.type}</CardLabelDesc>
                </div>
              </div>
              <CardLabel className="chb-chb-community-hall-details-card">Terms and Conditions</CardLabel>
              <CardLabelDesc>
                <ul>{renderList(selectedHall.termsAndCondition)}</ul>
              </CardLabelDesc>
              {/* <CardLabel className="custom-style">Disclaimer</CardLabel>
              <CardLabelDesc>{selectedHall.disclaimer}</CardLabelDesc>
              <CardLabel className="custom-style">Cancellation Policy</CardLabel>
              <CardLabelDesc>
                <ul>{renderList(selectedHall.cancellationPolicy)}</ul>
              </CardLabelDesc>
              <CardLabel className="custom-style">Remarks</CardLabel>
              <CardLabelDesc>{selectedHall.remarks}</CardLabelDesc> */}
            </div>
          }
          actionCancelLabel={null}  // Hide Cancel button
          actionCancelOnSubmit={null}  // No action for Cancel
          actionSaveLabel={null}  // Hide Save button
          actionSaveOnSubmit={null}  // No action for Save
          actionSingleLabel={null}  // Hide Submit button
          actionSingleSubmit={null}  // No action for Submit
          error={null}
          setError={() => {}}
          formId="modalForm"
          isDisabled={false}
          hideSubmit={true}  // Ensure submit is hidden
          popupModuleMianStyles={{ padding: "10px" }}
          headerBarMainStyle={{ position: "sticky",top: 0,backgroundColor: "#f5f5f5" }}
          isOBPSFlow={false}
          popupModuleActionBarStyles={{ display: 'none' }}  // Hide Action Bar
          isOpen={showPopup}  // Pass isOpen prop
          onClose={handleClosePopup}  // Pass onClose prop
        />
      )}
    </div>
  );
};

export default ChbCommunityHallDetails;
