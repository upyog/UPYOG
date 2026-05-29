import { Card, ButtonSelector } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import "../../../css/pt-inline-auto.css";
const PropertyInvalidMobileNumber = ({
  userType,
  propertyId: propertyIdFromProp,
  skipNContinue,
  updateMobileNumber
}) => {
  const {
    t
  } = useTranslation();
  const {
    propertyIds
  } = useParams();
  return <React.Fragment>
      <Card>
        <div>
          <p>{t('PT_INVALID_OWNERS_MOBILE_NO')}</p>
        </div>
        <div className="pt-auto-114">
          <ButtonSelector theme="border" label={t('PT_SKIP_N_CONTINUE')} onSubmit={skipNContinue} className="pt-auto-115" />
          <ButtonSelector label={t('PT_UPDATE_MOBILE_NO')} onSubmit={updateMobileNumber} className="pt-auto-116" />
        </div>
      </Card>
    </React.Fragment>;
};
export default PropertyInvalidMobileNumber;
