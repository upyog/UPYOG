import { useQuery } from "@tanstack/react-query";
import { lookup, depsOf } from "./formEngine";

const EMPTY = [];

const fillDeep = (x, fn) =>
    typeof x === "string" ? x.replace(/\{(\w+)\}/g, (_, k) => fn(k))
        : Array.isArray(x) ? x.map((i) => fillDeep(i, fn))
            : x && typeof x === "object" ? Object.fromEntries(Object.entries(x).map(([k, v]) => [k, fillDeep(v, fn)]))
                : x;

/**
 * field.options is either
 *   - an array of { value, label, parent? }   → static list, no network, or
 *   - { source: "mdms" | "api", ... }         → loaded through loadOptions (returns [{ value, label, parent? }]).
 * field.dependsOn: only options whose `parent` equals the chosen parent value are shown.
 */
export function useOptions(field, values, scope, loadOptions) {
    const spec = field.options;
    const isStatic = Array.isArray(spec);
    const waiting = depsOf(field).some((k) => !lookup(values, scope, k));

    const resolved = !spec || isStatic ? null : fillDeep(spec, (k) => lookup(values, scope, k) ?? "");
    const isMdms = resolved?.source === "mdms";
    const request = isMdms ? { source: "mdms", master: resolved.master } : resolved;

    const { data = EMPTY, isLoading } = useQuery({
        queryKey: ["form-options", request],
        queryFn: async () => (await loadOptions(request)) ?? [],
        enabled: !!request && !!loadOptions && (isMdms || !waiting),
        staleTime: isMdms ? 60 * 60 * 1000 : 5 * 60 * 1000,
    });

    if (waiting) return { options: EMPTY, loading: false, waiting: true };

    let options = isStatic ? spec : request ? data : EMPTY;
    if (field.dependsOn) {
        const parent = lookup(values, scope, field.dependsOn);
        const parentNorm = String(parent ?? "").toUpperCase();
        options = options.filter((o) => String(o.parent ?? "").toUpperCase() === parentNorm);
    }
    if (isMdms && resolved.filterBy)
        options = options.filter((o) => Object.entries(resolved.filterBy).every(([k, v]) => o[k] === v));

    return { options, loading: isLoading, waiting: false };
}