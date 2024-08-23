import { AppContainer, BreadCrumb, PrivateRoute } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Switch, useLocation, useRouteMatch } from "react-router-dom";

const App = ({ path, stateCode, userType, tenants }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const match = useRouteMatch();
  const SecurityCheckPage = Digit.ComponentRegistryService?.getComponent("SecurityCheckPage");
  const ParkingFeePage = Digit.ComponentRegistryService?.getComponent("ParkingFeePage");
  const RemovalPage = Digit.ComponentRegistryService?.getComponent("RemovalPage");
  const AssignShopkeeperAfterTradingPage = Digit.ComponentRegistryService?.getComponent("AssignShopkeeperAfterTrading");
  const EntryFeePage = Digit.ComponentRegistryService?.getComponent("EntryFeeCollection");
  const RemovalFeePage = Digit.ComponentRegistryService?.getComponent("RemovalFeePage");
  const StablingFeePage = Digit.ComponentRegistryService?.getComponent("StablingFeePage");
  const AnteMortemInspectionPage = Digit.ComponentRegistryService?.getComponent("AnteMortemInspectionPage");
  const ReAnteMortemInspectionPage = Digit.ComponentRegistryService?.getComponent("ReAnteMortemInspection");
  const AnteMortemPreSlaughterInspectionPage = Digit.ComponentRegistryService?.getComponent("AnteMortemPreSlaughterInspectionPage");
  const PostMortemInspectionPage = Digit.ComponentRegistryService?.getComponent("PostMortemInspectionPage");
  const SlaughterFeeRecoveryPage = Digit.ComponentRegistryService?.getComponent("SlaughterFeeRecoveryPage");
  const VehicleWashing = Digit.ComponentRegistryService?.getComponent("VehicleWashing");
  const WeighingCharge = Digit.ComponentRegistryService?.getComponent("WeighingCharge");
  const PenaltyCharge = Digit.ComponentRegistryService?.getComponent("PenaltyCharge");
  const GatePass = Digit.ComponentRegistryService?.getComponent("GatePass");

  const mobileView = innerWidth <= 640;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const inboxInitialState = {
    searchParams: {
      tenantId: tenantId,
    },
  };
  const ProjectBreadCrumb = ({ location }) => {
    const { t } = useTranslation();
    const crumbs = [
      {
        path: `/${window?.contextPath}/employee`,
        content: t("HOME"),
        show: true,
      },
      {
        path: `/${window?.contextPath}/employee`,
        content: t(location.pathname.split("/").pop()),
        show: true,
      },
    ];
    return <BreadCrumb crumbs={crumbs} spanStyle={{ maxWidth: "min-content" }} />;
  };
  return (
    <Switch>
      <AppContainer className="ground-container">
        <React.Fragment>
          <ProjectBreadCrumb location={location} />
        </React.Fragment>
        <PrivateRoute path={`${path}/securitycheck`} component={SecurityCheckPage} />
        <PrivateRoute path={`${path}/parking`} component={ParkingFeePage} />
        <PrivateRoute path={`${path}/removal`} component={RemovalPage} />
        <PrivateRoute path={`${path}/assignshopkeeper`} component={AssignShopkeeperAfterTradingPage} />
        <PrivateRoute path={`${path}/entryfee`} component={EntryFeePage} />
        <PrivateRoute path={`${path}/removalfee`} component={RemovalFeePage} />
        <PrivateRoute path={`${path}/stablingfee`} component={StablingFeePage} />
        <PrivateRoute path={`${path}/antemorteminspection`} component={AnteMortemInspectionPage} />
        <PrivateRoute path={`${path}/reantemorteminspection`} component={ReAnteMortemInspectionPage} />
        <PrivateRoute path={`${path}/antemortembeforeslaughterinspection`} component={AnteMortemPreSlaughterInspectionPage} />
        <PrivateRoute path={`${path}/postmorteminspection`} component={PostMortemInspectionPage} />
        <PrivateRoute path={`${path}/slaughterfeerecovery`} component={SlaughterFeeRecoveryPage} />
        <PrivateRoute path={`${path}/vehiclewashing`} component={VehicleWashing} />
        <PrivateRoute path={`${path}/weighingcharge`} component={WeighingCharge} />
        <PrivateRoute path={`${path}/penaltyCharge`} component={PenaltyCharge} />
        <PrivateRoute path={`${path}/gatePass`} component={GatePass} />
      </AppContainer>
    </Switch>
  );
};

export default App;
