import React from "react";
import { useForm, Controller } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg } from "@nudmcdgnpm/digit-ui-react-components";

const SearchComplaint = ({ onSearch, type, onClose, searchParams }) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm({
    defaultValues: {
      serviceRequestId: searchParams?.search?.serviceRequestId || "",
      mobileNumber: searchParams?.search?.mobileNumber || "",
    },
  });

  const { t } = useTranslation();

  const onSubmitInput = (data) => {
    if (data.serviceRequestId) {
      onSearch({ serviceRequestId: data.serviceRequestId });
    } else if (data.mobileNumber) {
      onSearch({ mobileNumber: data.mobileNumber });
    } else {
      onSearch({});
    }

    if (type === "mobile") {
      onClose();
    }
  };

  function clearSearch() {
    reset({
      serviceRequestId: "",
      mobileNumber: "",
    });

    onSearch({
      serviceRequestId: "",
      mobileNumber: "",
    });
  }

  const clearAll = () => {
    return (
      <LinkLabel className="clear-search-label" onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)} style={{ marginLeft: "24px" }}>
      <React.Fragment>
        <div className="search-container" style={{ width: "auto" }}>
          <div className="search-complaint-container">
            {type === "mobile" && (
              <div className="complaint-header">
                <h2>{t("CS_COMMON_SEARCH_BY")}:</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}

            <div className="complaint-input-container" style={{ display: "grid" }}>
              <span className="complaint-input">
                <Label>{t("CS_COMMON_COMPLAINT_NO")}.</Label>
                <Controller
                  name="serviceRequestId"
                  control={control}
                  rules={{
                    pattern: /(?!^$)([^\s])/,
                  }}
                  render={({ field }) => (
                    <TextInput
                      value={field.value}
                      onChange={(e) => field.onChange(e.target.value)}  // ✅ fixed
                      style={{ marginBottom: "8px" }}
                    />
                  )}
                />
              </span>

              <span className="mobile-input">
                <Label>{t("CS_COMMON_MOBILE_NO")}.</Label>
                <Controller
                  name="mobileNumber"
                  control={control}
                  rules={{
                    minLength: {
                      value: 10,
                      message: t("CORE_COMMON_MOBILE_ERROR"),
                    },
                    maxLength: {
                      value: 10,
                      message: t("CORE_COMMON_MOBILE_ERROR"),
                    },
                    pattern: {
                      value: /[6789][0-9]{9}/,
                      message: t("CORE_COMMON_MOBILE_ERROR"),
                    },
                  }}
                  render={({ field }) => (
                    <>
                      <TextInput
                        value={field.value}
                        onChange={(e) => {
                          const value = e.target.value.replace(/\D/g, ""); // strip non-digits
                          if (value.length <= 10) {                        // block beyond 10
                            field.onChange(value);
                          }
                        }}
                        maxLength={10}                           // fallback for native input
                        isInvalid={!!errors.mobileNumber}
                      />
                      {errors.mobileNumber && (
                        <span style={{ color: "red", fontSize: "12px" }}>
                          {errors.mobileNumber.message}
                        </span>
                      )}
                    </>
                  )}
                />
              </span>

              {type === "desktop" && (
                <SubmitBar
                  style={{
                    marginTop: 32,
                    marginLeft: "16px",
                    width: "calc( 100% - 16px )",
                  }}
                  label={t("ES_COMMON_SEARCH")}
                  submit={true}
                  disabled={Object.keys(errors).some((key) => errors[key])}
                />
              )}
            </div>

            {type === "desktop" && (
              <span className="clear-search">{clearAll()}</span>
            )}
          </div>
        </div>

        {type === "mobile" && (
          <ActionBar>
            <SubmitBar label="Search" submit={true} />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchComplaint;