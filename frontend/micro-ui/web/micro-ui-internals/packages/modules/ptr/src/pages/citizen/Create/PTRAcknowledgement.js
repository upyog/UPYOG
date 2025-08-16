/**
 * PTRAcknowledgement Component
 * 
 * This component handles the acknowledgement display after submitting a pet registration application.
 * It shows a banner with the application status, provides a download option for the acknowledgment form, 
 * and offers navigation back to the home page.
 * 
 * Features:
 * - Displays a success, loading, or failure message with `BannerPicker`.
 * - Downloads the acknowledgment form in PDF format.
 * - Redirects to either the citizen or employee home page based on the user type.
 */

import { Banner, Card, LinkButton, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import getPetAcknowledgementData from "../../../getPetAcknowledgementData";
import { PetDataConvert } from "../../../utils";

/**
 * GetActionMessage Component
 * 
 * Displays the appropriate message based on the application status (success, loading, or failure).
 */

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("revised-application") ? t("ES_PTR_RESPONSE_CREATE_ACTION") : t("PTR_REVISED_SUCCESSFULLY");
  } else if (props.isLoading) {
    return  t("CS_PTR_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    return t("CS_PTR_APPLICATION_FAILED");
  }
};

/**
 * Styling for the row container layout.
 */
const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

/**
 * BannerPicker Component
 * 
 * Displays the application banner with the application number and success or failure message.
 */
const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props.data?.PetRegistrationApplications[0].applicationNumber}
      info={props.isSuccess ? props.t("PTR_APPLICATION_NO") : ""}
      successful={props.isSuccess}
      style={{width: "100%"}}
    />
  );
};


/**
 * PTRAcknowledgement Component
 * 
 * Handles the display of the application acknowledgment, including the download option and navigation buttons.
 */
const PTRAcknowledgement = ({ data, onSuccess }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const user = Digit.UserService.getUser().info;
  const mutation = Digit.Hooks.ptr.usePTRCreateAPI(data.address?.city?.code); 
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  useEffect(() => {
    try {
      data.tenantId = tenantId;
      let formdata = PetDataConvert(data)
      mutation.mutate(formdata, {onSuccess});
    } catch (err) {
    }
  }, []);

  
/**
   * Handles the PDF download of the application acknowledgment form.
   */
  const handleDownloadPdf = async () => {
    const { PetRegistrationApplications = [] } = mutation.data;
    let Pet = (PetRegistrationApplications && PetRegistrationApplications[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === Pet.tenantId);
    let tenantId = Pet.tenantId || tenantId;
    const data = await getPetAcknowledgementData({ ...Pet }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last       
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>
      {mutation.isSuccess && <SubmitBar label={t("PTR_PET_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}
      {user?.type==="CITIZEN"?
      <Link to={`/digit-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
      :
      <Link to={`/digit-ui/employee`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>}
    </Card>
  );
};

export default PTRAcknowledgement;