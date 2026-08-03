import React, {Fragment} from "react"
import { Controller, useWatch } from "react-hook-form"; // Importing form handling utilities from react-hook-form
 // Importing UI components from digit-ui-react-components library
import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header, Menu } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * Component: SearchFields
 * Description: This component renders a form with fields for filtering/searching based on:
 * - Application Number
 * - Mobile Number
 * - From Date
 * - To Date
 * - Includes a Submit button and a Reset/Clear All option
 * 
 * @param {Object} props - Component props
 * @param {Function} register - Function to register form inputs with react-hook-form
 * @param {Object} control - Control object for managing controlled inputs
 * @param {Function} reset - Function to reset form fields to their initial values
 * @param {String} tenantId - Tenant ID for the current context
 * @param {Function} t - Translation function for localization
 * @param {Object} formState - Object containing form validation states
 * @param {Function} setShowToast - Function to display toast notifications
 * @param {Function} previousPage - Function to navigate to the previous page
 */

const SearchFields = ({register, control, reset, tenantId, t, formState, setShowToast, previousPage }) => {

    

    return <>
                <SearchField>
                    <label>{t("PTR_APPLICATION_NO_LABEL")}</label>
                    <TextInput name="applicationNumber" {...register("applicationNumber")} />
                </SearchField>

                <SearchField>
                <label>{t("PTR_PET_TYPE")}</label>

                <Controller
                    control={control}
                    name="petType"
                    render={({ field }) => (
                    <Dropdown
                        selected={field.value}
                        select={(e) => field.onChange(e?.code)}
                        option={Menu || []}
                        optionKey="i18nKey"
                        t={t}
                        placeholder={t("PTR_PET_TYPE")}
                    />
                    )}
                />
                </SearchField>


                
                <SearchField>
                <label>{t("PTR_OWNER_MOBILE_NO")}</label>
                <Controller
                    control={control}
                    name="mobileNumber"
                    rules={{
                        minLength: { value: 10, message: t("CORE_COMMON_MOBILE_ERROR") },
                        maxLength: { value: 10, message: t("CORE_COMMON_MOBILE_ERROR") },
                        pattern: { value: /[6789][0-9]{9}/, message: t("CORE_COMMON_MOBILE_ERROR") },
                    }}
                    render={({ field }) => (
                        <MobileNumber
                            value={field.value}
                            onChange={field.onChange}
                            type="number"
                            componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
                        />
                    )}
                />
                <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError>
                </SearchField>
                
                <SearchField>
                    <label>{t("PTR_FROM_DATE")}</label>
                    <Controller
                        render={({ field }) => <DatePicker date={field.value} disabled={false} onChange={field.onChange} />}
                        name="fromDate"
                        control={control}
                        />
                </SearchField>
                <SearchField>
                    <label>{t("PTR_TO_DATE")}</label>
                    <Controller
                        render={({ field }) => <DatePicker date={field.value} disabled={false} onChange={field.onChange} />}
                        name="toDate"
                        control={control}
                        />
                </SearchField>
                <SearchField className="submit">
                    <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
                    <p style={{marginTop:"10px"}}
                     onClick={() => {
                        reset({ 
                            applicationNumber: "", 
                            fromDate: "", 
                            toDate: "",
                            mobileNumber:"",
                            petType:"",
                            status: "",
                            creationReason: "",
                            offset: 0,
                            limit: 10,
                            sortBy: "commencementDate",
                            sortOrder: "DESC"
                        });
                        setShowToast(null);
                        previousPage();
                    }}>{t(`ES_COMMON_CLEAR_ALL`)}</p>
                </SearchField>
    </>
}
export default SearchFields