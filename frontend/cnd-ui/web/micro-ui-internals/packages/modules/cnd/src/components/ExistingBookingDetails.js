import React, { useState, useEffect } from "react";
import { Loader, Card, KeyNote } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";

export const ExistingBookingDetails = ({ onSubmit, setExistingDataSet }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [filters, setFilters] = useState(null);
  const [isDataSet, setIsDataSet] = useState(false);
  const { offset } = useParams();

  const setCndData = (application) => {
    const siteMediaPhoto = application?.documentDetails?.find(doc => doc.documentType === "siteMediaPhoto");
    const siteStackPhoto = application?.documentDetails?.find(doc => doc.documentType === "siteStack");

    // Create indexed waste type details
    const wasteTypeIndexed = {};
    application?.wasteTypeDetails?.forEach((waste, index) => {
      wasteTypeIndexed[index] = {
        wasteType: {
          code: waste.wasteType,
          i18nKey: waste.wasteType
        },
        quantity: waste.quantity?.toString() || "0",
        metrics: waste.metrics || ""
      };
    });

    const newSessionData = {
      owner: {
        applicantName: application?.applicantDetail?.nameOfApplicant,
        mobileNumber: application?.applicantDetail?.mobileNumber,
        alternateNumber: application?.applicantDetail?.alternateMobileNumber || "",
        emailId: application?.applicantDetail?.emailId,
      },
      propertyNature: {
        houseArea: application?.houseArea?.toString(),
        propertyUsage: {
          i18nKey: application?.propertyType,
          code: application?.propertyType,
          value: application?.propertyType
        },
        constructionFrom: application?.constructionFromDate,
        constructionTo: application?.constructionToDate,
        constructionType: {
          i18nKey: application?.typeOfConstruction,
          code: application?.typeOfConstruction,
          value: application?.typeOfConstruction
        }
      },
      addressDetails: {
        selectedAddressStatement: {
          pinCode: application?.addressDetail?.pinCode,
          city: application?.addressDetail?.city,
          address: application?.addressDetail?.addressLine1,
          type: application?.addressDetail?.addressType,
          addressType: application?.addressDetail?.addressType,
          address2: application?.addressDetail?.addressLine2,
          houseNumber: application?.addressDetail?.houseNumber,
          landmark: application?.addressDetail?.landmark,
          locality: application?.addressDetail?.locality,
        }
      },
      wasteType: {
        ...wasteTypeIndexed,
        wasteMaterialType: application?.wasteTypeDetails?.map(waste => ({
          i18nKey: waste.wasteType,
          code: waste.wasteType,
          value: waste.wasteType
        })) || [],
        wasteQuantity: application?.totalWasteQuantity?.toString() || "",
        pickupDate: application?.requestedPickupDate || "",
        siteMediaPhoto: siteMediaPhoto?.fileStoreId || "",
        siteStack: siteStackPhoto?.fileStoreId || "",
        wasteDetails: {}
      },
      requestType: {
        totalWasteQuantity: application?.totalWasteQuantity?.toString() || "",
        requestedPickupDate: application?.requestedPickupDate || "",
        requestType: {
          i18nKey: "REQ_PICK_UP",
          code: "REQ_PICK_UP",
          value: "REQ_PICK_UP"
        }
      },
      siteMediaPhoto: siteMediaPhoto ? [{ fileStoreId: siteMediaPhoto.fileStoreId }] : [],
      siteStack: siteStackPhoto ? [{ fileStoreId: siteStackPhoto.fileStoreId }] : []
    };
    setExistingDataSet(newSessionData);
    setIsDataSet(true);
  };

  useEffect(() => {
    const submitCallback = () => {
      if (isDataSet) {
        onSubmit();
        setIsDataSet(false);
      }
    };

    submitCallback();
  }, [isDataSet, onSubmit]);

  let paginationOffset = offset && !isNaN(parseInt(offset)) ? offset : "0";
  let initialFilters = {
    limit: "3",
    sortOrder: "ASC",
    sortBy: "createdTime",
    offset: paginationOffset,
    tenantId
  };

  useEffect(() => {
    setFilters(initialFilters);
  }, [offset]);

  const { isLoading, data } = Digit.Hooks.cnd.useCndSearchApplication(
    {
      tenantId,
      filters: { mobileNumber: Digit.UserService.getUser().info?.mobileNumber, isUserDetailRequired: true, limit: "3", offset: "0" },
      auth: {}
    },
    {}
  );

  if (isLoading) {
    return <Loader />;
  }

  const filteredApplications = data?.cndApplicationDetail || [];
  const applicationContainerStyle = {
    padding: '10px',
    margin: '10px 0',
    border: '1px solid #ccc',
    transition: 'background-color 0.3s ease, box-shadow 0.3s ease',
  };

  const applicationContainerHoverStyle = {
    boxShadow: '1px 4px 4px 7px rgba(0, 0, 0, 0.5)',
  };

  return (
    <React.Fragment>
      <div>
        {filteredApplications.length > 0 &&
          filteredApplications.map((application, index) => (
            <div key={index}>
              <Card
                style={{ ...applicationContainerStyle, cursor: "pointer" }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.backgroundColor = applicationContainerHoverStyle.backgroundColor;
                  e.currentTarget.style.boxShadow = applicationContainerHoverStyle.boxShadow;
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.backgroundColor = '';
                  e.currentTarget.style.boxShadow = '';
                }}
                onClick={() => {
                  setCndData(application);
                }}
              >
                <KeyNote keyValue={t("APPLICATION_NO")} note={application?.applicationNumber} />
                <KeyNote keyValue={t("APPLICANT_NAME")} note={application?.applicantDetail?.nameOfApplicant} />
              </Card>
            </div>
          ))}
        {filteredApplications.length === 0 && !isLoading && (
          <p className="cnd-existing-booking-details-empty-text">
            {t("NO_APPLICATION_FOUND_MSG")}
          </p>
        )}
      </div>
    </React.Fragment>
  );
};
