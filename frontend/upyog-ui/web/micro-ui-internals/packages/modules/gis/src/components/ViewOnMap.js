import React, { useEffect, useRef, useState } from "react";
import { Modal } from "@nudmcdgnpm/digit-ui-react-components";
import "../css/gis-inline.css";

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 
             5 6.41 10.59 12 5 17.59 6.41 19 
             12 13.41 17.59 19 19 17.59 
             13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = ({ onClick }) => (
  <div className="icon-bg-secondary" onClick={onClick}>
    <Close />
  </div>
);

/**
 * 
 * Displays an interactive Leaflet map inside a modal for visualizing 
 * an immovable asset’s location and geometry (Point or Polygon). 
 * 
 * The component dynamically loads the Leaflet library (if not already loaded), 
 * initializes a map centered on the provided geometry, and adds the asset as 
 * a styled GeoJSON layer. When the asset is clicked, a popup shows key 
 * asset details such as application number, classification, category, 
 * possession date, area, city, and pincode.
 * 
 * Props:
 *  - closeModal: Function to close the modal.
 *  - location: GeoJSON object representing the asset geometry.
 *  - assetDetails: Object containing metadata displayed in the popup.
 */


const ViewOnMap = ({ closeModal, location,assetDetails }) => {
  const mapRef = useRef(null);

  useEffect(() => {
    if (!location) return;

    const loadLeaflet = () => {
      if (!window.L) {
        const link = document.createElement("link");
        link.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
        link.rel = "stylesheet";
        document.head.appendChild(link);

        const script = document.createElement("script");
        script.src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";
        script.onload = initMap;
        document.head.appendChild(script);
      } else {
        initMap();
      }
    };

    loadLeaflet();
  }, [location]);

  const initMap = () => {
    if (!mapRef.current || !location) return;

    if (mapRef.current._leaflet_id) {
      mapRef.current._leaflet_id = null;
      mapRef.current.innerHTML = "";
    }

    // Wrap your single feature in a FeatureCollection
    const geoJsonData = {
      type: "FeatureCollection",
      features: [location],
    };

    // Pick a default center from geometry
    let center = [28.61, 77.23]; // fallback: Delhi
    if (location.geometry.type === "Point") {
      const [lng, lat] = location.geometry.coordinates;
      center = [lat, lng];
    } else if (location.geometry.type === "Polygon") {
      const coords = location.geometry.coordinates[0][0];
      center = [coords[1], coords[0]];
    }

    const map = window.L.map(mapRef.current).setView(center, 18);

    window.L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map);

    // Add the feature(s) dynamically
    window.L.geoJSON(geoJsonData, {
      style: {
        color: "#e51916ff",
        weight: 2,
        opacity: 0.65,
      },
      
        onEachFeature: (feature, layer) => {
            const props = assetDetails || {};

            const popupContent = `
                <div class="gis-asset-popup">
                <h3 class="gis-asset-popup__title">
                    Asset Details
                </h3>
                <div>
                    <div><b>Application No:</b> ${props.applicationNo || "-"}</div>
                    <div><b>Classification:</b> ${props.assetClassification || "-"}</div>
                    <div><b>Category:</b> ${props.assetCategory || "-"}</div>
                    <div><b>Date of Possession:</b> ${props.additionalDetails.dateOfPossession|| "-"}</div>
                    <div><b>Area:</b> ${(props.additionalDetails?.area ? Number(props.additionalDetails.area).toFixed(3) : "-")} Sq. m</div>
                    <div><b>City:</b> ${props.addressDetails.city || "-"}</div>
                    <div><b>Pincode:</b> ${props.addressDetails.pincode|| "-"}</div>
                </div>
                </div>
            `;

        layer.bindPopup(popupContent);
        }}).addTo(map);

        // Zoom to feature
        map.fitBounds(window.L.geoJSON(geoJsonData).getBounds());
    };

  

  return (
    <Modal
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      hideSubmit={true}
      formId="modal-action"
      popupStyles={{ width: "90vw", height: "85vh" }}
    >
      <div ref={mapRef} className="gis-modal-map" />
    </Modal>
  );
};

export default ViewOnMap;
