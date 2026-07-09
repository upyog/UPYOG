import React, { useState, useEffect } from "react";
import {
  Header,
  SearchField,
  TextInput,
  Dropdown,
  SubmitBar,
  SearchForm,
  Loader,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useForm } from "react-hook-form";
import AssetTable from "./shared/AssetTable";
import useAssetTableColumns from "./shared/useAssetTableColumns";
import useIsMobile from "./shared/useIsMobile";
import { sortAssetsByEstateNo } from "./shared/assetTableUtils";

const AllProperties = ({ t }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const isMobile = useIsMobile();

  const { isLoading, isSuccess, data: apiData } = Digit.Hooks.estate.useESTAssetSearch(
    { tenantId, filters: { AssetSearchCriteria: {} } },
    { enabled: true }
  );

  const [properties, setProperties] = useState([]);
  const [filteredProperties, setFilteredProperties] = useState([]);
  const [selectedStatus, setSelectedStatus] = useState(null);
  const [selectedType, setSelectedType] = useState(null);
  const { register, handleSubmit, reset } = useForm();

  const assetNumberField = register("assetNumber");
  const buildingNameField = register("buildingName");

  useEffect(() => {
    if (isSuccess && apiData?.Assets) {
      const sorted = sortAssetsByEstateNo(apiData.Assets);
      setProperties(sorted);
      setFilteredProperties(sorted);
    }
  }, [isSuccess, apiData]);

  const assetStatusOptions = [
    { code: "", name: "All", i18nKey: "ES_COMMON_ALL" },
    { code: "active", name: "Active", i18nKey: "EST_ACTIVE" },
    { code: "inactive", name: "Inactive", i18nKey: "EST_INACTIVE" },
  ];

  const assetTypeOptions = [
    { code: "", name: "All", i18nKey: "ES_COMMON_ALL" },
    { code: "COMMERCIAL", name: "Commercial", i18nKey: "EST_COMMERCIAL" },
    { code: "RESIDENTIAL", name: "Residential", i18nKey: "EST_RESIDENTIAL" },
  ];

  const columns = useAssetTableColumns({
    isMobile,
    showAssetRef: false,
    showAllotmentStatus: true,
    actions: "none",
  });

  const onFilterSubmit = (data) => {
    let filtered = properties;

    if (data.assetNumber) {
      filtered = filtered.filter((p) =>
        p.estateNo?.toLowerCase().includes(data.assetNumber.toLowerCase())
      );
    }
    if (data.buildingName) {
      filtered = filtered.filter((p) =>
        p.buildingName?.toLowerCase().includes(data.buildingName.toLowerCase())
      );
    }
    if (selectedStatus?.code) {
      filtered = filtered.filter((p) => p.assetStatus === selectedStatus.code);
    }
    if (selectedType?.code) {
      filtered = filtered.filter((p) => p.assetType === selectedType.code);
    }

    setFilteredProperties(filtered);
  };

  const clearFilters = () => {
    reset();
    setSelectedStatus(null);
    setSelectedType(null);
    setFilteredProperties(properties);
  };

  if (isLoading) return <Loader />;

  return (
    <div style={{ padding: isMobile ? "10px" : "20px" }}>
      <Header style={{ fontSize: isMobile ? "18px" : "24px", marginBottom: "15px" }}>
        All Properties
      </Header>

      <SearchForm
        onSubmit={onFilterSubmit}
        handleSubmit={handleSubmit}
        style={{
          display: "flex",
          flexDirection: isMobile ? "column" : "row",
          gap: isMobile ? "10px" : "15px",
          flexWrap: "wrap",
        }}
      >
        <SearchField style={{ marginBottom: isMobile ? "10px" : "15px", flex: isMobile ? "1 1 100%" : "1 1 200px" }}>
          <label style={{ fontSize: isMobile ? "14px" : "16px", marginBottom: "5px", display: "block" }}>
            {t("EST_ASSET_NUMBER")}
          </label>
          <TextInput
            name="assetNumber"
            inputRef={assetNumberField.ref}
            onChange={assetNumberField.onChange}
            onBlur={assetNumberField.onBlur}
            style={{ width: "100%", fontSize: isMobile ? "14px" : "16px", padding: isMobile ? "8px" : "10px" }}
          />
        </SearchField>

        <SearchField style={{ marginBottom: isMobile ? "10px" : "15px", flex: isMobile ? "1 1 100%" : "1 1 200px" }}>
          <label style={{ fontSize: isMobile ? "14px" : "16px", marginBottom: "5px", display: "block" }}>
            {t("EST_BUILDING_NAME")}
          </label>
          <TextInput
            name="buildingName"
            inputRef={buildingNameField.ref}
            onChange={buildingNameField.onChange}
            onBlur={buildingNameField.onBlur}
            style={{ width: "100%", fontSize: isMobile ? "14px" : "16px", padding: isMobile ? "8px" : "10px" }}
          />
        </SearchField>

        <SearchField style={{ marginBottom: isMobile ? "10px" : "15px", flex: isMobile ? "1 1 100%" : "1 1 200px" }}>
          <label style={{ fontSize: isMobile ? "14px" : "16px", marginBottom: "5px", display: "block" }}>
            {t("EST_ASSET_STATUS")}
          </label>
          <Dropdown
            option={assetStatusOptions}
            optionKey="i18nKey"
            selected={selectedStatus}
            select={setSelectedStatus}
            placeholder={t("ES_COMMON_ALL")}
            t={t}
            style={{ width: "100%", fontSize: isMobile ? "14px" : "16px" }}
          />
        </SearchField>

        <SearchField style={{ marginBottom: isMobile ? "10px" : "15px", flex: isMobile ? "1 1 100%" : "1 1 200px" }}>
          <label style={{ fontSize: isMobile ? "14px" : "16px", marginBottom: "5px", display: "block" }}>
            {t("EST_ASSET_TYPE")}
          </label>
          <Dropdown
            option={assetTypeOptions}
            optionKey="i18nKey"
            selected={selectedType}
            select={setSelectedType}
            placeholder={t("ES_COMMON_ALL")}
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
            flex: isMobile ? "1 1 100%" : "1 1 200px",
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
            onClick={clearFilters}
          >
            {t("ES_COMMON_CLEAR_ALL")}
          </p>
        </SearchField>
      </SearchForm>

      <AssetTable
        t={t}
        data={filteredProperties}
        columns={columns}
        totalRecords={filteredProperties.length}
        isMobile={isMobile}
      />
    </div>
  );
};

export default AllProperties;
