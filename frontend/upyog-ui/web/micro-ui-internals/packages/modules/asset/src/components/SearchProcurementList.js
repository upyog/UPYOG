import React, { useCallback, useMemo, useEffect, useState, useRef } from "react"
import { useForm, Controller } from "react-hook-form";
import { TextInput, SubmitBar, ActionBar, DatePicker, SearchForm, Dropdown, SearchField, Table, Card, Loader, Header,Toast } from "@upyog/digit-ui-react-components";
import { useRouteMatch, Link, useHistory } from "react-router-dom";
import jsPDF from 'jspdf';
import QRCode from 'qrcode';
import * as XLSX from 'xlsx';


const SearchProcurementList = ({ isLoading, t, onSubmit, data, count, setShowToast, ActionBarStyle = {}, MenuStyle = {}, parentRoute, tenantId }) => {
  const isMobile = window.Digit.Utils.browser.isMobile();
  const todaydate = new Date();
  const today = todaydate.toISOString().split("T")[0];
  const history = useHistory();
  // Calculate the date 7 days ago
  const fromDate = new Date(todaydate);
  fromDate.setDate(todaydate.getDate() - 7);  // Subtract 7 days from today

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
    }
  })

  useEffect(() => {
    register("offset", 0)
    register("limit", 10)
    register("sortBy", "commencementDate")
    register("sortOrder", "DESC")
    setValue("fromDate", fromDateFormatted);
    setValue("toDate", today);
  }, [register, setValue, today, fromDateFormatted])

  // Get base path

  var base_url = window.location.origin;
  const printReport = async (applicationNo) => {

    const applicationDetails = await Digit.ASSETService.search({
      tenantId,
      filters: { applicationNo: applicationNo }
    });
    let response = await Digit.PaymentService.generatePdf(tenantId, { Assets: [applicationDetails?.Assets?.[0]] }, "asset-report");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };



  const handleDelete = async (asset) => {
    if (window.confirm(t("ARE_YOU_SURE_DELETE"))) {
      try {
        // Add your delete API call here
        setShowToast({ success: true, label: t("ASSET_DELETED_SUCCESSFULLY") });
      } catch (error) {
        setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
      }
    }
  };

  const GetCell = (value) => <span className="cell-text">{value}</span>;

  const columns = useMemo(() => ([
    {
      Header: t("PRO_REQUEST_ID"),
      Cell: (row) => {
        return GetCell(`${row?.row?.original?.["requestId"]}`)
      },
      disableSortBy: true,
    },
    {
      Header: t("PRO_ITEM_NAME"),
      Cell: (row) => {
        return GetCell(`${row?.row?.original?.["item"]}`)
      },
      disableSortBy: true,
    },
    {
      Header: t("PRO_ITEM_TYPE"),
      Cell: (row) => {
        return GetCell(`${row?.row?.original?.["itemType"]}`)
      },
      disableSortBy: true,
    },
    {
      Header: t("PRO_QUANTITY"),
      Cell: ({ row }) => {
        return GetCell(`${row?.original?.["quantity"]}`)
      },
      disableSortBy: true,

    },
    {
      Header: t("PRO_STATUS"),
      Cell: ({ row }) => {
        return GetCell(`${row?.original?.["status"]}`)
      },
      disableSortBy: true,
    },
   
 //later will convert it into the action bar same as i have iused in ApplicationDetailsActionBar.js file in template
    {
      Header: t("PRO_ACTIONS"),// take action button
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
          <div ref={menuRef} style={{ display: 'flex', gap: '10px' }}>
            <Link
              to={`/upyog-ui/employee/asset/assetservice/procurement-edit/${row.original?.requestId}`}
              style={{
                padding: '5px 10px',
                textDecoration: 'none',
                color: 'blue',
                border: '1px solid blue',
                borderRadius: '4px'
              }}
            >
              Edit
            </Link>
            <button
              onClick={() => handleDelete(row.original)}
              style={{
                padding: '5px 10px',
                backgroundColor: 'red',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              Delete
            </button>
          </div>
        );
      },
      mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNo"]),
    },
  ]), [])

  const onSort = useCallback((args) => {
    if (args.length === 0) return
    setValue("sortBy", args.id)
    setValue("sortOrder", args.desc ? "DESC" : "ASC")
  }, [setValue])

  function onPageSizeChange(e) {
    setValue("limit", Number(e.target.value))
    handleSubmit(onSubmit)()
  }

  function nextPage() {
    setValue("offset", getValues("offset") + getValues("limit"))
    handleSubmit(onSubmit)()
  }
  function previousPage() {
    setValue("offset", getValues("offset") - getValues("limit"))
    handleSubmit(onSubmit)()
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
        doc.addImage(qrCodeURL, 'JPEG', 10, 20 + yOffset, 50, 50);

        // Add details below QR code
        doc.setFontSize(12);
        doc.text(`Application No: ${row.applicationNo}`, 70, 30 + yOffset);
        doc.text(`Asset Classification: ${row.assetClassification}`, 70, 40 + yOffset);
        doc.text(`Asset Parent Category: ${row.assetParentCategory}`, 70, 50 + yOffset);
        doc.text(`Asset Name: ${row.assetName}`, 70, 60 + yOffset);
        doc.text(`Track: ${row.location}`, 70, 70 + yOffset);
      

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
    const tableColumn = columns.map(col => t(col.Header));
    const tableRows = data.map(row => [
      row.applicationNo,
      row.assetClassification,
      row.assetParentCategory,
      row.assetName,
      row.department
    ]);
    const worksheet = XLSX.utils.aoa_to_sheet([tableColumn, ...tableRows]);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Asset Report");
    XLSX.writeFile(workbook, "Asset-Reports.xlsx");
  };


  return <React.Fragment>

    <div>
      <Header>{t("PROCUREMENT_LIST")}</Header>
  
      {!isLoading && data?.display ?
        <Card style={{ marginTop: 20 }}>
          {
            t(data.display)
              .split("\\n")
              .map((text, index) => (
                <p key={index} style={{ textAlign: "center" }}>
                  {text}
                </p>
              ))
          }
        </Card>
        :
        (!isLoading && data !== "" ?
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
                  fontSize: "16px"
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
          /> : data !== "" || isLoading && <Loader />)}
    </div>
  </React.Fragment>
}

export default SearchProcurementList