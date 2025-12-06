import React, { useState, useEffect,useMemo } from "react";
import { CardLabel, LabelFieldPair, Toast, TextInput, LinkButton, CardLabelError, MobileNumber, DatePicker, Loader, Header, ImageUploadHandler, UploadFile, MultiUploadWrapper } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useForm, Controller } from "react-hook-form";
import _ from "lodash";

function allTruthy(obj) {
    for (let key in obj) {
      if (!obj[key]) {
        return false;
      }
    }
    return true;
  }

const QualityParameter = ({onSelect,formData,setValue,unregister,config,...props}) => {
    
    const { t } = useTranslation();
    const { control} = useForm();
    const [showComponent, setShowComponent] = useState(false);
    const {plantCode,processCode,stageCode,materialCode} = formData?.TestStandard || {}
    
    const [showToast, setShowToast] = useState(false);
    const tenant = Digit.ULBService.getStateId();
    const [allFieldsDefined, setallFieldsDefined] = useState(false);
    useEffect(() => {
        if(formData?.TestStandard && allTruthy(formData?.TestStandard)){
            setallFieldsDefined(true)
        }
    }, [formData])
    

    
    

    const { isLoading, data } = Digit.Hooks.tqm.useCustomMDMSV2({
        tenantId: tenant,
        "filters": {
            "plant": plantCode?.plantCode,
            "process": processCode?.process,
            "stage": stageCode?.stage,
            "material": materialCode?.material
        },
        schemaCode: "PQM.TestStandard",
        changeQueryName: `${plantCode?.code}${processCode?.code}${stageCode?.code}${materialCode?.code}`,
        config: {
            enabled: !!allFieldsDefined,
            staleTime: 0,
            cacheTime:0,
        }
    })
    const closeToast = () => {
        setTimeout(() => {
            setShowToast(false);
        }, 5000)
    };
    useEffect(() => {
        if (!data) {
            setShowToast(false);
        }
        else if (!data || Object.keys(data).length === 0) {
            // setShowToast({
            //     label: t('TQM_QUALITY_CRITERIA_NOT_PRESENT'),
            //     isWarning: true
            // });
            closeToast();
            // setShowComponent(false);
        }
        // else setShowComponent(true);

        if(plantCode && processCode && stageCode  && materialCode){
            setShowComponent(true)
        }else{
            setShowComponent(false)
        }
    }, [data,formData]);

    //here make sure this is single array of unique items
    // const qualityCriteria = data?.map(item => item.qualityCriteria);
    const qualityCriteria = useMemo(() => data?.length > 0 ? [...new Set(data?.map(item => item.qualityCriteria)?.flatMap(array => array))]: [], [data])
    sessionStorage.setItem('Digit.qualityCriteria',qualityCriteria?qualityCriteria:[])
    // const qualityCriteria = data?.length > 0 ? [...new Set(data?.map(item => item.qualityCriteria)?.flatMap(array => array))]: []
    const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };
    const CardLabelStyle = { marginTop: "-5px" }
    const [quality, setQuality] = useState(formData?.QualityParameter ? formData?.QualityParameter : {});

    // useEffect(() => {
    //     if(showComponent){
    //       setQuality({})
    //     }
    //   }, [showComponent])

    function displayValue(newValue, criteria, index) {
        setQuality((prevState)=> {
            return {
                ...prevState,
                [criteria]:newValue
            }
        })
    }

    useEffect(() => {
        onSelect("QualityParameter", quality);
    }, [quality])

    if(isLoading) {
        return <Loader />
    }
    
    return (
        <div>
            {!showComponent && (
                <React.Fragment>
                    <div className="suitableOption">{t("ES_TQM_CHOOSE_OPTION")}</div>
                </React.Fragment>
            )
            }
            {showComponent && (

                <React.Fragment>
                    <Header> {t("ES_TQM_QUALITY_PARAMETERS")}</Header>
                    {qualityCriteria?.map((criteria, index) => (
                        <div key={index}>
                                <LabelFieldPair key={index}>
                                    <CardLabel style={CardLabelStyle}>{t(Digit.Utils.locale.getTransformedLocale(`${"PQM.TestStandard"}_${criteria}`))} {qualityCriteria?.length === 1 ? "*" : ""}</CardLabel>
                                    
                                    <div className="field">
                                        <Controller
                                            control={control}
                                            name={`QualityParameter.${criteria}`}
                                            rules={{
                                                pattern: /^-?([0-9]+(\.[0-9]{1,2})?|\.[0-9]{1,2})$/,
                                            }}
                                            render={(props) => (
                                                <TextInput
                                                    // value={props.value}
                                                    // defaultValue={formData?.QualityParameter?.[criteria]}
                                                    value={quality?.[criteria]}
                                                    defaultValue={quality?.[criteria]}
                                                    pattern="^-?([0-9]+(\.[0-9]{1,2})?|\.[0-9]{1,2})$"
                                                    title={t("ES_TQM_TEST_FORMAT_TIP")}
                                                    type={"text"}
                                                    onChange={(e) => {
                                                        const newValue = e.target.value;
                                                        displayValue(newValue, criteria, index);
                                                        setValue(`QualityParameter.${criteria}`,newValue)
                                                    }}
                                                />
                                            )}
                                        />
                                    </div>
                                </LabelFieldPair>
                                </div>
                            ))}

                    <LabelFieldPair>
                        <CardLabel style={CardLabelStyle}>{`${t("ES_TQM_TEST_PARAM_ATTACH_DOCUMENTS")}`}</CardLabel>
                        <div className="field">
                            <Controller
                                defaultValue={quality?.document || []}
                                name={`QualityParameter.document`}
                                control={control}
                                rules={{}}
                                render={({ onChange, ref, value = [] }) => {
                                    function getFileStoreData(filesData) {
                                        const numberOfFiles = filesData.length;
                                        let finalDocumentData = [];
                                        if (numberOfFiles > 0) {
                                          filesData.forEach((value) => {
                                            finalDocumentData.push({
                                              fileName: value?.[0],
                                              fileStoreId: value?.[1]?.fileStoreId?.fileStoreId,
                                              documentType: value?.[1]?.file?.type,
                                            });
                                          });
                                        }
                                        //here we need to update the form the same way as the state of the reducer in multiupload, since Upload component within the multiupload wrapper uses that same format of state so we need to set the form data as well in the same way. Previously we were altering it and updating the formData

                                        setQuality((prevState) => {
                                            return {
                                                ...prevState,
                                                document:filesData
                                            }
                                        })
                                        onChange(numberOfFiles > 0 ? filesData : []);
                                      }
                                    return (
                                        <MultiUploadWrapper
                                            t={t}
                                            module="works"
                                            tenantId={Digit.ULBService.getCurrentTenantId()}
                                            getFormState={getFileStoreData}
                                            showHintBelow={false}
                                            setuploadedstate={value || quality?.document || []}
                                            allowedFileTypesRegex={/(jpg|jpeg|png|pdf)$/i}
                                            allowedMaxSizeInMB={2}
                                            maxFilesAllowed={1}    
                                        />
                                    );
                                }}
                            />
                        </div>
                    </LabelFieldPair>

                </React.Fragment>

            )}
            {showToast && !showComponent && <Toast warning={showToast.isWarning} label={showToast.label} isDleteBtn={"true"} onClose={() => setShowToast(false)} />}


        </div>
    )
}

export default QualityParameter;


