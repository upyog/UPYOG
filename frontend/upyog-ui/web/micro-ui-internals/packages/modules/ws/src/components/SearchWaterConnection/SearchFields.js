import React, { Fragment } from "react";
import { Controller, useWatch } from "react-hook-form";
import { TextInput, SubmitBar, DatePicker, SearchField, Dropdown, Loader, MobileNumber } from "@nudmcdgnpm/digit-ui-react-components";

const SearchFields = ({ register, control, reset, tenantId, t, onSubmit, onClearSearch }) => {
  const propsForMobileNumber = {
    maxlength: 10,
    pattern: "[6-9][0-9]{9}",
    title: t("ES_SEARCH_APPLICATION_MOBILE_INVALID"),
    componentInFront: "+91",
  };

  const propsForOldConnectionNumberNpropertyId = {
    pattern: "[A-Za-z]{2}-[A-Za-z]{2}-[0-9]{4}-[0-9]{6}",
    title: t("ERR_DEFAULT_INPUT_FIELD_MSG"),
  };
  let validation = {}
  return (
    <>
      <SearchField>
        <label>{t("WS_MYCONNECTIONS_CONSUMER_NO")}</label>
        <Controller
          control={control}
          name="connectionNumber"
          rules={{
            required: false,
            pattern: {
              value: /^[a-zA-Z0-9\/-]*$/,
              message: t("ERR_INVALID_CONSUMER_NO")
            }
          }}
          render={({ field }) => (
            <TextInput
              value={field.value || ""}
              onChange={field.onChange}
              onBlur={field.onBlur}
            />
          )}
        />
      </SearchField>
      <SearchField>
        <label>{t("WS_SEARCH_CONNNECTION_OLD_CONSUMER_LABEL")}</label>
        <Controller
          control={control}
          name="oldConnectionNumber"
          rules={{
            pattern: {
              value: new RegExp(propsForOldConnectionNumberNpropertyId.pattern),
              message: propsForOldConnectionNumberNpropertyId.title
            }
          }}
          render={({ field }) => (
            <TextInput
              value={field.value || ""}
              onChange={field.onChange}
              onBlur={field.onBlur}
            />
          )}
        />
      </SearchField>
      <SearchField>
        <label>{t("WS_PROPERTY_ID_LABEL")}</label>
        <Controller
          control={control}
          name="propertyId"
          rules={{
            pattern: {
              value: new RegExp(propsForOldConnectionNumberNpropertyId.pattern),
              message: propsForOldConnectionNumberNpropertyId.title
            }
          }}
          render={({ field }) => (
            <TextInput
              value={field.value || ""}
              onChange={field.onChange}
              onBlur={field.onBlur}
            />
          )}
        />
      </SearchField>
      <SearchField>
        <label>{t("WS_HOME_SEARCH_RESULTS_OWN_MOB_LABEL")}</label>
        <Controller
          control={control}
          name="mobileNumber"
          rules={{
            validate: (value) => {
              if (!value) return true;
              if (!/^[6-9][0-9]{9}$/.test(value)) return t("ES_SEARCH_APPLICATION_MOBILE_INVALID");
              return true;
            }
          }}
          render={({ field }) => (
            <MobileNumber
              value={field.value || ""}
              onChange={field.onChange}
              onBlur={field.onBlur}
              {...propsForMobileNumber}
            />
          )}
        />
      </SearchField>
      <SearchField className="submit">
        <SubmitBar label={t("WS_SEARCH_CONNECTION_SEARCH_BUTTON")} submit />
        <p
          onClick={() => {
            const resetValues = {
              searchType:"CONNECTION",
              mobileNumber: "",
              offset: 0,
              limit: 10,
              sortBy: "commencementDate",
              sortOrder: "DESC",
              propertyId: "",
              connectionNumber: "",
              oldConnectionNumber: "",
            };
            reset(resetValues);
            if (onClearSearch) {
              onClearSearch();
            } else if (onSubmit) {
              onSubmit(resetValues);
            }
          }}
        >
          {t("WS_SEARCH_CONNECTION_RESET_BUTTON")}
        </p>
      </SearchField>
    </>
  );
};
export default SearchFields;