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
    console.log("img", img)
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

const defaultLogo = ""
  const jsPdfGenerator = async ({ breakPageLimit = null, tenantId, logo, name, email, phoneNumber, heading, details, applicationNumber, isTOCRequired = true, t = (text) => text }) => {
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

    background:[
    //   {
   
    //   width:595,
    //   height:842
    // }
  ],
    margin:[20,20,20,20],
  
    header: {
     
    },

    footer: function (currentPage, pageCount) {
            return {
        columns: [
         
          { text: `Page ${currentPage}`, alignment: "right", margin: [0, -17, 50, 0], fontSize: 11, color: "#6f777c", font: "Hind" },
        ],
      };
    },
    content: [
      ...createHeaderDetails(details,name, phoneNumber, email, logo, tenantId, heading, applicationNumber),
      ...createContent(details, phoneNumber,logo, tenantId,breakPageLimit),
      {
        text: t("PDF_SYSTEM_GENERATED_ACKNOWLEDGEMENT"),
        font: "Hind",
        fontSize: 11,
        color: "#6f777c",
        margin: [10, 10],
      }
              
    ],
    defaultStyle: {
      font: "Hind",
      margin:[20, 10, 20,10]
    },
  };

  /** 
   * @description: Function to append Terms and conditions if required
   * Appends Terms and Conditions if isTOCRequired is true
   * Solves the problem of displaying Terms and conditions even if those are not required
   * 
   * @author Khalid Rashid
   */
  if (isTOCRequired) {
    dd.content.push(
      {
        text: "TERMS_AND_CONDITIONS_OF_LICENSE",
        fontSize: 16,
        bold: true,
        alignment: "center",
        decoration: "underline",
        pageBreak: "before",
        margin: [0, 25, 0, 0],
      },
      {
        text: "TERMS_AND_CONDITIONS_OF_LICENSE_CONTENT",
        fontSize: 8,
        margin: [10, 20, 10, 0],
      }
    );
  }


  pdfMake.vfs = Fonts;
  let locale = Digit.SessionStorage.get("locale") || "en_IN";
  let Hind = pdfFonts[locale] || pdfFonts["Hind"];
  let ack;
  if(applicationNumber!==undefined &&applicationNumber.split("-")[0]==="FSM"){
    ack="FSM-AckForm"
  }
  else if(applicationNumber!==undefined &&applicationNumber.split("-")[1]==="PGR"){
    ack="PGR-AckForm"
  }
  else if(applicationNumber!==undefined &&applicationNumber.split("-")[1]==="TL"){
    ack="TL-AckForm"
  }
  else if(applicationNumber!==undefined &&applicationNumber.split("_")[0]==="WS"){
    ack="WS-AckForm"
  }
  else if(applicationNumber!==undefined &&applicationNumber.split("_")[0]==="SW"){
    ack="SW-AckForm"
  }
  else if(applicationNumber!==undefined &&applicationNumber.split("-")[1]==="AC"){
    ack="PT-AckForm"
  }
  else if(applicationNumber!==undefined &&applicationNumber.split("-")[1]==="BP"){
    ack="BPA-AckForm"
  }
  else if(applicationNumber===undefined)
  {
    module=details[0]?.values[0]?.value
    if(module.split("-")[1]==="MT"){
      ack="PT-AckForm"
    }
    else{
      ack="acknowledgement"
    }
    
  }
  else{
    ack="acknowledgement"
  }
 
  pdfMake.fonts = { Hind: { ...Hind } };
  const generatedPDF = pdfMake.createPdf(dd);
  downloadPDFFileUsingBase64(generatedPDF, ack);
};

/**
 * Function to create tables in the pdf/acknowledgement
 * Created for E-Waste Module
 * packages\modules\ew\src\utils\getEwAcknowledgementData.js
 * 
 *
 * @example
 * Digit.Utils.pdf.generateTable()
 *
 * @returns Downloads a pdf  
 */
