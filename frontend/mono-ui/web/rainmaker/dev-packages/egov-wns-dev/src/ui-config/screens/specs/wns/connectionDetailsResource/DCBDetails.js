import {
  getCommonGrayCard,
  getCommonSubHeader,
  getCommonContainer,
} from "egov-ui-framework/ui-config/screens/specs/utils";
import { getLabelOnlyValue,getLabelOnlyValueforColumnData, getLabelOnlyValueforColumn , getLabelOnlyValueForTableHeader1 ,getLabelOnlyValueForTableHeader2, getLabelOnlyValueForTableHeader3, handleAmount} from '../../utils';


export const Header =  () =>  {
  return( 
     getCommonContainer({
      installment: getLabelOnlyValueForTableHeader2({ labelKey: "Installments" })  ,
      demand: getLabelOnlyValueForTableHeader3({ labelKey: "Demand" })  ,
      collection: getLabelOnlyValueForTableHeader3({ labelKey: "Collection"})  ,
      balance: getLabelOnlyValueForTableHeader3({ labelKey: "Balance"})  ,
      advance: getLabelOnlyValueForTableHeader1({ labelKey: "Advance"})  ,
     })
   )
  }

  export const subHeader =  () =>  {
    return( 
       getCommonContainer({
        installment: getLabelOnlyValueForTableHeader2({ labelKey: " " })  ,
        taxAmount: getLabelOnlyValueforColumn({ labelKey: "Tax"}),
        interestAmount: getLabelOnlyValueforColumn({ labelKey: "Interest" })  ,
        penaltyAmount: getLabelOnlyValueforColumn({ labelKey: "Penalty" })  ,
        taxCollected: getLabelOnlyValueforColumn({ labelKey: "Tax" })  ,
        interestCollected: getLabelOnlyValueforColumn({ labelKey: "Interest" })  ,
        penaltyCollected: getLabelOnlyValueforColumn({ labelKey: "Penalty" })  ,
        taxBalance: getLabelOnlyValueforColumn({ labelKey: "Tax" })  ,
        interestBalance: getLabelOnlyValueforColumn({ labelKey: "Interest" })  ,
        penaltyBalance: getLabelOnlyValueforColumn({ labelKey: "Penalty" })  ,
        advance: getLabelOnlyValueForTableHeader1({ labelKey: "Advance" })  ,
       })
     )
    }


    export const totalRow1 =  () =>  {
      return( 
         getCommonContainer(
          {
          installmentbox: getLabelOnlyValueForTableHeader2({ labelKey: " Total " })  ,
          taxAmountbox: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalTaxDemand" , callBack: handleAmount}),
          interestAmountbox: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalInterestDemand" , callBack: handleAmount })  ,
          penaltyAmountbox: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalPenaltyDemand" , callBack: handleAmount })  ,
          taxCollectedbox: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalTaxCollected" , callBack: handleAmount })  ,
          interestCollectedbox: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalInterestCollected" , callBack: handleAmount })  ,
          penaltyCollectedbox: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalPenaltyCollected" , callBack: handleAmount })  ,
          taxBalance: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
          interestBalance: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
          penaltyBalance: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
          advance: getLabelOnlyValueForTableHeader1({ labelKey: " " })  ,
         })
      )
      }
      export const totalRow2 =  () =>  {
        return( 
          getCommonContainer({
            installmentbox: getLabelOnlyValueForTableHeader2({ labelKey: " " })  ,
            taxAmountbox: getLabelOnlyValueforColumnData({ labelKey: " "}),
            interestAmountbox: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
            penaltyAmountbox: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
            taxCollectedbox: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
            interestCollectedbox: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
            penaltyCollectedbox: getLabelOnlyValueforColumn({ labelKey: "Total" })  ,
            taxBalance: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalTaxBalance" , callBack: handleAmount })  ,
            interestBalance: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalInterestBalance" , callBack: handleAmount })  ,
            penaltyBalance: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalPenaltyBalance" , callBack: handleAmount })  ,
            advance: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalAdvance" , callBack: handleAmount })  ,
           })
        )
        }

        export const totalRow3 =  () =>  {
          return( 
            getCommonContainer({
              installmentbox: getLabelOnlyValueForTableHeader2({ labelKey: " " })  ,
              taxAmountbox: getLabelOnlyValueforColumnData({ labelKey: " "}),
              interestAmountbox: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
              penaltyAmountbox: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
              taxCollectedbox: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
              interestCollectedbox: getLabelOnlyValueforColumnData({ labelKey: " " })  ,
              penaltyCollectedbox: getLabelOnlyValueforColumn({ labelKey: "Total Balance" })  ,
              taxBalance: getLabelOnlyValueforColumnData({ jsonPath: "dcbtotalDetails[0].totalBalance" , callBack: handleAmount })  ,
              
             })
          )
          }

export const resData =  () =>  {
  return( 
     getCommonContainer({
       
      installment: getLabelOnlyValueForTableHeader2({ jsonPath: "dcbDetails[0].installment" , callBack: handleAmount  })  ,
      taxAmount: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].taxAmount" , callBack: handleAmount })  ,
      interestAmount: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].interestAmount" , callBack: handleAmount })  ,
      penaltyAmount: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].penaltyAmount" , callBack: handleAmount })  ,
    
    
      taxCollected: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].taxCollected" , callBack: handleAmount })  ,
      interestCollected: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].interestCollected", callBack: handleAmount  })  ,
      penaltyCollected: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].penaltyCollected", callBack: handleAmount  })  ,
    
      taxBalance: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].taxBalance" , callBack: handleAmount })  ,
      interestBalance: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].interestBalance" , callBack: handleAmount })  ,
      penaltyBalance: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].penaltyBalance" , callBack: handleAmount })  ,
      advance: getLabelOnlyValueforColumnData({ jsonPath: "dcbDetails[0].advance" , callBack: handleAmount })  ,
     })
   )
  }



export const getDCBDetails = () => {
    return getCommonGrayCard({
      headerDiv: {
        uiFramework: "custom-atoms",
        componentPath: "Container",
        props: {
          style: { marginBottom: "10px" }
        },
        children: {
          header: {
            gridDefination: {
              xs: 12,
              sm: 10
            },
            ...getCommonSubHeader({
              label: "DCB Details"
            })
          }
        }
      },
      dcbHeader:Header(),
      dcbsubHeader:subHeader(),
      dcbDetails:{
        uiFramework: "custom-containers",
        componentPath: "MultiItem",
        props: {
      
          scheama: resData(),
          items: [],
          hasAddItem: false,
          isReviewPage: true,
          sourceJsonPath: "dcbDetails",
          prefixSourceJsonPath: "children",
          afterPrefixJsonPath: "children.value.children.key"
      
        },
        type: "array"
      },
      totalRow1:totalRow1(), 
      totalRow2:totalRow2(), 
      totalRow3:totalRow3(), 
    });
  };