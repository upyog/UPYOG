  import React, { useCallback, useMemo, useEffect,useRef,useState } from "react"
  import { useForm, Controller } from "react-hook-form";
  import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@nudmcdgnpm/digit-ui-react-components";
  import { Link,useHistory} from "react-router-dom";

  const CHBSearchApplication = ({tenantId, isLoading, t, onSubmit, data, count, setShowToast }) => {
    
      const isMobile = window.Digit.Utils.browser.isMobile();
      const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
          defaultValues: {
              offset: 0,
              limit: !isMobile && 10,
              sortBy: "commencementDate",
              sortOrder: "DESC",
              fromDate: new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().split('T')[0], // Default to one month ago
              toDate: new Date().toISOString().split('T')[0], // Default to today's date
              status: { i18nKey: "Booked", code: "BOOKED", value: t("CHB_BOOKED") }
          }
      })
      useEffect(() => {
        register("offset", 0)
        register("limit", 10)
        register("sortBy", "commencementDate")
        register("sortOrder", "DESC")
        handleSubmit(onSubmit)();
      },[register])
      

      const mutation = Digit.Hooks.chb.useChbCreateAPI(tenantId, false);
      const { data: Menu } = Digit.Hooks.chb.useChbCommunityHalls(tenantId, "CHB", "ChbCommunityHalls");
    
      let menu = [];

      

      Menu &&
    Menu.map((one) => {
      menu.push({ i18nKey: `${one.code}`, code: `${one.code}`, value: `${one.name}` });
    });

      const GetCell = (value) => <span className="cell-text">{value}</span>;
      
      const columns = useMemo( () => ([
          
          {
              Header: t("CHB_BOOKING_NO"),
              accessor: "bookingNo",
              disableSortBy: true,
              Cell: ({ row }) => {
                return (
                  <div>
                    <span className="link">
                      <Link to={`/digit-ui/employee/chb/applicationsearch/application-details/${row.original["bookingNo"]}`}>
                        {row.original["bookingNo"]}
                      </Link>
                    </span>
                  </div>
                );
              },
            },
          

            {
              Header: t("CHB_APPLICANT_NAME"),
              Cell: ( row ) => {
                return GetCell(`${row?.row?.original?.applicantDetail?.["applicantName"]}`)
                
              },
              disableSortBy: true,
            },
            {
              Header: t("CHB_COMMUNITY_HALL_NAME"),
              Cell: ({ row }) => {
                return GetCell(`${t(row.original["communityHallCode"])}`)
              },
              disableSortBy: true,
            
            },
            {
              Header: t("CHB_BOOKING_DATE"),
              Cell: ({ row }) => {
                return row?.original?.bookingSlotDetails.length > 1 
                ? GetCell(`${row?.original?.bookingSlotDetails[0]?.["bookingDate"]}` + " - " + `${row?.original?.bookingSlotDetails[row?.original?.bookingSlotDetails.length-1]?.["bookingDate"]}`) 
                : GetCell(`${row?.original?.bookingSlotDetails[0]?.["bookingDate"]}`);
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
            
            {
              Header: t("CHB_ACTIONS"),
              Cell: ({ row }) => {
                const [isMenuOpen, setIsMenuOpen] = useState(false);
                const menuRef = useRef();
                const history = useHistory(); // Initialize history

                const toggleMenu = () => {
                  setIsMenuOpen(!isMenuOpen);
                };

                const closeMenu = (e) => {
                  if (menuRef.current && !menuRef.current.contains(e.target)) {
                    setIsMenuOpen(false);
                  }
                };

                React.useEffect(() => {
                  document.addEventListener("mousedown", closeMenu);
                  return () => {
                    document.removeEventListener("mousedown", closeMenu);
                  };
                }, []);

                let application = row?.original;
                
                const handleCancel = async () => {
                  const updatedApplication = {
                    ...application,
                    bookingStatus: "CANCELLED"
                  };
                  await mutation.mutateAsync({
                    hallsBookingApplication: updatedApplication
                  });
                  setIsMenuOpen(false);
                  handleSubmit(onSubmit)();
                };

                const handleNavigate = (url) => {
                  history.push(url); // Use history.push for navigation
                };

                return (
                  <div ref={menuRef}>
                    <React.Fragment>
                      <SubmitBar
                        label={t("WF_TAKE_ACTION")}
                        onSubmit={toggleMenu}
                        disabled={
                          application?.bookingStatus === "CANCELLED" ||
                          application?.bookingStatus === "EXPIRED"
                        } // Disable button
                      />
                      {isMenuOpen && (
                        <div
                          style={{
                            position: 'absolute',
                            backgroundColor: 'white',
                            border: '1px solid #ccc',
                            borderRadius: '4px',
                            padding: '8px',
                            zIndex: 1000,
                          }}
                        >
                          {/* Action for Cancel */}
                          {application?.bookingStatus === "BOOKED" && (
                            <div
                              onClick={handleCancel}
                              style={{
                                display: 'block',
                                padding: '8px',
                                textDecoration: 'none',
                                color: 'black',
                                cursor: 'pointer',
                              }}
                            >
                              {t("CHB_CANCEL")}
                            </div>
                          )}
            
                          {/* Action for Collect Payment */}
                          {application?.bookingStatus !== "BOOKED" && (
                            <div
                              onClick={() => handleNavigate(`/digit-ui/employee/payment/collect/chb-services/${row?.original?.bookingNo}`)}
                              style={{
                                display: 'block',
                                padding: '8px',
                                textDecoration: 'none',
                                color: 'black',
                                cursor: 'pointer',
                              }}
                            >
                              {t("CHB_COLLECT_PAYMENT")}
                            </div>
                          )}
                        </div>
                      )}
                    </React.Fragment>
                  </div>
                );
              },
            }
        ]), [] )
        const statusOptions = [
          { i18nKey: "Booked", code: "BOOKED", value: t("CHB_BOOKED") },
          { i18nKey: "Booking in Progres", code: "BOOKING_CREATED", value: t("CHB_BOOKING_IN_PROGRES") }
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
                  
                  <div>
                  <Header>{t("CHB_SEARCH_BOOKINGS")}</Header>
                  < Card className={"card-search-heading"}>
                      <span style={{color:"#505A5F"}}>{t("Provide at least one parameter to search for an application")}</span>
                  </Card>
                  <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                  <SearchField>
                      <label>{t("CHB_BOOKING_NO")}</label>
                      <TextInput name="bookingNo" inputRef={register({})} />
                  </SearchField>
                  <SearchField>
                      <label>{t("CHB_COMMUNITY_HALL_NAME")}</label>
                      <Controller
                              control={control}
                              name="communityHallCode"
                              render={(props) => (

                                  <Dropdown
                                  selected={props.value}
                                  select={props.onChange}
                                  onBlur={props.onBlur}
                                  option={menu}
                                  optionKey="i18nKey"
                                  t={t}
                                  disable={false}
                                  />
                                  
                              )}
                              />
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
                                  option={statusOptions}
                                  optionKey="i18nKey"
                                  t={t}
                                  disable={false}
                                  />
                                  
                              )}
                              />
                  </SearchField>
                  <SearchField>
                  <label>{t("CHB_MOBILE_NUMBER")}</label>
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
                  //maxlength={10}
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
                      minWidth: cellInfo.column.Header === t("CHB_INBOX_APPLICATION_NO") ? "240px" : "",
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

  export default CHBSearchApplication