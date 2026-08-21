import "../../css/ndc.css";
import React, { useState, useEffect, useMemo } from "react";

import { CardLabel, Dropdown, TextInput, CardLabelError, Loader } from "@nudmcdgnpm/digit-ui-react-components";

import { useForm, Controller } from "react-hook-form";
import { useSelector } from "react-redux";
import { useTranslation } from "react-i18next";

import Timeline from "../components/NDCTimeline";

// This component renders the reason for applying for NDC.
// The layout is intentionally vertical:
// Label
// Dropdown
//
// If OTHERS is selected:
// Label
// TextInput

function SelectNDCReason({ config, onSelect, userType, formData, setError, formState, clearErrors }) {
  const [ndcReason, setNDCReason] = useState(formData?.NDCReason || {});

  const {
    control,
    formState: localFormState,
    watch,
    setValue,
  } = useForm({
    defaultValues: formData?.NDCReason || {},
  });

  const { t } = useTranslation();

  const apiDataCheck = useSelector((state) => state.ndc.NDCForm?.formData?.responseData);

  const tenantId = Digit.ULBService.getCurrentTenantId();

  /*
   * ---------------------------------------------------------
   * FETCH REASONS FROM MDMS
   * ---------------------------------------------------------
   */

  const { data: menuList, isLoading } = Digit.Hooks.useCustomMDMS(tenantId, "NDC", [{ name: "Reasons" }]);

  /*
   * ---------------------------------------------------------
   * REASON OPTIONS
   * ---------------------------------------------------------
   */

  const ndcReasonOptions = useMemo(() => {
    const MenuListOfReasons = [];

    if (menuList?.NDC?.Reasons?.length > 0) {
      menuList.NDC.Reasons.forEach((val) => {
        MenuListOfReasons.push({
          i18nKey: val?.code,
          code: val?.code,
        });
      });
    }

    return MenuListOfReasons;
  }, [menuList]);

  /*
   * ---------------------------------------------------------
   * SEND DATA TO PARENT
   * ---------------------------------------------------------
   */

  useEffect(() => {
    onSelect("NDCReason", ndcReason, config);
  }, [ndcReason]);

  /*
   * ---------------------------------------------------------
   * LOAD EXISTING REASON FROM API
   * ---------------------------------------------------------
   */

  useEffect(() => {
    if (apiDataCheck && ndcReasonOptions?.length > 0) {
      const matchedOption = ndcReasonOptions.find((opt) => opt?.code === apiDataCheck?.[0]?.reason);

      if (matchedOption) {
        setNDCReason(matchedOption);

        setValue("NDCReason", matchedOption);
      }
    }
  }, [apiDataCheck, ndcReasonOptions, setValue]);

  /*
   * ---------------------------------------------------------
   * LOADER
   * ---------------------------------------------------------
   */

  if (isLoading) {
    return <Loader />;
  }

  /*
   * ---------------------------------------------------------
   * INLINE STYLES
   * ---------------------------------------------------------
   */

  /*
   * ---------------------------------------------------------
   * UI
   * ---------------------------------------------------------
   */

  return (
    <div className="ndc-css-87310ad8"
      
    >
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}

      {/* =====================================================
          REASON
      ===================================================== */}

      <div className="ndc-css-d088c348" >
        <CardLabel className="card-label-smaller ndc_card_labels ndc-css-5b150134" >
          {t("NDC_NEW_NDC_APPLICATION_NDC_REASON")}

          <span className="ndc-css-2c30dc20"
            
          >
            *
          </span>
        </CardLabel>

        <div className="ndc-css-87310ad8" >
          <Controller
            name="NDCReason"
            rules={{
              required: t("REQUIRED_FIELD"),
            }}
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
        </div>

        <CardLabelError className="ndc-card-label-error ndc-css-c3d3dc34" >
          {localFormState.touched?.NDCReason ? localFormState.errors?.NDCReason?.message : ""}
        </CardLabelError>
      </div>

      {/* =====================================================
          OTHER REASON
      ===================================================== */}

      {watch("NDCReason")?.code === "OTHERS" && (
        <div className="ndc-css-d088c348" >
          <CardLabel className="card-label-smaller ndc_card_labels ndc-css-5b150134" >
            {t("Reason")}
          </CardLabel>

          <div className="ndc-css-87310ad8" >
            <Controller
              control={control}
              name="reason"
              defaultValue={ndcReason?.reason || ""}
              render={({ field }) => (
                <TextInput
                  value={field.value || ""}
                  onChange={(e) => {
                    const updatedReason = {
                      ...ndcReason,
                      reason: e.target.value,
                    };

                    setNDCReason(updatedReason);

                    onSelect("NDCReason", updatedReason, config);

                    field.onChange(e.target.value);
                  }}
                  onBlur={field.onBlur}
                />
              )}
            />
          </div>
        </div>
      )}
    </div>
  );
}

export default SelectNDCReason;
