import { SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";


const EmptyResultInbox = (props) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const addNewProprty = () => {
    navigate("/upyog-ui/employee/pt/new-application");
  };
  return <React.Fragment>
      {props.data ? <React.Fragment>
          <div className="pt-auto-15">{t("PT_NO_MATCHING_PROPERTY_FOUND")}</div>
          <div className="pt-auto-16">
            <SubmitBar onSubmit={addNewProprty} label={t("PT_ADD_NEW_PROPERTY_BUTTON")} />
          </div>
        </React.Fragment> : null}
    </React.Fragment>;
};
export default EmptyResultInbox;
