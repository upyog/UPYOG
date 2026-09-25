import React, { use, useEffect, useState } from "react";
import { TextInput, CardLabel, Dropdown, TextArea, ActionBar, SubmitBar, LabelFieldPair } from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { Loader } from "../components/Loader";
import { parse, format } from "date-fns";

/**
 * OffenceDetails component:
 * - Captures offence-related details
 * - Uses dropdowns and inputs with react-hook-form
 */

const OffenceDetails = ({ onGoBack, goNext, currentStepData, t }) => {
  const [loader, setLoader] = useState(false);

  const tenantId = Digit.ULBService.getCurrentTenantId()

  const { data: categoryData, isLoading: categoryLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "Category" }]);
  const { data: subCategoryData, isLoading: subCategoryLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "SubCategory" }]);
  const { data: OffenceTypeData, isLoading: OffenceTypeLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "OffenceType" }]);

  const {
    control,
    handleSubmit,
    setValue,
    reset,
    formState: { errors },
    getValues,
  } = useForm({
    defaultValues: {
      shouldUnregister: false,
    },
  });

  const onSubmit = (data) => {
    goNext(data);
  };

  return (
    <React.Fragment>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div>
          {/* offence type */}
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">
              {t("CHB_PURPOSE_DESCRIPTION")} <span className="requiredField">*</span>
            </CardLabel>
            <Controller
              control={control}
              name={"offenceType"}
              defaultValue={null}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  select={props.onChange}
                  selected={props.value}
                  option={OffenceTypeData?.Challan?.OffenceType}
                  optionKey="name"
                  t={t}
                />
              )}
            />
            {errors.offenceType && <p className="requiredField">{errors.offenceType.message}</p>}
          </LabelFieldPair>

          {/* Offence Category */}
          <LabelFieldPair>
            <CardLabel>
              {t("CHALLAN_OFFENCE_CATEGORY")} <span className="requiredField">*</span>
            </CardLabel>
            <Controller
              control={control}
              name={"offenceCategory"}
              defaultValue={null}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  select={props.onChange}
                  selected={props.value}
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
              render={(props) => (
                <Dropdown
                  className="form-field"
                  select={props.onChange}
                  selected={props.value}
                  option={subCategoryData?.Challan?.SubCategory}
                  optionKey="name"
                  t={t}
                />
              )}
            />
            {errors.offenceSubCategory && <p className="requiredField">{errors.offenceSubCategory.message}</p>}
          </LabelFieldPair>

          {/* Challan Amount */}
          <LabelFieldPair className="challan-amount-field">
            <CardLabel>
              {`${t("CHALLAN_AMOUNT")}`} <span className="requiredField">*</span>
            </CardLabel>
            <Controller
              control={control}
              name="challanAmount"
              render={(props) => (
                <TextInput
                  value={props.value}
                  error={errors?.name?.message}
                  onChange={(e) => {
                    props.onChange(e.target.value);
                  }}
                  onBlur={(e) => {
                    props.onBlur(e);
                  }}
                  t={t}
                />
              )}
            />
            {errors?.challanAmount && <p className="requiredField">{errors.challanAmount.message}</p>}
          </LabelFieldPair>
        </div>
        <ActionBar>
          <SubmitBar className="submit-bar-back" label="Back" onSubmit={onGoBack} />
          <SubmitBar label="Next" submit="submit" />
        </ActionBar>
      </form>
      {(loader || categoryLoading || subCategoryLoading || OffenceTypeLoading) && <Loader page={true} />}
    </React.Fragment>
  );
};

export default OffenceDetails;
