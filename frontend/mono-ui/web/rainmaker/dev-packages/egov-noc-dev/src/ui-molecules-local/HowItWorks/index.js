import React from "react";
import { withStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";

const styles = theme => ({
  root: {
    margin: "16px 8px",
    backgroundColor: theme.palette.background.paper
  }
});

class HowItWorks extends React.Component {
  
  handleClick = () => {

    let url = window.location.href;
    let fUrl = url.replace ("fire-noc/home" ,"fire-noc/how-it-works");
    window.location.href = fUrl;     
  
  };
handleClick1 = () => {

    let url = window.location.href;
    let fUrl = url.replace ("fire-noc/home" ,"fire-noc/nbc-part4");
    window.location.href = fUrl;     
  
  };
  handleClick2 = () => {

    let url = window.location.href;
    let fUrl = url.replace ("fire-noc/home" ,"fire-noc/noc-app-form");
    window.location.href = fUrl;     
  
  };

  handleClick3 = () => {

    let url = window.location.href;
    let fUrl = url.replace ("fire-noc/home" ,"fire-noc/pfs-questionnaire");
    window.location.href = fUrl;     
  
  };

  handleClick4 = () => {

    let url = window.location.href;
    let fUrl = url.replace ("fire-noc/home" ,"fire-noc/pa-form");
    window.location.href = fUrl;     
  
  };
  handleClick5 = () => {

    let url = window.location.href;
    let fUrl = url.replace ("fire-noc/home" ,"fire-noc/pfb-rules1977");
    window.location.href = fUrl;     
  
  };

  handleClick6 = () => {

    let url = window.location.href;
    let fUrl = url.replace ("fire-noc/home" ,"fire-noc/pfp-act2004");
    window.location.href = fUrl;     
  
  };

  handleClick7 = () => {

    let url = window.location.href;
    let fUrl = url.replace ("fire-noc/home" ,"fire-noc/pfp-act2005");
    window.location.href = fUrl;     
  
  };

  render() {
    const { classes } = this.props;
    return [(
      <div className={classes.root}>
        <List component="nav">
          <ListItem button>
            <ListItemText onClick={this.handleClick}
            primary="How It Works?" />
          </ListItem>
        </List>
      </div>
    ),
     (
      <div className={classes.root}>
        <List component="nav">
          <ListItem button>
            <ListItemText onClick={this.handleClick1}
            primary="NBC Part 4" />
          </ListItem>
        </List>
      </div>
    ),
    (
      <div className={classes.root}>
        <List component="nav">
          <ListItem button>
            <ListItemText onClick={this.handleClick2}
            primary="NOC Application Form for Fire" />
          </ListItem>
        </List>
      </div>
    ),
    (
      <div className={classes.root}>
        <List component="nav">
          <ListItem button>
            <ListItemText onClick={this.handleClick3}
            primary="Owner's Checklist/ Questionnaire" />
          </ListItem>
        </List>
      </div>
    ),
    (
      <div className={classes.root}>
        <List component="nav">
          <ListItem button>
            <ListItemText onClick={this.handleClick4}
            primary="Proforma Application Form" />
          </ListItem>
        </List>
      </div>
    ),
    (
      <div className={classes.root}>
        <List component="nav">
          <ListItem button>
            <ListItemText onClick={this.handleClick5}
            primary="The Punjab Fire Brigade Rules 1977" />
          </ListItem>
        </List>
      </div>
    ),
    (
      <div className={classes.root}>
        <List component="nav">
          <ListItem button>
            <ListItemText onClick={this.handleClick6}
            primary="The Punjab Fire Prevention and Fire Safety Act, 2004" />
          </ListItem>
        </List>
      </div>
    ),
    (
      <div className={classes.root}>
        <List component="nav">
          <ListItem button>
            <ListItemText onClick={this.handleClick7}
            primary="The Punjab Fire Prevention and Fire Safety Act, 2005" />
          </ListItem>
        </List>
      </div>
    )]
  }
}

export default withStyles(styles)(HowItWorks);
