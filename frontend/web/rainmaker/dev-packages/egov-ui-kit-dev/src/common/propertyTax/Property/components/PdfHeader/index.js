import { Card, Image } from "components";
import Label from "egov-ui-kit/utils/translationNode";
import React from "react";
import "./index.css";

const logoStyle = {
  height: "6em",
  width: "6em",
};

const PdfHeader = ({ header = {}, subHeader = {} }) => {
  const { logoUrl, corpCity, ulbGrade, label: headerLabel } = header;
  const { label: subHeaderLabel, value: subHeaderValue } = subHeader;

  return (
    <div className="pdf-header" id="pdf-header" style={{ width: '100%' }}>
      <Card
                    style={{ display: "flex", backgroundColor: "rgb(255,255,255)", minHeight: "50px", alignItems: "center", paddingLeft: "10px" }}
                    textChildren={
          <div style={{ display: "flex" }}>
            <Image id="image-id" style={logoStyle} source={logoUrl} />
            <div style={{ marginLeft: '2em' }}>
              <div style={{ display: "flex", marginBottom: '0.5em' }}>
                <Label label={corpCity} fontSize="20px" fontWeight="500" color="rgb(0, 0, 0)" containerStyle={{ marginRight: 10, textTransform: "uppercase" }} />
                <Label label={ulbGrade} fontSize="20px" fontWeight="500" color="rgb(0, 0, 0)" />
              </div>
              <Label label={headerLabel} fontSize="16px" fontWeight="500" />
            </div>
          </div>
        }
      />
      {subHeaderLabel && subHeaderValue && <div style={{ display: "flex", justifyContent: "space-between" }}>
        <div style={{ display: "flex" }}>
          <Label label={subHeaderLabel} color="rgb(0, 0, 0)" fontSize="20px" containerStyle={{ marginRight: 10 }} />
          <Label label={subHeaderValue} fontSize="20px" />
        </div>
      </div>}
    </div>
  );
};

export default PdfHeader;
