import React, { useEffect, useState } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast } from "@upyog/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useParams } from "react-router-dom"
import { useTranslation } from "react-i18next";
import PTSearchAppeal from "../../components/PTSearchAppeal";

const getAppealData = async (tenantId, appealId, setAppDetailsToShow, updateCanFetchBillData) => {
    const appealData = await Digit.PTService.appealSearch({ tenantId, filters: { appealid:appealId } });
    let billData = {};
    if (appealData?.Appeals?.length > 0) {
        setAppDetailsToShow(appealData?.Appeals[0])
     
    }
    updateCanFetchBillData({
        loading: false,
        loaded: false,
        canLoad: false,
      });
  };

const SearchAppeal = ({path}) => {
    const { variant } = useParams();
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [payload, setPayload] = useState({})
    const [showToast, setShowToast] = useState(null);
    const [appealData, setAppealData] = useState(null);

    function onSubmit (_data) {
        
        const data = {..._data}

        let payload = Object.keys(data).filter( k => data[k] ).reduce( (acc, key) => ({...acc,  [key]: typeof data[key] === "object" ? data[key].code : data[key] }), {} );
        if(Object.entries(payload).length>0 && !payload.acknowldgementNumber && !payload.appealid && !payload.propertyIds)
        setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS" });
        
        else
        setPayload(payload)
    }

    const config = {
        enabled: !!( payload && Object.keys(payload).length > 0 )
    }
    useEffect(async()=>{
        const res = await Digit.PTService.appealSearch({ tenantId, filters: payload });
        setAppealData(res?.Appeals)
    },[payload])
    // const { isLoading, isSuccess, isError, error, data: {Properties: searchReult, Count: count} = {} } = Digit.Hooks.pt.usePropertySearch(
    //     { tenantId,
    //       filters: payload
    //     },
    //    config,
    //   );
    // const { isLoading, isSuccess, isError, error, data: {Properties: searchReult, Count: count} = {} }
    
    return <React.Fragment>
        <PTSearchAppeal t={t} isLoading={appealData?.length>0?false:true} tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} data={  (appealData?.length>0? appealData : { display: "ES_COMMON_NO_DATA" } )} count={appealData?.length} /> 
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

export default SearchAppeal