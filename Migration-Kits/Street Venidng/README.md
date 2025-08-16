# Street Vendor Migration Kit

Simple tool to migrate street vendor data from Excel to the system with real-time status tracking.

## Prerequisites

### 1. Install Python
- Download Python from https://python.org/downloads
- Install Python 3.8 or higher
- Check installation: Open Terminal/Command Prompt and type `python3 --version`

### 2. Install VS Code
- Download from https://code.visualstudio.com
- Install Python extension in VS Code

### 3. Install Required Libraries
Open Terminal in VS Code and run:
```bash
pip3 install pandas requests openpyxl --break-system-packages
```



## Setup

### 1. Download Files
- Download `svmk.py` script
- Place it in any folder (e.g., Desktop or Documents)

### 2. Prepare Excel File
- Your Excel file must have these columns:
  - `name` - Vendor name
  - `mobileNo` - Mobile number
  - `tenant_id` - Tenant ID
  - Other vendor details as per your data

### 3. Update File Path
Open `svmk.py` and change this line:
```python
EXCEL_PATH = "/Users/atul/Desktop/migrate.xlsx"
```
Change to your Excel file location:
```python
EXCEL_PATH = "/path/to/your/excel/file.xlsx"
```

### 4. Update API Settings
In `svmk.py`, update these if needed:
- `AUTH_TOKEN` - Your authorization token
- `USER_AUTH_TOKEN` - User service token
- API URLs (if different from localhost)

## Required Services (Port Forwarding)

Make sure these services are running and port forwarded:

- **user** service
- **sv-services** service  
- **billing-service** service
- **collection-services** service

Use kubectl port-forward commands for each service.

## How to Run in VS Code

### 1. Open VS Code
- Open the folder containing `svmk.py`
- File → Open Folder → Select your folder

### 2. Open Terminal in VS Code
- View → Terminal (or Ctrl+`)
- Make sure you're in the correct folder

### 3. Run the Script
```bash
python3 svmk.py
```

### 4. Monitor Progress
- Keep Excel file open
- Press `Ctrl+F5` (Windows) or `Cmd+R` (Mac) to refresh Excel
- Check `migrationStatus` and `errorDetails` columns
- Watch terminal output for real-time progress

## VS Code Tips
- Use `Ctrl+C` in terminal to stop script
- Split screen: Excel on one side, VS Code on other
- Terminal shows detailed progress logs
- Use `Ctrl+Shift+P` → "Python: Select Interpreter" if needed

## Status Values

- `STARTING` - Migration started
- `USER_CREATED` - User created successfully
- `CREATED` - Application created
- `APPROVED` - Application approved
- `BILL_READY` - Bill generated
- `SUCCESS` - Complete migration success
- `USER_FAILED` - User creation failed
- `CREATE_FAILED` - Application creation failed
- `UPDATE_FAILED` - Application approval failed
- `BILL_FAILED` - Bill generation failed
- `PAYMENT_FAILED` - Payment failed

## Troubleshooting

### Common Issues

1. **"No module named 'requests'"**
   ```bash
   pip3 install requests --break-system-packages
   ```

2. **"Excel file not found"**
   - Check file path in `EXCEL_PATH`
   - Make sure file exists

3. **"Connection refused"**
   - Check if services are running
   - Verify port forwarding is active

4. **"Mobile not found in Excel"**
   - Check if `mobileNo` column exists
   - Verify mobile numbers are correct format

### Getting Help
- Check VS Code terminal for error messages
- Look at `migration.db` file for detailed logs
- Check `errorDetails` column in Excel for specific errors
- Use VS Code's integrated terminal for better error visibility

## Files Created
- `migration.db` - SQLite database with detailed logs
- `migrate_status.csv` - Real-time status file (optional)

## Tips
- Keep Excel file closed while script runs for better performance
- Use `Ctrl+C` to stop script if needed
- Check port forwarding if APIs fail
- Backup your Excel file before running