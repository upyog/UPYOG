import { useState, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
// import mergeConfig from "../../config/mergeConfig";
import { StoreService } from "../services/molecules/Store/service";

export const useStore = ({ stateCode, moduleCode, language }) => {
  // Updated: TanStack Query v5 — useQuery no longer accepts positional args (key, fn, options).
// Now accepts single object with queryKey, queryFn, and options merged together.
  return useQuery({
    queryKey: ["store", stateCode, moduleCode, language],
    queryFn: () => StoreService.defaultData(stateCode, moduleCode, language)
  });
};

export const useInitStore = (stateCode, enabledModules) => {
  // Updated: TanStack Query v5 — useQuery no longer accepts positional args (key, fn, options).
// Now accepts single object with queryKey, queryFn, and options merged together.
  const { isLoading, error, isError, data } = useQuery({
    queryKey: ["initStore", stateCode, enabledModules],
    queryFn: () => StoreService.digitInitData(stateCode, enabledModules),
    staleTime: Infinity,
  });
  return { isLoading, error, isError, data };
};
