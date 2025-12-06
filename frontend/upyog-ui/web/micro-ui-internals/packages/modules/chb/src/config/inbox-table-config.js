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
            return (
              <div>
                <span className="link">
                  
                  <Link to={`${props.parentRoute}/application-details/` + `${row?.original?.searchData?.["bookingNo"]}`}>

                    {row.original?.searchData?.["bookingNo"]}
                  </Link>
                </span>
              </div>
            );
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["bookingNo"]),
        },
        
        {
          Header: t("CHB_APPLICANT_NAME"),
          Cell: ( row ) => {
          
            return GetCell(`${row?.cell?.row?.original?.searchData?.applicantDetail?.["applicantName"]}`)
            
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.applicantDetail?.["applicantName"]),
          
        },
        {
          Header: t("CHB_COMMUNITY_HALL_NAME"),
          Cell: ({ row }) => {
            return GetCell(`${t(row.original?.searchData?.["communityHallCode"])}`);
           
          },
          mobileCell: (original) => GetMobCell(`${t(original?.searchData?.["communityHallCode"])}`),
        },

        {
          Header: t("CHB_BOOKING_DATE"),
          Cell: ({ row }) => {
            return row?.original?.searchData?.bookingSlotDetails.length > 1 
            ? GetCell(`${row?.original?.searchData?.bookingSlotDetails[0]?.["bookingDate"]}` + " - " + `${row?.original?.searchData?.bookingSlotDetails[row?.original?.searchData?.bookingSlotDetails.length-1]?.["bookingDate"]}`) 
            : GetCell(`${row?.original?.searchData?.bookingSlotDetails[0]?.["bookingDate"]}`);
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.bookingSlotDetails.length > 1 
            ? GetCell(`${row?.original?.searchData?.bookingSlotDetails[0]?.["bookingDate"]}` + " - " + `${row?.original?.searchData?.bookingSlotDetails[row?.original?.searchData?.bookingSlotDetails.length-1]?.["bookingDate"]}`) 
            : GetCell(`${row?.original?.searchData?.bookingSlotDetails[0]?.["bookingDate"]}`)),
        },

        
        {
          Header: t("CHB_STATUS"),
          Cell: ({ row }) => {
            
            const wf = row.original?.workflowData;
            return GetCell(t(`${row?.original?.workflowData?.state?.["applicationStatus"]}`));


          },
          mobileCell: (original) => GetMobCell(t(`ES_CHB_COMMON_STATUS_${original?.workflowData?.state?.["applicationStatus"]}`)),
        

        },
        
      ],
      serviceRequestIdKey: (original) => original?.[t("CHB_INBOX_UNIQUE_BOOKING_NO")]?.props?.children,

      
    },
  });
