import React, { useCallback, useMemo, useEffect, useState, useRef } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  TextInput,
  SubmitBar,
  ActionBar,
  DatePicker,
  SearchForm,
  Dropdown,
  PopUp,
  SearchField,
  Table,
  Card,
  Loader,
  Header,
  Toast,
} from "@upyog/digit-ui-react-components";
import { useRouteMatch, Link, useHistory } from "react-router-dom";
import jsPDF from "jspdf";
import QRCode from "qrcode";
import * as XLSX from "xlsx";
import { useTranslation } from "react-i18next";

const DisposeForm = ({ onClose, onSubmit }) => {
  const { t } = useTranslation();
  const [formData, setFormData] = useState({
    disposalReason: "",
    amountReceived: 0.0,
    disposalDate: new Date().getTime(),
  });

  return (
    <div className="popup-module">
      <div
        style={{
          padding: "20px",
          backgroundColor: "white",
          borderRadius: "8px",
          boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
          maxWidth: "500px",
          width: "100%",
        }}
      >
        <h3
          style={{
            marginBottom: "20px",
            color: "#333",
            borderBottom: "2px solid #f0f0f0",
            paddingBottom: "10px",
          }}
        >
          {t("AST_DISPOSE_ASSET")}
        </h3>

        <div style={{ marginBottom: "15px" }}>
          <label
            style={{
              display: "block",
              marginBottom: "5px",
              fontWeight: "500",
              color: "#333",
            }}
          >
            {t("AST_REASON_DISPOSAL")} <span style={{ color: "red" }}>*</span>
          </label>
          <input
            type="text"
            value={formData.disposalReason}
            onChange={(e) => setFormData({ ...formData, disposalReason: e.target.value })}
            style={{
              width: "100%",
              padding: "10px",
              border: "1px solid #ccc",
              borderRadius: "4px",
              fontSize: "14px",
              boxSizing: "border-box",
            }}
            placeholder="Enter reason for disposal"
          />
        </div>

        <div style={{ marginBottom: "15px" }}>
          <label
            style={{
              display: "block",
              marginBottom: "5px",
              fontWeight: "500",
              color: "#333",
            }}
          >
            {t("AST_AMOUNT_RECEIVED_DISPOSAL")} <span style={{ color: "red" }}>*</span>
          </label>
          <input
            type="number"
            value={formData.amountReceived}
            onChange={(e) => setFormData({ ...formData, amountReceived: e.target.value })}
            style={{
              width: "100%",
              padding: "10px",
              border: "1px solid #ccc",
              borderRadius: "4px",
              fontSize: "14px",
              boxSizing: "border-box",
            }}
            placeholder="Enter minimum cost"
          />
        </div>

        <div
          style={{
            display: "flex",
            gap: "10px",
            justifyContent: "flex-end",
            borderTop: "1px solid #f0f0f0",
            paddingTop: "15px",
          }}
        >
          <button
            onClick={onClose}
            style={{
              padding: "10px 20px",
              border: "1px solid #ccc",
              borderRadius: "4px",
              backgroundColor: "#f8f9fa",
              color: "#333",
              cursor: "pointer",
              fontSize: "14px",
              fontWeight: "500",
            }}
          >
            {t("CANCEL")}
          </button>
          <button
            onClick={() => onSubmit(formData)}
            style={{
              padding: "10px 20px",
              border: "none",
              borderRadius: "4px",
              backgroundColor: "#007bff",
              color: "white",
              cursor: "pointer",
              fontSize: "14px",
              fontWeight: "500",
            }}
          >
            {t("DISPOSE")}
          </button>
        </div>
      </div>
    </div>
  );
};

