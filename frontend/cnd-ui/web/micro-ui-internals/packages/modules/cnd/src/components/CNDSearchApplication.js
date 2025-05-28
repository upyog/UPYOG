import React, { useCallback, useMemo, useEffect } from "react"
import { useForm, Controller } from "react-hook-form";
import { TextInput, SubmitBar, DatePicker, CardLabelError, SearchForm, SearchField, Dropdown, Table, Card, MobileNumber, Loader, CardText, Header } from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { cndStyles } from "../utils/cndStyles";

/** The CNDSearchApplication component renders the input fields and table with its data
 * Gets data from its parent component through data prop
 * Displays table only when the data is available and on the click of search button
 */
const CNDSearchApplication = ({ tenantId, isLoading, t, onSubmit, data, count, setShowToast }) => {

    const isMobile = window.Digit.Utils.browser.isMobile();
    const todaydate = new Date();
    const today = todaydate.toISOString().split("T")[0];
    const { register, control, handleSubmit, setValue, getValues, reset, formState } = useForm({
        defaultValues: {
            offset: 0,
            limit: !isMobile && 10,
            sortBy: "commencementDate",
            sortOrder: "DESC",
            isUserDetailRequired:"true",
            fromDate: today,
            toDate: today,
        }
    })


    useEffect(() => {
        register("offset", 0)
        register("limit", 10)
        register("sortBy", "commencementDate")
        register("sortOrder", "DESC")
        register("isUserDetailRequired", "true")
        setValue("fromDate", today);
        setValue("toDate", today);
    }, [register, setValue, today])


    const GetCell = (value) => <span className="cell-text">{value}</span>;

    // The table columns of the search Application
    const columns = useMemo(() => ([
        {
            Header: t("CND_APPLICATION_NUMBER"),
            accessor: "applicationNumber",
            disableSortBy: true,
            Cell: ({ row }) => {
                return (
                    <div>
                        <span className="link">
                            <Link to={`/cnd-ui/employee/cnd/applicationsearch/application-details/${row.original["applicationNumber"]}`}>
                                {row.original["applicationNumber"]}
                            </Link>
                        </span>
                    </div>
                );
            },
        },
        {
            Header: t("COMMON_APPLICANT_NAME"),
            Cell: (row) => {
                return GetCell(`${row?.row?.original?.applicantDetail?.nameOfApplicant}`)

            },
            disableSortBy: true,
        },
        {
            Header: t("CND_SCHEDULE_PICKUP"),
            Cell: (row) => {
                return GetCell(`${row?.row?.original?.requestedPickupDate}`)

            },
            disableSortBy: true,
        },
        {
            Header: t("COMMON_MOBILE_NUMBER"),
            Cell: ({ row }) => {
                return GetCell(`${row?.original?.applicantDetail?.mobileNumber}`)
            },
            disableSortBy: true,
        },
        {
            Header: t("CND_APPLICATION_STATUS"),
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
            <Header>{t("CND_SEARCH_APPLICATIONS")}</Header>
            < Card className={"card-search-heading"}>
                <span style={cndStyles.searchApplicationWarning}>{t("Provide at least one parameter to search for an application")}</span>
            </Card>
            <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
                <SearchField>
                    <label>{t("CND_APPLICATION_NUMBER")}</label>
                    <TextInput name="applicationNumber" inputRef={register({})} />
                </SearchField>

                <SearchField>
                    <label>{t("CND_REGISTERED_MOB_NUMBER")}</label>
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
                                message: t("CORE_COMMON_MOBILE_ERROR"),
                            },
                        })}
                        type="number"
                        componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
                    />
                    <CardLabelError>{formState?.errors?.["mobileNumber"]?.message}</CardLabelError>
                </SearchField>
                <SearchField>
                    <label>{t("ES_FROM_DATE")}</label>
                    <Controller
                        render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} max={today} />}
                        name="fromDate"
                        control={control}
                    />
                </SearchField>
                <SearchField>
                    <label>{t("ES_TO_DATE")}</label>
                    <Controller
                        render={(props) => <DatePicker date={props.value} disabled={false} onChange={props.onChange} max={today} />}
                        name="toDate"
                        control={control}
                    />
                </SearchField>
                {/* Empty Field added for the formatting of the form */}
                <SearchField></SearchField> 
                <SearchField className="submit">
                    <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
                    <p style={cndStyles.clearButton}
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
                            <p key={index} style={cndStyles.noInboxApplication}>
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
                        return {style: cndStyles.applicationTable};
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

export default CNDSearchApplication