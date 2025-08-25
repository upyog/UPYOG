import React, { useContext } from "react";

/**
 
 * @example
 *         const { privacy , updatePrivacy } = Digit.Hooks.usePrivacyContext()
 *
 * @returns {Object} Returns the object which contains privacy value and updatePrivacy method
 */
export const usePrivacyContext = () => {
  const { privacy, updatePrivacy, ...rest } = useContext(Digit.Contexts.PrivacyProvider);
  return { privacy, updatePrivacy, ...rest };
};
