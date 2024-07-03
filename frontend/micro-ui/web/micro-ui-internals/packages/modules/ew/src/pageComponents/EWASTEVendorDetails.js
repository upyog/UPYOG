import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, LabelFieldPair, Dropdown, Menu, MobileNumber } from "@upyog/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/EWASTETimeline";

const EWVendorDetails
  = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
    const { pathname: url } = useLocation();

    let index = 0
    // window.location.href.charAt(window.location.href.length - 1);
    // console.log("index in detail page ",  index)

    let validation = {};

    const [vendor, setVendor] = useState(
      (formData.vendorKey && formData.vendorKey[index] && formData.vendorKey[index].vendor) || formData?.vendorKey?.vendor || ""
    );

    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
    const { control } = useForm();

    // static values are passed for vendor
    const roughvendor = [
      {
        code: "vendor a",
        i18nKey: "vendor a"
      },
      {
        code: "vendor b",
        i18nKey: "vendor b"
      },
      {
        code: "vendor c",
        i18nKey: "vendor c"
      },
      {
        code: "vendor d",
        i18nKey: "vendor d"
      }
    ]


    const goNext = () => {
      let owner = formData.vendorKey && formData.vendorKey[index];
      let ownerStep;
      if (userType === "citizen") {
        ownerStep = { ...owner, vendor};
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
      } else {

        ownerStep = { ...owner, vendor};
        onSelect(config.key, ownerStep, false, index);
      }
    };

    const onSkip = () => onSelect();




    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [vendor]);



    return (
      <React.Fragment>
        {
          window.location.href.includes("/citizen") ?
            <Timeline currentStep={5} />
            : null
        }

        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
        isDisabled={!vendor}
        >
          <div>

            <CardLabel>{`${t("EWASTE_VENDOR_NAME")}`}</CardLabel>
            <Controller
              control={control}
              name={"vendor"}
              defaultValue={vendor}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={vendor}
                  select={setVendor}
                  option={roughvendor}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />

          </div>

        </FormStep>
      </React.Fragment>
    );
  };

export default EWVendorDetails;