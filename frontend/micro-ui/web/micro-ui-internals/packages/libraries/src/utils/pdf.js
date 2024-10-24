import { Fonts } from "./fonts";
const pdfMake = require("pdfmake/build/pdfmake.js");
// const pdfFonts = require("pdfmake/build/vfs_fonts.js");
// pdfMake.vfs = pdfFonts.pdfMake.vfs;

let pdfFonts = {
  //   Roboto: {
  //     normal: "https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-Regular.ttf",
  //     bold: "https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.66/fonts/Roboto/Roboto-Medium.ttf"
  //   },
  Hind: {
    normal: "Hind-Regular.ttf",
    bold: "Hind-Bold.ttf",
  },
  en_IN: {
    normal: "Hind-Regular.ttf",
    bold: "Hind-Bold.ttf",
  },
  pn_IN: {
    normal: "BalooPaaji2-Regular.ttf",
    bold: "BalooPaaji2-Bold.ttf",
  },
  od_IN: {
    normal: "BalooBhaina2-Regular.ttf",
    bold: "BalooBhaina2-Bold.ttf",
  },
  hi_IN: {
    normal: "Hind-Regular.ttf",
    bold: "Hind-Bold.ttf",
  },
};
pdfMake.vfs = Fonts;

pdfMake.fonts = pdfFonts;

const downloadPDFFileUsingBase64 = (receiptPDF, filename) => {
  if (
    window &&
    window.mSewaApp &&
    window.mSewaApp.isMsewaApp &&
    window.mSewaApp.isMsewaApp() &&
    window.mSewaApp.downloadBase64File &&
    window.Digit.Utils.browser.isMobile()
  ) {
    // we are running under webview
    receiptPDF.getBase64((data) => {
      window.mSewaApp.downloadBase64File(data, filename);
    });
  } else {
    // we are running in browser
    receiptPDF.download(filename);
  }
};

function getBase64Image(tenantId) {
  try {
    const img = document.getElementById(`logo-${tenantId}`);
    var canvas = document.createElement("canvas");
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0);
    return canvas.toDataURL("image/png");
  } catch (e) {
    return "";
  }
}

const defaultLogo =
  "data:image/jpeg;base64,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"
const jsPdfGenerator = async ({ breakPageLimit = null, tenantId, logo, name, email, phoneNumber, heading, details, t = (text) => text }) => {

  const emailLeftMargin =
    email.length <= 15
      ? 190
      : email.length <= 20
      ? 150
      : email.length <= 25
      ? 130
      : email.length <= 30
      ? 90
      : email.length <= 35
      ? 50
      : email.length <= 40
      ? 10
      : email.length <= 45
      ? 0
      : email.length <= 50
      ? -20
      : email.length <= 55
      ? -70
      : email.length <= 60
      ? -100
      : -60;

  const dd = {
    pageMargins: [40, 80, 40, 30],
    header: {
      columns: [
        {
          image: logo || getBase64Image(tenantId) || defaultLogo,
          width: 50,
          margin: [10, 10],
        },
        {
          text: name,
          margin: [20, 25],
          font: "Hind",
          fontSize: 14,
          // bold: true,
        },
        {
          text: email,
          margin: [emailLeftMargin, 25, 0, 25],
          font: "Hind",
          fontSize: 11,
          color: "#464747",
        },
        {
          text: phoneNumber,
          color: "#6f777c",
          font: "Hind",
          fontSize: 11,
          margin: [-65, 45, 0, 25],
        },
      ],
    },

    footer: function (currentPage, pageCount) {
      return {
        columns: [
          { text: `${name} / ${heading}`, margin: [15, 0, 0, 0], fontSize: 11, color: "#6f777c", width: 400, font: "Hind" },
          { text: `Page ${currentPage}`, alignment: "right", margin: [0, 0, 25, 0], fontSize: 11, color: "#6f777c", font: "Hind" },
        ],
      };
    },
    content: [
      {
        text: heading,
        font: "Hind",
        fontSize: 24,
        // bold: true,
        margin: [-25, 5, 0, 0],
      },
      ...createContent(details, phoneNumber, breakPageLimit),
      {
        text: t("PDF_SYSTEM_GENERATED_ACKNOWLEDGEMENT"),
        font: "Hind",
        fontSize: 11,
        color: "#6f777c",
        margin: [-25, 32],
      },
    ],
    defaultStyle: {
      font: "Hind",
    },
  };
  pdfMake.vfs = Fonts;
  let locale = Digit.SessionStorage.get("locale") || "en_IN";
  let Hind = pdfFonts[locale] || pdfFonts["Hind"];
  pdfMake.fonts = { Hind: { ...Hind } };
  const generatedPDF = pdfMake.createPdf(dd);
  downloadPDFFileUsingBase64(generatedPDF, "acknowledgement.pdf");
};


