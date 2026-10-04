import { Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { CustomButton, Menu } from "@nudmcdgnpm/digit-ui-react-components";


const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
  if (searcher == "") return str;
  while (str?.includes(searcher)) {
    str = str?.replace(searcher, replaceWith);
  }
  return str;
};

const ChangeCity = (prop) => {
  const [dropDownData, setDropDownData] = useState(null);
  const [selectCityData, setSelectCityData] = useState([]);
  const [selectedCity, setSelectedCity] = useState([]); //selectedCities?.[0]?.value
  const navigate = Digit.Hooks.useCustomNavigate();
  const isDropdown = prop.dropdown || false;
  let selectedCities = [];

  const handleChangeCity = (city) => {
    const loggedInData = Digit.SessionStorage.get("citizen.userRequestObject");
    const filteredRoles = Digit.SessionStorage.get("citizen.userRequestObject")?.info?.roles?.filter(role => role.tenantId === city.value);
    if (filteredRoles?.length > 0) {
      loggedInData.info.roles = filteredRoles;
      loggedInData.info.tenantId = city?.value;
    }
    Digit.SessionStorage.set("Employee.tenantId", city?.value);
    Digit.UserService.setUser(loggedInData);
    setDropDownData(city);
    if (window.location.href.includes("/upyog-ui/employee/")) {
      const redirectPath = location.state?.from || "/upyog-ui/employee";
      navigate(redirectPath, { replace: true });
    }
    window.location.reload();
  };

  useEffect(() => {
    const userloggedValues = Digit.SessionStorage.get("citizen.userRequestObject");
    let teantsArray = [], filteredArray = [];
    userloggedValues?.info?.roles?.forEach(role => teantsArray.push(role.tenantId));
    let unique = teantsArray.filter((item, i, ar) => ar.indexOf(item) === i);
    unique?.forEach(uniCode => {
      filteredArray.push({
        label: prop?.t(`TENANT_TENANTS_${stringReplaceAll(uniCode, ".", "_")?.toUpperCase()}`),
        value: uniCode
      })
    });
    selectedCities = filteredArray?.filter(select => select.value == Digit.SessionStorage.get("Employee.tenantId"));
    setSelectCityData(filteredArray);
  }, [dropDownData]);

  // if (isDropdown) {
  const currentTenant = Digit.SessionStorage.get("Employee.tenantId");
  const currentCityLabel =
    selectCityData?.find((cityValue) => cityValue.value === (dropDownData?.value || currentTenant))?.label ||
    prop?.t(`TENANT_TENANTS_${stringReplaceAll(currentTenant, ".", "_")?.toUpperCase()}`) ||
    "Select City";

  return (
    <div style={prop?.mobileView ? { color: "#767676" } : {}} className={prop.classes || "nav-city-change"}>
      <Dropdown
        option={selectCityData}
        selected={selectCityData.find((cityValue) => cityValue.value === (dropDownData?.value || currentTenant))}
        optionKey={"label"}
        select={handleChangeCity}
        showArrow={false}
        freeze={true}
        customSelector={
          <div className="nav-city-selector">
            <span className="nav-city-text">{currentCityLabel}</span>
            <svg className="nav-dropdown-chevron" width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
              <path d="M7 10l5 5 5-5z" />
            </svg>
          </div>
        }
      />
    </div>
  );
  // } else {
  //   return (
  //     <React.Fragment>
  //       <div className="custom-style">City</div>
  //       <div className="language-selector" className="custom-style">
  //         {selectCityData?.map((city, index) => (
  //           <div className="language-button-container" key={index}>
  //             <CustomButton
  //               selected={city.value === Digit.SessionStorage.get("Employee.tenantId")}
  //               text={city.label}
  //               onClick={() => handleChangeCity(city)}
  //             ></CustomButton>
  //           </div>
  //         ))}
  //       </div>
  //     </React.Fragment>
  //   );
  // }
};

export default ChangeCity;
