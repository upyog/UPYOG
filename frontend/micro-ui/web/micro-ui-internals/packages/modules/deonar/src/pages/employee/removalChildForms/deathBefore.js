import React from "react";
import { useTranslation } from "react-i18next";
import TraderNameField from "../commonFormFields/traderName";
import BrokerNameField from "../commonFormFields/brokerName";
import GawalNameField from "../commonFormFields/gawalName";
import RemovalDateField from "../commonFormFields/removalDate";
import RemovalTimeField from "../commonFormFields/removalTime";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";
import RemovalFeeAmountField from "../commonFormFields/removalFeeAmt";
import PaymentModeField from "../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../commonFormFields/paymentReferenceNumber";

const DeathBefore = ({stage, control, data, setData}) => {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <TraderNameField control={control} setData={setData} data={data} />
        <BrokerNameField control={control} setData={setData} data={data} />
        <GawalNameField control={control} setData={setData} data={data} />
            {
              (stage === "SECURITY_CHECKPOINT") ?
                <React.Fragment>
                  <RemovalDateField control={control} setData={setData} data={data} />
                  <RemovalTimeField control={control} setData={setData} data={data} />
                  <SubmitPrintButtonFields control={control} setData={setData} data={data} />
                </React.Fragment>
              :
              <React.Fragment></React.Fragment>
            }
              
            {
              (stage === "COLLECTION_POINT") ?
                <React.Fragment>
                  <RemovalFeeAmountField control={control} setData={setData} data={data} />
                  <PaymentModeField control={control} setData={setData} data={data} />
                  <PaymentReferenceNumberField control={control} setData={setData} data={data} />
                  <SubmitPrintButtonFields control={control} setData={setData} data={data} />
                </React.Fragment>
              :
                <React.Fragment></React.Fragment>
            }
            
      </div>
    </React.Fragment>
  );
};

export default DeathBefore;
