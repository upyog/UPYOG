# UPYOG Config-Driven Selenium Automation Framework

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.x-brightgreen)
![Selenium](https://img.shields.io/badge/Selenium-4.x-success)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)

Config-Driven | Workflow-Based | Selenium Grid | Docker | HTML Dashboard | Extent Reports


## Overview
The UPYOG Config-Driven Selenium Automation Framework is a scalable, metadata-driven automation framework developed to automate end-to-end workflows across multiple UPYOG modules. The framework executes tests using JSON-based configurations, eliminating hardcoded Selenium logic and enabling easy onboarding of new modules with minimal code changes.

## Features
- 100% JSON-driven automation framework
- Config-driven workflow execution
- Stakeholder-based execution (Citizen, Employee, Vendor)
- Multi-module execution
- Generic Action Executor
- Runtime data sharing between workflows
- Dynamic file upload support
- Environment-based execution (Local / DEV / UAT)
- Selenium Grid & Docker support
- HTML Dashboard for execution
- Extent Report integration
- Modular and reusable architecture

## Architecture

```text
HTML Dashboard
        │
        ▼
Module Controller
        │
        ▼
Module Service
        │
        ▼
Workflow Executor
        │
        ▼
Workflow JSON
        │
        ▼
Stakeholder JSON
        │
        ▼
Citizen / Employee / Vendor
        │
        ▼
Common Test Classes
        │
        ▼
Test Engine
        │
        ▼
Action Executor
        │
        ▼
Selenium WebDriver
```


## Supported Modules
The framework supports execution of one or multiple modules in a single run.

- Advertisement
- Asset Management
- Community Hall Booking
- Construction & Demolition
- Desludging Service
- E-Waste Management System
- Online Building Plan Approval System (OBPAS)
- Pet Registration
- Property Tax
- Public Grievance Redressal (PGR)
- Request Service
- Street Vending
- Trade License
- Water & Sewerage

## Project Structure

```text
src
├── main
│   ├── java
│   │   ├── common
│   │   ├── config
│   │   ├── controller
│   │   ├── engine
│   │   ├── model
│   │   ├── reports
│   │   ├── service
│   │   └── utils
│   │
│   └── resources
│       ├── config
│       ├── Documents
│       └── test-config
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

Each module is completely configuration-driven and contains dedicated JSON files.

Example:

- workflow.json
- stakeholder_module.json
- citizen_module.json
- employee_module.json
- vendor_module.json

These files define workflow execution, stakeholders, module steps, and runtime behaviour.


## Application Configuration

Update the required properties inside:

```
application.properties
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
java -jar target/upyog-automation.jar
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

The framework provides an HTML dashboard for executing automation without running Java classes manually.The dashboard allows users to execute automation without opening the IDE.

Features include:

- Module Selection
- Multiple Module Execution
- Base URL Selection
- Citizen Testing
- Employee Testing
- Vendor Testing
- Live Status Updates
- View Report
- Download Report

Access

```
http://localhost:8080
```

Server URLs

```
http://13.201.2.162:8080

http://65.0.8.57:8080
```


## Reporting

Extent Reports are generated automatically after every execution.

Reports include:

- Step-level logging
- Pass / Fail status
- Execution duration
- Workflow summary
- Module summary

Reports can be viewed or downloaded directly from the HTML Dashboard.

## Current Framework Capabilities

- Config-driven execution
- Workflow-driven automation
- Stakeholder-driven login
- Dynamic runtime data sharing
- Multi-module execution
- Generic Action Executor
- Dynamic document upload
- Environment detection
- Selenium Grid support
- Docker deployment
- HTML Dashboard
- Extent Reports
- VNC execution support
- Reusable Selenium components
- Modular architecture
- Easy onboarding of new modules

## Technologies Used

- Java 17
- Spring Boot
- Selenium WebDriver
- Selenium Grid
- Docker
- Maven
- Jackson
- Extent Reports
- HTML
- CSS
- JavaScript
- ChromeDriver
- Selenium Grid

## Future Enhancements

- Parallel execution
- Cross-browser execution
- Jenkins CI/CD integration
- Screenshot capture on failures
- Video recording
- Retry mechanism
- Dashboard analytics

## Notes

- Update all document paths according to your local machine before execution.
- Ensure Chrome and ChromeDriver versions are compatible.
- Docker users must mount the required document directories.
- Workflow configurations and stakeholder details are maintained using JSON files.
- New modules can be added by creating configuration files without modifying the automation engine.