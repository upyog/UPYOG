# UPYOG (Urban Platform for deliverY of Online Governance)

<p align="center">
  <img src="https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg" alt="UPYOG Logo" width="100%"/>
</p>

## Overview

UPYOG (Urban Platform for deliverY of Online Governance) is a comprehensive open-source digital governance platform designed to accelerate the delivery of citizen-centric urban services across India.

UPYOG provides a set of Open APIs, reusable microservices, registries, governance modules, and reference implementations that enable government entities, urban local bodies (ULBs), businesses, startups, and civil society organizations to build and scale digital governance solutions efficiently.

As a public digital good, UPYOG promotes interoperability, openness, innovation, transparency, and inclusion while reducing vendor lock-in and implementation complexity.

---

## Vision

To provide a scalable, secure, and interoperable digital public infrastructure that enables seamless delivery of municipal and governance services to citizens through multiple channels.

---
## Documentation Map

- [Architecture](Architecture.md)
- [API Guide](API.md)
- [Deployment Guide](Deployment.md)
- [Troubleshooting Guide](Troubleshooting.md)
- [Security Review](Security.md)
- [Developer Guide](DeveloperGuide.md)
- [Operations Guide](OperationsGuide.md)
- [Configuration Guide](ConfigurationGuide.md)
- [Build Guide](BuildGuide.md)
- [Code Quality Report](CodeQuality.md)
- [Service-wise Documentation](ServiceWiseDocumentation/INDEX.md)

## Platform Purpose

The platform enables urban local bodies to deliver digital citizen and employee workflows: registration, grievance redressal, property tax, trade license, water/sewerage, building approvals, no-objection certificates, waste services, street vending, community hall booking, payments, receipts, dashboards, notifications, audit, search, localization, and master-data management.

## Architecture at a Glance

- **API gateway:** Zuul and Spring Cloud Gateway variants route and secure APIs.
- **Authentication and authorization:** `egov-user` issues/validates tokens; `egov-accesscontrol` provides RBAC metadata.
- **Business services:** municipal and revenue services implement domain workflows.
- **Workflow:** `egov-workflow-v2` owns business-service states, actions, transitions, assignees, and escalation endpoints.
- **Persistence:** PostgreSQL is the primary data store; most services use Flyway and repository/JDBC patterns. Some events are persisted asynchronously by `egov-persister`.
- **Messaging:** Kafka carries persistence, indexing, notification, payment, audit, dashboard, and workflow events.
- **Search:** `egov-indexer` writes to Elasticsearch; `egov-searcher` exposes YAML-driven search APIs.
- **Cache:** Redis is used by gateway/Zuul, user, localization, URL shortening, and select registry services.
- **Files:** `egov-filestore` abstracts object storage.
- **Notifications:** SMS, email, user-event, Turn.io/chatbot integrations deliver outbound messages.
- **Legacy coexistence:** `finance/` and `edcr/` remain WildFly/Java 8 style applications alongside Spring Boot services.

## Technologies Used

| Area | Technology |
|---|---|
| Java services | Spring Boot 3.2.2 for most modern services; Spring Boot 2.2.x/1.5.x for transitional services |
| Java versions | Java 17 for modern Boot 3 services; Java 8 for legacy/transitional/WildFly modules |
| Gateway | Netflix Zuul and Spring Cloud Gateway |
| Messaging | Apache Kafka |
| Database | PostgreSQL with Flyway migrations |
| Search | Elasticsearch via indexer/searcher services |
| Cache | Redis |
| Frontend | React, Yarn/npm workspaces, nginx containers |
| Node services | PDF, NLP/chatbot, Firenoc services/calculator, xstate chatbot |
| Legacy runtime | WildFly/JBoss EAR for finance and eDCR |
| Build | Maven, npm/yarn, Gradle for Android, Make/Ansible for legacy finance |
| CI/CD | Jenkins build pipelines using `build/build-config.yml`; selected GitHub Actions for package publishing, GHCR build, CodeQL, and Jenkins triggers |
| Containers | Dockerfiles per service/domain; Flyway migration images |
| Kubernetes | External manifests/Helm values; repo contains discovery utilities and local setup references, not full charts |

## Repository Service Counts

| Area | Detected deployable/component count |
| --- | --- |
| MyCityApp | 1 |
| business-services | 11 |
| core-services | 38 |
| dx-services | 3 |
| edcr | 2 |
| finance | 1 |
| frontend | 7 |
| mobile-app | 1 |
| municipal-services | 43 |
| tutorials | 2 |
| utilities | 11 |

## Key Principles

- Open Standards and Open APIs
- Mobile-First Design
- Cloud Native Architecture
- Modular and Extensible Platform
- Citizen-Centric Service Delivery
- Interoperability Across Systems
- Scalability and High Availability
- Vendor-Neutral Ecosystem

