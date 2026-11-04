import React from "react";
import { useTranslation } from "react-i18next";
import { SearchField, RadioButtons } from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useFormContext } from "react-hook-form";
import { format } from "date-fns";

const useInboxMobileCardsData = ({ parentRoute, table }) => {
  const { t } = useTranslation();

  const dataForMobileInboxCards = table?.map((value) => {
    return {
      [t("NOC_HOME_SEARCH_RESULTS_APP_NO_LABEL")]: value?.applicationId,
      [t("TL_COMMON_TABLE_COL_APP_DATE")]: format(new Date(value?.date), "dd/MM/yyyy"),
      [t("PT_COMMON_TABLE_COL_STATUS_LABEL")]: value?.status,
    };
  });

  const MobileSortFormValues = () => {
    const sortOrderOptions = [
      {
        code: "DESC",
        i18nKey: "ES_COMMON_SORT_BY_DESC",
      },
      {
        code: "ASC",
        i18nKey: "ES_COMMON_SORT_BY_ASC",
      },
    ];
    const { control: controlSortForm } = useFormContext();
    return (
      <SearchField>
        <Controller
          name="sortOrder"
          control={controlSortForm}
          render={({ onChange, value }) => (
            <RadioButtons
              onSelect={(e) => {
                onChange(e.code);
              }}
              selectedOption={sortOrderOptions.filter((option) => option.code === value)[0]}
              optionsKey="i18nKey"
              name="sortOrder"
              options={sortOrderOptions}
            />
          )}
        />
      </SearchField>
    );
  };

  return {
    data: dataForMobileInboxCards,
    linkPrefix: `${parentRoute}/inbox/application-overview/`,
    serviceRequestIdKey: t("NOC_HOME_SEARCH_RESULTS_APP_NO_LABEL"),
    MobileSortFormValues,
  };
};

export default useInboxMobileCardsData;
