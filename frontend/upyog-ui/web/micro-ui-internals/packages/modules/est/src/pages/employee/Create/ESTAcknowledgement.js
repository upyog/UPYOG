import {
  Banner,
  Card,
  LinkButton,
  Row,
  StatusTable,
} from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";
import styles from '../../../styles/ESTAcknowledgement.module.scss'

/**
 * ESTAcknowledgement
 * -------------------
 * Displays the result of the EST asset create API call.
 *
 * The API call is made in ESTRegCheckPage (mutation.mutate).
 * On success/failure, ESTRegCreate.estcreate navigates here with:
 *   location.state = { data: apiResponse, isSuccess: true }
 *   location.state = { data: null, isSuccess: false, error }
 *
 * This component just reads location.state and renders the banner.
 * It does NOT make any API calls itself.
 */

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

// ─── BannerPicker ──────────────────────────────────────────────────────────────
const BannerPicker = ({ t, isSuccess, applicationNumber }) => {
  if (isSuccess) {
    return (
      <Banner
        message={
          window?.location?.href?.includes("edit")
            ? t("EST_UPDATE_SUCCESSFULL")
            : t("EST_SUBMIT_SUCCESSFULL")
        }
        applicationNumber={applicationNumber || ""}
        info={applicationNumber ? t("EST_APPLICATION_NO") : ""}
        successful={true}
        className={styles["festAcknowledgement__full-width"]}
      />
    );
  }

  return (
    <Banner
      message={t("EST_APPLICATION_FAILED")}
      successful={false}
      className={styles["festAcknowledgement__full-width"]}
    />
  );
};

// ─── Extract estateNo from API response ───────────────────────────────────────
const extractEstateNo = (response) => {
  if (!response) return "";
  try {
    const assets =
      response?.Assets ||
      response?.data?.Assets ||
      (Array.isArray(response) ? response : []);
    const asset0 = Array.isArray(assets) && assets.length ? assets[0] : {};
    return (
      asset0?.estateNo ||
      asset0?.applicationNo ||
      response?.estateNo ||
      ""
    );
  } catch {
    return "";
  }
};

// ─── ESTAcknowledgement ───────────────────────────────────────────────────────
const ESTAcknowledgement = ({ onSuccess }) => {
  const { t } = useTranslation();
  const location = useLocation();

  // ESTRegCreate.estcreate navigates here with state:
  //   { data: apiResponse, isSuccess: true }   ← success
  //   { data: null, isSuccess: false, error }   ← failure
  const { data: apiResponse, isSuccess = false, error } = location?.state || {};

  const applicationNumber = extractEstateNo(apiResponse);

  if (error) console.error("EST Acknowledgement — error:", error);

  // Call parent onSuccess callback if provided (clears session/query cache)
  React.useEffect(() => {
    if (isSuccess && typeof onSuccess === "function") {
      onSuccess(apiResponse);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const getUserHomeLink = () => {
    try {
      const type = Digit?.UserService?.getUser()?.info?.type;
      return type === "CITIZEN" ? "/upyog-ui/citizen" : "/upyog-ui/employee";
    } catch {
      return "/upyog-ui/employee";
    }
  };

  return (
    <React.Fragment>
      <Card>
        <BannerPicker
          t={t}
          isSuccess={isSuccess}
          applicationNumber={applicationNumber}
        />

        <StatusTable className={styles["estAcknowledgement__status-table"]}>
          <Row
            rowContainerStyle={undefined}
            className={styles["estAcknowledgement__row"]}
            last
            textStyle={undefined}
          />
        </StatusTable>

        <div className={styles["estAcknowledgement__action-row"]}>
          <Link to={getUserHomeLink()} className={styles["estAcknowledgement__home-link"]}>
            <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
          </Link>
        </div>

      </Card>
    </React.Fragment>
  );
};

export default ESTAcknowledgement;
