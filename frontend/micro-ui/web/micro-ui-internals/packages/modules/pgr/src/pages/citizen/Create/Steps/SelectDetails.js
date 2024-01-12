import React, { useState } from "react";
import { FormStep } from "@egovernments/digit-ui-react-components";

const SelectDetails = ({ t, config, onSelect, value }) => {
  const pttype=sessionStorage.getItem("type")
  const storedpropertyid=sessionStorage.getItem("propertyid")
  let value2=value
  const [details, setDetails] = useState(() => {
    if(pttype=="PT"){        
        value2=storedpropertyid;
        return value2;      
    }    
    else{
      const {details}=value;
      return details ? details : "";
    }
    
  });

  const onChange = (event) => {
    const { value } = event.target;
    setDetails(value);
  };

  return <FormStep config={config} onChange={onChange} onSelect={() => onSelect({ details })} value={details} t={t} />;
};

export default SelectDetails;
