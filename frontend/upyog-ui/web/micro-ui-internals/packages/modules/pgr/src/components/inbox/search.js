import React from "react";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { TextInput, Label, SubmitBar, LinkLabel, ActionBar, CloseSvg } from "@nudmcdgnpm/digit-ui-react-components";

const SearchComplaint = ({ onSearch, type, onClose, searchParams }) => {
  const { register, handleSubmit, formState: { errors }, reset } = useForm({
    defaultValues: {
      serviceRequestId: searchParams?.search?.serviceRequestId || "",
      mobileNumber: searchParams?.search?.mobileNumber || "",
    }
  });
  const { t } = useTranslation();

  const { ref: serviceRequestIdRef, ...serviceRequestIdRegister } = register("serviceRequestId", {
    pattern: /(?!^$)([^\s])/,
  });

  const { ref: mobileNumberRef, ...mobileNumberRegister } = register("mobileNumber", {
    pattern: /^[6-9]\d{9}$/,
  });

  const onSubmitInput = (data) => {
    if (!Object.keys(errors).filter((i) => errors[i]).length) {
      if (data.serviceRequestId !== "") {
        onSearch({ serviceRequestId: data.serviceRequestId });
      } else if (data.mobileNumber !== "") {
        onSearch({ mobileNumber: data.mobileNumber });
      } else {
        onSearch({});
      }

      if (type === "mobile") {
        onClose();
      }
    }
  };

  function clearSearch() {
    reset({
      serviceRequestId: "",
      mobileNumber: "",
    });
    onSearch({});
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
                <h2> {t("CS_COMMON_SEARCH_BY")}:</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className="complaint-input-container" style={{ display: "grid" }}>
              <span className="complaint-input">
                <Label>{t("CS_COMMON_COMPLAINT_NO")}.</Label>
                <TextInput
                  inputRef={serviceRequestIdRef}
                  {...serviceRequestIdRegister}
                  style={{ marginBottom: "8px" }}
                ></TextInput>
              </span>
              <span className="mobile-input">
                <Label>{t("CS_COMMON_MOBILE_NO")}.</Label>
                <TextInput
                  inputRef={mobileNumberRef}
                  {...mobileNumberRegister}
                ></TextInput>
              </span>
              {type === "desktop" && (
                <SubmitBar
                  style={{ marginTop: 32, marginLeft: "16px", width: "calc( 100% - 16px )" }}
                  label={t("ES_COMMON_SEARCH")}
                  submit={true}
                  disabled={Object.keys(errors).filter((i) => errors[i]).length}
                />
              )}
            </div>
            {type === "desktop" && <span className="clear-search">{clearAll()}</span>}
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
