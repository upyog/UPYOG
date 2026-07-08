import React, { useCallback, useMemo, useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import {
  TextInput,
  SubmitBar,
  SearchForm,
  SearchField,
  Table,
  Card,
  Loader,
  Header,
  Dropdown,
} from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";

const ESTSearchApplication = ({
  tenantId,
  isLoading,
  t,
  onSubmit,
  data,
  count,
  setShowToast,
}) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const [selectedAssetType, setSelectedAssetType] = useState(null); // assetParentCategory
  const [selectedLocality, setSelectedLocality] = useState(null);   // localityCode
  const [properties, setProperties] = useState([]);
  const [isCleared, setIsCleared] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  const { register, handleSubmit, setValue, getValues, reset } = useForm({
    defaultValues: {
      offset: 0,
      limit: 10,
      sortBy: "createdDate",
      sortOrder: "DESC",
      estateNo: "",
    },
  });

  // Asset Parent Category (LAND / BUILDING)
  const { data: assetTypeData } = Digit.Hooks.useCustomMDMS(
    Digit.ULBService.getStateId(),
    "ASSET",
    [{ name: "assetParentCategory" }],
    {
      select: (data) => {
        const formattedData = data?.ASSET?.assetParentCategory || [];
        return formattedData
          .filter((item) => item.active)
          .map((item) => ({
            code: item.code,
            name: item.name,
          }));
      },
    }
  );

  const assetTypeOptions =
    assetTypeData?.map((item) => ({
      code: item.code,
      i18nKey: item.name,
      label: item.name,
    })) || [];

  // 🔹 Locality options (boundary) – like create page
  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    tenantId,
    "revenue",
    { enabled: !!tenantId },
    t
  );

  const localityOptions =
    fetchedLocalities?.map((loc) => ({
      ...loc,
      code: loc.code,
      i18nKey: loc.name || loc.i18nKey || loc.label,
      label: loc.name || loc.label || loc.code,
    })) || [];

  // Submit handler: send clean payload to parent
  const handleFormSubmit = (formData) => {
    setIsCleared(false); // Reset cleared state when searching
    const searchData = {
      ...formData,
      assetParentCategory: selectedAssetType?.code || undefined,
      localityCode: selectedLocality?.code || undefined,
    };

    onSubmit(searchData);
  };

  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    register("sortBy", "createdDate");
    register("sortOrder", "DESC");
  }, [register]);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= 768);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Apply frontend filtering (type + locality) over API data
  useEffect(() => {
    if (!Array.isArray(data) || isCleared) return;

    let result = data;

    // Filter by asset type (LAND/BUILDING)
    if (selectedAssetType?.code) {
      const selectedCode = selectedAssetType.code.toUpperCase();
      result = result.filter((asset) => {
        const type = (asset.assetType || asset.assetParentCategory || "").toUpperCase();
        return type === selectedCode;
      });
    }

    // Filter by locality code
    if (selectedLocality?.code) {
      const locCode = selectedLocality.code.toUpperCase();
      result = result.filter((asset) => {
        const assetLocCode = (
          asset.localityCode ||
          asset.locality?.code ||
          asset.locality
        );
        return assetLocCode && assetLocCode.toUpperCase() === locCode;
      });
    }

    // Apply pagination to show only 10 items per page
    const offset = getValues("offset") || 0;
    const limit = getValues("limit") || 10;
    const paginatedResult = result.slice(offset, offset + limit);

    setProperties(paginatedResult);
  }, [data, selectedAssetType, selectedLocality, getValues("offset"), getValues("limit"), isCleared]);

  const GetCell = (value) => <span className="cell-text">{value || "N/A"}</span>;

  const handleAllotAsset = useCallback(
    (asset) => {
      navigate(`${modulePath}/assignassets/info`, { state: { assetData: asset } });
    },
    [navigate, modulePath]
  );

  const columns = useMemo(
    () => [
      {
        Header: "Asset Number",
        accessor: "estateNo",
        disableSortBy: true,
        Cell: ({ row }) => (
          <div>
            <span className="link">
              <Link to={`application-details/${row.original["estateNo"]}`}>
                {row.original["estateNo"]}
              </Link>
            </span>
          </div>
        ),
      },
      {
        Header: "Asset Ref",
        // Fallbacks: rows created before the apiFieldName/numeric fixes in
        // config.js may carry legacy keys (assetRef, buildingFloor) or nulls.
        Cell: ({ row }) => GetCell(row.original["refAssetNo"] || row.original["assetRef"]),
        disableSortBy: true,
      },
      {
        Header: "Building Name",
        Cell: ({ row }) => GetCell(row.original["buildingName"] || row.original["assetName"]),
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
        Cell: ({ row }) => {
          const l = row.original["dimensionLength"];
          const w = row.original["dimensionWidth"];
          // Avoid rendering the literal "null x null" for legacy rows
          return GetCell(l != null && w != null ? `${l} x ${w}` : null);
        },
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
            <button
              onClick={() => !isAllotted && handleAllotAsset(row.original)}
              style={{
                backgroundColor: isAllotted ? "#ccc" : "#007bff",
                color: "white",
                border: "none",
                padding: isMobile ? "3px 6px" : "6px 10px",
                borderRadius: "4px",
                cursor: isAllotted ? "not-allowed" : "pointer",
                fontSize: isMobile ? "9px" : "12px",
                minWidth: isMobile ? "50px" : "auto",
              }}
              disabled={isAllotted}
            >
              Allot Asset
            </button>
          );
        },
        disableSortBy: true,
      },
    ],
    // Old deps were [] — the Action cell captured the first-render isMobile and
    // handleAllotAsset forever, so mobile styles never updated on resize.
    [isMobile, handleAllotAsset]
  );

  const onSort = useCallback(
    (args) => {
      if (args.length === 0) return;
      setValue("sortBy", args.id);
      setValue("sortOrder", args.desc ? "DESC" : "ASC");
    },
    [setValue]
  );

  function onPageSizeChange(e) {
    setValue("limit", Number(e.target.value));
    handleSubmit(handleFormSubmit)();
  }

  function nextPage() {
    setValue("offset", getValues("offset") + getValues("limit"));
    handleSubmit(handleFormSubmit)();
  }

  function previousPage() {
    setValue("offset", getValues("offset") - getValues("limit"));
    handleSubmit(handleFormSubmit)();
  }

  return (
    <React.Fragment>
      <div style={{ padding: isMobile ? '10px' : '20px' }}>
        <Header style={{ fontSize: isMobile ? '18px' : '24px', marginBottom: '15px' }}>{t("EST_SEARCH_APPLICATIONS")}</Header>

        <SearchForm onSubmit={handleFormSubmit} handleSubmit={handleSubmit}>
          {/* Asset Number */}
          <SearchField style={{ marginBottom: isMobile ? '10px' : '15px' }}>
            <label style={{ fontSize: isMobile ? '14px' : '16px', marginBottom: '5px', display: 'block' }}>{t("EST_SEARCH_ASSET_NUMBER")}</label>
            {/* <TextInput name="estateNo" inputRef={register({})} style={{ width: '100%', fontSize: isMobile ? '14px' : '16px', padding: isMobile ? '8px' : '10px' }} />  */}
          </SearchField>

          {/* Locality dropdown */}
          <SearchField style={{ marginBottom: isMobile ? '10px' : '15px' }}>
            <label style={{ fontSize: isMobile ? '14px' : '16px', marginBottom: '5px', display: 'block' }}>{t("EST_LOCALITY")}</label>
            <Dropdown
              option={localityOptions}
              optionKey="i18nKey"
              selected={selectedLocality}
              select={setSelectedLocality}
              placeholder={t("EST_SELECT_LOCALITY")}
              t={t}
              optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
              style={{ width: '100%', fontSize: isMobile ? '14px' : '16px' }}
            />
          </SearchField>

          {/* Asset Type (parent category) */}
          <SearchField style={{ marginBottom: isMobile ? '10px' : '15px' }}>
            <label style={{ fontSize: isMobile ? '14px' : '16px', marginBottom: '5px', display: 'block' }}>{t("EST_ASSET_TYPE")}</label>
            <Dropdown
              option={assetTypeOptions}
              optionKey="i18nKey"
              selected={selectedAssetType}
              select={setSelectedAssetType}
              placeholder={t("EST_SELECT_ASSET_TYPE")}
              t={t}
              style={{ width: '100%', fontSize: isMobile ? '14px' : '16px' }}
            />
          </SearchField>

          {/* Submit + Clear */}
          <SearchField className="submit" style={{ display: 'flex', flexDirection: isMobile ? 'column' : 'row', gap: '10px', alignItems: isMobile ? 'stretch' : 'center' }}>
            <SubmitBar label={t("ES_COMMON_SEARCH")} submit style={{ width: isMobile ? '100%' : 'auto', fontSize: isMobile ? '14px' : '16px' }} />
            <p
              style={{ 
                marginTop: isMobile ? "10px" : "0", 
                cursor: "pointer", 
                width: isMobile ? '100%' : 'auto',
                textAlign: isMobile ? 'center' : 'left',
                fontSize: isMobile ? '14px' : '16px'
              }}
              onClick={() => {
                reset({
                  estateNo: "",
                  offset: 0,
                  limit: 10,
                  sortBy: "createdDate",
                  sortOrder: "DESC"
                });
                setSelectedAssetType(null);
                setSelectedLocality(null);
                setShowToast(null);
                setProperties([]);
                setIsCleared(true);
              }}
            >
              {t("ES_COMMON_CLEAR_ALL")}
            </p>
          </SearchField>
        </SearchForm>

        {/* Results */}
        {!isLoading && data?.display ? (
          <Card style={{ marginTop: 20, textAlign: "center" }}>
            {t(data.display)
              .split("\\n")
              .map((text, index) => (
                <p key={index}>{text}</p>
              ))}

            <button
              onClick={() => navigate(`${modulePath}/create-asset`)}
              style={{
                backgroundColor: "#007bff",
                color: "white",
                border: "none",
                padding: "10px 10px",
                borderRadius: "4px",
                cursor: "pointer",
                marginTop: "10px",
              }}
            >
              {t("EST_CREATE_ASSET")}
            </button>
          </Card>
        ) : !isLoading && Array.isArray(data) && data.length > 0 ? (
          <div style={{ 
            overflowX: "auto", 
            width: "100%", 
            marginTop: "20px",
            WebkitOverflowScrolling: "touch",
            padding: isMobile ? '5px' : '10px'
          }}>
            <Table
              t={t}
              data={properties}
              totalRecords={count}
              columns={columns}
              getCellProps={() => ({
                style: {
                  minWidth: isMobile ? "70px" : "100px",
                  padding: isMobile ? "4px 2px" : "8px 6px",
                  fontSize: isMobile ? "10px" : "12px",
                  textAlign: "center",
                  whiteSpace: "nowrap",
                },
              })}
              onPageSizeChange={onPageSizeChange}
              currentPage={getValues("offset") / getValues("limit")}
              onNextPage={nextPage}
              onPrevPage={previousPage}
              pageSizeLimit={getValues("limit")}
              onSort={onSort}
              disableSort={false}
              sortParams={[
                {
                  id: getValues("sortBy"),
                  desc: getValues("sortOrder") === "DESC",
                },
              ]}
            />
          </div>
        ) : (
          isLoading && <Loader />
        )}
      </div>
    </React.Fragment>
  );
};

export default ESTSearchApplication;
