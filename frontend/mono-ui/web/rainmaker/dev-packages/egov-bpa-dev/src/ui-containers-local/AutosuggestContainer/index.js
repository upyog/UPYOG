import React, { Component } from "react";
import { connect } from "react-redux";
import { AutoSuggest } from "../../ui-atoms-local";
import { findItemInArrayOfObject } from "../../ui-utils/commons";
import { prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import {
  transformById,
  getLocaleLabels,
  appendModulePrefix
} from "egov-ui-framework/ui-utils/commons";
import get from "lodash/get";
import isEmpty from "lodash/isEmpty";
import { getLocalization } from "egov-ui-kit/utils/localStorageUtils";

// const localizationLabels = JSON.parse(getLocalization("localization_en_IN"));
// const transfomedKeys = transformById(localizationLabels, "code");
class AutoSuggestor extends Component {
  onSelect = value => {
    const { onChange,jsonPath, approveCheck } = this.props;
    //Storing multiSelect values not handled yet
    onChange({ target: { value: value } });
    // approveCheck(jsonPath, value);
  };

  render() {
    const {
      value,
      preparedFinalObject,
      label,
      placeholder,
      suggestions,
      className,
      localizationLabels,
      jsonPath,
      ...rest
    } = this.props;
    let translatedLabel = getLocaleLabels(
      label.labelName,
      label.labelKey,
      localizationLabels
    );
    let translatedPlaceholder = getLocaleLabels(
      placeholder.labelName,
      placeholder.labelKey,
      localizationLabels
    );
    //For multiSelect to be enabled, pass isMultiSelect=true in props.
    return (
      <div>
        <AutoSuggest
          onSelect={this.onSelect}
          suggestions={suggestions}
          value={value}
          className={className}
          label={translatedLabel}
          placeholder={translatedPlaceholder}
          {...rest}
        />
      </div>
    );
  }
}

const getLocalisedSuggestions = (suggestions, localePrefix, transfomedKeys) => {
  return (
    suggestions &&
    suggestions.length > 0 &&
    suggestions.map((option, key) => {
      option.name = getLocaleLabels(
        option.code,
        localePrefix && !isEmpty(localePrefix)
          ? appendModulePrefix(option.code, localePrefix)
          : option.name,
        transfomedKeys
      );
      return option;
    })
  );
};

const mapStateToProps = (state, ownprops) => {
  const { localizationLabels } = state.app;
  let {
    jsonPath,
    value,
    sourceJsonPath,
    labelsFromLocalisation,
    data,
    localePrefix
  } = ownprops;
  let suggestions =
    data && data.length > 0
      ? data
      : get(state.screenConfiguration.preparedFinalObject, sourceJsonPath, []);
  value = value
    ? value
    : get(state.screenConfiguration.preparedFinalObject, jsonPath);
  //To fetch corresponding labels from localisation for the suggestions, if needed.
  if (labelsFromLocalisation) {
    suggestions = getLocalisedSuggestions(
      JSON.parse(JSON.stringify(suggestions)),
      localePrefix,
      localizationLabels
    );
  }
  //To find correct option object as per the value (for showing the selected value).
  const selectedItem = findItemInArrayOfObject(suggestions, item => {
    if (item.code === value) {
      return true;
    } else return false;
  });
  //Make value object as the Autosuggest expects.
  if (selectedItem && selectedItem.name) {
    value = { label: selectedItem.name, value: selectedItem.code };
  }

  if(jsonPath.includes("edcr.blockDetail")) {
    if(value && Array.isArray(value)){
      let valuesArray = [];
      value.forEach(echValue => {
        let translatedLabel = getLocaleLabels(
          `BPA_SUBOCCUPANCYTYPE_${echValue.value}`,
          `BPA_SUBOCCUPANCYTYPE_${echValue.value}`, 
          localizationLabels
        );
        valuesArray.push({value : echValue.value, label: translatedLabel })
      });
      value = valuesArray;
    }
  }
  // console.log(value, suggestions);
  return { value, jsonPath, suggestions, localizationLabels };
};

const mapDispatchToProps = dispatch => {
  return {
    prepareFinalObject: (path, value) =>
      dispatch(prepareFinalObject(path, value))
    // approveCheck: (jsonPath, value) => {
    //   dispatch(prepareFinalObject(jsonPath, value));
    // }
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AutoSuggestor);