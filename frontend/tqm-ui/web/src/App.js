import React from 'react';

import { initDSSComponents } from '@egovernments/digit-ui-module-dss';
import {
  DigitUI,
  initCoreComponents,
} from '@egovernments/digit-ui-module-core';
import { initLibraries } from '@egovernments/digit-ui-libraries';
import { initEngagementComponents } from '@egovernments/digit-ui-module-engagement';
import { initFSMLibraries } from '@egovernments/digit-ui-fsm-libraries';

import { initFSMComponents } from '@egovernments/digit-ui-module-fsm';
import { initTQMComponents } from '@egovernments/digit-ui-module-tqm';
import { initHRMSComponents } from '@egovernments/digit-ui-module-hrms';
import {
  PaymentModule,
  PaymentLinks,
  paymentConfigs,
} from '@egovernments/digit-ui-module-common';
import { initUtilitiesComponents } from '@egovernments/digit-ui-module-utilities';
import { UICustomizations } from './Customisations/UICustomizations';

window.contextPath = window?.globalConfigs?.getConfig('CONTEXT_PATH');
window.Digit.Customizations = {
  commonUiConfig: UICustomizations,
};

initLibraries();
initFSMLibraries();

const enabledModules = ['Tqm'];
window.Digit.ComponentRegistryService.setupRegistry({
  ...paymentConfigs,
  PaymentModule,
  PaymentLinks,
});
initCoreComponents();
initDSSComponents();
initEngagementComponents();
initFSMComponents();
initHRMSComponents();
initUtilitiesComponents();

const moduleReducers = (initData) => ({
  initData,
});

//calling it here so that UICustomizations inside tqm gets added after the common Customizations are added
initTQMComponents();
function App() {
  window.contextPath = window?.globalConfigs?.getConfig('CONTEXT_PATH');
  const stateCode =
    window.globalConfigs?.getConfig('STATE_LEVEL_TENANT_ID') ||
    process.env.REACT_APP_STATE_LEVEL_TENANT_ID;
  if (!stateCode) {
    return <h1>stateCode is not defined</h1>;
  }
  return (
    <DigitUI
      stateCode={stateCode}
      enabledModules={enabledModules}
      moduleReducers={moduleReducers}
    />
  );
}

export default App;
