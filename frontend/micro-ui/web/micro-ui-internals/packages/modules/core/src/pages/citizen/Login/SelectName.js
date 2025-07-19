import React,{useState,useEffect} from "react";
import { TextArea ,ButtonSelector,Card,CardHeader,TextInput,UploadFile,Dropdown} from "@demodigit/digit-ui-react-components";
import Tesseract from 'tesseract.js';
import { parse } from 'mrz';
const SelectName = ({ config, onSelect, t, isDisabled }) => {
  const [mrzResult, setMrzResult] = useState(null);

  const [isProcessing, setIsProcessing] = useState(false);
  const [formValues, setFormValues] = useState({
    username: '',
    password: '',
    name: '',
    address: '',
    email: '',
    mobileNumber: '',
    passportNumber: '',
    gender:"",
    confirmPassword:"",
    dob:"",
    passsportFront: null,
    passportBack:null
  });
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);
  const [disabled, setDisabled] = useState(true);

  const [passwordError, setPasswordError] = useState('');
  const isMobile = window.Digit.Utils.browser.isMobile();
  const steps = ['Passport Details', 'Personal Details', 'Account Details'];
  const [currentStep, setCurrentStep] = useState(0);
const [isActive, setIsActive] = useState(false);
const [isActiveA, setIsActiveA] = useState(false);
const [isActiveP, setIsActiveP] = useState(false);
  const nextStep = () => setCurrentStep(prev => Math.min(prev + 1, steps.length - 1));
  const prevStep = () => setCurrentStep(prev => Math.max(prev - 1, 0));

  const handleImageUpload = async (event) => {

    const file = event.target.files[0];
 
    if (file) {
      setIsProcessing(true);
      const reader = new FileReader();
      reader.onload = async () => {
        const base64Image = reader.result;
       
          const { data: { text } } = await Tesseract.recognize(base64Image, 'eng', {
            logger: m => console.log(m),
          });
          console.log('MRZ texttext:',text);
          // Clean and process the text to extract possible MRZ lines
          const lines = text.split('\n').map(line => line.trim()).filter(line => line.length > 0);
          console.log("lines",lines)
          // Test the function
          const correctedMRZLines = correctLastTwoMRZLines(lines);
          console.log("correctedMRZLines",correctedMRZLines);
          const mrzLines = lines.filter(line => isValidMrzLine(line));
          console.log("mrzLines",mrzLines)
          // Join the MRZ lines to form the MRZ string
          const mrzString = correctedMRZLines.join('\n');
          console.log('MRZ String:', mrzString,text);

          const result = parse(mrzString);
          console.log("resultresult",result)
          const dob = formatDOB(result?.fields?.birthDate)
          const fullName = result?.fields?.firstName + result?.fields?.lastName
          const passport = result?.fields?.documentNumber
          const mobileNo = result?.fields?.personalNumber
          const gender = {key:result?.fields?.sex?.charAt(0).toUpperCase() + result?.fields?.sex?.slice(1).toLowerCase(), value:result?.fields?.sex?.charAt(0).toUpperCase() + result?.fields?.sex?.slice(1).toLowerCase()}
          setFormValues({
            ...formValues,
            name: fullName,
            mobileNumber: mobileNo,
            dob : dob,
            passportNumber: passport,
            passsportFront:file,
            gender
          });

          if (result.valid) {
            setMrzResult(result);
          } else {
            setError('Invalid MRZ');
          }
        
      };
      reader.readAsDataURL(file);
    }
  };
  const correctMRZLine = (mrzLine) =>{
    // Pad the line with '<' if it's shorter than 44 characters
    if (mrzLine.length < 44) {
        mrzLine = mrzLine.padEnd(44, '<');
    }
    
    // If it's longer than 44 characters, truncate it to 44 characters
    if (mrzLine.length > 44) {
        mrzLine = mrzLine.slice(0, 44);
    }

    // Identify the type of MRZ line (1st or 2nd) based on the initial character
    if (mrzLine.startsWith('P<')) {
        // Correct the 1st line
        const parts = mrzLine.split('<');
        const documentType = parts[0];
        const issuingCountry = parts[1];
        const surname = parts[2];
        const givenNames = parts.slice(3).join('<').replace(/<+$/, ''); // Join and remove trailing '<'
    
        // Construct the corrected MRZ line
        let correctedLine = `${documentType}<${issuingCountry}${surname}<<${givenNames}`;
        console.log("beforeeeee",correctedLine,mrzLine)
        const correctedLineNew = replaceAfterNthOccurrence(mrzLine, '<', 4);
        // Ensure no multiple '<' together
        // correctedLine = correctedLine.replace(/<+/g, '<');
        console.log("afterrrr",correctedLineNew)
        // Pad with '<' to make the length 44
        if (correctedLineNew.length < 44) {
          correctedLineNew = correctedLineNew.padEnd(44, '<');
        } else if (correctedLineNew.length > 44) {
          correctedLineNew = correctedLineNew.slice(0, 44);
        }
    
        return correctedLineNew;
    } else {
        // Correct the 2nd line
        const lastDigitIndex = mrzLine.search(/\d(?=[^0-9]*$)/); // Find the last digit
        let correctedLine = mrzLine.slice(0, lastDigitIndex + 1);
        
        // Pad with '<' to make the length 44
        if (correctedLine.length < 44) {
            correctedLine = correctedLine.padEnd(44, '<');
        } else if (correctedLine.length > 44) {
            correctedLine = correctedLine.slice(0, 44);
        }
        
        return correctedLine;
    }
}
const replaceAfterNthOccurrence = (mrzLine, character, n) =>{
  let occurrenceCount = 0;
  let newLine = '';

  for (let i = 0; i < mrzLine.length; i++) {
      if (mrzLine[i] === character) {
          occurrenceCount++;
      }
      if (occurrenceCount > n) {
          newLine += '<';
      } else {
          newLine += mrzLine[i];
      }
  }

  // Ensure the line is 44 characters long
  if (newLine.length < 44) {
      newLine = newLine.padEnd(44, '<');
  } else if (newLine.length > 44) {
      newLine = newLine.slice(0, 44);
  }

  return newLine;
}
const correctLastTwoMRZLines = (array) =>{
    // Extract the last two rows from the array
    const lastTwoRows = array.slice(-2);
    
    // Correct the last two MRZ lines
    const correctedLines = lastTwoRows.map(line => correctMRZLine(line));
    console.log("lastTwoRowslastTwoRows",lastTwoRows,correctedLines)

    return correctedLines;
}
const handlePassportBack = async (event) => {
  const file = event.target.files[0];

  if (file) {
    setIsProcessing(true);
    const reader = new FileReader();
    reader.onload = async () => {
      const base64Image = reader.result;
     
        const { data: { text } } = await Tesseract.recognize(base64Image, 'eng', {
          logger: m => console.log(m),
        });
        console.log('MRZ texttext:',text);
        // Clean and process the text to extract possible MRZ lines
        const lines = text.split('\n').map(line => line.trim()).filter(line => line.length > 0);
        console.log("lines",lines)
      const address = extractAddress(lines)
      console.log("addressaddress",address)
      setFormValues({
        ...formValues,
        "address": address,
        passportBack : file
      });
      console.log("address",address)

    };
    reader.readAsDataURL(file);
  }
};

 const extractAddress = (array) => {
  const startPatterns = [
    /\bHOUSE\b|\bAPP\b|\bBLDG\b|\bSTREET\b|\bROAD\b|\bCHOWK\b|\bSECTOR\b/i, // Look for common address keywords
  ];
  const endPatterns = [
    /\bPIN:\d{6}\b|\bINDIA\b/i, // Look for common ending keywords including "INDIA"
  ];

  let address = [];
  let addressFound = false;
  let indiaIndex = -1;

  for (let line of array) {
    // Check if the line matches any of the start patterns
    if (!addressFound && startPatterns.some((pattern) => pattern.test(line))) {
      addressFound = true;
    }

    // If address start is found, collect the lines
    if (addressFound) {
      address.push(line.trim());

      // Check if the line matches any of the end patterns
      if (endPatterns.some((pattern) => pattern.test(line))) {
        // Capture the index of "INDIA"
        indiaIndex = line.indexOf('INDIA');
        if (indiaIndex !== -1) {
          break; // Stop processing further lines once "INDIA" is found
        }
      }
    }
  }

  // Remove unwanted characters and clean up lines
  address = address.map((line) =>
    line.replace(/\|/g, '').replace(/[\u2013\u2014]/g, '').trim()
  );

  // Join address parts with commas
  let finalAddress = address.join(', ');

  // Ensure we stop at "INDIA" correctly
  if (indiaIndex !== -1) {
    finalAddress = finalAddress.substring(0, finalAddress.indexOf('INDIA') + 5); // +5 to include "INDIA"
  }

  return finalAddress;
};

 const isValidMrzLine = (line) => {
  const validLengths = [30, 36, 44];
  return /^(?=.*[A-Z])(?=.*<)[A-Z<\d]+$/.test(line);
};
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({
      ...formValues,
      [name]: value,
    });
    console.log("formValueswwww",formValues)
    if(formValues?.passsportFront && formValues?.passportBack && formValues?.visa && formValues?.passportNumber)
    {
      console.log("formValues12",formValues)
      setIsActive(true)
    }
    else if(formValues?.name && formValues?.address && formValues?.gender && formValues?.dob && formValues?.email && formValues?.mobileNumber)
    {
      console.log("formValues12",formValues)
      setIsActiveP(true)
    }
    else if( formValues?.username && formValues?.password && formValues?.confirmPassword)
    {
      setIsActiveA(true)
    }
    console.log("valuevalue",value,name)
    if (name === 'confirmPassword') {
      if (formValues.password !== value) {
        setPasswordError('Passwords do not match');
        setDisabled(true);
      } else {
        setPasswordError('');
        setDisabled(isAnyFieldNonEmpty());
      }
    }
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formValues.file && formValues.file.size > 5000000) {
      setError(t("FILE_SIZE_ERROR"));
      return;
    }
    setError(null);
    console.log('Form submitted:', formValues);
    onSelect(formValues);
  };

  function selectfile(e) {
    setFormValues({
      ...formValues,
      ["visa"]: e.target.files[0],
    });
    if(formValues?.passsportFront && formValues?.passportBack && formValues?.visa && formValues?.passportNumber)
    {
      console.log("formValues12",formValues)
      setIsActive(true)
    }
    // formValues.visa
    // setFile(e.target.files[0]);
  }
  // useEffect(() => {
  //   // Wait until window.MrzWorker is defined
  //   if (window.mrz_worker) {
  //     // Example usage
  //     console.log("window.mrz_worker",window.mrz_worker)
  //     const worker = new window.mrz_worker();
  //     worker.doSomething(); // Replace with actual methods from the library
  //   } else {
  //     console.error('MrzWorker is not available yet.');
  //   }
  // }, []);
  const handleChangeGender=(e)=>{
console.log("handleChangeGender",e.target)
setFormValues({
  ...formValues,
  ["gender"]: e.target,
});
  }
  const formatDOB = (inputDOB) =>{
    // Assuming inputDOB is in the format "YYMMDD"
    if (inputDOB.length !== 6) {
        throw new Error("Invalid date of birth format. Expected YYMMDD.");
    }

    // Extract year, month, and day from the input string
    const year = inputDOB.substring(0, 2);
    const month = inputDOB.substring(2, 4);
    const day = inputDOB.substring(4, 6);

    // Format into "DD/MM/YYYY"
    const formattedDOB = `${"19" + year}-${month}-${day}`; // Assuming it's 19YY for years between 1900 and 1999

    return formattedDOB;
}
  useEffect(() => {
    setDisabled(isAnyFieldNonEmpty())
    const data = isAnyFieldNonEmpty()
    console.log("isAnyFieldNonEmpty", isAnyFieldNonEmpty())
  }, [formValues])
