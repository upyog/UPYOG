import React from "react";
import { LabelFieldPair, CardLabel, TextInput } from "@nudmcdgnpm/digit-ui-react-components";


const DocumentName = ({ userType, t, setValue, onSelect, config, data, formData, register, errors, setError, clearErrors, formState, control }) => {
  return (
    <React.Fragment>
      <LabelFieldPair className="eng-engagement-doc-name-bottom-spacing"> 
        <CardLabel className="eng-engagement-docs-ulb-card">{t("ES_COMMON_DOC_NAME") + " *"}</CardLabel>
        <div className="field">
          {(() => {
            const { ref: nameRef, ...nameRest } = register(config.key);
            return (
              <TextInput name={config.key} inputRef={nameRef} {...nameRest} />
            );
          })()}
        </div>
      </LabelFieldPair>
    </React.Fragment>
  );
};

export default DocumentName;
