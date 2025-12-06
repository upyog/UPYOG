import React, { useState, useCallback, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardHeader, Header, Loader } from "@egovernments/digit-ui-react-components";
import DesktopInbox from "../../components/DesktopInbox";
import MobileInbox from "../../components/MobileInbox";
import { Link, useHistory, useLocation } from "react-router-dom";

const config = {
  select: (response) => {
    return {
      totalCount: response?.totalCount,
      vehicleLog: response?.vehicleTrip.map((trip) => {
        const owner = trip.tripOwner;
        const displayName = owner.name;
        const tripOwner = { ...owner, displayName };
        return { ...trip, tripOwner };
      }),
    };
  },
};

const Alerts = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [searchParamsApplication, setSearchParamsApplication] = useState(null);
  const [alerts, setAlerts] = useState(null);
  const [filterData, setFilterData] = useState(null);

  const requestCriteria = {
    url: "/trackingservice/api/v3/trip/_alerts",
    method: "GET",
    params: {
      tenantId: tenantId,
    },
  };
  const { isLoading, data: alertsData, revalidate } = Digit.Hooks.useCustomAPIHook(requestCriteria);

  const { isLoading: isSearchLoading, data: { totalCount, vehicleLog } = {}, isSuccess } = Digit.Hooks.fsm.useVehicleSearch({
    tenantId,
    filters: searchParamsApplication,
    config,
    options: { searchWithDSO: false },
  });

  useEffect(() => {
    setAlerts(alertsData);
  }, [alertsData, isLoading]);

  useEffect(() => {
    if (alerts && !searchParamsApplication?.refernceNos) {
      const applicationNos = alerts?.map((i) => i?.applicationNo).join(",");
      setSearchParamsApplication({
        refernceNos: applicationNos ? applicationNos : "null",
      });
    }
  }, [alerts, searchParamsApplication]);

  const onSearch = (params = {}) => {
    const isSearch = params?.applicationNos?.length > 0 ? true : false;
    setSearchParamsApplication({
      refernceNos: params?.applicationNos,
    });
    const filterAlerts = isSearch ? alerts.filter((i) => i.applicationNo === params.applicationNos) : null;
    if (!isSearch) {
      setFilterData(null);
    }
    isSearch && filterAlerts !== null ? setFilterData(filterAlerts) : null;
  };

  const searchFields = [
    {
      label: t("ES_SEARCH_APPLICATION_APPLICATION_NO"),
      name: "applicationNos",
    },
  ];

  const handleSort = useCallback((args) => {
    if (args?.length === 0) return;
    setSortParams(args);
  }, []);

  return (
    <div>
      <Header>{t("ES_FSM_ALERTS")}</Header>
      <DesktopInbox
        data={{ table: vehicleLog }}
        alertsData={alerts}
        isLoading={isSearchLoading || isLoading}
        onSort={handleSort}
        isAlertsData={true}
        disableSort={true}
        userRole={"FSM_ALERTS"}
        searchFields={searchFields}
        onSearch={onSearch}
        totalRecords={0}
        isPaginationRequired={false}
      />
    </div>
  );
};

export default Alerts;
