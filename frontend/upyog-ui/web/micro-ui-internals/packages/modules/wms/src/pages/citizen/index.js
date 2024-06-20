import {AppContainer, PrivateRoute,BackButton } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
const CitizenApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const inboxInitialState = {
    searchParams: {
      tenantId: tenantId,
    },
  };
  const hideBackButtonConfig = [
    { screenPath: "" },
  ];
  const WmsSorList = Digit?.ComponentRegistryService?.getComponent("WmsSorList");
  const WmsSorCreate = Digit?.ComponentRegistryService?.getComponent("WmsSorCreate");
  const WmsSorDetails = Digit?.ComponentRegistryService?.getComponent("WmsSorDetails");  
  const WmsSorEdit = Digit?.ComponentRegistryService?.getComponent("WmsSorEdit");
  const WmsSorResponse = Digit?.ComponentRegistryService?.getComponent("WmsSorResponse");
  
  const WmsSchList = Digit?.ComponentRegistryService?.getComponent("WmsSchList");
  const WmsSchCreate = Digit?.ComponentRegistryService?.getComponent("WmsSchCreate");
  const WmsSchDetails = Digit?.ComponentRegistryService?.getComponent("WmsSchDetails");  
  const WmsSchEdit = Digit?.ComponentRegistryService?.getComponent("WmsSchEdit");
  const WmsSchResponse = Digit?.ComponentRegistryService?.getComponent("WmsSchResponse");

  const WmsPrjList = Digit?.ComponentRegistryService?.getComponent("WmsPrjList");
  const WmsPrjCreate = Digit?.ComponentRegistryService?.getComponent("WmsPrjCreate");
  const WmsPrjDetails = Digit?.ComponentRegistryService?.getComponent("WmsPrjDetails");  
  const WmsPrjEdit = Digit?.ComponentRegistryService?.getComponent("WmsPrjEdit");
  const WmsPrjResponse = Digit?.ComponentRegistryService?.getComponent("WmsPrjResponse");

  const WmsPhmList = Digit?.ComponentRegistryService?.getComponent("WmsPhmList");
  const WmsPhmCreate = Digit?.ComponentRegistryService?.getComponent("WmsPhmCreate");
  const WmsPhmDetails = Digit?.ComponentRegistryService?.getComponent("WmsPhmDetails");  
  const WmsPhmEdit = Digit?.ComponentRegistryService?.getComponent("WmsPhmEdit");
  const WmsPhmResponse = Digit?.ComponentRegistryService?.getComponent("WmsPhmResponse");

  const WmsPmaList = Digit?.ComponentRegistryService?.getComponent("WmsPmaList");
  const WmsPmaCreate = Digit?.ComponentRegistryService?.getComponent("WmsPmaCreate");
  const WmsPmaDetails = Digit?.ComponentRegistryService?.getComponent("WmsPmaDetails");  
  const WmsPmaEdit = Digit?.ComponentRegistryService?.getComponent("WmsPmaEdit");
  const WmsPmaResponse = Digit?.ComponentRegistryService?.getComponent("WmsPmaResponse");

  const WmsMbList = Digit?.ComponentRegistryService?.getComponent("WmsMbList");
  const WmsMbCreate = Digit?.ComponentRegistryService?.getComponent("WmsMbCreate");
  const WmsMbDetails = Digit?.ComponentRegistryService?.getComponent("WmsMbDetails");  
  const WmsMbEdit = Digit?.ComponentRegistryService?.getComponent("WmsMbEdit");
  const WmsMbResponse = Digit?.ComponentRegistryService?.getComponent("WmsMbResponse");

  const WmsDrList = Digit?.ComponentRegistryService?.getComponent("WmsDrList");
  const WmsDrCreate = Digit?.ComponentRegistryService?.getComponent("WmsDrCreate");
  const WmsDrDetails = Digit?.ComponentRegistryService?.getComponent("WmsDrDetails");  
  const WmsDrEdit = Digit?.ComponentRegistryService?.getComponent("WmsDrEdit");
  const WmsDrResponse = Digit?.ComponentRegistryService?.getComponent("WmsDrResponse");

  const WmsPrList = Digit?.ComponentRegistryService?.getComponent("WmsPrList");
  const WmsPrCreate = Digit?.ComponentRegistryService?.getComponent("WmsPrCreate");
  const WmsPrDetails = Digit?.ComponentRegistryService?.getComponent("WmsPrDetails");  
  const WmsPrEdit = Digit?.ComponentRegistryService?.getComponent("WmsPrEdit");
  const WmsPrResponse = Digit?.ComponentRegistryService?.getComponent("WmsPrResponse");

  const WmsWsrList = Digit?.ComponentRegistryService?.getComponent("WmsWsrList");
  const WmsWsrCreate = Digit?.ComponentRegistryService?.getComponent("WmsWsrCreate");
  const WmsWsrDetails = Digit?.ComponentRegistryService?.getComponent("WmsWsrDetails");  
  const WmsWsrEdit = Digit?.ComponentRegistryService?.getComponent("WmsWsrEdit");
  const WmsWsrResponse = Digit?.ComponentRegistryService?.getComponent("WmsWsrResponse");



  const CMView = Digit?.ComponentRegistryService?.getComponent("CMView");
  const WMSDetails = Digit?.ComponentRegistryService?.getComponent("WMSDetails");
  const ContrMasterAdd = Digit?.ComponentRegistryService?.getComponent("ContrMasterAdd");
  const ContrMasterView = Digit?.ComponentRegistryService?.getComponent("ContrMasterView");
  const ContractViewTableTest = Digit?.ComponentRegistryService?.getComponent("ContractViewTableTest");
  const ViewBankTable = Digit?.ComponentRegistryService?.getComponent("ViewBankTable");
  const BankAdd = Digit?.ComponentRegistryService?.getComponent("BankAdd");
  const AppEditBank = Digit?.ComponentRegistryService?.getComponent("AppEditBank");
  const View = Digit?.ComponentRegistryService?.getComponent("View");
  const VendorTypeAdd = Digit?.ComponentRegistryService?.getComponent("VendorTypeAdd");
  const VendorTypeEdit = Digit?.ComponentRegistryService?.getComponent("VendorTypeEdit");
  const EditCitizen = Digit?.ComponentRegistryService?.getComponent("WMSEditCitizen");

  const EditSubType = Digit?.ComponentRegistryService?.getComponent("EditSubType");
  const AddSubType = Digit?.ComponentRegistryService?.getComponent("AddSubType");
  const ViewSubType = Digit?.ComponentRegistryService?.getComponent("ViewSubType");

  const VendorClassView = Digit?.ComponentRegistryService?.getComponent("VendorClassView");
  const VendorClassAdd = Digit?.ComponentRegistryService?.getComponent("VendorClassAdd");
  const VendorClassEdit = Digit?.ComponentRegistryService?.getComponent("VendorClassEdit");

  const AccountHeadAdd = Digit?.ComponentRegistryService?.getComponent("AccountHeadAdd");
  const AccountHeadView = Digit?.ComponentRegistryService?.getComponent("AccountHeadView");
  const AccountHeadEdit = Digit?.ComponentRegistryService?.getComponent("AccountHeadEdit");
  
  const FunctionAppAdd = Digit?.ComponentRegistryService?.getComponent("FunctionAppAdd");
  const FunctionAppEdit = Digit?.ComponentRegistryService?.getComponent("FunctionAppEdit");
  const FunctionAppView = Digit?.ComponentRegistryService?.getComponent("FunctionAppView");

  const TenderEntryAdd = Digit?.ComponentRegistryService?.getComponent("TenderEntryAdd");
  const TenderEntryView = Digit?.ComponentRegistryService?.getComponent("TenderEntryView");
  const EditTender = Digit?.ComponentRegistryService?.getComponent("EditTender");
  
  const DepartmentAdd = Digit?.ComponentRegistryService?.getComponent("DepartmentAdd");
  const DepartmentView = Digit?.ComponentRegistryService?.getComponent("DepartmentView");
  const DepartmentEdit = Digit?.ComponentRegistryService?.getComponent("DepartmentEdit");
  
  const TenderCategoryAdd = Digit?.ComponentRegistryService?.getComponent("TenderCategoryAdd");
  const TenderCategoryView = Digit?.ComponentRegistryService?.getComponent("TenderCategoryView");
  const TenderCategoryEdit = Digit?.ComponentRegistryService?.getComponent("TenderCategoryEdit");
  const MasterPageList = Digit?.ComponentRegistryService?.getComponent("MasterPageList");

  const ContractAgreementAdd = Digit?.ComponentRegistryService?.getComponent("ContractAgreementAdd");
  const ContractAgreementEdit = Digit?.ComponentRegistryService?.getComponent("ContractAgreementEdit");
  const ContractAgreementView = Digit?.ComponentRegistryService?.getComponent("ContractAgreementView");
  const ContractAgreementDetail = Digit?.ComponentRegistryService?.getComponent("ContractAgreementDetail");
  
  const WmsRAFBCreate = Digit?.ComponentRegistryService?.getComponent("WmsRAFBCreate");
  const WmsRAFBCreateTest = Digit?.ComponentRegistryService?.getComponent("WmsRAFBCreateTest");
  const WmsRAFBEdit = Digit?.ComponentRegistryService?.getComponent("WmsRAFBEdit");
  const WmsRAFBDetail = Digit?.ComponentRegistryService?.getComponent("WmsRAFBDetail");
  const WmsRAFBList = Digit?.ComponentRegistryService?.getComponent("WmsRAFBList");
  // const WMSCheckPage = Digit?.ComponentRegistryService?.getComponent("WMSCheckPage");
  const TenderEntryDetail = Digit?.ComponentRegistryService?.getComponent("TenderEntryDetail");
  
  
  return (
    <span className={"pt-citizen"}>
    <Switch>
      <AppContainer>
      {shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
        <div className="ground-container">
        
          <PrivateRoute
            path={`${path}/sor-home`}
            component={() => (
              <WmsSorList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
        <PrivateRoute path={`${path}/master-data`} component={() => <MasterPageList />} />

          <PrivateRoute path={`${path}/sor-create`} component={() => <WmsSorCreate />} />
          <PrivateRoute path={`${path}/sor-details/:id`} component={() => <WmsSorDetails />} />
          <PrivateRoute path={`${path}/sor-edit/:id`} component={() => <WmsSorEdit />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <WmsSorResponse {...props} parentRoute={path} />} />
          
          <PrivateRoute
            path={`${path}/sch-home`}
            component={() => (
              <WmsSchList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/sch-create`} component={() => <WmsSchCreate />} />
          <PrivateRoute path={`${path}/sch-details/:id`} component={() => <WmsSchDetails />} />
          <PrivateRoute path={`${path}/sch-edit/:id`} component={() => <WmsSchEdit />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <WmsSchResponse {...props} parentRoute={path} />} />

          <PrivateRoute
            path={`${path}/prjmst-home`}
            component={() => (
              <WmsPrjList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/prjmst-create`} component={() => <WmsPrjCreate />} />
          <PrivateRoute path={`${path}/prjmst-details/:id`} component={() => <WmsPrjDetails />} />
          <PrivateRoute path={`${path}/prjmst-edit/:id`} component={() => <WmsPrjEdit />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <WmsPrjResponse {...props} parentRoute={path} />} />
          
	  <PrivateRoute
            path={`${path}/phm-home`}
            component={() => (
              <WmsPhmList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/phm-create`} component={() => <WmsPhmCreate />} />
          <PrivateRoute path={`${path}/phm-details/:id`} component={() => <WmsPhmDetails />} />
          <PrivateRoute path={`${path}/phm-edit/:id`} component={() => <WmsPhmEdit />} />
          <PrivateRoute path={`${path}/phmresponse`} component={(props) => <WmsPhmResponse {...props} parentRoute={path} />} />

          <PrivateRoute
            path={`${path}/pma-home`}
            component={() => (
              <WmsPmaList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/pma-create`} component={() => <WmsPmaCreate />} />
          <PrivateRoute path={`${path}/pma-details/:id`} component={() => <WmsPmaDetails />} />
          <PrivateRoute path={`${path}/pma-edit/:id`} component={() => <WmsPmaEdit />} />
          <PrivateRoute path={`${path}/pmaresponse`} component={(props) => <WmsPmaResponse {...props} parentRoute={path} />} />
    
          <PrivateRoute
            path={`${path}/mb-home`}
            component={() => (
              <WmsMbList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
        <PrivateRoute path={`${path}/master-data`} component={() => <MasterPageList />} />

          <PrivateRoute path={`${path}/mb-create`} component={() => <WmsMbCreate />} />
          <PrivateRoute path={`${path}/mb-details/:id`} component={() => <WmsMbDetails />} />
          <PrivateRoute path={`${path}/mb-edit/:id`} component={() => <WmsMbEdit />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <WmsMbResponse {...props} parentRoute={path} />} />

        <PrivateRoute
            path={`${path}/dr-home`}
            component={() => (
              <WmsDrList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/dr-create`} component={() => <WmsDrCreate />} />
          <PrivateRoute path={`${path}/dr-details/:id`} component={() => <WmsDrDetails />} />
          <PrivateRoute path={`${path}/dr-edit/:id`} component={() => <WmsDrEdit />} />
          <PrivateRoute path={`${path}/drresponse`} component={(props) => <WmsDrResponse {...props} parentRoute={path} />} />

          <PrivateRoute
            path={`${path}/pr-home`}
            component={() => (
              <WmsPrList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/pr-create`} component={() => <WmsPrCreate />} />
          <PrivateRoute path={`${path}/pr-details/:id`} component={() => <WmsPrDetails />} />
          <PrivateRoute path={`${path}/pr-edit/:id`} component={() => <WmsPrEdit />} />
          <PrivateRoute path={`${path}/prresponse`} component={(props) => <WmsPrResponse {...props} parentRoute={path} />} />

          <PrivateRoute
            path={`${path}/wsr-home`}
            component={() => (
              <WmsWsrList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/wsr-create`} component={() => <WmsWsrCreate />} />
          <PrivateRoute path={`${path}/wsr-details/:id`} component={() => <WmsWsrDetails />} />
          <PrivateRoute path={`${path}/wsr-edit/:id`} component={() => <WmsWsrEdit />} />
          <PrivateRoute path={`${path}/wsrresponse`} component={(props) => <WmsWsrResponse {...props} parentRoute={path} />} />

          <PrivateRoute path={`${path}/cm-home`} component={props => <CMView {...props} tenants={tenantId} parentRoute={path} />} />
          <PrivateRoute path={`${path}/details/:tenantId/:id`} component={() => <WMSDetails />} />
          <PrivateRoute path={`${path}/add`} component={ContrMasterAdd} />
          <PrivateRoute path={`${path}/cm-table-view`} component={ContrMasterView} />
          <PrivateRoute path={`${path}/cm-table-test`} component={ContractViewTableTest} />
          <PrivateRoute path={`${path}/bank/list`} component={ViewBankTable} />
          <PrivateRoute path={`${path}/bank/add`} component={BankAdd} />
          <PrivateRoute path={`${path}/bank/edit/:tenantId/:id`} component={AppEditBank} />
          <PrivateRoute path={`${path}/vendor-type/list`} component={View} />
          <PrivateRoute path={`${path}/vendor-type/add`} component={VendorTypeAdd} />
          <PrivateRoute path={`${path}/vendor-type/edit/:tenantId/:id`} component={VendorTypeEdit} />
          <PrivateRoute path={`${path}/vendor-sub-type/list`} component={ViewSubType} />
          <PrivateRoute path={`${path}/vendor-sub-type/add`} component={AddSubType} />
          <PrivateRoute path={`${path}/vendor-sub-type/edit/:tenantId/:id`} component={EditSubType} />
          <PrivateRoute path={`${path}/edit/:tenantId/:id`} component={() => <EditCitizen />} /> 
          <PrivateRoute path={`${path}/vendor-class/list`} component={VendorClassView} />
          <PrivateRoute path={`${path}/vendor-class/add`} component={VendorClassAdd} />
          <PrivateRoute path={`${path}/vendor-class/edit/:tenantId/:id`} component={VendorClassEdit} />

          <PrivateRoute path={`${path}/account-head/list`} component={AccountHeadView} />
          <PrivateRoute path={`${path}/account-head/add`} component={AccountHeadAdd} />
          <PrivateRoute path={`${path}/account-head/edit/:tenantId/:id`} component={AccountHeadEdit} />

          <PrivateRoute path={`${path}/function-app/list`} component={FunctionAppView} />
          <PrivateRoute path={`${path}/function-app/add`} component={FunctionAppAdd} />
          <PrivateRoute path={`${path}/function-app/edit/:tenantId/:id`} component={FunctionAppEdit} />

            {/*Tender Entry*/}  

            <PrivateRoute path={`${path}/tender-entry/add`} component={TenderEntryAdd} />
            <PrivateRoute path={`${path}/tender-entry/home`} component={TenderEntryView} />
            <PrivateRoute path={`${path}/tender-entry/edit/:tenantId/:id`} component={EditTender} />
            <PrivateRoute path={`${path}/tender-entry/detail/:tenantId/:id`} component={TenderEntryDetail} />

            <PrivateRoute path={`${path}/tender-entry/department/add`} component={DepartmentAdd} />
            <PrivateRoute path={`${path}/tender-entry/department/list`} component={DepartmentView} />
            <PrivateRoute path={`${path}/tender-entry/department/edit/:tenantId/:id`} component={DepartmentEdit} />
            
            <PrivateRoute path={`${path}/tender-entry/tender-category/add`} component={TenderCategoryAdd} />
            <PrivateRoute path={`${path}/tender-entry/tender-category/list`} component={TenderCategoryView} />
            <PrivateRoute path={`${path}/tender-entry/tender-category/edit/:tenantId/:id`} component={TenderCategoryEdit} />

            {/*Contract Agreement*/}  
            <PrivateRoute path={`${path}/contract-agreement/add`} component={() => <ContractAgreementAdd />} />
            <PrivateRoute path={`${path}/contract-agreement/edit/:tenantId/:id`} component={() => <ContractAgreementEdit />} />
            <PrivateRoute path={`${path}/contract-agreement/detail/:tenantId/:id`} component={() => <ContractAgreementDetail />} />
            <PrivateRoute path={`${path}/contract-agreement/list`} component={() => <ContractAgreementView />} />
            
            {/* Running Account / Final Bill */}
            <PrivateRoute path={`${path}/running-account/add`} component={() => <WmsRAFBCreate />} />
            <PrivateRoute path={`${path}/running-account/addTest`} component={() => <WmsRAFBCreateTest />} />
            <PrivateRoute path={`${path}/running-account/edit/:tenantId/:id`} component={() => <WmsRAFBEdit />} />
            <PrivateRoute path={`${path}/running-account/detail/:tenantId/:id`} component={() => <WmsRAFBDetail />} />
            <PrivateRoute path={`${path}/running-account/list`} component={() => <WmsRAFBList filterComponent="FINAL_BILL_SEARCH_FILTER"  initialStates={inboxInitialState} isInbox={true} />} />
            {/* <PrivateRoute path={`${path}/running-account/check`} component={() => <WMSCheckPage />} /> */}
            
        </div>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default CitizenApp;
