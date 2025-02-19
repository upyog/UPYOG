import Grid from "@material-ui/core/Grid";
import Icon from "@material-ui/core/Icon";
import { withStyles } from "@material-ui/core/styles";
import { AutosuggestContainer, LabelContainer } from "egov-ui-framework/ui-containers";
import LoadingIndicator from "egov-ui-framework/ui-molecules/LoadingIndicator";
import { prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getFileUrlFromAPI, getTransformedLocale, handleFileUpload } from "egov-ui-framework/ui-utils/commons";
import Label from "egov-ui-kit/utils/translationNode";
import get from "lodash/get";
import PropTypes from "prop-types";
import React, { Component } from "react";
import { connect } from "react-redux";
import UploadSingleFile from "../UploadSingleFile";

const themeStyles = theme => ({
  documentContainer: {
    backgroundColor: "#F2F2F2",
    padding: "16px",
    marginTop: "10px",
    marginBottom: "16px"
  },
  documentCard: {
    backgroundColor: "#F2F2F2",
    padding: "16px",
    marginTop: "10px",
    marginBottom: "16px"
  },
  documentSubCard: {
    backgroundColor: "#F2F2F2",
    padding: "16px",
    marginTop: "10px",
    marginBottom: "10px",
    border: "#d6d6d6",
    borderStyle: "solid",
    borderWidth: "1px"
  },
  documentIcon: {
    backgroundColor: "#FFFFFF",
    borderRadius: "100%",
    width: "36px",
    height: "36px",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    color: "rgba(0, 0, 0, 0.8700000047683716)",
    fontFamily: "Roboto",
    fontSize: "20px",
    fontWeight: 400,
    letterSpacing: "0.83px",
    lineHeight: "24px"
  },
  documentSuccess: {
    borderRadius: "100%",
    width: "36px",
    height: "36px",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#39CB74",
    color: "white"
  },
  button: {
    margin: theme.spacing.unit,
    padding: "8px 38px"
  },
  input: {
    display: "none"
  },
  iconDiv: {
    display: "flex",
    alignItems: "center"
  },
  descriptionDiv: {
    display: "flex",
    alignItems: "center"
  },
  formControl: {
    minWidth: 250,
    padding: "0px"
  },
  fileUploadDiv: {
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-end",
    paddingTop: "5px"
  }
});

const styles = {
  documentTitle: {
    color: "rgba(0, 0, 0, 0.87)",
    fontFamily: "Roboto",
    fontSize: "16px",
    fontWeight: 500,
    letterSpacing: "0.67px",
    lineHeight: "19px",
    paddingBottom: "5px"
  },
  documentName: {
    color: "rgba(0, 0, 0, 0.87)",
    fontFamily: "Roboto",
    fontSize: "16px",
    fontWeight: 400,
    letterSpacing: "0.67px",
    lineHeight: "19px"
  },
  dropdownLabel: {
    color: "rgba(0, 0, 0, 0.54)",
    fontSize: "12px"
  },
  containerTitle: {
    color: "rgba(0, 0, 0, 0.7)",
    fontFamily: "Roboto",
    fontWeight: 500,
    letterSpacing: "0.83px",
    lineHeight: "24px",
    textAlign: "left"
  },
  containerSubTitle: {
    color: "rgb(0, 0, 0, 0.6)",
    fontFamily: "Roboto",
    fontWeight: 400,
    lineHeight: "20px",
    textAlign: "left"
  }
};

const requiredIcon = (
  <sup style={{ color: "#E54D42", paddingLeft: "5px" }}>*</sup>
);

