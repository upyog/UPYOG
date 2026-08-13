"""
Manual-trigger Airflow DAG for the UPYOG National Dashboard accelerator.

Pipeline per module:
  extract (Elasticsearch) -> transform (placeholder) -> load (UPYOG ingest API)

Flow:
  1. dump_kibana() runs ES queries for the module/date and builds ward-level payloads.
  2. Payload is pushed to XCom under key payload_{MODULE} from the extract task.
  3. load() pulls that XCom from the extract task (not transform) and posts to ingest API.

Trigger with optional conf: {"date": "DD-MM-YYYY"}. Defaults to yesterday (IST) if omitted.

Required Airflow config:
  Connections: es_conn, digit-auth
  Variables: username, password, tenantid, usertype, token, totalulb_url, upyogurl
"""

import sys
from pathlib import Path

# Ensure accelerator plugins and dags dir are importable when DAGs run from .../dags/
_dag_dir = Path(__file__).resolve().parent
_plugins_dir = _dag_dir.parent / "plugins"
for _d in (_plugins_dir, _dag_dir):
    if _d.is_dir() and str(_d) not in sys.path:
        sys.path.insert(0, str(_d))

from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.utils import timezone as airflow_tz
from airflow.models import Variable
from airflow.hooks.base import BaseHook
from datetime import datetime, timedelta
from pytz import timezone
import logging
import json
import requests
from hooks.elastic_hook import ElasticHook
from queries.tl import *
from queries.pgr import *
from queries.ws import *
from queries.pt import *
from queries.firenoc import *
from queries.mcollect import *
from queries.obps import *
from queries.common import *

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 3,
    'retry_delay': timedelta(seconds=10),
    'start_date': airflow_tz.utcnow() - timedelta(days=1),
}

# Maps each service module to its ES query list and empty payload template function.
# Only PT is active here; uncomment others to enable additional modules.
module_map = {
    # 'TL' : (tl_queries, empty_tl_payload),
    # 'PGR' : (pgr_queries, empty_pgr_payload),
    # 'WS' : (ws_queries, empty_ws_payload),
    'PT' : (pt_queries, empty_pt_payload)
    # 'FIRENOC' : (firenoc_queries, empty_firenoc_payload),
    # 'MCOLLECT' : (mcollect_queries, empty_mcollect_payload),
    # 'OBPS' : (obps_queries, empty_obps_payload),
    # 'COMMON' : (common_queries,empty_common_payload)
}


# Manual trigger only; pass conf e.g. {"date": "06-02-2026"} when triggering.
dag = DAG('national_dashboard_template_manual', default_args=default_args, schedule=None)
log_endpoint = 'kibana/api/console/proxy'
batch_size = 50  # Number of ward payloads sent per ingest API call

# Shared state used while building COMMON module metrics (ULB liveness, SLA totals).
ulbs = {}
modules = {}
total_ulbs = 0
totalApplications = 0
totalApplicationWithinSLA = 0


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _extract_task_id(module):
    """Return the Airflow task_id of the extract step for a given module."""
    return 'elastic_search_extract_{0}'.format(module.lower())


def _get_run_date_str(kwargs):
    """
    Returns date string in DD-MM-YYYY.
    Prefers dag_run.conf['date']; falls back to yesterday (Asia/Kolkata) if not provided.
    """
    dag_run = kwargs.get('dag_run')
    conf = getattr(dag_run, "conf", None)
    date = None
    if conf and isinstance(conf, dict):
        date = conf.get('date')
    if not date:
        localtz = timezone('Asia/Kolkata')
        yesterday = (datetime.now(localtz) - timedelta(days=1)).strftime("%d-%m-%Y")
        logging.info(f"No 'date' in dag_run.conf; defaulting to yesterday: {yesterday}")
        date = yesterday
    return date


# ---------------------------------------------------------------------------
# Extract: query Elasticsearch and build payloads
# ---------------------------------------------------------------------------

