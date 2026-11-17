import { useQuery, useQueryClient } from "@tanstack/react-query";

const USE_MOCK = true; // Set to false once the backend endpoint is connected

export const INITIAL_MOCK_ACTION_PLANS = [
    {
        applicationNumber: "WORKS-AP-2026-0001",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_LDH",
        scheme: "WORKS_SCHEME_FC15",
        subScheme: "WORKS_SUB_SCHEME_MUNICIPAL_ROAD_WORKS",
        approvedAmount: 5000000,
        approvedBy: "Chief Engineer",
        status: "PENDING_FOR_VERIFICATION",
        businessService: "WORKS",
        slaDays: 14,
        createdTime: Date.now() - 2 * 24 * 60 * 60 * 1000,
        projectsCount: 4,
        componentsCount: 9,
    },
    {
        applicationNumber: "WORKS-AP-2026-0002",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_LDH",
        scheme: "WORKS_SCHEME_SBM",
        subScheme: "WORKS_SUB_SCHEME_GENERAL_SBM",
        approvedAmount: 2500000,
        approvedBy: "Executive Engineer",
        status: "PENDING_APPROVAL",
        businessService: "WORKS",
        slaDays: 5,
        createdTime: Date.now() - 4 * 24 * 60 * 60 * 1000,
        projectsCount: 2,
        componentsCount: 5,
    },
    {
        applicationNumber: "WORKS-AP-2026-0003",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_ASR",
        scheme: "WORKS_SCHEME_MW",
        subScheme: "WORKS_SUB_SCHEME_STREET_LIGHTS",
        approvedAmount: 1800000,
        approvedBy: "Superintending Engineer",
        status: "APPROVED",
        businessService: "WORKS",
        slaDays: 0,
        createdTime: Date.now() - 8 * 24 * 60 * 60 * 1000,
        projectsCount: 1,
        componentsCount: 3,
    },
    {
        applicationNumber: "WORKS-AP-2026-0004",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_JAL",
        scheme: "WORKS_SCHEME_PMAY",
        subScheme: "WORKS_SUB_SCHEME_GENERAL_PMAY",
        approvedAmount: 7500000,
        approvedBy: "Commissioner",
        status: "INITIATED",
        businessService: "WORKS",
        slaDays: 21,
        createdTime: Date.now() - 1 * 24 * 60 * 60 * 1000,
        projectsCount: 5,
        componentsCount: 12,
    },
    {
        applicationNumber: "WORKS-AP-2025-0012",
        financialYear: "2024-25",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_LDH",
        scheme: "WORKS_SCHEME_FC15",
        subScheme: "WORKS_SUB_SCHEME_GENERAL_FC15",
        approvedAmount: 3200000,
        approvedBy: "Executive Engineer",
        status: "REJECTED",
        businessService: "WORKS",
        slaDays: -3,
        createdTime: Date.now() - 15 * 24 * 60 * 60 * 1000,
        projectsCount: 2,
        componentsCount: 4,
    },
    {
        applicationNumber: "WORKS-AP-2026-0005",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_LDH",
        scheme: "WORKS_SCHEME_MW",
        subScheme: "WORKS_SUB_SCHEME_MUNICIPAL_ROAD_WORKS",
        approvedAmount: 4200000,
        approvedBy: "Chief Engineer",
        status: "PENDING_FOR_VERIFICATION",
        businessService: "WORKS",
        slaDays: 11,
        createdTime: Date.now() - 3 * 24 * 60 * 60 * 1000,
        projectsCount: 3,
        componentsCount: 7,
    },
    {
        applicationNumber: "WORKS-AP-2026-0006",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_ASR",
        scheme: "WORKS_SCHEME_NSDP",
        subScheme: "WORKS_SUB_SCHEME_GENERAL_NSDP",
        approvedAmount: 6100000,
        approvedBy: "Superintending Engineer",
        status: "PENDING_APPROVAL",
        businessService: "WORKS",
        slaDays: 2,
        createdTime: Date.now() - 6 * 24 * 60 * 60 * 1000,
        projectsCount: 4,
        componentsCount: 8,
    },
    {
        applicationNumber: "WORKS-AP-2026-0007",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_LDH",
        scheme: "WORKS_SCHEME_SBM",
        subScheme: "WORKS_SUB_SCHEME_GENERAL_SBM",
        approvedAmount: 1500000,
        approvedBy: "Executive Engineer",
        status: "APPROVED",
        businessService: "WORKS",
        slaDays: 0,
        createdTime: Date.now() - 10 * 24 * 60 * 60 * 1000,
        projectsCount: 1,
        componentsCount: 2,
    },
    {
        applicationNumber: "WORKS-AP-2025-0019",
        financialYear: "2024-25",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_JAL",
        scheme: "WORKS_SCHEME_FC",
        subScheme: "WORKS_SUB_SCHEME_STREET_LIGHTS",
        approvedAmount: 2900000,
        approvedBy: "Commissioner",
        status: "PENDING_FOR_VERIFICATION",
        businessService: "WORKS",
        slaDays: 8,
        createdTime: Date.now() - 5 * 24 * 60 * 60 * 1000,
        projectsCount: 2,
        componentsCount: 6,
    },
    {
        applicationNumber: "WORKS-AP-2026-0008",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_LDH",
        scheme: "WORKS_SCHEME_MW",
        subScheme: "WORKS_SUB_SCHEME_MUNICIPAL_ROAD_WORKS",
        approvedAmount: 8900000,
        approvedBy: "Chief Engineer",
        status: "INITIATED",
        businessService: "WORKS",
        slaDays: 25,
        createdTime: Date.now() - 1 * 60 * 60 * 1000,
        projectsCount: 6,
        componentsCount: 15,
    },
    {
        applicationNumber: "WORKS-AP-2026-0009",
        financialYear: "2025-26",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_ASR",
        scheme: "WORKS_SCHEME_FC15",
        subScheme: "WORKS_SUB_SCHEME_GENERAL_FC15",
        approvedAmount: 3400000,
        approvedBy: "Executive Engineer",
        status: "PENDING_APPROVAL",
        businessService: "WORKS",
        slaDays: 4,
        createdTime: Date.now() - 7 * 24 * 60 * 60 * 1000,
        projectsCount: 3,
        componentsCount: 6,
    },
    {
        applicationNumber: "WORKS-AP-2025-0025",
        financialYear: "2024-25",
        ulb: "pg.citya",
        state: "WORKS_STATE_PB",
        district: "WORKS_DISTRICT_LDH",
        scheme: "WORKS_SCHEME_PMAY",
        subScheme: "WORKS_SUB_SCHEME_GENERAL_PMAY",
        approvedAmount: 4800000,
        approvedBy: "Commissioner",
        status: "APPROVED",
        businessService: "WORKS",
        slaDays: 0,
        createdTime: Date.now() - 20 * 24 * 60 * 60 * 1000,
        projectsCount: 3,
        componentsCount: 9,
    }
];

