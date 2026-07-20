import React, { useState, useEffect, useMemo, useCallback, useRef } from "react";
import {
  CardLabel,
  LabelFieldPair,
  Dropdown,
  TextInput,
  CardLabelError,
  Loader,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import { useSelector } from "react-redux";
import { useTranslation } from "react-i18next";
import _ from "lodash";
import Timeline from "../components/NDCTimeline";

// This component is responsible for rendering the dropdown to select the reason for NDC application. 
// It fetches the list of reasons from MDMS and displays it in the dropdown. 
// If the user selects "OTHERS" as the reason, it renders a text input field to enter the reason. It also handles the validation for the dropdown and text input field.

function SelectNDCReason({ config, onSelect, userType, formData, setError, formState, clearErrors }) {
  const [ndcReason, setNDCReason] = useState(formData?.NDCReason || {});
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    getValues,
  } = useForm({
    defaultValues: formData?.NDCReason || {},
  });
  const { t } = useTranslation();
  const apiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.responseData);
  // const firstTimeRef = useRef(true);
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { data: menuList, isLoading } = Digit.Hooks.useCustomMDMS(tenantId, "NDC", [{ name: "Reasons" }]);
  const ndcReasonOptions = useMemo(() => {
    const MenuListOfReasons = [];
    if (menuList?.NDC?.Reasons?.length > 0) {
      menuList?.NDC?.Reasons?.map((val) => {
        MenuListOfReasons.push({
          i18nKey: val?.code,
          code: val?.code,
        });
      });
    }
    return MenuListOfReasons;
  }, [menuList]);

  useEffect(() => {
    onSelect("NDCReason", ndcReason, config);
  }, [ndcReason]);

  useEffect(() => {
    if (apiDataCheck && ndcReasonOptions?.length > 0) {
      // find the matching option from MDMS
      const matchedOption = ndcReasonOptions.find((opt) => opt?.code === apiDataCheck?.[0]?.reason);
      if (matchedOption) {
        setNDCReason(matchedOption);
        setValue("NDCReason", matchedOption); // update react-hook-form value
      }
    }
  }, [apiDataCheck, ndcReasonOptions]);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller ndc_card_labels">{`${t("NDC_NEW_NDC_APPLICATION_NDC_REASON")} * `}</CardLabel>
        <Controller
          name="NDCReason"
          rules={{ required: t("REQUIRED_FIELD") }}
          defaultValue={ndcReason}
          control={control}
          render={({ field }) => (
            <Dropdown
              className="form-field"
              selected={field.value}
              option={ndcReasonOptions}
              select={(e) => {
                setNDCReason(e);
                field.onChange(e);
              }}
              optionKey="i18nKey"
              onBlur={field.onBlur}
              t={t}
            />
          )}
        />
      </LabelFieldPair>
      <CardLabelError className="ndc-card-label-error">{localFormState.touched?.NDCReason ? localFormState.errors?.NDCReason?.message : ""}</CardLabelError>
      {/* Reason */}
      {watch("NDCReason")?.code == "OTHERS" && (
        <LabelFieldPair>
          <CardLabel className="card-label-smaller ndc_card_labels">{`${t("Reason")}`}</CardLabel>
          <div className="form-field">
            <Controller
              control={control}
              name={"reason"}
              defaultValue={ndcReason?.reason || ""}
              render={({ field }) => (
                <TextInput
                  value={field.value}
                  onChange={(e) => {
                    onSelect("NDCReason", { ...formData?.NDCReason, reason: e.target.value }, config);
                    field.onChange(e.target.value);
                  }}
                  onBlur={field.onBlur}
                />
              )}
            />
          </div>
        </LabelFieldPair>
      )}
    </div>
  );
}

export default SelectNDCReason;
