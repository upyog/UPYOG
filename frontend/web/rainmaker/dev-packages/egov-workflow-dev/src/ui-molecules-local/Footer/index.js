import { Container, Item } from "egov-ui-framework/ui-atoms";
import MenuButton from "egov-ui-framework/ui-molecules/MenuButton";
import { setRoute } from "egov-ui-framework/ui-redux/app/actions";
import { toggleSnackbar } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { httpRequest } from "egov-ui-framework/ui-utils/api";
import { hideSpinner, showSpinner } from "egov-ui-kit/redux/common/actions";
import { getTenantId, getUserInfo } from "egov-ui-kit/utils/localStorageUtils";
import get from "lodash/get";
import isEmpty from "lodash/isEmpty";
import set from "lodash/set";
import React from "react";
import { connect } from "react-redux";
import { ActionDialog } from "../";
import {
  getNextFinancialYearForRenewal
} from "../../ui-utils/commons";
import { getDownloadItems } from "./downloadItems";
import "./index.css";

class Footer extends React.Component {
  state = {
    open: false,
    data: {},
    employeeList: []
    //responseLength: 0
  };

  getDownloadData = () => {
    const { dataPath, state } = this.props;
    const data = get(
      state,
      `screenConfiguration.preparedFinalObject.${dataPath}`
    );
    const { status, applicationNumber } = (data && data[0]) || "";
    return {
      label: "Download",
      leftIcon: "cloud_download",
      rightIcon: "arrow_drop_down",
      props: { variant: "outlined", style: { marginLeft: 10 } },
      menu: getDownloadItems(status, applicationNumber, state).downloadMenu
      // menu: ["One ", "Two", "Three"]
    };
  };
  getCurrentFinancialYear = () => {
    var today = new Date();
    var curMonth = today.getMonth();
    var fiscalYr = "";
    if (curMonth >= 3) {
      var nextYr1 = (today.getFullYear() + 1).toString();
      var nextYr1format=nextYr1.substring(2,4);
      fiscalYr = today.getFullYear().toString() + "-" + nextYr1format;
    } else {
      var nextYr2 = today.getFullYear().toString();
      var nextYr2format=nextYr2.substring(2,4);
      fiscalYr = (today.getFullYear() - 1).toString() + "-" + nextYr2format;
    }
    return fiscalYr;
  };
  getPrintData = () => {
    const { dataPath, state } = this.props;
    const data = get(
      state,
      `screenConfiguration.preparedFinalObject.${dataPath}`
    );
    const { status, applicationNumber } = (data && data[0]) || "";
    return {
      label: "Print",
      leftIcon: "print",
      rightIcon: "arrow_drop_down",
      props: { variant: "outlined", style: { marginLeft: 10 } },
      // menu: ["One ", "Two", "Three"]
      menu: getDownloadItems(status, applicationNumber, state).printMenu
    };
  };