def dump_kibana(**kwargs):
    """
    Extract task entry point.

    For the given module and date:
      - Computes IST day window as epoch milliseconds for ES range queries.
      - Runs all module queries against Elasticsearch via es_conn.
      - Transforms aggregation results into ward-level (or state-level for COMMON) payloads.
      - Pushes serialized payload to XCom as payload_{MODULE} for the load task.
    """
    hook = ElasticHook('GET', 'es_conn')
    module = kwargs['module']
    module_config = module_map.get(module)
    if not module_config:
        raise ValueError("Unknown module '{0}'. Available modules: {1}".format(module, list(module_map.keys())))
    queries = module_config[0]
    date = _get_run_date_str(kwargs)
    logging.info("Starting extract for module=%s, date=%s", module, date)
    localtz = timezone('Asia/Kolkata')
    dt_aware = localtz.localize(datetime.strptime(date, "%d-%m-%Y"))
    # ES queries filter on epoch_millis; start = 00:00:00 IST, end = 23:59:59.999 IST
    start = int(dt_aware.timestamp() * 1000)
    end = start + (24 * 60 * 60 * 1000) - 1000
    logging.info(start)
    logging.info(end)
    logging.info("start the DAGS")
    # COMMON module aggregates across all time for ULB liveness; other modules use the day window.
    if module == 'COMMON':
        actualstart = int(localtz.localize(datetime.strptime('01-01-1970', "%d-%m-%Y")).timestamp() * 1000)
        end = start + (24 * 60 * 60 * 1000) - 1000
        start = actualstart

    merged_document = {}
    live_ulbs = 0

    isStateLive = "N/A"
    # Run each ES query defined in queries/{module}.py and collect raw aggregation responses.
    for query in queries:
        q = query.get('query').format(start,end)
        logging.info(q)
        response = hook.search(query.get('path'),json.loads(q))
        merged_document[query.get('name')] = response
        logging.info(json.dumps(response))
        if module == 'COMMON' :
            transform_response_common(merged_document,query.get('name'),query.get('module'))


    if module == 'COMMON':
        # Build a single state-level payload with ULB liveness, citizen count, and SLA metrics.
        present = datetime.strptime(date,"%d-%m-%Y")
        logging.info(present.strftime("%Y-%m-%d %H:%M:%S"))
        citizen_count = get_citizen_count(present.strftime("%Y-%m-%d %H:%M:%S"))
        total_ulbs = readulb()
        common_metrics = {}
        module_ulbs = []
        for tenantid in ulbs:
            if len(ulbs[tenantid]) >= 2:
                live_ulbs +=1
                for md in ulbs[tenantid]:
                    if md in modules:
                        modules[md].append(tenantid)
                    else:
                        modules[md] = [tenantid]

        if live_ulbs >= total_ulbs/2:
            isStateLive = "Live"

        for md in modules:
            module_ulbs.append({'name': md, 'value': len(modules[md])})

        common_metrics['totalLiveUlbsCount'] = live_ulbs
        common_metrics['status']  = isStateLive
        common_metrics['onboardedUlbsCount'] = 0
        common_metrics['totalCitizensCount'] = citizen_count
        common_metrics['slaAchievement'] = (totalApplicationWithinSLA/totalApplications) * 100
        common_metrics['totalUlbCount'] = total_ulbs
        common_metrics['liveUlbsCount'] = [{'groupBy': 'serviceModuleCode', 'buckets': module_ulbs}]
        logging.info(json.dumps(common_metrics))

        empty_lambda =  module_config[1]
        common_list = []
        common_payload = empty_lambda('N/A', 'pb.amritsar', 'N/A', date)
        common_payload['metrics'] = common_metrics
        common_list.append(common_payload)
        payload_json = json.dumps(common_list)
        logging.info("Pushing XCom payload_%s with %d record(s) from extract task", module, len(common_list))
        kwargs['ti'].xcom_push(key='payload_{0}'.format(module), value=payload_json)
        return payload_json
    else:
        # Build one payload per ward|ulb combination and store in XCom for load().
        ward_list = transform_response_sample(merged_document, date, module)
        payload_json = json.dumps(ward_list)
        logging.info("Pushing XCom payload_%s with %d ward record(s) from extract task", module, len(ward_list))
        kwargs['ti'].xcom_push(key='payload_{0}'.format(module), value=payload_json)
        return payload_json


