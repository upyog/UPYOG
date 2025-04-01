import React from 'react';
import ReactDOM from 'react-dom';

import { initLibraries } from '@egovernments/digit-ui-libraries';
import { initFSMLibraries } from '@egovernments/digit-ui-fsm-libraries';
import {
  PaymentModule,
  PaymentLinks,
  paymentConfigs,
} from '@egovernments/digit-ui-module-common';
import { initDSSComponents } from '@egovernments/digit-ui-module-dss';
import { initFSMComponents } from '@egovernments/digit-ui-module-fsm';
import { initEngagementComponents } from '@egovernments/digit-ui-module-engagement';
import {
  DigitUI,
  initCoreComponents,
} from '@egovernments/digit-ui-module-core';
import {
  HRMSModule,
  initHRMSComponents,
} from '@egovernments/digit-ui-module-hrms';
import { initUtilitiesComponents } from '@egovernments/digit-ui-module-utilities';
import { initTQMComponents } from '@egovernments/digit-ui-module-tqm';
import { UICustomizations } from './UICustomizations';
import '@egovernments/digit-ui-fsm-css/example/index.css';

var Digit = window.Digit || {};

const enabledModules = ['Tqm'];

const initTokens = (stateCode) => {
  const userType =
    window.sessionStorage.getItem('userType') ||
    process.env.REACT_APP_USER_TYPE ||
    'CITIZEN';

  const token =
    window.localStorage.getItem('token') ||
    process.env[`REACT_APP_${userType}_TOKEN`];

  const citizenInfo = window.localStorage.getItem('Citizen.user-info');

  const citizenTenantId =
    window.localStorage.getItem('Citizen.tenant-id') || stateCode;

  const employeeInfo = window.localStorage.getItem('Employee.user-info');
  const employeeTenantId = window.localStorage.getItem('Employee.tenant-id');

  const userTypeInfo =
    userType === 'CITIZEN' || userType === 'QACT' ? 'citizen' : 'employee';
  window.Digit.SessionStorage.set('user_type', userTypeInfo);
  window.Digit.SessionStorage.set('userType', userTypeInfo);

  if (userType !== 'CITIZEN') {
    window.Digit.SessionStorage.set('User', {
      access_token: token,
      info: userType !== 'CITIZEN' ? JSON.parse(employeeInfo) : citizenInfo,
    });
  } else {
    // if (!window.Digit.SessionStorage.get("User")?.extraRoleInfo) window.Digit.SessionStorage.set("User", { access_token: token, info: citizenInfo });
  }

  window.Digit.SessionStorage.set('Citizen.tenantId', citizenTenantId);

  if (employeeTenantId && employeeTenantId.length)
    window.Digit.SessionStorage.set('Employee.tenantId', employeeTenantId);
};

const initDigitUI = () => {
  window.contextPath =
    window?.globalConfigs?.getConfig('CONTEXT_PATH') || 'sanitation-ui';

  window.Digit.ComponentRegistryService.setupRegistry({
    ...paymentConfigs,
    PaymentModule,
    PaymentLinks,
  });

  // initPGRComponents();
  initCoreComponents();
  initDSSComponents();
  initEngagementComponents();
  // initWorksComponents();
  initHRMSComponents();
  initFSMComponents();
  initUtilitiesComponents();

  const moduleReducers = (initData) => initData;
  window.Digit.Customizations = {
    TL: {
      customiseCreateFormData: (formData, licenceObject) => licenceObject,
      customiseRenewalCreateFormData: (formData, licenceObject) =>
        licenceObject,
      customiseSendbackFormData: (formData, licenceObject) => licenceObject,
    },
    commonUiConfig: UICustomizations,
  };

  //calling it here so that UICustomizations inside tqm gets added after the common Customizations are added
  initTQMComponents();

  const stateCode =
    window?.globalConfigs?.getConfig('STATE_LEVEL_TENANT_ID') || 'pb';
  initTokens(stateCode);

  const registry = window?.Digit.ComponentRegistryService.getRegistry();
  ReactDOM.render(
    <DigitUI
      stateCode={stateCode}
      enabledModules={enabledModules}
      moduleReducers={moduleReducers}
    />,
    document.getElementById('root')
  );
};

initLibraries().then(() => {
  initFSMLibraries();
  initDigitUI();
});
