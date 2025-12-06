import React, {Fragment} from "react"
import { Controller, useWatch } from "react-hook-form";
import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@upyog/digit-ui-react-components";

/**
 * SearchFields Component
 * 
 * This component is responsible for rendering the search fields for the CHB module.
 * It allows users to input search parameters such as application number and mobile number to filter results.
 * 
 * Props:
 * - `register`: Function from React Hook Form to register input fields for form validation and state management.
 * - `control`: Control object from React Hook Form to manage controlled components.
 * - `reset`: Function to reset the form fields.
 * - `tenantId`: The tenant ID for which the search is being performed.
 * - `t`: Translation function for internationalization.
 * - `formState`: Object containing the current state of the form, including errors.
 * - `setShowToast`: Function to manage the visibility and content of toast notifications.
 * - `previousPage`: Callback function to navigate to the previous page.
 * 
 * Logic:
 * - Renders a search field for the application number:
 *    - Uses the `TextInput` component to allow users to input the application number.
 * - Renders a search field for the mobile number:
 *    - Uses the `MobileNumber` component with validation rules:
 *        - Minimum and maximum length of 10 digits.
 *        - Pattern to ensure the mobile number starts with 6, 7, 8, or 9.
 *        - Displays error messages for invalid input using the `t` function for translations.
 * 
 * Returns:
 * - A set of search fields for application number and mobile number, with validation and error handling.
 * - These fields can be used to filter search results in the CHB module.
 */

const SearchFields = ({register, control, reset, tenantId, t, formState, setShowToast, previousPage }) => {

    

    return <>
                <SearchField>
                    <label>{t("ghjg_APPLICATION_NO_LABEL")}</label>
                    <TextInput name="applicationNumber" inputRef={register({})} />
                </SearchField>

                
                <SearchField>
                <label>{t("PTR_OWNER_MOBILE_NO")}</label>
                <MobileNumber
                    name="mobileNumber"
                    inputRef={register({
                    minLength: {
                        value: 10,
                        message: t("CORE_COMMON_MOBILE_ERROR"),
                    },
                    maxLength: {
                        value: 10,
                        message: t("CORE_COMMON_MOBILE_ERROR"),
                    },
                    pattern: {
                    value: /[6789][0-9]{9}/,
                    //type: "tel",
                    message: t("CORE_COMMON_MOBILE_ERROR"),
                    },
                })}
                type="number"
                componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
                //maxlength={10}
                />
                 <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError>
                </SearchField>
                
                <SearchField>
                    <label>{t("PTR_FROM_DATE")}</label>
                    <Controller
                        render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
                        name="fromDate"
                        control={control}
                        />
                </SearchField>
                <SearchField>
                    <label>{t("PTR_TO_DATE")}</label>
                    <Controller
                        render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
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