import React, { useState } from 'react';
import { PopUp } from '@nudmcdgnpm/digit-ui-react-components';

const ChbCancellationPolicy = () => {
  const [showCancellationPolicy, setShowCancellationPolicy] = useState(false);
  const [showPriceBreakup, setShowPriceBreakup] = useState(false);
  const stateId = Digit.ULBService.getStateId();

  const { data: cancelpolicyData } = Digit.Hooks.useCustomMDMS(stateId, "CHB", [{ name: "CommunityHalls" }],
    {
      select: (data) => {
        const formattedData = data?.["CHB"]?.["CommunityHalls"];
        return formattedData;
      },
  });

  const { data: CalculationType } = Digit.Hooks.useCustomMDMS(stateId, "CHB", [{ name: "CalculationType" }],
    {
      select: (data) => {
        const formattedData = data?.["CHB"]?.["CalculationType"];
        return formattedData;
      },
  });

  const handleCancellationPolicyClick = () => {
    setShowCancellationPolicy(!showCancellationPolicy);
  };

  const handlePriceBreakupClick = () => {
    setShowPriceBreakup(!showPriceBreakup);
  };

  const popupContentStyle = {
    backgroundColor: 'white',
    color: 'black',
    padding: '20px',
    borderRadius: '8px',
    maxWidth: '700px',
    width: '90%',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    maxHeight: '90vh',
    overflowY: 'auto'
  };

  const closeButtonStyle = {
    background: 'none',
    border: 'none',
    color: 'black',
    fontSize: '25px',
    cursor: 'pointer',
    position: 'absolute',
    top: '10px',
    right: '10px'
  };

  const listItemStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    marginBottom: '5px'
  };

  const totalStyle = {
    fontWeight: 'bold',
    marginTop: '10px',
    display: 'flex',
    justifyContent: 'space-between'
  };

  const renderCancellationPolicy = (policy) => {
    const policyLines = policy
      .split('\n')
      .filter(line => line.trim() !== '')
      .map((line, index) => `${index + 1}. ${line.trim()}`);

    return (
      <ol>
        {policyLines.map((line, index) => (
          <li key={index}>{line}</li>
        ))}
      </ol>
    );
  };

  const calculateTotalAmount = (CalculationType) => {
    return CalculationType.reduce((total, item) => total + item.amount, 0);
  };

  return (
    <div>
      <div style={{ color: '#FE7A51' }}>Total booking amount</div>
      <div style={{ display: 'flex' }}>
        <div style={{ marginLeft: '30px', marginRight: '60px' }}>
          Rs {CalculationType ? calculateTotalAmount(CalculationType) : 'Loading...'} /-
        </div>
        <div onClick={handlePriceBreakupClick} style={{ cursor: 'pointer', margin: '0 20px', color: '#FE7A51' }}>VIEW ESTIMATE PRICE BREAKUP</div>
        <div onClick={handleCancellationPolicyClick} style={{ cursor: 'pointer', color: '#FE7A51' }}>
          VIEW CANCELLATION POLICY
        </div>
      </div>

      {showCancellationPolicy && (
        <PopUp onClose={handleCancellationPolicyClick}>
          <div style={popupContentStyle}>
            <button onClick={handleCancellationPolicyClick} style={closeButtonStyle}>&times;</button>
            {cancelpolicyData ? (
              <div>
                <h3>Cancellation Policy</h3>
                {renderCancellationPolicy(cancelpolicyData[0].cancellationPolicy)}
              </div>
            ) : (
              <p>Loading...</p>
            )}
          </div>
        </PopUp>
      )}
      {showPriceBreakup && (
        <PopUp onClose={handlePriceBreakupClick}>
          <div style={popupContentStyle}>
            <button onClick={handlePriceBreakupClick} style={closeButtonStyle}>&times;</button>
            <div>
              <h3>Calculation BreakUp</h3>
              <p>Estimate Price Details</p>
              <ul>
                {CalculationType && CalculationType.map((finance, index) => (
                  <li key={index} style={listItemStyle}>
                    <span>{finance.feeType}</span>
                    <span>{finance.amount}</span>
                  </li>
                ))}
              </ul>
              <hr />
              <div style={totalStyle}>
                <span>Total</span>
                <span>Rs {CalculationType && calculateTotalAmount(CalculationType)}</span>
              </div>
            </div>
          </div>
        </PopUp>
      )}
    </div>
  );
};

export default ChbCancellationPolicy;
