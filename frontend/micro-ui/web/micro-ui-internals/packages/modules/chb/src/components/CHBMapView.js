import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from "react-i18next";
import { useHistory } from 'react-router-dom';
import { CardLabel,SubmitBar, Dropdown } from '@nudmcdgnpm/digit-ui-react-components';


const CHBMapView = () => {
  const mapRef = useRef(null);
  const [userLocation, setUserLocation] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [inputValue, setInputValue] = useState('');
  const { t } = useTranslation();
  const history = useHistory();

  const geoJsonData = {
    "type": "FeatureCollection",
    "features": [
      // Delhi Cantt Community Halls
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.123490, 28.564143] },
        "properties": {
          "city": "Delhi Cantt", "price": 9000, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Atul", "capacity": 250, "contact_number": "011-25695450",
          "community_hall_code": "MEHRAM_NAGAR_BARAT_GHAR", "community_hall_name": "Mehram Nagar Barat Ghar",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.1378208, 28.5802044] },
        "properties": {
          "city": "Delhi Cantt", "price": 9200, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Nikhil", "capacity": 300, "contact_number": "011-25695450",
          "community_hall_code": "JHARERA_BARAT_GHAR", "community_hall_name": "Jharera Barat Ghar"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.115316, 28.605519] },
        "properties": {
          "city": "Delhi Cantt", "price": 8800, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Nikhil", "capacity": 200, "contact_number": "011-25695450",
          "community_hall_code": "MANGLAM_BARAT_GHAR", "community_hall_name": "Manglam Barat Ghar",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"

        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.1322511, 28.5978913] },
        "properties": {
          "city": "Delhi Cantt", "price": 9100, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Nikhil", "capacity": 300, "contact_number": "011-25695450",
          "community_hall_code": "ASHOKA_BARAT_GHAR", "community_hall_name": "Ashoka Barat Ghar"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.1195994, 28.5934095] },
        "properties": {
          "city": "Delhi Cantt", "price": 9300, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Kunal", "capacity": 350, "contact_number": "011-25695450",
          "community_hall_code": "ALANKAR_BARAT_GHAR", "community_hall_name": "Alankar Barat Ghar",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.1196247, 28.5926723] },
        "properties": {
          "city": "Delhi Cantt", "price": 9500, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Kunal", "capacity": 500, "contact_number": "011-25695450",
          "community_hall_code": "DUSSEHRA_GROUND_PART_I", "community_hall_name": "Dussehra Ground Part I"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.1196247, 28.5926723] },
        "properties": {
          "city": "Delhi Cantt", "price": 9500, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Kunal", "capacity": 500, "contact_number": "011-25695450",
          "community_hall_code": "DUSSEHRA_GROUND_PART_II", "community_hall_name": "Dussehra Ground Part II",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.121770, 28.596320] },
        "properties": {
          "city": "Delhi Cantt", "price": 8900, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Sourabh", "capacity": 300, "contact_number": "011-25695450",
          "community_hall_code": "DAV_GROUND", "community_hall_name": "DAV Ground",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.120439, 28.594060] },
        "properties": {
          "city": "Delhi Cantt", "price": 8800, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Sourabh", "capacity": 300, "contact_number": "011-25695450",
          "community_hall_code": "JAIN_MANDIR", "community_hall_name": "Jain Mandir"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [77.121738, 28.595236] },
        "properties": {
          "city": "Delhi Cantt", "price": 8800, "state": "Delhi", "tenant": "pg.delhi",
          "manager": "Sourabh", "capacity": 300, "contact_number": "011-25695450",
          "community_hall_code": "MASJID_GROUND", "community_hall_name": "Masjid Ground"
        }
      },
  
      // Punjab (Mohali) Community Halls (already provided in previous step)
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [76.708, 30.683] },
        "properties": {
          "city": "Mohali", "price": 9600, "state": "Punjab", "tenant": "pg.mohali",
          "manager": "Kavita Yadav", "capacity": 360, "contact_number": "9876512347",
          "community_hall_code": "Sector_54_Phase_2", "community_hall_name": "Sector 54 Phase 2", "community_hall_id":"1",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [76.709, 30.686] },
        "properties": {
          "city": "Mohali", "price": 9700, "state": "Punjab", "tenant": "pg.mohali",
          "manager": "Vikram Mehra", "capacity": 370, "contact_number": "9876512346",
          "community_hall_code": "Sector_55_Phase_1", "community_hall_name": "Sector 55 Phase 1","community_hall_id":"2"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [76.711, 30.689] },
        "properties": {
          "city": "Mohali", "price": 9800, "state": "Punjab", "tenant": "pg.mohali",
          "manager": "Neha Gupta", "capacity": 390, "contact_number": "9876512345",
          "community_hall_code": "Sector_59_Phase_5", "community_hall_name": "Sector 59 Phase 5","community_hall_id":"3",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [76.713, 30.692] },
        "properties": {
          "city": "Mohali", "price": 10500, "state": "Punjab", "tenant": "pg.mohali",
          "manager": "Amit Joshi", "capacity": 420, "contact_number": "9876512344",
          "community_hall_code": "Sector_61_Phase_7", "community_hall_name": "Sector 61 Phase 7","community_hall_id":"4"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [76.715, 30.695] },
        "properties": {
          "city": "Mohali", "price": 11000, "state": "Punjab", "tenant": "pg.mohali",
          "manager": "Pooja Singh", "capacity": 450, "contact_number": "9876512343",
          "community_hall_code": "Sector_65_Phase_11", "community_hall_name": "Sector 65 Phase 11","community_hall_id":"5",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [76.717, 30.698] },
        "properties": {
          "city": "Mohali", "price": 9200, "state": "Punjab", "tenant": "pg.mohali",
          "manager": "Rohit Verma", "capacity": 320, "contact_number": "9876512342",
          "community_hall_code": "Sector_69", "community_hall_name": "Sector 69","community_hall_id":"6",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [76.718, 30.701] },
        "properties": {
          "city": "Mohali", "price": 9500, "state": "Punjab", "tenant": "pg.mohali",
          "manager": "Anjali Sharma", "capacity": 350, "contact_number": "9876512341",
          "community_hall_code": "Sector_70", "community_hall_name": "Sector 70","community_hall_id":"7",
          "image_url": "https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"
        }
      },
      {
        "type": "Feature",
        "geometry": { "type": "Point", "coordinates": [76.7191, 30.7046] },
        "properties": {
          "city": "Mohali", "price": 10000, "state": "Punjab", "tenant": "pg.mohali",
          "manager": "Deepak Kumar", "capacity": 400, "contact_number": "9876512340",
          "community_hall_code": "Sector_71", "community_hall_name": "Sector 71","community_hall_id":"8"
        }
      }
    ]
  };
  
  const statusOptions = geoJsonData.features.map(feature => ({
    code: feature.properties.community_hall_code,
    value: feature.properties.community_hall_code,
    i18nKey: feature.properties.community_hall_code
  }));

  const handleSearch = () => {
    // This will just trigger a re-run of the useEffect with updated searchTerm
    setSearchTerm(inputValue?.value || inputValue || "");
  };

  const calculateDistance = (lat1, lng1, lat2, lng2) => {
    const R = 6371;
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLng/2) * Math.sin(dLng/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  };

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
          // Do NOT set a default; just leave userLocation null
        }
      );
    }
  }, []);

  useEffect(() => {
    if (!userLocation) return;

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
  }, [userLocation,searchTerm]);

  const initMap = () => {
    if (!mapRef.current) return;

    if (mapRef.current._leaflet_id) {
        mapRef.current._leaflet_id = null;
        mapRef.current.innerHTML = "";
    }

    const map = window.L.map(mapRef.current).setView([userLocation.lat, userLocation.lng], 12);
    window.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);

    const userIcon = window.L.divIcon({
      html: '<div style="font-size: 24px; color: blue;">⦿</div>',
      iconSize: [40, 40],
      className: 'user-location-marker'
    });

    window.L.marker([userLocation.lat, userLocation.lng], { icon: userIcon })
      .addTo(map)
      .bindPopup('<b>Your Location</b>');

    // Store markers in a map for searching later
    const markerMap = new Map();

    geoJsonData.features.forEach(feature => {
      const [lng, lat] = feature.geometry.coordinates;
      const props = feature.properties;
      const distance = calculateDistance(userLocation.lat, userLocation.lng, lat, lng);
      const marker = window.L.marker([lat, lng]).addTo(map);

      const popupContent = `
        <div style="position: relative; width: 300px;">
          <img src="${props.image_url || 'https://via.placeholder.com/80x60?text=Hall'}" alt="Community Hall" style="position: absolute; top: 0; right: 0; width: 150px; height: 110px; object-fit: cover; border-radius: 4px;" />
          <div style="margin-right: 85px;">
            <b>${props.community_hall_name}</b><br>
            City: ${props.city}<br>
            Price: ₹${props.price} per Day<br>
            Capacity: ${props.capacity} Person<br>
          </div>
          <div>
            Manager: ${props.manager}<br>
            Contact: ${props.contact_number}<br>
            Distance: ${distance.toFixed(1)} km<br>
          </div>
          <div>
            <a href="https://www.google.com/maps/dir/${userLocation.lat},${userLocation.lng}/${lat},${lng}" 
            target="_blank" 
            style="color: #0066cc; text-decoration: none; font-weight: 500; display: block; margin-bottom: 5px;">
            ${t("CHB_GET_DIRECTION")} (Google Maps)
            </a>
            <button style="
            background-color: #a82227;
            color: white;
            border: none;
            padding: 6px 12px;
            border-radius: 4px;
            font-weight: bold;
            cursor: pointer;
            font-size: 12px;
            " onclick="window.selectHall('${props.community_hall_code}', '${props.community_hall_id}')">
                ${t("CHB_BOOK_NOW")}
            </button>
          </div>
        </div>
        `;
      marker.bindPopup(popupContent);

          // Add to markerMap for searching
        markerMap.set(
        `${props.community_hall_code.toLowerCase()}`,
        { marker, lat, lng }
      );
    });

    // Search and fly-to logic
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
    window.selectHall = (hallCode,hallId) => {
      history.push({
        pathname: `/digit-ui/citizen/chb/bookHall/searchhall`,
        selectedCommunityHall: {code: hallCode, value: hallCode, i18nKey: hallCode, communityHallId:hallId}
      });
    }
      
  };

  return (
    <div>
      <div style={{ marginLeft: "10px", marginRight: "10px"}}>
      <CardLabel>{t("CHB_SEARCH_COMMUNITY_HALL")}</CardLabel>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "flex-start", gap: "16px", position: "relative", zIndex: 2000 }}>
            <Dropdown
            className="form-field"
            selected={inputValue}
            select={setInputValue}
            option={statusOptions}
            placeholder={t("Select Community Hall")}
            optionKey="i18nKey"
            style={{ width: "100%", position: "relative", zIndex: 2001 }}
            t={t}
          />
            <div style={{marginTop:"5px", display: "flex", gap: "16px"}}>
              <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
              <p
                className="link"
                style={{ cursor: "pointer" }}
                onClick={() => {
                  setSearchTerm("");
                  setInputValue("");
                }}
              >
                {t("ES_COMMON_CLEAR_ALL")}
              </p>
            </div>
        </div>

        <div ref={mapRef} style={{ height: '86vh', width: '100%', border: '1px solid #ccc', marginTop: "0px" }} />
      </div>
    </div>
  );
};

export default CHBMapView;
