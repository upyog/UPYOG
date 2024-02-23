import get from "lodash/get";
import isEmpty from "lodash/isEmpty";
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "./vfs_fonts";

//import pdfFonts from "pdfmake/build/vfs_fonts";

import QRCode from "qrcode";
import { getTransformedLocale } from "egov-ui-framework/ui-utils/commons";
import { getMessageFromLocalization } from "./receiptTransformer";
// import {getLocaleLabels} from "egov-ui-framework/ui-utils/commons.js"
//pdfMake.vfs = pdfFonts.pdfMake.vfs;

//import pdfMakeCustom from "pdfmake/build/pdfmake";
const updateMohall=(data)=>{
  if (data.address.search(data.mohalla)>-1) {
    data.address=data.address.replace(data.mohalla,getMessageFromLocalization(data.mohalla))
  }
  return data;
}

const getOwners = data => {
   let retowners = [];
  data.owners.forEach(owner => {
    retowners.push(
      [
        {
          text: "Mobile No.",
          border: [true, true, false, false]
        },
        {
          text: "Name",
          border: [false, true, false, false]
        },
        {
          text: "Gender",
          border: [false, true, false, false]
        },
        {
          text: "Date of Birth",
          border: [false, true, true, false]
        }
      ],
      [
        {
          text: get(owner, "mobile"),
          style: "receipt-table-value",
          border: [true, false, false, false]
        },
        {
          text: get(owner, "name"),
          style: "receipt-table-value",
          border: [false, false, false, false]
        },
        {
          text: get(owner, "gender"),
          style: "receipt-table-value",
          border: [false, false, false, false]
        },
        {
          text: get(owner, "dob"),
          style: "receipt-table-value",
          border: [false, false, true, false]
        }
      ],
      [
        {
          text: "",
          border: [true, false, false, false]
        },
        {
          text: "",
          border: [false, false, false, false]
        },
        {
          text: "",
          border: [false, false, false, false]
        },

        {
          text: "",
          border: [false, false, true, false]
        }
      ],
      [
        {
          text: "Father/Husband's Name",
          border: [true, false, false, false]
        },
        {
          text: "Relationship",
          border: [false, false, false, false]
        },
        {
          text: "Email",
          border: [false, false, false, false]
        },
        {
          text: "PAN No.",
          border: [false, false, true, false]
        }
      ],
      [
        {
          text: get(owner, "fatherHusbandName"),
          style: "receipt-table-value",
          border: [true, false, false, false]
        },
        {
          text: get(owner, "relationship"),
          style: "receipt-table-value",
          border: [false, false, false, false]
        },
        {
          text: get(owner, "email"),
          style: "receipt-table-value",
          border: [false, false, false, false]
        },
        {
          text: get(owner, "pan"),
          style: "receipt-table-value",
          border: [false, false, true, false]
        }
      ],
      // [
      //   {
      //     text: "",
      //     border: [true, false, false, false]
      //   },
      //   {
      //     text: "",
      //     border: [false, false, false, false]
      //   },
      //   {
      //     text: "",
      //     border: [false, false, false, false]
      //   },
      //
      //   {
      //     text: "",
      //     border: [false, false, true, false]
      //   }
      // ],
      [
        {
          text: "Correspondence Address",
          border: [true, false, false, false]
        },
        {
          text: "",
          border: [false, false, false, false]
        },
        {
          text: "",
          border: [false, false, false, false]
        },
        {
          text: "",
          border: [false, false, true, false]
        }
      ],
      [
        {
          text: get(owner, "address"),
          style: "receipt-table-value",
          border: [true, false, false, true]
        },
        {
          text: "",
          style: "receipt-table-value",
          border: [false, false, false, true]
        },
        {
          text: "",
          style: "receipt-table-value",
          border: [false, false, false, true]
        },

        {
          text: "",
          style: "receipt-table-value",
          border: [false, false, true, true]
        }
      ]
    );
  });

  return retowners;
};

