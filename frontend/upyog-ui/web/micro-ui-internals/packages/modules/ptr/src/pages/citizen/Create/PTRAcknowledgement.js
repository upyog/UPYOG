/**
 * PTRAcknowledgement Component
 *
 * Handles acknowledgement display for success and failure cases.
 */

import {
  Banner,
  Card,
  LinkButton,
  Loader,
  Row,
  StatusTable,
  SubmitBar,
} from "@nudmcdgnpm/digit-ui-react-components";

import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";

import getPetAcknowledgementData from "../../../getPetAcknowledgementData";

/**
 * GetActionMessage Component
 */
const GetActionMessage = (props) => {
  const { t } = useTranslation();

  if (props?.isSuccess) {
    return window.location.href.includes("revised-application")
      ? t("PTR_REVISED_SUCCESSFULLY")
      : t("ES_PTR_RESPONSE_CREATE_ACTION");
  }

  return t("CS_PTR_APPLICATION_FAILED");
};

/**
 * Styling for row container
 */
const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

/**
 * BannerPicker Component
 */
const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={
        props?.data?.PetRegistrationApplications?.[0]
          ?.applicationNumber || ""
      }
      info={props?.isSuccess ? props.t("PTR_APPLICATION_NO") : ""}
      successful={props?.isSuccess}
      style={{ width: "100%" }}
    />
  );
};

/**
 * PTRAcknowledgement Component
 */
const PTRAcknowledgement = () => {
  const { t } = useTranslation();

  const user = Digit.UserService.getUser().info;

  const { data: storeData } =
    Digit.Hooks.useStore.getInitData();

  const { state } = useLocation();

  const { tenants } = storeData || {};

  /**
   * Handles PDF download
   */
  const handleDownloadPdf = async () => {
    const { PetRegistrationApplications = [] } =
      state?.data || {};

    let Pet = PetRegistrationApplications?.[0] || {};

    const tenantInfo = tenants?.find(
      (tenant) => tenant.code === Pet?.tenantId
    );

    const data = await getPetAcknowledgementData(
      { ...Pet },
      tenantInfo,
      t
    );

    Digit.Utils.pdf.generate(data);
  };

  // Initial loading state
  if (!state) {
    return <Loader />;
  }

 return (
    <Card>
      <BannerPicker
        t={t}
        data={state.data}
        isSuccess={state.isSuccess}
      />

      <StatusTable>
        {state?.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>

      {state?.isSuccess && (
        <SubmitBar
          label={t("PTR_PET_DOWNLOAD_ACK_FORM")}
          onSubmit={handleDownloadPdf}
        />
      )}

      {user?.type === "CITIZEN" ? (
        <Link to={`/upyog-ui/citizen`}>
          <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      ) : (
        <Link to={`/upyog-ui/employee`}>
          <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      )}
    </Card>
  );
};

export default PTRAcknowledgement;