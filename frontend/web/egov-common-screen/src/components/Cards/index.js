import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
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
import Propertyimg from'../../img/icons/1.png';
import Tradeimg from'../../img/icons/3.png';
import waterimg from'../../img/icons/5.png';
import Sewargeimg from'../../img/icons/7.png';
import Fireimg from'../../img/icons/9.png';
import Petimg from'../../img/icons/11.png';
import obps from'../../img/icons/13.png';
import pgr from'../../img/icons/1q.png';
import "./index.css";


const useStyles = makeStyles((theme) => ({
 
  card: 
  {
    textAlign: 'center'
  }

}));

export default function CenteredGrid() {
  const classes = useStyles();

  return (
   <div>
     
     <div class="secondpart">
     <div class="bodaypage">
     <div class="container">
      <div class="row features">
      <div class="col-sm-12 col-md-3">
          <div class="cardservice ">
          <a href='https://mseva.lgpunjab.gov.in/citizen' target="_new">
                <img src={Propertyimg} class="propertyimgone" alt='Punjab Property Tax'/>
                <h5 class="card-title">Property Tax</h5></a>
                            
            </div>
          </div>
          <div class="col-sm-12 col-md-3">
            <div class="cardservice ">
            <a href='https://mseva.lgpunjab.gov.in/citizen' target="_new">
                  <img src={Tradeimg} class="propertyimgone" alt='Punjab Trade license'/>                  
                  <h5 class="card-title">Trade license</h5></a>
                              
              </div>
            </div>  <div class="col-sm-12 col-md-3">
              <div class="cardservice ">
              <a href='https://mseva.lgpunjab.gov.in/citizen' target="_new">
                    <img src={waterimg} class="propertyimgone" alt='Punjab Water'/>
                    <h5 class="card-title">Water</h5></a>
                                
                </div>
              </div>  <div class="col-sm-12 col-md-3">
                <div class="cardservice ">
                <a href='https://mseva.lgpunjab.gov.in/citizen' target="_new">
                      <img src={Sewargeimg} class="propertyimgone" alt='Punjab Water'/>
                      <h5 class="card-title">Sewerage</h5></a>
                                  
                  </div>
                </div>
              </div>
              
              <div class="row featuresthree">
                <div class="col-sm-12 col-md-3">
                    <div class="cardservice ">
                    <a href='https://mseva.lgpunjab.gov.in/citizen' target="_new">
                          <img src={Fireimg} class="propertyimgone" alt='Punjab Fire Noc'/>
                          <h5 class="card-title">Fire Noc</h5></a>
                                      
                      </div>
                    </div>
                    <div class="col-sm-12 col-md-3">
                      <div class="cardservice ">
                      <a href='http://petlicense.punjab.gov.in:8080/pet-license/' target="_new">
                            <img src={Petimg} class="propertyimgone" alt='Punjab Pet license'/>
                            <h5 class="card-title">Pet license</h5></a>
                                        
                        </div>
                      </div> 
                      <div class="col-sm-12 col-md-3">
                        <div class="cardservice ">
                        <a href='https://enaksha.lgpunjab.gov.in/' target="_new">
                              <img src={obps} class="propertyimgone" alt='Punjab E-Naksha'/>
                              <h5 class="card-title">E-Naksha</h5></a>
                                          
                          </div>
                        </div> 
                        <div class="col-sm-12 col-md-3">
                          <div class="cardservice ">
                          <a href='https://mseva.lgpunjab.gov.in/citizen' target="_new">
                                <img src={pgr} class="propertyimgone" alt='Punjab Public Grievance'/>
                                <h5 class="card-title">Public Grievance</h5></a>
                                            
                            </div>
                          </div> 
                        </div>
                        </div>
                      </div>
                    </div>
   
   </div>
  );
}
