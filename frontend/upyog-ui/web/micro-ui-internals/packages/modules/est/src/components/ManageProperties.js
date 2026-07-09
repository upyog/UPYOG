// table  ----  D:\UPYOG-NIUA\Hema-UPYOG-NIUA\frontend\upyog-ui\web\micro-ui-internals\packages\modules\est\src\components\ManageProperties.js

import React, { useMemo, useState, useEffect } from "react";
import { 
  Table, 
  Header, 
  SearchField, 
  TextInput, 
  Dropdown, 
  SubmitBar,
  SearchForm,
  Loader
} from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { getCreateAssetPath } from "../utils/estRoutes";

// Manage Properties Component
// This component allows users to manage properties by viewing, filtering, editing, and allotting assets.

const ManageProperties = ({ t }) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= 768);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);
  
  const { isLoading, isSuccess, data: apiData, error } = Digit.Hooks.estate.useESTAssetSearch({
    tenantId,
    filters: {"AssetSearchCriteria": {}},
  }, {
    enabled: true,
  });

 const handleEditAsset = (asset) => {
  sessionStorage.setItem("EST_EDIT_DATA", JSON.stringify(asset));

  navigate(`${getCreateAssetPath(modulePath)}?edit=true`);
};

  const [properties, setProperties] = useState([]);
  const [filteredProperties, setFilteredProperties] = useState([]);
  const { register, handleSubmit, reset } = useForm();

   const { data: assetStatusData } = Digit.Hooks.useCustomMDMS(
    Digit.ULBService.getStateId(), 
    "Estate", 
    [{ name: "AssetStatus" }],
    {
      select: (data) => {
        const formattedData = data?.["Estate"]?.["AssetStatus"];
        return formattedData;
      },
    }
  );

    const { data: assetTypeData } = Digit.Hooks.useCustomMDMS(
    Digit.ULBService.getStateId(), 
    "Estate", 
    [{ name: "AssetType" }],
    {
      select: (data) => {
        const formattedData = data?.["Estate"]?.["AssetType"];
        return formattedData;
      },
    }
  );

  let assetStatusOptions = [{ code: "", name: "All", i18nKey: "ES_COMMON_ALL" }];
  if (assetStatusData && assetStatusData.length > 0) {
    assetStatusData.forEach((status) => {
      assetStatusOptions.push({ 
        code: status.code, 
        name: status.name, 
        i18nKey: status.name 
      });
    });
  } else {
    assetStatusOptions = [
      { code: "", name: "All", i18nKey: "ES_COMMON_ALL" },
      { code: "Allotted", name: "Allotted", i18nKey: "EST_ALLOTTED" },
      { code: "Available", name: "Available", i18nKey: "EST_AVAILABLE" }
    ];
  }
  let assetTypeOptions = [{ code: "", name: "All", i18nKey: "ES_COMMON_ALL" }];
  if (assetTypeData && assetTypeData.length > 0) {
    assetTypeData.forEach((type) => {
      assetTypeOptions.push({ 
        code: type.code, 
        name: type.name, 
        i18nKey: type.name 
      });
    });
  } else {
    assetTypeOptions = [
    { code: "", name: "All", i18nKey: "ES_COMMON_ALL" },
    { code: "Commercial", name: "Commercial", i18nKey: "EST_COMMERCIAL" },
    { code: "Residential", name: "Residential", i18nKey: "EST_RESIDENTIAL" }
  ];
}
    


  useEffect(() => {
    if (isSuccess && apiData?.Assets) {
      const sortedAssets = apiData.Assets.sort((a, b) => {
        const numA = parseInt(a.estateNo.split('-').pop());
        const numB = parseInt(b.estateNo.split('-').pop());
        return numA - numB;
      });
      setProperties(sortedAssets);
      setFilteredProperties(sortedAssets);
    }
  }, [isSuccess, apiData]);

  const onFilterSubmit = (data) => {
    let filtered = properties;

    if (data.assetNumber) {
      filtered = filtered.filter(p => 
        p.estateNo?.toLowerCase().includes(data.assetNumber.toLowerCase())
      );
    }

    if (data.buildingName) {
      filtered = filtered.filter(p => 
        p.buildingName?.toLowerCase().includes(data.buildingName.toLowerCase())
      );
    }

    if (data.assetStatus && data.assetStatus !== "") {
      filtered = filtered.filter(p => p.assetStatus === data.assetStatus);
    }

    if (data.assetType && data.assetType !== "") {
      filtered = filtered.filter(p => p.assetType === data.assetType);
    }

    setFilteredProperties(filtered);
  };

  const clearFilters = () => {
    reset();
    setFilteredProperties(properties);
  };

  const GetCell = (value) => <span className="cell-text">{value || "N/A"}</span>;

  const handleAllotAsset = (asset) => {
    navigate(`${modulePath}/assignassets/info`, { state: { assetData: asset } });
  };

  const columns = useMemo(
    () => [
     {
  Header: "Asset Number",
  accessor: "estateNo",
  disableSortBy: true,
  Cell: ({ row }) => (
    <span
      style={{
        color: "#a82227",
        cursor: "pointer",
        textDecoration: "underline"
      }}
      onClick={() =>
        navigate(`${modulePath}/application-details/${row.original.estateNo}`)
      }
    >
      {row.original.estateNo}
    </span>
  ),
},
      {
        Header: "Asset Ref",
        Cell: ({ row }) => GetCell(row.original["assetRef"]),
        disableSortBy: true,
      },
      {
        Header: "Building Name",
        Cell: ({ row }) => GetCell(row.original["buildingName"]),
        disableSortBy: true,
      },
      {
        Header: "Locality",
        Cell: ({ row }) => GetCell(row.original["locality"]),
        disableSortBy: true,
      },
      {
        Header: "Plot Area",
        Cell: ({ row }) => GetCell(row.original["totalFloorArea"]),
        disableSortBy: true,
      },
      {
        Header: "Dimensions",
        Cell: ({ row }) => GetCell(`${row.original["dimensionLength"] || ""}x${row.original["dimensionWidth"] || ""}`),
        disableSortBy: true,
      },
      {
        Header: "Asset Type",
        Cell: ({ row }) => GetCell(row.original["assetType"]),
        disableSortBy: true,
      },
      {
        Header: "Rate/sqft",
        Cell: ({ row }) => GetCell(row.original["rate"]),
        disableSortBy: true,
      },
      {
        Header: "Status",
        Cell: ({ row }) => GetCell(row.original["assetStatus"]),
        disableSortBy: true,
      },
      {
        Header: "Action",
        Cell: ({ row }) => {
          const isAllotted = row.original["assetStatus"] === "Allotted";
          return (
            <div style={{ display: "flex", flexDirection: isMobile ? "column" : "row", gap: isMobile ? "4px" : "8px", justifyContent: "center" }}>
            <button
              onClick={() => !isAllotted && handleAllotAsset(row.original)}
              style={{
                backgroundColor: isAllotted ? "#ccc" : "#007bff",
                color: "white",
                border: "none",
                padding: isMobile ? "4px 8px" : "6px 10px",
                borderRadius: "4px",
                cursor: isAllotted ? "not-allowed" : "pointer",
                fontSize: isMobile ? "10px" : "12px",
                minWidth: isMobile ? "60px" : "auto"
              }}
              disabled={isAllotted}
            >
              Allot Asset
            </button>
            <button
  onClick={() => handleEditAsset(row.original)}
  style={{
    backgroundColor: "#a23c59",
    color: "white",
    border: "none",
    padding: isMobile ? "4px 8px" : "6px 10px",
    borderRadius: "4px",
    cursor: "pointer",
    fontSize: isMobile ? "10px" : "12px",
    minWidth: isMobile ? "40px" : "auto"
  }}
>
  Edit
</button>

            </div>
            
          );
        },
        disableSortBy: true,
      },
    ],
    [properties]
  );

 if (isLoading) return <Loader />;

  return (
    <div style={{ padding: isMobile ? '10px' : '20px' }}>
      <Header style={{ fontSize: isMobile ? '18px' : '24px', marginBottom: '15px' }}>Manage Properties</Header>

      <SearchForm onSubmit={onFilterSubmit} handleSubmit={handleSubmit} style={{ display: 'flex', flexDirection: isMobile ? 'column' : 'row', gap: isMobile ? '10px' : '15px', flexWrap: 'wrap' }}>
        <SearchField style={{ marginBottom: isMobile ? '10px' : '15px', flex: isMobile ? '1 1 100%' : '1 1 200px' }}>
          <label style={{ fontSize: isMobile ? '14px' : '16px', marginBottom: '5px', display: 'block' }}>{t("EST_ASSET_NUMBER")}</label>
          <TextInput name="assetNumber" inputRef={register({})} style={{ width: '100%', fontSize: isMobile ? '14px' : '16px', padding: isMobile ? '8px' : '10px' }} />
        </SearchField>

        <SearchField style={{ marginBottom: isMobile ? '10px' : '15px', flex: isMobile ? '1 1 100%' : '1 1 200px' }}>
          <label style={{ fontSize: isMobile ? '14px' : '16px', marginBottom: '5px', display: 'block' }}>{t("EST_BUILDING_NAME")}</label>
          <TextInput name="buildingName" inputRef={register({})} style={{ width: '100%', fontSize: isMobile ? '14px' : '16px', padding: isMobile ? '8px' : '10px' }} />
        </SearchField>

        <SearchField style={{ marginBottom: isMobile ? '10px' : '15px', flex: isMobile ? '1 1 100%' : '1 1 200px' }}>
          <label style={{ fontSize: isMobile ? '14px' : '16px', marginBottom: '5px', display: 'block' }}>{t("EST_LOCALITY")}</label>
          <TextInput name="locality" inputRef={register({})} style={{ width: '100%', fontSize: isMobile ? '14px' : '16px', padding: isMobile ? '8px' : '10px' }} />
        </SearchField>

        <SearchField className="submit" style={{ display: 'flex', flexDirection: isMobile ? 'column' : 'row', gap: '10px', alignItems: isMobile ? 'stretch' : 'center', flex: isMobile ? '1 1 100%' : '1 1 200px' }}>
          <SubmitBar label={t("ES_COMMON_SEARCH")} submit style={{ width: isMobile ? '100%' : 'auto', fontSize: isMobile ? '14px' : '16px' }} />
          <p
            style={{ 
              marginTop: isMobile ? "10px" : "0", 
              cursor: "pointer",
              width: isMobile ? '100%' : 'auto',
              textAlign: isMobile ? 'center' : 'left',
              fontSize: isMobile ? '14px' : '16px'
            }}
            onClick={clearFilters}
          >
            {t("ES_COMMON_CLEAR_ALL")}
          </p>
        </SearchField>
      </SearchForm>

      <div style={{ 
        overflowX: "auto", 
        width: "100%", 
        marginTop: "20px",
        WebkitOverflowScrolling: "touch",
        padding: isMobile ? '5px' : '10px'
      }}>
        <Table
          t={t}
          data={filteredProperties}
          totalRecords={filteredProperties.length}
          columns={columns}
          manualPagination={false}
          globalSearch={false}
          pageSizeLimit={10}
          getCellProps={(cellInfo) => ({
            style: {
              minWidth: isMobile ? "70px" : "100px",
              padding: isMobile ? "4px 2px" : "8px 6px",
              fontSize: isMobile ? "10px" : "12px",
              textAlign: "center",
              whiteSpace: "nowrap",
            },
          })}
          disableSort={false}
        />
      </div>
    </div>
  );
};

export default ManageProperties;
