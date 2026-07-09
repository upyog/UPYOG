import React, { useCallback, useMemo, useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import {
  TextInput,
  SubmitBar,
  SearchForm,
  SearchField,
  Card,
  Loader,
  Header,
  Dropdown,
} from "@nudmcdgnpm/digit-ui-react-components";
import { getCreateAssetPath } from "../utils/estRoutes";
import {
  fetchAllotmentByAssetNo,
  fetchAllottedAssetNos,
  getAssetIdentity,
  hasExistingAllotment,
  mapAllotmentApiToFormData,
} from "../utils/allotmentFormUtils";
import AssetTable from "./shared/AssetTable";
import useAssetTableColumns from "./shared/useAssetTableColumns";
import useIsMobile from "./shared/useIsMobile";

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
  const isMobile = useIsMobile();

  const [selectedAssetType, setSelectedAssetType] = useState(null);
  const [selectedLocality, setSelectedLocality] = useState(null);
  const [isCleared, setIsCleared] = useState(false);
  const [allottedAssetNos, setAllottedAssetNos] = useState(new Set());

  const { register, handleSubmit, setValue, getValues, reset, watch } = useForm({
    defaultValues: {
      offset: 0,
      limit: 10,
      sortBy: "createdDate",
      sortOrder: "DESC",
      estateNo: "",
    },
  });

  const { data: assetTypeData } = Digit.Hooks.useCustomMDMS(
    Digit.ULBService.getStateId(),
    "ASSET",
    [{ name: "assetParentCategory" }],
    {
      select: (data) => {
        const formattedData = data?.ASSET?.assetParentCategory || [];
        return formattedData
          .filter((item) => item.active)
          .map((item) => ({ code: item.code, name: item.name }));
      },
    }
  );

  const assetTypeOptions =
    assetTypeData?.map((item) => ({
      code: item.code,
      i18nKey: item.name,
      label: item.name,
    })) || [];

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

  const handleFormSubmit = (formData) => {
    setIsCleared(false);
    setValue("offset", 0);
    onSubmit({
      ...formData,
      offset: 0,
      assetParentCategory: selectedAssetType?.code || undefined,
      localityCode: selectedLocality?.code || undefined,
    });
  };

  const estateNoField = register("estateNo");
  const offset = watch("offset") || 0;
  const limit = watch("limit") || 10;

  const paginatedData = useMemo(() => {
    if (!Array.isArray(data) || isCleared) return [];
    return data.slice(offset, offset + limit);
  }, [data, offset, limit, isCleared]);

  useEffect(() => {
    register("offset");
    register("limit");
    register("sortBy");
    register("sortOrder");
  }, [register]);

  useEffect(() => {
    if (!Array.isArray(data) || data.length === 0 || isCleared) {
      setAllottedAssetNos(new Set());
      return;
    }

    let cancelled = false;
    const loadAllottedAssets = async () => {
      const allotted = await fetchAllottedAssetNos(data, tenantId);
      if (!cancelled) setAllottedAssetNos(allotted);
    };

    loadAllottedAssets();
    return () => {
      cancelled = true;
    };
  }, [data, tenantId, isCleared]);

  const [sessionParams] = Digit.Hooks.useSessionStorage("EST_ASSIGN_ASSETS", {});

  const navigateToAssignFlow = useCallback(
    (asset, { allotmentForm, targetStep = "info", resetSession = false } = {}) => {
      navigate(`${modulePath}/assignassets/${targetStep}`, {
        state: {
          assetData: asset,
          allotmentData: allotmentForm,
          resetSession,
        },
      });
    },
    [navigate, modulePath]
  );

  const handleAllotAsset = useCallback(
    (asset) => {
      const hasSavedDraft =
        getAssetIdentity(sessionParams?.assetData) === getAssetIdentity(asset) &&
        sessionParams?.Allotments?.Allotments?.[0];

      navigateToAssignFlow(asset, {
        targetStep: hasSavedDraft ? "assign-assets" : "info",
        resetSession: !hasSavedDraft,
      });
    },
    [sessionParams, navigateToAssignFlow]
  );

  const handleEditAsset = useCallback(
    async (asset) => {
      try {
        const allotment = await fetchAllotmentByAssetNo(asset.estateNo, tenantId);
        if (!allotment) {
          setShowToast?.({ error: true, label: "ES_COMMON_NO_DATA" });
          return;
        }
        navigateToAssignFlow(asset, {
          allotmentForm: mapAllotmentApiToFormData(allotment),
          targetStep: "assign-assets",
          resetSession: true,
        });
      } catch (error) {
        console.error("Error fetching allotment details:", error);
        setShowToast?.({ error: true, label: "EST_ALLOTMENT_FETCH_FAILED" });
      }
    },
    [tenantId, navigateToAssignFlow, setShowToast]
  );

  const handleAssetAction = useCallback(
    (asset) => {
      if (hasExistingAllotment(asset, allottedAssetNos)) {
        handleEditAsset(asset);
      } else {
        handleAllotAsset(asset);
      }
    },
    [allottedAssetNos, handleAllotAsset, handleEditAsset]
  );

  const columns = useAssetTableColumns({
    isMobile,
    modulePath,
    navigate,
    estateNoLink: "link",
    showAssetRef: true,
    actions: "allot",
    onAllot: handleAssetAction,
    onEdit: handleEditAsset,
    allottedAssetNos,
  });

  const onSort = useCallback(
    (args) => {
      if (args.length === 0) return;
      setValue("sortBy", args.id);
      setValue("sortOrder", args.desc ? "DESC" : "ASC");
    },
    [setValue]
  );

  const onPageSizeChange = (e) => {
    setValue("limit", Number(e.target.value));
    setValue("offset", 0);
  };

  const nextPage = () => {
    const newOffset = (getValues("offset") || 0) + (getValues("limit") || 10);
    if (newOffset < count) setValue("offset", newOffset);
  };

  const previousPage = () => {
    const newOffset = (getValues("offset") || 0) - (getValues("limit") || 10);
    if (newOffset >= 0) setValue("offset", newOffset);
  };

  return (
    <React.Fragment>
      <div style={{ padding: isMobile ? "10px" : "20px" }}>
        <Header style={{ fontSize: isMobile ? "18px" : "24px", marginBottom: "15px" }}>
          {t("EST_SEARCH_APPLICATIONS")}
        </Header>

        <SearchForm onSubmit={handleFormSubmit} handleSubmit={handleSubmit}>
          <SearchField style={{ marginBottom: isMobile ? "10px" : "15px" }}>
            <label style={{ fontSize: isMobile ? "14px" : "16px", marginBottom: "5px", display: "block" }}>
              {t("EST_SEARCH_ASSET_NUMBER")}
            </label>
            <TextInput
              name="estateNo"
              inputRef={estateNoField.ref}
              onChange={estateNoField.onChange}
              onBlur={estateNoField.onBlur}
              style={{ width: "100%", fontSize: isMobile ? "14px" : "16px", padding: isMobile ? "8px" : "10px" }}
            />
          </SearchField>

          <SearchField style={{ marginBottom: isMobile ? "10px" : "15px" }}>
            <label style={{ fontSize: isMobile ? "14px" : "16px", marginBottom: "5px", display: "block" }}>
              {t("EST_LOCALITY")}
            </label>
            <Dropdown
              option={localityOptions}
              optionKey="i18nKey"
              selected={selectedLocality}
              select={setSelectedLocality}
              placeholder={t("EST_SELECT_LOCALITY")}
              t={t}
              optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
              style={{ width: "100%", fontSize: isMobile ? "14px" : "16px" }}
            />
          </SearchField>

          <SearchField style={{ marginBottom: isMobile ? "10px" : "15px" }}>
            <label style={{ fontSize: isMobile ? "14px" : "16px", marginBottom: "5px", display: "block" }}>
              {t("EST_ASSET_TYPE")}
            </label>
            <Dropdown
              option={assetTypeOptions}
              optionKey="i18nKey"
              selected={selectedAssetType}
              select={setSelectedAssetType}
              placeholder={t("EST_SELECT_ASSET_TYPE")}
              t={t}
              style={{ width: "100%", fontSize: isMobile ? "14px" : "16px" }}
            />
          </SearchField>

          <SearchField
            className="submit"
            style={{
              display: "flex",
              flexDirection: isMobile ? "column" : "row",
              gap: "10px",
              alignItems: isMobile ? "stretch" : "center",
            }}
          >
            <SubmitBar
              label={t("ES_COMMON_SEARCH")}
              submit
              style={{ width: isMobile ? "100%" : "auto", fontSize: isMobile ? "14px" : "16px" }}
            />
            <p
              style={{
                marginTop: isMobile ? "10px" : "0",
                cursor: "pointer",
                width: isMobile ? "100%" : "auto",
                textAlign: isMobile ? "center" : "left",
                fontSize: isMobile ? "14px" : "16px",
              }}
              onClick={() => {
                reset({
                  estateNo: "",
                  offset: 0,
                  limit: 10,
                  sortBy: "createdDate",
                  sortOrder: "DESC",
                });
                setSelectedAssetType(null);
                setSelectedLocality(null);
                setShowToast(null);
                setIsCleared(true);
              }}
            >
              {t("ES_COMMON_CLEAR_ALL")}
            </p>
          </SearchField>
        </SearchForm>

        {!isLoading && data?.display ? (
          <Card style={{ marginTop: 20, textAlign: "center" }}>
            {String(t(data.display) || "")
              .split("\\n")
              .map((text, index) => (
                <p key={index}>{text}</p>
              ))}
            <button
              onClick={() => navigate(getCreateAssetPath(modulePath))}
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
          <AssetTable
            t={t}
            data={paginatedData}
            columns={columns}
            totalRecords={count}
            isMobile={isMobile}
            pagination={{
              onPageSizeChange,
              currentPage: offset / limit,
              onNextPage: nextPage,
              onPrevPage: previousPage,
              pageSizeLimit: limit,
              onSort,
              sortParams: [{ id: watch("sortBy"), desc: watch("sortOrder") === "DESC" }],
            }}
          />
        ) : (
          isLoading && <Loader />
        )}
      </div>
    </React.Fragment>
  );
};

export default ESTSearchApplication;
