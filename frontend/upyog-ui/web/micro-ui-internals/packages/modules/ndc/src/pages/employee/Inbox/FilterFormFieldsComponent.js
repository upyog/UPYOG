import React, { useState, Fragment, useEffect } from "react";
import { FilterFormField, Loader, RadioButtons, Localities, RemoveableTag, Dropdown, CheckBox } from "@nudmcdgnpm/digit-ui-react-components";
import { Controller } from "react-hook-form";
import { useTranslation } from "react-i18next";

const FilterFormFieldsComponent = ({ statuses, controlFilterForm, applicationTypesOfBPA, handleFilter }) => {
  const { t } = useTranslation();
  const [tlfilters, setTLFilters] = useState({
    applicationStatus: [],
  });

  useEffect(() => {
    if (tlfilters) {
      handleFilter(tlfilters);
    }
  }, [tlfilters]);

  applicationTypesOfBPA?.forEach((type) => {
    type.name = t(`WF_BPA_${type.code}`);
    type.i18nKey = t(`WF_BPA_${type.code}`);
  });



  return (
    <>

      <FilterFormField>
        <Controller
          name="applicationStatus"
          control={controlFilterForm}
          defaultValue={[]}
          render={({ field }) => {

            const selectedValues = field.value || [];

            const toggleStatus = (statusCode) => {

              if (selectedValues.includes(statusCode)) {

                field.onChange(
                  selectedValues.filter(
                    (code) => code !== statusCode
                  )
                );

              } else {

                field.onChange([
                  ...selectedValues,
                  statusCode,
                ]);

              }
            };

            return (
              <>
                {statuses?.map((status, index) => (
                  <CheckBox
                    key={status.applicationstatus}
                    label={`${t(status.applicationstatus)} - ${status.count}`}
                    value={status.applicationstatus}
                    checked={selectedValues.includes(
                      status.applicationstatus
                    )}
                    onChange={() =>
                      toggleStatus(status.applicationstatus)
                    }
                    index={index}
                  />
                ))}
              </>
            );
          }}
        />
      </FilterFormField>
    </>
  );
};

export default FilterFormFieldsComponent;
