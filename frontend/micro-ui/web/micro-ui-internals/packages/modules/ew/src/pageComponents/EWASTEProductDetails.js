import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, Dropdown, RadioOrSelect } from "@upyog/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/EWASTETimeline";
import { Controller, useForm } from "react-hook-form";
import { SubmitBar } from "@upyog/digit-ui-react-components";
import ProductList from "../components/EWASTEProductList";

const EWProductDetails = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
  const { pathname: url } = useLocation();
  let index = window.location.href.charAt(window.location.href.length - 1);
  let validation = {};

  const [productName, setProductName] = useState(
    (formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.productName) || formData?.ewdet?.productName || ""
  );
  const [productQuantity, setProductQuantity] = useState(
    (formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.productQuantity) || formData?.ewdet?.productQuantity || "1"
  );
  // const productPrice = (formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.productPrice) || formData?.ewdet?.productPrice || "0.0";

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  const { data: Menu } = Digit.Hooks.ew.useProductPriceMDMS(stateId, "Ewaste", "ProductName");

  let menu = [];

  Menu?.Ewaste?.ProductName &&
    Menu?.Ewaste?.ProductName.map((ewasteDetails) => {
      menu.push({ i18nKey: `EWASTE_${ewasteDetails.code}`, code: `${ewasteDetails.code}`, value: `${ewasteDetails.name}`, price: `${ewasteDetails.price}` });
    });

  const { control } = useForm();

  function setproductQuantity(e) {
    setProductQuantity(e.target.value);
  }

  const [prlistName, setPrlistName] = useState([]);
  const [prlistQuantity, setPrlistQuantity] = useState([]);
  // const [prlistTotalprice, setPrlistTotalprice] = useState([]);

  const handleAddProduct = () => {
    setPrlistName([...prlistName, { code: productName.code, i18nKey: productName.i18nKey, price: productName.price }]);
    setPrlistQuantity([...prlistQuantity, { code: productQuantity }]);
    // setPrlistTotalprice([...prlistTotalprice, { code: productPrice }]);
    // prlist.map((pr) => {
    //   console.log("product is" + pr.code);
    // })
  };

  const goNext = () => {
    let owner = formData.ewdet && formData.ewdet[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, productName, productQuantity };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      ownerStep = { ...owner, productName, productQuantity };
      onSelect(config.key, ownerStep, false, index);
    }
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [productName, productQuantity]);

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}

      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!productName || !productQuantity}
      >
        <div>
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
              optionKey="i18nKey" 
              t={t} 
              />
            )}
          />

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
            ValidationRequired={false}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9]*$",
              type: "text",
              title: t("EWASTE_NUMBER_ERROR_MESSAGE"),
            })}
          />


          <CardLabel>{`${t("EWASTE_UNIT_PRICE")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="productPrice"
            value={productName.price}
            disable={true}
            //  onChange={setproductPrice}
            style={{ width: "86%" }}
            ValidationRequired={false}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9]*$",
              type: "text",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />


        </div>
        <SubmitBar label="Add Product" style={{ marginBottom: "10px" }} onSubmit={handleAddProduct} />
      </FormStep>

      <div>
        <ProductList
          t={t}
          prlistName={prlistName}
          setPrlistName={setPrlistName}
          prlistQuantity={prlistQuantity}
          setPrlistQuantity={setPrlistQuantity}
          // prlistTotalprice={prlistTotalprice}
        />
      </div>
    </React.Fragment>
  );
};

export default EWProductDetails;
