import React, { useState } from "react";

import { LabelFieldPair, CardLabel, TextInput, CardLabelError, CheckBox } from "@egovernments/digit-ui-react-components";

import { useLocation } from "react-router-dom";

const WmsCMAllowDirectPayment = ({ t, config, onSelect, formData = {}, userType, register, errors }) => {
console.log("formData ",{config, onSelect, formData})

  const { pathname: url } = useLocation();
const [CheckBoxData,setCheckBoxData]=useState(formData?.WmsCMAllowDirectPayment)
console.log("CheckBoxData ",CheckBoxData)
  const inputs = [

    {
      label: "Allow Direct Payment",
      type: "checkbox",
      name: "allow_direct_payment",
      
      // validation: {
      //   isRequired: true,
      //   // pattern: Digit.Utils.getPattern('Name'),
      //   // title: t("CORE_COMMON_APPLICANT_NAME_INVALID"),
      //   title: "Require",

      // },
      // isMandatory: true,
    },

  ];

 

  function setValue(value, input) {
    setCheckBoxData(CheckBoxData?.allow_direct_payment=="Yes"?{allow_direct_payment:"No"}:{allow_direct_payment:"Yes"})
    onSelect(config.key, { ...formData[config.key], [input]: value?"Yes":"No" });

  }
  return (

    <div>

      {inputs?.map((input, index) => {

        let currentValue=formData && formData[config.key] && formData[config.key][input.name]||'';

        return(<React.Fragment key={index}>

          {errors[input.name] && <CardLabelError>{t(input.error)}</CardLabelError>}

          <LabelFieldPair>

            <CardLabel className="card-label-smaller">

              {t(input.label)}

              {/* {input.isMandatory ? " * " : null} */}

            </CardLabel>

            <div className="field">

              <CheckBox

                key={input.name}

                onChange={(e) =>setValue(e.target.checked, input.name)}

                disable={false}

                defaultValue={undefined}
                checked={CheckBoxData?.allow_direct_payment=="Yes"?true:false}

                {...input.validation}

              />

            {/* {currentValue&&currentValue.length>0&&!currentValue.match(Digit.Utils.getPattern('Name'))&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{t("CORE_COMMON_APPLICANT_NAME_INVALID")}</CardLabelError>} */}

            </div>

          </LabelFieldPair>

        </React.Fragment>

      )})}

    </div>

  );

};
export default WmsCMAllowDirectPayment;

 
 