import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { Card, CardHeader } from "..";

const ULBHomeCard = (props) => {
  const { t } = useTranslation();
  const state = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const history = useHistory();

  return (
    <React.Fragment>
      <Card className="home-card-tiles-container">
        <CardHeader> {t(props.title)} </CardHeader>
        <div className="tiles-card-container">
          {props.module.map((i,idx) => {
            return (
              <Card className={`tiles-card tiles-card-${idx}`} onClick={() => (i.hyperlink ? location.assign(i.link) : history.push(i.link))}>
                {i.icon}
                <p> {t(i.name)} </p>
              </Card>
            );
          })}
        </div>
      </Card>
    </React.Fragment>
  );
};

export default ULBHomeCard;
