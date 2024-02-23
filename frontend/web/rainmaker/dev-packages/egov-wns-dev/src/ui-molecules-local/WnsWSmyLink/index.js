import React, { Component } from "react";
import { Card, Button } from "components";
import Screen from "egov-ui-kit/common/common/Screen";
import Label from "egov-ui-kit/utils/translationNode";
import { List, ListItem } from "material-ui/List";
import { connect } from "react-redux";
import { addBreadCrumbs } from "egov-ui-kit/redux/app/actions";
// import "../PTHome/index.css";
import "./index.css";
import BreadCrumbs from "../../ui-atoms-local/BreadCrumbs/index"

const genericInnerdivStyle = {
  paddingLeft: 0
};

const videoCardStyle = {
  minHeight: 270
};

class WnsWSmyLink extends Component {
  listItems = [
    {
      question: "WATER & SEWERAGE",
      answer: [{ 
        text: ` DESCRIPTION: Water & Sewerage Management System provides a digital interface, allowing citizen to apply for a water & sewerage connection, track the applications, subsequently make the payment online for connection, download the payment receipt and sanction order. Application also generated bill for water and sewerage charges.For towns other than MC Fazilka, Please use the bellow links to avail the services
        Apply New Connection: http://lgpunjab.gov.in/cms/apply-new-connection.php<br/>
        Pay Water Bill: http://lgpunjab.gov.in/cms/pay-water-bill.php<br/>
        Pay Sewerage Bill: http://lgpunjab.gov.in/cms/pay-sewerage-bill.php<br/>
        STEP INVOLVED: 
        1. Apply for Water &amp; Sewerage or only Water or Sewerage with relevant data and
        applicable documents 
        2. Pay the Application Fee (if Applicable) 
        3. Application will be verified, inspected and approved by MC officials. 
        4. On approval of the application, pay the Water &amp; Sewerage /
        Water/Sewerage connection fees &amp; Download the application, Payment
        Receipt &amp; Sanction Order. 
        FACILITIES AVAILABLE: 
        1. Online tracking of the status of the application 
        2. Status update through SMS and Email 
        3. Download &amp; Print the submitted Application copy, Receipts &amp; Sanction Order. 
        4. Generation of Water / Sewerage Bill &amp; notification through SMS and Email 
        5. Payment of Water / Sewerage Bill though online/offline mode`
      }]
    },
    {
      question: "CS_HOWITWORKS_QUESTION2",
      answer: [
        {
          text: "CS_HOWITWORKS_ANSWER2"
        }
      ]
    },
    {
      question: "CS_HOWITWORKS_QUESTION3",
      answer: [
        {
          text: "CS_HOWITWORKS_ANSWER3"
        }
      ]
    },
    {
      question: "CS_HOWITWORKS_QUESTION4",
      answer: [
        {
          text: "CS_HOWITWORKS_ANSWER4"
        }
      ]
    },
    {
      question: "CS_HOWITWORKS_QUESTION5",
      answer: [
        {
          text: "CS_HOWITWORKS_ANSWER5"
        }
      ]
    },
    {
      question: "CS_HOWITWORKS_QUESTION6",
      answer: [{ text: "CS_HOWITWORKS_ANSWER6" }]
    },
    {
      question: "CS_HOWITWORKS_QUESTION7",
      answer: [{ text: "CS_HOWITWORKS_ANSWER7" }]
    },
    {
      question: "CS_HOWITWORKS_QUESTION8",
      answer: [
        {
          text: "CS_HOWITWORKS_ANSWER8"
        }
      ]
    },
    {
      question: "CS_HOWITWORKS_QUESTION9",
      answer: [{ text: "CS_HOWITWORKS_ANSWER9" }]
    },
    {
      question: "CS_HOWITWORKS_QUESTION20",
      answer: [
        {
          text: "CS_HOWITWORKS_ANSWER10"
        }
      ]
    },
    {
      question: "CS_HOWITWORKS_QUESTION11",
      answer: [
        {
          text: "CS_HOWITWORKS_ANSWER11"
        }
      ]
    },
    {
      question: "CS_HOWITWORKS_QUESTION12",
      answer: [{ text: "CS_HOWITWORKS_ANSWER12" }]
    }
  ];