/**
 * Util function that can be used
 * to download WS connection acknowledgement pdfs
 * Data is passed to this function from this file
 * packages\modules\ws\src\utils\getWSAcknowledgementData.js
 * @author nipunarora-egov
 *
 * @example
 * Digit.Utils.pdf.generatev1()
 *
 * @returns Downloads a pdf  
 */
const jsPdfGeneratorv1 = async ({ breakPageLimit = null, tenantId, logo, name, email, phoneNumber, heading, details, headerDetails, t = (text) => text }) => {
  const emailLeftMargin =
    email.length <= 15
      ? 190
      : email.length <= 20
        ? 150
        : email.length <= 25
          ? 130
          : email.length <= 30
            ? 90
            : email.length <= 35
              ? 50
              : email.length <= 40
                ? 10
                : email.length <= 45
                  ? 0
                  : email.length <= 50
                    ? -20
                    : email.length <= 55
                      ? -70
                      : email.length <= 60
                        ? -100
                        : -60;

  const dd = {
    pageMargins: [40, 40, 40, 30],
    header: {},
    footer: function (currentPage, pageCount) {
      return {
        columns: [
          { text: `${name} / ${heading}`, margin: [15, 0, 0, 0], fontSize: 11, color: "#6f777c", width: 400, font: "Hind" },
          { text: `Page ${currentPage}`, alignment: "right", margin: [0, 0, 25, 0], fontSize: 11, color: "#6f777c", font: "Hind" },
        ],
      };
    },
    content: [
      ...createHeader(headerDetails,logo,tenantId),
      // {
      //   text: heading,
      //   font: "Hind",
      //   fontSize: 24,
      //   bold: true,
      //   margin: [-25, 5, 0, 0],
      // },
      ...createContentDetails(details),
      {
        text: t("PDF_SYSTEM_GENERATED_ACKNOWLEDGEMENT"),
        font: "Hind",
        fontSize: 11,
        color: "#6f777c",
        margin: [-25, 32],
      },
    ],
    defaultStyle: {
      font: "Hind",
    },
  };
  
  pdfMake.vfs = Fonts;
  let locale = Digit.SessionStorage.get("locale") || "en_IN";
  let Hind = pdfFonts[locale] || pdfFonts["Hind"];
  pdfMake.fonts = { Hind: { ...Hind } };
  const generatedPDF = pdfMake.createPdf(dd);
  downloadPDFFileUsingBase64(generatedPDF, "acknowledgement.pdf");
};

/**
 * Util function that can be used
 * to download WS modify connection application acknowledgement pdfs
 * Data is passed to this function from this file
 * packages\modules\ws\src\utils\getWsAckDataForModifyPdfs.js
 * @author nipunarora-egov
 *
 * @example
 * Digit.Utils.pdf.generateModifyPdf()
 *
 * @returns Downloads a pdf  
 */

const jsPdfGeneratorForModifyPDF = async({tenantId,bodyDetails,headerDetails,logo}) =>{
  //here follow an approch to render specific table for every object in bodyDetails
  //keep the header logic same for now
  //we are expecting the bodyDetails to be array of objects where each obj will be a table 
  //format of each obj {title:[array of str],values:[array of obj]}
  
  const dd = {
    pageMargins: [40, 40, 40, 30],
    header: {},
    defaultStyle: {
      font: "Hind",
    },
    content:[
      ...createHeader(headerDetails, logo, tenantId),
      ...createBodyContent(bodyDetails)
    ]
  
  }

  pdfMake.vfs = Fonts;
  let locale = Digit.SessionStorage.get("locale") || "en_IN";
  let Hind = pdfFonts[locale] || pdfFonts["Hind"];
  pdfMake.fonts = { Hind: { ...Hind } };
  const generatedPDF = pdfMake.createPdf(dd);
  downloadPDFFileUsingBase64(generatedPDF, "acknowledgement.pdf");
}

