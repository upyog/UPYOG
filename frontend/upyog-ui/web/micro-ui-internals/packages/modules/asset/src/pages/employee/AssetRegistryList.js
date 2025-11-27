import React, { useState, useCallback, useMemo , useEffect} from "react";
import { useTranslation } from "react-i18next";
import { Header, Card, Table, Loader } from "@upyog/digit-ui-react-components";
import SearchAssetForUP from "../../components/SearchAssetForUP";

const AssetRegistryList = ({ parentRoute }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [payload, setPayload] = useState({});
  
  const [showToast, setShowToast] = useState(null);
 
  const config = {
    enabled: !!(payload && Object.keys(payload).length > 0),
  };

  const {  isSuccess, isError, data: { Assets: searchReult, Count: count } = {} } = Digit.Hooks.asset.useASSETSearch(
    { tenantId, filters: {} },
    config
  );

  function onSubmit (_data) {
    setPayload({randomId: Math.random()});
  }

  // Call onSubmit only once when component mounts
  useEffect(() => {
    onSubmit();
  }, []);

  //toaster msg with
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 1500); // Close toast after 1.5 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);

  const columns = useMemo(
    () => [
      {
        Header: t("ASSET_ID"),
        accessor: "id",
        disableSortBy: true,
      },
      {
        Header: t("ASSET_NAME"),
        accessor: "assetName",
        disableSortBy: true,
      },
      {
        Header: t("ASSET_CLASSIFICATION"),
        accessor: "assetClassification",
        disableSortBy: true,
      },
      {
        Header: t("DEPARTMENT"),
        accessor: "department",
        disableSortBy: true,
      },
      {
        Header: t("STATUS"),
        accessor: "status",
        disableSortBy: true,
      },
      {
        Header: t("ACTIONS"),
        accessor: "actions",
        disableSortBy: true,
        Cell: ({ row }) => (
          <div>
            <button onClick={() => handleView(row.original)} style={{ marginRight: "10px", padding: "5px 10px" }}>
              {t("VIEW")}
            </button>
            <button onClick={() => handleEdit(row.original)} style={{ padding: "5px 10px" }}>
              {t("EDIT")}
            </button>
          </div>
        ),
      },
    ],
    [t]
  );

  const handleView = (asset) => {
    // Navigate to view page
    window.location.href = `/upyog-ui/employee/asset/assetservice/application-details/${asset.id}`;
  };

  const handleEdit = (asset) => {
    // Navigate to edit page
    window.location.href = `/upyog-ui/employee/asset/assetservice/edit/${asset.id}`;
  };

  const onPageSizeChange = (e) => {
    setSearchParams((prev) => ({ ...prev, limit: Number(e.target.value), offset: 0 }));
  };

  const nextPage = () => {
    setSearchParams((prev) => ({ ...prev, offset: prev.offset + prev.limit }));
  };

  const previousPage = () => {
    setSearchParams((prev) => ({ ...prev, offset: Math.max(0, prev.offset - prev.limit) }));
  };

  if (false) return <Loader />;

  return <React.Fragment>
        <SearchAssetForUP t={t} isLoading={false} parentRoute={parentRoute} 
        tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} 
        data={  isSuccess ? (searchReult.length>0? searchReult : { display: "ES_COMMON_NO_DATA" } ):""} count={count} /> 
        {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          isDleteBtn={true}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </React.Fragment>
};

export default AssetRegistryList;
