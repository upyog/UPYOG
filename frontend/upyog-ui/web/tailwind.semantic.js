const plugin = require("tailwindcss/plugin");

console.log("Semantic plugin loaded");

module.exports = plugin(function ({ addUtilities, theme }) {
  addUtilities({
    /* ===========================
       Typography
    =========================== */

    ".text-heading-xl": {
      fontSize: "32px",
      lineHeight: "40px",
    },

    ".text-heading-l": {
      fontSize: "24px",
      lineHeight: "32px",
    },

    ".text-heading-m": {
      fontSize: "20px",
      lineHeight: "28px",
    },

    ".text-heading-s": {
      fontSize: "18px",
      lineHeight: "24px",
    },

    ".text-body-l": {
      fontSize: "16px",
      lineHeight: "24px",
    },

    ".text-body-m": {
      fontSize: "14px",
      lineHeight: "20px",
    },

    ".text-body-s": {
      fontSize: "12px",
      lineHeight: "16px",
    },

    ".text-caption-xl": {
      fontSize: "18px",
      lineHeight: "24px",
    },

    ".text-caption-l": {
      fontSize: "14px",
      lineHeight: "20px",
    },

    ".text-caption-m": {
      fontSize: "12px",
      lineHeight: "16px",
    },

    /* ===========================
       Desktop variants
    =========================== */

    ".text-heading-xl-dt": {
      fontSize: "40px",
      lineHeight: "48px",
    },

    ".text-heading-l-dt": {
      fontSize: "32px",
      lineHeight: "40px",
    },

    ".text-heading-m-dt": {
      fontSize: "24px",
      lineHeight: "32px",
    },

    ".text-heading-s-dt": {
      fontSize: "20px",
      lineHeight: "28px",
    },

    ".text-body-l-dt": {
      fontSize: "18px",
      lineHeight: "24px",
    },

    ".text-body-s-dt": {
      fontSize: "14px",
      lineHeight: "20px",
    },

    ".text-caption-xl-dt": {
      fontSize: "20px",
      lineHeight: "28px",
    },

    ".text-caption-m-dt": {
      fontSize: "14px",
      lineHeight: "20px",
    },

    /* ===========================
       Semantic text styles
    =========================== */

    ".text-link": {
      textDecoration: "underline",
    },

    ".text-form-field": {
      fontSize: "16px",
      lineHeight: "24px",
      fontWeight: "500",
    },

    ".text-navbarheader": {
      fontSize: "20px",
      lineHeight: "24px",
      fontWeight: "700",
    },

    ".text-legend": {
      fontSize: "19px",
      lineHeight: "32px",
      fontWeight: "500",
    },

    ".text-text-btn": {
      fontSize: "16px",
      lineHeight: "20px",
      fontWeight: "500",
    },

    /* ===========================
       Shadows
    =========================== */

    ".shadow-card": {
      boxShadow: "0 1px 4px rgba(0,0,0,0.15)",
    },
  });
});