def readulb(**kwargs):
    """Fetch total onboarded ULB count from MDMS tenants JSON (totalulb_url Variable)."""
    ulbs = []
    url = Variable.get('totalulb_url')
    logging.info("Fetching ULB list from %s", url)
    response = requests.get(url)
    response.raise_for_status()
    json_data = json.loads(response.text)
    tenants_array = json_data["tenants"]
    for tenant in tenants_array:
        ulbs.append(tenant["code"])
    total_ulbs = len(ulbs)
    logging.info("Fetched %d ULBs", total_ulbs)
    return total_ulbs

def get_citizen_count(startdate):
    """Fetch unique citizen count from UPYOG for the COMMON module metrics."""
    upyogurl = Variable.get('upyogurl').rstrip('/')
    url = '{0}/egov-searcher/unique-citizen-count?date={1}'.format(upyogurl, startdate)
    logging.info("Fetching citizen count from %s", url)
    response = requests.get(url)
    if response.status_code == 200:
        logging.info("Successfully fetched citizen count data")
        return response.json()
    logging.error("Citizen count request failed with status %s: %s", response.status_code, response.text)
    return None


# ---------------------------------------------------------------------------
# Transform: ES aggregation buckets -> dashboard payload structure
# ---------------------------------------------------------------------------

def transform_response_common(merged_document,query_name,query_module):
    """Accumulate SLA and ULB-module mappings from a single COMMON module ES response."""
    single_document = merged_document[query_name]
    single_document = single_document.get('aggregations')
    transform_single_common(single_document,query_module)

def transform_single_common(single_document,query_module):
    """Update global SLA counters and track which modules each ULB is active on."""
    global totalApplicationWithinSLA,totalApplications
    sla =  single_document.get('applicationsIssuedWithinSLA').get('withinsla').get('value')
    total =  single_document.get('totalApplications').get('value')
    totalApplications+=total
    totalApplicationWithinSLA+=sla


    ulb_agg = single_document.get('ulbs')
    ulb_buckets = ulb_agg.get('buckets')
    for ulb_bucket in ulb_buckets:
        tenantid = ulb_bucket['key']
        if tenantid in ulbs:
            ulbs[tenantid].append(query_module)
        else:
            ulbs[tenantid] = [query_module]



def transform_response_sample(merged_document, date, module):
    """
    Merge all ES query results for a module into ward-level payloads.

    Each query contributes metrics via its lambda function; results are keyed by ward|ulb.
    """
    module_config = module_map.get(module)
    queries = module_config[0]
    ward_map = {}
    ward_list = []
    for query in queries:
        single_document = merged_document[query.get('name')]
        single_document = single_document.get('aggregations')
        lambda_function = query.get('lambda')
        ward_map = transform_single(single_document, ward_map, date, lambda_function, module)
    ward_list = [ward_map[k] for k in ward_map.keys()]
    return ward_list

def get_key(ward, ulb):
    """Composite key to deduplicate payloads for the same ward within a ULB."""
    return '{0}|{1}'.format(ward, ulb)

def transform_single(single_document, ward_map, date, lambda_function, module):
    """
    Walk ward -> ulb -> region aggregation buckets and populate metrics on each payload.

    Uses empty_{module}_payload() as the base structure for new ward|ulb entries.
    """
    module_config = module_map.get(module)
    empty_lambda = module_config[1]
    ward_agg = single_document.get('ward')
    ward_buckets = ward_agg.get('buckets')
    for ward_bucket in ward_buckets:
        ward = ward_bucket.get('key')
        ulb_agg = ward_bucket.get('ulb')
        ulb_buckets = ulb_agg.get('buckets')
        for ulb_bucket in ulb_buckets:
            ulb = ulb_bucket.get('key')
            region_agg = ulb_bucket.get('region')
            region_buckets = region_agg.get('buckets')
            for region_bucket in region_buckets:
                region = region_bucket.get('key')
                if ward_map.get(get_key(ward,ulb)):
                    ward_payload = ward_map.get(get_key(ward,ulb))
                else:
                    ward_payload = empty_lambda(region, ulb, ward, date)
                metrics = ward_payload.get('metrics')
                metrics = lambda_function(metrics, region_bucket)
                ward_payload['metrics'] = metrics
                ward_map[get_key(ward, ulb)] = ward_payload
    return ward_map


