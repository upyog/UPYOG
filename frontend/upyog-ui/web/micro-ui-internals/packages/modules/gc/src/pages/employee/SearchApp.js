import React, { useState, useEffect } from "react";
import { Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import GCSearchApplication from "../../components/SearchApplication";

/**
 * `SearchApp` is a React component that handles the search functionality for Garbage Collection applications.
 * - It allows the user to input search filters such as application number, date range, and status.
 * - Displays a toast notification for validation errors when invalid or incomplete input is provided.
 * - Submits the search query to the `useGCSearch` hook and displays the results using the `GCSearchApplication` component.
 * - Handles loading, success, and error states of the API call.
 * 
 * @returns {JSX.Element} The search interface with the results and error handling.
 */
const SearchApp = () => {
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [payload, setPayload] = useState({});
    const [pageConfig, setPageConfig] = useState({ limit: 10, offset: 0 });
    const [showToast, setShowToast] = useState(null);

    function onSubmit(_data) {
        const { fromDate, toDate, offset, limit } = _data;
        const data = {
            ..._data,
            ...(toDate ? { toDate: new Date(`${toDate}T23:59:59.999Z`).getTime() } : {}),
            ...(fromDate ? { fromDate: new Date(`${fromDate}T00:00:00.000Z`).getTime() } : {})
        };
        delete data.offset;
        delete data.limit;
        delete data.sortBy;
        delete data.sortOrder;

        // Filter out empty values and convert objects to codes
        let newPayload = Object.keys(data).reduce((acc, key) => {
            const value = data[key];
            // Skip if value is empty, null, undefined, or just whitespace
            if (!value || (typeof value === 'string' && !value.trim())) return acc;
            // Convert object to code, otherwise use value as-is
            return {...acc, [key]: typeof value === "object" ? value.code : value};
        }, {});
        
        if (newPayload.status) newPayload.applicationStatus = newPayload.status;
        if (newPayload.applicationNo) newPayload.applicationNumber = newPayload.applicationNo;

        // Check if any actual search field is provided (excluding pagination fields)
        const hasSearchFields = newPayload.applicationNo || newPayload.fromDate || newPayload.status || newPayload.toDate || newPayload.mobileNumber || newPayload.applicationStatus || newPayload.applicationNumber;
        
        if(!hasSearchFields) {
            setShowToast({ warning: true, label: "ERR_PROVIDE_ONE_PARAMETERS" });
        } else if ((newPayload.fromDate && !newPayload.toDate) || (!newPayload.fromDate && newPayload.toDate)) {
            setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        } else {
            // Request large limit to ensure local filtering can search all records if backend ignores parameters
            setPayload({ ...newPayload, limit: 10000, offset: 0 });
            setPageConfig({ limit: limit || 10, offset: offset || 0 });
        }
    }

    const onClear = () => {
        setPayload({});
        setPageConfig({ limit: 10, offset: 0 });
    };

    useEffect(() => {
      if (showToast) {
        const timer = setTimeout(() => setShowToast(null), 2000); 
        return () => clearTimeout(timer); // Clear timer on cleanup
      }
    }, [showToast]);

    const config = {
        enabled: !!( payload && Object.keys(payload).length > 0 )
    };

    // DO NOT pass the payload to the API directly to prevent it from wrongly filtering and returning 0 results
    const apiFilters = payload && Object.keys(payload).length > 0 ? { limit: 10000, offset: 0 } : {};
    const { isLoading, isSuccess, isError, error, data } = Digit.Hooks.gc.useGCSearch({ tenantId, filters: apiFilters }, config);

    let searchResult = data?.garbageAccounts || data?.GarbageApplications || data?.data || [];

    // --- LOCAL FILTERING FALLBACK ---
    if (payload?.applicationNo || payload?.applicationNumber) {
        searchResult = searchResult.filter(app => {
            const appNo = app?.grbgApplication?.applicationNo || app?.applicationNo || app?.grbgApplicationNumber || "";
            return appNo.toLowerCase().includes((payload.applicationNo || payload.applicationNumber).toLowerCase());
        });
    }

    if (payload?.mobileNumber) {
        searchResult = searchResult.filter(app => {
            const owners = app?.applicantDetails || [];
            const mobile = owners?.map(o => o?.mobileNumber)?.join(", ") || app?.mobileNumber || app?.garbageSpecification?.phoneNumber || "";
            return mobile.includes(payload.mobileNumber);
        });
    }

    if (payload?.status || payload?.applicationStatus) {
        searchResult = searchResult.filter(app => {
            const target = payload.status || payload.applicationStatus;
            const s1 = app?.applicationStatus || "";
            const s2 = app?.status || "";
            const s3 = app?.grbgApplication?.status || "";
            
            // Allow matching "APPLICATION_CREATED" with "INITIATED" gracefully
            if (target === "INITIATED" || target === "APPLICATION_CREATED") {
                return s1 === "INITIATED" || s2 === "INITIATED" || s3 === "INITIATED" || s1 === "APPLICATION_CREATED" || s2 === "APPLICATION_CREATED" || s3 === "APPLICATION_CREATED";
            }
            return s1 === target || s2 === target || s3 === target;
        });
    }

    if (payload?.fromDate && payload?.toDate) {
        searchResult = searchResult.filter(app => {
            const appDate = app?.auditDetails?.createdTime || app?.auditDetails?.createdDate;
            if (!appDate) return true;
            return appDate >= payload.fromDate && appDate <= payload.toDate;
        });
    }

    let count = searchResult?.length || 0;
    const paginatedResult = searchResult.slice(pageConfig.offset, pageConfig.offset + pageConfig.limit);

    return (
        <React.Fragment>
            <GCSearchApplication t={t} isLoading={isLoading} tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} onClear={onClear} data={isSuccess && !isLoading ? (paginatedResult.length > 0 ? paginatedResult : { display: "ES_COMMON_NO_DATA" }) : ""} count={count} />
            {showToast && <Toast error={showToast.error} warning={showToast.warning} label={t(showToast.label)} onClose={() => setShowToast(null)} />}
        </React.Fragment>
    );
};

export default SearchApp;