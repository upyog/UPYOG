# Changelog
All notable changes to this module will be documented in this file.

## 2.2.0 - 2026-04-15

### Platform Upgrade (Java 8 → Java 17)

Major runtime and dependency upgrade to modernize the EDCR service stack.

#### Runtime
- Upgraded JDK from 8 to **17** (Maven compiler source/target set to 17)
- Upgraded application server from WildFly 11 to **WildFly 26**
- Docker build now uses `amazoncorretto:17-alpine`; runtime image `nudmcdg/edcr-wildfly26:07`

#### Framework & Persistence
- **Spring Framework** 5.3.31, **Spring Security** 5.7.11, **Spring Data JPA** 2.7.18
- **Hibernate ORM** 5.6.15.Final with EhCache region factory (replaces Infinispan cache on WildFly 26)
- **Hibernate Validator** 6.2.5.Final, **javax.validation** 2.0.1.Final
- JTA platform updated to `JBossAppServerJtaPlatform` for WildFly 26 compatibility
- Connection handling mode set to `DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION`
- Multi-tenant schema provider: `supportsAggressiveRelease` set to `false` to prevent JTA transaction errors

#### Reporting
- **JasperReports** upgraded to 6.20.0
- Added Java 17-safe `jasperreports.properties` (subreport runner factory, JDT compiler target 17, font overrides)
- Bundled custom font extensions (Arial) for PDF report generation

#### Dependencies
- **Jackson** 2.13.5, **PostgreSQL JDBC** 42.7.10, **Flyway** 9.22.3
- **JUnit 5** 5.10.2 and **Mockito** 5.23.0 (replaced JUnit 4 / springockito)
- **Log4j 2** 2.17.1 (replaced legacy log4j 1.x usage)
- Apache Commons libraries upgraded (lang3, io, codec, dbcp, fileupload2, etc.)
- WildFly server modules for Hibernate, Jackson, and Infinispan excluded via `jboss-deployment-structure.xml` to avoid classloading conflicts

#### Code Changes
- Introduced `ICityService` interface for Spring JDK-proxy compatibility in filters and custom impl provider
- `LayerNames` cache: lazy-loaded inside JTA transaction to fix WildFly datasource enlistment errors
- `EgovMasterDataCaching`: local `DefaultCacheManager` replaces removed WildFly Infinispan JNDI lookup
- Embedded Redis startup disabled; standalone Redis configuration required for production
- Report template compilation no longer cached to reflect template changes without restart

## 2.1.2 - 2023-09-14
Central Instance Library Integration

## 2.1.1

- Data push for edcr

## 2.1.0

- Dimension validation feature
- DXF to PDF conversion feature
- Enhance search filters by fromdate, todate, application number, status and added pagination in fetch API

## 2.0.0

- The Extracting data from dxf file module has been made **open source**.
- The new module named **egov-edcr-extract** has been added as part of **eGov-dcr-serivce** repo, it contains all extraction code.
- Enhance river, to support with color code. The color code used to identify river type.
- Capture the level of basement under the ground as dimension with color code 3
- Capture height from floor to the bottom of beam of the stilt floor

## 1.2.0

- Security audit issues fix
- Cleanup unused code and database tables

## 1.1.1

- Enhanced Door, to support door widths with color code. The color code is used to identify type of door.

## 1.1.0
- Added portico feature
- Added glass facade openings feature
- Added Information and Communication Technology landing point feature
- Added capturing mezzanine area at room level
- Enhanced accessory block feature to accommodate units with color code, and multiple distances with color code support
- Enhanced chimney feature to accommodate multiple area and height
- Enhanced parapet feature to accommodate multiple area and height

## 1.0.2

- Extract dimesion values for road reserve
- Extract the room type dynamically based on config available in the database
- Set color code for floor unit
- Extract slope for vehicle ramp

## 1.0.1

- Added Drainage feature
- Added Footpath feauture
- Added supplyline Utility (waterline, electricline, sewerageline, etc...) feature
- Added Road Reserve (In Front, Rear, Side1, and Side2 setback sides) feature

## 1.0.0

- Base version
