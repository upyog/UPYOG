  /**
 * @description 
 * This component handles the **Fire Noc Search** functionality.
 * It provides a form with multiple fields for filtering applications based on:
 * 
 * @props 
 * - `tenantId` (string): The ID of the current tenant.
 * - `isLoading` (boolean): Flag indicating whether data is loading.
 * - `t` (function): Translation function for multilingual support.
 * - `onSubmit` (function): Callback function to submit the form.
 * - `data` (object): Search results data.
 * - `count` (number): Total records count.
 * - `setShowToast` (function): Function to manage toast notifications.
 */

  import React, { useCallback, useMemo, useEffect } from "react"
  import { useForm, Controller } from "react-hook-form";
  import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@nudmcdgnpm/digit-ui-react-components";
  import { Link } from "react-router-dom";

  const FireNocSearchApplications = ({tenantId, isLoading, t, onSubmit, onClear, data, count, setShowToast }) => {
    const stateId = Digit.ULBService.getStateId();
      const isMobile = window.Digit.Utils.browser.isMobile();
      const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
          defaultValues: {
              applicationNumber: "",
            //   applicationType: "",
              mobileNumber: "",
              fromDate: "",
              toDate: "",
              offset: 0,
              limit: !isMobile && 10,
          }
      })
      useEffect(() => {
        register("offset")
        register("limit")
        register("sortOrder")
      },[register])
      
    //   const applicationType = [
    //       {
    //           code: "PROVISIONAL",
    //           i18nKey: "PROVISIONAL"
    //       },
    //       {
    //           code: "NEW",
    //           i18nKey: "NEW"
    //       }
    //   ]     
      const GetCell = (value) => <span className="cell-text">{value}</span>;
      
      const columns = useMemo( () => ([
          {
              Header: t("FN_APPLICATION_NUMBER_LABEL"),
              accessor: "applicationNumber",
              disableSortBy: true,
              Cell: ({ row }) => {
                return (
                  <div>
                    <span className="link">
                      <Link to={`/upyog-ui/employee/noc/firenoc/application-overview/${row.original["applicationNumber"]}`}>
                        {row.original["applicationNumber"]}
                      </Link>
                    </span>
                  </div>
                );
              },
            },
            {
              Header: t("FN_APPLICANT_NAME"),
              Cell: ({ row }) => {
                return GetCell(`${row?.original?.fireNOCDetails?.applicantDetails?.owners?.[0]?.["name"]}`)
                
              },
              disableSortBy: true,
            },
            {
              Header: t("FN_MOBILE_NUMBER"),
              Cell: ({ row }) => {
                return GetCell(`${row?.original?.fireNOCDetails?.applicantDetails?.owners?.[0]?.["mobileNumber"]}`)
              },
              disableSortBy: true,
            
            },
            {
              Header: t("FN_BUILDING_USAGE_TYPE"),
              Cell: ({ row }) => {
                return GetCell(t(`${row?.original?.fireNOCDetails?.buildings?.[0]?.["usageType"]}`))
              },
              disableSortBy: true,
            },
            {
              Header: t("FN_NOC_TYPE"),
              Cell: ({ row }) => {
                return GetCell(t(`${row?.original?.fireNOCDetails?.["fireNOCType"]}`))
              },
              disableSortBy: true,
            },
            {
              Header: t("FN_STATUS"),
              Cell: ({ row }) => {
                return GetCell(t(`${row?.original?.fireNOCDetails?.["status"]}`))
              },
              disableSortBy: true,
            },
        ]), [] )

      const onSort = useCallback((args) => {
          if (args.length === 0) return
          setValue("sortOrder", args.desc ? "DESC" : "ASC")
      }, [])

      function onPageSizeChange(e){
          setValue("limit",Number(e.target.value))
          handleSubmit(onSubmit)()
      }

      function nextPage () {
          setValue("offset", getValues("offset") + getValues("limit"))
          handleSubmit(onSubmit)()
      }
      function previousPage () {
          setValue("offset", getValues("offset") - getValues("limit") )
          handleSubmit(onSubmit)()
      }
      let validation={}

      return <React.Fragment>
                  
                  <div>
                  <Header>{t("FN_SEARCH_APPLICATIONS")}</Header>
                  <Card className={"card-search-heading"}>
                      <span style={{color:"#505A5F"}}>{t("Provide at least one parameter to search for an application")}</span>
                  </Card>
                  <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                  <SearchField>
                      <label>{t("FN_APPLICATION_NUMBER_LABEL")}</label>
                      <Controller
                          control={control}
                          name="applicationNumber"
                          render={({ field }) => (
                              <TextInput
                                  name={field.name}
                                  value={field.value}
                                  onChange={field.onChange}
                                  onBlur={field.onBlur}
                                  inputRef={field.ref}
                              />
                          )}
                      />
                  </SearchField>
                  <SearchField>
                  <label>{t("FN_MOBILE_NUMBER")}</label>
                  <Controller
                      control={control}
                      name="mobileNumber"
                      rules={{
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
                      }}
                      render={({ field }) => (
                          <MobileNumber
                              name={field.name}
                              value={field.value}
                              onChange={field.onChange}
                              onBlur={field.onBlur}
                              inputRef={field.ref}
                          />
                      )}
                  />
                  <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError>
                  </SearchField> 
                  <SearchField>
                      <label>{t("FN_FROM_DATE")}</label>
                      <Controller
                          render={({ field }) => <DatePicker date={field.value} disabled={false} onChange={field.onChange} />}
                          name="fromDate"
                          control={control}
                          />
                  </SearchField>
                  <SearchField>
                      <label>{t("FN_TO_DATE")}</label>
                      <Controller
                          render={({ field }) => <DatePicker date={field.value} disabled={false} onChange={field.onChange} />}
                          name="toDate"
                          control={control}
                          />
                  </SearchField>
                  <SearchField className="submit">
                      <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
                      <p style={{marginTop:"10px"}}
                      onClick={() => {
                          reset({ 
                              applicationNumber: "", 
                              fromDate: "", 
                              toDate: "",
                              mobileNumber:"",
                              offset: 0,
                              limit: 10,
                              sortOrder: "DESC"
                          });
                          setShowToast(null);
                          onClear();
                      }}>{t(`ES_COMMON_CLEAR_ALL`)}</p>
                  </SearchField>
              </SearchForm>
              {!isLoading && data?.display ? <Card style={{ marginTop: 20 }}>
                  {
                  t(data.display)
                      .split("\\n")
                      .map((text, index) => (
                      <p key={index} style={{ textAlign: "center" }}>
                          {text}
                      </p>
                      ))
                  }
              </Card>
              :(!isLoading && data !== ""? <Table
                  t={t}
                  data={data}
                  totalRecords={count}
                  columns={columns}
                  getCellProps={(cellInfo) => {
                  return {
                      style: {
                      minWidth: cellInfo.column.Header === t("FN_INBOX_APPLICATION_NO") ? "240px" : "",
                      padding: "20px 18px",
                      fontSize: "16px"
                    },
                  };
                  }}
                  onPageSizeChange={onPageSizeChange}
                  currentPage={getValues("offset")/getValues("limit")}
                  onNextPage={nextPage}
                  onPrevPage={previousPage}
                  pageSizeLimit={getValues("limit")}
                  onSort={onSort}
                  disableSort={false}
                  sortParams={[{id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false}]}
              />: data !== "" || isLoading && <Loader/>)}
              </div>
          </React.Fragment>
  }

  export default FireNocSearchApplications