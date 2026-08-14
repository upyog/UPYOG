import React, { useEffect, useRef, useState, useMemo } from "react";
import { Modal, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import * as turf from "@turf/turf";
import L from "leaflet";

import "leaflet/dist/leaflet.css";
import "leaflet-draw";
import "leaflet-draw/dist/leaflet.draw.css";
import "../css/gis-inline.css";

/**
 * @author - Shivank - NUDM
 * 
 * Interactive map component for property boundary marking in UPYOG asset management system.
 * Developed to enable field officers to digitally mark immovable asset boundaries by drawing 
 * shapes (polygons, rectangles, markers) on OpenStreetMap and automatically calculate area in square meters.
 * 
 * Usage: Pass location coordinates, onGeometrySave callback for GeoJSON data, and onAreaSave callback 
 * for calculated area. Component renders in modal with drawing tools on map's top-right corner.
 */

/* ---------- Close Button ---------- */
const Close = () => (
  <svg viewBox="0 0 24 24" fill="#fff">
    <path d="M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
  </svg>
);

const CloseBtn = ({ onClick }) => (
  <div className="icon-bg-secondary" onClick={onClick}>
    <Close />
  </div>
);

const MarkOnMap = ({
  closeModal,
  location,
  onGeometrySave,
  onAreaSave,
  savedGeometry,
  savedArea,
}) => {
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);

  const [geometry, setGeometry] = useState(savedGeometry || null);
  const [area, setArea] = useState(savedArea || null);
  const [showToast, setShowToast] = useState(null);

  const center = useMemo(() => {
    return location
      ? [location.lat, location.lng]
      : [20.5937, 78.9629];
  }, [location]);

  useEffect(() => {
    if (!mapRef.current) return;

    // 🔥 destroy old map if exists
    if (mapInstanceRef.current) {
      mapInstanceRef.current.remove();
      mapInstanceRef.current = null;
    }

    const map = L.map(mapRef.current).setView(center, 20);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map);

    const featureGroup = new L.FeatureGroup();
    map.addLayer(featureGroup);

    const drawControl = new L.Control.Draw({
      draw: {
        rectangle: true,
        polygon: true,
        polyline: true,
        marker: true,
        circle: false,
        circlemarker: false,
      },
      edit: {
        featureGroup: featureGroup,
      },
    });

    map.addControl(drawControl);

    // restore geometry
    if (savedGeometry) {
      try {
        const geoJsonLayer = L.geoJSON(savedGeometry);
        geoJsonLayer.eachLayer((layer) => {
          featureGroup.addLayer(layer);
          if (layer.getBounds) map.fitBounds(layer.getBounds());
        });
      } catch (e) {}
    }

    map.on(L.Draw.Event.CREATED, (e) => {
      const layer = e.layer;
      featureGroup.addLayer(layer);

      const geoJson = layer.toGeoJSON();
      setGeometry(geoJson);
      onGeometrySave?.(geoJson);

      if (geoJson.geometry.type === "Polygon") {
        const polygon = turf.polygon(geoJson.geometry.coordinates);
        const polygonArea = turf.area(polygon);
        setArea(polygonArea);
        onAreaSave?.(polygonArea);
      }
    });

    mapInstanceRef.current = map;

    // 🔥 fix half map issue
    setTimeout(() => map.invalidateSize(), 200);

    return () => {
      map.remove();
      mapInstanceRef.current = null;
    };
  }, [center]);

  useEffect(() => {
    if (geometry && area) {
      setShowToast({ label: "Marking Successfully Captured" });
    }
  }, [geometry, area]);

  return (
    <Modal
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      hideSubmit
      popupStyles={{ width: "90vw", height: "85vh" }}
    >
      <div className="gis-markmap-wrapper">
        <div ref={mapRef} className="gis-markmap-map" />
      </div>

      {showToast && (
        <Toast label={showToast.label} onClose={() => setShowToast(null)} />
      )}
    </Modal>
  );
};

export default MarkOnMap;