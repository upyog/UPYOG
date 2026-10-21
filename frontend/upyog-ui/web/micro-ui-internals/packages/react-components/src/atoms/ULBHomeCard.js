import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { Card, CardHeader } from "..";

const ULBHomeCard = (props) => {
  const { t } = useTranslation();
  const state = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const navigate = Digit.Hooks.useCustomNavigate();

  return (
    <React.Fragment>
      <Card className="fsm rc-ulbhome-card-card">
        <CardHeader> {t(props.title)} </CardHeader>
        <div className="rc-ulbhome-card-grid-container">
          {props.module.map((i) => {
            return (
              <Card
                className="rc-ulbhome-card-clickable"
                onClick={() => (i.hyperlink ? location.assign(i.link) : navigate(i.link))}
                children={
                  <>
                    {" "}
                    {i.icon} <p> {t(i.name)} </p>{" "}
                  </>
                }
              ></Card>
            );
          })}
        </div>
      </Card>
    </React.Fragment>
  );
};

export default ULBHomeCard;
