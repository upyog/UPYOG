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

class TDTradeApply extends Component {
  listItems = [
    {
      question: "Trade License",
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
      <p>Trade license issued by the respective ULB's to gives permission to the applicant (person seeking to open a business) to commence a particular trade or business in a particular area/location.</p>
<h5>Please use the bellow links to avail the services</h5>
</div>
      </div>
      <div className="row">
      <div className="col-sm-4 btnAplly" >
           <a
           className="btnWS"
            href={
              "https://enaksha.lgpunjab.gov.in/"
            }
            target="_blank"
            >
            <Button
              label={
                <Label
                  buttonLabel={true}
                  label="Apply Trade license"
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
              "https://firenoc.lgpunjab.gov.in/pg_Citizen_Login.aspx"
            }
            target="_blank"
          >
            <Button
              label={
                <Label
                  buttonLabel={true}
                  label="Track Application"
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
              "https://firenoc.lgpunjab.gov.in/pg_Citizen_Login.aspx"
            }
            target="_blank"
          >
            <Button
              label={
                <Label
                  buttonLabel={true}
                  label="Pay Applcation Fees"
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
         <h5>FACILITIES AVAILABLE:</h5>
         <ol>
          <li>Online tracking of the status of the application</li>
<li>Status update through SMS and Email</li>
<l1>Download & Print the submitted Application copy, Receipts & Sanction Order.</l1>
<li>Generation of Trade License & notification through SMS and Email</li>
<li>Payment of Trade Licens though online/offline mode</li>
         </ol>
        </div>

      </div>
      </div>
    )
  }

  
  render() {
    const { renderList, listItems } = this;
    const { urls, history } = this.props;
    return (
      <Screen className="screen-with-bredcrumb">
        <BreadCrumbs url={urls} history={history} label="How to apply & pay Trade License"/>
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
)(TDTradeApply);
