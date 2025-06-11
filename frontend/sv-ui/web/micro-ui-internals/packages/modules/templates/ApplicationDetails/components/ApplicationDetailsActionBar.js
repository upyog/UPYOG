import React, {useEffect, useRef} from "react";
import { useTranslation } from "react-i18next";
import { SubmitBar, ActionBar, Menu, CardLabel } from "@nudmcdgnpm/digit-ui-react-components";

function ApplicationDetailsActionBar({ workflowDetails, displayMenu, onActionSelect, setDisplayMenu, businessService, forcedActionPrefix,ActionBarStyle={},MenuStyle={},isAction,applicationDetails }) {
  const { t } = useTranslation();
  let user = Digit.UserService.getUser();
  const menuRef = useRef();
  const userRoles = user?.info?.roles?.map((e) => e.code);
  let isSingleButton = false;
  let isMenuBotton = false;
  let actions = workflowDetails?.data?.actionState?.nextActions?.filter((e) => {
    return userRoles?.some((role) => e.roles?.includes(role)) || !e.roles;
  }) || workflowDetails?.data?.nextActions?.filter((e) => {
    return userRoles?.some((role) => e.roles?.includes(role)) || !e.roles;
  });

    const closeMenu = () => {
          setDisplayMenu(false);
      }
    Digit.Hooks.useClickOutside(menuRef, closeMenu, displayMenu );

  if (actions?.length > 0) {
    isMenuBotton = true; 
    isSingleButton = false;
  }
  const Session = Digit.SessionStorage.get("User");
  const uuid = Session?.info?.uuid;
  const modified = applicationDetails?.applicationData?.auditDetails?.lastModifiedBy;

  return (
    <React.Fragment>
      {!workflowDetails?.isLoading && isMenuBotton && !isSingleButton && !isAction && (
        <ActionBar style={{...ActionBarStyle}}>
          {displayMenu && (workflowDetails?.data?.actionState?.nextActions || workflowDetails?.data?.nextActions) ? (
            <Menu
              localeKeyPrefix={forcedActionPrefix || `WF_EMPLOYEE_${businessService?.toUpperCase()}`}
              options={actions}
              optionKey={"action"}
              t={t}
              onSelect={onActionSelect}
              style={MenuStyle}
            />
          ) : null}
          <SubmitBar ref={menuRef} label={t("WF_TAKE_ACTION")} onSubmit={() => setDisplayMenu(!displayMenu)} />
        </ActionBar>
      )}
      {!workflowDetails?.isLoading && !isMenuBotton && isSingleButton && !isAction && (
        <ActionBar style={{...ActionBarStyle}}>
          <button
              style={{ color: "#FFFFFF", fontSize: "18px" }}
              className={"submit-bar"}
              name={actions?.[0]?.action}
              value={actions?.[0]?.action}
              onClick={(e) => { onActionSelect(actions?.[0] || {})}}>
              {t(`${forcedActionPrefix || `WF_EMPLOYEE_${businessService?.toUpperCase()}`}_${actions?.[0]?.action}`)}
            </button>
        </ActionBar>
      )}
    </React.Fragment>
  );
}

export default ApplicationDetailsActionBar;
