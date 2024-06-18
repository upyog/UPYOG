import { DetailsCard, Loader, Table, Modal,SearchField,SubmitBar,SearchForm } from "@egovernments/digit-ui-react-components";
import React, { memo, useEffect, useMemo, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import PropertyInvalidMobileNumber from "../../pages/citizen/MyProperties/PropertyInvalidMobileNumber";
import { useQuery, useQueryClient } from "react-query";
const GetCell = (value) => <span className="cell-text">{value}</span>;

const SearchPTID = ({ tenantId, t, payload, showToast, setShowToast,ptSearchConfig }) => {
  const history = useHistory();
  
  const [searchQuery, setSearchQuery] = useState({
    /* ...defaultValues,   to enable pagination */
    ...payload,
  });
  const [showModal, setShowModal] = useState(false);
  const [showUpdateNo, setShowUpdateNo] = useState(false);
  const [selectedProperty, setSelectedProperty] = useState(null);
  const [ownerInvalidMobileNumberIndex, setOwnerInvalidMobileNumberIndex] = useState(0);
const [showDownloads, setShowDownloads] = useState(false);
const [groupBillrecords, setGroupBillrecords] = useState([]);
console.log("payload", payload);
let filters={ ...payload,isDefaulterNoticeSearch:true }
const args = tenantId ? { tenantId, filters } : { filters };
const { isLoading, error, data, isSuccess } = useQuery(["propertySearchList", tenantId,filters ], () => Digit.PTService.search(args));
  

  const mutation = Digit.Hooks.pt.usePropertyAPI(tenantId, false);

  const UpdatePropertyNumberComponent = Digit?.ComponentRegistryService?.getComponent("EmployeeUpdateOwnerNumber");
  const { data: updateNumberConfig } = Digit.Hooks.useCommonMDMSV2(Digit.ULBService.getStateId(), "PropertyTax", ["UpdateNumber"], {
    select: (data) => {
      return data?.PropertyTax?.UpdateNumber?.[0];
    },
    retry: false,
    enable: false,
  });

  const handleCollectTaxClick = (val) => {
    let isAtleastOneMobileNumberInvalid = false

    let { owners } = val;

    owners = owners && owners.filter(owner => owner.status == "ACTIVE");
    owners && owners.map((owner, index) => {
      let number = owner.mobileNumber;
      
      if (
          (
            (number == updateNumberConfig?.invalidNumber)
            || !number.match(updateNumberConfig?.invalidPattern) 
            && number == JSON.parse(getUserInfo()).mobileNumber
          )
        ) {
        isAtleastOneMobileNumberInvalid = true;
        setOwnerInvalidMobileNumberIndex(index);
      }
    })

    if(isAtleastOneMobileNumberInvalid) {
      setShowModal(true);
      setSelectedProperty(val);
    } else {
      revalidate();
      history.push(`/digit-ui/employee/payment/collect/PT/${val?.["propertyId"]}`)
    }

  }

  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
      <path d="M0 0h24v24H0V0z" fill="none" />
      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>
  );

  const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };

  const skipNContinue = () => {
    history.push(`/digit-ui/employee/payment/collect/PT/${selectedProperty?.['propertyId']}`)
  }

  const updateMobileNumber = () => {
    const ind = ownerInvalidMobileNumberIndex;

    setShowModal(true);
    setShowUpdateNo({
      name: selectedProperty?.owners[ind]?.name,
      mobileNumber: selectedProperty?.owners[ind]?.mobileNumber,
      index: ind,
    });
  }

  const columns = useMemo(
    () => [
      {
        Header: t("PT_COMMON_TABLE_COL_PT_ID"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <div>
              <span className="link">
                <Link to={`/digit-ui/employee/pt/ptsearch/property-details/${row.original["propertyId"]}`}>{row.original["propertyId"]}</Link>
              </span>
            </div>
          );
        },
      },
      {
        Header: t("PT_COMMON_TABLE_COL_OWNER_NAME"),
        disableSortBy: true,
        Cell: ({ row }) => GetCell(`${row.original.owners.map((ob) => ob.name).join(",")}` || ""),
      },
      {
        Header: t("PT_COMMON_TABLE_COL_STATUS_LABEL"),
        Cell: ({ row }) => GetCell(t(row?.original?.status || "NA")),
        disableSortBy: true,
      },
      {
        Header: t("PT_AMOUNT_DUE"),
        Cell: ({ row }) => GetCell(row?.original?.dueAmount?`₹ ${row?.original?.dueAmount}`:t("PT_NA")),
        disableSortBy: true,
      },
      {
        Header: t("PT_AMOUNT_YEAR"),
        Cell: ({ row }) => GetCell(row?.original?.dueAmountYear?`${row?.original?.dueAmountYear}`:t("PT_NA")),
        disableSortBy: true,
      },
      {
        Header: t("PT_PROPERTY_TYPE"),
        Cell: ({ row }) => GetCell(row?.original?.propertyType?`${row?.original?.propertyType}`:t("PT_NA")),
        disableSortBy: true,
      }
    ],
    []
  );
  const columns2 = useMemo(
    () => [
   
      {
        Header: t("ES_TOTAL_RECORD"),
        disableSortBy: true,
        Cell: ({ row }) => GetCell(t(row.original.totalrecords) || ""),
      },
      {
        Header: t("ES_TOTAL_RECORD_COMPLETED"),
        disableSortBy: true,
        Cell: ({ row }) => GetCell(t(row.original.recordscompleted) || ""),
      },
      {
        Header: t("ES_TOTAL_RECORD_STATUS"),
        disableSortBy: true,
        Cell: ({ row }) => GetCell(t(row.original.status) || ""),
      },
  
      {
        Header: t("ES_SEARCH_ACTION"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <div>
              {Digit.Utils.didEmployeeHasRole("PT_CEMP") ? (
                <span className="link"> 
                  <a  style={{textDecoration:'none'}} onClick={() => downloadNotice(row.original)}>{t("ES_PT_COLLECT_TAX")}</a>
                </span>
              ) : null}
            </div>
          );
        },
      },
    ],
    []
  );
   const pdfDownloadLink = (documents = {}, fileStoreId = "", format = "") => {
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
const downloadNotice = async (document) => {
    console.log("document",document)
    let fileStoreIds= [document.filestoreid]
    const res = await Digit.UploadServices.Filefetch([document?.filestoreid], tenantId);
    console.log("ressss",res)
   let documentLink = pdfDownloadLink(res.data, document?.filestoreid);
   window.open(documentLink, "_blank");
  };
  const onSubmit= async ()=>{
  let key
  let properties
    let response = await Digit.PTService.generateDefaulterNotice(tenantId,properties =Object.values(data?.Properties || {}));
    setShowToast({
        label: `${t("GRP_JOB_INITIATED_STATUS")} ${response?.jobId}`
    })  
}
  let isMobile = window.Digit.Utils.browser.isMobile();

  if (isLoading) {
    showToast && setShowToast(null);
    return <Loader />;
  }
//   if (error) {
//     !showToast && setShowToast({ error: true, label: error?.response?.data?.Errors?.[0]?.code || error });
//     return null;
//   }
const onViewDownload =async () =>{
    let response = await Digit.PTService.getDefaulterNoticeStatus({offset:0,limit:100});
    setShowDownloads(true)
    setGroupBillrecords(response.groupBillrecords)
    console.log("response",response)
}
  const PTEmptyResultInbox = memo(Digit.ComponentRegistryService.getComponent("PTEmptyResultInbox"));
  const getData = (tableData = []) => {
    return tableData?.map((dataObj) => {
      const obj = {};
      columns.forEach((el) => {
        if (el.Cell) obj[el.Header] = el.Cell({row:{original:dataObj}});
      });
      return obj;
    });
  };
  const tableData = Object.values(data?.Properties || {}) || [];
  if(ptSearchConfig?.ptSearchCount&&payload.locality&&tableData&&tableData.length>ptSearchConfig?.ptSearchCount){
   // !showToast &&setShowToast({ error: true, label: "PT_MODIFY_SEARCH_CRITERIA" });
    return null;
  }
  const tableData2 = Object.values(groupBillrecords || {}) || [];
 
  return (
    <React.Fragment>
      {data?.Properties?.length === 0 ? (
        <PTEmptyResultInbox data={true}></PTEmptyResultInbox>
      ) : isMobile ? (
        <DetailsCard data={getData(tableData)} t={t} />
      ) : (
        <div>

       
        <Table
          t={t}
          data={tableData}
          totalRecords={data?.Properties?.length}
          columns={columns}
          getCellProps={(cellInfo) => {
            return {
              style: {
                padding: "20px 18px",
                fontSize: "16px",
              },
            };
          }}
          manualPagination={false}
          disableSort={true}
        />
         {/* <SearchForm onSubmit={onSubmit} className={"pt-property-search"} handleSubmit={onSubmit}> */}
         <div style={{display:"flex", flexDirection:"row-reverse"}}>   
         <div style={{width:"inherit",marginLeft:"10px"}}>
         <SearchField className="pt-search-action-submit" >
          <SubmitBar
            label={t("CS_COMMON_GENERATE_NOTICE")}
            onSubmit={onSubmit}
          />
        </SearchField>
         </div>
    <div style={{width:"inherit",marginLeft:"10px"}}>
        <SearchField className="pt-search-action-submit">
          {/* <SubmitBar label={t("ES_COMMON_SEARCH")} submit /> */}
          <SubmitBar
            label={t("CS_COMMON_DOWNLOADS")}
            onSubmit={onViewDownload}
          />
        </SearchField></div></div>
      
        {showDownloads &&(
        <Table
          t={t}
          data={tableData2}
          totalRecords={groupBillrecords?.length}
          columns={columns2}
          getCellProps={(cellInfo) => {
            return {
              style: {
                padding: "20px 18px",
                fontSize: "16px",
              },
            };
          }}
          manualPagination={false}
          disableSort={true}
        />)}
         </div>
      )}

    </React.Fragment>
  );
};

export default SearchPTID;
