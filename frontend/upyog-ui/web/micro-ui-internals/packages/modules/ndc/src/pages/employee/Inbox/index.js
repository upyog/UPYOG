import React, { Fragment, useCallback, useMemo, useReducer, useState, useEffect } from "react";

import { InboxComposer, ComplaintIcon, Header } from "@nudmcdgnpm/digit-ui-react-components";

import { useTranslation } from "react-i18next";

import SearchFormFieldsComponents from "./SearchFormFieldsComponent";
import FilterFormFieldsComponent from "./FilterFormFieldsComponent";
import useInboxTableConfig from "./useInboxTableConfig";
import useInboxMobileCardsData from "./useInboxMobileCardsData";

import { businessServiceList } from "../../../utils";

const Inbox = ({ parentRoute }) => {
  const { t } = useTranslation();

  const tenantId = window.localStorage.getItem("Employee.tenant-id");

  const [getFilter, setFilter] = useState();

  /*
   * ------------------------------------------------------------
   * SEARCH FORM DEFAULT VALUES
   * ------------------------------------------------------------
   */

  const searchFormDefaultValues = {
    applicationNo: "",
    mobileNumber: "",
  };

  /*
   * ------------------------------------------------------------
   * FILTER FORM DEFAULT VALUES
   * ------------------------------------------------------------
   */

  const filterFormDefaultValues = {
    moduleName: "ndc-services",
    applicationStatus: [],
    businessService: null,
    locality: [],
    assignee: "ASSIGNED_TO_ALL",
    businessServiceArray: businessServiceList(true) || [],
  };

  /*
   * ------------------------------------------------------------
   * TABLE DEFAULT VALUES
   * ------------------------------------------------------------
   */

  const tableOrderFormDefaultValues = {
    sortBy: "",
    limit: window.Digit.Utils.browser.isMobile() ? 50 : 10,
    offset: 0,
    sortOrder: "DESC",
  };

  /*
   * ------------------------------------------------------------
   * FORM REDUCER
   * ------------------------------------------------------------
   */

  function formReducer(state, payload) {
    switch (payload.action) {
      case "mutateSearchForm":
        Digit.SessionStorage.set("NDC.INBOX", {
          ...state,
          searchForm: payload.data,
        });

        return {
          ...state,
          searchForm: payload.data,
        };

      case "mutateFilterForm":
        Digit.SessionStorage.set("NDC.INBOX", {
          ...state,
          filterForm: payload.data,
        });

        return {
          ...state,
          filterForm: payload.data,
        };

      case "mutateTableForm":
        Digit.SessionStorage.set("NDC.INBOX", {
          ...state,
          tableForm: payload.data,
        });

        return {
          ...state,
          tableForm: payload.data,
        };

      default:
        return state;
    }
  }

  /*
   * ------------------------------------------------------------
   * SESSION STORAGE
   * ------------------------------------------------------------
   */

  const InboxObjectInSessionStorage = Digit.SessionStorage.get("NDC.INBOX");

  /*
   * ------------------------------------------------------------
   * RESET SEARCH
   * ------------------------------------------------------------
   */

  const onSearchFormReset = (setSearchFormValue) => {
    setSearchFormValue("applicationNo", "");
    setSearchFormValue("mobileNumber", "");

    dispatch({
      action: "mutateSearchForm",
      data: {
        ...searchFormDefaultValues,
      },
    });
  };

  /*
   * ------------------------------------------------------------
   * RESET FILTER
   * ------------------------------------------------------------
   */

  const onFilterFormReset = (setFilterFormValue) => {
    setFilterFormValue("moduleName", "ndc-services");
    setFilterFormValue("applicationStatus", []);
    setFilterFormValue("locality", []);
    setFilterFormValue("assignee", "ASSIGNED_TO_ALL");
    setFilterFormValue("businessService", null);

    dispatch({
      action: "mutateFilterForm",
      data: filterFormDefaultValues,
    });
  };

  /*
   * ------------------------------------------------------------
   * RESET SORT
   * ------------------------------------------------------------
   */

  const onSortFormReset = (setSortFormValue) => {
    setSortFormValue("sortOrder", "DESC");

    dispatch({
      action: "mutateTableForm",
      data: tableOrderFormDefaultValues,
    });
  };

  /*
   * ------------------------------------------------------------
   * INITIAL FORM VALUE
   * ------------------------------------------------------------
   */

  const formInitValue = useMemo(() => {
    return (
      InboxObjectInSessionStorage || {
        filterForm: filterFormDefaultValues,
        searchForm: searchFormDefaultValues,
        tableForm: tableOrderFormDefaultValues,
      }
    );
  }, [InboxObjectInSessionStorage?.filterForm, InboxObjectInSessionStorage?.searchForm, InboxObjectInSessionStorage?.tableForm]);

  const [formState, dispatch] = useReducer(formReducer, formInitValue);

  /*
   * ------------------------------------------------------------
   * PAGE SIZE
   * ------------------------------------------------------------
   */

  const onPageSizeChange = (e) => {
    dispatch({
      action: "mutateTableForm",
      data: {
        ...formState.tableForm,
        limit: Number(e.target.value),
        offset: 0,
      },
    });
  };

  /*
   * ------------------------------------------------------------
   * TABLE SORTING
   * ------------------------------------------------------------
   */

  const onSortingByData = (e) => {
    if (e && e.length > 0) {
      const [{ id, desc }] = e;

      const sortOrder = desc ? "DESC" : "ASC";
      const sortBy = id;

      if (formState.tableForm.sortBy !== sortBy || formState.tableForm.sortOrder !== sortOrder) {
        dispatch({
          action: "mutateTableForm",
          data: {
            ...formState.tableForm,
            sortBy,
            sortOrder,
          },
        });
      }
    }
  };

  /*
   * ------------------------------------------------------------
   * MOBILE SORTING
   * ------------------------------------------------------------
   */

  const onMobileSortOrderData = (data) => {
    const { sortOrder } = data;

    dispatch({
      action: "mutateTableForm",
      data: {
        ...formState.tableForm,
        sortOrder,
      },
    });
  };

  /*
   * ------------------------------------------------------------
   * LOCALITIES
   * ------------------------------------------------------------
   */

  const { data: localitiesForEmployeesCurrentTenant, isLoading: loadingLocalitiesForEmployeesCurrentTenant } = Digit.Hooks.useBoundaryLocalities(
    tenantId,
    "revenue",
    {},
    t,
  );

  /*
   * ------------------------------------------------------------
   * FILTER HANDLER
   * ------------------------------------------------------------
   */

  const handleFilter = (filterStatus) => {
    setFilter(filterStatus);
  };

  /*
   * ------------------------------------------------------------
   * INBOX API
   * ------------------------------------------------------------
   */

  const { isLoading: isInboxLoading, data } = Digit.Hooks.ndc.useInbox({
    tenantId,
    filters: {
      ...formState,
      getFilter,
    },
  });

  /*
   * ------------------------------------------------------------
   * TABLE DATA
   * ------------------------------------------------------------
   */

  const [table, setTable] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [totalCount, setTotalCount] = useState(0);

  useEffect(() => {
    if (data) {
      setStatuses(data?.statuses || []);
      setTable(data?.table || []);
      setTotalCount(data?.totalCount || 0);
    }
  }, [data]);

  /*
   * ------------------------------------------------------------
   * INBOX CARD
   * ------------------------------------------------------------
   */

  const PropsForInboxLinks = {
    logoIcon: <ComplaintIcon />,

    headerText: t("MODULE_NKS_NO_DUE_CERTIFICATE_FEES"),

    links: [
      {
        text: "",
        link: "",
        accessTo: [""],
      },
    ],
  };

  /*
   * ------------------------------------------------------------
   * SEARCH FORM
   * ------------------------------------------------------------
   */

  const SearchFormFields = useCallback(
    ({ registerRef, control, searchFormState, searchFieldComponents }) => (
      <SearchFormFieldsComponents
        {...{
          registerRef,
          control,
          searchFormState,
          searchFieldComponents,
        }}
      />
    ),
    [],
  );

  /*
   * ------------------------------------------------------------
   * FILTER FORM
   * ------------------------------------------------------------
   */

  const FilterFormFields = useCallback(
    ({ registerRef, controlFilterForm, setFilterFormValue, getFilterFormValue }) => (
      <FilterFormFieldsComponent
        {...{
          statuses,
          isInboxLoading,
          registerRef,
          controlFilterForm,
          setFilterFormValue,
          filterFormState: formState?.filterForm,
          getFilterFormValue,
          localitiesForEmployeesCurrentTenant,
          loadingLocalitiesForEmployeesCurrentTenant,
        }}
        handleFilter={handleFilter}
      />
    ),
    [statuses, isInboxLoading, localitiesForEmployeesCurrentTenant, loadingLocalitiesForEmployeesCurrentTenant, formState?.filterForm],
  );

  /*
   * ------------------------------------------------------------
   * SEARCH SUBMIT
   * ------------------------------------------------------------
   */

  const onSearchFormSubmit = (data) => {
    if (data && Object.prototype.hasOwnProperty.call(data, "")) {
      delete data[""];
    }

    dispatch({
      action: "mutateTableForm",
      data: {
        ...tableOrderFormDefaultValues,
      },
    });

    dispatch({
      action: "mutateSearchForm",
      data,
    });
  };

  /*
   * ------------------------------------------------------------
   * FILTER SUBMIT
   * ------------------------------------------------------------
   */

  const onFilterFormSubmit = (data) => {
    if (data && Object.prototype.hasOwnProperty.call(data, "")) {
      delete data[""];
    }

    dispatch({
      action: "mutateTableForm",
      data: {
        ...tableOrderFormDefaultValues,
      },
    });

    dispatch({
      action: "mutateFilterForm",
      data,
    });
  };

  /*
   * ------------------------------------------------------------
   * SEARCH PROPS
   * ------------------------------------------------------------
   */

  const propsForSearchForm = {
    SearchFormFields,
    onSearchFormSubmit,
    searchFormDefaultValues: formState?.searchForm,
    resetSearchFormDefaultValues: searchFormDefaultValues,
    onSearchFormReset,
  };

  /*
   * ------------------------------------------------------------
   * FILTER PROPS
   * ------------------------------------------------------------
   */

  const propsForFilterForm = {
    FilterFormFields,
    onFilterFormSubmit,
    filterFormDefaultValues: formState?.filterForm,
    resetFilterFormDefaultValues: filterFormDefaultValues,
    onFilterFormReset,
  };

  /*
   * ------------------------------------------------------------
   * TABLE CONFIG
   * ------------------------------------------------------------
   */

  const propsForInboxTable = useInboxTableConfig({
    parentRoute,
    onPageSizeChange,
    formState,
    totalCount,
    table,
    dispatch,
    onSortingByData,
  });

  /*
   * ------------------------------------------------------------
   * MOBILE CARDS
   * ------------------------------------------------------------
   */

  const propsForInboxMobileCards = useInboxMobileCardsData({
    parentRoute,
    table,
  });

  /*
   * ------------------------------------------------------------
   * MOBILE SORT
   * ------------------------------------------------------------
   */

  const propsForMobileSortForm = {
    onMobileSortOrderData,
    sortFormDefaultValues: formState?.tableForm,
    onSortFormReset,
  };

  /*
   * ------------------------------------------------------------
   * UI
   * ------------------------------------------------------------
   */

  return (
    <>
      {/* =====================================================
          NDC INBOX OVERRIDE CSS
          ===================================================== */}

      <style>
        {`
          /* ---------------------------------------------
             MAIN INBOX
             --------------------------------------------- */

          .NDCSection {
            width: 100%;
            font-size: 14px !important;
          }

          .NDCSection * {
            box-sizing: border-box;
          }


          /* ---------------------------------------------
             INBOX HEADER
             --------------------------------------------- */

          .NDCSection .inbox-count {
            font-size: 14px !important;
            font-weight: 600 !important;
          }


          /* ---------------------------------------------
             SEARCH AREA
             --------------------------------------------- */

          .NDCSection .search-container {
            width: 100% !important;
          }

          .NDCSection .search-complaint-container {
            width: 100% !important;
          }

          .NDCSection .complaint-input-container {
            width: 100% !important;
            display: flex !important;
            align-items: flex-end !important;
            gap: 24px !important;
          }

          .NDCSection .complaint-input-container > * {
            font-size: 14px !important;
          }


          /* ---------------------------------------------
             SEARCH LABELS
             --------------------------------------------- */

          .NDCSection .search-container label,
          .NDCSection .search-complaint-container label,
          .NDCSection .complaint-input-container label {
            font-size: 14px !important;
            font-weight: 600 !important;
            line-height: 20px !important;
          }


          /* ---------------------------------------------
             SEARCH INPUT
             --------------------------------------------- */

          .NDCSection .search-container input,
          .NDCSection .search-complaint-container input,
          .NDCSection .complaint-input-container input {
            font-size: 14px !important;
            height: 42px !important;
            min-height: 42px !important;
            padding: 8px 12px !important;
          }


          /* ---------------------------------------------
             SEARCH FIELD WIDTH
             --------------------------------------------- */

          .NDCSection .complaint-input-container
          .search-field {
            min-width: 240px !important;
          }


          /* ---------------------------------------------
             SEARCH BUTTON AREA
             --------------------------------------------- */

          .NDCSection .search-action-wrapper {
            flex: 1 !important;
            min-width: 220px !important;
          }

          .NDCSection .search-action-wrapper button {
            height: 42px !important;
            min-height: 42px !important;
            font-size: 15px !important;
            font-weight: 600 !important;
          }


          /* ---------------------------------------------
             CLEAR SEARCH
             --------------------------------------------- */

          .NDCSection .search-action-wrapper a,
          .NDCSection .search-action-wrapper span {
            font-size: 14px !important;
          }


          /* ---------------------------------------------
             TABLE CONTAINER
             --------------------------------------------- */

          .NDCSection table {
            width: 100% !important;
            font-size: 14px !important;
          }


          /* ---------------------------------------------
             TABLE HEADER
             --------------------------------------------- */

          .NDCSection table thead th,
          .NDCSection table th {
            font-size: 14px !important;
            font-weight: 700 !important;
            line-height: 20px !important;
            padding: 12px 8px !important;
            white-space: nowrap !important;
          }


          /* ---------------------------------------------
             TABLE ROWS
             --------------------------------------------- */

          .NDCSection table tbody td,
          .NDCSection table td {
            font-size: 14px !important;
            font-weight: 500 !important;
            line-height: 20px !important;
            padding: 12px 8px !important;
          }


          /* ---------------------------------------------
             APPLICATION NUMBER
             --------------------------------------------- */

          .NDCSection table tbody td a {
            font-size: 14px !important;
            font-weight: 600 !important;
          }


          /* ---------------------------------------------
             TABLE STATUS
             --------------------------------------------- */

          .NDCSection table tbody td:last-child {
            font-size: 14px !important;
            font-weight: 500 !important;
          }


          /* ---------------------------------------------
             FILTER PANEL
             --------------------------------------------- */

          .NDCSection .filter {
            font-size: 14px !important;
          }

          .NDCSection .filter label {
            font-size: 14px !important;
            line-height: 20px !important;
          }

          .NDCSection .filter button {
            font-size: 14px !important;
          }


          /* ---------------------------------------------
             PAGINATION
             --------------------------------------------- */

          .NDCSection table + div,
          .NDCSection .pagination {
            font-size: 14px !important;
          }


          /* ---------------------------------------------
             MOBILE
             --------------------------------------------- */

          @media (max-width: 768px) {

            .NDCSection .complaint-input-container {
              display: block !important;
            }

            .NDCSection .complaint-input-container > * {
              width: 100% !important;
              margin-bottom: 12px !important;
            }

            .NDCSection .search-container input {
              width: 100% !important;
            }

            .NDCSection table th,
            .NDCSection table td {
              font-size: 13px !important;
              padding: 10px 6px !important;
            }

          }
        `}
      </style>

      <Header>
        {t("ES_COMMON_INBOX")}

        {totalCount ? <p className="inbox-count">{totalCount}</p> : null}
      </Header>

      <div className="NDCSection">
        <InboxComposer
          {...{
            isInboxLoading,
            PropsForInboxLinks,

            ...propsForSearchForm,
            ...propsForFilterForm,
            ...propsForMobileSortForm,

            propsForInboxTable,
            propsForInboxMobileCards,

            formState,
          }}
        />
      </div>
    </>
  );
};

export default Inbox;
