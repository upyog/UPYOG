import React, { useMemo } from "react";
import defaultTheme from "./theme.json";

// { color: { primary: "#fff" } }  →  [["--ui-color-primary", "#fff"]]
const flatten = (obj, prefix = "--ui") =>
    Object.entries(obj).flatMap(([k, v]) =>
        typeof v === "object" ? flatten(v, `${prefix}-${k}`) : [[`${prefix}-${k}`, v]]
    );

export default function ThemeProvider({ theme = defaultTheme, children }) {
    const style = useMemo(
        () => ({
            ...Object.fromEntries(flatten(theme)),
            fontFamily: "var(--ui-font-family)",
            fontSize: "var(--ui-font-size)",
            color: "var(--ui-color-text)",
        }),
        [theme]
    );
    return (
        <div className="ui-theme" style={style}>
            {children}
        </div>
    );
}