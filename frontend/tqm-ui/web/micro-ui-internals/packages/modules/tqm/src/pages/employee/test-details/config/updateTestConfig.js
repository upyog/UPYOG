export const updateConfig = ({ t, testLabs }) => {
  return [
    {
      head: t("ES_TQM_SELECT_SAMPLE_TO_LAB_LABEL"),
      body: [
        {
          label: t("ES_TQM_SELECT_LAB_LABEL"),
          isMandatory: true,
          key: "status",
          type: "dropdown",
          disable: false,
          populators: {
            name: "status",
            optionsKey: "i18nKey",
            error: t("ES_TQM_SELECT_LAB_LABEL_ERROR"),
            required: true,
            options: testLabs?.length >=0 ? testLabs:[],
          },
        },
      ],
    },
  ];
};
