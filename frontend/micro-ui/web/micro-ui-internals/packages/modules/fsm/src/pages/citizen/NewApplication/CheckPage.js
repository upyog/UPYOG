import React from "react";
import {
  Card,
  CardCaption,
  CardHeader,
  CardLabel,
  CardSubHeader,
  StatusTable,
  Row,
  ActionLinks,
  LinkButton,
  SubmitBar,
  CardText,
  CitizenInfoLabel,
} from "@upyog/digit-ui-react-components";
import { useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Timeline from "../../../components/TLTimelineInFSM";
import { getAddress } from "@upyog/digit-ui-module-ws/src/utils";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();

  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

const CheckPage = ({ onSubmit, value }) => {
  const { t } = useTranslation();
  const history = useHistory();

  const { address, propertyID,  propertyType, subtype, pitType, pitDetail, selectGender, selectPaymentPreference, selectTripNo, roadWidth, distancefromroad } = value;

  const pitDetailValues = pitDetail ? Object.values(pitDetail).filter((value) => !!value) : null;

  const pitMeasurement = pitDetailValues?.reduce((previous, current, index, array) => {
    if (index === array.length - 1) {
      return previous + current + "m";
    } else {
      return previous + current + "m x ";
    }
  }, "");

  const getAddress=(address,t)=>{
    console.log("address",address)
    if(address?.gramPanchayat?.code =="OTH1")
    {
      return   `${address?.doorNo?.trim() ? `${address?.doorNo?.trim()}, ` : ""} ${address?.street?.trim() ? `${address?.street?.trim()}, ` : ""}${
        address?.propertyLocation?.code === "WITHIN_ULB_LIMITS" ? t(address?.locality?.i18nkey) : address?.newGramPanchayat
      },${t(address?.village?.code)}, ${t(address?.city.code)}`
    }
    else if(address?.propertyLocation?.code === "WITHIN_ULB_LIMITS" )
    {
      console.log("trueee", `${t(address?.locality?.code)}`)
    return `${t(address?.locality?.code)}`
    }
    else {
      return `${address?.doorNo?.trim() ? `${address?.doorNo?.trim()}, ` : ""} ${address?.street?.trim() ? `${address?.street?.trim()}, ` : ""}${
        address?.propertyLocation?.code === "WITHIN_ULB_LIMITS" ? t(address?.locality?.i18nkey) : address?.gramPanchayat?.i18nkey
      },${t(address?.village?.code)}, ${t(address?.city.code)}`
    }
  }
  return (
    <React.Fragment>
      <Timeline currentStep={4} flow="APPLY" />
      <Card>
        <CardHeader>{t("CS_CHECK_CHECK_YOUR_ANSWERS")}</CardHeader>
        <CardText>{t("CS_CHECK_CHECK_YOUR_ANSWERS_TEXT")}</CardText>
        <CardSubHeader>{t("CS_CHECK_PROPERTY_DETAILS")}</CardSubHeader>
        <StatusTable>
          {selectTripNo && selectTripNo?.tripNo && (
            <Row
              label={t("ES_FSM_ACTION_NUMBER_OF_TRIPS")}
              text={t(selectTripNo?.tripNo?.i18nKey)}
              actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/select-trip-number" />}
            />
          )}
          {selectTripNo && selectTripNo?.vehicleCapacity && (
            <Row
              label={t("ES_VEHICLE CAPACITY")}
              text={t(selectTripNo?.vehicleCapacity?.capacity)}
              actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/select-trip-number" />}
            />
          )}
          {selectGender && (
            <Row
              label={t("ES_FSM_ACTION_GENDER_TYPE")}
              text={t(selectGender?.i18nKey)}
              actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/select-gender" />}
            />
          )}
          <Row
            label={t("CS_CHECK_PROPERTY_ID")}
            text={value?.cptId?.id ? value?.cptId?.id : "NA"}
            actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/know-your-property" />}
          />
          <Row
            label={t("CS_CHECK_PROPERTY_TYPE")}
            text={t(propertyType?.i18nKey)}
            actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/property-type" />}
          />
          <Row
            label={t("CS_CHECK_PROPERTY_SUB_TYPE")}
            text={t(subtype?.i18nKey)}
            actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/property-subtype" />}
          />
          <Row
            label={t("CS_CHECK_ADDRESS")}
            text={getAddress(address,t)}
            actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/pincode" />}
          />
          {address?.landmark?.trim() && (
            <Row
              label={t("CS_CHECK_LANDMARK")}
              text={address?.landmark?.trim()}
              actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/landmark" />}
            />
          )}
          {address?.slumArea?.code === true && (
            <Row
              label={t("CS_APPLICATION_DETAILS_SLUM_NAME")}
              text={t(address?.slumData?.i18nKey)}
              actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/slum-details" />}
            />
          )}
          {pitType && (
            <Row
              label={t("CS_CHECK_PIT_TYPE")}
              text={t(pitType.i18nKey)}
              actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/pit-type" />}
            />
          )}
           
          <Row
            label={t("CS_CHECK_ROAD_WIDTH")}
            text={t(roadWidth?.roadWidth)}
            actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/road-details" />}
          />
          <Row
            label={t("CS_CHECK_DISTANCE_FROM_ROAD")}
            text={t(roadWidth?.distancefromroad)}
            actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/road-details" />}
          />

          {pitMeasurement && (
            <Row
              label={t("CS_CHECK_SIZE")}
              text={[pitMeasurement]}
              actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/tank-size" />}
            />
          )}
          {(selectPaymentPreference?.advanceAmount || selectPaymentPreference?.advanceAmount === 0) && (
            <Row
              label={t("ADV_AMOUNT")}
              text={"₹ " + t(selectPaymentPreference?.advanceAmount)}
              actionButton={<ActionButton jumpTo="/digit-ui/citizen/fsm/new-application/select-payment-preference" />}
            />
          )}
        </StatusTable>
        {/* <CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("CS_CHECK_INFO_TEXT")} /> */}
        <SubmitBar label={t("CS_COMMON_SUBMIT")} onSubmit={onSubmit} />
      </Card>
      {propertyType && (
        <CitizenInfoLabel
          style={{ marginTop: "8px", padding: "16px" }}
          info={t("CS_FILE_APPLICATION_INFO_LABEL")}
          text={t("CS_FILE_APPLICATION_INFO_TEXT", { content: t("CS_DEFAULT_INFO_TEXT"), ...propertyType })}
        />
      )}
    </React.Fragment>
  );
};

export default CheckPage;
