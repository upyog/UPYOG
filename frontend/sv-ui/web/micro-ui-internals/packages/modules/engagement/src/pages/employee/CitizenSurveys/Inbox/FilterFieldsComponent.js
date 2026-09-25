import React, { Fragment, useMemo } from "react"
import { FilterFormField, Loader, Dropdown} from "@nudmcdgnpm/upyog-ui-react-components-lts";
import { Controller } from "react-hook-form";
import { useTranslation } from "react-i18next";

const FilterFormFieldsComponent = ({statuses, isInboxLoading, registerRef, controlFilterForm, setFilterFormValue, filterFormState, getFilterFormValue, localitiesForEmployeesCurrentTenant, loadingLocalitiesForEmployeesCurrentTenant}) => {
  const { t } = useTranslation()
   /**
     * ToDo how to display default value correctly ask @egov-saurabh
     */
  
  return <Fragment>
    <FilterFormField>
      <Controller
          name="status"
          control={controlFilterForm}
          render={({ field }) => {
            return <Fragment>
              <div className="filter-label">{t("CS_SURVEY_STATUS")}</div>
              <Dropdown
                inputRef={field.ref}
                option={statuses}
                optionKey="code"
                t={t}
                select={field.onChange}
                selected={field.value}
                />
            </Fragment>
          }
        }
        />
    </FilterFormField>
  </Fragment>
}

export default FilterFormFieldsComponent
