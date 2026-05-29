import React, { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, MobileNumber, Dropdown } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import "../../css/wt-inline-auto.css";
const fieldComponents = {
  mobileNumber: MobileNumber
};

/*
    A dynamic search form for applications, allowing users to filter by various fields
    such as mobile number, and hall codes,booking no. 
    The form adapts to both mobile and desktop views, with input validation and error handling.
    It also includes functionality for clearing search filters.
  */

const SearchApplication = ({
  onSearch,
  type,
  onClose,
  searchFields,
  searchParams,
  isInboxPage,
  defaultSearchParams,
  clearSearch: _clearSearch
}) => {
  const {
    t
  } = useTranslation();
  const {
    handleSubmit,
    reset,
    watch,
    control,
    setError,
    clearErrors,
    formState,
    setValue
  } = useForm({
    defaultValues: isInboxPage ? searchParams : {
      locality: null,
      city: null,
      ...searchParams
    }
  });
  const form = watch();
  const formValueEmpty = () => {
    let isEmpty = true;
    Object.keys(form).forEach(key => {
      if (!["locality", "city"].includes(key) && form[key]) isEmpty = false;
    });
    if (searchFields?.find(e => e.name === "locality") && !form?.locality?.code) isEmpty = true;
    return isEmpty;
  };
  const mobileView = innerWidth <= 640;
  const onSubmitInput = data => {
    if (!data.mobileNumber) {
      delete data.mobileNumber;
    }
    data.delete = [];
    searchFields.forEach(field => {
      if (!data[field.name]) data.delete.push(field.name);
    });
    onSearch(data);
    if (type === "mobile") {
      onClose();
    }
  };
  function clearSearch() {
    const resetValues = searchFields.reduce((acc, field) => ({
      ...acc,
      [field?.name]: ""
    }), {});
    reset(resetValues);
    if (isInboxPage) {
      const _newParams = {
        ...searchParams
      };
      _newParams.delete = [];
      searchFields.forEach(e => {
        _newParams.delete.push(e?.name);
      });
      onSearch({
        ..._newParams
      });
    } else {
      _clearSearch();
    }
  }
  const clearAll = mobileView => {
    const mobileViewStyles = mobileView ? {
      margin: 0
    } : {};
    return <LinkLabel style={{
      display: "inline",
      ...mobileViewStyles
    }} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>;
  };
  return <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className="search-container" style={{
        width: "auto",
        marginLeft: isInboxPage ? "24px" : "revert"
      }}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && <div className="complaint-header">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>}
            <div className={"complaint-input-container for-pt " + (!isInboxPage ? "for-search" : "")}>
              {searchFields?.filter(e => true)?.map((input, index) => <div key={input.name} className="input-fields">
                    {/* <span className={index === 0 ? "complaint-input" : "mobile-input"}> */}
                    <span className={"mobile-input"}>
                      <Label>{t(input.label) + ` ${input.isMendatory ? "*" : ""}`}</Label>
                      {!input.type ? <Controller render={props => {
                  return <TextInput onChange={props.onChange} value={props.value} />;
                }} name={input.name} control={control} defaultValue={""} /> : <Controller render={props => {
                  const Comp = fieldComponents?.[input.type];
                  return <Comp formValue={form} setValue={setValue} onChange={props.onChange} value={props.value} />;
                }} name={input.name} control={control} defaultValue={""} />}
                    </span>
                    {formState?.dirtyFields?.[input.name] ? <span className="inbox-search-form-error wt-auto-22">
                        {formState?.errors?.[input.name]?.message}
                      </span> : null}
                  </div>)}

              {isInboxPage && <div className="input-fields wt-auto-23">
                  <div>{clearAll()}</div>
                </div>}

              {type === "desktop" && !mobileView && <div className="search-submit-wrapper wt-auto-24">
                  <SubmitBar className="submit-bar-search" label={t("ES_COMMON_SEARCH")} disabled={!!Object.keys(formState.errors).length || formValueEmpty()} submit />
                  {/* style={{ paddingTop: "16px", textAlign: "center" }} className="clear-search" */}
                  {!isInboxPage && <div>{clearAll()}</div>}
                </div>}
            </div>
          </div>
        </div>
        {(type === "mobile" || mobileView) && <ActionBar className="clear-search-container">
            <button className="clear-search wt-auto-25">
              {clearAll(mobileView)}
            </button>
            <SubmitBar disabled={!!Object.keys(formState.errors).length} label={t("ES_COMMON_SEARCH")} submit={true} className="wt-auto-26" />
          </ActionBar>}
      </React.Fragment>
    </form>;
};
export default SearchApplication;
