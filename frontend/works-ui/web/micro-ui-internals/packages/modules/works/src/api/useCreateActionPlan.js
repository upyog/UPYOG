import { useMutation, useQueryClient } from "@tanstack/react-query";

const toEpoch = (dateStr) => {
  if (!dateStr) return null;
  if (typeof dateStr === "number") return dateStr;
  const parsed = Date.parse(dateStr);
  return Number.isNaN(parsed) ? null : parsed;
};

const toDDMMYYYY = (dateStr) => {
  if (!dateStr) return "";
  if (typeof dateStr === "string" && /^\d{4}-\d{2}-\d{2}/.test(dateStr)) {
    const [yyyy, mm, dd] = dateStr.slice(0, 10).split("-");
    return `${dd}/${mm}/${yyyy}`;
  }
  const d = new Date(dateStr);
  if (Number.isNaN(d.getTime())) return String(dateStr);
  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const yyyy = d.getFullYear();
  return `${dd}/${mm}/${yyyy}`;
};

const uploadFileIfNeeded = async (fileObj, tenantId) => {
  if (!fileObj) return null;
  if (fileObj.fileStoreId) return fileObj.fileStoreId;
  const rawFile = fileObj.file || (fileObj instanceof File ? fileObj : null);
  if (!rawFile) return null;
  const res = await Digit.UploadServices.Filestorage("WORKS", rawFile, tenantId);
  return res?.data?.files?.[0]?.fileStoreId || null;
};

const deriveActionPlanFundSource = (projects = []) => {
  const sources = new Set();
  projects.forEach((p) => {
    (p.components || []).forEach((c) => {
      if (c.fundSource) sources.add(c.fundSource);
    });
  });
  if (sources.size === 1) return [...sources][0];
  if (sources.size > 1) return "PARTIAL";
  return "OSR";
};

