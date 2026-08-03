import React, { useEffect, useState } from "react";
// Local state management
// showToast → controls toast visibility
// loader → controls loading spinner
// error → stores validation or document errors
// documentsData → stores uploaded documents data
import { useDispatch, useSelector } from "react-redux";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import {
  Dropdown,
  LabelFieldPair,
  CardHeader,
  Toast,
  TextInput,
  CardLabel,
  MobileNumber,
  TextArea,
  ActionBar,
  SubmitBar,
} from "@nudmcdgnpm/digit-ui-react-components";
import { Loader } from "../../components/Loader";
import { SET_ChallanApplication_STEP } from "../../../redux/action/ChallanApplicationActions";
import SelectNDCDocuments from "../ChallanDocuments";

// // ChallanStepperForm Component
// ---------------------------------------------
// This component is responsible for rendering the challan creation form.
// It handles:
// - User input (offender details, offence details)
// - Fetching master data (Category, Subcategory, Offence Type, Rates, Documents)
// - Auto-populating user details based on mobile number
// - Document upload and validation
// - Preparing and submitting challan payload to backend API
// - Navigation after successful submission

const ChallanStepperForm = () => {
  const navigate = Digit.Hooks.useCustomNavigate();
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const [showToast, setShowToast] = useState(null);
  const [loader, setLoader] = useState(false);
  const [error, setError] = useState(null);
  const [documentsData, setDocumentsData] = useState({});
  const isCitizen = window.location.href.includes("citizen");

// Handles document selection from SelectNDCDocuments component
// Updates local state with uploaded document details

  const handleDocumentsSelect = (key, data) => {
    setDocumentsData(data);
  };

// Fetching master data (MDMS)
// These APIs provide dropdown values and configurations
// Category, SubCategory, OffenceType, Rates, Documents, Acts

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: categoryData, isLoading: categoryLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "Category" }]);
  const { data: subCategoryData, isLoading: subCategoryLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "SubCategory" }]);
  const { data: OffenceTypeData, isLoading: OffenceTypeLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "OffenceType" }]);
  const { data: OffenceRates, isLoading: OffenceRatesLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "Rates" }]);
  const { data: docData, isLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "Documents" }]);
  const { data: OffenceActData, isLoading: OffenceActLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "Acts" }]);

  const {
    control,
    handleSubmit,
    setValue,
    reset,
    formState: { errors },
    getValues,
    clearErrors,
  } = useForm({
    defaultValues: {
      shouldUnregister: false,
    },
  });

  // Utility function to map offenceType → Acts
  // Converts act codes into readable act names (comma separated)
const getActs = (offenceType, offenceActData) =>
  offenceType?.acts?.map(
    (code) => offenceActData?.Challan?.Acts?.find((a) => a?.code === code)?.name
  )?.filter(Boolean)?.join(", ") || "";

