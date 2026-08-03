import React, {useEffect, useState} from "react";
import { FormStep, CardHeader, CardLabel, Dropdown, SubmitBar, Calender, Toast, InfoIcon, Card} from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import BookingPopup from "../components/BookingPopup";
import VenueDetails from "../components/VenueDetails";
import DateRangeFilter from "../components/DateRangeFilter";
import {format} from "date-fns";
import ApplicationTable from "../components/inbox/ApplicationTable";

/**
 * VenueSearch Component
 *
 * This component is responsible for searching venue availability, displaying
 * available booking slots, allowing users to select one or more consecutive
 * slots, and initiating the Community Hall Booking flow.
 *
 * ---------------------------------------------------------------------------
 * Component Update Date: 16-Jul-2026
 * ---------------------------------------------------------------------------
 *
 * Issue:
 * Previously, when a user searched for a date range (e.g. 17-Jul to 19-Jul)
 * but selected only a subset of dates (e.g. only 17-Jul), the original search
 * date range was being carried throughout the booking flow. As a result:
 *
 * - TimerValues was validating the entire searched date range instead of the
 *   selected booking dates.
 * - CHBCitizenDetails received the original search dates.
 * - The final booking payload sent bookingStartDate and bookingEndDate based
 *   on the search filters instead of the user's actual selected dates.
 *
 * Root Cause:
 * Updating the shared `searchData` state during booking triggered the
 * `useEffect([searchData])`, which re-fetched slot availability and cleared
 * `bookingSlotDetails`, resulting in incorrect booking data.
 *
 * Fix Implemented:
 * - Preserved the original `searchData` as the search filter state.
 * - Created an `updatedSearchData` object at booking time using only the
 *   selected booking dates.
 * - Passed the updated search data through the booking flow via the modal
 *   instead of mutating the original search state.
 * - Updated `goNext()` to accept a searchData parameter so subsequent screens
 *   (Citizen Details, TimerValues, Booking Payload, etc.) receive the selected
 *   booking dates rather than the searched date range.
 */