/**
 * Util function that can be used
 * to download bill amendment application acknowledgement pdfs
 * Data is passed to this function from this file
 * packages\modules\ws\src\utils\getWsAckDataForBillAmendPdf.js
 * @author nipunarora-egov
 *
 * @example
 * Digit.Utils.pdf.generateBillAmendPDF()
 *
 * @returns Downloads a pdf  
 */


const generateBillAmendPDF = async ({ tenantId, bodyDetails, headerDetails, logo,t }) => {
  const dd = {
    pageMargins: [40, 40, 40, 30],
    header: {},
    defaultStyle: {
      font: "Hind",
    },
    content: [
      ...createHeaderBillAmend(headerDetails, logo, tenantId,t),
      ...createBodyContentBillAmend(bodyDetails,t)
    ]

  }
  

  pdfMake.vfs = Fonts;
  let locale = Digit.SessionStorage.get("locale") || "en_IN";
  let Hind = pdfFonts[locale] || pdfFonts["Hind"];
  pdfMake.fonts = { Hind: { ...Hind } };
  const generatedPDF = pdfMake.createPdf(dd);
  downloadPDFFileUsingBase64(generatedPDF, "acknowledgement.pdf");
}

export default { generate: jsPdfGenerator, generatev1: jsPdfGeneratorv1, generateModifyPdf: jsPdfGeneratorForModifyPDF, generateBillAmendPDF };

const createBodyContentBillAmend = (table,t) => {
  let bodyData = []
  bodyData.push({
    text: t(table?.title),
    color: "#000000",
    style: "header",
    fontSize: 14,
    bold: true,
    margin: [0, 15, 0, 10]
  })
  bodyData.push({
    layout:{
      color:function(rowIndex,node,columnIndex){
        if(rowIndex === (table?.tableRows?.length)) {
          return "#FFFFFF"
        }
      },
      fillColor:function(rowIndex,node,columnIndex){
        if(rowIndex === (table?.tableRows?.length)) {
          return "#0f4f9e"
        }
        return (rowIndex % 2 === 0) ? "#0f4f9e" : null; 
      },
      fillOpacity:function(rowIndex,node,columnIndex) {
        if (rowIndex === (table?.tableRows?.length)) {
          return 1;
        }
        return (rowIndex % 2 === 0) ? 0.15 : 1;
      }
    },
    table:{
      headerRows:1,
      widths: ["*", "*", "*", "*"],
      body:[
        table?.headers?.map(header =>{
          return {
            text:t(header),
            style:"header",
            fontSize:11,
            bold:true,
            border: [false, false, false, false]
          }
        }),
        ...table?.tableRows?.map(row => {
          return [
            {
              text:t(row?.[0]),
              style:"header",
              fontSize:11,
              border: [false, false, false, false]
            },
            {
              text: t(row?.[1]),
              style: "header",
              fontSize: 11,
              border: [false, false, false, false]
            },
            {
              text: t(row?.[2]),
              style: "header",
              fontSize: 11,
              border: [false, false, false, false]
            },
            {
              text: t(row?.[3]),
              style: "header",
              fontSize: 11,
              border: [false, false, false, false]
            }
          ]
        })
      ]
    }
  })
  return bodyData
}

