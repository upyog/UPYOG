import React, { useState } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useParams } from "react-router-dom"
import { useTranslation } from "react-i18next";
import CHBSearchApplication from "../../components/SearchApplication";

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
          isDleteBtn={true}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </React.Fragment>

}

export default SearchApp