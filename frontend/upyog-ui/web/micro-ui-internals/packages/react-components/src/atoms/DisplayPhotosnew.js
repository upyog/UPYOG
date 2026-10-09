import React, { useState } from "react";
import PropTypes from "prop-types";
import {PDFSvg} from "./svgindex"

const ImageOrPDFIcon = ({source, index, last=false, onClick, selectedIndex, drawingNo }) => {
  const isSelected = selectedIndex === index;
  return Digit.Utils.getFileTypeFromFileStoreURL(source) === "pdf" ?
  <div className="rc-display-photos-flex-row">
    <a target="_blank" href={source} className="rc-display-photos-spacing" key={index}>
      <div className="rc-display-photos-flex-row-2">
        <PDFSvg className="rc-display-photos-icon" width="100px" height="100px" minWidth="100px" />
      </div>
    </a>
  </div>
  :
  <div>
  <img className={`photo-thumb-200 ${isSelected ? "photo-selected-border" : ""}`} key={index} src={source}{...(last ? {className:"last" } : {})}alt="issue thumbnail" onClick={() => onClick(source, index)}/>
  <div className="rc-display-photos-top-spacing">
        {drawingNo}
  </div>
  </div>
}

const DisplayPhotosnew = (props) => {
  const [selectedIndex, setSelectedIndex] = useState(null);
  const handleImageClick = (source, index) => {
    setSelectedIndex(index);
    if (props.onClick) {
      props.onClick(source, index);
    }
  };
  return (
    <div className="photos-wrap rc-display-photos-grid-container">
      {props.srcs.thumbs.map((source, index) => {
        return <ImageOrPDFIcon {...{source, index, ...props}} last={index === props.srcs.length - 1} selectedIndex={selectedIndex} onClick={handleImageClick} drawingNo={props.srcs.drawingNo[index]}/>   
      })}
    </div>
  );
};

DisplayPhotosnew.propTypes = {
  /**
   * images
   */
  srcs: PropTypes.array,
  /**
   * optional click handler
   */
  onClick: PropTypes.func,
};

DisplayPhotosnew.defaultProps = {
  srcs: [],
};

export default DisplayPhotosnew;