const VenueSearch = ({ t, config, onSelect, userType, formData }) => {
    const { control } = useForm();
    const isMobile = window.Digit.Utils.browser.isMobile();
    const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
    const [venueTypes, setVenueTypes] = useState(formData?.slotlist?.venueTypes || "");
    const [venueName, setVenueNames] = useState(formData?.slotlist?.venueName || "");
    const [venueCode, setVenueCode] = useState(formData?.slotlist?.venueCode || "");
    const [bookingDate, setBookingDate] = useState(formData?.slotlist?.bookingDate || null);
    const [searchData, setSearchData] = useState(formData?.slotlist?.searchData || {})
    const [bookingSlotDetails, setBookingSlotDetails] = useState(formData?.slotlist?.bookingSlotDetails || []);
    const [existingDataSet, setExistingDataSet] = useState("");
    const [slots, setSlots] = useState(formData?.slotlist?.slots ||"")
    const [isExistingPopupRequired,setIsExistingPopupRequired] = useState(true);
    const [showModal,setShowModal] = useState(null);
    const [showToast, setShowToast] = useState(null);
    const [showTable, setShowTable] = useState(false);
    const [showDetails, setShowDetails] = useState(false);
    const [isCheckboxSelected, setIsCheckboxSelected] = useState(false);
    const [data, setData] = useState([]);

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

    const { data: venueCodes } = Digit.Hooks.useEnabledMDMS(tenantId, "CHB", [{name: venueName?.childMasterCode, filter: "[?(@.venueCode=='" + venueName?.code + "')]"}],
    {
      select: (data) => data?.CHB?.[venueName?.childMasterCode],
    }
  );



    let venues = [];
    venueLists && venueLists.map((venue) => {
        venues.push({i18nKey: `${venue.code}`, code: `${venue.code}`, value: `${venue.name}`, timeSlots: venue.timeSlot, parentMasterType:venue.parentMasterType});
    });

    let venuenames = [];
    venueNames && venueNames.map((venuename) => {
        venuenames.push({
          i18nKey: `${venuename.code}`, 
          code: `${venuename.code}`, 
          value: `${venuename.venueName}`, 
          childMasterCode: `${venuename.childMasterCode}`,
          address: `${venuename.address}`,
          contactDetails: `${venuename.contactDetails}`,
          geoLocation: `${venuename.geoLocation}`,
          parkDescription: `${venuename.parkDescription}`,
          termsAndCondition: `${venuename.termsAndCondition}`,
          venueDescription: `${venuename.venueDescription}`,
          venueId: `${venuename.venueId}`
        });
    });

    let venuecode = [];
    venueCodes && venueCodes.map((venuecodes) => {
        venuecode.push({i18nKey: `${venuecodes.code}`, code: `${venuecodes.code}`, value: `${venuecodes.venueCode}`, timeSlot: venuecodes?.timeSlot });
    });

    const timeSlotOptions = venueCode?.timeSlot?.map(slot => ({
      ...slot,
      i18nKey: `${slot.from} - ${slot.to}`,
      code: slot.id,
    })) || [];



    const columns = [
    { Header: `${t("CHB_HALL_NAME")}` + "/" + `${t("CHB_PARK")}`, accessor: "name" },
    { Header: `${t("CHB_ADDRESS")}`, accessor: "address" },
    { Header: `${t("CHB_HALL_CODE")}`, accessor: "venueCode" },
    { Header: `${t("CHB_BOOKING_DATE")}`, accessor: "bookingDate" },
    { Header: `${t("CHB_STATUS")}`, accessor: "status" },
  ];


  // useEffect to close the toast automatically after 2 sec
    useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000);

      return () => clearTimeout(timer);
    }
  }, [showToast]);

  const handleViewReportClick = () => {
    if (venueName) {
      // Trigger the popup
      setShowDetails(true); 
    } else {
      // Show toast message
      setShowToast({ error: true, label: t("SELECT_VENUE") });
    }
  };

  const handleRowSelection = (rowIndex) => {
    setBookingSlotDetails((prevSelectedRows) => {
      const updatedSelectedRows = prevSelectedRows.some(row => row.slotId === data[rowIndex].slotId) ?
        prevSelectedRows.filter(row => row.slotId !== data[rowIndex].slotId) :
        [...prevSelectedRows, data[rowIndex]];
        const sortedSelectedRows = updatedSelectedRows.sort((a, b) => a.slotId - b.slotId);

        setIsCheckboxSelected(sortedSelectedRows.length > 0);
        return sortedSelectedRows;
    });
  };

  const goNext = (searchDataToUse = searchData) => {
      let allVenueDetails = formData.slotlist;
      let venueDetails;
        venueDetails = { ...allVenueDetails, bookingSlotDetails, venueTypes, venueName, venueCode, bookingDate,searchData: searchDataToUse,existingDataSet };
        onSelect(config.key, venueDetails, false);
    };

    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [bookingSlotDetails, venueTypes, venueName, venueCode, bookingDate, searchData]);

   const handleBookClick = () => {
    if (bookingSlotDetails.length > 0) {
      // Sort selected slots
      const sortedSlots = [...bookingSlotDetails].sort(
        (a, b) => a.slotId - b.slotId
      );

      // Check consecutive slots
      let isConsecutive = true;
      for (let i = 1; i < sortedSlots.length; i++) {
        if (sortedSlots[i].slotId !== sortedSlots[i - 1].slotId + 1) {
          isConsecutive = false;
          break;
        }
      }

      if (isConsecutive) {
        // Selected booking dates
        const selectedDates = sortedSlots
          .map((slot) => slot.bookingDate)
          .sort(
            (a, b) =>
              new Date(a.split("-").reverse().join("-")) -
              new Date(b.split("-").reverse().join("-"))
          );

        // Build updated search data (NO state update)
        const updatedSearchData = {
          ...searchData,
          bookingStartDate: format(
            new Date(selectedDates[0].split("-").reverse().join("-")),
            "yyyy-MM-dd"
          ),
          bookingEndDate: format(
            new Date(
              selectedDates[selectedDates.length - 1]
                .split("-")
                .reverse()
                .join("-")
            ),
            "yyyy-MM-dd"
          ),
        };

        if (isExistingPopupRequired) {
          setShowModal(updatedSearchData);
        } else {
          goNext(updatedSearchData);
        }
      } else {
        setShowToast({
          error: true,
          label: t("CHB_SELECT_CONSECUTIVE_SLOT"),
        });
      }
    } else {
      if (!isCheckboxSelected) {
        setShowToast({
          error: true,
          label: t("CHB_SELECT_AT_LEAST_ONE_SLOT"),
        });
      } else {
        if (isExistingPopupRequired) {
          setShowModal(searchData);
        } else {
          goNext(searchData);
        }
      }
    }
  };


  const handleSearch = () => {
    const selectedVenueName = venueName?.code || "";
    const startDate = bookingDate?.startDate ? format(new Date(bookingDate.startDate), "yyyy-MM-dd"): "";
    const endDate = bookingDate?.totalDays === 1 ? startDate : bookingDate?.endDate ? format(new Date(bookingDate.endDate), "yyyy-MM-dd") : "";
    const selectedVenueCode = venueCode?.code || "";

    if (selectedVenueName && startDate && endDate && selectedVenueCode) {
      const filters = {
        venueCode: selectedVenueName,
        bookingStartDate: startDate,
        bookingEndDate: endDate,
        hallAddress: venueName?.address,
        unitCode: selectedVenueCode,
        fromTime: slots?.from,
        toTime: slots?.to
      };

      setSearchData(filters);
    } else {
      setShowToast({ error: true, label: t("VENUE_SELECT_DATE_HALLCODE")});
    }
  };

  const {data: slotSearchData, refetch} = Digit.Hooks.chb.useChbSlotSearch({tenantId,
    filters: {
      venueCode: searchData?.venueCode,
      unitCode: searchData?.unitCode,
      bookingStartDate: searchData?.bookingStartDate,
      bookingEndDate: searchData?.bookingEndDate,
      fromTime: searchData?.fromTime,
      toTime: searchData?.toTime,
      isTimerRequired: false,
    },
  });

    useEffect(() => {
      if (slotSearchData?.hallSlotAvailabiltityDetails) {
          const newData =
            slotSearchData.hallSlotAvailabiltityDetails.map(
              (slot, index) => ({
                slotId: index + 1,
                name: venueName?.value,
                address: venueName?.address,
                venueCode: slot.code,
                bookingDate: slot.bookingDate,
                status:
                  slot.slotStaus === "AVAILABLE" ? (
                    <div className="sla-cell-success">
                      Available
                    </div>
                  ) : (
                    <div className="sla-cell-error">
                      Booked
                    </div>
                  ),
              })
            );

          setData(newData);
          setShowTable(true);
        }
      }, [slotSearchData]);

    useEffect(() => {
      if (searchData?.venueCode) {
        refetch();
        setBookingSlotDetails([]);
        setIsCheckboxSelected(false);
      }
    }, [searchData]);

  const checkboxColumn = {
    id: "selection",
    Header: ({ getToggleAllRowsSelectedProps }) => (
      <div style={{ paddingLeft: '50px' }}>
        <input
          type="checkbox"
          checked={bookingSlotDetails.length === data.length}
          disabled={data.every(row => row.status.props.children !== "Available")} 
          onChange={() => {
            if (bookingSlotDetails.length === data.length) {
              setBookingSlotDetails([]);
              setIsCheckboxSelected(false);
            } else {
              const allRows = data.filter(row => row.status.props.children === "Available");
              setBookingSlotDetails(allRows);
              if(data.length>0){
              setIsCheckboxSelected(true);
              }
            }
          }}
        />
      </div>
    ),
    Cell: ({ row }) => (
      <div style={{ paddingLeft: '50px' }}>
        <input
          type="checkbox"
          checked={bookingSlotDetails.some(selectedRow => selectedRow.slotId === row.original.slotId)}
          onChange={() => handleRowSelection(row.index)}
          disabled={row.original.status.props.children !== "Available"} // Disable checkbox if status is "Booked"
        />
      </div>
    ),
  };

  const enhancedColumns = [checkboxColumn, ...columns];

    return(
    <React.Fragment>
        <FormStep 
        t={t} 
        config={config} 
        onSelect={onSelect} 
        isDisabled={!venueTypes || !venueName || !venueCode || !bookingDate}>
                <CardHeader>{t("CHB_VENUE_SEARCH")}</CardHeader>
                <div>
                <CardLabel>{t("CHB_VENUE_TYPE_LABEL")}<span className="astericColor"> *</span></CardLabel>
                <Dropdown
                    t={t}
                    option={venues}
                    className="form-field"
                    selected={venueTypes}
                    select={(selected) => {
                        setVenueTypes(selected);
                        setVenueNames("");
                        setSlots("");
                        setVenueCode("");
                    }}
                    placeholder={t("CHB_VENUE_TYPE_PLACEHOLDER")}
                    optionKey="i18nKey"
                />
                <CardLabel>{t("CHB_VENUE_NAME_LABEL")}<span className="astericColor"> *</span></CardLabel>
                <div style={{position:"relative"}}>
                <Dropdown
                    t={t}
                    option={venuenames}
                    className="form-field"
                    selected={venueName}
                    select={(selected) => {
                        setVenueNames(selected);
                        setVenueCode("");
                        setSlots("");
                    }}
                    placeholder={t("CHB_VENUE_NAME_PLACEHOLDER")}
                    optionKey="i18nKey"
                />
                <div onClick={handleViewReportClick}  style={{ cursor: "pointer",position: "absolute", ...(isMobile ? {
                    top: "-30px",
                    right: "0",
                  }
                : {
                    top: "50%",
                    left: "calc(50% + 20px)",
                    transform: "translateY(-50%)",
                  }),
                }}>
                    <InfoIcon/>
                </div>
                {showDetails &&(
                <VenueDetails venueData={venueName} setShowDetails={setShowDetails} t={t}/>
                )}
                </div>
                <CardLabel>{t("CHB_HALL_CODE_LABEL")}<span className="astericColor"> *</span></CardLabel>
                <Dropdown
                    t={t}
                    option={venuecode}
                    className="form-field"
                    selected={venueCode}
                    select={setVenueCode}
                    placeholder={t("CHB_HALL_CODE_PLACEHOLDER")}
                    optionKey="i18nKey"
                />
                <CardLabel>{t("CHB_VENUE_DATE_LABEL")}<span className="astericColor"> *</span></CardLabel>
                <DateRangeFilter
                  t={t}
                  value={bookingDate}
                  maxDays={3}
                  setShowToast={setShowToast}
                  onChange={(range) => {
                    setBookingDate(range);
                  }}
                />
                <CardLabel>{t("CHB_VENUE_SLOTS")}<span className="astericColor"> *</span></CardLabel>
                <Dropdown
                    t={t}
                    option={timeSlotOptions}
                    className="form-field"
                    selected={slots}
                    select={setSlots}
                    placeholder={t("CHB_VENUE_SLOTS_PLACEHOLDER")}
                    optionKey="i18nKey"
                />
                </div>
                <div>
                  <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
                  <SubmitBar label={t("VENUE_BOOK")} onSubmit={handleBookClick} style={{ margin: "20px" }} disabled={!isCheckboxSelected} />
                </div>
        </FormStep>
            {showTable && ( // Only show table when showTable is true
        <Card style={{ overflowX: 'auto'}}>
            <ApplicationTable
            t={t}
            data={data}
            columns={enhancedColumns}
            getCellProps={(cellInfo) => ({
                style: {
                minWidth: "140px",
                padding: "20px",
                fontSize: "16px",
                },
            })}
            isPaginationRequired={false}
            totalRecords={data.length}
            />
            </Card>
            )}
            {showToast && (
                <Toast
                error={showToast.error}
                warning={showToast.warning}
                label={t(showToast.label)}
                onClose={() => {
                    setShowToast(null);
                }}
                />
            )}
            {showModal && (
                <BookingPopup
                t={t}
                closeModal={() => setShowModal(null)}  // Close modal when "BACK" is clicked
                actionCancelOnSubmit={() => setShowModal(null)}  // Close modal when "BACK" is clicked
                onSubmit={() => {
                    goNext(showModal);  // Ensure action is called only when submitting
                    setShowModal(null);  // Close modal after action
                }}
                setExistingDataSet={setExistingDataSet}
                searchData={showModal}
                tenantId={tenantId}
                />
            )}
    </React.Fragment>
    )
};

export default VenueSearch;