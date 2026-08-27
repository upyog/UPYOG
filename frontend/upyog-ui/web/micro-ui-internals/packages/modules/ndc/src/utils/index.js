import React from "react";

/* methid to get date from epoch */
export const convertEpochToDate = (dateEpoch) => {
    // Returning null in else case because new Date(null) returns initial date from calender
    if (dateEpoch) {
        const dateFromApi = new Date(dateEpoch);
        let month = dateFromApi.getMonth() + 1;
        let day = dateFromApi.getDate();
        let year = dateFromApi.getFullYear();
        month = (month > 9 ? "" : "0") + month;
        day = (day > 9 ? "" : "0") + day;
        return `${day}/${month}/${year}`;
    } else {
        return null;
    }
};

export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
  };

export const businessServiceList = (isCode= false) => {
    let isSearchScreen = window.location.href.includes("/search");
    const availableBusinessServices = [{
        code: isSearchScreen ? "FIRE_NOC" : "FIRE_NOC_SRV",
        active: true,
        roles: ["FIRE_NOC_APPROVER"],
        i18nKey: "WF_FIRE_NOC_FIRE_NOC_SRV",
    }, {
        code: isSearchScreen ? "AIRPORT_AUTHORITY" : "AIRPORT_NOC_SRV",
        active: true,
        roles: ["AIRPORT_AUTHORITY_APPROVER"],
        i18nKey: "WF_FIRE_NOC_AIRPORT_NOC_SRV"
    }];

    const newAvailableBusinessServices = [];
    const loggedInUserRoles = Digit.UserService.getUser().info.roles;
    availableBusinessServices.map(({ roles }, index) => {
        roles.map((role) => {
            loggedInUserRoles.map((el) => {
                if (el.code === role) {
                    isCode ? newAvailableBusinessServices.push(availableBusinessServices?.[index]?.code) : newAvailableBusinessServices.push(availableBusinessServices?.[index])
                }
            })
        })
    });

    return newAvailableBusinessServices;
}

export const pdfDownloadLink = (documents = {}, fileStoreId = "", format = "") => {
    /* Need to enhance this util to return required format*/
  
    let downloadLink = documents[fileStoreId] || "";
    let differentFormats = downloadLink?.split(",") || [];
    let fileURL = "";
    differentFormats.length > 0 &&
      differentFormats.map((link) => {
        if (!link.includes("large") && !link.includes("medium") && !link.includes("small")) {
          fileURL = link;
        }
      });
    return fileURL;
  };

  export const pdfDocumentName = (documentLink = "", index = 0) => {
    let documentName = decodeURIComponent(documentLink.split("?")[0].split("/").pop().slice(13)) || `Document - ${index + 1}`;
    return documentName;
  };

  export const EmployeeData = (tenantId, approver) => {
  const employeeDataByCode = Digit.Hooks.useEmployeeSearch(tenantId, { codes: approver, isActive: true }, { enabled: !!approver });
  const employeeDataByName = Digit.Hooks.useEmployeeSearch( tenantId, { names: approver, isActive: true }, { enabled: !!approver && !employeeDataByCode?.data?.Employees?.length } );
  const employeeData = employeeDataByCode?.data?.Employees?.length ? employeeDataByCode : employeeDataByName;
  const officerRaw = employeeData?.data?.Employees?.[0];
  const officerAssignment = officerRaw?.assignments?.[0];

  const officer = officerRaw
    ? {
        code: officerRaw?.code,
        id: officerRaw?.id,
        name: officerRaw?.user?.name,
        department: officerAssignment?.department,
        designation: officerAssignment?.designation,
      }
    : null;

  return { officer };
};

export const downloadNDCAcknowledgement = async (application, tenants, t) => {
  const appData = application?.Applications?.[0] || application?.data?.Applications?.[0] || application;
  const tenantInfo = tenants?.find((tenant) => tenant.code === appData?.tenantId);
  const getNdcAcknowledgementData = (await import("../getNdcAcknowledgementData")).default;
  const ackData = await getNdcAcknowledgementData(application, tenantInfo, t);
  Digit.Utils.pdf.generate(ackData);
};

export const downloadNDCReceipt = async (tenantId, payments, application) => {
  const paymentList = Array.isArray(payments) ? payments : [payments];
  let response;
  if (paymentList[0]?.fileStoreId) {
    response = { filestoreIds: [paymentList[0].fileStoreId] };
  } else {
    response = await Digit.PaymentService.generatePdf(
      tenantId,
      {
        Payments: [
          {
            ...(paymentList[0] || {}),
            ...(application?.Applications?.[0] || application || {}),
          },
        ],
      },
      "ndc-receipt"
    );
  }
  const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
  window.open(fileStore[response.filestoreIds[0]], "_blank");
};