import React, { useState, useEffect } from "react";
import Select, { components } from "react-select";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { Loader, InfoBannerIcon, Button, Close } from "@egovernments/digit-ui-react-components";
import MDMSSearchv2Popup from "../pages/employee/MDMSSearchv2Popup";

const customStyles = {
  control: (provided, state) => ({
    ...provided,
    borderColor: state.isFocused ? "#f47738" : "#505a5f",
    borderRadius: "unset",
    "&:hover": {
      borderColor: "#f47738",
    },
  }),
  option: (provided, state) => ({
    ...provided,
    backgroundColor: state.isSelected ? "#f47738" : "white", // Background color for selected options
    color: state.isSelected ? "white" : "black", // Text color for selected options
    "&:hover": {
      backgroundColor: "#ffe6cc", // Very light orange background color on hover
    },
  }),
};

/* Multiple support not added TODO jagan to fix this issue */
const CustomSelectWidget = (props) => {
  const { t } = useTranslation();
  const history = useHistory();
  const { moduleName, masterName } = Digit.Hooks.useQueryParams();
  const {
    options,
    value,
    disabled,
    readonly,
    onChange,
    onBlur,
    onFocus,
    placeholder,
    multiple = false,
    schema = { schemaCode: "", fieldPath: "" },
  } = props;
  const { schemaCode = `${moduleName}.${masterName}`, tenantId, fieldPath } = schema;
  const [showTooltipFlag, setShowTooltipFlag] = useState(false);

  const [mainData, setMainData] = useState([]);

  /*
  logic added to fetch data of schemas in each component itself
  */
  const reqCriteriaForData = {
    url: `/${Digit.Hooks.workbench.getMDMSContextPath()}/v2/_search`,
    params: {},
    body: {
      MdmsCriteria: {
        tenantId: tenantId,
        schemaCode: schemaCode,
        limit: 100,
        offset: 0,
      },
    },
    config: {
      enabled: schemaCode && schemaCode?.length > 0,
      select: (data) => {
        const respData = data?.mdms?.map((e) => ({ label: e?.uniqueIdentifier, value: e?.uniqueIdentifier }));
        const finalJSONPath = `registry.rootSchema.properties.${Digit.Utils.workbench.getUpdatedPath(fieldPath)}.enum`;
        if (_.has(props, finalJSONPath)) {
          _.set(
            props,
            finalJSONPath,
            respData?.map((e) => e.value)
          );
        }
        setMainData(data?.mdms);
        return respData;
      },
    },
    changeQueryName: `data-${schemaCode}`,
  };
  const { isLoading, data } = Digit.Hooks.useCustomAPIHook(reqCriteriaForData);
  const optionsList = data || options?.enumOptions || options || [];
  const optionsLimit = 10;
  const formattedOptions = React.useMemo(
    () => optionsList.map((e) => ({ label: t(Digit.Utils.locale.getTransformedLocale(`${schemaCode}_${e?.label}`)), value: e.value })),
    [optionsList, schemaCode, data]
  );
  const [formattedOptions2, setFormattedOptions2] = useState([]);
  const [limitedOptions, setLimitedOptions] = useState([]);
  const [selectedDetails, setSelectedDetails] = useState(null);
  const [showDetails, setShowDetails] = useState(null);
  const [isSelect, setIsSelect] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [isSeeAll, setIsSeeAll] = useState(false);  
  const handleSeeAll = () => {
    setShowModal(true);
  };
  const handleCloseModal = () => {
    setShowModal(false);
  };
  const SelectMenuButton = (props) => {
    return (
      <div>
        <components.MenuList {...props}>{props.children}</components.MenuList>
        <div className="link-container">
          <div onClick={handleSeeAll} className="view-all-link">
          {t("VIEW_ALL")}
          </div>
        </div>
      </div>
    );
  };
  const selectedOption = formattedOptions2?.filter((obj) => (multiple ? value?.includes(obj.value) : obj.value == value));
  const handleChange = (selectedValue) => {
    setShowTooltipFlag(true);
    setIsSelect(true);
    setShowDetails(
      mainData?.filter((obj) => (multiple ? selectedValue.value?.includes(obj.uniqueIdentifier) : obj.uniqueIdentifier == selectedValue.value))
    );
  };
  const handleSelect = (detail) => {
    setShowTooltipFlag(false);
    setIsSelect(false);
    onChange(data ? detail.uniqueIdentifier : detail.value);
    setSelectedDetails([detail]);
  };

  useEffect(() => {
    setLimitedOptions(formattedOptions.slice(0, optionsLimit));
    if (optionsLimit < formattedOptions.length) {
      setIsSeeAll(true);
    }
    setSelectedDetails(mainData?.filter((obj) => (multiple ? value?.includes(obj.uniqueIdentifier) : obj.uniqueIdentifier == value)));

    // Update formattedOptions2
    let newFormattedOptions2 = [...formattedOptions];
    if (value && value !== "") {
      const existingOption = formattedOptions.find((option) => option.value === value);
      if (!existingOption) {
        newFormattedOptions2.push({ value, label: `${schemaCode}_${value}` });
        // const updatedSelectedDetails = mainData?.filter((obj) => (multiple ? value?.includes(obj.uniqueIdentifier) : obj.uniqueIdentifier == value));
        // setSelectedDetails(updatedSelectedDetails);
      }
    }
    setFormattedOptions2(newFormattedOptions2);

  }, [formattedOptions, optionsLimit,value]);
  const onClickSelect = (selectedValue) => {
    selectedValue = { ...selectedValue, value: selectedValue.uniqueIdentifier, label: selectedValue.description };
    onChange(selectedValue.uniqueIdentifier);
    setSelectedDetails(
      mainData?.filter((obj) => (multiple ? selectedValue.value?.includes(obj.uniqueIdentifier) : obj.uniqueIdentifier == selectedValue.value))
    );
    setShowModal(false);
  };
  const handleViewMoreClick = (detail) => {
    const schemaCode = detail?.schemaCode;
    const [moduleName, masterName] = schemaCode.split(".");
    const uniqueIdentifier = detail?.uniqueIdentifier;
    history.push(
      `/${window.contextPath}/employee/workbench/mdms-view?moduleName=${moduleName}&masterName=${masterName}&uniqueIdentifier=${uniqueIdentifier}`
    );
  };
  const OptionWithInfo = (props) => {
    const { data } = props;

    // Find the index of the selected option within limitedOptions
    const index = limitedOptions.findIndex((option) => option.value === data.value);

    const handleInfoBannerClick = () => {
      // Create a singleton array with the selected detail
      const selectedDetail = mainData[index];
      setSelectedDetails([selectedDetail]);
      setShowTooltipFlag(true);
    };

    return (
      <components.Option {...props}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <span>{data.label}</span>
          {/* <span
            style={{ cursor: "pointer" }}
            onClick={handleInfoBannerClick} // Add the click event handler
          >
            <InfoBannerIcon fill={"#f47738"} style={{ marginLeft: "10px" }} />
          </span> */}
        </div>
      </components.Option>
    );
  };

  if (isLoading) {
    return <Loader />;
  }
  return (
    <div className="multiselect">
      <Select
        className="form-control form-select"
        classNamePrefix="digit"
        options={data ? limitedOptions : formattedOptions2}
        isDisabled={disabled || readonly}
        placeholder={placeholder}
        onBlur={onBlur}
        onFocus={onFocus}
        closeMenuOnScroll={true}
        value={selectedOption}
        onChange={data ? handleChange : handleSelect}
        isSearchable={true}
        isMulti={multiple}
        styles={customStyles}
        // components={isSeeAll ? { MenuList: SelectMenuButton, Option: OptionWithInfo } : { Option: OptionWithInfo }}
        components={{ MenuList: SelectMenuButton, Option: OptionWithInfo }}
      />

      <div className="info-icon-container">
        <div
          className="info-icon"
          onClick={() => {
            setShowTooltipFlag(true);
          }}
        >
          {selectedDetails && selectedDetails.length > 0 && data && (
            <span>
              <InfoBannerIcon fill={"#f47738"} />
            </span>
          )}
        </div>
      </div>
      {showTooltipFlag && (
        <div className="option-details">
          {isSelect && (
            <div>
              {showDetails?.map((detail) => (
                <div>
                  <div className="detail-container" key={detail.id}>
                    {Object.keys(detail.data).map((key) => {
                      const value = detail.data[key];
                      if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                        return (
                          <div className="detail-item" key={key}>
                            <div className="key">{t(Digit.Utils.locale.getTransformedLocale(`${schemaCode}_${key}`))}</div>
                            <div className="value">{String(value)}</div>
                          </div>
                        );
                      }
                      return null;
                    })}
                  </div>
                  <div className="select">
                    <Button label={t("WORKBENCH_LABEL_SELECT")} onButtonClick={() => handleSelect(detail)} />
                  </div>
                </div>
              ))}
            </div>
          )}
          {!isSelect && (
            <div>
              {selectedDetails?.map((detail) => (
                <div>
                  <div className="detail-container" key={detail.id}>
                    {Object.keys(detail.data).map((key) => {
                      const value = detail.data[key];
                      if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                        return (
                          <div className="detail-item" key={key}>
                            <div className="key">{t(Digit.Utils.locale.getTransformedLocale(`${schemaCode}_${key}`))}</div>
                            <div className="value">{String(value)}</div>
                          </div>
                        );
                      }
                      return null;
                    })}
                  </div>
                  <div className="view-more">
                    <Button label={t("WORKBENCH_LABEL_VIEW_MORE")} onButtonClick={() => handleViewMoreClick(detail)} />
                  </div>
                </div>
              ))}
            </div>
          )}
          <div
            className="close-button"
            onClick={() => {
              setShowTooltipFlag(false);
              setIsSelect(false);
            }}
          >
            <Close />
          </div>
        </div>
      )}

      {
        showModal && (
          <div className="modal-wrapper">
            <div className="modal-content">
              <div className="modal-inner">
                <MDMSSearchv2Popup masterNameInherited={schema.schemaCode.split(".")[1]} moduleNameInherited={schema.schemaCode.split(".")[0]} onClickSelect={onClickSelect} />
              </div>
              <Button label={"Close"} onButtonClick={handleCloseModal}></Button>
            </div>
          </div>
        )
      }
    </div>
  );
};
export default CustomSelectWidget;
