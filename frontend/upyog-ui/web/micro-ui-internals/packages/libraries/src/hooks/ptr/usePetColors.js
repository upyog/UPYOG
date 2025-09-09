export const usePetColors = () => {
  return Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "PetService", [{ name: "PetColor" }],
  {
    select: (data) => {
      const formattedData = data?.["PetService"]?.["PetColor"].map((petone) => {
        return { i18nKey: `${petone.colourName}`, colourCode: `${petone.colourCode}`, code: `${petone.colourName}`, active: `${petone.active}` };
      })
      return formattedData;
    },
  });
};