export const createConfig = [
    {
      head: "VENDOR_DETAILS",
      body: [
        {
          route: "info",
          component: "ServiceDoc",
          nextStep: "vendor-details",
          key: "Documents",
        },
        {
          route: "vendor-details",
          component: "VendorDetails",
          withoutLabel: true,
          key: "vendordet",
          type: "component",
          nextStep: "documents",
          hideInEmployee: true,
          isMandatory: true,
          texts: {
            header: "VENDOR_ADDITIONAL_DETAILS",
            submitBarLabel: "VENDOR_COMMON_NEXT",
          },
        },
      ],
    },
    {
      head: "VENDOR_DOCUMENT_DETAILS",
      body: [
        {
          route: "documents",
          component: "VendorDocuments",
          withoutLabel: true,
          key: "documents",
          type: "component",
          nextStep: null,
          texts: {
            header: "VENDOR_DOCUMENTS_DETAILS",
            submitBarLabel: "VENDOR_COMMON_NEXT",
          },
        },
      ],
    }
  ];
  