import React, { useEffect, useState } from "react";
import {
  CardLabel,
  UploadFile,
  Toast,
  LabelFieldPair,
  ActionBar,
} from "@nudmcdgnpm/digit-ui-react-components";
import { Loader } from "../components/Loader";
import exifr from "exifr";

const ChallanDocuments = ({
  t,
  config,
  onSelect,
  userType,
  formData,
  setError: setFormError,
  clearErrors: clearFormErrors,
  formState,
  data,
  isLoading,
  error,
  setError,
}) => {
  const [documents, setDocuments] = useState(
    formData?.documents?.documents || []
  );

  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(false);

  const tenantId = window.location.href.includes("employee")
    ? Digit.ULBService.getCurrentTenantId()
    : localStorage.getItem("CITIZEN.CITY");

  const hasValue = (value) => {
    return (
      value !== null &&
      value !== undefined &&
      value !== ""
    );
  };

  /**
   * IMPORTANT:
   * Sync local document state back to parent form.
   *
   * This does NOT submit/navigate.
   * It only updates documentsData in ChallanStepperForm.
   */
  useEffect(() => {
    if (!onSelect || !config?.key) {
      return;
    }

    const document = formData?.documents || {};

    const documentStep = {
      ...document,
      documents: documents,
    };

    onSelect(config.key, documentStep);
  }, [documents]);

  /**
   * Validate documents.
   */
  const validateDocuments = () => {
    let isValid = true;

    data?.Challan?.Documents?.forEach((doc) => {
      const documentData = documents?.find(
        (item) => item?.documentType === doc?.code
      );

      /**
       * Required document validation.
       */
      if (doc?.required && !documentData?.filestoreId) {
        isValid = false;
      }

      /**
       * Evidence Image validation.
       *
       * Evidence image requires:
       * - file
       * - latitude
       * - longitude
       */
      if (doc?.code === "CHALLAN.EVIDENCE_IMAGE") {
        const hasFile = hasValue(
          documentData?.filestoreId
        );

        const hasLatitude = hasValue(
          documentData?.latitude
        );

        const hasLongitude = hasValue(
          documentData?.longitude
        );

        console.log(
          "Evidence Image Validation:",
          {
            documentData,
            hasFile,
            hasLatitude,
            hasLongitude,
            latitude: documentData?.latitude,
            longitude: documentData?.longitude,
          }
        );

        if (
          !hasFile ||
          !hasLatitude ||
          !hasLongitude
        ) {
          isValid = false;
        }
      }
    });

    return isValid;
  };

  /**
   * Local document validation.
   *
   * The parent form still performs the final submit.
   */
  useEffect(() => {
    const isValid = validateDocuments();

    setEnableSubmit(!isValid);

    if (isValid) {
      setError(null);
    }
  }, [
    documents,
    data,
    checkRequiredFields,
  ]);

  /**
   * This is only used if this component
   * itself has a Next button.
   *
   * In your current parent form the final
   * SubmitBar handles the submission.
   */
  const handleSubmit = () => {
    const isValid = validateDocuments();

    if (!isValid) {
      return;
    }

    setError(null);

    const document = formData?.documents || {};

    const documentStep = {
      ...document,
      documents: documents,
    };

    onSelect(config.key, documentStep);
  };

  const onSkip = () => {
    onSelect();
  };

  return (
    <div>
      {!isLoading ? (
        <div>
          {data?.Challan?.Documents?.map(
            (document, index) => {
              return (
                <PTRSelectDocument
                  key={index}
                  document={document}
                  t={t}
                  error={error}
                  setError={setError}
                  setDocuments={setDocuments}
                  documents={documents}
                  setCheckRequiredFields={
                    setCheckRequiredFields
                  }
                  handleSubmit={handleSubmit}
                  onSelect={onSelect}
                  config={config}
                  formData={formData}
                />
              );
            }
          )}

          {error && (
            <Toast
              isDleteBtn={true}
              label={error}
              onClose={() => setError(null)}
              error
            />
          )}

          {/*
            Keep this ActionBar only if this component
            is supposed to have its own Next/Skip buttons.
          */}
          <ActionBar>
            <button
              onClick={onSkip}
              className="btn-secondary cg-btn-margin-right"
            >
              {t("COMMON_SKIP")}
            </button>

            <button
              onClick={handleSubmit}
              className="btn-primary"
              disabled={enableSubmit}
            >
              {t("COMMON_NEXT")}
            </button>
          </ActionBar>
        </div>
      ) : (
        <Loader />
      )}
    </div>
  );
};

