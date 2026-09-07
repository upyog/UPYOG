import React, { useEffect } from "react";
import { SearchIconSvg } from "./svgindex";
import { Loader } from "@googlemaps/js-api-loader";

let defaultBounds = {};

const updateDefaultBounds = (center) => {
  if (!center.lat || !center.lng) {
    return;
  }
  defaultBounds = {
    north: center.lat + 0.1,
    south: center.lat - 0.1,
    east: center.lng + 0.1,
    west: center.lng - 0.1,
  };
};
const GetPinCode = (places) => {
  let postalCode = null;
  places?.address_components?.forEach((place) => {
    let hasPostalCode = place.types.includes("postal_code");
    postalCode = hasPostalCode ? place.long_name : null;
  });
  return postalCode;
};

const getName = (places) => {
  let name = "";
  places?.address_components?.forEach((place) => {
    let hasName = place.types.includes("sublocality_level_2") || place.types.includes("sublocality_level_1");
    if (hasName) {
      name = hasName ? place.long_name : null;
    }
  });
  return name;
};

const loadGoogleMaps = (callback) => {
  // Google Maps API key must be provided through
  // window.globalConfigs.getConfig("GMAPS_API_KEY")
  const key = window.globalConfigs?.getConfig?.("GMAPS_API_KEY") || window.globalConfigs?.getConfig?.("GMAPS_API_KEY");
  if (!key) {
    console.error("Google Maps API key is not configured.");
    return;
  }
  const loader = new Loader({
    apiKey: key,
    version: "weekly",
    libraries: ["places", "geometry", "drawing"],
  });

  loader
    .load()
    .then(() => {
      if (callback) callback();
    })
    .catch((e) => {
      // do something
    });
};

const mapStyles = [
  {
    elementType: "geometry",
    stylers: [
      {
        color: "#f5f5f5",
      },
    ],
  },
  {
    elementType: "labels.icon",
    stylers: [
      {
        visibility: "off",
      },
    ],
  },
  {
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#616161",
      },
    ],
  },
  {
    elementType: "labels.text.stroke",
    stylers: [
      {
        color: "#f5f5f5",
      },
    ],
  },
  {
    featureType: "administrative.land_parcel",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#bdbdbd",
      },
    ],
  },
  {
    featureType: "poi",
    elementType: "geometry",
    stylers: [
      {
        color: "#eeeeee",
      },
    ],
  },
  {
    featureType: "poi",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#757575",
      },
    ],
  },
  {
    featureType: "poi.park",
    elementType: "geometry",
    stylers: [
      {
        color: "#e5e5e5",
      },
    ],
  },
  {
    featureType: "poi.park",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#9e9e9e",
      },
    ],
  },
  {
    featureType: "road",
    elementType: "geometry",
    stylers: [
      {
        color: "#ffffff",
      },
    ],
  },
  {
    featureType: "road.arterial",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#757575",
      },
    ],
  },
  {
    featureType: "road.highway",
    elementType: "geometry",
    stylers: [
      {
        color: "#dadada",
      },
    ],
  },
  {
    featureType: "road.highway",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#616161",
      },
    ],
  },
  {
    featureType: "road.local",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#9e9e9e",
      },
    ],
  },
  {
    featureType: "transit.line",
    elementType: "geometry",
    stylers: [
      {
        color: "#e5e5e5",
      },
    ],
  },
  {
    featureType: "transit.station",
    elementType: "geometry",
    stylers: [
      {
        color: "#eeeeee",
      },
    ],
  },
  {
    featureType: "water",
    elementType: "geometry",
    stylers: [
      {
        color: "#c9c9c9",
      },
    ],
  },
  {
    featureType: "water",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#9e9e9e",
      },
    ],
  },
];

const setLocationText = (location, onChange, inputElement, isPlaceRequired = false) => {
  const geocoder = new google.maps.Geocoder();
  geocoder.geocode(
    {
      location,
    },
    function (results, status) {
      if (status === "OK") {
        if (results[0]) {
          let pincode = GetPinCode(results[0]);
          if (inputElement) {
            inputElement.value = getName(results[0]);
          }
          if (onChange) {
            if (isPlaceRequired) {
              onChange(pincode, { longitude: location.lng, latitude: location.lat }, inputElement ? inputElement.value : "");
            } else {
              onChange(pincode, { longitude: location.lng, latitude: location.lat });
            }
          }
        }
      }
    }
  );
};

const onMarkerDragged = (marker, onChange, inputElement, isPlaceRequired = false) => {
  if (!marker) return;
  const { latLng } = marker;
  const currLat = latLng.lat();
  const currLang = latLng.lng();
  const location = {
    lat: currLat,
    lng: currLang,
  };
  if (isPlaceRequired) {
    setLocationText(location, onChange, inputElement, true);
  } else {
    setLocationText(location, onChange, inputElement);
  }
};

