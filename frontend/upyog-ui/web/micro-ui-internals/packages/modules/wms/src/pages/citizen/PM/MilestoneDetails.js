import React, {Fragment, useMemo} from 'react'
import { SearchForm, Table, Card,CardHeader, Loader, Header, DownloadBtnCommon, DownloadIcon } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
// import { AddIcon } from '@egovernments/digit-ui-react-components';
import { AddIcon } from "../../../../../../react-components/src/atoms/svgindex";
//why is this icon not getting imported even after putting the right path ??




const MilestoneDetails = () => {

    const { t } = useTranslation();


    // const GetCell = (value) => <span className="cell-text">{value}</span>;
    // const columns = useMemo(
    //     () => [
    //         {
    //             Header: t("ABG_BILL_NUMBER_LABEL"),
    //             disableSortBy: true,
    //             accessor: "billNumber",
    //             Cell: ({ row }) => {
    //                 return getBillLink(row)
    //             },
    //         },
    //         {
    //             Header: t("ABG_COMMON_TABLE_COL_CONSUMER_NAME"),
    //             disableSortBy: true,
    //             Cell: ({ row }) => {
    //                 return GetCell(row.original?.user?.name || "NA");
    //             },
    //         },

    //         {
    //             Header: t("ABG_COMMON_TABLE_COL_BILL_DATE"),
    //             disableSortBy: true,
    //             Cell: ({ row }) => {
    //                 return GetCell(convertEpochToDate(row?.original?.billDate));
    //             },
    //         },
    //         {
    //             Header: t("ABG_COMMON_TABLE_COL_BILL_AMOUNT"),
    //             disableSortBy: true,
    //             Cell: ({ row }) => {
    //                 return GetCell(row?.original?.totalAmount || "NA");
    //             },
    //         },
    //         {
    //             Header: t("ABG_COMMON_TABLE_COL_STATUS"),
    //             disableSortBy: true,
    //             Cell: ({ row }) => {
    //                 return GetCell(row?.original?.status || "NA");
    //             },
    //         },
    //         {
    //             Header: t("ABG_COMMON_TABLE_COL_ACTION"),
    //             disableSortBy: true,
    //             Cell: ({ row }) => {
    //                 return GetCell(getActionItem(row));
    //             },
    //         },
    //     ],
    //     []
    // );

  return (
    <Fragment>
    <CardHeader style={{marginTop:"20px"}}> <AddIcon/> Add Milestone Activities</CardHeader>
    </Fragment>
    

  )
}

export default MilestoneDetails