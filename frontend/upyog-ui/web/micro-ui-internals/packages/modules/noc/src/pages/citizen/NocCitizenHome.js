import React from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import {
  BackButton,
  CitizenHomeCard,
} from "@nudmcdgnpm/digit-ui-react-components";

// Custom inline SVG Icon
const FlameIcon = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M12 2C11.5 3.5 10 5.5 8 7C6 8.5 5 10.5 5 13C5 16.87 8.13 20 12 20C15.87 20 19 16.87 19 13C19 10.5 18 8.5 16 7C14 5.5 12.5 3.5 12 2Z" fill="#FE7A51" />
  </svg>
);

const NocCitizenHome = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  React.useEffect(() => {
    Digit.SessionStorage.del("NOC_CREATE_APPLICATION");
    Digit.SessionStorage.del("NOC_SUCCESSFUL_APPLICATION");
  }, []);
  
  const getPrefix = () => {
    return window.location.pathname.includes("upyog-ui") ? "/upyog-ui" : "/digit-ui";
  };
  const path = `${getPrefix()}/citizen/firenoc`;

  const isMobile = window.Digit.Utils.browser.isMobile();
  const storeData = Digit.SessionStorage.get("initData");
  const stateInfo = storeData?.stateInfo;
  const bannerImage = "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg";

  const links = [
    {
      link: `${path}/new-application`,
      i18nKey: t("NOC_APPLY"),
      parentModule: "FIRENOC"
    },
    {
      link: `${path}/my-applications`,
      i18nKey: t("NOC_MY_APPLICATIONS"),
      parentModule: "FIRENOC"
    }
  ];

  return (
    <div className="moduleLinkHomePage">
      <img src={bannerImage || stateInfo?.bannerUrl} alt="noimagefound" />
      <BackButton className="moduleLinkHomePageBackButton" />
      {isMobile ? (
        <h4 style={{ top: "calc(16vw + 40px)", left: "1.5rem", position: "absolute", color: "white" }}>
          {t("MODULE_FIRENOC")}
        </h4>
      ) : (
        <h1>{t("MODULE_FIRENOC")}</h1>
      )}

      <div className="moduleLinkHomePageModuleLinks" style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
        <CitizenHomeCard
          header={t("MODULE_FIRENOC")}
          links={links}
          Icon={() => <FlameIcon />}
        />
      </div>
    </div>
  );
};

export default NocCitizenHome;
