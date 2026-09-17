import React, { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  TextInput,
  Label,
  SubmitBar,
  LinkLabel,
  ActionBar,
  CloseSvg,
  DatePicker,
  MobileNumber,
  Dropdown,
  Localities,
} from "@nudmcdgnpm/digit-ui-react-components";
import "../../css/noc-inline.css";
import { useTranslation } from "react-i18next";

const fieldComponents = {
  date: DatePicker,
  mobileNumber: MobileNumber,
  dropdown: Dropdown

};

/**
 * Component Developed for searching the applications as per applicationNumber and mobileNumber
 * inside inbox
 */

const SearchApplication = ({ onSearch, type, onClose, searchFields, searchParams, isInboxPage, defaultSearchParams, clearSearch: _clearSearch }) => {
  const { t } = useTranslation();
  const { handleSubmit, reset, watch, control, setError, clearErrors, formState, setValue } = useForm({
    defaultValues: isInboxPage ? searchParams : { locality: null, city: null, ...searchParams },
  });

  const form = watch();

  const formValueEmpty = () => {
    let isEmpty = true;
    Object.keys(form).forEach((key) => {
      if (!["locality", "city"].includes(key) && form[key]) isEmpty = false;
    });

    if (searchFields?.find((e) => e.name === "locality") && !form?.locality?.code) isEmpty = true;
    return isEmpty;
  };

  const stateId = Digit.ULBService.getStateId();

  const mobileView = innerWidth <= 640;

  const onSubmitInput = (data) => {
    if (!data.mobileNumber) {
      delete data.mobileNumber;
    }

    // Convert any dropdown object values to their code string
    Object.keys(data).forEach((key) => {
      if (data[key] && typeof data[key] === "object" && data[key].code) {
        data[key] = data[key].code;
      }
    });

    data.delete = [];

    searchFields.forEach((field) => {
      if (!data[field.name]) data.delete.push(field.name);
    });

    onSearch(data);
    if (type === "mobile") {
      onClose();
    }
  };

  function clearSearch() {
    const resetValues = searchFields.reduce((acc, field) => ({ ...acc, [field?.name]: "" }), {});
    reset(resetValues);
    if (isInboxPage) {
      const _newParams = { ...searchParams };
      _newParams.delete = [];
      searchFields.forEach((e) => {
        _newParams.delete.push(e?.name);
      });
      onSearch({ ..._newParams });
    } else {
      _clearSearch();
    }
  }

  const clearAll = (mobileView) => {
    return (
      <LinkLabel className={"clear-all-link" + (mobileView ? " clear-all-link--mobile" : "")} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className="search-container" style={{ width: "auto", marginLeft: isInboxPage ? "24px" : "revert" }}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className={"complaint-input-container for-pt " + (!isInboxPage ? "for-search" : "")} style={{ width: "100%" }}>
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    <span className={"mobile-input"}>
                      <Label>{t(input.label) + ` ${input.isMendatory ? "*" : ""}`}</Label>
                      {!input.type ? (
                        <Controller
                          render={({ field }) => {
                            return <TextInput onChange={field.onChange} value={field.value} />;
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      ) : (
                        <Controller
                          render={({ field }) => {
                            const Comp = fieldComponents?.[input.type];
                            return <Comp formValue={form} setValue={setValue} onChange={field.onChange} value={field.value} />;
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      )}
                    </span>
                    {formState?.dirtyFields?.[input.name] ? (
                      <span className="search-field-error inbox-search-form-error">
                        {formState?.errors?.[input.name]?.message}
                      </span>
                    ) : null}
                  </div>
                ))}

             {type === "desktop" && !mobileView && (
              <div className="search-submit-wrappers">
                <SubmitBar
                  label={t("ES_COMMON_SEARCH")}
                  disabled={!!Object.keys(formState.errors).length}
                  submit
                />

                <div className="search-clear-all-wrapper">
                  {clearAll()}
                </div>
              </div>
            )}
            </div>
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search action-bar-flex-item">
              {clearAll(mobileView)}
            </button>
            <SubmitBar
              disabled={!!Object.keys(formState.errors).length}
              label={t("ES_COMMON_SEARCH")}
              className="action-bar-flex-item"
              submit={true}
            />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;