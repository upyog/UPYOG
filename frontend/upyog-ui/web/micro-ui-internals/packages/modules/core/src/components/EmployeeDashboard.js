import React, { useEffect, useMemo, useState } from "react";
import HeaderBoxSection from "./HeaderBoxSection";
import UserActionLayout from "./UserActionLayout";
import { useTranslation } from "react-i18next";

const EmployeeDashboard = () => {
  const [isMobile, setIsMobile] = useState(typeof window !== "undefined" ? window.innerWidth < 900 : false);
  const currentType = "V3";
  const { t } = useTranslation();
  
  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth < 900);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const styles = useMemo(
    () => ({
      page: {
        height: "inherit",
        overflowY: "scroll",
        margin: "0px 16px",
        // padding: isMobile ? "12px" : "20px 24px 92px",
        fontFamily: "Roboto, 'Segoe UI', sans-serif",
        
      },
      shell: {
        maxWidth: "1260px",
        margin: "0 auto",
        border: "1px solid #e6eaf3",
        background: "#ffffff",
        borderRadius: "16px",
        boxShadow: "0 10px 35px rgba(20, 28, 45, 0.05)",
        padding: isMobile ? "12px" : "16px",
      },
      hero: {
        borderRadius: "12px",
        background: "linear-gradient(90deg, #1f3d83 0%, #162f66 62%, #102149 100%)",
        color: "#ffffff",
        padding: isMobile ? "14px" : "16px 18px",
        display: "flex",
        alignItems: isMobile ? "flex-start" : "center",
        justifyContent: "space-between",
        flexDirection: isMobile ? "column" : "row",
        gap: isMobile ? "12px" : "8px",
      },
      heroMeta: {
        fontSize: "10px",
        letterSpacing: "0.5px",
        opacity: 0.82,
        textTransform: "uppercase",
        marginBottom: "4px",
      },
      heroTitle: {
        margin: 0,
        fontWeight: 700,
        fontSize: isMobile ? "24px" : "32px",
        lineHeight: 1.15,
      },
      heroSubTitle: {
        marginTop: "6px",
        marginBottom: 0,
        fontSize: isMobile ? "12px" : "13px",
        color: "rgba(255,255,255,0.84)",
      },
      heroButtons: {
        display: "flex",
        gap: "8px",
      },
      heroButton: {
        border: "none",
        borderRadius: "999px",
        fontWeight: 600,
        fontSize: "11px",
        padding: "9px 12px",
        color: "#ffffff",
        whiteSpace: "nowrap",
      },
      sectionGap: { marginTop: "12px" },
      kpiGrid: {
        display: "grid",
        gridTemplateColumns: isMobile ? "1fr" : "repeat(5, minmax(0, 1fr))",
        gap: "10px",
      },
      kpiCard: {
        borderRadius: "10px",
        padding: "14px 16px",
        border: "1px solid #edf0f5",
      },
      kpiValue: {
        margin: 0,
        fontSize: "34px",
        lineHeight: 1,
        color: "#1f2a44",
        fontWeight: 700,
      },
      kpiLabel: {
        marginTop: "6px",
        fontSize: "12px",
        fontWeight: 600,
        color: "#4f5a72",
      },
      quickGrid: {
        display: "grid",
        gridTemplateColumns: isMobile ? "1fr" : "repeat(4, minmax(0, 1fr))",
        gap: "10px",
      },
      card: {
        background: "#ffffff",
        border: "1px solid #edf0f5",
        borderRadius: "10px",
        boxShadow: "0 1px 2px rgba(16, 34, 68, 0.04)",
      },
      quickCard: {
        padding: "14px",
        minHeight: "84px",
      },
      iconChip: {
        width: "28px",
        height: "28px",
        borderRadius: "8px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontWeight: 700,
        fontSize: "13px",
        marginBottom: "10px",
      },
      quickTitle: {
        margin: 0,
        fontSize: "13px",
        color: "#2a3551",
        fontWeight: 700,
      },
      quickSub: {
        marginTop: "4px",
        marginBottom: 0,
        fontSize: "11px",
        color: "#7a849a",
      },
      middleGrid: {
        display: "grid",
        gridTemplateColumns: isMobile ? "1fr" : "2fr 1fr",
        gap: "12px",
      },
      panelHead: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "12px 14px",
        borderBottom: "1px solid #edf0f5",
      },
      panelTitle: {
        margin: 0,
        fontSize: "15px",
        fontWeight: 700,
        color: "#25304a",
      },
      panelLink: {
        fontSize: "12px",
        color: "#4b66d3",
        fontWeight: 600,
      },
      tableWrapper: {
        overflowX: "auto",
      },
      table: {
        width: "100%",
        borderCollapse: "collapse",
        minWidth: isMobile ? "720px" : "unset",
      },
      th: {
        padding: "10px 14px",
        textAlign: "left",
        fontSize: "11px",
        color: "#8b95ab",
        textTransform: "uppercase",
        letterSpacing: "0.4px",
        borderBottom: "1px solid #edf0f5",
      },
      td: {
        padding: "12px 14px",
        fontSize: "12px",
        color: "#3c4762",
        borderBottom: "1px solid #f2f4f8",
      },
      appLink: {
        color: "#4b66d3",
        fontWeight: 700,
      },
      statusBadge: {
        display: "inline-flex",
        alignItems: "center",
        padding: "4px 10px",
        borderRadius: "999px",
        fontSize: "11px",
        fontWeight: 700,
      },
      sideStack: {
        display: "grid",
        gridTemplateRows: "1fr 1fr",
        gap: "12px",
      },
      sideBody: {
        padding: "6px 14px 10px",
      },
      timelineRow: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "flex-start",
        gap: "10px",
        padding: "10px 0",
        borderBottom: "1px solid #f2f4f8",
      },
      timelineTitle: {
        margin: 0,
        fontSize: "13px",
        fontWeight: 700,
        color: "#2f3a55",
      },
      timelineSub: {
        marginTop: "3px",
        marginBottom: 0,
        fontSize: "11px",
        color: "#818aa1",
      },
      timelineTime: {
        fontSize: "11px",
        color: "#9aa2b4",
        whiteSpace: "nowrap",
      },
      serviceHeader: {
        marginTop: "14px",
      },
      serviceTitle: {
        margin: 0,
        fontSize: "24px",
        lineHeight: 1.2,
        color: "#25304a",
        fontWeight: 700,
      },
      serviceMeta: {
        marginTop: "4px",
        marginBottom: 0,
        fontSize: "12px",
        color: "#7b8498",
      },
      serviceGrid: {
        marginTop: "12px",
        display: "grid",
        gridTemplateColumns: isMobile ? "1fr" : "repeat(4, minmax(0, 1fr))",
        gap: "10px",
      },
      moduleCard: {
        padding: "12px 14px",
        minHeight: "102px",
      },
      moduleHead: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      },
      moduleCount: {
        fontSize: "30px",
        lineHeight: 1,
        fontWeight: 700,
      },
      moduleName: {
        marginTop: "10px",
        marginBottom: 0,
        fontSize: "14px",
        fontWeight: 700,
        color: "#2f3a55",
      },
      moduleSub: {
        marginTop: "4px",
        marginBottom: "8px",
        fontSize: "11px",
        color: "#818aa1",
      },
      tinyPill: {
        display: "inline-flex",
        padding: "4px 8px",
        borderRadius: "999px",
        fontWeight: 700,
        fontSize: "10px",
      },
      viewAllTile: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        gap: "6px",
        color: "#5b6781",
      },
    }),
    [isMobile]
  );

  const kpis = [
    { value: 124, label: "Total Assigned", bg: "#eef0ff" },
    { value: 7, label: "Pending Payment", bg: "#f0ecff" },
    { value: 14, label: "Under Review", bg: "#fff1df" },
    { value: 8, label: "Pending Docs", bg: "#ffe9eb" },
    { value: 2, label: "Overdue", bg: "#e8f7ed" },
  ];

  const quickActions = [
    { icon: "R", iconBg: "#efeafe", title: "Review Building Plans", subtitle: "9 pending in your queue" },
    { icon: "S", iconBg: "#efeafe", title: "Schedule Inspection", subtitle: "4 site visits this week" },
    { icon: "V", iconBg: "#fff3e5", title: "Verify Payments", subtitle: "7 awaiting confirmation" },
    { icon: "G", iconBg: "#e8f7ed", title: "Generate Report", subtitle: "Weekly SLA summary" },
  ];

  const worklist = [
    {
      application: "BP-CT-2026-06-02-253990",
      module: "BPA (Category-B)",
      status: "Approval Pending",
      statusType: "warning",
      lastUpdate: "18/06/2026",
    },
    {
      application: "BLR-CT-2026-06-04-805675",
      module: "BLR (Category-A)",
      status: "Field Inspection",
      statusType: "info",
      lastUpdate: "17/06/2026",
    },
    {
      application: "TL-TST-2026-06-06-171467",
      module: "New Trade License",
      status: "Pending Payment",
      statusType: "danger",
      lastUpdate: "06/06/2026",
    },
    {
      application: "BP-TST-2025-03-27-203124",
      module: "BPA (Category-D)",
      status: "Approval Pending",
      statusType: "warning",
      lastUpdate: "30/04/2025",
    },
  ];

  const notifications = [
    {
      title: "New document uploaded",
      subtitle: "BP-CT-2026-06-02-253990",
      time: "10:30 AM",
    },
    {
      title: "Payment confirmed",
      subtitle: "TL-TST-2026-06-06-171466",
      time: "9:12 AM",
    },
  ];

  const todaysSchedule = [
    {
      title: "Site inspection - Connaught Colony",
      subtitle: "Layout Plan Approval",
      time: "11:00 AM",
    },
    {
      title: "Team review meeting",
      subtitle: "BPA desk sync",
      time: "3:00 PM",
    },
  ];

  const serviceModules = [
    {
      count: 18,
      color: "#5a57f4",
      name: "Building Plan Approval",
      sub: "6 approval pending - 3 inspections",
      tag: "2 overdue",
      tagBg: "#ffe8ea",
      tagColor: "#dc415f",
      icon: "B",
      iconBg: "#efeafe",
    },
    {
      count: 9,
      color: "#ef7d3d",
      name: "Layout Plan Approval",
      sub: "4 field inspection pending",
      tag: "Due this week",
      tagBg: "#fff1df",
      tagColor: "#d56c21",
      icon: "L",
      iconBg: "#fff3e5",
    },
    {
      count: 7,
      color: "#5a57f4",
      name: "Trade License",
      sub: "7 pending payment",
      tag: "Payment stalled",
      tagBg: "#ffe8ea",
      tagColor: "#dc415f",
      icon: "T",
      iconBg: "#efeafe",
    },
    {
      count: 4,
      color: "#1ea04a",
      name: "Property Tax",
      sub: "All up to date",
      tag: "On track",
      tagBg: "#e8f7ed",
      tagColor: "#209d4a",
      icon: "P",
      iconBg: "#e8f7ed",
    },
    {
      count: 3,
      color: "#5a57f4",
      name: "Water & Sewerage",
      sub: "2 under review",
      tag: "Low volume",
      tagBg: "#eef0ff",
      tagColor: "#5f63d8",
      icon: "W",
      iconBg: "#eef0ff",
    },
    {
      count: 2,
      color: "#ef7d3d",
      name: "Marriage Registration",
      sub: "1 pending docs",
      tag: "Low volume",
      tagBg: "#fff1df",
      tagColor: "#d56c21",
      icon: "M",
      iconBg: "#fff3e5",
    },
    {
      count: 5,
      color: "#5a57f4",
      name: "FSM / Septic Tank",
      sub: "3 scheduled today",
      tag: "Field-heavy",
      tagBg: "#eef0ff",
      tagColor: "#5f63d8",
      icon: "F",
      iconBg: "#efeafe",
    },
  ];

  const getStatusStyle = (type) => {
    if (type === "danger") return { background: "#ffe8ea", color: "#dc415f" };
    if (type === "info") return { background: "#ecebff", color: "#6264ea" };
    return { background: "#fff1df", color: "#d56c21" };
  };

  const renderV1 = () => (
    <div style={styles.page}>
      <div style={styles.shell}>
        <section style={styles.hero}>
          <div>
            <p style={styles.heroMeta}>Tuesday, 20 Aug - Building Plan Approval Desk</p>
            <h1 style={styles.heroTitle}>Good morning, Shubham</h1>
            <p style={styles.heroSubTitle}>You have 14 applications under review and 2 overdue items that need attention today.</p>
          </div>
          <div style={styles.heroButtons}>
            <button type="button" style={{ ...styles.heroButton, background: "#f28a3f" }}>
              View Overdue (2)
            </button>
            <button type="button" style={{ ...styles.heroButton, background: "#2b3b72" }}>
              Today&apos;s Schedule
            </button>
          </div>
        </section>

        <section style={{ ...styles.sectionGap, ...styles.kpiGrid }}>
          {kpis.map((item) => (
            <div key={item.label} style={{ ...styles.kpiCard, background: item.bg }}>
              <p style={styles.kpiValue}>{item.value}</p>
              <p style={styles.kpiLabel}>{item.label}</p>
            </div>
          ))}
        </section>

        <section style={{ ...styles.sectionGap, ...styles.quickGrid }}>
          {quickActions.map((item) => (
            <article key={item.title} style={{ ...styles.card, ...styles.quickCard }}>
              <div style={{ ...styles.iconChip, background: item.iconBg }}>{item.icon}</div>
              <p style={styles.quickTitle}>{item.title}</p>
              <p style={styles.quickSub}>{item.subtitle}</p>
            </article>
          ))}
        </section>

        <section style={{ ...styles.sectionGap, ...styles.middleGrid }}>
          <article style={styles.card}>
            <div style={styles.panelHead}>
              <h2 style={styles.panelTitle}>My Worklist</h2>
              <span style={styles.panelLink}>View all</span>
            </div>
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th style={styles.th}>Application</th>
                    <th style={styles.th}>Module</th>
                    <th style={styles.th}>Status</th>
                    <th style={styles.th}>Last Update</th>
                  </tr>
                </thead>
                <tbody>
                  {worklist.map((row) => (
                    <tr key={row.application}>
                      <td style={{ ...styles.td, ...styles.appLink }}>{row.application}</td>
                      <td style={styles.td}>{row.module}</td>
                      <td style={styles.td}>
                        <span style={{ ...styles.statusBadge, ...getStatusStyle(row.statusType) }}>{row.status}</span>
                      </td>
                      <td style={styles.td}>{row.lastUpdate}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </article>

          <div style={styles.sideStack}>
            <article style={styles.card}>
              <div style={styles.panelHead}>
                <h2 style={styles.panelTitle}>Notifications</h2>
                <span style={styles.panelLink}>All</span>
              </div>
              <div style={styles.sideBody}>
                {notifications.map((item) => (
                  <div key={item.title} style={styles.timelineRow}>
                    <div>
                      <p style={styles.timelineTitle}>{item.title}</p>
                      <p style={styles.timelineSub}>{item.subtitle}</p>
                    </div>
                    <span style={styles.timelineTime}>{item.time}</span>
                  </div>
                ))}
              </div>
            </article>

            <article style={styles.card}>
              <div style={styles.panelHead}>
                <h2 style={styles.panelTitle}>Today&apos;s Schedule</h2>
              </div>
              <div style={styles.sideBody}>
                {todaysSchedule.map((item) => (
                  <div key={item.title} style={styles.timelineRow}>
                    <div>
                      <p style={styles.timelineTitle}>{item.title}</p>
                      <p style={styles.timelineSub}>{item.subtitle}</p>
                    </div>
                    <span style={styles.timelineTime}>{item.time}</span>
                  </div>
                ))}
              </div>
            </article>
          </div>
        </section>

        <section style={styles.serviceHeader}>
          <h2 style={styles.serviceTitle}>Your service modules</h2>
          <p style={styles.serviceMeta}>31 items need action across 4 active modules</p>
        </section>

        <section style={styles.serviceGrid}>
          {serviceModules.map((item) => (
            <article key={item.name} style={{ ...styles.card, ...styles.moduleCard }}>
              <div style={styles.moduleHead}>
                <div style={{ ...styles.iconChip, background: item.iconBg, marginBottom: 0 }}>{item.icon}</div>
                <span style={{ ...styles.moduleCount, color: item.color }}>{item.count}</span>
              </div>
              <p style={styles.moduleName}>{item.name}</p>
              <p style={styles.moduleSub}>{item.sub}</p>
              <span style={{ ...styles.tinyPill, background: item.tagBg, color: item.tagColor }}>{item.tag}</span>
            </article>
          ))}

          <article style={{ ...styles.card, ...styles.moduleCard, ...styles.viewAllTile }}>
            <div style={{ fontSize: "28px", lineHeight: 1 }}>+</div>
            <div style={{ fontWeight: 700, fontSize: "13px" }}>View all 11 modules</div>
          </article>
        </section>
      </div>
    </div>
  );

  const renderV2 = () => (
    <div style={styles.page}>
      <div style={{ ...styles.shell, padding: isMobile ? "12px" : "18px" }}>
        <div style={{ display: "grid", gridTemplateColumns: isMobile ? "1fr" : "1.35fr 0.85fr", gap: "12px" }}>
          <div>
            <section style={{ ...styles.hero, marginBottom: "12px" }}>
              <div>
                <p style={styles.heroMeta}>Department overview</p>
                <h1 style={styles.heroTitle}>Operations Dashboard</h1>
                <p style={styles.heroSubTitle}>Street-level service requests, approvals, and team performance across your office.</p>
              </div>
              <div style={styles.heroButtons}>
                <button type="button" style={{ ...styles.heroButton, background: "#f28a3f" }}>Generate report</button>
                <button type="button" style={{ ...styles.heroButton, background: "#2b3b72" }}>Upcoming work</button>
              </div>
            </section>

            <section style={{ ...styles.sectionGap, ...styles.kpiGrid }}>
              {kpis.map((item) => (
                <div key={item.label} style={{ ...styles.kpiCard, background: item.bg }}>
                  <p style={styles.kpiValue}>{item.value}</p>
                  <p style={styles.kpiLabel}>{item.label}</p>
                </div>
              ))}
            </section>

            <section style={{ ...styles.sectionGap, ...styles.quickGrid }}>
              {quickActions.slice(0, 2).map((item) => (
                <article key={item.title} style={{ ...styles.card, ...styles.quickCard }}>
                  <div style={{ ...styles.iconChip, background: item.iconBg }}>{item.icon}</div>
                  <p style={styles.quickTitle}>{item.title}</p>
                  <p style={styles.quickSub}>{item.subtitle}</p>
                </article>
              ))}
            </section>

            <section style={{ ...styles.sectionGap }}>
              <article style={styles.card}>
                <div style={styles.panelHead}>
                  <h2 style={styles.panelTitle}>Priority queue</h2>
                  <span style={styles.panelLink}>View all</span>
                </div>
                <div style={styles.sideBody}>
                  {worklist.map((row) => (
                    <div key={row.application} style={{ ...styles.timelineRow, padding: "12px 0" }}>
                      <div>
                        <p style={styles.timelineTitle}>{row.application}</p>
                        <p style={styles.timelineSub}>{row.module}</p>
                      </div>
                      <span style={{ ...styles.statusBadge, ...getStatusStyle(row.statusType) }}>{row.status}</span>
                    </div>
                  ))}
                </div>
              </article>
            </section>
          </div>

          <div style={styles.sideStack}>
            <article style={styles.card}>
              <div style={styles.panelHead}>
                <h2 style={styles.panelTitle}>Attendance</h2>
              </div>
              <div style={styles.sideBody}>
                <div style={{ display: "grid", gridTemplateColumns: "repeat(2, minmax(0, 1fr))", gap: "8px" }}>
                  {["On duty", "On leave", "Pending review", "Field visits"].map((label, index) => (
                    <div key={label} style={{ background: index % 2 === 0 ? "#eef0ff" : "#fff1df", borderRadius: "8px", padding: "10px 8px", textAlign: "center" }}>
                      <div style={{ fontSize: "22px", fontWeight: 700, color: "#1f2a44" }}>{[12, 3, 8, 5][index]}</div>
                      <div style={{ fontSize: "11px", color: "#55627d", marginTop: "4px" }}>{label}</div>
                    </div>
                  ))}
                </div>
              </div>
            </article>

            <article style={styles.card}>
              <div style={styles.panelHead}>
                <h2 style={styles.panelTitle}>Today&apos;s Schedule</h2>
              </div>
              <div style={styles.sideBody}>
                {todaysSchedule.map((item) => (
                  <div key={item.title} style={styles.timelineRow}>
                    <div>
                      <p style={styles.timelineTitle}>{item.title}</p>
                      <p style={styles.timelineSub}>{item.subtitle}</p>
                    </div>
                    <span style={styles.timelineTime}>{item.time}</span>
                  </div>
                ))}
              </div>
            </article>

            <article style={styles.card}>
              <div style={styles.panelHead}>
                <h2 style={styles.panelTitle}>Notifications</h2>
                <span style={styles.panelLink}>All</span>
              </div>
              <div style={styles.sideBody}>
                {notifications.map((item) => (
                  <div key={item.title} style={styles.timelineRow}>
                    <div>
                      <p style={styles.timelineTitle}>{item.title}</p>
                      <p style={styles.timelineSub}>{item.subtitle}</p>
                    </div>
                    <span style={styles.timelineTime}>{item.time}</span>
                  </div>
                ))}
              </div>
            </article>
          </div>
        </div>

        <section style={{ ...styles.serviceHeader, marginTop: "18px" }}>
          <h2 style={styles.serviceTitle}>Your service modules</h2>
          <p style={styles.serviceMeta}>31 items need action across 4 active modules</p>
        </section>

        <section style={styles.serviceGrid}>
          {serviceModules.slice(0, 4).map((item) => (
            <article key={item.name} style={{ ...styles.card, ...styles.moduleCard }}>
              <div style={styles.moduleHead}>
                <div style={{ ...styles.iconChip, background: item.iconBg, marginBottom: 0 }}>{item.icon}</div>
                <span style={{ ...styles.moduleCount, color: item.color }}>{item.count}</span>
              </div>
              <p style={styles.moduleName}>{item.name}</p>
              <p style={styles.moduleSub}>{item.sub}</p>
              <span style={{ ...styles.tinyPill, background: item.tagBg, color: item.tagColor }}>{item.tag}</span>
            </article>
          ))}
        </section>
      </div>
    </div>
  );

  const renderV3 = () => (
    <div className="HomePageContainer" style={{ width: "100%", overflowX: "scroll" }}>
      <div className="HomePageWrapper">
         <div className="welcomeBanner">
          <p>Welcome, Shubham</p>
          <h1>What are you working on today?</h1>
          <div>
            <img src="/images/search.svg" alt="searc icon" />
            <input type="text" placeholder="Search by applicant number, ID, name or module..." />
            <button>Search</button>
          </div>
        </div>

        <div style={{ width: "100%", marginTop: "28px" }}>
          <HeaderBoxSection title="Quick Services" cards={returnConstants(t, "quickServiceCards", ()=>null)} variant="service" columns={4} />
          <UserActionLayout cards={returnConstants(t, "userActionCards", ()=>null)} showEmpty={true} />
          <HeaderBoxSection cards={returnConstants(t, "taskSummaryCards", ()=>null)} columns={4} />
        </div>

      </div>
    </div>
  )

  return renderV1()
};

function returnConstants(t, type, fn) {
  switch (type) {
    case "allCitizenServicesProps":
      return {
        header: t(fn?.headerLabel),
        sideOption: {
          name: t(fn?.sideOption?.name),
          onClick: () => navigate(fn?.sideOption?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
        },
        options: [
          {
            name: t(fn?.props?.[0]?.label),
            Icon: <ComplaintIcon />,
            onClick: () => navigate(fn?.props?.[0]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[1]?.label),
            Icon: <PTIcon className="fill-path-primary-main" />,
            onClick: () => navigate(fn?.props?.[1]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[2]?.label),
            Icon: <CaseIcon className="fill-path-primary-main" />,
            onClick: () => navigate(fn?.props?.[2]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          // {
          //     name: t("ACTION_TEST_WATER_AND_SEWERAGE"),
          //     Icon: <DropIcon/>,
          //     onClick: () => navigate("/upyog-ui/citizen")
          // },
          {
            name: t(fn?.props?.[3]?.label),
            Icon: <WSICon />,
            onClick: () => navigate(fn?.props?.[3]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
        ],
        styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%", marginRight: "16px" },
      }
    case "allInfoAndUpdatesProps":
      return {
        header: t(fn?.headerLabel),
        sideOption: {
          name: t(fn?.sideOption?.name),
          onClick: () => navigate(fn?.sideOption?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
        },
        options: [
          {
            name: t(fn?.props?.[0]?.label),
            Icon: <HomeIcon />,
            onClick: () => navigate(fn?.props?.[0]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[1]?.label),
            Icon: <Calender />,
            onClick: () => navigate(fn?.props?.[1]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[2]?.label),
            Icon: <DocumentIcon />,
            onClick: () => navigate(fn?.props?.[2]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[3]?.label),
            Icon: <DocumentIcon />,
            onClick: () => navigate(fn?.props?.[3]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          // {
          //     name: t("CS_COMMON_HELP"),
          //     Icon: <HelpIcon/>
          // }
        ],
        styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
      }
    case "taskSummaryCards":
      return [
        { title: "Task Assigned", value: 0, actionLabel: "View all tasks", onClick: () => fn("/upyog-ui/citizen") },
        { title: "Pending Payment", value: 0, actionLabel: "Pay now", onClick: () => fn("/upyog-ui/citizen") },
        { title: "Pending Documents", value: 0, actionLabel: "Take action", onClick: () => fn("/upyog-ui/citizen") },
        { title: "Under Review", value: 0, actionLabel: "Track status", onClick: () => fn("/upyog-ui/citizen") },
      ]
    case "quickServiceCards":
      return [
        { title: "PT", "text": "View & manage property tax", subtitle: "Property Tax", icon: "PT", onClick: () => fn("/upyog-ui/citizen/pt/property-tax") },
        { title: "TL", "text": "Apply for trade licence", subtitle: "Trade License", icon: "TL", onClick: () => fn("/upyog-ui/citizen/tl/trade-license") },
        { title: "BPA", "text": "Apply & track building plans", subtitle: "Building Plan Approval", icon: "BPA", onClick: () => fn("/upyog-ui/citizen/obps") },
        { title: "+", "text": "Expand all services", subtitle: "View All", icon: "+", onClick: () => fn("/upyog-ui/citizen") },
      ]
    case "userActionCards":
      return [
        {
          title: "Recent Applications",
          // viewAllLabel: "View All",
          onViewAll: () => fn("/upyog-ui/citizen/pt/property-tax"),
          footerLabel: "Go to My Applications",
          onFooterClick: () => fn("/upyog-ui/citizen/pt/property-tax"),
          items: [
            { title: "Property Tax Management", subTitle: "APR-2024-001234", status: "Paid", statusVariant: "success", icon: "🏠" },
            { title: "Community Hall Booking", subTitle: "APR-2024-001234", status: "Approved", statusVariant: "success", icon: "🏛️" },
            { title: "Advertisement Renewal", subTitle: "APR-2024-001234", status: "In Review", statusVariant: "info", icon: "📣" },
            { title: "Birth Registration", subTitle: "APR-2024-001234", status: "Rejected", statusVariant: "warning", icon: "👶" },
          ],
        },
        {
          title: "Notifications",
          // viewAllLabel: "View All",
          onViewAll: () => fn("/upyog-ui/citizen/engagement/notifications"),
          footerLabel: "View All Notifications",
          onFooterClick: () => fn("/upyog-ui/citizen/engagement/notifications"),
          items: [
            { title: "Property Tax Management", subTitle: "Receipt No: PT-2024-55677", icon: "🏠" },
            { title: "Community Hall booking request has...", subTitle: "Booking ID: CHB-2024-7788", icon: "🏛️" },
            { title: "Advertisement Renewed Successfully", subTitle: "Renewal ID: ADV-2024-7788", icon: "📣" },
            { title: "Birth Registration Completed", subTitle: "Registration ID: BR-2024-7788", icon: "👶" },
          ],
        },
        {
          title: "Upcoming Events",
          // viewAllLabel: "View All",
          onViewAll: () => fn("/upyog-ui/citizen/engagement/events"),
          footerLabel: "View All Upcoming Events",
          onFooterClick: () => fn("/upyog-ui/citizen/engagement/events"),
          items: [
            {
              title: "Clean City Drive",
              subTitle: "Connaught Place, Delhi",
              date: { day: "26", month: "JUN" },
              cta: "Register",
            },
            {
              title: "Clean City Drive",
              subTitle: "Connaught Place, Delhi",
              date: { day: "26", month: "JUN" },
              cta: "Register",
            },
            {
              title: "Clean City Drive",
              subTitle: "Connaught Place, Delhi",
              date: { day: "26", month: "JUN" },
              cta: "Register",
            },
          ],
        },
      ]
    default:
      return;
  }
}

export default EmployeeDashboard;
