import { Banner, Card, LinkButton, Loader, Row, StatusTable, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import { cndPayload } from "../../../utils";
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
    return  t("CND_SUCCESS_MESSAGE");
  } else if (props.isLoading) {
    return  t("CND_APP_PENDING_MESSAGE");
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

const CndAcknowledgement = ({ data, onSuccess }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;
  const mutation = Digit.Hooks.cnd.useCndCreateApi(tenantId); 
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  useEffect(() => {
    try {
      data.tenantId = tenantId;
      let formdata = cndPayload(data)
      mutation.mutate(formdata, {onSuccess});
    } catch (err) {
    }
  }, []);

  //after submitting the application form, when someone click back button it will take user to Home instead of previous page 
  Digit.Hooks.useCustomBackNavigation({
    redirectPath: CND_VARIABLES.HOME_PATH
  })

  

  const handleDownloadPdf = async () => {
    const { cndApplicationDetails = [] } = mutation.data;
    let Cnd = (cndApplicationDetails) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === Cnd.tenantId);
    let tenantId = Cnd.tenantId || tenantId;
    const data = await cndAcknowledgementData({ ...Cnd }, tenantInfo, t);
    Digit.Utils.pdf.generateTable(data);
  };

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={cndStyles.rowContainerStyle}
            last       
            textStyle={cndStyles.textStyle}
          />
        )}
      </StatusTable>
      {mutation.isSuccess && <SubmitBar label={t("CND_ACKNOWLEDGEMENT")} onSubmit={handleDownloadPdf} />}
      <Link to={`/cnd-ui/${user?.type === "CITIZEN" ? "citizen" : "employee"}`}>
          <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default CndAcknowledgement;