const jsPdfGeneratorForTable = async ({ breakPageLimit = null, tenantId, logo, name, email, phoneNumber, heading, details, tableData, applicationNumber, t = (text) => text }) => {
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

    background:[
    //   {

    //   width:595,
    //   height:842
    // }
  ],
    margin:[20,20,20,20],
  
    header: {
     
    },

    footer: function (currentPage, pageCount) {
            return {
        columns: [
         
          { text: `Page ${currentPage}`, alignment: "right", margin: [0, -17, 50, 0], fontSize: 11, color: "#6f777c", font: "Hind" },
        ],
      };
    },
    content: [
      ...createHeaderDetails(details,name, phoneNumber, email, logo, tenantId, heading, applicationNumber),
      ...tableContent(t, details),
      {
        text: t("PDF_SYSTEM_GENERATED_ACKNOWLEDGEMENT"),
        font: "Hind",
        fontSize: 11,
        color: "#6f777c",
        margin: [10, 10],
      },
      {
        text:"TERMS_AND_CONDITIONS_OF_LICENSE",
        fontSize:16, 
        bold:true, 
        alignment:"center",
        decoration:"underline",
        pageBreak:'before',
        margin:[0, 25, 0, 0],
      },
      {
        text:"TERMS_AND_CONDITIONS_OF_LICENSE_CONTENT",
        fontSize:8,
        margin:[10, 20, 10,0] 
      },
              
    ],
    defaultStyle: {
      font: "Hind",
      margin:[20, 10, 20,10]
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
 * 
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
    background:[
    //   {
   
    //   width:595,
    //   height:842
    // }
  ],
  
    pageMargins: [40, 40, 40, 30],
        header: {},
    footer: function (currentPage, pageCount) {
      return {
        columns: [
          
          { text: `Page ${currentPage}`, alignment: "right", margin: [0, -30, 45, 0], fontSize: 11, color: "#6f777c", font: "Hind" },
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
        margin: [10, 32],
      },
      {
        text: t("TERMS_AND_CONDITIONS_OF_LICENSE"),
        fontSize:16, 
        bold:true, 
        alignment:"center",
        decoration:"underline",
        pageBreak:'before',
        margin:[0, 25, 0, 0],
      },
      {
        text: t("TERMS_AND_CONDITIONS_OF_LICENSE_CONTENT"),
        fontSize:8,
        margin:[10, 20, 10,0] 
        
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
  let ack;
  if(headerDetails[0]?.values[0].value.split("_")[0]==="WS"){
    ack="WS-AckForm"
  }
  else if(headerDetails[0]?.values[0].value.split("_")[0]==="SW"){
    ack="SW-AckForm"
  }
  else{
    ack="acknowledgement.pdf"
  }
  downloadPDFFileUsingBase64(generatedPDF, ack);
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

export default { generate: jsPdfGenerator, generateTable: jsPdfGeneratorForTable, generatev1: jsPdfGeneratorv1, generateModifyPdf: jsPdfGeneratorForModifyPDF, generateBillAmendPDF };

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
          return "#a82227"
        }
        return (rowIndex % 2 === 0) ? "#a82227" : null; 
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
          //   {
          //   // image: logo || getBase64Image(tenantId) ,
          //   // width: 50,
          //   margin: [10, 10],
          //   fit: [50, 50],
          //   //width: 50,
          //   //margin: [10, 10]
          // },
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
            color: "#a82227"
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
            color: "#a82227"
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
            color: "#a82227",
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
  let counter=1;
  details.forEach((detail, index) => {
    if (detail?.title) {
      detailsHeaders.push({
        style: 'tableExample',
        layout: "noBorders", 
        margin:[20,30,20,0],
        table: {
          widths: ['101.8%', '*'],
          body: [
             [
                    {
                      text: `${counter}. ${detail?.title}`, 
                      //border:[true, true, true, false], 
                      color: "#454545",                 
                      style: "header",
                      fontSize: 14,
                      bold: true
                    }
                         ]
          ]
        }
      })
      counter++;
    }
    if (detail?.isAttachments && detail.values) {
      detailsHeaders.push({
          style: 'tableExample',
          layout: "noBorders",
          margin:[20,0,20,0],
          table: {

            widths: ['40%', '*'],
            body: [
              [
                {
                 ul: detail?.values,
                 style: "header",
                 //border:  index< detail?.values?.length-1 ?  [true, false, true,false]:index===detail?.values?.length-1? [true, false, true, true]:[] ,
                  fontSize: 10,
                  //bold: true
                },
              ]
            ]
      }})
    } else {
      detail?.values?.forEach(indData => {
        detailsHeaders.push({
          style: 'tableExample',
          layout: "noBorders",
          margin:[20,0,20,0],
          table: {
            widths: ['40%', '*'],
            body: [
              [
                {
                  text: indData?.title,
                  style: "header",
                  fontSize: 10,
                  //border:  index< detail?.values?.length-1 ?  [true, false, false,false]:index===detail?.values?.length-1? [true, false, false, true]:[] ,
                  //bold: true
                },

                {
                  text: `:  ${indData?.value}`,
                  fontSize: 10,
                  //border: index< detail?.values?.length-1    ?  [false, false, true, false]:index===detail?.values?.length-1? [false, false, true, true] :[],
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
  function createHeaderDetails(details,name, phoneNumber, email, logo, tenantId, heading, applicationNumber){
    let headerData = [];
    headerData.push({
      style: 'tableExample',
      layout: "noBorders",
      //fillColor: "#f7e0d4",
      margin: [0, 10, 0, 0],
      table: {
        widths: ['100%'],
        body: [
          [
          {
              text: heading, //"New Sewerage Connection",
              bold: true,
              fontSize: 19,
              alignment: "center",
              decoration : "underline"
             
          }
           ]
        ]
       }
    });
    headerData.push({
      style : 'tableExample',
      layout: "noBorders",
      margin:[0,0,0,0],
      table:{
        widths:['100%'],
        body:[
          [{
            
              text: `Issued by the ${name}`, 
              alignment:"center",
              fontSize: 11,
              //bold: true
                
          }]
        ]
      }
  })
  headerData.push({
    style : 'tableExample',
    layout: "noBorders",
    margin:[0,-6,0,0],
    table:{
      widths:['100%'],
      body:[
        [{
        
            text: `${email}   ${phoneNumber}`, //"Amritsar Municipal Corporation",
            alignment:"center",
            fontSize: 11,
            //bold: true 
        }]
      ]
    }
})
  headerData.push({
    style : 'tableExample',
    layout: "noBorders",
    margin: [0, -45, 0, 20],
    table:{
      widths: ['20%', '*', '10%'],
      body:[
        [
          // {
          //   // image: logo|| getBase64Image(tenantId) ,
          //   width: 70,
          //   margin: [10, 10],
          //   fit:[50,50]
          // }, 
          
      ]
      ]
    }
})
headerData.push({
  style : 'tableExample',
  layout: "noBorders",
  margin: [0, 30, 5, 0],
 
  table:{
    widths:['100%'],
    body:[
      [
        {
          
          text: `Application Number: ${applicationNumber}`,
          alignment:"right",
          fontSize: 9,
          //bold: true      
        },
      ]
    ]
  }
})

  return headerData;
  console.log("details", details)
}

function createHeader(headerDetails,logo,tenantId) {
  let headerData = [];
  headerData.push({
    style: 'tableExample',
    layout: "noBorders",
    //fillColor: "#f7e0d4",
    margin: [0, 10, 0, 0],
    table: {
      widths: ['100%'],
      body: [
        [
        {
            text: (headerDetails?.[0]?.typeOfApplication).toUpperCase(), //"New Sewerage Connection",
            bold: true,
            fontSize: 19,
            alignment: "center",
            decoration : "underline"
           
        }
         ]
      ]
     }
  });
  headerData.push({
      style : 'tableExample',
      layout: "noBorders",
      margin:[0,0,0,0],
      table:{
        widths:['100%'],
        body:[
          [{
            
              text: `Issued by the ${headerDetails?.[0]?.subHeader}`, //"Amritsar Municipal Corporation",
              alignment:"center",
              fontSize: 11,
              //bold: true
                
          }]
        ]
      }
  })
  const description=headerDetails?.[0]?.description||"";
  const extractedDetails=description.split("|");
  let email=extractedDetails[2];;
  let phoneNumber=extractedDetails[0];
  headerData.push({
    style : 'tableExample',
    layout: "noBorders",
    margin:[0,-6,0,0],
    table:{
      widths:['100%'],
      body:[
        [{
        
            text: `${email}   ${phoneNumber}`, //"Amritsar Municipal Corporation",
            alignment:"center",
            fontSize: 11,
            //bold: true 
        }]
      ]
    }
})
  
  headerData.push({
    style : 'tableExample',
    layout: "noBorders",
    margin: [0, -45, 0, 20],
    table:{
      widths: ['auto', '*', 'auto'],
      body:[
        [
          // {
          //   // image: logo || getBase64Image(tenantId) ,
          //   width: 50,
          //   margin: [10, 10],
          //   fit:[50,50]
          // }, 
          
      ]
      ]
    }
})

headerData.push({
  style: 'tableExample',
  layout: 'noBorders',
  margin: [0, 30, 5, 0], // positive top & bottom spacing
  table: {
    widths: ['100%'],
    body: [
      [
        {
          text: `Application Number: ${applicationNumber}`,
          alignment: 'right',
          fontSize: 10,
          bold: true
        }
      ]
    ]
  }
});

  return headerData;
}
function createContent(details, logo, tenantId,phoneNumber, breakPageLimit = null) {
  const detailsHeaders = []; 
  let counter=1;
  details.forEach((detail, index) => {
    console.log("detail",detail)
    if (detail?.values?.length > 0) {
      console.log("lennn", detail?.title.length)
      detailsHeaders.push({
        style: 'tableExample',
        margin:[10,20,10,0],
        layout:"noBorders",
        table: {
          widths: ['101.8%', '*'],
          body: [
              [
                {
                  text:  `${counter}. ${detail?.title}`, 
                  border:[true, true, true, false],
                  color: "#454545",                 
                  style: "header",
                  fontSize: 14,
                  bold: true,
                  margin:[0, 5, 0, 5]
                }
              ]
          ]
        }
      })
      counter++;
    }
    if (detail?.isAttachments && detail.values) {
      detailsHeaders.push({
        style: 'tableExample',
        
        margin:[10,0,0,0],
        table: {
          widths: ['40%', '60%'],
          body: [
            [
              {
               ul: detail?.values,
               style: "header",
               
                fontSize: 10,
                //bold: true
              },
            ]
          ]
    }})}
    else {
      if (Array.isArray(detail?.values)) {
          // Check if the title is "Owner Details" and if there are multiple owners
          const hasMultipleOwners = detail?.values.some(
              indData => indData?.title === "Ownership" && indData?.value === "Multiple Owners"
          );
  
          if ((detail?.title === "Owner Details" ||detail?.title ==="Transferor Details" ||detail?.title ==="Mutation")&& hasMultipleOwners) {
              // Creating a new table for owner details with borders
              const ownerDetailsTable = {
                  style: 'tableExample',
                  layout: {
                      hLineWidth: () => 1,
                      vLineWidth: () => 1,
                      hLineColor: () => '#000',
                      vLineColor: () => '#000',
                      paddingLeft: () => 10,
                      paddingRight: () => 10,
                      paddingTop: () => 5,
                      paddingBottom: () => 5
                  },
                  margin: [10, 0, 10, 0],
                  table: {
                      widths: ['40%', '60%'],
                      body: []
                  }
              };
  
              // Populating the body of the table with owner details
              detail.values.forEach((indData) => {
                  ownerDetailsTable.table.body.push([
                      {
                          text: indData?.title,
                          style: "header",
                          fontSize: 10,
                          border: [true, true, false, true], 
                      },
                      {
                          text: `:  ${indData?.value}`,
                          fontSize: 10,
                          border: [false, true, true, true], 
                      }
                  ]);
  
                  // After "Owner Address", check for the flag
                  if (indData?.title === "Owner Address") {
                      // Add an empty row after "Owner Address"
                      ownerDetailsTable.table.body.push([
                          {
                              text: '', // Empty cell
                              border: [false, false, false, false], 
                          },
                          {
                              text: '', // Empty cell
                              border: [false, false, false, false], 
                          }
                      ]);
                  }
              });
  
              // Push the owner details table to detailsHeaders
              detailsHeaders.push(ownerDetailsTable);
          } else {
              // Default behavior for other titles
              detail.values.forEach((indData) => {
                  detailsHeaders.push({
                      style: 'tableExample',
                      layout: "noBorders",
                      margin: [10, 0, 0, 0],
                      table: {
                          widths: ['40%', '60%'],
                          body: [
                              [
                                  {
                                      text: indData?.title,
                                      style: "header",
                                      fontSize: 10,
                                      border: [true, true, false, true], 
                                  },
                                  {
                                      text: `:  ${indData?.value}`,
                                      fontSize: 10,
                                      border: [false, true, true, true], 
                                  }
                              ]
                          ]
                      }
                  });
              });
          }
      }
  }
  
  });
 

  return detailsHeaders;
}



/**
 * Function to create table in pdf.
 * Completely a separate function and doesn't affect any other function.
 * @author khalid rashid
 */
function tableContent(t, details) {

  let tableBody = [
    [t("S_NO"), t("PRODUCT_NAME"), t("PRODUCT_QUANTITY"), t("UNIT_PRICE"), t("TOTAL_PRODUCT_PRICE") ]
  ]

  const detailsHeaders = []; 
  let counter=1;
  details.forEach((detail) => {
    if (detail?.values?.length > 0) {
      detailsHeaders.push({
        style: 'tableExample',
        margin:[10,20,10,0],
        layout:"noBorders",
        table: {
          widths: ['101.8%', '*'],
          body: [
              [
                {
                  text:  `${counter}. ${detail?.title}`, 
                  border:[true, true, true, false],
                  color: "#454545",                 
                  style: "header",
                  fontSize: 14,
                  bold: true
                }
              ]
          ]
        }
      })
      counter++;
    }
    if (detail?.tableData?.rows) {
      detailsHeaders.push({
        style: 'tableExample',
        margin:[10,20,10,0],
        layout:"noBorders",
        table: {
          widths: ['101.8%', '*'],
          body: [
              [
                {
                  text:  `${counter}. ${detail?.tableData?.title}`, 
                  border:[true, true, true, false],
                  color: "#454545",                 
                  style: "header",
                  fontSize: 14,
                  bold: true
                }
              ]
          ]
        }
      })
      counter++;
    }
    if (detail?.isAttachments && detail.values) {
      detailsHeaders.push({
        style: 'tableExample',
        
        margin:[10,0,10,0],
        table: {
          widths: ['40%', '*'],
          body: [
            [
              {
               ul: detail?.values,
               style: "header",
                fontSize: 10,
                //bold: true
              },
            ]
          ]
    }})
    } else {
      detail?.values?.map((indData, index) => {
        detailsHeaders.push({
          style: 'tableExample',
          layout: "noBorders", 
          margin:[10,0,10,0],
          table: {
            widths: ['40%', '*'],
            body: [
              [
                {
                  text: indData?.title,
                  style: "header",
                  fontSize: 10,
                },

                {
                  text: `:  ${indData?.value}`,
                  fontSize: 10,
                }
              ]
            ]
          }
        })
      })
    
    }

  
    if(detail.tableData){
      if(detail?.tableData?.rows){
        tableBody.push(
          ...detail.tableData.rows.map((d,index) => {
          return [
            {
              text: index+1, 
              fontSize: 10,
            },
            {
              text: d.productName, 
              fontSize: 10,
            },
            {
              text: d.quantity, 
              fontSize: 10,
            },
            {
              text: d.price/d.quantity, 
              fontSize: 10,
            },
            {
              text: d.price, 
              fontSize: 10,
            }

           ]
        }))
      }

      detailsHeaders.push({
        style: 'tableExample',
        margin:[10,0,10,0],
        table: {
          headerRows: 1,
          widths: ['12%', '22%', '22%', '22%','22%'],
          body: tableBody,
        }
      }
    )
    }

  });

  return detailsHeaders;
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
  console.log("createContentForDetailsWithLengthOfOneAndThree",values, data, column1, column2)
  values.forEach((value, index) => {
    if (index === 0) {
      column1.push({
        text: value.title,
        font: "Hind",
        fontSize: 12,
        // bold: true,
        width: "30%",
        margin:[-25, 0, 0, 0],
    });
      column2.push({
        text: value.value,
        font: "Hind",
        fontSize: 9,
        color: "#1a1a1a",
        margin:[-25, 0, 0, 0],
                width: "30%",
      });
    } else if (index === 2) {
      column1.push({
        text: value.title,
        font: "Hind",
        fontSize: 9,
        // bold: true,
        width: "30%",
                margin:[-25, 0, 0, 0],
      });
      column2.push({
        text: value.value,
        font: "Hind",
        fontSize: 9,
        margin:[-25, 0, 0, 0],
        color: "#1a1a1a",
        width: "30%",
              });
    } else {
      column1.push({
        text: value.title,
        font: "Hind",
        fontSize: 9,
        // bold: true,
        width: "30%",
        margin:[-25, 0, 0, 0],
              });
      column2.push({
        text: value.value,
        font: "Hind",
        fontSize: 9,
        margin:[-25, 0, 0, 0],
        color: "#1a1a1a",
        width: "30%",
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
