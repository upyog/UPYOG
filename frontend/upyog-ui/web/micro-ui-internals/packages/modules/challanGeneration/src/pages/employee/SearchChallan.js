import React, { useState, useEffect, useMemo } from "react";
import { Card, TextInput, Header, SubmitBar, Loader, Toast, Dropdown, Table } from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, FormProvider, Controller } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

/**
 * SearchChallan component:
 * - Provides UI to search challans
 * - Displays results in a table with filters
 */

const SearchChallan = (props) => {

  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [isLoading, setIsLoading] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const [tableData, setTableData] = useState([]);

  const { data: EmployeeStatusData = [], isLoading: callMDMS } = Digit.Hooks.useCustomMDMS(
    tenantId,
    "BillingService",
    [{ name: "BusinessService", filter: "[?(@.type=='Adhoc')]" }],
    {
      select: (data) => {
        const formattedData = data?.["BillingService"]?.["BusinessService"];
        return formattedData;
      },
    }
  );

  const methods = useForm({
    defaultValues: {
      categoryName: "",
    },
  });

  const {
    register,
    handleSubmit,
    reset,
    control,
    getValues,
    formState: { errors },
  } = methods;

  const onSubmit = async (data) => {
    // check if all fields are empty
    const noFieldFilled = !data?.challanNo?.trim() && !data?.businessService && !data?.mobileNumber?.trim();

    if (noFieldFilled) {
      setShowToast({ isError: true, label: "Please fill at least one field to search." });
      return;
    }

    setIsLoading(true);
    const businessServiceData = data?.businessService?.code;

    // Filter out empty strings, null, undefined, and empty arrays
    const filters = Object.entries(data).reduce((acc, [key, value]) => {
      if (
        value !== null &&
        value !== undefined &&
        !(typeof value === "string" && value.trim() === "") &&
        !(Array.isArray(value) && value.length === 0)
      ) {
        // Replace businessService with its code
        acc[key] = key === "businessService" ? businessServiceData : value;
      }
      return acc;
    }, {});


    try {
      const response = await Digit.ChallanGenerationService.search({ tenantId, filters });
      setTableData(response?.challans);
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
    }
  };

  const closeToast = () => {
    setShowToast(null);
  };

  //need to get from workflow
  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const columns = useMemo(
    () => [
      {
        Header: "Challan No",
        disableSortBy: true,
        accessor: (row) => {
          const challanNumber = row?.challanNo;
          return (
            <span className="link">
              <Link to={`${props.parentRoute}/challansearch/` + challanNumber}>{challanNumber}</Link>
            </span>
          );
        },
      },
      {
        Header: "Consumer Name",
        disableSortBy: true,
        accessor: (row) => {
          return GetCell(row?.citizen?.name);
        },
      },
      {
        Header: "Service Type",
        disableSortBy: true,
        accessor: (row) => {
          return GetCell(row?.businessService);
        },
      },
      {
        Header: "Status",
        disableSortBy: true,
        accessor: (row) => {
          const formattedStatus = row?.applicationStatus.toLowerCase().replace(/^\w/, (c) => c.toUpperCase());
          return (
            <span className="cell-text cg-status-green">{formattedStatus}</span>
          );
        },
      },
    ],
    []
  );

  return (
    <React.Fragment>

      <div className={"employee-application-details"} >
        <Header>Search Challan</Header>
      </div>

      <div className="card">
        <FormProvider {...methods}>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="ndcFormCard">
              <div className="surveydetailsform-wrapper">
                <label>Challan No</label>
                <TextInput
                  name="challanNo"
                  type="text"
                  inputRef={register({
                    maxLength: {
                      value: 200,
                    },
                  })}
                />
                {errors.challanNo && <p className="requiredField">{errors.challanNo.message}</p>}
              </div>
              <div className="surveydetailsform-wrapper">
                <label>Service Type</label>
                <Controller
                  control={control}
                  name="businessService"
                  render={(props) => (
                    <Dropdown
                      option={EmployeeStatusData}
                      select={(e) => {
                        props.onChange(e);
                      }}
                      optionKey="code"
                      onBlur={props.onBlur}
                      t={t}
                      selected={props.value}
                    />
                  )}
                />

                {errors.businessService && <p className="requiredField">{errors.businessService.message}</p>}
              </div>
              <div className="surveydetailsform-wrapper">
                <label>Mobile No</label>
                <div className="field-container">
                    <span className="citizen-card-input citizen-card-input--front cg-flex-none">+91</span>
                  <TextInput
                    name="mobileNumber"
                    type="text"
                    inputRef={register({
                      pattern: {
                        value: /^[0-9]+$/,
                        message: "Only numbers are allowed",
                      },
                      minLength: {
                        value: 10,
                        message: "Mobile number must be at least 10 digits",
                      },
                      maxLength: {
                        value: 15,
                        message: "Mobile number cannot exceed 15 digits",
                      },
                    })}
                  />
                  {errors.mobileNumber && <p className="requiredField">{errors.mobileNumber.message}</p>}
                </div>
              </div>
            </div>
            <SubmitBar label="Search" submit="submit" />
          </form>
        </FormProvider>

        {tableData?.length > 0 && (
          <div className="challan-search-table" >
            <Table
              t={t}
              data={tableData}
              totalRecords={9}
              columns={columns}
              getCellProps={(cellInfo) => {
                const classes = ["cg-table-cell"];
                if (cellInfo.column.Header === t("ES_INBOX_APPLICATION_NO")) classes.push("cg-table-minwide");
                return { className: classes.join(" ") };
              }}
              currentPage={getValues("offset") / getValues("limit")}
              pageSizeLimit={getValues("limit")}
              disableSort={false}
              sortParams={[{ id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false }]}
            />
          </div>
        )}
        {showToast && <Toast error={showToast.isError} label={t(showToast.label)} onClose={closeToast} isDleteBtn={"true"} />}
        {isLoading && <Loader />}
      </div>
    </React.Fragment>
  );
};

export default SearchChallan;
