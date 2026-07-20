import React, { useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import { MAP_TILE_URL } from "../utils";
import "../css/gis-inline.css";

// Vite's ?raw suffix loads the GeoJSON file as a plain text string at build time.
// We then JSON.parse() it ourselves.
import hoshiarpurRaw from "../data/hoshiarpur.geojson?raw";
import karolbaghRaw from "../data/karolbagh.geojson?raw";

/**
 * LayerView renders survey parcels as coloured POLYGONS on a Leaflet map.
 *
 * Each dataset is called an "area". Two areas are loaded:
 *   - Hoshiarpur Block 05-A1  → coloured by Property Type (no tax data in that shapefile)
 *   - Karol Bagh (Delhi)      → coloured by real Tax Status (Paid / Due / Overdue / Partial)
 *
 * To add a third area: add one new entry to the AREAS array below.
 * To go live with real backend data: replace each area's `raw` field with an API response.
 */


/**
 * Cleans a single raw field value from the shapefile.
 * Shapefiles often store missing data as blank strings or "-" instead of null.
 * This function converts all of those into a proper null so the popup can hide them.
 */
const cleanVal = (rawValue) => {
  if (rawValue === null || rawValue === undefined) return null;
  const trimmedValue = String(rawValue).trim();
  return trimmedValue === "" || trimmedValue === "-" ? null : trimmedValue;
};

/**
 * Parses a raw shapefile money/number field into a plain JavaScript number.
 * Strips currency symbols, commas, and other non-numeric characters before parsing.
 * Returns 0 for blank, null, or unparseable values so callers can safely do arithmetic.
 */
const toNum = (rawValue) => {
  const parsed = parseFloat(String(rawValue ?? "").replace(/[^0-9.\-]/g, ""));
  return Number.isFinite(parsed) ? parsed : 0;
};

/**
 * Formats a rupee amount (in full rupees) into the compact "Rs.X.XX L" (lakh) notation
 * used throughout the tax analysis panel, e.g. 1642000 → "Rs.16.42 L".
 */
const formatLakh = (amount) => `Rs.${(amount / 100000).toFixed(2)} L`;

/**
 * AREAS is the master config list. Each entry represents one dataset (one GeoJSON file).
 *
 * Every area defines:
 *   id          — unique identifier used by the dropdown
 *   label       — human-readable name shown in the dropdown
 *   raw         — the imported GeoJSON text (loaded at build time via ?raw)
 *   legendTitle — what the colours represent ("Property Type" / "Tax Status")
 *   colors      — map of category name → hex colour for the polygon fill
 *   filters     — the filter chips shown above the map (ALL + each category)
 *   toFeature   — function that reads one feature's raw shapefile fields and returns
 *                 the common shape: { category, title, rows }
 *                 category = the value we colour + filter by
 *                 title    = heading shown at the top of the popup
 *                 rows     = [[label, value], ...] pairs shown in the popup body
 *
 * QGIS analogy: each entry is like a separate layer in the Layers panel, each with
 * its own symbology (colors), attribute table (rows), and field mapping (toFeature).
 */
const AREAS = [
  {
    id: "hoshiarpur",
    label: "Hoshiarpur · Block 05-A1",
    raw: hoshiarpurRaw,
    legendTitle: "Property Type",
    // One colour per property type. "Unspecified" catches anything not in this list.
    colors: {
      Commercial: "#4e79a7",
      Residential: "#59a14f",
      "Mixed (Residential/Commercial)": "#b07aa1",
      "Public semi public": "#76b7b2",
      Open: "#edc948",
      Unspecified: "#bab0ac",
    },
    // "ALL" always goes first — it shows every parcel regardless of type.
    filters: [
      { code: "ALL", label: "All" },
      { code: "Commercial", label: "Commercial" },
      { code: "Residential", label: "Residential" },
      { code: "Mixed (Residential/Commercial)", label: "Mixed" },
      { code: "Public semi public", label: "Public" },
      { code: "Open", label: "Open" },
      { code: "Unspecified", label: "Other" },
    ],
    // Maps the raw shapefile fields (truncated to 10 chars by dBASE format) → readable rows.
    // Fields like TYPE_OF_PR, OWNER_MOBI, NO_OF_FLOO are truncated shapefile field names.
    toFeature: (rawProperties) => {
      const plotArea = cleanVal(rawProperties.PLOT_AREA);
      return {
        category: cleanVal(rawProperties.TYPE_OF_PR) || "Unspecified", // drives colour + filter
        title: cleanVal(rawProperties.TYPE_OF_PR) || "Property",       // popup heading
        // Hoshiarpur shapefile has no tax data — zero so AnalysisPanel arithmetic stays safe.
        assessed: 0,
        outstanding: 0,
        rows: [
          ["Parcel No", cleanVal(rawProperties.Parcel_No)],
          ["Owner", cleanVal(rawProperties.OWNER_NAME)],
          ["Mobile", cleanVal(rawProperties.OWNER_MOBI)],
          ["Block", cleanVal(rawProperties.BLOCK_NO)],
          // Only show unit if the value exists; plotArea && "..." returns null when plotArea is null
          ["Plot Area", plotArea && `${plotArea} sq.ft`],
          ["Floors", cleanVal(rawProperties.NO_OF_FLOO)],
          ["Construction", cleanVal(rawProperties.TYPE_OF_CO)],
        ],
      };
    },
  },
  {
    id: "karolbagh",
    label: "Karol Bagh · Delhi",
    raw: karolbaghRaw,
    legendTitle: "Tax Status",
    // Traffic-light palette: green = paid, yellow = due, orange = partial, red = overdue.
    colors: {
      Paid: "#59a14f",
      Due: "#edc948",
      "Partially Paid": "#f28e2b",
      Overdue: "#e15759",
      Unknown: "#bab0ac",
    },
    filters: [
      { code: "ALL", label: "All" },
      { code: "Paid", label: "Paid" },
      { code: "Due", label: "Due" },
      { code: "Partially Paid", label: "Partial" },
      { code: "Overdue", label: "Overdue" },
    ],
    // Enables the right-side AnalysisPanel (DonutChart + tax breakdown) for this area.
    // Areas without this flag show the simpler legend panel instead.
    analysis: true,
    // Karol Bagh fields use a "data_" prefix — completely different schema from Hoshiarpur.
    // toFeature() is the bridge: the rest of the component never touches raw field names.
    toFeature: (rawProperties) => {
      const plotArea = cleanVal(rawProperties.data_Plot_Area_sqm);
      return {
        category: cleanVal(rawProperties.data_Tax_Status) || "Unknown",
        title: cleanVal(rawProperties.data_Property_Type) || "Property",
        // Parsed to plain numbers (via toNum) so the analysis useMemo can sum them directly.
        assessed: toNum(rawProperties.data_Annual_Tax_Assessed),
        outstanding: toNum(rawProperties.data_Outstanding_Dues),
        rows: [
          ["Property ID", cleanVal(rawProperties.data_Property_ID)],
          ["UPIN", cleanVal(rawProperties.data_UPIN)],
          ["Owner", cleanVal(rawProperties.data_Owner_Name)],
          ["Type", cleanVal(rawProperties.data_Property_Type)],
          ["Floors", cleanVal(rawProperties.data_Floors)],
          ["Plot Area", plotArea && `${plotArea} sqm`],
          ["Tax Status", cleanVal(rawProperties.data_Tax_Status)],
          ["Annual Tax", cleanVal(rawProperties.data_Annual_Tax_Assessed)],
          ["Outstanding", cleanVal(rawProperties.data_Outstanding_Dues)],
          ["Last Payment", cleanVal(rawProperties.data_Last_Payment_Date)],
          ["Ward", cleanVal(rawProperties.data_Ward_No)],
        ],
      };
    },
  },
];

/**
 * Parses the raw GeoJSON string for one area and normalises every feature's
 * properties into the common { category, title, rows } shape.
 * This runs once per area switch (cached by useMemo) — not on every render.
 *
 * QGIS analogy: like running "Field Calculator" on every row of the attribute table
 * to produce a clean set of columns before styling.
 */
const normalizeFeatureCollection = (areaConfig) => ({
  type: "FeatureCollection",
  features: JSON.parse(areaConfig.raw).features.map((rawFeature) => ({
    type: "Feature",
    geometry: rawFeature.geometry,                              // polygon coordinates — unchanged
    properties: areaConfig.toFeature(rawFeature.properties || {}), // normalised attributes
  })),
});

const LayerView = () => {
  const { t } = useTranslation();

  // mapRef  — a reference to the <div> DOM element that Leaflet will draw into.
  // mapObj  — a reference to the Leaflet map instance (window.L.map(...)).
  // layerRef — a reference to the currently visible GeoJSON polygon layer.
  // Refs (useRef) hold values that persist across renders without triggering a re-render.
  const mapRef = useRef(null);
  const mapObj = useRef(null);
  const layerRef = useRef(null);

  // areaId tracks which area the user has selected in the dropdown.
  // filter  tracks which category chip is active ("ALL" means show everything).
  const [areaId, setAreaId] = useState(AREAS[0].id);
  const [filter, setFilter] = useState("ALL");

  // Derive the full area config object from the selected id.
  const area = AREAS.find((a) => a.id === areaId);

  // useMemo caches the parsed + normalised GeoJSON. It only re-runs when areaId changes,
  // not on every render — parsing a 600-feature GeoJSON on every keystroke would be slow.
  const geoJsonData = useMemo(() => normalizeFeatureCollection(area), [areaId]); // eslint-disable-line react-hooks/exhaustive-deps

  // Build a count of how many parcels fall into each category.
  // This drives the numbers shown next to each legend swatch (e.g. "Paid  350").
  const categoryCounts = {};
  geoJsonData.features.forEach((feature) => {
    const category = feature.properties.category;
    categoryCounts[category] = (categoryCounts[category] || 0) + 1;
  });
  const total = geoJsonData.features.length;

  // For areas with analysis:true (currently Karol Bagh), aggregate the tax figures needed
  // by AnalysisPanel in a single pass over all features:
  //   totalAssessed    — sum of annual tax assessed across all parcels
  //   totalOutstanding — sum of outstanding dues across all parcels
  //   perStatus        — per-category { count, outstanding } used for the status cards
  // Returns null for areas without tax data, which hides the panel entirely.
  const analysis = useMemo(() => {
    if (!area.analysis) return null;
    let totalAssessed = 0;
    let totalOutstanding = 0;
    const perStatus = {};
    geoJsonData.features.forEach(({ properties }) => {
      const assessed = properties.assessed || 0;
      const outstanding = properties.outstanding || 0;
      totalAssessed += assessed;
      totalOutstanding += outstanding;
      const bucket = perStatus[properties.category] || { count: 0, outstanding: 0 };
      bucket.count += 1;
      bucket.outstanding += outstanding;
      perStatus[properties.category] = bucket;
    });
    return { totalAssessed, totalOutstanding, perStatus };
  }, [areaId]); // eslint-disable-line react-hooks/exhaustive-deps

  /**
   * Returns the Leaflet style object for one polygon.
   * fillColor comes from the area's color map; falls back to grey if the
   * category is not listed (e.g. a new type added to the data after deploy).
   */
  const getPolygonStyle = (feature) => ({
    color: "#333",       // border colour
    weight: 1,           // border thickness in pixels
    fillColor: area.colors[feature.properties.category] || "#cccccc",
    fillOpacity: 0.75,
  });

  /**
   * Builds the HTML string shown inside the Leaflet popup when a parcel is clicked.
   * Rows where the value is null/empty are filtered out so the popup stays clean.
   */
  const buildPopup = (featureProps) => {
    const rowsHtml = featureProps.rows
      .filter(([, value]) => value !== null && value !== undefined && value !== "")
      .map(([label, value]) => `<b>${t(label)}:</b> ${value}`)
      .join("<br/>");
    return `<div class="gis-popup"><b>${featureProps.title}</b><br/>${rowsHtml}</div>`;
  };

  /**
   * Loads Leaflet from the CDN if it hasn't been loaded yet, then initialises the map.
   * We load it dynamically (via <script> tag) so it's not bundled into the app's JS.
   * script.onload = initializeMap ensures we only call initializeMap AFTER Leaflet
   * has fully downloaded — otherwise window.L would not exist yet and the map would crash.
   * The [] dependency array means this runs exactly once when the component first mounts.
   */
  useEffect(() => {
    const loadLeaflet = () => {
      if (!window.L) {
        // Step 1: add the Leaflet CSS (styles for zoom controls, popups, etc.)
        const link = document.createElement("link");
        link.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
        link.rel = "stylesheet";
        document.head.appendChild(link);

        // Step 2: add the Leaflet JS and start the map only after it finishes loading
        const script = document.createElement("script");
        script.src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";
        script.onload = initializeMap;
        document.head.appendChild(script);
      } else {
        // Leaflet already loaded (e.g. user navigated away and came back) — init directly
        initializeMap();
      }
    };
    loadLeaflet();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  /**
   * Zooms the map so all visible polygons fit inside the viewport.
   * fitBounds() is Leaflet's equivalent of QGIS "Zoom to Layer".
   * The try/catch handles the edge case where the active filter hides all features
   * (the layer would have an empty bounding box, which throws an error).
   */
  const zoomToLayer = () => {
    const leafletMap = mapObj.current;
    if (leafletMap && layerRef.current) {
      try {
        leafletMap.fitBounds(layerRef.current.getBounds(), { padding: [20, 20] });
      } catch (e) {
        /* layer is empty — no features match the current filter */
      }
    }
  };

  /**
   * Creates the Leaflet map instance, adds the basemap tile layer, then draws parcels.
   * setTimeout(invalidateSize) fixes a common Leaflet bug: if the map container's size
   * hasn't settled yet when Leaflet first renders, tiles appear grey and parcels are
   * offset. Waiting 250ms lets the browser finish laying out the page before recalculating.
   */
  const initializeMap = () => {
    if (!mapRef.current || mapObj.current) return; // already initialised — skip
    const leafletMap = window.L.map(mapRef.current);
    window.L.tileLayer(MAP_TILE_URL, { attribution: "© OpenStreetMap", maxZoom: 20 }).addTo(leafletMap);
    mapObj.current = leafletMap;
    renderPolygonLayer();
    setTimeout(() => {
      leafletMap.invalidateSize(); // recalculate canvas size after layout settles
      zoomToLayer();
    }, 250);
  };

  /**
   * Removes the existing polygon layer (if any) and draws a fresh one.
   * Called whenever the selected area or active filter changes.
   * We always remove-then-redraw rather than updating in place because Leaflet's
   * geoJSON layer has no built-in "replace data" method.
   */
  const renderPolygonLayer = () => {
    const leafletMap = mapObj.current;
    if (!leafletMap || !window.L) return;

    // Remove the old layer so parcels from the previous area/filter don't linger
    if (layerRef.current) {
      leafletMap.removeLayer(layerRef.current);
      layerRef.current = null;
    }

    const polygonLayer = window.L.geoJSON(geoJsonData, {
      style: getPolygonStyle,
      // The filter function runs per-feature: return false to hide a parcel
      filter: (feature) => filter === "ALL" || feature.properties.category === filter,
      // bindPopup attaches a click popup to every polygon
      onEachFeature: (feature, layer) => layer.bindPopup(buildPopup(feature.properties)),
    }).addTo(leafletMap);

    layerRef.current = polygonLayer;
    zoomToLayer(); // auto-zoom so the new area fills the viewport
  };

  // Re-draw the polygon layer whenever the user switches area or clicks a filter chip.
  useEffect(() => {
    renderPolygonLayer();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [areaId, filter]);

  // When the browser window is resized, tell Leaflet to recalculate the map canvas size.
  // Without this, resizing the window leaves grey tiles along the new edges.
  useEffect(() => {
    const onResize = () => mapObj.current && mapObj.current.invalidateSize();
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize); // cleanup on unmount
  }, []);

  return (
    <div className="gis-map-wrapper">
      {/* Leaflet draws into this div. It must be 100% height/width of its parent. */}
      <div ref={mapRef} className="gis-map-container" />

      {/* Back button — overlaid top-right so it doesn't affect page layout */}
      <div className="gis-back-button">
        <BackButton />
      </div>

      {/* ── LEFT INFO PANEL: title + area switcher dropdown + summary counts ── */}
      <div className="gis-panel gis-panel--info">
        <div className="gis-panel__title">{t("Property Map")}</div>
        <div className="gis-panel__subtitle">UPYOG GIS</div>

        <label className="gis-panel__label">{t("Area")}</label>
        {/* Switching area resets the filter to ALL so stale filters don't carry over */}
        <select
          value={areaId}
          onChange={(e) => {
            setAreaId(e.target.value);
            setFilter("ALL");
          }}
          className="gis-panel__select"
        >
          {AREAS.map((areaOption) => (
            <option key={areaOption.id} value={areaOption.id}>
              {areaOption.label}
            </option>
          ))}
        </select>

        {/* Summary row: total parcel count */}
        <div className="gis-summary-row">
          <span>{t("Total Properties")}</span>
          <b>{total}</b>
        </div>
        {/* Summary row: what the colours represent for this area */}
        <div className="gis-summary-row">
          <span>{t("Coloured by")}</span>
          <b className="gis-accent">{t(area.legendTitle)}</b>
        </div>
      </div>

      {/* ── FILTER CHIPS: one button per category + "All" ── */}
      <div className="gis-panel gis-panel--filters">
        <span className="gis-filter__label">{t(area.legendTitle)}:</span>
        {area.filters.map((chip) => (
          <button
            key={chip.code}
            className={`gis-filter-btn${filter === chip.code ? " gis-filter-btn--active" : ""}`}
            onClick={() => setFilter(chip.code)}
          >
            {t(chip.label)}
          </button>
        ))}
      </div>

      {/* ── LEGEND (areas without tax analysis) ── */}
      {!analysis && (
        <div className="gis-panel gis-panel--legend">
          <div className="gis-legend__title">{t(area.legendTitle)}</div>
          {area.filters
            .filter((chip) => chip.code !== "ALL")
            .map((chip) => (
              <div key={chip.code} className="gis-summary-row">
                <span className="gis-legend__label">
                  <span
                    className="gis-legend__swatch"
                    style={{ background: area.colors[chip.code] }}
                  />
                  {t(chip.label)}
                </span>
                <b>{categoryCounts[chip.code] || 0}</b>
              </div>
            ))}
        </div>
      )}

      {/* ── ANALYSIS PANEL (Karol Bagh — donut chart + tax breakdown) ── */}
      {analysis && (
        <AnalysisPanel t={t} area={area} total={total} analysis={analysis} />
      )}
    </div>
  );
};

// ── DonutChart ────────────────────────────────────────────────────────────────

/**
 * Pure SVG donut chart rendered without any charting library.
 *
 * Props:
 *   slices — array of { code, label, color, count } (one per tax-status category)
 *   total  — sum of all counts, used to compute each slice's sweep angle
 *
 * Interaction: hovering a slice pops it outward and shows its count + percentage
 * in the centre hole. The idle centre shows the overall property count.
 * Hit-testing is done in onMouseMove via polar coordinates (no SVG pointer-events
 * on paths, which avoids z-order issues between overlapping paths).
 */
const DonutChart = ({ slices, total }) => {
  const [hovered, setHovered] = useState(null);
  const svgRef = useRef(null);

  // SVG canvas is 170×170; centre at (85,85). outerR/innerR define the donut ring width.
  const cx = 85, cy = 85, outerR = 68, innerR = 40;
  const toRad = (deg) => (deg * Math.PI) / 180;

  /**
   * Builds the SVG "d" attribute for one donut slice using two arcs (outer + inner)
   * connected at their endpoints — the standard SVG donut-slice technique.
   * Angles are measured clockwise from 12 o'clock (hence the -90° offset).
   * The `large` flag flips to 1 when the slice spans more than 180° so the arc
   * takes the long way around rather than cutting through the centre.
   */
  const arcPath = (startDeg, endDeg) => {
    const s = toRad(startDeg - 90);
    const e = toRad(endDeg - 90);
    const large = endDeg - startDeg > 180 ? 1 : 0;
    const x1 = cx + outerR * Math.cos(s), y1 = cy + outerR * Math.sin(s);
    const x2 = cx + outerR * Math.cos(e), y2 = cy + outerR * Math.sin(e);
    const x3 = cx + innerR * Math.cos(e), y3 = cy + innerR * Math.sin(e);
    const x4 = cx + innerR * Math.cos(s), y4 = cy + innerR * Math.sin(s);
    return `M${x1},${y1} A${outerR},${outerR} 0 ${large} 1 ${x2},${y2} L${x3},${y3} A${innerR},${innerR} 0 ${large} 0 ${x4},${y4} Z`;
  };

  // Convert each slice into a segment with pre-computed start/end angles and percentage.
  // Slices with count=0 are dropped so they don't produce invisible zero-width paths.
  let angle = 0;
  const segments = slices
    .filter((s) => s.count > 0)
    .map((s) => {
      const sweep = (s.count / total) * 360;
      const seg = {
        ...s,
        startAngle: angle,
        endAngle: angle + sweep,
        midAngle: angle + sweep / 2, // used to compute the pop-out translation direction
        pct: ((s.count / total) * 100).toFixed(1),
      };
      angle += sweep;
      return seg;
    });

  /**
   * Determines which slice the cursor is over by converting the mouse position to
   * SVG coordinates, computing the polar angle from the centre, then finding the
   * segment whose [startAngle, endAngle) range contains that angle.
   * Cursor inside the inner hole or outside the outer ring clears the hover.
   */
  const handleMouseMove = (e) => {
    const svg = svgRef.current;
    if (!svg) return;
    const rect = svg.getBoundingClientRect();
    // Scale from CSS pixels to SVG viewBox units (170×170)
    const svgX = (e.clientX - rect.left) * (170 / rect.width);
    const svgY = (e.clientY - rect.top) * (170 / rect.height);
    const dx = svgX - cx;
    const dy = svgY - cy;
    const dist = Math.sqrt(dx * dx + dy * dy);
    if (dist < innerR || dist > outerR + 8) { setHovered(null); return; }
    // atan2 returns [-π, π]; shift so 0° is at 12 o'clock matching our arc convention
    let deg = Math.atan2(dy, dx) * (180 / Math.PI) + 90;
    if (deg < 0) deg += 360;
    const seg = segments.find((s) => deg >= s.startAngle && deg < s.endAngle);
    setHovered(seg ? seg.code : null);
  };

  const hoveredSeg = segments.find((s) => s.code === hovered);

  return (
    <svg
      ref={svgRef}
      viewBox="0 0 170 170"
      width="170"
      height="170"
      className="gis-donut-svg"
      onMouseMove={handleMouseMove}
      onMouseLeave={() => setHovered(null)}
    >
      {segments.map((seg) => {
        const isHov = hovered === seg.code;
        const midRad = toRad(seg.midAngle - 90);
        const tx = isHov ? +(Math.cos(midRad) * 8).toFixed(2) : 0;
        const ty = isHov ? +(Math.sin(midRad) * 8).toFixed(2) : 0;
        return (
          <path
            key={seg.code}
            d={arcPath(seg.startAngle, seg.endAngle)}
            fill={seg.color}
            transform={`translate(${tx},${ty})`}
            className="gis-donut-path"
            style={{ filter: isHov ? "drop-shadow(0 3px 6px rgba(0,0,0,0.28))" : "none" }}
          />
        );
      })}
      {hoveredSeg ? (
        <>
          <text x={cx} y={cy - 10} className="gis-donut-text gis-donut-text--count" style={{ fill: hoveredSeg.color }}>{hoveredSeg.count}</text>
          <text x={cx} y={cy + 7}  className="gis-donut-text gis-donut-text--label">{hoveredSeg.label}</text>
          <text x={cx} y={cy + 20} className="gis-donut-text gis-donut-text--meta">{hoveredSeg.pct}%</text>
        </>
      ) : (
        <>
          <text x={cx} y={cy - 4}  className="gis-donut-text gis-donut-text--count">{total}</text>
          <text x={cx} y={cy + 13} className="gis-donut-text gis-donut-text--meta">Properties</text>
        </>
      )}
    </svg>
  );
};

// ── AnalysisPanel ─────────────────────────────────────────────────────────────

const AnalysisPanel = ({ t, area, total, analysis }) => {
  const statusRows = area.filters
    .filter((chip) => chip.code !== "ALL")
    .map((chip) => ({
      code: chip.code,
      label: chip.label,
      color: area.colors[chip.code],
      count: analysis.perStatus[chip.code]?.count || 0,
      outstanding: analysis.perStatus[chip.code]?.outstanding || 0,
      pct: total > 0 ? +(((analysis.perStatus[chip.code]?.count || 0) / total) * 100).toFixed(1) : 0,
    }));

  return (
    <div className="gis-panel gis-panel--analysis">
      <div className="gis-analysis-section">
        <span className="gis-analysis-section__title">{t("Count Distribution")}</span>
      </div>

      <DonutChart slices={statusRows} total={total} />

      <div className="gis-analysis-section gis-analysis-section--spaced">
        <span className="gis-analysis-section__title">{t("Property Count By Status")}</span>
      </div>

      {statusRows.map((row) => (
        <div key={row.code} className="gis-status-card" style={{ "--status-color": row.color }}>
          <div className="gis-status-card__header">
            <span className="gis-status-card__label">{t(row.label)}</span>
            <span className="gis-status-card__count" style={{ color: row.color }}>
              {row.count}
              <span className="gis-status-card__pct">{row.pct}%</span>
            </span>
          </div>
          <div className="gis-status-card__bar-track">
            <div className="gis-status-card__bar-fill" style={{ width: `${row.pct}%`, background: row.color }} />
          </div>
          {row.outstanding > 0 && (
            <div className="gis-status-card__dues">
              <span>{t("Outstanding")}</span>
              <span style={{ color: row.color }}>{formatLakh(row.outstanding)}</span>
            </div>
          )}
        </div>
      ))}

      <div className="gis-analysis-total">
        <span>{t("Total Outstanding")}</span>
        <b>{formatLakh(analysis.totalOutstanding)}</b>
      </div>
    </div>
  );
};

export default LayerView;
