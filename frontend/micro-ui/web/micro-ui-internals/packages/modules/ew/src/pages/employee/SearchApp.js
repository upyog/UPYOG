// Importing necessary components and hooks from external libraries and local files
import React, { useState } from "react";
import { Toast } from "@nudmcdgnpm/digit-ui-react-components"; // Component for displaying toast notifications
import { useParams } from "react-router-dom"; // Hook to access route parameters
import { useTranslation } from "react-i18next"; // Hook for translations
import EWSearchApplication from "../../components/SearchApplication"; // Component for rendering the search application UI

// Main component for searching E-Waste applications
const SearchApp = ({ path }) => {
    const { t } = useTranslation(); // Translation hook
    const tenantId = Digit.ULBService.getCurrentTenantId(); // Fetching the current tenant ID
    const [payload, setPayload] = useState({}); // State to store the search payload
    const [showToast, setShowToast] = useState(null); // State to manage toast notifications

    // Function to handle form submission
    function onSubmit(_data) {
        // Adjusting the fromDate and toDate to include time offsets
        var fromDate = new Date(_data?.fromDate);
        fromDate?.setSeconds(fromDate?.getSeconds() - 19800); // Subtracting 5 hours and 30 minutes (IST offset)
        var toDate = new Date(_data?.toDate);
        toDate?.setSeconds(toDate?.getSeconds() + 86399 - 19800); // Adding 23 hours, 59 minutes, and 59 seconds

        // Constructing the search payload
        const data = {
            ..._data,
            ...(_data.toDate ? { toDate: toDate?.getTime() } : {}),
            ...(_data.fromDate ? { fromDate: fromDate?.getTime() } : {}),
        };

        // Filtering and formatting the payload
        let payload = Object.keys(data)
            .filter((k) => data[k])
            .reduce((acc, key) => ({ ...acc, [key]: typeof data[key] === "object" ? data[key].code : data[key] }), {});

        // Validating the payload and showing appropriate toast messages
        if (
            Object.entries(payload).length > 0 &&
            !payload.requestId &&
            !payload.creationReason &&
            !payload.fromDate &&
            !payload.mobileNumber &&
            !payload.status &&
            !payload.toDate
        )
            setShowToast({ warning: true, label: "ERR_EW_FILL_VALID_FIELDS" });
        else if (
            Object.entries(payload).length > 0 &&
            (payload.creationReason || payload.status) &&
            (!payload.requestId && !payload.fromDate && !payload.mobileNumber && !payload.toDate)
        )
            setShowToast({ warning: true, label: "ERR_PROVIDE_MORE_PARAM_WITH_TYPE_STATUS" });
        else if (
            Object.entries(payload).length > 0 &&
            (payload.fromDate && !payload.toDate) ||
            (!payload.fromDate && payload.toDate)
        )
            setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        else setPayload(payload); // Setting the payload if validation passes
    }

    // Configuration for enabling the search API call
    const config = {
        enabled: !!(payload && Object.keys(payload).length > 0), // Enable only if the payload is not empty
    };

    // Fetching search results using a custom hook
    const {
        isLoading, // Loading state
        isSuccess, // Success state
        isError, // Error state
        error, // Error details
        data: { EwasteApplication: searchResult, Count: count } = {}, // Extracting search results and count
    } = Digit.Hooks.ew.useEWSearch(
        {
            tenantId,
            filters: payload, // Passing the search payload as filters
        },
        config
    );

    return (
        <React.Fragment>
            {/* Rendering the search application UI */}
            <EWSearchApplication
                t={t}
                isLoading={isLoading}
                tenantId={tenantId}
                setShowToast={setShowToast}
                onSubmit={onSubmit}
                data={
                    isSuccess && !isLoading
                        ? searchResult.length > 0
                            ? searchResult
                            : { display: "ES_COMMON_NO_DATA" } // Display message if no data is found
                        : ""
                }
                count={count} // Total count of search results
            />

            {/* Rendering the toast notification */}
            {showToast && (
                <Toast
                    error={showToast.error}
                    warning={showToast.warning}
                    label={t(showToast.label)} // Displaying the toast message
                    isDleteBtn={true}
                    onClose={() => {
                        setShowToast(null); // Closing the toast
                    }}
                />
            )}
        </React.Fragment>
    );
};

export default SearchApp; // Exporting the component