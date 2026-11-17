import { useMemo } from "react";
import { useLocation } from "react-router-dom";

/**
 * React Router v6 replacement for common `useRouteMatch()` usage where the
 * matched "module" base path is `/works-ui/{citizen|employee}/{moduleCode}`.
 */
export default function useModuleBasePath() {
  const { pathname } = useLocation();
  return useMemo(() => {
    const parts = pathname.split("/").filter(Boolean);
    let path = pathname;
    const basePrefixes = ["works-ui", "upyog-ui", "digit-ui"];
    if (basePrefixes.includes(parts[0]) && parts.length >= 3 && (parts[1] === "citizen" || parts[1] === "employee")) {
      // Employee shell routes live under /works-ui/employee/user/... but the app match path is /works-ui/employee
      if (parts[1] === "employee" && parts[2] === "user") {
        path = `/${parts[0]}/employee`;
      } else {
        path = `/${parts.slice(0, 3).join("/")}`;
      }
    }
    return { path, url: path, isExact: pathname === path, params: {} };
  }, [pathname]);
}
