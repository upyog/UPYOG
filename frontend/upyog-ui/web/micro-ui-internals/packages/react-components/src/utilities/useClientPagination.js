import { useCallback, useEffect, useMemo } from "react";
import { useForm } from "react-hook-form";
import { DEFAULT_SEARCH_PAGINATION, paginateArray } from "./searchUtils";

/**
 * Client-side pagination + sort state on top of react-hook-form.
 * Use when the search API returns the full list and the UI pages locally.
 *
 * @param {object} options
 * @param {object} options.defaultValues - merged over DEFAULT_SEARCH_PAGINATION
 * @param {array}  options.data - full result list
 * @param {number} options.count - total records (for next-page guard)
 * @param {boolean} options.isCleared - when true, table shows empty
 */
const useClientPagination = ({
  defaultValues = {},
  data,
  count = 0,
  isCleared = false,
} = {}) => {
  const defaults = useMemo(
    () => ({ ...DEFAULT_SEARCH_PAGINATION, ...defaultValues }),
    [defaultValues]
  );

  const { register, handleSubmit, setValue, getValues, reset, watch } = useForm({
    defaultValues: defaults,
  });

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

  const paginatedData = useMemo(() => {
    if (isCleared || !Array.isArray(data)) return [];
    return paginateArray(data, offset, limit);
  }, [data, offset, limit, isCleared]);

  const onSort = useCallback(
    (args) => {
      if (!args?.length) return;
      setValue("sortBy", args.id);
      setValue("sortOrder", args.desc ? "DESC" : "ASC");
    },
    [setValue]
  );

  const onPageSizeChange = useCallback(
    (e) => {
      setValue("limit", Number(e.target.value));
      setValue("offset", 0);
    },
    [setValue]
  );

  const nextPage = useCallback(() => {
    const pageSize = getValues("limit") || defaults.limit || 10;
    const next = (getValues("offset") || 0) + pageSize;
    if (next < count) setValue("offset", next);
  }, [getValues, setValue, count, defaults.limit]);

  const previousPage = useCallback(() => {
    const pageSize = getValues("limit") || defaults.limit || 10;
    const prev = (getValues("offset") || 0) - pageSize;
    if (prev >= 0) setValue("offset", prev);
  }, [getValues, setValue, defaults.limit]);

  const resetToDefaults = useCallback(() => {
    reset(defaults);
  }, [reset, defaults]);

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
