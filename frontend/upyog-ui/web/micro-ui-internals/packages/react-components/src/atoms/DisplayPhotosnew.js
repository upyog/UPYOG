import React, { useState } from "react";
import PropTypes from "prop-types";
import {PDFSvg} from "./svgindex"

const ImageOrPDFIcon = ({source, index, last=false, onClick, selectedIndex, drawingNo }) => {
  const isSelected = selectedIndex === index;
  return Digit.Utils.getFileTypeFromFileStoreURL(source) === "pdf" ?
  <div style={{ display: "flex", flexWrap: "wrap", justifyContent: "flex-start", alignContent: "center" }}>
    <a target="_blank" href={source} style={{ minWidth: "100px", marginRight: "10px", maxWidth: "100px", height: "auto" }} key={index}>
      <div style={{ display: "flex", justifyContent: "center" }}>
        <PDFSvg style={{ background: "#f6f6f6", padding: "8px", width:"100px" }} width="100px" height="100px" minWidth="100px" />
      </div>
    </a>
  </div>
  :
  <div>
  <img style={{width:"200px", padding:"3px",height:"200px", margin:"8px",border: isSelected ? "4px solid black" : "none"}} key={index} src={source}{...(last ? {className:"last" } : {})}alt="issue thumbnail" onClick={() => onClick(source, index)}/>
  <div style={{ marginTop: "12px", marginLeft:"12px", marginBottom:"5px", fontSize: "14px" }}>
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
    <div className="photos-wrap" style={{display:"grid", gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))" }}>
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