import { Card, Loader } from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import ApplicationTable from "./inbox/ApplicationTable";
import InboxLinks from "./inbox/InboxLink";
import SearchApplication from "./inbox/search";

const DesktopInbox = ({ tableConfig, filterComponent, ...props }) => {
  const { data, useNewInboxAPI } = props;
  const { t } = useTranslation();
  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));
  const [EmptyInboxComp, setEmptyInboxComp] = useState(() => {
    const com = Digit.ComponentRegistryService?.getComponent(props.EmptyResultInboxComp);
    return com;
  });

  const parseValue = (value) => {
    try {
      return JSON.parse(value)
    } catch (e) {
      return value
    }
  }
  const getFromStorage = (key) => {
    const value = window.localStorage.getItem(key);
    return value && value !== "undefined" ? parseValue(value) : null;
  }
  const employeeToken = getFromStorage("Employee.token")
  const employeeInfo = getFromStorage("Employee.user-info")
  const getUserDetails = (access_token, info) => ({ token: access_token, access_token, info })

  const userDetails = getUserDetails(employeeToken, employeeInfo)
  
  let userRole='';
  if(userDetails && userDetails.info && userDetails.info?.roles) {
    userDetails.info.roles.map((role)=>{
      if(role?.code == "ASSIGNING_OFFICER") {
        userRole = role.code;
      } else if(role?.code == "EXECUTING_OFFICER") {
        userRole = role.code;
      }
    })
  }

  const [clearSearchCalled, setClearSearchCalled] = useState(false);

  const columns = React.useMemo(() => userRole && userRole=='ASSIGNING_OFFICER' ? (tableConfig.inboxColumnsAssessment(props) || []) : userRole && userRole=='EXECUTING_OFFICER' ? (tableConfig.inboxColumnsAppeal(props) || []) : (props.isSearch ? tableConfig.searchColumns(props) : tableConfig.inboxColumns(props) || []), []);

  let result;
  if (props.isLoading) {
    result = <Loader />;
  } else if (clearSearchCalled) {
    result = null;
  } else if (!data || data?.length === 0 || (useNewInboxAPI && data?.[0].dataEmpty)) {
    result =
      (EmptyInboxComp && <EmptyInboxComp data={data} />) ||
      (data?.length === 0 || (useNewInboxAPI && data?.[0].dataEmpty) ? (
        <Card style={{ marginTop: 20 }}>
          {t("CS_MYAPPLICATIONS_NO_APPLICATION")
            .split("\\n")
            .map((text, index) => (
              <p key={index} style={{ textAlign: "center" }}>
                {text}
              </p>
            ))}
        </Card>
      ) : (
        <Loader />
      ));
  } else if (data?.length > 0) {
    result = (
      <ApplicationTable
        t={t}
        data={data}
        columns={columns}
        getCellProps={(cellInfo) => {
          return {
            style: {
              minWidth: cellInfo.column.Header === t("ES_INBOX_APPLICATION_NO") ? "240px" : "",
              // padding: "20px 18px",
              fontSize: "16px",
            },
          };
        }}
        onPageSizeChange={props.onPageSizeChange}
        currentPage={props.currentPage}
        onNextPage={props.onNextPage}
        onPrevPage={props.onPrevPage}
        pageSizeLimit={props.pageSizeLimit}
        onSort={props.onSort}
        disableSort={props.disableSort}
        sortParams={props.sortParams}
        totalRecords={props.totalRecords}
      />
    );
  }

  return (
    <div className="inbox-container">
      {!props.isSearch && (
        <div className="filters-container">
          <InboxLinks parentRoute={props.parentRoute} businessService={props.moduleCode} />
          <div>
            {
              <FilterComponent
                defaultSearchParams={props.defaultSearchParams}
                onFilterChange={props.onFilterChange}
                searchParams={props.searchParams}
                type="desktop"
                useNewInboxAPI={useNewInboxAPI}
                statusMap={useNewInboxAPI ? data?.[0].statusMap : null}
                moduleCode={props.moduleCode}
              />
            }
          </div>
        </div>
      )}
      <div style={{ flex: 1 }}>
        <SearchApplication
          defaultSearchParams={props.defaultSearchParams}
          onSearch={(d) => {
            props.onSearch(d);
            setClearSearchCalled(false);
          }}
          type="desktop"
          searchFields={props.searchFields}
          isInboxPage={!props?.isSearch}
          searchParams={props.searchParams}
          clearSearch={() => setClearSearchCalled(true)}
        />
        <div className="result" style={{ marginLeft: !props?.isSearch ? "24px" : "", flex: 1, width: "100%" }}>
          {result}
        </div>
      </div>
    </div>
  );
};

export default DesktopInbox;
