import {
  ActionBar,
  CloseSvg,
  DatePicker,
  Label,
  LinkLabel,
  MobileNumber,
  SubmitBar,
  TextInput,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState, useRef } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

const fieldComponents = {
  date: DatePicker,
  mobileNumber: MobileNumber,
};

const SearchApplication = ({ onSearch, type, onClose, searchFields, searchParams, isInboxPage, defaultSearchParams, clearSearch: _clearSearch }) => {
  const { t } = useTranslation();
  const { register, handleSubmit, reset, watch, control, setError, clearErrors, formState } = useForm({
    defaultValues: searchParams,
  });
  const [showToast, setShowToast] = useState(null);

  const form = watch();
  const mobileView = innerWidth <= 640;

  const closeToast = () => {
    setShowToast(null);
  };
  setTimeout(() => {
    closeToast();
  }, 10000);

  useEffect(() => {
    searchFields.forEach(({ pattern, name, maxLength, minLength, errorMessages, ...el }) => {
      const value = form[name];
      const error = formState.errors[name];
      if (pattern) {
        if (!new RegExp(pattern).test(value) && !error)
          setError(name, { type: "pattern", message: t(errorMessages?.pattern) || t(`PATTERN_${name.toUpperCase()}_FAILED`) });
        else if (new RegExp(pattern).test(value) && error?.type === "pattern") clearErrors([name]);
      }
      if (minLength) {
        if (value?.length < minLength && !error)
          setError(name, { type: "minLength", message: t(errorMessages?.minLength || `MINLENGTH_${name.toUpperCase()}_FAILED`) });
        else if (value?.length >= minLength && error?.type === "minLength") clearErrors([name]);
      }
      if (maxLength) {
        if (value?.length > maxLength && !error)
          setError(name, { type: "maxLength", message: t(errorMessages?.maxLength || `MAXLENGTH_${name.toUpperCase()}_FAILED`) });
        else if (value?.length <= maxLength && error?.type === "maxLength") clearErrors([name]);
      }
    });
  }, [form, formState, setError, clearErrors]);

  const onSubmitInput = (data) => {
    if (!searchParams.businesService) {
      setShowToast({ key: true, label: "ABG_SEARCH_SELECT_AT_LEAST_SERVICE_TOAST_MESSAGE" });
      return;
    }

    if (true) {
      if (!data.mobileNumber) {
        delete data.mobileNumber;
      }
      data.delete = [];
      searchFields.forEach((field) => {
        if (!data[field.name]) data.delete.push(field.name);
      });
      onSearch(data);
      if (type === "mobile") {
        onClose();
      }
    }
  };

  function clearSearch() {
    const resetValues = searchFields.reduce((acc, field) => ({ ...acc, [field?.name]: "" }), {});
    reset(resetValues);
    const _newParams = { ...searchParams };
    _newParams.delete = [];
    searchFields.forEach((e) => {
      _newParams.delete.push(e?.name);
    });
    onSearch({ ..._newParams }, true);
    if (type === "mobile") {
      onClose();
    }
  }

  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {};
    return (
      <LinkLabel className="search-clear-link" style={mobileViewStyles} onClick={clearSearch}>
        {t("ABG_RESET_BUTTON")}
      </LinkLabel>
    );
  };

  const formValueEmpty = () => {
    let isEmpty = true;
    Object.keys(form).forEach((key) => {
      if (!["locality", "city"].includes(key) && form[key]) isEmpty = false;
    });

    if (searchFields?.find((e) => e.name === "locality") && !form?.locality?.code) isEmpty = true;
    return isEmpty;
  };

  return (
    <React.Fragment>
      <form onSubmit={handleSubmit(onSubmitInput)}>
        <div className={`search-container ${isInboxPage ? "search-container-auto-margin" : "search-container-auto"}`}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header bills-search-citizen-flex-row">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className="complaint-input-container group-complaint-input-container bills-search-citizen-fullwidth">
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    <span className={"mobile-input"}>
                      <Label>{t(input.label)}</Label>
                      {!input.type ? (
                        <Controller
                          render={(props) => {
                            return (
                              <div className="field-container">
                                {input?.componentInFront ? (
                                  <span className="employee-card-input employee-card-input--front bills-search-citizen-wrapper">
                                    {input?.componentInFront}
                                  </span>
                                ) : null}
                                <TextInput {...input} inputRef={register} watch={watch} shouldUpdate={true} />
                              </div>
                            );
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      ) : (
                        <Controller
                          render={(props) => {
                            const Comp = fieldComponents?.[input.type];
                            return <Comp onChange={props.onChange} value={props.value} />;
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      )}
                    </span>
                    {formState?.dirtyFields?.[input.name] ? (
                      <span
                        className="inbox-search-form-error bills-search-citizen-top-spacing"
                      >
                        {formState?.errors?.[input.name]?.message}
                      </span>
                    ) : null}
                  </div>
                ))}
              {type === "desktop" && !mobileView && !isInboxPage && (
                <div className="search-action-wrapper">
                  <SubmitBar
                    className="submit-bar-search"
                    label={t("ABG_SEARCH_BUTTON")}
                    // disabled={!!Object.keys(formState.errors).length || Object.keys(form).every((key) => !form?.[key])}
                    submit
                  />
                  <div className="bills-search-citizen-top-spacing-2">
                    {clearAll()}
                  </div>
                </div>
              )}
            </div>
            {isInboxPage && (
              <div className="inbox-action-container">
                {type === "desktop" && !mobileView && (
                  <span className="clear-search bills-search-citizen-spacing">
                    {clearAll()}
                  </span>
                )}
                {type === "desktop" && !mobileView && (
                  <SubmitBar
                    // disabled={!!Object.keys(formState.errors).length || Object.keys(form).every((key) => !form?.[key])}
                    className="submit-bar-search bills-search-citizen-top-spacing-3"
                    label={t("ABG_SEARCH_BUTTON")}
                    submit
                  />
                )}
              </div>
            )}
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search bills-search-citizen-action-btn">
              {clearAll(mobileView)}
            </button>
            <SubmitBar label={t("ABG_SEARCH_BUTTON")} className="bills-search-citizen-action-btn" submit={true} />
          </ActionBar>
        )}
      </form>
      {showToast && <Toast error={showToast.key} label={t(showToast.label)} onClose={closeToast} />}
    </React.Fragment>
  );
};

export default SearchApplication;