const createHeaderBillAmend = (headerDetails, logo, tenantId,t) => {
  
  let headerData = [];
  headerData.push({
    style: 'tableExample',
    layout: "noBorders",
    fillColor: "#f7e0d4",
    margin: [-40, -40, -40, 40],
    table: {
      widths: ['5%', 'auto', '*'],
      body: [
        [
            {
            image: logo || getBase64Image(tenantId) || defaultLogo,
            // width: 50,
            margin: [10, 10],
            fit: [50, 50],
            //width: 50,
            //margin: [10, 10]
          },
          {
            text: headerDetails?.header, //"Amritsar Municipal Corporation",
            margin: [40, 10, 2, 4],
            style: "header",
            // italics: true, 
            fontSize: 18,
            bold: true
          },
          {
            text: headerDetails?.typeOfApplication, //"New Sewerage Connection",
            bold: true,
            fontSize: 16,
            alignment: "right",
            margin: [-40, 10, 2, 0],
            color: "#0f4f9e"
          }
        ],
        [
          { text: "" },
          {
            text: headerDetails?.subHeader, //"Municipal Corporation Amritsar, Town Hall, Amritsar, Punjab.",
            margin: [40, -45, -2, -5],
            style: "header",
            // italics: true, 
            fontSize: 10,
            bold: false
          },

          {
            text: headerDetails?.date, //"28/03/2022",
            bold: true,
            fontSize: 16,
            margin: [0, -45, 10, 0],
            alignment: "right",
            color: "#0f4f9e"
          }
        ],
        [
          { text: "" },

          {
            text: headerDetails?.description, //"0183-2545155 | www.amritsarcorp.com | cmcasr@gmail.com",
            margin: [40, -40, 2, 10],
            style: "header",
            // italics: true, 
            fontSize: 10,
            bold: false
          },
          {
            text: "",
          }
        ]
      ]
    }
  });
  headerDetails?.values?.forEach((header, index) => {
    headerData.push({
      style: 'tableExample',
      layout: "noBorders",
      fillColor: "#f7e0d4",
      margin: [-40, -40, -40, 20],
      table: {
        widths: ['30%', '*'],
        body: [
          [
            {
              text: header?.title,
              margin: index == 0 ? [40, 0, 2, 10] : [40, 10, 2, 10],
              style: "header",
              fontSize: 10,
              bold: true
            },
            {
              text: header?.value,
              bold: false,
              fontSize: 10,
              alignment: "left",
              margin: index == 0 ? [0, 0, 2, 10] : [0, 10, 2, 10],
            }
          ]
        ]
      }
    })
  })
  //push demand revision details old way

  headerData.push({
    style: 'tableExample',
    layout: "noBorders",
    fillColor: "#f7e0d4",
    margin: [-40, -25, -1000000, 20],
    table: {
      widths: ['30%', '*'],
      body: [
        [
          {
            text: headerDetails?.DemandRevision?.title,
            margin: [40, 0, 2, 20],
            style: "header",
            fontSize: 13,
            bold: true
          }
        ]
      ]
    }
  })

  headerDetails?.DemandRevision?.values?.forEach((header, index) => {
    headerData.push({
      style: 'tableExample',
      layout: "noBorders",
      fillColor: "#f7e0d4",
      margin: [-40, -40, -40, 20],
      table: {
        widths: ['30%', '*'],
        body: [
          [
            {
              text: header?.title,
              margin: index == 0 ? [40, 0, 2, 10] : [40, 10, 2, 10],
              style: "header",
              fontSize: 10,
              bold: true
            },
            {
              text: header?.value,
              bold: false,
              fontSize: 10,
              alignment: "left",
              margin: index == 0 ? [0, 0, 2, 10] : [0, 10, 2, 10],
            }
          ]
        ]
      }
    })
  })

 //attachment details
  headerData.push({
    style: 'tableExample',
    layout: "noBorders",
    fillColor: "#f7e0d4",
    margin: [-40, -25, -1000000, 20],
    table: {
      widths: ['30%', '*'],
      body: [
        [
          {
            text: headerDetails?.Attachments?.title,
            margin: [40, 0, 2, 110],
            style: "header",
            fontSize: 13,
            bold: true
          }
        ]
      ]
    }
  })
  
  headerData.push({
    layout: "noBorders",
    ul: headerDetails?.Attachments?.values,
    margin:[0,-130,0,40]
   })

  return headerData;
}

const createBodyContent = (details) => {
  let detailsHeaders = []
  details.map((table,index) =>{
    if (table?.isAttachments && table.values) {
      detailsHeaders.push({
        style: 'tableExample',
        layout: "noBorders",
        margin: [0, 13, 0, 5],
        table: {
          body: [
            [
              {
                text: table?.title,
                color: "#000000",
                style: "header",
                fontSize: 14,
                bold: true
              }
            ]
          ]
        }
      })
      detailsHeaders.push({
        layout:'noBorders',
        ul: table?.values
      })
      return
    }
    detailsHeaders.push({
      layout:'noBorders',
      table:{
        headerRows:1,
        widths:["*","*","*"],
        body:[
          table?.title?.map(t=>{ 
            return {
            text:t,
            color: "#0f4f9e",
            style: "header",
            fontSize: 14,
            bold: true,
            margin:[0,15,0,0]
            }
          }),
          ...table?.values?.map((value,index) => {
            return [
              {
                text:value?.val1,
                style: "header",
                fontSize: 10,
                bold: true
              },
              {
                text: value?.val2,
                fontSize: 10
              },
              {
                text: value?.val3,
                fontSize: 10
              }
            ]
          })
        ]
      }
    })
  })

  return detailsHeaders
}

