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
    window.location.href = `bookad/searchads`;
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
    window.location.href = `bookad/searchads`;
  };
  return <div className="ads-advertisement-module-card-bordered-rounded">
      <div className="ads-advertisement-module-card-full-width-relative">
        <img src={imageSrc} alt="Advertisement" className="ads-advertisement-module-card-img-full-width" />
      </div>
      <div className="ads-advertisement-module-card-item">
        <p className="ads-advertisement-module-card-icon-red">{light}</p>
        <h3 className="ads-advertisement-module-card-bold">{title}</h3>
        <p>
          {location} (
          <button type="button" className="ads-advertisement-module-card-header-red">
            View Map
          </button>
          )
        </p>
        <div className="ads-advertisement-module-card-row-between-flex">
          <p>Pole No: {poleNo}</p>
          <p>₹ {price}</p>
        </div>
        <div className="ads-advertisement-module-card-row-between-flex">
          <button type="button" onClick={handleViewAvailability} className="ads-advertisement-module-card-btn-green-bordered">
            View Availability
          </button>
          <button type="button" onClick={handleBookNow} className="ads-advertisement-module-card-btn-red-bordered">
            Book Now
          </button>
        </div>
      </div>
    </div>;
};
export { AdvertisementModuleCard };
