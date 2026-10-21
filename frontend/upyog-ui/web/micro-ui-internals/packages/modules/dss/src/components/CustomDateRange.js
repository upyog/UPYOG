import { Calender } from "@nudmcdgnpm/digit-ui-react-components";
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

  const isValidDate = (d) => {
    if (!d) return false;
    const date = d instanceof Date ? d : new Date(d);
    return !isNaN(date.getTime());
  };

  const toValidDate = (d) => {
    if (!d) return new Date();
    const date = d instanceof Date ? d : new Date(d);
    return isNaN(date.getTime()) ? new Date() : date;
  };

  const safeFormat = (date, formatStr, fallback = "") => {
    if (!date) return fallback;
    const d = date instanceof Date ? date : new Date(date);
    if (isNaN(d.getTime())) return fallback;
    try {
      return format(d, formatStr);
    } catch (e) {
      return fallback;
    }
  };

  useEffect(() => {
    if (values?.startDate && values?.endDate && isValidDate(values.startDate) && isValidDate(values.endDate)) {
      const s = toValidDate(values.startDate);
      const e = toValidDate(values.endDate);
      setSelectionRange((prev) => {
        if (prev?.startDate && prev?.endDate && prev.startDate.getTime?.() === s.getTime() && prev.endDate.getTime?.() === e.getTime()) {
          return prev;
        }
        return {
          startDate: s,
          endDate: e,
          interval: values?.interval || "",
          title: values?.title || ""
        };
      });
    }
  }, [values]);

  useEffect(()=>{
    if (calenderDataEndDate && calenderDataStartDate){
      let selection = null;
      if (rangeType === "year") {
        let startYearNum = parseInt(calenderDataStartDate);
        let endYearNum = parseInt(calenderDataEndDate);
        if (isNaN(startYearNum)) return;
        if (isNaN(endYearNum)) endYearNum = startYearNum;
        if (endYearNum < startYearNum) {
          const temp = startYearNum;
          startYearNum = endYearNum;
          endYearNum = temp;
        }
        const s = new Date(startYearNum, 3, 1, 0, 0, 0, 0);
        const e = new Date(endYearNum + 1, 2, 31, 23, 59, 59, 999);
        selection = {
          startDate: s,
          endDate: e,
          interval: "month",
          title: startYearNum === endYearNum
            ? `FY ${startYearNum}-${(startYearNum + 1).toString().slice(-2)}`
            : `FY ${startYearNum}-${(startYearNum + 1).toString().slice(-2)} - FY ${endYearNum}-${(endYearNum + 1).toString().slice(-2)}`
        };
      } else if (rangeType === "month") {
        let [tempStartYear, tempStartMonth] = String(calenderDataStartDate || "").split('-');
        let [tempEndYear, tempEndMonth] = String(calenderDataEndDate || "").split('-');
        let months = {"Jan": 0,"Feb": 1,"Mar": 2,"Apr": 3,"May": 4,"Jun": 5,"Jul": 6,"Aug": 7,"Sep": 8,"Oct": 9,"Nov": 10,"Dec": 11};
        if (tempStartYear && tempStartMonth && months[tempStartMonth] !== undefined && tempEndYear && tempEndMonth && months[tempEndMonth] !== undefined) {
          let s = new Date(parseInt(tempStartYear), months[tempStartMonth], 1, 0, 0, 0, 0);
          let e = new Date(parseInt(tempEndYear), months[tempEndMonth] + 1, 0, 23, 59, 59, 999);
          if (e < s) {
            const temp = s;
            s = e;
            e = temp;
          }
          selection = {
            startDate: s,
            endDate: e,
            interval: "month",
            title: `${safeFormat(s, "MMM yyyy")} - ${safeFormat(e, "MMM yyyy")}`
          };
        } else {
          return;
        }
      }

      if (!selection) return;

      let customDateFilter = {
        filterType : rangeType
      };
      sessionStorage.setItem("customDateFilter",JSON.stringify(customDateFilter));
      if (isValidDate(selection.startDate) && isValidDate(selection.endDate)) {
        setTimeout(() => {
          setIsModalOpen(false);
          setSelectionRange(selection);
        }, 150);
      }
    }
  },[calenderDataStartDate,calenderDataEndDate,rangeType]);

  const wrapperRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        if (rangeType === "year" && calenderDataStartDate && !calenderDataEndDate) {
          setCalenderDataEndDate(calenderDataStartDate);
        } else if (rangeType === "month" && calenderDataStartDate && !calenderDataEndDate) {
          setCalenderDataEndDate(calenderDataStartDate);
        }
        setIsModalOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [wrapperRef, rangeType, calenderDataStartDate, calenderDataEndDate]);

  useEffect(() => {
    if (!isModalOpen && isValidDate(selectionRange?.startDate) && isValidDate(selectionRange?.endDate)) {
      const startDate = toValidDate(selectionRange?.startDate);
      const endDate = toValidDate(selectionRange?.endDate);
      const interval = getDuration(startDate, endDate);
      const startFormatted = safeFormat(startDate, "MMM d, yyyy");
      const endFormatted = safeFormat(endDate, "MMM d, yyyy");
      const title = selectionRange?.title || `${startFormatted} - ${endFormatted}`;
      onFilterChange({
        range: { startDate, endDate, interval, title },
        requestDate: {
          startDate: startDate.getTime(),
          endDate: endDate.getTime(),
          interval,
          title
        }
      });
    }
  }, [selectionRange, isModalOpen]);


  const getDuration = (startDate, endDate) => {
    const s = toValidDate(startDate);
    const e = toValidDate(endDate);
    let noOfDays = (e.getTime() - s.getTime()) / (1000 * 3600 * 24);
    if (noOfDays > 91) {
      return "month";
    }
    if (noOfDays < 90 && noOfDays >= 14) {
      return "week";
    }
    if (noOfDays <= 14) {
      return "day";
    }
    return "month";
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

  const onYearsSelect = (e, selectedYear) =>{
    let year = selectedYear !== undefined ? String(selectedYear) : (e?.target?.textContent ? e.target.textContent.trim() : "");
    if (!year || isNaN(parseInt(year))) {
      let parts = (e?.target?.className || "").split(' ');
      for (let p of parts) {
        if (/^\d{4}$/.test(p)) {
          year = p;
          break;
        }
      }
    }
    if (!year || isNaN(parseInt(year))) return;

    let tempCalenderData = { ...calenderData };
    if (!tempCalenderData.startDate && !tempCalenderData.endDate){
      tempCalenderData.startDate = year;
    }else if (!tempCalenderData.endDate){
      tempCalenderData.endDate = year;

      if (parseInt(tempCalenderData.endDate) < parseInt(tempCalenderData.startDate)){
        let start = tempCalenderData.startDate;
        tempCalenderData.startDate = tempCalenderData.endDate;
        tempCalenderData.endDate = start;
      }

    }else if (tempCalenderData.startDate && tempCalenderData.endDate){
      tempCalenderData.startDate = year;
      tempCalenderData.endDate = null;
    }
    setCalenderData(tempCalenderData);
    setCalenderDataStartDate(tempCalenderData.startDate);
    setCalenderDataEndDate(tempCalenderData.endDate);
  }

  const onMonthSelect = (e, selectedMonthYear) =>{
    let year = selectedMonthYear;
    if (!year) {
      let text = e?.target?.textContent?.trim?.();
      if (text && monthCurrentYear) {
        year = `${monthCurrentYear}-${text}`;
      }
    }
    if (!year) return;

    let tempCalenderData = { ...calenderData };
    if (!tempCalenderData.startDate && !tempCalenderData.endDate){
      tempCalenderData.startDate = year;
    }else if (!tempCalenderData.endDate){
      tempCalenderData.endDate = year;
      let selection = {};
      let [tempStartYear, tempStartMonth] = String(tempCalenderData.startDate || "").split('-');
      let [tempEndYear, tempEndMonth] = String(tempCalenderData.endDate || "").split('-');
      let months = {"Jan": 0,"Feb": 1,"Mar": 2,"Apr": 3,"May": 4,"Jun": 5,"Jul": 6,"Aug": 7,"Sep": 8,"Oct": 9,"Nov": 10,"Dec": 11};
      let s = new Date(parseInt(tempStartYear), months[tempStartMonth] || 0, 1);
      let endMonthIdx = months[tempEndMonth] !== undefined ? months[tempEndMonth] + 1 : 1;
      let end = new Date(parseInt(tempEndYear), endMonthIdx, 0);

      if (end < s){
        let start = tempCalenderData.startDate;
        tempCalenderData.startDate = tempCalenderData.endDate;
        tempCalenderData.endDate = start;
      }

    }else if (tempCalenderData.startDate && tempCalenderData.endDate){
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
          <div onClick={(e)=>{onMonthSelect(e, `${monthCurrentYear}-${ele}`)}} className={`range-box ${className} ${monthCurrentYear}-${ele} ${className === "selected" ? "dss-range-box--selected" : "dss-range-box--unselected"} dss-range-box-base`}>
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
          <div onClick={(e)=>{onYearsSelect(e,year)}} className={`range-box ${className} ${year} ${className === "selected" ? "dss-range-box--selected" : "dss-range-box--unselected"} dss-range-box-base`}>
            {year}
          </div>
        )
      }
    }
    let calender = <div className="show-display-range dss-custom-date-range-flex-container">{dispayRange}</div>;
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
          <div className="options-card dss-custom-date-range-wrapper">
            <div className="rdrDateRangePickerWrapper pickerShadow">
              <div className="rdrDefinedRangesWrapper dss-custom-date-range-wrapper-2">
                <button type="button" className={`rdrStaticRange ${rangeType === "week" ? "font-weight-bold" : "font-weight-normal"}`} onClick={() => { selectRangeType("week") }}>
                  <span tabIndex="-1" className="rdrStaticRangeLabel">By Weeks</span>
                </button>
                <button type="button" className={`rdrStaticRange ${rangeType === "month" ? "font-weight-bold" : "font-weight-normal"}`} onClick={() => { selectRangeType("month") }}>
                  <span tabIndex="-1" className="rdrStaticRangeLabel">By Months</span>
                </button>
                <button type="button" className={`rdrStaticRange ${rangeType === "year" ? "font-weight-bold" : "font-weight-normal"}`} onClick={() => { selectRangeType("year") }}>
                  <span tabIndex="-1" className="rdrStaticRangeLabel">By Years</span>
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
                      <div className="rdrDateDisplay dss-custom-date-range-wrapper-3">
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive dss-custom-date-range-wrapper-4">
                          {calenderDataStartDate}
                        </span>
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive dss-custom-date-range-wrapper-4">
                          {calenderDataEndDate}
                        </span>
                      </div>
                      <div className="dss-custom-date-range-wrapper-5">
                        <div className="rdrMonthAndYearWrapper dss-custom-date-range-spacing">
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
                      <div className="rdrDateDisplay dss-custom-date-range-wrapper-3">
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive dss-custom-date-range-wrapper-4">
                          {calenderDataStartDate}
                        </span>
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive dss-custom-date-range-wrapper-4">
                          {calenderDataEndDate}
                        </span>
                      </div>
                      <div className="dss-custom-date-range-wrapper-5">
                        <div className="rdrMonthAndYearWrapper dss-custom-date-range-spacing">
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