const SearchAssetForUP = ({ isLoading, t, onSubmit, data, count, ActionBarStyle = {}, MenuStyle = {}, parentRoute, tenantId }) => {
  const isMobile = window.Digit.Utils.browser.isMobile();
  const [popup, setPopup] = useState(false);
  const [selectedAsset, setSelectedAsset] = useState(null);

  const [showToast, setShowToast] = useState(null);

  const todaydate = new Date();
  const today = todaydate.toISOString().split("T")[0];
  const history = useHistory();
  // Calculate the date 7 days ago
  const fromDate = new Date(todaydate);
  fromDate.setDate(todaydate.getDate() - 7); // Subtract 7 days from today

  // Convert fromDate to YYYY-MM-DD format
  const fromDateFormatted = fromDate.toISOString().split("T")[0];
  const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
    defaultValues: {
      offset: 0,
      limit: !isMobile && 10,
      sortBy: "commencementDate",
      sortOrder: "DESC",
      fromDate: fromDateFormatted,
      toDate: today,
    },
  });

  const handlePopupClose = () => {
    setPopup(false);
    setSelectedAsset(null);
  };

  const handleDispose = async (formData) => {
    try {
      formData.assetId = selectedAsset.id;
      formData.applicationNo = selectedAsset.applicationNo;

      const applicationDetails = await Digit.ASSETService.assetDisposedCreate({
        AssetDisposal: formData,
      });
      setShowToast({ label: t("ASSET_DISPOSED_SUCCESSFULLY") });
      setPopup(false);
      onSubmit();
    } catch (error) {
      console.log(error);
      setPopup(false);
      setShowToast({ error: t("CS_SOMETHING_WENT_WRONG") });
    }
  };

  useEffect(() => {
    register("offset", 0);
    register("limit", 10);
    register("sortBy", "commencementDate");
    register("sortOrder", "DESC");
    setValue("fromDate", fromDateFormatted);
    setValue("toDate", today);
  }, [register, setValue, today, fromDateFormatted]);

  // Get base path

  var base_url = window.location.origin;
  const { data: Menu_Asset } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "assetClassification" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["assetClassification"];
      return formattedData;
    },
  });

  let menu_Asset = [];
  Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
      menu_Asset.push({ i18nKey: `${asset_mdms.name}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
    });
  const statusOption = [
    { i18nKey: "ACTIVE", code: "ACTIVE", value: "ACTIVE" },
    { i18nKey: "DISPOSED", code: "DISPOSED", value: "DISPOSED" },
  ];
  const monetizeOption = [
    { i18nKey: "MONETIZE", code: "MONETIZE", value: "MONETIZE" },
    { i18nKey: "NON MONETIZE", code: "NON MONETIZE", value: "NON MONETIZE" },
  ];

  const { data: actionDetail } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "ActionOption" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["ActionOption"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });

  const printReport = async (applicationNo) => {
    const applicationDetails = await Digit.ASSETService.search({
      tenantId,
      filters: { applicationNo: applicationNo },
    });
    let response = await Digit.PaymentService.generatePdf(tenantId, { Assets: [applicationDetails?.Assets?.[0]] }, "asset-report");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  const handleDelete = async () => {
    console.log(selectedAsset);
  };

  const GetCell = (value) => <span className="cell-text">{value}</span>;

  const columns = useMemo(
    () => [
      {
        Header: t("ES_ASSET_RESPONSE_CREATE_LABEL"),
        Cell: (row) => {
          return GetCell(`${row?.row?.original?.["applicationNo"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("AST_DIVISION"),
        Cell: (row) => {
          return GetCell(`${row?.row?.original?.["division"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("AST_DISTRICT"),
        Cell: (row) => {
          return GetCell(`${row?.row?.original?.["district"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("AST_PARENT_CATEGORY_LABEL"),
        Cell: ({ row }) => {
          return GetCell(`${row?.original?.["assetParentCategory"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("AST_NAME_LABEL"),
        Cell: ({ row }) => {
          return GetCell(`${row?.original?.["assetName"]}`);
        },
        disableSortBy: true,
      },
      {
        Header: t("AST_APPLICATION_STATUS"),
        Cell: ({ row }) => {
          return GetCell(`${row?.original?.["status"]}`);
        },
        disableSortBy: true,
      },

      //later will convert it into the action bar same as i have iused in ApplicationDetailsActionBar.js file in template
      {
        Header: t("AST_ACTIONS"), // take action button
        Cell: ({ row }) => {
          const [isMenuOpen, setIsMenuOpen] = useState(false);
          const menuRef = useRef();

          const toggleMenu = () => {
            setIsMenuOpen(!isMenuOpen);
          };

          const closeMenu = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
              setIsMenuOpen(false);
            }
          };

          React.useEffect(() => {
            document.addEventListener("mousedown", closeMenu);
            return () => {
              document.removeEventListener("mousedown", closeMenu);
            };
          }, []);
          // const actionOptions = collectAction(row);

          return (
            <div ref={menuRef} style={{ display: "flex", gap: "10px" }}>
              <Link
                to={`/upyog-ui/employee/asset/assetservice/asset-edit/${row.original?.applicationNo}`}
                style={{
                  padding: "5px 10px",
                  textDecoration: "none",
                  color: "blue",
                  border: "1px solid blue",
                  borderRadius: "4px",
                }}
              >
                Edit
              </Link>
              <button
                onClick={() => {
                  setSelectedAsset(row.original);
                  setPopup(true);
                }}
                style={{
                  padding: "5px 10px",
                  backgroundColor: "red",
                  color: "white",
                  border: "none",
                  borderRadius: "4px",
                  cursor: "pointer",
                }}
              >
                Delete
              </button>
            </div>
          );
        },
        mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNo"]),
      },
    ],
    []
  );

  const onSort = useCallback(
    (args) => {
      if (args.length === 0) return;
      setValue("sortBy", args.id);
      setValue("sortOrder", args.desc ? "DESC" : "ASC");
    },
    [setValue]
  );

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

  const downloadQRReport = async () => {
    const doc = new jsPDF();
    const generateQRCode = async (text) => {
      return await QRCode.toDataURL(text);
    };

    // Add a title for the entire page
    doc.setFontSize(16);
    doc.text("Asset QR Code Report", 70, 10);

    const batchSize = 3; // Define batch size to process and add in a page so that QR code generation and PDF creation don't overwhelm the system's memory.
    for (let i = 0; i < data.length; i += batchSize) {
      const batch = data.slice(i, i + batchSize);
      const qrPromises = batch.map(async (row, index) => {
        // const url = `https://niuatt.niua.in/upyog-ui/employee/asset/assetservice/applicationsearch/application-details/${row.applicationNo}`;
        const url = `${base_url}/upyog-ui/citizen/assets/services?tenantId=${tenantId}&applicationNo=${row.applicationNo}`;
        const qrCodeURL = await generateQRCode(url);
        const yOffset = (index % batchSize) * 90;

        // Add QR code image
        doc.addImage(qrCodeURL, "JPEG", 10, 20 + yOffset, 50, 50);

        // Add details below QR code
        doc.setFontSize(12);
        doc.text(`Application No: ${row.applicationNo}`, 70, 30 + yOffset);
        doc.text(`Asset Classification: ${row.assetClassification}`, 70, 40 + yOffset);
        doc.text(`Asset Parent Category: ${row.assetParentCategory}`, 70, 50 + yOffset);
        doc.text(`Asset Name: ${row.assetName}`, 70, 60 + yOffset);
        doc.text(`Track: ${row.location}`, 70, 70 + yOffset);
        // doc.text(`Track Location: ${row.location}`, 70, 60 + yOffset);

        // Add horizontal line
        doc.line(10, 80 + yOffset, 200, 80 + yOffset);
      });

      await Promise.all(qrPromises);

      // Add a new page after processing each batch except for the last batch
      if (i + batchSize < data.length) {
        doc.addPage();
      }
    }

    doc.save("Asset-QR-Reports.pdf");
  };
  const downloadXLS = () => {
    const tableColumn = columns.map((col) => t(col.Header));
    const tableRows = data.map((row) => [row.applicationNo, row.assetClassification, row.assetParentCategory, row.assetName, row.department]);
    const worksheet = XLSX.utils.aoa_to_sheet([tableColumn, ...tableRows]);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Asset Report");
    XLSX.writeFile(workbook, "Asset-Reports.xlsx");
  };
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 1500); // Close toast after 1.5 seconds

      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);

  return (
    <React.Fragment>
      <div>
        {popup && (
          <PopUp>
            <DisposeForm onClose={handlePopupClose} onSubmit={handleDispose} />
          </PopUp>
        )}
        {showToast && (
          <Toast
            error={showToast.error}
            warning={showToast.warning}
            label={t(showToast.label)}
            isDleteBtn={true}
            onClose={() => {
              setShowToast(null);
            }}
          />
        )}
        <Header>{t("ASSET_APPLICATIONS")}</Header>
        <Card className={"card-search-heading"}>
          <span style={{ color: "#505A5F" }}>{t("Provide at ddd least one parameter to search for an application")}</span>
        </Card>
        <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
          <SearchField>
            <label>{t("AST_STATUS")}</label>
            <Controller
              control={control}
              name="status"
              render={(props) => (
                <Dropdown
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  option={statusOption}
                  optionKey="i18nKey"
                  t={t}
                  disable={false}
                />
              )}
            />
          </SearchField>
          <SearchField>
            <label>{t("MONETIZE_STATUS")}</label>
            <Controller
              control={control}
              name="status"
              render={(props) => (
                <Dropdown
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  option={monetizeOption}
                  optionKey="i18nKey"
                  t={t}
                  disable={false}
                />
              )}
            />
          </SearchField>

          <SearchField>
            <label>{t("AST_PARENT_CATEGORY_LV1")}</label>
            <Controller
              control={control}
              name="status"
              render={(props) => (
                <Dropdown
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  option={menu_Asset}
                  optionKey="i18nKey"
                  t={t}
                  disable={false}
                />
              )}
            />
          </SearchField>
          <SearchField className="submit">
            <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
            <p
              style={{ marginTop: "10px" }}
              onClick={() => {
                reset({
                  applicationNo: "",
                  fromDate: fromDateFormatted,
                  toDate: today,
                  status: "",
                  offset: 0,
                  limit: 10,
                  sortBy: "commencementDate",
                  sortOrder: "DESC",
                });
                setShowToast(null);
                previousPage();
              }}
            >
              {t(`ES_COMMON_CLEAR_ALL`)}
            </p>
          </SearchField>
        </SearchForm>

        <br></br>
        {data !== "" ? (
          <div style={{ display: "flex", justifyContent: "flex-end", marginTop: "10px" }}>
            {/* <button onClick={downloadXLS} style = {{ color: "maroon", border: "2px solid #333", padding: "10px 20px",cursor: "pointer"}}>Download XLS</button>  */}
            <button
              onClick={downloadQRReport}
              style={{ color: "maroon", border: "2px solid #333", padding: "10px 20px", cursor: "pointer", marginLeft: "15px" }}
            >
              Download QR Report
            </button>
          </div>
        ) : (
          ""
        )}

        <br></br>
        {!isLoading && data?.display ? (
          <Card style={{ marginTop: 20 }}>
            {t(data.display)
              .split("\\n")
              .map((text, index) => (
                <p key={index} style={{ textAlign: "center" }}>
                  {text}
                </p>
              ))}
          </Card>
        ) : !isLoading && data !== "" ? (
          <Table
            t={t}
            data={data}
            totalRecords={count}
            columns={columns}
            getCellProps={(cellInfo) => {
              return {
                style: {
                  minWidth: cellInfo.column.Header === t("ASSET_INBOX_APPLICATION_NO") ? "240px" : "",
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
        ) : (
          data !== "" || (isLoading && <Loader />)
        )}
      </div>
    </React.Fragment>
  );
};

export default SearchAssetForUP;