class DocumentList extends Component {
  state = {
    uploadedDocIndex: 0,
    docUploaded: false,
    fileUploadingStatus: null
  };
  initDocumentData() {
    const {
      ptDocumentsList,
      documentsUploadRedux = {},
      prepareFinalObject
    } = this.props;
    let index = 0;
    let docsUploaded = {};
    ptDocumentsList.forEach(docType => {
      docType.cards &&
        docType.cards.forEach(card => {
          if (card.subCards) {
            card.subCards.forEach(subCard => {
              let oldDocType = get(
                documentsUploadRedux,
                `[${index}].documentType`
              );
              let oldDocCode = get(
                documentsUploadRedux,
                `[${index}].documentCode`
              );
              let oldDocSubCode = get(
                documentsUploadRedux,
                `[${index}].documentSubCode`
              );
              if (
                oldDocType != docType.code ||
                oldDocCode != card.name ||
                oldDocSubCode != subCard.name
              ) {
                docsUploaded[index] = {
                  documentType: docType.code,
                  documentCode: card.name,
                  documentSubCode: subCard.name
                };

              }
              index++;
            });
          } else {
            let oldDocType = get(
              documentsUploadRedux,
              `[${index}].documentType`
            );
            let oldDocCode = get(
              documentsUploadRedux,
              `[${index}].documentCode`
            );
            if (oldDocType != docType.code || oldDocCode != card.name) {
              docsUploaded[index] = {
                documentType: docType.code,
                documentCode: card.name,
                isDocumentRequired: card.required,
                isDocumentTypeRequired: card.dropdown
                  ? card.dropdown.required
                  : false
              };
              if (card.dropdown && card.dropdown.value) {
                docsUploaded[index]['dropdown'] = {}
                docsUploaded[index]['dropdown']['value'] = card.dropdown.value;
              }
            }
            if (card.dropdown && card.dropdown.value) {
              docsUploaded[index] = docsUploaded[index] ? docsUploaded[index] : {};
              docsUploaded[index]['dropdown'] = docsUploaded[index]['dropdown'] ? docsUploaded[index]['dropdown'] : {};
              docsUploaded[index]['dropdown']['value'] = card.dropdown.value;
              docsUploaded[index]['documentType'] = docType.code;
              docsUploaded[index]['documentCode'] = card.name;
              docsUploaded[index]['isDocumentRequired'] = card.required;
              docsUploaded[index]['isDocumentTypeRequired'] = card.dropdown
                ? card.dropdown.required
                : false
            }
            index++;
          }
        });
    });
    if (documentsUploadRedux && Object.keys(documentsUploadRedux) && Object.keys(documentsUploadRedux).length) {
      if (!this.state.docUploaded) {
        prepareFinalObject("documentsUploadRedux", this.getDocumentsUploaded(documentsUploadRedux, docsUploaded));
        this.setState({ docUploaded: true });
      }

    } else {
      prepareFinalObject("documentsUploadRedux", docsUploaded);
    }
  }


  showLoading = () => {
    this.setState({ fileUploadingStatus: "uploading" });
  }
  hideLoading = () => {
    this.setState({ fileUploadingStatus: null });
  }

  getDocumentsUploaded = (documentsUploadRedux, docsUploaded) => {
    let docObj = {};
    docObj = { ...documentsUploadRedux, ...docsUploaded };
    if (Object.keys(docsUploaded).length > 0) {
      Object.keys(docObj).map(key => {
        Object.keys(documentsUploadRedux).map(docKey => {
          if (docObj[key] && docObj[key].documentCode && documentsUploadRedux[docKey] && documentsUploadRedux[docKey].dropdown && documentsUploadRedux[docKey].dropdown.value && documentsUploadRedux[docKey].dropdown.value.indexOf(docObj[key].documentCode) > -1) {
            docObj[key].documents = documentsUploadRedux[docKey].documents;
            docObj[key].dropdown = documentsUploadRedux[docKey].dropdown;
          }
        })
      })
      return docObj;
    }
    return docObj;
  }

  componentDidMount = () => {
    this.initDocumentData()
  };
  componentDidUpdate(prevProps) {
    // Typical usage (don't forget to compare props):
    if (this.props.ptDocumentsList !== prevProps.ptDocumentsList) {
      this.initDocumentData()
    }
  }
  onUploadClick = uploadedDocIndex => {
    this.setState({ uploadedDocIndex });
  };

  handleDocument = async (file, fileStoreId) => {
    let { uploadedDocIndex } = this.state;
    const { prepareFinalObject, documentsUploadRedux } = this.props;
    const fileUrl = await getFileUrlFromAPI(fileStoreId);

    prepareFinalObject("documentsUploadRedux", {
      ...documentsUploadRedux,
      [uploadedDocIndex]: {
        ...documentsUploadRedux[uploadedDocIndex],
        documents: [
          {
            fileName: file.name,
            fileStoreId,
            fileUrl: Object.values(fileUrl)[0]
          }
        ]
      }
    });
    this.hideLoading();
  };

  removeDocument = remDocIndex => {
    const { prepareFinalObject } = this.props;
    prepareFinalObject(
      `documentsUploadRedux.${remDocIndex}.documents`,
      undefined
    );
    this.forceUpdate();
  };

  handleChange = (key, event) => {
    const { documentsUploadRedux, prepareFinalObject } = this.props;
    prepareFinalObject(`documentsUploadRedux`, {
      ...documentsUploadRedux,
      [key]: {
        ...documentsUploadRedux[key],
        dropdown: { value: event.target.value }
      }
    });
  };

