import React, { useCallback, useMemo, useEffect, useState } from "react"
import { useForm, Controller } from "react-hook-form";
import { TextInput, SubmitBar, LinkLabel, ActionBar, CloseSvg, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@upyog/digit-ui-react-components";
import { Link } from "react-router-dom";

/** The SVSearchApplication component renders the input fields and table with its data
 * Gets data from its parent component through data prop
 * Displays table only when the data is available and on the click of search button
 */
const SVSearchApplication = ({ tenantId, isLoading, t, onSubmit, data, count, setShowToast }) => {
    const allCities = Digit.Hooks.sv.useTenants();
    const isMobile = window.Digit.Utils.browser.isMobile();
    const todaydate = new Date();
    const today = todaydate.toISOString().split("T")[0];
    const [vendingLocality, setVendingLocality] = useState(null)
    
    const { register, control, handleSubmit, setValue, getValues, reset, formState, watch } = useForm({
        defaultValues: {
            offset: 0,
            limit: !isMobile && 10,
            sortBy: "commencementDate",
            sortOrder: "DESC",
            isDraftApplication:"false",
            fromDate: today,
            toDate: today
        }
    })


    useEffect(() => {
        register("offset", 0)
        register("limit", 10)
        register("sortBy", "commencementDate")
        register("sortOrder", "DESC")
        register("isDraftApplication", "false")
        setValue("fromDate", today);
        setValue("toDate", today);
    }, [register, setValue, today])

    const paymentStatusOptions = [
        { i18nKey: "SV_PENDING_PAYMENT", code: "PENDING_PAYMENT", value: "Pending Payment" },
        { i18nKey: "SV_PAID", code: "PAID", value: "Paid" }
    ];
    const renewalStatusOptions = [
        { i18nKey: "SV_ELIGIBLE_TO_RENEW", code: "ELIGIBLE_TO_RENEW", value: "Eligible to Renew" },
        { i18nKey: "SV_RENEW_IN_PROGRESS", code: "RENEW_IN_PROGRESS", value: "Renew in Progress" },
        { i18nKey: "SV_RENEW_APPLICATION_CREATED", code: "RENEW_APPLICATION_CREATED", value: "Renew Application Created" },  
        { i18nKey: "SV_RENEWED", code: "RENEWED", value: "Renewed" }
    ];          

    // hook for fetching vending type data
    const { data: vendingTypeData } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "StreetVending", [{ name: "VendingActivityType" }],
        {
            select: (data) => {
                const formattedData = data?.["StreetVending"]?.["VendingActivityType"]
                return formattedData;
            },
        });
    let vendingTypeOptions = [];
    vendingTypeData && vendingTypeData.map((vending) => {
        vendingTypeOptions.push({ i18nKey: `${vending.name}`, code: `${vending.code}`, value: `${vending.name}` })
    })

    /* fetching vending zones from boundary service */
    const { data: fetchedVendingZones } = Digit.Hooks.useBoundaryLocalities(
      vendingLocality?.code,
      "vendingzones",
      {
        enabled: !!vendingLocality,
      },
      t
    );

    let vending_Zone = [];
    fetchedVendingZones && fetchedVendingZones.map((vendingData) => {
      vending_Zone.push({ i18nKey: vendingData?.i18nkey, code: vendingData?.code, value: vendingData?.name })
    })

    const stateId = Digit.ULBService.getStateId();

    const GetCell = (value) => <span className="cell-text">{value}</span>;

    // The table columns of the search Application
    const columns = useMemo(() => ([
        {
            Header: t("SV_APPLICATION_NUMBER"),
            accessor: "applicationNo",
            disableSortBy: true,
            Cell: ({ row }) => {
                return (
                    <div>
                        <span className="link">
                            <Link to={`/digit-ui/employee/sv/applicationsearch/application-details/${row.original["applicationNo"]}`}>
                                {row.original["applicationNo"]}
                            </Link>
                        </span>
                    </div>
                );
            },
        },


        {
            Header: t("SV_VENDOR_NAME"),
            Cell: (row) => {
                return GetCell(`${row?.row?.original?.vendorDetail[0]?.name}`)

            },
            disableSortBy: true,
        },
        {
            Header: t("SV_VENDING_TYPE"),
            Cell: (row) => {
                return GetCell(`${row?.row?.original?.vendingActivity}`)

            },
            disableSortBy: true,
        },
        {
            Header: t("SV_VENDING_ZONES"),
            Cell: (row) => {
                return GetCell(`${t(row?.row?.original?.vendingZoneValue)}`)

            },
            disableSortBy: true,
        },
        {
            Header: t("SV_REGISTERED_MOB_NUMBER"),
            Cell: ({ row }) => {
                return GetCell(`${row?.original?.vendorDetail[0]?.mobileNo}`)
            },
            disableSortBy: true,
        },
        {
            Header: t("SV_APPLICATION_STATUS"),
            Cell: ({ row }) => {
                return GetCell(`${row?.original?.["applicationStatus"]}`)
            },
            disableSortBy: true,
        },
    ]), [])

    // sorts the table data based on the defined field
    const onSort = useCallback((args) => {
        if (args.length === 0) return
        setValue("sortBy", args.id)
        setValue("sortOrder", args.desc ? "DESC" : "ASC")
    }, [])

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
    let validation = {}

    return <React.Fragment>

        {/* Following are the input fields which renders in the search application page and takes input for the parameters to search */}
        <div>
            <Header>{t("SV_SEARCH_APPLICATIONS")}</Header>
            < Card className={"card-search-heading"}>
                <span style={{ color: "#505A5F" }}>{t("Provide at least one parameter to search for an application")}</span>
            </Card>
            <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                <SearchField>
                    <label>{t("SV_APPLICATION_NUMBER")}</label>
                    <TextInput name="applicationNumber" inputRef={register({})} />
                </SearchField>

                                <SearchField>
                    <label>{t("SV_REGISTERED_MOB_NUMBER")}</label>
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
                    <label>{t("SV_FROM_DATE")}</label>
                    <Controller
                        render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} max={today} />}
                        name="fromDate"
                        control={control}
                    />
                </SearchField>
                <SearchField>
                    <label>{t("SV_TO_DATE")}</label>
                    <Controller
                        render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} max={today} />}
                        name="toDate"
                        control={control}
                    />
                </SearchField>

                <SearchField>
                    <label>{t("SV_VENDING_TYPE")}</label>
                    <Controller
                        control={control}
                        name="vendingType"
                        render={(props) => (
                            <Dropdown
                                selected={props.value}
                                select={props.onChange}
                                onBlur={props.onBlur}
                                option={vendingTypeOptions}
                                optionKey="i18nKey"
                                t={t}
                                disable={false}
                            />
                        )}
                    />
                </SearchField>

                <SearchField>
                    <label>{t("SV_VENDING_LOCALITY")}</label>
                    <Controller
                        control={control}
                        name="vendingLocality"
                        defaultValue={vendingLocality}
                        render={(props) => (
                            <Dropdown
                                selected={vendingLocality}
                                select={(e) => setVendingLocality(e)}
                                // selected={props.value}
                                // select={props.onChange}
                                option={allCities}
                                optionKey="i18nKey"
                                t={t}
                                disable={false}
                            />
                        )}
                    />
                </SearchField>

                <SearchField>
                    <label>{t("SV_VENDING_ZONES")}</label>
                    <Controller
                        control={control}
                        name="vendingZone"
                        render={(props) => (
                            <Dropdown
                                selected={props.value}
                                select={props.onChange}
                                onBlur={props.onBlur}
                                option={vending_Zone}
                                optionKey="i18nKey"
                                t={t}
                                disable={false}
                            />
                        )}
                    />
                </SearchField>

                <SearchField>
                    <label>{t("SV_PAYMENT_STATUS")}</label>
                    <Controller
                        control={control}
                        name="paymentStatus"
                        render={(props) => (
                            <Dropdown
                                selected={props.value}
                                select={props.onChange}
                                onBlur={props.onBlur}
                                option={paymentStatusOptions}
                                optionKey="i18nKey"
                                t={t}
                                disable={false}
                            />
                        )}
                    />
                </SearchField>

                <SearchField>
                    <label>{t("SV_RENEWAL_STATUS")}</label>
                    <Controller
                        control={control}
                        name="renewalStatus"
                        render={(props) => (
                            <Dropdown
                                selected={props.value}
                                select={props.onChange}
                                onBlur={props.onBlur}
                                option={renewalStatusOptions}
                                optionKey="i18nKey"
                                t={t}
                                disable={false}
                            />
                        )}
                    />
                </SearchField>


                {/* Empty Field added for the formatting of the form */}
                <SearchField></SearchField> 
                <SearchField className="submit">
                    <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
                    <p style={{ marginTop: "10px" }}
                        onClick={() => {
                            reset({
                                applicationNumber: "",
                                fromDate: "",
                                toDate: "",
                                mobileNumber: "",
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
                // Displaying the data in the table if data is stopped loading and its fetched
                : (!isLoading && data !== "" ? <Table
                    t={t}
                    data={data}
                    totalRecords={count}
                    columns={columns}
                    getCellProps={(cellInfo) => {
                        return {
                            style: {
                                minWidth: cellInfo.column.Header === t("SV_INBOX_APPLICATION_NO") ? "240px" : "",
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

export default SVSearchApplication