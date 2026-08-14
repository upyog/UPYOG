import React, { useState, useEffect } from "react";
import { Loader, Card, KeyNote } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { getSlotSearchCriteria } from "../utils";

/**
 * ExistingBookingDetails fetches and displays existing booking applications.
 * useADSSearch to retrieve booking data.
 * useADSSlotSearch to fetch slot availability.
 * Populates session state with selected booking details.
 * Triggers `onSubmit` after session data is updated.
 */

export const ExistingBookingDetails = ({
  onSubmit,
  setExistingDataSet,
  Searchdata,
  selectedLocation // Added selectedLocation prop to allow map to raw location code
}) => {
  const {
    t
  } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [filters, setFilters] = useState(null);
  const user = Digit.UserService.getUser().info;

  // Slot search data for Ads (Advertisement)
  const slotSearchData = Digit.Hooks.ads.useADSSlotSearch();

  const formdata = {
    advertisementSlotSearchCriteria: getSlotSearchCriteria(Searchdata, tenantId, selectedLocation)
  };
  const setchbData = async application => {
    const result = await slotSearchData.mutateAsync(formdata);
    /* timerValue is resolved directly from top-level of response payload per backend contract */
    const timerValue = result?.timerValue;
    const newSessionData = {
      documents: {
        documents: application?.documents?.map(doc => ({
          documentDetailId: doc?.documentDetailId,
          documentType: doc?.documentType,
          fileStoreId: doc?.fileStoreId
        }))
      },
      address: {
        pincode: application?.address?.pincode,
        houseNo: application?.address?.houseNo,
        streetName: application?.address?.streetName,
        landmark: application?.address?.landmark,
        cityValue: {
          code: application?.address?.city,
          value: application?.address?.city,
          i18nKey: application?.address?.city,
          city: {
            name: application?.address?.city,
            code: application?.address?.cityCode
          }
        },
        locality: {
          code: application?.address?.localityCode,
          value: application?.address?.locality,
          i18nKey: application?.address?.locality
        },
        addressline1: application?.address?.addressLine1,
        addressline2: application?.address?.addressLine2
      },
      applicant: {
        applicantName: application?.applicantDetail?.applicantName,
        mobileNumber: application?.applicantDetail?.applicantMobileNo,
        alternateNumber: application?.applicantDetail?.applicantAlternateMobileNo,
        emailId: application?.applicantDetail?.applicantEmailId
      },
      timervalue: {
        timervalue: timerValue || 0,
        /* Store lock start timestamp to calculate elapsed offset during navigation remounts */
        timerStartedAt: Date.now()
      },
      draftId: result?.draftId || ""
    };
    setExistingDataSet(newSessionData);
    /* Propagate sessionData synchronously to avoid React state batching race conditions in goNext */
    onSubmit(newSessionData);
  };

  // URL parsing for dynamic filter values
  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 3;
  }
  let initialFilters = !isNaN(parseInt(filter)) ? {
    limit: "3",
    sortOrder: "ASC",
    sortBy: "createdTime",
    offset: off,
    tenantId
  } : {
    limit: "3",
    sortOrder: "ASC",
    sortBy: "createdTime",
    offset: "0",
    tenantId,
    mobileNumber: user?.mobileNumber
  };
  useEffect(() => {
    setFilters(initialFilters);
  }, [filter]);

  // Use the search hook with dynamic filters
  const {
    isLoading,
    data
  } = Digit.Hooks.ads.useADSSearch({
    filters
  });
  if (isLoading) {
    return <Loader />;
  }
  const filteredApplications = data?.bookingApplication || [];
  const applicationContainerStyle = {
    padding: '10px',
    margin: '10px 0',
    border: '1px solid #ccc',
    transition: 'background-color 0.3s ease, box-shadow 0.3s ease'
  };
  const applicationContainerHoverStyle = {
    boxShadow: '1px 4px 4px 7px rgba(0, 0, 0, 0.5)' // Black shadow with 50% opacity
  };
  return <React.Fragment>
      <div>
        {filteredApplications.length > 0 && filteredApplications.map((application, index) => <div key={index}>
              
  <Card style={{
          ...applicationContainerStyle,
          cursor: "pointer"
        }} onMouseEnter={e => {
          e.currentTarget.style.backgroundColor = applicationContainerHoverStyle.backgroundColor;
          e.currentTarget.style.boxShadow = applicationContainerHoverStyle.boxShadow;
        }} onMouseLeave={e => {
          e.currentTarget.style.backgroundColor = '';
          e.currentTarget.style.boxShadow = '';
        }} onClick={() => {
          // Trigger the setchbData function with the clicked application data
          setchbData(application);
        }}>
                <KeyNote keyValue={t("ADS_BOOKING_NO")} note={application?.bookingNo} />
                <KeyNote keyValue={t("ADS_APPLICANT_NAME")} note={application?.applicantDetail?.applicantName} />
                <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`${application?.bookingStatus}`)} />
              </Card>
            </div>)}
        {filteredApplications.length === 0 && !isLoading && <p className="ads-auto-43">
            {t("ADS_NO_APPLICATION_FOUND_MSG")}
          </p>}
      </div>
    </React.Fragment>;
};
