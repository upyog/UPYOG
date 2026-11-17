import masters from "./worksMasters.json";

// The ONLY place that knows where dropdown data comes from.
// Today: the JSON file. Later: replace the body with an MDMS call; nothing else changes.
export async function loadOptions(spec) {
    if (spec.source !== "mdms") return [];
    return (masters[spec.master] ?? [])
        .filter((r) => r.active !== false)
        .map((r) => ({ value: r.code, label: r.name, parent: r.parent }));
}