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

// -----------------------------------------------------------------
// INLINE STYLE FALLBACKS
// These mirror the rules in ndc.css. They're applied directly via the
// `style` prop so the layout still works even if:
//   - the digit-ui-react-components library doesn't forward `className`
//     to the actual rendered DOM node, or
//   - ndc.css hasn't loaded/parsed yet, or
//   - some other stylesheet's specificity is winning.
// Inline styles always win (short of `!important` elsewhere), so this
// guarantees the sizing/spacing shows up regardless of the cause.
// -----------------------------------------------------------------
const styles = {
  container: {
    width: "100%",
    boxSizing: "border-box",
  },
  field: {
    width: "100%",
    maxWidth: 340,
    marginBottom: 22,
    boxSizing: "border-box",
  },
  label: {
    display: "block",
    width: "100%",
    marginBottom: 8,
    fontSize: 13,
    fontWeight: 600,
    color: "#111111",
    lineHeight: 1.4,
    boxSizing: "border-box",
  },
  required: {
    color: "#a82227",
    marginLeft: 3,
  },
  control: {
    width: "100%",
    boxSizing: "border-box",
  },
  dropdown: {
    width: 340,
    maxWidth: "100%",
    height: 40,
    minHeight: 40,
    boxSizing: "border-box",
  },
  input: {
    display: "block",
    width: 340,
    maxWidth: "100%",
    height: 40,
    minHeight: 40,
    margin: 0,
    boxSizing: "border-box",
  },
  errorMessage: {
    marginTop: 5,
  },
};

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

  return (
    <div className="ndc-reason-container" style={styles.container}>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}

      {/* =====================================================
          REASON
      ===================================================== */}

      <div className="ndc-reason-field" style={styles.field}>
        <CardLabel className="card-label-smaller ndc_card_labels ndc-reason-label" style={styles.label}>
          {t("NDC_NEW_NDC_APPLICATION_NDC_REASON")}

          <span className="ndc-required" style={styles.required}>
            *
          </span>
        </CardLabel>

        <div className="ndc-reason-control" style={styles.control}>
          <Controller
            name="NDCReason"
            rules={{
              required: t("REQUIRED_FIELD"),
            }}
            defaultValue={ndcReason}
            control={control}
            render={({ field }) => (
              <Dropdown
                className="ndc-reason-dropdown"
                style={styles.dropdown}
                selected={field.value}
                option={ndcReasonOptions}
                select={(e) => {
                  setNDCReason(e);
                  field.onChange(e);
                }}
                optionKey="i18nKey"
                onBlur={field.onBlur}
                t={t}
                searchable={false}
              />
            )}
          />
        </div>

        <CardLabelError className="ndc-card-label-error ndc-error-message" style={styles.errorMessage}>
          {localFormState.touched?.NDCReason ? localFormState.errors?.NDCReason?.message : ""}
        </CardLabelError>
      </div>

      {/* =====================================================
          OTHER REASON
      ===================================================== */}

      {watch("NDCReason")?.code === "OTHERS" && (
        <div className="ndc-reason-field" style={styles.field}>
          <CardLabel className="card-label-smaller ndc_card_labels ndc-reason-label" style={styles.label}>
            {t("Reason")}
          </CardLabel>

          <div className="ndc-reason-control" style={styles.control}>
            <Controller
              control={control}
              name="reason"
              defaultValue={ndcReason?.reason || ""}
              render={({ field }) => (
                <TextInput
                  className="ndc-reason-input"
                  style={styles.input}
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
