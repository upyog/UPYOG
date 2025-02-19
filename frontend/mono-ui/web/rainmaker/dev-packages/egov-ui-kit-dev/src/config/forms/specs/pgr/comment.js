const formConfig = {
  name: "comment",
  fields: {
    comment: {
      id: "citizen-comment",
      jsonPath: "actionInfo[0].comments",
      hintText: "CS_COMMON_COMMENTS_PLACEHOLDER2",
      value: "",
      required: true,
    },
  },
  saveUrl: "/rainmaker-pgr/v1/requests/_update",
  redirectionRoute: "",
};

export default formConfig;
