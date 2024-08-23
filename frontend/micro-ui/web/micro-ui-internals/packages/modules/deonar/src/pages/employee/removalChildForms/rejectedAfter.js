import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import ShopkeeperNameField from "../commonFormFields/shopkeeperName";
import DawanwalaNameField from "../commonFormFields/dawanwalaName";
import NumberOfAnimalsField from "../commonFormFields/numberOfAnimals";
import AnimalTokenNumberField from "../commonFormFields/animalTokenNumber";
import RemovalDateField from "../commonFormFields/removalDate";
import RemovalTimeField from "../commonFormFields/removalTime";
import SubmitButtonField from "../commonFormFields/submitBtn";
import RemovalFeeAmountField from "../commonFormFields/removalFeeAmt";
import PaymentModeField from "../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";

const RejectedAfter = ({stage, control, data, setData}) => {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="bmc-card-full">
          <ShopkeeperNameField control={control} setData={setData} data={data} />
          <DawanwalaNameField control={control} setData={setData} data={data} />
              {
                (stage === "COLLECTION_POINT") ?
                  <NumberOfAnimalsField control={control} data={data} setData={setData} disabled={false} />
                :
                  <React.Fragment></React.Fragment>
              }
              <AnimalTokenNumberField control={control} data={data} setData={setData} />
              {
                (stage === "SECURITY_CHECKPOINT") ?
                  <React.Fragment>
                    <RemovalDateField control={control} data={data} setData={setData} />
                    <RemovalTimeField control={control} data={data} setData={setData} />
                    <SubmitButtonField />
                  </React.Fragment>
                :
                  <React.Fragment></React.Fragment>
              }
            
              {
                (stage === "COLLECTION_POINT") ?
                  <React.Fragment>
                    <RemovalFeeAmountField control={control} data={data} setData={setData} />
                    <PaymentModeField control={control} data={data} setData={setData} />
                    <PaymentReferenceNumberField control={control} data={data} setData={setData} />
                    <SubmitPrintButtonFields />
                  </React.Fragment>
                :
                  <React.Fragment></React.Fragment>
              }
            
      </div>
    </React.Fragment>
  );
};

export default RejectedAfter;
