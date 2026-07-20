import React, { useCallback, useMemo, useEffect, useRef, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@nudmcdgnpm/digit-ui-react-components";
import { Link  } from "react-router-dom";
import ADSCancelBooking from "./ADSCancelBooking";
import { getSlotSearchCriteria } from "../utils";

/** 
 * ADSSearchApplication is used for searching ADS bookings and displaying results in a table format.
 * It provides search filters like booking number,status, mobile number, and date range.
 * The component also includes ability to view details or cancel bookings.
 */
const ADSSearchApplication = ({tenantId, isLoading, t, onSubmit, onClear, data, count, setShowToast }) => {
  
    const isMobile = window.Digit.Utils.browser.isMobile();
    const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
        defaultValues: {
            bookingNo: "",
            applicantName: "",
            faceArea: undefined,
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
    const mutation = Digit.Hooks.ads.useADSCreateAPI(tenantId, false);
    // to do 

  const {
    data: Face
  } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "Advertisement", [{
    name: "FaceArea"
  }], {
    select: data => {
      const formattedData = data?.["Advertisement"]?.["FaceArea"].map(details => {
        return {
          i18nKey: `${details.name}`,
          code: `${details.code}`,
          name: `${details.name}`,
          active: `${details.active}`
        };
      });
      return formattedData;
    }
  });
  let face = [];
  Face && Face.map(one => {
    face.push({
      i18nKey: `${one.code}`,
      code: `${one.code}`,
      value: `${one.name}`
    });
  });
  const GetCell = value => <span className="cell-text">{value}</span>;
  const handleCancelBooking = async () => {
    setShowModal(false);
    const updatedApplication = {
      ...bookingDetails,
      bookingStatus: "CANCELLED"
    };
    await mutation.mutateAsync({
      bookingApplication: updatedApplication
    });
    handleSubmit(onSubmit)();
  };
  const columns = useMemo(() => [{
    Header: t("ADS_BOOKING_NO"),
    accessor: "bookingNo",
    disableSortBy: true,
    Cell: ({
      row
    }) => {
      return <div>
                  <span className="link">
                    <Link to={`/upyog-ui/employee/ads/applicationsearch/application-details/${row.original["bookingNo"]}`}>
                      {row.original["bookingNo"]}
                    </Link>
                  </span>
                </div>;
    }
  }, {
    Header: t("ADS_APPLICANT_NAME"),
    Cell: row => {
      return GetCell(`${row?.row?.original?.applicantDetail?.["applicantName"]}`);
    },
    disableSortBy: true
  }, {
    Header: t("ADS_LOCALITY"),
    Cell: ({
      row
    }) => {
      const cartDetails = row?.original?.cartDetails;
      // Map over cartDetails to extract all locations
      const locations = cartDetails?.map(detail => detail?.location).join(", ");

              // Return the mapped locations using GetCell
              return GetCell(locations);
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
            Header: t("ADS_ACTIONS"),
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
                const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();
                let formdata = {
                  advertisementSlotSearchCriteria: getSlotSearchCriteria(application?.cartDetails, tenantId, {}, undefined, application?.bookingId)
                };
                const handleMakePayment = async () => {
                  try {
                    /* Await the mutation and capture the result directly */
                    const result = await slotSearchData.mutateAsync(formdata);
                    let SlotSearchData={
                      bookingId:application?.bookingId,
                      tenantId: tenantId,
                      cartDetails:application?.cartDetails,
                    };
                    const isSlotBooked = result?.advertisementSlotAvailabiltityDetails?.some((slot) => slot.slotStaus === "BOOKED");
                    /* timerValue is resolved directly from top-level of response payload per backend contract */
                    const timerValue = result?.timerValue;

                    if (isSlotBooked) {
                      setShowToast({ error: true, label: t("ADS_ADVERTISEMENT_ALREADY_BOOKED") });
                    } else {
                      navigate(
                        `/upyog-ui/employee/payment/collect/${"adv-services"}/${application?.bookingNo}`,
                        { state: { tenantId: application?.tenantId, bookingNo: application?.bookingNo, timerValue:timerValue, SlotSearchData:SlotSearchData } }
                      );
                    }
                } catch (error) {
                  setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
                }
                };
              return (
                <div ref={menuRef}>
                  <React.Fragment>
                    <SubmitBar label={t("WF_TAKE_ACTION")} onSubmit={toggleMenu} disabled={application?.bookingStatus === "CANCELLED" || application?.bookingStatus === "EXPIRED"} // Disable button
          />
                    {isMenuOpen && <div className="ads-auto-44">
                        {/* Action for Cancel */}
                        {application?.bookingStatus === "BOOKED" && <div onClick={handleCancel} className="ads-auto-45">
                            {t("ADS_CANCEL")}
                          </div>}
          
                        {/* Action for Collect Payment */}
                        {application?.bookingStatus !== "BOOKED" && <div onClick={() => handleMakePayment()} className="ads-auto-46">
                            {t("ADS_COLLECT_PAYMENT")}
                          </div>}
                      </div>}
                  </React.Fragment>
                </div>
              );
            },
          }
      ], [])
      const statusOptions = [
        { i18nKey: "Booked", code: "BOOKED", value: t("ADS_BOOKED") },
        { i18nKey: "Booking in Progress", code: "BOOKING_CREATED", value: t("ADS_BOOKING_IN_PROGRES") },
        { i18nKey: "Pending For Payment", code: "PENDING_FOR_PAYMENT", value: t("PENDING_FOR_PAYMENT") },
        { i18nKey: "Booking Expired", code: "BOOKING_EXPIRED", value: t("BOOKING_EXPIRED") },
        { i18nKey: "Cancelled", code: "CANCELLED", value: t("ADS_CANCELLED") }
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
          setValue(
            "offset",
            Math.max(0, getValues("offset") - getValues("limit"))
          );
          handleSubmit(onSubmit)();
        }
        let validation = {}

    return <React.Fragment>
                
                <div>
                <Header>{t("ADS_SEARCH_BOOKINGS")}</Header>
                <Card className={"card-search-heading"}>
                    <span className="ads-auto-47">{t("Provide at least one parameter to search for an application")}</span>
                </Card>
                <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                <SearchField>
                    <label>{t("ADS_BOOKING_NO")}</label>
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
                    <label>{t("ADS_APPLICANT_NAME")}</label>
                    <Controller
                        control={control}
                        name="applicantName"
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
                    <label>{t("ADS_FACE_AREA")}</label>
                    <Controller
                            control={control}
                            name="faceArea"
                            render={({ field }) => (
                                <Dropdown
                                selected={field.value}
                                select={field.onChange}
                                onBlur={field.onBlur}
                                option={face}
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
                <label>{t("ADS_MOBILE_NUMBER")}</label>
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
                <SearchField className="submit">
              {/** to do */}
                    <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
                    <p onClick={() => {
            reset({
              bookingNo: "",
              applicantName: "",
              faceArea: "",
              fromDate: "",
              toDate: "",
              mobileNumber: "",
              status: "",
              offset: 0,
              limit: 10,
              sortBy: "commencementDate",
              sortOrder: "DESC"
            });
            setShowToast(null);
            onClear();
          }} className="ads-auto-48">{t(`ES_COMMON_CLEAR_ALL`)}</p>
                </SearchField>
            </SearchForm>
            {!isLoading && data?.display ? <Card className="ads-auto-49">
                {t(data.display).split("\\n").map((text, index) => <p key={index} className="ads-auto-50">
                        {text}
                    </p>)}
            </Card> : !isLoading && data !== "" ? <Table t={t} data={data} totalRecords={count} columns={columns} getCellProps={cellInfo => {
        return {
          style: {
            minWidth: cellInfo.column.Header === t("ADS_INBOX_APPLICATION_NO") ? "240px" : "",
            padding: "20px 18px",
            fontSize: "16px"
          }
        };
      }} onPageSizeChange={onPageSizeChange} currentPage={getValues("offset") / getValues("limit")} onNextPage={nextPage} onPrevPage={previousPage} pageSizeLimit={getValues("limit")} onSort={onSort} disableSort={false} sortParams={[{
        id: getValues("sortBy"),
        desc: getValues("sortOrder") === "DESC" ? true : false
      }]} /> : data !== "" || isLoading && <Loader />}
            </div>
            {showModal && <ADSCancelBooking t={t}
    //surveyTitle={surveyData.title}
    closeModal={() => setShowModal(false)} actionCancelLabel={"BACK"} actionCancelOnSubmit={() => setShowModal(false)} actionSaveLabel={"ADS_CANCEL"} actionSaveOnSubmit={handleCancelBooking} onSubmit={handleCancelBooking}>
          </ADSCancelBooking>}
        </React.Fragment>;
};
export default ADSSearchApplication;
