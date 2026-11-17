import { Card, Loader, Header, CardSubHeader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
const About = () => {
  const { t } = useTranslation();
  const { isLoading, data } = Digit.Hooks.useGetDSSAboutJSON(Digit.ULBService.getStateId());
  const moduleAbout = data?.MdmsRes["dss-dashboard"]?.About[0]?.[`DSS`].About;
  const definitionlist = (defineObj) => {
    let array = [];
    for (var i = 0; i < defineObj.length; i++) {
      array.push(t(defineObj[i]));
    }
    return array.join(" ");
  }
    if (isLoading) {
    return <Loader />
  }
  return (
    <Fragment>
      <Header styles={{ marginLeft: "15px", paddingTop: "10px", fontSize: "36px" }}>{t("DSS_ABOUT_DASHBOARD")}</Header>
      <Card>{moduleAbout.map((obj) => (
        <div>
          <CardSubHeader className="dss-about-header" >{t(obj?.titleHeader)}</CardSubHeader>
          <div className="dss-about-bottom-spacing">{definitionlist(obj?.define)}</div>
          {obj?.definePoints && obj?.titleHeader === "KEY_TERMS"  ?
             <div>
              {obj?.definePoints?.map((about, i) => (
                <div className="dss-about-bottom-spacing-2">{"•"}<div className="dss-about-top-spacing"><b>{t(`${about?.point}_HEADER`)}</b> - {t(`${about?.point}_MSG`)}</div></div>
              ))}
            </div> : null}
            {obj?.definePoints && obj?.titleHeader !== "KEY_TERMS"  ?
             <div>
              {obj?.definePoints?.map((about, i) => (
                <div className="dss-about-bottom-spacing-2">{"•"}<div className="dss-about-top-spacing">{t(about?.point)}</div></div>
              ))}
            </div> : null}
            <div className="dss-about-bottom-spacing">{t(obj?.subdefine)}</div> 
            {obj?.subdefinePoints ?
            <div>
              {obj?.subdefinePoints?.map((about, i) => (
                <div className="dss-about-bottom-spacing-2">{"•"}<div className="dss-about-top-spacing">{t(about?.point)}</div></div>
              ))}
            </div> : null}
        </div>
      ))}</Card>
    </Fragment>
  );
};
export default About;