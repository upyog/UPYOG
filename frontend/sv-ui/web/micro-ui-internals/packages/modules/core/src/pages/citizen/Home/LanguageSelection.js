import React, { useMemo } from "react";
import { PageBasedInput, Loader, RadioButtons, CardHeader } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import { useTranslation } from "react-i18next";

const LanguageSelection = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { data: { languages, stateInfo } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  const selectedLanguage = Digit.StoreData.getCurrentLanguage();
  const texts = useMemo(
    () => ({
      header: t("CS_COMMON_CHOOSE_LANGUAGE"),
      submitBarLabel: t("CORE_COMMON_CONTINUE"),
    }),
    [t]
  );

  const RadioButtonProps = useMemo(
    () => ({
      options: languages,
      optionsKey: "label",
      additionalWrapperClass: "reverse-radio-selection-wrapper",
      onSelect: (language) => Digit.LocalizationService.changeLanguage(language.value, stateInfo.code),
      selectedOption: languages?.filter((i) => i.value === selectedLanguage)[0],
    }),
    [selectedLanguage, languages]
  );

  function onSubmit() {
    navigate(`/sv-ui/citizen/select-location`);
  }

  return isLoading ? (
    <Loader />
  ) : (
    <div className="selection-card-wrapper">
      <PageBasedInput texts={texts} onSubmit={onSubmit}>
        <CardHeader>{t("CS_COMMON_CHOOSE_LANGUAGE")}</CardHeader>
        <RadioButtons {...RadioButtonProps} />
      </PageBasedInput>
    </div>
  );
};

export default LanguageSelection;
