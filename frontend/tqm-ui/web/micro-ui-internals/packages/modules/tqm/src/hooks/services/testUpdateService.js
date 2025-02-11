const testUpdateService = async (testData, tenantId) => {
  try {
    const response = await Digit.CustomService.getResponse({
      url: "/pqm-service/v1/_update",
      body: {
        tests: [testData],
      },
    });
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default testUpdateService;
