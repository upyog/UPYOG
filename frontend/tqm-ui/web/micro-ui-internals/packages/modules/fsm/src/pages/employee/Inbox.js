import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { Header, InboxSearchComposer, Loader } from "@egovernments/digit-ui-react-components";
// import { InboxSearchComposer } from "@egovernments/digit-ui-module-utilities";
import { useLocation } from "react-router-dom";
import { FSMInboxConfig } from "./configs/FSMInboxConfig";

const Inbox = () => {
  const { t } = useTranslation();
  const location = useLocation();
  // const data = FSMInboxConfig();

  //fetch this config from mdms and pass it to the preProcess fn
  // let configs = inboxConfigMukta(t);
  const [pageConfig, setPageConfig] = useState(null);
  // const moduleName = Digit.Utils.getConfigModuleName();
  const tenant = Digit.ULBService.getStateId();

  const { isLoading, data } = Digit.Hooks.useCustomMDMS(
    tenant,
    "commonUiConfig",
    [
      {
        name: "FSMInboxConfig",
      },
    ],
    {
      select: (data) => {
        return FSMInboxConfig();
      },
    }
  );

  const updatedConfig = useMemo(() => Digit.Utils.preProcessMDMSConfigInboxSearch(t, pageConfig, "sections.search.uiConfig.fields", {}), [data, pageConfig]);
  
  useEffect(() => {
    setPageConfig(_.cloneDeep(data?.["commonUiConfig"]?.FSMInboxConfig?.[0]));
  }, [data, location]);

  const fsmInboxSession = Digit.Hooks.useSessionStorage("FSM_INBOX_SESSION", {})

  if (isLoading || !pageConfig) return <Loader />;
  return (
    <React.Fragment>
      <Header styles={{ fontSize: "32px" }}>
        {t(updatedConfig?.label)}
        {location?.state?.count ? <span className="inbox-count">{location?.state?.count}</span> : null}
      </Header>
      <div className="inbox-search-wrapper">
        <InboxSearchComposer configs={updatedConfig} browserSession={fsmInboxSession}></InboxSearchComposer>
      </div>
    </React.Fragment>
  );
};

export default Inbox;
