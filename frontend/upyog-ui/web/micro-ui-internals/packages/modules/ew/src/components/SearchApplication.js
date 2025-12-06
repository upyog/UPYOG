import React, { useCallback, useMemo, useEffect } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  TextInput,
  SubmitBar,
  DatePicker,
  CardLabelError,
  SearchForm,
  SearchField,
  Table,
  Card,
  MobileNumber,
  Loader,
  CardText,
  Header,
} from "@upyog/digit-ui-react-components";
import { Link } from "react-router-dom";

/**
 * Search interface for E-Waste applications with filtering and pagination capabilities.
 * Provides advanced search functionality including request ID, mobile number, and date range filters.
 *
 * @param {Object} props Component properties
 * @param {string} props.tenantId Current tenant identifier
 * @param {boolean} props.isLoading Loading state indicator
 * @param {Function} props.t Translation function
 * @param {Function} props.onSubmit Form submission handler
 * @param {Array} props.data Search results data
 * @param {number} props.count Total number of records
 * @param {Function} props.setShowToast Toast notification handler
 * @returns {JSX.Element} Search interface with form and results table
 */
const EWSearchApplication = ({ tenantId, isLoading, t, onSubmit, data, count, setShowToast }) => {
  const isMobile = window.Digit.Utils.browser.isMobile();
  const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
    defaultValues: {
      offset: 0,
      limit: !isMobile && 10,
      sortBy: "commencementDate",
      sortOrder: "DESC",
    },
  });

  /**
   * Registers default form fields for pagination and sorting
   */
  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    register("sortBy", "commencementDate");
    register("sortOrder", "DESC");
  }, [register]);

  const GetCell = (value) => <span className="cell-text">{value}</span>;

  /**
   * Table column configuration with cell renderers and sorting options
   */
  const columns = useMemo(
    () => [
      {
        Header: t("EW_REQUEST_ID"), // Header for the "Request ID" column
        accessor: "requestId",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <div>
              <span className="link">
                <Link to={`applicationsearch/application-details/${row.original["requestId"]}`}>{row.original["requestId"]}</Link>
              </span>
            </div>
          );
        },
      },
      {
        Header: t("EW_APPLICANT_NAME"), // Header for the "Applicant Name" column
        Cell: (row) => {
          return GetCell(`${row?.row?.original?.applicant?.["applicantName"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("EW_MOBILE_NUMBER"), // Header for the "Mobile Number" column
        Cell: ({ row }) => {
          return GetCell(`${row.original?.applicant?.["mobileNumber"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("EW_AMOUNT"), // Header for the "Amount" column
        Cell: ({ row }) => {
          return GetCell(`${row.original?.["calculatedAmount"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("EW_STATUS"), // Header for the "Status" column
        Cell: ({ row }) => {
          return GetCell(`${row?.original?.["requestStatus"]}`);
        },
        disableSortBy: true,
      },
    ],
    []
  );

  /**
   * Handles table sorting by updating form values
   * @param {Object} args Sorting parameters
   */
  const onSort = useCallback((args) => {
    if (args.length === 0) return;
    setValue("sortBy", args.id);
    setValue("sortOrder", args.desc ? "DESC" : "ASC");
  }, []);

  /**
   * Updates page size and triggers search
   * @param {Event} e Change event
   */
  function onPageSizeChange(e) {
    setValue("limit", Number(e.target.value));
    handleSubmit(onSubmit)();
  }

  /**
   * Navigates to next page and triggers search
   */
  function nextPage() {
    setValue("offset", getValues("offset") + getValues("limit"));
    handleSubmit(onSubmit)();
  }

  /**
   * Navigates to previous page and triggers search
   */
  function previousPage() {
    setValue("offset", getValues("offset") - getValues("limit"));
    handleSubmit(onSubmit)();
  }

  let validation = {}; // Placeholder for validation rules

  return (
    <React.Fragment>
      <div>
        <Header>{t("EW_SEARCH_REQUEST_ID")}</Header> {/* Header for the search form */}
        <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
          {/* Search field for Request ID */}
          <SearchField>
            <label>{t("EW_REQUEST_ID")}</label>
            <TextInput name="requestId" inputRef={register({})} />
          </SearchField>

          {/* Search field for Mobile Number */}
          <SearchField>
            <label>{t("EW_OWNER_MOBILE_NO")}</label>
            <MobileNumber
              name="mobileNumber"
              inputRef={register({
                minLength: {
                  value: 10,
                  message: t("CORE_COMMON_MOBILE_ERROR"),
                },
                maxLength: {
                  value: 10,
                  message: t("CORE_COMMON_MOBILE_ERROR"),
                },
                pattern: {
                  value: /[6789][0-9]{9}/,
                  message: t("CORE_COMMON_MOBILE_ERROR"),
                },
              })}
              type="number"
              componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
            />
            <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError>
          </SearchField>

          {/* Search field for From Date */}
          <SearchField>
            <label>{t("EW_FROM_DATE")}</label>
            <Controller
              render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
              name="fromDate"
              control={control}
            />
          </SearchField>

          {/* Search field for To Date */}
          <SearchField>
            <label>{t("EW_TO_DATE")}</label>
            <Controller
              render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
              name="toDate"
              control={control}
            />
          </SearchField>

          {/* Submit and Clear All buttons */}
          <SearchField className="submit">
            <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
            <p
              style={{ marginTop: "10px" }}
              onClick={() => {
                reset({
                  applicationNumber: "",
                  fromDate: "",
                  toDate: "",
                  mobileNumber: "",
                  status: "",
                  offset: 0,
                  limit: 10,
                  sortBy: "commencementDate",
                  sortOrder: "DESC",
                });
                setShowToast(null);
                previousPage();
              }}
            >
              {t(`ES_COMMON_CLEAR_ALL`)}
            </p>
          </SearchField>
        </SearchForm>

        {/* Render the table or loader based on the data and loading state */}
        {!isLoading && data?.display ? (
          <Card style={{ marginTop: 20 }}>
            {t(data.display)
              .split("\\n")
              .map((text, index) => (
                <p key={index} style={{ textAlign: "center" }}>
                  {text}
                </p>
              ))}
          </Card>
        ) : !isLoading && data !== "" ? (
          <Table
            t={t}
            data={data}
            totalRecords={count}
            columns={columns}
            getCellProps={(cellInfo) => {
              return {
                style: {
                  minWidth: cellInfo.column.Header === t("EW_INBOX_APPLICATION_NO") ? "240px" : "",
                  padding: "20px 18px",
                  fontSize: "16px",
                },
              };
            }}
            onPageSizeChange={onPageSizeChange}
            currentPage={getValues("offset") / getValues("limit")}
            onNextPage={nextPage}
            onPrevPage={previousPage}
            pageSizeLimit={getValues("limit")}
            onSort={onSort}
            disableSort={false}
            sortParams={[{ id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false }]}
          />
        ) : (
          data !== "" || (isLoading && <Loader />)
        )}
      </div>
    </React.Fragment>
  );
};

export default EWSearchApplication; // Exporting the component