---

## Useful Links

| Resource | Link |
|-----------|------|
| Documentation | https://upyog-docs.gitbook.io/upyog |
| Community Discussions | https://github.com/orgs/upyog/discussions |
| Issue Reporting | https://github.com/upyog/UPYOG/issues |
| NIUA | https://niua.in |
| License Terms | https://upyog.niua.org/employee/Upyog%20Code%20and%20Copyright%20License_v1.pdf |

---

# Platform Features

UPYOG offers a wide range of municipal, departmental, and governance services.

<p align="center">
  <img src="docs/images/upyog-features.png" alt="UPYOG Features" width="100%">
</p>

### Municipal Services

- Property Tax
- Trade License
- Grievances Redressal
- Building Plan Approval
- Water & Sewerage
- Birth & Death Registration
- Pet Registration
- Community Hall Booking
- E-Waste Management
- Advertisement Management
- Fire NOC
- Desludging Services
- Challans
- Service Requests
- Garbage Collection
- No Due Certificate

### Department Services

- Finance
- Asset Management
- Miscellaneous Collections
- Department Specific Services
- Estate Management
- Challan Generation

### Governance Services

- User Management
- Access Control
- Master Data Management
- Billing & Collection
- OTP Services
- SMS & Email Notifications
- Localization
- Workflow Engine
- Payment Gateway
- Reporting
- File Store
- Encryption
- PDF Generation

### Registries

- User Registry
- Property Registry
- Trade Registry
- Land Registry
- Water Connection Registry
- Boundary Registry
- Vendor Registry

### Data Infrastructure

- Property Ontology
- Trade Ontology
- Data Templates
- Building Code Specifications

---

# Technical Overview

<p align="center">
  <img src="docs/images/upyog-tech-overview.png" alt="UPYOG Technology Overview" width="100%">
</p>

### Technology Characteristics

- Microservices Architecture
- API-First Design
- Event-Driven Communication
- Cloud Native Deployment
- OpenAPI (OAS 2.0) Compliant
- Multi-Tenant Architecture
- Secure Authentication & Authorization
- Scalable Data Infrastructure

---

# Architecture

UPYOG is India's open-source platform for digital governance. Built on OpenAPI (OAS 2.0), it enables state governments and urban local bodies to rapidly deploy digital citizen services while integrating existing systems into a unified governance ecosystem.

The platform can be deployed on:

- Public Cloud
- Private Cloud
- Government Cloud
- Hybrid Infrastructure
- On-Premise Datacenters

---

## Platform Architecture

<p align="center">
  <img src="docs/images/upyog-platform-architecture1.png" alt="UPYOG Platform Architecture" width="100%">
</p>

### Architecture Highlights

- Multi-channel citizen access
- Web, Mobile, WhatsApp and Chatbot support
- Service-oriented modular design
- Shared governance infrastructure
- Common registries and master data
- Integrated payment and notification systems
- GIS and third-party integrations

---

## Infrastructure Architecture

<p align="center">
  <img src="docs/images/upyog-infra-architecture.png" alt="UPYOG Infrastructure Architecture" width="100%">
</p>

### Core Components

#### Citizen Channels

- Citizen Web Portal
- Mobile Applications
- WhatsApp Integrations

#### Employee Channels

- Employee Dashboard
- Inbox Management
- Service Workflows

#### Governance Infrastructure

- User Service
- Access Control
- Localization Service
- Master Data Service
- File Store
- Encryption Service
- ID Generator
- URL Shortening

#### Core Service Layer

- Billing Service
- Collection Service
- Report Service
- PDF Service
- OTP Service
- Workflow Engine
- Payment Gateway

#### Data Layer

- Relational Database
- Analytical Database
- Search Engine
- Event Streaming
- Messaging Queue

---

# Deployment

UPYOG supports deployment on:

- Kubernetes
- OpenShift
- Docker
- AWS
- Azure
- GCP
- Government Cloud Infrastructure
- On-Premise Datacenters

---

# Security

UPYOG incorporates security best practices including:

- Role-Based Access Control (RBAC)
- Authentication & Authorization
- Encryption Services
- Audit Logging
- Secure API Gateway
- Data Privacy Controls

---

# Contributing

We welcome contributions from governments, developers, startups, system integrators, and community members.

Please:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Submit a Pull Request

---

# License

UPYOG source code is licensed under:

**UPYOG Code, Copyright and Contribution License Terms**

https://upyog.niua.org/employee/Upyog%20Code%20and%20Copyright%20License_v1.pdf

---

# Developed and Maintained By

**National Institute of Urban Affairs (NIUA)**

https://niua.in

---

<p align="center">
  <strong>Building Digital Public Infrastructure for Urban India</strong>
</p>
