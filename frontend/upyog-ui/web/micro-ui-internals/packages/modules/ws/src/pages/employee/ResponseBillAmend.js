import { ActionBar, Banner, Card, CardText, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components"
import React, { useEffect } from "react"
import { useTranslation } from "react-i18next"
import { Link, useLocation } from "react-router-dom"

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
            <ActionBar className="ws-response-bill-amend-link-flex">
                <Link to={`/upyog-ui/employee`} className="ws-response-bill-amend-link">
                    <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
                </Link>
            </ActionBar>
        </Card>
    </div>;
};
export default ResponseBillAmend;
