import { Banner, Card, CardText, LinkButton, Loader, Row, StatusTable, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";
import { CND_VARIABLES } from "../../../utils";
import cndAcknowledgementData from "../../../utils/cndAcknowledgementData";
import { cndStyles } from "../../../utils/cndStyles";

/**
 * Acknowledgement page which will render when form is submitted, it will show
 * Download Acknowledement button as well as Application Number.
 */

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return t("CND_SUCCESS_MESSAGE");
  } else if (props.isLoading) {
    return t("CND_APP_PENDING_MESSAGE");
  } else if (!props.isSuccess) {
    return t("CND_APP_FAILED_MESSAGE");
  }
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props.data?.cndApplicationDetails?.applicationNumber}
      info={props.isSuccess ? props.t("CND_APPLICATION_NO") : ""}
      successful={props.isSuccess}
      style={cndStyles.successBar}
    />
  );
};

const CndAcknowledgement = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const { state } = useLocation();

  const applicationNumber = Digit.Utils.GetParamFromUrl("applicationNumber");
  const tenantIdParam = Digit.Utils.GetParamFromUrl("tenantId");

  const { data: searchResult, isLoading: isSearchLoading } = Digit.Hooks.cnd.useCndSearchApplication(
    {
      tenantId: tenantIdParam || tenantId,
      filters: { applicationNumber },
    },
    {
      enabled: !state && !!applicationNumber,
    }
  );

  const { data: stateData, isSuccess: stateSuccess, error, isLoading: stateLoading = false } = state || {};
  const ackData = stateData || (searchResult?.cndApplicationDetail?.[0] ? { cndApplicationDetails: searchResult.cndApplicationDetail[0] } : null);

  //after submitting the application form, when someone click back button it will take user to Home instead of previous page 
  Digit.Hooks.useCustomBackNavigation({
    redirectPath: CND_VARIABLES.HOME_PATH
  })

  const isSuccess = stateSuccess || !!ackData?.cndApplicationDetails;
  const isLoading = stateLoading || isSearchLoading;
  const showLoader = isLoading && !ackData;


  const handleDownloadPdf = async () => {
    const { cndApplicationDetails } = ackData || {};
    let Cnd = cndApplicationDetails || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === Cnd.tenantId);
    const pdfData = await cndAcknowledgementData({ ...Cnd }, tenantInfo, t);
    Digit.Utils.pdf.generateTable(pdfData);
  };

  if (showLoader) {
    return <Loader />;
  }

  return (
    <Card>
      <BannerPicker t={t} data={ackData} isSuccess={isSuccess} isLoading={isLoading} />
      {isSuccess && (
        <CardText>{t("CND_FILE_RESPONSE_MESSAGE")}</CardText>
      )}
      {!isSuccess && !isLoading && (
        <CardText>
          {t("CND_FILE_FAILED_RESPONSE_MESSAGE")}
          {error?.response?.data?.Errors?.[0]?.message ? `. ${error.response.data.Errors[0].message}` : ""}
        </CardText>
      )}
      <StatusTable>
        {isSuccess && (
          <Row
            rowContainerStyle={cndStyles.rowContainerStyle}
            last
            textStyle={cndStyles.textStyle}
          />
        )}
      </StatusTable>
      {isSuccess && <SubmitBar label={t("CND_ACKNOWLEDGEMENT")} onSubmit={handleDownloadPdf} />}
      <Link to={`/cnd-ui/${user?.type === "CITIZEN" ? "citizen" : "employee"}`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default CndAcknowledgement;