import { Header, Localities, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import PropertyType  from "../../utils/PropertyType";
import React, { memo, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

const PTSearchFields = {
  searchId: {
    propertyIds: {
      type: "text",
      label: "PT_PROPERTY_UNIQUE_ID",
      placeHolder: "PT_PROPERTY_UNIQUE_ID_PLACEHOLDER",
      validation: {
        pattern: {
          value: "/[A-Za-z]{2}\-[A-Za-z]{2}\-[0-9]{4}\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}\-[0-9]{6}|[A-Za-z]{2}\-[A-Za-z]{2}\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}\-[0-9]{6}/,PG-PT-\d\d\d\d-\d\d\d\d-\d\d-\d\d-\d\d\d\d\d\d",
          message: "ERR_INVALID_PROPERTY_ID",
        },
      },
    },
    oldPropertyId: {
      type: "text",
      label: "PT_EXISTING_PROPERTY_ID",
      placeholder: "PT_EXISTING_PROPERTY_ID_PLACEHOLDER",
      validation: {
        pattern: {
          value: "/[A-Za-z]{2}\-[A-Za-z]{2}\-[0-9]{4}\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}\-[0-9]{6}|[A-Za-z]{2}\-[A-Za-z]{2}\-[0-9]{4}\-[0-9]{2}\-[0-9]{2}\-[0-9]{6}/,PG-PT-\d\d\d\d-\d\d\d\d-\d\d-\d\d-\d\d\d\d\d\d",
          message: "ERR_INVALID_PROPERTY_ID",
        },
      },
    },
    mobileNumber: {
      type: "number",
      label: "PT_HOME_SEARCH_RESULTS_OWN_MOB_LABEL",
      placeHolder: "PT_HOME_SEARCH_RESULTS_OWN_MOB_PLACEHOLDER",
      validation: {
        minLength: {
          value: 10,
          message: "CORE_COMMON_MOBILE_ERROR",
        },
        maxLength: {
          value: 10,
          message: "CORE_COMMON_MOBILE_ERROR",
        },
        pattern: {
          value: /[6789][0-9]{9}/,
          message: "CORE_COMMON_MOBILE_ERROR",
        },
      },
    },
    // acknowledgementIds: {
    //   type: "text",
    //   label: "PT_PROPERTY_APPLICATION_NO",
    //   placeholder: "PT_PROPERTY_APPLICATION_NO_PLACEHOLDER",
    //   validation: {
    //     pattern: {
    //       value: /^[a-zA-Z0-9-]*$/i,
    //       message: "ERR_INVALID_PROPERTY_ID",
    //     },
    //   },
    // }
  },
  searchDetail: {
    locality: {
      type: "custom",
      label: "PT_SEARCH_LOCALITY",
      placeHolder: "PT_SEARCH_LOCALITY_PLACEHOLDER",
      validation: {
        required: "PTLOCALITYMANDATORY",
      },
      customComponent: Localities,
      customCompProps: {
        boundaryType: "revenue",
        keepNull: false,
        optionCardStyles: { height: "600px", overflow: "auto", zIndex: "10" },
        disableLoader: true,
      },
    },
    doorNo: {
      type: "text",
      label: "PT_SEARCHPROPERTY_TABEL_DOOR_NO",
      placeHolder: "PT_SEARCH_DOOR_NO_PLACEHOLDER",
      validation: {
        pattern: {
          value:  "[A-Za-z0-9#,/ -()]{1,63}",
          message: "ERR_INVALID_DOOR_NO",
        },
      },
    },
    name: {
      type: "text",
      label: "PT_SEARCHPROPERTY_TABEL_OWNERNAME",
      placeHolder: "PT_SEARCH_OWNER_NAME_PLACEHOLDER",
      validation: {
        minLength: {
          value: 3,
          message: "PT_MIN_3CHAR",
        },
        pattern: {
          value:  "^[a-zA-Z ]+$",
          message: "PAYMENT_INVALID_NAME",
        },
      },
    },
  },
  defaulterNotice:{
    locality: {
      type: "custom",
      label: "PT_SEARCH_LOCALITY",
      placeHolder: "PT_SEARCH_LOCALITY_PLACEHOLDER",
      validation: {
        required: "PTLOCALITYMANDATORY",
      },
      customComponent: Localities,
      customCompProps: {
        boundaryType: "revenue",
        keepNull: false,
        optionCardStyles: { height: "600px", overflow: "auto", zIndex: "10" },
        disableLoader: true,
      },
    },
    propertyType: {
      type: "custom",
      label: "PT_SEARCH_PROPERTY_TYPE",
      placeHolder: "PT_SEARCH_PROPERTY_TYPE_PLACEHOLDER",
      validation: {
        required: "PTPROPERTYTYPEMANDATORY",
      },
      customComponent: PropertyType,
      customCompProps: {
        keepNull: false,
        optionCardStyles: { height: "600px", overflow: "auto", zIndex: "10" },
        disableLoader: true,
      },
    }
    // propertyType: {
    //   type: "propertyType",
    //   label: "PT_SEARCHPROPERTY_TABEL_PROPERTY_TYPE",
    //   placeHolder: "PT_SEARCH_DOOR_NO_PLACEHOLDER",
     
    // },
  },
};

const defaultValues = {
  propertyIds: "",
  mobileNumber: "",
  oldPropertyId: "",
  locality: "",
  name: "",
  doorNo: "",
  propertyType:""
};

const Search = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [payload, setPayload] = useState({});
  const [formData, setFormData] = useState(defaultValues);
  const [searchBy, setSearchBy] = useState("searchId");
  const [showToast, setShowToast] = useState(null);
  const SearchComponent = memo(Digit.ComponentRegistryService.getComponent("PropertySearchForm"));
  const SearchResultComponent = memo(Digit.ComponentRegistryService.getComponent("PropertySearchResults"));
  const SearchPTIDPropComponent = memo(Digit.ComponentRegistryService.getComponent("SearchPTIDProp"));
  const { data: ptSearchConfig, isLoading } = Digit.Hooks.pt.useMDMS(Digit.ULBService.getStateId(), "DIGIT-UI", "HelpText", {
    select: (data) => {
      return data?.["DIGIT-UI"]?.["HelpText"]?.[0]?.PT;
    },
  });
  const onReset = useCallback(() => {
    setFormData(defaultValues);
     setPayload({});
    setShowToast(null);
  });

  useEffect (() =>{
    if(sessionStorage.getItem("searchDetailValue") == 1 && searchBy === "searchId"){
      setSearchBy("searchDetail")
    }
  },[searchBy])
  const onSubmit = useCallback((_data) => {
    console.log("_data",_data)
    if(Object.keys(_data).includes("propertyType"))
    {
      setFormData(_data);
      console.log("_data2",payload)
      setPayload({locality:_data.locality.code, propertyType:_data.propertyType.code})
      console.log("_data3",payload)
    }
    else {
      setFormData(_data);   
      console.log("_data5",formData)  
      if (Object.keys(_data).filter((k) => _data[k] && typeof _data[k] !== "object")) {
        setPayload(
          Object.keys(_data)
            .filter((k) => _data[k])
            .reduce((acc, key) => ({ ...acc, [key]: typeof _data[key] === "object" ? _data[key].code : _data[key] }), {})
        );
        console.log("_data4",payload)
        setShowToast(null);
      } else {
        setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS" });
      }
    }
  
  });
  return (
    <React.Fragment>
      <Header>{t("SEARCH_PROPERTY")}</Header>
      <SearchComponent
        t={t}
        payload={formData}
        searchBy={searchBy}
        setSearchBy={setSearchBy}
        PTSearchFields={PTSearchFields}
        tenantId={tenantId}
        onSubmit={onSubmit}
        onReset={onReset}
      />
      
      {Object.keys(payload).includes("propertyType") ?
      <SearchPTIDPropComponent t={t} showToast={showToast} setShowToast={setShowToast} tenantId={tenantId} payload={payload} ptSearchConfig={{...ptSearchConfig}} />
      : Object.keys(payload).length > 0 ? (
        <SearchResultComponent t={t} showToast={showToast} setShowToast={setShowToast} tenantId={tenantId} payload={payload} ptSearchConfig={{...ptSearchConfig}} />
      ):""}
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

export default Search;
