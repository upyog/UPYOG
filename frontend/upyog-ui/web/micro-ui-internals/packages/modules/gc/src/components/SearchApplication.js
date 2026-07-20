import React, { useCallback, useMemo } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  TextInput,
  SubmitBar,
  DatePicker,
  CardLabelError,
  SearchForm,
  SearchField,
  Dropdown,
  Table,
  Card,
  MobileNumber,
  Loader,
  Header,
} from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";

/**
 * `GCSearchApplication` component provides a search interface for Garbage Collection (GC) applications.
 * It allows users to search, filter, and sort application records based on parameters like application number,
 * applicant name, mobile number, status, and date range.
 */
const GCSearchApplication = ({ tenantId, isLoading, t, onSubmit, onClear, data, count, setShowToast }) => {
  const isMobile = window.Digit.Utils.browser.isMobile();
  const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
    defaultValues: {
      applicationNo: "",
      status: undefined,
      mobileNumber: "",
      fromDate: "",
      toDate: "",
      offset: 0,
      limit: !isMobile && 10,
    },
  });
  const user = Digit.UserService.getUser().info;
  const GetCell = (value) => <span className="cell-text">{value}</span>;

  const columns = useMemo(() => [
    {
      Header: t("GC_APPLICATION_NUMBER_LABEL"),
      accessor: "applicationNo",
      disableSortBy: true,
      Cell: ({ row }) => {
        const appNo = row.original?.grbgApplication?.applicationNo || row.original?.applicationNo;
        return (
          <div>
            <span className="link">
              <Link to={`/upyog-ui/employee/gc/application-details/${encodeURIComponent(appNo)}`}>
                {appNo || t("CS_NA")}
              </Link>
            </span>
          </div>
        );
      },
    },
    {
      Header: t("GC_APPLICANT_NAME"),
      disableSortBy: true,
      Cell: ({ row }) => {
        const owners = row.original?.applicantDetails || [];
        const name = owners?.map((o) => o?.name || o?.applicantName)?.join(", ") || row.original?.name || row.original?.garbageSpecification?.name;
        return GetCell(name || t("CS_NA"));
      },
    },
    {
      Header: t("GC_MOBILE_NUMBER"),
      disableSortBy: true,
      Cell: ({ row }) => {
        const owners = row.original?.applicantDetails || [];
        const mobile = owners?.map((o) => o?.mobileNumber)?.join(", ") || row.original?.mobileNumber || row.original?.garbageSpecification?.phoneNumber;
        return GetCell(mobile || t("CS_NA"));
      },
    },
    {
      Header: t("PT_COMMON_TABLE_COL_STATUS_LABEL"),
      disableSortBy: true,
      Cell: ({ row }) => {
        const status = row.original?.applicationStatus || row.original?.status || row.original?.grbgApplication?.status;
        return GetCell(status ? t(`GC_STATUS_${status}`) : t("CS_NA"));
      },
    },
  ], [t]);

  const statusOptions = [
    {
      i18nKey: t("GC_STATUS_INITIATED"),
      code: "INITIATED",
    },
    {
      i18nKey: t("GC_STATUS_PENDING_FOR_VERIFICATION"),
      code: "PENDING_FOR_VERIFICATION",
    },
    {
      i18nKey: t("GC_STATUS_PENDING_FOR_APPROVAL"),
      code: "PENDING_FOR_APPROVAL",
    },
    {
      i18nKey: t("GC_STATUS_EDIT_APPLICATION"),
      code: "EDIT_APPLICATION",
    },
    {
      i18nKey: t("GC_STATUS_APPROVED"),
      code: "APPROVED",
    },
    {
      i18nKey: t("GC_STATUS_REJECTED"),
      code: "REJECTED",
    },
  ];

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

  return (
    <React.Fragment>
      <div style={{ padding: user?.type === "CITIZEN" ? "0 24px 0 24px" : "" }}>
        <Header>{t("GC_SEARCH_APPLICATION")}</Header>
        {user?.type === "EMPLOYEE" && (
          <Card className={"card-search-heading"}>
            <span style={{ color: "#505A5F" }}>{t("PROVIDE_ATLEAST_ONE_PARAMETERS")}</span>
          </Card>
        )}
        {user?.type === "CITIZEN" && (
          <span style={{ color: "#505A5F", marginBottom: "10px" }}>{t("PROVIDE_ATLEAST_ONE_PARAMETERS")}</span>
        )}

        <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
          <SearchField>
            <label>{t("GC_APPLICATION_NUMBER_LABEL")}</label>
            <Controller control={control} name="applicationNo" render={({ field }) => <TextInput name={field.name} value={field.value} onChange={field.onChange} onBlur={field.onBlur} inputRef={field.ref} />} />
          </SearchField>
          <SearchField>
            <label>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</label>
            <Controller control={control} name="status" render={({ field }) => <Dropdown selected={field.value} select={field.onChange} onBlur={field.onBlur} option={statusOptions} optionKey="i18nKey" t={t} disable={false} />} />
          </SearchField>
          <SearchField>
            <label>{t("GC_MOBILE_NUMBER")}</label>
            <Controller control={control} name="mobileNumber" rules={{ minLength: { value: 10, message: t("CORE_COMMON_MOBILE_ERROR") }, maxLength: { value: 10, message: t("CORE_COMMON_MOBILE_ERROR") }, pattern: { value: /[6789][0-9]{9}/, message: t("CORE_COMMON_MOBILE_ERROR") } }} render={({ field }) => <MobileNumber name={field.name} value={field.value} onChange={field.onChange} onBlur={field.onBlur} inputRef={field.ref} />} />
            <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError>
          </SearchField>
          <SearchField>
            <label>{t("FROM_DATE")}</label>
            <Controller render={({ field }) => <DatePicker date={field.value} disabled={false} onChange={field.onChange} max={new Date().toISOString().split("T")[0]} />} name="fromDate" control={control} />
          </SearchField>
          <SearchField>
            <label>{t("TO_DATE")}</label>
            <Controller render={({ field }) => <DatePicker date={field.value} disabled={false} onChange={field.onChange} />} name="toDate" control={control} />
          </SearchField>
          <SearchField></SearchField>
          <SearchField className="submit">
            <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
            <p style={{ marginTop: "10px", cursor: "pointer" }} onClick={() => { reset({ applicationNo: "", fromDate: "", toDate: "", mobileNumber: "", status: undefined, offset: 0, limit: 10 }); setShowToast(null); onClear(); }}>
              {t(`ES_COMMON_CLEAR_ALL`)}
            </p>
          </SearchField>
        </SearchForm>
        {!isLoading && data?.display ? <Card style={{ marginTop: 20 }}>{t(data.display).split("\\n").map((text, index) => (<p key={index} style={{ textAlign: "center" }}>{text}</p>))}</Card> : !isLoading && data !== "" ? <Table t={t} data={data} totalRecords={count} columns={columns} getCellProps={(cellInfo) => { return { style: { minWidth: cellInfo.column.Header === t("GC_APPLICATION_NUMBER_LABEL") ? "240px" : "", padding: "20px 18px", fontSize: "16px" } }; }} onPageSizeChange={onPageSizeChange} currentPage={getValues("offset") / getValues("limit")} onNextPage={nextPage} onPrevPage={previousPage} pageSizeLimit={getValues("limit")} disableSort={true} /> : data !== "" || (isLoading && <Loader />)}
      </div>
    </React.Fragment>
  );
};

export default GCSearchApplication;