import React, {useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import ApplicationTable from "../components/inbox/ApplicationTable";
import SearchCHB from "../components/SearchCHB";
import {
  Card,
  CardHeader,
  CardLabel,
  Dropdown,
  SubmitBar,
  TextInput,
  DateRange,
} from "@upyog/digit-ui-react-components";
// import Hall from "../pages/citizen/Create/Hall";

const CommunityHallSearch = ({ t,onSelect, onSearch,onFilterChange, searchParams  }) => {
  // Initialize state for search criteria
  let validation = {};
  const [communityList, setCommunityList] = useState("");
  const [hallCode, sethallCode] = useState("");
  const [selectedRows, setSelectedRows] = useState([]);
  const [localSearchParams, setLocalSearchParams] = useState(() => ({ ...searchParams }));

  const handleChange = (data) => {
    setLocalSearchParams({ ...localSearchParams, ...data });
  };


  let Community_list =[
    {
      "value": "Sanskriti Kendra",
      "code": "Sanskriti Kendra",
      "i18nKey": "Sanskriti Kendra",
    },
    {
      "value": "Lalit Kala Akademi",
      "code": "Lalit Kala Akademi",
      "i18nKey": "Lalit Kala Akademi"
    },
    {
      "value": "Nehru Park",
      "code": "Nehru Park",
      "i18nKey": "Nehru Park"
    },
    {
      "value": "India Habitat Centre",
      "code": "India Habitat Centre",
      "i18nKey": "India Habitat Centre"
    },
    {
      "value": "India International Centre (IIC)",
      "code": "India International Centre (IIC)",
      "i18nKey": "India International Centre (IIC)"
    },
    {
      "value": "Alliance Francaise de Delhi",
      "code": "Alliance Francaise de Delhi",
      "i18nKey": "Alliance Francaise de Delhi"
    },
    {
      "value": "Constitution Club of India",
      "code": "Constitution Club of India",
      "i18nKey": "Constitution Club of India"
    },
    {
      "value": "Kamani Auditorium",
      "code": "Kamani Auditorium",
      "i18nKey": "Kamani Auditorium"
    },
    {
      "value": "Siri Fort Auditorium",
      "code": "Siri Fort Auditorium",
      "i18nKey": "Siri Fort Auditorium"
    },
    {
      "value": "Karnataka Sangha Delhi",
      "code": "Karnataka Sangha Delhi",
      "i18nKey": "Karnataka Sangha Delhi"
    }
  ];  


   const initialData = [
    {
      id: 1,
      name: "Sanskriti Kendra",
      number: "3456",
      date1: "2024-05-10",
      date2: "2024-05-15",
      status: "Available"
    },
    {
      id: 2,
      name: "Lalit Kala Akademi",
      number: "7890",
      date1: "2024-05-12",
      date2: "2024-05-17",
      status: "Available"
    },
    {
      id: 3,
      name: "Nehru Park",
      number: "1234",
      date1: "2024-05-14",
      date2: "2024-05-19",
      status: "Booked"
    },
    {
      id: 4,
      name: "India Habitat Centre",
      number: "5678",
      date1: "2024-05-16",
      date2: "2024-05-21",
      status: "Available"
    },
    {
      id: 5,
      name: "India International Centre (IIC)",
      number: "2345",
      date1: "2024-05-18",
      date2: "2024-05-23",
      status: "Booked"
    },
    {
      id: 6,
      name: "Alliance Francaise de Delhi",
      number: "6789",
      date1: "2024-05-20",
      date2: "2024-05-25",
      status: "Booked"
    },
    {
      id: 7,
      name: "Constitution Club of India",
      number: "9012",
      date1: "2024-05-22",
      date2: "2024-05-27",
      status: "Booked"
    },
    {
      id: 8,
      name: "Kamani Auditorium",
      number: "3456",
      date1: "2024-05-24",
      date2: "2024-05-29",
      status: <span className="sla-cell-success">Available"</span>
    },
    {
      id: 9,
      name: "Siri Fort Auditorium",
      number: "7890",
      date1: "2024-05-26",
      date2: "2024-05-31",
      status: "Booked"
    },
    {
      id: 10,
      name: "Karnataka Sangha Delhi",
      number: "1234",
      date1: "2024-05-28",
      date2: "2024-06-02",
      status: "Available"
    },
  ];

  const [data, setData] = useState(initialData);

  const columns = [
    { Header: "Hall Name", accessor: "name" },
    { Header: "Hall Code", accessor: "number" },
    { Header: "Start Date", accessor: "date1" },
    { Header: "End Date", accessor: "date2" },
    { Header: "Status", accessor: "status" },
  ];

  const { control } = useForm();
  function onSave() {}
  function goNext() {
    onSelect();
  }

  // Function to handle form submission
  const handleSearch = () => {
    onSearch({ communityList, hallCode, ...localSearchParams.range });
   
  };

  
  const handleRowSelection = (rowId) => {
    setSelectedRows((prevSelectedRows) => {
      if (prevSelectedRows.includes(rowId)) {
        return prevSelectedRows.filter((id) => id !== rowId);
      } else {
        return [...prevSelectedRows, rowId];
      }
    });
  };

  const checkboxColumn = {
    id: "selection",
    Header: ({ getToggleAllRowsSelectedProps }) => (
      <div style={{ paddingLeft: '50px' }}>
        <input
          type="checkbox"
          checked={selectedRows.length === data.length}
          onChange={() => {
            if (selectedRows.length === data.length) {
              setSelectedRows([]);
            } else {
              setSelectedRows(data.map((_, index) => index));
            }
          }}
        />
      </div>
    ),
    Cell: ({ row }) => (
      <div style={{ paddingLeft: '50px' }}>
        <input
          type="checkbox"
          checked={selectedRows.includes(row.index)}
          onChange={() => handleRowSelection(row.index)}
        />
      </div>
    ),
  };

  const enhancedColumns = [checkboxColumn, ...columns];
  const handleBook = () => {
    if (selectedRows.length <=0 || selectedRows.length > 3) {
      alert("Please select three or less than three checkbox");
      return;
    }
    else{
      onSelect();
    }
  };

  return (
    <Card>
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
      <SubmitBar label={t("Book")} onSubmit={handleBook} style={{margin:"20px"}}/>
      </div>
    {/* {selectedRows.length > 3 && (
      <div style={{ color: "red", marginTop: "5px" }}>
        {t("Please select 3 or fewer checkboxes.")}
      </div>
    )} */}

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
    </Card>
  );
};

export default CommunityHallSearch;