def dump(**kwargs):
    """Legacy/debug helper — not used in the active PT pipeline."""
    ds = kwargs['ds']
    hook = ElasticHook('GET', 'test-es')
    resp = hook.search('/dss-collection_v2', {
        'size': 10000,
        "query": {
            "term": {
                "dataObject.paymentDetails.businessService.keyword": "TL"
            }
        }
    })
    return resp['hits']['hits']

# ---------------------------------------------------------------------------
# Load: authenticate and push payloads to UPYOG National Dashboard ingest API
# ---------------------------------------------------------------------------

def get_auth_token(connection):
    """Obtain OAuth access token from UPYOG using digit-auth connection and Airflow Variables."""
    endpoint = 'user/oauth/token'
    url = '{0}://{1}/{2}'.format('https', connection.host, endpoint)
    logging.info("Requesting auth token from %s", url)
    data = {
        'grant_type' : 'password',
        'scope' : 'read',
        'username' : Variable.get('username'),
        'password' : Variable.get('password'),
        'tenantId' : Variable.get('tenantid'),
        'userType' : Variable.get('usertype')
    }

    r = requests.post(url, data=data, headers={'Authorization' : 'Basic {0}'.format(Variable.get('token')), 'Content-Type' : 'application/x-www-form-urlencoded'})
    response = r.json()
    if not response.get('access_token'):
        logging.error("Failed to obtain auth token: %s", response)
        raise ValueError("Auth token request failed; check digit-auth connection and Airflow Variables")
    logging.info("Auth token obtained successfully")
    return (response.get('access_token'), response.get('refresh_token'), response.get('UserRequest'))


def _write_adaptor_log(module, startdate, response):
    """Write ingest response to adaptor_logs index using es_conn over HTTP."""
    from uuid import uuid4
    try:
        conn = BaseHook.get_connection('es_conn')
        scheme = conn.schema or 'https'
        host = '{0}:{1}'.format(conn.host, conn.port) if conn.port else conn.host
        url = '{0}://{1}/adaptor_logs/_doc/{2}'.format(scheme, host, uuid4())
        doc = {
            'timestamp': startdate,
            'module': module,
            'severity': 'Info',
            'state': 'Punjab',
            'message': json.dumps(response),
        }
        auth = (conn.login, conn.password) if conn.login else None
        logging.info("Writing adaptor log to %s", url)
        r = requests.post(url, json=doc, auth=auth, verify=False)
        r.raise_for_status()
        logging.info("Adaptor log written successfully")
    except Exception as exc:
        # Ingest already succeeded; do not fail the task if audit logging fails.
        logging.warning("Failed to write adaptor log (ingest succeeded): %s", exc)


def call_ingest_api(connection, access_token, user_info, payload, module,startdate):
    """POST a batch of ward payloads to national-dashboard/metric/_ingest and log the response."""
    endpoint = 'national-dashboard/metric/_ingest'
    url = '{0}://{1}/{2}'.format('https', connection.host, endpoint)
    data = {
        "RequestInfo": {
            "apiId": "asset-services",
            "ver": None,
            "ts": None,
            "action": None,
            "did": None,
            "key": None,
            "msgId": "search with from and to values",
            "authToken": access_token,
            "userInfo": user_info
        },
        "Data": payload

    }


    r = requests.post(url, data=json.dumps(data), headers={'Content-Type' : 'application/json'})
    response = r.json()
    logging.info(json.dumps(data))
    logging.info(response)

    _write_adaptor_log(module, startdate, response)
    return response



