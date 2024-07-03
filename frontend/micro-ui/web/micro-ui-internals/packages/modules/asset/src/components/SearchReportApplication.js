/**
*   @author - Shivank-NIUA
*  Integrated the Download PDF and Download exel feature to download the Asset Report. 
*  You can customize the Number of table and data you want to show in PDF and Exel. 
*  Integrated QR code Report Download feture 
*/






import React, { useCallback, useMemo, useEffect } from "react"
import { useForm, Controller } from "react-hook-form";
import { TextInput, SubmitBar, DatePicker, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@nudmcdgnpm/digit-ui-react-components";
import jsPDF from 'jspdf';
import 'jspdf-autotable';
import * as XLSX from 'xlsx';
import QRCode from 'qrcode';



const ASSETReportApplication = ({isLoading, t, onSubmit, data, count, setShowToast }) => {
    
    
    const isMobile = window.Digit.Utils.browser.isMobile();

    const user = Digit.UserService.getUser();

   console.log("user",user);
   const { pincode, city } =  "";
   const allCities = Digit.Hooks.asset.useTenants();
   const cities = user?.info?.type  === "EMPLOYEE" ? allCities.filter((city) => city?.name=== "Mohali") : allCities;

  console.log("citiessss",cities);
 
    

    const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
        defaultValues: {
            offset: 0,
            limit: !isMobile && 10,
            sortBy: "commencementDate",
            sortOrder: "DESC",
            city: cities?.[0]?.name
        }
    })
    useEffect(() => {
      register("offset", 0)
      register("limit", 10)
      register("sortBy", "commencementDate")
      register("sortOrder", "DESC")
    },[register])

    const stateId = Digit.ULBService.getStateId();


    const { data: Menu_Asset } = Digit.Hooks.asset.useAssetClassification(stateId, "ASSET", "assetClassification"); 
    let menu_Asset = [];   //variable name for assetCalssification

    Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
        menu_Asset.push({ i18nKey: `ASSET_CLASS_${asset_mdms.code}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
    });
  
    
    const { data: Asset_Type } = Digit.Hooks.asset.useAssetType(stateId, "ASSET", "assetParentCategory"); 

    let asset_Type = [];

    Asset_Type &&
    Asset_Type.map((asset_type_mdms) => {
      asset_Type.push({ i18nKey: `${asset_type_mdms.name}`, code: `${asset_type_mdms.code}`, value: `${asset_type_mdms.name}` });
    });
   
    

    
    const GetCell = (value) => <span className="cell-text">{value}</span>;

    const columns = useMemo( () => ([
        
        
      
          {
            Header: t("ES_ASSET_RESPONSE_CREATE_LABEL"),
            Cell: ( row ) => {
              console.log("roeewwwinaplicationn",row);
              return GetCell(`${row?.row?.original?.["applicationNo"]}`)
            },
            disableSortBy: true,
          },
          {
            Header: t("AST_ASSET_CATEGORY_LABEL"),
            Cell: ( row ) => {
              return GetCell(`${row?.row?.original?.["assetClassification"]}`)
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
              return GetCell(`${row?.original?.["department"]}`)
            },
            disableSortBy: true,
          },

      ]), [] )

    const onSort = useCallback((args) => {
        if (args.length === 0) return
        setValue("sortBy", args.id)
        setValue("sortOrder", args.desc ? "DESC" : "ASC")
    }, [])

    function onPageSizeChange(e){
        setValue("limit",Number(e.target.value))
        handleSubmit(onSubmit)()
    }

    function nextPage () {
        setValue("offset", getValues("offset") + getValues("limit"))
        handleSubmit(onSubmit)()
    }
    function previousPage () {
        setValue("offset", getValues("offset") - getValues("limit") )
        handleSubmit(onSubmit)()
    }
    let validation={}


    //custom Functions to download the data which is coming in the Coloumns as a PDF and XLS 

    const downloadPDF = () => {
      const doc = new jsPDF("landscape");
      const tableColumn = ["S.No", ...columns.map(col => t(col.Header))];
      const tableRows = data.map((row, index) => [
          index + 1, // for the S.No it will increase the Number by +1
          row.applicationNo,
          row.assetClassification,
          row.assetParentCategory,
          row.assetName,
          row.addressDetails?.pincode,
          row.department
      ]);
      
      doc.autoTable({
          head: [tableColumn],
          body: tableRows,
      });

      doc.save("Asset-Reports.pdf");
  }

  const downloadXLS = () => {
    const tableColumn = columns.map(col => t(col.Header));
    const tableRows = data.map(row => [
        row.applicationNo,
        row.assetClassification,
        row.assetParentCategory,
        row.assetName,
        row.addressDetails?.pincode,
        row.department
    ]);
    const worksheet = XLSX.utils.aoa_to_sheet([tableColumn, ...tableRows]);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Asset Report");
    XLSX.writeFile(workbook, "Asset-Reports.xlsx");
    };





//   const downloadQRReport = async () => {
//     const doc = new jsPDF("landscape");
//     const generateQRCode = async (text) => {
//         return await QRCode.toDataURL(text);
//     };

//     const batchSize = 4; // Define batch size to process and added in a page so that QR code generation and PDF creation don't overwhelm the system's memory.
//     for (let i = 0; i < data.length; i += batchSize) {
//         const batch = data.slice(i, i + batchSize);
//         const qrPromises = batch.map(async (row, index) => {
//             const assetDetails = `
//             Application No: ${row.applicationNo}
//             Asset Classification: ${row.assetClassification}
//             Asset Parent Category: ${row.assetParentCategory}
//             Asset Name: ${row.assetName}
//             `;
//             const qrCodeURL = await generateQRCode(assetDetails.trim());
//             const yOffset = (index % batchSize) * 70;

//             doc.addImage(qrCodeURL, 'JPEG', 10, 20 + yOffset, 50, 50);
//             doc.text(`Application No: ${row.applicationNo}`, 70, 30 + yOffset);
//             doc.text(`Asset Classification: ${row.assetClassification}`, 70, 40 + yOffset);
//             doc.text(`Asset Parent Category: ${row.assetParentCategory}`, 70, 50 + yOffset);
//             doc.text(`Asset Name: ${row.assetName}`, 70, 60 + yOffset);
//         });

//         await Promise.all(qrPromises);

//         // Add a new page after processing each batch except for the last batch
//         if (i + batchSize < data.length) {
//             doc.addPage();
//         }
//     }

//     doc.save("Asset-QR-Reports.pdf");
// };


  // const downloadQRReport = async () => {
  //   const doc = new jsPDF();
  //   const generateQRCode = async (text) => {
  //       return await QRCode.toDataURL(text);
  //   };

  //   // Add a title for the entire page
  //   doc.setFontSize(16);
  //   doc.text("Asset QR Code Report", 70, 10);

  //   const batchSize = 3; // Define batch size to process and added in a page so that QR code generation and PDF creation don't overwhelm the system's memory.
  //   for (let i = 0; i < data.length; i += batchSize) {
  //       const batch = data.slice(i, i + batchSize);
  //       const qrPromises = batch.map(async (row, index) => {
  //           const assetDetails = `
  //           Application No: ${row.applicationNo}
  //           Asset Classification: ${row.assetClassification}
  //           Asset Parent Category: ${row.assetParentCategory}
  //           Asset Name: ${row.assetName}
  //           `;
  //           const qrCodeURL = await generateQRCode(assetDetails.trim());
  //           const yOffset = (index % batchSize) * 90;

  //           // Add QR code image
  //           doc.addImage(qrCodeURL, 'JPEG', 10, 20 + yOffset, 50, 50);

  //           // Add details below QR code
  //           doc.setFontSize(12);
  //           doc.text(`Application No: ${row.applicationNo}`, 70, 30 + yOffset);
  //           doc.text(`Asset Classification: ${row.assetClassification}`, 70, 40 + yOffset);
  //           doc.text(`Asset Parent Category: ${row.assetParentCategory}`, 70, 50 + yOffset);
  //           doc.text(`Asset Name: ${row.assetName}`, 70, 60 + yOffset);

  //           // Add horizontal line
  //           doc.line(10, 80 + yOffset, 200, 80 + yOffset);
  //       });

  //       await Promise.all(qrPromises);

  //       // Add a new page after processing each batch except for the last batch
  //       if (i + batchSize < data.length) {
  //           doc.addPage();
  //       }
  //   }

  //   doc.save("Asset-QR-Reports.pdf");
  // };


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
        const url = `https://niuatt.niua.in/digit-ui/employee/asset/assetservice/applicationsearch/application-details/${row.applicationNo}`;
        const assetDetails = `
        Application No: ${row.applicationNo}
        Asset Classification: ${row.assetClassification}
        Asset Parent Category: ${row.assetParentCategory}
        Asset Name: ${row.assetName}
        `;
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
  

    return <React.Fragment>
                
                <div>
                <Header>{t("AST_REPORTS_CHECK")}</Header>
                < Card className={"card-search-heading"}>
                    <span style={{color:"#505A5F"}}>{t("Provide at least one parameter to search for an application")}</span>
                </Card>
                <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                <SearchField>
                    <label>{t("MYCITY_CODE_LABEL")}</label>
                    <TextInput name="city" inputRef={register({})} />
                </SearchField>
                <SearchField>
                    <label>{t("AST_PINCODE")}</label>
                    <TextInput name="pincode" inputRef={register({})} />
                </SearchField>
                
                <SearchField>
                      <label>{t("AST_ASSET_CATEGORY_LABEL")}</label>
                      <Controller
                              control={control}
                              name="assetClassification"
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
                  <SearchField>
                      <label>{t("AST_PARENT_CATEGORY_LABEL")}</label>
                      <Controller
                              control={control}
                              name="assetParentCategory"
                              render={(props) => (
                                  <Dropdown
                                  selected={props.value}
                                  select={props.onChange}
                                  onBlur={props.onBlur}
                                  option={asset_Type}
                                  optionKey="i18nKey"
                                  t={t}
                                  disable={false}
                                  />
                              )}
                              />
                  </SearchField>
                <SearchField>
                    <label>{t("AST_FROM_DATE")}</label>
                    <Controller
                        render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
                        name="fromDate"
                        control={control}
                        />
                </SearchField>
                <SearchField>
                    <label>{t("AST_TO_DATE")}</label>
                    <Controller
                        render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
                        name="toDate"
                        control={control}
                        />
                </SearchField>
                <SearchField className="submit">
                    <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
                    <p style={{marginTop:"10px"}}
                    onClick={() => {
                        reset({ 
                            applicationNo: "", 
                            city: cities?.[0]?.name,
                            fromDate: "", 
                            toDate: "",
                            assetClassification: "",
                            assetParentCategory: "",
                            pincode: "",
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
           { data !== "" ? 
            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: "10px" }}>
            <button onClick={downloadPDF} style = {{ color: "maroon", border: "2px solid #333", padding: "8px 16px", cursor: "pointer",marginRight: "10px"}} >Download PDF</button>
            <button onClick={downloadXLS} style = {{ color: "maroon", border: "2px solid #333", padding: "10px 20px",cursor: "pointer"}}>Download XLS</button> 
            <button onClick={downloadQRReport} style = {{ color: "maroon", border: "2px solid #333", padding: "10px 20px",cursor: "pointer",marginLeft:"7px", marginRight: "10px"}}>Download QR Report</button> 

            </div>
            : "" }

            <br></br>
            
            {!isLoading && data?.display ? <Card style={{ marginTop: 20 }}>
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
            :(!isLoading && data !== ""? <Table
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
                currentPage={getValues("offset")/getValues("limit")}
                onNextPage={nextPage}
                onPrevPage={previousPage}
                pageSizeLimit={getValues("limit")}
                onSort={onSort}
                disableSort={false}
                sortParams={[{id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false}]}
            />: data !== "" || isLoading && <Loader/>)}
            </div>

            
        </React.Fragment>
}

export default ASSETReportApplication