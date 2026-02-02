import { Dropdown, FormComposer, InfoBannerIcon, Loader, Localities, RadioButtons, Toast } from "@upyog/digit-ui-react-components";
import _ from "lodash";
import PropTypes from "prop-types";
import React, { useEffect, useLayoutEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory ,Link } from "react-router-dom";

const description = {
  description: "PT_SEARCH_OR_DESC",
  descriptionStyles: {
    fontWeight: "300  ",
    color: "#505A5F",
    marginTop: "0px",
    textAlign: "center",
    marginBottom: "20px",
    maxWidth: "540px",
  },
};

const SearchAssessment = ({ config: propsConfig, onSelect }) => {
  const { t } = useTranslation();
  const history = useHistory();
  const { action = 0 } = Digit.Hooks.useQueryParams();
  const [searchData, setSearchData] = useState({});
  const [showToast, setShowToast] = useState(null);
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(null);


  const allCities = Digit.Hooks.pt.useTenants()?.sort((a, b) => a?.i18nKey?.localeCompare?.(b?.i18nKey));
  const { isLoading: financialYearsLoading, data: financialYearsData } = Digit.Hooks.pt.useMDMS(
    Digit.ULBService.getStateId(),
    '',
    "FINANCIAL_YEARLS",
    {},
    {
      details: {
        tenantId: Digit.ULBService.getStateId(),
        moduleDetails: [{ moduleName: "egf-master", masterDetails: [{ name: "FinancialYear", filter: "[?(@.module == 'PT')]" }] }],
      },
    }
  );
  useEffect(() => {
    if (financialYearsData && financialYearsData["egf-master"]) {
      setFinancialYears(financialYearsData["egf-master"]?.["FinancialYear"]);
    }
  }, [financialYearsData]);
  const [cityCode, setCityCode] = useState();
  const [formValue, setFormValue] = useState();
  const [errorShown, seterrorShown] = useState(false);
  const { data: propertyData, isLoading: propertyDataLoading, error, isSuccess, billData } = Digit.Hooks.pt.usePropertySearchWithDue({
    tenantId: searchData?.city,
    filters: searchData?.filters,
    auth: true /*  to enable open search set false  */,
    configs: { enabled: Object.keys(searchData).length > 0, retry: false, retryOnMount: false, staleTime: Infinity },
  });

  const isMobile = window.Digit.Utils.browser.isMobile();

  useEffect(() => {
    if ( !(searchData?.filters?.mobileNumber && Object.keys(searchData?.filters)?.length == 1) && 
      propertyData?.Properties.length > 0 &&
      ptSearchConfig.maxResultValidation &&
      propertyData?.Properties.length > ptSearchConfig.maxPropertyResult &&
      !errorShown
    ) {
      setShowToast({ error: true, warning: true, label: "ERR_PLEASE_REFINED_UR_SEARCH" });
    }
  }, [propertyData]);

  useEffect(() => {
    showToast && showToast?.label !== "ERR_PLEASE_REFINED_UR_SEARCH" && setShowToast(null);
  }, [action, propertyDataLoading]);

  useLayoutEffect(() => {
    //Why do we need this? !!!!!
    const getActionBar = () => {
      let el = document.querySelector("div.action-bar-wrap");
      if (el) {
        el.style.position = "static";
        el.style.padding = "8px 0";
        el.style.boxShadow = "none";
        el.style.marginBottom = "16px";
        el.style.textAlign = "left";
        el.style.background = "none";
      } else {
        setTimeout(() => {
          getActionBar();
        }, 100);
      }
    };
    getActionBar();
  }, []);

  const { data: ptSearchConfig, isLoading } = Digit.Hooks.pt.useMDMS(Digit.ULBService.getStateId(), "DIGIT-UI", "HelpText", {
    select: (data) => {
      return data?.["DIGIT-UI"]?.["HelpText"]?.[0]?.PT;
    },
  });

  const [ property ] = propsConfig.inputs;

  const config = [
    {
      body: [
        
        {
          label: "PT_SELECT_CITY",
          isMandatory: true,
          type: "custom",
          populators: {
            name: "city",
            defaultValue: null,
            rules: { required: true },
            customProps: { t, isMandatory: true, option: [...allCities], optionKey: "i18nKey" },
            component: (props, customProps) => (
              <Dropdown
                {...customProps}
                selected={props.value}
                select={(d) => {
                  Digit.LocalizationService.getLocale({
                    modules: [`rainmaker-${props?.value?.code}`],
                    locale: Digit.StoreData.getCurrentLanguage(),
                    tenantId: `${props?.value?.code}`,
                  });
                  if (d.code !== cityCode) props.setValue("locality", null);
                  props.onChange(d);
                }}
              />
            ),
          },
        },
               
        {
          label: property.label,
          
          type: property.type,
          populators: {
            name: property.name,
            defaultValue: "",
            validation: property?.validation,
          },
          isMandatory: true,
          isInsideBox: false,
          placementinbox: 1,
        },
        {
            label: t("ES_PT_FINANCIAL_YEARS"),
            isMandatory: true,
            type: "dropdown",
            populators:<Dropdown isMandatory optionCardStyles={{zIndex: 111111}} selected={selectedFinancialYear} optionKey="name" option={financialYears} select={setSelectedFinancialYear} t={t} />,

        },
      ],
    },
  ];

  const onPropertySearch = async (data) => {
    if (
      ptSearchConfig.maxResultValidation &&
      propertyData?.Properties.length > 0 &&
      propertyData?.Properties.length > ptSearchConfig.maxPropertyResult &&
      errorShown
    ) {
      seterrorShown(true);
      return;
    }
    if (!data?.city?.code) {
      setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS"});
      return;
    }
    if (action == 0) {
      if (!(data?.mobileNumber || data?.propertyIds || data?.oldPropertyId)) {
        setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS" });
        return;
      }
      if (data?.propertyIds && !data.propertyIds?.match(property?.validation?.pattern?.value)) {
        setShowToast({ warning: true, label: property?.validation?.pattern?.message });
        return;
      }
      if (data?.oldPropertyId && !data.oldPropertyId?.match(oldProperty?.validation?.pattern?.value)) {
        setShowToast({ warning: true, label: oldProperty?.validation?.pattern?.message });
        return;
      }
      if (!selectedFinancialYear) {
        setShowToast({ warning: true, label: 'Please select financial year' });
        return;
      } else {
        data.financialYear = selectedFinancialYear
      }
    } else {
      if (!data?.locality?.code) {
        setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS" });
        return;
      }
      if (!(data?.doorNo || data?.name)) {
        setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS" });
        return;
      }

      if (data?.name && !data.name?.match(name?.validation?.pattern?.value)) {
        setShowToast({ warning: true, label: name?.validation?.pattern?.message });
        return;
      }
      if (data?.doorNo && !data.doorNo?.match(doorNo?.validation?.pattern?.value)) {
        setShowToast({ warning: true, label: doorNo?.validation?.pattern?.message });
        return;
      }
    }

    if (showToast?.label !== "ERR_PLEASE_REFINED_UR_SEARCH") setShowToast(null);
    if (data?.doorNo && data?.doorNo !== "" && data?.propertyIds !== "") {
      data["propertyIds"] = "";
    }

    let tempObject = Object.keys(data)
      .filter((k) => data[k])
      .reduce((acc, key) => ({ ...acc, [key]: typeof data[key] === "object" ? data[key].code : data[key] }), {});
    let city = tempObject.city;
    delete tempObject.addParam;
    delete tempObject.addParam1;
    delete tempObject.city;
    setSearchData({ city: city, filters: tempObject, financialYear: selectedFinancialYear });
    return;
  };
  
  const onFormValueChange = (setValue, data, formState) => {
    
    const propId = data?.[property.name];
    const city = data?.city;
    const locality = data?.locality;

    if (city?.code !== cityCode) {
      setCityCode(city?.code);
    }

    if (!_.isEqual(data, formValue)) {
      setFormValue(data);
    }

    if (!locality || !city) {
      return;
    }
  };

  if (isLoading) {
    return <Loader />;
  }

  let validation = ptSearchConfig.maxResultValidation && !(searchData?.filters?.mobileNumber && Object.keys(searchData?.filters)?.length == 1)   ? propertyData?.Properties.length<ptSearchConfig.maxPropertyResult && (showToast == null || (showToast !== null && !showToast?.error)) : true;

  if (propertyData && !propertyDataLoading && !error && validation ) {
    let qs = {};
    qs = { ...searchData.filters, city: searchData.city };

    if ( !(searchData?.filters?.mobileNumber && Object.keys(searchData?.filters)?.length == 1) && 
      ptSearchConfig?.ptSearchCount &&
      searchData?.filters?.locality &&
      propertyDataLoading &&
      propertyDataLoading?.Properties?.length &&
      propertyDataLoading.Properties.length > ptSearchConfig.ptSearchCount
    ) {
      !showToast && setShowToast({ error: true, label: "PT_MODIFY_SEARCH_CRITERIA" });
    } else if (propsConfig.action === "MUTATION") {
      onSelect(propsConfig.key, qs, null, null, null, {
        queryParams: { ...qs },
      });
    } else {
      history.push(
        `/digit-ui/citizen/pt/property/property-assessment/search-assessment-results?${Object.keys(qs)
          .map((key) => `${key}=${qs[key]}`)
          .join("&")}`
      );
    }
  }

  if (error) {
    !showToast && setShowToast({ error: true, label: error?.response?.data?.Errors?.[0]?.code || error });
  }
//   if (action == 1) {
//     config[0].body = [...config[0].body1];
//   }

  return (
    <div style={{ marginTop: "16px", marginBottom: "16px" ,backgroundColor:"#ffffff9e", maxWidth:"960px", borderRadius: "6px"}}>
        {financialYearsLoading ? (
        <Loader />
      ) : (
      <FormComposer
        onSubmit={onPropertySearch}
        noBoxShadow
        inline
        config={config}
        label={propsConfig.texts.submitButtonLabel}
        heading={t(propsConfig.texts.header)}
        text={t(propsConfig.texts.text)}
        headingStyle={{ fontSize: "16px", marginBottom: "16px", fontFamily: "Roboto Condensed,sans-serif" }}
        onFormValueChange={onFormValueChange}
        cardStyle={{marginBottom:"0"}}
      ></FormComposer>
      )}
      {showToast && (
        <Toast
          error={showToast.error}
          isDleteBtn={true}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
            seterrorShown(false);
          }}
        />
      )}
    </div>
  );
};

SearchAssessment.propTypes = {
  loginParams: PropTypes.any,
};

SearchAssessment.defaultProps = {
  loginParams: null,
};

export default SearchAssessment;
