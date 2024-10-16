import React, { useRef, useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import ApplicationTable from "../components/ApplicationTable";
import {
  FormStep,
  CardHeader,
  CardLabel,
  Dropdown,
  SubmitBar,
  Calender,
  Toast,
  InfoIcon,
  Card,
  RadioButtons,
} from "@nudmcdgnpm/digit-ui-react-components";
import { DateRangePicker, createStaticRanges } from "react-date-range";
import { addDays, startOfDay, endOfDay, format, differenceInCalendarDays } from "date-fns";
const ADSSearch = ({ t, onSelect, config, userType, formData }) => {
  const { pathname: url } = useLocation();
  let index = 0;
  const [bookingSlotDetails, setBookingSlotDetails] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].bookingSlotDetails) || formData?.adslist?.bookingSlotDetails || []
  );
  const [selectedHall, setSelectedHall] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].selectedHall) || formData?.adslist?.selectedHall || ""
  );
  const [selectedLocation, setSelectedLocation] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].selectedLocation) || formData?.adslist?.selectedLocation || ""
  );
  const [selectedFace, setSelectedFace] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].selectedFace) || formData?.adslist?.selectedFace || ""
  );
  const [Searchdata, setSearchData] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].Searchdata) || formData?.adslist?.Searchdata || []
  );
  const [showToast, setShowToast] = useState(null);
  const [showDetails, setShowDetails] = useState(false);
  const dateRangePickerRef = useRef(null);
  const [showDateRangePicker, setShowDateRangePicker] = useState(false);
  const [hallCode, setHallCode] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].hallCode) || formData?.adslist?.hallCode || ""
  );
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [dateRange, setDateRange] = useState([
    {
      startDate: null,
      endDate: null,
      key: "selection",
    },
  ]);
  // const { data: hallList } = Digit.Hooks.chb.useChbCommunityHalls(tenantId, "CHB",
  ("ChbCommunityHalls");
  const { data: Hall } = Digit.Hooks.chb.useChbHallCode(tenantId, "CHB", "ChbHallCode");
  let HallName = [];
  let HallId = [];
  let LocationId = [];
  let FaceId = [];
  const { data: hallList } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "Advertisement", [{ name: "AdType" }], {
    select: (data) => {
      const formattedData = data?.["Advertisement"]?.["AdType"].map((details) => {
        return { i18nKey: `${details.name}`, code: `${details.code}`, name: `${details.name}`, active: `${details.active}` };
      });
      return formattedData;
    },
  });
  const { data: hall } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "Advertisement", [{ name: "Location" }], {
    select: (data) => {
      const formattedData = data?.["Advertisement"]?.["Location"].map((details) => {
        return { i18nKey: `${details.name}`, code: `${details.code}`, name: `${details.name}`, active: `${details.active}` };
      });
      return formattedData;
    },
  });
  const { data: Face } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "Advertisement", [{ name: "FaceArea" }], {
    select: (data) => {
      const formattedData = data?.["Advertisement"]?.["FaceArea"].map((details) => {
        return { i18nKey: `${details.name}`, code: `${details.code}`, name: `${details.name}`, active: `${details.active}` };
      });
      return formattedData;
    },
  });

  hallList &&
    hallList.map((slot) => {
      HallName.push({ i18nKey: `${slot.name}`, code: `${slot.code}`, value: `${slot.name}` });
    });
  hall &&
    hall.map((slot) => {
      LocationId.push({ i18nKey: `${slot.name}`, code: `${slot.code}`, value: `${slot.name}` });
    });
  Face &&
    Face.map((slot) => {
      FaceId.push({ i18nKey: `${slot.name}`, code: `${slot.code}`, value: `${slot.name}` });
    });

  Hall &&
    Hall.map((slot) => {
      HallId.push({
        i18nKey: `${slot.HallCode}`,
        code: `${slot.HallCode}`,
        value: `${slot.HallCode}`,
        communityHallId: slot.communityHallId,
        capacity: `${slot.capacity}`,
        fromTime: slot?.timeSlots[0].from,
        toTime: slot?.timeSlots[0].to,
      });
    });
  const hallCodeId = HallId.map((slot) => {
    if (selectedHall.communityHallId === slot.communityHallId) {
      return {
        i18nKey: slot.code + " - " + slot.capacity + " Person",
        code: slot.code,
        value: slot.code,
        communityHallId: slot.communityHallId,
        capacity: slot.capacity + " Person",
        toTime: slot.toTime,
        fromTime: slot.fromTime,
      };
    }
  }).filter((item) => item !== undefined);
  // Define the slot_search hook to refetch data on search
  const { data: slotSearchData, refetch } = Digit.Hooks.chb.useChbSlotSearch({
    tenantId: tenantId,
    filters: {
      communityHallCode: Searchdata.communityHallCode,
      bookingStartDate: Searchdata.bookingStartDate,
      bookingEndDate: Searchdata.bookingEndDate,
      hallCode: Searchdata.hallCode,
    },
  });
  useEffect(() => {
    if (slotSearchData && slotSearchData.hallSlotAvailabiltityDetails) {
      const newData = slotSearchData.hallSlotAvailabiltityDetails.map((slot, index) => ({
        slotId: index + 1,

        name: `${t(slot.communityHallCode)}`,
        code: slot.communityHallCode,
        hallCode1: slot.hallCode,
        address: Searchdata.hallAddress,
        hallCode: slot.hallCode + " - " + Searchdata.capacity,
        toTime: hallCode.toTime,
        fromTime: hallCode.fromTime,
        capacity: Searchdata.capacity,
        bookingDate: slot.bookingDate,
        status: slot.slotStaus === "AVAILABLE" ? <div className="sla-cell-success">Available</div> : <div className="sla-cell-error">Booked</div>,
      }));
      // Only update state if newData is different from current state
      setData((prevData) => {
        if (JSON.stringify(prevData) !== JSON.stringify(newData)) {
          return newData;
        }
        return prevData;
      });
      setShowTable((prevShowTable) => {
        if (!prevShowTable) {
          return true;
        }
        return prevShowTable;
      });
    }
  }, [slotSearchData, Searchdata]);
  const [data, setData] = useState("");
  const [showTable, setShowTable] = useState(false); // State to control table visibility
  const [isCheckboxSelected, setIsCheckboxSelected] = useState(false);
  const columns = [
    { Header: `${t("CHB_HALL_NAME")}` + "/" + `${t("CHB_PARK")}`, accessor: "name" },
    { Header: `${t("CHB_ADDRESS")}`, accessor: "address" },
    { Header: `${t("CHB_HALL_CODE")}`, accessor: "hallCode" },
    { Header: `${t("CHB_BOOKING_DATE")}`, accessor: "bookingDate" },
    { Header: `${t("CHB_STATUS")}`, accessor: "status" },
  ];
  const { control } = useForm();
  const goNext = () => {
    let owner = formData.adslist && formData.adslist[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, bookingSlotDetails, selectedHall, hallCode, Searchdata };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      ownerStep = { ...owner, bookingSlotDetails, selectedHall, hallCode, Searchdata };
      onSelect(config.key, ownerStep, false, index);
    }
  };
  const [selectNight, setselectNight] = useState([]);
  const ABmenu = [
    {
      i18nKey: "Yes",
      code: "Yes",
      value: "Yes",
    },
    {
      i18nKey: "No",
      code: "No",
      value: "No",
    },
  ];
