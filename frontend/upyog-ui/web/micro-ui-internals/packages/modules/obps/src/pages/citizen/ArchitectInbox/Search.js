import React from "react";
import { useForm, Controller } from "react-hook-form";
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardHeader } from "@nudmcdgnpm/digit-ui-react-components";

const Search = ({ onSearch, searchParams, searchFields, type, onClose, isInboxPage, t }) => {
  const { register, handleSubmit, formState, reset, watch, control } = useForm({
    defaultValues: searchParams,
  });
  const mobileView = innerWidth <= 640;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const getFields = (input) => {
    switch (input.type) {
      default:
        return (
          <Controller
            render={({ field }) => <TextInput onChange={field.onChange} value={field.value} />}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        )
    }
  }

  const onSubmitInput = (data) => {
    onSearch(data);
    if (type === "mobile") {
      onClose();
    }
  }

  // const clearSearch = () => {
  //   reset({});
  //   onSearch({})
  // };

  function clearSearch() {
    const resetValues = searchFields.reduce((acc, field) => ({ ...acc, [field?.name]: "" }), {});
    reset(resetValues);
    const _newParams = { ...searchParams };
    _newParams.delete = [];
    searchFields.forEach((e) => {
      _newParams.delete.push(e?.name);
    });
    onSubmitInput({ applicationNo: null });

  }

  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {};
    return (
      <LinkLabel className="search-clear-link" style={mobileViewStyles} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <div className={`search-container ${isInboxPage ? "search-container-auto-margin" : "search-container-auto"}`}>
        <div className="search-complaint-container">
          {(type === "mobile" || mobileView) && (
            <div className="complaint-header">
              <h2 className="obps-search-header">{t("BPA_SEARCH_BY_LABEL")}:</h2>
              <span onClick={onClose} className="obps-search-top-spacing">
                <CloseSvg />
              </span>

            </div>
          )}
          <div className={`${"complaint-input-container for-pt " + (!isInboxPage ? "for-search" : "")} obps-search-fullwidth`}>
            {searchFields?.map((input, index) => (
              <div key={input.name} className="input-fields" style={mobileView ? { marginTop: "30px" } : {}}>
                {/* <span className={index === 0 ? "complaint-input" : "mobile-input"}> */}
                <span className={"mobile-input"}>
                  <Label>{t(input.label) + ` ${input.isMendatory ? "*" : ""}`}</Label>
                  {getFields(input)}
                </span>
                {formState?.dirtyFields?.[input.name] ? (
                  <span
                    className="inbox-search-form-error obps-search-top-spacing-2"
                  >
                    {formState?.errors?.[input.name]?.message}
                  </span>
                ) : null}
              </div>
            ))}
            {type === "desktop" && <SubmitBar className="submit-bar-search obps-search-top-spacing-3" label={t("ES_COMMON_SEARCH")} submit />}
            {type === "desktop" && !mobileView && (
              <span className="clear-search obps-search-spacing">
                {clearAll()}
              </span>
            )}
          </div>
        </div>
      </div>
      {(type === "mobile" || mobileView) && (
        <ActionBar className="clear-search-container">
          <button className="clear-search obps-building-plan-scrutiny-wrapper">
            {clearAll(mobileView)}
          </button>
          <SubmitBar disabled={!!Object.keys(formState.errors).length} label={t("ES_COMMON_SEARCH")} className="obps-building-plan-scrutiny-wrapper" submit={true} />
        </ActionBar>
      )}
    </form>
  );
}

export default Search;
