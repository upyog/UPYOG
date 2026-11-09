import { Calender } from "@nudmcdgnpm/digit-ui-react-components";
import {
  format,
  startOfWeek,
  endOfWeek
} from "date-fns";
import React, { useEffect, useCallback, useMemo, useRef, useState } from "react";
import { DateRange } from "react-date-range";

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

const getFYStartYear = (date) => {
  if (!date) return new Date().getFullYear();
  const d = date instanceof Date ? date : new Date(date);
  if (isNaN(d.getTime())) return new Date().getFullYear();
  return d.getMonth() < 3 ? d.getFullYear() - 1 : d.getFullYear();
};

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

const SelectCustomDateRange = ({ values, onFilterChange, t }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectionRange, setSelectionRange] = useState(() => ({
    startDate: values?.startDate && isValidDate(values.startDate) ? toValidDate(values.startDate) : new Date(),
    endDate: values?.endDate && isValidDate(values.endDate) ? toValidDate(values.endDate) : new Date(),
    interval: values?.interval || "",
    title: values?.title || ""
  }));
  const [rangeType, setRangeType] = useState("year");
  const currentYear = useMemo(() => new Date().getFullYear(), []);
  const [startYearRange, setStartYearRange] = useState(() => new Date().getFullYear() - 11);
  const [endYearRange, setEndYearRange] = useState(() => new Date().getFullYear());
  const [monthCurrentYear, setMonthCurrentYear] = useState(() => new Date().getFullYear());
  const [calenderDataStartDate, setCalenderDataStartDate] = useState();
  const [calenderDataEndDate, setCalenderDataEndDate] = useState();
  const [hoverYear, setHoverYear] = useState(null);
  const [calenderData, setCalenderData] = useState({
    endDate: "", interval: "", startDate: "", title: ""
  });
  const [calender, setCalender] = useState();

  const wrapperRef = useRef(null);

  useEffect(() => {
    if (values?.startDate && values?.endDate && isValidDate(values.startDate) && isValidDate(values.endDate)) {
      const s = toValidDate(values.startDate);
      const e = toValidDate(values.endDate);
      setSelectionRange((prev) => {
        if (
          prev?.startDate &&
          prev?.endDate &&
          prev.startDate.getTime?.() === s.getTime() &&
          prev.endDate.getTime?.() === e.getTime() &&
          prev.title === values?.title
        ) {
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

  const applySelection = useCallback((selection, filterTypeValue) => {
    if (!selection || !isValidDate(selection.startDate) || !isValidDate(selection.endDate)) return;
    const startDate = toValidDate(selection.startDate);
    const endDate = toValidDate(selection.endDate);
    const interval = selection.interval || getDuration(startDate, endDate);
    const startFormatted = safeFormat(startDate, "MMM d, yyyy");
    const endFormatted = safeFormat(endDate, "MMM d, yyyy");
    const title = selection.title || `${startFormatted} - ${endFormatted}`;

    const newRange = { startDate, endDate, interval, title };
    const filterData = {
      range: newRange,
      requestDate: {
        startDate: startDate.getTime(),
        endDate: endDate.getTime(),
        interval,
        title
      }
    };

    if (filterTypeValue) {
      sessionStorage.setItem("customDateFilter", JSON.stringify({ filterType: filterTypeValue }));
    }

    setSelectionRange(newRange);
    setIsModalOpen(false);
    if (onFilterChange) {
      onFilterChange(filterData);
    }
  }, [onFilterChange]);

  useEffect(() => {
    if (isModalOpen) {
      if (rangeType === "year") {
        const startFy = getFYStartYear(selectionRange?.startDate);
        const endFy = getFYStartYear(selectionRange?.endDate);
        setCalenderDataStartDate(String(startFy));
        setCalenderDataEndDate(String(endFy));
        setHoverYear(null);
        setCalenderData({
          startDate: String(startFy),
          endDate: String(endFy),
          interval: "month",
          title: selectionRange?.title || (startFy === endFy
            ? `FY ${startFy}-${(startFy + 1).toString().slice(-2)}`
            : `FY ${startFy}-${(startFy + 1).toString().slice(-2)} - FY ${endFy}-${(endFy + 1).toString().slice(-2)}`)
        });
        if (startFy < startYearRange || endFy > endYearRange) {
          const newEnd = Math.min(Math.max(endFy, startFy + 11), currentYear);
          setEndYearRange(newEnd);
          setStartYearRange(newEnd - 11);
        }
      } else if (rangeType === "month") {
        const s = toValidDate(selectionRange?.startDate);
        const e = toValidDate(selectionRange?.endDate);
        const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        const sStr = `${s.getFullYear()}-${monthNames[s.getMonth()]}`;
        const eStr = `${e.getFullYear()}-${monthNames[e.getMonth()]}`;
        setMonthCurrentYear(s.getFullYear());
        setCalenderDataStartDate(sStr);
        setCalenderDataEndDate(eStr);
        setCalenderData({
          startDate: sStr,
          endDate: eStr,
          interval: "month",
          title: selectionRange?.title || ""
        });
      }
    }
  }, [isModalOpen, rangeType]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        if (rangeType === "year" && calenderDataStartDate && !calenderDataEndDate) {
          const yearNum = parseInt(calenderDataStartDate);
          const startDate = new Date(yearNum, 3, 1, 0, 0, 0, 0);
          const endDate = new Date(yearNum + 1, 2, 31, 23, 59, 59, 999);
          const title = `FY ${yearNum}-${(yearNum + 1).toString().slice(-2)}`;
          applySelection({ startDate, endDate, interval: "month", title }, "year");
        } else if (rangeType === "month" && calenderDataStartDate && !calenderDataEndDate) {
          let [tempYear, tempMonth] = String(calenderDataStartDate || "").split("-");
          const months = { "Jan": 0, "Feb": 1, "Mar": 2, "Apr": 3, "May": 4, "Jun": 5, "Jul": 6, "Aug": 7, "Sep": 8, "Oct": 9, "Nov": 10, "Dec": 11 };
          let s = new Date(parseInt(tempYear), months[tempMonth] || 0, 1, 0, 0, 0, 0);
          let end = new Date(parseInt(tempYear), (months[tempMonth] || 0) + 1, 0, 23, 59, 59, 999);
          const title = `${safeFormat(s, "MMM yyyy")} - ${safeFormat(end, "MMM yyyy")}`;
          applySelection({ startDate: s, endDate: end, interval: "month", title }, "month");
        } else {
          setIsModalOpen(false);
        }
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [wrapperRef, rangeType, calenderDataStartDate, calenderDataEndDate, applySelection]);

  const handleSelect = (ranges) => {
    let { range1 } = ranges;
    let s = startOfWeek(range1.startDate);
    let e = endOfWeek(range1.endDate);
    let selection = {
      startDate: s,
      endDate: e,
      interval: "week",
      title: `${safeFormat(s, "MMM d, yyyy")} - ${safeFormat(e, "MMM d, yyyy")}`
    };

    setSelectionRange(selection);

    if (range1.startDate && range1.endDate && range1.startDate.getTime() !== range1.endDate.getTime()) {
      setTimeout(() => {
        applySelection(selection, "week");
      }, 200);
    }
  };

  const selectRangeType = (value) => {
    setRangeType(value);
    setCalenderDataStartDate(undefined);
    setCalenderDataEndDate(undefined);
    setHoverYear(null);
    setCalenderData({ endDate: "", interval: "", startDate: "", title: "" });
  };

  const decreaseYearRange = () => {
    setEndYearRange((prevEnd) => {
      const newEnd = prevEnd - 12;
      setStartYearRange(newEnd - 11);
      return newEnd;
    });
  };

  const increaseYearRange = () => {
    setEndYearRange((prevEnd) => {
      let newEnd = prevEnd + 12;
      if (newEnd > currentYear) {
        newEnd = currentYear;
      }
      setStartYearRange(newEnd - 11);
      return newEnd;
    });
  };

  const onMonthYearChange = (type) => {
    let data = monthCurrentYear;
    if (type === "+") {
      data++;
    } else {
      data--;
    }
    setMonthCurrentYear(data);
  };

  const onYearsSelect = (event, selectedYear) => {
    let year = selectedYear !== undefined ? String(selectedYear) : (event?.target?.textContent ? event.target.textContent.trim() : "");
    if (!year || isNaN(parseInt(year))) {
      let parts = (event?.target?.className || "").split(" ");
      for (let p of parts) {
        if (/^\d{4}$/.test(p)) {
          year = p;
          break;
        }
      }
    }
    if (!year || isNaN(parseInt(year))) return;

    let tempCalenderData = { ...calenderData };

    if (!tempCalenderData.startDate || (tempCalenderData.startDate && tempCalenderData.endDate)) {
      // First click: select start year of range
      tempCalenderData.startDate = year;
      tempCalenderData.endDate = null;
      setCalenderData(tempCalenderData);
      setCalenderDataStartDate(year);
      setCalenderDataEndDate(undefined);
      setHoverYear(null);
    } else if (!tempCalenderData.endDate) {
      // Second click: select end year of range (can be same year or different year)
      let startYearNum = parseInt(tempCalenderData.startDate);
      let endYearNum = parseInt(year);

      if (endYearNum < startYearNum) {
        const temp = startYearNum;
        startYearNum = endYearNum;
        endYearNum = temp;
      }

      const startDate = new Date(startYearNum, 3, 1, 0, 0, 0, 0);
      const endDate = new Date(endYearNum + 1, 2, 31, 23, 59, 59, 999);
      const title = startYearNum === endYearNum
        ? `FY ${startYearNum}-${(startYearNum + 1).toString().slice(-2)}`
        : `FY ${startYearNum}-${(startYearNum + 1).toString().slice(-2)} - FY ${endYearNum}-${(endYearNum + 1).toString().slice(-2)}`;

      tempCalenderData.startDate = String(startYearNum);
      tempCalenderData.endDate = String(endYearNum);
      tempCalenderData.title = title;

      setCalenderData(tempCalenderData);
      setCalenderDataStartDate(String(startYearNum));
      setCalenderDataEndDate(String(endYearNum));
      setHoverYear(null);

      setTimeout(() => {
        applySelection({
          startDate,
          endDate,
          interval: "month",
          title
        }, "year");
      }, 150);
    }
  };

  const onMonthSelect = (event, selectedMonthYear) => {
    let year = selectedMonthYear;
    if (!year) {
      let text = event?.target?.textContent?.trim?.();
      if (text && monthCurrentYear) {
        year = `${monthCurrentYear}-${text}`;
      }
    }
    if (!year) return;

    let tempCalenderData = { ...calenderData };
    if (!tempCalenderData.startDate || (tempCalenderData.startDate && tempCalenderData.endDate)) {
      tempCalenderData.startDate = year;
      tempCalenderData.endDate = null;
      setCalenderData(tempCalenderData);
      setCalenderDataStartDate(year);
      setCalenderDataEndDate(undefined);
    } else if (!tempCalenderData.endDate) {
      tempCalenderData.endDate = year;
      let [tempStartYear, tempStartMonth] = String(tempCalenderData.startDate || "").split("-");
      let [tempEndYear, tempEndMonth] = String(tempCalenderData.endDate || "").split("-");
      const months = { "Jan": 0, "Feb": 1, "Mar": 2, "Apr": 3, "May": 4, "Jun": 5, "Jul": 6, "Aug": 7, "Sep": 8, "Oct": 9, "Nov": 10, "Dec": 11 };

      let startDate = new Date(parseInt(tempStartYear), months[tempStartMonth] || 0, 1, 0, 0, 0, 0);
      let endMonthIdx = months[tempEndMonth] !== undefined ? months[tempEndMonth] + 1 : 1;
      let endDate = new Date(parseInt(tempEndYear), endMonthIdx, 0, 23, 59, 59, 999);

      if (endDate < startDate) {
        const temp = startDate;
        startDate = endDate;
        endDate = temp;
        const tempStr = tempCalenderData.startDate;
        tempCalenderData.startDate = tempCalenderData.endDate;
        tempCalenderData.endDate = tempStr;
      }

      const title = `${safeFormat(startDate, "MMM yyyy")} - ${safeFormat(endDate, "MMM yyyy")}`;
      tempCalenderData.title = title;
      setCalenderData(tempCalenderData);
      setCalenderDataStartDate(tempCalenderData.startDate);
      setCalenderDataEndDate(tempCalenderData.endDate);

      setTimeout(() => {
        applySelection({
          startDate,
          endDate,
          interval: "month",
          title
        }, "month");
      }, 150);
    }
  };

  useEffect(() => {
    let type = rangeType;
    let dispayRange = [];
    if (type === "month") {
      let monthList = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
      const months = { "Jan": 0, "Feb": 1, "Mar": 2, "Apr": 3, "May": 4, "Jun": 5, "Jul": 6, "Aug": 7, "Sep": 8, "Oct": 9, "Nov": 10, "Dec": 11 };
      monthList.forEach((ele) => {
        let isSelected = false;
        const currentItem = `${monthCurrentYear}-${ele}`;
        if (calenderDataStartDate === currentItem || calenderDataEndDate === currentItem) {
          isSelected = true;
        } else if (calenderDataStartDate && calenderDataEndDate) {
          let [sYear, sMonth] = calenderDataStartDate.split("-");
          let [eYear, eMonth] = calenderDataEndDate.split("-");
          let sDate = new Date(parseInt(sYear), months[sMonth], 1);
          let eDate = new Date(parseInt(eYear), months[eMonth], 1);
          let curDate = new Date(monthCurrentYear, months[ele], 1);
          if (curDate >= sDate && curDate <= eDate) {
            isSelected = true;
          }
        }
        dispayRange.push(
          <div
            key={currentItem}
            onClick={(e) => onMonthSelect(e, currentItem)}
            className={`range-box ${currentItem} ${isSelected ? "selected dss-range-box--selected" : "dss-range-box--unselected"} dss-range-box-base`}
          >
            {ele}
          </div>
        );
      });
    } else if (type === "year") {
      const startNum = parseInt(calenderDataStartDate);
      const endNum = parseInt(calenderDataEndDate);
      for (let y = startYearRange; y <= endYearRange; y++) {
        let isSelected = false;
        if (!isNaN(startNum) && isNaN(endNum)) {
          if (hoverYear) {
            const minY = Math.min(startNum, hoverYear);
            const maxY = Math.max(startNum, hoverYear);
            isSelected = y >= minY && y <= maxY;
          } else {
            isSelected = y === startNum;
          }
        } else if (!isNaN(startNum) && !isNaN(endNum)) {
          const minY = Math.min(startNum, endNum);
          const maxY = Math.max(startNum, endNum);
          isSelected = y >= minY && y <= maxY;
        }
        dispayRange.push(
          <div
            key={y}
            onClick={(e) => onYearsSelect(e, y)}
            onMouseEnter={() => {
              if (calenderDataStartDate && !calenderDataEndDate) {
                setHoverYear(y);
              }
            }}
            onMouseLeave={() => {
              if (hoverYear) {
                setHoverYear(null);
              }
            }}
            className={`range-box ${y} ${isSelected ? "selected dss-range-box--selected" : "dss-range-box--unselected"} dss-range-box-base`}
          >
            {y}
          </div>
        );
      }
    }
    let calenderElement = <div className="show-display-range dss-custom-date-range-flex-container">{dispayRange}</div>;
    setCalender(calenderElement);
  }, [rangeType, startYearRange, endYearRange, calenderDataStartDate, calenderDataEndDate, hoverYear, monthCurrentYear]);

  return (
    <>
      <div className="mbsm">{t(`ES_DSS_DATE_RANGE`)}</div>
      <div className="employee-select-wrap" ref={wrapperRef}>
        <div className={`select ${isModalOpen ? "dss-input-active-border" : ""}`}>
          <input
            className="employee-select-wrap--elipses"
            type="text"
            value={values?.title || selectionRange?.title || ""}
            readOnly
            onClick={() => setIsModalOpen((prevState) => !prevState)}
          />
          <Calender className="cursorPointer" onClick={() => setIsModalOpen((prevState) => !prevState)} />
        </div>
        {isModalOpen && (
          <div className="options-card dss-custom-date-range-wrapper">
            <div className="rdrDateRangePickerWrapper pickerShadow">
              <div className="rdrDefinedRangesWrapper dss-custom-date-range-wrapper-2">
                <button type="button" className={`rdrStaticRange ${rangeType === "week" ? "font-weight-bold" : "font-weight-normal"}`} onClick={() => selectRangeType("week")}>
                  <span tabIndex="-1" className="rdrStaticRangeLabel">By Weeks</span>
                </button>
                <button type="button" className={`rdrStaticRange ${rangeType === "month" ? "font-weight-bold" : "font-weight-normal"}`} onClick={() => selectRangeType("month")}>
                  <span tabIndex="-1" className="rdrStaticRangeLabel">By Months</span>
                </button>
                <button type="button" className={`rdrStaticRange ${rangeType === "year" ? "font-weight-bold" : "font-weight-normal"}`} onClick={() => selectRangeType("year")}>
                  <span tabIndex="-1" className="rdrStaticRangeLabel">By Years</span>
                </button>
              </div>
              <div className="rdrCalendarWrapper rdrDateRangeWrapper">
                <div className="rdrDateDisplayWrapper">
                  {/* Condition for Week Type Date Range */}
                  {rangeType === "week" ? (
                    <React.Fragment>
                      <DateRange
                        editableDateInputs={true}
                        onChange={handleSelect}
                        moveRangeOnFirstSelection={false}
                        ranges={[selectionRange]}
                      />
                    </React.Fragment>
                  ) : null}
                  {/* Condition for Months Type Date Range */}
                  {rangeType === "month" ? (
                    <React.Fragment>
                      <div className="rdrDateDisplay dss-custom-date-range-wrapper-3">
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive dss-custom-date-range-wrapper-4">
                          {calenderDataStartDate || ""}
                        </span>
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive dss-custom-date-range-wrapper-4">
                          {calenderDataEndDate || calenderDataStartDate || ""}
                        </span>
                      </div>
                      <div className="dss-custom-date-range-wrapper-5">
                        <div className="rdrMonthAndYearWrapper dss-custom-date-range-spacing">
                          <button type="button" className="rdrNextPrevButton rdrPprevButton" onClick={() => onMonthYearChange("-")}>
                            <i></i>
                          </button>
                          <span className="rdrMonthAndYearPickers">{monthCurrentYear}</span>
                          <button type="button" className="rdrNextPrevButton rdrNextButton" onClick={() => onMonthYearChange("+")}>
                            <i></i>
                          </button>
                        </div>
                        {calender}
                      </div>
                    </React.Fragment>
                  ) : null}
                  {/* Condition for Year Type Date Range */}
                  {rangeType === "year" ? (
                    <React.Fragment>
                      <div className="rdrDateDisplay dss-custom-date-range-wrapper-3">
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive dss-custom-date-range-wrapper-4">
                          {calenderDataStartDate ? `FY ${calenderDataStartDate}-${(parseInt(calenderDataStartDate) + 1).toString().slice(-2)}` : ""}
                        </span>
                        <span className="rdrDateInput rdrDateDisplayItem rdrDateDisplayItemActive dss-custom-date-range-wrapper-4">
                          {calenderDataEndDate
                            ? `FY ${calenderDataEndDate}-${(parseInt(calenderDataEndDate) + 1).toString().slice(-2)}`
                            : (hoverYear && calenderDataStartDate
                              ? `FY ${hoverYear}-${(parseInt(hoverYear) + 1).toString().slice(-2)}`
                              : (calenderDataStartDate ? t("DSS_SELECT_END_YEAR", "Select End Year") : ""))}
                        </span>
                      </div>
                      <div className="dss-custom-date-range-wrapper-5">
                        <div className="rdrMonthAndYearWrapper dss-custom-date-range-spacing">
                          <button type="button" className="rdrNextPrevButton rdrPprevButton" onClick={decreaseYearRange}>
                            <i></i>
                          </button>
                          <span className="rdrMonthAndYearPickers">{startYearRange} - {endYearRange}</span>
                          <button type="button" className="rdrNextPrevButton rdrNextButton" onClick={increaseYearRange}>
                            <i></i>
                          </button>
                        </div>
                        {calender}
                      </div>
                    </React.Fragment>
                  ) : null}
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
