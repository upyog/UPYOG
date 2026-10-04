import { Card, ButtonSelector } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";

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
      <div className="pt-property-invalid-mobile-number-full-width-flex">
        <ButtonSelector theme="border" label={t('PT_SKIP_N_CONTINUE')} onSubmit={skipNContinue} className="pt-property-invalid-mobile-number-btn" />
        <ButtonSelector label={t('PT_UPDATE_MOBILE_NO')} onSubmit={updateMobileNumber} className="pt-property-invalid-mobile-number-btn" />
      </div>
    </Card>
  </React.Fragment>;
};
export default PropertyInvalidMobileNumber;
