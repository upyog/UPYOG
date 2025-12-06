import requests
import json, os
from dotenv import load_dotenv
import logging
import sys

logging.basicConfig(format='%(asctime)s - %(message)s', level=logging.INFO)

rootTenantId = os.getenv('TENANT_ID')

def getRequestInfo(userInfo):
    requestInfo = json.loads("{\n    \"apiId\": \"Rainmaker\",\n    \"ver\": \".01\",\n    \"action\": \"\",\n    \"did\": \"1\",\n    \"key\": \"\",\n    \"msgId\": \"20170310130900|en_IN\",\n    \"requesterId\": \"\",\n    \"userInfo\": \"\"\n  }")
    requestInfo["userInfo"] = userInfo
    return requestInfo

def getUser(): 
    try:
        userInfo = None
        user_host = os.getenv('USER_SERVICE_HOST')
        user_search = os.getenv('USER_SEARCH')
        # # Call user search to fetch SYSTEM user
        user_url = "{}{}?tenantId={}".format(user_host, user_search, rootTenantId)
        # It will be active after adding role config
        tenantId = os.getenv('TENANT_ID')
        userName = os.getenv('USER_NAME')
        user_payload = {"requestInfo":{"apiId":"ap.public","ver":"1","ts":45646456,"action":"POST","did":None,"key":None,"msgId":"8c11c5ca-03bd-11e7-93ae-92361f002671","userInfo":{"id":32},"authToken":"5eb3655f-31b1-4cd5-b8c2-4f9c033510d4"},"tenantId":tenantId,"userName":userName,"pageSize":"1"}

        # tenantId = "pg.citya"
        # uuid = os.getenv('USER_UUID')
        # user_payload = {"requestInfo":{"apiId":"ap.public","ver":"1","ts":45646456,"action":"POST","did":None,"key":None,"msgId":"8c11c5ca-03bd-11e7-93ae-92361f002671","userInfo":{"id":32},"authToken":"5eb3655f-31b1-4cd5-b8c2-4f9c033510d4"},"tenantId":tenantId,"uuid":[uuid],"pageSize":"1"}
        user_payload = json.dumps(user_payload)
        user_headers = {'Content-Type': 'application/json'}
        user_response = requests.request("POST", user_url, headers=user_headers, data = user_payload)
        logging.info("Recieved response from user:{}".format(user_response.status_code))
        users = user_response.json()['user']
        if len(users)==0:
            raise Exception("user not found")
        else:
            userInfo = users[0]
        return userInfo
    except Exception as ex:
        logging.info("Exception while fetching user info.")
        logging.error("Exception occurred", exc_info=True)
        return None

def triggerCronjob(requestInfo):
    success = 0
    failed = 0
    try:
        pqm_host = os.getenv('PQM_SERVICE_HOST')
        pqm_scheduler = os.getenv('PQM_SCHEDULER')
        url = "{}{}".format(pqm_host,pqm_scheduler)
        payload = {}
        payload["RequestInfo"] = requestInfo
        headers = {'Content-Type': 'application/json'}
        requestData = json.dumps(payload)
        logging.info("Request URL -> " + url)
        try:
            response = requests.request("POST", url, headers=headers, data = requestData)
            logging.info("Response: {}".format(response.status_code))
        except Exception as ex:
            logging.info("Exception while scheduling for tenant id")
            
    except Exception as ex:
        logging.error("Exception while scheduling")

if __name__ == "__main__":
    try:
        logging.info('Scheduler Started')
        load_dotenv()
        userInfo = getUser()
        if userInfo != None:
            requestInfo=getRequestInfo(userInfo)
            triggerCronjob(requestInfo)
        else:
            logging.error("Could not fetch user")

    except Exception as ex:
        logging.error("Exception occured on main.", exc_info=True)
        raise(ex)