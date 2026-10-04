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
      style={{
        background: "#fff",
        border: "1px solid #D6CDE4",
        borderRadius: "8px",
        padding: "10px",
        width: "100%",
        display: "flex",
        flexDirection: "column",
        boxSizing: "border-box",
        ...cardStyle,
      }}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          marginBottom: "18px",
          gap: "12px",
        }}
      >
        <h3
          style={{
            margin: 0,
            fontSize: "20px",
            lineHeight: "1.15",
            fontWeight: 700,
            color: "#1f1b2d",
          }}
        >
          {title}
        </h3>

        {viewAllLabel ? (
          <button
            type="button"
            onClick={onViewAll}
            style={{
              background: "transparent",
              border: "none",
              borderBottom: "2px solid #2b2b2b",
              padding: 0,
              fontSize: "18px",
              fontWeight: 600,
              cursor: "pointer",
              color: "#2c2f36",
              textDecoration: "none",
            }}
          >
            {viewAllLabel}
          </button>
        ) : null}
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
        {items.slice(0,3).map((item, index) => {
          const badgeStyle = statusStyles[item.statusVariant] || statusStyles.neutral;

          return (
            <div
              key={`${item.title}-${index}`}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "14px",
                borderBottom: "1px solid #e8e2ef",
                paddingBottom: "12px",
              }}
            >
              {item.date ? (
                <div
                  style={{
                    minWidth: "68px",
                    textAlign: "center",
                    color: "#5d4d88",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    justifyContent: "center",
                    paddingTop: "4px",
                  }}
                >
                  <div
                    style={{
                      fontSize: "16px",
                      fontWeight: 700,
                      lineHeight: 1,
                      marginBottom: "6px",
                    }}
                  >
                    {item.date.day}
                  </div>
                  <div
                    style={{
                      fontSize: "12px",
                      fontWeight: 700,
                      letterSpacing: "0.08em",
                    }}
                  >
                    {item.date.month}
                  </div>
                </div>
              ) : (
                <div
                  style={{
                    width: "42px",
                    height: "42px",
                    borderRadius: "12px",
                    background: "#ece7f6",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    color: "#4a3d73",
                    fontSize: "18px",
                    fontWeight: 700,
                    flexShrink: 0,
                  }}
                >
                  {item.icon || item.title?.charAt(0) || "•"}
                </div>
              )}

              <div
                style={{
                  flex: 1,
                  display: "flex",
                  alignItems: item.status ? "center" : "flex-start",
                  justifyContent: "space-between",
                  gap: "10px",
                  minWidth: 0,
                }}
              >
                <div
                  style={{
                    minWidth: 0,
                    flex: 1,
                  }}
                >
                  <div
                    style={{
                      fontSize: "14px",
                      fontWeight: 600,
                      color: "#1f1b2d",
                      lineHeight: "1.4",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                    }}
                  >
                    {item.title}
                  </div>

                  {item.subTitle ? (
                    <div
                      style={{
                        fontSize: "13px",
                        color: "#4b4962",
                        marginTop: "4px",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap",
                      }}
                    >
                      {item.subTitle}
                    </div>
                  ) : null}

                  {item.metaText ? (
                    <div
                      style={{
                        fontSize: "15px",
                        color: "#4b4962",
                        marginTop: "4px",
                        display: "flex",
                        flexWrap: "wrap",
                        gap: "8px",
                      }}
                    >
                      {item.metaText}
                    </div>
                  ) : null}
                </div>

                {item.status ? (
                  <span
                    style={{
                      ...badgeStyle,
                      borderRadius: "8px",
                      padding: "5px 10px",
                      fontSize: "12px",
                      fontWeight: 600,
                      whiteSpace: "nowrap",
                    }}
                  >
                    {item.status}
                  </span>
                ) : null}

                {item.cta ? (
                  <button
                    type="button"
                    style={{
                      background: "#4b4667",
                      border: "none",
                      color: "#fff",
                      borderRadius: "10px",
                      padding: "6px 12px",
                      fontSize: "14px",
                      fontWeight: 700,
                      cursor: "pointer",
                    }}
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
          style={{
            background: "transparent",
            border: "none",
            color: "#1f1b2d",
            fontSize: "14px",
            fontWeight: 700,
            textAlign: "left",
            cursor: "pointer",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
          }}
        >
          <span>{footerLabel}</span>
          <span style={{ fontSize: "34px", lineHeight: 1 }}>→</span>
        </button>
      ) : null}
    </div>
  );
};

export default UserActionCard;