function createContentDetails(details) {
  let detailsHeaders = [];
  details.forEach((detail, index) => {
    if (detail?.title) {
      detailsHeaders.push({
        style: 'tableExample',
        layout: "noBorders",
        margin:[0,13,0,5],
        table: {
          body: [
            [
              {
                text: detail?.title,
                color: "#0f4f9e",
                style: "header",
                fontSize: 14,
                bold: true
              }
            ]
          ]
        }
      })
    }
    if (detail?.isAttachments && detail.values) {
      detailsHeaders.push({
        ul: detail?.values
      })
    } else {
      detail?.values?.forEach(indData => {
        detailsHeaders.push({
          style: 'tableExample',
          layout: "noBorders",
          table: {
            widths: ['40%', '*'],
            body: [
              [
                {
                  text: indData?.title,
                  style: "header",
                  fontSize: 10,
                  bold: true
                },

                {
                  text: indData?.value,
                  fontSize: 10
                }
              ]
            ]
          }
        })
      })
    }
  });
  return detailsHeaders;
}

function createHeader(headerDetails,logo,tenantId) {
  let headerData = [];
  headerData.push({
    style: 'tableExample',
    layout: "noBorders",
    fillColor: "#f7e0d4",
    "margin": [-40, -40, -40, 40],
    table: {
      widths: ['5%', 'auto', '*'],
      body: [
        [
          // {
          //   margin: [40, 10, 2, 2],
          //   "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAABBtJREFUWAntV11oVEcUnjN3mzXm111toQ8lWOtLTbIG0YgpMdQ05CEv0iTU1hhR7EMRWYuKSkteVExJBRGkpaUk0ChRSYtpKam4sbE0ia7RSNaS0uIfWtT4E8Xs5u7O8Vy7c3f27t2fh4W+ZF7OmfN9c86Ze2bOvZex/3mAGv/RIU9xJCL2IWIdAS7C/Nyhtbm8l39XeVKfOrLcHZoOHmCA7zJkC6VdSmAQRoYBAHbCXZBzED726xKT0kwgGnyUgpdI0JBEEMD4B+4dV3pU+9Mvyl4NMTZKAV5X7cl0APC5P127BqBNqBwuJ5Gw2G8NbmDIGEcQR+9/u6pAcg0ZYtiRaXCDT75rHnT0bjF0dZgJkLP3VEDVEakcj58ti7MBJOWrPFUHJurUuaGbCVCd5llBdQ4Yw7GnUaM9Fal4JjptJCGGmQA964upnPBXHCYOTSciDMGcp1qnYpzBBXVu6LEEGHxOByViJURJX7m2+W+qmKax3cn4Kk/qdJgnnXOdHXIupZnA/B1jw5TP+wzgngSpLEhX6ahLy/dKm5Su7WODBK4l/I60JZPkJ0DcuvxPLvxr5ZjXUAL45crchxD00A12OR3apTyv/67E7CQerndOztwto9uymPI1N2RwOcMwgBYorigah5qBsN36WVtCCZI9kqqu8Td0DG2mhlJKdb8JGvQOrV86YMevPDZagjpuQoFLqPY3gDtOjawvH7TjZpRAZeelesHwON3jQtUJtej2kdalu1RbZZe/QSB0U6L5ph0AObB9wy0Vn5m2qJI2geWd19yI09eo8SywLjbmdMgaRjZ4+gx9RffV13BGD1BXNV5kCYMzrW641dOvAnGnVgVMHYLUPu2DGxxk4iPJFeFwfbLgL7lcfCi5UqZNgK7WIkm2k4AxHARLyaUSJuBpE6AtBuwCmzaAGM5Tc6neMW7UQdoEcnOdv9Cpv24GjFNAAPCvpalwTuFP1J5vy7kqqRtGOGjfqDZDT5vAQNPbzzTgzQmOAWZotXe4xXNeOj3T9OYTjUMzHU1Le4YQImwdaimndh8/0t4CSV/T83fR1PRUI9W8lALc4jla3x/ryv6UuCqrvh+bp+t6IwL81weQn6abMqFyZnX5BDIugVyQifT52hxD7HyVAFFKb8nreVg46K354bHd2qwn0H6u9i0dI9S2scIMSN8YHHDjnmrfz6YtqmQ1gZ7xxpyJ+5MX6ROYDqplADzPAc2zs/rXv1Qk7TVUyen0iclHDbbBjYWIc3UR3mb1kdUEQGC5NYA6p1dzAp7VBKjulgakhjf+sqwNKoNOGO8i9Uxz8H6KEkzKAvzRimX1Cex+58w/9O2/nT4S4v7/jKDUyo/vrfZ1WxPI6i2Qzvf/VrtKRMJbKewSeiI3aJcn96w++53EVfkCw79XQZYr/EsAAAAASUVORK5CYII="
          // },
          {
            image: logo || getBase64Image(tenantId) || defaultLogo,
            // width: 50,
            margin: [10, 10],
            fit: [50,50],
            //width: 50,
            //margin: [10, 10]
          },
          {
            text: headerDetails?.[0]?.header, //"Amritsar Municipal Corporation",
            margin: [40, 10, 2, 4],
            style: "header",
            // italics: true, 
            fontSize: 18,
            bold: true
          },
          {
            text: headerDetails?.[0]?.typeOfApplication, //"New Sewerage Connection",
            bold: true,
            fontSize: 16,
            alignment: "right",
            margin: [-40, 10, 2, 0],
            color: "#0f4f9e"
          }
        ],
        [
          { text: "" },
          {
            text: headerDetails?.[0]?.subHeader, //"Municipal Corporation Amritsar, Town Hall, Amritsar, Punjab.",
            margin: [40, -45, -2, -5],
            style: "header",
            // italics: true, 
            fontSize: 10,
            // bold: true
          },

          {
            text: headerDetails?.[0]?.date, //"28/03/2022",
            bold: true,
            fontSize: 16,
            margin: [0, -50, 10, 0],
            alignment: "right",
            color: "#0f4f9e"
          }
        ],
        [
          { text: "" },

          {
            text: headerDetails?.[0]?.description, //"0183-2545155 | www.amritsarcorp.com | cmcasr@gmail.com",
            margin: [40, -40, 2, 10],
            style: "header",
            // italics: true, 
            fontSize: 10,
            // bold: true
          },
          {
            text: "",
          }
        ]
      ]
    }
  });
  headerDetails?.[0]?.values?.forEach((header, index) => {
    headerData.push({
      style: 'tableExample',
      layout: "noBorders",
      fillColor: "#f7e0d4",
      "margin": [-40, -40, -40, 20],
      table: {
        widths: ['30%', '*'],
        body: [
          [
            {
              text: header?.title,
              margin: index == 0 ? [40, 0, 2, 10] : [40, 10, 2, 10],
              style: "header",
              fontSize: 10,
              bold: true
            },
            {
              text: header?.value,
              // bold: true,
              fontSize: 10,
              alignment: "left",
              margin: index == 0 ? [0, 0, 2, 10] : [0, 10, 2, 10],
            }
          ]
        ]
      }
    })
  })

  return headerData;
}


