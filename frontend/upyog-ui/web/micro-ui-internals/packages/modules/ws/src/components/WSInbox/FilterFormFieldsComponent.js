import React, { Fragment, useMemo } from "react";
import { FilterFormField, Loader, RadioButtons, RemoveableTag, CheckBox, MultiSelectDropdown } from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useWatch } from "react-hook-form";
import { useTranslation } from "react-i18next";

const FilterFormFieldsComponent = ({
  statuses,
  isInboxLoading,
  controlFilterForm,
  filterFormState,
  localitiesForEmployeesCurrentTenant,
  loadingLocalitiesForEmployeesCurrentTenant,
  checkPathName,
}) => {
  const { t } = useTranslation();
  const availableOptions = [
    { code: "ASSIGNED_TO_ME", name: `${t("ES_INBOX_ASSIGNED_TO_ME")}` },
    { code: "ASSIGNED_TO_ALL", name: `${t("ES_INBOX_ASSIGNED_TO_ALL")}` },
  ];

  const selectedApplicationType = useWatch({
    control: controlFilterForm,
    name: "applicationType",
    defaultValue: filterFormState?.applicationType || [],
  });

  const totalnewWSCount = statuses?.filter((e) => e.businessservice === "NewWS1")?.reduce((sum, data) => sum + (data?.count || 0), 0) || 0;
  const totalModifyWSCount = statuses?.filter((e) => e.businessservice === "ModifyWSConnection")?.reduce((sum, data) => sum + (data?.count || 0), 0) || 0;
  const totalDisconnectionWSCount = statuses?.filter((e) => e.businessservice === "DisconnectWSConnection")?.reduce((sum, data) => sum + (data?.count || 0), 0) || 0;
  const totalnewSWCount = statuses?.filter((e) => e.businessservice === "NewSW1")?.reduce((sum, data) => sum + (data?.count || 0), 0) || 0;
  const totalModifySWCount = statuses?.filter((e) => e.businessservice === "ModifySWConnection")?.reduce((sum, data) => sum + (data?.count || 0), 0) || 0;
  const totalDisconnectionSWCount = statuses?.filter((e) => e.businessservice === "DisconnectSWConnection")?.reduce((sum, data) => sum + (data?.count || 0), 0) || 0;

  const applicationTypeStatuses = checkPathName
    ? [
        {
          code: "NewWS1",
          name: `${t("CS_COMMON_INBOX_NEWWS1")} (${totalnewWSCount})`,
        },
        {
          code: "ModifyWSConnection",
          name: `${t("CS_COMMON_INBOX_MODIFYWSCONNECTION")} (${totalModifyWSCount})`,
        },
        {
          code: "DisconnectWSConnection",
          name: `${t("CS_COMMON_INBOX_DISCONNECTIONWS")} (${totalDisconnectionWSCount})`,
        },
      ]
    : [
        {
          code: "NewSW1",
          name: `${t("CS_COMMON_INBOX_NEWSW1")} (${totalnewSWCount})`,
        },
        {
          code: "ModifySWConnection",
          name: `${t("CS_COMMON_INBOX_MODIFYSWCONNECTION")} (${totalModifySWCount})`,
        },
        {
          code: "DisconnectSWConnection",
          name: `${t("CS_COMMON_INBOX_DISCONNECTIONSW")} (${totalDisconnectionSWCount})`,
        },
      ];

  return (
    <>
      <FilterFormField>
        <Controller
          name="assignee"
          control={controlFilterForm}
          render={({ field }) => {
            return (
              <RadioButtons
                onSelect={(e) => {
                  field.onChange(e.code);
                }}
                selectedOption={availableOptions.filter((option) => option.code === field.value)[0]}
                optionsKey="name"
                name="assignee"
                options={availableOptions}
              />
            );
          }}
        />
      </FilterFormField>

      <FilterFormField>
        <Controller
          name="locality"
          control={controlFilterForm}
          render={({ field }) => {
            const renderRemovableTokens = field.value?.map((locality, index) => {
              return (
                <RemoveableTag
                  key={index}
                  text={locality.i18nkey}
                  onClick={() => {
                    field.onChange(field.value?.filter((loc) => loc.code !== locality.code));
                  }}
                />
              );
            });
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
                  defaultLabel={t("ES_WS_ALL_SELECTED")}
                  defaultUnit={t("WS_SELECTED_TEXT")}
                />
                <div className="tag-container">{renderRemovableTokens}</div>
              </>
            );
          }}
        />
      </FilterFormField>

      <FilterFormField>
        <Controller
          name="applicationType"
          control={controlFilterForm}
          render={({ field }) => {
            function changeItemCheckStatus(value) {
              field.onChange(value);
            }
            const renderStatusCheckBoxess = applicationTypeStatuses?.map((status, index) => {
              return (
                <CheckBox
                  onChange={(e) =>
                    e.target.checked
                      ? changeItemCheckStatus([...field.value, status?.code])
                      : changeItemCheckStatus(field.value?.filter((ele) => ele !== status?.code))
                  }
                  checked={field.value?.includes(status?.code)}
                  label={status?.name}
                  value={status.name}
                  key={index + 1}
                />
              );
            });
            return (
              <>
                <div className="filter-label sub-filter-label">{t("WS_COMMON_TABLE_COL_APP_TYPE_LABEL")}</div>
                {isInboxLoading ? <Loader /> : <>{renderStatusCheckBoxess}</>}
              </>
            );
          }}
        />
      </FilterFormField>

      {selectedApplicationType?.length > 0 ? (
        <FilterFormField>
          <Controller
            name="applicationStatus"
            control={controlFilterForm}
            render={({ field }) => {
              function changeItemCheckStatus(value) {
                field.onChange(value);
              }
              const renderStatusCheckBoxes = statuses
                ?.filter((e) => {
                  const value = selectedApplicationType;
                  return value.includes(e.businessservice);
                })
                ?.map((status, index) => {
                  return (
                    <CheckBox
                      key={index + 1}
                      onChange={(e) =>
                        e.target.checked
                          ? changeItemCheckStatus([...field.value, status?.statusid])
                          : changeItemCheckStatus(field.value?.filter((id) => id !== status?.statusid))
                      }
                      checked={field.value?.includes(status?.statusid)}
                      label={`${t(`WF_${status.businessservice.toUpperCase()}_${status.applicationstatus.split("_").pop()}`)} (${status.count})`}
                    />
                  );
                });
              return (
                <>
                  <div className="filter-label sub-filter-label">{t("WS_MYCONNECTIONS_STATUS")}</div>
                  {isInboxLoading ? <Loader /> : <>{renderStatusCheckBoxes}</>}
                </>
              );
            }}
          />
        </FilterFormField>
      ) : null}
    </>
  );
};

export default FilterFormFieldsComponent;