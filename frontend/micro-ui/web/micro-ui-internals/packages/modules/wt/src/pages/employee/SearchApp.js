import React, { useState,useEffect } from "react"
import {Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useParams } from "react-router-dom"
import { useTranslation } from "react-i18next";
import WTSearchApplication from "../../components/SearchApplication";

/**
 * `SearchApp` is a React component that handles the search functionality for Water Tanker applications.
 * - It allows the user to input search filters such as booking number, date range, and status.
 * - Displays a toast notification for validation errors when invalid or incomplete input is provided.
 * - Submits the search query to the `useTankerSearchAPI` hook and displays the results using the `WTSearchApplication` component.
 * - Handles loading, success, and error states of the API call.
 * 
 * @param {string} path - The base path for the routes (not currently used in the component).
 * @returns {JSX.Element} The search interface with the results and error handling.
 */
const SearchApp = ({path}) => {
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

        let payload = Object.keys(data).filter( k => data[k] ).reduce( (acc, key) => ({...acc,  [key]: typeof data[key] === "object" ? data[key].code : data[key] }), {} );
        if(Object.entries(payload).length>0 && (!payload.bookingNo && !payload.fromDate && !payload.status  && !payload.toDate && !payload.mobileNumber))
        setShowToast({ warning: true, label: "ERR_PROVIDE_ONE_PARAMETERS" });
        else if(Object.entries(payload).length>0 && (payload.fromDate && !payload.toDate) || (!payload.fromDate && payload.toDate))
        setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        else
        setPayload(payload)
    }
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

    const { isLoading, isSuccess, isError, error, data: {waterTankerBookingDetail: searchReult, Count: count} = {} } = Digit.Hooks.wt.useTankerSearchAPI(
        { tenantId,
          filters: payload
        },
       config,
      );
    return <React.Fragment>
        <WTSearchApplication t={t} isLoading={isLoading} tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} data={  isSuccess && !isLoading ? (searchReult.length>0? searchReult : { display: "ES_COMMON_NO_DATA" } ):""} count={count} /> 
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