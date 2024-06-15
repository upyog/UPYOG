import React, {useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import ApplicationTable from "../components/inbox/ApplicationTable";
import SearchCHB from "../components/SearchCHB";
import {
  FormStep,
  Card,
  CardHeader,
  CardLabel,
  Dropdown,
  SubmitBar,
  TextInput,
  DateRange,
} from "@upyog/digit-ui-react-components";

const CommunityHallSearch = ({ t,onSelect,config, onSearch,userType,formData,onFilterChange, searchParams  }) => {
  const { pathname: url } = useLocation();
  let index =0;
  let validation = {};
  const [bookingSlotDetails, setBookingSlotDetails] = useState((formData.slotlist && formData.slotlist[index] && formData.slotlist[index].bookingSlotDetails) || formData?.slotlist?.bookingSlotDetails || []);
  const [localSearchParams, setLocalSearchParams] = useState(() => ({ ...searchParams }));

  const handleChange = (data) => {
    setLocalSearchParams({ ...localSearchParams, ...data });
  };


   const initialData = [
    {
      slotId: 1,
      hallName: "Sanskriti Kendra",
      hallCode: "3456",
      date1: "2024-05-10",
      date2: "2024-05-15",
      status: "Available"
    },
    {
      slotId: 2,
      hallName: "Lalit Kala Akademi",
      hallCode: "7890",
      date1: "2024-05-12",
      date2: "2024-05-17",
      status: "Available"
    },
    {
      slotId: 3,
      hallName: "Nehru Park",
      hallCode: "1234",
      date1: "2024-05-14",
      date2: "2024-05-19",
      status: "Booked"
    },
    {
      slotId: 4,
      hallName: "India Habitat Centre",
      hallCode: "5678",
      date1: "2024-05-16",
      date2: "2024-05-21",
      status: "Available"
    },
    {
      slotId: 5,
      hallName: "India International Centre (IIC)",
      hallCode: "2345",
      date1: "2024-05-18",
      date2: "2024-05-23",
      status: "Booked"
    },
    {
      slotId: 6,
      hallName: "Alliance Francaise de Delhi",
      hallCode: "6789",
      date1: "2024-05-20",
      date2: "2024-05-25",
      status: "Booked"
    },
    {
      slotId: 7,
      hallName: "Constitution Club of India",
      hallCode: "9012",
      date1: "2024-05-22",
      date2: "2024-05-27",
      status: "Booked"
    },
    {
      slotId: 8,
      hallName: "Kamani Auditorium",
      hallCode: "3456",
      date1: "2024-05-24",
      date2: "2024-05-29",
      status: <span className="sla-cell-success">Available"</span>
    }
  ];

  const [data, setData] = useState(initialData);

  const columns = [
    { Header: "Hall Name", accessor: "hallName" },
    { Header: "Hall Code", accessor: "hallCode" },
    { Header: "Start Date", accessor: "date1" },
    { Header: "End Date", accessor: "date2" },
    { Header: "Status", accessor: "status" },
  ];

  const { control } = useForm();

  function goNext() {
    let owner = formData.slotlist && formData.slotlist[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner,bookingSlotDetails};
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      
      ownerStep = { ...owner,bookingSlotDetails };
      onSelect(config.key, ownerStep, false,index);
    }
    console.log("details",ownerStep);
  }

  const onSkip = () => onSelect();
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [bookingSlotDetails]);

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

  return (
    <React.Fragment>
        {
          window.location.href.includes("/citizen")
        }
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          // isDisabled={ !selectslot || !residentType || !specialCategory || !purpose}
        >
      <CardHeader>{`${t("SEARCH_COMMUNITY_HALL")}`}</CardHeader>
      <SearchCHB />
      {/* <CardLabel>{`${t("Enter Hall Code")}`}</CardLabel>
      <TextInput
          t={t}
          type={"text"}
          isMandatory={false}
          optionKey="i18nKey"
          name="hallCode"
          value={hallCode}
          onChange={(e) => sethallCode(e.target.value)}
          style={{ width: "86%"}}
          ValidationRequired = {false}
        /> */}
      {/* Search button */}
      {/* <div >
      <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
      <SubmitBar label={t("Book")} onSubmit={handleBook} style={{margin:"20px"}}/>
      </div> */}
      <div>
      <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
      <SubmitBar label={t("Book")} onSubmit={goNext} style={{margin:"20px"}}/>
      </div>

      <br></br>
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
