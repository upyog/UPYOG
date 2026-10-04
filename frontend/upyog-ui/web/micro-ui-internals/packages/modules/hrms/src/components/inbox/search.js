import { ActionBar, CloseSvg, DatePicker, Label, LinkLabel, SubmitBar, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

const SearchApplication = ({ onSearch, type, onClose, searchFields, searchParams, isInboxPage, defaultSearchParams }) => {
  const { t } = useTranslation();
  const { register, handleSubmit, reset, watch, control } = useForm({
    defaultValues: searchParams,
  });
  const mobileView = innerWidth <= 640;
  const onSubmitInput = (data) => {
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
  };

  function clearSearch() {
    const resetValues = searchFields.reduce((acc, field) => ({ ...acc, [field?.name]: "" }), {});
    reset(resetValues);
    const _newParams = { ...searchParams };
    _newParams.delete = [];
    searchFields.forEach((e) => {
      _newParams.delete.push(e?.name);
    });
    onSearch({ ..._newParams });
  }

  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {};
    return (
      <LinkLabel className="search-clear-link" style={mobileViewStyles} onClick={clearSearch}>
        {t("HR_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className="search-container" className={isInboxPage ? "search-container-auto-margin" : "search-container-auto"}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header hrms-search-flex-row">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className="complaint-input-container hrms-search-fullwidth">
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    <span className={"mobile-input"}>
                      <Label>{input.label}</Label>
                      {input.type !== "date" ? (
                        <div className="field-container">
                          {input?.componentInFront ? (
                            <span className="citizen-card-input citizen-card-input--front hrms-search-wrapper">
                              {input?.componentInFront}
                            </span>
                          ) : null}
                          <TextInput {...input} inputRef={register(input.name).ref} {...register(input.name)} watch={watch} shouldUpdate={true} />
                        </div>
                      ) : (
                        <Controller
                          render={({ field }) => <DatePicker date={field.value} onChange={field.onChange} />}
                          name={input.name}
                          control={control}
                          defaultValue={null}
                        />
                      )}{" "}
                    </span>
                  </div>
                ))}
            </div>
            <div className="inbox-action-container">
              {type === "desktop" && !mobileView && (
                <span className="clear-search hrms-search-spacing">
                  {clearAll()}
                </span>
              )}
              {type === "desktop" && !mobileView && (
                <SubmitBar
                  className="submit-bar-search hrms-search-top-spacing"
                  label={t("ES_COMMON_SEARCH")}
                  submit
                />
              )}
            </div>
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search hrms-inbox-filter-wrapper">
              {clearAll(mobileView)}
            </button>
            <SubmitBar label={t("HR_COMMON_SEARCH")} className="hrms-inbox-filter-wrapper" submit={true} />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;
