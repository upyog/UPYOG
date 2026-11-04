import React, { useState } from "react";
import { Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import EWSearchApplication from "../../components/SearchApplication";

/**
 * Employee interface for searching E-Waste applications.
 * Provides advanced search capabilities with date ranges, status filters,
 * and mobile number search. Includes validation and error handling.
 *
 * @param {Object} props Component properties
 * @param {string} props.path Base route path
 * @returns {JSX.Element} Search interface with results display
 */
const SearchApp = ({ path }) => {
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [payload, setPayload] = useState({});
    const [showToast, setShowToast] = useState(null);

    /**
     * Processes form submission and validates search criteria
     * Handles date adjustments for timezone and formats payload
     * 
     * @param {Object} _data Raw form data
     */
    function onSubmit(_data) {
        var fromDate = new Date(_data?.fromDate);
        fromDate?.setSeconds(fromDate?.getSeconds() - 19800);
        var toDate = new Date(_data?.toDate);
        toDate?.setSeconds(toDate?.getSeconds() + 86399 - 19800);

        const data = {
            ..._data,
            ...(_data.toDate ? { toDate: toDate?.getTime() } : {}),
            ...(_data.fromDate ? { fromDate: fromDate?.getTime() } : {}),
        };

        // Filter out empty values and convert objects to codes
        let payload = Object.keys(data).reduce((acc, key) => {
            const value = data[key];
            // Skip if value is empty, null, undefined, or just whitespace
            if (!value || (typeof value === 'string' && !value.trim())) return acc;
            // Convert object to code, otherwise use value as-is
            return {...acc, [key]: typeof value === "object" ? value.code : value};
        }, {});
        
        // Check if any actual search field is provided (excluding pagination fields)
        const hasSearchFields = payload.requestId || payload.creationReason || payload.fromDate || payload.mobileNumber || payload.status || payload.toDate;
        
        if(!hasSearchFields)
        {
            setShowToast({ warning: true, label: "ERR_EW_FILL_VALID_FIELDS" });
        } else if (
            Object.entries(payload).length > 0 &&
            (payload.creationReason || payload.status) &&
            (!payload.requestId && !payload.fromDate && !payload.mobileNumber && !payload.toDate)
        ) {
            setShowToast({ warning: true, label: "ERR_PROVIDE_MORE_PARAM_WITH_TYPE_STATUS" });
        } else if((payload.fromDate && !payload.toDate) || (!payload.fromDate && payload.toDate)) {
            setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        } else {
            setPayload(payload);
        }
    }

    const onClear = () => {
        setPayload({});
    }

    const config = {
        enabled: !!(payload && Object.keys(payload).length > 0),
    };

    const {
        isLoading,
        isSuccess,
        isError,
        error,
        data: { EwasteApplication: searchResult, Count: count } = {},
    } = Digit.Hooks.ew.useEWSearch(
        {
            tenantId,
            filters: payload,
        },
        config
    );

    return (
        <React.Fragment>
            <EWSearchApplication
                t={t}
                isLoading={isLoading}
                tenantId={tenantId}
                setShowToast={setShowToast}
                onSubmit={onSubmit}
                onClear={onClear}
                data={
                    isSuccess && !isLoading
                        ? searchResult.length > 0
                            ? searchResult
                            : { display: "ES_COMMON_NO_DATA" }
                        : ""
                }
                count={count}
            />

            {showToast && (
                <Toast
                    error={showToast.error}
                    warning={showToast.warning}
                    label={t(showToast.label)}
                    isDleteBtn={true}
                    onClose={() => {
                        setShowToast(null);
                    }}
                />
            )}
        </React.Fragment>
    );
};

export default SearchApp;