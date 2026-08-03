import React, { useState } from "react";
import {
  Card,
  Header,
  LabelFieldPair,
  CardLabelError,
  CardLabel,
  TextInput,
  Dropdown,
  FormComposer,
  TextArea,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const SelectULB = ({ userType, t, setValue, onSelect, config, data, formData, register, errors, setError, clearErrors, formState, control }) => {  return (
    <React.Fragment>
      <LabelFieldPair>
        <CardLabel style={{ fontWeight: "bold" }}>{t("ES_COMMON_DOC_DESCRIPTION")}</CardLabel>
        <div className="field">
          {(() => {
            const { ref: descRef, ...descRest } = register(config.key, {
              maxLength: 140,
            });
            return (
              <TextArea
                name={config.key}
                inputRef={descRef}
                {...descRest}
              />
            );
          })()}
          {errors?.description?.type && <CardLabelError>{t(`ERROR_${errors?.description?.type?.toUpperCase()}_140`)}</CardLabelError>}
        </div>
      </LabelFieldPair>
    </React.Fragment>
  );
};

export default SelectULB;
