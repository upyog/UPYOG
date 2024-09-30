import React, { useState, useEffect } from 'react';
import { Modal, CardLabel, CardLabelDesc, CardSubHeader } from '@nudmcdgnpm/digit-ui-react-components';

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

const ChbCommunityHallDetails = ({ hallId }) => {
  const [selectedHall, setSelectedHall] = useState(null);
  const [showPopup, setShowPopup] = useState(false);
  const stateId = Digit.ULBService.getStateId();

  const { data: communityHalls } = Digit.Hooks.useCustomMDMS(stateId, "CHB", [{ name: "CommunityHalls" }], {
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
    setShowPopup(false);
  };

  const renderList = (text) => {
    return text
      .split('\n')
      .filter(line => line.trim() !== '')
      .map((line, index) => <li key={index} style={{ marginBottom: '10px' }}>{line.trim()}</li>);
  };

  return (
    <div>
      {showPopup && selectedHall && (
        <Modal
          headerBarMain={<CardSubHeader style={{ color: '#a82227', margin: '35px' }}>Community Hall Details</CardSubHeader>}
          headerBarEnd={<CloseBtn onClick={handleClosePopup} />}
          popupStyles={{ backgroundColor: "#fff", position: 'relative', width: '90%', maxWidth: '1200px', maxHeight: '90vh', overflowY: 'auto' }}
          children={
            <div style={{ padding: '15px', paddingTop: '1px' }}>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                <div style={{ flex: '1 1 45%' }}>
                  <CardLabel style={{ fontSize: '20px' }}>Name</CardLabel>
                  <CardLabelDesc>{selectedHall.name}</CardLabelDesc>
                </div>
                <div style={{ flex: '1 1 45%' }}>
                  <CardLabel style={{ fontSize: '20px' }}>Geo Location</CardLabel>
                  <CardLabelDesc>{selectedHall.geoLocation}</CardLabelDesc>
                </div>
                <div style={{ flex: '1 1 45%' }}>
                  <CardLabel style={{ fontSize: '20px' }}>Address</CardLabel>
                  <CardLabelDesc>{selectedHall.address}</CardLabelDesc>
                </div>
                <div style={{ flex: '1 1 45%' }}>
                  <CardLabel style={{ fontSize: '20px' }}>Contact Details</CardLabel>
                  <CardLabelDesc>{selectedHall.contactDetails}</CardLabelDesc>
                </div>
                <div style={{ flex: '1 1 45%' }}>
                  <CardLabel style={{ fontSize: '20px' }}>Description</CardLabel>
                  <CardLabelDesc>{selectedHall.hallDescription}</CardLabelDesc>
                </div>
                <div style={{ flex: '1 1 45%' }}>
                  <CardLabel style={{ fontSize: '20px' }}>Type</CardLabel>
                  <CardLabelDesc>{selectedHall.type}</CardLabelDesc>
                </div>
              </div>
              <CardLabel style={{ fontSize: '20px', marginTop: '10px' }}>Terms and Conditions</CardLabel>
              <CardLabelDesc>
                <ul>{renderList(selectedHall.termsAndCondition)}</ul>
              </CardLabelDesc>
              <CardLabel style={{ fontSize: '20px', marginTop: '15px' }}>Disclaimer</CardLabel>
              <CardLabelDesc>{selectedHall.disclaimer}</CardLabelDesc>
              <CardLabel style={{ fontSize: '20px', marginTop: '15px' }}>Cancellation Policy</CardLabel>
              <CardLabelDesc>
                <ul>{renderList(selectedHall.cancellationPolicy)}</ul>
              </CardLabelDesc>
              <CardLabel style={{ fontSize: '20px', marginTop: '15px' }}>Remarks</CardLabel>
              <CardLabelDesc>{selectedHall.remarks}</CardLabelDesc>
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
          style={{}}
          popupModuleMianStyles={{ padding: "10px" }}
          headerBarMainStyle={{ backgroundColor: "#f5f5f5" }}
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