const initAutocomplete = (onChange, position, mapElement, inputElement, isPlaceRequired = false) => {
  if (!mapElement || !inputElement) return null;
  const map = new window.google.maps.Map(mapElement, {
    center: position,
    zoom: 15,
    mapTypeId: "roadmap",
    styles: mapStyles,
  });

  const input = inputElement;
  updateDefaultBounds(position);
  const options = {
    bounds: defaultBounds,
    componentRestrictions: { country: "in" },
    fields: ["address_components", "geometry", "icon", "name"],
    origin: position,
    strictBounds: false,
    types: ["address"],
  };
  const searchBox = new window.google.maps.places.Autocomplete(input, options);

  const boundsListener = map.addListener("bounds_changed", () => {
    searchBox.setBounds(map.getBounds());
  });

  let markers = [
    new window.google.maps.Marker({
      map,
      title: "a",
      position: position,
      draggable: true,
      clickable: true,
    }),
  ];

  if (isPlaceRequired) {
    setLocationText(position, onChange, input, true);
  } else {
    setLocationText(position, onChange, input);
  }

  const dragListener = markers[0].addListener("dragend", (marker) => onMarkerDragged(marker, onChange, input, isPlaceRequired));

  const placeListener = searchBox.addListener("place_changed", () => {
    const place = searchBox.getPlace();

    if (!place) {
      return;
    }
    let pincode = GetPinCode(place);
    if (pincode) {
      const { geometry } = place;
      const geoLocation = {
        latitude: geometry.location.lat(),
        longitude: geometry.location.lng(),
      };
      if (isPlaceRequired) {
        onChange(pincode, geoLocation, place.name);
      } else {
        onChange(pincode, geoLocation);
      }
    }
    markers.forEach((marker) => {
      marker.setMap(null);
    });
    markers = [];

    const bounds = new window.google.maps.LatLngBounds();
    if (!place.geometry) {
      return;
    }

    markers.push(
      new window.google.maps.Marker({
        map,
        title: place.name,
        position: place.geometry.location,
        draggable: true,
        clickable: true,
      })
    );
    markers[0].addListener("dragend", (marker) => onMarkerDragged(marker, onChange, input, isPlaceRequired));
    if (place.geometry.viewport) {
      bounds.union(place.geometry.viewport);
    } else {
      bounds.extend(place.geometry.location);
    }

    map.fitBounds(bounds);
  });

  // Return cleanup function to unbind listeners
  return () => {
    if (boundsListener && typeof boundsListener.remove === "function") boundsListener.remove();
    if (dragListener && typeof dragListener.remove === "function") dragListener.remove();
    if (placeListener && typeof placeListener.remove === "function") placeListener.remove();
  };
};

const LocationSearch = (props) => {
  const mapRef = React.useRef(null);
  const inputRef = React.useRef(null);

  useEffect(() => {
    let active = true;
    let cleanupListeners = null;

    async function mapScriptCall() {
      const getLatLng = (position) => {
        if (!active) return;
        const lat = parseFloat(position.coords?.latitude || position.latitude);
        const lng = parseFloat(position.coords?.longitude || position.longitude);
        if (isNaN(lat) || isNaN(lng)) {
          getLatLngError();
        } else {
          cleanupListeners = initAutocomplete(
            props.onChange,
            { lat, lng },
            mapRef.current,
            inputRef.current,
            props.isPlaceRequired
          );
        }
      };

      const getLatLngError = (error) => {
        if (!active) return;
        let defaultLatLong = {};
        if (props?.isPTDefault) {
          defaultLatLong = props?.PTdefaultcoord?.defaultConfig || { lat: 31.6160638, lng: 74.8978579 };
        } else {
          defaultLatLong = {
            lat: 31.6160638,
            lng: 74.8978579,
          };
        }
        cleanupListeners = initAutocomplete(
          props.onChange,
          defaultLatLong,
          mapRef.current,
          inputRef.current,
          props.isPlaceRequired
        );
      };

      const initMaps = () => {
        if (!active) return;
        if (props.position?.latitude && props.position?.longitude) {
          getLatLng({ coords: props.position });
        } else if (navigator?.geolocation) {
          navigator.geolocation.getCurrentPosition(getLatLng, getLatLngError);
        } else {
          getLatLngError();
        }
      };

      loadGoogleMaps(initMaps);
    }

    mapScriptCall();

    return () => {
      active = false;
      if (cleanupListeners) cleanupListeners();
    };
  }, []);

  return (
    <div className="map-wrap">
      <div className="map-search-bar-wrap">
        <SearchIconSvg className="map-search-bar-icon" />
        <input
          ref={inputRef}
          id="pac-input"
          className="map-search-bar"
          type="text"
          placeholder="Search Address"
          style={{ backgroundPosition: "left" }}
        />
      </div>
      <div ref={mapRef} id="map" className="map"></div>
    </div>
  );
};

export default LocationSearch;
