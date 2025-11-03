  import React, { useCallback, useMemo, useEffect} from "react"
  import { useForm, Controller } from "react-hook-form";
  import { TextInput, SubmitBar, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, Header } from "@nudmcdgnpm/digit-ui-react-components";
  import { Link} from "react-router-dom";
  import { APPLICATION_PATH} from "../utils";

  /**
 * `WTSearchApplication` component provides a search interface for Water Tanker (WT) bookings. 
 * It allows users to search, filter, and sort booking records based on parameters like booking number, 
 * applicant name, mobile number, status, and date range. The component uses `react-hook-form` for form management 
 * and displays the results in a table format. It supports pagination, sorting, and displays relevant status 
 * information for each booking. It also includes functionality for clearing the search form and handling 
 * the submission of search queries.
 * 
 * @param {Object} props - The component's props.
 * @param {string} props.tenantId - The tenant ID for the current session.
 * @param {boolean} props.isLoading - Indicates if data is currently being loaded.
 * @param {Function} props.t - Translation function for internationalization.
 * @param {Function} props.onSubmit - Callback function to handle form submission.
 * @param {Array} props.data - Data to be displayed in the table.
 * @param {number} props.count - Total number of records for pagination.
 * @param {Function} props.setShowToast - Function to control toast messages.
 */

  const WTSearchApplication = ({tenantId, isLoading, t, onSubmit, data, count, setShowToast, moduleCode }) => {
    
      const isMobile = window.Digit.Utils.browser.isMobile();
      const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
          defaultValues: {
              offset: 0,
              limit: !isMobile && 10,
              sortBy: "commencementDate",
              sortOrder: "DESC",
          }
      })
      useEffect(() => {
        register("offset", 0)
        register("limit", 10)
        register("sortBy", "commencementDate")
        register("sortOrder", "DESC")
      },[register])
      const user = Digit.UserService.getUser().info;
      const GetCell = (value) => <span className="cell-text">{value}</span>;
    
      const columns = useMemo( () => ([
          
          {
              Header: t("WT_BOOKING_NO"),
              accessor: "bookingNo",
              disableSortBy: true,
              Cell: ({ row }) => {
                return (
                  <div>
                    <span className="link">
                    {user.type === "CITIZEN" && (
                      <Link to={`${APPLICATION_PATH}/citizen/wt/bookingsearch/booking-details/${row.original["bookingNo"]}`}>
                        {row.original["bookingNo"]}
                      </Link>
                    )}
                     {user.type === "EMPLOYEE" && (
                      <Link to={`${APPLICATION_PATH}/employee/wt/bookingsearch/booking-details/${row.original["bookingNo"]}`}>
                        {row.original["bookingNo"]}
                      </Link>
                    )}
                    </span>
                  </div>
                );
              },
            },
          

            {
              Header: t("WT_APPLICANT_NAME"),
              Cell: ( row ) => {
                return GetCell(`${row?.row?.original?.applicantDetail?.["name"]}`)
                
              },
              disableSortBy: true,
            },
            {
              Header: t("WT_MOBILE_NUMBER"),
              Cell: ( row ) => {
                return GetCell(`${row?.row?.original?.applicantDetail?.["mobileNumber"]}`)
                
              },
              disableSortBy: true,
            },
            {
              Header: t("PT_COMMON_TABLE_COL_STATUS_LABEL"),
              Cell: ({ row }) => {
                return GetCell(`${t(row?.original["bookingStatus"])}`)
              },
              disableSortBy: true,
            },
            
        ]), [] )
        const statusOptions = [
          { i18nKey: "Booking Created", code: "BOOKING_CREATED", value: t("WT_BOOKING_CREATED") },
          { i18nKey: "Booking Approved", code: "APPROVED", value: t("WT_BOOKING_APPROVED") },
          { i18nKey: "Tanker Delivered", code: "TANKER_DELIVERED", value: t("WT_TANKER_DELIVERED") },
          { i18nKey: "Vendor Assigned", code: "ASSIGN_VENDOR", value: t("WT_ASSIGN_VENDOR") },
          { i18nKey: "Rejected", code: "REJECT", value: t("WT_BOOKING_REJECTED") }
        ];

        const statusOptionForTreePruning = [
          {
            i18nKey: "TP_BOOKING_CREATED",
            code: "BOOKING_CREATED",
            value: t("TP_BOOKING_CREATED")
          },
          {
            i18nKey: "TP_PENDING_FOR_APPROVAL",
            code: "PENDING_FOR_APPROVAL",
            value: t("TP_PENDING_FOR_APPROVAL")
          },
          {
            i18nKey: "TP_PAYMENT_PENDING",
            code: "PAYMENT_PENDING",
            value: t("TP_PAYMENT_PENDING")
          },
          {
            i18nKey: "TP_TEAM_ASSIGNMENT_FOR_VERIFICATION",
            code: "TEAM_ASSIGNMENT_FOR_VERIFICATION",
            value: t("TP_TEAM_ASSIGNMENT_FOR_VERIFICATION")
          },
          {
            i18nKey: "TP_TEAM_ASSIGNMENT_FOR_EXECUTION",
            code: "TEAM_ASSIGNMENT_FOR_EXECUTION",
            value: t("TP_TEAM_ASSIGNMENT_FOR_EXECUTION")
          },
          {
            i18nKey: "TP_TREE_PRUNING_SERVICE_COMPLETED",
            code: "TREE_PRUNING_SERVICE_COMPLETED",
            value: t("TP_TREE_PRUNING_SERVICE_COMPLETED")
          }
        ];
                
      const onSort = useCallback((args) => {
          if (args.length === 0) return
          setValue("sortBy", args.id)
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
                  
                  <div style={{ padding: user?.type === "CITIZEN" ? "0 24px 0 24px" : ""}}>
                  <Header>{t("WT_SEARCH_BOOKINGS")}</Header>
                  { user?.type === "EMPLOYEE" && (
                  <Card className={"card-search-heading"}>
                      <span style={{color:"#505A5F"}}>{t("PROVIDE_ATLEAST_ONE_PARAMETERS")}</span>
                  </Card>
                  )}
                  { user?.type === "CITIZEN" && (
                      <span style={{color:"#505A5F", marginBottom:"10px"}}>{t("PROVIDE_ATLEAST_ONE_PARAMETERS")}</span>
                  )}

                  <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                  <SearchField>
                      <label>{t("WT_BOOKING_NO")}</label>
                      <TextInput name="bookingNo" inputRef={register({})} />
                  </SearchField>
                  <SearchField>
                      <label>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</label>
                      <Controller
                              control={control}
                              name="status"
                              render={(props) => (
                                  <Dropdown
                                  selected={props.value}
                                  select={props.onChange}
                                  onBlur={props.onBlur}
                                  option={moduleCode==="TP"? statusOptionForTreePruning : statusOptions}
                                  optionKey="i18nKey"
                                  t={t}
                                  disable={false}
                                  />
                                  
                              )}
                              />
                  </SearchField>
                  <SearchField>
                  <label>{t("WT_MOBILE_NUMBER")}</label>
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
                      //type: "tel",
                      message: t("CORE_COMMON_MOBILE_ERROR"),
                      },
                  })}
                  type="number"
                  componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
                  maxlength={10}
                  />
                  <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError>
                  </SearchField> 
                  <SearchField>
                      <label>{t("FROM_DATE")}</label>
                      <Controller
                          render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange}  max={new Date().toISOString().split('T')[0]}/>}
                          name="fromDate"
                          control={control}
                          />
                  </SearchField>
                  <SearchField>
                      <label>{t("TO_DATE")}</label>
                      <Controller
                          render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
                          name="toDate"
                          control={control}
                          />
                  </SearchField>
                  <SearchField></SearchField>
                  <SearchField className="submit">
                      <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
                      <p style={{marginTop:"10px"}}
                      onClick={() => {
                          reset({ 
                              bookingNo: "", 
                              communityHallCode: "",
                              fromDate: "", 
                              toDate: "",
                              mobileNumber:"",
                              status: "",
                              offset: 0,
                              limit: 10,
                              sortBy: "commencementDate",
                              sortOrder: "DESC"
                          });
                          setShowToast(null);
                          previousPage();
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
                      minWidth: cellInfo.column.Header === t("WT_INBOX_APPLICATION_NO") ? "240px" : "",
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

  export default WTSearchApplication;