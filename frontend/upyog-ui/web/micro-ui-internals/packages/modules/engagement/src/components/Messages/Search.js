import React, {useCallback} from "react";
import { useForm, Controller } from "react-hook-form";
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, DateRange } from "@nudmcdgnpm/digit-ui-react-components";
import DropdownUlb from "./DropdownUlb";
import { alphabeticalSortFunctionForTenantsBasedOnName } from "../../utils";

const Search = ({ onSearch, searchParams, searchFields, type, onClose, isInboxPage, t }) => {
  const { register, handleSubmit, formState, reset, watch, control } = useForm({
    defaultValues: searchParams,
  });
  const mobileView = innerWidth <= 640;
  const ulbs = Digit.SessionStorage.get("ENGAGEMENT_TENANTS");
  const userInfo = Digit.UserService.getUser().info;
  const userUlbs = ulbs.filter(ulb => userInfo?.roles?.some(role => role?.tenantId === ulb?.code)).sort(alphabeticalSortFunctionForTenantsBasedOnName);

  const getFields = (input) => {
    switch (input.type) {
      case "ulb":
        return (
          <Controller
            rules={{ required: true }}
            render={({field}) => (
              <DropdownUlb
                onAssignmentChange={field.onChange}
                value={field.value}
                ulb={userUlbs}
                t={t}
              />
            )}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        )

      case "range":
        return (
          <Controller
            render={({field}) =>{             
              return <DateRange t={t} values={field.value} onFilterChange={(value)=> field.onChange(value.range)} labelClass="filter-label" />
            }}
            name={input.name}
            control={control}
            defaultValue={null}
          />

        )

      default:
        return (
          <Controller
            render={({field}) => <TextInput onChange={field.onChange} value={field.value} />}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        )
    }
  }

  const onSubmitInput = (data) => {
    // searchFields.forEach((field) => {
    //   if (!data[field.name]) data.delete.push(field.name);
    // });

    onSearch(data);
    if (type === "mobile") {
      onClose();
    }
  }

  const clearSearch = () => {
    reset({ ulb: null, name: '', });
    onSearch({ ulb: null, name: '' })
  };

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
      <div className="search-container" className={isInboxPage ? "search-container-auto-margin" : "search-container-auto"}>
        <div className="search-complaint-container">
          {(type === "mobile" || mobileView) && (
            <div className="complaint-header">
              <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
              <span onClick={onClose}>
                <CloseSvg />
              </span>
            </div>
          )}
          <div className={`${"complaint-input-container for-pt " + (!isInboxPage ? "for-search" : "")} eng-search-fullwidth`}>
            {searchFields
              ?.map((input, index) => (
                <div key={input.name} className="input-fields">
                  {/* <span className={index === 0 ? "complaint-input" : "mobile-input"}> */}
                  <span className={"mobile-input"}>
                    <Label>{t(input.label) + ` ${input.isMendatory ? "*" : ""}`}</Label>
                    {getFields(input)}
                  </span>
                  {formState?.dirtyFields?.[input.name] ? (
                    <span
                      className="inbox-search-form-error eng-search-top-spacing"
                    >
                      {formState?.errors?.[input.name]?.message}
                    </span>
                  ) : null}
                </div>
              ))}

            {/* {isInboxPage && ( */}
            {/* // <div className="custom-style" className="input-fields"> */}
            {/* <div>{clearAll()}</div> */}
            {/* // </div> */}
            {/* )} */}

            {type === "desktop" && !mobileView && (
                <div className="search-submit-wrapper eng-search-top-spacing-2">
                  <SubmitBar
                    className="submit-bar-search"
                    label={t("ES_COMMON_SEARCH")}
                    // disabled={!!Object.keys(formState.errors).length || formValueEmpty()}
                    submit
                  />
                  
                  <div>{clearAll()}</div>
                </div>
              )}
          </div>
        </div>
      </div>
      {(type === "mobile" || mobileView) && (
        <ActionBar className="clear-search-container">
          <button className="clear-search eng-desktop-inbox-wrapper">
            {clearAll(mobileView)}
          </button>
          <SubmitBar disabled={!!Object.keys(formState.errors).length} label={t("ES_COMMON_SEARCH")} className="eng-desktop-inbox-wrapper" submit={true} />
        </ActionBar>
      )}
    </form>
  )

}

export default Search;