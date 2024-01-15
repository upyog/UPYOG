import React from "react";
import { useForm, Controller } from "react-hook-form";
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, Dropdown } from "@egovernments/digit-ui-react-components";
// import DropdownUlb from "./DropdownUlb";
import { alphabeticalSortFunctionForTenantsBasedOnName } from "../../../utils";

const Search = ({ onSearch, searchParams, searchFields, type, onClose, isInboxPage, t }) => {
  console.log("searchFields fff ",searchFields)
  const { register, handleSubmit, formState, reset, watch, control } = useForm({
    defaultValues: searchParams,
  });
  const mobileView = innerWidth <= 640;
  const ulb = Digit.SessionStorage.get("ENGAGEMENT_TENANTS");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const userInfo = Digit.UserService.getUser().info;
  // const userUlbs = ulb.filter(ulb => userInfo?.roles?.some(role => role?.tenantId === ulb?.code)).sort(alphabeticalSortFunctionForTenantsBasedOnName)
  
  const getFields = (input) => {
    switch(input.type) {
      case "ulb":
        return (
          <Controller
          rules={{ required: true }}
            render={props => (
              // <DropdownUlb
              <Dropdown
                onAssignmentChange={props.onChange}
                value={props.value}
                // ulb={userUlbs}
                t={t}
              />
            )}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        )
        break
        case "date":
          return (<Controller
              name={input.name}
              control={control}
              defaultValue={null}
              // rules={{ required: true }}
              render={(props) => <DatePicker onChange={props.onChange} date={props.value} />}
              // render={({ field, value, onChange }) => <DatePicker date={value} onChange={(data) => setFormData(data, "tender_date", index)} />}

                  />
                  )
                  break
      default:
        return (
          <Controller
            render={(props) => <TextInput onChange={props.onChange} value={props.value} />}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        )
    }
  }

  const onSubmitInput = (data) => {
    // data.preventDefault();
    alert("ddddddddddddddd")
    console.log("search data/ ",data)
    data.delete = [];
    searchFields.forEach((field) => {
    console.log("search data/ field ",field)

    // console.log("search data name ",data[field.name])
    // console.log("search data name ",!data[field.name])
      if (!data[field.name]) data.delete.push(field.name);
    });
    console.log("search data/ data modify",data)

    onSearch(data);
    if (type === "mobile"){
      onClose();
    }
  }
  const clearSearch = () => {
    onSearch("reset");
    
    // reset({ pfms_vendor_code:'',vendor_type:'' });
    // onSearch({ pfms_vendor_code:'d',vendor_type:'d' })
    // reset({ ulb: null, eventName: '' });
    // onSearch({ ulb: null, eventName: '' })
  };

  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {};
    return (
      <LinkLabel style={{ display: "inline", ...mobileViewStyles }} onClick={clearSearch}>
        {/* {t("ES_COMMON_CLEAR_SEARCH")} */}
        {"Reset"}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <div className="search-container" style={{ width: "auto", marginLeft: isInboxPage ? "24px" : "revert" }}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header">
                {/* <h2>{t("ES_COMMON_SEARCH_BY")}</h2> */}
                <h2>{"ES_COMMON_SEARCH_BY"}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className={"complaint-input-container for-pt " + (!isInboxPage ? "for-search" : "")} style={{ width: "100%" }}>
              {searchFields
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    {/* <span className={index === 0 ? "complaint-input" : "mobile-input"}> */}
                    <span className={"mobile-input"}>
                      <Label>{input.label + ` ${input.isMendatory ? "*" : ""}`}</Label>
                      {getFields(input)}
                    </span>
                    {formState?.dirtyFields?.[input.name] ? (
                      <span
                        style={{ fontWeight: "700", color: "rgba(212, 53, 28)", paddingLeft: "8px", marginTop: "-20px", fontSize: "12px" }}
                        className="inbox-search-form-error"
                      >
                        {formState?.errors?.[input.name]?.message}
                      </span>
                    ) : null}
                  </div>
                ))}

     
              {/* {type === "desktop" && !mobileView && (
                <div style={{ maxWidth: "unset", marginLeft: "unset", marginTop: "55px"}} className="search-submit-wrapper">
                  <SubmitBar
                  style={{ marginTop: "unset" }}
                    className="submit-bar-search"
                    // label={"ES_COMMON_SEARCH"}
                    label={"Search"}
                    // disabled={!!Object.keys(formState.errors).length || formValueEmpty()}
                    submit
                  />
                  <div>{clearAll()}</div>
                </div>
              )} */}
            </div>
            <div className="inbox-action-container">
              {type === "desktop" && !mobileView && (
                <span style={{ paddingTop: "9px" }} className="clear-search">
                  {clearAll()}
                </span>
              )}
              {type === "desktop" && !mobileView && (
                <SubmitBar
                  style={{ marginTop: "unset" }}
                  className="submit-bar-search"
                  label={t("WMS_COMMON_SEARCH")}
                  submit
                />
              )}
            </div>
            
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search" style={{ flex: 1 }}>
              {clearAll(mobileView)}
            </button>
            {/* <SubmitBar disabled={!!Object.keys(formState.errors).length} label={"ES_COMMON_SEARCH"} style={{ flex: 1 }} submit={true} /> */}
            <SubmitBar disabled={!!Object.keys(formState.errors).length} label={"Search"} style={{ flex: 1 }} submit={true} />
          </ActionBar>
        )}
    </form>
  )

}

export default Search;