  componentDidMount() {
    const { addBreadCrumbs, title } = this.props;
    title && addBreadCrumbs({ title: title, path: window.location.pathname });
  }
  renderList = items => {
    return(<div>
      <div className="row">
        <div className="col-sm-12">
      <h4>DESCRIPTION:</h4>
      <p>Water & Sewerage Management System provides a digital interface, allowing
citizen to apply for a water & sewerage connection, track the applications,
subsequently make the payment online for connection, download the payment
receipt and sanction order. Application also generated bill for water and sewerage
charges.</p>
<h5>For towns other than MC Fazilka, Please use the bellow links to avail the services</h5>
</div>
      </div>
      <div className="row">
      <div className="col-sm-4 btnAplly" >
           <a
           className="btnWS"
            href={
              "http://lgpunjab.gov.in/cms/apply-new-connection.php"
            }
            target="_blank"
            >
            <Button
              label={
                <Label
                  buttonLabel={true}
                  label="Apply New Connection"
                  fontSize="12px"
                />
              }
              primary={true}
              style={{ height: 30, lineHeight: "auto", minWidth: "inherit" }}
            />
          </a>
        </div>
        <div className="col-sm-4 btnAplly" >
           <a className="btnWS"
            href={
              "http://lgpunjab.gov.in/cms/pay-water-bill.php"
            }
            target="_blank"
          >
            <Button
              label={
                <Label
                  buttonLabel={true}
                  label="Pay Water Bill"
                  fontSize="12px"
                />
              }
              primary={true}
              style={{ height: 30, lineHeight: "auto", minWidth: "inherit" }}
            />
          </a>
        </div>
        <div className="col-sm-4 btnAplly" >
           <a className="btnWS"
            href={
              "http://lgpunjab.gov.in/cms/pay-sewerage-bill.php"
            }
            target="_blank"
          >
            <Button
              label={
                <Label
                  buttonLabel={true}
                  label="Pay Sewerage Bill"
                  fontSize="12px"
                />
              }
              primary={true}
              style={{ height: 30, lineHeight: "auto", minWidth: "inherit" }}
            />
          </a>
        </div>
      </div>
      <div className="row">
        <div className="col-sm-12">
         <h5>STEP INVOLVED:</h5>
         <ol>
          <li>Apply for Water & Sewerage or only Water or Sewerage with relevant data and
applicable documents</li>
<li>Pay the Application Fee (if Applicable)</li>
<l1>Application will be verified, inspected and approved by MC officials.</l1>
<li>On approval of the application, pay the Water & Sewerage /
Water/Sewerage connection fees & Download the application, Payment
Receipt &Sanction Order.</li>
         </ol>
        </div>

      </div>
      <div className="row">
        <div className="col-sm-12">
         <h5>FACILITIES AVAILABLE:</h5>
         <ol>
          <li>Online tracking of the status of the application</li>
<li>Status update through SMS and Email</li>
<l1>Download & Print the submitted Application copy, Receipts & Sanction Order.</l1>
<li>Generation of Water / Sewerage Bill & notification through SMS and Email</li>
<li>Payment of Water / Sewerage Bill though online/offline mode</li>
         </ol>
        </div>

      </div>
      </div>
    )
  }

  // renderList = items => {
  //   return (
  //     <div>
  //       {/* <div className="row">
  //         <div style={{ padding: "15px" }}>
  //           <Label
  //             label="CS_HOWITWORKS_HELP_VIDEOS_PUNJABI"
  //             color="#484848"
  //             fontSize="20px"
  //           />
  //         </div>
  //         <div className="col-sm-4" style={videoCardStyle}>
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/5GpLiCYS584?rel=0"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_TAX_PAYMENT" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_TAX_PAYMENT_DESCRIPTION" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4" style={videoCardStyle}>
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/P9U3EGNxrKU?rel=0"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS__PARTIAL_PAY" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS__PARTIAL_PAY_DISCRIPTION" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4" style={videoCardStyle}>
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/PKHSa33puxQ?rel=0"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_ASSESSMENTS" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_ASSESSMENTS_DISCRIPTION" />
  //           </p>
  //         </div>

