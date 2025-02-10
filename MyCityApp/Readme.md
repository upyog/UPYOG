# mobile_app

A new Flutter project.

## Installation
**Clone the repository**
```bash
git clone https://github.com/selco-git/app.git

```

**Navigate to the project directory**
```bash
cd app

```

**Get the dependencies**
```bash
flutter pub get

```

**Run the project**
```bash
flutter run

```

**Rename app name command**
```bash 
dart run rename_app:main all="My App Name"

```

## State Management
This project uses the `Get` package to manage state across the app. It efficiently handles user add employee logic.



## Project Folder Structure

```
mobile_app
├─ lib
│  ├─ components
│  │  ├─ acknowlegement
│  │  ├─ documents_not_found
│  │  ├─ drawer
│  │  │  ├─ citizen
│  │  │  └─ employee
│  │  ├─ error_page
│  │  ├─ filter
│  │  ├─ locality_widget
│  │  ├─ no_application_found
│  │  └─ no_bill_found
│  ├─ config
│  ├─ controller
│  ├─ env
│  ├─ icons
│  ├─ model
│  │  ├─ citizen
│  │  │  ├─ bill
│  │  │  ├─ birth_death_model
│  │  │  ├─ bpa_model
│  │  │  │  ├─ comparison
│  │  │  │  └ noc
│  │  │  │
│  │  │  ├─ files
│  │  │  ├─ fire_noc
│  │  │  ├─ fsm
│  │  │  ├─ grievance
│  │  │  ├─ localization
│  │  │  ├─ notification
│  │  │  ├─ payments
│  │  │  ├─ properties_tax
│  │  │  ├─ property
│  │  │  ├─ request
│  │  │  ├─ timeline
│  │  │  ├─ token
│  │  │  ├─ trade_license
│  │  │  ├─ transaction
│  │  │  ├─ user_profile
│  │  │  └─ water_sewerage
│  │  ├─ common
│  │  │  ├─ estimate_model
│  │  │  └─ locality
│  │  └─ employee
│  │     ├─ employee_model
│  │     ├─ emp_bpa_model
│  │     ├─ emp_mdms_model
│  │     ├─ emp_pt_model
│  │     ├─ emp_tl_model
│  │     ├─ emp_ws_model
│  │     ├─ status_map
│  │     └─ workflow_business_service
│  ├─ repository
│  ├─ routes
│  ├─ screens
│  │  ├─ citizen
│  │  │  ├─ bottom_navigation
│  │  │  ├─ building_plan_approval
│  │  │  │  └ bpa_my_applications
│  │  │  ├─ fire_noc
│  │  │  ├─ fsm
│  │  │  ├─ grievance_redressal
│  │  │  │  ├─ grievance_details_screen
│  │  │  │  └─ my_grievances
│  │  │  ├─ home
│  │  │  │  ├─ my_applications
│  │  │  │  ├─ my_certificates
│  │  │  │  └─ my_payments
│  │  │  ├─ home_global_search
│  │  │  ├─ home_location_choose
│  │  │  ├─ home_select_language
│  │  │  ├─ location_choose
│  │  │  ├─ login
│  │  │  ├─ misc_collections
│  │  │  │  └─ my_challans
│  │  │  ├─ notification
│  │  │  ├─ ntt_data_aipay_payment
│  │  │  ├─ otp_screen
│  │  │  ├─ payments
│  │  │  ├─ profile
│  │  │  ├─ property_tax
│  │  │  │  ├─ my_applications
│  │  │  │  ├─ my_bills
│  │  │  │  ├─ my_payments
│  │  │  │  └─ my_properties
│  │  │  ├─ settings
│  │  │  ├─ sign_up
│  │  │  ├─ trade_license
│  │  │  │  ├─ my_tl_applications
│  │  │  │  └─ property_information
│  │  │  └─ water_sewerage
│  │  │     ├─ sewerage
│  │  │     │  ├─ sewerage_application
│  │  │     │  ├─ sewerage_connections
│  │  │     │  ├─ sewerage_my_bills
│  │  │     │  ├─ sewerage_my_payments
│  │  │     │  └─ sewerage_screen.dart
│  │  │     ├─ water
│  │  │     │  ├─ water_application
│  │  │     │  ├─ water_connections
│  │  │     │  ├─ water_my_bills
│  │  │     │  └─ water_my_payments
│  │  │     ├─ water_additional_details
│  │  │     ├─ water_property_information
│  │  │     └─ water_view_consumption_detail
│  │  ├─ employee
│  │  │  ├─ emp_bottom_navigation
│  │  │  ├─ emp_bpa
│  │  │  │  ├─ emp_bpa_details
│  │  │  ├─ emp_dashboard
│  │  │  │  └─ tl_inbox
│  │  │  ├─ emp_home_city
│  │  │  ├─ emp_login
│  │  │  ├─ emp_profile
│  │  │  ├─ emp_pt
│  │  │  │  └─ emp_pt_details
│  │  │  ├─ emp_trade_license
│  │  │  │  ├─ emp_tl_details
│  │  │  │  └─ property_information
│  │  │  └─ emp_water
│  │  │     ├─ emp_sw
│  │  │     │  └─ emp_sw_details
│  │  │     ├─ emp_water_edit_application
│  │  │     └─ emp_ws
│  │  ├─ select_citizen
│  │  └─ splash
│  ├─ services
│  ├─ utils
│  │  ├─ constants
│  │  ├─ enums
│  │  ├─ errors
│  │  ├─ extension
│  │  ├─ firebase_configurations
│  │  ├─ loaders.dart
│  │  ├─ localization
│  │  ├─ platforms
│  └─ widgets
├─ pubspec.lock
├─ pubspec.yaml
└ README.md

```