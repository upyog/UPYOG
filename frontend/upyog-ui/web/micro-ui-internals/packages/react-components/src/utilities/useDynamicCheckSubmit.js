import { useCallback, useState } from "react";

/**
 * Shared submit handler for config-driven check pages.
 * Validates routeConfig.form, builds payload, and runs a react-query mutation.
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
  const [isSubmitting, setIsSubmitting] = useState(false);

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
