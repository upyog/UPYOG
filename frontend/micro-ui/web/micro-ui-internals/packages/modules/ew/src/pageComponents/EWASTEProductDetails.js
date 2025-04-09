// Importing necessary components and hooks from external libraries and local files
import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Dropdown, Toast } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for form steps, input fields, dropdowns, and notifications
import Timeline from "../components/EWASTETimeline"; // Component for displaying the timeline
import { Controller, useForm } from "react-hook-form"; // React Hook Form for managing form state
import { SubmitBar } from "@nudmcdgnpm/digit-ui-react-components"; // Component for rendering a submit button
import ProductList from "../components/EWASTEProductList"; // Component for displaying the list of added products

// Main component for capturing product details in the E-Waste module
const EWProductDetails = ({ t, config, onSelect, userType, formData }) => {
  let index = window.location.href.charAt(window.location.href.length - 1); // Extracting the index from the URL
  let validation = {}; // Variable to store validation rules

  // State variables to manage product details
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

  const [showToast, setShowToast] = useState(null); // State to manage toast notifications

  const tenantId = Digit.ULBService.getCurrentTenantId(); // Fetching the current tenant ID
  const stateId = Digit.ULBService.getStateId(); // Fetching the state ID

  // Fetching product details from MDMS (Master Data Management System)
  const { data: Menu } = Digit.Hooks.ew.useProductPriceMDMS(stateId, "Ewaste", "ProductName");

  let menu = []; // Array to store product options

  // Populating the product options menu
  Menu?.Ewaste?.ProductName &&
    Menu?.Ewaste?.ProductName.map((ewasteDetails) => {
      menu.push({
        i18nKey: `EWASTE_${ewasteDetails.code}`, // Internationalization key for the product
        code: `${ewasteDetails.name}`, // Product code
        value: `${ewasteDetails.name}`, // Product name
        price: `${ewasteDetails.price}`, // Product price
      });
    });

  const { control, setError, clearErrors } = useForm(); // React Hook Form methods

  // Handler for updating the product quantity
  function setproductQuantity(e) {
    setShowToast(null); // Clear any existing toast notifications
    setProductQuantity(e.target.value); // Update the product quantity
  }

  // Function to handle adding a product to the list
  const handleAddProduct = () => {
    if (!/^[1-9][0-9]*$/.test(productQuantity)) {
      // Validate that the quantity is a positive integer
      setShowToast({
        label: t("EWASTE_ZERO_ERROR_MESSAGE"), // Show an error message if validation fails
      });
      return;
    }

    // Check if the selected product already exists in the list
    const productExists = prlistName.some((item) => item.code === productName.code);

    if (!productExists) {
      if (productName) {
        // Add the product to the list if it doesn't already exist
        setPrlistName([...prlistName, { code: productName.code, i18nKey: productName.i18nKey, price: productName.price }]);
        setPrlistQuantity([...prlistQuantity, { code: productQuantity }]);
      } else {
        setShowToast({
          label: t("EWASTE_PRODUCT_NAME_NOT_SELECTED_LABEL"), // Show an error message if no product is selected
        });
      }
    } else {
      setShowToast({
        label: t("EWASTE_DUPLICATE_PRODUCT_ERROR_MESSAGE"), // Show an error message if the product already exists
      });
    }
    setProductName(""); // Reset the product name
    setProductQuantity(""); // Reset the product quantity
  };

  // Function to handle the "Next" button click
  const goNext = () => {
    let owner = formData.ewdet && formData.ewdet[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, prlistName, prlistQuantity, calculatedAmount }; // Combine form data with state variables
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index); // Pass the data to the parent component
    } else {
      ownerStep = { ...owner, prlistName, prlistQuantity, calculatedAmount };
      onSelect(config.key, ownerStep, false, index);
    }
  };

  // Function to handle skipping the step
  const onSkip = () => onSelect();

  // Effect to automatically call goNext when product details change
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