function createContent(details, phoneNumber, breakPageLimit = null) {
  const data = [];

  details.forEach((detail, index) => {
    if (detail?.values?.length > 0) {
      let column1 = [];
      let column2 = [];

      if ( breakPageLimit ?  (index + 1) % breakPageLimit === 0 : (index + 1) % 7 === 0) {
        data.push({
          text: "",
          margin: [-25, 0, 0, 200],
        });
      }

      data.push({
        text: `${detail.title}`,
        font: "Hind",
        fontSize: 18,
        // bold: true,
        margin: [-25, 20, 0, 20],
      });

      const newArray = [];
      let count = 0;
      let arrayNumber = 0;

      detail.values.forEach((value, index) => {
        if (count <= 3) {
          if (!newArray[arrayNumber]) {
            newArray[arrayNumber] = [];
          }
          if (value) {
            newArray[arrayNumber].push(value);
          }
          count++;
        }
        if (count === 4) {
          count = 0;
          arrayNumber++;
        }
      });

      newArray.forEach((value) => {
        if (value?.length === 2) {
          createContentForDetailsWithLengthOfTwo(value, data, column1, column2, detail.values.length > 3 ? 10 : 0);
        } else if (value?.length === 1 || value?.length === 3) {
          createContentForDetailsWithLengthOfOneAndThree(value, data, column1, column2, detail.values.length > 3 ? 10 : 0);
        } else {
          value.forEach((value, index) => {
            let margin = [-25, 0, 0, 5];
            if (index === 1) margin = [15, 0, 0, 5];
            if (index === 2) margin = [26, 0, 0, 5];
            if (index === 3) margin = [30, 0, 0, 5];
            column1.push({
              text: value.title,
              font: "Hind",
              fontSize: 11,
              // bold: true,
              margin,
            });
            if (index === 1) margin = [15, 0, 0, 10];
            if (index === 2) margin = [26, 0, 0, 10];
            if (index === 3) margin = [30, 0, 0, 10];
            column2.push({
              text: value.value,
              font: "Hind",
              fontSize: 9,
              margin,
              color: "#1a1a1a",
              width: "25%",
            });
          });
          data.push({ columns: column1 });
          data.push({ columns: column2 });
          column1 = [];
          column2 = [];
        }
      });
    }
  });

  return data;
}

