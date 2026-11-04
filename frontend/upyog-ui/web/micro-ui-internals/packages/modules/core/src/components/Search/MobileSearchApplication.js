import React, { Fragment, useCallback, useMemo, useReducer, useState, useEffect } from "react";
import {
  CloseSvg,
  SearchForm,
  Table,
  Card,
  SearchAction,
  FilterAction,
  PopUp,
  DetailsCard,
  Loader,
  Toast,
  BackButton,
  Header,
  DownloadBtnCommon
} from "@nudmcdgnpm/digit-ui-react-components";
import SearchFormFields from "./SearchFields";
// import { convertEpochToDateDMY } from "../../utils";

const MobileSearchApplication = ({ Controller, register, control, t, reset, previousPage, handleSubmit, tenantId, data, onSubmit, isLoading, onClear }) => {

  function activateModal(state, action) {
    switch (action.type) {
      case "set":
        return action.payload;
      case "remove":
        return false;
      default:
        break;
    }
  }
  const [tabledata, settabledata] = useState([]);
  const DownloadBtn = (props) => {
    return (
      <div className={props.className || "citizen-audit-download-btn"} onClick={props.onClick}>
        <DownloadBtnCommon />
      </div>
    );
  };
  const handleExcelDownload = (tabData) => {
    if (tabData?.[0] !== undefined) {
      return Digit.Download.Excel(tabData?.[0], "AuditReport");
    }
  };
  useEffect(() => {
    if (data?.length > 0) {
      settabledata([

        data?.map((obj) => {
          let returnObject = {};
          returnObject[t("AUDIT_DATE_LABEL")] = convertEpochToDate(obj?.timestamp) || "-";
          returnObject[t("AUDIT_TIME_LABEL")] = convertEpochToTimeInHours(obj?.timestamp) || "-";
          returnObject[t("AUDIT_DATAVIEWED_LABEL")] = obj?.dataView?.join(", ") || "-";
          returnObject[t("AUDIT_DATAVIEWED_BY_LABEL")] = obj?.dataViewedBy || "-";
          returnObject[t("AUDIT_ROLE_LABEL")] = obj?.roles?.map(obj => obj.name).join(", ") || "-";
          return {
            ...returnObject,
          }
        })
      ])
    }
  }, [data]);
  const convertEpochToDate = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
      return "NA";
    }
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${day}/${month}/${year}`;
  };
  const convertEpochToTimeInHours = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
      return "NA";
    }
    const dateFromApi = new Date(dateEpoch);
    let hour = dateFromApi.getHours();
    let min = dateFromApi.getMinutes();
    let period = hour > 12 ? "PM" : "AM";
    hour = hour > 12 ? hour - 12 : hour;
    hour = (hour > 9 ? "" : "0") + hour;
    min = (min > 9 ? "" : "0") + min;
    return `${hour}:${min} ${period}`;
  };
  const [currentlyActiveMobileModal, setActiveMobileModal] = useReducer(activateModal, false);

  const closeMobilePopupModal = () => {
    setActiveMobileModal({ type: "remove" });
  };

  const MobilePopUpCloseButton = () => (
    <div className="InboxMobilePopupCloseButtonWrapper" onClick={closeMobilePopupModal}>
      <CloseSvg />
    </div>
  );
  const searchFormFieldsComponentProps = { Controller, register, control, t, reset, previousPage, onClear };

  const MobileComponentDirectory = ({ currentlyActiveMobileModal, searchFormFieldsComponentProps, tenantId, ...props }) => {
    const { closeMobilePopupModal } = props;
    switch (currentlyActiveMobileModal) {
      case "SearchFormComponent":
        return (
          <SearchForm {...props}>
            <MobilePopUpCloseButton />
            <div className="MobilePopupHeadingWrapper">
              <h2>{t("PRIVACY_AUDIT_REPORT")}:</h2>
            </div>
            <SearchFormFields {...searchFormFieldsComponentProps} {...{ closeMobilePopupModal, tenantId, t }} />
          </SearchForm>
        );
      default:
        return <span></span>;
    }
  };
  const CurrentMobileModalComponent = useCallback(
    ({ currentlyActiveMobileModal, searchFormFieldsComponentProps, tenantId, ...props }) =>
      MobileComponentDirectory({ currentlyActiveMobileModal, searchFormFieldsComponentProps, tenantId, ...props }),
    [currentlyActiveMobileModal]
  );
  let roles = [];
  data?.roles?.forEach((item) => {
    roles.push(item?.name);
  });
  const propsMobileInboxCards = useMemo(
    () => {
      if (data?.display) {
        return []
      }
      if (data === "") {
        return [];
      }
      return data?.map((data) => ({
        [t("AUDIT_DATE_LABEL")]: convertEpochToDate(data.timestamp) || "-",
        [t("AUDIT_TIME_LABEL")]: convertEpochToTimeInHours(data.timestamp) || "-",
        [t("AUDIT_DATAVIEWED_LABEL")]: data.dataView?.join(", ") || "-",
        [t("AUDIT_DATAVIEWED_BY_LABEL")]: data.dataViewedBy || "-",
        [t("AUDIT_ROLE_LABEL")]: data.roles?.slice(0, 3)?.map((e) => e.name).join(", ") || "-",
      }))
    },
    [data]
  );

  return (
    <React.Fragment>
      <div className="citizen-audit-top-nav">
        <BackButton />
      </div>
      <div className="citizen-audit-header-wrap">
        <div className="citizen-audit-title-block">
          <div className="citizen-audit-header-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            </svg>
          </div>
          <div className="citizen-audit-title-text">
            <div className="citizen-audit-title-row">
              <Header className="citizen-audit-title">{t("PRIVACY_AUDIT_REPORT")}</Header>
            </div>
          </div>
        </div>
      </div>
      <div className="citizen-audit-mobile-bar">
        <div className="searchBox">
          <SearchAction
            text={t("ES_COMMON_SEARCH")}
            handleActionClick={() => setActiveMobileModal({ type: "set", payload: "SearchFormComponent" })}
            {...{ tenantId, t }}
          />
        </div>
        {Array.isArray(data) && data.length > 0 && (
          <DownloadBtn className="citizen-audit-download-btn" onClick={() => handleExcelDownload(tabledata)} />
        )}
      </div>
      {currentlyActiveMobileModal ? (
        <PopUp>
          <CurrentMobileModalComponent
            onSubmit={(data) => {
              setActiveMobileModal({ type: "remove" });
              onSubmit(data);
            }}
            handleSubmit={handleSubmit}
            id="search-form"
            className="rm-mb form-field-flex-one inboxPopupMobileWrapper"
            {...{ searchFormFieldsComponentProps, currentlyActiveMobileModal, closeMobilePopupModal, tenantId }}
          />
        </PopUp>
      ) : null}
      {isLoading && (
        <div className="citizen-audit-loader-card">
          <Loader />
        </div>
      )}
      {!isLoading && data?.display ? (
        <div className="citizen-audit-empty-card">
          <div className="citizen-audit-empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
          </div>
          {t(data.display)
            .split("\\n")
            .map((text, index) => (
              <p key={index} className="citizen-audit-empty-text">
                {text}
              </p>
            ))}
        </div>
      ) : (
        <DetailsCard
          {...{
            data: propsMobileInboxCards,
          }}
        />
      )}
    </React.Fragment>
  )
}

export default MobileSearchApplication   