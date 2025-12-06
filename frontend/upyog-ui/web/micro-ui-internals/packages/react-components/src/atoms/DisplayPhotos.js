import React, { useState } from "react";
import PropTypes from "prop-types";
import { PDFSvg } from "./svgindex";

const ImageOrPDFIcon = ({ source, index, last = false, onClick, selectedIndex, drawingNo }) => {
  const isSelected = selectedIndex === index;

  const isPDF = Digit.Utils.getFileTypeFromFileStoreURL(source) === "pdf";

  if (isPDF) {
    return (
      <div style={{ display: "flex", flexWrap: "wrap", justifyContent: "flex-start", alignContent: "center" }}>
        <a
          target="_blank"
          rel="noopener noreferrer"
          href={source}
          style={{ minWidth: "100px", marginRight: "10px", maxWidth: "100px", height: "auto" }}
        >
          <div style={{ display: "flex", justifyContent: "center" }}>
            <PDFSvg style={{ background: "#f6f6f6", padding: "8px", width: "100px" }} width="100px" height="100px" />
          </div>
        </a>
      </div>
    );
  }

  return (
    <div>
      <img
        style={{
          width: "200px",
          padding: "3px",
          height: "200px",
          margin: "8px",
          border: isSelected ? "4px solid black" : "none",
        }}
        src={source}
        alt="issue thumbnail"
        onClick={() => onClick(source, index)}
        className={last ? "last" : ""}
      />
      {drawingNo && (
        <div style={{ marginTop: "12px", marginLeft: "12px", marginBottom: "5px", fontSize: "14px" }}>
          {drawingNo}
        </div>
      )}
    </div>
  );
};

const DisplayPhotos = ({ srcs, drawingNos = [], onClick }) => {
  const [selectedIndex, setSelectedIndex] = useState(null);

  const handleImageClick = (source, index) => {
    setSelectedIndex(index);
    if (onClick) onClick(source, index);
  };

  return (
    <div
      className="photos-wrap"
      style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))" }}
    >
      {srcs?.map((source, index) => (
        <ImageOrPDFIcon
          key={index}
          source={source}
          index={index}
          last={index === srcs.length - 1}
          selectedIndex={selectedIndex}
          onClick={handleImageClick}
          drawingNo={drawingNos[index]}
        />
      ))}
    </div>
  );
};

DisplayPhotos.propTypes = {
  srcs: PropTypes.arrayOf(PropTypes.string),
  drawingNos: PropTypes.arrayOf(PropTypes.string),
  onClick: PropTypes.func,
};

DisplayPhotos.defaultProps = {
  srcs: [],
  drawingNos: [],
};

export default DisplayPhotos;
