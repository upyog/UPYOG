import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import 'bootstrap/dist/css/bootstrap.min.css';
import Paper from '@material-ui/core/Paper';
import {
  Grid,
  Card,
  CardActions,
  CardContent,
  List,
  ListItem,
  ListItemText,
  Typography,
} from "@material-ui/core";
import "./index.css";

import facebook from "../../img/facebook.png";
import twitter from "../../img/twitter.png";
import chrome from "../../img/chrome.png";
import edge from "../../img/edge.png";
import mozilla from "../../img/mozilla.png";
import wz from "../../img/w3c.jpg";
import gigw from "../../img/GIGW_LOGO.png";
import privacy from "../../img/Data-Policy.pdf";
import BlockchainStrategy from "../../img/BlockchainStrategymerged.pdf";
import AIStrategy from "../../img/AIStrategymerged.pdf";
import Emerging from "../../img/Emerging-Technologies.pdf";
const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,

    backgroundColor:"#0b519e"

  },
  container :
  {    
  marginLeft:"10%",
  marginRight:"10%",
  marginTop:"10%",
  paddingRight:"18%",
  paddingTop: "50px"

  },
  header :
  {
    fontFamily: "Roboto",
    fontStyle: "normal",
    fontWeight: "500",
    fontSize: "16px",
    lineHeight: "24px",
    color : "#FFFFFF"

  },
  paragraph :
  {
    color : "#FFFFFF",
    fontFamily: "Roboto",
    fontStyle: "normal",
    fontWeight: "normal",
    fontSize: "14px",
    lineHeight: "20px"

  },
  paper: {
    padding: theme.spacing(2),
    textAlign: 'center',
    color: theme.palette.text.secondary,
  },
}));

//var counterContainer = document.querySelector(".website-counter");
var counterContainer;
var resetButton = document.querySelector("#reset");
var visitCount = localStorage.getItem("page_view");

// Check if page_view entry is present
if (visitCount) {
  visitCount = Number(visitCount) + 1;
  localStorage.setItem("page_view", visitCount);
} else {
  visitCount = 1;
  localStorage.setItem("page_view", 1);
}
counterContainer= visitCount;

// Adding onClick event listener
// resetButton.addEventListener("click", () => {
//   visitCount = 1;
//   localStorage.setItem("page_view", 1);
//   counterContainer = visitCount;
// });