const getStoredActionPlans = () => {
    try {
        const stored = sessionStorage.getItem("WORKS_MOCK_ACTION_PLANS");
        if (stored) {
            const parsed = JSON.parse(stored);
            if (Array.isArray(parsed) && parsed.length > 0) return parsed;
        }
    } catch (e) {
        console.error("Failed to read session action plans", e);
    }
    return INITIAL_MOCK_ACTION_PLANS;
};

export const saveMockActionPlan = (newPlan) => {
    try {
        const current = getStoredActionPlans();
        const updated = [newPlan, ...current];
        sessionStorage.setItem("WORKS_MOCK_ACTION_PLANS", JSON.stringify(updated));
    } catch (e) {
        console.error("Failed to store mock action plan", e);
    }
};

export const useWorksInbox = ({ tenantId, searchParams = {}, filters = {}, pagination = { limit: 10, offset: 0, sortOrder: "DESC" } }) => {
    const queryClient = useQueryClient();

    const query = useQuery({
        queryKey: ["works", "inbox", tenantId, searchParams, filters, pagination],
        queryFn: async () => {
            if (USE_MOCK) {
                // Simulate network latency
                await new Promise((r) => setTimeout(r, 200));

                let allRecords = getStoredActionPlans();

                // 1. Search Param Filtering
                if (searchParams.applicationNumber) {
                    const q = searchParams.applicationNumber.trim().toLowerCase();
                    allRecords = allRecords.filter((item) =>
                        item.applicationNumber.toLowerCase().includes(q)
                    );
                }

                if (searchParams.financialYear) {
                    allRecords = allRecords.filter(
                        (item) => item.financialYear === searchParams.financialYear
                    );
                }

                if (searchParams.scheme) {
                    allRecords = allRecords.filter(
                        (item) => item.scheme === searchParams.scheme
                    );
                }

                // 2. Filter Component Filtering (Status, etc.)
                if (filters.status && filters.status.length > 0) {
                    const statusList = Array.isArray(filters.status) ? filters.status : [filters.status];
                    allRecords = allRecords.filter((item) => statusList.includes(item.status));
                }

                if (filters.scheme) {
                    allRecords = allRecords.filter((item) => item.scheme === filters.scheme);
                }

                if (filters.financialYear) {
                    allRecords = allRecords.filter((item) => item.financialYear === filters.financialYear);
                }

                // 3. Status summary counts
                const statusCounts = allRecords.reduce((acc, curr) => {
                    acc[curr.status] = (acc[curr.status] || 0) + 1;
                    return acc;
                }, {});

                // 4. Sorting
                allRecords.sort((a, b) => {
                    const tA = a.createdTime || 0;
                    const tB = b.createdTime || 0;
                    return pagination.sortOrder === "ASC" ? tA - tB : tB - tA;
                });

                const totalCount = allRecords.length;
                const offset = Number(pagination.offset) || 0;
                const limit = Number(pagination.limit) || 10;
                const paginatedRecords = allRecords.slice(offset, offset + limit);

                return {
                    ActionPlans: paginatedRecords,
                    totalCount,
                    statusCounts,
                };
            }

            // Real backend API fallback
            const res = await Digit.CustomService.getResponse({
                url: "/works-services/actionplan/_inbox",
                method: "POST",
                body: {
                    inbox: {
                        tenantId,
                        searchParams,
                        filters,
                        pagination,
                    },
                },
            });
            return res;
        },
        keepPreviousData: true,
    });

    return {
        ...query,
        revalidate: () => queryClient.invalidateQueries({ queryKey: ["works", "inbox"] }),
    };
};

