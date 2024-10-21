  import React, { useCallback, useMemo, useEffect } from "react"
  import { useForm, Controller } from "react-hook-form";
  import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@nudmcdgnpm/digit-ui-react-components";
  import { Link } from "react-router-dom";

  const SVSearchApplication = ({tenantId, isLoading, t, onSubmit, data, count, setShowToast }) => {
    
      const isMobile = window.Digit.Utils.browser.isMobile();
      const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
          defaultValues: {
              offset: 0,
              limit: !isMobile && 10,
              sortBy: "commencementDate",
              sortOrder: "DESC"
          }
      })
      useEffect(() => {
        register("offset", 0)
        register("limit", 10)
        register("sortBy", "commencementDate")
        register("sortOrder", "DESC")
      },[register])
      
      const applicationStatuses = [
          {
              code: "ACTIVE",
              i18nKey: "WF_PTR_ACTIVE"
          },
          {
              code: "INACTIVE",
              i18nKey: "WF_PTR_INACTIVE"
          },
          {
              code: "INWORKFLOW",
              i18nKey: "WF_PTR_INWORKFLOW"
          },
      ]
      
      const stateId = Digit.ULBService.getStateId();


      const { data: Menu } = Digit.Hooks.ptr.usePTRPetMDMS(stateId, "PetService", "PetType");
      // const { data: Breed_Type } = Digit.Hooks.ptr.useBreedTypeMDMS(stateId, "PetService", "BreedType");  // hooks for breed type

      let menu = [];

      
      // let breed_type = [];


      Menu &&
    Menu.map((petone) => {
      menu.push({ i18nKey: `PTR_PET_${petone.code}`, code: `${petone.code}`, value: `${petone.name}` });
    });


      
      const GetCell = (value) => <span className="cell-text">{value}</span>;
      
      const columns = useMemo( () => ([
          
          {
              Header: t("PTR_APPLICATION_NUMBER"),
              accessor: "applicationNumber",
              disableSortBy: true,
              Cell: ({ row }) => {
                return (
                  <div>
                    <span className="link">
                      <Link to={`/digit-ui/employee/ptr/petservice/applicationsearch/application-details/${row.original["applicationNumber"]}`}>
                        {row.original["applicationNumber"]}
                      </Link>
                    </span>
                  </div>
                );
              },
            },
          

            {
              Header: t("PTR_APPLICANT_NAME"),
              Cell: ( row ) => {
                return GetCell(`${row?.row?.original?.["applicantName"]}`)
                
              },
              disableSortBy: true,
            },
            {
              Header: t("PTR_PET_TYPE"),
              Cell: ({ row }) => {
                return GetCell(`${row.original?.petDetails?.["petType"]}`)
              },
              disableSortBy: true,
            
            },
            {
              Header: t("PTR_BREED_TYPE"),
              Cell: ({ row }) => {
                return GetCell(`${row.original?.petDetails?.["breedType"]}`)
              },
              disableSortBy: true,
            },
            {
              Header: t("PTR_MOBILE_NUMBER"),
              Cell: ({ row }) => {
                return GetCell(`${row?.original?.["mobileNumber"]}`)
              },
              disableSortBy: true,
            },
            // field added for status of application
            {
              Header: t("PTR_APPLICATION_STATUS"),
              Cell: ({ row }) => {
                return GetCell(`${row?.original?.["status"]}`)
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
                  <Header>{t("PTR_SEARCH_PET_APPLICATIONS")}</Header>
                  < Card className={"card-search-heading"}>
                      <span style={{color:"#505A5F"}}>{t("Provide at least one parameter to search for an application")}</span>
                  </Card>
                  <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                  <SearchField>
                      <label>{t("PTR_APPLICATION_NO_LABEL")}</label>
                      <TextInput name="applicationNumber" inputRef={register({})} />
                  </SearchField>
                  <SearchField>
                      <label>{t("PTR_SEARCH_PET_TYPE")}</label>
                      {/* <TextInput name="petType" inputRef={register({})} /> */}
                      <Controller
                              control={control}
                              name="petType"
                              render={(props) => (
                                  <Dropdown
                                  selected={props.value}
                                  select={props.onChange}
                                  onBlur={props.onBlur}
                                  option={menu}
                                  optionKey="i18nKey"
                                  t={t}
                                  disable={false}
                                  />
                              )}
                              />
                  </SearchField>
                  {/* <SearchField>
                      <label>{t("PTR_SEARCH_BREED_TYPE")}</label>
                       <Controller
                              control={control}
                              name="breedType"
                              render={(props) => (
                                  <Dropdown
                                  selected={props.value}
                                  select={props.onChange}
                                  onBlur={props.onBlur}
                                  option={breed_type}
                                  optionKey="i18nKey"
                                  t={t}
                                  disable={false}
                                  />
                              )}
                              />
                  </SearchField> */}
                  <SearchField>
                  <label>{t("PTR_OWNER_MOBILE_NO")}</label>
                  <MobileNumber
                      name="mobileNumber"
                      inputRef={register({
                      minLength: {
                          value: 10,
                          message: t("CORE_COMMON_MOBILE_ERROR"),
                      },
                      maxLength: {
                          value: 10,
                          message: t("CORE_COMMON_MOBILE_ERROR"),
                      },
                      pattern: {
                      value: /[6789][0-9]{9}/,
                      //type: "tel",
                      message: t("CORE_COMMON_MOBILE_ERROR"),
                      },
                  })}
                  type="number"
                  componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
                  //maxlength={10}
                  />
                  <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError>
                  </SearchField> 
                  <SearchField>
                      <label>{t("PTR_FROM_DATE")}</label>
                      <Controller
                          render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} />}
                          name="fromDate"
                          control={control}
                          />
                  </SearchField>
                  <SearchField>
                      <label>{t("PTR_TO_DATE")}</label>
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
                              applicationNumber: "", 
                              fromDate: "", 
                              toDate: "",
                              petType: "",
                              mobileNumber:"",
                              status: "",
                              breedType: "",
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
                      minWidth: cellInfo.column.Header === t("PTR_INBOX_APPLICATION_NO") ? "240px" : "",
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

  export default SVSearchApplication