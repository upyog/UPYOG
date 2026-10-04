import React, { useEffect } from 'react';
// this code shows the image and the detail of the advertisement

const AdvertisementModuleCard = ({ imageSrc, title, location, poleNo, price, path, light,adType,faceArea }) => {
  const [params, setParams,clearParams] = Digit.Hooks.useSessionStorage("ADS_CREATE", {});
  const handleViewAvailability = () => {
    setParams({
      faceArea:{code:faceArea,value:faceArea,i18nKey:faceArea},
      adType:{code:adType,value:adType,i18nKey:adType},
      location:{code:location,value:location,i18nKey:location},
      fromDate: new Date(new Date().setDate(new Date().getDate() + 1)).toISOString().split('T')[0],
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
      className="rc-advertisement-module-card-spacing"
    >
      <div className="rc-advertisement-module-card-fullwidth">
        <img
          src={imageSrc}
          alt="Advertisement"
          className="rc-advertisement-module-card-fullwidth-2"
        />
      </div>
      <div className="rc-advertisement-module-card-container-padding">
        <p className="rc-advertisement-module-card-spacing-2">{light}</p>
        <h3 className="rc-advertisement-module-card-header">{title}</h3>
        <p>
          {location} (
          <button type="button" className="rc-advertisement-module-card-action-btn">
            View Map
          </button>
          )
        </p>
        <div className="rc-advertisement-module-card-flex-row">
          <p>Pole No: {poleNo}</p>
          <p>₹ {price}</p>
        </div>
        <div className="rc-advertisement-module-card-flex-row">
          <button
            type="button"
            onClick={handleViewAvailability}
            className="rc-advertisement-module-card-action-btn-2"
          >
            View Availability
          </button>
          <button
            type="button"
            onClick={handleBookNow}
            className="rc-advertisement-module-card-action-btn-3"
          >
            Book Now
          </button>
        </div>
      </div>
    </div>
  );
};
export { AdvertisementModuleCard };