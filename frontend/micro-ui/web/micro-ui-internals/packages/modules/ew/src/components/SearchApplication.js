import React, { useCallback, useMemo, useEffect } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  TextInput,
  SubmitBar,
  LinkLabel,
  ActionBar,
  CloseSvg,
  DatePicker,
  CardLabelError,
  SearchForm,
  SearchField,
  Dropdown,
  Table,
  Card,
  MobileNumber,
  Loader,
  CardText,
  Header,
} from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";

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
  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    register("sortBy", "commencementDate");
    register("sortOrder", "DESC");
  }, [register]);

  const stateId = Digit.ULBService.getStateId();
  const GetCell = (value) => <span className="cell-text">{value}</span>;
  let menu = [];

  const columns = useMemo(
    () => [
      {
        Header: t("EW_REQUEST_ID"),
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
        Header: t("EW_APPLICANT_NAME"),
        Cell: (row) => {
          return GetCell(`${row?.row?.original?.applicant?.["applicantName"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("EW_MOBILE_NUMBER"),
        Cell: ({ row }) => {
          return GetCell(`${row.original?.applicant?.["mobileNumber"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("EW_AMOUNT"),
        Cell: ({ row }) => {
          return GetCell(`${row.original?.["calculatedAmount"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("EW_STATUS"),
        Cell: ({ row }) => {
          return GetCell(`${row?.original?.["requestStatus"]}`);
        },
        disableSortBy: true,
      },
    ],
    []
  );

  const onSort = useCallback((args) => {
    if (args.length === 0) return;
    setValue("sortBy", args.id);
    setValue("sortOrder", args.desc ? "DESC" : "ASC");
  }, []);

  function onPageSizeChange(e) {
    setValue("limit", Number(e.target.value));
    handleSubmit(onSubmit)();
  }

  function nextPage() {
    setValue("offset", getValues("offset") + getValues("limit"));
    handleSubmit(onSubmit)();
  }
  function previousPage() {
    setValue("offset", getValues("offset") - getValues("limit"));
    handleSubmit(onSubmit)();
  }
  let validation = {};

  return (
    <React.Fragment>
      <div>
        <Header>{t("EW_SEARCH_REQUEST_ID")}</Header>
        <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
          <SearchField>
            <label>{t("EW_REQUEST_ID")}</label>
            <TextInput name="requestId" inputRef={register({})} />
          </SearchField>

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
          <SearchField>
            <label>{t("EW_FROM_DATE")}</label>
            <Controller
              render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
              name="fromDate"
              control={control}
            />
          </SearchField>
          <SearchField>
            <label>{t("EW_TO_DATE")}</label>
            <Controller
              render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
              name="toDate"
              control={control}
            />
          </SearchField>
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

export default EWSearchApplication;
