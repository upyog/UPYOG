/**
 * useDynamicCheckSubmit.js
 *
 * Shared submit handler for config-driven check pages (e.g. DynamicCheckPage
 * wrappers). Validates that routeConfig.form exists (optional), builds the API
 * payload via a caller-supplied buildPayload, and runs a react-query mutation.
 *
 * @param {object}   options
 * @param {object}   options.routeConfig       Merged MDMS + local route config.
 * @param {Function} options.buildPayload      () => API payload object.
 * @param {object}   options.mutation          react-query mutation with .mutate.
 * @param {Function} [options.onSubmit]        Called with mutation response on success.
 * @param {Function} [options.onError]         Called on validation or mutation error.
 * @param {boolean}  [options.validateForm=true] Refuse submit when form config is empty.
 * @param {string}   [options.logTag]          Prefix for console.error messages.
 * @returns {{ isSubmitting: boolean, handleSubmit: Function }}
 *
 * @example
 *   const { isSubmitting, handleSubmit } = useDynamicCheckSubmit({
 *     routeConfig,
 *     buildPayload: () => buildApiPayload(routeConfig, formValues, tenantId),
 *     mutation: createMutation,
 *     onSubmit: goToAcknowledgement,
 *   });
 */

import { useCallback, useState } from "react";

/**
 * Wires declaration/submit UX to a mutation with loading and error handling.
 *
 * @param {object} options — see file-level docs.
 * @returns {{ isSubmitting: boolean, handleSubmit: Function }}
 */
const useDynamicCheckSubmit = ({
  routeConfig,
  buildPayload,
  mutation,
  onSubmit,
  onError,
  validateForm = true,
  logTag = "DYNAMIC_CHECK",
}) => {
  /** True while mutation.mutate is in flight (guards double-submit). */
  const [isSubmitting, setIsSubmitting] = useState(false);

  /**
   * Final check-page submit.
   * Optionally refuses empty routeConfig.form, builds payload, then mutates.
   * onSubmit errors after a successful API call are logged only (mutation already succeeded).
   */
  const handleSubmit = useCallback(() => {
    if (isSubmitting) return;

    if (validateForm && (!Array.isArray(routeConfig?.form) || routeConfig.form.length === 0)) {
      console.error(`${logTag}: routeConfig.form is empty — refusing to submit`);
      onError?.(new Error(`${logTag}: empty form config`));
      return;
    }

    const payload = buildPayload();
    setIsSubmitting(true);

    mutation.mutate(payload, {
      onSuccess: (response) => {
        setIsSubmitting(false);
        try {
          onSubmit?.(response);
        } catch (err) {
          // API already succeeded — log only; onCheckSuccess itself must not throw.
          console.error(`${logTag}: onSubmit failed after successful mutation`, err);
        }
      },
      onError: (error) => {
        console.error(`${logTag} error:`, error?.response?.data || error);
        setIsSubmitting(false);
        onError?.(error);
      },
    });
  }, [isSubmitting, routeConfig, buildPayload, mutation, onSubmit, onError, validateForm, logTag]);

  return { isSubmitting, handleSubmit };
};

export default useDynamicCheckSubmit;
