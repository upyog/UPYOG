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
import "../css/ads-inline-auto.css";
const AdvertisementModuleCard = ({
  imageSrc,
  title,
  location,
  poleNo,
  price,
  path,
  light,
  adType,
  faceArea
}) => {
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("ADS_CREATE", {});
  const handleViewAvailability = () => {
    setParams({
      faceArea: {
        code: faceArea,
        value: faceArea,
        i18nKey: faceArea
      },
      adType: {
        code: adType,
        value: adType,
        i18nKey: adType
      },
      location: {
        code: location,
        value: location,
        i18nKey: location
      },
      fromDate: new Date().toISOString().split("T")[0],
      // Current date
      toDate: new Date(new Date().setMonth(new Date().getMonth() + 2)).toISOString().split("T")[0],
      // 3 months later
      nightLight: {
        i18nKey: "Yes",
        code: "Yes",
        value: "true"
      }
    });
    window.location.href = `${path}bookad/searchads`;
  };
  useEffect(() => {
    clearParams();
  }, []);
  const handleBookNow = () => {
    setParams({
      faceArea: {
        code: faceArea,
        value: faceArea,
        i18nKey: faceArea
      },
      adType: {
        code: adType,
        value: adType,
        i18nKey: adType
      },
      location: {
        code: location,
        value: location,
        i18nKey: location
      },
      nightLight: {
        i18nKey: "Yes",
        code: "Yes",
        value: "true"
      }
    });
    window.location.href = `${path}bookad/searchads`;
  };
  return <div className="ads-auto-30">
      <div className="ads-auto-31">
        <img src={imageSrc} alt="Advertisement" className="ads-auto-32" />
      </div>
      <div className="ads-auto-33">
        <p className="ads-auto-34">{light}</p>
        <h3 className="ads-auto-35">{title}</h3>
        <p>
          {location} (
          <button type="button" className="ads-auto-36">
            View Map
          </button>
          )
        </p>
        <div className="ads-auto-37">
          <p>Pole No: {poleNo}</p>
          <p>₹ {price}</p>
        </div>
        <div className="ads-auto-38">
          <button type="button" onClick={handleViewAvailability} className="ads-auto-39">
            View Availability
          </button>
          <button type="button" onClick={handleBookNow} className="ads-auto-40">
            Book Now
          </button>
        </div>
      </div>
    </div>;
};
export { AdvertisementModuleCard };
