import React, { useState } from "react";
import { RadioButtons } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { CloseSvg } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * This SortBy component creates a filtering interface that allows users to sort by date (ascending or descending). 
 * It uses radio buttons from what appears to be a custom UI library and supports both mobile and desktop views.
 * Key observations:
 * 1. The component takes props for handling sorting and closing the modal/popup.
 * 2. It translates UI text using i18next.
 * 3. It maintains a selected sort option state with useState.
 * 4. It renders differently based on the "type" prop (mobile vs desktop).
 */
const SortBy = (props) => {
  const { t } = useTranslation();
  /* Initialize the selected sort option based on existing sort parameters
   If the first sort parameter has desc=true, select "Latest First" option
   Otherwise, select "Latest Last" option
   */
  const [selectedOption, setSelectedOption] = useState(() => {
    return props.sortParams?.[0]?.desc
      ? { code: "DESC", name: t("ES_INBOX_DATE_LATEST_FIRST") }
      : { code: "ASC", name: t("ES_INBOX_DATE_LATEST_LAST") };
  });

  function clearAll() {}

  function onSort(option) {
    props.onSort([{ id: "createdTime", desc: option.code === "DESC" ? true : false }]);
    props.onClose();
  }

  return (
    <React.Fragment>
      <div className="filter">
        <div className="filter-card">
          <div className="heading">
            <div className="filter-label">{t("SORT_BY")}:</div>
            <div className="clearAll" onClick={clearAll}>
              {t("ES_COMMON_CLEAR_ALL")}
            </div>
            
            {props.type === "desktop" && (
              <span className="clear-search" onClick={clearAll}>
                {t("ES_COMMON_CLEAR_ALL")}
              </span>
            )}
            {props.type === "mobile" && (
              <span onClick={props.onClose}>
                <CloseSvg />
              </span>
            )}
          </div>
          <div>
            <RadioButtons
              onSelect={onSort}
              selectedOption={selectedOption}
              optionsKey="name"
              options={[
                { code: "DESC", name: t("ES_INBOX_DATE_LATEST_FIRST") },
                { code: "ASC", name: t("ES_INBOX_DATE_LATEST_LAST") },
              ]}
            />
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default SortBy;