  openActionDialog = async (item,label) => {
    const { dataPath, state } = this.props;
    let diffDays;
    debugger;
    const getdate = get(
      state,
      "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.applicationNumber"
    );
    const firenocstatus = get(
      state,
      "screenConfiguration.preparedFinalObject.FireNOCs[0].fireNOCDetails.status"
    );
    if(getdate){
    const cd= getdate.split("PB-FN-");
    const appActualDate=cd[1].slice(0,10);
    console.log(appActualDate);
    const currentDate = new Date();
    const appDate = new Date(cd[1].slice(0,10));
    const diffTime = Math.abs(appDate - currentDate);
    diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
    console.log(diffTime + " milliseconds");
    console.log(diffDays + " days");
    }
    //if(firenocstatus.toUpperCase() == "CITIZENACTIONREQUIRED-DV" || firenocstatus.toUpperCase() == "CITIZENACTIONREQUIRED"){
    if(true){
    if (diffDays>=90){
      alert("You are not eligible for Re-Submit ");
      }
    // console.log(data, "test1");
    // alert("Test Re-Submit");
    else{
    const { handleFieldChange, setRoute, dataPath,onDialogButtonClick  } = this.props;
    let employeeList = [],empList=[]; 
    if (item.buttonLabel === "ACTIVATE_CONNECTION") {
      if (item.moduleName === "NewWS1" || item.moduleName === "NewSW1") {
        item.showEmployeeList = false;
      }
    }
    if (dataPath === "BPA") {
      handleFieldChange(`${dataPath}.comment`, "");
      handleFieldChange(`${dataPath}.assignees`, "");
    } else {
      handleFieldChange(`${dataPath}[0].comment`, "");
      handleFieldChange(`${dataPath}[0].assignee`, []);
    }

    if (item.isLast) {
      let url =
        process.env.NODE_ENV === "development"
          ? item.buttonUrl
          : item.buttonUrl;

      /* Quick fix for edit mutation application */
      if (url.includes('pt-mutation/apply')) {
        url = url + '&mode=MODIFY';
        window.location.href = url.replace("/pt-mutation/", '');
        return;
      }

      setRoute(url);
      return;
    }
    if (item.showEmployeeList) {
      const tenantId = getTenantId();
      const queryObj = [
        {
          key: "roles",
          value: item.roles
        },
        {
          key: "tenantId",
          value: tenantId
        }
      ];
    //   const payload = await httpRequest(
    //     "post",
    //     "/egov-hrms/employees/_search",
    //     "",
    //     queryObj
    //   );
    //   employeeList =
    //     payload &&
    //     payload.Employees.map((item, index) => {
    //       const name = get(item, "user.name");
    //       return {
    //         value: item.uuid,
    //         label: name
    //       };
    //     });
    // }

    const payload = await httpRequest(
      "post",
      "/egov-hrms/employees/_search",
      "",
      queryObj
    );
    empList =payload && payload.Employees.map((item, index) => {
    // Add only User With Active Status 
     const active = JSON.stringify(item.user.active);
      if(active=="true")
      {
        const name = get(item, "user.name");
        return {
          value: item.uuid,
          label: name
        };
      }
      else{
        return { value: item.uuid,
                 label: 'blank'
      };
    }
    });
     empList.forEach((res, index) => {
      if (res.label=='blank') {
        empList.splice(index, 1) // remove element
  };
})
    for (var i of empList) {
  employeeList.push(i);
}  
}

    if(label === "APPROVE"){
      this.setState({ data: item, employeeList });
     
      onDialogButtonClick(label,false);

    }
    else{
      this.setState({ open : true,data: item, employeeList });

    }
    // this.setState({ open: true, data: item, employeeList });
  }
}
else{
  const { handleFieldChange, setRoute, dataPath,onDialogButtonClick  } = this.props;
  let employeeList = [],empList=[]; 
  if (item.buttonLabel === "ACTIVATE_CONNECTION") {
    if (item.moduleName === "NewWS1" || item.moduleName === "NewSW1") {
      item.showEmployeeList = false;
    }
  }
  if (dataPath === "BPA") {
    handleFieldChange(`${dataPath}.comment`, "");
    handleFieldChange(`${dataPath}.assignees`, "");
  } else {
    handleFieldChange(`${dataPath}[0].comment`, "");
    handleFieldChange(`${dataPath}[0].assignee`, []);
  }

  if (item.isLast) {
    let url =
      process.env.NODE_ENV === "development"
        ? item.buttonUrl
        : item.buttonUrl;

    /* Quick fix for edit mutation application */
    if (url.includes('pt-mutation/apply')) {
      url = url + '&mode=MODIFY';
      window.location.href = url.replace("/pt-mutation/", '');
      return;
    }

    setRoute(url);
    return;
  }
  if (item.showEmployeeList) {
    const tenantId = getTenantId();
    const queryObj = [
      {
        key: "roles",
        value: item.roles
      },
      {
        key: "tenantId",
        value: tenantId
      }
    ];
  //   const payload = await httpRequest(
  //     "post",
  //     "/egov-hrms/employees/_search",
  //     "",
  //     queryObj
  //   );
  //   employeeList =
  //     payload &&
  //     payload.Employees.map((item, index) => {
  //       const name = get(item, "user.name");
  //       return {
  //         value: item.uuid,
  //         label: name
  //       };
  //     });
  // }

  const payload = await httpRequest(
    "post",
    "/egov-hrms/employees/_search",
    "",
    queryObj
  );
  empList =payload && payload.Employees.map((item, index) => {
  // Add only User With Active Status 
   const active = JSON.stringify(item.user.active);
    if(active=="true")
    {
      const name = get(item, "user.name");
      return {
        value: item.uuid,
        label: name
      };
    }
    else{
      return { value: item.uuid,
               label: 'blank'
    };
  }
  });
   empList.forEach((res, index) => {
    if (res.label=='blank') {
      empList.splice(index, 1) // remove element
};
})
  for (var i of empList) {
employeeList.push(i);
}  
}

  if(label === "APPROVE"){
    this.setState({ data: item, employeeList });
   
    onDialogButtonClick(label,false);

  }
  else{
    this.setState({ open : true,data: item, employeeList });

  }
  // this.setState({ open: true, data: item, employeeList });
}
  };

