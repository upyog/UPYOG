import React from "react";
import { LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
const WmsMbId = ({ t, config, onSelect, formData = {}, userType, register, errors }) => {
  const inputs = [
    {
      label: "",
      type: "text",
      name: "sor_id",
    },
  ];
  return (
    <div>
      {inputs?.map((input, index) => {
        let currentValue=formData && formData[config.key] && formData[config.key][input.name]||'';
        return(<React.Fragment key={index}>          
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">
              {t(input.label)}
            </CardLabel>
            <div className="field">
              {currentValue}
            </div>
          </LabelFieldPair>
        </React.Fragment>
      )})}
    </div>
  );
};

export default WmsMbId;
