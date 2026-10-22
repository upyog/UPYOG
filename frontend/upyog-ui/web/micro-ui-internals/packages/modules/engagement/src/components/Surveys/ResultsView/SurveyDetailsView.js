import { TextInput, Dropdown, RemoveableTag, LinkButton,LinkLabel, SearchField,SubmitBar } from "@nudmcdgnpm/digit-ui-react-components"
import React,{useMemo} from 'react'
import { Link } from "react-router-dom";

const SurveyDetailsView = ({surveyTitle,surveyDesc,t,surveyId}) => {
    const navigate = Digit.Hooks.useCustomNavigate();
    const ulbs = Digit.SessionStorage.get("ENGAGEMENT_TENANTS");
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const userInfo = Digit.UserService.getUser().info;
    const userUlbs = ulbs
    .filter((ulb) => userInfo?.roles?.some((role) => role?.tenantId === ulb?.code))
    const selectedTenat = useMemo(() => {
    const filtered = ulbs.filter((item) => item.code === tenantId);
    return filtered;
  }, [ulbs]);
  return (
      <div className="surveydetailsform-wrapper">
        <span className="surveyformfield">
            <label>{`${t("LABEL_FOR_ULB")} * :`}</label>
            <div className="eng-survey-details-view-grid-container">
                    <Dropdown
                    allowMultiselect={true}
                    optionKey={"i18nKey"}
                    option={userUlbs}
                    selected={selectedTenat}
                    keepNull={true}
                    disable={true}
                    t={t}
                    />
                    <RemoveableTag
                      key={"tag"}
                      text={t(userUlbs[0].i18nKey)}
                      extraStyles={{tagStyles : {display:"flex"}}}
                    />
            </div>  
        {/* <button
          type={"button"}
          className="custom-style"
          onClick={() => history.push(`/upyog-ui/employee/engagement/surveys/inbox/details/${surveyId}`)}
        >
          <header className="custom-style">{t("SURVEY_QUESTIONS")}</header>
        </button> */}
        <LinkLabel  onClick={() => navigate(`/upyog-ui/employee/engagement/surveys/inbox/details/${surveyTitle}`)}>
        {t("VIEW_SURVEY_QUESTIONS")}
        </LinkLabel>
        
        </span>
    
        <span className="surveyformfield">
            <label>{t("CS_SURVEY_NAME")}</label>
            <TextInput
            name="title"
            type="text"
            disable={true}
            value={surveyTitle}
            />
        </span>
        <span className="surveyformfield">
            <label>{t("CS_SURVEY_DESCRIPTION")}</label>
            <TextInput
            name="description"
            type="text"
            disable={true}
            value={surveyDesc}
            />
        </span>
    </div>
  )
}

export default SurveyDetailsView