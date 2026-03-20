import React from "react";

const Option = ({ name, Icon, onClick, className }) => {
  return (
    <div className={className || `CardBasedOptionsMainChildOption`} onClick={onClick}
    style={{ background: "#fff",
    borderRadius: "10px",
    boxShadow: "0 0 8px rgba(0, 0, 0, 0.15)",
    padding: "20px",
    textAlign: "center",
    cursor: "pointer", 
    height: "150px",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",}}>
      <div className="ChildOptionImageWrapper">{Icon}</div>
      <p className="ChildOptionName" style={{paddingBottom: "12px", fontSize: "14px"}}>{name}</p>
    </div>
  );
};

const CardBasedOptions = ({ header, sideOption,type = "", options, styles = {}, style = {} }) => {
  return (
    <div className="CardBasedOptions" style={{
      ...style,
      background: "transparent", // ✅ invisible container
      border: "none",
      boxShadow: "none",
    }}>
    <div className="body" style={{ margin: "0px", padding: "0px", height: "100%" }}>
      <div className="mainContent citizenAllServiceGrid"  style={{
            display: "grid",
            gridTemplateColumns: type === "Information&Updates" ? "repeat(4, 1fr)" : "repeat(6, 1fr)", // ✅ 4 items per row
            gap: "16px", // spacing between items
          }}>
        {options.map((props, index) =>
          <Option key={index} {...props} />
        )}
      </div>
    </div>
    </div>
  );
};

export default CardBasedOptions;