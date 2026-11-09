import React from "react";
import { useForm, Controller } from "react-hook-form";
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, MobileNumber } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

/**
 * SearchApplication component:
 * - Dynamic search form using react-hook-form
 * - Supports multiple input types (text, date, mobile)
 * - Handles search, clear, and mobile/desktop layouts
 */

const fieldComponents = {
  date: DatePicker,
  mobileNumber: MobileNumber,
};

const SearchApplication = ({ onSearch, type, onClose, searchFields, searchParams, isInboxPage, defaultSearchParams, clearSearch: _clearSearch }) => {
  const { t } = useTranslation();
  const { handleSubmit, reset, watch, control, formState, setValue } = useForm({
    defaultValues: searchParams,
  });

  const form = watch();
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
    if (isInboxPage) {
      const _newParams = { ...searchParams };
      _newParams.delete = [];
      searchFields.forEach((e) => {
        _newParams.delete.push(e?.name);
      });
      onSearch({ ..._newParams });
    } else if (_clearSearch) {
      _clearSearch();
    }
  }

  const clearAll = (mobileView) => {
    return (
      <LinkLabel className={mobileView ? "cg-mobile-margin-0" : ""} onClick={clearSearch}>
        {t("CS_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className={`challan-search-card ${isInboxPage ? "cg-result-margin-left" : ""}`}>
          <div className="search-complaint-container challan-search-container">
            {type === "desktop" && !mobileView && (
              <div className="challan-search-header">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <circle cx="11" cy="11" r="8"></circle>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
                <span>{t("ES_COMMON_SEARCH_BY")}</span>
              </div>
            )}
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className={`complaint-input-container challan-search-row ${!isInboxPage ? "for-search" : ""}`}>
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields challan-search-field">
                    <span className="mobile-input challan-search-field-inner">
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
                  </div>
                ))}

              {!mobileView && (
                <div className="search-action-wrapper challan-search-actions-inline">
                  <SubmitBar className="submit-bar-search challan-search-submit" label={t("ES_COMMON_SEARCH")} submit />
                  <span className="clear-search challan-search-clear">{clearAll()}</span>
                </div>
              )}
            </div>
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search cg-flex-1">{clearAll(mobileView)}</button>
            <SubmitBar label={t("ES_COMMON_SEARCH")} className="cg-flex-1" submit={true} />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;
