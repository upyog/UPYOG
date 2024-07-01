import React from "react";
import { Header, Loader, Card } from "@upyog/digit-ui-react-components";
import MyApplication from "./MyApplication";
import { useTranslation } from "react-i18next";

export const MyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { info: userInfo } = Digit.UserService.getUser();

  const { isLoading, isError, error, data: { data: { table: applicationsList } = {} } = {} } = Digit.Hooks.fsm.useSearchAll(tenantId, {
    uuid: userInfo.uuid,
    limit: 100,
  });

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <div>
        <Header>{t("CS_FSM_APPLICATION_TITLE_MY_APPLICATION")}</Header>
        {applicationsList?.length > 0 &&
          applicationsList.map((application, index) => (
            <div key={index}>
              <MyApplication application={application} />
            </div>
          ))}
          {applicationsList.length === 0 && (
          <Card>
            <p style={{ textAlign: "center" }}>{`${t("FSM_NO_APPLICATION")} ${userInfo.mobileNumber}`}</p>
          </Card>
        )}
      </div>
    </React.Fragment>
  );
};
