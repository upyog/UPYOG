import React from "react";
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
  CardLabelError
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { cndStyles } from "../../utils/cndStyles";
import { getValidationRules } from "../../utils";
/**
 * The SearchApplication component creates a dynamic search form based on configurable search fields, 
 * with different layouts for mobile and desktop views. It manages form state, validation, and submission of search criteria.
 */

const fieldComponents = {
  date: DatePicker,
  mobileNumber: MobileNumber,
};

const SearchApplication = ({ onSearch, type, onClose, searchFields, searchParams, isInboxPage, defaultSearchParams, clearSearch: _clearSearch }) => {
  const { t } = useTranslation();
  const { handleSubmit, reset, watch, control, formState, setValue } = useForm({
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
    } else {
      _clearSearch();
    }
  }

  const clearAll = (mobileView) => {
    return (
      <LinkLabel style={mobileView ? cndStyles.clearButtonMobile : cndStyles.clearButtonDesktop} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className="search-container" style={isInboxPage ? cndStyles.searchContainerInbox : cndStyles.searchContainer}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className={"complaint-input-container for-pt " + (!isInboxPage ? "for-search" : "")}>
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    <span className={"mobile-input"}>
                      <Label>{t(input.label) + ` ${input.isMandatory ? "*" : ""}`}</Label>
                      {!input.type ? (
                        <Controller
                          render={({ field }) => (
                            <TextInput onChange={field.onChange} value={field.value} maxlength={input.maxLength} maxLength={input.maxLength} />
                          )}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                          rules={getValidationRules(input, t)}
                        />
                      ) : (
                        <Controller
                          render={({ field }) => {
                            const Comp = fieldComponents?.[input.type];
                            return <Comp formValue={form} setValue={setValue} onChange={field.onChange} value={field.value} maxlength={input.maxLength} maxLength={input.maxLength} />;
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                          rules={getValidationRules(input, t)}
                        />
                      )}
                    </span>
                    {formState?.errors?.[input.name] && (
                      <CardLabelError className="cnd-search-field-error">
                        {formState?.errors?.[input.name]?.message}
                      </CardLabelError>
                    )}
                  </div>
                ))}

              {type === "desktop" && !mobileView && (
                <div className="search-submit-wrapper cnd-search-wrapper">
                  <SubmitBar
                    className="submit-bar-search cnd-search-submit-bar"
                    label={t("ES_COMMON_SEARCH")}
                    disabled={!!Object.keys(formState.errors).length || formValueEmpty()}
                    submit
                  />
                  {!isInboxPage && <div>{clearAll()}</div>}
                </div>
              )}
              {isInboxPage && (
                <div className="input-fields cnd-inbox-clear-btn">
                  <div>{clearAll()}</div>
                </div>
              )}
            </div>
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search cnd-submit-bar-flex">
              {clearAll(mobileView)}
            </button>
            <SubmitBar disabled={!!Object.keys(formState.errors).length} label={t("ES_COMMON_SEARCH")} className="cnd-submit-bar-flex" submit={true} />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;
