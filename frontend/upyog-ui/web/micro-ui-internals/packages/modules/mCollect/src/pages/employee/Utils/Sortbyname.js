export const sortDropdownNames = (options, optionkey, locilizationkey) => {
  if (!options || !Array.isArray(options)) return [];
  return [...options].sort((a, b) => locilizationkey(a[optionkey]).localeCompare(locilizationkey(b[optionkey])));
};
