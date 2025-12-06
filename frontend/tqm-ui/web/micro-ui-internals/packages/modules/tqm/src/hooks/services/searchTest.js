export const searchTest = async ({ id }) => {
  const response = await Digit.CustomService.getResponse({
    url: "/pqm-service/v1/_search",
    body: {
      pagination: {
        // sortBy: "id",
        // sortOrder: "ASC",
      },
      testSearchCriteria: {
        ids: [id],
      },
    },
  });
  const testResponse = response?.tests?.[0];

  return testResponse;
};
