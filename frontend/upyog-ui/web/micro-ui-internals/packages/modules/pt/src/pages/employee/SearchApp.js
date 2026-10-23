import React, { useState } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useParams } from "react-router-dom"
import { useTranslation } from "react-i18next";
import PTSearchApplication from "../../components/SearchApplication";

const SearchApp = ({path}) => {
    const { variant } = useParams();
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [payload, setPayload] = useState({})
    const [showToast, setShowToast] = useState(null);

    function onSubmit (_data) {
        var fromDate = new Date(_data?.fromDate)
        fromDate?.setSeconds(fromDate?.getSeconds() - 19800 )
        var toDate = new Date(_data?.toDate)
        toDate?.setSeconds(toDate?.getSeconds() + 86399 - 19800)
        const data = {
            ..._data,
            ...(_data.toDate ? {toDate: toDate?.getTime()} : {}),
            ...(_data.fromDate ? {fromDate: fromDate?.getTime()} : {})
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
        const hasSearchFields = payload.acknowledgementIds || payload.creationReason || payload.fromDate || payload.mobileNumber || payload.propertyIds || payload.status || payload.toDate;
        
        if(!hasSearchFields)
        setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS" });
        else if((payload.creationReason || payload.status) && !payload.acknowledgementIds && !payload.fromDate && !payload.mobileNumber && !payload.propertyIds && !payload.toDate)
        setShowToast({ warning: true, label: "ERR_PROVIDE_MORE_PARAM_WITH_TYPE_STATUS" });
        else if ((payload.fromDate && !payload.toDate) || (!payload.fromDate && payload.toDate))
        setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        else
        setPayload(payload)
    }

    const config = {
        enabled: !!( payload && Object.keys(payload).length > 0 )
    }

    const onClear = () => {
        setPayload({});
    };

    const { isLoading, isSuccess, isError, error, data: {Properties: searchReult, Count: count} = {} } = Digit.Hooks.pt.usePropertySearch(
        { tenantId,
          filters: payload
        },
       config,
      );
    return <React.Fragment>
        <PTSearchApplication t={t} isLoading={isLoading} tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} onClear={onClear} data={  isSuccess && !isLoading ? (searchReult.length>0? searchReult : { display: "ES_COMMON_NO_DATA" } ):""} count={count} /> 
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

}

export default SearchApp