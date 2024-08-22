import React from "react";
import { useTranslation } from "react-i18next";
import TraderNameField from "../commonFormFields/traderName";
import BrokerNameField from "../commonFormFields/brokerName";
import GawalNameField from "../commonFormFields/gawalName";
import RemovalDateField from "../commonFormFields/removalDate";
import RemovalTimeField from "../commonFormFields/removalTime";
import SubmitButtonField from "../commonFormFields/submitBtn";
import RemovalFeeAmountField from "../commonFormFields/removalFeeAmt";
import PaymentModeField from "../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";
import NumberOfAnimalsField from "../commonFormFields/numberOfAnimals";

const RejectedBefore = ({stage, control, data, setData, setValues}) => {
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
                  <SubmitButtonField control={control} setData={setData} data={data} />
                </React.Fragment>
              :
                <React.Fragment></React.Fragment>
            }
          
            {
              (stage === "COLLECTION_POINT") ?
                <React.Fragment>
                  <NumberOfAnimalsField control={control} setData={setData} data={data} setValues={setValues} source="removal" />
                  <RemovalFeeAmountField control={control} setData={setData} data={data} disabled={true} />
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

export default RejectedBefore;
