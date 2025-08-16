import React, { useState, useEffect } from "react";
import { Loader, Card, KeyNote } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import {useParams} from "react-router-dom";

export const ExistingBookingDetails = ({ onSubmit,setExistingDataSet }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [filters, setFilters] = useState(null);
  const [isDataSet, setIsDataSet] = useState(false); // State to track if data has been set
  const { offset } = useParams(); 

   // Function to set the data and update the state
  const setwtData = (application) => {
    const newSessionData = {
      address: {
        houseNo: application?.address?.houseNo,
        streetName: application?.address?.streetName,
        addressline1: application?.address?.addressLine1,
        addressline2: application?.address?.addressLine2,
        landmark: application?.address?.landmark,
        pincode: application?.address?.pincode,
        cityValue: {code:application?.address?.city, value:application?.address?.city, i18nKey:application?.address?.city, 
        city:{name:application?.address?.city,code:application?.address?.cityCode}},
        locality: {code:application?.address?.localityCode, value:application?.address?.locality, i18nKey:application?.address?.locality},
      },
      owner: {
        applicantName: application?.applicantDetail?.name,
        mobileNumber: application?.applicantDetail?.mobileNumber,
        alternateNumber: application?.applicantDetail?.alternateNumber,
        emailId: application?.applicantDetail?.emailId,

      },
      requestDetails: {
        deliveryDate: application?.deliveryDate,
        deliveryTime: application?.deliveryTime,
        waterQuantity: {code: application?.waterQuantity, value: application?.waterQuantity, i18nKey: application?.waterQuantity},
        tankerType: {code: application?.tankerType, value : application?.tankerType, i18nKey: application?.tankerType},
        description: application?.description,
      },
    };
    setExistingDataSet(newSessionData);
    setIsDataSet(true);  // Set the flag to true after data is set
  };
  // useEffect hook to call onSubmit when data is set
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

  // Use the search hook with filters
  const { isLoading, data } = Digit.Hooks.wt.useTankerSearchAPI({ filters });

  if (isLoading) {
    return <Loader />;
  }

  const filteredApplications = data?.waterTankerBookingDetail || [];
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
                  // Trigger the setwtData function with the clicked application data
                  setwtData(application);
                }}
              >
                
                <KeyNote keyValue={t("BOOKING_NO")} note={application?.bookingNo} />
                <KeyNote keyValue={t("APPLICANT_NAME")} note={application?.applicantDetail?.name} />
                <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(`${application?.bookingStatus}`)} />
              </Card>
            </div>
          ))}
        {filteredApplications.length === 0 && !isLoading && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>
            {t("NO_APPLICATION_FOUND_MSG")}
          </p>
        )}
      </div>
    </React.Fragment>
  );
};
