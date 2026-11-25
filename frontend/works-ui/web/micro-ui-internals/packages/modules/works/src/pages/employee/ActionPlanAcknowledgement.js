import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, useParams } from "react-router-dom";
import { Banner, Card, CardText, SubmitBar, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";

const ActionPlanAcknowledgement = ({ parentRoute = "/works-ui/employee/works" }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const location = useLocation();
  const { applicationNumber: paramAppNo } = useParams();

  const state = location?.state || {};
  const actionPlan = state?.actionPlan || {};
  const applicationNumber =
    paramAppNo ||
    state?.applicationNumber ||
    actionPlan?.applicationNumber ||
    "";

  return (
    <div style={{ padding: "24px", margin: "0 auto" }}>
      <Card>
        <Banner
          applicationNumber={applicationNumber}
          info={t("WORKS_AP_APPLICATION_NO", "Application Number")}
          message={t(
            "WORKS_AP_SUBMITTED_SUCCESS",
            "Action Plan Submitted Successfully"
          )}
          successful={true}
        />

        <CardText style={{ marginTop: "20px", fontSize: "15px", lineHeight: 1.6 }}>
          {t(
            "WORKS_AP_SUBMITTED_DESC",
            "Your Action Plan has been created and submitted for verification. You can track its progress from the Works Inbox or view full details below."
          )}
        </CardText>

        <div
          style={{
            marginTop: "24px",
            display: "flex",
            flexWrap: "wrap",
            gap: "12px",
            alignItems: "center",
          }}
        >
          {applicationNumber && (
            <SubmitBar
              label={t("WORKS_VIEW_APPLICATION_DETAILS", "View Application Details")}
              onSubmit={() =>
                navigate(`${parentRoute}/application-details/${applicationNumber}`)
              }
            />
          )}

          <Link
            to={`${parentRoute}/inbox`}
            style={{
              padding: "10px 20px",
              borderRadius: "6px",
              border: "1.5px solid #584f74",
              color: "#584f74",
              fontWeight: 600,
              textDecoration: "none",
              fontSize: "14px",
            }}
          >
            {t("WORKS_GO_TO_INBOX", "Go to Works Inbox")}
          </Link>

          <Link
            to="/works-ui/employee"
            style={{
              padding: "10px 16px",
              color: "#475569",
              fontWeight: 500,
              textDecoration: "none",
              fontSize: "14px",
            }}
          >
            {t("CORE_COMMON_GO_TO_HOME", "Go to Home")}
          </Link>
        </div>
      </Card>

      <ActionBar>
        <SubmitBar
          label={t("WORKS_GO_TO_INBOX", "Go to Works Inbox")}
          onSubmit={() => navigate(`${parentRoute}/inbox`)}
        />
      </ActionBar>
    </div>
  );
};

export default ActionPlanAcknowledgement;

