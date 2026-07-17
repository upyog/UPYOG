import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Header, Table, Card, Loader, TextInput, SubmitBar, SearchForm, SearchField } from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useForm, Controller } from "react-hook-form";

// Employee Inbox Component for Garbage Collection
const Inbox = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [appliedSearch, setAppliedSearch] = useState("");
  const [pageOffset, setPageOffset] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  const { control, handleSubmit, reset } = useForm({
    defaultValues: { applicationNo: "" }
  });

  const { isLoading, data } = Digit.Hooks.gc.useGCSearch({
    tenantId,
    // Request a larger limit so we can paginate locally
    filters: { limit: 10, offset: 0 }
  });

  let applications = data?.garbageAccounts || data?.GarbageApplications || data?.data || [];

  if (appliedSearch) {
    applications = applications.filter((app) => {
      const appNo = app?.grbgApplication?.applicationNo || app?.applicationNo || app?.grbgApplicationNumber || "";
      return appNo.toLowerCase().includes(appliedSearch.toLowerCase());
    });
  }

  const totalRecords = applications.length;
  const paginatedApplications = applications.slice(pageOffset, pageOffset + pageSize);

  const tableData = React.useMemo(() => paginatedApplications, [paginatedApplications]);

  const columns = React.useMemo(() => {
    return [
      {
        Header: t("GC_APPLICATION_NUMBER_LABEL"),
        accessor: "applicationNo",
        disableSortBy: true,
        Cell: ({ row }) => {
          const appNo = row.original?.grbgApplication?.applicationNo || row.original?.applicationNo;
          return (
            <span className="link">
              <Link to={`/upyog-ui/employee/gc/application-details/${encodeURIComponent(appNo)}`}>
                {appNo || t("CS_NA")}
              </Link>
            </span>
          );
        },
      },
      {
        Header: t("GC_APPLICANT_NAME"),
        accessor: "name",
        disableSortBy: true,
        Cell: ({ row }) => {
          const owners = row.original?.applicantDetails || [];
          const name = owners?.map(o => o?.name || o?.applicantName)?.join(", ") || row.original?.name || row.original?.garbageSpecification?.name;
          return <span className="cell-text">{name || t("CS_NA")}</span>;
        }
      },
      {
        Header: t("GC_MOBILE_NUMBER"),
        accessor: "mobileNumber",
        disableSortBy: true,
        Cell: ({ row }) => {
          const owners = row.original?.applicantDetails || [];
          const mobile = owners?.map(o => o?.mobileNumber)?.join(", ") || row.original?.mobileNumber || row.original?.garbageSpecification?.phoneNumber;
          return <span className="cell-text">{mobile || t("CS_NA")}</span>;
        }
      },
      {
        Header: t("GC_APPLICATION_STATUS_LABEL"),
        accessor: "status",
        disableSortBy: true,
        Cell: ({ row }) => {
          const status = row.original?.applicationStatus || row.original?.status || row.original?.grbgApplication?.status;
          return <span className="cell-text">{status ? t(`GC_STATUS_${status}`) : t("CS_NA")}</span>;
        }
      }
    ];
  }, [t]);

  const onSubmit = (formData) => {
    setAppliedSearch(formData?.applicationNo?.trim() || "");
    setPageOffset(0);
  };

  const clearSearch = () => {
    reset({ applicationNo: "" });
    setAppliedSearch("");
    setPageOffset(0);
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <Header>{t("ES_COMMON_INBOX")}</Header>
      <div className="inbox-search-wrapper">
        <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
          <SearchField>
            <label>{t("GC_APPLICATION_NUMBER_LABEL")}</label>
            <Controller 
              control={control} 
              name="applicationNo" 
              render={({ field }) => (
                <TextInput 
                  t={t}
                  name={field.name} 
                  value={field.value} 
                  onChange={field.onChange} 
                  onBlur={field.onBlur} 
                  inputRef={field.ref} 
                />
              )} 
            />
          </SearchField>
          <SearchField className="submit">
            <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
            <p className="link" style={{ marginTop: "10px", cursor: "pointer" }} onClick={clearSearch}>
              {t("ES_COMMON_CLEAR_ALL")}
            </p>
          </SearchField>
        </SearchForm>
      </div>
      
      <div style={{ marginTop: "24px" }}>
        {paginatedApplications.length > 0 ? (
          <Table 
            t={t} 
            data={tableData} 
            columns={columns} 
            getCellProps={(cellInfo) => ({ style: { minWidth: "150px", padding: "20px 18px", fontSize: "16px" } })} 
            currentPage={Math.floor(pageOffset / pageSize)}
            onNextPage={() => setPageOffset(pageOffset + pageSize)}
            onPrevPage={() => setPageOffset(pageOffset - pageSize)}
            pageSizeLimit={pageSize}
            onPageSizeChange={(e) => { setPageSize(Number(e.target.value)); setPageOffset(0); }}
            totalRecords={totalRecords}
            disableSort={true}
          />
        ) : (
          <Card style={{ marginTop: 20 }}>
            <div style={{ textAlign: "center", padding: "20px" }}>{t("GC_NO_APPLICATION_FOUND_MSG")}</div>
          </Card>
        )}
      </div>
    </React.Fragment>
  );
};

export default Inbox;