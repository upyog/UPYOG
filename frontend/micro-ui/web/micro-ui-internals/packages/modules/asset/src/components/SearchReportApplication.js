/**
*   @author - Shivank-NIUA
*  Integrated the Download PDF and Download exel feature to download the Asset Report. 
*  You can customize the Number of table and data you want to show in PDF and Exel. 
*/






import React, { useCallback, useMemo, useEffect } from "react"
import { useForm, Controller } from "react-hook-form";
import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@upyog/digit-ui-react-components";
import { Link } from "react-router-dom";
import jsPDF from 'jspdf';
import 'jspdf-autotable';
import * as XLSX from 'xlsx';


const ASSETReportApplication = ({tenantId, isLoading, userType, t, onSubmit, data, count, setShowToast }) => {
    console.log("data",data);
    
    const isMobile = window.Digit.Utils.browser.isMobile();

    const user = Digit.UserService.getUser().info;


    
    

    const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
        defaultValues: {
            offset: 0,
            limit: !isMobile && 10,
            sortBy: "commencementDate",
            sortOrder: "DESC",
            city: user?.tenantId
        }
    })
    useEffect(() => {
      register("offset", 0)
      register("limit", 10)
      register("sortBy", "commencementDate")
      register("sortOrder", "DESC")
    },[register])

    const stateId = Digit.ULBService.getStateId();


    const { data: Menu_Asset } = Digit.Hooks.asset.useAssetClassification(stateId, "ASSET", "assetClassification"); // hook for asset classification Type
    let menu_Asset = [];   //variable name for assetCalssification

    Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
        menu_Asset.push({ i18nKey: `ASSET_CLASS_${asset_mdms.code}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
    });
  
    

    
   
    

    
    const GetCell = (value) => <span className="cell-text">{value}</span>;

    const columns = useMemo( () => ([
        
        
        {
            Header: t("ES_ASSET_RESPONSE_CREATE_LABEL"),
            accessor: "applicationNo",
            disableSortBy: true,
            Cell: ({ row }) => {
              return (
                <div>
                  <span className="link">
                    <Link to={`/digit-ui/employee/asset/assetservice/applicationsearch/application-details/${row.original?.["applicationNo"]}`}>
                      {row.original?.["applicationNo"]}
                    </Link>
                  </span>
                </div>
              );
            },
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
            Header: t("AST_PINCODE"),
            Cell: ({ row }) => {
              console.log("row in pincode",row);
                return GetCell(`${row?.original?.addressDetails?.["pincode"]}`)
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
                      <label>{t("AST_CATEGORY")}</label>
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
                            city: user?.tenantId,
                            fromDate: "", 
                            toDate: "",
                            assetClassification: "",
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