import React, { Fragment } from "react"
import { Controller, useWatch } from "react-hook-form";
import { TextInput, SubmitBar, DatePicker, SearchField, Dropdown, Loader } from "@nudmcdgnpm/digit-ui-react-components";

const SearchFields = ({ register, control, reset, tenantId, t, previousPage }) => {
  let validation = {};

  return <>
    <SearchField>
      <label>{t("TL_TRADE_LICENSE_LABEL")}</label>
      <TextInput name="licenseNumbers" inputRef={register("licenseNumbers").ref} {...register("licenseNumbers")} />
    </SearchField>
    <SearchField>
      <label>{t("TL_TRADE_OWNER_S_NUMBER_LABEL")}</label>
      <TextInput name="mobileNumber" inputRef={register("mobileNumber").ref} {...register("mobileNumber")}
        type="mobileNumber"
        componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
        maxlength={10}
        {...(validation = {
          pattern: "[6-9]{1}[0-9]{9}",
          type: "tel",
          title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
        })} />
    </SearchField>
    <SearchField>
      <label>{t("TL_SEARCH_TRADE_LICENSE_ISSUED_FROM")}</label>
      <Controller
        name="fromDate"
        control={control}
        defaultValue={null}
        render={({ field }) => (
          <DatePicker
            date={field.value}
            onChange={(val) => field.onChange(val)}
          />
        )}
      />
    </SearchField>

    <SearchField>
      <label>{t("TL_SEARCH_TRADE_LICENSE_ISSUED_TO")}</label>
      <Controller
        name="toDate"
        control={control}
        defaultValue={null}
        render={({ field }) => (
          <DatePicker
            date={field.value}
            onChange={(val) => field.onChange(val)}
          />
        )}
      />
    </SearchField>

    <SearchField>
      <label>{t("TL_LOCALIZATION_TRADE_NAME")}</label>
      <TextInput name="tradeName" inputRef={register("tradeName").ref} {...register("tradeName")} />
    </SearchField>
    <SearchField className="submit">
      <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
      <p onClick={() => {
        reset({
          licenseNumbers: "",
          mobileNumber: "",
          fromDate: "",
          toDate: "",
          offset: 0,
          limit: 10,
          sortBy: "commencementDate",
          sortOrder: "DESC",
          status: "",

        });
        previousPage();
      }
      }>{t(`ES_COMMON_CLEAR_ALL`)}</p>
    </SearchField>
  </>
}
export default SearchFields