console.log("dddd",selectNight);

  const onSkip = () => onSelect();
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [bookingSlotDetails, selectedHall, hallCode, Searchdata]);
  const handleViewReportClick = () => {
    if (selectedHall) {
      // Trigger the popup
      setShowDetails(true);
    } else {
      // Show toast message
      setShowToast({ error: true, label: t("CHB_SELECT_HALL_NAME") });
    }
  };
  const handleRowSelection = (rowIndex) => {
    setBookingSlotDetails((prevSelectedRows) => {
      const updatedSelectedRows = prevSelectedRows.some((row) => row.slotId === data[rowIndex].slotId)
        ? prevSelectedRows.filter((row) => row.slotId !== data[rowIndex].slotId)
        : [...prevSelectedRows, data[rowIndex]];
      const sortedSelectedRows = updatedSelectedRows.sort((a, b) => a.slotId - b.slotId);
      setIsCheckboxSelected(sortedSelectedRows.length > 0);
      return sortedSelectedRows;
    });
  };
  const checkboxColumn = {
    id: "selection",
    Header: ({ getToggleAllRowsSelectedProps }) => (
      <div style={{ paddingLeft: "50px" }}>
        <input
          type="checkbox"
          checked={bookingSlotDetails.length === data.length}
          disabled={data.every((row) => row.status.props.children !== "Available")}
          onChange={() => {
            if (bookingSlotDetails.length === data.length) {
              setBookingSlotDetails([]);
              setIsCheckboxSelected(false);
            } else {
              const allRows = data.filter((row) => row.status.props.children === "Available");
              setBookingSlotDetails(allRows);
              if (data.length > 0) {
                setIsCheckboxSelected(true);
              }
            }
          }}
        />
      </div>
    ),
    Cell: ({ row }) => (
      <div style={{ paddingLeft: "50px" }}>
        <input
          type="checkbox"
          checked={bookingSlotDetails.some((selectedRow) => selectedRow.slotId === row.original.slotId)}
          onChange={() => handleRowSelection(row.index)}
          disabled={row.original.status.props.children !== "Available"} // Disable checkbox if status
          // is
        />
      </div>
    ),
  };
  const enhancedColumns = [checkboxColumn, ...columns];
  const staticRanges = createStaticRanges([
    {
      label: "One Day",
      range: () => ({
        startDate: startOfDay(addDays(new Date(), 1)),
        endDate: endOfDay(addDays(new Date(), 1)),
      }),
    },
    {
      label: "Two Days",
      range: () => ({
        startDate: startOfDay(addDays(new Date(), 1)),
        endDate: endOfDay(addDays(new Date(), 2)),
      }),
    },
    {
      label: "Three Days",
      range: () => ({
        startDate: startOfDay(addDays(new Date(), 1)),
        endDate: endOfDay(addDays(new Date(), 3)),
      }),
    },
  ]);
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dateRangePickerRef.current && !dateRangePickerRef.current.contains(event.target) && !event.target.closest(".calendar-icon")) {
        setShowDateRangePicker(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);
  const handleDateRangeChange = (item) => {
    const { startDate, endDate } = item.selection;
    const dayDifference = differenceInCalendarDays(endDate, startDate);
    if (dayDifference < 3) {
      // Limit the date range to 3 days
      setDateRange([item.selection]);
      if (startDate && endDate) {
        setShowDateRangePicker(false); // Close date range picker when both dates are selected
      }
    } else {
      setShowToast({ error: true, label: t("CHB_DATE_RANGE_LIMIT") });
    }
  };
  const handleSearch = () => {
    const selectedHallName = selectedHall?.code || "";
    const startDate = dateRange[0].startDate ? format(dateRange[0].startDate, "yyyy-MM-dd") : "";
    const endDate = dateRange[0].endDate ? format(dateRange[0].endDate, "yyyy-MM-dd") : "";
    const selectedHallCode = hallCode?.code || "";
    if (selectedHallName && startDate && endDate && selectedHallCode) {
      // Find hallCodeId based on selectedHall.communityHallId
      // const hallCodeIds = hallCodeId.find(slot => selectedHall.communityHallId ===
      slot.communityHallId;
      const filters = {
        communityHallCode: selectedHallName,
        bookingStartDate: startDate,
        bookingEndDate: endDate,
        hallAddress: selectedHall?.address,
        hallCode: selectedHallCode,
        capacity: hallCode?.capacity,
      };
      setSearchData(filters);
    } else {
      setShowToast({ error: true, label: t("CHB_SELECT_COMMUNITY_HALL_DATE_HALLCODE") });
    }
  };

  const handleBookClick = () => {
    if (!isCheckboxSelected) {
      setShowToast({ error: true, label: t("CHB_SELECT_AT_LEAST_ONE_SLOT") });
    } else {
      goNext();
    }
  };
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 1500); // Close toast after 1.5 seconds
      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);
  useEffect(() => {
    if (Searchdata.communityHallCode) {
      refetch();
      setBookingSlotDetails([]);
      setIsCheckboxSelected(false);
    }
  }, [Searchdata]);
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen")}
      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t}>
        <CardHeader>{t("ADS_SEARCH_HEADER")}</CardHeader>
        <CardLabel>
          {`${t("ADS_TYPE")}`} <span className="check-page-link-button">*</span>
        </CardLabel>
        <div style={{ display: "flex", flexDirection: "row", gap: "10px" }}>
          <Controller
            control={control}
            name={"selectedHall"}
            defaultValue={selectedHall}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={selectedHall}
                select={(selected) => {
                  setSelectedHall(selected);
                  setHallCode(""); // Clear hallCode when a new hall is selected
                }}
                placeholder={"Select Ad Type"}
                option={HallName}
                optionKey="i18nKey"
                t={t}
              />
            )}
          />
        </div>

        <CardLabel>
          {`${t("ADS_LOCATION")}`} <span className="check-page-link-button">*</span>
        </CardLabel>
        <div style={{ display: "flex", flexDirection: "row", gap: "10px" }}>
          <Controller
            control={control}
            name={"selectedHall"}
            defaultValue={selectedLocation}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={selectedLocation}
                select={(selected) => {
                  setSelectedLocation(selected);
                  setHallCode(""); // Clear hallCode when a new hall is selected
                }}
                placeholder={"Select Ad Type"}
                option={LocationId}
                optionKey="i18nKey"
                t={t}
              />
            )}
          />
        </div>
        <CardLabel>
          {`${t("ADS_NIGHT_LIGHT")}`} <span className="astericColor">*</span>
        </CardLabel>
        <RadioButtons
          t={t}
          options={ABmenu}
          optionsKey="code"
          name="selectNight"
          value={selectNight}
          selectedOption={selectNight}
          innerStyles={{ display: "inline-block", marginLeft: "10px", paddingBottom: "2px", marginBottom: "2px" }}
          onSelect={setselectNight}
          isDependent={true}
        />
        <div>
          <CardLabel>
            {`${t("ADS_FACE_AREA")}`} <span className="check-page-link-button">*</span>
          </CardLabel>
          <div style={{ display: "flex", flexDirection: "row", gap: "10px" }}>
            <Controller
              control={control}
              name={"selectedHall"}
              defaultValue={selectedFace}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={selectedFace}
                  select={(selected) => {
                    setSelectedFace(selected);
                    setHallCode(""); // Clear hallCode when a new hall is selected
                  }}
                  placeholder={"Select Ad Type"}
                  option={FaceId}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </div>
          <div className="filter-label">
            <CardLabel>
              {`${t("CHB_SELECT_DATE")}`} <span className="check-page-link-button">*</span>
            </CardLabel>
          </div>
          <div className="employee-select-wrap" style={{ width: "50%" }}>
            <div className="select">
              <input
                className="employee-select-wrap--elipses"
                type="text"
                placeholder={"Select Date"}
                value={
                  dateRange[0].startDate && dateRange[0].endDate
                    ? `${format(dateRange[0].startDate, "dd/MM/yyyy")} -

${format(dateRange[0].endDate, "dd/MM/yyyy")}`
                    : ""
                }
                readOnly
              />
              <Calender
                className="cursorPointer calendar-icon"
                onClick={(e) => {
                  e.stopPropagation(); // Prevent the event from bubbling up to the document
                  setShowDateRangePicker((prevState) => !prevState);
                }}
              />
            </div>
            {showDateRangePicker && (
              <div ref={dateRangePickerRef} className="date-range-picker-wrapper">
                <DateRangePicker
                  className="pickerShadow"
                  ranges={dateRange}
                  onChange={handleDateRangeChange}
                  showSelectionPreview={true}
                  rangeColors={["#9E9E9E"]}
                  moveRangeOnFirstSelection={false}
                  staticRanges={staticRanges}
                  inputRanges={[]}
                  placeholder={"Select Community Hall"}
                  minDate={addDays(new Date(), 1)} // This sets the minimum date to tomorrow
                />
              </div>
            )}
          </div>
        </div>
        <div style={{ display: "flex", flexDirection: "row", gap: "15px" }}>
          <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
          <SubmitBar label={t("ADS_ADD_TO_CART")} onSubmit={handleBookClick} disabled={!isCheckboxSelected} />
          <SubmitBar label={t("ADS_VIEW_CART")} onSubmit={handleBookClick} disabled={!isCheckboxSelected} />
          {/* <SubmitBar label={t("ADS_BOOK_NOW")} onSubmit={handleBookClick} disabled=
{!isCheckboxSelected} /> */}
          <SubmitBar label={t("ADS_BOOK_NOW")} onSubmit={goNext} />
        </div>
      </FormStep>
      {showTable && ( // Only show table when showTable is true
        <Card>
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
    </React.Fragment>
  );
};
export default ADSSearch;