  //         <div className="col-sm-4" style={videoCardStyle}>
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/uF_G9dk_GBY?rel=0"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_ASSESSMENTS_INCOMPLETE" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_ASSESSMENTS_INCOMPLETE_DISCRIPTION" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4" style={videoCardStyle}>
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/8V1k-v93BRg?rel=0"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_FULL_PAY" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_FULL_PAY_DISCRIPTION" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4" style={videoCardStyle}>
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/gw7bS_-7aM8?rel=0"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_PARTIAL_PAYMENT" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_PARTIAL_PAYMENT_DISCRIPTION" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4" style={videoCardStyle}>
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/fVRd6ylStdY?rel=0"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_ASS" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_ASS_DISCRIPTION" />
  //           </p>
  //         </div>
  //       </div> 

  //       <div className="row" style={{ paddingTop: "10px" }}>
  //         <div style={{ padding: "15px" }}>
  //           <Label
  //             label="CS_HOWITWORKS_HELP_VIDEOS_ENGLISH"
  //             color="#484848"
  //             fontSize="20px"
  //           />
  //         </div>
  //         <div className="col-sm-4">
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/E0g26AzwRvs"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_HOMEPG_REG" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_HOMEPG_REG_DISCRIPTION" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4">
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/G2_EA0zTiM0"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_FLOOR_UNIT" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_FLOOR_UNIT_DISCRIPTION" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4">
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/UbmY5LmdiQc"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_ASS_PAY" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_ASS_PAY_DISCRIPTION" />
  //           </p>
  //         </div>

  //         <div className="col-sm-4">
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/r6k7_J7jkYc"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_FULL_PAYMENT1" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_FULL_PAYMENT1_DISCRIPTION" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4">
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/oQu4qDNWP7I"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_PARTIAL1_PAY" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_EXPLAIN" />
  //           </p>
  //         </div>
  //         <div className="col-sm-4">
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/3s6GtEWmf00"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_COMPLETE_ASS" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_COMPLETE_ASS_VIDEO" />
  //           </p>
  //         </div>

  //         <div className="col-sm-4">
  //           <iframe
  //             allowFullScreen="allowFullScreen"
  //             frameBorder="0"
  //             src="https://www.youtube.com/embed/mKLsORPO1o8"
  //           />
  //           <h4>
  //             <Label label="CS_HOWITWORKS_PROPERTY_INCOMP_ASS" />
  //           </h4>
  //           <p>
  //             <Label label="CS_HOWITWORKS_PROPERTY_INCOMP_ASS_VIDEO" />
  //           </p>
  //         </div>
  //       </div>*/}

