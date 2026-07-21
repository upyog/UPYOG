import React from "react";

/* method to get date from epoch */
export const convertEpochToDate = (dateEpoch) => {
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

export const businessServiceList = (isCode = false) => {
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
};

export const getCurrentFinancialYearForFireNoc = () => {
  var today = new Date();
  var curMonth = today.getMonth();
  var fiscalYr = "";
  if (curMonth > 3) {
    var nextYr1 = (today.getFullYear() + 1).toString();
    fiscalYr = today.getFullYear().toString() + "-" + nextYr1.slice(-2);
  } else {
    var nextYr2 = today.getFullYear().toString();
    fiscalYr = (today.getFullYear() - 1).toString() + "-" + nextYr2.slice(-2);
  }
  return fiscalYr;
};

export const convertDateToEpoch = (dateString) => {
  try {
    if (!dateString) return null;
    const parts = dateString.split("-");
    const date = new Date(parts[0], parts[1] - 1, parts[2]);
    return date.getTime();
  } catch (e) {
    return null;
  }
};

/**
 * decoulped pure serializer mapping wizard formData to final FireNOCs[0] payload
 */
export const convertToFireNOCPayload = (formData, { action = "INITIATE", isUpdate = false, existingApplication = null }) => {
  const locationData = formData?.property?.location?.property || formData?.location;
  const tenantId = locationData?.city?.code || Digit.ULBService.getCurrentTenantId();

  const parseCoordinate = (val) => {
    const num = parseFloat(val);
    return isNaN(num) ? null : num;
  };

  // 1. Root application fields
  const payload = {
    tenantId,
    fireNOCDetails: {
      action,
      channel: "CITIZEN",
      fireNOCType: formData?.nocType?.nocType || "NEW",
      financialYear: getCurrentFinancialYearForFireNoc(),
      noOfBuildings: formData?.property?.noOfBuildings || "SINGLE",
      additionalDetail: {
        documents: []
      },
      documents: []
    }
  };

  // 2. Applicant / owners
  const owners = (formData?.owners?.owners || []).map((owner, index) => {
    const ownerObj = {
      name: owner.name,
      mobileNumber: owner.mobileNumber,
      gender: owner.gender?.code || owner.gender || "MALE",
      relationship: owner.relationship?.code || owner.relationship || "FATHER",
      dob: convertDateToEpoch(owner.dob),
      fatherOrHusbandName: owner.fatherOrHusbandName,
      correspondenceAddress: owner.correspondenceAddress,
      emailId: owner.emailId || null,
      pan: owner.pan || null,
      ownerType: owner.ownerType?.code || owner.ownerType || "NONE"
    };

    // 8. Update-specific ID preservation for owners
    if (isUpdate && existingApplication) {
      const existingOwners = existingApplication?.fireNOCDetails?.applicantDetails?.owners || [];
      const match = existingOwners[index] || existingOwners.find(o => o.mobileNumber === owner.mobileNumber);
      if (match) {
        ownerObj.id = match.id;
        ownerObj.uuid = match.uuid;
        if (match.addresses) {
          ownerObj.addresses = match.addresses;
        }
      }
    }
    return ownerObj;
  });

  payload.fireNOCDetails.applicantDetails = {
    ownerShipType: formData?.ownershipCategory?.ownershipCategory?.code || "INDIVIDUAL.SINGLEOWNER",
    ownerShipMajorType: formData?.ownershipCategory?.ownershipCategory?.code?.split(".")?.[0] || "INDIVIDUAL",
    owners,
    additionalDetail: {}
  };

  // 3. Property and address
  const address = {
    tenantId,
    city: locationData?.city?.code || tenantId,
    doorNo: locationData?.plotNo || null,
    pincode: locationData?.pincode || null,
    street: locationData?.streetName || null,
    buildingName: locationData?.buildingName || null,
    locality: {
      code: locationData?.locality?.code || ""
    }
  };

  const lat = parseCoordinate(locationData?.latitude);
  const lng = parseCoordinate(locationData?.longitude);
  if (lat !== null) address.latitude = lat;
  if (lng !== null) address.longitude = lng;

  // 8. Update-specific ID preservation for address
  if (isUpdate && existingApplication) {
    const existingAddress = existingApplication?.fireNOCDetails?.propertyDetails?.address;
    if (existingAddress) {
      address.id = existingAddress.id;
    }
  }

  const propertyDetails = { address };

  if (typeof locationData?.propertyId === "string" && locationData.propertyId.trim()) {
    propertyDetails.propertyId = locationData.propertyId.trim();
  }

  payload.fireNOCDetails.propertyDetails = propertyDetails;

  // 8. Update-specific ID preservation for propertyDetails root id
  if (isUpdate && existingApplication) {
    payload.fireNOCDetails.propertyDetails.id = existingApplication?.fireNOCDetails?.propertyDetails?.id;
  }

  // 4. Buildings and UOMs
  payload.fireNOCDetails.buildings = (formData?.property?.buildings || []).map((building, index) => {
    const requiredUoms = building?.buildingSubUsageType?.uom || [];
    const allUoms = [
      ...new Set([
        ...requiredUoms,
        ...["NO_OF_FLOORS", "NO_OF_BASEMENTS", "PLOT_SIZE", "BUILTUP_AREA", "HEIGHT_OF_BUILDING"]
      ])
    ];

    const finalUoms = [];
    allUoms.forEach((uom) => {
      const val = building?.uomsMap?.[uom];
      if (val !== undefined && val !== null && val !== "") {
        const uomObj = {
          code: uom,
          value: parseInt(val),
          isActiveUom: requiredUoms.includes(uom),
          active: true
        };

        // 8. Update-specific ID preservation for building uoms
        if (isUpdate && existingApplication) {
          const existingBuildings = existingApplication?.fireNOCDetails?.buildings || [];
          const existingBuilding = existingBuildings[index] || existingBuildings.find(b => b.name === building.name);
          const existingUom = existingBuilding?.uoms?.find(u => u.code === uom);
          if (existingUom) {
            uomObj.id = existingUom.id;
          }
        }
        finalUoms.push(uomObj);
      }
    });

    // 6. Building documents
    const buildingDocs = (formData?.documents?.documents || [])
      .filter((doc) => doc.buildingName === building.name)
      .map((doc) => ({
        tenantId,
        documentType: doc.documentType,
        fileStoreId: doc.fileStoreId
      }));

    const buildingObj = {
      name: building.name,
      usageType: building?.buildingSubUsageType?.code || null,
      usageTypeMajor: building?.buildingUsageType?.code || building?.buildingSubUsageType?.code?.split(".")?.[0] || null,
      uomsMap: building?.uomsMap || null,
      uoms: finalUoms,
      applicationDocuments: buildingDocs
    };

    // 8. Update-specific ID preservation for buildings
    if (isUpdate && existingApplication) {
      const existingBuildings = existingApplication?.fireNOCDetails?.buildings || [];
      const match = existingBuildings[index] || existingBuildings.find(b => b.name === building.name);
      if (match) {
        buildingObj.id = match.id;
        buildingObj.tenantId = match.tenantId || tenantId;
      }
    }
    return buildingObj;
  });

  // 5. Owner documents
  const ownerDocs = (formData?.documents?.documents || [])
    .filter((doc) => !doc.buildingName)
    .map((doc) => ({
      tenantId,
      documentType: doc.documentType,
      fileStoreId: doc.fileStoreId
    }));

  payload.fireNOCDetails.applicantDetails.additionalDetail.documents = ownerDocs;

  // 7. Derived / default fields
  payload.fireNOCDetails.firestationId = locationData?.fireStation?.code || locationData?.fireStation || null;

  if (formData?.nocType?.provisionalNocNumber) {
    payload.provisionFireNOCNumber = formData.nocType.provisionalNocNumber;
  }
  if (formData?.nocType?.oldFireNOCNumber) {
    payload.oldFireNOCNumber = formData.nocType.oldFireNOCNumber;
  }

  // 8. Update-specific ID preservation at root level
  if (isUpdate && existingApplication) {
    payload.id = existingApplication.id;
    if (existingApplication.fireNOCNumber) {
      payload.fireNOCNumber = existingApplication.fireNOCNumber;
    }
    if (existingApplication.dateOfApplied) {
      payload.dateOfApplied = existingApplication.dateOfApplied;
    }
    payload.fireNOCDetails.id = existingApplication.fireNOCDetails?.id;
    if (existingApplication.fireNOCDetails?.applicationNumber) {
      payload.fireNOCDetails.applicationNumber = existingApplication.fireNOCDetails.applicationNumber;
    }
    if (existingApplication.fireNOCDetails?.applicationDate) {
      payload.fireNOCDetails.applicationDate = existingApplication.fireNOCDetails.applicationDate;
    }
    payload.fireNOCDetails.additionalDetail = {
      ...existingApplication.fireNOCDetails?.additionalDetail,
    };
  }

  return {
    FireNOCs: [payload]
  };
};

export { default as getNocAcknowledgementData } from "./getNocAcknowledgementData";