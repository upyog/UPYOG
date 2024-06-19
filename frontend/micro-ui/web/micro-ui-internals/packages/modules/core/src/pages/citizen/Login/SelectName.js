import React,{useState} from "react";
import { FormStep ,ButtonSelector,Card,CardHeader,TextInput,UploadFile} from "@upyog/digit-ui-react-components";

const SelectName = ({ config, onSelect, t, isDisabled }) => {

  const [formValues, setFormValues] = useState({
    username: '',
    password: '',
    name: '',
    address: '',
    email: '',
    phone: '',
    passportNumber: '',
    file: null
  });
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);


  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({
      ...formValues,
      [name]: value,
    });
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

  const selectFile = (e) => {
    setUploadedFile(e.target.files[0]);
  };
  return (
    <React.Fragment>
         <Card>

        <CardHeader variant="h4" component="h1" gutterBottom>
          Registration Form
        </CardHeader>
          <label>User Name</label>
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
          <label>Password</label>
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
          <label>Full Name</label>
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
          <label>Address</label>
          <TextInput
            required
            id="address"
            name="address"
            label="Address"
            variant="outlined"
            value={formValues.address}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          <label>Email Id</label>
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
          <label>Mobile No</label>
          <TextInput
            required
            id="phone"
            name="phone"
            label="Phone"
            variant="outlined"
            value={formValues.phone}
            onChange={handleChange}
            fullWidth
            margin="normal"
          />
          <label>Passport No</label>
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
          <UploadFile
            id="tl-doc"
            extraStyleName="propertyCreate"
            accept=".jpg,.png,.pdf,.jpeg"
            onUpload={selectFile}
            onDelete={() => setUploadedFile(null)}
            message={uploadedFile ? `1 ${t("TL_ACTION_FILEUPLOADED")}` : t("TL_ACTION_NO_FILEUPLOADED")}
            error={error}
          />
          <ButtonSelector label="Register" onSubmit={handleSubmit} style={{ marginLeft: "0px" ,marginTop:"10px", width:"200px" }} />


    </Card>
    </React.Fragment>
  );
};

export default SelectName;
