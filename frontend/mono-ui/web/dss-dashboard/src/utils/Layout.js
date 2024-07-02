import React, { Component } from 'react';
import { withStyles } from '@material-ui/core/styles';
import AppRouter from './AppRouter';
import Spinner from '../components/common/Spinner';
// import SideBar from '../components/common/sidebar'
import { isMobile } from 'react-device-detect';
import { isNurtDashboard } from './commons';

const styles = (theme) => ({
    root: {
        display: 'flex',
        flex: 1,
        flexDirection: 'column',
        padding: isMobile ? '5px' : '10px'
      },
      appContainer: {
        display: 'flex',
    
      },
      main: {
        display: 'flex',
        flex: 1
      }
});

class Layout extends Component {
    prepareLayout() {
        const { classes } = this.props;
        let sourceUrl = `${window.location.origin}/citizen`;
            sourceUrl="https://s3.ap-south-1.amazonaws.com/egov-qa-assets";

        const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf"
        return (
            <div className={`App ${classes.root}`}>
                {/* <div>
                    Change Language: <select onChange={this.handleLanguageChange}>
                        <option value="en">En- English</option>
                        <option value="hi">hi- Hindi</option>
                    </select>
                </div> */}
                {/* <NavBar /> */}
                <div className={classes.appContainer}>
                    {/* <div className="row"> */}
                    <Spinner />
                    <main role="main" 
                    // style={{ backgroundColor:isNurtDashboard()? "#EEEEEE":'#f4f7fb' }} 
                    style={{ backgroundColor:"#EEEEEE" }} 
                    className={classes.main}>
                    {/* <SideBar /> */}
                        <AppRouter />
                    </main>
                </div>
                <div style={{ width: '100%', bottom: 0 }}>
                    <div style={{ display: 'flex', justifyContent: 'center', color:"#22394d" }}>
                        {/* <img style={{ cursor: "pointer", display: "inline-flex", height: '1.4em' }} alt={"Powered by DIGIT"} src={`${sourceUrl}/digit-footer.png`} onError={"this.src='./../digit-footer.png'"} onClick={() => {
                        window.open('https://www.digit.org/', '_blank').focus();
                        }}></img> */}
                        {/* <span style={{ margin: "0 10px" }}>|</span> */}
                        <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
                        <span style={{ margin: "0 10px" }}>|</span>
                        <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open(pdfUrl, '_blank').focus();}}>UPYOG License</span>
                    </div>
                </div>
            </div>
        )

    }

    render() {
        return this.prepareLayout();
    }

}

export default withStyles(styles)(Layout);