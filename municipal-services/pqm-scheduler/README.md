# PQM Scheduler

Process Quality Monitoring Scheduler

## Service Dependencies
- egov-user-service
- pqm-service

## Service Details
- This pqm scheduler is a cronjob scheduler for scheduling tests. It runs based on environment configuration. 
- It triggers the _scheduler API from PQM-Service to generate the Tests on the basis of Test Standards present in MDMS.
