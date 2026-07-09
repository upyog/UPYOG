import React from "react";

export const AssetCell = ({ value }) => (
  <span className="cell-text">{value || "N/A"}</span>
);

export const formatDimensions = (length, width) => {
  if (length != null && width != null) return `${length} x ${width}`;
  return null;
};

export const sortAssetsByEstateNo = (assets = []) =>
  [...assets].sort((a, b) => {
    const numA = parseInt(String(a.estateNo).split("-").pop(), 10) || 0;
    const numB = parseInt(String(b.estateNo).split("-").pop(), 10) || 0;
    return numA - numB;
  });

export const getAssetTableCellProps = (isMobile) => ({
  style: {
    minWidth: isMobile ? "70px" : "100px",
    padding: isMobile ? "4px 2px" : "8px 6px",
    fontSize: isMobile ? "10px" : "12px",
    textAlign: "center",
    whiteSpace: "nowrap",
  },
});

export const assetTableWrapperStyle = (isMobile) => ({
  overflowX: "auto",
  width: "100%",
  marginTop: "20px",
  WebkitOverflowScrolling: "touch",
  padding: isMobile ? "5px" : "10px",
});
