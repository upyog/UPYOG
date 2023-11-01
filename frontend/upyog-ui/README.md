# UPYOG UI
UPYOG (Urban Platform for deliverY of Online Governance) is India's largest platform for governance services. Visit [UPYOG documentation portal](https://upyog-docs.gitbook.io/upyog-v-1.0/) for more details.

This repository contains source code for web implementation of the UPYOG UI modules with dependencies and libraries. test

#### Starting local server
1. To run server locally first change directory to **web** subdirectory
1. In the project run **yarn install** to install node modules and dependencies 
1. Run **yarn start** to start the local server

#### Updating modules
To update the modules run *install-dep.sh* script this will pull all the updates from *micro-ui-internals* subfolder

#### Reference Docs

###### Module wise reference docs
Module | Reference Docs
------ | --------------
PGR | [PGR: UI Implementation - Guidelines & FAQs](https://upyog-docs.gitbook.io/upyog-v-1.0/upyog-1/platform/customize-upyog/upyog-ui-implementation-development-guidelines-and-faqs/pgr-ui-implementation-guidelines-and-faqs)
FSM | [FSM: UI Implementation - Guidelines & FAQs](https://upyog-docs.gitbook.io/upyog-v-1.0/upyog-1/platform/customize-upyog/upyog-ui-implementation-development-guidelines-and-faqs/fsm-ui-implementation-guidelines-and-faqs)
UPYOG Services | [UPYOG Service Stack](https://upyog-docs.gitbook.io/upyog-v-1.0/upyog-1/platform/configure-upyog/configuring-upyog-service-stack)

###### Dependencies and their references
1. https://www.npmjs.com/package/react-query
2. https://react-redux.js.org/
3. https://react-hook-form.com/
4. https://www.npmjs.com/package/react-table
5. https://www.npmjs.com/package/react-time-picker
6. https://reactrouter.com/web/guides/quick-start
7. https://recharts.org/

#### License
UPYOG Source Code is open sources under License [UPYOG CODE, COPYRIGHT AND CONTRIBUTION LICENSE TERMS](https://upyog.niua.org/employee/Upyog%20Code%20and%20Copyright%20License_v1.pdf)