const buildCreatePayload = async (values, tenantId) => {
  const projects = values.projects || [];

  const projectDetails = await Promise.all(
    projects.map(async (project) => {
      const components = await Promise.all(
        (project.components || []).map(async (comp) => {
          const fundDetails = [];
          if (
            comp.fundSource === "GOVT_GRANT" ||
            comp.fundSource === "GRANT" ||
            comp.fundSource === "PARTIAL"
          ) {
            fundDetails.push({
              fundType: "GOVT_GRANT",
              sourceCode: comp.grantType || "",
              fundName: comp.grantName || "",
              amount: Number(comp.grantAmount) || 0,
            });
          }
          if (comp.fundSource === "OSR" || comp.fundSource === "PARTIAL") {
            fundDetails.push({
              fundType: "OSR",
              sourceCode: comp.osrSource || "",
              fundName: comp.osrName || "",
              amount: Number(comp.osrAmount) || 0,
            });
          }

          const supportingFileStoreId = await uploadFileIfNeeded(comp.supportingDoc, tenantId);
          const documents = supportingFileStoreId
            ? [{ documentType: "COMPONENT_SUPPORTING", fileStoreId: supportingFileStoreId }]
            : [];

          const beneficiaries =
            comp.beneficiary === "YES" && comp.beneficiaryName
              ? [
                  {
                    name: comp.beneficiaryName,
                    remark: comp.beneficiaryRemark || "",
                  },
                ]
              : [];

          return {
            componentName: comp.componentName,
            category: comp.category,
            ...(comp.subCategory ? { subCategory: comp.subCategory } : {}),
            fundSource: comp.fundSource,
            approvedAmount: Number(comp.componentAmount) || 0,
            fundDetails,
            documents,
            beneficiaries,
          };
        })
      );

      return {
        projectId: project.projectId,
        projectName: project.projectName,
        purposeCode: project.purpose,
        ...(project.purpose === "OTHERS" && project.specifyPurpose
          ? { specifyPurpose: project.specifyPurpose }
          : {}),
        startDate: toEpoch(project.timeRange?.from),
        endDate: toEpoch(project.timeRange?.to),
        typeOfWork: project.typeOfWork,
        workCategory: project.workCategory,
        components,
      };
    })
  );

  const recommendationFileStoreId = await uploadFileIfNeeded(values.recommendationDoc, tenantId);
  const recommendations = values.recommendedBy
    ? [
        {
          recommendedBy: values.recommendedBy,
          ...(values.recommendedBy === "OTHERS" && values.recommendedByName
            ? { recommendedByName: values.recommendedByName }
            : {}),
          ...(recommendationFileStoreId
            ? {
                documents: [
                  {
                    documentType: "RECOMMENDATION",
                    fileStoreId: recommendationFileStoreId,
                  },
                ],
              }
            : {}),
        },
      ]
    : [];

  const locations =
    values.state || values.district || values.mlaConstituency || values.mpConstituency
      ? [
          {
            stateCode: values.state || "",
            districtCode: values.district || "",
            mlaConstituency: values.mlaConstituency || "",
            mpConstituency: values.mpConstituency || "",
          },
        ]
      : [];

  const documents = [];
  if (Array.isArray(values.documents)) {
    for (const docFile of values.documents) {
      const fileStoreId = await uploadFileIfNeeded(docFile, tenantId);
      if (fileStoreId) {
        documents.push({
          documentType: "ACTION_PLAN",
          fileStoreId,
        });
      }
    }
  }

  const techSanctionFileStoreId = await uploadFileIfNeeded(values.technicalSanctionDoc, tenantId);
  if (techSanctionFileStoreId) {
    documents.push({
      documentType: "TECHNICAL_SANCTION",
      fileStoreId: techSanctionFileStoreId,
    });
  }

  const totalComponents = projectDetails.reduce((sum, p) => sum + (p.components?.length || 0), 0);

  return {
    tenantId,
    financialYear: values.financialYear,
    adminSanction: Boolean(values.administrativeSanction),
    technicalSanction: values.technicalSanction === "YES" || values.technicalSanction === true,
    offlineApprovalDate: toEpoch(values.approvalDate),
    approvingAuthority: values.approvedBy,
    schemeCode: values.scheme,
    subSchemeCode: values.subScheme,
    approvedAmount: Number(values.approvedAmount) || 0,
    approvalDate: toDDMMYYYY(values.approvalDate),
    approvedBy: values.approvedBy,
    fundSource: deriveActionPlanFundSource(projects),
    totalComponents,
    declarationAccepted: Boolean(values.declaration),
    projectDetails,
    recommendations,
    locations,
    documents,
    workflow: {
      action: "INITIATE",
      comments: "Action Plan created",
    },
  };
};

export const useCreateActionPlan = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (values) => {
      const tenantId = values.tenantId || values.ulb || Digit.ULBService.getCurrentTenantId();
      const actionPlanPayload = await buildCreatePayload(values, tenantId);

      const res = await Digit.WorksService.createActionPlan(
        { ActionPlan: actionPlanPayload },
        tenantId
      );

      const created =
        res?.ActionPlan ||
        res?.ActionPlans?.[0] ||
        res?.actionPlan ||
        res?.actionPlans?.[0] ||
        res;

      if (!created || (!created.id && !created.applicationNumber)) {
        return created;
      }

      // Immediately call _update with SUBMIT action so the newly initiated Action Plan is submitted
      const submitRes = await Digit.WorksService.updateActionPlan(
        {
          ActionPlan: {
            ...created,
            workflow: {
              action: "SUBMIT",
              comments: "Submitted for verification",
            },
          },
        },
        created?.tenantId || tenantId
      );

      const submitted =
        submitRes?.ActionPlan ||
        submitRes?.ActionPlans?.[0] ||
        submitRes?.actionPlan ||
        submitRes?.actionPlans?.[0] ||
        created;

      return submitted;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["works", "inbox"] });
      queryClient.invalidateQueries({ queryKey: ["works", "search"] });
    },
  });
};