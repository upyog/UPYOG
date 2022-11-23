import React from 'react'
import {BackButton, CommonInputWrapper, CommonUploadFiles,CommonSecondaryBackButton} from "@egovernments/digit-ui-react-components";

const FormsUi = () => {
  const tmpOptions = [
    { label: "pension 2021-22", value: "pension 2021-22" },
    { label: "pension 2023-23", value: "pension 2022-23" },
  ];
  return (
    <div>
        {/* <BackButton >Back</BackButton> */}
        <CommonSecondaryBackButton className="secondaryBackBtn"/>
        <div className='Form-Conatiner p-0 rowSections '>
          <div className='card-wrapper cardSectionBody'>
            <form>
              <h5>Register Birth</h5>
               <CommonInputWrapper label="First Name" placeholder="First Name" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Last Name" placeholder="Last Name" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Aadhar Name" placeholder="Aadhar Name" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Title" placeholder="Title" type="Dropdown" selectOptions={tmpOptions} onChange="" value="" fullWidth />
               <CommonInputWrapper label="Email" placeholder="Email" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Mobile Number" placeholder="Mobile Number" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Date of Birth" placeholder="Date of Birth" type="DatePicker" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Fathers First Name" placeholder="Fathers First Name" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Fathers Last Name" placeholder="Fathers Last Name" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Mothers First Name" placeholder="Mothers First Name" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Mothers Last Name" placeholder="Mothers Last Name" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Category" placeholder="Category" type="Dropdown" selectOptions={tmpOptions} onChange="" value="" fullWidth />
               <CommonInputWrapper label="Bank Account No" placeholder="Bank Account No" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="TenantID" placeholder="TenantID" type="Dropdown" selectOptions={tmpOptions} onChange="" value="" fullWidth />
               <h5>Address</h5>
               <CommonInputWrapper label="House No" placeholder="House No" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="House Name" placeholder="House Name" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Steet" placeholder="Steet" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="PostOffice" placeholder="PostOffice" type="Dropdown" selectOptions={tmpOptions}  onChange="" value="" fullWidth />
               <CommonInputWrapper label="Pincode" placeholder="Pincode" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="ResAssociation Number" placeholder="ResAssociation Number" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Local Place" placeholder="Local Place" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Main place" placeholder="Main place" type="TextInput" onChange="" value="" fullWidth />
               <CommonInputWrapper label="Ward No" placeholder="Ward No" type="Dropdown" selectOptions={tmpOptions} onChange="" value="" fullWidth />
               <CommonUploadFiles title="Attach Documents " />
               <h5>Service Details</h5>
               <CommonInputWrapper label="Application Form Details" placeholder="Application Form Details" type="TextArea" onChange="" value="" fullWidth />
               <CommonUploadFiles title="Application Forms" />
                <div className='Common_terms_checkbox'>
                 <div className='input-checkbox'>
                  <input className="" type="checkbox"/>
                  <label>I hereby declare that all the details are valid</label>
                  </div> 
                </div>
                <div className='Common_submitBtn'>
                    <div className=''>
                      <hr className='m-v-15' style={{borderTop:" 1px solid #b3b3b3"}}></hr>
                    </div> 
                    <div className='C_submit-btn'> 
                      <button className='btn btn-primary m-t-10'>Submit</button>
                    </div>
                </div>
            </form> 
          </div>
           
        </div>
    </div>
  )
}

export default FormsUi