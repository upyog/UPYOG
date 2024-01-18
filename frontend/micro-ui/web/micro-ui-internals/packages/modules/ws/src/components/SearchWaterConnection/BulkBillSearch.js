import React, { Fragment, useState,useEffect, useCallback, useMemo } from "react";
import { SearchForm, Table, Card, Loader, Header,Toast,DownloadBtnCommon, UploadFile } from "@egovernments/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import SearchFields from "./SearchFields";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import MobileSearchWater from "./MobileSearchWater";
import { useHistory } from "react-router-dom";
import {convertDateToEpoch} from "../../utils/index"
import * as XLSX from "xlsx";
const BulkBillSearch = ({ tenantId, onSubmit, data, count, resultOk, businessService, isLoading }) => {
  const history = useHistory()
  const [result,setResult]=  useState([])
  const [showToast, setShowToast] = useState(null);
  const [uploadedFile, setUploadedFile] = useState("a");
  const [file,setFile] = useState("")
  const [items, setItems] = useState([]);
  function selectfile(e) {
    console.log("eeeeee",e)
    e.preventDefault()
      setFile(e.target.files[0]);
      readExcel(e.target.files[0]);

    }
  const replaceUnderscore = (str) => {
    str = str.replace(/_/g, " ");
    return str;
  };


  const readExcel = (file) => {
    console.log("filefile",file)
    const promise = new Promise((resolve, reject) => {
      const fileReader = new FileReader();
      fileReader.readAsArrayBuffer(file);

      fileReader.onload = (e) => {
        const bufferArray = e.target.result;

        const wb = XLSX.read(bufferArray, { type: "buffer" });

        const wsname = wb.SheetNames[0];

        const ws = wb.Sheets[wsname];

        const data = XLSX.utils.sheet_to_json(ws);
        
        let d=new Date((44944 - (25567 + 1))*86400*1000)
        //console.log("datadatadata",data,d)
        console.log("fileReader",data)
        let dnwq=data.map((ff)=>{
          console.log("ff",ff)
         ff.currentReadingDate = convertDateToEpoch(ff.currentReadingDate)
        })
        console.log("datadatadatagggggg",dnwq)
        resolve(data);
      };

      fileReader.onerror = (error) => {
        reject(error);
      };
    });

    promise.then((d) => {
      console.log("dd",d)
      //setItems(d);
    });
  };

  const convertEpochToDate = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
      return "NA";
    }
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${day}/${month}/${year}`;
  };
  useEffect(async () => {
    const payload = {
      "BulkBillCriteria": {
        "tenantId": "pg.citya"
      }
    }
    let data = await Digit.WSService.WSSewsearchDemand(payload, window.location.href.includes("ws/sewerage/search-demand") ? "sw" : "ws")
    setResult(data.connection)
  }, [])
  const { t } = useTranslation();
  const { register, control, handleSubmit, setValue, getValues, reset } = useForm({
    defaultValues: {
      offset: 0,
      limit: 10,
      sortBy: "commencementDate",
      sortOrder: "DESC",
      searchType: "CONNECTION",
    },
  });
  const DownloadBtn = (props) => {
    return (
        <div onClick={props.onClick}>
            <DownloadBtnCommon />
        </div>
    );
};
// function selectfile(e) {
//   //setFile(e.target.files[0]);
//   readExcel(e.target.files[0]);

// }
  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    register("sortBy", "commencementDate");
    register("searchType", "CONNECTION");
    register("sortOrder", "DESC");
    register("propertyId", "");
  }, [register]);

  const onSort = useCallback((args) => {
    if (args.length === 0) return;
    setValue("sortBy", args.id);
    setValue("sortOrder", args.desc ? "DESC" : "ASC");
  }, []);

  function onPageSizeChange(e) {
    setValue("limit", Number(e.target.value));
    handleSubmit(onSubmit)();
  }

  function nextPage() {
    setValue("offset", getValues("offset") + getValues("limit"));
    handleSubmit(onSubmit)();
  }
  function previousPage() {
    setValue("offset", getValues("offset") - getValues("limit"));
    handleSubmit(onSubmit)();
  }
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (isMobile) {
    return <MobileSearchWater {...{ Controller, register, control, t, reset, previousPage, handleSubmit, tenantId, data, onSubmit }} />;
  }
  //need to get from workflow
  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const handleExcelDownload = (e,tabData) => {
    e.preventDefault()
    console.log("handleExcelDownload",tabData)
    if (tabData?.[0] !== undefined) {
      return Digit.Download.Excel(tabData,"bulBill");
    }
  };
  const columns = useMemo(
    () => [
      {
        Header: t("WS_COMMON_TABLE_COL_CONSUMER_NO_LABEL"),
        disableSortBy: true,
        accessor: "connectionNo",
        Cell: ({ row }) => {
          return (
            <div>
              {row.original["connectionNo"] ? (
                <span className={"link"}>
                    {row.original["connectionNo"] || "NA"}
             
                </span>
              ) : (
                <span>{t("NA")}</span>
              )}
            </div>
          );
        },
      },
      {
        Header: t("BILLING_CYCLE"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["billingPeriod"]);
        },
      },
      {
        Header: t("LAST_READING"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["lastReading"]);
        },
        
      },
      {
        Header: t("METER_READING_DATE"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["lastReadingDate"]);
        },
        
      },
      {
        Header: t("METER_STATUS"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["meterStatus"]);
        },
        
      },
      {
        Header: t("CURRECT_READING"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["currentReading"]);
        },
        
      },
      {
        Header: t("CURRECT_READING_DATE"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["currentReadingDate"]);
        },
        
      },
      {
        Header: t("CONSUMPTION"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["consumption"]);
        },
        
      },
     
    ],
    []
  );
  const columns2 = useMemo(
    () => [
      {
        Header: t("WS_COMMON_TABLE_COL_CONSUMER_NO_LABEL"),
        disableSortBy: true,
        accessor: "connectionNo",
        Cell: ({ row }) => {
          return (
            <div>
              {row.original["connectionNo"] ? (
                <span className={"link"}>
                  <Link
                    to={`/digit-ui/employee/ws/connection-details?applicationNumber=${row.original["connectionNo"]}&tenantId=${tenantId}&service=${
                      row.original?.["service"]
                      }&connectionType=${row.original?.["connectionType"]}&due=${row.original?.due || 0}&from=WS_SEWERAGE_CONNECTION_SEARCH_LABEL`}
                  >
                    {row.original["connectionNo"] || "NA"}
                  </Link>
                </span>
              ) : (
                <span>{t("NA")}</span>
              )}
            </div>
          );
        },
      },
      {
        Header: t("WS_COMMON_TABLE_COL_applicationNo_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return (<div>{row.original["applicationNo"]}</div>);
        },
      },

      {
        Header: t("WS_COMMON_TABLE_COL_propertyId_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(t(`WS_${row.original?.["propertyId"]?.toUpperCase()}`));
        },
      },
  

      {
        Header: t("WS_COMMON_TABLE_COL_ACTION_LABEL"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(generateDemand1(row))
            //return (<div style={{cursor :"pointer", color :"#a82227"}}>{t(`${"WS_COMMON_COLLECT_DEMAND"}`)} </div>)
          } 
      },
    ],
    []
  );

  const generateDemand1 = (row) => {
    return (
        <div>
            <span className="link">
                <button onClick={() => { generateDemand(row) }}>
                    {t(`${"WS_COMMON_COLLECT_DEMAND"}`)}{" "}
                </button>
            </span>
        </div>
    )
};
  const generateDemand = async (row) => {
    const payload = {
      "BulkBillCriteria": {
        "tenantId": "pg.citya",
        "consumerCode": row.original["connectionNo"]
      }
    }
    let data = await Digit.WSService.WSSewsearchDemandGen(payload, window.location.href.includes("ws/sewerage/search-demand") ? "sw" : "ws")

    setShowToast({
      label: `${data}`
  })
    history.push(`/digit-ui/employee/payment/collect/SW/${encodeURIComponent(
      row.original?.["connectionNo"])}/${row.original?.["tenantId"]}?tenantId=${row.original?.["tenantId"]}?workflow=WS&ISWSCON`)

  }
  const getActionItem = (status, row) => {
    const userInfo = Digit.UserService.getUser();
    const userRoles = userInfo.info.roles.map((roleData) => roleData.code);
    const isUserAllowedToAddMeterReading = userRoles.filter(role => (role === "WS_CEMP" || role === "SW_CEMP")).length > 0
    if(!isUserAllowedToAddMeterReading) return null
    switch (status) {
      case "Active":
        return (
          <div>
            <span className="link">
              {row.original?.service === "WATER" ? (
                <Link
                  to={{
                    pathname: `/digit-ui/employee/payment/collect/${row.original?.["service"] === "WATER" ? "WS" : "SW"}/${encodeURIComponent(
                      row.original?.["connectionNo"]
                    )}/${row.original?.["tenantId"]}?tenantId=${row.original?.["tenantId"]}?workflow=WS&ISWSCON`,
                  }}
                >
                  {t(`${"WS_COMMON_COLLECT_LABEL"}`)}{" "}
                </Link>
              ) : (
                <Link
                  to={{
                    pathname: `/digit-ui/employee/payment/collect/${row.original?.["service"] === "WATER" ? "WS" : "SW"}/${encodeURIComponent(
                      row.original?.["connectionNo"]
                    )}/${row.original?.["tenantId"]}?tenantId=${row.original?.["tenantId"]}?workflow=SW&ISWSCON`,
                  }}
                >
                  {t(`${"WS_COMMON_COLLECT_LABEL"}`)}{" "}
                </Link>
              )}
            </span>
          </div>
        );
    }
  };

  return (
    <>
      <Header styles={{ fontSize: "32px" }}>
        {window.location.href.includes("water") ? t("WS_WATER_SEARCH_CONNECTION_SUB_HEADER") : t("WS_SEWERAGE_SEARCH_CONNECTION_SUB_HEADER")}
      </Header>
      {window.location.href.includes("search-demand")?"":<SearchForm className="ws-custom-wrapper" onSubmit={onSubmit} handleSubmit={handleSubmit}>
        <SearchFields {...{ register, control, reset, tenantId, t }} />
      </SearchForm>}
      { isLoading ? <Loader /> : null } 
      {data?.display && !resultOk ? (
        <Card style={{ marginTop: 20 }}>
          {t(data?.display)
            .split("\\n")
            .map((text, index) => (
              <p key={index} style={{ textAlign: "center" }}>
                {text}
              </p>
            ))}
        </Card>
        // <></>
      ) : true ? (
        <div style={{ backgroundColor: "white" }}>
        <div className="sideContent" style={{ float: "left", padding:"20px 10px", fontSize:"24px", fontWeight:"700", fontFamily:"Roboto"}}>
        {t("ABG_SEARCH_RESULTS_HEADER")}
        </div>
        <div className="sideContent" style={{ float: "right", padding: "10px 30px" }}>
            <span className="table-search-wrapper" style={{cursor:"pointer"}}>
                <DownloadBtn className="mrlg cursorPointer" onClick={(e) => handleExcelDownload(e,data)} />
            </span>
        </div>
            <div>
              <UploadFile
                extraStyleName={"BulkBill"}
                accept=".xlsx"
                onUpload={(e) => selectfile(e)}
                onDelete={(e) => {
                  setUploadedFile(null);
                }}

              />
            </div> 
        <Table
          t={t}
          data={data}
          totalRecords={count}
          columns={columns}
          getCellProps={(cellInfo) => {
            return {
              style: {
                minWidth: cellInfo.column.Header === t("ES_INBOX_APPLICATION_NO") ? "240px" : "",
                padding: "20px 18px",
                fontSize: "16px",
              },
            };
          }}
          onPageSizeChange={onPageSizeChange}
          currentPage={getValues("offset") / getValues("limit")}
          onNextPage={nextPage}
          onPrevPage={previousPage}
          pageSizeLimit={getValues("limit")}
          onSort={onSort}
          disableSort={false}
          sortParams={[{ id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false }]}
        />
        </div>
      ) : null}
      
      {window.location.href.includes("search-demand")? result?.length > 0 ? <Table
      t={t}
      data={result}
      totalRecords={count}
      columns={columns2}
      getCellProps={(cellInfo) => {
        return {
          style: {
            minWidth: cellInfo.column.Header === t("ES_INBOX_APPLICATION_NO") ? "240px" : "",
            padding: "20px 18px",
            fontSize: "16px",
          },
        };
      }}
      onPageSizeChange={onPageSizeChange}
      currentPage={getValues("offset") / getValues("limit")}
      onNextPage={nextPage}
      onPrevPage={previousPage}
      pageSizeLimit={getValues("limit")}
      onSort={onSort}
      disableSort={false}
      sortParams={[{ id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false }]}
    />:<Card style={{ marginTop: 20 }}>
          {
            <p  style={{ textAlign: "center" }}>
              No Data Found
            </p>
          }
  </Card>:""}
  {showToast?.label && (
        <Toast
          label={showToast?.label}
          onClose={(w) => {
            setShowToast((x) => null);
          }}
        />
      )}
    </>
  );
};

export default BulkBillSearch;