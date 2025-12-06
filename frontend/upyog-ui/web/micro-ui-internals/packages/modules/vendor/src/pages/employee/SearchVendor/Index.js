import React, { useState, useCallback, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@upyog/digit-ui-react-components";
//import RegisryInbox from "../../../components/RegistryInbox";
import VendorInbox from "../../../components/VendorInbox";
import { useHistory } from "react-router-dom";

const SearchVendor = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [searchParams, setSearchParams] = useState({});
  const [sortParams, setSortParams] = useState([{ id: "createdTime", desc: true }]);
  const [pageOffset, setPageOffset] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [tab, setTab] = useState("VENDOR");
  const [vehicleIds, setVehicleIds] = useState("");
  const [driverIds, setDriverIds] = useState("");
  const [tableData, setTableData] = useState([]);

  const userInfo = Digit.UserService.getUser();

 //Read URL parameter to set the active tab when page loads
  useEffect(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const selectedTab = urlParams.get('selectedTabs');
  if (selectedTab && ['VENDOR', 'VEHICLE', 'DRIVER'].includes(selectedTab)) {
    setTab(selectedTab);
  }
}, []);


  let paginationParms = { limit: pageSize, offset: pageOffset, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };

    const { data: dsoData, isLoading: isLoading, isSuccess: isDsoSuccess, error: dsoError, refetch } =
      tab === "VEHICLE"
        ? Digit.Hooks.fsm.useVehiclesSearch({    //
            tenantId,
            filters: {
              ...paginationParms,
              registrationNumber: searchParams?.registrationNumber,
              status: "ACTIVE,DISABLED",
            },
            config: { enabled: false },
          })
        : tab === "DRIVER"
        ? Digit.Hooks.fsm.useDriverSearch({
            tenantId,
            filters: {
              ...paginationParms,
              name: searchParams?.name,
              status: "ACTIVE,DISABLED",
            },
            config: { enabled: false },
          })
        : Digit.Hooks.fsm.useVendorSearch({
            tenantId,
            filters: {
              ...paginationParms,
              name: searchParams?.name,
              status: "ACTIVE,DISABLED",
            },
            config: { enabled: false },
          });

  //this is for specially vendor search
  //const { data: dsoData, isLoading, isSuccess: isDsoSuccess, error: dsoError, refetch } =
    // console.log("ddaaddaatttaaaa", data);
    // console.log("issucceesssss", isSuccess);

    Digit.Hooks.fsm.useVendorSearch({
      tenantId,
      filters: {
        ...paginationParms,
        name: searchParams?.name,
        status: "ACTIVE,DISABLED",
      },
      config: { enabled: false },
    });

  const {
    data: vendorData,
    isLoading: isVendorLoading,
    isSuccess: isVendorSuccess,
    error: vendorError,
    refetch: refetchVendor,
  } = Digit.Hooks.fsm.useDsoSearch(
    tenantId,
    {
      vehicleIds: vehicleIds,
      driverIds: driverIds,
      status: "ACTIVE",
    },
    { enabled: false }
  );



  
  const inboxTotalCount = dsoData?.totalCount || 50;

  useEffect(() => {
    refetch();
  }, [tab]);

  useEffect(() => {
    refetch();
  }, [searchParams, sortParams, pageOffset, pageSize]);

  useEffect(() => {
    if (dsoData?.vehicle && tab === "VEHICLE") {
      let vehicleIds = "";
      dsoData.vehicle.map((data) => {
        vehicleIds += `${data.id},`;
      });
      setVehicleIds(vehicleIds);
      setTableData(dsoData.vehicle);
    }
    if (dsoData?.driver && tab === "DRIVER") {
      let driverIds = "";
      dsoData.driver.map((data) => {
        driverIds += `${data.id},`;
      });
      setDriverIds(driverIds);
      setTableData(dsoData?.driver);
    }
    if (dsoData?.vendor && tab === "VENDOR") {
      const tableData = dsoData.vendor.map((dso) => ({
        mobileNumber: dso.owner?.mobileNumber,
        name: dso.name,
        id: dso.id,
        auditDetails: dso.auditDetails,
        drivers: dso.drivers,
        activeDrivers: dso.drivers?.filter((driver) => driver.status === "ACTIVE"),
        allVehicles: dso.vehicles,
        dsoDetails: dso,
        vehicles: dso.vehicles
          ?.filter((vehicle) => vehicle.status === "ACTIVE")
          ?.map((vehicle) => ({
            id: vehicle.id,
            registrationNumber: vehicle?.registrationNumber,
            type: vehicle.type,
            i18nKey: `FSM_VEHICLE_TYPE_${vehicle.type}`,
            capacity: vehicle.tankCapacity,
            suctionType: vehicle.suctionType,
            model: vehicle.model,
          })),
      }));
      setTableData(tableData);
    }
  }, [dsoData]);

  useEffect(() => {
    if (vehicleIds !== "" || driverIds !== "") refetchVendor();
  }, [vehicleIds, driverIds]);

  useEffect(() => {
    if (vendorData) {
      if (tab === "VEHICLE") {
        const vehicles = dsoData?.vehicle.map((data) => {
          let vendor = vendorData.find((ele) => ele.dsoDetails?.vehicles?.find((vehicle) => vehicle.id === data.id));
          if (vendor) {
            data.vendor = vendor.dsoDetails;
          }
          return data;
        });
        setTableData(vehicles);
        setVehicleIds("");
      }
      if (tab === "DRIVER") {
        const drivers = dsoData?.driver.map((data) => {
          let vendor = vendorData.find((ele) => ele.dsoDetails?.drivers?.find((driver) => driver.id === data.id));
          if (vendor) {
            data.vendor = vendor.dsoDetails;
          }
          return data;
        });
        setTableData(drivers);
        setDriverIds("");
      }
    }
  }, [vendorData, dsoData]);

  //functions to handle search, pagination, sorting and filter
  const onSearch = (params = {}) => {
    if (tab === "VEHICLE" && params.name) {
      params.registrationNumber = params.name;
      delete params.name;
    }
    setSearchParams({ ...params });
  };

  const fetchNextPage = () => {
    setPageOffset((prevState) => prevState + pageSize);
  };

  const fetchPrevPage = () => {
    setPageOffset((prevState) => prevState - pageSize);
  };

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const handleFilterChange = () => {};

    const searchFields =
      tab === "VEHICLE"
        ? [
            {
              label: t("ES_VEHICLE_SEARCH_VEHICLE_NUMBER"),
              name: "registrationNumber",
              pattern: `[A-Z]{2}\\s{1}[0-9]{2}\\s{0,1}[A-Z]{1,2}\\s{1}[0-9]{4}`,
              title: t("ES_FSM_VEHICLE_FORMAT_TIP"),
            },
          ]
        : tab === "DRIVER"
        ? [
            {
              label: t("ES_DRIVER_SEARCH_DRIVER_NAME"),
              name: "name",
            },
          ]
        : [
            {
              label: t("ES_VENDOR_SEARCH_VENDOR_NAME"),
              name: "name",
            },
          ];

  // const searchFields = [
  //   {
  //     label: t("VENDOR_SEARCH_VENDOR_NAME"),
  //     name: "name",
  //   },
  // ];

  const handleSort = useCallback((args) => {
    if (args?.length === 0) return;
    setSortParams(args);
  }, []);

  const onTabChange = (tab) => {
  setTab(tab);
  history.push(`/upyog-ui/employee/vendor/search-vendor?selectedTabs=${tab}`);
};

  const refetchData = () => {
    refetch();
  };

  const refetchVendorData = () => {
    refetchVendor();
  };

  useEffect(() => {
    refetch();
    refetchVendor();
  }, []);

  return (
    <div>
      <Header>{t("VENDOR_SEARCH")}</Header>
      <VendorInbox
        data={{ table: tableData }}
        isLoading={isLoading || isVendorLoading}
        onSort={handleSort}
        disableSort={false}
        sortParams={sortParams}
        userRole={"FSM_ADMIN"}
        onFilterChange={handleFilterChange}
        searchFields={searchFields}
        onSearch={onSearch}
        onNextPage={fetchNextPage}
        onPrevPage={fetchPrevPage}
        currentPage={Math.floor(pageOffset / pageSize)}
        pageSizeLimit={pageSize}
        onPageSizeChange={handlePageSizeChange}
        totalRecords={inboxTotalCount || 0}
        onTabChange={onTabChange}
        selectedTab={tab}
        refetchData={refetchData}
        refetchVendor={refetchVendorData}
      />
    </div>
  );
};

export default SearchVendor;