def load(**kwargs):
    """
    Load task entry point.

    Pulls payload_{MODULE} from the extract task (not transform — transform is a no-op),
    authenticates with UPYOG, and sends payloads in batches to the ingest API.
    """
    connection = BaseHook.get_connection('digit-auth')
    module = kwargs['module']
    extract_task_id = _extract_task_id(module)
    xcom_key = 'payload_{0}'.format(module)
    logging.info("Starting load for module=%s, pulling XCom key=%s from task=%s", module, xcom_key, extract_task_id)

    (access_token, refresh_token, user_info) = get_auth_token(connection)

    # Must pull from extract task explicitly; immediate upstream (transform) does not push this key.
    payload = kwargs['ti'].xcom_pull(key=xcom_key, task_ids=extract_task_id)
    if payload is None:
        raise ValueError(
            "No XCom payload found for key '{0}' from task '{1}'. "
            "Ensure the extract task completed successfully before load runs.".format(xcom_key, extract_task_id)
        )
    logging.info("Retrieved XCom payload for module=%s (length=%d chars)", module, len(payload))
    payload_obj = json.loads(payload)
    logging.info("Payload contains %d record(s) for module=%s", len(payload_obj), module)
    localtz = timezone('Asia/Kolkata')
    date = _get_run_date_str(kwargs)
    dt_aware = localtz.localize(datetime.strptime(date, "%d-%m-%Y"))
    startdate = int(dt_aware.timestamp() * 1000)
    logging.info(startdate)
    if access_token and refresh_token:
        for i in range(0, len(payload_obj), batch_size):
            logging.info('calling ingest api for batch starting at {0} with batch size {1}'.format(i, batch_size))
            call_ingest_api(connection, access_token, user_info, payload_obj[i:i+batch_size], module,startdate)
    return None

def transform(**kwargs):
    """Placeholder transform step — add custom post-processing here if needed."""
    logging.info('Your transformations go here')
    return 'Post Transformed Data'


# ---------------------------------------------------------------------------
# Airflow task definitions and dependencies
# ---------------------------------------------------------------------------
# Each active module follows: extract >> transform >> load
# Only PT is enabled; other modules are commented out below.

# Disabled TL tasks (kept for reference)
# extract_tl = PythonOperator(
#     task_id='elastic_search_extract_tl',
#     python_callable=dump_kibana,
#     op_kwargs={ 'module' : 'TL'},
#     dag=dag)
#
# transform_tl = PythonOperator(
#     task_id='nudb_transform_tl',
#     python_callable=transform,
#     dag=dag)
#
# load_tl = PythonOperator(
#     task_id='nudb_ingest_load_tl',
#     python_callable=load,
#     op_kwargs={ 'module' : 'TL'},
#     dag=dag)


# Disabled PGR tasks (kept for reference)
# extract_pgr = PythonOperator(
#     task_id='elastic_search_extract_pgr',
#     python_callable=dump_kibana,
#     op_kwargs={ 'module' : 'PGR'},
#     dag=dag)
#
# transform_pgr = PythonOperator(
#     task_id='nudb_transform_pgr',
#     python_callable=transform,
#     dag=dag)
#
# load_pgr = PythonOperator(
#     task_id='nudb_ingest_load_pgr',
#     python_callable=load,
#     op_kwargs={ 'module' : 'PGR'},
#     dag=dag)
#
# Disabled WS tasks (kept for reference)
# extract_ws = PythonOperator(
#     task_id='elastic_search_extract_ws',
#     python_callable=dump_kibana,
#     op_kwargs={ 'module' : 'WS'},
#     dag=dag)
#
# transform_ws = PythonOperator(
#     task_id='nudb_transform_ws',
#     python_callable=transform,
#     dag=dag)
#
# load_ws = PythonOperator(
#     task_id='nudb_ingest_load_ws',
#     python_callable=load,
#     op_kwargs={ 'module' : 'WS'},
#     dag=dag)


