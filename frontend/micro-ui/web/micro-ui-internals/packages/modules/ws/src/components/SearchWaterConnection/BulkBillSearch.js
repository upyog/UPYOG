import React, { Fragment, useState, useEffect, useCallback, useMemo } from "react";
import { SearchForm, Table, Card, CardText, Loader, Header, Toast, DownloadBtnCommon, UploadFile, SubmitBar, Modal} from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import BulkBillSearchFields from "./BulkBillSearchFields";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import MobileSearchWater from "./MobileSearchWater";
import { useHistory } from "react-router-dom";
import { convertDateToEpoch } from "../../utils/index"
import * as XLSX from "xlsx";
const BulkBillSearch = ({ tenantId, onSubmit, data, count, resultOk, businessService, isLoading }) => {
  const [showToast, setShowToast] = useState(null);
  const [showModal, setModalReject] = useState(null);
  const [showModalResult, setShowModalResult] = useState(null)
  const [uploadedFile, setUploadedFile] = useState(() => null);
  const [file, setFile] = useState("")
  const [meterReadingData, setMeterReadingData] = useState([])
  const [rejectedReading, setRejectedReading] = useState([])
  const [bulkReadingStatus, setBulkReadingStatus] = useState([])
  const [isLoadingBulkMeterReading, setIsLoadingBulkMeterReading] =useState(false)
  function selectfile(e) {
    e.preventDefault()
    setFile(e.target.files[0]);
    readExcel(e.target.files[0]);
    setUploadedFile("bulk");

  }
  const {
    isLoading: updatingMeterConnectionLoading,
    isError: updateMeterConnectionError,
    data: updateMeterConnectionResponse,
    error: updateMeterError,
    mutate: meterReadingMutation,
  } = Digit.Hooks.ws.useBulkMeterReadingCreateAPI(businessService);

  const readExcel = async (file) => {
    const promise = new Promise((resolve, reject) => {
      const fileReader = new FileReader();
      fileReader.readAsArrayBuffer(file);

      fileReader.onload = (e) => {
        const bufferArray = e.target.result;

        const wb = XLSX.read(bufferArray, { type: "buffer" });

        const wsname = wb.SheetNames[0];

        const ws = wb.Sheets[wsname];

        const data = XLSX.utils.sheet_to_json(ws);

        const meterReadingListFilter = data.map((meter) => {

          return { "billingPeriod": meter.billingPeriod, "currentReading": meter.currentReading, "currentReadingDate": meter.currentReadingDate, "lastReading": meter.lastReading, "lastReadingDate": meter.lastReadingDate, "connectionNo": meter.connectionNo, "meterStatus": meter.meterStatus, tenantId: "pg.citya" }
        })
        const meterReadingList = meterReadingListFilter.filter((item) => {
          return item.currentReading >= item.lastReading && ExcelDateToJSDate(item?.currentReadingDate) > item.lastReadingDate
        })
        const rejectedReading =
          meterReadingListFilter.filter((element) => !meterReadingList.includes(element));
        console.log("reading list", meterReadingList, rejectedReading)
        setRejectedReading(rejectedReading)
        resolve(meterReadingList, rejectedReading);
      };

      fileReader.onerror = (error) => {
        reject(error);
      };
    });

    promise.then(async (meterReading, rejectedReading) => {
      setMeterReadingData(meterReading)

    });
  };

  const ExcelDateToJSDate = (date) => {
    const e0date = new Date(0); // epoch "zero" date
    const offset = e0date.getTimezoneOffset(); // tz offset in min

    // calculate Excel xxx days later, with local tz offset
    const jsdate = new Date(0, 0, date - 1, 0, -offset, 0);
    return Digit.Utils.pt.convertDateToEpoch(jsdate?.toJSON()?.split("T")[0])
  }
  const handleBulkSubmit = async () => {
    if (meterReadingMutation) {
      setIsLoadingBulkMeterReading(true)
      let meterReadingsPayload = { meterReadingList: meterReadingData };
      await meterReadingMutation(meterReadingsPayload, {
        onError: (error, variables) => {
          setIsLoadingBulkMeterReading(false)
          setShowToast({ error: true, label: error?.message ? error.message : error });
          setTimeout(closeToast, 5000);
        },
        onSuccess: async (data, variables) => {
          setIsLoadingBulkMeterReading(false)
          console.log("data",data)
          setShowModalResult(true)
          setBulkReadingStatus(data.meterReadings)
          setShowToast({ key: "success", label: "WS_METER_READING_ADDED_SUCCESFULLY" });
          setTimeout(closeToast, 3000);
          // setTimeout(() => {
          //   window.location.reload();
          // }, 5000);
        },
      });
    }
  }
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
  const closeModal = () => {
    setModalReject(false)
  }
  const closeModalStatus = () => {
    setShowModalResult(false)
  }
  const setModal = () => {
    setModalReject(false)
    handleBulkSubmit()
  }
  const Heading = (props) => {
    return <h1 className="heading-m">{props.label}</h1>;
  };

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
  const { t } = useTranslation();
  const { register, control, handleSubmit, setValue, getValues, reset } = useForm({
    defaultValues: {
      offset: 0,
      limit: 10,
      sortBy: "consumerNo",
      sortOrder: "DESC",
      searchType: "CONNECTION",
      locality: "",
      tenantId: ""
    },
  });
  const DownloadBtn = (props) => {
    return (
      <div onClick={props.onClick}>
        <DownloadBtnCommon />
      </div>
    );
  };
  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    register("sortBy", "commencementDate");
    register("searchType", "CONNECTION");
    register("sortOrder", "DESC");
    register("locality", "");
    register("tenantId", "");
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
  const closeToast = () => {
    setShowToast(null);
  };
  const isMobile = window.Digit.Utils.browser.isMobile();
  const handleProceed = () => {
    if (rejectedReading?.length > 0) {
      setModalReject(true)
    }
  }
  if (isMobile) {
    return <MobileSearchWater {...{ Controller, register, control, t, reset, previousPage, handleSubmit, tenantId, data, onSubmit, setValue }} />;
  }
  //need to get from workflow
  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const handleExcelDownload = (e, tabData) => {
    e.preventDefault()
    if (tabData?.[0] !== undefined) {
      return Digit.Download.Excel(tabData, "Bulk-Bill");
    }
  };
  const columns = useMemo(
    () => [

      {
        Header: t("BILLING_CYCLE"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["billingPeriod"]);
        },
      },
      {
        Header: t("WS_COMMON_TABLE_COL_CONSUMER_NO_LABEL"),
        disableSortBy: true,
        accessor: "connectionNo",
        Cell: ({ row }) => {
          return (
            <div>
              {row.original["connectionNo"] ? (
                <span>
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
          return GetCell(convertEpochToDate(row.original?.["lastReadingDate"]));
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
          return GetCell(convertEpochToDate(row.original?.["currentReadingDate"]));
        },

      }

    ],
  );
  const columns2 = useMemo(
    () => [

      {
        Header: t("BILLING_CYCLE"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["billingPeriod"]);
        },
      },
      {
        Header: t("WS_COMMON_TABLE_COL_CONSUMER_NO_LABEL"),
        disableSortBy: true,
        accessor: "connectionNo",
        Cell: ({ row }) => {
          return (
            <div>
              {row.original["connectionNo"] ? (
                <span>
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
          return GetCell(convertEpochToDate(row.original?.["lastReadingDate"]));
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
          return GetCell(convertEpochToDate(row.original?.["currentReadingDate"]));
        },

      },
      {
        Header: t("CURRECT_STATUS"),
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(row.original?.["status"]);
        },

      }

    ],
  );
  return (
    <>
      <Header styles={{ fontSize: "32px" }}>
        {t("WS_WATER_SEARCH_BULK_CONNECTION_SUB_HEADER")}
      </Header>
      <SearchForm className="ws-custom-wrapper" onSubmit={onSubmit} handleSubmit={handleSubmit}>
        <BulkBillSearchFields {...{ register, control, reset, tenantId, t, setValue }} />
      </SearchForm>
      {isLoading ? <Loader /> : null}
      {isLoadingBulkMeterReading && <Loader/>}
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
      ) : resultOk ? (
        <div style={{ backgroundColor: "white" }}>

          <div className="sideContent" style={{ float: "right", padding: "10px 30px" }}>
            <span className="table-search-wrapper" style={{ cursor: "pointer" }}>
              <DownloadBtn className="mrlg cursorPointer" onClick={(e) => handleExcelDownload(e, data)} />
            </span>
          </div>
          <div style={{ display: "flex" }}>
            <div style={{ width: "70%" }}>
              <UploadFile
                id={"Bulk-Bill"}
                extraStyleName={"propertyCreate"}
                message={uploadedFile ? `1 ${t(`CS_WS_ACTION_FILEUPLOADED`)}` : t(`CS_WS_ACTION_NO_FILEUPLOADED`)}
                accept=".xlsx"
                onUpload={(e) => selectfile(e)}
                onDelete={(e) => {
                  setUploadedFile(null);
                  setMeterReadingData([])
                }}
              />
            </div>
            {meterReadingData?.length > 0 ? <div style={{ top: "5px", left: "10px", position: "relative" }}>
              <span>
                <SubmitBar label={t("WS_COMMON_SUBMIT_READING")} onSubmit={handleProceed} />
              </span>
            </div> : ""}
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
            onSort={onSort}
            disableSort={false}
            sortParams={[{ id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false }]}
          />

        </div>
      ) : null}
        
      <div>
        {showModal && <Modal
          headerBarMain={<Heading label={t("WS_BULK_READING_REJECT")} />}
          headerBarEnd={<CloseBtn onClick={closeModal} />}
          actionCancelLabel={"Cancel"}
          actionCancelOnSubmit={closeModal}
          actionSaveLabel={"Proceed"}
          actionSaveOnSubmit={setModal}
          formId="modal-action"
          popupStyles={{ width: "auto" }}
        >  <div style={{ width: "100%" }}>
            <Card>
            <div><CardText>{t("WS_REASON_FOR_REJECT")}</CardText></div>
            <div><CardText>{t("WS_SUCCESS_DATA_COUNT")} - {meterReadingData?.length}</CardText></div>
            <div><CardText>{t("WS_REJECT_DATA_COUNT")} - {rejectedReading?.length}</CardText></div>
              <Table
                t={t}
                data={rejectedReading}
                totalRecords={rejectedReading?.length}
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
                disableSort={true}
                sortParams={[{ id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false }]}
              />
              

            </Card>
          </div>
        </Modal>}
        {showModalResult && <Modal
          headerBarMain={<Heading label={t("WS_BULK_READING_STATUS")} />}
          headerBarEnd={<CloseBtn onClick={closeModalStatus} />}
          formId="modal-action"
          popupStyles={{ width: "auto" }}
        >  <div style={{ width: "100%" }}>

            <Card>
            <div><CardText>{t("Bulk Meter Reading Status")}</CardText></div>
            <div className="sideContent" style={{ float: "right", padding: "10px 30px" }}>
            <span className="table-search-wrapper" style={{ cursor: "pointer" }}>
              <DownloadBtn className="mrlg cursorPointer" onClick={(e) => handleExcelDownload(e, bulkReadingStatus)} />
            </span>
          </div>
              <Table
                t={t}
                data={bulkReadingStatus}
                totalRecords={bulkReadingStatus?.length}
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
                disableSort={true}
                sortParams={[{ id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false }]}
              />
              

            </Card>
          </div>
        </Modal>}
      </div>
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