function createContentForDetailsWithLengthOfTwo(values, data, column1, column2, num = 0) {
  values.forEach((value, index) => {
    if (index === 0) {
      column1.push({
        text: value.title,
        font: "Hind",
        fontSize: 12,
        // bold: true,
        margin: [-25, num - 10, -25, 0],
      });
      column2.push({
        text: value.value,
        font: "Hind",
        fontSize: 9,
        margin: [-25, 5, 0, 0],
        color: "#1a1a1a",
        width: "25%",
      });
    } else {
      column1.push({
        text: value.title,
        font: "Hind",
        fontSize: 12,
        // bold: true,
        margin: [-115, num - 10, -115, 0],
      });
      column2.push({
        text: value.value,
        font: "Hind",
        fontSize: 9,
        margin: [15, 5, 0, 0],
        color: "#1a1a1a",
        width: "25%",
      });
    }
  });
  data.push({ columns: column1 });
  data.push({ columns: column2 });
}

function createContentForDetailsWithLengthOfOneAndThree(values, data, column1, column2, num = 0) {
  values.forEach((value, index) => {
    if (index === 0) {
      column1.push({
        text: value.title,
        font: "Hind",
        fontSize: 12,
        // bold: true,
        margin: values.length > 1 ? [-25, -5, 0, 0] : [-25, 0, 0, 0],
      });
      column2.push({
        text: value.value,
        font: "Hind",
        fontSize: 9,
        color: "#1a1a1a",
        margin: values.length > 1 ? [-25, 5, 0, 0] : [-25, 5, 0, 0],
        width: "25%",
      });
    } else if (index === 2) {
      column1.push({
        text: value.title,
        font: "Hind",
        fontSize: 12,
        // bold: true,
        margin: [-60, -5, 0, 0],
      });
      column2.push({
        text: value.value,
        font: "Hind",
        fontSize: 9,
        margin: [26, 5, 0, 0],
        color: "#1a1a1a",
        width: "25%",
      });
    } else {
      column1.push({
        text: value.title,
        font: "Hind",
        fontSize: 12,
        // bold: true,
        margin: [-28, -5, 0, 0],
      });
      column2.push({
        text: value.value,
        font: "Hind",
        fontSize: 9,
        margin: [15, 5, 0, 0],
        color: "#1a1a1a",
        width: "25%",
      });
    }
  });
  data.push({ columns: column1 });
  data.push({ columns: column2 });
}

// EXAMPLE
// <button
//   onClick={() =>
//     Digit.Utils.pdf.generate({
//       logo:
//         "data:image/jpeg;base64,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",
//       name: "Berhampur Municipal Council",
//       email: "care@berhampur.gov.in",
//       phoneNumber: "080-454234",
//       heading: "Desludging request - Acknowledgement",
//       details: [
//         {
//           title: "Application Details",
//           values: [
//             { title: "Application No.", value: "FSM-277373" },
//             { title: "Application Date", value: "12/08/2020" },
//             { title: "Application Channel", value: "Counter" },
//           ],
//         },
//         {
//           title: "Applicant Details",
//           values: [
//             { title: "Applicant Name", value: "Satinder Pal Singh" },
//             { title: "Mobile No.", value: "2272773737" },
//           ],
//         },
//         {
//           title: "Property Details",
//           values: [
//             { title: "Property Type", value: "Commercial" },
//             { title: "Property Sub Type", value: "Mail" },
//           ],
//         },
//         {
//           title: "Property Location Details",
//           values: [
//             { title: "Pincode", value: "234678" },
//             { title: "City", value: "Berhampur" },
//             { title: "Mohall", value: "Alakapuri" },
//             { title: "Street", value: "Alakapuri Street" },
//             { title: "Building No.", value: "707/B" },
//             { title: "Landmark", value: "Behind SBI bank" },
//           ],
//         },
//         {
//           title: "Pit/Septic Tank Details",
//           values: [
//             { title: "Dimension", value: "2m x 2m x 3m" },
//             { title: "Distance from Road", value: "500m" },
//             { title: "No. of Trips", value: "1" },
//             { title: "Amount per Trip", value: "₹ 1000.00" },
//             { title: "Total Amount Due", value: "₹ 1000.00" },
//           ],
//         },
//       ],
//     })
//   }
// >
//   Download PDF
// </button>,