// Form submission handler
// ---------------------------------------------
// Steps:
// 1. Validate required documents
// 2. Prepare challan payload
// 3. Call backend API to create challan
// 4. Redirect to response page on success
// 5. Handle errors gracefully

  const onSubmit = async (data) => {
    let missingDocs = [];
    const actString = getActs(data?.offenceType, OffenceActData); 
    docData?.Challan?.Documents?.forEach((doc) => {
      if (doc.required) {
        const hasFile = documentsData?.documents?.some((d) => d.documentType.includes(doc.code) && d.filestoreId);
        if (!hasFile) missingDocs.push(t(doc.code));
      }
    });

// Validate required documents
// If any required document is missing → stop submission and show error

    if (missingDocs.length > 0) {
      setError(t("CHALLAN_MESSAGE_CHALLAN_" + missingDocs[0].replace(/\s+/g, "_").toUpperCase()));
      return;
    }

// Construct Challan payload as per backend contract
// Includes:
// - Citizen details
// - Address
// - Offence details
// - Amount
// - Geo-location (latitude/longitude)
// - Uploaded documents

    setLoader(true);
    const Challan = {
      tenantId: tenantId,
      citizen: {
        name: data?.name,
        mobileNumber: data?.mobileNumber,
        tenantId: tenantId,
        active: true,
      },
      address: {
        addressLine1: data?.address,
      },
      businessService: "Challan_Generation",
      offenceTypeName: data?.offenceType?.name,
      offenceCategoryName: data?.offenceCategory?.name,
      offenceSubCategoryName: data?.offenceSubCategory?.name,
      challanAmount: data?.challanAmount,
      // amount: data?.amount,
      amount: [
        {
          amount: data?.amount,
        },
      ],
      additionalDetail: {
        latitude: documentsData?.documents?.[1]?.latitude,
        longitude: documentsData?.documents?.[1]?.longitude,
        offenceActs: actString,
      },
      // address: {},
      documents: documentsData?.documents,
      workflow: {
        action: "SUBMIT",
      },
    };

// API call to create challan
// On success → redirect to response screen with challan number

    try {
      const response = await Digit.ChallanGenerationService.create({ Challan: Challan });
      setLoader(false);
      const id = response?.challans?.[0]?.challanNo;
      navigate("/upyog-ui/employee/challangeneration/response/" + `${id}`);
    } catch (error) {
      setLoader(false);
    }
  };

  const handleMobileChange = async (value) => {
    setLoader(true);
    try {
      const userData = await Digit.UserService.userSearch(tenantId, { userName: value, mobileNumber: value, userType: "CITIZEN" }, {});
      if (userData?.user?.[0]?.name) {
        setValue("name", userData.user[0].name); // ✅ populate name
        setValue("address", userData.user[0].permanentAddress); // ✅ populate name
        clearErrors("name"); // ✅ remove validation error if any
      }
      setLoader(false);
    } catch (error) {
      setLoader(false);
    }
  };

  const handleRates = (val) => {
    const filterRates = OffenceRates?.Challan?.Rates?.filter((item) => item?.offenceTypeId == val?.id);
    setValue("amount", filterRates?.[0]?.amount);
  };

  return (
    <div className="card custom-challan-card">
      <div className="challan-stepper-parent-component">
        <CardHeader divider={true}>{t("CREATE_CHALLAN")}</CardHeader>
        <form onSubmit={handleSubmit(onSubmit)}>
          <CardLabel>{t("CHALLAN_OFFENDER_DETAILS")}</CardLabel>
          <div className="cg-width-100">
            <div>
              <CardLabel>
                {`${t("NOC_APPLICANT_MOBILE_NO_LABEL")}`} <span className="requiredField">*</span>
              </CardLabel>
              <Controller
                control={control}
                name="mobileNumber"
                rules={{
                  required: "Mobile number is required",
                  pattern: {
                    value: /^[6-9]\d{9}$/,
                    message: "Enter a valid 10-digit mobile number",
                  },
                }}
                render={({ field }) => (
                  <MobileNumber
                    value={field.value}
                    maxlength={10}
                    onChange={(e) => {
                      field.onChange(e);
                      setValue("name", "");
                      setValue("address", "");
                      if (e.length == 10) {
                        handleMobileChange(e);
                      }
                    }}
                    onBlur={field.onBlur}
                    t={t}
                  />
                )}
              />
              {errors?.mobileNumber && <p className="requiredField">{errors.mobileNumber.message}</p>}
            </div>

            <div>
              <CardLabel>
                {`${t("BPA_BASIC_DETAILS_APPLICATION_NAME_LABEL")}`} <span className="requiredField">*</span>
              </CardLabel>
              <Controller
                control={control}
                name="name"
                rules={{
                  required: "Name is required",
                  minLength: { value: 2, message: "Name must be at least 2 characters" },
                }}
                render={({ field }) => (
                  <TextInput
                    value={field.value}
                    error={errors?.name?.message}
                    onChange={(e) => field.onChange(e.target.value)}
                    onBlur={field.onBlur}
                    t={t}
                  />
                )}
              />
              {errors?.name && <p className="requiredField">{errors.name.message}</p>}
            </div>

            {/* address field yes */}
            <div>
              <CardLabel>
                {`${t("PT_COMMON_COL_ADDRESS")}`} <span className="requiredField">*</span>
              </CardLabel>
              <Controller
                control={control}
                name="address"
                rules={{
                  required: "Address is required",
                  minLength: { value: 5, message: "Address must be at least 5 characters" },
                }}
                render={({ field }) => (
                  <TextArea
                    name="address"
                    value={field.value}
                    onChange={(e) => field.onChange(e.target.value)}
                    onBlur={field.onBlur}
                    t={t}
                  />
                )}
              />
              {errors?.address && <p className="requiredField">{errors.address.message}</p>}
            </div>

            {/* Offence Category */}
            <LabelFieldPair>
              <CardLabel>
                {t("CHALLAN_OFFENCE_CATEGORY")} <span className="requiredField">*</span>
              </CardLabel>
              <Controller
                control={control}
                name={"offenceCategory"}
                defaultValue={null}
                rules={{ required: t("CHALLAN_OFFENCE_CATEGORY_REQUIRED") }}
                render={({ field }) => (
                  <Dropdown
                    className="form-field"
                    select={field.onChange}
                    selected={field.value}
                    option={categoryData?.Challan?.Category}
                    optionKey="name"
                    t={t}
                  />
                )}
              />
              {errors.offenceCategory && <p className="requiredField">{errors.offenceCategory.message}</p>}
            </LabelFieldPair>

            {/* Offence Subcategory */}
            <LabelFieldPair>
              <CardLabel>
                {t("CHALLAN_OFFENCE_SUB_CATEGORY")} <span className="requiredField">*</span>
              </CardLabel>
              <Controller
                control={control}
                name={"offenceSubCategory"}
                defaultValue={null}
                rules={{ required: t("CHALLAN_OFFENCE_SUB_CATEGORY_REQUIRED") }}
                render={({ field }) => (
                  <Dropdown
                    className="form-field"
                    select={field.onChange}
                    selected={field.value}
                    option={subCategoryData?.Challan?.SubCategory}
                    optionKey="name"
                    t={t}
                  />
                )}
              />
              {errors.offenceSubCategory && <p className="requiredField">{errors.offenceSubCategory.message}</p>}
            </LabelFieldPair>

            {/* offence type */}
            <LabelFieldPair>
              <CardLabel>
                {t("CHALLAN_TYPE_OFFENCE")} <span className="requiredField">*</span>
              </CardLabel>
              <Controller
                control={control}
                name={"offenceType"}
                defaultValue={null}
                rules={{ required: t("CHALLAN_TYPE_OFFENCE_REQUIRED") }}
                render={({ field }) => (
                  <Dropdown
                    className="form-field"
                    select={(e) => {
                      field.onChange(e);
                      handleRates(e);
                    }}
                    selected={field.value}
                    option={OffenceTypeData?.Challan?.OffenceType}
                    optionKey="name"
                    t={t}
                  />
                )}
              />
              {errors.offenceType && <p className="requiredField">{errors.offenceType.message}</p>}
            </LabelFieldPair>

            {/* Challan Amount Default */}
            <LabelFieldPair>
              <CardLabel>{`${t("DEFAULT_CHALLAN_AMOUNT")}`}</CardLabel>
              <Controller
                control={control}
                name="amount"
                render={({ field }) => (
                  <TextInput
                    type="number"
                    value={field.value}
                    error={errors?.name?.message}
                    disable={true}
                    onChange={(e) => field.onChange(e.target.value)}
                    onBlur={field.onBlur}
                    t={t}
                  />
                )}
              />
            </LabelFieldPair>
          </div>

          <CardLabel>
            {t("CHALLAN_DOCUMENTS")} <span className="requiredField">*</span>
          </CardLabel>
          <div>
            <SelectNDCDocuments
              t={t}
              config={{ key: "documents" }}
              onSelect={handleDocumentsSelect}
              userType="CITIZEN"
              formData={{ documents: documentsData }}
              setError={setError}
              error={error}
              clearErrors={() => {}}
              formState={{}}
              data={docData}
              isLoading={isLoading}
            />
          </div>

          <ActionBar>
            <SubmitBar label="Submit" submit="submit" />
          </ActionBar>
        </form>
      </div>
      {showToast && (
        <Toast
          error={showToast.key}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
          isDleteBtn={"true"}
        />
      )}
      {(OffenceRatesLoading || loader || categoryLoading || subCategoryLoading || OffenceTypeLoading || OffenceActLoading) && <Loader page={true} />}
    </div>
  );
};

export default ChallanStepperForm;

// SUMMARY:
// This component manages the complete lifecycle of challan creation:
// input → validation → enrichment → submission → navigation