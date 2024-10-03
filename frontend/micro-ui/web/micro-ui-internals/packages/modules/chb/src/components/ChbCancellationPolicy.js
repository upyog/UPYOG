import React, { useState } from 'react';
import { CardLabel, CardText, CardLabelDesc, CardSubHeader, Modal } from '@nudmcdgnpm/digit-ui-react-components';
import { useTranslation } from "react-i18next";
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

const ChbCancellationPolicy = ({ slotDetail }) => {
  const [showCancellationPolicy, setShowCancellationPolicy] = useState(false);
  const [showPriceBreakup, setShowPriceBreakup] = useState(false);
  const [showdemandEstimation,setShowDemandEstimation]=useState(false);
  const { t } = useTranslation();
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const { data: cancelpolicyData } = Digit.Hooks.useCustomMDMS(tenantId, "CHB", [{ name: "CommunityHalls" }],
    {
      select: (data) => {
        const formattedData = data?.["CHB"]?.["CommunityHalls"];
        return formattedData;
      },
  });
  const DateConvert = (date) => {
    if (!date) return "";
    const [day, month, year] = date.split("-");
    return `${year}-${month}-${day}`; // For the <input type="date" /> format
  };
  let hallDetails = slotDetail.map((slot) => {
    return { 
      hallCode: slot.hallCode1,
      bookingDate:DateConvert(slot.bookingDate),
      bookingFromTime:slot.fromTime,
      bookingToTime:slot.toTime,
      status:"BOOKING_CREATED",
      capacity:slot.capacity
    }; });
  const mutation = Digit.Hooks.chb.useDemandEstimation();
      let formdata = {
        tenantId:tenantId,
        bookingSlotDetails:hallDetails,
        communityHallCode:slotDetail[0].code
        
      };

  if(showdemandEstimation===false){
    mutation.mutate(formdata);
    setShowDemandEstimation(true);
  }
  const handleCancellationPolicyClick = () => {
    setShowCancellationPolicy(!showCancellationPolicy);
  };

  const handlePriceBreakupClick = () => {
    setShowPriceBreakup(!showPriceBreakup);
  };

  const renderCancellationPolicy = (policy) => {
    const policyLines = policy
      .split('\n')
      .filter(line => line.trim() !== '')
      .map((line, index) => `${line.trim()}`);

    return (
      <ol style={{ paddingLeft: '20px' }}>
        {policyLines.map((line, index) => (
          <li key={index} style={{ marginBottom: '10px' }}><CardLabelDesc>{line}</CardLabelDesc></li>
        ))}
      </ol>
    );
  };

  const calculateTotalAmount = (CalculationType) => {
    return CalculationType.reduce((total, item) => total + item.taxAmount, 0);
  };
  return (
    <div>
      <CardSubHeader style={{ color: '#a82227', fontSize: '18px'}}>
        Total Booking Amount
      </CardSubHeader>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <div style={{ marginLeft: '30px', marginRight: '60px', fontSize: '16px', fontWeight: 'bold' }}>
          Rs {mutation.data?.demands[0]?.demandDetails ? calculateTotalAmount(mutation.data?.demands[0]?.demandDetails) : 'Loading...'} /-
        </div>
        <div 
          onClick={handlePriceBreakupClick} 
          style={{ cursor: 'pointer', margin: '0 18px', color: '#a82227', fontSize: '20px', textDecoration: 'none' }}
        >
          Estimate Price Breakup
        </div>
        <div 
          onClick={handleCancellationPolicyClick} 
          style={{ cursor: 'pointer', color: '#a82227', fontSize: '20px', textDecoration: 'none' }}
        >
          Terms and Conditions
        </div>
      </div>

      {showCancellationPolicy && (
        <Modal
          headerBarMain={<CardSubHeader style={{ color: '#a82227', margin: '25px' }}>Terms and Conditions</CardSubHeader>}
          headerBarEnd={<CloseBtn onClick={handleCancellationPolicyClick} />}
          popupStyles={{ backgroundColor: "#fff", position: 'relative', maxHeight: '90vh', width: '80%', overflowY: 'auto' }}
          children={
            <div>
              {cancelpolicyData ? (
                <div>
                  {renderCancellationPolicy(cancelpolicyData[0].termsAndCondition)}
                </div>
              ) : (
                <CardLabel style={{ fontSize: '20px' }}>Loading...</CardLabel>
              )}
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
          headerBarMainStyle={{position: "sticky",top: 0, backgroundColor: "#f5f5f5" }}
          isOBPSFlow={false}
          popupModuleActionBarStyles={{ display: 'none' }}  // Hide Action Bar
          isOpen={showCancellationPolicy}  // Pass isOpen prop
          onClose={handleCancellationPolicyClick}  // Pass onClose prop
        />
      )}
      {showPriceBreakup && (
        <Modal
          headerBarMain={<CardSubHeader style={{ color: '#a82227', margin: '25px' }}>Price Breakup</CardSubHeader>}
          headerBarEnd={<CloseBtn onClick={handlePriceBreakupClick} />}
          popupStyles={{ backgroundColor: "#fff", position: 'relative', maxHeight: '90vh', width: '60%', overflowY: 'auto' }}
          children={
            <div>
              <CardLabelDesc style={{ marginBottom: '15px' }}>Estimate Price Details</CardLabelDesc>
              <ul>
                {mutation.data?.demands[0]?.demandDetails && mutation.data?.demands[0]?.demandDetails.map((demands, index) => (
                  <li key={index} style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <CardText>{t(`${demands.taxHeadMasterCode}`)}</CardText>
                    <CardText>Rs {demands.taxAmount}</CardText>
                  </li>
                ))}
              </ul>
              <hr />
              <div style={{ fontWeight: 'bold', marginTop: '10px', display: 'flex', justifyContent: 'space-between' }}>
                <CardLabelDesc>Total</CardLabelDesc>
                <CardLabelDesc>Rs {mutation.data?.demands[0]?.demandDetails && calculateTotalAmount(mutation.data?.demands[0]?.demandDetails)}</CardLabelDesc>
              </div>
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
          // popupModuleMianStyles={{ padding: "10px" }}
          headerBarMainStyle={{position: "sticky",top: 0, backgroundColor: "#f5f5f5" }}
          isOBPSFlow={false}
          popupModuleActionBarStyles={{ display: 'none' }}  // Hide Action Bar
          isOpen={showPriceBreakup}  // Pass isOpen prop
          onClose={handlePriceBreakupClick}  // Pass onClose prop
        />
      )}
    </div>
  );
};

export default ChbCancellationPolicy;
