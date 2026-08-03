import React, { useMemo } from "react";
import { Link } from "react-router-dom";
import { AssetCell, formatDimensions } from "./assetTableUtils";
import { isPendingForAllotment } from "../../utils/allotmentFormUtils";

const allotButtonStyle = (isMobile, isDisabled) => ({
  backgroundColor: isDisabled ? "#ccc" : "#007bff",
  color: "white",
  border: "none",
  padding: isMobile ? "3px 6px" : "6px 10px",
  borderRadius: "4px",
  cursor: isDisabled ? "not-allowed" : "pointer",
  fontSize: isMobile ? "9px" : "12px",
  minWidth: isMobile ? "50px" : "auto",
});

const editButtonStyle = (isMobile) => ({
  backgroundColor: "#a23c59",
  color: "white",
  border: "none",
  padding: isMobile ? "4px 8px" : "6px 10px",
  borderRadius: "4px",
  cursor: "pointer",
  fontSize: isMobile ? "10px" : "12px",
  minWidth: isMobile ? "40px" : "auto",
});

/**
 * Shared column definitions for EST asset tables.
 *
 * @param {object} options
 * @param {boolean} options.isMobile
 * @param {string}  options.modulePath - base module path for absolute links
 * @param {function} options.navigate
 * @param {"link"|"navigate"} options.estateNoLink - how to render estate number
 * @param {function} options.onEstateNoClick - if set, estate number click handler (e.g. summary page)
 * @param {boolean} options.showAssetRef
 * @param {boolean} options.showAllotmentStatus
 * @param {"none"|"allot"|"allot-edit"} options.actions
 * @param {function} options.onAllot
 * @param {function} options.onEdit
 */
const useAssetTableColumns = ({
  isMobile,
  modulePath,
  navigate,
  estateNoLink = "link",
  showAssetRef = true,
  showAllotmentStatus = false,
  actions = "none",
  onAllot,
  onEdit,
  onEstateNoClick,
}) => {
  return useMemo(() => {
    const columns = [
      {
        Header: "Asset Number",
        accessor: "estateNo",
        disableSortBy: true,
        Cell: ({ row }) => {
          const estateNo = row.original.estateNo;
          if (onEstateNoClick) {
            return (
              <span
                style={{ color: "#a82227", cursor: "pointer", textDecoration: "underline" }}
                onClick={() => onEstateNoClick(row.original)}
              >
                {estateNo}
              </span>
            );
          }
          if (estateNoLink === "navigate") {
            return (
              <span
                style={{ color: "#a82227", cursor: "pointer", textDecoration: "underline" }}
                onClick={() => navigate(`${modulePath}/application-details/${estateNo}`)}
              >
                {estateNo}
              </span>
            );
          }
          return (
            <div>
              <span className="link">
                <Link to={`application-details/${estateNo}`}>{estateNo}</Link>
              </span>
            </div>
          );
        },
      },
    ];

    if (showAssetRef) {
      columns.push({
        Header: "Asset Ref",
        disableSortBy: true,
        Cell: ({ row }) => (
          <AssetCell value={row.original.refAssetNo || row.original.assetRef} />
        ),
      });
    }

    columns.push(
      {
        Header: "Building Name",
        disableSortBy: true,
        Cell: ({ row }) => (
          <AssetCell value={row.original.buildingName || row.original.assetName} />
        ),
      },
      {
        Header: "Locality",
        disableSortBy: true,
        Cell: ({ row }) => <AssetCell value={row.original.locality} />,
      },
      {
        Header: "Plot Area",
        disableSortBy: true,
        Cell: ({ row }) => <AssetCell value={row.original.totalFloorArea} />,
      },
      {
        Header: "Dimensions",
        disableSortBy: true,
        Cell: ({ row }) => (
          <AssetCell
            value={formatDimensions(
              row.original.dimensionLength,
              row.original.dimensionWidth
            )}
          />
        ),
      },
      {
        Header: "Asset Type",
        disableSortBy: true,
        Cell: ({ row }) => <AssetCell value={row.original.assetType} />,
      },
      {
        Header: "Rate/sqft",
        disableSortBy: true,
        Cell: ({ row }) => <AssetCell value={row.original.rate} />,
      },
      {
        Header: "Status",
        disableSortBy: true,
        Cell: ({ row }) => (
          <AssetCell value={row.original.assetAllotmentStatus || "N/A"} />
        ),
      }
    );

    if (showAllotmentStatus) {
      columns.push({
        Header: "Allotment Status",
        disableSortBy: true,
        Cell: ({ row }) => (
          <AssetCell value={row.original.assetAllotmentStatus || "N/A"} />
        ),
      });
    }

    if (actions !== "none") {
      columns.push({
        Header: "Action",
        disableSortBy: true,
        Cell: ({ row }) => {
          // Enable Allot only when assetAllotmentStatus is PENDING_FOR_ALLOTMENT.
          const canAllot = isPendingForAllotment(row.original);
          const showEdit = actions === "allot-edit";

          if (actions === "allot") {
            return (
              <button
                onClick={() => canAllot && onAllot?.(row.original)}
                style={allotButtonStyle(isMobile, !canAllot)}
                disabled={!canAllot}
              >
                Allot Asset
              </button>
            );
          }

          return (
            <div
              style={{
                display: "flex",
                flexDirection: isMobile && showEdit ? "column" : "row",
                gap: isMobile ? "4px" : "8px",
                justifyContent: "center",
              }}
            >
              <button
                onClick={() => canAllot && onAllot?.(row.original)}
                style={allotButtonStyle(isMobile, !canAllot)}
                disabled={!canAllot}
              >
                Allot Asset
              </button>
              {showEdit && (
                <button
                  onClick={() => onEdit?.(row.original)}
                  style={editButtonStyle(isMobile)}
                >
                  Edit
                </button>
              )}
            </div>
          );
        },
      });
    }

    return columns;
  }, [
    isMobile,
    modulePath,
    navigate,
    estateNoLink,
    showAssetRef,
    showAllotmentStatus,
    actions,
    onAllot,
    onEdit,
    onEstateNoClick,
  ]);
};

export default useAssetTableColumns;
