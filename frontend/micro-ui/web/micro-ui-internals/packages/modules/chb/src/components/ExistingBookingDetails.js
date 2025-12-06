import React, { useState, useEffect } from "react";
import { Loader, Card, KeyNote } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";

/**
 * ExistingBookingDetails Component
 * 
 * This component is responsible for displaying and managing the details of an existing booking in the CHB module.
 * It allows users to view and update booking details, including bank details, documents, and other metadata.
 * 
 * Props:
 * - `onSubmit`: Callback function triggered when the user submits the updated booking details.
 * - `setExistingDataSet`: Function to update the existing dataset with the fetched booking details.
 * - `Searchdata`: Object containing search parameters for fetching booking details, including:
 *    - `communityHallCode`: Code of the community hall.
 *    - `bookingStartDate`: Start date of the booking.
 *    - `bookingEndDate`: End date of the booking.
 *    - `hallCode`: Code of the hall.
 * 
 * State Variables:
 * - `filters`: State variable to manage the filters applied for fetching booking details.
 * - `isDataSet`: Boolean state to track whether the booking data has been set.
 * 
 * Hooks:
 * - `useTranslation`: Provides the `t` function for internationalization.
 * 
 * Variables:
 * - `tenantId`: The current tenant ID fetched using the Digit ULB Service.
 * 
 * Logic:
 * - Fetches booking details based on the `Searchdata` parameters using the `useChbSlotSearch` hook (commented out in the current code).
 * - Processes the fetched booking details and updates the session data with:
 *    - Bank details: Includes account number, IFSC code, bank name, branch name, and account holder name.
 *    - Documents: Maps the fetched documents to a structured format for further processing.
 * 
 * Functions:
 * - `setchbData`: Processes the fetched booking details and updates the session data with the relevant information.
 *    - Extracts bank details and documents from the `application` object.
 *    - Prepares the data in a structured format for use in the application workflow.
 * 
 * Returns:
 * - A component that displays the existing booking details and allows users to update or manage the booking.
 */
export const ExistingBookingDetails = ({ onSubmit,setExistingDataSet,Searchdata }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [filters, setFilters] = useState(null);
  const [isDataSet, setIsDataSet] = useState(false); // State to track if data has been set

  // Define the slot_search hook to refetch data on search
  // const {refetch} = Digit.Hooks.chb.useChbSlotSearch({
  //   tenantId:tenantId,
  //   filters: {
  //     communityHallCode:Searchdata.communityHallCode,
  //     bookingStartDate:Searchdata.bookingStartDate,
  //     bookingEndDate:Searchdata.bookingEndDate,
  //     hallCode:Searchdata.hallCode,
  //     isTimerRequired:true,
  //   }
  // });

  const setchbData = (application) => {
    // const result =refetch();
    const newSessionData = {
      bankdetails: {
        accountNumber: application?.applicantDetail?.accountNumber,
        confirmAccountNumber: application?.applicantDetail?.accountNumber,
        ifscCode: application?.applicantDetail?.ifscCode,
        bankName: application?.applicantDetail?.bankName,
        bankBranchName: application?.applicantDetail?.bankBranchName,
        accountHolderName: application?.applicantDetail?.accountHolderName,
      },
      documents: {
        documents: application?.documents?.map(doc => ({
          documentDetailId: doc?.documentDetailId,
          documentType: doc?.documentType,
          fileStoreId: doc?.fileStoreId,
        })),
      },
      address: {
        pincode: application?.address?.pincode,
        city: application?.address?.city,
        locality: application?.address?.locality,
        houseNo: application?.address?.houseNo,
        streetName: application?.address?.streetName,
        landmark: application?.address?.landmark,
      },
      ownerss: {
        applicantName: application?.applicantDetail?.applicantName,
        mobileNumber: application?.applicantDetail?.applicantMobileNo,
        alternateNumber: application?.applicantDetail?.applicantAlternateMobileNo,
        emailId: application?.applicantDetail?.applicantEmailId,
      },
      slots: {
        specialCategory: { i18nKey: application?.specialCategory?.category, code: application?.specialCategory?.category, value: application?.specialCategory?.category },
        purpose: { i18nKey: application?.purpose?.purpose, code: application?.purpose?.purpose, value: application?.purpose?.purpose },
        purposeDescription: application?.purposeDescription,
      },
      timervalue:{
        // timervalue:result?.timerValue || 0
        timervalue:1800
      }
    };
    setExistingDataSet(newSessionData);
    setIsDataSet(true);  // Set the flag to true after data is set
  };

  useEffect(() => {
    if (isDataSet) { // If data is set, call onSubmit
      onSubmit();
      setIsDataSet(false);  // Reset the flag after onSubmit is called
    }
  }, [isDataSet, onSubmit]);

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
  let initialFilters = !isNaN(parseInt(filter))
    ? { limit: "3", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId }
    : { limit: "3", sortOrder: "ASC", sortBy: "createdTime", offset: "0", tenantId };

  useEffect(() => {
    setFilters(initialFilters);
  }, [filter]);

  // Use the search hook with dynamic filters
  const { isLoading, data } = Digit.Hooks.chb.useChbSearch({ filters });

  if (isLoading) {
    return <Loader />;
  }

  const filteredApplications = data?.hallsBookingApplication || [];
  const applicationContainerStyle = {
    padding: '10px',
    margin: '10px 0',
    border: '1px solid #ccc',
    transition: 'background-color 0.3s ease, box-shadow 0.3s ease',
  };

  const applicationContainerHoverStyle = {
    boxShadow: '1px 4px 4px 7px rgba(0, 0, 0, 0.5)', // Black shadow with 50% opacity
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
                  // Trigger the setchbData function with the clicked application data
                  setchbData(application);
                }}
              >
                <KeyNote keyValue={t("CHB_BOOKING_NO")} note={application?.bookingNo} />
                <KeyNote keyValue={t("CHB_APPLICANT_NAME")} note={application?.applicantDetail?.applicantName} />
                <KeyNote keyValue={t("CHB_COMMUNITY_HALL_NAME")} note={t(`${application?.communityHallCode}`)} />
                <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`${application?.bookingStatus}`)} />
              </Card>
            </div>
          ))}
        {filteredApplications.length === 0 && !isLoading && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>
            {t("CHB_NO_APPLICATION_FOUND_MSG")}
          </p>
        )}
      </div>
    </React.Fragment>
  );
};
