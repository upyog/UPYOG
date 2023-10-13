import React,{useState} from "react";

import { LabelFieldPair, CardLabel, TextInput, CardLabelError } from "@egovernments/digit-ui-react-components";

import { useLocation } from "react-router-dom";

const WmsCMBankName = ({ t, config, onSelect, formData = {}, userType, register, errors }) => {

  const { pathname: url } = useLocation();
  const [isTrue, setisTrue] = useState(false);

  const inputs = [

    {
      label: "Bank Name",
      type: "text",
      name: "bank_name",
      validation: {
        isRequired: true,
        // pattern: Digit.Utils.getPattern('Name'),
        // title: t("CORE_COMMON_APPLICANT_NAME_INVALID"),
        title: "Require",

      },
      isMandatory: true,
    },

  ];

 function blurValue(e){
    if(!e.target.value){setisTrue(true)}else{setisTrue(false)}
 }
  function setValue(value, input) {
    onSelect(config.key, { ...formData[config.key], [input]: value });
    if(value){setisTrue(false)}else{setisTrue(true)}
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

              {input.isMandatory ? <span style={{"color":"red"}}> * </span> : null}

            </CardLabel>

            <div className="field">

              <TextInput

                key={input.name}

                value={formData && formData[config.key] ? formData[config.key][input.name] : undefined}

                onChange={(e) => setValue(e.target.value, input.name)}
                onBlur={blurValue}

                disable={false}

                defaultValue={undefined}

                {...input.validation}

              />
            {isTrue&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{t("Require Field")}</CardLabelError>}

            {currentValue&&currentValue.length>0&&!currentValue.match(Digit.Utils.getPattern('Name'))&&<CardLabelError style={{ width: "100%", marginTop: '-15px', fontSize: '16px', marginBottom: '12px'}}>{t("Only Alphabets are allowed")}</CardLabelError>}

            </div>

          </LabelFieldPair>

        </React.Fragment>

      )})}

    </div>

  );

};
export default WmsCMBankName;

 