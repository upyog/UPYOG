import React, { Fragment, useState, useEffect } from "react";
import { Loader } from "../atoms/Loader";
import { useTranslation } from "react-i18next";
import _ from "lodash";
import CheckBox from "../atoms/CheckBox";
import CardLabel from "../atoms/CardLabel";

const ApiCheckboxes = ({ populators, formData, props }) => {
  //based on the reqCriteria and populators we will render options in checkboxes

  const [options, setOptions] = useState([]);
  const { t } = useTranslation();

  const reqCriteria = Digit?.Customizations?.[populators?.masterName]?.[populators?.moduleName]?.[populators?.customfn](populators)
  
  const { isLoading: isApiLoading, data: apiData, revalidate, isFetching: isApiFetching } = Digit.Hooks.useCustomAPIHook(reqCriteria);

  useEffect(() => {
    setOptions(apiData);
  }, [apiData]);

  if (isApiLoading) return <Loader />;

  return (
    <>
      {options?.map((row,idx) => {
        return (
          <CheckBox
            onChange={(e) => {
              const obj = {
                ...props.value,
                [e.target.value]: e.target.checked,
              };
              props.onChange(obj);
            }}
            value={row?.[populators?.optionsKey]}
            checked={formData?.[populators.name]?.[row?.[populators?.optionsKey]]}
            label={t(row?.[populators?.labelKey])}
          />
        );
      })}
    </>
  );
};

export default ApiCheckboxes;
