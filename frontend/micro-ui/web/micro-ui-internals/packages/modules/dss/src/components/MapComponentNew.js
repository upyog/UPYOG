import React, { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css'; // Ensure you import Leaflet's CSS for styling
import geojsonData from './countries.geo.json'; // Import your local GeoJSON file
import 'leaflet.markercluster/dist/MarkerCluster.css';
import 'leaflet.markercluster/dist/MarkerCluster.Default.css';
import 'leaflet.markercluster';

const LeafletMap = () => {
  const [complaint, setComplaint] = useState([]);
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null); // Ref to store the map instance
  const { isLoading, error, data, revalidate } = Digit.Hooks.pgr.useComplaintsListByServiceRequestPlain();
  useEffect(() => {
    // Initialize Leaflet map when component mounts
    // Center the map on India with a suitable zoom level
    const map = L.map(mapRef.current).setView([20.5937, 78.9629], 1.5);
    mapInstanceRef.current = map; // Store the map instance

    // Add a tile layer (example using OpenStreetMap tiles)
    var googleMapsUrl = 'https://mt1.google.com/vt/lyrs=r&x={x}&y={y}&z={z}&key=AIzaSyBrWOKWviSRGqTTJMVPIs9FzMhKXSXtZLs';

    L.tileLayer(googleMapsUrl, {
      attribution: '&copy; <a href="https://maps.google.com">Google Maps</a>',
      maxZoom: 20
    }).addTo(map);

    // Add GeoJSON layer to the map from your local file
    L.geoJSON(geojsonData, {
      style: feature => {
        // Define default style for all countries
        const defaultStyle = {
          fillColor: '#D6D6DA',
          fillOpacity: 0,
          color: '#FFFFFF',
          weight: 1
        };

        // Conditionally set style for specific countries
        let highlightStyle = null;
        // if (feature.properties.name === 'India' || feature.properties.name === 'United States of America') {
        //   highlightStyle = {
        //     fillColor: 'red',
        //     fillOpacity: 0.5,
        //     color: 'black',
        //     weight: 2
        //   };
        // }

        return highlightStyle || defaultStyle; // Use highlight style if set, otherwise default style
      }
    }).addTo(map);

    if (mapRef.current) {
      setTimeout(() => {
        map.invalidateSize(); // Call invalidateSize on map instance directly
      }, 1000);
    }

    return () => {
      // Clean up Leaflet map when component unmounts
      map.remove();
    };
  }, []);

  useEffect(() => {
    setComplaint(data);

    if (data) {
      const filteredCoordinates = (data.ServiceWrappers || [])
        .filter(wrapper => wrapper.service.address && 
                           wrapper.service.address.geoLocation && 
                           wrapper.service.address.geoLocation.latitude && 
                           wrapper.service.address.geoLocation.latitude !== 0 && 
                           wrapper.service.address.geoLocation.longitude && 
                           wrapper.service.address.geoLocation.longitude !== 0)
        .map(wrapper => ({
          latitude: wrapper.service.address.geoLocation.latitude,
          longitude: wrapper.service.address.geoLocation.longitude,
          serviceRequestId: wrapper.service.serviceRequestId,
          serviceCode: wrapper.service.serviceCode,
          description: wrapper.service.description
        }));

      if (filteredCoordinates.length > 0) {
        console.log("filteredCoordinates", filteredCoordinates);
        const markersGroup = L.markerClusterGroup({
          iconCreateFunction: function(cluster) {
            let count = cluster.getChildCount();
            let size = 'large';
            if (count < 10) {
              size = 'small';
            } else if (count < 100) {
              size = 'medium';
            }

            let className = 'marker-cluster marker-cluster-' + size;
            return new L.DivIcon({
              html: '<div><span>' + count + '</span></div>',
              className: className,
              iconSize: new L.Point(40, 40)
            });
          }
        });

        filteredCoordinates.forEach((coordinates, index) => {
          const marker = L.marker([coordinates.latitude, coordinates.longitude])
            .bindPopup(`<b>Incident ID:</b> ${coordinates.serviceRequestId}<br>
                        <b>Incident Type:</b> ${coordinates.serviceCode}<br>
                        <b>Description:</b> ${coordinates.description}`);
          markersGroup.addLayer(marker);
        });

        const map = mapInstanceRef.current;
        if (map) {
          map.addLayer(markersGroup);
        } else {
          console.error("Map instance is not available");
        }
      }
    }
  }, [data]);

  return (
    <div>
      <style>
        {`
          .marker-cluster-small {
            background-color: rgba(181, 226, 140, 0.6);
            color: white;
          }
          
          .marker-cluster-medium {
            background-color: rgba(241, 211, 87, 0.6);
            color: white;
          }
          
          .marker-cluster-large {
            background-color: rgba(253, 156, 115, 0.6);
            color: white;
          }
          
          .marker-cluster {
            width: 30px;
            height: 30px;
            margin-left: 5px;
            margin-top: 5px;
            border-radius: 50%;
            text-align: center;
            line-height: 30px;
          }
        `}
      </style>
      <div ref={mapRef} id="map" style={{ position: 'relative', height: '65vh', overflow: 'hidden' }} />
    </div>
  );
};

export default LeafletMap;
