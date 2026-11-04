# UPYOG Config-Driven Selenium Automation Framework

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.x-brightgreen)
![Selenium](https://img.shields.io/badge/Selenium-4.x-success)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)

Config-Driven | Workflow-Based | Excel-Driven | Selenium Grid | Docker | HTML Dashboard | Extent Reports | User Manual Generator


## Overview
The UPYOG Config-Driven Selenium Automation Framework is a scalable, metadata-driven automation framework developed to automate end-to-end workflows across multiple UPYOG modules. The framework executes tests using JSON-based configurations and Excel test-data sheets, eliminating hardcoded Selenium logic and enabling easy onboarding of new modules with minimal code changes. It also generates interactive, printable step-by-step User Manuals and Extent Reports with embedded screenshots.

## Features
- 100% JSON-driven automation framework
- Config-driven workflow execution
- Excel-driven multi-test case execution with module-specific test case IDs (`PET_001`, `PGR_001`, etc.)
- Stakeholder-based execution (Citizen, Employee, Vendor)
- Multi-module execution support
- Generic Action Executor with locator fallback strategies
- Automated screenshot capturing of filled screens and failure checkpoints
- Standalone, printable step-by-step User Manual generator with embedded Base64 screenshots
- Downloadable User Manual ZIP packages and raw screenshot archives
- Real-time execution progress tracking with live percentage, active test case counter, and progress bar
- Dynamic Excel test-data upload and updated result download
- Runtime data sharing between workflow steps
- Dynamic file and document upload support
- Environment-based execution (Local / DEV / UAT)
- Selenium Grid & Docker support
- Interactive HTML Dashboard for configuration and execution
- Extent Report integration with inline Base64 media capture
- Modular, maintainable, and reusable architecture

## Architecture

```text
HTML Dashboard / REST API
        │
        ▼
Module Controller / Report Controller
        │
        ▼
Module Service / Test Services
        │
        ▼
Workflow Executor ──► Excel Data Reader (test-data.xlsx)
        │
        ▼
Workflow JSON (test-config/*)
        │
        ▼
Stakeholder JSON (Citizen / Employee / Vendor)
        │
        ▼
Common Test Classes
        │
        ▼
Test Engine
        │
        ▼
Action Executor ──► Screenshot Manager ──► User Manual Generator
        │                                 ──► Extent Report Manager
        ▼
Selenium WebDriver (Local Chrome / Selenium Grid)
```


## Supported Modules
The framework supports execution of one or multiple modules in a single run.

- Advertisement
- Asset Management
- Challan Generation
- Community Hall Booking (CHB)
- Construction & Demolition (CND)
- Desludging Service (FSM)
- Estate Management
- E-Waste Management System
- Garbage Collection
- Mobile Toilet
- No Due Certificate (NDC)
- Online Building Plan Approval System (OBPAS)
- OBPAS Occupancy Certificate (OBPAS OC)
- Pet Registration
- Property Tax
- Public Grievance Redressal (PGR)
- Request Service
- Street Vending
- Trade License
- Tree Pruning
- Water & Sewerage
- Water Tanker

## Project Structure

```text
src
├── main
│   ├── java/org/upyog/Automation
│   │   ├── Base            # WebDriver lifecycle & base test abstractions
│   │   ├── Common          # Common citizen, employee, and vendor test orchestrators
│   │   ├── config          # Application & environment configuration classes
│   │   ├── Controller      # REST controllers (ModuleTest, Report, Test)
│   │   ├── engine          # Core ActionExecutor & TestEngine
│   │   ├── model           # DTOs, execution results, test instructions
│   │   ├── Modules         # Module-specific workflow definitions
│   │   ├── Reports         # ExtentManager, ReportManager, UserManualGenerator
│   │   ├── runner          # ModuleRunner execution dispatchers
│   │   ├── Service         # Test services (Module, Citizen, Employee, Vendor)
│   │   └── Utils           # AutomationConstants, ExcelDataReader, DriverFactory, ScreenshotManager
│   │
│   └── resources
│       ├── config          # dev.properties, niuatt.properties
│       ├── Documents       # Sample upload attachments (PDF, PNG, DXF, etc.)
│       ├── static          # Web Dashboard UI (HTML, CSS, JavaScript)
│       ├── test-config     # JSON workflow and stakeholder module definitions
│       └── test-data       # test-data.xlsx master test dataset
```

## Prerequisites
- Java 17
- Maven 3.9+
- Google Chrome
- Docker (Optional)
- Selenium Grid (Optional)


## Local Setup

Clone the repository

```bash
git clone <repository-url>
```

Build the project

```bash
mvn clean install
```

Run the application

```bash
mvn spring-boot:run
```

Open

```
http://localhost:8080
```
Ensure Google Chrome is installed before running the framework locally.

## Configuration Files

Each module is completely configuration-driven and contains dedicated JSON files in `src/main/resources/test-config/`:

Example:

- `workflow.json`
- `stakeholder_module.json`
- `citizen_module.json`
- `employee_module.json`
- `vendor_module.json`

These files define workflow execution sequences, stakeholder roles, locators, actions, and runtime behaviour.


## Application Configuration

Update the required properties inside:

```
src/main/resources/config/dev.properties
```

Example

```properties
selenium.grid.enabled=false

executionMode=local

webdriver.wait.timeout=20
```

Document paths should be updated according to the local machine or Docker-mounted directories.

Example

```properties
document.common.proof=/Users/username/Documents/advertisement.pdf

document.common.png=/Users/username/Documents/send.png

document.common.dxf=/Users/username/Documents/OBPAS1.dxf
```

## Running the Project

### Run using Maven

```bash
mvn spring-boot:run
```

### Run JAR

```bash
java -jar target/upyog-Automation-tests-0.0.1-SNAPSHOT.jar
```

### Access HTML Dashboard

```
http://localhost:8080
```


## Docker Deployment

Build Docker image

```bash
docker build -t upyog-automation .
```

Run Docker container

```bash
docker run -d \
--name upyog-automation \
--network upyog-network \
-p 8080:8080 \
-v /home/ubuntu/documents:/home/ubuntu/documents \
-v /home/ubuntu/OBPAS1.dxf:/home/ubuntu/OBPAS1.dxf \
upyog-automation:latest
```

## HTML Dashboard

The framework provides an HTML dashboard for executing automation without running Java classes manually. The dashboard allows users to execute automation without opening the IDE.

Features include:

- Module Selection & Multi-Module Execution
- Base URL Selection (Upyog Test, Niuatt, Localhost, etc.)
- Citizen, Employee, and Vendor Testing Tabs
- Real-Time Execution Progress Bar & Active Test Case Counter (`Running Test Case 1 of 4: PET_001`)
- Custom Excel Test Data Upload & Result Excel Download
- View Extent Reports & Download Report HTML
- Interactive Step-by-Step User Manual Viewer
- Download User Manual ZIP Packages (HTML Manual + Raw PNG Images)
- Download All Captured Screenshots in ZIP Archives

Access

```
http://localhost:8080
```

Server URLs

```
http://13.201.2.162:8080

http://65.0.8.57:8080
```


## Reporting & User Manuals

### Extent Reports
Extent Reports are generated automatically after every execution under `target/reports/`.
- Step-level action logging
- Pass / Fail status and failure root-cause analysis
- Inline Base64 screen captures on filled screens and failures
- Execution duration and workflow summary

### User Manual Generation
Interactive visual User Manuals are automatically generated from captured screen snapshots under `target/manuals/`.
- Step-by-step numbered walkthroughs with human-readable screen titles
- Embedded high-resolution Base64 screenshots (offline-ready)
- Click-to-enlarge Lightbox modal
- Print-to-PDF ready styling (`window.print()`)
- Direct ZIP download options containing the full manual and raw image files

### API Endpoints

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/module/run` | `POST` | Executes module tests from JSON/Excel |
| `/api/module/progress` | `GET` | Returns real-time test execution progress |
| `/api/module/download-template` | `GET` | Downloads the master Excel test-data template |
| `/api/module/upload-excel` | `POST` | Uploads custom Excel test dataset |
| `/api/module/download-result` | `GET` | Downloads updated Excel with execution results |
| `/api/report/view` | `GET` | Views the latest Extent Report |
| `/api/report/download` | `GET` | Downloads the latest Extent Report HTML |
| `/api/report/manual/{moduleName}` | `GET` | Views interactive visual User Manual |
| `/api/report/manual/download/{moduleName}` | `GET` | Downloads User Manual package (ZIP) |
| `/api/report/screenshots/download/{moduleName}`| `GET` | Downloads module screenshots archive (ZIP) |
| `/api/report/screenshots/download` | `GET` | Downloads all captured screenshots (ZIP) |

## Current Framework Capabilities

- Config-driven execution
- Workflow-driven automation
- Excel-driven test dataset execution
- Real-time test progress tracking and calculation
- Stakeholder-driven login
- Dynamic runtime data sharing
- Multi-module execution
- Generic Action Executor
- Dynamic document upload
- Automated screenshot capturing
- Standalone HTML User Manual generation
- ZIP package bundling for manuals and screenshots
- Environment detection
- Selenium Grid support
- Docker deployment
- HTML Dashboard
- Extent Reports with Base64 embedding
- VNC live execution viewer support
- Reusable Selenium components
- Modular architecture
- Easy onboarding of new modules

## Technologies Used

- Java 17
- Spring Boot 2.7.x
- Selenium WebDriver 4.x
- Apache POI 5.x
- Extent Reports 5.x
- Jackson Databind
- Selenium Grid
- Docker
- Maven
- ChromeDriver
- HTML5
- CSS3
- JavaScript (Vanilla ES6)

## Future Enhancements

- Parallel execution
- Cross-browser execution
- Jenkins CI/CD integration
- Video recording of test sessions
- Retry mechanism for flaky network assertions
- Dashboard analytics and trend charts

## Notes

- Update all document paths according to your local machine before execution.
- Ensure Chrome and ChromeDriver versions are compatible.
- Docker users must mount the required document directories.
- Workflow configurations and stakeholder details are maintained using JSON files.
- Test data for multi-case execution is maintained in `src/main/resources/test-data/test-data.xlsx`.
- New modules can be added by creating configuration files without modifying the automation engine.