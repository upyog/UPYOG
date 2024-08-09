-- Need to Refer eg_user where ever eg_user is referred.

-- Create table if not exists for eg_bmc_Caste
CREATE TABLE IF NOT EXISTS eg_bmc_Caste (
    ID SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_Religion
CREATE TABLE IF NOT EXISTS eg_bmc_Religion (
    ID SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_Divyang
CREATE TABLE IF NOT EXISTS eg_bmc_Divyang (
    ID SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_FileUploaded
CREATE TABLE IF NOT EXISTS eg_bmc_FileUploaded (
    ID SERIAL PRIMARY KEY,
    userid bigint NOT NULL,
    tenantid VARCHAR(255) NOT NULL,
    FILEID VARCHAR(255) NOT NULL,
    URL VARCHAR(255) NOT NULL,
    Type VARCHAR(255) NOT NULL,
    CONSTRAINT fk_fileuploaded_eg_user FOREIGN KEY (userid, tenantid)
        REFERENCES eg_user (id, tenantid) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Create table if not exists for eg_bmc_WardMaster
CREATE TABLE IF NOT EXISTS eg_bmc_WardMaster (
    ID SERIAL PRIMARY KEY,
    CityName VARCHAR(255) NOT NULL,
    WardName VARCHAR(255) NOT NULL,
    Remark VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_ElectoralWardMaster
CREATE TABLE IF NOT EXISTS eg_bmc_ElectoralWardMaster (
    ID SERIAL PRIMARY KEY,
    CityName VARCHAR(255) NOT NULL,
    WardName VARCHAR(255) NOT NULL,
    ElectoralWardName VARCHAR(255) NOT NULL,
    Remark VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_BankMaster
CREATE TABLE IF NOT EXISTS eg_bmc_BankMaster (
    ID SERIAL PRIMARY KEY,
    BankName VARCHAR(255) NOT NULL,
    BankCode VARCHAR(255) NOT NULL,
    Remark VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_CourseMaster
CREATE TABLE IF NOT EXISTS eg_bmc_CourseMaster (
    ID SERIAL PRIMARY KEY,
    Sector VARCHAR(255) NOT NULL,
    CourseCode VARCHAR(255) NOT NULL UNIQUE,
    CourseName VARCHAR(255) NOT NULL,
    Qualification VARCHAR(255) NOT NULL,
    CourseDuration INT NOT NULL,
    TotalCost FLOAT NOT NULL,
    NSQFLevel INT NOT NULL,
    NQRCode VARCHAR(255),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_QualificationMaster
CREATE TABLE IF NOT EXISTS eg_bmc_QualificationMaster (
    ID SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_SectorMaster
CREATE TABLE IF NOT EXISTS eg_bmc_SectorMaster (
    ID SERIAL PRIMARY KEY,
    Sector VARCHAR(255) NOT NULL,
    Remark VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_Schemes
CREATE TABLE IF NOT EXISTS eg_bmc_Schemes (
    Id SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description VARCHAR(1000) NOT NULL,
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_Event
CREATE TABLE IF NOT EXISTS eg_bmc_Event (
    Id SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    StartDt BIGINT NOT NULL,
    EndDt BIGINT,
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

-- Create table if not exists for eg_bmc_SchemeEvent
CREATE TABLE IF NOT EXISTS eg_bmc_SchemeEvent (
    Id SERIAL PRIMARY KEY,
    SchemeID INT NOT NULL,
    EventID INT NOT NULL,
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255),
    CONSTRAINT fk_schemeevent_scheme FOREIGN KEY (SchemeID) REFERENCES eg_bmc_Schemes(Id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_schemeevent_event FOREIGN KEY (EventID) REFERENCES eg_bmc_Event(Id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Create table if not exists for eg_bmc_AadharUser
CREATE TABLE IF NOT EXISTS eg_bmc_AadharUser (
    id SERIAL PRIMARY KEY,
    AadharRef VARCHAR(12) NOT NULL,
    userid bigint NOT NULL,
    tenantid VARCHAR(255) NOT NULL,
    AadharFatherName VARCHAR(255) NOT NULL,
    AadharName VARCHAR(255) NOT NULL,
    AadharDOB DATE NOT NULL,
    AAdharMobile VARCHAR(15) NOT NULL,
    Gender VARCHAR(10) NOT NULL, -- Added Gender
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255),
    CONSTRAINT fk_aadharuser_eg_user FOREIGN KEY (userid, tenantid)
        REFERENCES eg_user (id, tenantid) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Create table if not exists for eg_bmc_Address
CREATE TABLE IF NOT EXISTS eg_bmc_Address (
    ID SERIAL PRIMARY KEY,
    userid bigint NOT NULL,
    tenantid VARCHAR(255) NOT NULL,
    Address1 VARCHAR(255) NOT NULL,
    Address2 VARCHAR(255),
    Location VARCHAR(255),
    Ward VARCHAR(255),
    City VARCHAR(255) NOT NULL,
    District VARCHAR(255) NOT NULL,
    State VARCHAR(255) NOT NULL,
    Country VARCHAR(255) NOT NULL,
    Pincode VARCHAR(10) NOT NULL,
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255),
    CONSTRAINT fk_address_eg_user FOREIGN KEY (userid, tenantid)
        REFERENCES eg_user (id, tenantid) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Create table if not exists for eg_bmc_UserOtherDetails
CREATE TABLE IF NOT EXISTS eg_bmc_UserOtherDetails (
    ID SERIAL PRIMARY KEY,
    userid bigint NOT NULL,
    tenantid VARCHAR(255) NOT NULL,
    CasteID INT,
    ReligionID INT,
    DivyangCardID VARCHAR(255),
    DivyangPercent FLOAT,
    TransgenderID VARCHAR(255),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255),
    DivyangID INT,
    rationCardCategory VARCHAR(255), -- Added rationCardCategory
    educationLevel VARCHAR(255), -- Added educationLevel
    udid VARCHAR(255), -- Added udid
    CONSTRAINT fk_userotherdetails_eg_user FOREIGN KEY (userid, tenantid)
        REFERENCES eg_user (id, tenantid) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_userotherdetails_cast FOREIGN KEY (CasteID) REFERENCES eg_bmc_Caste(ID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_userotherdetails_religion FOREIGN KEY (ReligionID) REFERENCES eg_bmc_Religion(ID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_userotherdetails_divyang FOREIGN KEY (DivyangID) REFERENCES eg_bmc_Divyang(ID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Create table if not exists for eg_bmc_UserSchemeApplication
CREATE TABLE IF NOT EXISTS eg_bmc_UserSchemeApplication (
    id SERIAL PRIMARY KEY,
    applicationNumber VARCHAR(64),
    userid bigint NOT NULL,
    tenantid VARCHAR(255) NOT NULL,
    optedId INT NOT NULL,
    ApplicationStatus BOOLEAN NOT NULL,
    VerificationStatus BOOLEAN NOT NULL,
    FirstApprovalStatus BOOLEAN NOT NULL,
    RandomSelection BOOLEAN NOT NULL,
    FinalApproval BOOLEAN NOT NULL,
    Submitted BOOLEAN NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255),
    CONSTRAINT fk_userschemeapplication_eg_user FOREIGN KEY (userid, tenantid)
        REFERENCES eg_user (id, tenantid) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Create table if not exists for eg_bmc_Courses
CREATE TABLE IF NOT EXISTS eg_bmc_Courses (
    ID SERIAL PRIMARY KEY,
    CourseName VARCHAR(255) NOT NULL,
    Description VARCHAR(1000),
    Duration VARCHAR(50),
    StartDt BIGINT NOT NULL,
    EndDt BIGINT,
    TypeID INT NOT NULL,
    URL VARCHAR(255),
    Institute VARCHAR(255) NOT NULL,
    ImgURL VARCHAR(255),
    InstituteAddress VARCHAR(255),
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255),
    Amount FLOAT
);

-- Create table if not exists for eg_bmc_SchemeCourse
CREATE TABLE IF NOT EXISTS eg_bmc_SchemeCourse (
    id SERIAL PRIMARY KEY,
    SchemeID INT NOT NULL,
    CourseID INT NOT NULL,
    GrantAmount FLOAT,
    CONSTRAINT fk_schemecourse_scheme FOREIGN KEY (SchemeID) REFERENCES eg_bmc_Schemes(Id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_schemecourse_course FOREIGN KEY (CourseID) REFERENCES eg_bmc_Courses(ID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Create table if not exists for eg_bmc_Machines
CREATE TABLE IF NOT EXISTS eg_bmc_Machines (
    ID SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description VARCHAR(1000),
    Amount FLOAT
);

-- Create table if not exists for eg_bmc_SchemeMachine
CREATE TABLE IF NOT EXISTS eg_bmc_SchemeMachine (
    id SERIAL PRIMARY KEY,
    MachineID INT NOT NULL,
    SchemeID INT NOT NULL,
    GrantAmount FLOAT,
    CONSTRAINT fk_schemachine_machine FOREIGN KEY (MachineID) REFERENCES eg_bmc_Machines(ID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_schemachine_scheme FOREIGN KEY (SchemeID) REFERENCES eg_bmc_Schemes(Id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
