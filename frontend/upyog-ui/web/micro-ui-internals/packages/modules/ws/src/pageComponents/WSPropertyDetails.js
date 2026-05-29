import { CardLabel, LabelFieldPair, LinkButton, Loader, TextInput } from "@upyog/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import "../css/ws-inline-auto.css";
const WSPropertyDetails = ({
  t,
  config,
  onSelect,
  userType,
  formData,
  setError,
  formState,
  clearErrors
}) => {
  const redirectBackUrl = `/upyog-ui/${userType}/ws/new-application`;
  const [propertyId, setPropertyId] = React.useState("");
  const tenantId = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code;
  const {
    isLoading: loading,
    data: propertyDetails
  } = Digit.Hooks.pt.usePropertySearch({
    filters: {
      propertyIds: formData?.cptId?.id
    },
    tenantId: tenantId
  }, {
    filters: {
      propertyIds: formData?.cptId?.id
    },
    tenantId: tenantId
  });
  const getPropertyAddress = () => {
    const property = propertyDetails?.Properties?.at(0);
    const doorNo = property?.doorNo;
    const street = property?.address?.street;
    const landMark = property?.address?.landmark;
    const locality = property?.address?.locality?.name;
    const city = property?.address?.city;
    const pinCode = property?.address?.pincode;
    return `${doorNo ? doorNo + ", " : ""}
      ${street ? street + ", " : ""}
      ${landMark ? landMark + ", " : ""}
      ${locality ? locality + ", " : ""}
      ${city ? city : ""}
      ${pinCode ? ", " + pinCode : ""}`;
  };
  if (loading) {
    return <Loader />;
  }
  return <React.Fragment>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(`PROPERTY_ID`)}`}</CardLabel>
        <div className="field ws-auto-135">
          <TextInput key={config.key} value={propertyId} onChange={e => {
          setPropertyId(e.target.value);
          onSelect(config.key, {
            id: e.target.value
          });
        }} className="ws-auto-136" />
          <button className="submit-bar" type="button" onClick={() => setPropertyId(propertyId)}>
            {`${t("PT_SEARCH")}`}
          </button>
        </div>
      </LabelFieldPair>
      <Link to={`/upyog-ui/employee/pt/search`}>
        <LinkButton label={t("CPT_SEARCH_PROPERTY")} className="ws-auto-137" />
      </Link>
      &nbsp; | &nbsp;
      <Link to={`/upyog-ui/employee/pt/new-application`}>
        <LinkButton label={t("CPT_CREATE_PROPERTY")} className="ws-auto-138" />
      </Link>
      {propertyDetails && propertyDetails?.Properties.length && <React.Fragment>
          <header className="card-section-header ws-auto-139">
            {t("PT_DETAILS")}
          </header>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{`${t(`PROPERTY_ID`)}`}</CardLabel>
            <div className="field">
              <p>{propertyDetails?.Properties[0]?.propertyId}</p>
            </div>
          </LabelFieldPair>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{`${t(`OWNER_NAME`)}`}</CardLabel>
            <div className="field">
              <p>{propertyDetails?.Properties[0]?.owners[0]?.name}</p>
            </div>
          </LabelFieldPair>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{`${t(`PROPERTY_ADDRESS`)}`}</CardLabel>
            <div className="field">
              <p>{getPropertyAddress()}</p>
            </div>
          </LabelFieldPair>
          <Link to={`/upyog-ui/employee/commonpt/view-property?propertyId=${propertyId}&tenantId=${tenantId}`}>
            <LinkButton label={t("CPT_COMPLETE_PROPERTY_DETAILS")} className="ws-auto-140" />
          </Link>
        </React.Fragment>}
    </React.Fragment>;
};
export default WSPropertyDetails;
