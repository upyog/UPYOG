import { useEffect } from "react";

/**
 * Warns the user before leaving the page (refresh / close tab / external navigation)
 * while the form has unsaved changes.
 *
 * Usage: <UnsavedPrompt when={isDirty} message="You have unsaved changes" />
 */
const UnsavedPrompt = ({ when = false, message = "You have unsaved changes. Are you sure you want to leave?" }) => {
    useEffect(() => {
        if (!when) return undefined;

        const handleBeforeUnload = (event) => {
            event.preventDefault();
            event.returnValue = message; // required for Chrome; most browsers show their own text
            return message;
        };

        window.addEventListener("beforeunload", handleBeforeUnload);
        return () => window.removeEventListener("beforeunload", handleBeforeUnload);
    }, [when, message]);

    return null;
};

export default UnsavedPrompt;