export default function CenteredGrid() {
  const classes = useStyles();
  const dateyear = new Date();
  let currentyear = dateyear.getFullYear();
  return (
    <div className={classes.root}>
      <Grid container spacing={1}  className={classes.container}>
       
        <Grid item xs={12} sm={6} md={3}>    
        <p>   
        <h5 className={classes.header}  style={{fontSize: "1rem", marginTop: "-5px"}}>
                <b>Contact Details</b>
              </h5></p>
              <p className={classes.paragraph} >
                Office Address: <br />
                Punjab Municipal Bhawan,<br /> 3, Dakshin Marg, 35A,<br />
                Chandigarh, 160022 <br />
                </p>
                <p className={classes.paragraph} >
                Call Us:<br />
                1800 1800 0172<br />             
                </p>
                <p className={classes.paragraph} >
                Email Us:<br />
                <a href="mailto:pgrs.lg@punjab.gov.in" className='email'>pgrs.lg@punjab.gov.in</a>
                <br /></p>  
                <p className={classes.paragraph} >
                For any  issues regarding online payments:<br />
                <p>
                <a href="mailto:egovdolg@gmail.com" className='email'>egovdolg@gmail.com</a>
                <br /> </p>
                <a href="https://www.facebook.com/pmidc1/" target='_blank'>                
                <img src={facebook}   style={{width: "6%", marginRight: "6%"}}  alt='mSewa Punjab' /></a>
                <a href="https://twitter.com/pmidcpunjab" target='_blank'>                                 
                <img src={twitter}   style={{width: "6%"}} alt='mSewa Punjab' /></a>           
                </p> 
                   </Grid>
        <Grid item xs={12} sm={6} md={3}>
       
        <p>
                <h5 className={classes.header} style={{fontSize: "1rem" , marginTop: "-5px"}}><b>Other Departments</b></h5>
              </p>
              <a
                href="https://lgpunjab.gov.in/"
                id ="flink"
                //className={classes.block}
                target="_blank"
              >
                Local Government Punjab
              </a><br />
              <a
                href="https://pmidc.punjab.gov.in/"
                id ="flink"
                //className={classes.block}
                target="_blank"
              >
                PMIDC
              </a><br />
              <a
                href="https://lgpunjab.gov.in/cms/contact-us.php"
                id ="flink"
                //className={classes.block}
                target="_blank"
              >
                Contact Us
              </a><br />
              <a
                href="https://lgpunjab.gov.in/cms/website-policy.php"
                id ="flink"
                //className={classes.block}
                target="_blank"
              >
                Privacy policy
              </a><br />  
              <a
                href="https://nesda.centralindia.cloudapp.azure.com/#/citizen-survey"
                id ="flink"
                //className={classes.block}
                target="_blank"
              >
                Citizen Survey
              </a><br />  
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
        
        <p              >
                <h5 className={classes.header} style={{fontSize: "1rem", marginTop: "-5px"}}><b>User Manuals</b></h5>
              </p>       
              <a
                href="https://urban.digit.org/products/modules/property-tax"
                //className={classes.block}
                target="_blank"
                id ="flink"

              >
                Property Tax
              </a><br />
              <a
                href="https://urban.digit.org/products/modules/trade-license-tl"
                //className={classes.block}
                target="_blank"
                id ="flink"

              >
                Trade Licence
              </a><br />
              <a
                href="https://urban.digit.org/products/modules/water-and-sewerage"
                //className={classes.block}
                target="_blank"
                id ="flink"

              >
                Water & Sewerage
              </a><br />
              <a
                href="https://urban.digit.org/products/modules/public-grievances-and-redressal"
                //className={classes.block}
                target="_blank"
                id ="flink"

              >
                Public Grievances & Redressal
              </a><br />
               <a
                href="https://urban.digit.org/products/modules/fire-noc"
                //className={classes.block}
                target="_blank"
                id ="flink"

              >
               Fire NOC
              </a><br />
              <a
                href={AIStrategy}
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              AI Strategy
              </a> <br />
              <a
                href={BlockchainStrategy}
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              Block Chain Strategy
              </a> <br />
              <a
                href={Emerging}
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              Emerging Technologies
              </a> <br />
              <a
                href="https://www.youtube.com/@eSewaPunjabDOLGPunjab"
                //className={classes.block}
                target="_blank"
                id ="flink"

              >
                mSewa YouTube Channel
              </a><br />
                 
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
        
        <p>
                <h5 className={classes.header} style={{fontSize: "1rem", marginTop: "-5px"}}><b>About Us</b></h5>
              </p>              
              <a
                href="#"
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              About mSewa
              </a><br />
              
              <a
                href=" http://egov.org.in/"
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              About eGov Foundation
              </a> <br />
              <a
                href="https://mseva.lgpunjab.gov.in/citizen/language-selection"
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              What's New
              </a> <br />
              <a
                href="https://lgpunjab.gov.in/cms/transparency-act.php"
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              Service Delivery Charter
              </a> <br />
              
              <a
                href="https://punjab.gov.in/wp-content/uploads/2022/12/Holidays-List-2023-Pbi-and-Eng_0001.pdf"
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              Holiday List
              </a> <br />
              <a
                href={privacy}
                //className={classes.block}
                target="_blank"
                id ="flink"
              >
              Data Security Policy
              </a> <br />
              
            </Grid>

      </Grid><br />
      <div className='infofoot'>
      <center style={{color:"#ffffff"}}>Information provided online is update and no physical visit is required</center> <br />    
      {/* <center style={{color:"#ffffff"}}>Last Updated July 2021</center> <br />     */}
      <center style={{color:"#ffffff"}}>Number of Visitor 1224{counterContainer}</center> <br /> 
      <center style={{color:"#ffffff"}}><span style={{color:"red"}}>*</span>Supported browser versions</center> <br />    
      <center >
     
        <table >
      <tr >
          <td style={{color:"#ffffff", width: "100px"}}><center> <img src={chrome}  alt='mSewa Punjab'   width='60px'/></center></td>
          {/* <td style={{color:"#ffffff", width: "100px"}}> <center><img src={edge}   /></center></td> */}
          <td style={{color:"#ffffff", width: "100px"}}> <center><img src={mozilla} alt='mSewa Punjab' width='30px'  /></center></td>
          </tr>
        <tr >
          <td style={{color:"#ffffff", width: "100px"}}><center> &gt;V-81</center></td>
          {/* <td style={{color:"#ffffff", width: "100px"}}> <center>&gt;V-84</center></td> */}
          <td style={{color:"#ffffff", width: "100px"}}> <center>&gt;V-79</center></td>
          </tr>
          
          </table>     </center>
          </div>
          <div class="footerchange">
          <div class="container">
          <div className="row ">
      
      <div className="col-sm-12 col-md-6">
        <p>© {currentyear} PMIDC, GOVERNMENT OF PUNJAB. All Rights Reserved by PMIDC</p>
        </div>
        <div className="col-sm-12 col-md-6 lupwz">
        <p>Last updated on : 01-02-2024 | <img src={wz} alt='mSewa Punjab' className="wz"/> <img src={gigw} alt='mSewa Punjab' className="wz"/></p>
        </div>
        </div>
        </div>
        </div>
    </div>
    
  );
}
