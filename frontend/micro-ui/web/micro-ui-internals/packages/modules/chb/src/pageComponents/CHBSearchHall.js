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
import { addDays, startOfDay, endOfDay,format } from 'date-fns';

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
  const [hallCode, setHallCode] = useState( (formData.slotlist && formData.slotlist[index] && formData.slotlist[index].hallCode) ||
  formData?.slotlist?.hallCode ||
  "");
  const stateId = Digit.ULBService.getStateId();
 
  const [dateRange, setDateRange] = useState([{
    startDate: new Date(),
    endDate: addDays(new Date(), 1),
    key: 'selection'
  }]);
  const { data: hallList } = Digit.Hooks.chb.useChbCommunityHalls(stateId, "CHB", "ChbCommunityHalls");
  const { data: Hall } = Digit.Hooks.chb.useChbHallCode(stateId, "CHB", "ChbHallCode");
  
  let initialData=[];
  let HallName=[];
  let HallId=[];

  hallList &&
  hallList.map((slot) => {
    initialData.push({slotId:slot.communityHallId,name:slot.name, address:slot.address });
      
      });
  Hall &&
 Hall.map((slot) => {
    HallName.push({ i18nKey: `${slot.name}`, code: `${slot.name}`, value: `${slot.name}` });
      
      });
  Hall &&
 Hall.map((slot) => {
  HallId.push({ i18nKey: `${slot.HallCode}`, code: `${slot.HallCode}`, slotId: `${slot.communityHallId}` });
});
  const addBookingDate = (data) => {
    const now = new Date();
    const tomorrow = new Date(now);
    tomorrow.setDate(now.getDate() + 2);
    
    const formatDate = (date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${day}-${month}-${year} ${hours}:${minutes}`;
    };
  
    const bookingDate = `${formatDate(now)} to ${formatDate(tomorrow)}`;
    
    return data.map(slot => ({
      ...slot,
      bookingDate
    }));
  };
  
  const updatedData = addBookingDate(initialData);
  
  console.log("hall",Hall);
  
  const [data, setData] = useState(initialData);

  const columns = [
    { Header: `${t("CHB_HALL_NAME")}`, accessor: "name" },
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
      ownerStep = { ...owner, bookingSlotDetails,selectedHall,hallCode };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      ownerStep = { ...owner, bookingSlotDetails,selectedHall,hallCode };
      onSelect(config.key, ownerStep, false, index);
    }
    console.log("details", ownerStep);
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [bookingSlotDetails,selectedHall,hallCode]);

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
  console.log("daterange",dateRange[0].startDate && dateRange[0].endDate);
  const handleSearch = () => {
    let filteredData = initialData;

    if (selectedHall) {
      filteredData = filteredData.filter(item => item.name === selectedHall.code);
      console.log("selectedHall",selectedHall.code);
    }
    if (hallCode) {
      filteredData = filteredData.filter(item => item.slotId === hallCode.slotId);
      console.log("hallCode",hallCode);
    }

    if (dateRange[0] && dateRange[1]) {
      filteredData = filteredData.filter(item =>
        new Date(item.date1) >= new Date(dateRange[0]) && new Date(item.date2) <= new Date(dateRange[1])
      );
      console.log("daterange",dateRange.startDate && dateRange.endDate);
    }

    setData(filteredData);
    console.log("filteredData", filteredData);
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
        <CardHeader>{`${t("CHB_SEARCH_COMMUNITY_HALL_HEADER")}`}</CardHeader>
        <div >
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

          <div className="filter-label"><CardLabel>{`${t("SELECT_DATE")}`}</CardLabel></div>
                <div className="employee-select-wrap" style={{ width: "50%" }}>
                  <div className="select">
                    <input className="employee-select-wrap--elipses" type="text"  value={`${format(dateRange[0].startDate, 'dd/MM/yyyy')} - ${format(dateRange[0].endDate, 'dd/MM/yyyy')}`} readOnly />
                    <Calender className="cursorPointer" onClick={() => setShowDateRangePicker((prevState) => !prevState)} />
                  </div>
            {showDateRangePicker && (
               <div className="options-card" style={{ overflow: "visible", width: "unset", maxWidth: "unset" }}>
              <DateRangePicker
               className="pickerShadow"
                ranges={dateRange}
                onChange={item => setDateRange([item.selection])}
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
      </FormStep>
    </React.Fragment>
  );
};

export default CommunityHallSearch;
