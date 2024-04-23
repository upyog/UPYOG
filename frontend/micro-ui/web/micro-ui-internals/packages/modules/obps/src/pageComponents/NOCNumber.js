import React, { useState,useEffect } from "react";
import { FormStep,SearchIcon,TextInput } from "@upyog/digit-ui-react-components";
import Timeline from "../components/Timeline";

const NOCNumber = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState }) => {
    const [nocNumber,setValueNoc]=useState(formData?.additionalDetails?.nocNumber || formData?.nocnumber?.nocNumber ||"");
    const tenantId = Digit.ULBService.getCurrentTenantId();
    let Webview = !Digit.Utils.browser.isMobile();

    function onChange(data) {
        setValueNoc(data.target.value);        
      }

      function onClick(e){
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
                    <div className="field-container">
                    <TextInput
                      //style={{ background: "#FAFAFA", marginLeft:"25px"}}
                      class="card-input"
                      type={"text"}
                      t={t}
                      isMandatory={false}
                      optionKey="i18nKey"
                      name="nocNumber"
                      value={nocNumber}
                      onChange={(e) => onChange( e)}
                      defaultValue={formData?.nocnumber?.nocNumber || formData?.additionalDetails?.nocNumber}
                      />                       
                    <div style={{ position: "relative", zIndex: "100", right: "20px", marginTop: "-24px", marginRight:Webview?"-20px":"-20px" }} onClick={(e) => onClick( e)}> <SearchIcon /> </div>
                    </div>
                </FormStep> 
        </div>

      );
}

export default NOCNumber