  //       <div className="col-sm-12" style={{ padding: "15px 0px 30px 0px" }}>
  //         <a
  //           href={
  //             "https://s3.ap-south-1.amazonaws.com/pb-egov-assets/pb/PT_User_Manual_Citizen.pdf"
  //           }
  //           target="_blank"
  //         >
  //           <Button
  //             label={
  //               <Label
  //                 buttonLabel={true}
  //                 label="PT_DOWNLOAD_HELP_DOCUMENT"
  //                 fontSize="12px"
  //               />
  //             }
  //             primary={true}
  //             style={{ height: 30, lineHeight: "auto", minWidth: "inherit" }}
  //           />
  //         </a>
  //       </div>
  //       <div className="col-sm-12" style={{ padding: "15px 0px 30px 0px" }}>
  //         <a
  //           href={
  //             "https://s3.ap-south-1.amazonaws.com/pb-egov-assets/pb/PT_User_Manual_Citizen.pdf"
  //           }
  //           target="_blank"
  //         >
  //           <Button
  //             label={
  //               <Label
  //                 buttonLabel={true}
  //                 label="PT_DOWNLOAD_HELP_DOCUMENT"
  //                 fontSize="12px"
  //               />
  //             }
  //             primary={true}
  //             style={{ height: 30, lineHeight: "auto", minWidth: "inherit" }}
  //           />
  //         </a>
  //       </div>
  //       <div className="col-sm-12" style={{ padding: "15px 0px 30px 0px" }}>
  //         <a
  //           href={
  //             "https://s3.ap-south-1.amazonaws.com/pb-egov-assets/pb/PT_User_Manual_Citizen.pdf"
  //           }
  //           target="_blank"
  //         >
  //           <Button
  //             label={
  //               <Label
  //                 buttonLabel={true}
  //                 label="PT_DOWNLOAD_HELP_DOCUMENT"
  //                 fontSize="12px"
  //               />
  //             }
  //             primary={true}
  //             style={{ height: 30, lineHeight: "auto", minWidth: "inherit" }}
  //           />
  //         </a>
  //       </div>
  //       <div className="col-sm-12" style={{ padding: "15px 0px 30px 0px" }}>
  //         <a
  //           href={
  //             "https://s3.ap-south-1.amazonaws.com/pb-egov-assets/pb/PT_User_Manual_Citizen.pdf"
  //           }
  //           target="_blank"
  //         >
  //           <Button
  //             label={
  //               <Label
  //                 buttonLabel={true}
  //                 label="PT_DOWNLOAD_HELP_DOCUMENT"
  //                 fontSize="12px"
  //               />
  //             }
  //             primary={true}
  //             style={{ height: 30, lineHeight: "auto", minWidth: "inherit" }}
  //           />
  //         </a>
  //       </div>
  //       <div>
  //         <Label label="PT_FAQ" color="#484848" fontSize="20px" />
  //       </div>

  //       <hr />

  //       {/* <List style={{ padding: 0 }}>
  //         {items.map((item, index) => {
  //           return (
  //             <ListItem
  //               innerDivStyle={
  //                 index !== 0
  //                   ? {
  //                       ...genericInnerdivStyle,
  //                       borderTop: "solid 1px #e0e0e0"
  //                     }
  //                   : genericInnerdivStyle
  //               }
  //               nestedListStyle={{ padding: "0 0 16px 0" }}
  //               primaryText={
  //                 <Label dark={true} label={item.question} fontSize={16} />
  //               }
  //               nestedItems={item.answer.map(nestedItem => {
  //                 return (
  //                   <ListItem
  //                     hoverColor="#fff"
  //                     primaryText={
  //                       <Label fontSize={16} label={nestedItem.text} />
  //                     }
  //                     innerDivStyle={{ padding: 0 }}
  //                   />
  //                 );
  //               })}
  //               primaryTogglesNestedList={true}
  //               hoverColor="#fff"
  //             />
  //           );
  //         })}
  //       </List> */}
  //     </div>
  //   );
  // };

  render() {
    const { renderList, listItems } = this;
    const { urls, history } = this.props;
    return (
      <Screen className="screen-with-bredcrumb">
        <BreadCrumbs url={urls} history={history} label="How to apply Water & Sewerage"/>
        <div className="form-without-button-cont-generic">
          <Card
            className="how-it-works-card"
            textChildren={renderList(listItems)}
          />
        </div>
      </Screen>
    );
  }
}

const mapStateToProps = state => {
  const { common, app } = state;
  const { urls } = app;
  return { urls };
};

const mapDispatchToProps = dispatch => {
  return {
    addBreadCrumbs: url => dispatch(addBreadCrumbs(url))
  };
};

const acc = document.getElementsByClassName("accordion");
function demo(){
alert("test");
}

for (let i=0 ; i < acc.length; i++) {
  acc[i].addEventListener("click", function() {
    this.classList.toggle("active");
    const panel = this.nextElementSibling;
    if (panel.style.display === "block") {
      panel.style.display = "none";
    } else {
      panel.style.display = "block";
    }
  });
}
export default connect(
  mapStateToProps,
  mapDispatchToProps
)(WnsWSmyLink);
