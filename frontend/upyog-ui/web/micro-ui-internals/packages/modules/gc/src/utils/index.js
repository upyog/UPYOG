// Determines if the back button should be hidden based on the current URL matching screen paths in the config array.

import { CloseSvg } from "@nudmcdgnpm/digit-ui-react-components";

// Returns true to hide the back button if a match is found; otherwise, returns false.
export const shouldHideBackButton = (config = []) => {
    return config.filter((key) => window.location.href.includes(key.screenPath)).length > 0;
};

export const checkForNotNull = (value = "") => {
    return value && value !== null && value !== undefined && value !== "";
};

export const checkForNA = (value = "") => {
    return checkForNotNull(value) ? value : "NA";
};

export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher === "") return str;

    while (str.includes(searcher)) {
        str = str.replace(searcher, replaceWith);
    }

    return str;
};

export const pdfDownloadLink = (documents = {}, fileStoreId = "", format = "") => {
    let downloadLink = documents[fileStoreId] || "";
    let differentFormats = downloadLink?.split(",") || [];
    let fileURL = "";

    differentFormats.length > 0 &&
        differentFormats.forEach((link) => {
            if (
                !link.includes("large") &&
                !link.includes("medium") &&
                !link.includes("small")
            ) {
                fileURL = link;
            }
        });

    return fileURL;
};

/**
 * Applicant Details
 */
export const setApplicantDetails = (data) => {
    let owners = [];
    if (Array.isArray(data?.owners)) {
        owners = data.owners;
    } else if (Array.isArray(data?.owner?.owner)) {
        owners = data.owner.owner;
    } else if (Array.isArray(data?.owner)) {
        owners = data.owner;
    } else if (data?.owner && typeof data.owner === 'object') {
        if (data.owner.name || data.owner.mobileNumber || data.owner.applicantName) {
            owners = [data.owner];
        } else {
            owners = Object.values(data.owner).filter(o => o?.name || o?.mobileNumber || o?.applicantName);
        }
    }

    data.owners = owners.map(owner => {
        if (owner && owner.applicantName && !owner.name) {
            return { ...owner};
        }
        return owner;
    });
    return data;
};

/**
 * Property Location Details
 */
export const setPropertyLocationDetails = (data) => {
    let gcpropertylocdetails = data?.gcpropertylocdetails || data?.address || {};

    let propertyLocation = {
        propertyId: gcpropertylocdetails?.propertyId,
        houseNo: gcpropertylocdetails?.houseNo,
        houseName: gcpropertylocdetails?.houseName,
        streetName: gcpropertylocdetails?.streetName,
        addressline1: gcpropertylocdetails?.addressline1,
        addressline2: gcpropertylocdetails?.addressline2,
        landmark: gcpropertylocdetails?.landmark,
        city: gcpropertylocdetails?.city?.code || gcpropertylocdetails?.city,
        locality: gcpropertylocdetails?.locality?.code || gcpropertylocdetails?.locality,
        pincode: gcpropertylocdetails?.pincode,
    };

    data.propertyLocation = propertyLocation;
    return data;
};

/**
 * Garbage Specification Details
 */
export const setGarbageSpecificationDetails = (data) => {
    let gcspecifications = data?.gcspecifications || data?.GCSpecifications || {};
    let specialCategoryVal = data?.gcspecialcategory?.specialCategory || data?.specialCategory || "";

    let garbageSpecification = {
        oldGarbageId: gcspecifications?.oldGarbageId,

        typeOfCollection: gcspecifications?.typeOfCollection?.code || gcspecifications?.typeOfCollection,

        propertyOwnerType: gcspecifications?.propertyOwnerType?.code || gcspecifications?.propertyOwnerType,

        name: gcspecifications?.name,

        phoneNumber: gcspecifications?.phoneNumber,

        gender: gcspecifications?.gender?.code || gcspecifications?.gender,

        email: gcspecifications?.email,

        category: gcspecifications?.category?.code || gcspecifications?.category,

        subCategory: gcspecifications?.subCategory?.code || gcspecifications?.subCategory,

        subCategoryType: gcspecifications?.subCategoryType?.code || gcspecifications?.subCategoryType,

        isVariableCalculation:
            gcspecifications?.isVariableCalculation || false,

        isbulkgeneration:
            gcspecifications?.isbulkgeneration || false,

        no_of_units: Number(gcspecifications?.no_of_units) || 0,

        isAdditional: gcspecifications?.isAdditional || false,

        isInheritance: data?.owner?.isInheritance || gcspecifications?.isInheritance || false,

        specialCategory: specialCategoryVal?.code || specialCategoryVal || "",
    };

    data.garbageSpecification = garbageSpecification;
    return data;
};