const isAnyFieldNonEmpty = () => {
  for (const key in formValues) {
    if (formValues[key] == '') {
      console.log("formValues[key]",formValues[key])
      return true;
    }
  }
  return false;
};
let addStyle = {};
if (Digit.UserService.getType() === "citizen") {
  addStyle = { maxWidth: "540px" };
}
const Gender=[{
  key:"Male",
  value:"Male"
},
{
  key:"Female",
  value:"Female"
}]
  return (
    <React.Fragment>
            <style>
        {
          `.loginHomePage{

          }
          .card .card-header {
            font-size : 28px
          }
          .container {
            width: 25%;
          }
          
          .step {
            
            padding: 10px;
            
            display: flex;
            flex-direction: row;
            justify-content: flex-start;
            
            background-color: cream;
          }
          
          .v-stepper {
            position: relative;
          /*   visibility: visible; */
          }
          
          
          /* regular step */
          .step .circle {
            background-color: white;
            border: 3px solid gray;
            border-radius: 100%;
            width: 30px;    /* +6 for border */
            height: 30px;
            display: inline-block;
          }
          
          .step .line {
              top: 23px;
            left: 14px;
          /*   height: 120px; */
            height: 100%;
              
              position: absolute;
              border-left: 3px solid gray;
          }
          
          .step.completed .circle {
            visibility: visible;
            background-color: rgb(6,150,215);
            border-color: rgb(6,150,215);
          }
          
          .step.completed .line {
            border-left: 3px solid rgb(6,150,215);
          }
          
          .step.active .circle {
          visibility: visible;
            border-color: rgb(6,150,215);
          }
          
          .step.empty .circle {
              
          }
          
          .step.empty .line {
          /*     visibility: hidden; */
          /*   height: 150%; */
            top: 0;
            height: 150%;
          }
          
          
          .step:last-child .line {
            border-left: 3px solid white;
            z-index: -1; /* behind the circle to completely hide */
          }
          
          .content {
            margin-left: 20px;
            display: inline-block;
            font-weight:bold
          }
          
          html *
          {
             font-size: 18px !important;
             color: #000 !important;
             font-family: Arial !important;
          }
          .upload-file
          {
            border-radius:10px
          }
          .selector-button-border
          {
            width:30% !important;
            border-radius:10px
          }
          .employee-card-input
          {
            border-radius:15px;

          }
          .card .card-header
          {
            display:flex;
            fontWeight:bold
          }
          .card-header {
            background-color:white !important;
            padding:0px
          }
          .text-input{
            margin-top:10px
          }
          .select-wrap .select{
            border-radius:15px
          }
          `
        }
      </style>
            <div className="loginHomePage">
        <div style={{display:"flex",  justifyContent:isMobile?"":"center"}}>
         <Card style={{width:isMobile?"":"100%",maxWidth:isMobile?"":"100%"}}>

       
        <div style={{display:"flex",flexDirection:"row"}}>
        <div style={{position:"fixed",width:"20%", height:"100%",display:isMobile?"none":"flex", flexDirection:"column",alignItems:"center",justifyContent:"center"}}>
        <div className={`step ${isActive ? 'completed' : 'active'}`} style={{height:"25%"}}>
      <div class="v-stepper" >
        <div class="circle"></div>
        <div class="line"></div>
        <div class="content">
        Passport Details
      </div>
      </div>
      </div>
      <div className={`step ${isActiveP ? 'completed' : 'active'}`}  style={{height:"25%"}}>
    <div class="v-stepper">
      <div class="circle"></div>
      <div class="line"></div>
      <div class="content">
        Personal Details
      </div>
    </div>
    </div>
    <div className={`step ${isActiveA ? 'completed' : 'active'}`} style={{height:"33%"}}>
      <div class="v-stepper">
        <div class="circle"></div>
        <div class="line"></div>
        <div class="content">
        Account Details
      </div>
      </div>
      </div>
        </div>
        <div style={{width:isMobile?"100%":"80%", height:"100%",marginLeft:isMobile?"":"20%"}}>

        <div style={{ marginTop:"10px",flexDirection:"row",flexWrap:"wrap",padding:"2%",boxShadow: "rgba(0, 0, 0, 0.35) 0px 5px 15px"}}>
<div>
<CardHeader gutterBottom style={{display:"flex", fontWeight:"bold"}}> 
<div style={{display:"flex", flexDirection:"column"}}>
  <span>
         {t("Passport Details")}
         </span>
         <span style={{fontWeight:"200"}}>{t("Please upload passport and visa to autofill information")}</span>
         </div>
        </CardHeader>
      
</div>
<div style={{display:"flex",flexDirection:"row",flexWrap:"wrap"}}>
      <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>
          <label>{t("Passport_Front")}</label>
          <UploadFile
            id="tl-doc"
            extraStyleName="propertyCreate"
            accept=".jpg,.png,.pdf,.jpeg"
            onUpload={handleImageUpload}
            onDelete={() => setUploadedFile(null)}
            message={formValues.passsportFront ? `1 ${t("TL_ACTION_FILEUPLOADED")}` : t("TL_ACTION_NO_FILEUPLOADED")}
            error={error}
            style={{maxWidth:"100%",borderRadius:"10px"}}
          />
        </div>
      
          <div style={{width:isMobile?"100%":"45%", marginLeft:isMobile?"":"10px",borderRadius:"10px"}}>
          <label>{t("Passport_Back")}</label>
          <UploadFile
            id="tl-doc"
            extraStyleName="propertyCreate"
            accept=".jpg,.png,.pdf,.jpeg"
            onUpload={handlePassportBack}
            onDelete={() => setUploadedFile(null)}
            message={formValues.visa ? `1 ${t("TL_ACTION_FILEUPLOADED")}` : t("TL_ACTION_NO_FILEUPLOADED")}
            error={error}
          />
          </div>
          <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>
          <label>{t("Visa")}</label>
          <UploadFile
            id="tl-doc"
            extraStyleName="propertyCreate"
            accept=".jpg,.png,.pdf,.jpeg"
            onUpload={selectfile}
            onDelete={() => setUploadedFile(null)}
            message={formValues.visa ? `1 ${t("TL_ACTION_FILEUPLOADED")}` : t("TL_ACTION_NO_FILEUPLOADED")}
            error={error}
            style={{maxWidth:"100%",borderRadius:"10px"}}
          />
        </div>
          <div style={{width:isMobile?"100%":"45%",borderRadius:"10px",marginLeft:isMobile?"":"10px"}}>
        <label>{t("Passport_No")}</label>
          <TextInput
            required
            id="passportNumber"
            name="passportNumber"
            label="Passport Number"
            variant="outlined"
            value={formValues.passportNumber}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          </div>
          </div>
          </div>
          <div style={{marginTop:"10px",flexDirection:"row",flexWrap:"wrap",padding:"2%",boxShadow: "rgba(0, 0, 0, 0.35) 0px 5px 15px"}}>
          <div>
<CardHeader gutterBottom style={{display:"flex", fontWeight:"bold"}}> 
{t("Personal Details")}
        </CardHeader>
</div>
<div style={{display:"flex",flexDirection:"row",flexWrap:"wrap"}}>
            <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>
              <div>
            <label>{t("Full_Name")}</label>
          <TextInput
            required
            id="name"
            name="name"
            label="Name"
            variant="outlined"
            value={formValues.name}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          </div>
          <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>
                   <label>{t("Date_Of_Birth")}</label>
           <div style={{ position: "relative", width: "100%", cursor: "pointer", ...addStyle }}>
      <React.Fragment>
        <input
          className={`employee-card-input disabled"}`}
          style={{ width: "calc(100%-62px)" }}
          name="dob"
          value={formValues.dob}
          type="date"
          onChange={handleChange}
          defaultValue={""}
          required={true}
        />
      </React.Fragment>
    </div>
    <div style={{width:"100%",borderRadius:"10px"}}>
            <label>{t("Email_Id")}</label>
          <TextInput
            required
            id="email"
            name="email"
            label="Email"
            type="email"
            variant="outlined"
            value={formValues.email}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          </div>
    </div>
    <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>
            <label>{t("Mobile_No")}</label>
          <TextInput
            required
            id="mobileNumber"
            name="mobileNumber"
            label="Phone"
            variant="outlined"
            value={formValues.mobileNumber}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          </div>
          </div>
          <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>

          <div style={{width:"100%",borderRadius:"10px",marginLeft:isMobile?"":"10px"}}>
               <label>{t("Gender")}</label>
          {/* <TextInput
            required
            id="gender"
            name="gender"
            label="Gender"
            variant="outlined"
            value={formValues.gender}
            onChange={handleChange}
            fullWidth
            margin="normal"
          /> */}
           <Dropdown option={Gender} optionKey={"key"} value={formValues?.gender} selected={formValues?.gender} select={handleChangeGender} style={{borderRadius:"15px"}}t={t} />
          </div>
          <div style={{width:"100%",borderRadius:"10px",height:"100%",marginLeft:isMobile?"":"10px"}}>
            <label>{t("Address")}</label>
          <TextArea
            required
            id="address"
            name="address"
            label="Address"
            variant="outlined"
            value={formValues.address}
            onChange={handleChange}
            fullWidth
            style={{height:isMobile?"":"50%"}}
            margin="normal"
          />
          </div>
          </div>
          </div>
          </div>
          <div style={{marginTop:"10px",flexDirection:"row",flexWrap:"wrap",padding:"2%",boxShadow: "rgba(0, 0, 0, 0.35) 0px 5px 15px"}}>
          <div>
<CardHeader gutterBottom style={{display:"flex", fontWeight:"bold"}}> 
{t("Account Details")}
        </CardHeader>
</div>
<div style={{display:"flex",flexDirection:"row",flexWrap:"wrap"}}>
          <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>
              <label>{t("User_Name")}</label>
          <TextInput
            required
            id="username"
            name="username"
            label="Username"
            variant="outlined"
            value={formValues.username}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          </div>
          <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>
          <label>{t("Password")}</label>
          <TextInput
            required
            id="password"
            name="password"
            label="Password"
            type="password"
            variant="outlined"
            value={formValues.password}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          <div>
     
          </div>
          </div>

          <div style={{width:isMobile?"100%":"45%",borderRadius:"10px"}}>
          <label>{t("Confirm Password")}</label>
          <TextInput
            required
            id="password"
            name="confirmPassword"
            label="confirmPassword"
            type="password"
            variant="outlined"
            value={formValues.confirmPassword}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          </div>
</div>
             </div>
          {passwordError && <p>{passwordError}</p>}
          <div style={{display:"flex",flexDirection:"row-reverse"}}>
          <ButtonSelector label="Register" onSubmit={handleSubmit} style={{ marginLeft: "0px" ,marginTop:"20px" , width:isMobile?"100%":"20%", height:"2.5rem", borderRadius:"10px", color:"white !important"}} isDisabled ={disabled}/>
          </div>
          </div>
          </div>

    </Card>
    </div>
    </div>
    </React.Fragment>
  );
};

export default SelectName;
