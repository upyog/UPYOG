/** Steps that are unreachable — wizard uses /check instead. */
const SKIP_COMPONENTS = new Set(["ReviewDetails"]);

/**
 * Flatten MDMS wizard entries into routable steps (employee only).
 * Skips preview-only steps and unknown preview components.
 */
export const buildWizardSteps = (initialConfig, indexRoute) => {
  if (!initialConfig || !Array.isArray(initialConfig)) return [];

  const steps = initialConfig.reduce((acc, entry) => {
    if (!entry?.body) return acc;
    return acc.concat(
      entry.body.filter((step) => {
        if (step.hideInEmployee) return false;
        if (SKIP_COMPONENTS.has(step.component)) return false;
        if (step.isPreview && (!Array.isArray(step.form) || step.form.length === 0)) {
          return false;
        }
        return true;
      })
    );
  }, []);

  steps.indexRoute = indexRoute;
  return steps;
};

export const getWizardBasePath = (pathname, pathnameBase, terminalSegments) => {
  if (pathnameBase) return pathnameBase;

  const parts = pathname.split("/");
  const terminalIndex = parts.findIndex((p) => terminalSegments.includes(p));
  if (terminalIndex > 0) {
    return parts.slice(0, terminalIndex).join("/");
  }
  return parts.slice(0, -1).join("/");
};

/**
 * Shared goNext used by EST registration + allotment wizards.
 */
export const createWizardGoNext = ({ pathname, config, navigate, multiStep = true }) => {
  return (skipStep, index, isAddMultiple, key) => {
    let currentPath = pathname.split("/").pop();
    let isMultiple = false;

    if (multiStep) {
      const lastchar = currentPath.charAt(currentPath.length - 1);
      if (Number(parseInt(currentPath)) || currentPath === "0" || currentPath === "-1") {
        if (currentPath === "-1" || currentPath === "-2") {
          currentPath = pathname.slice(0, -3).split("/").pop();
        } else {
          currentPath = pathname.slice(0, -2).split("/").pop();
        }
        isMultiple = true;
      } else {
        isMultiple = false;
      }
      if (!Number.isNaN(Number(lastchar))) isMultiple = true;
    }

    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath) || {};

    let redirectWithHistory = (to, state) =>
      navigate(to, state != null ? { state } : undefined);

    if (skipStep) {
      redirectWithHistory = (to, state) =>
        navigate(to, state != null ? { replace: true, state } : { replace: true });
    }

    if (isAddMultiple) nextStep = key;
    if (nextStep === null) return redirectWithHistory("check");
    if (typeof nextStep !== "string") return redirectWithHistory("check");

    const nextPage =
      multiStep && !Number.isNaN(Number(nextStep.split("/").pop())) && nextStep !== "map"
        ? `${nextStep}`
        : isMultiple && nextStep !== "map"
          ? `${nextStep}/${index}`
          : `${nextStep}`;

    redirectWithHistory(nextPage);
  };
};
