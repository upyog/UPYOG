import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, Dropdown, RadioOrSelect } from "@egovernments/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/EWASTETimeline";
import { Controller, useForm } from "react-hook-form";
import { SubmitBar } from "@egovernments/digit-ui-react-components";
import ProductList from "../components/EWASTEProductList";


const EWProductDetails = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
    const { pathname: url } = useLocation();
    let index = window.location.href.charAt(window.location.href.length - 1);
    let validation = {};

    const [productName, setProductName] = useState((formData.ewdet && formData.ewdet[index] && formData.ewdet[index]?.productName) || formData?.ewdet?.productName || "");
    const [productQuantity, setProductQuantity] = useState("1");
    const [productPrice, setProductPrice] = useState("23");

    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();

    // console.log("the productdetails component is running");

    // const { data: Menu } = Digit.Hooks.ptr.usePTRPetMDMS(stateId, "ewdetervice", "PetType");

    // const { data: Breed_Type } = Digit.Hooks.ptr.useBreedTypeMDMS(stateId, "ewdetervice", "BreedType");  // hooks for breed type

    // let menu = [];   //variable name for pettype
    // let breed_type = [];
    // variable name for breedtype

    // Menu &&
    //   Menu.map((petone) => {
    //     menu.push({ i18nKey: `PTR_PET_${petone.code}`, code: `${petone.code}`, value: `${petone.name}` });
    //   });

    const { control } = useForm();


    function setproductQuantity(e){
      setProductQuantity(e.target.value);
    }


    const PPrice = [
      {
        prod: "AC",
        price: "45"
      },
      {
        prod: "CPU",
        price: "79"
      },{
        prod: "VGA Cables",
        price: "21"
      },{
        prod: "Wires",
        price: "12"
      },{
        prod: "Monitor",
        price: "405"
      },
    ]

    function setproductPrice(e){
      setProductPrice(e.target.value);
    }

    // var t = 0;
    // PPrice[0].map((pre) => {
    //   if(pre.prod == productName.code){
    //     t = pre.price;
    //   }
    // })
    // setProductPrice(t)

    const productNamerough = [
      {
        code: "AC",
        i18nKey: "AC"
      },
      {
        code: "CPU",
        i18nKey: "CPU"
      },
      {
        code: "VGA Cables",
        i18nKey: "VGA Cables"
      },
      {
        code: "Wires",
        i18nKey: "Wires"
      },
      {
        code: "Monitor",
        i18nKey: "Monitor"
      },
      {
        code: "Keyboard",
        i18nKey: "Keyboard"
      },
      {
        code: "Mouse",
        i18nKey: "Mouse"
      }
    ]

    const [prlistName, setPrlistName] = useState([])
    const [prlistQuantity, setPrlistQuantity] = useState([])
    const [prlistTotalprice, setPrlistTotalprice] = useState([])

    const handleAddProduct = () =>{
      setPrlistName([...prlistName, {code: productName.code, i18nKey: productName.i18nKey}])
      setPrlistQuantity([...prlistQuantity, {code: productQuantity}])
      setPrlistTotalprice([...prlistTotalprice, {code: productPrice}])
      // prlist.map((pr) => {
      //   console.log("product is" + pr.code);
      // })
    }

    const goNext = () => {
      let owner = formData.ewdet && formData.ewdet[index];
      let ownerStep;
      if (userType === "citizen") {
        ownerStep = { ...owner, productName, productQuantity, productPrice };
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
      } else {

        ownerStep = { ...owner, productName, productQuantity, productPrice };
        onSelect(config.key, ownerStep, false, index);
      }
    };

    const onSkip = () => onSelect();


    // useEffect(() => {
    //   if (userType === "citizen") {
    //     goNext();
    //   }
    // }, [petType, breedType, petGender, petName, petAge, doctorName, lastVaccineDate]);

        useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [productName, productQuantity, productPrice]);


    return (
      <React.Fragment>
        {
          window.location.href.includes("/citizen") ?
            <Timeline currentStep={1} />
            : null
        }

        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          // isDisabled={!petType || !productName || !breedType || !petGender || !petName || !petAge || !doctorName || !clinicName || !lastVaccineDate || !vaccinationNumber}
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
                  option={productNamerough}
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


            <CardLabel>{`${t("EWASTE_TOTAL_PRICE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="productPrice"
              value={productPrice}
              disable={true}
              onChange={setproductPrice}
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
          <SubmitBar label="Add Product" style={{marginBottom: "10px"}} onSubmit = {handleAddProduct} />

        </FormStep>
        
        <div>
          <ProductList t={t} prlistName={prlistName} prlistQuantity={prlistQuantity} prlistTotalprice={prlistTotalprice} />
        </div>
      </React.Fragment>
    );
  };

export default EWProductDetails;