const getBuildings = data => {
  let retbuildings = [];
  data &&
  data.buildings.forEach(building => {
    retbuildings.push([
      {
        text: "Property Type",
        border: [true, true, false, false]
      },
      {
        text: " Name of Building",
        border: [false, true, false, false]
      },
      {
        text: "Building Usage Type",
        border: [false, true, false, false]
      },
      {
        text: "Building Usage Subtype",
        border: [false, true, true, false]
      }
    ]);
    retbuildings.push([
      {
        text: data.propertyType,
        style: "receipt-table-value",
        border: [true, false, false, false]
      },
      {
        text: get(building, "name", "NA"),
        style: "receipt-table-value",
        border: [false, false, false, false]
      },
      {
        text: get(building, "usageType", "NA"),
        style: "receipt-table-value",
        border: [false, false, false, false]
      },
      {
        text: get(building, "usageSubType", "NA"),
        style: "receipt-table-value",
        border: [false, false, true, false]
      }
    ]);

    retbuildings.push([
      {
        text: "Land Area",
        border: [true, false, false, false]
      },
      {
        text: "Total Covered Area",
        border: [false, false, false, false]
      },
      {
        text: "Parking Area",
        border: [false, false, false, false]
      },
      {
        text: "Left Surrounding",
        border: [false, false, true, false]
      }
    ]);

    retbuildings.push([
      {
        text: data.landArea,
        style: "receipt-table-value",
        border: [true, false, false, false]
      },
      {
        text: data.totalCoveredArea,
        style: "receipt-table-value",
        border: [false, false, false, false]
      },
      {
        text: data.parkingArea,
        style: "receipt-table-value",
        border: [false, false, false, false]
      },
      {
        text: data.leftSurrounding,
        style: "receipt-table-value",
        border: [false, false, true, false]
      }
    ]);

    retbuildings.push([
      {
        text: "Right Surrounding",
        border: [true, false, false, false]
      },
      {
        text: "Back Surrounding",
        border: [false, false, false, false]
      },
      {
        text: "Front Surrounding",
        border: [false, false, false, false]
      },
      {
        text: "",
        border: [false, false, true, false]
      }
    ]);

    retbuildings.push([
      {
        text: data.rightSurrounding,
        style: "receipt-table-value",
        border: [true, false, false, false]
      },
      {
        text: data.backSurrounding,
        style: "receipt-table-value",
        border: [false, false, false, false]
      },
      {
        text: data.frontSurrounding,
        style: "receipt-table-value",
        border: [false, false, false, false]
      },
      {
        text: "",
        style: "receipt-table-value",
        border: [false, false, true, false]
      }
    ]);
    let headerrow = [];
    let valuerow = [];
    for (let [uomkey, uomvalue] of Object.entries(building.uoms)) {
      headerrow.push({
        text: getMessageFromLocalization(
          `NOC_PROPERTY_DETAILS_${getTransformedLocale(uomkey)}_LABEL`
        ),
        border: valuerow.length == 0
          ? [true, false, false, false]
          : valuerow.length == 3
            ? [false, false, true, false]
            : [false, false, false, false]
      });
      valuerow.push({
        text: uomvalue,
        style: "receipt-table-value",
        border: valuerow.length == 0
          ? [true, false, false, false]
          : valuerow.length == 3
            ? [false, false, true, false]
            : [false, false, false, false]
      // left, top ,right ,down
      });
      // draw when elements in one row are four
      if (headerrow.length == 4) {
        retbuildings.push(
          [headerrow[0], headerrow[1], headerrow[2], headerrow[3]],
          [valuerow[0], valuerow[1], valuerow[2], valuerow[3]]
        );
        headerrow = [];
        valuerow = [];
      }
    }
    if (headerrow.length > 0) {
      var i;
      for (i = 4 - headerrow.length; i > 0; i--) {
        headerrow.push({
          text: "",
          border: valuerow.length == 3
            ? [false, false, true, false]
            : [false, false, false, false]
        });
        valuerow.push({
          text: "",
          style: "receipt-table-value",
          border: valuerow.length == 3
            ? [false, false, true, false]
            : [false, false, false, true]
        });
      }
      retbuildings.push(
        [headerrow[0], headerrow[1], headerrow[2], headerrow[3]],
        [valuerow[0], valuerow[1], valuerow[2], valuerow[3]]
      );
      headerrow = [];
      valuerow = [];
    }
    // set last row bottom border
    retbuildings[retbuildings.length - 1][0].border[3] = true;
    retbuildings[retbuildings.length - 1][1].border[3] = true;
    retbuildings[retbuildings.length - 1][2].border[3] = true;
    retbuildings[retbuildings.length - 1][3].border[3] = true;
  });
  return retbuildings;
};
const getApplicationData = async (transformedData, ulbLogo, type) => {
  debugger;

  const ddi=transformedData.buildings[0].uoms;
  var NoBase=0,NoHeight=0;
  Object.keys(ddi).map((key,i) => {
  if(key == "NO_OF_BASEMENTS")
  NoBase=1;

  if(key == "HEIGHT_OF_BUILDING")
  NoHeight=1;
  });
  let reasonss =null;
  if(NoBase == 0 && NoHeight >0)
  {
    reasonss = {
      "HEIGHT_OF_BUILDING": transformedData.buildings[0].uoms.HEIGHT_OF_BUILDING,
      "NO_OF_BASEMENTS":0,
      "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
      }
  }
else if(NoBase >0 && NoHeight == 0)
{
  reasonss = {
    "HEIGHT_OF_BUILDING": 0,
    "NO_OF_BASEMENTS":transformedData.buildings[0].uoms.NO_OF_BASEMENTS,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}
else if(NoBase >0 && NoHeight> 0)
{
  reasonss = {
    "HEIGHT_OF_BUILDING": transformedData.buildings[0].uoms.HEIGHT_OF_BUILDING,
    "NO_OF_BASEMENTS":transformedData.buildings[0].uoms.NO_OF_BASEMENTS,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}
else 
{
  reasonss = {
    "HEIGHT_OF_BUILDING": 0,
    "NO_OF_BASEMENTS":0,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}

transformedData.buildings[0].uoms=reasonss;
  transformedData=updateMohall(transformedData)
  let borderLayout = {
    hLineWidth: function(i, node) {
      return i === 0 || i === node.table.body.length ? 0.1 : 0.1;
    },
    vLineWidth: function(i, node) {
      return i === 0 || i === node.table.widths.length ? 0.1 : 0.1;
    },
    hLineColor: function(i, node) {
      return i === 0 || i === node.table.body.length ? "#979797" : "#979797";
    },
    vLineColor: function(i, node) {
      return i === 0 || i === node.table.widths.length ? "#979797" : "#979797";
    }
  // paddingLeft: function(i, node) {
  //   return 5;
  // },
  // paddingRight: function(i, node) {
  //   return 5;
  // },
  // paddingTop: function(i, node) {
  //   return 5;
  // },
  // paddingBottom: function(i, node) {
  //   return 5;
  // }
  };

  let headerText = "Application Confirmation";
  let nocSubheadOne = [
    {
      text: [
        {
          text: "Application No.     ",
          bold: true
        },
        {
          text: transformedData.applicationNumber,
          bold: false
        }
      ],
      alignment: "left"
    },
    {
      text: [
        {
          text: "Date of Application ",
          bold: true
        },
        {
          text: transformedData.applicationDate,
          bold: false
        }
      ],
      alignment: "right"
    }
  ];
  let nocSubheadTwo = [
    {
      text: [
        {
          text: "Application Mode ",
          bold: true
        },
        {
          text: transformedData.applicationMode,
          bold: false
        }
      ],
      alignment: "left"
    },
    {
      text: [
        {
          text: "Application Status ",
          bold: true
        },
        {
          text: transformedData.applicationStatus,
          bold: false
        }
      ],
      alignment: "right"
    }
  ];
  let nocDetails = [
    {
      text: "NOC DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*", "*"],
        body: [
          [
            {
              text: "NOC Type",
              border: [true, true, false, false]
            },
            {
              text: transformedData.nocType=="RENEWAL"?"Old Fire NOC No.":"Provisional NOC No.",
              border: [false, true, false, false]
            },
            {
              text: "Applicable Fire station",
              border: [false, true, true, false]
            }
          ],
          [
            {
              text: transformedData.nocType,
              border: [true, false, false, true],
              style: "receipt-table-value"
            },
            {
              text: transformedData.nocType=="RENEWAL"?transformedData.oldFireNOCNumber:transformedData.provisionalNocNumber,
              border: [false, false, false, true],
              style: "receipt-table-value"
            },
            {
              text: transformedData.fireStationId,
              border: [false, false, true, true],
              style: "receipt-table-value"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];
 
  var buildAreaData =[];
  var buildtableHead = [];
  // ---- head table----
    buildtableHead.push([
      {
        text: "Name of Building",
        border: [true, true, true, true]
      },
      {
        text: "No of Floors",
        border: [true, true, true, true]
      },
      {
        text: "Area (sq. mtr.)",
        border: [true, true, true, true]
      }
    ]);
    // -----------------
  if(transformedData.buildings.length > 1){
    
  for(let mybuldingdata of transformedData.buildings){
   
    buildAreaData.push( [
      {
        text: mybuldingdata.name,
        border: [true, true, true, true],
        style: "receipt-table-value"
      },
      {
        text: mybuldingdata.uoms.NO_OF_FLOORS,
        border: [true, true, true, true],
        style: "receipt-table-value"
      },
      {
        text: mybuldingdata.uoms.BUILTUP_AREA,
        border: [true, true, true, true],
        style: "receipt-table-value"
      },
    ]);
    
  }
  }

  // ------------------------my details---------------------------
 
  let nocDetailsmy = [];
  if(transformedData.buildings.length > 1){
nocDetailsmy.push([
  {
    text: "BUILDING DETAILS",
    style: "noc-title-my",
    alignment: "left",
    margin: [10, 0, 0, 0],
  },
  {
    style: "noc-table-my",
    table: {
      widths: ["25%", "25%", "25%", "25%"],
      body: [
        ...buildtableHead,
        ...buildAreaData
        ]
    },
    layout: borderLayout
  }
]);
  }

  // -------------------------------------------------------------
  let propertyDetails = [
    {
      text: "PROPERTY DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: getBuildings(transformedData)
      },
      layout: borderLayout
    }
  ];
  let propertyLocationDetails = [
    {
      text: "PROPERTY LOCATION DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: [

          [
            {
              text: "Area Type",
              border: [true, true, false, false]
            },
            {
              text: "District Name",
              border: [false, true, false, false]
            },
            {
              text: "Tehsil",
              border: [false, true, false, false]
            },
            {
              text: "Property Id",
              border: [false, true, true, false]
            },

          ],
          [
            {
              text: transformedData.areaType,
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.district,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.areaType ==='Rural'? transformedData.subDistrict:'N/A',
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.propertyId,
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],

          [

            {
              text: "City",
              border: [true, false, false, false]
            },
            {
              text: "Plot/Survey No.",
              border: [false, false, false, false]
            },

            {
              text: "Street Name",
              border: [false, false, false, false]
            },
            {
              text: "Location on Map",
              border: [false, false, true, false]
             },
          ],
          [

            {
              text: transformedData.areaType ==='Urban'? transformedData.city:'N/A',
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.door,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.street,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },

             {
              text: transformedData.mapLoc,
              style: "receipt-table-value",
             border: [false, false, true, false]


            },
          ],
          [


            {
              text: " village",
              border: [true, false, false, false]
            },
            {
              text: " landmark",
              border: [false, false, false, false]
            },


            {
              text: " Mohalla",
              border: [false, false, false, false]
            },
            {
              text: "Pincode",
              border: [false, false, true, false]
            },

          ],
          [

            {
              text: transformedData.village,
              style: "receipt-table-value",
              border: [true, false, false, false]
            },

            {
              text: transformedData.landmark,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },

            {
              text: getMessageFromLocalization(transformedData.mohalla),
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.pincode,
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],

           [
            {
              text: "",
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],
          [
            {
              text: '',
              style: "receipt-table-value",
              border: [true, false, false, true]
            },
            {
              text: '',
              style: "receipt-table-firestation",
              border: [false, false, false, true]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, true, true]
            },
          ]

        ]
      },
      layout: borderLayout
    },

  ]

  let applicantDetails = [
    {
      text: "APPLICANT DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        //widths: ["*", "*", "*", "*"],
        widths: ["25%", "25%", "25%", "25%"],
        body: getOwners(transformedData)
      },
      layout: borderLayout
    }
  ];
  let institutionDetails = [
    {
      text: "INSTITUTION DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: [
          [
            {
              text: "Type of Institution",
              border: [true, true, false, false]
            },
            {
              text: "Name of Institute",
              border: [false, true, false, false]
            },
            {
              text: "Official Telephone No.",
              border: [false, true, false, false]
            },
            {
              text: "Authorized Person",
              border: [false, true, true, false]
            }
          ],
          [
            {
              text: getMessageFromLocalization(
                `COMMON_MASTERS_OWNERSHIPCATEGORY_${getTransformedLocale(
                  transformedData.ownershipType
                )}`
              ),
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.institutionName,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.telephoneNumber,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.owners[0].name,
              style: "receipt-table-value",
              border: [false, false, true, false]
            }
          ],
          [
            {
              text: "Designation in Institution",
              border: [true, false, false, false]
            },
            {
              text: "Mobile No. of Authorized Person",
              border: [false, false, false, false]
            },
            {
              text: "Email of Authorized Person",
              border: [false, false, false, false]
            },
            {
              text: "Official Correspondence Address",
              border: [false, false, true, false]
            }
          ],
          [
            {
              text: transformedData.institutionDesignation,
              style: "receipt-table-value",
              border: [true, false, false, true]
            },
            {
              text: transformedData.owners[0].mobile,
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: transformedData.owners[0].email,
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: transformedData.owners[0].address,
              style: "receipt-table-value",
              border: [false, false, true, true]
            }
          ]
        ]
      },
      layout: borderLayout
    },



  ];
  let documents = [];
  let owners = transformedData.owners.map(owner => [
    {
      text: "Applicant Name",
      border: [true, true, false, true],
      style: "receipt-table-value"
    },
    {
      text: owner.name,
      border: [false, true, true, true]
    },
    {
      text: "Mobile No.",
      border: [true, true, false, true],
      style: "receipt-table-value"
    },
    {
      text: owner.mobile,
      border: [false, true, true, true]
    }
  ]);
  let applicantInformation = [
    {
      text: "APPLICANT INFORMATION",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
      //  widths: ["25%", "25%", "25%", "25%"],
        widths: ["25%", "25%", "25%", "25%"],
        body: owners
      },
      layout: borderLayout
    }
  ];
  let amountPaid = [
    {
      text: "AMOUNT PAID",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*", "*"],
        body: [
          [
            {
              text: "NOC Fee",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            /* {
              text: "NOC Taxes",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }, */
            {
              text: "Adhoc Penalty/Rebate",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            {
              text: "TOTAL",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }
          ],
          [
            {
              text: transformedData.nocFee,
              border: [true, true, true, true],
              alignment: "center"
            },
           /*  {
              text: transformedData.nocTaxes,
              border: [true, true, true, true],
              alignment: "center"
            }, */
            {
              text: transformedData.nocAdhocPenaltyRebate,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.totalAmount,
              border: [true, true, true, true],
              alignment: "center"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];
  let paymentInformation = [
    {
      text: "PAYMENT INFORMATION",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*","*"],
        body: [
          [
            {
              text: "Payment Mode",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            {
              text: "Transaction ID/ Cheque/ DD No.",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }, {
              text: "Bank Name & Branch",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }
          ],
          [
            {
              text: transformedData.paymentMode,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.transactionNumber,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.bankAndBranch,
              border: [true, true, true, true],
              alignment: "center"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];

  let disclaimers = [
    {
         "text":"\n\nNote:",
         "style":"header",
         "bold":true
      },
      {
         "ol":[
            "This documents is not proof of Property Ownership or Copy of NOC.",
            "This is computer genereted document,hence requires no signature.",
            "Payment is subjected to verification.Scrutiny by competitive authority."
         ]
      }
    ]

  let citizengeneratedApprovedBy = [
    {
      style: "receipt-approver",
      columns: [
        {
          text: [
            {
             // text: "Approved by: ",
              //bold: true
            },
            {
            //  text: transformedData.auditorName,
             // bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Commissioner/EO",
              bold: true
            }
          ],
          alignment: "right"
        }
      ]
    }
 ] ;
  let generatedApprovedBy = [
    {
      style: "receipt-approver",
      columns: [
        {
          text: [
            {
              text: "Generated by: ",
              bold: true
            },
            {
              text: transformedData.auditorName,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Commissioner/EO",
              bold: true
            }
          ],
          alignment: "right"
        }
      ]
    }
  ];
  let qrText = `Application: ${transformedData.applicationNumber},\n Date: ${
  transformedData.applicationDate
  },\n Buildings: ${transformedData.propertyType},\n Applicant: ${
  transformedData.owners[0].name
  },\nApplication Status:${transformedData.applicationStatus
  },\n Address: ${transformedData.address}`;

  if (transformedData.ownershipType.startsWith("INSTITUTION")) {
    applicantDetails = [];
    applicantInformation = [];
  } else {
    institutionDetails = [];
  }



  switch (type) {
    case "application":
      disclaimers=[]
      applicantInformation = [];
      amountPaid = [];
      paymentInformation = [];
      generatedApprovedBy = [];
      break;
    case "receipt":
      headerText = "Payment Receipt";
      nocSubheadOne = [
        {
          text: [
            {
              text: "Application No. ",
              bold: true
            },
            {
              text: transformedData.applicationNumber,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Date of Payment ",
              bold: true
            },
            {
              text: transformedData.paymentDate,
              bold: false
            }
          ],
          alignment: "right"
        }
      ];
      nocSubheadTwo = [
        {
          text: [
            {
              text: "Payment Receipt No.  ",
              bold: true
            },
            {
              text: transformedData.receiptNumber,
              bold: false
            }
          ],
          alignment: "left"
        }
      ];
      nocDetails = [];
      nocDetailsmy = [];
      buildAreaData = [];
      propertyDetails = [];
      propertyLocationDetails = [];
      applicantDetails = [];
      documents = [];
      qrText = `Application: ${
      transformedData.applicationNumber
      }, Receipt number: ${transformedData.receiptNumber}, Date of payment: ${
      transformedData.paymentDate
      }, Fees Paid: ${transformedData.amountPaid}, Payment mode: ${
      transformedData.paymentMode
      }, Transaction ID: ${transformedData.transactionNumber}`;
      break;
    case "certificate":
      headerText = "Certificate";
      disclaimers=[]
      nocSubheadOne = [
        {
          text: [
            {
              text: "Fire NOC No. ",
              bold: true
            },
            {
              text: transformedData.fireNOCNumber,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Application No. ",
              bold: true
            },
            {
              text: transformedData.applicationNumber,
              bold: false
            }
          ],
          alignment: "right"
        }
      ];
       nocSubheadTwo = [
        {
          text: [
            {
              text: "Date of Issue ",
              bold: true
            },
            {
              text: transformedData.issuedDate,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Valid Till ",
              bold: true
            },
            {
              text: transformedData.validTo,
              bold: false
            }
          ],
          alignment: "right"
        }
      ];
      applicantDetails = [];
      documents = [];


      citizengeneratedApprovedBy = [
        {
          style: "receipt-approver",
          columns: [
            {
              text: [
                {
                 // text: "Approved by: ",
                  //bold: true
                },
                {
                //  text: transformedData.auditorName,
                 // bold: false
                }
              ],
              alignment: "left"
            },
            {
              text: [
                {
                  text: "Commissioner/EO",
                  bold: true
                }
              ],
              alignment: "right"
            }
          ]
        }
     ] ;

      generatedApprovedBy = [
        {
          style: "receipt-approver",
          columns: [
            {
              text: [
                {
                  text: "Approved by: ",
                  bold: true
                },
                {
                  text: transformedData.auditorName,
                  bold: false
                }
              ],
              alignment: "left"
            },
            {
              text: [
                {
                  text: "Commissioner/EO",
                  bold: true
                }
              ],
              alignment: "right"
            }
          ]
        }
     ] ;

      qrText = `Application: ${
      transformedData.applicationNumber
      }, NOC Number: ${transformedData.fireNOCNumber}, Date of Issue: ${
      transformedData.issuedDate
      }, Valid Till: ${transformedData.validTo}, Buildings: ${
      transformedData.propertyType
      },Applicant: ${transformedData.owners[0].name}`;
      break;
  }

  // Generate QR code base64 image
  let qrcode = await QRCode.toDataURL(qrText);



  let dd = {
    defaultStyle: {
      font: "raavi",


    },
    content: [
      {
        style: "noc-head",
        table: {
          widths: [120, "*", 120],
          body: [
            [
              {
                image: ulbLogo,
                width: 60,
                // height: 61.25,
                height :60,
                margin: [51, 12, 10, 3]
              },
              {
                stack: [
                  {
                    text: transformedData.corporationName,
                    style: "receipt-logo-header"
                  },
                  {
                    text: `Fire NOC ${headerText}`,
                    style: "receipt-logo-sub-header"
                  }
                ],
                alignment: "left",
                margin: [10, 23, 0, 0]
              },
              {
                image: qrcode,
                width: 70,
                height: 70,
                margin: [80, 8, 8, 8],
                alignment: "right"
              }
            ]
          ]
        },
        layout: "noBorders"
      },
      {
        style: "noc-subhead",
        columns: nocSubheadOne
      },
      {
        style: "noc-subhead",
        columns: nocSubheadTwo
      },
     ...nocDetails,
     ...nocDetailsmy,
     ...buildAreaData,
     ...propertyDetails,
     ...propertyLocationDetails,
     ...applicantDetails,
     ...documents,
     ...applicantInformation,
     ...institutionDetails,
     ...amountPaid,
     ...paymentInformation,
     ...process.env.REACT_APP_NAME !== "Citizen"? generatedApprovedBy : citizengeneratedApprovedBy,
     ...disclaimers,{
      text:"Now log a complaint from WhatsApp, give a missed call on 8750975975 or send a Hi message on WhatsApp to this number", bold: true, fontSize: 8, color: 'blue', decoration: 'underline'} 
    ],
    footer: [],
    "styles":{
      "noc-head":{
         "fillColor":"#F2F2F2",
         "margin":[
            -70,
            -41,
            -81,
            0
         ]
      },
      "noc-head-new":{
         "fontSize":7,
         "fillColor":"#F2F2F2",
         "margin":[
            0,
            0,
            0,
            0
         ]
      },
      "receipt-logo-header":{
         "color":"#484848",
         "fontFamily":"raavi",
         "fontSize":14,
         "bold":true,
         "letterSpacing":0.74,
         "margin":[
            0,
            0,
            0,
            5
         ]
      },
      "receipt-logo-sub-header":{
         "color":"#484848",
         "fontFamily":"raavi",
         "fontSize":11,
         "letterSpacing":0.6
      },
      "noc-subhead":{
         "fontSize":9,
         "bold":true,
         "margin":[
            -18,
            8,
            0,
            0
         ],
         "color":"#484848"
      },
      "noc-title":{
         "fontSize":8,
         "bold":true,
         "margin":[
            -18,
            16,
            8,
            8
         ],
         "color":"#484848",
         "fontWeight":500
      },
      "noc-table":{
         "fontSize":9,
         "color":"#484848",
         "margin":[
            -20,
            -2,
            -8,
            -8
         ]
      },
      "receipt-header-details":{
         "fontSize":8,
         "margin":[
            0,
            0,
            0,
            8
         ],
         "color":"#484848"
      },
      "noc-table-key":{
         "color":"#484848",
         "bold":false,
         "fontSize":9
      },
      "receipt-table-value":{
         "color":"#484848",
         "bold":true,
         "fontSize":9
      },
      "receipt-table-firestation":{
         "color":"#484848",
         "bold":true,
         "fontSize":9
      },
      "receipt-footer":{
         "color":"#484848",
         "fontSize":7,
         "margin":[
            -6,
            15,
            -15,
            -10
         ]
      },
      "receipt-no":{
         "color":"#484848",
         "fontSize":9
      },
      "receipt-approver":{
         "fontSize":11,
         "bold":true,
         "margin":[
            -20,
            30,
            -10,
            0
         ],
         "color":"#484848"
      }
   }
  };
  return dd;
};

const newgetApplicationData = async (transformedData, ulbLogo, type) => {
 
  const ddi=transformedData.buildings[0].uoms;
  var NoBase=0,NoHeight=0;
  Object.keys(ddi).map((key,i) => {
  if(key == "NO_OF_BASEMENTS")
  NoBase=1;

  if(key == "HEIGHT_OF_BUILDING")
  NoHeight=1;
  });
  let reasonss =null;
  if(NoBase == 0 && NoHeight >0)
  {
    reasonss = {
      "HEIGHT_OF_BUILDING": transformedData.buildings[0].uoms.HEIGHT_OF_BUILDING,
      "NO_OF_BASEMENTS":0,
      "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
      }
  }
else if(NoBase >0 && NoHeight == 0)
{
  reasonss = {
    "HEIGHT_OF_BUILDING": 0,
    "NO_OF_BASEMENTS":transformedData.buildings[0].uoms.NO_OF_BASEMENTS,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}
else if(NoBase >0 && NoHeight> 0)
{
  reasonss = {
    "HEIGHT_OF_BUILDING": transformedData.buildings[0].uoms.HEIGHT_OF_BUILDING,
    "NO_OF_BASEMENTS":transformedData.buildings[0].uoms.NO_OF_BASEMENTS,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}
else 
{
  reasonss = {
    "HEIGHT_OF_BUILDING": 0,
    "NO_OF_BASEMENTS":0,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}

transformedData.buildings[0].uoms=reasonss;
  transformedData=updateMohall(transformedData)
  let borderLayout = {
    hLineWidth: function(i, node) {
      return i === 0 || i === node.table.body.length ? 0.1 : 0.1;
    },
    vLineWidth: function(i, node) {
      return i === 0 || i === node.table.widths.length ? 0.1 : 0.1;
    },
    hLineColor: function(i, node) {
      return i === 0 || i === node.table.body.length ? "#979797" : "#979797";
    },
    vLineColor: function(i, node) {
      return i === 0 || i === node.table.widths.length ? "#979797" : "#979797";
    }
  // paddingLeft: function(i, node) {
  //   return 5;
  // },
  // paddingRight: function(i, node) {
  //   return 5;
  // },
  // paddingTop: function(i, node) {
  //   return 5;
  // },
  // paddingBottom: function(i, node) {
  //   return 5;
  // }
  };

  let headerText = "Application Confirmation";
  let nocSubheadOne = [
    {
      text: [
        {
          text: "Application No.     ",
          bold: true
        },
        {
          text: transformedData.applicationNumber,
          bold: false
        }
      ],
      alignment: "left"
    },
    {
      text: [
        {
          text: "Date of Application ",
          bold: true
        },
        {
          text: transformedData.applicationDate,
          bold: false
        }
      ],
      alignment: "right"
    }
  ];
  let nocSubheadTwo = [
    {
      text: [
        {
          text: "Application Mode ",
          bold: true
        },
        {
          text: transformedData.applicationMode,
          bold: false
        }
      ],
      alignment: "left"
    }
  ];
  let nocDetails = [
    {
      style: "noc-table",
      table: {
        widths: ["*", "*", "*"],
        body: [
          [
            {
              //text: "NOC No ".transformedData.fireNOCNumber,
              text: `NOC No: ${transformedData.fireNOCNumber}`,
              border: [false, false, false, false],
              alignment: "left"

            },
            {
              text: `NOC Type: ${transformedData.nocType}`,
              border: [false, false, false, false],
              alignment: "center"
            },
            {
              text: `Dated: ${transformedData.issuedDate}`,
              border: [false, false, false, false],
              alignment: "right"
            }
          ],
          /* [
            {
              text: transformedData.fireNOCNumber,
              border: [false, false, false, false],
              style: "receipt-table-value"
            },
            {
              text: transformedData.nocType,
              border: [false, false, false, false],
              style: "receipt-table-value"
            },
            {
              text: transformedData.issuedDate,
              border: [false, false, false, false],
              style: "receipt-table-value"
            }
          ] */
        ]
      },
     // layout: borderLayout
    }
  ];
// ------------------------my details---------------------------
var buildAreaData =[];
var buildtableHead = [];
// ---- head table----
buildtableHead.push([
  {
    text: "Name of Building",
    border: [true, true, true, true]
  },
  {
    text: "No of Floors",
    border: [true, true, true, true]
  },
  {
    text: "Area (sq. mtr.)",
    border: [true, true, true, true]
  }
]);
// -----------------
if(transformedData.buildings.length > 1){

for(let mybuldingdata of transformedData.buildings){
 
  buildAreaData.push( [
    {
      text: mybuldingdata.name,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
    {
      text: mybuldingdata.uoms.NO_OF_FLOORS,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
    {
      text: mybuldingdata.uoms.BUILTUP_AREA,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
  ]);
  
}
}

let nocDetailsmy = [];
if(transformedData.buildings.length > 1){
nocDetailsmy.push([
  {
    text: "BUILDING DETAILS",
    style: "noc-title-my",
    alignment: "left",
    margin: [10, 0, 0, 0],
  },
  {
    style: "noc-table-my",
    table: {
      widths: ["25%", "25%", "25%", "25%"],
      body: [
        ...buildtableHead,
        ...buildAreaData
        ]
    },
    layout: borderLayout
  }
]);
}
// -------------------------------------------------------------
  let space = [
    {
      text: "",
      style: "noc-title"
    }];



  let propertyDetails = [
    {
      text: "PROPERTY DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: getBuildings(transformedData)
      },
      layout: borderLayout
    }
  ];
  let propertyLocationDetails = [
    {
      text: "PROPERTY LOCATION DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: [

          [
            {
              text: "Area Type",
              border: [true, true, false, false]
            },
            {
              text: "District Name",
              border: [false, true, false, false]
            },
            {
              text: "Tehsil",
              border: [false, true, false, false]
            },
            {
              text: "Property Id",
              border: [false, true, true, false]
            },

          ],
          [
            {
              text: transformedData.areaType,
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.district,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.areaType ==='Rural'? transformedData.subDistrict:'N/A',
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.propertyId,
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],

          [

            {
              text: "City",
              border: [true, false, false, false]
            },
            {
              text: "Plot/Survey No.",
              border: [false, false, false, false]
            },

            {
              text: "Street Name",
              border: [false, false, false, false]
            },
            {
              text: "Location on Map",
              border: [false, false, true, false]

             },
          ],
          [

            {
              text: transformedData.areaType ==='Urban'? transformedData.city:'N/A',
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.door,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.street,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },

             {
              text: transformedData.gis,
              style: "receipt-table-value",
              border: [false, false, false, true]

            },
          ],
          [


            {
              text: " Village",
              border: [true, false, false, false]
            },
            {
              text: " Landmark",
              border: [false, false, false, false]
            },


            {
              text: " Mohalla",
              border: [false, false, false, false]
            },
            {
              text: "Pincode",
              border: [false, false, true, false]
            },

          ],
          [

            {
              text: transformedData.village,
              style: "receipt-table-value",
              border: [true, false, false, false]
            },

            {
              text: transformedData.landmark,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },

            {
              text: getMessageFromLocalization(transformedData.mohalla),
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.pincode,
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],

          [
            {
              text: "",
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],
          [
            {
              text: '',
              style: "receipt-table-value",
              border: [true, false, false, true]
            },
            {
              text: '',
              style: "receipt-table-firestation",
              border: [false, false, false, true]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, true, true]
            },
          ]

        ]
      },
      layout: borderLayout
    },

  ]

  let applicantDetails = [
    {
      text: "APPLICANT DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        //widths: ["*", "*", "*", "*"],
        widths: ["25%", "25%", "25%", "25%"],
        body: getOwners(transformedData)
      },
      layout: borderLayout
    }
  ];
  let institutionDetails = [
    {
      text: "INSTITUTION DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: [
          [
            {
              text: "Type of Institution",
              border: [true, true, false, false]
            },
            {
              text: "Name of Institute",
              border: [false, true, false, false]
            },
            {
              text: "Official Telephone No.",
              border: [false, true, false, false]
            },
            {
              text: "Authorized Person",
              border: [false, true, true, false]
            }
          ],
          [
            {
              text: getMessageFromLocalization(
                `COMMON_MASTERS_OWNERSHIPCATEGORY_${getTransformedLocale(
                  transformedData.ownershipType
                )}`
              ),
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.institutionName,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.telephoneNumber,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.owners[0].name,
              style: "receipt-table-value",
              border: [false, false, true, false]
            }
          ],
          [
            {
              text: "Designation in Institution",
              border: [true, false, false, false]
            },
            {
              text: "Mobile No. of Authorized Person",
              border: [false, false, false, false]
            },
            {
              text: "Email of Authorized Person",
              border: [false, false, false, false]
            },
            {
              text: "Official Correspondence Address",
              border: [false, false, true, false]
            }
          ],
          [
            {
              text: transformedData.institutionDesignation,
              style: "receipt-table-value",
              border: [true, false, false, true]
            },
            {
              text: transformedData.owners[0].mobile,
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: transformedData.owners[0].email,
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: transformedData.owners[0].address,
              style: "receipt-table-value",
              border: [false, false, true, true]
            }
          ]
        ]
      },
      layout: borderLayout
    },



  ];
  let documents = [];
  let owners = transformedData.owners.map(owner => [
    {
      text: "Applicant Name",
      border: [true, true, false, true],
      style: "receipt-table-value"
    },
    {
      text: owner.name,
      border: [false, true, true, true]
    },
    {
      text: "Mobile No.",
      border: [true, true, false, true],
      style: "receipt-table-value"
    },
    {
      text: owner.mobile,
      border: [false, true, true, true]
    }
  ]);
  let applicantInformation = [
    {
      text: "APPLICANT INFORMATION",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
      //  widths: ["25%", "25%", "25%", "25%"],
        widths: ["25%", "25%", "25%", "25%"],
        body: owners
      },
      layout: borderLayout
    }
  ];
  let amountPaid = [
    {
      text: "AMOUNT PAID",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*", "*"],
        body: [
          [
            {
              text: "NOC Fee",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            /* {
              text: "NOC Taxes",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }, */
            {
              text: "Adhoc Penalty/Rebate",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            {
              text: "TOTAL",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }
          ],
          [
            {
              text: transformedData.nocFee,
              border: [true, true, true, true],
              alignment: "center"
            },
           /*  {
              text: transformedData.nocTaxes,
              border: [true, true, true, true],
              alignment: "center"
            }, */
            {
              text: transformedData.nocAdhocPenaltyRebate,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.totalAmount,
              border: [true, true, true, true],
              alignment: "center"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];
  let paymentInformation = [
    {
      text: "PAYMENT INFORMATION",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*"],
        body: [
          [
            {
              text: "Payment Mode",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            {
              text: "Transaction ID/ Cheque/ DD No.",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }
          ],
          [
            {
              text: transformedData.paymentMode,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.transactionNumber,
              border: [true, true, true, true],
              alignment: "center"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];

  let citizengeneratedApprovedBy = [
    {
      style: "receipt-approver",
      columns: [
        {
          text: [
            {
             // text: "Approved by: ",
              //bold: true
            },
            {
            //  text: transformedData.auditorName,
             // bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Commissioner/EO",
              bold: true
            }
          ],
          alignment: "right"
        }
      ]
    }
 ] ;

  let generatedApprovedBy = [
    {
      style: "receipt-approver",
      columns: [
        {
          text: [
            {
              text: "Generated by: ",
              bold: true
            },
            {
              text: transformedData.auditorName,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Commissioner/EO",
              bold: true
            }
          ],
          alignment: "right"
        }
      ]
    }
  ];
  let qrText = `Application: ${transformedData.applicationNumber}, Date: ${
  transformedData.applicationDate
  }, Buildings: ${transformedData.propertyType}, Applicant: ${
  transformedData.owners[0].name
  }, Address: ${transformedData.address}`;

  if (transformedData.ownershipType.startsWith("INSTITUTION")) {
    applicantDetails = [];
    applicantInformation = [];
  } else {
    institutionDetails = [];
  }



  switch (type) {
    case "application":
      applicantInformation = [];
      amountPaid = [];
      paymentInformation = [];
      generatedApprovedBy = [];
      break;
    case "receipt":
      headerText = "Payment Receipt";
      nocSubheadOne = [
        {
          text: [
            {
              text: "Application No. ",
              bold: true
            },
            {
              text: transformedData.applicationNumber,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Date of Payment ",
              bold: true
            },
            {
              text: transformedData.paymentDate,
              bold: false
            }
          ],
          alignment: "right"
        }
      ];
      nocSubheadTwo = [
        {
          text: [
            {
              text: "Payment Receipt No.  ",
              bold: true
            },
            {
              text: transformedData.receiptNumber,
              bold: false
            }
          ],
          alignment: "left"
        }
      ];
      nocDetails = [];
      nocDetailsmy = [];
      buildAreaData = [];
      propertyDetails = [];
      propertyLocationDetails = [];
      applicantDetails = [];
      amountPaid = [];
      paymentInformation = [];
      documents = [];
      qrText = `Application: ${
      transformedData.applicationNumber
      }, Receipt number: ${transformedData.receiptNumber}, Date of payment: ${
      transformedData.paymentDate
      }, Fees Paid: ${transformedData.amountPaid}, Payment mode: ${
      transformedData.paymentMode
      }, Transaction ID: ${transformedData.transactionNumber}`;
      break;
    case "certificate":
      headerText = "Certificate";

      applicantDetails = [];
      documents = [];


      citizengeneratedApprovedBy = [
        {
          style: "receipt-approver",
          columns: [
            {
              text: [
                {
                 // text: "Approved by: ",
                  //bold: true
                },
                {
                //  text: transformedData.auditorName,
                 // bold: false
                }
              ],
              alignment: "left"
            },
            {
              text: [
                {
                  text: "Commissioner/EO",
                  bold: true
                }
              ],
              alignment: "right"
            }
          ]
        }
     ] ;

      generatedApprovedBy = [
        {
          style: "receipt-approver",
          columns: [
            {
              text: [
                {
                  text: "Approved by: ",
                  bold: true
                },
                {
                  text: transformedData.auditorName,
                  bold: false
                }
              ],
              alignment: "left"
            },
            {
              text: [
                {
                  text: "Commissioner/EO",
                  bold: true
                }
              ],
              alignment: "right"
            }
          ]
        }
     ] ;

      qrText = `Application: ${
      transformedData.applicationNumber
      }, NOC Number: ${transformedData.fireNOCNumber}, Date of Issue: ${
      transformedData.issuedDate
      }, Valid Till: ${transformedData.validTo}, Buildings: ${
      transformedData.propertyType
      }, Applicant: ${transformedData.owners[0].name}`;
      break;
  }

  // Generate QR code base64 image
  let qrcode = await QRCode.toDataURL(qrText);


  let dd = {
    defaultStyle: {
      font: "raavi",
    },

    content: [
      {
        style: "noc-head-new",
        table: {
          widths: [120, "*", 120],
          body: [
            [
              {
                image: ulbLogo,
                width: 60,
               // height: 61.25,
                height :60,
                margin: [31, 12, 10, 10],
                border: [true, true, false, false],

              },
              {
                stack: [
                  {
                       text: [{ text:"Punjab Fire Services" , bold:true },],
                    style: "receipt-logo-header",
                    alignment: "center",
                  },
                  {
                       text: [{ text:`( ${transformedData.corporationName} )` , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                    fontSize:11
                  },
                  {
                     text: [{ text:"FIRE SAFETY CERTIFICATE" , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  },
                  {
                     text: [{ text:"ਫਾਇਰ ਸੇਫਟੀ ਪਮਾਣ ਪੱਤਰ" , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  }
                ],
                  /* {
                    //text: transformedData.corporationName,
                    text: "Punjab Fire Services",
                    style: "receipt-logo-header",
                    alignment: "center",
                  },
                  {
                    //text:  transformedData.areaType ==='Rural'? transformedData.corporationName: transformedData.city,
                    text:  `( ${ transformedData.corporationName} )`,
                    style: "receipt-logo-sub-header",
                    alignment: "center",

                  },
                  {
                    text: "FIRE SAFETY CERTIFICATE",
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  },
                  {
                    text: "ਫਾਇਰ ਸੇਫਟੀ ਪਮਾਣ ਪੱਤਰ",
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  }
                ], */
                alignment: "left",
                margin: [10, 23, 0, 0],
                border: [false, true, false, false],

              },
              {
                image: qrcode,
                width: 70,
                height: 70,
                margin: [20, 8, 8, 8],
                alignment: "right",
                border: [false, true, true, false],

              }

            ],
    /*         [
              {
                text: `NOC No ${transformedData.fireNOCNumber}`,
                border: [true, false, false, false],
                alignment: "left"

              },
              {

                text: `NOC Type: ${transformedData.nocType}`,
                border: [false, false, false, false],
                alignment: "center"
              },
              {

                text: `Dated ${transformedData.issuedDate}`,
                border: [false, false, true, false],
                alignment: "right"
              }

            ],  */



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*", '*', '*'],
          body: [

            [
              {
                //text: `NOC No: ${transformedData.fireNOCNumber}`,
                text: [  "NOC No: " ,
                { text: `${transformedData.fireNOCNumber}`,
                bold:true },
                ],
                border: [true, false, false, false],
                alignment: "left",
                style:"noc-table-nocnumber"

              },
              {

               // text: `NOC Type: ${transformedData.nocType}`,
                text: [  "NOC Type: " ,
                { text: `${transformedData.nocType}`,
                bold:true },
                ],
                border: [false, false, false, false],
                alignment: "center"
              },
              {

                //text: `Dated: ${transformedData.issuedDate}`,
                text: [  "Dated: " ,
                { text: `${transformedData.issuedDate}`,
                bold:true },
                ],
                border: [false, false, true, false],
                alignment: "right"
              }

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [

              {
                text: ["                        Certified that the ",{ text:`${transformedData.buildings[0].name}`, bold:true }, " at " , { text:`${transformedData.address}`, bold:true }, "comprised of ", { text:`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS} `?`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`:'0', bold:true }, "  basements and ", { text:`${transformedData.buildings[0].uoms.NO_OF_FLOORS}`, bold:true }, " (Upper floor) owned/occupied by ",{ text:`${transformedData.owners[0].name}`, bold:true }, " have compiled with the fire prevention and fire safety requirements of National Building Code and verified by the officer concerned of fire service on ", { text:`${transformedData.issuedDate}`, bold:true }, " in the presence of ", { text:`${transformedData.owners[0].name}`, bold:true }, " (Name of the owner or his representative) and that the building/premises is fit for occupancy " , { text:`${transformedData.NBCGroup}`, bold:true }, " subdivision ", { text:`${transformedData.NBCSubGroup}`, bold:true }, " (As per NBC) for period of ", { text:"one year", bold:true }, " from issue date. Subject to the following conditions."],
                border: [true, false, true, false],
                alignment: "justify",
                preserveLeadingSpaces: true


              },

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [

              {
                text: ["Issued on ",{ text:`${transformedData.issuedDate}`, bold:true }," at ", { text:`${transformedData.corporationName}`, bold:true }],

               // text: `Issued on ${transformedData.issuedDate} at ${transformedData.corporationName}`,

                border: [true, false, true, false],
                alignment: "left"
              },

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: ["                        ਤਸਦੀਕ ਕੀਤਾ ਜਾਂਦਾ ਹੈ ਕ‌ਿ ",{ text:`${transformedData.buildings[0].name}`, bold:true }, ", ",{ text:`${transformedData.address}`, bold:true }, " ਸਮੇਤ ",{ text:`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`?`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`:'0', bold:true }, " ਬੇਸਮਟ ਅਤੇ ",{ text:`${transformedData.buildings[0].uoms.NO_OF_FLOORS}`, bold:true }, " (ਉਪਰਲੀ ਮੰਜ਼ਿਲ) ਮਲਕੀਅਤ/ਕਬਜ਼ਾਦਾਰ ",{ text:`${transformedData.buildings[0].name}`, bold:true }, " ਰਾਸ਼ਟਰੀ ਬਿਲਡਿੰਗ ਕੋਡ ਅਨੁਸਾਰ ਅੱਗ ਬੁਝਾਉਣ ਦੇ ਪ੍ਰਭਾਵ ਅਤੇ ਬਚਾਅ ਦੀਆਂ ਲੌੜਾਂ ਨੂੰ ਪੂਰਾ ਕਰਦੀ ਹੈ  ਜਿਸ ਨੂੰ ਸਬੰਧਤ ਫਾਇਰ ਅਧਿਕਾਰੀ ਵੱਲੌਂ ",{ text:`${transformedData.owners[0].name}`, bold:true }, " (ਮਾਲਕ ਜਾਂ ਉਸ ਦੇ ਪ੍ਰਤਿਨਿਧੀ ਦਾ ਨਾਮ ) ਦੀ ਮੋਜੂਦਗੀ ਵਿੱਚ ",{ text:`${transformedData.issuedDate}`, bold:true }, " ਨੂੰ ਪ੍ਰਮਾਣਿਤ ਕੀਤਾ ਗਿਆ ਅਤੇ ਇਮਾਰਤ / ਬਿਲਡਿੰਗ ",{ text:`${transformedData.NBCGroup}`, bold:true }, " subdivision ",{ text:`${transformedData.NBCSubGroup}`, bold:true }, " (ਐਨ. ਬੀ. ਸੀ. ਦੇ ਅਨੁਸਾਰ) ਦੀ ਆਬਾਦੀ ਲਈ Issue date ਤੌਂ ",{ text:"ਇੱਕ ਸਾਲ", bold:true }, " ਤੱਕ ਯੋਗ ਹੈ ਜਿਸ ਲਈ ਨਿਮਨ ਅਨੁਸਾਰ ਹਦਾਇਤਾਂ ਹਨ।"],
                border: [true, false, true, false],
                alignment: "justify",
                preserveLeadingSpaces: true


              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "",
                border: [true, false, true, false],
                alignment: "left",
              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
               // text: `ਜਾਰੀ ਕਰਨ ਦੀ ਿਮਤੀ ${transformedData.issuedDate}  ਿਕੱਥੇ ${transformedData.corporationName}.`,
                text: [{ text:`${transformedData.corporationName}`, bold:true },"  ਵਿਖੇ ਜਾਰੀ ਕਰਨ ਦੀ ਮਿਤੀ ", { text:`${transformedData.issuedDate}`, bold:true },"."],

                border: [true, false, true, false],
                alignment: "left"
              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "",
                border: [true, false, true, false],
                alignment: "left",
              },
           ],



          ]
        },

        layout: {},
      },
...nocDetailsmy,
      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                margin: [10, 0, 0, 0],

                text: "1. Fire Safety arrangements shall be kept in working condition at all times",
                border: [true, false, true, false],
                alignment: "left"
              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਹਰ ਸਮੇਂ ਅੱਗ ਬਚਾਅ ਦੇ ਯੰਤਰਾਂ ਨੂੰ ਚਾਲੂ /ਚੰਗੀ ਹਾਲਤ ਵਿੱਚ ਰੱਖਿਆ ਜਾਵੇ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "2. No, alteration/ addition/ change in use of occupancy is allowed.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਕਿਸੇ ਵੀ ਤਰਾਂ ਦੇ ਬਦਲਾਅ/ ਵਾਧੇ/ ਕਬਜ਼ਾਦਾਰ ਵਿੱਚ ਬਦਲਾਵ ਦੀ ਮਨਾਹੀ ਹੈ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "3. Occupants/ owner should have trained staff to operate the operaon of fire safety system provided there in.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਉਪਲੱਬਧ ਅੱਗ ਬੁਝਾਉਣ ਦੇ ਯੰਤਰ ਦੀ ਵਰਤੋਂ ਲਈ ਰਿਹਣ ਵਾਲੇ ਲੋਕਾਂ / ਮਾਲਕ ਨੂੰ ਜਾਣੂੰ ਕਰਵਾਇਆ ਜਾਣਾ ਯਕੀਨੀ ਬਣਾਇਆ ਜਾਵੇ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "4. Fire Officer can check the arrangements of fire safety at any time, this cerficate will be withdrawn without any notice if any deficiency is found.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਫਾਇਰ ਬ੍ਰਿਗੇਡ ਅਧਿਕਾਰੀ ਕਿਸੇ ਵੀ ਵਕਤ ਇਨਾਂ ਸਾਰੇ ਪ੍ਰਬੰਧਾਂ ਨੂੰ ਚੈਕ ਕਰ ਸਕਦਾ ਹੈ, ਜੇਕਰ ਕੋਈ ਕਮੀ ਪਾਈ ਗਈ ਤਾਂ ਬਿਨਾਂ ਕਿਸੇ ਨੋਟਿਸ ਦੇ ਇਹ ਸਰਟੀਿਫਕੇਟ ਰੱਦ ਸਮਝਿਆ ਜਾਵੇਗਾ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "5.Occupants/ owner should apply for renewal of fire safety cerficate one month prior to expiry of this cerficate.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਮਾਲਕ ਜਾਰੀ ਕੀਤੇ ਗਏ ਫਾਇਰ ਸੇਫਟੀ ਸਰਟੀਿਫਕੇਟ ਦੀ ਮਿਤੀ ਖਤਮ ਹੋਣ ਤੌਂ ਇੱਕ ਮਹੀਨਾ ਪਹਿਲਾਂ ਰੀਨੀਊ ਕਰਵਾਉਣ ਲਈ ਪਾਬੰਦ ਹੋਵੇਗਾ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },


      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "* Above Details cannot be used as ownership proof.",
                border: [true, false, true, false],
                alignment: "left",
                color: "#FF0000",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: [{ text:"ਉਪਰੋਕਤ ਦਰਸਾਈ ਗਈ ਜਾਣਕਾਰੀ ਨੂੰ  ਮਾਲਕਾਨਾ ਦੇ ਸਬੂਤ ਵਜ਼ੋ ਨਹੀਂ ਵਰਤਿਆ ਜਾਵੇਗਾ।", bold:true  }],
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "This is digitaly created cerificate, no signatue are needed",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: [{ text:"ਇਹ ਡਿਜੀਟਲੀ (ਕੰਪ‌ਿਊਟਰਾਈਜ਼ਡ) ਤਿਆਰ ਕੀਤਾ ਗਿਆ ਸਰਟੀਫਿਕੇਟ ਹੈ, ਜਿਸ ਵਿੱਚ ਦਸਤਖਤ ਦੀ ਕੋਈ ਲੋੜ ਨਹੀਂ ਹੈ।", bold:true  }],
                border: [true, false, true, true],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },







     /* ...space,
     ...space,
     ...nocDetails,
     ...space,
     ...firstparagraph,
     ...issued,
     ...secondparagraph,
     ...pointsheadline,
     ...firstpointinenglish,
     ...firstpointinpunjabi,
     ...secondpointinenglish,
     ...secondpointinpunjabi,
     ...thridpointinenglish,
     ...thirdpointinpunjabi,
     ...fourthpointinenglish,
     ...fourthpointinpunjabi,
     ...fifthpointinenglish,
     ...fifthpointinpunjabi,
     ...starmarkoneinenglish,
     ...starmarkoneinpunjabi,
     ...starmarktwoinenglish,
     ...starmarktwoinpunjabi, */


     /* ...propertyDetails,
     ...propertyLocationDetails,
     ...applicantDetails,
     ...documents,
     ...applicantInformation,
     ...institutionDetails,
     ...amountPaid,
     ...paymentInformation, */
     //...process.env.REACT_APP_NAME !== "Citizen"? generatedApprovedBy : citizengeneratedApprovedBy


    ],

    footer: [],
    styles: {
      "noc-head": {
        fillColor: "#F2F2F2",
        margin: [-70, -41, -81, 0]
      },
      "noc-head-new": {
        fontSize: 9,
        //fillColor: "#F2F2F2",
        margin: [0, 0, 0, 0],
      },


      "receipt-logo-header": {
        color: "#484848",
        fontFamily: "raavi",
        fontSize: 16,
        bold: true,
        letterSpacing: 0.74,
        margin: [0, 0, 0, 5]
      },
      "receipt-logo-sub-header": {
        color: "#484848",
        fontFamily: "raavi",
        fontSize: 13,
        letterSpacing: 0.6
      },
      "noc-subhead": {
        fontSize: 12,
        bold: true,
        margin: [-18, 8, 0, 0],
        color: "#484848"
      },
      "noc-title": {
        fontSize: 10,
        bold: true,
        margin: [0, 0, 0, 0],
        color: "#484848",
        fontWeight: 500
      },
      "noc-table-my":{
        fontSize: 10,
        width: 60,
        margin:[100, 0, 0, 0]
      },
      "noc-table": {
        fontSize: 10,
        color: "#484848",
        margin: [-20, -2, -8, -8]
      },
      "noc-table-nocnumber":{
        bold:true,
        // fontSize:20
      },
      "receipt-header-details": {
        fontSize: 9,
        margin: [0, 0, 0, 8],
        color: "#484848"
      },
      "noc-table-key": {
        color: "#484848",
        bold: false,
        fontSize: 10
      },
      "receipt-table-value": {
        color: "#484848",
        bold: true,
        fontSize: 10
      },
      "receipt-table-firestation": {
        color: "#484848",
        bold: true,
        fontSize: 10
      },
      "receipt-footer": {
        color: "#484848",
        fontSize: 8,
        margin: [-6, 15, -15, -10]
      },
      "receipt-no": {
        color: "#484848",
        fontSize: 10
      },
      "receipt-approver": {
        fontSize: 12,
        bold: true,
        margin: [-20, 30, -10, 0],
        color: "#484848"
      }
    }
  };


  return dd;
};
//-------------Renew pdf -----------
const renewgetApplicationData = async (transformedData, ulbLogo, type) => {
  
  const ddi=transformedData.buildings[0].uoms;
  var NoBase=0,NoHeight=0;
  Object.keys(ddi).map((key,i) => {
  if(key == "NO_OF_BASEMENTS")
  NoBase=1;

  if(key == "HEIGHT_OF_BUILDING")
  NoHeight=1;
  });
  let reasonss =null;
  if(NoBase == 0 && NoHeight >0)
  {
    reasonss = {
      "HEIGHT_OF_BUILDING": transformedData.buildings[0].uoms.HEIGHT_OF_BUILDING,
      "NO_OF_BASEMENTS":0,
      "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
      }
  }
else if(NoBase >0 && NoHeight == 0)
{
  reasonss = {
    "HEIGHT_OF_BUILDING": 0,
    "NO_OF_BASEMENTS":transformedData.buildings[0].uoms.NO_OF_BASEMENTS,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}
else if(NoBase >0 && NoHeight> 0)
{
  reasonss = {
    "HEIGHT_OF_BUILDING": transformedData.buildings[0].uoms.HEIGHT_OF_BUILDING,
    "NO_OF_BASEMENTS":transformedData.buildings[0].uoms.NO_OF_BASEMENTS,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}
else 
{
  reasonss = {
    "HEIGHT_OF_BUILDING": 0,
    "NO_OF_BASEMENTS":0,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}

transformedData.buildings[0].uoms=reasonss;
  transformedData=updateMohall(transformedData)
  let borderLayout = {
    hLineWidth: function(i, node) {
      return i === 0 || i === node.table.body.length ? 0.1 : 0.1;
    },
    vLineWidth: function(i, node) {
      return i === 0 || i === node.table.widths.length ? 0.1 : 0.1;
    },
    hLineColor: function(i, node) {
      return i === 0 || i === node.table.body.length ? "#979797" : "#979797";
    },
    vLineColor: function(i, node) {
      return i === 0 || i === node.table.widths.length ? "#979797" : "#979797";
    }
  // paddingLeft: function(i, node) {
  //   return 5;
  // },
  // paddingRight: function(i, node) {
  //   return 5;
  // },
  // paddingTop: function(i, node) {
  //   return 5;
  // },
  // paddingBottom: function(i, node) {
  //   return 5;
  // }
  };

  let headerText = "Application Confirmation";
  let nocSubheadOne = [
    {
      text: [
        {
          text: "Application No.     ",
          bold: true
        },
        {
          text: transformedData.applicationNumber,
          bold: false
        }
      ],
      alignment: "left"
    },
    {
      text: [
        {
          text: "Date of Application ",
          bold: true
        },
        {
          text: transformedData.applicationDate,
          bold: false
        }
      ],
      alignment: "right"
    }
  ];
  let nocSubheadTwo = [
    {
      text: [
        {
          text: "Application Mode ",
          bold: true
        },
        {
          text: transformedData.applicationMode,
          bold: false
        }
      ],
      alignment: "left"
    }
  ];
  let nocDetails = [
    {
      style: "noc-table",
      table: {
        widths: ["*", "*", "*"],
        body: [
          [
            {
              //text: "NOC No ".transformedData.fireNOCNumber,
              text: `NOC No: ${transformedData.fireNOCNumber}`,
              border: [false, false, false, false],
              alignment: "left"

            },
            {
              text: `NOC Type: ${transformedData.nocType}`,
              border: [false, false, false, false],
              alignment: "center"
            },
            {
              text: `Dated: ${transformedData.issuedDate}`,
              border: [false, false, false, false],
              alignment: "right"
            }
          ],
          /* [
            {
              text: transformedData.fireNOCNumber,
              border: [false, false, false, false],
              style: "receipt-table-value"
            },
            {
              text: transformedData.nocType,
              border: [false, false, false, false],
              style: "receipt-table-value"
            },
            {
              text: transformedData.issuedDate,
              border: [false, false, false, false],
              style: "receipt-table-value"
            }
          ] */
        ]
      },
     // layout: borderLayout
    }
  ];
// ------------------------my renew details---------------------------

var buildAreaData =[];
var buildtableHead = [];
// ---- head table----
buildtableHead.push([
  {
    text: "Name of Building",
    border: [true, true, true, true]
  },
  {
    text: "No of Floors",
    border: [true, true, true, true]
  },
  {
    text: "Area (sq. mtr.)",
    border: [true, true, true, true]
  }
]);
// -----------------
if(transformedData.buildings.length > 1){
  
for(let mybuldingdata of transformedData.buildings){
 
  buildAreaData.push([
    {
      text: mybuldingdata.name,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
    {
      text: mybuldingdata.uoms.NO_OF_FLOORS,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
    {
      text: mybuldingdata.uoms.BUILTUP_AREA,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
  ]);
 
}
}

let nocDetailsmy = [];
if(transformedData.buildings.length > 1){
nocDetailsmy.push([
  {
    text: "BUILDING DETAILS",
    style: "noc-title-my",
    alignment: "left",
    margin: [10, 0, 0, 0],
  },
  {
    style: "noc-table-my",
    table: {
      widths: ["25%", "25%", "25%", "25%"],
      body: [
        ...buildtableHead,
        ...buildAreaData
        ]
    },
    layout: borderLayout
  }
]);
}

// -------------------------------------------------------------
  let space = [
    {
      text: "",
      style: "noc-title"
    }];



  let propertyDetails = [
    {
      text: "PROPERTY DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: getBuildings(transformedData)
      },
      layout: borderLayout
    }
  ];
  let propertyLocationDetails = [
    {
      text: "PROPERTY LOCATION DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: [

          [
            {
              text: "Area Type",
              border: [true, true, false, false]
            },
            {
              text: "District Name",
              border: [false, true, false, false]
            },
            {
              text: "Tehsil",
              border: [false, true, false, false]
            },
            {
              text: "Property Id",
              border: [false, true, true, false]
            },

          ],
          [
            {
              text: transformedData.areaType,
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.district,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.areaType ==='Rural'? transformedData.subDistrict:'N/A',
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.propertyId,
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],

          [

            {
              text: "City",
              border: [true, false, false, false]
            },
            {
              text: "Plot/Survey No.",
              border: [false, false, false, false]
            },

            {
              text: "Street Name",
              border: [false, false, false, false]
            },
            {
              text: "Location on Map",
              border: [false, false, true, false]

             },
          ],
          [

            {
              text: transformedData.areaType ==='Urban'? transformedData.city:'N/A',
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.door,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.street,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },

             {
              text: transformedData.gis,
              style: "receipt-table-value",
              border: [false, false, false, true]

            },
          ],
          [


            {
              text: " Village",
              border: [true, false, false, false]
            },
            {
              text: " Landmark",
              border: [false, false, false, false]
            },


            {
              text: " Mohalla",
              border: [false, false, false, false]
            },
            {
              text: "Pincode",
              border: [false, false, true, false]
            },

          ],
          [

            {
              text: transformedData.village,
              style: "receipt-table-value",
              border: [true, false, false, false]
            },

            {
              text: transformedData.landmark,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },

            {
              text: getMessageFromLocalization(transformedData.mohalla),
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.pincode,
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],

          [
            {
              text: "",
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],
          [
            {
              text: '',
              style: "receipt-table-value",
              border: [true, false, false, true]
            },
            {
              text: '',
              style: "receipt-table-firestation",
              border: [false, false, false, true]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, true, true]
            },
          ]

        ]
      },
      layout: borderLayout
    },

  ]

  let applicantDetails = [
    {
      text: "APPLICANT DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        //widths: ["*", "*", "*", "*"],
        widths: ["25%", "25%", "25%", "25%"],
        body: getOwners(transformedData)
      },
      layout: borderLayout
    }
  ];
  let institutionDetails = [
    {
      text: "INSTITUTION DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: [
          [
            {
              text: "Type of Institution",
              border: [true, true, false, false]
            },
            {
              text: "Name of Institute",
              border: [false, true, false, false]
            },
            {
              text: "Official Telephone No.",
              border: [false, true, false, false]
            },
            {
              text: "Authorized Person",
              border: [false, true, true, false]
            }
          ],
          [
            {
              text: getMessageFromLocalization(
                `COMMON_MASTERS_OWNERSHIPCATEGORY_${getTransformedLocale(
                  transformedData.ownershipType
                )}`
              ),
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.institutionName,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.telephoneNumber,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.owners[0].name,
              style: "receipt-table-value",
              border: [false, false, true, false]
            }
          ],
          [
            {
              text: "Designation in Institution",
              border: [true, false, false, false]
            },
            {
              text: "Mobile No. of Authorized Person",
              border: [false, false, false, false]
            },
            {
              text: "Email of Authorized Person",
              border: [false, false, false, false]
            },
            {
              text: "Official Correspondence Address",
              border: [false, false, true, false]
            }
          ],
          [
            {
              text: transformedData.institutionDesignation,
              style: "receipt-table-value",
              border: [true, false, false, true]
            },
            {
              text: transformedData.owners[0].mobile,
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: transformedData.owners[0].email,
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: transformedData.owners[0].address,
              style: "receipt-table-value",
              border: [false, false, true, true]
            }
          ]
        ]
      },
      layout: borderLayout
    },



  ];
  let documents = [];
  let owners = transformedData.owners.map(owner => [
    {
      text: "Applicant Name",
      border: [true, true, false, true],
      style: "receipt-table-value"
    },
    {
      text: owner.name,
      border: [false, true, true, true]
    },
    {
      text: "Mobile No.",
      border: [true, true, false, true],
      style: "receipt-table-value"
    },
    {
      text: owner.mobile,
      border: [false, true, true, true]
    }
  ]);
  let applicantInformation = [
    {
      text: "APPLICANT INFORMATION",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
      //  widths: ["25%", "25%", "25%", "25%"],
        widths: ["25%", "25%", "25%", "25%"],
        body: owners
      },
      layout: borderLayout
    }
  ];
  let amountPaid = [
    {
      text: "AMOUNT PAID",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*", "*"],
        body: [
          [
            {
              text: "NOC Fee",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            /* {
              text: "NOC Taxes",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }, */
            {
              text: "Adhoc Penalty/Rebate",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            {
              text: "TOTAL",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }
          ],
          [
            {
              text: transformedData.nocFee,
              border: [true, true, true, true],
              alignment: "center"
            },
           /*  {
              text: transformedData.nocTaxes,
              border: [true, true, true, true],
              alignment: "center"
            }, */
            {
              text: transformedData.nocAdhocPenaltyRebate,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.totalAmount,
              border: [true, true, true, true],
              alignment: "center"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];
  let paymentInformation = [
    {
      text: "PAYMENT INFORMATION",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*"],
        body: [
          [
            {
              text: "Payment Mode",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            {
              text: "Transaction ID/ Cheque/ DD No.",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }
          ],
          [
            {
              text: transformedData.paymentMode,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.transactionNumber,
              border: [true, true, true, true],
              alignment: "center"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];

  let citizengeneratedApprovedBy = [
    {
      style: "receipt-approver",
      columns: [
        {
          text: [
            {
             // text: "Approved by: ",
              //bold: true
            },
            {
            //  text: transformedData.auditorName,
             // bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Commissioner/EO",
              bold: true
            }
          ],
          alignment: "right"
        }
      ]
    }
 ] ;

  let generatedApprovedBy = [
    {
      style: "receipt-approver",
      columns: [
        {
          text: [
            {
              text: "Generated by: ",
              bold: true
            },
            {
              text: transformedData.auditorName,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Commissioner/EO",
              bold: true
            }
          ],
          alignment: "right"
        }
      ]
    }
  ];
  let qrText = `Application: ${transformedData.applicationNumber}, Date: ${
  transformedData.applicationDate
  }, Buildings: ${transformedData.propertyType}, Applicant: ${
  transformedData.owners[0].name
  }, Address: ${transformedData.address}`;

  if (transformedData.ownershipType.startsWith("INSTITUTION")) {
    applicantDetails = [];
    applicantInformation = [];
  } else {
    institutionDetails = [];
  }



  switch (type) {
    case "application":
      applicantInformation = [];
      amountPaid = [];
      paymentInformation = [];
      generatedApprovedBy = [];
      break;
    case "receipt":
      headerText = "Payment Receipt";
      nocSubheadOne = [
        {
          text: [
            {
              text: "Application No. ",
              bold: true
            },
            {
              text: transformedData.applicationNumber,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Date of Payment ",
              bold: true
            },
            {
              text: transformedData.paymentDate,
              bold: false
            }
          ],
          alignment: "right"
        }
      ];
      nocSubheadTwo = [
        {
          text: [
            {
              text: "Payment Receipt No.  ",
              bold: true
            },
            {
              text: transformedData.receiptNumber,
              bold: false
            }
          ],
          alignment: "left"
        }
      ];
      nocDetails = [];
      //nocDetailsmy = [];
      buildAreaData = [];
      propertyDetails = [];
      propertyLocationDetails = [];
      applicantDetails = [];
      amountPaid = [];
      paymentInformation = [];
      documents = [];
      qrText = `Application: ${
      transformedData.applicationNumber
      }, Receipt number: ${transformedData.receiptNumber}, Date of payment: ${
      transformedData.paymentDate
      }, Fees Paid: ${transformedData.amountPaid}, Payment mode: ${
      transformedData.paymentMode
      }, Transaction ID: ${transformedData.transactionNumber}`;
      break;
    case "certificate":
      headerText = "Certificate";

      applicantDetails = [];
      documents = [];


      citizengeneratedApprovedBy = [
        {
          style: "receipt-approver",
          columns: [
            {
              text: [
                {
                 // text: "Approved by: ",
                  //bold: true
                },
                {
                //  text: transformedData.auditorName,
                 // bold: false
                }
              ],
              alignment: "left"
            },
            {
              text: [
                {
                  text: "Commissioner/EO",
                  bold: true
                }
              ],
              alignment: "right"
            }
          ]
        }
     ] ;

      generatedApprovedBy = [
        {
          style: "receipt-approver",
          columns: [
            {
              text: [
                {
                  text: "Approved by: ",
                  bold: true
                },
                {
                  text: transformedData.auditorName,
                  bold: false
                }
              ],
              alignment: "left"
            },
            {
              text: [
                {
                  text: "Commissioner/EO",
                  bold: true
                }
              ],
              alignment: "right"
            }
          ]
        }
     ] ;

      qrText = `Application: ${
      transformedData.applicationNumber
      }, NOC Number: ${transformedData.fireNOCNumber}, Date of Issue: ${
      transformedData.issuedDate
      }, Valid Till: ${transformedData.validTo}, Buildings: ${
      transformedData.propertyType
      }, Applicant: ${transformedData.owners[0].name}`;
      break;
  }

  // Generate QR code base64 image
  let qrcode = await QRCode.toDataURL(qrText);


  let dd = {
    defaultStyle: {
      font: "raavi",
    },

    content: [
      {
        style: "noc-head-new",
        table: {
          widths: [120, "*", 120],
          body: [
            [
              {
                image: ulbLogo,
                width: 60,
               // height: 61.25,
                height :60,
                margin: [31, 12, 10, 10],
                border: [true, true, false, false],

              },
              {
                stack: [
                  {
                       text: [{ text:"Punjab Fire Services" , bold:true },],
                    style: "receipt-logo-header",
                    alignment: "center",
                  },
                  {
                       text: [{ text:`( ${transformedData.corporationName} )` , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                    fontSize:11
                  },
                  {
                     text: [{ text:"FIRE SAFETY CERTIFICATE" , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  },
                  {
                     text: [{ text:"ਫਾਇਰ ਸੇਫਟੀ ਪਮਾਣ ਪੱਤਰ" , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  }
                ],
                  /* {
                    //text: transformedData.corporationName,
                    text: "Punjab Fire Services",
                    style: "receipt-logo-header",
                    alignment: "center",
                  },
                  {
                    //text:  transformedData.areaType ==='Rural'? transformedData.corporationName: transformedData.city,
                    text:  `( ${ transformedData.corporationName} )`,
                    style: "receipt-logo-sub-header",
                    alignment: "center",

                  },
                  {
                    text: "FIRE SAFETY CERTIFICATE",
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  },
                  {
                    text: "ਫਾਇਰ ਸੇਫਟੀ ਪਮਾਣ ਪੱਤਰ",
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  }
                ], */
                alignment: "left",
                margin: [10, 23, 0, 0],
                border: [false, true, false, false],

              },
              {
                image: qrcode,
                width: 70,
                height: 70,
                margin: [20, 8, 8, 8],
                alignment: "right",
                border: [false, true, true, false],

              }

            ],
    /*         [
              {
                text: `NOC No ${transformedData.fireNOCNumber}`,
                border: [true, false, false, false],
                alignment: "left"

              },
              {

                text: `NOC Type: ${transformedData.nocType}`,
                border: [false, false, false, false],
                alignment: "center"
              },
              {

                text: `Dated ${transformedData.issuedDate}`,
                border: [false, false, true, false],
                alignment: "right"
              }

            ],  */



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*", '*', '*'],
          body: [

            [
              {
                //text: `NOC No: ${transformedData.fireNOCNumber}`,
                text: [  "NOC No:" ,
                { text: `${transformedData.fireNOCNumber}`,
                bold:true },
                ],
                border: [true, false, false, false],
                alignment: "left",
                style:"noc-table-nocnumber"

              },
              {

               // text: `NOC Type: ${transformedData.nocType}`,
                text: [  "NOC Type:" ,
                { text: `${transformedData.nocType}`,
                bold:true },
                ],
                border: [false, false, false, false],
                alignment: "center"
              },
              {

                //text: `Dated: ${transformedData.issuedDate}`,
                text: [  "Dated: " ,
                { text: `${transformedData.issuedDate}`,
                bold:true },
                ],
                border: [false, false, true, false],
                alignment: "right"
              }

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [

              {
                text: ["                       Certified that the ",{ text:`${transformedData.buildings[0].name}`, bold:true }, " at " , { text:`${transformedData.address}`, bold:true }, "comprised of ", { text:`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`?`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`:'0', bold:true }, "  basements and ", { text:`${transformedData.buildings[0].uoms.NO_OF_FLOORS}`, bold:true }, " (Upper floor) owned/occupied by ",{ text:`${transformedData.owners[0].name}`, bold:true }, " have compiled with the fire prevention and fire safety requirements of National Building Code and verified by the officer concerned of fire service on ", { text:`${transformedData.issuedDate}`, bold:true }, " in the presence of ", { text:`${transformedData.owners[0].name}`, bold:true }, " (Name of the owner or his representative) and that the building/premises is fit for occupancy " , { text:`${transformedData.NBCGroup}`, bold:true }, " subdivision ", { text:`${transformedData.NBCSubGroup}`, bold:true }, " (As per NBC) for period of ", { text:"one year", bold:true }, " from issue date. Subject to the following conditions."],
                border: [true, false, true, false],
                alignment: "justify",
                preserveLeadingSpaces: true


              },

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [

              {
                text: ["Issued on two",{ text:`${transformedData.issuedDate}`, bold:true }," at ", { text:`${transformedData.corporationName}`, bold:true }],

               // text: `Issued on ${transformedData.issuedDate} at ${transformedData.corporationName}`,

                border: [true, false, true, false],
                alignment: "left"
              },

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: ["                        ਤਸਦੀਕ ਕੀਤਾ ਜਾਂਦਾ ਹੈ ਕ‌ਿ ",{ text:`${transformedData.buildings[0].name}`, bold:true }, ", ",{ text:`${transformedData.address}`, bold:true }, " ਸਮੇਤ ",{ text:`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`?`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`:'0', bold:true }, " ਬੇਸਮਟ ਅਤੇ ",{ text:`${transformedData.buildings[0].uoms.NO_OF_FLOORS}`, bold:true }, " (ਉਪਰਲੀ ਮੰਜ਼ਿਲ) ਮਲਕੀਅਤ/ਕਬਜ਼ਾਦਾਰ ",{ text:`${transformedData.buildings[0].name}`, bold:true }, " ਰਾਸ਼ਟਰੀ ਬਿਲਡਿੰਗ ਕੋਡ ਅਨੁਸਾਰ ਅੱਗ ਬੁਝਾਉਣ ਦੇ ਪ੍ਰਭਾਵ ਅਤੇ ਬਚਾਅ ਦੀਆਂ ਲੌੜਾਂ ਨੂੰ ਪੂਰਾ ਕਰਦੀ ਹੈ  ਜਿਸ ਨੂੰ ਸਬੰਧਤ ਫਾਇਰ ਅਧਿਕਾਰੀ ਵੱਲੌਂ ",{ text:`${transformedData.owners[0].name}`, bold:true }, " (ਮਾਲਕ ਜਾਂ ਉਸ ਦੇ ਪ੍ਰਤਿਨਿਧੀ ਦਾ ਨਾਮ ) ਦੀ ਮੋਜੂਦਗੀ ਵਿੱਚ ",{ text:`${transformedData.issuedDate}`, bold:true }, " ਨੂੰ ਪ੍ਰਮਾਣਿਤ ਕੀਤਾ ਗਿਆ ਅਤੇ ਇਮਾਰਤ / ਬਿਲਡਿੰਗ ",{ text:`${transformedData.NBCGroup}`, bold:true }, " subdivision ",{ text:`${transformedData.NBCSubGroup}`, bold:true }, " (ਐਨ. ਬੀ. ਸੀ. ਦੇ ਅਨੁਸਾਰ) ਦੀ ਆਬਾਦੀ ਲਈ Issue date ਤੌਂ ",{ text:"ਇੱਕ ਸਾਲ", bold:true }, " ਤੱਕ ਯੋਗ ਹੈ ਜਿਸ ਲਈ ਨਿਮਨ ਅਨੁਸਾਰ ਹਦਾਇਤਾਂ ਹਨ।"],
                border: [true, false, true, false],
                alignment: "justify",
                preserveLeadingSpaces: true


              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "",
                border: [true, false, true, false],
                alignment: "left",
              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
               // text: `ਜਾਰੀ ਕਰਨ ਦੀ ਿਮਤੀ ${transformedData.issuedDate}  ਿਕੱਥੇ ${transformedData.corporationName}.`,
                text: [{ text:`${transformedData.corporationName}`, bold:true },"  ਵਿਖੇ ਜਾਰੀ ਕਰਨ ਦੀ ਮਿਤੀ ", { text:`${transformedData.issuedDate}`, bold:true },"."],

                border: [true, false, true, false],
                alignment: "left"
              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "",
                border: [true, false, true, false],
                alignment: "left",
              },
           ],



          ]
        },

        layout: {},
      },
      ...nocDetailsmy,
      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                margin: [10, 0, 0, 0],

                text: "1. Fire Safety arrangements shall be kept in working condition at all times",
                border: [true, false, true, false],
                alignment: "left"
              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਹਰ ਸਮੇਂ ਅੱਗ ਬਚਾਅ ਦੇ ਯੰਤਰਾਂ ਨੂੰ ਚਾਲੂ /ਚੰਗੀ ਹਾਲਤ ਵਿੱਚ ਰੱਖਿਆ ਜਾਵੇ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "2. No, alteration/ addition/ change in use of occupancy is allowed.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਕਿਸੇ ਵੀ ਤਰਾਂ ਦੇ ਬਦਲਾਅ/ ਵਾਧੇ/ ਕਬਜ਼ਾਦਾਰ ਵਿੱਚ ਬਦਲਾਵ ਦੀ ਮਨਾਹੀ ਹੈ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "3. Occupants/ owner should have trained staff to operate the operaon of fire safety system provided there in.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਉਪਲੱਬਧ ਅੱਗ ਬੁਝਾਉਣ ਦੇ ਯੰਤਰ ਦੀ ਵਰਤੋਂ ਲਈ ਰਿਹਣ ਵਾਲੇ ਲੋਕਾਂ / ਮਾਲਕ ਨੂੰ ਜਾਣੂੰ ਕਰਵਾਇਆ ਜਾਣਾ ਯਕੀਨੀ ਬਣਾਇਆ ਜਾਵੇ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "4. Fire Officer can check the arrangements of fire safety at any time, this cerficate will be withdrawn without any notice if any deficiency is found.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਫਾਇਰ ਬ੍ਰਿਗੇਡ ਅਧਿਕਾਰੀ ਕਿਸੇ ਵੀ ਵਕਤ ਇਨਾਂ ਸਾਰੇ ਪ੍ਰਬੰਧਾਂ ਨੂੰ ਚੈਕ ਕਰ ਸਕਦਾ ਹੈ, ਜੇਕਰ ਕੋਈ ਕਮੀ ਪਾਈ ਗਈ ਤਾਂ ਬਿਨਾਂ ਕਿਸੇ ਨੋਟਿਸ ਦੇ ਇਹ ਸਰਟੀਿਫਕੇਟ ਰੱਦ ਸਮਝਿਆ ਜਾਵੇਗਾ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "5.Occupants/ owner should apply for renewal of fire safety cerficate one month prior to expiry of this cerficate.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਮਾਲਕ ਜਾਰੀ ਕੀਤੇ ਗਏ ਫਾਇਰ ਸੇਫਟੀ ਸਰਟੀਿਫਕੇਟ ਦੀ ਮਿਤੀ ਖਤਮ ਹੋਣ ਤੌਂ ਇੱਕ ਮਹੀਨਾ ਪਹਿਲਾਂ ਰੀਨੀਊ ਕਰਵਾਉਣ ਲਈ ਪਾਬੰਦ ਹੋਵੇਗਾ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },


      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "* Above Details cannot be used as ownership proof.",
                border: [true, false, true, false],
                alignment: "left",
                color: "#FF0000",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: [{ text:"ਉਪਰੋਕਤ ਦਰਸਾਈ ਗਈ ਜਾਣਕਾਰੀ ਨੂੰ  ਮਾਲਕਾਨਾ ਦੇ ਸਬੂਤ ਵਜ਼ੋ ਨਹੀਂ ਵਰਤਿਆ ਜਾਵੇਗਾ।", bold:true  }],
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "This is digitaly created cerificate, no signatue are needed",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: [{ text:"ਇਹ ਡਿਜੀਟਲੀ (ਕੰਪ‌ਿਊਟਰਾਈਜ਼ਡ) ਤਿਆਰ ਕੀਤਾ ਗਿਆ ਸਰਟੀਫਿਕੇਟ ਹੈ, ਜਿਸ ਵਿੱਚ ਦਸਤਖਤ ਦੀ ਕੋਈ ਲੋੜ ਨਹੀਂ ਹੈ।", bold:true  }],
                border: [true, false, true, true],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },







     /* ...space,
     ...space,
     ...nocDetails,
     ...space,
     ...firstparagraph,
     ...issued,
     ...secondparagraph,
     ...pointsheadline,
     ...firstpointinenglish,
     ...firstpointinpunjabi,
     ...secondpointinenglish,
     ...secondpointinpunjabi,
     ...thridpointinenglish,
     ...thirdpointinpunjabi,
     ...fourthpointinenglish,
     ...fourthpointinpunjabi,
     ...fifthpointinenglish,
     ...fifthpointinpunjabi,
     ...starmarkoneinenglish,
     ...starmarkoneinpunjabi,
     ...starmarktwoinenglish,
     ...starmarktwoinpunjabi, */


     /* ...propertyDetails,
     ...propertyLocationDetails,
     ...applicantDetails,
     ...documents,
     ...applicantInformation,
     ...institutionDetails,
     ...amountPaid,
     ...paymentInformation, */
     //...process.env.REACT_APP_NAME !== "Citizen"? generatedApprovedBy : citizengeneratedApprovedBy


    ],

    footer: [],
    styles: {
      "noc-head": {
        fillColor: "#F2F2F2",
        margin: [-70, -41, -81, 0]
      },
      "noc-head-new": {
        fontSize: 9,
        //fillColor: "#F2F2F2",
        margin: [0, 0, 0, 0],
      },


      "receipt-logo-header": {
        color: "#484848",
        fontFamily: "raavi",
        fontSize: 16,
        bold: true,
        letterSpacing: 0.74,
        margin: [0, 0, 0, 5]
      },
      "receipt-logo-sub-header": {
        color: "#484848",
        fontFamily: "raavi",
        fontSize: 13,
        letterSpacing: 0.6
      },
      "noc-subhead": {
        fontSize: 12,
        bold: true,
        margin: [-18, 8, 0, 0],
        color: "#484848"
      },
      "noc-title": {
        fontSize: 10,
        bold: true,
        margin: [0, 0, 0, 0],
        color: "#484848",
        fontWeight: 500
      },
      "noc-table-my":{
        fontSize: 10,
        width: 60,
        margin:[100, 0, 0, 0]
      },
      "noc-table": {
        fontSize: 10,
        color: "#484848",
        margin: [-20, -2, -8, -8]
      },
      "noc-table-nocnumber":{
        bold:true,
        // fontSize:20
      },
      "receipt-header-details": {
        fontSize: 9,
        margin: [0, 0, 0, 8],
        color: "#484848"
      },
      "noc-table-key": {
        color: "#484848",
        bold: false,
        fontSize: 10
      },
      "receipt-table-value": {
        color: "#484848",
        bold: true,
        fontSize: 10
      },
      "receipt-table-firestation": {
        color: "#484848",
        bold: true,
        fontSize: 10
      },
      "receipt-footer": {
        color: "#484848",
        fontSize: 8,
        margin: [-6, 15, -15, -10]
      },
      "receipt-no": {
        color: "#484848",
        fontSize: 10
      },
      "receipt-approver": {
        fontSize: 12,
        bold: true,
        margin: [-20, 30, -10, 0],
        color: "#484848"
      }
    }
  };


  return dd;
};
//---------------------------end renew pdf--------------
const provisionApplicationData = async (transformedData, ulbLogo, type) => {
 
  const ddi=transformedData.buildings[0].uoms;
  var NoBase=0,NoHeight=0;
  Object.keys(ddi).map((key,i) => {
  if(key == "NO_OF_BASEMENTS")
  NoBase=1;

  if(key == "HEIGHT_OF_BUILDING")
  NoHeight=1;
  });
  let reasonss =null;
  if(NoBase == 0 && NoHeight >0)
  {
    reasonss = {
      "HEIGHT_OF_BUILDING": transformedData.buildings[0].uoms.HEIGHT_OF_BUILDING,
      "NO_OF_BASEMENTS":0,
      "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
      }
  }
else if(NoBase >0 && NoHeight == 0)
{
  reasonss = {
    "HEIGHT_OF_BUILDING": 0,
    "NO_OF_BASEMENTS":transformedData.buildings[0].uoms.NO_OF_BASEMENTS,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}
else if(NoBase >0 && NoHeight> 0)
{
  reasonss = {
    "HEIGHT_OF_BUILDING": transformedData.buildings[0].uoms.HEIGHT_OF_BUILDING,
    "NO_OF_BASEMENTS":transformedData.buildings[0].uoms.NO_OF_BASEMENTS,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}
else 
{
  reasonss = {
    "HEIGHT_OF_BUILDING": 0,
    "NO_OF_BASEMENTS":0,
    "NO_OF_FLOORS":transformedData.buildings[0].uoms.NO_OF_FLOORS
    }
}

transformedData.buildings[0].uoms=reasonss;
  transformedData=updateMohall(transformedData)
  let borderLayout = {
    hLineWidth: function(i, node) {
      return i === 0 || i === node.table.body.length ? 0.1 : 0.1;
    },
    vLineWidth: function(i, node) {
      return i === 0 || i === node.table.widths.length ? 0.1 : 0.1;
    },
    hLineColor: function(i, node) {
      return i === 0 || i === node.table.body.length ? "#979797" : "#979797";
    },
    vLineColor: function(i, node) {
      return i === 0 || i === node.table.widths.length ? "#979797" : "#979797";
    }
  // paddingLeft: function(i, node) {
  //   return 5;
  // },
  // paddingRight: function(i, node) {
  //   return 5;
  // },
  // paddingTop: function(i, node) {
  //   return 5;
  // },
  // paddingBottom: function(i, node) {
  //   return 5;
  // }
  };

  let headerText = "Application Confirmation";
  let nocSubheadOne = [
    {
      text: [
        {
          text: "Application No.     ",
          bold: true
        },
        {
          text: transformedData.applicationNumber,
          bold: false
        }
      ],
      alignment: "left"
    },
    {
      text: [
        {
          text: "Date of Application ",
          bold: true
        },
        {
          text: transformedData.applicationDate,
          bold: false
        }
      ],
      alignment: "right"
    }
  ];
  let nocSubheadTwo = [
    {
      text: [
        {
          text: "Application Mode ",
          bold: true
        },
        {
          text: transformedData.applicationMode,
          bold: false
        }
      ],
      alignment: "left"
    }
  ];
  let nocDetails = [
    {
      style: "noc-table",
      table: {
        widths: ["*", "*", "*"],
        body: [
          [
            {
            //  text: `NOC No: ${transformedData.fireNOCNumber}`,
              text: [  "NOC No:" ,
              { text: '${transformedData.fireNOCNumber}',
              bold:true },
               ],
              border: [false, false, false, false],
              //style: "noc-table-key"
              alignment: "left"

            },
            {
              text: `NOC Type: ${transformedData.nocType}`,
              border: [false, false, false, false],
              alignment: "center"
            },
            {
              text: `Dated: ${transformedData.issuedDate}`,
              border: [false, false, false, false],
              alignment: "right"
            }
          ],
          /* [
            {
              text: transformedData.fireNOCNumber,
              border: [false, false, false, false],
              style: "receipt-table-value"
            },
            {
              text: transformedData.nocType,
              border: [false, false, false, false],
              style: "receipt-table-value"
            },
            {
              text: transformedData.issuedDate,
              border: [false, false, false, false],
              style: "receipt-table-value"
            }
          ] */
        ]
      },
     // layout: borderLayout
    }
  ];
// ------------------------my details---------------------------

var buildAreaData =[];
var buildtableHead = [];
// ---- head table----
buildtableHead.push([
  {
    text: "Name of Building",
    border: [true, true, true, true]
  },
  {
    text: "No of Floors",
    border: [true, true, true, true]
  },
  {
    text: "Area (sq. mtr.)",
    border: [true, true, true, true]
  }
]);
// -----------------

if(transformedData.buildings.length > 1){


for(let mybuldingdata of transformedData.buildings){

  buildAreaData.push( [
    {
      text: mybuldingdata.name,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
    {
      text: mybuldingdata.uoms.NO_OF_FLOORS,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
    {
      text: mybuldingdata.uoms.BUILTUP_AREA,
      border: [true, true, true, true],
      style: "receipt-table-value"
    },
  ]);
  
}
}
let nocDetailsmy = [];
if(transformedData.buildings.length > 1){
nocDetailsmy.push([
  {
    text: "BUILDING DETAILS",
    style: "noc-title-my",
    alignment: "left",
    margin: [10, 0, 0, 0],
  },
  {
    style: "noc-table-my",
    table: {
      widths: ["25%", "25%", "25%", "25%"],
      body: [
        ...buildtableHead,
        ...buildAreaData
        ]
    },
    layout: borderLayout
  }
]);
}
// -------------------------------------------------------------
  let space = [
    {
      text: "",
      style: "noc-title"
    }];






  let propertyDetails = [
    {
      text: "PROPERTY DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: getBuildings(transformedData)
      },
      layout: borderLayout
    }
  ];
  let propertyLocationDetails = [
    {
      text: "PROPERTY LOCATION DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: [

          [
            {
              text: "Area Type",
              border: [true, true, false, false]
            },
            {
              text: "District Name",
              border: [false, true, false, false]
            },
            {
              text: "Tehsil",
              border: [false, true, false, false]
            },
            {
              text: "Property Id",
              border: [false, true, true, false]
            },

          ],
          [
            {
              text: transformedData.areaType,
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.district,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.areaType ==='Rural'? transformedData.subDistrict:'N/A',
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.propertyId,
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],

          [

            {
              text: "City",
              border: [true, false, false, false]
            },
            {
              text: "Plot/Survey No.",
              border: [false, false, false, false]
            },

            {
              text: "Street Name",
              border: [false, false, false, false]
            },
            {
              text: "Location on Map",
              border: [false, false, true, false]

             },
          ],
          [

            {
              text: transformedData.areaType ==='Urban'? transformedData.city:'N/A',
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.door,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.street,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },

             {
              text: transformedData.gis,
              style: "receipt-table-value",
              border: [false, false, false, true]

            },
          ],
          [


            {
              text: " village",
              border: [true, false, false, false]
            },
            {
              text: " landmark",
              border: [false, false, false, false]
            },


            {
              text: " Mohalla",
              border: [false, false, false, false]
            },
            {
              text: "Pincode",
              border: [false, false, true, false]
            },

          ],
          [

            {
              text: transformedData.village,
              style: "receipt-table-value",
              border: [true, false, false, false]
            },

            {
              text: transformedData.landmark,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },

            {
              text: getMessageFromLocalization(transformedData.mohalla),
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.pincode,
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],
           [
            {
              text: "",
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, true, false]
            },

          ],
          [
            {
              text: '',
              style: "receipt-table-value",
              border: [true, false, false, true]
            },
            {
              text: '',
              style: "receipt-table-firestation",
              border: [false, false, false, true]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: "",
              style: "receipt-table-value",
              border: [false, false, true, true]
            },
          ]

        ]
      },
      layout: borderLayout
    },

  ]

  let applicantDetails = [
    {
      text: "APPLICANT DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        //widths: ["*", "*", "*", "*"],
        widths: ["25%", "25%", "25%", "25%"],
        body: getOwners(transformedData)
      },
      layout: borderLayout
    }
  ];
  let institutionDetails = [
    {
      text: "INSTITUTION DETAILS",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["25%", "25%", "25%", "25%"],
        body: [
          [
            {
              text: "Type of Institution",
              border: [true, true, false, false]
            },
            {
              text: "Name of Institute",
              border: [false, true, false, false]
            },
            {
              text: "Official Telephone No.",
              border: [false, true, false, false]
            },
            {
              text: "Authorized Person",
              border: [false, true, true, false]
            }
          ],
          [
            {
              text: getMessageFromLocalization(
                `COMMON_MASTERS_OWNERSHIPCATEGORY_${getTransformedLocale(
                  transformedData.ownershipType
                )}`
              ),
              style: "receipt-table-value",
              border: [true, false, false, false]
            },
            {
              text: transformedData.institutionName,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.telephoneNumber,
              style: "receipt-table-value",
              border: [false, false, false, false]
            },
            {
              text: transformedData.owners[0].name,
              style: "receipt-table-value",
              border: [false, false, true, false]
            }
          ],
          [
            {
              text: "Designation in Institution",
              border: [true, false, false, false]
            },
            {
              text: "Mobile No. of Authorized Person",
              border: [false, false, false, false]
            },
            {
              text: "Email of Authorized Person",
              border: [false, false, false, false]
            },
            {
              text: "Official Correspondence Address",
              border: [false, false, true, false]
            }
          ],
          [
            {
              text: transformedData.institutionDesignation,
              style: "receipt-table-value",
              border: [true, false, false, true]
            },
            {
              text: transformedData.owners[0].mobile,
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: transformedData.owners[0].email,
              style: "receipt-table-value",
              border: [false, false, false, true]
            },
            {
              text: transformedData.owners[0].address,
              style: "receipt-table-value",
              border: [false, false, true, true]
            }
          ]
        ]
      },
      layout: borderLayout
    },



  ];
  let documents = [];
  let owners = transformedData.owners.map(owner => [
    {
      text: "Applicant Name",
      border: [true, true, false, true],
      style: "receipt-table-value"
    },
    {
      text: owner.name,
      border: [false, true, true, true]
    },
    {
      text: "Mobile No.",
      border: [true, true, false, true],
      style: "receipt-table-value"
    },
    {
      text: owner.mobile,
      border: [false, true, true, true]
    }
  ]);
  let applicantInformation = [
    {
      text: "APPLICANT INFORMATION",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
      //  widths: ["25%", "25%", "25%", "25%"],
        widths: ["25%", "25%", "25%", "25%"],
        body: owners
      },
      layout: borderLayout
    }
  ];
  let amountPaid = [
    {
      text: "AMOUNT PAID",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*", "*"],
        body: [
          [
            {
              text: "NOC Fee",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            /* {
              text: "NOC Taxes",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }, */
            {
              text: "Adhoc Penalty/Rebate",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            {
              text: "TOTAL",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }
          ],
          [
            {
              text: transformedData.nocFee,
              border: [true, true, true, true],
              alignment: "center"
            },
            /* {
              text: transformedData.nocTaxes,
              border: [true, true, true, true],
              alignment: "center"
            }, */
            {
              text: transformedData.nocAdhocPenaltyRebate,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.totalAmount,
              border: [true, true, true, true],
              alignment: "center"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];
  let paymentInformation = [
    {
      text: "PAYMENT INFORMATION",
      style: "noc-title"
    },
    {
      style: "noc-table",
      table: {
        widths: ["*", "*"],
        body: [
          [
            {
              text: "Payment Mode",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            },
            {
              text: "Transaction ID/ Cheque/ DD No.",
              border: [true, true, true, true],
              style: "receipt-table-value",
              alignment: "center"
            }
          ],
          [
            {
              text: transformedData.paymentMode,
              border: [true, true, true, true],
              alignment: "center"
            },
            {
              text: transformedData.transactionNumber,
              border: [true, true, true, true],
              alignment: "center"
            }
          ]
        ]
      },
      layout: borderLayout
    }
  ];

  let citizengeneratedApprovedBy = [
    {
      style: "receipt-approver",
      columns: [
        {
          text: [
            {
             // text: "Approved by: ",
              //bold: true
            },
            {
            //  text: transformedData.auditorName,
             // bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Commissioner/EO",
              bold: true
            }
          ],
          alignment: "right"
        }
      ]
    }
 ] ;

  let generatedApprovedBy = [
    {
      style: "receipt-approver",
      columns: [
        {
          text: [
            {
              text: "Generated by: ",
              bold: true
            },
            {
              text: transformedData.auditorName,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Commissioner/EO",
              bold: true
            }
          ],
          alignment: "right"
        }
      ]
    }
  ];
  let qrText = `Application: ${transformedData.applicationNumber}, Date: ${
  transformedData.applicationDate
  }, Buildings: ${transformedData.propertyType}, Applicant: ${
  transformedData.owners[0].name
  }, Address: ${transformedData.address}`;

  if (transformedData.ownershipType.startsWith("INSTITUTION")) {
    applicantDetails = [];
    applicantInformation = [];
  } else {
    institutionDetails = [];
  }



  switch (type) {
    case "application":
      applicantInformation = [];
      amountPaid = [];
      paymentInformation = [];
      generatedApprovedBy = [];
      break;
    case "receipt":
      headerText = "Payment Receipt";
      nocSubheadOne = [
        {
          text: [
            {
              text: "Application No. ",
              bold: true
            },
            {
              text: transformedData.applicationNumber,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Date of Payment ",
              bold: true
            },
            {
              text: transformedData.paymentDate,
              bold: false
            }
          ],
          alignment: "right"
        }
      ];
      nocSubheadTwo = [
        {
          text: [
            {
              text: "Payment Receipt No.  ",
              bold: true
            },
            {
              text: transformedData.receiptNumber,
              bold: false
            }
          ],
          alignment: "left"
        }
      ];
      nocDetails = [];
      nocDetailsmy = [];
      buildAreaData = [];
      propertyDetails = [];
      propertyLocationDetails = [];
      applicantDetails = [];
      documents = [];
      qrText = `Application: ${
      transformedData.applicationNumber
      }, Receipt number: ${transformedData.receiptNumber}, Date of payment: ${
      transformedData.paymentDate
      }, Fees Paid: ${transformedData.amountPaid}, Payment mode: ${
      transformedData.paymentMode
      }, Transaction ID: ${transformedData.transactionNumber}`;
      break;
    case "certificate":
      headerText = "Certificate";
      /* nocSubheadOne = [
        {
          text: [
            {
              text: "Fire NOC No. ",
              bold: true
            },
            {
              text: transformedData.fireNOCNumber,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Application No. ",
              bold: true
            },
            {
              text: transformedData.applicationNumber,
              bold: false
            }
          ],
          alignment: "right"
        }
      ];
       nocSubheadTwo = [
        {
          text: [
            {
              text: "Date of Issue ",
              bold: true
            },
            {
              text: transformedData.issuedDate,
              bold: false
            }
          ],
          alignment: "left"
        },
        {
          text: [
            {
              text: "Valid Till ",
              bold: true
            },
            {
              text: transformedData.validTo,
              bold: false
            }
          ],
          alignment: "right"
        }
      ]; */
      applicantDetails = [];
      documents = [];


      citizengeneratedApprovedBy = [
        {
          style: "receipt-approver",
          columns: [
            {
              text: [
                {
                 // text: "Approved by: ",
                  //bold: true
                },
                {
                //  text: transformedData.auditorName,
                 // bold: false
                }
              ],
              alignment: "left"
            },
            {
              text: [
                {
                  text: "Commissioner/EO",
                  bold: true
                }
              ],
              alignment: "right"
            }
          ]
        }
     ] ;

      generatedApprovedBy = [
        {
          style: "receipt-approver",
          columns: [
            {
              text: [
                {
                  text: "Approved by: ",
                  bold: true
                },
                {
                  text: transformedData.auditorName,
                  bold: false
                }
              ],
              alignment: "left"
            },
            {
              text: [
                {
                  text: "Commissioner/EO",
                  bold: true
                }
              ],
              alignment: "right"
            }
          ]
        }
     ] ;

      qrText = `Application: ${
      transformedData.applicationNumber
      }, NOC Number: ${transformedData.fireNOCNumber}, Date of Issue: ${
      transformedData.issuedDate
      }, Valid Till: ${transformedData.validTo}, Buildings: ${
      transformedData.propertyType
      }, Applicant: ${transformedData.owners[0].name}`;
      break;
  }

  // Generate QR code base64 image
  let qrcode = await QRCode.toDataURL(qrText);


  let dd = {
    defaultStyle: {
      font: "raavi",

    },

    content: [
      {
        style: "noc-head-new",
        table: {
          widths: [120, "*", 120],
          body: [
            [
              {
                image: ulbLogo,
                width: 60,
                // height: 61.25,
                height :60,
                margin: [30, 12, 10, 10],
                border: [true, true, false, false],

              },
              {
                stack: [
                  {
                       text: [{ text:"Punjab Fire Services" , bold:true },],
                    style: "receipt-logo-header",
                    alignment: "center",
                  },
                  {
                       text: [{ text:`( ${transformedData.corporationName} )` , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                    fontSize:11
                  },
                  {
                     text: [{ text:"FIRE SAFETY CERTIFICATE" , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  },
                  {
                     text: [{ text:"ਫਾਇਰ ਸੇਫਟੀ ਪਮਾਣ ਪੱਤਰ" , bold:true },],
                    style: "receipt-logo-sub-header",
                    alignment: "center",
                  }
                ],
                alignment: "left",
                margin: [10, 23, 0, 0],
                border: [false, true, false, false],

              },
              {
                image: qrcode,
                width: 70,
                height: 70,
                margin: [10, 8, 8, 8],
                alignment: "right",
                border: [false, true, true, false],

              }

            ],
    /*         [
              {
                text: `NOC No ${transformedData.fireNOCNumber}`,
                border: [true, false, false, false],
                alignment: "left"

              },
              {

                text: `NOC Type: ${transformedData.nocType}`,
                border: [false, false, false, false],
                alignment: "center"
              },
              {

                text: `Dated ${transformedData.issuedDate}`,
                border: [false, false, true, false],
                alignment: "right"
              }

            ],  */



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*", '*', '*'],
          body: [

            [
              {
                text: [  "NOC No: " ,
                  { text: `${transformedData.fireNOCNumber}`,
                  bold:true },
                ],
                border: [true, false, false, false],
                alignment: "left"

              },
              {
                text: [  "NOC Type: " ,
                  { text: `${transformedData.nocType}`,
                  bold:true },
                ],

                //text: `NOC Type: ${transformedData.nocType}`,
                border: [false, false, false, false],
                alignment: "center"
              },
              {
                text: [  "Dated: " ,
                  { text: `${transformedData.issuedDate}`,
                  bold:true },
                ],

                //text: `Dated: ${transformedData.issuedDate}`,
                border: [false, false, true, false],
                alignment: "right"
              }

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [

              {
                text:  ["                   Certified that the ", { text:`${transformedData.buildings[0].name}`, bold:true }, " at ", { text:`${transformedData.address}`, bold:true }, "has been inspected by the fire officer. This site is vacant/under-construction and is accessible to fire brigade. As per proposed drawing, building is to be constructed with", { text:`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`?`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`:'0', bold:true }, "  basements and ", { text:`${transformedData.buildings[0].uoms.NO_OF_FLOORS}`, bold:true }, " (Upper floor). Fire department has examined the fire safety layout plan/drawing and found it fit for occupancy of ", { text:`${transformedData.NBCGroup}`, bold:true }, " subdivision ", { text:`${transformedData.NBCSubGroup}`, bold:true }, " (as per NBC)."],
                border: [true, false, true, false],
                alignment: "justify",
                textIndent : 50,
                preserveLeadingSpaces: true

              },

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [

              {
                text: ["Issued on ",{ text:`${transformedData.issuedDate}`, bold:true }," at ", { text:`${transformedData.corporationName}`, bold:true }],

                    border: [true, false, true, false],
                alignment: "left",

              },

           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: ["                        ਤਸਦੀਕ ਕੀਤਾ ਜਾਂਦਾ ਹੈ ਕਿ ",  { text:`${transformedData.buildings[0].name}`, bold:true }, ", ", { text:`${transformedData.address}`, bold:true }, " ਦੀ ਫਾਇਰ ਅਫਸਰ ਵੱਲੌਂ ਪੜਤਾਲ ਕੀਤੀ ਗਈ। ਇਸ ਸਮੇਂ ਇਹ ਜਗਾ ਖਾਲੀ/ਉਸਾਰੀ ਅਧੀਨ ਹੈ ਅਤੇ ਫਾਇਰ ਬ੍ਰਿਗੇਡ ਦੀ ਪਹੁੰਚ ਦੇ ਅੰਦਰ ਹੈ। ਲੇਆਊਟ ਪਲਾਨ/ ਡਰਾਇੰਗ ਮੁਤਾਬਕ ", { text:`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`?`${transformedData.buildings[0].uoms.NO_OF_BASEMENTS}`:'0', bold:true }, " ਬੇਸਮਟ ਅਤੇ ", { text:`${transformedData.buildings[0].uoms.NO_OF_FLOORS}`, bold:true }, " ਮੰਜ਼ਿਲ ਹਨ। ਫਾਇਰ ਵਿਭਾਗ ਵਲੋਂ  ਜਮਾ ਕਰਵਾਏ ਗਏ ਫਾਇਰ ਸੇਫਟੀ ਲੇਆਊਟ ਪਲਾਨ/ਡਰਾਇੰਗ ਨੂੰ ਘੌਖਿਆ ਗਿਆ ਅਤੇ ਬਿਲਡਿੰਗ ਕੋਡ ਅਨੁਸਾਰ  ਇਮਾਰਤ / ਬਿਲਡਿੰਗ ਨੂੰ ",{ text:`${transformedData.NBCGroup}`, bold:true }, " subdivision ",{ text:`${transformedData.NBCSubGroup}`, bold:true }, " (ਐਨ. ਬੀ. ਸੀ. ਦੇ ਅਨੁਸਾਰ) ਦੀ ਆਬਾਦੀ ਲਈ ਯੋਗ ਪਾਇਆ ਗਿਆ"] ,
                border: [true, false, true, false],
                alignment: "justify",
                preserveLeadingSpaces: true,

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: [{ text:`${transformedData.corporationName}`, bold:true },"  ਵਿਖੇ ਜਾਰੀ ਕਰਨ ਦੀ  ਮਿਤੀ", { text:`${transformedData.issuedDate}`, bold:true },"."],
                border: [true, false, true, false],
                alignment: "left",


              },
           ],



          ]
        },

        layout: {},
      },
      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "",
                border: [true, false, true, false],
                alignment: "left",
              },
           ],



          ]
        },

        layout: {},
      },
...nocDetailsmy,
      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: [{ text:"Provisional NOC ", bold:true  }, " is issued subject to following conditions:"],
                border: [true, false, true, false],
                alignment: "left",
              },
           ],



          ]
        },

        layout: {},
      },
      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "",
                border: [true, false, true, false],
                alignment: "left",
              },
           ],
          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: [{ text:"ਪ੍ਰੌਵੀਜਨਲ ਐਨ.ਓ.ਸੀ", bold:true  }, " ਹੇਠ ਲਿਖਿਆਂ ਸ਼ਰਤਾਂ ਦੇ ਆਧਾਰ ਤੇ ਜਾਰੀ ਕੀਤਾ ਜਾਂਦਾ ਹੈ।"],
                border: [true, false, true, false],
                alignment: "left"
              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                margin: [10, 0, 0, 0],

                text: "1. Occupant/Owner must install/provide fire safety arrangements as per submitted fire layout plan/drawing during construction.",
                border: [true, false, true, false],
                alignment: "left"
              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਕਾਬਜਕਾਰ/ਮਾਲਕ ਵਲੋਂ ਜਮਾਂ ਕਰਵਾਏ ਗਏ ਲੇਆਊਟ ਪਲਾਨ/ਡਰਾਇੰਗ ਅਨੁਸਾਰ ਫਾਇਰ ਸੇਫਟੀ ਦੇ ਪਬੰਧ ਕਰਨੇ ਲਾਜਮੀ ਹੋਣਗੇ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },
      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "2. Occupant/Owner must obtain the final NOC from fire department on completion of building construction before occupancy.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],
          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਕਾਬਜਕਾਰ/ਮਾਲਕ ਵਲੋਂ ਇਸ ਪ੍ਰੋਜੈਕਟ ਦੀ ਉਸਾਰੀ ਉਪਰੰਤ ਫਾਇਰ ਸੇਫਟੀ ਸਬੰਧੀ ਫਾਇਰ ਐਨ.ਓ.ਸੀ ਲੈਣਾ ਹੋਵੇਗਾ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "3. Fire department may ask for additional arrangements (if necessary) aGer the completion of construction of building.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਫਾਇਰ ਵਿਭਾਗ ਵਲੋਂ ਬਿਲਡਿੰਗ ਦੀ ਉਸਾਰੀ ਦਾ ਕੰਮ ਮੁਕੰਮਲ ਹੋਣ ਤੇ ਫਾਇਰ ਸੇਫਟੀ ਦਾ ਵਾਧੂ ਪ੍ਰੋਬੰਧ (ਜੇਕਰ ਲੋੜ ਹੋਵੇ) ਕਰਨ ਲਈ ਕਿਹਾ ਜਾ ਸਕਦਾ ਹੈ।",
                border: [true, false, true, false],
                alignment: "left", 
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "4. During construction, ﬁre safety arrangements should be provided as per requirements of NBC.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਐਨ.ਬੀ.ਸੀ ਅਨੁਸਾਰ ਬਿਲਡਿੰਗ ਦੀ ਉਸਾਰੀ ਸਮੇਂ ਫਾਇਰ ਸੇਫਟੀ ਦੇ ਲੋੜੀਦੇ ਪ੍ਰੋਬੰਧ ਕਰਨੇ ਜਰੂਰੀ ਹੋਣਗੇ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "5. In case of any change/alteration in the building plan, owner/occupant must re-apply for the provisional certiﬁcate.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਜੇਕਰ ਕਾਬਜਕਾਰ/ਮਾਲਕ ਵਲੋਂ ਕੋਈ ਵੀ ਤਬਦੀਲੀ ਕੀਤੀ ਜਾਂਦੀ ਹੈ ਤਾਂ ਦੁਬਾਰਾ ਤੋ ਪ੍ਰੋਵੀਜਨਲ ਐਨ.ਓ.ਸੀ ਲਈ ਅਪਲਾਈ ਕਰਨਾ ਪਵੇਗਾ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "6. Fire department reserves the right to withdraw this issued certificate, if any change in fire/building layout plan is made without notice to fire department.",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "ਜੇਕਰ ਕਾਬਜਕਾਰ/ਮਾਲਕ ਵਲੋਂ  ਿਬਨਾ ਫਾਇਰ ਵਿਭਾਗ ਦੀ ਮੰਨਜੂਰੀ ਤੇ ਲੇਆਊਟ ਪਲਾਨ  ਵਿੱਚ ਕੋਈ ਤਬਦੀਲੀ ਕੀਤੀ ਜਾਂਦੀ  ਹੈ ਤਾਂ ਜਾਰੀ ਕੀਤਾ ਗਿਆ ਸਰਟੀਿਫਕੇਟ ਵਾਪਸ ਲੈਣ ਦਾ ਹੱਕ ਰਾਖਵਾਂ ਹੋਵੇਗਾ।",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },


      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "* Above Details cannot be used as ownership proof.",
                border: [true, false, true, false],
                alignment: "left",
                color: "#FF0000",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: [{ text:"ਉਪਰੋਕਤ ਦਰਸਾਈ ਗਈ ਜਾਣਕਾਰੀ ਨੂੰ ਮਾਲਕਾਨਾ ਦੇ ਸਬੂਤ ਵਜ਼ੋ ਨਹੀਂ ਵਰਤਿਆ ਜਾਵੇਗਾ।", bold:true  }],
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
                text: "This is digitaly created cerificate, no signatue are needed",
                border: [true, false, true, false],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

      {
        style: "noc-head-new",
        table: {
          widths: ["*"],
          body: [

            [
              {
               // text: "ਇਹ ਿਡਜੀਟਲੀ (ਕੰਿਪਊਟਰਾਈਜ਼ਡ) ਿਤਆਰ ਕੀਤਾ ਿਗਆ ਸਰਟੀਿਫਕੇਟ ਹੈ, ਿਜਸ ਿਵੱਚ ਦਸਤਖਤ ਦੀ ਕੋਈ ਲੋੜ ਨਹ ਹੈ।",
                text: [{ text:"ਇਹ ਡਿਜੀਟਲੀ (ਕੰਪਿਿਊਟਰਾਈਜ਼ਡ) ਤਿਆਰ ਕੀਤਾ ਗਿਆ ਸਰਟੀਫਿਕੇਟ ਹੈ,ਜਿਸ ਵਿੱਚ ਦਸਤਖਤ ਦੀ ਕੋਈ ਲੋੜ ਨਹੀਂ ਹੈ।", bold:true  }],
                border: [true, false, true, true],
                alignment: "left",
                margin: [10, 0, 0, 0],

              },
           ],



          ]
        },

        layout: {},
      },

     // ...process.env.REACT_APP_NAME !== "Citizen"? generatedApprovedBy : citizengeneratedApprovedBy




    ],

    footer: [],
    styles: {
      "noc-head": {
        fillColor: "#F2F2F2",
        margin: [-70, -41, -81, 0]
      },
      "noc-head-new": {
        fontSize: 9,
        //fillColor: "#F2F2F2",
        margin: [0, 0, 0, 0],
      },
      "receipt-logo-header": {
        color: "#484848",
        fontFamily: "raavi",
        fontSize: 16,
        bold: true,
        letterSpacing: 0.74,
        margin: [0, 0, 0, 5]
      },
      "receipt-logo-sub-header": {
        color: "#484848",
        fontFamily: "raavi",
        fontSize: 13,
        letterSpacing: 0.6
      },
      "noc-subhead": {
        fontSize: 12,
        bold: true,
        margin: [-18, 8, 0, 0],
        color: "#484848"
      },
      "noc-title": {
        fontSize: 10,
        bold: true,
        margin: [0, 0, 0, 0],
        color: "#484848",
        fontWeight: 500
      },
      "noc-title-my":{
        fontSize: 10,
        bold: true,
        width: 150,
        marginleft : 40,
        alignment: "center",
        color: "#484848",
        fontWeight: 500
      },
      "noc-table-my":{
        fontSize: 10,
        width: 60,
        margin:[100, 0, 0, 0]
      },
      "noc-table": {
        fontSize: 10,
        color: "#484848",
        margin:[100, 0, 0, 0]
      },
      
      "receipt-header-details": {
        fontSize: 9,
        margin: [0, 0, 0, 8],
        color: "#484848"
      },
      "noc-table-key": {
        color: "#484848",
        bold: false,
        fontSize: 10
      },
      "receipt-table-value": {
        color: "#484848",
        bold: true,
        fontSize: 10
      },
      "receipt-table-firestation": {
        color: "#484848",
        bold: true,
        fontSize: 10
      },
      "receipt-footer": {
        color: "#484848",
        fontSize: 8,
        margin: [-6, 15, -15, -10]
      },
      "receipt-no": {
        color: "#484848",
        fontSize: 10
      },
      "receipt-approver": {
        fontSize: 12,
        bold: true,
        margin: [-20, 30, -10, 0],
        color: "#484848"
      }
    }
  };


  return dd;
};

const generatePdf = async (state, dispatch, type) => {

      pdfMake.vfs = pdfFonts.vfs;

      pdfMake.fonts = {
        raavi: {
          normal: "raavi.ttf",
          bold: "raavib.ttf",
          italics: "raavi.ttf",
          bolditalics: "raavi.ttf"
        }
      };



  let applicationData = get(
    state.screenConfiguration.preparedFinalObject,
    "applicationDataForPdf",
    {}
  );
  

  let paymentData = get(
    state.screenConfiguration.preparedFinalObject,
    "receiptDataForPdf",
    {}
  );


  let mdmsData = get(
    state.screenConfiguration.preparedFinalObject,
    "mdmsDataForPdf",
    {}
  );

  let ulbLogo = get(
    state.screenConfiguration.preparedFinalObject,
    "base64UlbLogoForPdf",
    ""
  );
  let auditorData = get(
    state.screenConfiguration.preparedFinalObject,
    "userDataForPdf",
    {}
  );

 
/*    if (isEmpty(applicationData)) {
    console.log("Error in application data");
    return;
  } else if (isEmpty(mdmsData)) {
    console.log("Error in mdms data");
    return;
  } else if (isEmpty(ulbLogo)) {
    console.log("Error in image data");
    return;
  }  else if (
    (type.startsWith("receipt") || type.startsWith("certificate")) &&
    isEmpty(auditorData)
  ) {
    console.log("Error in auditor user data");
    return;
  }  else if (
    (type.startsWith("receipt") || type.startsWith("certificate")) &&
    isEmpty(paymentData)
  ) {
    console.log("Error in payment data");
    return;
  }  */
  let transformedData = {
    ...applicationData,
    ...paymentData,
    ...mdmsData,
    ...auditorData
  };

  switch (type) {
    case "application_download":
      let fileName = `noc_application_${transformedData.applicationNumber}`;
      let application_data = await getApplicationData(
        transformedData,
        ulbLogo,
        "application"
      );
      // console.log("application_data", application_data);
      // console.log("application************", transformedData)

      application_data &&
      pdfMake.createPdf(application_data).download(fileName);
      break;
    case "application_print":
      application_data = await getApplicationData(
        transformedData,
        ulbLogo,
        "application"
      );
      application_data && pdfMake.createPdf(application_data).print();
      break;
    case "receipt_download":
     
      fileName = `noc_receipt_${transformedData.receiptNumber}`;
      application_data = await getApplicationData(
        transformedData,
        ulbLogo,
        "receipt"
      );
      application_data &&
      pdfMake.createPdf(application_data).download(fileName);
      break;
    case "receipt_print":
      application_data = await getApplicationData(
        transformedData,
        ulbLogo,
        "receipt"
      );
      application_data && pdfMake.createPdf(application_data).print();
      break;
    case "certificate_download":
      fileName = `noc_certificate_${transformedData.fireNOCNumber}`;
      if(transformedData.nocType == "NEW")
      {
      application_data = await newgetApplicationData(
        transformedData,
        ulbLogo,
        "certificate"
      );
     }
     else if(transformedData.nocType == "PROVISIONAL"){
      
      application_data = await provisionApplicationData(
        transformedData,
        ulbLogo,
        "certificate"
      );
     }
         else
     {
      application_data = await renewgetApplicationData(
        transformedData,
        ulbLogo,
        "certificate"
      );
     }

      application_data && pdfMake.createPdf(application_data).download(fileName);
      break;
    case "certificate_print":
    fileName = `noc_certificate_${transformedData.fireNOCNumber}`;
    if(transformedData.nocType == "NEW")
    {
    application_data = await newgetApplicationData(
      transformedData,
      ulbLogo,
      "certificate"
    );
   }
   else if(transformedData.nocType == "PROVISIONAL"){
    
    application_data = await provisionApplicationData(
      transformedData,
      ulbLogo,
      "certificate"
    );
   }
else
   {
    application_data = await renewgetApplicationData(
      transformedData,
      ulbLogo,
      "certificate"
    );
   }

      application_data && pdfMake.createPdf(application_data).print();
      break;

    default:
      break;
  }
};

export default generatePdf;
