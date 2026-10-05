import React from "react";

const statusStyles = {
  success: {
    background: "#dff3ea",
    color: "#2f8f6a",
    border: "1px solid #c9ebdc",
  },
  info: {
    background: "#fef1d8",
    color: "#d4871c",
    border: "1px solid #f7d9a1",
  },
  warning: {
    background: "#f9e2df",
    color: "#d15d4e",
    border: "1px solid #f0b9b1",
  },
  neutral: {
    background: "#f1edf8",
    color: "#5b4d7d",
    border: "1px solid #dfd5ee",
  },
  primary: {
    background: "#e4ebff",
    color: "#4355d1",
    border: "1px solid #c9d4ff",
  },
};

const UserActionCard = ({
  title,
  viewAllLabel,
  onViewAll,
  items = [],
  footerLabel,
  onFooterClick,
  cardStyle = {},
}) => {
  return (
    <div
      className="userActionCard"
      style={cardStyle}
    >
      <div className="uac-header">
        <h3 className="uac-title">
          {title}
        </h3>

        {viewAllLabel ? (
          <button
            type="button"
            onClick={onViewAll}
            className="uac-view-all-btn"
          >
            {viewAllLabel}
          </button>
        ) : null}
      </div>

      <div className="uac-items-list">
        {items.slice(0, 3).map((item, index) => {
          const badgeStyle = statusStyles[item.statusVariant] || statusStyles.neutral;

          return (
            <div
              key={`${item.title}-${index}`}
              onClick={item.onClick}
              className={`uac-item-row ${item.onClick ? "clickable" : ""}`}
            >
              {item.date ? (
                <div className="uac-date-box">
                  <div className="uac-date-day">
                    {item.date.day}
                  </div>
                  <div className="uac-date-month">
                    {item.date.month}
                  </div>
                </div>
              ) : (
                <div className="uac-icon-box">
                  {item.icon || item.title?.charAt(0) || "•"}
                </div>
              )}

              <div className={`uac-content-wrap ${item.status ? "center-items" : ""}`}>
                <div className="uac-text-group">
                  <div className="uac-item-title">
                    {item.title}
                  </div>

                  {item.subTitle ? (
                    <div className="uac-item-subtitle">
                      {item.subTitle}
                    </div>
                  ) : null}

                  {item.metaText ? (
                    <div className="uac-item-meta">
                      {item.metaText}
                    </div>
                  ) : null}
                </div>

                {item.status ? (
                  <span
                    className="uac-badge"
                    style={badgeStyle}
                  >
                    {item.status}
                  </span>
                ) : null}

                {item.cta ? (
                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      if (item.onCtaClick) item.onCtaClick(e);
                      else if (item.onClick) item.onClick(e);
                    }}
                    className="uac-cta-btn"
                  >
                    {item.cta}
                  </button>
                ) : null}
              </div>
            </div>
          );
        })}
      </div>

      {footerLabel ? (
        <button
          type="button"
          onClick={onFooterClick}
          className="uac-footer-btn"
        >
          <span>{footerLabel}</span>
          <span className="uac-footer-arrow">→</span>
        </button>
      ) : null}
    </div>
  );
};

export default UserActionCard;
