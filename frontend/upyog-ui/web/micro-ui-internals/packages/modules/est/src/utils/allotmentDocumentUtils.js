/**
 * Shared helpers for allotment document preview (check page + acknowledgement).
 */
import {
  extractUrlFromFilefetchResponse,
} from "@nudmcdgnpm/digit-ui-react-components";

export const ALLOTMENT_DOCUMENT_FIELDS = [
  {
    labelKey: "EST_CITIZEN_REQUEST_LETTER",
    formName: "citizenLetter",
    apiName: "citizenRequestLetter",
  },
  {
    labelKey: "EST_ALLOTMENT_LETTER",
    formName: "allotmentLetter",
    apiName: "allotmentLetter",
  },
  {
    labelKey: "EST_SIGNED_DEED",
    formName: "signedDeed",
    apiName: "signedDeed",
  },
];

export const extractFileStoreId = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (typeof value === "string") return value;
  if (typeof value === "object") {
    return value.filestoreId || value.fileStoreId || value.documentuuid || null;
  }
  return null;
};

/** Collect fileStoreIds + i18n labels from allotment (API or form field names). */
export const collectAllotmentDocumentEntries = (allotment = {}) =>
  ALLOTMENT_DOCUMENT_FIELDS.map(({ labelKey, formName, apiName }) => {
    const id = extractFileStoreId(allotment[apiName] ?? allotment[formName]);
    return id ? { id, labelKey } : null;
  }).filter(Boolean);

/**
 * Fetch file URLs and shape for ESTDocumnetPreview: [{ values: [{ url, title }] }]
 */
export const fetchAllotmentDocumentPreviews = async (allotment = {}, t = (k) => k) => {
  const entries = collectAllotmentDocumentEntries(allotment);
  if (entries.length === 0) return [];

  const tenantId =
    Digit.ULBService.getCurrentTenantId() || Digit.ULBService.getStateId();
  const res = await Digit.UploadServices.Filefetch(
    entries.map((e) => e.id),
    tenantId
  );
  const arr = res?.data?.fileStoreIds || [];
  const byId = {};
  arr.forEach((fsObj, index) => {
    const fsid = fsObj?.fileStoreId || fsObj?.id || entries[index]?.id;
    const url = extractUrlFromFilefetchResponse(res, fsid, index);
    if (fsid && url) byId[fsid] = url;
    if (entries[index]?.id && url) byId[entries[index].id] = url;
  });

  const values = entries
    .map(({ id, labelKey }) => {
      const url = byId[id];
      if (!url) return null;
      return {
        url,
        title: t(labelKey),
        documentType: id,
        fileStoreId: id,
        reference: id,
      };
    })
    .filter(Boolean);

  return values.length ? [{ values }] : [];
};
