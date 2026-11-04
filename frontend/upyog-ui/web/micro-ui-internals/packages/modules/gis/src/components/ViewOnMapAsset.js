import React, { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Modal } from "@nudmcdgnpm/digit-ui-react-components";
import "../css/gis-inline.css";

/**
 * Close icon SVG component for the modal
 */
const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 
             5 6.41 10.59 12 5 17.59 6.41 19 
             12 13.41 17.59 19 19 17.59 
             13.41 12 19 6.41z" />
  </svg>
);

/**
 * Close button component with click handler
 * @param {Function} onClick - Function to call when close button is clicked
 */
const CloseBtn = ({ onClick }) => (
  <div
    className="icon-bg-secondary gis-viewasset-close"
    onClick={(e) => {
      e.stopPropagation();
      if (onClick) onClick();
    }}
  >
    <Close />
  </div>
);

/**
 * ViewOnMapAsset component displays asset location data on an interactive map within a modal.
 * It fetches GeoJSON data for a specific asset and renders it using Leaflet maps.
 */
const ViewOnMapAsset = ({ closeModal, applicationNumber }) => {
  const { t } = useTranslation();
  
  const handleClose = () => {
    if (closeModal) {
      closeModal();
    } else {
      // Fallback: close modal by removing from DOM or going back
      window.history.back();
    }
  };
  
  const mapRef = useRef(null);
  const [geoJsonData, setGeoJsonData] = useState(null);
  const [loading, setLoading] = useState(true);

  // Fetch asset data on component mount
  useEffect(() => {
    const fetchAssetData = async () => {
      try {
        const payload = {
          tenantId: "pg.citya",
          businessService: "ASSET",
          filters: {
            applicationNo: "PG-1013-2025-L-001295"
          },
          fromDate: 1743445800000,
          geometryType: "polygon",
          includeBillData: false
        };

        const response = await Digit.GIS.searchAsset(payload);
        
        if (response && response.geoJsonData) {
          setGeoJsonData(response.geoJsonData);
        }
      } catch (error) {
        console.error("Error fetching asset data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchAssetData();
  }, []);

  /**
   * Initialize Leaflet map with GeoJSON data
   * Sets up map tiles, adds asset geometry, and configures popups
   */
  const initMap = () => {
    if (!mapRef.current || !geoJsonData) return;

    // Clean up existing map instance if present
    if (mapRef.current._leaflet_id) {
      mapRef.current._leaflet_id = null;
      mapRef.current.innerHTML = "";
    }

    const feature = geoJsonData.features?.[0];
    if (!feature || !feature.geometry) {
      return;
    }

    // Determine map center based on geometry type
    let center = [28.61, 77.23]; // fallback: Delhi coordinates
    if (feature.geometry.type === "Point") {
      const [lng, lat] = feature.geometry.coordinates;
      center = [lat, lng];
    } else if (feature.geometry.type === "Polygon") {
      const coords = feature.geometry.coordinates[0][0];
      center = [coords[1], coords[0]];
    }

    // Initialize map with center and zoom level
    const map = window.L.map(mapRef.current).setView(center, 18);

    // Add OpenStreetMap tile layer
    window.L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map);

    // Add GeoJSON features to map with styling and popups
    window.L.geoJSON(geoJsonData, {
      style: {
        color: "#e51916ff", // Red color for asset boundaries
        weight: 2,
        opacity: 0.65,
      },
      onEachFeature: (feature, layer) => {
        const props = feature.properties || {};

        // Create styled popup content with asset details
        const popupContent = `
          <div class="gis-asset-popup">
          <h3 class="gis-asset-popup__title">
              Asset Details
          </h3>
          <div>
              <div><b>Application No:</b> ${props.applicationNumber || "-"}</div>
              <div><b>Asset Name:</b> ${props.assetName || "-"}</div>
              <div><b>Classification:</b> ${props.assetClassification || "-"}</div>
              <div><b>Category:</b> ${props.assetCategory || "-"}</div>
              <div><b>Department:</b> ${props.department || "-"}</div>
              <div><b>Status:</b> ${props.status || "-"}</div>
          </div>
          </div>
        `;

        layer.bindPopup(popupContent);
      }
    }).addTo(map);

    // Fit map view to show all features
    try {
      map.fitBounds(window.L.geoJSON(geoJsonData).getBounds());
    } catch (error) {
      console.error("Error fitting bounds:", error);
    }
  };

  // Initialize map when GeoJSON data is available
  useEffect(() => {
    if (geoJsonData && window.L) {
      setTimeout(initMap, 100);
    }
  }, [geoJsonData]);

  // Render modal with map or loading/error states
  return (
    <Modal
      headerBarEnd={<CloseBtn onClick={handleClose} />}
      hideSubmit={true}
      formId="modal-action"
      popupStyles={{ width: "90vw", height: "85vh" }}
    >
      {loading ? (
        // Loading state while fetching data
        <div className="gis-viewasset-state">
          {t("LOADING_MAP_DATA")}
        </div>
      ) : geoJsonData ? (
        // Map container when data is available
        <div ref={mapRef} className="gis-modal-map" />
      ) : (
        // Error state when no data is found
        <div className="gis-viewasset-state">
          {t("NO_LOCATION_DATA_FOUND")}
        </div>
      )}
    </Modal>
  );
};

export default ViewOnMapAsset;