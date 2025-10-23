import { TextInput, Dropdown, RemoveableTag, LinkButton,LinkLabel, SearchField,SubmitBar } from '@upyog/digit-ui-react-components'
import React,{useMemo} from 'react'
import { useHistory, Link } from "react-router-dom";

const SurveyDetailsView = ({surveyTitle,surveyDesc,t,surveyId}) => {
    const history = useHistory()
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
            <div style={{ display: "grid", gridAutoFlow: "row" }}>
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
          style={{ border: "2px solid #0f4f9e", backgroundColor: "white", padding: "8px 8px",width:"200px",marginLeft:"50px",marginTop:"-45px" }}
          onClick={() => history.push(`/digit-ui/employee/engagement/surveys/inbox/details/${surveyId}`)}
        >
          <header style={{ color:"#0f4f9e"}}>{t("SURVEY_QUESTIONS")}</header>
        </button> */}
        <LinkLabel  onClick={() => history.push(`/digit-ui/employee/engagement/surveys/inbox/details/${surveyId}`)}>
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