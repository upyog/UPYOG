import React, { useEffect, useState, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, useLocation } from "react-router-dom";
import { openUploadedDocument, downloadDocument } from "../../utils";

const NotificationsAndWhatsNew = ({ variant, parentRoute }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [activeTab, setActiveTab] = useState("ALL");
  const [searchQuery, setSearchQuery] = useState("");

  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const {
    data: { unreadCount: preVisitUnseenNotificationCount } = {},
    isSuccess: preVisitUnseenNotificationCountLoaded,
    refetch,
  } = Digit.Hooks.useNotificationCount({
    tenantId,
    config: {
      enabled: !!Digit.UserService?.getUser()?.access_token,
    },
  });

  const { mutate, isSuccess } = Digit.Hooks.useClearNotifications();

  useEffect(() => {
    if (isSuccess) {
      refetch();
    }
  }, [isSuccess]);

  useEffect(() => {
    if (preVisitUnseenNotificationCount && tenantId) {
      mutate({ tenantId });
    }
  }, [tenantId, preVisitUnseenNotificationCount]);

  const { data: rawEventsData, isLoading: EventsDataLoading } = Digit.Hooks.useEvents({ tenantId, variant });

  const eventsList = useMemo(() => {
    return Array.isArray(rawEventsData) ? rawEventsData : [];
  }, [rawEventsData]);

  // Counts by category
  const counts = useMemo(() => {
    let announcements = 0;
    let events = 0;
    eventsList.forEach((e) => {
      if (e?.eventType === "EVENTSONGROUND") {
        events += 1;
      } else {
        announcements += 1;
      }
    });
    return {
      all: eventsList.length,
      announcements,
      events,
    };
  }, [eventsList]);

  // Filtered notifications
  const filteredEvents = useMemo(() => {
    return eventsList.filter((item) => {
      const isEventOnGround = item?.eventType === "EVENTSONGROUND";
      if (activeTab === "ANNOUNCEMENTS" && isEventOnGround) return false;
      if (activeTab === "EVENTS" && !isEventOnGround) return false;

      if (searchQuery.trim()) {
        const query = searchQuery.toLowerCase().trim();
        const title = (item?.header || item?.name || "").toLowerCase();
        const desc = (item?.eventNotificationText || item?.description || "").toLowerCase();
        const locationText = (item?.eventDetails?.address || "").toLowerCase();
        return title.includes(query) || desc.includes(query) || locationText.includes(query);
      }
      return true;
    });
  }, [eventsList, activeTab, searchQuery]);

  const onEventCardClick = (id) => {
    navigate(parentRoute + "/events/details/" + id);
  };

  const getTransformedLocale = (label) => {
    if (typeof label === "number") return label;
    return label && label.toUpperCase().replace(/[.:-\s\/]/g, "_");
  };

  const pageTitle = variant === "whats-new" 
    ? t("CS_HEADER_WHATSNEW") 
    : t("CS_HEADER_NOTIFICATIONS");

  if (EventsDataLoading) {
    return (
      <div className="CitizenEngagementNotificationWrapper">
        <div className="notif-header-section">
          <div className="notif-title-row">
            <div className="notif-title-group">
              <div className="notif-icon-badge">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                  <path d="M13.73 21a2 2 0 0 1-3.46 0" />
                </svg>
              </div>
              <div>
                <h1 className="notif-page-title">{pageTitle}</h1>
                <p className="notif-page-subtitle">{t("CS_NOTIFICATIONS_SUBTITLE", "Stay informed with real-time updates, alerts, and public notices.")}</p>
              </div>
            </div>
          </div>
        </div>
        <div className="notif-loading-container">
          <div className="notif-skeleton-card" />
          <div className="notif-skeleton-card" />
          <div className="notif-skeleton-card" />
        </div>
      </div>
    );
  }

  return (
    <div className="CitizenEngagementNotificationWrapper">
      {/* Header Banner */}
      <div className="notif-header-section">
        <div className="notif-title-row">
          <div className="notif-title-group">
            <div className="notif-icon-badge">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                <path d="M13.73 21a2 2 0 0 1-3.46 0" />
              </svg>
            </div>
            <div>
              <div className="notif-title-flex">
                <h1 className="notif-page-title">{pageTitle}</h1>
                {eventsList.length > 0 && (
                  <span className="notif-count-pill">{eventsList.length}</span>
                )}
              </div>
              <p className="notif-page-subtitle">
                {t("CS_NOTIFICATIONS_SUBTITLE", "Stay informed with real-time updates, announcements, and city notices.")}
              </p>
            </div>
          </div>

          {eventsList.length > 0 && (
            <div className="notif-search-box">
              <svg className="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="11" cy="11" r="8" />
                <line x1="21" y1="21" x2="16.65" y2="16.65" />
              </svg>
              <input
                type="text"
                placeholder={t("CS_SEARCH_NOTIFICATIONS_PLACEHOLDER", "Search notifications...")}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              {searchQuery && (
                <button
                  type="button"
                  className="search-clear-btn"
                  onClick={() => setSearchQuery("")}
                  title="Clear search"
                >
                  ✕
                </button>
              )}
            </div>
          )}
        </div>

        {/* Filter Tabs */}
        {eventsList.length > 0 && (
          <div className="notif-tabs-bar">
            <button
              type="button"
              className={`notif-tab-btn ${activeTab === "ALL" ? "active" : ""}`}
              onClick={() => setActiveTab("ALL")}
            >
              <span>{t("CS_NOTIF_TAB_ALL", "All Updates")}</span>
              <span className="tab-badge">{counts.all}</span>
            </button>
            <button
              type="button"
              className={`notif-tab-btn ${activeTab === "ANNOUNCEMENTS" ? "active" : ""}`}
              onClick={() => setActiveTab("ANNOUNCEMENTS")}
            >
              <span>{t("CS_NOTIF_TAB_ANNOUNCEMENTS", "Announcements & Alerts")}</span>
              <span className="tab-badge">{counts.announcements}</span>
            </button>
            {counts.events > 0 && (
              <button
                type="button"
                className={`notif-tab-btn ${activeTab === "EVENTS" ? "active" : ""}`}
                onClick={() => setActiveTab("EVENTS")}
              >
                <span>{t("CS_NOTIF_TAB_EVENTS", "Events & Drives")}</span>
                <span className="tab-badge">{counts.events}</span>
              </button>
            )}
          </div>
        )}
      </div>

      {/* Notifications List */}
      <div className="notif-list-container">
        {filteredEvents.length > 0 ? (
          filteredEvents.map((item, index) => {
            const key = item.uuid || item.id || index;
            const isEventOnGround = item?.eventType === "EVENTSONGROUND";
            const isBroadcast = item?.eventType === "BROADCAST";
            const isSystem = item?.eventType === "SYSTEMGENERATED";

            const uploadedDocuments = item?.eventDetails?.documents || [];
            const timeAgo = item?.timePastAfterEventCreation 
              ? `${item.timePastAfterEventCreation} ${t(item.timeApproxiamationInUnits || "HOURS")}`
              : "";

            return (
              <div
                key={key}
                className={`notif-card ${isEventOnGround ? "notif-card--event" : ""} ${isBroadcast ? "notif-card--broadcast" : ""}`}
                onClick={() => {
                  if (isEventOnGround && item?.id) {
                    onEventCardClick(item.id);
                  }
                }}
              >
                {/* Card Top Row: Badge & Timestamp */}
                <div className="notif-card-header">
                  <div className="notif-type-tag">
                    {isEventOnGround ? (
                      <span className="tag-pill tag-pill--event">
                        <span className="tag-dot" />
                        {t("CS_NOTIF_TAG_EVENT", "Event / Drive")}
                      </span>
                    ) : isBroadcast ? (
                      <span className="tag-pill tag-pill--broadcast">
                        <span className="tag-dot" />
                        {t("CS_NOTIF_TAG_BROADCAST", "Public Notice")}
                      </span>
                    ) : isSystem ? (
                      <span className="tag-pill tag-pill--system">
                        <span className="tag-dot" />
                        {t("CS_NOTIF_TAG_ALERT", "System Alert")}
                      </span>
                    ) : (
                      <span className="tag-pill tag-pill--general">
                        <span className="tag-dot" />
                        {t("CS_NOTIF_TAG_UPDATE", "Official Update")}
                      </span>
                    )}
                  </div>

                  {timeAgo && (
                    <div className="notif-time-badge">
                      <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10" />
                        <polyline points="12 6 12 12 16 14" />
                      </svg>
                      <span>{timeAgo}</span>
                    </div>
                  )}
                </div>

                {/* Card Body */}
                <div className="notif-card-body">
                  <h2 className="notif-item-title">
                    {t(item.header || item.name || "Notice")}
                  </h2>

                  {(item.eventNotificationText || item.description) && (
                    <p className="notif-item-desc">
                      {item.eventNotificationText || item.description}
                    </p>
                  )}

                  {/* Event-specific details */}
                  {isEventOnGround && item?.eventDetails && (
                    <div className="notif-event-meta-box">
                      {item.eventDetails.address && (
                        <div className="notif-meta-item">
                          <svg className="meta-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                            <circle cx="12" cy="10" r="3" />
                          </svg>
                          <span>{item.eventDetails.address}</span>
                        </div>
                      )}
                      {item.onGroundEventTimeRange && (
                        <div className="notif-meta-item">
                          <svg className="meta-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <circle cx="12" cy="12" r="10" />
                            <polyline points="12 6 12 12 16 14" />
                          </svg>
                          <span>{item.onGroundEventTimeRange}</span>
                        </div>
                      )}
                    </div>
                  )}

                  {/* Document Attachments */}
                  {uploadedDocuments?.length > 0 && (
                    <div className="notif-attachments-wrapper">
                      <span className="attachments-label">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48" />
                        </svg>
                        {t("CS_NOTIF_ATTACHMENTS", "Attachments")} ({uploadedDocuments.length})
                      </span>
                      <div className="attachments-grid">
                        {uploadedDocuments.map((doc, dIdx) => {
                          const docName = doc.fileName || `${t("CS_ATTACHMENT", "Document")} ${dIdx + 1}`;
                          return (
                            <div key={dIdx} className="attachment-chip">
                              <span className="attachment-name" title={docName}>{docName}</span>
                              <div className="attachment-actions">
                                {doc.fileStoreId && (
                                  <button
                                    type="button"
                                    className="attach-btn attach-btn--view"
                                    onClick={(e) => {
                                      e.stopPropagation();
                                      openUploadedDocument(doc.fileStoreId, docName);
                                    }}
                                  >
                                    {t("CE_DOCUMENT_VIEW_LINK", "View")}
                                  </button>
                                )}
                                {doc.fileStoreId && (
                                  <button
                                    type="button"
                                    className="attach-btn attach-btn--download"
                                    onClick={(e) => {
                                      e.stopPropagation();
                                      downloadDocument(doc.fileStoreId, docName);
                                    }}
                                  >
                                    {t("CE_DOCUMENT_DOWNLOAD_LINK", "Download")}
                                  </button>
                                )}
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  )}
                </div>

                {/* Card Action Footer */}
                {item?.actions && item.actions.length > 0 && (
                  <div className="notif-card-footer">
                    {item.actions.map((act, aIdx) => {
                      const actionUrl = (act?.actionUrl || "").replace("digit-ui", "upyog-ui");
                      const actionLabel = t(`CS_COMMON_${getTransformedLocale(act?.code)}`) || act?.code || t("COMMON_VIEW_DETAILS", "View Details");
                      return (
                        <a
                          key={aIdx}
                          href={actionUrl}
                          className="notif-action-btn"
                          onClick={(e) => e.stopPropagation()}
                        >
                          <span>{actionLabel}</span>
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                            <polyline points="9 18 15 12 9 6" />
                          </svg>
                        </a>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          })
        ) : (
          /* Empty State */
          <div className="notif-empty-state">
            <div className="notif-empty-icon-wrap">
              <svg width="42" height="42" viewBox="0 0 24 24" fill="none" stroke="#584F74" strokeWidth="1.75">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                <path d="M13.73 21a2 2 0 0 1-3.46 0" />
                <circle cx="12" cy="8" r="1" fill="#584F74" />
              </svg>
            </div>
            <h3 className="notif-empty-title">
              {searchQuery ? t("CS_NO_SEARCH_NOTIFS", "No matching notifications") : t("CS_ALL_CAUGHT_UP", "You're all caught up!")}
            </h3>
            <p className="notif-empty-desc">
              {searchQuery
                ? t("CS_NO_SEARCH_NOTIFS_DESC", "No notices or updates match your search keyword. Try a different search term or clear the filter.")
                : t("CS_NO_NOTIFS_DESC", "There are no new notifications or notices at this moment. You will be notified here whenever there is an update.")}
            </p>
            {searchQuery ? (
              <button
                type="button"
                className="notif-empty-cta-btn"
                onClick={() => setSearchQuery("")}
              >
                {t("CS_CLEAR_SEARCH", "Clear Search")}
              </button>
            ) : (
              <button
                type="button"
                className="notif-empty-cta-btn"
                onClick={() => navigate("/upyog-ui/citizen")}
              >
                {t("CS_GO_TO_HOME", "Go to Home")}
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default NotificationsAndWhatsNew;