  onClose = () => {
    this.setState({
      open: false
    });
  };

  renewTradelicence = async (financialYear, tenantId) => {
    const { setRoute, state, toggleSnackbar } = this.props;
    const licences = get(
      state.screenConfiguration.preparedFinalObject,
      `Licenses`
    );
    this.props.showSpinner();
   var nextFinancialYear = await getNextFinancialYearForRenewal(
      financialYear
    );
    var currentFinancialYear=this.getCurrentFinancialYear();

    if(licences[0].financialYear=='2019-20' || licences[0].financialYear=='2020-21'){
      nextFinancialYear=currentFinancialYear;
    }

    const wfCode = "DIRECTRENEWAL";
    set(licences[0], "action", "INITIATE");
    set(licences[0], "workflowCode", wfCode);
    set(licences[0], "applicationType", "RENEWAL");
    set(licences[0], "financialYear", nextFinancialYear);
    set(licences[0], "tradeLicenseDetail.adhocPenalty", null);
    set(licences[0], "tradeLicenseDetail.adhocExemption", null);
    try {
      const response = await httpRequest(
        "post",
        "/tl-services/v1/_update",
        "",
        [],
        {
          Licenses: licences
        }
      );
      const renewedapplicationNo = get(response, `Licenses[0].applicationNumber`);
      const licenseNumber = get(response, `Licenses[0].licenseNumber`);
      this.props.hideSpinner();
      setRoute(
        `/tradelicence/acknowledgement?purpose=DIRECTRENEWAL&status=success&applicationNumber=${renewedapplicationNo}&licenseNumber=${licenseNumber}&FY=${nextFinancialYear}&tenantId=${tenantId}&action=${wfCode}`
      );
    } catch (exception) {
      this.props.hideSpinner();
      console.log(exception);
      toggleSnackbar(
        true,
        {
          labelName: "Please fill all the mandatory fields!",
          labelKey: exception && exception.message || exception
        },
        "error"
      );

    }

  };
  render() {
    const {
      contractData,
      handleFieldChange,
      onDialogButtonClick,
      dataPath,
      moduleName,
      state,
      dispatch
    } = this.props;
    const { open, data, employeeList } = this.state;
    const { isDocRequired } = data;
    const appName = process.env.REACT_APP_NAME;
    const downloadMenu =
      contractData &&
      contractData.map(item => {
        const { buttonLabel, moduleName } = item;
       
        return {
          labelName: { buttonLabel },
          labelKey: `WF_${appName.toUpperCase()}_${moduleName.toUpperCase()}_${buttonLabel}`,
          link: () => {
            (moduleName === "NewTL" || moduleName === "EDITRENEWAL") && buttonLabel === "APPLY" ? onDialogButtonClick(buttonLabel, isDocRequired) :
              this.openActionDialog(item,buttonLabel);
          }
        };
      });

    if (moduleName === "NewTL") {
      const status = get(
        state.screenConfiguration.preparedFinalObject,
        `Licenses[0].status`
      );
      const applicationType = get(
        state.screenConfiguration.preparedFinalObject,
        `Licenses[0].applicationType`
      );
      const applicationNumber = get(
        state.screenConfiguration.preparedFinalObject,
        `Licenses[0].applicationNumber`
      );
      const tenantId = get(
        state.screenConfiguration.preparedFinalObject,
        `Licenses[0].tenantId`
      );
      const financialYear = get(
        state.screenConfiguration.preparedFinalObject,
        `Licenses[0].financialYear`
      );
      const licenseNumber = get(
        state.screenConfiguration.preparedFinalObject,
        `Licenses[0].licenseNumber`
      );
      const responseLength = get(
        state.screenConfiguration.preparedFinalObject,
        `licenseCount`,
        1
      );

      const rolearray =
        getUserInfo() &&
        JSON.parse(getUserInfo()).roles.filter(item => {
          if (
            (item.code == "TL_CEMP" && item.tenantId === tenantId) ||
            item.code == "CITIZEN"
          )
            return true;
        });
      const rolecheck = rolearray.length > 0 ? true : false;
      const validTo = get(
        state.screenConfiguration.preparedFinalObject,
        `Licenses[0].validTo`
      );
      const currentDate = Date.now();
      const duration = validTo - currentDate;
      const renewalPeriod = get(
        state.screenConfiguration.preparedFinalObject,
        `renewalPeriod`
      );
      if (rolecheck && (status === "APPROVED" || status === "EXPIRED") &&
        duration <= renewalPeriod) {
        const editButton = {
          label: "Edit",
          labelKey: "WF_TL_RENEWAL_EDIT_BUTTON",
          link: () => {
            const baseURL =
              process.env.REACT_APP_NAME === "Citizen"
                ? "/tradelicense-citizen/apply"
                : "/tradelicence/apply";
            this.props.setRoute(
              `${baseURL}?applicationNumber=${applicationNumber}&licenseNumber=${licenseNumber}&tenantId=${tenantId}&action=EDITRENEWAL`
            );
          }
        };

        const submitButton = {
          label: "Submit",
          labelKey: "WF_TL_RENEWAL_SUBMIT_BUTTON",
          link: () => {
            this.renewTradelicence(financialYear, tenantId);
          }
        };
        if (responseLength > 1) {
          if (applicationType !== "NEW") {
            downloadMenu && downloadMenu.push(editButton);
            downloadMenu && downloadMenu.push(submitButton);
          }

        }
        else if (responseLength === 1) {

          downloadMenu && downloadMenu.push(editButton);
          downloadMenu && downloadMenu.push(submitButton);
        }




      }
    }
    const buttonItems = {
      label: { labelName: "Take Action", labelKey: "WF_TAKE_ACTION" },
      rightIcon: "arrow_drop_down",
      props: {
        variant: "outlined",
        style: {
          marginRight: 15,
          backgroundColor: "#FE7A51",
          color: "#fff",
          border: "none",
          height: "60px",
          width: "200px"
        }
      },
      menu: downloadMenu
    };
    return (
      <div className="wf-wizard-footer" id="custom-atoms-footer">
        {!isEmpty(downloadMenu) && (
          <Container>
            <Item xs={12} sm={12} className="wf-footer-container">
              <MenuButton data={buttonItems} />
            </Item>
          </Container>
        )}
        <ActionDialog
          open={open}
          onClose={this.onClose}
          dialogData={data}
          dropDownData={employeeList}
          handleFieldChange={handleFieldChange}
          onButtonClick={onDialogButtonClick}
          dataPath={dataPath}
        />
      </div>
    );
  }
}

const mapStateToProps = state => {
  return { state };
};

const mapDispatchToProps = dispatch => {
  return {
    setRoute: url => dispatch(setRoute(url)),
    toggleSnackbar: (open, message, variant) =>
      dispatch(toggleSnackbar(open, message, variant)),
    showSpinner: () =>
      dispatch(showSpinner()),
    hideSpinner: () =>
      dispatch(hideSpinner())
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Footer);