/**
 * Document Details
 */
export const setDocumentsDetails = (data) => {
    let docs = data?.gcdocuments?.documents || data?.documents || [];

    let documents = docs.map((doc) => ({
        uuid: doc?.uuid,
        documentType: doc?.documentType,
        fileStoreId: doc?.fileStoreId,
        documentUid: doc?.documentUid || doc?.fileStoreId,
    })) || [];

    data.documents = documents;
    return data;
};

/**
 * Main Payload Converter
 */
export const GCDataConvert = (data) => {
    data = setApplicantDetails(data);
    data = setPropertyLocationDetails(data);
    data = setGarbageSpecificationDetails(data);
    data = setDocumentsDetails(data);

    const formData = {
        garbageAccounts: [
            {
                tenantId: data?.tenantId || Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId(),

                applicantDetails: data?.owners,

                propertyLocation: data.propertyLocation,

                garbageSpecification: data.garbageSpecification,

                documents: data.documents,

                applicationStatus: "APPLICATION_CREATED",

                workflow: {
                    action: "APPLY",
                },
            }
        ]
    };

    return formData;
};

export const GCAPIToFormData = (application, params) => {
  const updatedApplication = { ...application };

  // Applicant Details
  updatedApplication.additionalDetail = {
    ...application.additionalDetail,
    applicantDetails: [
      {
        ...application?.additionalDetail?.applicantDetails?.[0],
        ...params.owner,
      },
    ],
  };

  // Basic Details
  updatedApplication.name = params?.gcspecifications?.name;
  updatedApplication.mobileNumber = params?.gcspecifications?.phoneNumber;
  updatedApplication.gender = params?.gcspecifications?.gender?.value || "";
  updatedApplication.emailId = params?.gcspecifications?.email;

  // Address
  updatedApplication.addresses = [
    {
      ...application?.addresses?.[0],
      city: params?.gcpropertylocdetails?.city?.value,
      pincode: params?.gcpropertylocdetails?.pincode,
      address1: params?.gcpropertylocdetails?.addressline1,
      address2: params?.gcpropertylocdetails?.addressline2,
      additionalDetail: {
        ...application?.addresses?.[0]?.additionalDetail,
        locality: params?.gcpropertylocdetails?.locality?.value,
        houseNo: params?.gcpropertylocdetails?.houseNo,
        houseName: params?.gcpropertylocdetails?.houseName,
        streetName: params?.gcpropertylocdetails?.streetName,
        landmark: params?.gcpropertylocdetails?.landmark,
      },
    },
  ];

  // Collection Unit
  updatedApplication.grbgCollectionUnits = [
    {
      ...application?.grbgCollectionUnits?.[0],
      ownerType: params?.gcspecifications?.propertyOwnerType?.value,
      unitType: params?.gcspecifications?.typeOfCollection?.value,
      category: params?.gcspecifications?.category?.value,
      subCategory: params?.gcspecifications?.subCategory?.value,
      subCategoryType: params?.gcspecifications?.subCategoryType?.value,
      specialCategory:
        params?.gcspecialcategory?.specialCategory?.value || "",
      oldGarbageId: params?.gcspecifications?.oldGarbageId,
      isVariableCalculation:
        params?.gcspecifications?.isVariableCalculation,
      isbulkgeneration: params?.gcspecifications?.isbulkgeneration,
      no_of_units: Number(params?.gcspecifications?.no_of_units || 0),
      isInheritance: params?.gcspecifications?.isInheritance,
    },
  ];

  // Documents
  updatedApplication.documents =
    params?.gcdocuments?.documents?.map((doc) => {
      const existing = application?.documents?.find(
        (d) => d.documentType === doc.documentType
      );

      return {
        ...existing,
        ...doc,
        garbageId: application?.garbageId,
      };
    }) || [];

  return updatedApplication;
};