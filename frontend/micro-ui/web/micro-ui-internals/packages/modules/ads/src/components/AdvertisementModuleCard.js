import React from "react";

// this code shows the image and the detail of the advertisement

const AdvertisementModuleCard = ({ imageSrc, title, location, poleNo, price, path, light }) => {
  const handleViewAvailability = () => {
    window.location.href = `${path}bookad/searchads`;
  };

  const handleBookNow = () => {
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