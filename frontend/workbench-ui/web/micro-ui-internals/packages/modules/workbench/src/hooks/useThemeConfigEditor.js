import { useState } from "react";
import { useTranslation } from "react-i18next";
import { deepSet, getInitialThemeConfig, submitThemeConfig } from "../utils";

/**
 * useThemeConfigEditor Custom Hook
 * 
 * WHY THIS WAS ADDED:
 * Previously, each customization form (ThemeCustomizeForm, OnBoardingContent, OnBoardingRegister, OnBoardingLogin)
 * repeated state definitions for tracking config drafts, toast alerts, confirmation modal states, hasUnsavedChanges detection,
 * local storage updates, and the submit API request handler.
 * 
 * Collapsing this shared logic here prevents code duplication (~40-50% overlap), maintains a single point of truth
 * for config writes, and enforces uniform save-confirmation UX patterns across all forms.
 */
export function useThemeConfigEditor() {
  const { t } = useTranslation();

  // State elements to manage active changes, baseline comparison state, toasts, and submission modals.
  const [config, setConfig] = useState(getInitialThemeConfig());
  const [lastSavedConfig, setLastSavedConfig] = useState(getInitialThemeConfig());
  const [toast, setToast] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  // Dirty check flag using string comparison. Prevents navigating away with unsaved inputs.
  const hasUnsavedChanges = JSON.stringify(config) !== JSON.stringify(lastSavedConfig);

  /**
   * Updates configuration values dynamically at the given dot-notation path.
   * Caches edits to localStorage on-the-fly for real-time draft persistence.
   * 
   * WHY THE PREFIX MAPPER EXISTS:
   * ThemeCustomizeForm settings paths (like primary colors or font sizing) do not have prefixes
   * and belong nested inside the `theme` object. Page onboarding configs already define absolute prefixes.
   * This logic maps paths to the correct structure dynamically.
   * 
   * @param {string} path - Dot-notation path to modify.
   * @param {*} value - New value to save.
   */
  const set = (path, value) => {
    const firstKey = path.split(".")[0];
    const finalPath = ["theme", "common", "pages"].includes(firstKey) ? path : `theme.${path}`;
    setConfig((prev) => {
      const next = deepSet(prev, finalPath, value);
      localStorage.setItem("UPYOG_THEME_CONFIG", JSON.stringify(next));
      return next;
    });
  };

  /**
   * Handles configuration submission.
   * Closes confirmation modals, hits the API mock route, updates comparison baselines, and displays toasts.
   */
  const handleSubmit = async () => {
    setShowConfirmModal(false);
    try {
      await submitThemeConfig(config);
      setLastSavedConfig(config);
      setToast({ label: t("Configuration updated and submitted successfully!"), error: false });
    } catch (err) {
      console.error("API submission failed:", err);
      setToast({ label: t("Failed to submit configuration to the API."), error: true });
    }
    setTimeout(() => setToast(null), 3000);
  };

  return {
    config,
    setConfig,
    lastSavedConfig,
    setLastSavedConfig,
    toast,
    setToast,
    showConfirmModal,
    setShowConfirmModal,
    hasUnsavedChanges,
    set,
    handleSubmit,
  };
}
