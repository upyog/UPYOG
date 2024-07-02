import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import ApplicationTable from "../components/inbox/ApplicationTable";
import {
  FormStep,
  Card,
  CardHeader,
  CardLabel,
  Dropdown,
  SubmitBar,
  TextInput,
  DateRange,
  Calender
} from "@upyog/digit-ui-react-components";
import { DateRangePicker, createStaticRanges } from "react-date-range";
import { addDays, startOfDay, endOfDay, format } from 'date-fns';

const CommunityHallSearch = ({ t, onSelect, config, onSearch, userType, formData, onFilterChange, searchParams }) => {
  const { pathname: url } = useLocation();
  let index = 0;
  const [bookingSlotDetails, setBookingSlotDetails] = useState(
    (formData.slotlist && formData.slotlist[index] && formData.slotlist[index].bookingSlotDetails) ||
    formData?.slotlist?.bookingSlotDetails ||
    []
  );
  const [selectedHall, setSelectedHall] = useState(
    (formData.slotlist && formData.slotlist[index] && formData.slotlist[index].selectedHall) ||
    formData?.slotlist?.selectedHall ||
    ""
  );
  const [showDateRangePicker, setShowDateRangePicker] = useState(false);
  const [hallCode, setHallCode] = useState(
    (formData.slotlist && formData.slotlist[index] && formData.slotlist[index].hallCode) ||
    formData?.slotlist?.hallCode ||
    ""
  );
  const stateId = Digit.ULBService.getStateId();
 
  const [dateRange, setDateRange] = useState([{
    startDate: null,
    endDate: null,
    key: 'selection'
  }]);
  const { data: hallList } = Digit.Hooks.chb.useChbCommunityHalls(stateId, "CHB", "ChbCommunityHalls");
  const { data: Hall } = Digit.Hooks.chb.useChbHallCode(stateId, "CHB", "ChbHallCode");
  
  let initialData = [];
  let HallName = [];
  let HallId = [];

  hallList &&
  hallList.map((slot) => {
    initialData.push({ slotId: slot.communityHallId, name: slot.name, address: slot.address });
  });
  Hall &&
  Hall.map((slot) => {
    HallName.push({ i18nKey: `${slot.name}`, code: `${slot.name}`, value: `${slot.name}` });
  });
  Hall &&
  Hall.map((slot) => {
    HallId.push({ i18nKey: `${slot.HallCode}`, code: `${slot.HallCode}`, slotId: `${slot.communityHallId}` });
  });

  // Define the slot_search hook to refetch data on search
  const { data: slotSearchData, refetch } = Digit.Hooks.chb.useChbSlotSearch({
    tenantId: "pg.citya",
    filters: {
      communityHallName: selectedHall.code,
      bookingStartDate: dateRange[0].startDate ? format(dateRange[0].startDate, 'dd-MM-yyyy') : "",
      bookingEndDate: dateRange[0].endDate ? format(dateRange[0].endDate, 'dd-MM-yyyy') : "",
      hallCode: hallCode.code
    }
  });

 

  useEffect(() => {
    const addressdata = initialData.map(slot => {
      if (slot.name === selectedHall.code) {
        return slot.address;
      }
    });
    if (slotSearchData && slotSearchData.hallSlotAvailabiltityDetails) {
      const newData = slotSearchData.hallSlotAvailabiltityDetails.map((slot, index) => ({
        slotId: index + 1,
        name: slot.communityHallName,
        address: addressdata || "", // Ensure address is set
        location: "NEW DELHI",
        bookingDate: slot.bookingDate, 
        status: slot.slotStaus === "AVAILABLE" ? (
          <SubmitBar label={slot.slotStaus} onSubmit={goNext} disabled={false}  style={{color:"green",width: "85%"}}/>
        ) : (
          <SubmitBar label={slot.slotStaus} disabled={true} />
        )
      }));
      setData(newData); // Update data state
      setShowTable(true); // Show the table after data is fetched
    }
  }, [slotSearchData, selectedHall, hallCode, dateRange,bookingSlotDetails]);

  const [data, setData] = useState("");
  const [showTable, setShowTable] = useState(false); // State to control table visibility

  const columns = [
    { Header: `${t("CHB_HALL_NAME")}`+ "/" + `${t("CHB_PARK")}`, accessor: "name" },
    { Header: `${t("CHB_ADDRESS")}`, accessor: "address" },
    { Header: `${t("CHB_LOCATION")}`, accessor: "location" },
    { Header: `${t("CHB_BOOKING_DATE")}`, accessor: "bookingDate" },
    { Header: `${t("CHB_STATUS")}`, accessor: "status" },
  ];

  const { control } = useForm();

  const goNext = () => {
    let owner = formData.slotlist && formData.slotlist[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, bookingSlotDetails, selectedHall, hallCode };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      ownerStep = { ...owner, bookingSlotDetails, selectedHall, hallCode };
      onSelect(config.key, ownerStep, false, index);
    }
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [bookingSlotDetails, selectedHall, hallCode]);

  const handleRowSelection = (rowIndex) => {
    setBookingSlotDetails((prevSelectedRows) => {
      const updatedSelectedRows = prevSelectedRows.some(row => row.slotId === data[rowIndex].slotId) ?
        prevSelectedRows.filter(row => row.slotId !== data[rowIndex].slotId) :
        [...prevSelectedRows, data[rowIndex]];
      return updatedSelectedRows;
    });
  };

  const checkboxColumn = {
    id: "selection",
    Header: ({ getToggleAllRowsSelectedProps }) => (
      <div style={{ paddingLeft: '50px' }}>
        <input
          type="checkbox"
          checked={bookingSlotDetails.length === data.length}
          onChange={() => {
            if (bookingSlotDetails.length === data.length) {
              setBookingSlotDetails([]);
            } else {
              const allRows = data;
              setBookingSlotDetails(allRows);
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
        />
      </div>
    ),
  };

  const enhancedColumns = [checkboxColumn, ...columns];
  const staticRanges = createStaticRanges([
    {
      label: 'Today',
      range: () => ({
        startDate: startOfDay(new Date()),
        endDate: endOfDay(new Date())
      })
    },
    {
      label: 'Tomorrow',
      range: () => ({
        startDate: startOfDay(addDays(new Date(), 1)),
        endDate: endOfDay(addDays(new Date(), 1))
      })
    },
    {
      label: 'Next 2 Days',
      range: () => ({
        startDate: startOfDay(new Date()),
        endDate: endOfDay(addDays(new Date(), 2))
      })
    },
  ]);

  const handleDateRangeChange = (item) => {
    setDateRange([item.selection]);
    if (item.selection.startDate && item.selection.endDate) {
      setShowDateRangePicker(false); // Close date range picker when both dates are selected
    }
  };

  const handleSearch = () => {
    if ((selectedHall.code && dateRange[0].startDate && dateRange[0].endDate) || (hallCode.code && dateRange[0].startDate && dateRange[0].endDate && selectedHall.code)) {
      refetch(); // Fetch data using refetch
    } else {
      alert('Please select either Community Hall Name with Date or Hall Code with Date.');
    }
  };

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen")}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
      >
        <CardHeader>{`${t("CHB_SEARCH_COMMUNITY_HALL_HEADER")}`}/{`${t("CHB_PARK")}`}</CardHeader>
        <div>
          <CardLabel>{`${t("CHB_SELECT_HALL_NAME")}`}</CardLabel>
          <Controller
            control={control}
            name={"selectedHall"}
            defaultValue={selectedHall}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={selectedHall}
                select={setSelectedHall}
                option={HallName}
                optionKey="i18nKey"
                t={t}
              />
            )}
          />
          <div className="filter-label"><CardLabel>{`${t("CHB_SELECT_DATE")}`}</CardLabel></div>
          <div className="employee-select-wrap" style={{ width: "50%" }}>
            <div className="select">
              <input
                className="employee-select-wrap--elipses"
                type="text"
                value={
                  dateRange[0].startDate && dateRange[0].endDate
                    ? `${format(dateRange[0].startDate, 'dd/MM/yyyy')} - ${format(dateRange[0].endDate, 'dd/MM/yyyy')}`
                    : ""
                }
                readOnly
              />
              <Calender className="cursorPointer" onClick={() => setShowDateRangePicker((prevState) => !prevState)} />
            </div>
            {showDateRangePicker && (
              <div className="options-card" style={{ overflow: "visible", width: "unset", maxWidth: "unset" }}>
                <DateRangePicker
                  className="pickerShadow"
                  ranges={dateRange}
                  onChange={handleDateRangeChange}
                  showSelectionPreview={true}
                  rangeColors={["#9E9E9E"]}
                  moveRangeOnFirstSelection={false}
                  staticRanges={staticRanges}
                  inputRanges={[]}
                  minDate={new Date()}
                />
              </div>
            )}
          </div>
          <CardLabel>{`${t("CHB_HALL_CODE")}`}</CardLabel>
          <Controller
            control={control}
            name={"hallCode"}
            defaultValue={hallCode}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={hallCode}
                select={setHallCode}
                option={HallId}
                optionKey="i18nKey"
                t={t}
              />
            )}
          />
        </div>
        <div>
          <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
          <SubmitBar label={t("CHB_BOOK")} onSubmit={goNext} style={{ margin: "20px" }} />
        </div>
        <br />
        {showTable && ( // Only show table when showTable is true
          <ApplicationTable
            t={t}
            data={data}
            columns={enhancedColumns}
            getCellProps={(cellInfo) => ({
              style: {
                minWidth: "150px",
                padding: "20px",
                fontSize: "16px",
              },
            })}
            totalRecords={data.length}
          />
        )}
      </FormStep>
    </React.Fragment>
  );
};

export default CommunityHallSearch;
