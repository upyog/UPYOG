import React from "react";
import "./myindex.css";
import 'bootstrap/dist/css/bootstrap.min.css';
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
import { withStyles } from "@material-ui/core/styles";
import axios from "axios";
import rightImage from "../../img/pmidc - final logo.png";
import leftImage from "../../img/Govt. of Punjab.png";
import slideone from '../../img/pmidcgurughar.jpg';
import homeicon from '../../img/homepage-icon.png';

const styles = (theme) => ({
  root: {
    paddingTop: "0px",
    overflow: "visible",
    height: "100%",
  },
  btn: {
    padding: "2.5% 5% ",
    width: "20%",
    borderRadius: 4,
    fontSize: "15px",
    background: "#EA784E",
    color: "white",
    textDecoration: "none",
    textAlign: "center",
    // marginTop: "42px",
  },
  text: {
    padding: "12px",
    fontSize: "22px",
    fontWeight: "680",
    lineHeight: 2,
  },
  listStyle: {},
});

class Header extends React.Component {
  state = {
    backgroundUrl: "",
    logoUrl: "",
  };

  componentDidMount() {
    let tenantIdFromPath = "";
    tenantIdFromPath = document.location.search
      ? document.location.search.split("=")[1]
      : "pb";
    console.log(tenantIdFromPath, "tenant");
    const RequestInfo = {
      RequestInfo: {
        apiId: "Rainmaker",
        ver: ".01",
        ts: "",
        action: "_search",
        did: "1",
        key: "",
        msgId: "20170310130900|en_IN",
        authToken: null,
      },
      MdmsCriteria: {
        tenantId: tenantIdFromPath,
        moduleDetails: [
          {
            moduleName: "common-masters",
            masterDetails: [
              {
                name: "StateInfo",
              },
            ],
          },
        ],
      },
    };
    axios
      .post(
        `${document.location.origin}/egov-mdms-service/v1/_search?tenantId=${tenantIdFromPath}`,
        RequestInfo
      )
      .then((response) => {
        console.log("ResponseInfo", response.data);
        this.setValuestoState("tenantId", tenantIdFromPath);
        this.setValuestoState(
          "backgroundUrl",
          response.data.MdmsRes["common-masters"].StateInfo[0].bannerUrl
        );
        this.setValuestoState(
          "logoUrl",
          response.data.MdmsRes["common-masters"].StateInfo[0].logoUrl
        );
      })
      .catch((err) => {
        console.log(err, "error");
      });
  }
  setValuestoState = (property, value) => {
    this.setState({
      [property]: value,
    });
  };
  render() {
    const { classNamees } = this.props;
    

    return (
    <div>
     <div>
     <div className="hearderchange">
      <div class="container">
      <div className="row">
      
            <div className="col-sm-12 col-md-6">
              <a href="#"><img src={homeicon} width="25px"/></a>
              </div>
              <div className="col-sm-12 col-md-6">
                <div className="langu"><a href="#">English</a>  / <a href="#"> ਪੰਜਾਬੀ</a></div>
                <div className="boldText">
                 
                  
             <a href="#">-A</a>
             <a href="#">A</a>
             <a href="#">+A</a>
             </div>
              </div>
              </div>
              </div>
              
              </div>
        <div className="row">
            <div className="col-sm-12 col-md-6">
                <div className="msewa-logo-postion">
                <img className="msewalogo" src={leftImage} alt="PMIDC" />
            </div>
            </div>
            <div className="col-sm-12 col-md-6">
                <div className="rightharder">
                <a href="https://mseva.lgpunjab.gov.in/employee/language-selection" target="_blank">Employee Login </a><img id="rightimg" src={rightImage} />
                </div>
            </div>
            </div>
            {/* <div class="row">
              <div class="col-sm-12 col-md-12">
                <marquee id="note">Dear Citizen , One Time settlement for Property Tax has been implemented in mSeva. You requested check and Re-Assess your property before paying tax .</marquee>
              </div>
            </div> */}
            <div className="row">
            <div className="col-sm-12 col-md-12">
            <img class="d-block w-100" src={slideone} alt="PMIDC" />
              </div>
              </div>

          </div>
    </div>
      
    );
  }
}
export default withStyles(styles)(Header);
