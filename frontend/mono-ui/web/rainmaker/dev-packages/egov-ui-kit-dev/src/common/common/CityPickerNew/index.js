import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import ArrowDropDownIcon from "@material-ui/icons/ArrowDropDown";
import Label from "egov-ui-kit/utils/translationNode";
import { getCityNameByCode } from "egov-ui-kit/utils/commons";
import { getTranslatedLabel } from "egov-ui-framework/ui-utils/commons";
import get from "lodash/get";

const containerStyle = {
  position: "relative",
  width: "100%",
  marginBottom: "1rem",
  marginTop: "12px"
};

const inputBoxStyle = {
  width: "100%",
  height: "44px",
  padding: "12px 40px 12px 15px",
  border: "1px solid #b3b3b3",
  borderRadius: "10px",
  fontSize: "16px",
  outline: "none",
  boxSizing: "border-box",
  transition: "all 0.2s ease",
  backgroundColor: "#fff",
  color: "#1B1B1B",
  letterSpacing: "0.7px",
};

const inputFocusStyle = {
  borderColor: "#0060BD",
  boxShadow: "0 0 0 3px rgba(0,96,189,0.15)",
};

const suffixIconStyle = {
  position: "absolute",
  right: "15px",
  top: "50%",
  transform: "translateY(-50%)",
  color: "#757575",
  pointerEvents: "none",
  fontSize: "22px",
};

const dropdownStyle = {
  position: "absolute",
  top: "110%",
  left: 0,
  right: 0,
  backgroundColor: "#fff",
  border: "1px solid #ddd",
  borderRadius: "8px",
  boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
  zIndex: 300,
  maxHeight: "250px",
  overflowY: "auto",
};

const listItemStyle = {
  padding: "10px 15px",
  borderBottom: "1px solid #eee",
  cursor: "pointer",
  fontSize: "16px",
  color: "#484848",
};

class CityPickerFieldNew extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isFocused: false,
      open: false,
      searchTerm: "",
    };
  }

  handleFocus = () => {
    this.setState({ isFocused: true, open: true });
  };

  handleBlur = () => {
    // small delay to allow click on dropdown item
    setTimeout(() => this.setState({ isFocused: false, open: false }), 150);
  };

  handleSearchChange = (e) => {
    this.setState({ searchTerm: e.target.value, open: true });
  };

  handleCityClick = (cityCode, cityName) => {
    const { fieldKey, onChange } = this.props;
    onChange(fieldKey, cityCode);
    this.setState({ searchTerm: cityName, open: false });
  };

  getFilteredCities = () => {
    const { cities, localizationLabels } = this.props;
    const { searchTerm } = this.state;
    if (!searchTerm) return cities;

    return cities.filter((city) => {
      const cityName = getTranslatedLabel(
        `TENANT_TENANTS_${city.key.toUpperCase().replace(/[.:-\s\/]/g, "_")}`,
        localizationLabels
      );
      return cityName && cityName.toLowerCase().includes(searchTerm.toLowerCase());
    });
  };

  componentDidMount() {
    const { field, localizationLabels } = this.props;
    if (field && field.value) {
      const selectedCity = getCityNameByCode(field.value, localizationLabels);
      this.setState({ searchTerm: selectedCity || "" });
    }
  }

  render() {
    const { isFocused, open, searchTerm } = this.state;
    const { localizationLabels } = this.props;
    const filteredCities = this.getFilteredCities();

    return (
      <div style={containerStyle}>
        <input
          type="text"
          placeholder="Select City"
          value={searchTerm}
          onChange={this.handleSearchChange}
          onFocus={this.handleFocus}
          onBlur={this.handleBlur}
          style={{
            ...inputBoxStyle,
            ...(isFocused ? inputFocusStyle : {}),
          }}
        />
        <ArrowDropDownIcon style={suffixIconStyle} />

        {open && (
          <div style={dropdownStyle}>
            {filteredCities.length > 0 ? (
              filteredCities.map((city, i) => {
                const cityName = getTranslatedLabel(
                  `TENANT_TENANTS_${city.key.toUpperCase().replace(/[.:-\s\/]/g, "_")}`,
                  localizationLabels
                );
                return (
                  <div
                    key={i}
                    style={listItemStyle}
                    onMouseDown={() => this.handleCityClick(city.key, cityName)}
                  >
                    {cityName}
                  </div>
                );
              })
            ) : (
              <div style={{ padding: "12px 15px", color: "#999" }}>No City Found</div>
            )}
          </div>
        )}
      </div>
    );
  }
}

CityPickerFieldNew.propTypes = {
  className: PropTypes.string,
  fieldKey: PropTypes.string,
  field: PropTypes.object,
  cities: PropTypes.array,
  localizationLabels: PropTypes.object,
  onChange: PropTypes.func.isRequired,
};

const mapStateToProps = (state) => {
  const cities = get(state, "common.cities", []);
  const localizationLabels = get(state, "app.localizationLabels", {});
  return { cities, localizationLabels };
};

export default connect(mapStateToProps)(CityPickerFieldNew);