  getUploadCard = (card, key) => {
    const { classes, documentsUploadRedux } = this.props;
    let jsonPath = `documentsUploadRedux[${key}].dropdown.value`;
    return (
      <Grid container={true}>
        <Grid item={true} xs={2} sm={1} className={classes.iconDiv}>
          {documentsUploadRedux[key] && documentsUploadRedux[key].documents ? (
            <div className={classes.documentSuccess}>
              <Icon>
                <i class="material-icons">done</i>
              </Icon>
            </div>
          ) : (
            <div className={classes.documentIcon}>
              <span>{key + 1}</span>
            </div>
          )}
        </Grid>
        <Grid
          item={true}
          xs={10}
          sm={5}
          md={4}
          align="left"
          className={classes.descriptionDiv}
        >
          <LabelContainer
            labelKey={getTransformedLocale(card.name)}
            style={styles.documentName}
          />
          {card.required && requiredIcon}
        </Grid>
        <Grid item={true} xs={12} sm={6} md={4}>
          {card.dropdown && (
            <AutosuggestContainer
              select={true}
              label={{ labelKey: getTransformedLocale(card.dropdown.label) }}
              placeholder={{ labelKey: card.dropdown.label }}
              data={card.dropdown.menu}
              disabled={card.dropdown.disabled && documentsUploadRedux[key] && documentsUploadRedux[key].documents ? true : false}
              optionValue="code"
              optionLabel="label"
              required={card.required}
              onChange={event => this.handleChange(key, event)}
              jsonPath={jsonPath}
              className="autocomplete-dropdown"
              labelsFromLocalisation={true}
            />
          )}
        </Grid>
        <Grid
          item={true}
          xs={12}
          sm={12}
          md={3}
          className={classes.fileUploadDiv}
        >
          <UploadSingleFile
            id={`jk-document-id-${key}`}
            classes={this.props.classes}
            handleFileUpload={e =>
              handleFileUpload(e, this.handleDocument, this.props, this.showLoading, this.hideLoading)
                        }
            uploaded={
              documentsUploadRedux[key] && documentsUploadRedux[key].documents
                ? true
                : false
            }
            removeDocument={() => this.removeDocument(key)}
            documents={
              documentsUploadRedux[key] && documentsUploadRedux[key].documents
            }
            onButtonClick={() => this.onUploadClick(key)}
            inputProps={this.props.inputProps}
            buttonLabel={this.props.buttonLabel}
            disabled={card.disabled && documentsUploadRedux[key] && documentsUploadRedux[key].documents ? true : false}
          />
        </Grid>
      </Grid>
    );
  };

  render() {
    const { classes, ptDocumentsList } = this.props;
    let index = 0;
    const { fileUploadingStatus } = this.state;
    return (
      <div>
        {fileUploadingStatus == "uploading" &&
          <div><LoadingIndicator></LoadingIndicator>
          </div>}
        {ptDocumentsList &&
          ptDocumentsList.map(container => {
            return (
              <div>
                <Label fontSize="20px"
                  label={"PT_REQUIRED_DOCUMENTS"}
                  labelStyle={styles.containerTitle}
                />
                <Label fontSize="14px"
                  label="PT_REQUIRED_DOC_SUB_HEADING"
                  labelStyle={styles.containerSubTitle}
                />
                {container.cards.map(card => {
                  return (
                    <div className={classes.documentContainer}>
                      {card.hasSubCards && (
                        <LabelContainer
                          labelKey={card.name}
                          style={styles.documentTitle}
                        />
                      )}
                      {card.hasSubCards &&
                        card.subCards.map(subCard => {
                          return (
                            <div className={classes.documentSubCard}>
                              {this.getUploadCard(subCard, index++)}
                            </div>
                          );
                        })}
                      {!card.hasSubCards && (
                        <div>{this.getUploadCard(card, index++)}</div>
                      )}
                    </div>
                  );
                })}
              </div>
            );
          })}
      </div>
    );
  }
}

DocumentList.propTypes = {
  classes: PropTypes.object.isRequired
};

const mapStateToProps = state => {
  const { screenConfiguration } = state;
  const { moduleName } = screenConfiguration;
  const documentsUploadRedux = get(
    screenConfiguration.preparedFinalObject,
    "documentsUploadRedux",
    {}
  );
  Object.keys(documentsUploadRedux).map(key => {
    let documentCode = documentsUploadRedux[key] && documentsUploadRedux[key].dropdown && documentsUploadRedux[key].dropdown.value || '';
    let codes = documentCode && documentCode.split('.');
    if (codes && codes.length == 1 && codes[0].length > 0) {
      documentsUploadRedux[key].dropdown.value = "OWNER.REGISTRATIONPROOF." + documentCode;
    }
  })
  return { documentsUploadRedux, moduleName };
};

const mapDispatchToProps = dispatch => {
  return {
    prepareFinalObject: (jsonPath, value) =>
      dispatch(prepareFinalObject(jsonPath, value))
  };
};

export default withStyles(themeStyles)(
  connect(
    mapStateToProps,
    mapDispatchToProps
  )(DocumentList)
);
