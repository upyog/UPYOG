const createService = async (testData, tenantId) => {
    try {
      const response = await Digit.CustomService.getResponse({
        url: "/pqm-service/v1/_create",
        body: {
          tests: testData,
        },
      });
      return response;
    } catch (error) {
      throw new Error(error?.response?.data?.Errors[0].message);
    }
  };
  
  export default createService;
  