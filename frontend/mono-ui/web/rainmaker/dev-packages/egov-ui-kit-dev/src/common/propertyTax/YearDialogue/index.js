import { Button, Dialog } from "components";
import commonConfig from "config/common.js";
import formHoc from "egov-ui-kit/hocs/form";
import { fetchGeneralMDMSData, prepareFormData, toggleSpinner } from "egov-ui-kit/redux/common/actions";
import { removeForm } from "egov-ui-kit/redux/form/actions";
import { reset_property_reset } from "egov-ui-kit/redux/properties/actions";
import { resetFormWizard } from "egov-ui-kit/utils/PTCommon";
import Label from "egov-ui-kit/utils/translationNode";
import React, { Component } from "react";
import { connect } from "react-redux";
import RadioButtonForm from "./components/RadioButtonForm";
import { httpRequest } from "../../../utils/api";
import "./index.css";
import { getTenantId, getUserInfo } from "../../../utils/localStorageUtils";
//"egov-ui-kit/utils/localStorageUtils"
var localityCode = null;
var surveyIdcode = null;
var editlocalityCode = null;
const mapStateToProps = (state) => {
  debugger;
localityCode = state.screenConfiguration.preparedFinalObject.propertiesAudit[0].address.locality.code;
editlocalityCode = state.screenConfiguration.preparedFinalObject.propertiesAudit[0].surveyId
;
surveyIdcode = state.screenConfiguration.preparedFinalObject.propertiesAudit[0].surveyId;
  const { common, form } = state;
  const { generalMDMSDataById } = common;
  const FinancialYear = generalMDMSDataById && generalMDMSDataById.FinancialYear;
  const getYearList = FinancialYear?Object.keys(FinancialYear).sort().reverse():null;
  return { getYearList, form };
};
var tenantIdcode =getTenantId();
var isLocMatch ;
const getUserDataFromUuid = async (state, dispatch) => {
  debugger;
  let request = { searchCriteria: { tenantId: tenantIdcode} };
  try {
    const response = await httpRequest(
      "/egov-searcher/rainmaker-pt-gissearch/GetTenantConfig/_get",
      "_get",
      [],
      request);
    if (response) {
      const data = response.data.find(obj => {
        return obj.locality == localityCode;
      });
      isLocMatch = data ? true : false;
    }
  } catch (error) {
    console.log("functions-js getUserDataFromUuid error", error);
  }
};
// const getYearList = () => {
//   let today = new Date();
//   let month = today.getMonth() + 1;
//   let yearRange = [];
//   var counter = 0;
//   if (month <= 3) {
//     return getLastFiveYear(yearRange, today.getFullYear() - 1, counter);
//   } else {
//     return getLastFiveYear(yearRange, today.getFullYear(), counter);
//   }
// };

// const getLastFiveYear = (yearRange, currentYear, counter) => {
//   if (counter < 5) {
//     counter++;
//     yearRange.push(`${currentYear}-${currentYear + 1}`);
//     getLastFiveYear(yearRange, currentYear - 1, counter);
//   }
//   return yearRange;
// };

const YearDialogueHOC = formHoc({ formKey: "financialYear", path: "PropertyTaxPay", isCoreConfiguration: true })(RadioButtonForm);

class YearDialog extends Component {
  state = {
    selectedYear: ''
  }
  handleRadioButton = (e) => {
    this.setState({
      selectedYear: e.target.value
    })
  }
  componentDidMount = () => {
    const { fetchGeneralMDMSData, toggleSpinner } = this.props;
    const requestBody = {
      MdmsCriteria: {
        tenantId: commonConfig.tenantId,
        moduleDetails: [
          {
            moduleName: "egf-master",
            masterDetails: [
              {
                name: "FinancialYear",
                filter: "[?(@.module == 'PT')]",
              },
            ],
          },
        ],
      },
    };
    toggleSpinner();
    fetchGeneralMDMSData(requestBody, "egf-master", ["FinancialYear"]);
    toggleSpinner();
  };

  resetForm = () => {
    const { form, removeForm, prepareFormData } = this.props;
    resetFormWizard(form, removeForm);
    prepareFormData("Properties", []);
  };

  render() {
    let { open, closeDialogue, getYearList, history, form, removeForm, urlToAppend, reset_property_reset } = this.props;
    return getYearList ? (
      <Dialog
        open={open}
        children={[
          <div key={1}>
            <div className="dialogue-question">
              <Label label="PT_FINANCIAL_YEAR_PLACEHOLDER" fontSize="20px" color="black" />
            </div>
            <div className="year-range-botton-cont">
              {getYearList &&
                Object.values(getYearList).map((item, index) => (
                  <YearDialogueHOC
                    handleRadioButton={this.handleRadioButton}
                    selectedYear={this.state.selectedYear}
                    key={index}
                    label={item}
                    history={history}
                    resetFormWizard={this.resetForm}
                    urlToAppend={urlToAppend}
                  />
                ))}
            </div>
            <div className='year-dialogue-button'>
              <Button
                label={<Label label="PT_CANCEL" buttonLabel={true} color="black" />}
                onClick={() => { closeDialogue() }}
                labelColor="#fe7a51"
                buttonStyle={{ border: "1px solid rgb(255, 255, 255)" }}></Button>
              <Button
                label={<Label label="PT_OK" buttonLabel={true} color="black" />}
                labelColor="#fe7a51"
                buttonStyle={{ border: "1px solid rgb(255, 255, 255)" }} onClick={ async() => {
                  if(tenantIdcode == "pb.jalandhar" || tenantIdcode == "pb.testing"){
                    await getUserDataFromUuid();
                  }
                   
                 if(isLocMatch){
                    if (this.state.selectedYear !== '' && surveyIdcode != null) {
                      this.resetForm()
                      history && urlToAppend ? history.push(`${urlToAppend}&FY=${this.state.selectedYear}`) : history.push(`/property-tax/assessment-form`);
                    }
                    // else if(this.state.selectedYear !== ''){
                    //   this.resetForm()
                    //   history && urlToAppend ? history.push(`${urlToAppend}&FY=${this.state.selectedYear}`) : history.push(`/property-tax/assessment-form`);
                    // }
                    else {
                      alert('Please Select a Financial Year and Enter Survey Id');
                    }
                  } else{
                  if (this.state.selectedYear !== '') {
                    this.resetForm()
                    history && urlToAppend ? history.push(`${urlToAppend}&FY=${this.state.selectedYear}`) : history.push(`/property-tax/assessment-form`);
                  }
                  else {
                    alert('Please Select a Financial Year! and Enter Survey Id');
                  }
                }
                }}></Button>
            </div>
          </div>,
        ]}
        bodyStyle={{ backgroundColor: "#ffffff" }}
        isClose={false}
        onRequestClose={closeDialogue}
        contentClassName="year-dialog-content"
        className="year-dialog"
      />
    ) : null;
  }
}


const mapDispatchToProps = (dispatch) => {
  
  return {
    fetchGeneralMDMSData: (requestBody, moduleName, masterName) => dispatch(fetchGeneralMDMSData(requestBody, moduleName, masterName)),
    removeForm: (formkey) => dispatch(removeForm(formkey)),
    toggleSpinner: () => dispatch(toggleSpinner()),
    prepareFormData: (path, value) => dispatch(prepareFormData(path, value)),
    reset_property_reset: () => dispatch(reset_property_reset())
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(YearDialog);
