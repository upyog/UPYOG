import { Header, Dropdown, Card, CardLabel, Button } from "@upyog/workbench-ui-react-components";
import React, { useState, useEffect, useCallback, useRef } from "react";
import { Info } from "@nudmcdgnpm/workbench-ui-svg-components";
import { useTranslation } from "react-i18next";
import { Config as Configg } from "../../configs/searchMDMSConfig";
import _ from "lodash";


const PAGE_SIZE = 10;

function sortByKey(arr, key) {
  return arr.slice().sort((a, b) => (a[key] < b[key] ? -1 : a[key] > b[key] ? 1 : 0));
}

function getPaginationRange(currentPage, totalPages) {
  const SIBLINGS = 1;
  const range = [];
  const left  = Math.max(2, currentPage - SIBLINGS);
  const right = Math.min(totalPages - 1, currentPage + SIBLINGS);
  range.push(1);
  if (left > 2)               range.push("...");
  for (let i = left; i <= right; i++) range.push(i);
  if (right < totalPages - 1) range.push("...");
  if (totalPages > 1)         range.push(totalPages);
  return range;
}

const PageBtn = ({ label, onClick, disabled, active }) => (
  <button
    onClick={onClick}
    disabled={disabled}
    className={`pagination-btn ${active ? "active" : ""} ${disabled ? "disabled" : ""}`}
  >
    {label}
  </button>
);

