import React, { useState,useEffect } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  TextInput,
  Label,
  SubmitBar,
  LinkLabel,
  ActionBar,
  CloseSvg,
  DatePicker,
  CardLabelError,
  Header,
} from "@nudmcdgnpm/digit-ui-react-components";
import DropdownStatus from "./DropdownStatus";
import { useTranslation } from "react-i18next";

/*

  WHAT WAS THE ISSUE? AFTER LTS AND HOW WE RESOLVED IT?


  searchValidation() was causing state updates (setError)
  during validation execution. React Hook Form expects
  validation functions to be pure and synchronous.
  The repeated state updates interrupted form submission,
  so handleSubmit(onSubmitInput) was never completing.
*/

const SearchApplication = ({ onSearch, type, onClose, isFstpOperator, searchFields, searchParams, isInboxPage }) => {
  const storedSearchParams = isInboxPage ? Digit.SessionStorage.get("fsm/inbox/searchParams") : Digit.SessionStorage.get("fsm/search/searchParams");

  const { data: applicationStatuses, isFetched: areApplicationStatus } = Digit.Hooks.fsm.useApplicationStatus();

  const { t } = useTranslation();
  const { register, handleSubmit, reset, watch, control } = useForm({
    defaultValues: storedSearchParams || searchParams,
  });
  const [error, setError] = useState(false);
  const mobileView = innerWidth <= 640;
  const FSTP = Digit.UserService.hasAccess("FSM_EMP_FSTPO") || false;

  const [applicationNos, mobileNumber, fromDate, toDate] = watch([
    "applicationNos",
    "mobileNumber",
    "fromDate",
    "toDate",
  ]);
  const [isReady, setIsReady] = useState(false);

  const onSubmitInput = (data) => {
    if (!data.mobileNumber) {
      delete data.mobileNumber;
    }
    onSearch(data);
    if (type === "mobile") {
      onClose();
    }
  };

  function clearSearch() {
    const resetValues = searchFields.reduce((acc, field) => ({ ...acc, [field?.name]: "" }), {});
    reset(resetValues);
    if (isInboxPage) {
      Digit.SessionStorage.del("fsm/inbox/searchParams");
    } else {
      Digit.SessionStorage.del("fsm/search/searchParams");
    }
    onSearch({});
  }

  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {};
    return (
      <LinkLabel style={{ display: "inline", ...mobileViewStyles }} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

    const searchValidation = () => {
      if (FSTP) return true;

      return !!( applicationNos || mobileNumber || (fromDate && toDate));
    };

  const getFields = (input) => {
    switch (input.type) {
      case "date":
        return (
          <Controller
            render={({ field }) => <DatePicker date={field.value} onChange={field.onChange} />}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        );
      case "status":
        return (
          <Controller
            render={({ field }) => (
              <DropdownStatus
                onAssignmentChange={field.onChange}
                value={field.value}
                applicationStatuses={applicationStatuses}
                areApplicationStatus={areApplicationStatus}
              />
            )}
            name={input.name}
            control={control}
            defaultValue={null}
          />
        );
      default:
        return (
          <Controller
            name={input.name}
            control={control}
            defaultValue=""
            rules={{
              validate: searchValidation,
            }}
            render={({ field }) => (
              <TextInput
                {...input}
                value={field.value || ""}
                onChange={field.onChange}
                onBlur={field.onBlur}
                ref={field.ref}
                name={field.name}
              />
            )}
          />
        );
    }
  };
  const checkInboxLocation =
    window.location.href.includes("employee/fsm/inbox") ||
    window.location.href.includes("employee/fsm/fstp-inbox") ||
    window.location.href.includes("employee/fsm/fstp-fsm-request");
  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        {!checkInboxLocation ? <Header styles={mobileView ? { marginTop: "10px" } : {}}>{t("ACTION_TEST_SEARCH_FSM_APPLICATION")}</Header> : ""}
        <div className="search-container" style={{ width: "auto", marginLeft: FSTP ? "" : isInboxPage ? "24px" : "revert" }}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span
                  style={{
                    position: "absolute",
                    top: "2%",
                    right: "8px",
                  }}
                  onClick={onClose}
                >
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className={FSTP ? "complaint-input-container for-pt for-search" : "complaint-input-container"} style={{ width: "100%" }}>
              {searchFields?.map((input, index) => (
                <span key={index} className={index === 0 ? "complaint-input" : "mobile-input"}>
                  <Label>
                    {input.label} {input.labelChildren && input.labelChildren}
                  </Label>
                  {getFields(input)}{" "}
                </span>
              ))}
              {type === "desktop" && !mobileView && <SubmitBar className="submit-bar-search" label={t("ES_COMMON_SEARCH")} submit />}
            </div>
            {error ? <CardLabelError className="search-error-label">{t("ES_SEARCH_APPLICATION_ERROR")}</CardLabelError> : null}
            {type === "desktop" && !mobileView && <span className="clear-search">{clearAll()}</span>}
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search" style={{ flex: 1 }}>
              {clearAll(mobileView)}
            </button>
            <SubmitBar label={t("ES_COMMON_SEARCH")} style={{ flex: 1 }} submit={true} />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;
