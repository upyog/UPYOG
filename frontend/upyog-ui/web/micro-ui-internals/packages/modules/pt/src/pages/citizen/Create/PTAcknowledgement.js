import { Banner, Card, CardText, LinkButton, Loader, Row, StatusTable, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import getPTAcknowledgementData from "../../../getPTAcknowledgementData";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  const isEditOrMutation = window.location.href.includes("edit-application") || window.location.href.includes("property-mutation");
  if (props.isSuccess) {
    return !isEditOrMutation ? t("CS_PROPERTY_APPLICATION_SUCCESS") : t("CS_PROPERTY_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    return !isEditOrMutation ? t("CS_PROPERTY_APPLICATION_PENDING") : t("CS_PROPERTY_UPDATE_APPLICATION_PENDING");
  } else {
    return !isEditOrMutation ? t("CS_PROPERTY_APPLICATION_FAILED") : t("CS_PROPERTY_UPDATE_APPLICATION_FAILED");
  }
};
const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between"
};
const BannerPicker = props => {
  return <Banner message={GetActionMessage(props)} applicationNumber={props.data?.Properties[0].acknowldgementNumber} info={props.isSuccess ? props.t("PT_APPLICATION_NO") : ""} successful={props.isSuccess} className="pt-auto-104" />;
};

/**
 * PTAcknowledgement is a pure display component.
 * The create/update API is called in Create/index.js.
 * ackData comes from sessionStorage (via parent) so refresh still shows the banner.
 */
const PTAcknowledgement = ({ ackData, isPending, error, onSuccess }) => {
  const { t } = useTranslation();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const isSuccess = !!ackData?.Properties?.length;
  // Show loader only while pending AND we don't have cached data yet
  const showLoader = isPending && !ackData;

  const handleDownloadPdf = async () => {
    const { Properties = [] } = ackData;
    let Property = (Properties && Properties[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === Property.tenantId);
    const tenantId = Property.tenantId;
    const propertyDetails = await Digit.PTService.search({ tenantId, filters: { propertyIds: Property?.propertyId, status: "INACTIVE" } });
    Property.transferorDetails = propertyDetails?.Properties?.[0] || [];
    Property.isTransferor = true;
    Property.transferorOwnershipCategory = propertyDetails?.Properties?.[0]?.ownershipCategory;
    const data = await getPTAcknowledgementData({ ...Property }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  if (showLoader) return <Loader />;

  const isEditOrMutation = window.location.href.includes("edit-application") || window.location.href.includes("property-mutation");

  return (
    <Card>
      <BannerPicker t={t} data={ackData} isSuccess={isSuccess} isLoading={isPending} />

      {/* Success/failure message — different text for new vs edit/mutation */}
      {isSuccess && (
        <CardText>{isEditOrMutation ? t("CS_FILE_PROPERTY_UPDATE_RESPONSE") : t("CS_FILE_PROPERTY_RESPONSE")}</CardText>
      )}
      {!isSuccess && !isPending && (
        <CardText>
          {isEditOrMutation ? t("CS_FILE_PROPERTY_UPDATE_FAILED_RESPONSE") : t("CS_FILE_PROPERTY_FAILED_RESPONSE")}
          {error?.response?.data?.Errors?.[0]?.message ? `. ${error.response.data.Errors[0].message}` : ""}
        </CardText>
      )}

      {/* Property ID row — only for new applications */}
      {!isEditOrMutation && (
        <StatusTable>
          {isSuccess && (
            <Row
              rowContainerStyle={rowContainerStyle}
              last
              label={t("PT_COMMON_TABLE_COL_PT_ID")}
              text={ackData?.Properties?.[0]?.propertyId}
              textStyle={{ whiteSpace: "pre", width: "60%" }}
            />
          )}
        </StatusTable>
      )}

      {/* Download button — shown for ALL flows on success */}
      {isSuccess && <SubmitBar label={t("PT_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}

      <Link to={`/upyog-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} onClick={onSuccess} />
      </Link>
    </Card>
  );
};
export default PTAcknowledgement;
