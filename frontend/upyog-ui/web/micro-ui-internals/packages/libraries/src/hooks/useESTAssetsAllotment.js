/**
 * useESTAssetsAllotment
 * ---------------------
 * TanStack Query v5 mutation hook for creating an EST allotment.
 *
 * WHERE TO PUT THIS FILE:
 *   Wherever your current useESTAssetsAllotment lives — find it with:
 *     grep -rn "useESTAssetsAllotment" --include="*.js" . | grep -v node_modules
 *   Replace that file's contents with this (keeping its registration in
 *   the module's hooks index, e.g. Digit.Hooks.estate.useESTAssetsAllotment).
 *
 * WHY YOUR ORIGINAL LIKELY BROKE:
 *   Your mutation object showed `isPending` / `status: "pending"` / `context`,
 *   which means TanStack Query v5. v5 REMOVED the positional overload
 *   useMutation(fn, options) — hooks still written that way misbehave.
 *   v5 requires the object form used below.
 */

import { useMutation } from "@tanstack/react-query";
// ⚠️ ADAPT #2: fix this import path to wherever you place the service file
import ESTAllotmentService from "../services/ESTAllotmentService";

const useESTAssetsAllotment = (tenantId, config = {}) => {
  return useMutation({
    mutationFn: (payload) => ESTAllotmentService.createAllotment(payload, tenantId),
    ...config,
  });
};

export default useESTAssetsAllotment;
