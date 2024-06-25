import React, { useState } from 'react';
import { PopUp } from '@upyog/digit-ui-react-components';

const ChbCancellationPolicy = () => {
  const [showCancellationPolicy, setShowCancellationPolicy] = useState(false);
  const [showPriceBreakup, setShowPriceBreakup] = useState(false);

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
    maxWidth: '500px',
    width: '90%',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    maxHeight: '80vh',
    overflowY: 'auto'
  };

  const closeButtonStyle = {
    background: 'none',
    border: 'none',
    color: 'black',
    fontSize: '16px',
    cursor: 'pointer',
    position: 'absolute',
    top: '10px',
    right: '10px'
  };
  const breakupContentStyle = {
    marginTop: '20px'
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


  return (
    <div >
      <div style={{color:'#FE7A51'}}>Total booking amount</div>
      <div style={{ display: 'flex'}}>
        <div style={{marginLeft:'30px', marginRight: '60px' }}>Rs 19350/-</div>
        <div onClick={handlePriceBreakupClick} style={{ cursor: 'pointer', margin: '0 20px',color:'#FE7A51'}}>VIEW ESTIMATE PRICE BREAKUP</div>
        <div onClick={handleCancellationPolicyClick} style={{ cursor: 'pointer', color: '#FE7A51' }}>
          VIEW CANCELLATION POLICY
        </div>
      </div>

      {showCancellationPolicy && (
        <PopUp onClose={handleCancellationPolicyClick}>
          <div style={popupContentStyle}>
            <button onClick={handleCancellationPolicyClick} style={closeButtonStyle}>Close</button>
            <h3>Cancellation Policy</h3>
            <p>When you cancel your booking</p>
            <p>You can cancel your application any time before the date of event. GST will not be refunded. Also, a percentage of rent is deducted based on when you cancel your application with respect to the date of event. The day range and percentage amount deducted from rent during refund is shown in the table below. Earlier you cancel, lesser will be the amount deducted. All other charges like Security Deposit, Electricity Charges, Water Charges and Conservation fees will be refunded.</p>
            <ul>
              <li>0 days - 19 days before the date of event: 50% will be deducted from rent</li>
              <li>20 days - 90 days before the date of event: 10% will be deducted from rent</li>
              <li>91 days - above before the date of event: 0% will be deducted from rent</li>
            </ul>
            <p>When Cantonment Board rejects your booking</p>
            <p>0% will be deducted from rent and GST is withheld during refund - if the Cantonment Board cancels your application when the documents submitted by you are found incorrect/invalid after verification (post booking) especially, in cases where you have falsely availed benefits offered by the Cantonment Board for certain specific categories (For example, for citizen of the Cantt Area, Retired employee of Cantonment Board etc.). You may book, again by submitting correct documents.</p>
            <p>When Cantonment Board cancels your booking due to some exigencies</p>
            <p>If the board cancels your application due to some exigencies, entire booking amount along with GST is refunded.</p>
          </div>
        </PopUp>
         )}
      {showPriceBreakup && (
        <PopUp onClose={handleCancellationPolicyClick}>
          <div style={popupContentStyle}>
          <button onClick={handlePriceBreakupClick} style={closeButtonStyle}>Close</button>
          <div>
            <h3>Calculation BreakUp</h3>
            <p>Estimate Price Details</p>
            <ul>
            <ul>
                <li style={listItemStyle}><span>Rent</span><span>7500</span></li>
                <li style={listItemStyle}><span>CGST on RENT</span><span>675</span></li>
                <li style={listItemStyle}><span>SGST on RENT</span><span>675</span></li>
                <li style={listItemStyle}><span>Security Deposit</span><span>7500</span></li>
                <li style={listItemStyle}><span>Water Charges</span><span>0</span></li>
                <li style={listItemStyle}><span>Electricity Charges</span><span>1500</span></li>
                <li style={listItemStyle}><span>Conservation Fee</span><span>1500</span></li>
              </ul>
            </ul>
            <hr/>
            <div style={totalStyle}>
              <span>Total</span>
              <span>Rs 19350</span>
            </div>
          </div>
        </div>
        </PopUp>
        )}
    </div>
  );
};

export default ChbCancellationPolicy;
