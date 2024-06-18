import { Calender } from "@egovernments/digit-ui-react-components";
import {
  differenceInDays,
  endOfDay,
  format,
  startOfWeek,
  endOfWeek
} from "date-fns";
import React, { useEffect, Fragment, useMemo, useRef, useState } from "react";
import { createStaticRanges, DateRangePicker, DateRange } from "react-date-range";

function isEndDateFocused(focusNumber) {
  return focusNumber === 1;
}

function isStartDateFocused(focusNumber) {
  return focusNumber === 0;
}

const SelectCustomDateRange = ({ values, onFilterChange, t }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [focusedRange, setFocusedRange] = useState([0, 0]);
  const [selectionRange, setSelectionRange] = useState({
    "startDate": new Date(),
    "endDate": new Date(),
    "interval": "",
    "title": ""
  });
  const [rangeType, setRangeType] = useState("year");
  const [currentYear, setCurrentYear] = useState(new Date().getFullYear());
  const [startYearRange,setStartYearRange] = useState(new Date().getFullYear());
  const [monthCurrentYear, setMonthCurrentYear] = useState(new Date().getFullYear());
  const [endYearRange,setEndYearRange] = useState(new Date().getFullYear());
  const [calenderDataStartDate, setCalenderDataStartDate] = useState();
  const [calenderDataEndDate, setCalenderDataEndDate] = useState();
  const [calenderData, setCalenderData ] = useState({
    endDate:"",interval:"",startDate:"",title:""
  });
  const [calender, setCalender] = useState()

  useEffect(()=>{
    if (calenderDataEndDate){
      let selection={
        "startDate": rangeType === "month"?"":new Date(calenderDataStartDate,3, 1),
        "endDate": rangeType === "month"?"":new Date(calenderDataEndDate, 2, 31),
        "interval": "month",
        "title": ""
      };

      if(rangeType === "year"){
        if (calenderDataStartDate === calenderDataEndDate){
          selection.endDate = new Date(parseInt(calenderDataEndDate)+1, 2, 31);
        }
        
      }else if (rangeType === "month"){
        let [tempStartYear, tempStartMonth] = calenderDataStartDate.split('-');
        let [tempEndYear, tempEndMonth] = calenderDataEndDate.split('-');
        let months = {"Jan": 0,"Feb": 1,"Mar": 2,"Apr": 3,"May": 4,"Jun": 5,"Jul": 6,"Aug": 7,"Sep": 8,"Oct": 9,"Nov": 10,"Dec": 11};
        selection.startDate = new Date(tempStartYear,months[tempStartMonth], 1);
        selection.endDate = new Date(tempEndYear,months[tempEndMonth]+1, 0);
      }
      let customDateFilter = {
        filterType : rangeType
      }
      sessionStorage.setItem("customDateFilter",JSON.stringify(customDateFilter))
      console.log(selection.startDate, "----",  selection.endDate )
      setTimeout(() => {
        setIsModalOpen(false);
        setSelectionRange(selection);
      }, 200);
    }
  },[calenderDataStartDate,calenderDataEndDate])

  const wrapperRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setIsModalOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [wrapperRef]);

  useEffect(() => {
    if (!isModalOpen) {
      const startDate = selectionRange?.startDate;
      const endDate = selectionRange?.endDate;
      const interval = getDuration(selectionRange?.startDate, selectionRange?.endDate);
      const title = `${format(selectionRange?.startDate, "MMM d, yyyy")} - ${format(selectionRange?.endDate, "MMM d, yyyy")}`;
      onFilterChange({ range: { startDate, endDate, interval, title }, requestDate: { startDate, endDate, interval, title } });
    }
  }, [selectionRange, isModalOpen]);


  const getDuration = (startDate, endDate) => {
    let noOfDays = (new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000 * 3600 * 24);
    if (noOfDays > 91) {
      return "month";
    }
    if (noOfDays < 90 && noOfDays >= 14) {
      return "week";
    }
    if (noOfDays <= 14) {
      return "day";
    }
  };

  const handleSelect = (ranges, e) => {
    let { range1 } = ranges;
    let selection ={
      "startDate":startOfWeek(range1.startDate),
      "endDate": endOfWeek(range1.endDate),
      "interval": "week",
      "title": ""
    };

    if (!calenderDataStartDate && !calenderDataEndDate){
      setCalenderDataStartDate(selection.startDate);
    }else if (calenderDataStartDate && !calenderDataEndDate){
      setCalenderDataStartDate(selection.endDate);
      let customDateFilter = {
        filterType : rangeType
      }
      sessionStorage.setItem("customDateFilter",JSON.stringify(customDateFilter))
      setTimeout(() => {
        setIsModalOpen(false);
        setSelectionRange(selection);
      }, 200);

    }else if (calenderDataStartDate && calenderDataEndDate){
      setCalenderDataStartDate(selection.startDate);
      setCalenderDataStartDate(undefined);
    }
      
    setSelectionRange(selection);
    console.log(isStartDateFocused(focusedRange[1]))
  };

  const selectRangeType = (value) => {
    setRangeType(value);
    setCalenderDataStartDate(undefined);
    setCalenderDataEndDate(undefined);
  }

  const decreaseYearRange = () =>{
    let tempStartYearRange = startYearRange;
    let tempEndYearRange = endYearRange;
    tempEndYearRange = tempStartYearRange-1;
    tempStartYearRange = tempStartYearRange-12;
    setStartYearRange(tempStartYearRange);
    setEndYearRange(tempEndYearRange);
  }

  const increaseYearRange = () =>{
    let tempStartYearRange = startYearRange;
    let tempEndYearRange = endYearRange;
    tempStartYearRange = (tempEndYearRange+1)>currentYear?currentYear:tempEndYearRange+1;
    tempEndYearRange = (tempEndYearRange+12)>currentYear?currentYear:tempEndYearRange+12;
    setStartYearRange(tempStartYearRange);
    setEndYearRange(tempEndYearRange);
  }

  const onMonthYearChange = (type) =>{
    let data = monthCurrentYear;
    if (type === "+"){
      data++;
    }else{
      data--;
    }
    setMonthCurrentYear(data);
  }

  const onYearsSelect = (e) =>{
    let year = e.target.className.split(' ').pop();
    let tempCalenderData = calenderData;
    if (!calenderData.startDate && !calenderData.endDate){
      tempCalenderData.startDate = year;
    }else if (!calenderData.endDate){
      tempCalenderData.endDate = year;

      if (tempCalenderData.endDate < calenderData.startDate){
        tempCalenderData.endDate = calenderData.startDate;
        tempCalenderData.startDate = year;
      }

    }else if (calenderData.startDate && calenderData.endDate){
      tempCalenderData.startDate = year;
      tempCalenderData.endDate = null;
    }
    setCalenderData(tempCalenderData);
    setCalenderDataStartDate(tempCalenderData.startDate);
    setCalenderDataEndDate(tempCalenderData.endDate);
  }

  const onMonthSelect = (e) =>{
    let year = e.target.className.split(' ').pop();
    let tempCalenderData = calenderData;
    if (!calenderData.startDate && !calenderData.endDate){
      tempCalenderData.startDate = year;
    }else if (!calenderData.endDate){
      tempCalenderData.endDate = year;
      let selection = {};
      let [tempStartYear, tempStartMonth] = calenderData.startDate.split('-');
      let [tempEndYear, tempEndMonth] = tempCalenderData.endDate.split('-');
      let months = {"Jan": 0,"Feb": 1,"Mar": 2,"Apr": 3,"May": 4,"Jun": 5,"Jul": 6,"Aug": 7,"Sep": 8,"Oct": 9,"Nov": 10,"Dec": 11};
      selection.startDate = new Date(tempStartYear,months[tempStartMonth], 1);
      selection.endDate = new Date(tempEndYear,months[tempEndMonth]+1, 0);

      if (selection.endDate < selection.startDate){
        tempCalenderData.endDate = calenderData.startDate;
        tempCalenderData.startDate = year;
      }

    }else if (calenderData.startDate && calenderData.endDate){
      tempCalenderData.startDate = year;
      tempCalenderData.endDate = null;
    }
    setCalenderData(tempCalenderData);
    setCalenderDataStartDate(tempCalenderData.startDate);
    setCalenderDataEndDate(tempCalenderData.endDate);
  }

  useEffect(()=>{
    let type = rangeType;
    let year = startYearRange;
    let dispayRange = [];
    let months = {"Jan": 0,"Feb": 1,"Mar": 2,"Apr": 3,"May": 4,"Jun": 5,"Jul": 6,"Aug": 7,"Sep": 8,"Oct": 9,"Nov": 10,"Dec": 11};
    if (type === "month"){
      let monthList = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
      monthList.map((ele,index)=>{
        let className;
        if (calenderDataStartDate === `${monthCurrentYear}-${ele}` || calenderDataEndDate === `${monthCurrentYear}-${ele}`){
          className = "selected";
        }
        if (calenderDataEndDate){
          let [tempStartYear, tempStartMonth] = calenderDataStartDate.split('-');
          let [tempEndYear, tempEndMonth] = calenderDataEndDate.split('-');
          let [tempCurrentYear, tempCurrentMonth] = `${monthCurrentYear}-${ele}`.split('-');
          let startDate = new Date(tempStartYear,months[tempStartMonth], 1);
          let endDate = new Date(tempEndYear,months[tempEndMonth], 1);
          let currentDate = new Date(tempCurrentYear,months[tempCurrentMonth], 1);
          if (currentDate>startDate && currentDate<endDate){
            className = "selected";
          }
        }
        dispayRange.push(
          <div className={`range-box ${className} ${monthCurrentYear}-${ele}`} onClick={(e)=>{onMonthSelect(e)}} style={{width: "50px",height: "50px",margin: "10px",lineHeight: "50px",textAlign: "center",border: "1px solid",cursor: "pointer", borderRadius: "4px",backgroundColor: className === "selected" ? "rgb(61, 145, 255)" : "white",color: className === "selected" ? "black" : "inherit",fontWeight: className === "selected" ? "bold" : "normal"}}>
            {ele}
          </div>
        )
      })

    }else if (type === "year"){
      let currentYear = endYearRange;
      for (year;year<=currentYear;year++){
        let className;
        if (calenderDataStartDate && year == calenderDataStartDate){
          className = "selected";
        }
        if (calenderData?.endDate && year >= calenderDataStartDate && year <= calenderData.endDate){
          className = "selected";
        }
        dispayRange.push(
          <div className={`range-box ${className} ${year}`} onClick={(e)=>{onYearsSelect(e,year)}} style={{width: "50px",height: "50px",margin: "10px",lineHeight: "50px",textAlign: "center",border: "1px solid",cursor: "pointer", borderRadius: "4px",backgroundColor: className === "selected" ? "rgb(61, 145, 255)" : "white",color: className === "selected" ? "black" : "inherit",fontWeight: className === "selected" ? "bold" : "normal"}}>
            {year}
          </div>
        )
      }
    }
    let calender = <div className="show-display-range" style={{display:"flex", width: "425px", flexWrap:"wrap"}}>{dispayRange}</div>;
    setCalender(calender)
  },[rangeType,startYearRange,endYearRange,calenderDataStartDate,calenderDataEndDate,monthCurrentYear])
 
  return (
    <>
      <div className="mbsm">{t(`ES_DSS_DATE_RANGE`)}</div>
      <div className="employee-select-wrap" ref={wrapperRef}>
        <div className={`select ${isModalOpen ? "dss-input-active-border" : ""}`}>
          <input
            className={`employee-select-wrap--elipses`}
            type="text"
            value={values?.title ? `${values?.title}` : ""}
            readOnly
            onClick={() => setIsModalOpen((prevState) => !prevState)}
          />
          <Calender className="cursorPointer" onClick={() => setIsModalOpen((prevState) => !prevState)} />
        </div>
        {isModalOpen && (
          <div className="options-card" style={{ overflow: "visible", width: "unset", maxWidth: "unset" }}>
            <div className="rdrDateRangePickerWrapper pickerShadow">
              <div className="rdrDefinedRangesWrapper" style={{width:"110px"}}>
                <button type="button" className="rdrStaticRange" style={{fontWeight:rangeType==="week"?"bold":"normal"}} onClick={() => { selectRangeType("week") }}>
                  <span tabindex="-1" className="rdrStaticRangeLabel">By Weeks</span>
                </button>
                <button type="button" className="rdrStaticRange" style={{fontWeight:rangeType==="month"?"bold":"normal"}} onClick={() => { selectRangeType("month") }}>
                  <span tabindex="-1" className="rdrStaticRangeLabel">By Months</span>
                </button>
                <button type="button" className="rdrStaticRange" style={{fontWeight:rangeType==="year"?"bold":"normal"}} onClick={() => { selectRangeType("year") }}>
                  <span tabindex="-1" className="rdrStaticRangeLabel">By Years</span>
                </button>
              </div>
              <div className="rdrCalendarWrapper rdrDateRangeWrapper">
                <div className="rdrDateDisplayWrapper">
                  {/* Condition for Week Type Date Range */}
                  {rangeType === "week" ?
                    <React.Fragment>
                      <DateRange
                        editableDateInputs={true}
                        onChange={handleSelect}
                        moveRangeOnFirstSelection={false}
                        ranges={[selectionRange]}
                        
                      />
                        
                    </React.Fragment> 
                    : ""}
                  {/* Condition for Months Type Date Range */}
                  {rangeType === "month" ?
                    <React.Fragment>
                      <div className="rdrDateDisplay" style={{height:"30px", lineHeight: "30px", color: "#7d888d"}}>
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive" style={{ borderRadius: "4px", boxShadow: "0 1px 2px 0 rgba(35, 57, 66, 0.21)", border: "none"}}>
                          {calenderDataStartDate}
                        </span>
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive" style={{ borderRadius: "4px", boxShadow: "0 1px 2px 0 rgba(35, 57, 66, 0.21)", border: "none"}}>
                          {calenderDataEndDate}
                        </span>
                      </div>
                      <div style={{background: "#fff"}}>
                        <div className="rdrMonthAndYearWrapper" style={{height:"30px", paddingTop:"10"}}>
                          <button type="button" className="rdrNextPrevButton rdrPprevButton" onClick={()=>onMonthYearChange("-")}><i></i></button>
                          <span className="rdrMonthAndYearPickers">{monthCurrentYear}</span>
                          <button type="button" className="rdrNextPrevButton rdrNextButton" onClick={()=>onMonthYearChange("+")}><i></i></button>
                        </div>
                        {calender}
                      </div>
                    </React.Fragment> : ""}
                  {/* Condition for Year Type Date Range */}
                  {rangeType === "year" ?
                    <React.Fragment>
                      <div className="rdrDateDisplay" style={{height:"30px", lineHeight: "30px", color: "#7d888d"}}>
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive" style={{ borderRadius: "4px",boxShadow: "0 1px 2px 0 rgba(35, 57, 66, 0.21)", border: "none"}}>
                          {calenderDataStartDate}
                        </span>
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive" style={{ borderRadius: "4px" ,boxShadow: "0 1px 2px 0 rgba(35, 57, 66, 0.21)", border: "none"}}>
                          {calenderDataEndDate}
                        </span>
                      </div>
                      <div style={{background: "#fff"}}>
                        <div className="rdrMonthAndYearWrapper" style={{height:"30px", paddingTop:"10"}}>
                          <button type="button" className="rdrNextPrevButton rdrPprevButton" onClick={()=>decreaseYearRange()}><i></i></button>
                          <span className="rdrMonthAndYearPickers">{startYearRange} - {endYearRange}</span>
                          <button type="button" className="rdrNextPrevButton rdrNextButton" onClick={()=>increaseYearRange()}><i></i></button>
                        </div>
                        {calender}
                      </div>
                    </React.Fragment> : ""}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default SelectCustomDateRange;
