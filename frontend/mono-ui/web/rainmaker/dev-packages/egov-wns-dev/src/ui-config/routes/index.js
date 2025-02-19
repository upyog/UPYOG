import React from "react";
import Loadable from 'react-loadable';
import LoadingIndicator from 'egov-ui-framework/ui-molecules/LoadingIndicator';
import * as mainRouteConstants from "./route-names";

const Loading = () => <LoadingIndicator/>;

const Landing = Loadable({
  loader: () => import('ui-views/Landing'),
  loading: Loading,
});

const Playground = Loadable({
  loader: () => import('ui-views/Playground'),
  loading: Loading,
});

const ScreenInterface=Loadable({
  loader:()=>import ('ui-views/ScreenInterface'),
  loading:Loading
})

const mainRoutes = [
  {
    path: mainRouteConstants.LANDING,
    component: Landing
  },
  {
    path:mainRouteConstants.SCREEN_INTERFACE,
    component:ScreenInterface
  },
  {
    path: mainRouteConstants.PLAYGROUND,
    component: Playground
  },
  {
    isRedirect:true,
    to:mainRouteConstants.REDIRECT
  }
];

export default mainRoutes;
