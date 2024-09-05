import React, { Fragment } from "react";
import Card from "./Card";
import CardHeader from "./CardHeader";
import ButtonSelector from "./ButtonSelector";
import CardText from "./CardText";
import ActionLinks from "./ActionLinks";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { Link } from "react-router-dom";

function NotificationComponent({linkObj,...props}) {
  const { t } = useTranslation();
  const history = useHistory();

  const onSubmit = (route, id) => {
    return history.push(`${props?.actionRoute}?id=${id}`);
  };

  return (
    <Card className="notification">
      <CardHeader>{props?.heading}</CardHeader>
      {props?.data?.length > 0 ? (
        props?.data?.map((i, index) =>
          index < 3 ? (
            <>
              <div className="notification-flex-container">
                <div className="icon">{i?.icon}</div>
                <CardText className="label">{t(i?.title)}</CardText>
                <ButtonSelector onSubmit={() => onSubmit(i?.route, i?.id)} theme="secondary" label={t(`ES_TQM_LABEL_${i?.action ? i?.action : ""}`)} />
              </div>
              <span className={i?.date < 0 ? `sla-cell-error bg` : `sla-cell-success bg`}>
                {i?.date < 0 ? t(`ES_TQM_SLA_PENDING_OVERDUE_DATE`, { NO_OF_DAYS: Math.ceil(i?.date) }) : t(`ES_TQM_SLA_PENDING_DUE_DATE`, { NO_OF_DAYS: Math.ceil(i?.date) })}
              </span>
              <hr className="break-line" />
            </>
          ) : (
            index === 3 && (
              <Link to={linkObj}>
                <ActionLinks>{t(`ES_TQM_VIEW_ALL_PENDING_TASKS`)}</ActionLinks>
              </Link>
            )
          )
        )
      ) : (
        <CardText className="label">{t("ES_TQM_NO_PENDING_TASK")}</CardText>
      )}
    </Card>
  );
}

export default NotificationComponent;