# Active PT pipeline: extract ES data -> transform (no-op) -> ingest to National Dashboard
extract_pt = PythonOperator(
    task_id='elastic_search_extract_pt',
    python_callable=dump_kibana,
    op_kwargs={ 'module' : 'PT'},
    dag=dag)

transform_pt = PythonOperator(
    task_id='nudb_transform_pt',
    python_callable=transform,
    dag=dag)

load_pt = PythonOperator(
    task_id='nudb_ingest_load_pt',
    python_callable=load,
    op_kwargs={ 'module' : 'PT'},
    dag=dag)

# Disabled FIRENOC tasks (kept for reference)
# extract_firenoc = PythonOperator(
#     task_id='elastic_search_extract_firenoc',
#     python_callable=dump_kibana,
#     op_kwargs={ 'module' : 'FIRENOC'},
#     dag=dag)
#
# transform_firenoc = PythonOperator(
#     task_id='nudb_transform_firenoc',
#     python_callable=transform,
#     dag=dag)
#
# load_firenoc = PythonOperator(
#     task_id='nudb_ingest_load_firenoc',
#     python_callable=load,
#     op_kwargs={ 'module' : 'FIRENOC'},
#     dag=dag)
#
# Disabled MCOLLECT tasks (kept for reference)
# extract_mcollect = PythonOperator(
#     task_id='elastic_search_extract_mcollect',
#     python_callable=dump_kibana,
#     op_kwargs={ 'module' : 'MCOLLECT'},
#     dag=dag)
#
# transform_mcollect = PythonOperator(
#     task_id='nudb_transform_mcollect',
#     python_callable=transform,
#     dag=dag)
#
# load_mcollect = PythonOperator(
#     task_id='nudb_ingest_load_mcollect',
#     python_callable=load,
#     op_kwargs={ 'module' : 'MCOLLECT'},
#     dag=dag)
#
# Disabled COMMON tasks (kept for reference)
# extract_common = PythonOperator(
#     task_id='elastic_search_extract_common',
#     python_callable=dump_kibana,
#     op_kwargs={ 'module' : 'COMMON'},
#     dag=dag)
#
# transform_common = PythonOperator(
#     task_id='nudb_transform_common',
#     python_callable=transform,
#     dag=dag)
#
# load_common = PythonOperator(
#     task_id='nudb_ingest_load_common',
#     python_callable=load,
#     op_kwargs={ 'module' : 'COMMON'},
#     dag=dag)

# extract_ws_digit = PythonOperator(
#     task_id='elastic_search_extract_ws_digit',
#     python_callable=dump_kibana,
# #     do_xcom_push=True,
#     op_kwargs={ 'module' : 'WS_DIGIT'},
#     dag=dag)

# transform_ws_digit = PythonOperator(
#     task_id='nudb_transform_ws_digit',
#     python_callable=transform,
# #     dag=dag)

# load_ws_digit = PythonOperator(
#     task_id='nudb_ingest_load_ws_digit',
#     python_callable=load,
# #     op_kwargs={ 'module' : 'WS_DIGIT'},
#     dag=dag)

# extract_obps = PythonOperator(
#     task_id='elastic_search_extract_obps',
#     python_callable=dump_kibana,
# #     do_xcom_push=True,
#     op_kwargs={ 'module' : 'OBPS'},
#     dag=dag)

# transform_obps = PythonOperator(
#     task_id='nudb_transform_obps',
#     python_callable=transform,
# #     dag=dag)

# load_obps = PythonOperator(
#     task_id='nudb_ingest_load_obps',



# extract_tl >> transform_tl >> load_tl
# extract_pgr >> transform_pgr >> load_pgr
# extract_ws >> transform_ws >> load_ws
#     python_callable=load,
# #     op_kwargs={ 'module' : 'OBPS'},
#     dag=dag)
extract_pt >> transform_pt >> load_pt
# extract_firenoc >> transform_firenoc >> load_firenoc
# extract_mcollect >> transform_mcollect >> load_mcollect
# extract_common >> transform_common >> load_common
#extract_ws_digit >> transform_ws_digit >> load_ws_digit
#extract_obps >> transform_obps >> load_obps