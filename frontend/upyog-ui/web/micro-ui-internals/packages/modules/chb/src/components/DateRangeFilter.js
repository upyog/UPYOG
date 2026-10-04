import React, { useEffect, useRef, useState } from "react";
import { DateRangePicker } from "react-date-range";
import { differenceInCalendarDays, addMonths, format} from "date-fns";
import { Calender } from "@nudmcdgnpm/digit-ui-react-components";
import "react-date-range/dist/styles.css";
import "react-date-range/dist/theme/default.css";

/**
 * 
 * Reusable Date Range Filter component.
 *
 * Supports single-date and date-range selection with configurable
 * maximum booking days. Useful for booking/search screens where
 * users should not be allowed to select more than a specified
 * number of consecutive days.
 *
 * Example:
 * <DateRangeFilter
 *   value={bookingDate}
 *   maxDays={3}
 *   onChange={setBookingDate}
 *   setShowToast={setShowToast}
 * />
 * 
 * 
 * TODO - Later Move to React Component Library
 */

const DateRangeFilter = ({
  value,
  onChange,
  maxDays = 3,
  setShowToast,
  t
}) => {
  const wrapperRef = useRef(null);

  const [open, setOpen] = useState(false);

  const [selection, setSelection] = useState({
    startDate: value?.startDate || new Date(),
    endDate: value?.endDate || new Date(),
    key: "selection",
  });

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (
        wrapperRef.current &&
        !wrapperRef.current.contains(event.target)
      ) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleOutsideClick);

    return () =>
      document.removeEventListener(
        "mousedown",
        handleOutsideClick
      );
  }, []);

  const handleSelect = (ranges) => {
    const selected = ranges.selection;

    const totalDays =
      differenceInCalendarDays(
        selected.endDate,
        selected.startDate
      ) + 1;

    if (totalDays > maxDays) {
      setShowToast({
        error: true,
        label: `Maximum ${maxDays} days can be selected`,
      });

      return;
    }

    setSelection(selected);

    onChange({
      startDate: selected.startDate,
      endDate: selected.endDate,
      totalDays,
      title:
        totalDays === 1
          ? format(selected.startDate, "dd/MM/yyyy")
          : `${format(
              selected.startDate,
              "dd/MM/yyyy"
            )} - ${format(
              selected.endDate,
              "dd/MM/yyyy"
            )}`,
    });

    if (
      selected.startDate &&
      selected.endDate &&
      totalDays >= 1
    ) {
      setOpen(false);
    }
  };

  return (
    <div
        className="employee-select-wrap chb-date-range-filter-wrapper"
        ref={wrapperRef}
    >
    <div
        className="select chb-date-range-filter-clickable"
        onClick={() => setOpen(!open)}
        >
        <span
            className="employee-select-wrap--elipses"
            className={`employee-select-wrap--elipses ${value?.title ? "text-dark" : "text-muted"}`}
        >
            {value?.title || t("CHB_SELECT_DATES")}
        </span>

        <Calender className="date-range-calendar-icon" />
        </div>

      {open && (
        <div
          className="options-card chb-date-range-filter-wrapper-2"
        >
          <DateRangePicker
            ranges={[selection]}
            onChange={handleSelect}
            moveRangeOnFirstSelection={false}
            retainEndDateOnFirstSelection={true}
            editableDateInputs={false}
            minDate={new Date()}
            maxDate={addMonths(new Date(), 3)}
            staticRanges={[]} // Remove predefined ranges like "Yesterday", "Last 7 Days", etc.
            inputRanges={[]}
          />
        </div>
      )}
    </div>
  );
};

export default DateRangeFilter;