/**
 * Renders the filter form controls for employee NOC Inbox.
 * Includes radio buttons and dropdowns for assignee, business service, locality, and application workflow status.
 */
import React, { Fragment, useMemo } from "react"
import { FilterFormField, Loader, RadioButtons, Localities, RemoveableTag, Dropdown, CheckBox } from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useWatch } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { businessServiceList } from "../../../utils";

const FilterFormFieldsComponent = ({statuses, isInboxLoading, registerRef, controlFilterForm, setFilterFormValue, filterFormState, getFilterFormValue, applicationTypesOfBPA, loadingApplicationTypesOfBPA,  localitiesForEmployeesCurrentTenant, loadingLocalitiesForEmployeesCurrentTenant}) => {
  const { t } = useTranslation()
  const tenantId = Digit.ULBService.getStateId();
  const availableOptions = [
    { code: "ASSIGNED_TO_ME", name: `${t("ES_INBOX_ASSIGNED_TO_ME")}` },
    { code: "ASSIGNED_TO_ALL", name: `${t("ES_INBOX_ASSIGNED_TO_ALL")}` },
  ];

  applicationTypesOfBPA?.forEach(type => {
    type.name = t(`WF_BPA_${type.code}`);
    type.i18nKey = t(`WF_BPA_${type.code}`);
  });


  const selectedBusinessService = useWatch({control: controlFilterForm, name: "businessService", defaultValue: null});

  return <>
    <FilterFormField>
      <Controller
        name="assignee"
        control={controlFilterForm}
        render={({ field }) => <RadioButtons
          onSelect={(e) => {
            field.onChange(e.code)
          }}
          selectedOption={availableOptions.filter((option) => option.code === field.value)[0]}
          optionsKey="name"
          options={availableOptions}
        />}
      />
    </FilterFormField>
    <FilterFormField>
      <Controller
          name="businessService"
          control={controlFilterForm}
          render={({ field }) => {
            return <>
              <div className="filter-label sub-filter-label" style={{fontSize: "18px", fontWeight: "600"}}>{t("BUSINESS_SERVICE")}</div>
                <RadioButtons
                  onSelect={(e) => {
                    setFilterFormValue("applicationStatus",[]);
                    field.onChange(e);
                }}
                  selectedOption={field.value}
                  optionsKey="i18nKey"
                  options={businessServiceList() || []}
                />  
              </>
          }}
      />
    </FilterFormField>
    {selectedBusinessService ? <FilterFormField>
      <Controller
        name="applicationStatus"
        control={controlFilterForm}
        render={({ field }) => {
          function changeItemCheckStatus(value){
            field.onChange(value)
          }
          const renderStatusCheckBoxes = useMemo(()=>statuses?.filter(e => e.businessservice === selectedBusinessService.code)?.map( status => {
            return <CheckBox
              onChange={(e) => e.target.checked ? changeItemCheckStatus([...field.value, status?.statusid]) : changeItemCheckStatus(field.value?.filter( id => id !== status?.statusid)) }
              checked={field.value?.includes(status?.statusid)}
              label={`${t(`WF_${status.businessservice}_${status.applicationstatus.split('_').pop()}`)} (${status.count})`}
            />}),[field.value, statuses, selectedBusinessService])
          return <>
            <div className="filter-label sub-filter-label" style={{fontSize: "18px", fontWeight: "600"}}>{t("ACTION_TEST_APPLICATION_STATUS")}</div>
            {isInboxLoading ? <Loader /> : <>{renderStatusCheckBoxes}</>}
          </>
        }}
      />
    </FilterFormField> : null}
  </>
}

export default FilterFormFieldsComponent