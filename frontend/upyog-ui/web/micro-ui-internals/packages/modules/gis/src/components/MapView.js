import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from "react-i18next";
import { CardLabel, SubmitBar, Dropdown, BackButton } from '@nudmcdgnpm/digit-ui-react-components';
import { MAP_TILE_URL, createMapIcons, LEAFLET_DEFAULT_ICON_OPTIONS } from '../utils';
import "../css/gis-inline.css";

/**
This MapView defines the MapView component, which is responsible for rendering and managing the map interface in the GIS module.
It integrates with mapping libraries (e.g., Leaflet, OpenLayers, or similar) to display geospatial data.
The component provides functionalities for searching and filtering map data based on user input and show details in pop up.
Displaying user's current location on the map.
Rendering data points as markers with popup details.
Allowing users to search and filter map data by application number.
Calculating distances between user location and feature locations.
 */

const MapView = () => {
  const mapRef = useRef(null);
  const [userLocation, setUserLocation] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [inputValue, setInputValue] = useState('');
  const [selectedFinancialYear, setSelectedFinancialYear] = useState({ code: '2024-25', value: { fromDate: 1712006400000, toDate: 1743542399000 }, i18nKey: '2024-25' });
  const [selectedPaymentStatus, setSelectedPaymentStatus] = useState({ code: 'ALL', value: 'ALL', i18nKey: 'All' });
  const [selectedUsageCategory, setSelectedUsageCategory] = useState({ code: 'ALL', value: 'ALL', i18nKey: 'All' });
  const [selectedAssetClassification, setSelectedAssetClassification] = useState({ code: 'ALL', value: 'ALL', i18nKey: 'All' });
  const [geoJsonData, setGeoJsonData] = useState({ type: "FeatureCollection", features: [] });
  const { t } = useTranslation();

 
  const { data: gisMdmsData } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "GIS", [{ name: "assetClassificationOptions" }, { name: "BaseLayer" },
  { name: "financialYear" }, { name: "paymentStatusOptions" }, { name: "BaseLayer" }, { name: "usageCategoryOptions" }], {
    select: (data) => data || {},
  });
  // Extracting necessary GIS configuration data from the fetched MDMS data.
  const gisLink = gisMdmsData?.GIS?.BaseLayer || [];
  const assetClassificationOptions =
    gisMdmsData?.GIS?.assetClassificationOptions || [];

  const financialYearOptions =
    gisMdmsData?.GIS?.financialYear || [];

  const paymentStatusOptions =
    gisMdmsData?.GIS?.paymentStatusOptions || [];

  const usageCategoryOptions =
    gisMdmsData?.GIS?.usageCategoryOptions || [];

  const getBusinessService = () => {
    const selectedServiceType = sessionStorage.getItem('selectedServiceType');
    if (selectedServiceType) {
      const serviceType = JSON.parse(selectedServiceType);
      return serviceType.businessService || "PT";
    }
    return "PT"; // default fallback
  };

  //Fetches the selected business service type from sessionStorage.
  //Used to determine which GIS API to call (Property or Asset).
  useEffect(() => {
    const fetchGISData = async () => {
      try {
        const businessService = getBusinessService();
        const dateRange = selectedFinancialYear?.value || { fromDate: 1743445800000, toDate: 1774981799000 };
        const payload = {
          tenantId: "pg.citya",
          fromDate: dateRange.fromDate,
          toDate: dateRange.toDate,
          includeBillData: true,
          businessService: businessService
        };

        const response = businessService === "PT"
          ? await Digit.GIS.searchPT(payload)
          : await Digit.GIS.searchAsset(payload);


        if (response && response.geoJsonData) {
          let filteredFeatures = response.geoJsonData.features;
          
          // Apply filters sequentially for PT
          if (businessService === "PT") {
            // Apply payment status filter
            if (selectedPaymentStatus.code !== 'ALL') {
              filteredFeatures = filteredFeatures.filter(features => 
                features.properties.paymentStatus === selectedPaymentStatus.code
              );
            }

            // Apply usage category filter with prefix matching
            if (selectedUsageCategory.code !== 'ALL') {
              filteredFeatures = filteredFeatures.filter(features => {
                const usageCategory = features.properties.usageCategory;
                return usageCategory?.startsWith(selectedUsageCategory.code);
              });
            }
          }

          // Apply filters sequentially for ASSET
          if (businessService !== "PT") {
            // Apply asset classification filter
            if (selectedAssetClassification.code !== 'ALL') {
              filteredFeatures = filteredFeatures.filter(features => 
                features.properties.assetClassification === selectedAssetClassification.code
              );
            }
          }

          // Filter valid coordinates after applying business filters
          const validFeatures = filteredFeatures.filter(features => {
            const coords = features.geometry?.coordinates;
            return (
              Array.isArray(coords) &&
              coords.length === 2 &&
              !(coords[0] === 0.0 && coords[1] === 0.0)
            );
          });

          const transformedData = {
            type: "FeatureCollection",
            features: validFeatures.map(features => ({
              type: "Feature",
              geometry: features.geometry,
              properties: businessService === "PT" ? {
                applicationNumber: features.properties.applicationNumber,
                propertyId: features.properties.applicationNumber,
                propertyType: features.properties.propertyType,
                status: features.properties.status,
                paymentStatus: features.properties.paymentStatus,
                landArea: features.properties.landArea,
                usageCategory: features.properties.usageCategory,
                ownershipCategory: features.properties.ownershipCategory
              } : {
                applicationNumber: features.properties.applicationNumber,
                assetName: features.properties.assetName,
                assetDepartment: features.properties.department,
                assetCategory: features.properties.assetCategory,
                assetClassification: features.properties.assetClassification,
                status: features.properties.status,
              }
            }))
          };

          setGeoJsonData(transformedData);
        }
      } catch (error) {
        console.error("Error fetching GIS data:", error);
      }
    };

    fetchGISData();
  }, [selectedFinancialYear, selectedPaymentStatus, selectedUsageCategory, selectedAssetClassification]);


  /**
     * Prepares dropdown options based on fetched GIS data.
     */
  const statusOptions = geoJsonData.features.map(feature => ({
    code: feature.properties.applicationNumber,
    value: feature.properties.applicationNumber,
    i18nKey: feature.properties.applicationNumber
  }));

  const handleSearch = () => {
    setSearchTerm(inputValue?.value || inputValue || "");
  };
  // Utility function to calculate distance between two geographic coordinates using the Haversine formula.
  const calculateDistance = (lat1, lng1, lat2, lng2) => {
    const R = 6371;
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) ** 2 +
      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLng / 2) ** 2;
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  };

  /**
   * Attempts to get the user's current location via the browser's Geolocation API.
   */
  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setUserLocation({
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          });
        },
        (error) => {
          console.warn("Location access denied or unavailable:", error);
        }
      );
    }
  }, []);

  /**
 * Loads the Leaflet library dynamically (if not already loaded)
 * and initializes the map once both the user location and GIS data are ready.
 */
  useEffect(() => {
    if (!userLocation || !geoJsonData.features.length) return;

    const loadLeaflet = () => {
      if (!window.L) {
        const link = document.createElement('link');
        link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
        link.rel = 'stylesheet';
        document.head.appendChild(link);

        const script = document.createElement('script');
        script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
        script.onload = initMap;
        document.head.appendChild(script);
      } else {
        initMap();
      }
    };

    loadLeaflet();
  }, [userLocation, geoJsonData, searchTerm]);

  const initMap = () => {
    if (!mapRef.current) return;

    if (mapRef.current._leaflet_id) {
      mapRef.current._leaflet_id = null;
      mapRef.current.innerHTML = "";
    }

    const map = window.L.map(mapRef.current).setView([userLocation.lat, userLocation.lng], 12);
    
    // Get tile URL from  hook
    const tileUrl = gisLink[0]?.url;
    window.L.tileLayer(tileUrl).addTo(map);

    // Fix Leaflet default marker icons
    delete window.L.Icon.Default.prototype._getIconUrl;
    window.L.Icon.Default.mergeOptions(LEAFLET_DEFAULT_ICON_OPTIONS);


    const iconConfigs = createMapIcons();
    const userIcon = window.L.divIcon(iconConfigs.userIcon);
    const paidIcon = new window.L.Icon(iconConfigs.paidIcon);
    const unpaidIcon = new window.L.Icon(iconConfigs.unpaidIcon);
    const defaultIcon = new window.L.Icon(iconConfigs.defaultIcon);

    window.L.marker([userLocation.lat, userLocation.lng], { icon: userIcon })
      .addTo(map)
      .bindPopup('<b>Your Location</b>');

    const markerMap = new Map();

    const businessService = getBusinessService();

    // Plot GIS features as markers
    geoJsonData.features.forEach(feature => {
      const [lng, lat] = feature.geometry.coordinates;
      const props = feature.properties;
      const distance = calculateDistance(userLocation.lat, userLocation.lng, lat, lng);
      
      let markerIcon;
      if (businessService === "PT" && props.paymentStatus) {
        markerIcon = props.paymentStatus === "PAID" ? paidIcon : unpaidIcon;
      } else {
        markerIcon = defaultIcon;
      }
      
      const marker = window.L.marker([lat, lng], { icon: markerIcon }).addTo(map);
      
     
      
      const popupContent = businessService === "PT" ? `
        <div>
          <div class="gis-mapview-popup-body">
            <b>${props.propertyType}</b><br>
            <b>Property ID:</b> ${props.applicationNumber}</br>
            <b>Status:</b> ${props.status || "N/A"}<br>
            <b>Payment:</b> ${props.paymentStatus || "N/A"}<br>
            <b>Land Area:</b> ${props.landArea || "N/A"}<br>
            <b>Usage:</b> ${props.usageCategory || "N/A"}<br>
            <b>Distance:</b> ${distance.toFixed(1)} km<br>
          </div>
        </div>
      ` : `
        <div>
          <div class="gis-mapview-popup-body">
            <b>${props.assetName}</b><br>
            <b>Status:</b> ${props.status || "N/A"}<br>
            <b>Asset Category:</b> ${props.assetCategory || "N/A"}<br>
            <b>Asset Department:</b> ${props.assetDepartment || "N/A"}<br>
            <b>Classification:</b> ${props.assetClassification || "N/A"}<br>
            <b>Digipin:</b> ${digipin}<br>
            <b>Distance:</b> ${distance.toFixed(1)} km<br>
          </div>
        </div>
      `;

      marker.bindPopup(popupContent);
      markerMap.set(props.applicationNumber.toLowerCase(), { marker, lat, lng });
    });

    if (searchTerm) {
      const term = searchTerm.trim().toLowerCase();
      for (let [key, { marker, lat, lng }] of markerMap) {
        if (key.includes(term)) {
          map.flyTo([lat, lng], 18);
          marker.openPopup();
          break;
        }
      }
    }
  };

  return (
    <div className="gis-mapview-root">
      <div className="gis-mapview-body">
        <BackButton />
        <div className="gis-mapview-toolbar">
          <div className="gis-mapview-filters">
            <div className="gis-mapview-field">
              <CardLabel>{t("Financial_Year")}</CardLabel>
              <Dropdown
                className="form-field"
                selected={selectedFinancialYear}
                select={setSelectedFinancialYear}
                option={financialYearOptions}
                placeholder={t("Select Financial Year")}
                optionKey="i18nKey"
                style={{ width: "100%", position: "relative", zIndex: 2002 }}
              />
            </div>
            {getBusinessService() === "PT" && (
              <React.Fragment>
                <div className="gis-mapview-field">
                  <CardLabel>{t("Payment_Status")}</CardLabel>
                  <Dropdown
                    className="form-field"
                    selected={selectedPaymentStatus}
                    select={setSelectedPaymentStatus}
                    option={paymentStatusOptions}
                    placeholder={t("Select Payment Status")}
                    optionKey="i18nKey"
                    style={{ width: "100%", position: "relative", zIndex: 2001 }}
                  />
                </div>
                <div className="gis-mapview-field">
                  <CardLabel>{t("Usage_Category")}</CardLabel>
                  <Dropdown
                    className="form-field"
                    selected={selectedUsageCategory}
                    select={setSelectedUsageCategory}
                    option={usageCategoryOptions}
                    placeholder={t("Select Usage Category")}
                    optionKey="i18nKey"
                    style={{ width: "100%", position: "relative", zIndex: 2000 }}
                  />
                </div>
              </React.Fragment>
            )}
            {getBusinessService() !== "PT" && (
              <div className="gis-mapview-field">
                <CardLabel>{t("Asset_Classification")}</CardLabel>
                <Dropdown
                  className="form-field"
                  selected={selectedAssetClassification}
                  select={setSelectedAssetClassification}
                  option={assetClassificationOptions}
                  placeholder={t("Select Asset Classification")}
                  optionKey="i18nKey"
                  style={{ width: "100%", position: "relative", zIndex: 2001 }}
                />
              </div>
            )}
          </div>
          <div className="gis-mapview-search">
            <div className="gis-mapview-field">
              <CardLabel>{getBusinessService() === "PT" ? t("SEARCH_BY_PROPERTYID") : t("SEARCH_BY_ASSETID")}</CardLabel>
              <Dropdown
                className="form-field"
                selected={inputValue}
                select={setInputValue}
                option={statusOptions}
                placeholder={t("Select Property")}
                optionKey="i18nKey"
                style={{ width: "100%", position: "relative", zIndex: 2001 }}
                optionCardStyles={{ maxHeight: "200px", overflowY: "auto" }}
                t={t}
              />
            </div>
            <div className="gis-mapview-actions">
              <CardLabel style={{ visibility: "hidden" }}>{t("Actions")}</CardLabel>
              <div className="gis-mapview-actions-row">
                <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
                <p className="link gis-mapview-clear" onClick={() => {
                  setSearchTerm("");
                  setInputValue("");
                  setSelectedPaymentStatus({ code: 'ALL', value: 'ALL', i18nKey: 'All' });
                  setSelectedUsageCategory({ code: 'ALL', value: 'ALL', i18nKey: 'All' });
                  setSelectedAssetClassification({ code: 'ALL', value: 'ALL', i18nKey: 'All' });
                }}>
                  {t("ES_COMMON_CLEAR_ALL")}
                </p>
              </div>
            </div>
          </div>
        </div>

        {geoJsonData.features.length > 0 ? (
          <div ref={mapRef} className="gis-mapview-map" />
        ) : (
          <p className="gis-mapview-empty">
            {getBusinessService() === "PT" ? t("NO_PROPERTY_FOUND") : t("NO_ASSET_FOUND")}
          </p>
        )}
      </div>
    </div>
  );
};

export default MapView;
