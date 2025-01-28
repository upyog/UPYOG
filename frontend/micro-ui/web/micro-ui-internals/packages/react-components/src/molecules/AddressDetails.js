import React, { useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { CardLabel, TextInput,TextArea,Dropdown,FormStep } from "@upyog/digit-ui-react-components";  //imported all from our common library

/**
 * Common Address Details component so that developer can use it just by importing it accross the UPYOG.
 * all cities data is fetching from common Tenants hook and locality data is fetched in the same way.
 * 
 * Steps to Use it in your Module/Application  :-
 *    1. Import it in your Module.js file of your Application/Module from  @nudmcdgnpm/digit-ui-react-components library.
 *    2. Insert it inside your componet registry Function in the same Module.js file.
 *    3. Then simply In your Config.js file which is present in your comfig folder inside src, add the component name "AddressDetails" in components and key should be "address"
 * 
 *     example :- 
 *              {
                "route": "add url route here",
                "component": "AddressDetails",
                "withoutLabel": true,
                "key": "address",
                "type": "component",
                "nextStep": "add your next to be url route here",
                "isMandatory": true,
                "texts": {
                    "submitBarLabel": "COMMON_SAVE_NEXT",
                    "header": "ADDRESS_DETAILS",
                }
            },

      4. Test it, it will work.
      

      TODO: Need to check how to use Timeline functioality 
 */

const AddressDetails = ({t, config, onSelect, userType, formData,editdata,previousData}) => {
  //added userType, editData and previous data for the future reference.
  const { data: allCities, isLoading } = Digit.Hooks.useTenants();
  let validation = {};
  const user = Digit.UserService.getUser().info;
  const [pincode, setPincode] = useState(formData?.address?.pincode || "");
  const [city, setCity] = useState(formData?.address?.city || "");
  const [locality, setLocality] = useState(formData?.address?.locality || "");
  const [houseNo, setHouseNo] = useState(formData?.address?.houseNo || "");
  const [streetName, setstreetName] = useState(formData?.address?.streetName || "");
  const [landmark, setLandmark] = useState(formData?.address?.landmark || "");
  const [addressLine1, setAddressLine1] = useState(formData?.address?.addressLine1 || "");
  const [addressLine2, setAddressLine2] = useState(formData?.address?.addressLine2 || "");
  const { control } = useForm();
  const inputStyles = {width:user.type === "EMPLOYEE" ? "50%" : "86%"};
  
  const { data: fetchedLocalities, isLoading: isLoadingLocalities } = Digit.Hooks.useBoundaryLocalities(
    city?.code,
    "revenue",
    {
      enabled: !!city,
    },
    t
  );

  let structuredLocality = [];
  fetchedLocalities &&
    fetchedLocalities.map((local, index) => {
      structuredLocality.push({ i18nKey: local.i18nkey, code: local.code, label: local.label, area: local.area, boundaryNum: local.boundaryNum });
    });
  
  const goNext = () => {
    let ownerAddress = formData.address;
    let addressStep = { ...ownerAddress, pincode, city, locality, houseNo, landmark, addressLine1, addressLine2, streetName };
    onSelect(config.key, { ...formData[config.key], ...addressStep }, false);
  };
  return (
    <React.Fragment>
        <FormStep
        config={config}
        onSelect={goNext}
        t={t}
        isDisabled={!houseNo || !city || !locality || !pincode || !addressLine1||!streetName||!addressLine2}
        >
    <div>
      <CardLabel> {`${t("HOUSE_NO")}`} <span className="check-page-link-button">*</span></CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="houseNo"
        value={houseNo}
        style={inputStyles}
        placeholder={"Enter House No"}
        onChange={(e) => {
            setHouseNo(e.target.value);
        }}
        ValidationRequired={true}
        validation={{
          isRequired: true,
          pattern: "^[a-zA-Z0-9 ,\\-]+$",
          type: "text",
          title: t("HOUSE_NO_ERROR_MESSAGE"),
        }}
      />

      <CardLabel>{`${t("STREET_NAME")}`} <span className="check-page-link-button">*</span></CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="streetName"
        value={streetName}
        style={inputStyles}
        placeholder={"Enter Street Name"}
        onChange={(e) => {
          setstreetName(e.target.value);
        }}
        ValidationRequired={true}
        validation={{
          pattern: "^[a-zA-Z0-9 ,\\-]+$",
          type: "text",
          title: t("STREET_NAME_ERROR_MESSAGE"),
        }}
      />

      <CardLabel>{`${t("ADDRESS_LINE1")}`} <span className="check-page-link-button">*</span></CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="addressLine1"
        value={addressLine1}
        style={inputStyles}
        placeholder={"Enter Address"}
        onChange={(e) => {
          setAddressLine1(e.target.value);
        }}
        ValidationRequired={false}
        {...(validation = {
          isRequired: false,
          pattern: "^[a-zA-Z,-/ ]*$",
          type: "textarea",
          title: t("ADDRESS_ERROR_MESSAGE"),
        })}
      />

      <CardLabel>{`${t("ADDRESS_LINE2")}`} <span className="check-page-link-button">*</span></CardLabel>
      <TextInput
        t={t}
        type={"text"}
        isMandatory={false}
        optionKey="i18nKey"
        name="addressLine2"
        value={addressLine2}
        style={inputStyles}
        placeholder={"Enter Address"}
        onChange={(e) => {
         setAddressLine2(e.target.value);
        }}
        ValidationRequired={false}
        {...(validation = {
          isRequired: false,
          pattern: "^[a-zA-Z,-/ ]*$",
          type: "textarea",
          title: t("ADDRESS_ERROR_MESSAGE"),
        })}
      />

      <CardLabel>{`${t("LANDMARK")}`}</CardLabel>
      <TextInput
        t={t}
        type={"textarea"}
        isMandatory={false}
        optionKey="i18nKey"
        name="landmark"
        value={landmark}
        style={inputStyles}
        placeholder={"Enter Landmark"}
        onChange={(e) => {
          setLandmark(e.target.value);
        }}
        ValidationRequired={true}
        validation={{
          isRequired: false,
          pattern: "^[a-zA-Z0-9 ]+$",
          type: "textarea",
          title: t("LANDMARK_ERROR_MESSAGE"),
        }}
      />

      <CardLabel>{`${t("CITY")}`} <span className="check-page-link-button">*</span></CardLabel>
      <Controller
        control={control}
        name={"city"}
        defaultValue={city}
        rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
        render={(props) => (
          <Dropdown
            className="form-field"
            selected={city}
            select={setCity}
            option={allCities}
            optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
            optionKey="i18nKey"
            t={t}
            placeholder={"Select"}
          />
        )}
      />

      <CardLabel>{`${t("LOCALITY")}`} <span className="check-page-link-button">*</span></CardLabel>
      <Controller
        control={control}
        name={"locality"}
        defaultValue={locality}
        rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
        render={(props) => (
          <Dropdown
            className="form-field"
            selected={locality}
            select={setLocality}
            option={structuredLocality}
            optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
            optionKey="i18nKey"
            t={t}
            placeholder={"Select"}
          />
        )}
      />

      <CardLabel>{`${t("PINCODE")}`} <span className="check-page-link-button">*</span></CardLabel>
      <TextInput
        t={t}
        type="text"
        isMandatory={false}
        optionKey="i18nKey"
        name="pincode"
        value={pincode}
        onChange={(e) => {
            setPincode(e.target.value);
          }}
        style={inputStyles}
        placeholder="Enter Pincode"
        ValidationRequired={true}
        validation={{
            required: false,
            pattern: "^[0-9]{6}$",
            type: "tel",
            title: t("SV_ADDRESS_PINCODE_INVALID"),
          }}
          maxLength={6}
      />
    </div>
    </FormStep>
    </React.Fragment>
  );
};

export default AddressDetails;