const MDMSManageMaster = () => {
  const Config = _.clone(Configg);
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const modalRef = useRef(null);

  let { masterName: modulee, moduleName: master, tenantId } = Digit.Hooks.useQueryParams();
  tenantId = tenantId || Digit.ULBService.getCurrentTenantId();

  const [masterName,    setMasterName]    = useState(null);
  const [moduleName,    setModuleName]    = useState(null);
  const [masterOptions, setMasterOptions] = useState([]);
  const [moduleOptions, setModuleOptions] = useState([]);
  const [schemaData,    setSchemaData]    = useState(null);
  const [showInfo,      setShowInfo]      = useState(false);
  const [showModal,     setShowModal]     = useState(false);
  const [modalLoading,  setModalLoading]  = useState(false);
  const [modalSearch,   setModalSearch]   = useState("");
  const [currentPage,   setCurrentPage]   = useState(1);
  const [allModules,    setAllModules]    = useState([]);

  const filteredModules = modalSearch.trim()
    ? allModules.filter((m) => m.toLowerCase().includes(modalSearch.trim().toLowerCase()))
    : allModules;

  const totalPages   = Math.max(1, Math.ceil(filteredModules.length / PAGE_SIZE));
  const pagedModules = filteredModules.slice(
    (currentPage - 1) * PAGE_SIZE,
    currentPage * PAGE_SIZE
  );

  const toDropdownObj = (master = "", mod = "") => ({
    name: mod || master,
    code: Digit.Utils.locale.getTransformedLocale(
      mod ? `WBH_MDMS_${master}_${mod}` : `WBH_MDMS_MASTER_${master}`
    ),
    translatedValue: t(
      Digit.Utils.locale.getTransformedLocale(
        mod ? `WBH_MDMS_${master}_${mod}` : `WBH_MDMS_MASTER_${master}`
      )
    ),
  });

  const callSchemaAPI = async (searchText) => {
    try {
      const response = await Digit.CustomService.getResponse({
        url: `/${Digit.Hooks.workbench.getMDMSContextPath()}/schema/v1/_search`,
        useCache: false,
        method: "POST",
        userService: false,
        body: { SchemaDefCriteria: { tenantId, limit: 200, moduleName: searchText } },
      });

      const schemas = response?.SchemaDefinitions || [];
      const obj = { mastersAvailable: [], schemas };

      schemas.forEach((schema) => {
        const [mas, mod] = schema.code.split(".");
        obj[mas] = obj[mas]?.length > 0
          ? [...obj[mas], toDropdownObj(mas, mod)]
          : [toDropdownObj(mas, mod)];
        obj.mastersAvailable.push(mas);
      });

      obj.mastersAvailable = [...new Set(obj.mastersAvailable)].map((mas) => toDropdownObj(mas));
      obj.mastersAvailable = sortByKey(obj.mastersAvailable, "translatedValue");

      setSchemaData(obj);
      setMasterOptions(obj.mastersAvailable);
    } catch (err) {
      console.error("Schema API Error:", err);
    }
  };

  const debouncedSearch = useCallback(
    _.debounce((text) => {
      if (text?.trim().length >= 3) callSchemaAPI(text.trim());
    }, 500),
    [tenantId]
  );

  const handleModuleSearch = (e) => {
    if (e.target.tagName !== "INPUT") return;
    debouncedSearch(e.target.value || "");
  };
  

  useEffect(() => {
    const handleOutsideClick = (e) => {
      if (modalRef.current && !modalRef.current.contains(e.target)) closeModal();
    };
    if (showModal) document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, [showModal]);

  const closeModal = () => {
    setShowModal(false);
    setAllModules([]);
    setModalSearch("");
    setCurrentPage(1);
  };

  const fetchAllModules = async () => {
    setModalLoading(true);
    try {
      const response = await Digit.CustomService.getResponse({
        url: `/${Digit.Hooks.workbench.getMDMSContextPath()}/schema/v1/_search`,
        useCache: false,
        method: "POST",
        userService: false,
        body: {
          SchemaDefCriteria: { tenantId, limit: 500, offset: 0, isGetAllCodes: true },
        },
      });

      const schemas = response?.SchemaDefinitions || [];
      const uniqueModules = [
        ...new Set(schemas.map((s) => s.code.includes(".") ? s.code.split(".")[0] : s.code))
      ].sort((a, b) => a.localeCompare(b));

      setAllModules(uniqueModules);
    } catch (err) {
      console.error("Error fetching all modules:", err);
    } finally {
      setModalLoading(false);
    }
  };

  const getAllModuleData = () => {
    setShowModal(true);
    setModalSearch("");
    setCurrentPage(1);
    fetchAllModules();
  };

  const handleModalSearch = (value = "") => {
        setModalSearch(value);
        setCurrentPage(1);
  };


  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages || page === currentPage) return;
    setCurrentPage(page);
  };

  const handleClear = () => {
    setMasterName(null);
    setModuleName(null);
    setModuleOptions([]);
    setMasterOptions([]);
  };

  useEffect(() => {
    if (masterName?.name && schemaData?.[masterName.name]?.length > 0) {
      setModuleOptions(sortByKey(schemaData[masterName.name], "translatedValue"));
    } else {
      setModuleOptions([]);
    }
  }, [masterName, schemaData]);

  useEffect(() => {
    if (masterName?.name && moduleName?.name) {
      navigate(
        `/${window?.contextPath}/employee/workbench/mdms-search-v2?moduleName=${masterName.name}&masterName=${moduleName.name}`
      );
    }
  }, [moduleName]);

  return (
    <React.Fragment>
      <Header className="works-header-search">{t(Config?.label)}</Header>

      <div className="jk-header-btn-wrapper">
        <Card className="manage-master-wrapper" style={{ alignItems: "end", gap: "1rem" }}>

          {/* Module Name + Info Icon */}
          <div style={{ width: "80%" }} onKeyUp={handleModuleSearch}>
            <CardLabel style={{ margin: 0 }}>{t("WBH_MODULE_NAME")}</CardLabel>
            <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <div style={{ flex: 1 }}>
                <Dropdown
                  style={{ width: "100%", margin: 0, flexShrink: 0 }}
                  option={masterOptions}
                  className="form-field"
                  optionKey="name"
                  selected={master && modulee ? toDropdownObj(master) : masterName}
                  select={(e) => { setMasterName(e); setModuleName(null); }}
                  t={t}
                  placeholder={t("ASSET_SEARCH_ENTER_MIN_3_CHARS")}
                  disable={!!master}
                />
              </div>

              {/* Info Icon ── only 2 lines changed: style → className */}
              <div
                className="info-icon-wrapper"
                onMouseEnter={() => setShowInfo(true)}
                onMouseLeave={() => setShowInfo(false)}
              >
                <Info
                  className="info-icon"
                  onClick={getAllModuleData}
                />
                {showInfo && !showModal && (
                  <div className="info-tooltip">
                    Browse all modules
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Master Name */}
          <div style={{ width: "100%" }}>
            <CardLabel style={{ margin: 0 }}>{t("WBH_MASTER_NAME")}</CardLabel>
            <Dropdown
              style={{ width: "100%", margin: 0 }}
              option={moduleOptions}
              className="form-field"
              optionKey="code"
              selected={master && modulee ? toDropdownObj(master, modulee) : moduleName}
              select={(e) => setModuleName(e)}
              t={t}
              placeholder={t("WBH_MASTER_NAME")}
              disable={!masterName}
            />
          </div>

          <Button
            style={{ width: "100%", maxWidth: "100px" }}
            label={t("CLEAR")}
            variation="secondary"
            onButtonClick={handleClear}
            type="button"
          />
        </Card>
      </div>

      {/* Module List Modal */}
      {showModal && (
        <div className="module-modal-backdrop">
          <div ref={modalRef} className="module-modal">

            {/* Header */}
            <div className="module-modal__header">
              <div>
                <div className="module-modal__title">{t("Available Modules")}</div>
                {!modalLoading && (
                  <div className="module-modal__subtitle">
                    {modalSearch.trim()
                      ? `${filteredModules.length} result${filteredModules.length !== 1 ? "s" : ""} for "${modalSearch}"`
                      : `Showing ${Math.min((currentPage - 1) * PAGE_SIZE + 1, filteredModules.length)}–${Math.min(currentPage * PAGE_SIZE, filteredModules.length)} of ${filteredModules.length}`
                    }
                  </div>
                )}
              </div>
              <button className="module-modal__close-btn" onClick={closeModal} aria-label="Close">
                ✕
              </button>
            </div>

            {/* Search bar */}
            <div className="module-modal__search-wrap">
              <div className="module-modal__search-inner">
                <span className="module-modal__search-icon">🔍</span>
                <input
                  autoFocus
                  type="text"
                  value={modalSearch}
                  onChange={(e) => handleModalSearch(e.target.value)}
                  placeholder="Search module name…"
                  className="module-modal__search-input"
                />
                {modalSearch && (
                  <button className="module-modal__search-clear" onClick={() => handleModalSearch()}>
                    ✕
                  </button>
                )}
              </div>
            </div>

            {/* List */}
            <div className="module-modal__list">
              {modalLoading ? (
                <div className="module-modal__loading">Loading modules…</div>
              ) : pagedModules.length === 0 ? (
                <div className="module-modal__empty">
                  {modalSearch.trim() ? `No results for "${modalSearch}"` : "No modules found"}
                </div>
              ) : pagedModules.map((modName, index) => (
                <div key={modName} className="module-modal__item">
                  <span className="module-modal__item-badge">
                    {(currentPage - 1) * PAGE_SIZE + index + 1}
                  </span>
                  <span className="module-modal__item-name">{modName}</span>
                </div>
              ))}
            </div>

            {/* Pagination Footer */}
            {!modalLoading && totalPages > 1 && (
              <div className="module-modal__footer">
                <span className="module-modal__page-info">
                  Page {currentPage} of {totalPages}
                </span>
                <div className="module-modal__page-controls">
                  <PageBtn label="«" onClick={() => handlePageChange(1)}                disabled={currentPage === 1}          />
                  <PageBtn label="‹" onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1}          />

                  {getPaginationRange(currentPage, totalPages).map((item, i) =>
                    item === "..." ? (
                      <span key={`dot-${i}`} className="module-modal__page-ellipsis">…</span>
                    ) : (
                      <PageBtn
                        key={item}
                        label={item}
                        onClick={() => handlePageChange(item)}
                        active={currentPage === item}
                      />
                    )
                  )}

                  <PageBtn label="›" onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages} />
                  <PageBtn label="»" onClick={() => handlePageChange(totalPages)}      disabled={currentPage === totalPages} />
                </div>
              </div>
            )}

          </div>
        </div>
      )}
    </React.Fragment>
  );
};

export default MDMSManageMaster;