function PTRSelectDocument({
  t,
  document: doc,
  setDocuments,
  setError,
  documents,
  action,
  formData,
  handleSubmit,
  id,
  onSelect,
  config,
}) {
  const filteredDocument = documents?.find(
    (item) =>
      item?.documentType === doc?.code
  );

  const tenantId =
    Digit.ULBService.getCurrentTenantId();

  const [selectedDocument, setSelectedDocument] =
    useState(() => {
      if (filteredDocument) {
        const match =
          doc?.dropdownData?.find(
            (e) =>
              e.code ===
              filteredDocument.documentType
          );

        return match
          ? {
            ...match,
            i18nKey:
              match.code?.replaceAll(
                ".",
                "_"
              ),
          }
          : {};
      }

      if (doc?.dropdownData?.length === 1) {
        const onlyOption =
          doc.dropdownData[0];

        return {
          ...onlyOption,
          i18nKey:
            onlyOption.code?.replaceAll(
              ".",
              "_"
            ),
        };
      }

      return {};
    });

  const [file, setFile] = useState(null);

  const [uploadedFile, setUploadedFile] =
    useState(
      () =>
        filteredDocument?.filestoreId ||
        null
    );

  const [isHidden, setHidden] =
    useState(false);

  const [getLoading, setLoading] =
    useState(false);

  const hasValue = (value) => {
    return (
      value !== null &&
      value !== undefined &&
      value !== ""
    );
  };

  /**
   * Select document type.
   */
  const handlePTRSelectDocument = (
    value
  ) => {
    setSelectedDocument(value);
  };

  /**
   * Add/update document.
   */
  const updateDocument = (
    selectedDocument,
    extraFields = {}
  ) => {
    if (!selectedDocument?.code) {
      console.warn(
        "Cannot update document: document type not selected"
      );
      return;
    }

    setDocuments((prev = []) => {
      const existingIndex =
        prev.findIndex(
          (item) =>
            item?.documentType ===
            selectedDocument?.code
        );

      if (existingIndex !== -1) {
        return prev.map(
          (item, index) => {
            if (
              index !== existingIndex
            ) {
              return item;
            }

            return {
              ...item,
              ...extraFields,
            };
          }
        );
      }

      return [
        ...prev,
        {
          documentType:
            selectedDocument.code,
          filestoreId: null,
          documentUid: null,
          ...extraFields,
        },
      ];
    });
  };

  /**
   * Handle file selection.
   */
  async function selectfile(e) {
    const selectedFile =
      e.target.files?.[0];

    if (!selectedFile) {
      return;
    }

    const fileType =
      selectedFile.type?.toLowerCase();

    /**
     * Image files.
     */
    if (
      fileType.includes(
        "image/jpeg"
      ) ||
      fileType.includes(
        "image/jpg"
      ) ||
      fileType.includes(
        "image/png"
      )
    ) {
      try {
        let latitude = null;
        let longitude = null;

        console.log(
          "Reading EXIF GPS from:",
          selectedFile.name
        );

        const gpsData =
          await exifr.gps(
            selectedFile
          );

        console.log(
          "EXIF GPS DATA:",
          gpsData
        );

        if (gpsData) {
          latitude =
            gpsData.latitude ??
            null;

          longitude =
            gpsData.longitude ??
            null;
        }

        setFile(selectedFile);

        /**
         * Save GPS information.
         */
        updateDocument(
          selectedDocument,
          {
            latitude,
            longitude,
          }
        );

        /**
         * Evidence image.
         */
        if (
          doc?.code ===
          "CHALLAN.EVIDENCE_IMAGE"
        ) {
          if (
            !hasValue(latitude) ||
            !hasValue(longitude)
          ) {
            console.warn(
              "Evidence image does not contain GPS coordinates"
            );
          } else {
            console.log(
              "Evidence image GPS coordinates found:",
              {
                latitude,
                longitude,
              }
            );

            setError(null);
          }
        }
      } catch (err) {
        console.error(
          "EXIF extraction failed:",
          err
        );

        setFile(selectedFile);

        /**
         * No GPS means coordinates
         * remain null.
         */
        updateDocument(
          selectedDocument,
          {
            latitude: null,
            longitude: null,
          }
        );
      }
    } else {
      /**
       * PDF / other files.
       */
      setFile(selectedFile);

      updateDocument(
        selectedDocument,
        {
          latitude: null,
          longitude: null,
        }
      );
    }
  }

  const { dropdownData } = doc;

  const dropDownData =
    dropdownData || [];

  /**
   * IMPORTANT:
   *
   * When file upload completes,
   * update only filestoreId.
   *
   * Latitude and longitude are preserved.
   */
  useEffect(() => {
    if (!selectedDocument?.code) {
      return;
    }

    setDocuments((prev) => {
      return prev.map((item) => {
        if (
          item?.documentType ===
          selectedDocument?.code
        ) {
          return {
            ...item,
            filestoreId:
              uploadedFile,
            documentUid:
              uploadedFile,
          };
        }

        return item;
      });
    });
  }, [
    uploadedFile,
    selectedDocument,
  ]);

  /**
   * Update mode.
   */
  useEffect(() => {
    if (action === "update") {
      const originalDoc =
        formData?.originalData?.documents?.find(
          (e) =>
            e.documentType?.includes(
              doc?.code
            )
        );

      const docType =
        dropDownData
          .filter(
            (e) =>
              e.code ===
              originalDoc?.documentType
          )
          .map((e) => ({
            ...e,
            i18nKey:
              e?.code?.replaceAll(
                ".",
                "_"
              ),
          }))[0];

      if (!docType) {
        setHidden(true);
      } else {
        setSelectedDocument(
          docType
        );

        setUploadedFile(
          originalDoc?.fileStoreId ||
          originalDoc?.filestoreId ||
          null
        );

        /**
         * Preserve GPS data
         * in update mode.
         */
        if (
          doc?.code ===
          "CHALLAN.EVIDENCE_IMAGE"
        ) {
          setDocuments(
            (prev = []) => {
              const exists =
                prev.some(
                  (item) =>
                    item?.documentType ===
                    originalDoc?.documentType
                );

              if (exists) {
                return prev.map(
                  (item) => {
                    if (
                      item?.documentType ===
                      originalDoc?.documentType
                    ) {
                      return {
                        ...item,
                        latitude:
                          originalDoc?.latitude ??
                          null,
                        longitude:
                          originalDoc?.longitude ??
                          null,
                      };
                    }

                    return item;
                  }
                );
              }

              return [
                ...prev,
                {
                  ...originalDoc,
                  latitude:
                    originalDoc?.latitude ??
                    null,
                  longitude:
                    originalDoc?.longitude ??
                    null,
                },
              ];
            }
          );
        }
      }
    }
  }, []);

  /**
   * Set document code when
   * dropdown is not required.
   */
  useEffect(() => {
    if (!doc?.hasDropdown) {
      setSelectedDocument({
        code: doc?.code,
        i18nKey:
          doc?.code?.replaceAll(
            ".",
            "_"
          ),
      });
    }
  }, []);

  /**
   * Upload file.
   */
  useEffect(() => {
    const uploadFile =
      async () => {
        if (!file) {
          return;
        }

        setLoading(true);

        if (
          file.size >= 5242880
        ) {
          setError(
            t(
              "CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"
            )
          );

          setLoading(false);
          return;
        }

        try {
          /**
           * Clear old file ID.
           *
           * GPS coordinates are NOT
           * cleared here.
           */
          setUploadedFile(null);

          const response =
            await Digit.UploadServices.Filestorage(
              "PTR",
              file,
              Digit.ULBService.getStateId()
            );

          if (
            response?.data?.files
              ?.length > 0
          ) {
            const fileStoreId =
              response?.data?.files?.[0]
                ?.fileStoreId;

            console.log(
              "File uploaded successfully:",
              fileStoreId
            );

            setUploadedFile(
              fileStoreId
            );
          } else {
            setError(
              t(
                "CS_FILE_UPLOAD_ERROR"
              )
            );
          }
        } catch (err) {
          console.error(
            "File upload failed:",
            err
          );

          setError(
            t(
              "CS_FILE_UPLOAD_ERROR"
            )
          );
        } finally {
          setLoading(false);
        }
      };

    uploadFile();
  }, [file]);

  /**
   * Clear uploaded file if
   * document is hidden.
   */
  useEffect(() => {
    if (isHidden) {
      setUploadedFile(null);
    }
  }, [isHidden]);

  return (
    <div className="challan-documents">
      <LabelFieldPair className="challan-label-field">
        <CardLabel className="challan-card-label">
          {t(doc?.code)}

          <span className="requiredField">
            {doc?.required && " *"}
          </span>
        </CardLabel>

        <div className="field cg-field-width-100">
          <UploadFile
            className="cg-upload-file"
            onUpload={selectfile}
            onDelete={() => {
              setUploadedFile(
                null
              );

              setFile(null);

              /**
               * Clear GPS when
               * evidence image is deleted.
               */
              if (
                doc?.code ===
                "CHALLAN.EVIDENCE_IMAGE"
              ) {
                setDocuments(
                  (prev = []) =>
                    prev.map(
                      (item) => {
                        if (
                          item?.documentType ===
                          doc?.code
                        ) {
                          return {
                            ...item,
                            filestoreId:
                              null,
                            documentUid:
                              null,
                            latitude:
                              null,
                            longitude:
                              null,
                          };
                        }

                        return item;
                      }
                    )
                );

                setError(null);
              }
            }}
            id={id}
            message={
              uploadedFile
                ? `1 ${t(
                  "CS_ACTION_FILEUPLOADED"
                )}`
                : t(
                  "CS_ACTION_NO_FILEUPLOADED"
                )
            }
            accept=".pdf, .jpeg, .jpg, .png"
            buttonType="button"
            error={!uploadedFile}
          />

          {doc?.code ===
            "CHALLAN.EVIDENCE_IMAGE" && (
              <span className="challan-note-green">
                <span className="challan-note-red">
                  {t(
                    "CHALLAN_NOTE_LABEL"
                  )}
                </span>{" "}
                {t(
                  "CHALLAN_UPLOAD_LOCATION_IMAGE"
                )}
              </span>
            )}

          {doc?.code ===
            "CHALLAN.EVIDENCE_IMAGE" &&
            documents
              ?.filter(
                (item) =>
                  item?.documentType ===
                  "CHALLAN.EVIDENCE_IMAGE"
              )
              ?.map(
                (
                  item,
                  index
                ) => {
                  const hasLatitude =
                    hasValue(
                      item?.latitude
                    );

                  const hasLongitude =
                    hasValue(
                      item?.longitude
                    );

                  return (
                    <div
                      key={
                        index
                      }
                    >
                      <div className="challan-lat-long">
                        <div>
                          <b>
                            Latitude:
                          </b>{" "}
                          {hasLatitude
                            ? item.latitude
                            : "Not available"}
                        </div>

                        <div>
                          <b>
                            Longitude:
                          </b>{" "}
                          {hasLongitude
                            ? item.longitude
                            : "Not available"}
                        </div>
                      </div>

                      {(!hasLatitude ||
                        !hasLongitude) && (
                          <div className="challan-gps-error">
                            {t(
                              "CHALLAN_UPLOAD_LOCATION_IMAGE"
                            )}
                          </div>
                        )}
                    </div>
                  );
                }
              )}
        </div>
      </LabelFieldPair>

      {getLoading && (
        <Loader page={true} />
      )}
    </div>
  );
}

export default ChallanDocuments;