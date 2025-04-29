import {
  BackButton,
  CitizenHomeCard,
  Loader,
  PTIcon,
} from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import EmployeeDashboard from "./EmployeeDashboard";
import {CNDIcon} from "./Svg"



/* 
Feature :: Citizen All service screen cards
*/
export const processLinkData = (newData, code, t) => {
  const obj = newData?.[`${code}`];
  if (obj) {
    obj.map((link) => {
      (link.link = link["navigationURL"]), (link.i18nKey = t(link["name"]));
    });
  }
  const newObj = {
    links: obj?.reverse(),
    header: Digit.Utils.locale.getTransformedLocale(`ACTION_TEST_${code}`),
    iconName: `CITIZEN_${code}_ICON`,
  };

  return newObj;
};


const iconSelector = (code) => {
  switch (code) {
    case "CND":
      return <CNDIcon className="fill-path-primary-main" />
    default:
      return <PTIcon className="fill-path-primary-main" />;
  }
};



const CitizenHome = ({ modules, getCitizenMenu, fetchedCitizen, isLoading }) => {
  const paymentModule = modules.filter(({ code }) => code === "Payment")[0];
  const moduleArr = modules.filter(({ code }) => code !== "Payment");
  const moduleArray = [paymentModule, ...moduleArr];
  const { t } = useTranslation();
  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <div className="citizen-all-services-wrapper">
        <BackButton />
        <div className="citizenAllServiceGrid" style={{display:"flex", flexDirection:"column",justifyContent:"center" }}>
          {moduleArray
            .filter((mod) => mod)
            .map(({ code }, index) => {
              let mdmsDataObj;
              if (fetchedCitizen) mdmsDataObj = fetchedCitizen ? processLinkData(getCitizenMenu, code, t) : undefined;
              if (mdmsDataObj?.links?.length > 0) {
                return (
                  <CitizenHomeCard
                    header={t(mdmsDataObj?.header)}
                    links={mdmsDataObj?.links?.filter((ele) => ele?.link)?.sort((x, y) => x?.orderNumber - y?.orderNumber)}
                    Icon={() => iconSelector(code)}
                    Info={""}
                    isInfo={""}
                  />
                );
              } else return <React.Fragment />;
            })}
        </div>
      </div>
    </React.Fragment>
  );
};

const EmployeeHome = ({ modules }) => {
  // const dashboardCemp = Digit.UserService.hasAccess(["DASHBOARD_EMPLOYEE"])?true:false;
  if(window.Digit.SessionStorage.get("PT_CREATE_EMP_TRADE_NEW_FORM")) window.Digit.SessionStorage.set("PT_CREATE_EMP_TRADE_NEW_FORM",{})
    // const { data: dashboardConfig } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(),"common-masters",[{ name: "CommonConfig" }],
    //   {
    //     select: (data) => {
    //       const formattedData = data?.["common-masters"]?.["CommonConfig"];
    //       // Find the object with cityDashboardEnabled and return its isActive value
    //       const cityDashboardObject = formattedData?.find(
    //         (item) => item?.name === "cityDashboardEnabled"
    //       );
    //       return cityDashboardObject?.isActive;
    //     },
    //   }
    // );
  return (
    <div className="employee-app-container">
      <br />
      {/* {(dashboardConfig && dashboardCemp)?<EmployeeDashboard modules={modules}/>:null} */}
      <div className="ground-container moduleCardWrapper gridModuleWrapper">
        {modules.map(({ code }, index) => {
          const Card = Digit.ComponentRegistryService.getComponent(`${code}Card`) || (() => <React.Fragment />);
          return <Card key={index} />;
        })}
      </div>
    </div>
  );
};

export const AppHome = ({ userType, modules, getCitizenMenu, fetchedCitizen, isLoading }) => {
  if (userType === "citizen") {
    return <CitizenHome modules={modules} getCitizenMenu={getCitizenMenu} fetchedCitizen={fetchedCitizen} isLoading={isLoading} />;
  }
  return <EmployeeHome modules={modules} />;
};