const downloadPdf = (blob, fileName) => {
  if (window.mSewaApp && window.mSewaApp.isMsewaApp() && window.mSewaApp.downloadBase64File) {
    var reader = new FileReader();
    reader.readAsDataURL(blob);
    reader.onloadend = function () {
      var base64data = reader.result;
      window.mSewaApp.downloadBase64File(base64data, fileName);
    };
  } else {
    const link = document.createElement("a");
    // create a blobURI pointing to our Blob
    link.href = URL.createObjectURL(blob);
    link.download = fileName;
    // some browser needs the anchor to be in the doc
    document.body.append(link);
    link.click();
    link.remove();
    // in case the Blob uses a lot of memory
    setTimeout(() => URL.revokeObjectURL(link.href), 7000);
  }
};

/* Download Receipts */

export const downloadReceipt = async (
  consumerCode,
  businessService,
  pdfKey = "consolidatedreceipt",
  tenantId = Digit.ULBService.getCurrentTenantId(),
  receiptNumber = null
) => {
  console.log("pdf.js")
  const response = await Digit.ReceiptsService.receipt_download(businessService, consumerCode, tenantId, pdfKey, receiptNumber);
  const responseStatus = parseInt(response.status, 10);
  if (responseStatus === 201 || responseStatus === 200) {
    let filename = receiptNumber ? `receiptNumber-${receiptNumber}.pdf` : `consumer-${consumerCode}.pdf`;
    downloadPdf(new Blob([response.data], { type: "application/pdf" }), filename);
  }
};
/* Download Bills */

export const downloadBill = async (
  consumerCode,
  businessService,
  pdfKey = "consolidatedbill",
  tenantId = Digit.ULBService.getCurrentTenantId(),
) => {
  const response = await Digit.ReceiptsService.bill_download(businessService, consumerCode, tenantId, pdfKey);
  const responseStatus = parseInt(response.status, 10);
  if (responseStatus === 201 || responseStatus === 200) {
    let filename = consumerCode ? `consumerCode-${consumerCode}.pdf` : `consumer-${consumerCode}.pdf`;
    downloadPdf(new Blob([response.data], { type: "application/pdf" }), filename);
  }
};

export const getFileUrl = (linkText = "") => {
  const linkList = (linkText && typeof linkText == "string" && linkText.split(",")) || [];
  let fileURL = "";
  linkList &&
    linkList.map((link) => {
      if (!link.includes("large") && !link.includes("medium") && !link.includes("small")) {
        fileURL = link;
      }
    });
  return fileURL;
};

/* Use this util function to download the file from any s3 links */
export const downloadPDFFromLink = async (link, openIn = "_blank") => {
  var response = await fetch(link, {
    responseType: "arraybuffer",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/pdf",
    },
    method: "GET",
    mode: "cors",
  }).then((res) => res.blob());
  if (window.mSewaApp && window.mSewaApp.isMsewaApp() && window.mSewaApp.downloadBase64File) {
    var reader = new FileReader();
    reader.readAsDataURL(response);
    reader.onloadend = function () {
      var base64data = reader.result;
      window.mSewaApp.downloadBase64File(base64data, decodeURIComponent(link.split("?")[0].split("/").pop().slice(13)));
    };
  } else {
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";
    let url = window.URL.createObjectURL(response);
    a.href = url;
    a.download = decodeURIComponent(link.split("?")[0].split("/").pop().slice(13));
    a.click();
    window.URL.revokeObjectURL(url);
  }
};
