import React, { useState, useEffect } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useParams } from "react-router-dom"
import { useTranslation } from "react-i18next";
import ASSETSearchApplication from "../../components/SearchApplication"

const SearchApp = ({path, parentRoute}) => {
    const { variant } = useParams();
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [payload, setPayload] = useState({})
    const [showToast, setShowToast] = useState(null);

    function onSubmit (_data) {
        var fromDate = new Date(_data?.fromDate)
        fromDate?.setSeconds(fromDate?.getSeconds() - 19800)
        var toDate = new Date(_data?.toDate)
        toDate?.setSeconds(toDate?.getSeconds() + 86399 - 19800)
        const data = {
            ..._data,
            ...(_data.toDate ? {toDate: toDate?.getTime()} : {}),
            ...(_data.fromDate ? {fromDate: fromDate?.getTime()} : {})
        }

        let payload = Object.keys(data).reduce((acc, key) => {
            const value = data[key];
            if (!value || (typeof value === 'string' && !value.trim())) return acc;
            return {...acc, [key]: typeof value === "object" ? value.code : value};
        }, {});

        const hasSearchFields = payload.applicationNo || payload.creationReason || payload.fromDate || payload.mobileNumber || payload.status || payload.toDate;

        if (!hasSearchFields)
            setShowToast({ warning: true, label: "ERR_VALID_FIELDS" });
        else if ((payload.creationReason || payload.status) && (!payload.applicationNo && !payload.fromDate && !payload.mobileNumber && !payload.toDate))
            setShowToast({ warning: true, label: "ERR_PROVIDE_MORE_PARAM_WITH_TYPE_STATUS" });
        else if ((payload.fromDate && !payload.toDate) || (!payload.fromDate && payload.toDate))
            setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        else
            setPayload(payload)
    }

    const onClear = () => {
        setPayload({});
    };

    const config = {
        enabled: !!(payload && Object.keys(payload).length > 0)
    }

    const { isLoading, isSuccess, isError, error, data: {Assets: searchReult, Count: count} = {} } = Digit.Hooks.asset.useASSETSearch(
        { tenantId, filters: payload },
        config,
    );

    useEffect(() => {
        if (showToast) {
            const timer = setTimeout(() => {
                setShowToast(null);
            }, 1500);
            return () => clearTimeout(timer);
        }
    }, [showToast]);

    return <React.Fragment>
        <ASSETSearchApplication 
            t={t} 
            isLoading={isLoading} 
            parentRoute={parentRoute} 
            tenantId={tenantId} 
            setShowToast={setShowToast} 
            onSubmit={onSubmit}
            onClear={onClear}
            data={isSuccess && !isLoading ? (searchReult.length > 0 ? searchReult : { display: "ES_COMMON_NO_DATA" }) : ""} 
            count={count} 
        />
        {showToast && (
            <Toast
                error={showToast.error}
                warning={showToast.warning}
                label={t(showToast.label)}
                isDleteBtn={true}
                onClose={() => setShowToast(null)}
            />
        )}
    </React.Fragment>
}

export default SearchApp;