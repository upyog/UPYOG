import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Dropdown, Toast } from "@upyog/digit-ui-react-components";
import Timeline from "../components/EWASTETimeline";
import { Controller, useForm } from "react-hook-form";
import { SubmitBar } from "@upyog/digit-ui-react-components";
import ProductList from "../components/EWASTEProductList";

/**
 * Form component for capturing E-Waste product details and quantities.
 * Manages product selection, quantity input, and price calculation.
 *
 * @param {Object} props Component properties
 * @param {Function} props.t Translation function
 * @param {Object} props.config Form configuration settings
 * @param {Function} props.onSelect Handler for form submission
 * @param {string} props.userType Type of user (citizen/employee)
 * @param {Object} props.formData Existing form data
 * @returns {JSX.Element} Product details form with list view
 */
const EWProductDetails = ({ t, config, onSelect, userType, formData }) => {
  const index = window.location.href.charAt(window.location.href.length - 1);
  let validation = {};

  /**
   * State management for product form fields and calculations
   */
  const [productName, setProductName] = useState(
    (formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.productName) || formData?.ewdet?.productName || ""
  );
  const [productQuantity, setProductQuantity] = useState(
    (formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.productQuantity) || formData?.ewdet?.productQuantity || "0"
  );
  const [calculatedAmount, setCalculatedAmount] = useState(
    (formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.calculatedAmount) || formData?.ewdet?.calculatedAmount || ""
  );

  const [prlistName, setPrlistName] = useState(
    (formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.prlistName) || formData?.ewdet?.prlistName || []
  );
  const [prlistQuantity, setPrlistQuantity] = useState(
    (formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.prlistQuantity) || formData?.ewdet?.prlistQuantity || []
  );

  const [showToast, setShowToast] = useState(null);

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  const { data: Menu } = Digit.Hooks.useEnabledMDMS(stateId, "Ewaste", [{ name: "ProductName" }],
    {
      select: (data) => {
        const formattedData = data?.["Ewaste"]?.["ProductName"]
        return formattedData;
      },
    });
  const { control, setError, clearErrors } = useForm();

  /**
   * Transforms MDMS product data into dropdown options
   */
  const menu = Menu?.map(ewasteDetails => ({
    i18nKey: `EWASTE_${ewasteDetails.code}`,
    code: ewasteDetails.name,
    value: ewasteDetails.name,
    price: ewasteDetails.price,
  })) || [];

  /**
   * Updates product quantity with validation
   */
  function setproductQuantity(e) {
    setShowToast(null);
    setProductQuantity(e.target.value);
  }

  /**
   * Handles adding new products to the list
   * Includes validation and duplicate checking
   */
  const handleAddProduct = () => {
    if (!/^[1-9][0-9]*$/.test(productQuantity)) {
      setShowToast({ label: t("EWASTE_ZERO_ERROR_MESSAGE") });
      return;
    }

    const productExists = prlistName.some((item) => item.code === productName.code);

    if (!productExists && productName) {
      setPrlistName([...prlistName, { code: productName.code, i18nKey: productName.i18nKey, price: productName.price }]);
      setPrlistQuantity([...prlistQuantity, { code: productQuantity }]);
    } else {
      setShowToast({
        label: productName ? t("EWASTE_DUPLICATE_PRODUCT_ERROR_MESSAGE") : t("EWASTE_PRODUCT_NAME_NOT_SELECTED_LABEL"),
      });
    }
    setProductName("");
    setProductQuantity("");
  };

  /**
   * Processes form data for submission
   * Handles different data structures for citizen and employee users
   */
  const goNext = () => {
    let owner = formData.ewdet && formData.ewdet[index];
    let ownerStep = { ...owner, prlistName, prlistQuantity, calculatedAmount };
    
    if (userType === "citizen") {
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      onSelect(config.key, ownerStep, false, index);
    }
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [prlistName, prlistQuantity, calculatedAmount]);

  return (
    <React.Fragment>
      {/* Display the timeline if the user is on the citizen portal */}
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}

      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t} isDisabled={!calculatedAmount}>
        <div>
          {/* Dropdown for selecting a product */}
          <CardLabel>{`${t("EWASTE_SEARCH_PRODUCT")}`}</CardLabel>
          <Controller
            control={control}
            name={"productName"}
            defaultValue={productName}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={productName}
                select={setProductName}
                option={menu}
                optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
                optionKey="i18nKey"
                t={t}
              />
            )}
          />

          {/* Input field for product quantity */}
          <CardLabel>{`${t("EWASTE_QUANTITY")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="productQuantity"
            value={productQuantity}
            onChange={setproductQuantity}
            style={{ width: "86%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: false,
              pattern: "^[0-9]*$",
              type: "text",
            })}
          />

          {/* Input field for product price (read-only) */}
          <CardLabel>{`${t("EWASTE_UNIT_PRICE")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="productPrice"
            value={productName && productName.price}
            disable={true}
            style={{ width: "86%" }}
            ValidationRequired={false}
          />
        </div>
        <SubmitBar label="Add Product" style={{ marginBottom: "10px" }} onSubmit={handleAddProduct} />
      </FormStep>

      {/* Component to display the list of added products */}
      <div>
        <ProductList
          t={t}
          prlistName={prlistName}
          setPrlistName={setPrlistName}
          prlistQuantity={prlistQuantity}
          setPrlistQuantity={setPrlistQuantity}
          setCalculatedAmount={setCalculatedAmount}
          calculatedAmount={calculatedAmount}
        />
      </div>

      {/* Display a toast notification if an error occurs */}
      {showToast?.label && (
        <Toast
          label={showToast.label}
          error={true}
          isDleteBtn={true}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </React.Fragment>
  );
};

export default EWProductDetails; // Exporting the component