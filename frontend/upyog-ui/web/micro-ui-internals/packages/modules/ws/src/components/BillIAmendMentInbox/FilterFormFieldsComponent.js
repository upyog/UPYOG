import React, { Fragment, useMemo } from "react";
import { FilterFormField, RadioButtons, MultiSelectDropdown, Loader, CheckBox, RemoveableTag } from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useWatch } from "react-hook-form";
import { useTranslation } from "react-i18next";

const FilterFormFieldsComponent = ({
  controlFilterForm,
  localitiesForEmployeesCurrentTenant,
  loadingLocalitiesForEmployeesCurrentTenant,
  isInboxLoading,
  filterFormState,
  statuses,
}) => {
  const { t } = useTranslation();
  const availableOptions = [
    { code: "ASSIGNED_TO_ME", name: `${t("ES_INBOX_ASSIGNED_TO_ME")}` },
    { code: "ASSIGNED_TO_ALL", name: `${t("ES_INBOX_ASSIGNED_TO_ALL")}` },
  ];
  const selectedApplicationStatus = useWatch({
    control: controlFilterForm,
    name: "status",
    defaultValue: filterFormState?.applicationStatus || null,
  });

  const totalnewWS = statuses?.filter((e) => e.businessservice === "BS.AMENDMENT").length;

  const applicationTypeStatuses = [
    {
      code: "NewWS1",
      name: `${t("INWORKFLOW")} `,
    },
    {
      code: "ModifyWSConnection",
      name: `${t("CONSUMED")} `,
    },
  ];

  const selectrole = (listOfSelections, props) => {
    const res = listOfSelections.map((propsData) => {
      const data = propsData[1];
      return data;
    });
    return props.onChange(res);
  };

  return (
    <>
      <FilterFormField>
        <Controller
          name="assignee"
          control={controlFilterForm}
          render={({ field }) => (
            <RadioButtons
              onSelect={(e) => {
                field.onChange(e.code);
              }}
              selectedOption={availableOptions.filter((option) => option.code === field.value)[0]}
              optionsKey="name"
              name="assignee"
              options={availableOptions}
            />
          )}
        />
      </FilterFormField>
      <FilterFormField>
        <Controller
          name="locality"
          control={controlFilterForm}
          render={({ field }) => {
            const renderRemovableTokens = useMemo(
              () =>
                field.value?.map((locality, index) => {
                  return (
                    <RemoveableTag
                      key={index}
                      text={locality.i18nkey}
                      onClick={() => {
                        field.onChange(field.value?.filter((loc) => loc.code !== locality.code));
                      }}
                    />
                  );
                }),
              [field.value]
            );
            return loadingLocalitiesForEmployeesCurrentTenant ? (
              <Loader />
            ) : (
              <>
                <div className="filter-label sub-filter-label">{t("ES_INBOX_LOCALITY")}</div>
                <MultiSelectDropdown
                  options={localitiesForEmployeesCurrentTenant ? localitiesForEmployeesCurrentTenant : []}
                  optionsKey="i18nkey"
                  props={{ field }}
                  isPropsNeeded={true}
                  onSelect={(listOfSelections) => {
                    const res = listOfSelections.map((propsData) => {
                      const data = propsData[1];
                      return data;
                    });
                    field.onChange(res);
                  }}
                  selected={field.value}
                  defaultLabel={t("ES_BPA_ALL_SELECTED")}
                  defaultUnit={t("BPA_SELECTED_TEXT")}
                  ServerStyle={{overflowX:"hidden", width:"100%"}}
                />
                <div className="tag-container">{renderRemovableTokens}</div>
              </>
            );
          }}
        />
      </FilterFormField>

      <FilterFormField>
        <Controller
          name="applicationStatus"
          control={controlFilterForm}
          render={({ field }) => {
            function changeItemCheckStatus(value) {
              field.onChange(value);
            }
            const renderStatusCheckBoxess = useMemo(
              () =>
                statuses?.map((status, index) => {
                  return (
                    <CheckBox
                      onChange={(e) =>
                        e.target.checked
                          ? changeItemCheckStatus([...field.value, status?.statusid])
                          : changeItemCheckStatus(field.value?.filter((ele) => ele !== status?.statusid))
                      }
                      checked={field.value?.includes(status?.statusid)}
                      label={`${t(`${status.applicationstatus} (${status.count})`)}`}
                      value={status.applicationStatus}
                      key={index + 1}
                    />
                  );
                }),
              [field.value, statuses]
            );
            return (
              <>
                <div className="filter-label sub-filter-label">{t("CS_INBOX_STATUS_FILTER")}</div>
                {isInboxLoading ? <Loader /> : <> {renderStatusCheckBoxess}</>}
              </>
            );
          }}
        />
      </FilterFormField>
    </>
  );
};

export default FilterFormFieldsComponent;
