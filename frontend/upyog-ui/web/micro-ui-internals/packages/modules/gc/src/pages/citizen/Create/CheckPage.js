import React, { useState } from "react";
import {
  Card,
  CardHeader,
  CardSubHeader,
  CardText,
  CheckBox,
  Row,
  StatusTable,
  SubmitBar,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { checkForNA } from "../../../utils";

const GCCheckPage = ({ onSubmit, value = {}, renewApplication }) => {
  
  const { t } = useTranslation();
  const [agree, setAgree] = useState(false);

  const location = value?.gcpropertylocdetails || {};
  const specifications = value?.gcspecifications || {};
  const specialCategory = value?.gcspecialcategory?.specialCategory || value?.specialCategory || "";
  const documents = value?.gcdocuments?.documents || value?.documents || [];
  const isInheritance = value?.owner?.isInheritance || value?.gcspecifications?.isInheritance || false;
  const UserData = Digit.UserService.getUser();

  // Extract owners robustly based on how ApplicantDetails component saves the data
  let owners = [];
  if (Array.isArray(value?.owner)) {
    owners = value.owner;
  } else if (Array.isArray(value?.owner?.owner)) {
    owners = value.owner.owner;
  } else if (Array.isArray(value?.owner)) {
    owners = value.owner;
  } else if (value?.owner && typeof value.owner === 'object') {
    if (value.owner.name || value.owner.mobileNumber) {
      owners = [value.owner];
    } else {
      owners = Object.values(value.owner).filter(o => o?.name || o?.mobileNumber);
    }
  }

  const setDeclarationHandler = () => {
    setAgree(!agree);
  };

  const GCDocuments = Digit?.ComponentRegistryService?.getComponent("GCDocuments");

  return (
    <Card>
      <CardHeader>{t("GC_SUMMARY")}</CardHeader>

      <CardText>{t("GC_CHECK_YOUR_DETAILS")}</CardText>

      {/* Applicant Details */}
      {owners && owners.length > 0 && (
        <>
          <CardSubHeader style={{ fontSize: "24px" }}>
            {t("ES_APPLICANT_DETAILS")}
          </CardSubHeader>
          {owners.map((owner, index) => (
            <StatusTable key={index}>
              <Row label={t("GC_APPLICANT_NAME")} text={owner?.applicantName || owner?.name || t("CS_NA")} />
              <Row label={t("GC_MOBILE_NUMBER")} text={owner?.mobileNumber || t("CS_NA")} />
              <Row label={t("GC_ALT_MOBILE_NUMBER")} text={`${t(checkForNA(owner?.alternateNumber))}`} />
              <Row label={t("GC_EMAIL")} text={owner?.emailId || owner?.email || t("CS_NA")} />
            </StatusTable>
          ))}
        </>
      )}

      {/* Property Location Details */}
      <CardSubHeader style={{ fontSize: "24px" }}>
        {t("GC_PROPERTY_LOCATION_DETAILS")}
      </CardSubHeader>

      <StatusTable>
        <Row
          label={t("GC_PROPERTY_ID")}
          text={location?.propertyId || t("CS_NA")}
        />

        <Row
          label={t("GC_HOUSE_NO")}
          text={location?.houseNo || t("CS_NA")}
        />

        <Row
          label={t("GC_HOUSE_NAME")}
          text={location?.houseName || t("CS_NA")}
        />

        <Row
          label={t("GC_STREET_NAME")}
          text={location?.streetName || t("CS_NA")}
        />

        <Row
          label={t("GC_ADDRESS_LINE1")}
          text={location?.addressline1 || t("CS_NA")}
        />

        <Row
          label={t("GC_ADDRESS_LINE2")}
          text={location?.addressline2 || t("CS_NA")}
        />

        <Row
          label={t("GC_LANDMARK")}
          text={location?.landmark || t("CS_NA")}
        />

        <Row
          label={t("GC_CITY")}
          text={location?.city ? t(location?.city?.i18nKey) : t("CS_NA")}
        />

        <Row
          label={t("GC_LOCALITY")}
          text={location?.locality ? t(location?.locality?.i18nKey) : t("CS_NA")}
        />

        <Row
          label={t("GC_ADDRESS_PINCODE")}
          text={location?.pincode || t("CS_NA")}
        />
      </StatusTable>

      {/* Garbage Specifications */}
      <CardSubHeader style={{ fontSize: "24px" }}>
        {t("GC_GARBAGE_SPECIFICATIONS")}
      </CardSubHeader>

      <StatusTable>
        <Row
          label={t("GC_OLD_GARBAGE_ID")}
          text={specifications?.oldGarbageId || t("CS_NA")}
        />

        <Row
          label={t("GC_TYPE_OF_COLLECTION")}
          text={specifications?.typeOfCollection ? t(specifications?.typeOfCollection?.i18nKey) : t("CS_NA")}
        />

        <Row
          label={t("GC_OWNER_OR_TENANT")}
          text={specifications?.propertyOwnerType ? t(specifications?.propertyOwnerType?.i18nKey) : t("CS_NA")}
        />

        <Row
          label={t("GC_NAME")}
          text={specifications?.name || t("CS_NA")}
        />

        <Row
          label={t("GC_PHONE_NUMBER")}
          text={specifications?.phoneNumber || t("CS_NA")}
        />

        <Row
          label={t("GC_GENDER")}
          text={specifications?.gender ? t(specifications?.gender?.i18nKey) : t("CS_NA")}
        />

        <Row
          label={t("GC_EMAIL")}
          text={specifications?.email || t("CS_NA")}
        />

        <Row
          label={t("GC_CATEGORY")}
          text={specifications?.category ? t(specifications?.category?.i18nKey) : t("CS_NA")}
        />

        <Row
          label={t("GC_SUB_CATEGORY")}
          text={specifications?.subCategory ? t(specifications?.subCategory?.i18nKey) : t("CS_NA")}
        />

        <Row
          label={t("GC_SUB_CATEGORY_TYPE")}
          text={specifications?.subCategoryType ? t(specifications?.subCategoryType?.i18nKey) : t("CS_NA")}
        />

        <Row
          label={t("GC_IS_INHERITANCE")}
          text={isInheritance ? t("YES") : t("NO")}
        />

        <Row
          label={t("GC_SPECIAL_CATEGORY")}
          text={specialCategory ? t(specialCategory?.i18nKey) : t("CS_NA")}
        />

        {specifications?.isAdditional && (
          <>
            <Row
              label={t("GC_CALCULATION_TYPE")}
              text={
                specifications?.isbulkgeneration
                  ? t("GC_FIXED")
                  : specifications?.isVariableCalculation
                  ? t("GC_VARIABLE")
                  : t("CS_NA")
              }
            />

            {specifications?.isVariableCalculation && (
              <Row
                label={t("GC_NO_OF_UNITS")}
                text={specifications?.no_of_units || t("CS_NA")}
              />
            )}
          </>
        )}
      </StatusTable>

      {/* Garbage Documents */}
      <CardSubHeader style={{ fontSize: "24px" }}>
        {t("GC_GARBAGE_DOCUMENTS")}
      </CardSubHeader>

      <StatusTable>
        {documents?.length > 0 ? (
          documents.map((doc, index) => (
            <Row
              key={index}
              label={t(`GC_${doc?.documentType?.replace(/\./g, "_")}`)}
              text={<GCDocuments value={documents} Code={doc?.documentType} />}
            />
          ))
        ) : (
          <Row label={t("GC_GARBAGE_DOCUMENTS")} text={t("CS_NA")} />
        )}
      </StatusTable>

      <CheckBox
        label={t("GC_FINAL_DECLARATION_MESSAGE")}
        onChange={setDeclarationHandler}
        styles={{ height: "auto" }}
      />

      <SubmitBar
        label={t("GC_COMMON_BUTTON_SUBMIT")}
        onSubmit={onSubmit}
        disabled={!agree}
      />
    </Card>
  );
};

export default GCCheckPage;