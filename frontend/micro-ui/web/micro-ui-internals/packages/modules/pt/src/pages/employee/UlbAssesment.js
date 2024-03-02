import React, { useState } from "react"
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Toast } from "@egovernments/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useParams } from "react-router-dom"
import { useTranslation } from "react-i18next";
import UlbAssesmentSearch from "../../components/UlbAssesmentSearch";

const UlbAssesment = ({path}) => {
    const { variant } = useParams();
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const [payload, setPayload] = useState({})
    const [showToast, setShowToast] = useState(null);

    function onSubmit (_data) {
        console.log("data",_data)
        let payload= {
            "tenantId":_data?.creationReason?.code,
            "assessmentYear":_data?.status?.code
        }
        setPayload(payload)
        assessmentMutate(
            { assessmentYear:_data?.status?.code,
                tenantId:_data?.creationReason?.code
            },
            {
              onError: (error, variables) => {
                console.log("error:123 ",error)
                setShowToast({ key: "error", action: error?.response?.data?.Errors[0]?.message || error.message, error : {  message:error?.response?.data?.Errors[0]?.code || error.message } });
                setTimeout(closeToast, 5000);
              },
              onSuccess: (data, variables) => {
                sessionStorage.setItem("IsPTAccessDone", data?.Assessments?.[0]?.auditDetails?.lastModifiedTime);
              console.log("success",data)
                
              },
            }
          );
        // var fromDate = new Date(_data?.fromDate)
        // fromDate?.setSeconds(fromDate?.getSeconds() - 19800 )
        // var toDate = new Date(_data?.toDate)
        // toDate?.setSeconds(toDate?.getSeconds() + 86399 - 19800)
        // const data = {
        //     ..._data,
        //     ...(_data.toDate ? {toDate: toDate?.getTime()} : {}),
        //     ...(_data.fromDate ? {fromDate: fromDate?.getTime()} : {})
        // }

        // let payload = Object.keys(data).filter( k => data[k] ).reduce( (acc, key) => ({...acc,  [key]: typeof data[key] === "object" ? data[key].code : data[key] }), {} );
        // if(Object.entries(payload).length>0 && !payload.acknowledgementIds && !payload.creationReason && !payload.fromDate && !payload.mobileNumber && !payload.propertyIds && !payload.status && !payload.toDate)
        // setShowToast({ warning: true, label: "ERR_PT_FILL_VALID_FIELDS" });
        // else if(Object.entries(payload).length>0 && (payload.creationReason || payload.status ) && (!payload.acknowledgementIds && !payload.fromDate && !payload.mobileNumber && !payload.propertyIds && !payload.toDate))
        // setShowToast({ warning: true, label: "ERR_PROVIDE_MORE_PARAM_WITH_TYPE_STATUS" });
        // else if(Object.entries(payload).length>0 && (payload.fromDate && !payload.toDate) || (!payload.fromDate && payload.toDate))
        // setShowToast({ warning: true, label: "ERR_PROVIDE_BOTH_FORM_TO_DATE" });
        // else
        // setPayload(payload)
    }

    const config = {
        enabled: false
    }

    // const {isLoading, isSuccess,error,count, data: AssesmentData = {} } = Digit.Hooks.pt.UseAssessmentCreateUlb(
    //     { tenantId,
    //       filters: payload
    //     },
    //    config,

    //   );
      const { isLoading, isSuccess,error,count, mutate: assessmentMutate } = Digit.Hooks.pt.UseAssessmentCreateUlb("pg.citya");
      const { isLoading: financialYearsLoading, data: financialYearsData } = Digit.Hooks.pt.useMDMS(
        tenantId,
        "pt",
        "FINANCIAL_YEARLS",
        {},
        {
          details: {
            tenantId: Digit.ULBService.getStateId(),
            moduleDetails: [{ moduleName: "egf-master", masterDetails: [{ name: "FinancialYear", filter: "[?(@.module == 'PT')]" }] }],
          },
        }
      );
      console.log("mutate",assessmentMutate)
    return <React.Fragment>
        <UlbAssesmentSearch t={t} isLoading={isLoading} tenantId={tenantId} setShowToast={setShowToast} onSubmit={onSubmit} data={  isSuccess && !isLoading ? (searchReult.length>0? searchReult : { display: "ES_COMMON_NO_DATA" } ):""} count={count} financialYearsData={financialYearsData} /> 
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

export default UlbAssesment