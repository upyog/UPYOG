/**
 * @author Shivank-NUDM
 * 
 * This file is Developed so that we can remove all the inline level CSS.
 * All the CSS used in the Components inside this CnD Module will be written here and in he same way 
 * in future we can just add the object of the new CSS and add here.
 */

import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";

export const cndStyles= {
  applicationContainerStyle : {
    padding: '10px',
    border: '1px solid #ccc',
    transition: 'background-color 0.3s ease, box-shadow 0.3s ease',
    position: 'relative',
    marginBottom: '16px',
    width:"50%",
    cursor: "pointer"
  },

  applicationContainerHoverStyle : {
    boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)'
  },

  selectedStyle : {
    backgroundColor: '#f0f7ff',
    borderColor: '#2196f3',
    boxShadow: '0px 0px 0px 2px rgba(33, 150, 243, 0.3)'
  },

  checkmarkStyle : {
    position: 'absolute',
    top: '10px',
    right: '10px',
    color: '#2196f3',
    fontWeight: 'bold',
    fontSize: '20px'
  },

  addButtonMargin:{
    marginBottom:"5px"
  },

  errorStyle:{
    width: "70%", 
    marginLeft: "30%", 
    fontSize: "12px", 
    marginTop: "-21px"
  },

  constructionDatePicker:{
    display: "flex", 
    gap: "1rem", 
    width: "50%"
  },
  
  employeeFields:{
    width:"50%"
  },
  
  citizenWidth:{
    width:"85%"
  },

  addressGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr 1fr",
    gap: "0px 20px",
  },

  loaderAlignment:{
    display: "flex", 
    alignItems: "center", 
    gap: "8px"
  },

  employeeSideWasteTypeFont:{
    fontWeight:"bold"
  },

  wasteQunatityInProgress:{
    width:"72%"
  },

  wasteQuantityCitizen:{
    width:"100%"
  },

  containerStyleInProgress:{
    display:"flex",
    alignItems:"center",
    marginBottom: '10px'
  },

  containerStyleNotInProgress:{
    display:"block",
    alignItems:"initial",
    marginBottom: '10px'
  },

  labelStyleInProgress:{
    minWidth:'180px',
    flexShrink:0
  },
  labelStyleNotInProgress:{
    minWidth:'auto',
    flexShrink:'initial'
  },

  siteSackPhotoEmployee:{
    marginBottom:"20px",
  },
  siteMediaPhotoEmployee:{
    marginBottom:"15px",
  },

  errorStyle: { 
    width: "70%", 
    marginLeft: "30%", 
    fontSize: "12px",
    marginTop: "-21px"
  },

  employeeSideContainer:{
    border: "1px solid #E3E3E3", 
    padding: "16px", 
    marginTop: "8px" 
  },

  wasteTypeTable:{
    marginTop: "20px", 
    marginBottom: "20px", 
    width: "50%"
  },

  wasteTypeHeader:{
    display: "flex", 
    borderBottom: "1px solid #ccc", 
    fontWeight: "bold", 
    padding: "10px 0" 
  },

  wasteTypeRow:{
    display: "flex", 
    padding: "15px 0", 
    borderBottom: "1px solid #eee",
    alignItems: "center" 
  },
  wasteTypeHeadingType:{
    flex: 2
  },
  wasteTypeHeadingQuantity:{
    flex: 1, 
    textAlign: "center", 
    marginRight: "8%"
  },
  wasteTypeHeadingUnits:{
    flex: 1, 
    textAlign: "center"
  },
  wasteQuantityInput:{
    flex: 1, 
    textAlign: "center"
  },

  quantityTextInput:{
    width: "50%",
    padding: "10px", 
    borderRadius: "4px", 
    border: "1px solid #ccc",
  },

  unitDropdown:{
    flex: 1, 
    textAlign: "center", 
    marginBottom: "3%"
  },

  unitDropDOwnSelect:{
    padding: "10px", 
    borderRadius: "4px", 
    border: "1px solid #ccc",
    width: "50%" 
  },

  noWasteTypeSelect:{
    padding: "15px 0", 
    textAlign: "center"
  },

  additionalWasteTypeRow:{
    display: "flex", 
    padding: "15px 0", 
    borderBottom: "1px solid #eee",
    alignItems: "center",
    backgroundColor: "#f9f9f9" 
  },

  additionalDropDown:{
    padding: "10px", 
    borderRadius: "4px", 
    border: "1px solid #ccc",
    width: "90%" 
  },

  additionalUnitDropdown:{
    flex: 1, 
    textAlign: "center", 
    display: "flex", 
    alignItems: "center", 
    justifyContent: "space-between", 
    paddingRight: "10px"
  },

  removeButton:{
    padding: "6px 10px",
    backgroundColor: "#882636",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
    fontSize: "12px"
  },

  addButton:{
    padding: "10px 15px",
    backgroundColor: "#882636",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    gap: "5px"
  },

  addButtonFont:{
    fontSize: "16px",
    color: "white"
  },

  payButton:{
    marginTop: "1em", 
    bottom: "0px", 
    width: "100%", 
    marginBottom: "1.2em"
  },

  timelineHeader:{
    marginBottom: "16px", 
    marginTop: "32px" 
  },

  moduleCardHeader:{
    width: "200px",
    wordWrap: "break-word"
  },

  noInboxApplication:{
    textAlign:"center"
  },

  applicationTable:{
    padding: "20px 18px",
    fontSize: "16px",
  },

  searchApplication:{
    width:"75%"
  },

  resultWithoutSearch:{
    marginLeft:"24px",
    flex:1
  },

  resultWithSearch:{
    flex:1
  },

  clearButton:{
    marginTop:"10px"
  },

  searchApplicationWarning:{
    color: "#505A5F"
  },
  
  filterLabel:{
    display: "flex", 
    alignItems: "center"
  },

  filterHeading:{
    alignItems: "center"
  },

  filterMargin:{
    marginLeft: "8px"
  },

  filterClearButton:{
    border: "1px solid #e0e0e0", 
    padding: "6px"
  },

  sortingStyle:{
    overflowX:"scroll"
  },

  fieldStyle:{
    marginRight: 0 
  },

  cardStyle:{
    width: "85%"
  },

  applicationDetailHeader:{
    marginLeft: "0px", 
    paddingTop: "10px", 
    fontSize: "32px"
  },

  applicationDetailCard:{
    zIndex: "10",
    display:"flex",
    flexDirection:"row-reverse",
    alignItems:"center",
    marginTop:"-25px"
  },

  downloadButton:{
    zIndex: "10",  
    position: "relative"
  },

  menuStyle:{
    color: "#FFFFFF", 
    fontSize: "18px"
  },

  backButton:{
    marginTop:"15px"
  },

  citizenApplicantDetailCard:{
    fontSize: "24px"
  },

  cardHeaderWithOptions:{
    marginRight: "auto", 
    maxWidth: "960px"
  },

  cardHeader:{
    fontSize: "32px"
  },

  citizenApplicationTable:{
    minWidth: "150px",
    padding: "10px",
    fontSize: "16px",
    paddingLeft: "20px",
  },

  applicationList:{
    marginLeft: "16px", 
    marginTop: "16px"
  },

  newApplication:{
    display: "block"
  },

  rowContainerStyle: {
    padding: "4px 0px",
    justifyContent: "space-between",
  },

  successBar:{
    width:"100%"
  },

  textStyle:{
    whiteSpace: "pre", 
    width: "60%"
  },

  editIcon:{
    marginTop: "-30px", 
    float: "right", 
    position: "relative", 
    bottom: "32px"
  },

  rowStyle:{
    marginTop:"30px",
    marginBottom:"30px"
  },

  checkBox:{
    height: "auto", 
    marginBottom:"30px", 
    marginTop:"10px"
  },

  inboxClearButton:{
    gridColumn: "3/3", 
    textAlign: "right", 
    paddingTop: "10px"
  },

  submitBarFlex:{
    flex: 1
  },

  searchSubmitBar:{
    width:"60%", 
    marginTop:"17%"
  },

  searchWrapper:{
    maxWidth: "unset", 
    marginLeft: "unset"
  },

  searchContainerInbox:{
    width: "auto",
    marginLeft:"24px"
  },

  searchContainer:{
    width: "auto",
    marginLeft:"revert"
  },

  clearButtonDesktop:{
    display: "inline", 
    margin: "25%"
  },

  clearButtonMobile:{
    display: "inline", 
    margin: 0
  }

}