import React, { useCallback, useMemo, useEffect, useState, useRef } from "react"
import { useForm, Controller } from "react-hook-form";
import { TextInput, SubmitBar, ActionBar, DatePicker, SearchForm, Dropdown, SearchField, Table, Card, Loader, Header,Toast } from "@upyog/digit-ui-react-components";
import { useRouteMatch, Link, useHistory } from "react-router-dom";
import jsPDF from 'jspdf';
import QRCode from 'qrcode';
import * as XLSX from 'xlsx';


const ASSETSearchApplication = ({ isLoading, t, onSubmit, data, count, setShowToast, ActionBarStyle = {}, MenuStyle = {}, parentRoute, tenantId }) => {
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
  const { data: actionState } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "Action" }],
    {
      select: (data) => {
        const formattedData = data?.["ASSET"]?.["Action"]
        return formattedData;
      },
    });
  let action = [];

  actionState && actionState.map((actionstate) => {
    action.push({ i18nKey: `${actionstate.name}`, code: `${actionstate.code}`, value: `${actionstate.name}` })
  })


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
      filters: { applicationNo: applicationNo }
    });
    let response = await Digit.PaymentService.generatePdf(tenantId, { Assets: [applicationDetails?.Assets?.[0]] }, "asset-report");
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
    window.open(fileStore[response?.filestoreIds[0]], "_blank");
  };

  const collectAction = (row) => {
    const actionMdms = [];

    actionDetail &&
      actionDetail.map((opt) => {

        // this condition use for if asset asign then show return asset if asset not assign then show asset Assign
        if (
          (row?.original?.assetAssignment?.isAssigned && opt.code === "AST_RETURN") ||
          (!row?.original?.assetAssignment?.isAssigned && opt.code === "AST_ASSIGN")
        ) {
          actionMdms.push({
            label: t(opt.code),
            link: `${opt.url}${row?.original?.["applicationNo"]}`,
            code: opt.code
          });
        } else if ( opt.code !== "AST_RETURN" && opt.code !== "AST_ASSIGN") {
          // Push other options unconditionally
          actionMdms.push({
            label: t(opt.code),
            link: `${opt.url}${row?.original?.["applicationNo"]}`,
            code: opt.code
          });
        }
      });

    return actionMdms;
  };

  const processDepreciation = async (applicationNo, assetId) => {
    try {
      const applicationDetails = await Digit.ASSETService.depriciationProcess({
        Asset: {
          tenantId,
          id: assetId,
          accountId: ""
        }
      });
      if(applicationDetails)
      history.replace("/upyog-ui/employee/asset/assetservice/asset-process-depreciation-response", { ProcessDepreciation: applicationDetails,  applicationNo});
    } catch (error) {
      setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
    }
  };

  const GetCell = (value) => <span className="cell-text">{value}</span>;

  const columns = useMemo(() => ([
    {
      Header: t("ES_ASSET_RESPONSE_CREATE_LABEL"),
      accessor: "applicationNo",
      disableSortBy: true,
      Cell: ({ row }) => {
        return (
          <div>
            <span className="link">
              <Link to={`/upyog-ui/employee/asset/assetservice/applicationsearch/application-details/${row.original?.["applicationNo"]}`}>
                {row.original?.["applicationNo"]}
              </Link>
            </span>
          </div>
        );
      },
    },


    {
      Header: t("AST_BOOK_REF_SERIAL_NUM"),
      Cell: (row) => {
        return GetCell(`${row?.row?.original?.["assetBookRefNo"]}`)
      },
      disableSortBy: true,
    },
    {
      Header: t("AST_PARENT_CATEGORY_LABEL"),
      Cell: ({ row }) => {
        return GetCell(`${row?.original?.["assetParentCategory"]}`)
      },
      disableSortBy: true,

    },
    {
      Header: t("AST_NAME_LABEL"),
      Cell: ({ row }) => {
        return GetCell(`${row?.original?.["assetName"]}`)
      },
      disableSortBy: true,
    },
    {
      Header: t("AST_DEPARTMENT_LABEL"),
      Cell: ({ row }) => {
        return GetCell(`${t('COMMON_MASTERS_DEPARTMENT_'+row?.original?.["department"])}`)
      },
      disableSortBy: true,
    },
    {
      Header: t("AST_APPLICATION_STATUS"),
      Cell: ({ row }) => {
        return GetCell(`${row?.original?.["status"]}`)
      },
      disableSortBy: true,
    },
   
 //later will convert it into the action bar same as i have iused in ApplicationDetailsActionBar.js file in template
    {
      Header: t("AST_ACTIONS"),// take action button
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
        const actionOptions = collectAction(row);
     
        // const actionOptions = [
        //   {
        //     label: row?.original?.assetAssignment?.isAssigned ? t("AST_RETURN") : t("AST_ASSIGN"),
        //     link: row?.original?.assetAssignment?.isAssigned
        //       ? `/upyog-ui/employee/asset/assetservice/return-assets/${row?.original?.["applicationNo"]}`
        //       : `/upyog-ui/employee/asset/assetservice/assign-assets/${row?.original?.["applicationNo"]}`,
        //   },
        //   {
        //     label: t("AST_DISPOSE"),
        //     link: `/upyog-ui/employee/asset/assetservice/dispose-assets/${row?.original?.["applicationNo"]}`,
        //   },
        //   {
        //     label: t("AST_DEPRECIATION"),
        //     link: `/upyog-ui/employee/asset/assetservice/depreciate-assets/${row?.original?.["applicationNo"]}`,
        //   },
        // ];

        return (
          <div ref={menuRef}>
            {row?.original?.status === "APPROVED" ? (
              <React.Fragment>
                <SubmitBar label={t("WF_TAKE_ACTION")} onSubmit={toggleMenu} />
                {isMenuOpen && (
                  <div style={{
                    position: 'absolute',
                    backgroundColor: 'white',
                    border: '1px solid #ccc',
                    borderRadius: '4px',
                    padding: '8px',
                    zIndex: 1000,
                  }}>
                    {actionOptions
                  .filter(option => {
                    // Only show AST_CERTIFICATE if assetStatus is "0"
                    if (row?.original?.assetStatus === "0") {
                      return option.code === "AST_CERTIFICATE";
                    }
                    return true; // Show all options otherwise
                  }) .map((option, index) => (
                      option.code === "AST_CERTIFICATE" ? (
                        <div
                          key={index}  // Ensure each element has a unique key
                          onClick={() => printReport(row.original?.["applicationNo"])}  // Wrap printReport in an arrow function
                          style={{
                            display: 'block',
                            padding: '8px',
                            textDecoration: 'none',
                            color: 'black',
                            cursor: 'pointer',
                          }}
                        >
                          {option.label}
                        </div>
                      ) : (option.code === "AST_DEPRECIATION" ) ?  
                        (<div
                          key={index}  // Ensure each element has a unique key
                          onClick={() => processDepreciation(row.original?.["applicationNo"], row.original?.["id"])}  // Wrap printReport in an arrow function
                          style={{
                            display: 'block',
                            padding: '8px',
                            textDecoration: 'none',
                            color: 'black',
                            cursor: 'pointer',
                          }}
                        >
                          {option.label}
                        </div>)
                        : (
                          <Link
                            key={index}  // Add key for the Link element as well
                            to={option.link}
                            style={{
                              display: 'block',
                              padding: '8px',
                              textDecoration: 'none',
                              color: 'black',
                            }}
                          >
                            {option.label}
                          </Link>
                        )
                    ))}
                  </div>
                )}
              </React.Fragment>
            ) : (
              t("AST_SHOULD_BE_APPROVED_FIRST")
            )}
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
      console.log('comming data:- ', batch);
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
        // doc.text(`Track Location: ${row.location}`, 70, 60 + yOffset);


        //  Add Track Location as a clickable hyperlink
        // const locationURL = `${row.location}`;
        // doc.text("Track:", 70, 70 + yOffset); // Add label
        // doc.textWithLink(locationURL, 70, 75 + yOffset, { url: locationURL }); // Add clickable URL

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
      <Header>{t("ASSET_APPLICATIONS")}</Header>
      < Card className={"card-search-heading"}>
        <span style={{ color: "#505A5F" }}>{t("Provide at least one parameter to search for an application")}</span>
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
                option={action}
                optionKey="i18nKey"
                t={t}
                disable={false}
              />
            )}
          />
        </SearchField>
        <SearchField>
          <label>{t("AST_APPLICATION_ID")}</label>
          <TextInput name="applicationNo" inputRef={register({})} />
        </SearchField>

        <SearchField>
          <label>{t("AST_FROM_DATE")}</label>
          <Controller
            render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} max={today} />}
            name="fromDate"
            control={control}
          />
        </SearchField>
        <SearchField>
          <label>{t("AST_TO_DATE")}</label>
          <Controller
            render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} max={today} />}
            name="toDate"
            control={control}
          />
        </SearchField>
        <SearchField className="submit">
          <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
          <p style={{ marginTop: "10px" }}
            onClick={() => {
              reset({
                applicationNo: "",
                fromDate: fromDateFormatted,
                toDate: today,
                status: "",
                offset: 0,
                limit: 10,
                sortBy: "commencementDate",
                sortOrder: "DESC"
              });
              setShowToast(null);
              previousPage();
            }}>{t(`ES_COMMON_CLEAR_ALL`)}</p>
        </SearchField>
      </SearchForm>

      <br></br>
      {data !== "" ?
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: "10px" }}>
          {/* <button onClick={downloadXLS} style = {{ color: "maroon", border: "2px solid #333", padding: "10px 20px",cursor: "pointer"}}>Download XLS</button>  */}
          <button onClick={downloadQRReport} style={{ color: "maroon", border: "2px solid #333", padding: "10px 20px", cursor: "pointer", marginLeft: "15px" }}>Download QR Report</button>

        </div>
        : ""}

      <br></br>
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

export default ASSETSearchApplication