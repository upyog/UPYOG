import React, { useEffect } from "react";
import { Loader } from "@googlemaps/js-api-loader";

const LocationCard = ({ position }) => {
  const { latitude, longitude } = position;
  const key = globalConfigs?.getConfig("GMAPS_API_KEY");
  useEffect(() => {
    const loader = new Loader({
      apiKey: key,
      version: "weekly",
    });

    loader.load().then(() => {
      const map = new window.google.maps.Map(document.getElementById("map"), {
        center: { lat: latitude, lng: longitude },
        zoom: 15,
        draggable: false,
      });

      const marker = new window.google.maps.Marker({
        position: { lat: latitude, lng: longitude },
        map: map,
        title: "Location",
      });
    });
  }, [latitude, longitude]);

  return (
    <div className="map-wrap">
      <div id="map" className="map"></div>
    </div>
  );
};

export default LocationCard;
