  import React, { useCallback, useMemo, useEffect,useRef,useState } from "react"
  import { useForm, Controller } from "react-hook-form";
  import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@nudmcdgnpm/digit-ui-react-components";
  import { Link,  } from "react-router-dom";
  import CHBCancelBooking from "./CHBCancelBooking";

  /**
 * CHBSearchApplication Component
 * 
 * This component is responsible for rendering the search functionality for CHB (Community Hall Booking) applications.
 * It allows users to search for applications based on various parameters such as date range, status, and other filters.
 * 
 * Props:
 * - `tenantId`: The tenant ID for which the search is being performed.
 * - `isLoading`: Boolean indicating whether the data is being loaded.
 * - `t`: Translation function for internationalization.
 * - `onSubmit`: Callback function triggered when the search form is submitted.
 * - `data`: The search results data.
 * - `count`: The total count of search results.
 * - `setShowToast`: Function to manage the visibility and content of toast notifications.
 * 
 * State Variables:
 * - `bookingDetails`: State variable to store the details of a selected booking.
 * - `showModal`: State variable to manage the visibility of the modal for booking details.
 * 
 * Hooks:
 * - `useForm`: React Hook Form hook for managing form state and validation.
 * - `useEffect`: Used to register default form values and trigger the initial search on component mount.
 * - `Digit.Hooks.chb.useChbCreateAPI`: Custom hook to handle API calls for creating CHB applications.
 * - `Digit.Hooks.chb.useChbCommunityHalls`: Custom hook to fetch the list of community halls for the CHB module.
 * 
 * Logic:
 * - Initializes the search form with default values, including:
 *    - `offset`: Pagination offset (default is 0).
 *    - `limit`: Number of results per page (default is 10 for desktop).
 *    - `sortBy`: Field to sort the results by (default is "commencementDate").
 *    - `sortOrder`: Sort order (default is "DESC").
 *    - `fromDate`: Default start date for the search (one month ago).
 *    - `toDate`: Default end date for the search (today's date).
 *    - `status`: Default status filter (e.g., "Booked").
 * - Automatically registers form fields and triggers the initial search on component mount.
 * - Fetches the list of community halls using the `useChbCommunityHalls` hook.
 * 
 * Returns:
 * - A search form with fields for date range, status, and other filters.
 * - Displays search results in a table format with pagination and sorting options.
 * - Includes a modal for viewing or managing booking details.
 */
  const CHBSearchApplication = ({tenantId, isLoading, t, onSubmit, onClear, data, count, setShowToast }) => {
      const [venueTypes, setVenueTypes] = useState("");
      const [venueCode, setVenueCode] = useState("");
      const isMobile = window.Digit.Utils.browser.isMobile();

      const { data: venueLists } = Digit.Hooks.useEnabledMDMS(tenantId, "CHB", [{ name: "Venues" }],
      {
        select: (data) => {
          const formattedData = data?.["CHB"]?.["Venues"]
          return formattedData;
        },
      });

      const { data: venueNames } = Digit.Hooks.useEnabledMDMS(tenantId, "CHB", [{ name: `${venueTypes?.parentMasterType}` }],
      {
        select: (data) => {
          const formattedData = data?.["CHB"]?.[`${venueTypes?.parentMasterType}`]
          return formattedData;
        },
      });


      let venues = [];
      venueLists && venueLists.map((venue) => {
          venues.push({i18nKey: `${venue.code}`, code: `${venue.code}`, value: `${venue.name}`, parentMasterType:venue.parentMasterType});
      });

      let venuenames = [];
      venueNames && venueNames.map((venuename) => {
          venuenames.push({
            i18nKey: `${venuename.code}`, 
            code: `${venuename.code}`, 
            value: `${venuename.venueName}`, 
            venueId: `${venuename.venueId}`
          });
      });


      const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
          defaultValues: {
              bookingNo: "",
              venueType: "",
              venueCode: "",
              status: undefined,
              mobileNumber: "",
              fromDate: "",
              toDate: "",
              offset: 0,
              limit: !isMobile && 10,
              sortBy: "commencementDate",
              sortOrder: "DESC"
          }
      })
      useEffect(() => {
        register("offset")
        register("limit")
        register("sortBy")
        register("sortOrder")
      },[register])
      const [bookingDetails,setBookingDetails]=useState("");
      const [showModal,setShowModal] = useState(false)
      const mutation = Digit.Hooks.chb.useChbCreateAPI(tenantId, false);

      const { data: recieptData } = Digit.Hooks.useRecieptSearch(
        {
          tenantId: tenantId,
          businessService: "chb-services",
          consumerCodes: bookingDetails?.bookingNo,
          isEmployee: false,
        },
        { enabled: bookingDetails?.bookingNo ? true : false }
      );
      const paymentMode = recieptData?.Payments?.[0]?.paymentMode;

      // Refund status display for cancelled bookings (mirrors ApplicationDetails.js)
      const isCancelledBooking = bookingDetails?.bookingStatus === "CANCELLED";
      const isOnlinePayment = recieptData?.Payments?.[0]?.paymentMode === "ONLINE";
      const isRefunded = recieptData?.Payments?.[0]?.instrumentStatus === "REFUNDED";
      const originalTxnId = recieptData?.Payments?.[0]?.transactionNumber;

      const { data: refundData } = Digit.Hooks.useCustomAPIHook(
        "/pg-service/refund/v1/_search",
        {
          originalTxnId: originalTxnId,
          tenantId: recieptData?.Payments?.[0]?.tenantId || tenantId,
        },
        {},
        {},
        {
          enabled: !!(isCancelledBooking && isOnlinePayment && originalTxnId),
        }
      );

      const refund = refundData?.Refund?.[0] || refundData?.Refunds?.[0] || refundData?.[0];
      const refundStatus = refund?.status || refund?.refundStatus;
      const isRefundInProgress = refundStatus && (
        refundStatus.toUpperCase() === "IN_PROGRESS" ||
        refundStatus.toUpperCase() === "INPROGRESS" ||
        refundStatus.toUpperCase() === "INITIATED"
      );
      const isRefundSuccess = refundStatus && (
        refundStatus.toUpperCase() === "SUCCESS" ||
        refundStatus.toUpperCase() === "SUCCESSFUL" ||
        refundStatus.toUpperCase() === "COMPLETED"
      );
      const refundStatusColor = isRefundSuccess
        ? { bg: "#D4EDDA", border: "#C3E6CB", text: "#155724" }
        : isRefundInProgress
        ? { bg: "#FFF3CD", border: "#FFEBAA", text: "#856404" }
        : { bg: "#E2E3E5", border: "#D6D8DB", text: "#383D41" };

      
      const GetCell = (value) => <span className="cell-text">{value}</span>;
      const handleCancelBooking = async (data) => {
        setShowModal(false);
        const bookingData = bookingDetails;
        const updatedApplication = {
          ...bookingData,
          bookingStatus: "CANCELLED",
          additionalDetails: {
            ...bookingData?.additionalDetails,
            cancellationReason: data?.cancelReason || ""
          }
        };
        let refundFailed = false;
        let refundErrorMessage = "";
        try {
          const paymentDetails = recieptData?.Payments?.[0];
          if (paymentDetails && paymentDetails.paymentMode === "ONLINE") {
            try {
              const refundPayload = {
                PaymentWorkflows: [
                  {
                    paymentId: paymentDetails.id,
                    action: "REFUND",
                    tenantId: paymentDetails.tenantId || tenantId,
                    reason: data?.cancelReason || "Customer requested refund"
                  }
                ]
              };
              await Digit.ReceiptsService.update(refundPayload, paymentDetails.tenantId || tenantId, "CHB");
            } catch (refundError) {
              refundFailed = true;
              refundErrorMessage = refundError?.response?.data?.Errors?.[0]?.message || refundError?.message || "";
            }
          }

          await mutation.mutateAsync({
            hallsBookingApplication: updatedApplication
          });

          if (refundFailed) {
            setShowToast({
              key: "warning",
              error: {
                message: `${t("CHB_CANCELLATION_SUCCESS_BUT_REFUND_FAILED") || "Booking cancelled, but refund initiation failed"}${refundErrorMessage ? `: ${refundErrorMessage}` : ""}`
              }
            });
          } else {
            setShowToast({ key: "success", action: { action: "CANCEL" } });
          }
          handleSubmit(onSubmit)();
        } catch (err) {
          setShowToast({ key: "error", error: { message: err?.response?.data?.Errors?.[0]?.message || err?.message || t("CS_SOMETHING_WENT_WRONG") } });
        }
      }
      const handleViewRefundStatus = (rowData) => {
        setBookingDetails(rowData);
      };

      const columns = useMemo( () => ([
          
          {
              Header: t("CHB_BOOKING_NO"),
              accessor: "bookingNo",
              disableSortBy: true,
              Cell: ({ row }) => {
                return (
                  <div>
                    <span className="link">
                      <Link to={`/upyog-ui/employee/chb/applicationsearch/application-details/${row.original["bookingNo"]}`}>
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
              Header: t("CHB_VENUE_TYPE_LABEL"),
              Cell: ({ row }) => {
                return GetCell(`${t(row.original["venueType"])}`)
              },
              disableSortBy: true,
            
            },
            {
              Header: t("CHB_VENUE_NAME_LABEL"),
              Cell: ({ row }) => {
                return GetCell(`${t(row.original["venueCode"])}`)
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
              Header: t("CHB_REFUND_STATUS") || "Refund Status",
              disableSortBy: true,
              Cell: ({ row }) => {
                if (row?.original?.bookingStatus !== "CANCELLED") {
                  return GetCell("-");
                }
                const isSelected = bookingDetails?.bookingNo === row?.original?.bookingNo;
                return (
                  <span
                    onClick={() => handleViewRefundStatus(row?.original)}
                    style={{
                      color: "#a82227",
                      cursor: "pointer",
                      fontWeight: isSelected ? "700" : "500",
                      textDecoration: "underline",
                      fontSize: "14px",
                    }}
                  >
                    {isSelected && refundStatus
                      ? refundStatus
                      : t("CHB_VIEW_REFUND_STATUS") || "View Refund Status"}
                  </span>
                );
              },
            },
            
            {
              Header: t("CHB_ACTIONS"),
              Cell: ({ row }) => {
                const [isMenuOpen, setIsMenuOpen] = useState(false);
                const menuRef = useRef();
                const navigate = Digit.Hooks.useCustomNavigate(); // Initialize history

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
                  setShowModal(true);
                  setBookingDetails(row?.original);
                };
                const { data: slotSearchData, refetch } = Digit.Hooks.chb.useChbSlotSearch({
                  tenantId: application?.tenantId,
                  filters: {
                    bookingId:application?.bookingId,
                    venueType: application?.venueType,
                    bookingStartDate: application?.bookingSlotDetails?.[0]?.bookingDate,
                    bookingEndDate: application?.bookingSlotDetails?.[application.bookingSlotDetails.length - 1]?.bookingDate,
                    venueCode: application?.venueType,
                    isTimerRequired:true
                  },
                  enabled: false, // Disable automatic refetch
                });
                const handleMakePayment = async () => {
                  try {
                  const result = await refetch();
                  let SlotSearchData={
                    tenantId: application?.tenantId,
                    bookingId:application?.bookingId,
                    venueType: application?.venueType,
                    venueCode: application?.venueType,
                    bookingStartDate: application?.bookingSlotDetails?.[0]?.bookingDate,
                    bookingEndDate: application?.bookingSlotDetails?.[application.bookingSlotDetails.length - 1]?.bookingDate,
                    isTimerRequired:true
              
                  }
                  const isSlotBooked = result?.data?.hallSlotAvailabiltityDetails?.some(
                    (slot) => slot.slotStaus === "BOOKED"
                  );
              
                  if (isSlotBooked) {
                    setShowToast({ error: true, label: t("CHB_COMMUNITY_HALL_ALREADY_BOOKED") });
                  } else {
                    navigate(
                      `/upyog-ui/employee/payment/collect/${"chb-services"}/${application?.bookingNo}`,
                      {
                      state: { tenantId: application?.tenantId, bookingNo: application?.bookingNo,timerValue:result?.data.timerValue ,SlotSearchData:SlotSearchData },
                    });
                  }
                } catch (error) {
                  setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
                  }
                };
                return (
                  <div ref={menuRef}>
                    <React.Fragment>
                      <SubmitBar
                        label={t("WF_TAKE_ACTION")}
                        onSubmit={toggleMenu}
                        disabled={
                          !["BOOKED", "BOOKING_CREATED", "PAYMENT_FAILED", "PENDING_FOR_PAYMENT"].includes(application?.bookingStatus)
                        } // Disable button if bookingStatus is not one of the allowed values
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
                          {(application.bookingStatus === "BOOKING_CREATED" || application.bookingStatus === "PAYMENT_FAILED" || application.bookingStatus === "PENDING_FOR_PAYMENT") && (
                            <div
                              onClick={() => handleMakePayment()}
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
        ]), [bookingDetails, refundStatus] )

      const statusOptions = [
          { i18nKey: "Booked", code: "BOOKED", value: t("CHB_BOOKED") },
          { i18nKey: "Booking in Progress", code: "BOOKING_CREATED", value: t("CHB_BOOKING_IN_PROGRES") },
          { i18nKey: "Pending For Payment", code: "PENDING_FOR_PAYMENT", value: t("PENDING_FOR_PAYMENT") },
          { i18nKey: "Booking Expired", code: "EXPIRED", value: t("EXPIRED") },
          { i18nKey: "Cancelled", code: "CANCELLED", value: t("CANCELLED") }
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
          const currentOffset = getValues("offset");
          const limit = getValues("limit");
          setValue("offset", Math.max(0, currentOffset - limit)); // Prevent negative
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
                      <Controller
                        control={control}
                        name="bookingNo"
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
                      <label>{t("CHB_VENUE_TYPE_LABEL")}</label>
                      <Controller
                              control={control}
                              name="venueType"
                              render={({ field }) => (
                                  <Dropdown
                                  selected={field.value}
                                  select={(value) => {
                                    field.onChange(value);
                                    setVenueTypes(value);
                                  }}
                                  onBlur={field.onBlur}
                                  option={venues}
                                  optionKey="i18nKey"
                                  t={t}
                                  disable={false}
                                  />
                              )}
                              />
                  </SearchField>
                  <SearchField>
                      <label>{t("CHB_VENUE_NAME_LABEL")}</label>
                      <Controller
                              control={control}
                              name="venueCode"
                              render={({ field }) => (
                                  <Dropdown
                                  selected={field.value}
                                  select={(value) => {
                                    field.onChange(value);
                                    setVenueCode(value);
                                  }}
                                  onBlur={field.onBlur}
                                  option={venuenames}
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
                              render={({ field }) => (
                                  <Dropdown
                                  selected={field.value}
                                  select={field.onChange}
                                  onBlur={field.onBlur}
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
                      <label>{t("FROM_DATE")}</label>
                      <Controller
                          render={({ field }) => <DatePicker date={field.value} disabled={false} onChange={field.onChange}  max={new Date().toISOString().split('T')[0]}/>}
                          name="fromDate"
                          control={control}
                          />
                  </SearchField>
                  <SearchField>
                      <label>{t("TO_DATE")}</label>
                      <Controller
                          render={({ field }) => <DatePicker date={field.value} disabled={false} onChange={field.onChange} />}
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
                              venueType: "",
                              venueCode: "",
                              fromDate: "", 
                              toDate: "",
                              mobileNumber:"",
                              status: "",
                              offset: 0,
                              limit: 10,
                              sortBy: "commencementDate",
                              sortOrder: "DESC",
                          });
                          setShowToast(null);
                          setVenueTypes(""); // setting local state empty when click on clear
                          setVenueCode("");  
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
              {/* Refund status banner for selected cancelled bookings */}
              {isCancelledBooking && bookingDetails?.bookingNo && (
                <div
                  style={{
                    margin: "16px 0",
                    padding: "14px 20px",
                    backgroundColor: refundStatus ? refundStatusColor.bg : "#F8F9FA",
                    border: `1px solid ${refundStatus ? refundStatusColor.border : "#DEE2E6"}`,
                    borderRadius: "6px",
                    color: refundStatus ? refundStatusColor.text : "#6C757D",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    flexWrap: "wrap",
                    gap: "8px",
                  }}
                >
                  <div style={{ fontWeight: "600", fontSize: "15px" }}>
                    {t("CHB_REFUND_STATUS_FOR") || "Refund Status for"}{" "}
                    <span style={{ fontWeight: "700" }}>{bookingDetails.bookingNo}</span>
                  </div>
                  <div style={{ display: "flex", alignItems: "center", gap: "16px", flexWrap: "wrap" }}>
                    {refund?.refundId && (
                      <span style={{ fontSize: "14px" }}>
                        <strong>{t("CHB_REFUND_ID") || "Refund ID"}:</strong>{" "}{refund.refundId}
                      </span>
                    )}
                    {refund?.refundAmount && (
                      <span style={{ fontSize: "14px" }}>
                        <strong>{t("CHB_REFUND_AMOUNT") || "Amount"}:</strong>{" "}₹{refund.refundAmount}
                      </span>
                    )}
                    <span
                      style={{
                        fontSize: "14px",
                        fontWeight: "700",
                        padding: "3px 12px",
                        borderRadius: "12px",
                        backgroundColor: refundStatus ? refundStatusColor.border : "#DEE2E6",
                        color: refundStatus ? refundStatusColor.text : "#6C757D",
                      }}
                    >
                      {refundStatus
                        ? `${t("CHB_REFUND_STATUS") || "Refund Status"}: ${refundStatus}`
                        : isOnlinePayment
                        ? t("CHB_REFUND_NOT_INITIATED") || "No refund initiated yet"
                        : t("CHB_OFFLINE_PAYMENT_NO_REFUND") || "Offline payment — no online refund applicable"}
                    </span>
                    <span
                      onClick={() => setBookingDetails("")}
                      style={{ cursor: "pointer", fontWeight: "600", fontSize: "18px", lineHeight: 1, opacity: 0.6 }}
                      title="Dismiss"
                    >
                      ✕
                    </span>
                  </div>
                </div>
              )}

              {showModal && <CHBCancelBooking 
                t={t}
                //surveyTitle={surveyData.title}
                closeModal={() => setShowModal(false)}
                actionCancelLabel={"BACK"}
                actionCancelOnSubmit={() => setShowModal(false)}
                actionSaveLabel={"CHB_CANCEL"}
                actionSaveOnSubmit={handleCancelBooking}   
                onSubmit={handleCancelBooking} 
                paymentMode={paymentMode}
                >
            </CHBCancelBooking> }
          </React.Fragment>
  }

  export default CHBSearchApplication