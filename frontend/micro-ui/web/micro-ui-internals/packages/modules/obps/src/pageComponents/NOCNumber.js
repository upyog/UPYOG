import React, { useState,useEffect } from "react";
import { FormStep} from "@upyog/digit-ui-react-components";
import Timeline from "../components/Timeline";

const NOCNumber = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState }) => {
    const [nocNumber,setValueNoc]=useState(formData?.additionalDetails?.nocNumber || formData?.nocnumber?.nocNumber ||"");
    const tenantId = Digit.ULBService.getCurrentTenantId();

    useEffect(()=>{
        if(formData?.additionalDetails?.nocNumber || formData?.nocnumber?.nocNumber){
            setValueNoc(formData?.additionalDetails?.nocNumber || formData?.nocnumber?.nocNumber);
        }
    },[formData]);

    function onChange(data) {
        setValueNoc(data.target.value);              
      }

      function onClick(){
        console.log("inside_search")
      }

      const handleSubmit = () => {
            onSelect(config?.key, { nocNumber});
    };

      return (
        <div>
            <Timeline currentStep={0} />
            <FormStep
                    t={t}
                    config={config}
                    onSelect={handleSubmit}
                    isDisabled={ !nocNumber }
                    
                >
                    
                    <input
                    class="card-input"
                    type="text"
                    value={nocNumber}
                    onChange={onChange}
                    style={{marginBottom:'10px'}}
                    defaultValues={formData?.nocnumber?.nocNumber || formData?.additionalDetails?.nocNumber}
                    />
                    <button onClick={onClick} style={{alignSelf:'flex-start',backgroundColor:'marron'}}></button>
                <span  onClick={onClick} style={{alignSelf:'flex-start', marginBottom:'5px', backgroundColor:'maroon', border:'1px solid marron',color:'white',padding:'5px 10px',borderRadius:'3px',cursor:'pointer'}}>SEARCH NOC</span> 
                </FormStep> 
        </div>

      );
}

export default NOCNumber