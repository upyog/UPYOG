import React, { useState } from 'react';
import {
  CardLabel,
  CardLabelDesc,
  CardSubHeader,
  Modal,
} from '@nudmcdgnpm/digit-ui-react-components';
import { useTranslation } from 'react-i18next';
import ApplicationTable from './ApplicationTable';

// Close button component
const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = ({ onClick }) => (
  <div className="icon-bg-secondary" onClick={onClick}>
    <Close />
  </div>
);

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
  const { t } = useTranslation();
  const [params] = Digit.Hooks.useSessionStorage("ADS_CREATE");
  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) || 
    Digit.ULBService.getCurrentTenantId();
  const { data: cancelpolicyData } = Digit.Hooks.useCustomMDMS(
    tenantId,
    'CHB',
    [{ name: 'CommunityHalls' }],
    {
      select: (data) => data?.['CHB']?.['CommunityHalls'] || [],
    }
  );

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
    { Header: t("NIGHT_LIGHT"), accessor: "nightLight" },
    { Header: t("BOOKING_DATE"), accessor: "bookingDate" },
    // { Header: t("TOTAL_PRICE"), accessor: "price" },
  ];


  const handleCartClick = () => {
    setShowViewCart((prev) => !prev);
  };

  const handleCancellationPolicyClick = () => {
    setShowCancellationPolicy((prev) => !prev);
  };

  const renderCancellationPolicy = (policy) => {
    return (
      <ol style={{ paddingLeft: '20px' }}>
        {policy
          .split('\n')
          .filter((line) => line.trim() !== '')
          .map((line, index) => (
            <li key={index} style={{ marginBottom: '10px' }}>
              <CardLabelDesc>{line.trim()}</CardLabelDesc>
            </li>
          ))}
      </ol>
    );
  };

  // Sample total booking amount
  const totalBookingAmount = 1500; // Replace with actual amount

  return (
    <div>

        <CardSubHeader style={{ color: '#a82227' }}>
            Cart Details
        </CardSubHeader>
        
        <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
           <div>
            <div
             onClick={handleCartClick}
            style={{
                cursor: 'pointer',
                color: '#a82227',
                fontSize: '20px',
                textDecoration: 'none',
                marginBottom: '10px', // Space below "View Cart"
            }}
            >
            View Cart
            </div>
            <div
            onClick={handleCancellationPolicyClick}
            style={{
                cursor: 'pointer',
                color: '#a82227',
                fontSize: '20px',
                textDecoration: 'none',
                marginBottom: '10px', // Space below "Terms and Conditions"
            }}
            >
            Terms and Conditions
            </div>
            </div>
            <div style={{ fontSize: '20px', color: '#a82227' }}>
            Total Booking Amount: <strong>{totalBookingAmount} INR</strong>
            </div>
        </div>
      {showCancellationPolicy && (
        <Modal
          headerBarMain={
            <CardSubHeader style={{ color: '#a82227', margin: '25px' }}>
              Terms and Conditions
            </CardSubHeader>
          }
          headerBarEnd={<CloseBtn onClick={handleCancellationPolicyClick} />}
          popupStyles={{
            backgroundColor: '#fff',
            position: 'relative',
            maxHeight: '90vh',
            width: '80%',
            overflowY: 'auto',
          }}
          children={
            <div>
              {cancelpolicyData.length > 0 ? (
                renderCancellationPolicy(cancelpolicyData[0].termsAndCondition)
              ) : (
                <CardLabel style={{ fontSize: '20px' }}>Loading...</CardLabel>
              )}
            </div>
          }
          actionCancelLabel={null}
          actionCancelOnSubmit={null}
          actionSaveLabel={null}
          actionSaveOnSubmit={null}
          actionSingleLabel={null}
          actionSingleSubmit={null}
          error={null}
          setError={() => {}}
          formId="modalForm"
          isDisabled={false}
          hideSubmit={true}
          style={{}}
          popupModuleMianStyles={{ padding: '10px' }}
          headerBarMainStyle={{ position: 'sticky', top: 0, backgroundColor: '#f5f5f5' }}
          isOBPSFlow={false}
          popupModuleActionBarStyles={{ display: 'none' }}
          isOpen={showCancellationPolicy}
          onClose={handleCancellationPolicyClick}
        />
      )}
      {showViewCart && (
        <Modal
        headerBarMain={<CardSubHeader style={{ color: '#a82227', margin: '25px' }}>MY_CART</CardSubHeader>}
        headerBarEnd={<CloseBtn onClick={handleCartClick} />}
        popupStyles={{ backgroundColor: "#fff", position: 'relative', maxHeight: '80vh', width: '80%', overflowY: 'auto' }}
        popupModuleMianStyles={{ padding: "10px" }}
        hideSubmit={true} 
        headerBarMainStyle={{ position: "sticky", top: 0, backgroundColor: "#f5f5f5" }}
        formId="modal-action"
      >
        <ApplicationTable
          t={t}
          data={params?.adslist?.cartDetails}
          columns={columns}
          getCellProps={(cellInfo) => ({
            style: {
              minWidth: "150px",
              padding: "20px",
              fontSize: "16px",
            },
          })}
          isPaginationRequired={false}
          totalRecords={params?.adslist?.cartDetails.length}
        />
      </Modal>
      )}
    </div>
  );
};

export default ADSCartAndCancellationPolicyDetails;
