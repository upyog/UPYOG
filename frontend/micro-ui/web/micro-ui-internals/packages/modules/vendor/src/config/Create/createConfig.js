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
          nextStep: "pincode",
          texts: {
            submitBarLabel: "VENDOR_COMMON_NEXT",
          },
        },
      ],
    },
    {
      head: "VENDOR_ADDRESS_DETAILS",
      body: [
        {
          route: "pincode",
          component: "VendorPincode",
          texts: {
            submitBarLabel: "VENDOR_COMMON_NEXT",
            skipText: "CORE_COMMON_SKIP_CONTINUE",
          },
          withoutLabel: true,
          key: "address",
          nextStep: "address",
          type: "component",
        },
        {
          route: "address",
          component: "VendorAddress",
          withoutLabel: true,
          texts: {
            submitBarLabel: "EWASTE_COMMON_NEXT",
          },
          key: "address",
          nextStep: null,
          isMandatory: true,
          type: "component",
        },
      ],
    },
  ];
  