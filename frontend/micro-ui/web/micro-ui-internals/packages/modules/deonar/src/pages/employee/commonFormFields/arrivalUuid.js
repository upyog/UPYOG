import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, TextInput, LabelFieldPair } from "@egovernments/digit-ui-react-components";
import { Controller } from "react-hook-form";

const ArrivalUuidField = ({ control, setData, data, uuid }) => {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="bmc-col3-card">
        <LabelFieldPair>
          <CardLabel className="bmc-label">{t("DEONAR_ARRIVAL_UUID")}</CardLabel>
          <Controller
            control={control}
            name="arrivalUuid"
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <TextInput
                value={uuid}
                optionKey="i18nKey"
                t={t}
                placeholder={t("DEONAR_ARRIVAL_UUID")}
                disabled={true}
              />
            )}
          />
        </LabelFieldPair>
      </div>    
    </React.Fragment>
  );
};

export default ArrivalUuidField;
