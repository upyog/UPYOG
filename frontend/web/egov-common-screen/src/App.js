import React from 'react';
import './App.css';
import { Grid } from '@material-ui/core';
//import LandingPage from './components/LandingPage';
import Header from './components/Header';
import Cards from './components/Cards';
import Faqs from './components/Faqs';
import Footer from './components/Footer';
import digitLogo from "./img/digit-footer.png";



function App() {
  return (
    
    <Grid className="app-grid">
   <div>
   <Header />
   <Cards />
  <center ><p  style ={{  fontFamily: "Roboto",
  fontStyle: "normal",
  fontWeight: "500",
  fontSize: "36px",
  lineHeight: "42px",
  textAlign: "center",
  color:"rgba(0, 0, 0, 0.87)",
  marginLeft: "10%",
  marginRight: "10%"
  }}>Frequently Asked Questions</p></center >
  <div  
              style={{marginLeft: "45%",
                marginRight: "45%", marginTop: "0%", borderBottom: "5px solid #f48952"}}>     
                </div> 
   <Faqs />
<Footer /> 
 </div>  
  </Grid>
  );
}
export default App;