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

const VendorSearchApplication = ({ tenantId, isLoading, t, onSubmit, data, count, setShowToast, vendorData }) => {
  const isMobile = window.Digit.Utils.browser.isMobile();

  const todaydate = new Date();
  const today = todaydate.toISOString().split("T")[0];
  const fromDate = new Date(todaydate);
  fromDate.setDate(todaydate.getDate() - 7);
  const fromDateFormatted = fromDate.toISOString().split("T")[0];
  const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
    defaultValues: {
      offset: 0,
      limit: !isMobile && 10,
      sortBy: "commencementDate",
      sortOrder: "DESC",
      fromDate: fromDateFormatted,
      toDate: today,
    },
  });
  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    register("sortBy", "commencementDate");
    register("sortOrder", "DESC");
    setValue("fromDate", fromDateFormatted);
    setValue("toDate", today);
  }, [register, handleSubmit, setValue, today, fromDateFormatted],);

  
  //Getting Vendor Data from SearchApp through props
  useEffect(() => {
    console.log("Fetched Vendor Data:");
    if (vendorData?.vendor?.length) {
      const vendorNames = vendorData.vendor.map((vendor) => vendor.name);
      console.log("Vendor Names:", vendorNames);
    }
  }, [vendorData]);


 
  const GetCell = (value) => <span className="cell-text">{value}</span>;
 

  const columns = useMemo(
    () => [
      {
        Header: t("VENDOR_REGISTRATION_NO"),
        accessor: "requestId",
        disableSortBy: true,
        Cell: ({ row }) => {
          const registrationNo = row.original.vendorAdditionalDetails?.registrationNo;
          return <div>{registrationNo || "N/A"}</div>;
        },
      },

      {
        Header: t("CONTACT_PERSON_NAME"),
        disableSortBy: true,
        Cell: ({ row }) => {
          const vendorName = row.original.vendorAdditionalDetails?.contactPerson;
          return <div>{vendorName || "N/A"}</div>;
        },
      },

      {
        Header: t("VENDOR_MOBILE_NUMBER"),
        disableSortBy: true,
        Cell: ({ row }) => {
          const vendorMobileNumber = row.original.vendorAdditionalDetails?.vendorMobileNumber;
          return <div>{vendorMobileNumber || "N/A"}</div>;
        },
      },

      {
        Header: t("VENDOR_BANK_NAME"),
        disableSortBy: true,
        Cell: ({ row }) => {
          const vendorBank = row.original.vendorAdditionalDetails?.bank;
          return <div>{vendorBank || "N/A"}</div>;
        },
      },

      {
        Header: t("VENDOR_STATUS"),
        disableSortBy: true,
        Cell: ({ row }) => {
          const vendorStatus = row.original.vendorAdditionalDetails?.status;
          return <div>{vendorStatus || "N/A"}</div>;
        },
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
        <Header>{t("VENDOR_COMMON_SEARCH_ADDITIONAL_DETAILS")}</Header>
        <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
          <SearchField>
            <label>{t("VENDOR_NAME")}</label>
            <Controller
              control={control}
              name="vendorName"
              render={(field) => (

                  <Dropdown
                    selected={field.value || ""}
                    select={field.onChange} 
                    onBlur={field.onBlur} 
                    option={
                      vendorData?.vendor?.map((vendor) => ({
                        name: vendor.name,
                        i18nKey: vendor.name,
                      })) || []
                    } // Default to an empty array
                    optionKey="i18nKey"
                    t={t}
                    disable={false}
                  />
                
              )}
            />
          </SearchField>

          <SearchField>
            <label>{t("VENDOR_MOBILE_NO")}</label>
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
            <label>{t("VENDOR_FROM_DATE")}</label>
            <Controller
              render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
              name="fromDate"
              control={control}
            />
          </SearchField>
          <SearchField>
            <label>{t("VENDOR_TO_DATE")}</label>
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
                  fromDate: fromDateFormatted,
                  toDate: "",
                  mobileNumber: "",
                  vendorName: "",
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

export default VendorSearchApplication;
