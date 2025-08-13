import React, { useEffect } from 'react';

/**
 * AdvertisementModuleCard Component
 *
 * Renders an advertisement card with details such as image, title, location, pole number, 
 * price, and lighting information. The component manages session storage parameters 
 * (`ADS_CREATE`) to persist selected ad details for booking or availability search.
 * - `handleViewAvailability`: Sets session storage with ad details and redirects to the search page.
 * - `useEffect`: Clears session storage on component mount to prevent stale data.
 */

const AdvertisementModuleCard = ({ imageSrc, title, location, poleNo, price, path, light,adType,faceArea }) => {
  const [params, setParams,clearParams] = Digit.Hooks.useSessionStorage("ADS_CREATE", {});
  const handleViewAvailability = () => {
    setParams({
      faceArea:{code:faceArea,value:faceArea,i18nKey:faceArea},
      adType:{code:adType,value:adType,i18nKey:adType},
      location:{code:location,value:location,i18nKey:location},
      fromDate: new Date().toISOString().split("T")[0], // Current date
      toDate: new Date(new Date().setMonth(new Date().getMonth() + 2)).toISOString().split("T")[0], // 3 months later
      nightLight:{
        i18nKey: "Yes",
        code: "Yes",
        value: "true",
      }
    });
    window.location.href = `${path}bookad/searchads`;
  };
  useEffect(() => {
    clearParams();
  }, []); 
  const handleBookNow = () => {
    setParams({
      faceArea:{code:faceArea,value:faceArea,i18nKey:faceArea},
      adType:{code:adType,value:adType,i18nKey:adType},
      location:{code:location,value:location,i18nKey:location},
      nightLight:{
        i18nKey: "Yes",
        code: "Yes",
        value: "true",
      }
    });
    window.location.href = `${path}bookad/searchads`;
  };
  return (
    <div
      style={{
        border: "1px solid #ccc",
        backgroundColor: "white",
        borderRadius: "8px",
        overflow: "hidden",
        maxWidth: "30%",
        margin: "10px auto",
        minWidth: "24%",
      }}
    >
      <div style={{ width: "100%", height: "200px", position: "relative",padding: "10px"}}>
        <img
          src={imageSrc}
          alt="Advertisement"
          style={{
            width: "100%",
            height: "100%",
            backgroundSize: "cover",
            backgroundRepeat: "no-repeat",
            backgroundPosition: "center",
            minWidth: "0",
          }}
        />
      </div>
      <div style={{ padding: "10px" }}>
        <p style={{ margin: "0", color: "#a82227" }}>{light}</p>
        <h3 style={{ margin: "5px 0", fontWeight: "bold" }}>{title}</h3>
        <p>
          {location} (
          <button type="button" style={{ marginLeft: "5px", color: "#a82227" }}>
            View Map
          </button>
          )
        </p>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <p>Pole No: {poleNo}</p>
          <p>₹ {price}</p>
        </div>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <button
            type="button"
            onClick={handleViewAvailability}
            style={{ backgroundColor: "green", color: "white", border: "1px solid #ccc", padding: "5px 10px", borderRadius: "4px" }}
          >
            View Availability
          </button>
          <button
            type="button"
            onClick={handleBookNow}
            style={{ backgroundColor: "#a82227", color: "white", border: "1px solid #ccc", padding: "5px 10px", borderRadius: "4px" }}
          >
            Book Now
          </button>
        </div>
      </div>
    </div>
  );
};
export { AdvertisementModuleCard };