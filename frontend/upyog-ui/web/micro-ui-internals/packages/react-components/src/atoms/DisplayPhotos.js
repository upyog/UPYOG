import React, { useState } from "react";
import PropTypes from "prop-types";
import { PDFSvg } from "./svgindex";

const ImageOrPDFIcon = ({ source, index, last = false, onClick, selectedIndex, drawingNo }) => {
  const isSelected = selectedIndex === index;

  const isPDF = Digit.Utils.getFileTypeFromFileStoreURL(source) === "pdf";

  if (isPDF) {
    return (
      <div className="rc-display-photos-flex-row">
        <a
          target="_blank"
          rel="noopener noreferrer"
          href={source}
          className="rc-display-photos-spacing"
        >
          <div className="rc-display-photos-flex-row-2">
            <PDFSvg className="rc-display-photos-icon" width="100px" height="100px" />
          </div>
        </a>
      </div>
    );
  }

  return (
    <div>
      <img className={`display-photo-item ${isSelected ? "border-selected-black" : "border-none"}`}
        src={source}
        alt="issue thumbnail"
        onClick={() => onClick(source, index)}
        className={last ? "last" : ""}
      />
      {drawingNo && (
        <div className="rc-display-photos-top-spacing">
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
      className="photos-wrap rc-display-photos-grid-container"
    >
      {srcs.map((source, index) => (
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
