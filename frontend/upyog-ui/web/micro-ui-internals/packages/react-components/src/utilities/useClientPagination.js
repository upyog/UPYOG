/**
 * useClientPagination.js
 *
 * Client-side pagination + sort state on top of react-hook-form.
 * Use when the search API returns the full list and the UI pages / sorts locally
 * (as opposed to server-side offset/limit round-trips).
 *
 * @param {object}  options
 * @param {object}  [options.defaultValues] Merged over DEFAULT_SEARCH_PAGINATION.
 * @param {array}   [options.data]          Full result list from the API.
 * @param {number}  [options.count=0]       Total records (guards next-page).
 * @param {boolean} [options.isCleared]     When true, table shows empty (clear search).
 * @returns {object} RHF helpers + paginatedData + pagination props for tables.
 *
 * @see DEFAULT_SEARCH_PAGINATION
 * @see paginateArray
 */

import { useCallback, useEffect, useMemo } from "react";
import { useForm } from "react-hook-form";
import { DEFAULT_SEARCH_PAGINATION, paginateArray } from "./searchUtils";

/**
 * Hook that owns offset/limit/sortBy/sortOrder and slices `data` for the table.
 *
 * @param {object} options — see file-level docs.
 * @returns {{
 *   register: Function,
 *   handleSubmit: Function,
 *   setValue: Function,
 *   getValues: Function,
 *   reset: Function,
 *   watch: Function,
 *   resetToDefaults: Function,
 *   offset: number,
 *   limit: number,
 *   paginatedData: array,
 *   pagination: object
 * }}
 */
const useClientPagination = ({
  defaultValues = {},
  data,
  count = 0,
  isCleared = false,
} = {}) => {
  /**
   * Merged RHF defaults (pagination constants + caller overrides).
   * @returns {object}
   */
  const defaults = useMemo(
    () => ({ ...DEFAULT_SEARCH_PAGINATION, ...defaultValues }),
    [defaultValues]
  );

  const { register, handleSubmit, setValue, getValues, reset, watch } = useForm({
    defaultValues: defaults,
  });

  /**
   * Registers pagination fields with RHF so watch/setValue work without inputs.
   */
  useEffect(() => {
    register("offset");
    register("limit");
    register("sortBy");
    register("sortOrder");
  }, [register]);

  const offset = watch("offset") || 0;
  const limit = watch("limit") || defaults.limit || 10;
  const sortBy = watch("sortBy") || defaults.sortBy;
  const sortOrder = watch("sortOrder") || defaults.sortOrder;

  /**
   * Current page slice of `data`, or [] when cleared / non-array.
   * @returns {array}
   */
  const paginatedData = useMemo(() => {
    if (isCleared || !Array.isArray(data)) return [];
    return paginateArray(data, offset, limit);
  }, [data, offset, limit, isCleared]);

  /**
   * Table sort handler — expects [{ id, desc }] style args (Digit Table).
   * @param {array} args
   */
  const onSort = useCallback(
    (args) => {
      if (!args?.length) return;
      setValue("sortBy", args.id);
      setValue("sortOrder", args.desc ? "DESC" : "ASC");
    },
    [setValue]
  );

  /**
   * Page-size select change; resets offset to 0.
   * @param {Event} e
   */
  const onPageSizeChange = useCallback(
    (e) => {
      setValue("limit", Number(e.target.value));
      setValue("offset", 0);
    },
    [setValue]
  );

  /** Advance offset by page size when another page exists (offset + limit < count). */
  const nextPage = useCallback(() => {
    const pageSize = getValues("limit") || defaults.limit || 10;
    const next = (getValues("offset") || 0) + pageSize;
    if (next < count) setValue("offset", next);
  }, [getValues, setValue, count, defaults.limit]);

  /** Move offset back by page size when not on the first page. */
  const previousPage = useCallback(() => {
    const pageSize = getValues("limit") || defaults.limit || 10;
    const prev = (getValues("offset") || 0) - pageSize;
    if (prev >= 0) setValue("offset", prev);
  }, [getValues, setValue, defaults.limit]);

  /** Reset RHF (and pagination) back to merged defaults. */
  const resetToDefaults = useCallback(() => {
    reset(defaults);
  }, [reset, defaults]);

  /**
   * Props bag commonly spread onto Digit search result tables.
   * @returns {object}
   */
  const pagination = useMemo(
    () => ({
      onPageSizeChange,
      currentPage: limit ? offset / limit : 0,
      onNextPage: nextPage,
      onPrevPage: previousPage,
      pageSizeLimit: limit,
      onSort,
      sortParams: [{ id: sortBy, desc: sortOrder === "DESC" }],
    }),
    [onPageSizeChange, offset, limit, nextPage, previousPage, onSort, sortBy, sortOrder]
  );

  return {
    register,
    handleSubmit,
    setValue,
    getValues,
    reset,
    watch,
    resetToDefaults,
    offset,
    limit,
    paginatedData,
    pagination,
  };
};

export default useClientPagination;
