import React, { useState, useEffect } from "react";
import { Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import GCSearchApplication from "../../components/SearchApplication";

/**
 * SearchApp Component
 * 
 * Employee-facing search page for GC applications.
 * Wraps `GCSearchApplication` with search state management, date normalization,
 * and client-side pagination. Validates that at least one search field is provided
 * and that both fromDate and toDate are supplied together.
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
            ...(fromDate ? { fromDate: new Date(`${fromDate}T00:00:00.000Z`).getTime() } : {}),
        };
        delete data.offset;
        delete data.limit;
        delete data.sortBy;
        delete data.sortOrder;

        const newPayload = Object.keys(data).reduce((acc, key) => {
            const value = data[key];
            if (!value || (typeof value === "string" && !value.trim())) return acc;
            return { ...acc, [key]: typeof value === "object" ? value.code : value };
        }, {});

        // Backend uses applicationNumber (List) and status (List)
        if (newPayload.applicationNo) {
            newPayload.applicationNumber = newPayload.applicationNo;
            delete newPayload.applicationNo;
        }
        // status stays as-is — backend field is `status`

        const hasSearchFields = Object.keys(newPayload).length > 0;

        if (!hasSearchFields) {
            setShowToast({ warning: true, label: "ERR_PROVIDE_ONE_PARAMETERS" });
        } else if ((newPayload.fromDate && !newPayload.toDate) || (!newPayload.fromDate && newPayload.toDate)) {
            setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        } else {
            setPayload({ ...newPayload, limit: limit || 10, offset: offset || 0 });
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
            return () => clearTimeout(timer);
        }
    }, [showToast]);

    const config = { enabled: !!(payload && Object.keys(payload).length > 0) };

    const { isLoading, isSuccess, data } = Digit.Hooks.gc.useGCSearch(
        { tenantId, filters: payload },
        config
    );

    const searchResult = data?.garbageAccounts || data?.GarbageApplications || data?.data || [];
    const count = searchResult?.length || 0;
    const paginatedResult = searchResult.slice(pageConfig.offset, pageConfig.offset + pageConfig.limit);

    return (
        <React.Fragment>
            <GCSearchApplication
                t={t}
                isLoading={isLoading}
                tenantId={tenantId}
                setShowToast={setShowToast}
                onSubmit={onSubmit}
                onClear={onClear}
                data={isSuccess && !isLoading ? (paginatedResult.length > 0 ? paginatedResult : { display: "ES_COMMON_NO_DATA" }) : ""}
                count={count}
            />
            {showToast && <Toast error={showToast.error} warning={showToast.warning} label={t(showToast.label)} onClose={() => setShowToast(null)} />}
        </React.Fragment>
    );
};

export default SearchApp;
