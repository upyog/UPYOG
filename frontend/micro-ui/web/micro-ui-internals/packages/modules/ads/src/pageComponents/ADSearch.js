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
  Toast,
  Card,
  RadioButtons,
  TextInput,
} from "@nudmcdgnpm/digit-ui-react-components";

/**
 * ADSSearch component handles the advertisement search functionality, 
 * allowing users to select advertisement types, locations, dates, 
 * and other criteria. It displays search results in a table format, 
 * supports adding items to a cart, and manages the overall form state 
 * for further processing based on user actions.
 */

import ADSCartDetails from "../components/ADSCartDetails";
const ADSSearch = ({ t, onSelect, config, userType, formData }) => {
  const { pathname: url } = useLocation();
  let index = 0;
  const [cartDetails, setCartDetails] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].cartDetails) || formData?.adslist?.cartDetails || []
  );
  const [adsType, setAdsType] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].adsType) || formData?.adslist?.adsType || ""
  );
  const [selectedLocation, setSelectedLocation] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].selectedLocation) || formData?.adslist?.selectedLocation || ""
  );
  const [selectedFace, setSelectedFace] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].selectedFace) || formData?.adslist?.selectedFace || ""
  );
  const [fromDate, setFromDate] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].fromDate) || formData?.adslist?.fromDate || ""
  );
  const [toDate, setToDate] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].toDate) || formData?.adslist?.toDate || ""
  );
  const [Searchdata, setSearchData] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].Searchdata) || formData?.adslist?.Searchdata || []
  );
  const [showToast, setShowToast] = useState(null);
  // const [hallCode, setHallCode] = useState(
  //   (formData.adslist && formData.adslist[index] && formData.adslist[index].hallCode) || formData?.adslist?.hallCode || ""
  // );
  const [selectNight, setselectNight] = useState(
    (formData.adslist && formData.adslist[index] && formData.adslist[index].selectNight) || formData?.adslist?.selectNight || ""
  );
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [showCartDetails, setShowCartDetails] = useState(false);

  let ADSTypeData = [];
  let LocationData = [];
  let FaceId = [];
  const { data: AdType } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "Advertisement", [{ name: "AdType" }], {
    select: (data) => {
      const formattedData = data?.["Advertisement"]?.["AdType"].map((details) => {
        return { i18nKey: `${details.name}`, code: `${details.code}`, name: `${details.name}`, active: `${details.active}` };
      });
      return formattedData;
    },
  });
  const { data: LocationDetails } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "Advertisement", [{ name: "Location" }], {
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

  AdType &&
    AdType.map((slot) => {
      ADSTypeData.push({ i18nKey: `${slot.name}`, code: `${slot.code}`, value: `${slot.name}` });
    });
  LocationDetails &&
    LocationDetails.map((slot) => {
      LocationData.push({ i18nKey: `${slot.name}`, code: `${slot.code}`, value: `${slot.name}` });
    });
  Face &&
    Face.map((slot) => {
      FaceId.push({ i18nKey: `${slot.name}`, code: `${slot.code}`, value: `${slot.name}` });
    });

  // const { data: slotSearchData, refetch } = Digit.Hooks.chb.useChbSlotSearch({
  //   tenantId: tenantId,
  //   filters: {
  //     communityHallCode: Searchdata.communityHallCode,
  //     bookingStartDate: Searchdata.bookingStartDate,
  //     bookingEndDate: Searchdata.bookingEndDate,
  //     hallCode: Searchdata.hallCode,
  //   },
  // });
  // useEffect(() => {
  //   if (slotSearchData && slotSearchData.hallSlotAvailabiltityDetails) {
  //     const newData = slotSearchData.hallSlotAvailabiltityDetails.map((slot, index) => ({
  //       slotId: index + 1,

  //       name: `${t(slot.communityHallCode)}`,
  //       code: slot.communityHallCode,
  //       hallCode1: slot.hallCode,
  //       address: Searchdata.hallAddress,
  //       hallCode: slot.hallCode + " - " + Searchdata.capacity,
  //       toTime: hallCode.toTime,
  //       fromTime: hallCode.fromTime,
  //       capacity: Searchdata.capacity,
  //       bookingDate: slot.bookingDate,
  //       status: slot.slotStaus === "AVAILABLE" ? <div className="sla-cell-success">Available</div> : <div className="sla-cell-error">Booked</div>,
  //     }));
  //     // Only update state if newData is different from current state
  //     setData((prevData) => {
  //       if (JSON.stringify(prevData) !== JSON.stringify(newData)) {
  //         return newData;
  //       }
  //       return prevData;
  //     });
  //     setShowTable((prevShowTable) => {
  //       if (!prevShowTable) {
  //         return true;
  //       }
  //       return prevShowTable;
  //     });
  //   }
  // }, [slotSearchData, Searchdata]);

  const data = [
  {
    slotId:1,
    name: "Unipolar", 
    address: "Green Unipole 18 X 8",
    hallCode: "Yes",
    bookingDate: "10-05-2024",
    status:<div className="sla-cell-success">Available</div>
  },
  {
    slotId:2,
    name: "Unipolar1", 
    address: "Unipole 12 X 8",
    hallCode: "Yes",
    bookingDate: "10-05-2025",
    status:<div className="sla-cell-success">Available</div>
  }
]
  


  // const [data, setData] = useState("");

 const [showTable, setShowTable] = useState(true); // State to control table visibility

  const [isCheckboxSelected, setIsCheckboxSelected] = useState(false);
  const columns = [
    { Header: `${t("ADS_TYPE")}`, accessor: "name" },
    { Header: `${t("ADS_FACE_AREA")}`, accessor: "address" },
    { Header: `${t("ADS_NIGHT_LIGHT")}`, accessor: "hallCode" },
    { Header: `${t("ADS_DATE")}`, accessor: "bookingDate" },
    { Header: `${t("ADS_STATUS")}`, accessor: "status" },
  ];
  const { control } = useForm();
  const goNext = () => {
    let owner = formData.adslist && formData.adslist[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, cartDetails, adsType, selectedLocation,selectedFace,selectNight,fromDate,toDate };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      ownerStep = { ...owner, cartDetails, adsType,selectNight,selectedLocation,selectedFace,fromDate,toDate };
      onSelect(config.key, ownerStep, false, index);
    }
    console.log("ownerStep",ownerStep);
  };
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
  function SetFromDate(e) {
    setFromDate(e.target.value);
  }

  function SetToDate(e) {
    setToDate(e.target.value);
  }

  const onSkip = () => onSelect();
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [cartDetails, adsType, selectNight,selectedFace,selectedLocation, Searchdata]);
 
  const handleRowSelection = (rowIndex) => {
    setCartDetails((prevSelectedRows) => {
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
          checked={cartDetails.length === data.length}
          disabled={data.every((row) => row.status.props.children !== "Available")}
          onChange={() => {
            if (cartDetails.length === data.length) {
              setCartDetails([]);
              setIsCheckboxSelected(false);
            } else {
              const allRows = data.filter((row) => row.status.props.children === "Available");
              setCartDetails(allRows);
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
          checked={cartDetails.some((selectedRow) => selectedRow.slotId === row.original.slotId)}
          onChange={() => handleRowSelection(row.index)}
          disabled={row.original.status.props.children !== "Available"} // Disable checkbox if status
          // is
        />
      </div>
    ),
  };
  const enhancedColumns = [checkboxColumn, ...columns];
 
  const handleSearch = () => {
    const adsType = adsType?.code || "";
    const startDate = fromDate;
    const endDate = toDate;
  
    if (adsType && startDate && endDate) {
     
      const filters = {
        communityHallCode: selectedHallName,
        bookingStartDate: startDate,
        bookingEndDate: endDate,
        hallAddress: adsType?.address,
        hallCode: selectedHallCode,
        capacity: hallCode?.capacity,
      };
      setSearchData(filters);
    } else {
      setShowToast({ error: true, label: t("CHB_SELECT_COMMUNITY_HALL_DATE_HALLCODE") });
    }
  };

  const handleCartClick= () => {
    if (!isCheckboxSelected) {
      setShowToast({ error: true, label: t("CHB_SELECT_AT_LEAST_ONE_SLOT") });
    } else {
      // Add selected rows to cart
      const selectedRows = data.filter(row => 
        cartDetails.some(cartRow => cartRow.slotId === row.slotId)
      );
      
      // Update the cartDetails state with selected rows
      setCartDetails(prevCart => [...prevCart, ...selectedRows]);
  
      // Optionally, you can also clear the current selection after adding to cart
      setIsCheckboxSelected(false);
      setCartDetails((prev) => {
        return prev.filter(row => !selectedRows.some(selectedRow => selectedRow.slotId === row.slotId));
      });
    }
  };
  
  const handleBookClick = () => {
    if (!isCheckboxSelected) {
      setShowToast({ error: true, label: t("CHB_SELECT_AT_LEAST_ONE_SLOT") });
    } else {
      goNext();
    }
  };
  const handleViewCart = () => {
    setShowCartDetails(true); // Show the cart details
};

const handleCloseCart = () => {
    setShowCartDetails(false); // Close the cart details
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
      setCartDetails([]);
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
          <Controller
            control={control}
            name={"adsType"}
            defaultValue={adsType}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={adsType}
                select={(selected) => {
                  setAdsType(selected);}}
                placeholder={"Select Advertisement Type"}
                option={ADSTypeData}
                optionKey="i18nKey"
                t={t}
              />
            )}
          />
        <CardLabel>
          {`${t("ADS_LOCATION")}`} <span className="check-page-link-button">*</span>
        </CardLabel>
          <Controller
            control={control}
            name={"selectedLocation"}
            defaultValue={selectedLocation}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={selectedLocation}
                select={(selected) => {
                  setSelectedLocation(selected);
                }}
                placeholder={"Select Ad Type"}
                option={LocationData}
                optionKey="i18nKey"
                t={t}
              />
            )}
          />
          <CardLabel>
            {`${t("ADS_FACE_AREA")}`} <span className="check-page-link-button">*</span>
          </CardLabel>
           <Controller
              control={control}
              name={"adsType"}
              defaultValue={selectedFace}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={selectedFace}
                  select={(selected) => {
                    setSelectedFace(selected);
                  }}
                  placeholder={"Select Ad Type"}
                  option={FaceId}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
            <CardLabel>{`${t("ADS_FROM_DATE")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="fromDate"
              value={fromDate}
              onChange={SetFromDate}
              style={{ width: "86%" }}
              min={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}

            />
            <CardLabel>{`${t("ADS_TO_DATE")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="toDate"
              value={toDate}
              onChange={SetToDate}
              style={{ width: "86%" }}
              min={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}

            />

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
        <div style={{ display: "flex", flexDirection: "row", gap: "15px" }}>
          <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
          <SubmitBar label={t("ADS_ADD_TO_CART")} onSubmit={handleCartClick} disabled={!isCheckboxSelected} />
          <SubmitBar label={t("ADS_VIEW_CART")} onSubmit={handleViewCart} />
          {showCartDetails && <ADSCartDetails onClose={handleCloseCart} />}
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