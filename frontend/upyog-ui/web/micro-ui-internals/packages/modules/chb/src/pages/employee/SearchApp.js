import React, { useState,useEffect } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast } from "@upyog/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useParams } from "react-router-dom"
import { useTranslation } from "react-i18next";
import CHBSearchApplication from "../../components/SearchApplication";

/**
 * SearchApp Component
 * 
 * This component is responsible for rendering the search functionality for applications in the CHB module.
 * It allows employees to search for applications based on various parameters such as booking number, date range, status, community hall code, and mobile number.
 * 
 * Props:
 * - `path`: The base path for the search functionality.
 * 
 * Hooks:
 * - `useParams`: Provides access to route parameters, such as `variant`.
 * - `useTranslation`: Provides the `t` function for internationalization.
 * - `useForm`: React Hook Form hook for managing form state and validation.
 * 
 * State Variables:
 * - `payload`: State variable to store the search payload generated from the form inputs.
 * - `showToast`: State variable to manage the visibility and content of toast notifications.
 * 
 * Variables:
 * - `variant`: The variant of the search page, extracted from the route parameters.
 * - `tenantId`: The current tenant ID fetched using the Digit ULB Service.
 * 
 * Functions:
 * - `onSubmit`: Handles the form submission and generates the search payload.
 *    - Extracts `fromDate` and `toDate` from the form data.
 *    - Formats the dates and constructs the search payload.
 *    - Validates the payload to ensure at least one search parameter is provided.
 *    - Displays appropriate toast notifications for validation errors:
 *        - If no search parameters are provided, displays a warning to provide at least one parameter.
 *        - If only one of `fromDate` or `toDate` is provided, displays a warning to provide both dates.
 *    - Updates the `payload` state with the validated search payload.
 * 
 * Logic:
 * - Ensures that the search form is flexible and allows searching by multiple parameters.
 * - Validates the form inputs to prevent incomplete or invalid search queries.
 * 
 * Returns:
 * - A search form with fields for booking number, date range, status, community hall code, and mobile number.
 * - Displays toast notifications for validation errors or warnings.
 */
const SearchApp = ({path}) => {
    const { variant } = useParams();
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [payload, setPayload] = useState({})
    const [showToast, setShowToast] = useState(null);

    function onSubmit (_data) {
        var fromDate=_data?.fromDate
        var toDate=_data?.toDate
        // var fromDate = new Date(_data?.fromDate)
        // fromDate?.setSeconds(fromDate?.getSeconds() - 19800 )
        // var toDate = new Date(_data?.toDate)
        // toDate?.setSeconds(toDate?.getSeconds() + 86399 - 19800)
        const data = {
            ..._data,
            ...(_data.toDate ? {toDate:toDate} : {}),
            ...(_data.fromDate ? {fromDate:fromDate} : {})
        }

        let payload = Object.keys(data).filter( k => data[k] ).reduce( (acc, key) => ({...acc,  [key]: typeof data[key] === "object" ? data[key].code : data[key] }), {} );
        if(Object.entries(payload).length>0 && (!payload.bookingNo && !payload.fromDate && !payload.status && !payload.communityHallCode && !payload.toDate && !payload.mobileNumber))
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

    const { isLoading, isSuccess, isError, error, data: {hallsBookingApplication: searchReult, Count: count} = {} } = Digit.Hooks.chb.useChbSearch(
        { tenantId,
          filters: payload
        },
       config,
      );
    return <React.Fragment>
        <CHBSearchApplication t={t} isLoading={isLoading} tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} data={  isSuccess && !isLoading ? (searchReult.length>0? searchReult : { display: "ES_COMMON_NO_DATA" } ):""} count={count} /> 
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