import React, { useState,useEffect } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useParams } from "react-router-dom"
import { useTranslation } from "react-i18next";
import ADSSearchApplication from "../../components/SearchApplication";

/**
 * SearchApp used for searching applications within the ADS. 
 * This component renders a search form and results display for ADS applications, 
 * including form inputs for date range, booking number, status, and other search filters. 
 * .**/

const SearchApp = ({path}) => {
    const { variant } = useParams();
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [payload, setPayload] = useState({})
    const [showToast, setShowToast] = useState(null);

    function onSubmit (_data) {
        var fromDate=_data?.fromDate
        var toDate=_data?.toDate
        const data = {
            ..._data,
            ...(_data.toDate ? {toDate:toDate} : {}),
            ...(_data.fromDate ? {fromDate:fromDate} : {})
        }

        // Filter out empty values and convert objects to codes
        let payload = Object.keys(data).reduce((acc, key) => {
            const value = data[key];
            // Skip if value is empty, null, undefined, or just whitespace
            if (!value || (typeof value === 'string' && !value.trim())) return acc;
            // Convert object to code, otherwise use value as-is
            return {...acc, [key]: typeof value === "object" ? value.code : value};
        }, {});
        
        // Check if any actual search field is provided (excluding pagination fields)
        const hasSearchFields = payload.bookingNo || payload.fromDate || payload.status || payload.applicantName || payload.faceArea || payload.toDate || payload.mobileNumber;
        
        if(!hasSearchFields)
        setShowToast({ warning: true, label: "ERR_PROVIDE_ONE_PARAMETERS" });
        else if((payload.fromDate && !payload.toDate) || (!payload.fromDate && payload.toDate))
        setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        else
        setPayload(payload)
    }

    const onClear = () => {
        setPayload({});
    };
    useEffect(() => {
      if (showToast) {
        const timer = setTimeout(() => {
          setShowToast(null);
        }, 1000); // Close toast after 1 seconds
        return () => clearTimeout(timer); // Clear timer on cleanup
      }
    }, [showToast]);

    const config = {
        enabled: !!( payload && Object.keys(payload).length > 0 )
    }

    const { isLoading, isSuccess, isError, error, data: {bookingApplication: searchReult, Count: count} = {} } = Digit.Hooks.ads.useADSSearch(
        { tenantId,
          filters: payload
        },
       config,
      );
    return <React.Fragment>
        <ADSSearchApplication t={t} isLoading={isLoading} tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} onClear={onClear} data={  isSuccess && !isLoading ? (searchReult.length>0? searchReult : { display: "ES_COMMON_NO_DATA" } ):""} count={count} /> 
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

}

export default SearchApp