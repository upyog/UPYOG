import { ActionBar, CloseSvg, DatePicker, Label, LinkLabel, MobileNumber, SubmitBar, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
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

  const form = watch();
  const mobileView = innerWidth <= 640;


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
        {t("CR_RESET_BUTTON")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className={`search-container ${isInboxPage ? "search-container-auto-margin" : "search-container-auto"}`}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header rcpt-search-flex-row">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className="complaint-input-container rcpt-search-fullwidth">
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    <span className={"mobile-input"}>
                      <Label>{t(input.label)}</Label>
                      {!input.type ? (
                        <Controller
                          render={(props) => {
                            return <div className="field-container">
                              {input?.componentInFront ? (
                                <span className="employee-card-input employee-card-input--front rcpt-search-wrapper">
                                  {input?.componentInFront}
                                </span>
                              ) : null}
                              <TextInput {...input} inputRef={register} watch={watch} shouldUpdate={true} />
                            </div>
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
                        className="inbox-search-form-error rcpt-search-top-spacing"
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
                    label={t("CR_SEARCH_BUTTON")}
                    // disabled={!!Object.keys(formState.errors).length || Object.keys(form).every((key) => !form?.[key])}
                    submit
                  />
                  <div className="rcpt-search-top-spacing-2">
                    {clearAll()}
                  </div>
                </div>
              )}
            </div>
            {isInboxPage && (
              <div className="inbox-action-container">
                {type === "desktop" && !mobileView && (
                  <span className="clear-search rcpt-search-spacing">
                    {clearAll()}
                  </span>
                )}
                {type === "desktop" && !mobileView && (
                  <SubmitBar
                    className="submit-bar-search rcpt-search-top-spacing-3"
                    label={t("CR_SEARCH_BUTTON")}
                    submit
                  />
                )}
              </div>
            )}
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search rcpt-receipts-filter-wrapper">
              {clearAll(mobileView)}
            </button>
            <SubmitBar
              label={t("CR_SEARCH_BUTTON")} className="rcpt-receipts-filter-wrapper" submit={true} />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;