import { useMemo } from "react";
import { useLocation } from "react-router-dom";

/**
 * React Router v6 replacement for common `useRouteMatch()` usage where the
 * matched "module" base path is `/cnd-ui/{citizen|employee}/{moduleCode}`.
 */
export default function useModuleBasePath() {
  const { pathname } = useLocation();
  return useMemo(() => {
    const parts = pathname.split("/").filter(Boolean);
    let path = pathname;
    if ((parts[0] === "cnd-ui" || parts[0] === "upyog-ui") && parts.length >= 3 && (parts[1] === "citizen" || parts[1] === "employee")) {
      const base = parts[0];
      // Employee shell routes live under /cnd-ui/employee/user/... but the app match path is /cnd-ui/employee
      if (parts[1] === "employee" && parts[2] === "user") {
        path = `/${base}/employee`;
      } else {
        path = `/${parts.slice(0, 3).join("/")}`;
      }
    }
    return { path, url: path, isExact: pathname === path, params: {} };
  }, [pathname]);
}
