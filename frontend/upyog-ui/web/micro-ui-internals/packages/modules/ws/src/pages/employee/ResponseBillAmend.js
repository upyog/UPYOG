import { ActionBar, Banner, Card, CardText, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";
import "../../css/ws-inline-auto.css";
const ResponseBillAmend = () => {
  const {
    state
  } = useLocation();
  const {
    t
  } = useTranslation();
  return <div>
         <Card>
            <Banner message={state ? t("WS_BILL_AMENDMENT_BANNER") : t("WS_BILL_AMENDMENT_FAILURE")} applicationNumber={state?.state?.Amendments?.[0]?.amendmentId} info={""} successful={state?.status ? true : false} />
            {!state.status ? null : <CardText>{t("WS_BILL_AMENDMENT_MESSAGE")}</CardText>}
            <ActionBar className="ws-auto-330">
                <Link to={`/upyog-ui/employee`} className="ws-auto-331">
                    <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
                </Link>
            </ActionBar>
        </Card>
    </div>;
};
export default ResponseBillAmend;
