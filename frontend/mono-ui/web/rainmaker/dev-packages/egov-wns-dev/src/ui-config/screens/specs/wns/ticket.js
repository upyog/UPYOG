import { getCommonHeader, getBreak, getCommonTitle, getLabel ,  getLabelWithValue,} from "egov-ui-framework/ui-config/screens/specs/utils";
import "./index.css";









const header = getCommonHeader({
    label: "Raise your Issue"
  });


const ticket = {
    uiFramework: "material-ui",
    name: "create",
    beforeInitScreen: (action, state, dispatch) => {
     
  
      
      return action;
    },
    components: {
        div: {
          uiFramework: "custom-atoms",
          componentPath: "Form",
          props: {
            className: "common-div-css",
            id: "create"
          },
          children: {
            

           // Add the box here-------------------

           subHeader: getCommonTitle({  label: "Raise Ticket" },
           {style: 
                        {
                        fontSize: "24px",
                        fontWeight: 800,
                        marginBottom: 20 } }),
         
        
                        subHeader1: getCommonTitle({  label: "If there is any Issue/Error regarding  Property Tax, Trade License, PGR , Miscellaneous  Collection or any other m Seva Service , please click on the Button below and follow the steps –" },
           {style: 
                        {color: "rgba(255, 0, 0, 0.60)",
                        fontSize: "20px",
                        fontWeight: 400,
                        marginBottom: 20 } }),

                     
                        headerDiv: {
                          uiFramework: "custom-atoms",
                          componentPath: "Container",
                
                          children: {
                            
                            newApplicationButton: {
                              componentPath: "Button",
                              gridDefination: {
                                xs: 12,
                                sm: 6,
                                align: "right"
                              },
                              visible: true,
                              props: {
                                variant: "contained",
                                color: "primary",
                                style: {
                                  color: "white",
                                  borderRadius: "2px",
                                  width: "250px",
                                  height: "48px"
                                }
                              },
                              children: {
                              
                                buttonLabel: getLabel({
                                  labelName: "Raise Your Ticket",
                                  label: "Raise Your Ticket"
                                })
                              },
                              onClickDefination: {
                                action: "condition",
                                callBack: (state, dispatch) => {
                                  openTicketTool(state, dispatch);
                
                                }
                              },
                             
                            }
            
                          }
                        },

           subHeader2: getCommonTitle({  label: "1. Click the above Button to go to our Ticketing Tool." },
           {style: { marginBottom: 20 , marginTop:20 ,  fontWeight:400} }),
           subHeader3: getCommonTitle({  label: "2. Enter User id and Password provided to you to Login this Tool." },
           {style: { marginBottom: 20 , marginTop:20 ,  fontWeight:400} }),
           subHeader4: getCommonTitle({  label: "3. Dashboard Will Appear, in which you can see your Total Tickets / Raised Tickets / Assigned Tickets and pending Tickets." },
           {style: { marginBottom: 20 , marginTop:20 , fontWeight:400} }),
           
           subHeader5: getCommonTitle({  label: "4. Click on Generate Tickets, Fill all the necessary Fields regarding your Issues and press Save to forward your issue to Our Team. You can also attach multiple Files. "},
           {style: { marginBottom: 20 , marginTop:20 ,  } }),
           subHeader6: getCommonTitle({  label: " In case of Multiple Bill Cancellations, Connection Disconnections, Employee id Generation We need proper Signed Letter from Senior Officers. ( Kindly keep in mind, always select PMIDC under Assignee Drop-down. )"},
           {style: {fontSize: "20px",fontWeight: 400, marginBottom: 20 } }),
           subHeader7: getCommonTitle({  label: "5. After entering your Issue, Go to Raised Ticket and find Ticket Number and Track your Issues."},
           {style: { marginBottom: 20 , marginTop:20 ,  fontWeight:400} }),
           subHeader8: getCommonTitle({  label: "6. Under Raised Ticket you can Find your Issues Status, Whether it is In Progress / On Hold / Resolved / Closed or Re-Opened."},
           {style: { marginBottom: 20 , marginTop:20 , fontWeight:400} }),
           subHeader9: getCommonTitle({  label: "( Kindly don't Use Whatsapp or Calls for any Issue, Login in this application, Generate your ticket, track ticket and make them resolved )"},
           {style:  {color: "rgba(255, 0, 0, 0.60)",
           fontSize: "20px",
           fontWeight: 400,
           marginBottom: 10 } }),


         





           //------------------------------


          }
        },
        adhocDialog: {
          uiFramework: "custom-containers",
          componentPath: "DialogContainer",
          props: {
            open: false,
            maxWidth: false,
            screenKey: "create"
          },
          children: {
            popup: {}
          }
        }
      }


}
export default ticket

const openTicketTool = (state, dispatch) => {
  window.open("https://stvending.punjab.gov.in/ticket/", "_blank")
};
