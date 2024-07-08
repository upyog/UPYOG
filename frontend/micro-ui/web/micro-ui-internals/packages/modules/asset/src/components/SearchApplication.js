  import React, { useCallback, useMemo, useEffect } from "react"
  import { useForm, Controller } from "react-hook-form";
  import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@nudmcdgnpm/digit-ui-react-components";
  import { Link } from "react-router-dom";

  const ASSETSearchApplication = ({tenantId, isLoading, t, onSubmit, data, count, setShowToast }) => {
      const isMobile = window.Digit.Utils.browser.isMobile();

      const user = Digit.UserService.getUser();

      const allCities = Digit.Hooks.asset.useTenants();
      const cities = user?.info?.type  === "EMPLOYEE" ? allCities.filter((city) => city?.name=== "Mohali") : allCities;
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

      return <React.Fragment>
                  
                  <div>
                  <Header>{t("ASSET_APPLICATIONS")}</Header>
                  < Card className={"card-search-heading"}>
                      <span style={{color:"#505A5F"}}>{t("Provide at least one parameter to search for an application")}</span>
                  </Card>
                  <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                  <SearchField>
                    <label>{t("MYCITY_CODE_LABEL")}</label>
                    <TextInput name="city" inputRef={register({})} />
                </SearchField>
                  <SearchField>
                      <label>{t("AST_APPLICATION_ID")}</label>
                      <TextInput name="applicationNo" inputRef={register({})} />
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
                              fromDate: "", 
                              city: cities?.[0]?.name,
                              toDate: "",
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

  export default ASSETSearchApplication