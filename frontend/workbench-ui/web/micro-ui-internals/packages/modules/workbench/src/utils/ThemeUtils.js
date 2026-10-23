import React from "react";
import { CitizenThemeConfig } from "../configs/CitizenThemeConfig";
import {
  ThemeTextIcon,
  ThemeBrandIcon,
  ThemeCommonIcon,
  ThemeBackgroundIcon,
  ThemeBorderIcon,
  ThemeDividerIcon,
  ThemeShadowsIcon,
  ThemeRadiusIcon,
  ThemeSidebarIcon,
  ThemeHeaderIcon,
  ThemeGradientsIcon,
  ThemeTypographyIcon,
  ThemeLogoIcon
} from "@upyog/workbench-ui-react-components";

/**
 * Formats camelCase or snake_case object keys into human-readable capitalized labels.
 * E.g., "primaryColor" -> "Primary Color", "border_radius" -> "Border Radius".
 * 
 * @param {string} key - The object property key.
 * @returns {string} The formatted label.
 */
export function formatLabel(key) {
  return key
    .replace(/([A-Z])/g, " $1")
    .replace(/[_-]/g, " ")
    .replace(/^\w/, (c) => c.toUpperCase())
    .replace(/\s+/g, " ")
    .trim();
}

// Custom display overrides for configuration keys to align with Figma terminology.
const labelOverrides = {
  "brand.primary": "Default Color",
  "divider.primary": "Input Color",
};

/**
 * Gets a clean user-facing label for color inputs.
 * Falls back to formatLabel if no explicit override exists.
 * 
 * @param {string} groupKey - The parent group identifier (e.g. "brand").
 * @param {string} colorKey - The sub-key representing the color.
 * @returns {string} The display label.
 */
export function getLabel(groupKey, colorKey) {
  const path = `${groupKey}.${colorKey}`;
  if (labelOverrides[path]) {
    return labelOverrides[path];
  }
  return `${formatLabel(colorKey)} Color`;
}

/**
 * Resolves the corresponding React SVG icon component based on the key name.
 * 
 * @param {string} key - Key identifying the card section.
 * @returns {React.Component|null} The vector SVG component.
 */
export const getCardIcon = (key) => {
  const iconComponents = {
    text: <ThemeTextIcon />,
    brand: <ThemeBrandIcon />,
    common: <ThemeCommonIcon />,
    background: <ThemeBackgroundIcon />,
    border: <ThemeBorderIcon />,
    divider: <ThemeDividerIcon />,
    shadows: <ThemeShadowsIcon />,
    borderRadius: <ThemeRadiusIcon />,
    sidebar: <ThemeSidebarIcon />,
    header: <ThemeHeaderIcon />,
    gradients: <ThemeGradientsIcon />,
    typography: <ThemeTypographyIcon />,
    logo: <ThemeLogoIcon />
  };
  return iconComponents[key] || null;
};

// Meta definitions (icon, title, description) for the standard theme color sections.
export const groupMeta = {
  text: { icon: getCardIcon("text"), title: "Text Colors", description: "Color used for text and content" },
  brand: { icon: getCardIcon("brand"), title: "Brand Colors", description: "Brand identity and primary colors" },
  common: { icon: getCardIcon("common"), title: "Common Color", description: "Common used for text and content" },
  background: { icon: getCardIcon("background"), title: "Background Color", description: "Backgrounds for layout and components" },
  border: { icon: getCardIcon("border"), title: "Border Colors", description: "Borders and outlines" },
  divider: { icon: getCardIcon("divider"), title: "Divider", description: "Borders and outlines" },
};

/**
 * Reusable helper to load the initial theme config from localStorage or fallback defaults.
 * 
 * @returns {Object} The resolved configuration state.
 */
export function getInitialThemeConfig() {
  const saved = localStorage.getItem("UPYOG_THEME_CONFIG");
  if (saved) {
    try {
      return JSON.parse(saved);
    } catch (e) {
      console.error("Failed to parse saved theme config:", e);
    }
  }
  return CitizenThemeConfig[0] || {};
}

/**
 * Reusable helper to post configuration changes to the server API and cache them.
 * 
 * @param {Object} config - The theme config object to save.
 * @returns {Promise<boolean>} True if submission succeeded.
 */
export async function submitThemeConfig(config) {
  localStorage.setItem("UPYOG_THEME_CONFIG", JSON.stringify(config));
  const response = await fetch("https://jsonplaceholder.typicode.com/posts", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      title: "Theme Config Update",
      body: config,
      userId: 1,
    }),
  });
  if (!response.ok) {
    throw new Error("API responded with error status");
  }
  return true;
}
