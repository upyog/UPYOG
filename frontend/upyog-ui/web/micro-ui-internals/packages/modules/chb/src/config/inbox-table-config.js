  import React from "react";
  import { Link } from "react-router-dom";

  const GetCell = (value) => <span className="cell-text">{value}</span>;
  

  const GetSlaCell = (value) => {
    if (isNaN(value)) return <span className="sla-cell-success">0</span>;
    return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>;
  };

  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

  export const TableConfig = (t) => ({
    CHB: {
      inboxColumns: (props) => [
        {
          Header: t("CHB_BOOKING_NO"),
          Cell: ({ row }) => {
            const bookingNo = row?.original?.searchData?.["bookingNo"];
            return (
              <div>
                <span className="link">
                  <Link to={`${props?.parentRoute}/application-details/` + `${bookingNo}`}>
                    {bookingNo || t("CS_NA")}
                  </Link>
                </span>
              </div>
            );
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["bookingNo"] || t("CS_NA")),
        },
        
        {
          Header: t("CHB_APPLICANT_NAME"),
          Cell: ({ row }) => {
            const applicantName = row?.original?.searchData?.applicantDetail?.["applicantName"] || row?.cell?.row?.original?.searchData?.applicantDetail?.["applicantName"];
            return GetCell(applicantName || t("CS_NA"));
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.applicantDetail?.["applicantName"] || t("CS_NA")),
        },
        {
          Header: t("CHB_VENUE_NAME_LABEL"),
          Cell: ({ row }) => {
            const hallOrVenueCode = row?.original?.searchData?.["venueCode"] || row?.original?.searchData?.["communityHallCode"];
            return GetCell(hallOrVenueCode ? t(hallOrVenueCode) : t("CS_NA"));
          },
          mobileCell: (original) => {
            const hallOrVenueCode = original?.searchData?.["venueCode"] || original?.searchData?.["communityHallCode"];
            return GetMobCell(hallOrVenueCode ? t(hallOrVenueCode) : t("CS_NA"));
          },
        },

        {
          Header: t("CHB_BOOKING_DATE"),
          Cell: ({ row }) => {
            const slots = row?.original?.searchData?.bookingSlotDetails;
            if (!slots || !slots.length) return GetCell(t("CS_NA"));
            return slots.length > 1 
              ? GetCell(`${slots[0]?.["bookingDate"]}` + " - " + `${slots[slots.length-1]?.["bookingDate"]}`) 
              : GetCell(`${slots[0]?.["bookingDate"]}`);
          },
          mobileCell: (original) => {
            const slots = original?.searchData?.bookingSlotDetails;
            if (!slots || !slots.length) return GetMobCell(t("CS_NA"));
            return slots.length > 1 
              ? GetMobCell(`${slots[0]?.["bookingDate"]}` + " - " + `${slots[slots.length-1]?.["bookingDate"]}`) 
              : GetMobCell(`${slots[0]?.["bookingDate"]}`);
          },
        },

        {
          Header: t("CHB_STATUS"),
          Cell: ({ row }) => {
            const status = row?.original?.workflowData?.state?.["applicationStatus"] || row?.original?.searchData?.["bookingStatus"];
            return GetCell(status ? t(status) : t("CS_NA"));
          },
          mobileCell: (original) => {
            const status = original?.workflowData?.state?.["applicationStatus"] || original?.searchData?.["bookingStatus"];
            return GetMobCell(status ? t(`ES_CHB_COMMON_STATUS_${status}`) || t(status) : t("CS_NA"));
          },
        },
      ],
      serviceRequestIdKey: (original) => original?.searchData?.["bookingNo"] || original?.[t("CHB_INBOX_UNIQUE_BOOKING_NO")]?.props?.children,
    },
  });
