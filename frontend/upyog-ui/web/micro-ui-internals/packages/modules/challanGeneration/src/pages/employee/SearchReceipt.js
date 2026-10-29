// ===================== IMPORTS =====================

// React hooks
import React, { useState, useEffect, useMemo } from "react";

// UI components
import { Card, TextInput, Header, ActionBar, SubmitBar, Loader, InfoIcon, Toast, Dropdown, Table } from "@nudmcdgnpm/digit-ui-react-components";

// React Hook Form for form handling
import { useForm, FormProvider, Controller } from "react-hook-form";

// i18n for translations
import { useTranslation } from "react-i18next";

/**
 * SearchReceipt Component
 * ------------------------------------------------------
 * Purpose:
 * - Allows user to search receipts using filters
 * - Displays results in table
 * - Enables PDF download of receipts
 */
const SearchReceipt = () => {

  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  // ===================== STATE =====================

  const [isLoading, setIsLoading] = useState(false);       // Loader state
  const [showToast, setShowToast] = useState(null);        // Toast messages
  const [tableData, setTableData] = useState([]);          // Table data (search results)
  const [hasSearched, setHasSearched] = useState(false);   // Flag to show "No Records Found"

  // ===================== FETCH SERVICE TYPES =====================

  /**
   * Fetch business services (Service Type dropdown)
   */
  const { data: EmployeeStatusData = [], isLoading: callMDMS } = Digit.Hooks.useCustomMDMS(
    tenantId,
    "BillingService",
    [{ name: "BusinessService", filter: "[?(@.type=='Adhoc')]" }],
    {
      select: (data) => {
        return data?.["BillingService"]?.["BusinessService"];
      },
    }
  );

  // ===================== FORM SETUP =====================

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

  // ===================== FORM SUBMISSION =====================

  /**
   * Handles search request
   * - Filters empty fields
   * - Calls receiptSearch API
   * - Updates table data
   */
  const onSubmit = async (data) => {
    setIsLoading(true);
    setHasSearched(true);

    const businessService = data?.businessServices?.code;

    /**
     * Remove empty/null values from request payload
     */
    const filteredData = Object.entries(data).reduce((acc, [key, value]) => {
      if (
        value !== null &&
        value !== undefined &&
        !(typeof value === "string" && value.trim() === "") &&
        !(Array.isArray(value) && value.length === 0)
      ) {
        acc[key] = key === "businessServices" ? businessService : value;
      }
      return acc;
    }, {});

    try {
      // Call API to search receipts
      const response = await Digit.ChallanGenerationService.recieptSearch(
        tenantId,
        businessService,
        filteredData
      );

      // Store results in table
      setTableData(response?.Payments);
      setIsLoading(false);

    } catch (error) {
      setIsLoading(false);
    }
  };

  // ===================== TOAST HANDLER =====================

  const closeToast = () => {
    setShowToast(null);
  };

  // ===================== PDF DOWNLOAD =====================

  /**
   * Generate and download receipt PDF
   */
  const downloadPDF = async (rowData) => {
    setIsLoading(true);

    try {
      // Generate PDF
      const response = await Digit.ChallanGenerationService.generatePdf(
        tenantId,
        { Payments: [{ ...rowData }] },
        "consolidatedreceiptold"
      );

      setIsLoading(false);

      // Fetch file using filestoreId
      fileFetch(response?.filestoreIds?.[0]);

    } catch (error) {
      setIsLoading(false);
    }
  };

  /**
   * Fetch file URL and open in new tab
   */
  const fileFetch = async (fileStoreId) => {
    setIsLoading(true);

    try {
      const response = await Digit.ChallanGenerationService.file_fetch(
        tenantId,
        fileStoreId
      );

      setIsLoading(false);

      // Extract file URL
      const fileUrl =
        response?.[fileStoreId] ||
        response?.fileStoreIds?.[0]?.url;

      if (fileUrl) {
        window.open(fileUrl, "_blank"); // open PDF
      } else {
        console.error("File URL not found");
      }

    } catch (error) {
      setIsLoading(false);
    }
  };

  // ===================== TABLE CONFIG =====================

  /**
   * Utility to render table cell
   */
  const GetCell = (value) => <span className="cell-text">{value}</span>;

  /**
   * Table columns definition
   */
  const columns = useMemo(() => [
    {
      Header: "Receipt No",
      accessor: (row) => {
        const receiptNumber = row?.paymentDetails?.[0]?.receiptNumber;

        // Clickable receipt → triggers PDF download
        return (
          <span className="cell-text cg-link-blue-pointer" onClick={() => downloadPDF(row)}>
            {receiptNumber}
          </span>
        );
      },
    },
    {
      Header: "Consumer Code/Application No/Challan No",
      accessor: (row) =>
        GetCell(row?.paymentDetails?.[0]?.bill?.consumerCode),
    },
    {
      Header: "Consumer Name",
      accessor: (row) => GetCell(row?.paidBy),
    },
    {
      Header: "Service Type",
      accessor: (row) =>
        GetCell(row?.paymentDetails?.[0]?.businessService),
    },
    {
      Header: "Receipt Date",
      accessor: (row) =>
        GetCell(row?.paymentDetails?.[0]?.receiptNumber),
    },
    {
      Header: "Amount Paid[INR]",
      accessor: (row) =>
        GetCell(row?.paymentDetails?.[0]?.bill?.totalAmount),
    },
  ], []);

  // ===================== UI =====================

  return (
    <React.Fragment>

      {/* Page Header */}
      <div className={"employee-application-details"}>
        <Header>Search Receipts</Header>
      </div>

      <div className="card">

        {/* ===================== SEARCH FORM ===================== */}
        <FormProvider {...methods}>
          <form onSubmit={handleSubmit(onSubmit)}>

            {/* Receipt Number */}
            <label>Receipt No</label>
            <TextInput name="receiptNumbers" inputRef={register()} />

            {/* Service Type (Mandatory Dropdown) */}
            <Controller
              control={control}
              name="businessServices"
              rules={{ required: t("REQUIRED_FIELD") }}
              render={(props) => (
                <Dropdown
                  option={EmployeeStatusData}
                  select={props.onChange}
                  optionKey="code"
                  selected={props.value}
                  t={t}
                />
              )}
            />

            {/* Consumer Code */}
            <TextInput name="consumerCodes" inputRef={register()} />

            {/* Mobile Number */}
            <TextInput name="mobileNumber" inputRef={register()} />

            {/* Submit Button */}
            <SubmitBar label="Search" submit="submit" />
          </form>
        </FormProvider>

        {/* ===================== RESULTS TABLE ===================== */}
        {tableData?.length > 0 ? (
          <Table data={tableData} columns={columns} />
        ) : (
          hasSearched && !isLoading && (
            <div>No Records Found</div>
          )
        )}

        {/* Loader */}
        {isLoading && <Loader />}

        {/* Toast */}
        {showToast && (
          <Toast
            error={showToast.isError}
            label={t(showToast.label)}
            onClose={closeToast}
          />
        )}

      </div>
    </React.Fragment>
  );
};

export default SearchReceipt;