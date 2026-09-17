#!/usr/bin/env python3
"""
Generate Swagger 2.0 API contract YAML files for UPYOG service modules.
Outputs to each module's src/main/resources/{service-name}-contract.yml
"""

import json
import re
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
DOC_DIR = REPO_ROOT / "ServiceWiseDocumentation"
COMMON_PATH = "https://raw.githubusercontent.com/egovernments/egov-services/master/docs/common/contracts/v1-1-1.yml"

SERVICE_ROOTS = [
    REPO_ROOT / "municipal-services",
    REPO_ROOT / "core-services",
    REPO_ROOT / "business-services",
    REPO_ROOT / "utilities",
    REPO_ROOT / "dx-services",
    REPO_ROOT / "edcr" / "service",
]

# Map existing docs contracts to service modules (prefer copy/adapt)
EXISTING_CONTRACTS = {
    "birth-death-services": REPO_ROOT / "municipal-services/docs/birth-death/birth-death.yml",
    "egov-accesscontrol": REPO_ROOT / "core-services/docs/access-control-contract.yml",
    "egov-document-uploader": REPO_ROOT / "core-services/docs/egov-document-uploader-contract.yml",
    "egov-enc-service": REPO_ROOT / "core-services/docs/enc-service-contract.yml",
    "egov-filestore": REPO_ROOT / "core-services/egov-filestore/src/main/resources/filestore-service-contract.yml",
    "egov-idgen": REPO_ROOT / "core-services/docs/idgen-contract.yml",
    "egov-indexer": REPO_ROOT / "core-services/docs/indexer-contract.yml",
    "egov-localization": REPO_ROOT / "core-services/docs/localisation-contract.yml",
    "egov-location": REPO_ROOT / "core-services/docs/egov-location-contract.yml",
    "egov-mdms-service": REPO_ROOT / "core-services/docs/mdms-contract.yml",
    "egov-otp": REPO_ROOT / "core-services/docs/egov-otp-contract.yml",
    "egov-pg-service": REPO_ROOT / "core-services/docs/egov-pg-contract.yml",
    "egov-survey-services": REPO_ROOT / "core-services/egov-survey-services/src/main/resources/egov-survey-service.yml",
    "egov-url-shortening": REPO_ROOT / "core-services/docs/url-shortening_contract.yml",
    "egov-user": REPO_ROOT / "core-services/docs/egov-user-contract.yml",
    "egov-workflow-v2": REPO_ROOT / "core-services/docs/worfklow-2.0.yml",
    "national-dashboard-ingest": REPO_ROOT / "core-services/docs/national-dashboard-ingest.yml",
    "firenoc-services": REPO_ROOT / "municipal-services/docs/fire_noc_contract.yaml",
    "firenoc-calculator": REPO_ROOT / "municipal-services/docs/fire_noc_calculation_service.yaml",
    "pdf-service": REPO_ROOT / "core-services/docs/pdf-service-contract.yml",
    "report": REPO_ROOT / "core-services/docs/report-contract.yml",
    "chatbot": REPO_ROOT / "core-services/docs/chatbot-contract.yml",
    "pgr-services": REPO_ROOT / "municipal-services/pgr-services/src/main/resources/swagger-contract.yml",
    "inbox": REPO_ROOT / "municipal-services/docs/inbox.yml",
    "firenoc-services": REPO_ROOT / "municipal-services/docs/fire_noc_contract.yaml",
    "firenoc-calculator": REPO_ROOT / "municipal-services/docs/fire_noc_calculation_service.yaml",
    "echallan-services": REPO_ROOT / "municipal-services/docs/e-Challan-v1.0.0.yaml",
    "bpa-services": REPO_ROOT / "municipal-services/docs/bpa/bpa-service.yaml",
    "bpa-calculator": REPO_ROOT / "municipal-services/docs/bpa/bpa-calculator.yaml",
    "noc-services": REPO_ROOT / "municipal-services/docs/noc-v-1.0.0.yaml",
    "property-services": REPO_ROOT / "municipal-services/docs/property-services/property-services.yml",
    "pt-services-v2": REPO_ROOT / "municipal-services/docs/property-services/pt-service-v2-contract.yml",
    "pt-calculator-v2": REPO_ROOT / "municipal-services/docs/property-services/property-calculation-service.yml",
    "tl-services": REPO_ROOT / "municipal-services/docs/tl-service.yml",
    "tl-calculator": REPO_ROOT / "municipal-services/docs/tl-calculator.yml",
    "ws-services": REPO_ROOT / "municipal-services/docs/water-sewerage-services.yaml",
    "sw-services": REPO_ROOT / "municipal-services/docs/water-sewerage-services.yaml",
    "rainmaker-pgr": REPO_ROOT / "municipal-services/docs/rainmaker-pgr-contract.yml",
    "egov-user-event": REPO_ROOT / "municipal-services/docs/user-events.yml",
    "billing-service": REPO_ROOT / "business-services/Docs/billingservice/V-2.0.yml",
    "collection-services": REPO_ROOT / "business-services/Docs/collection-services/V-2-0.yml",
    "egov-apportion-service": REPO_ROOT / "business-services/Docs/egov-apportion-service.yml",
    "egov-hrms": REPO_ROOT / "business-services/Docs/hrms-v1.0.0.yaml",
    "egf-master": REPO_ROOT / "business-services/Docs/egf-master-v1.0.0 .yaml",
    "dashboard-analytics": REPO_ROOT / "business-services/Docs/dss-dashboard/DSS Analytics Dashboard YAML Spec 1.0.0.yaml",
    "dashboard-ingest": REPO_ROOT / "business-services/Docs/dss-dashboard/DSS Ingest YAML Spec 1.0.0.yaml",
    "fsm": REPO_ROOT / "municipal-services/fsm/src/main/resources/fsm -api-spec.yaml",
    "garbage-service": REPO_ROOT / "municipal-services/garbage-service/src/main/resources/garbage-services.yml",
    "mdms-migration-toolkit": REPO_ROOT / "utilities/mdms-migration-toolkit/src/main/resources/mdms-migration-toolkit.yaml",
}


def find_service_modules():
    modules = []
    for root in SERVICE_ROOTS:
        if not root.exists():
            continue
        for entry in sorted(root.iterdir()):
            if not entry.is_dir():
                continue
            java_src = entry / "src/main/java"
            pom = entry / "pom.xml"
            go_mod = entry / "go.mod"
            if java_src.exists() and pom.exists():
                modules.append(entry)
            elif go_mod.exists() or (entry / "main.go").exists():
                modules.append(entry)
    return modules


def read_context_path(module_path: Path) -> str:
    props_files = list((module_path / "src/main/resources").glob("application*.properties"))
    for pf in props_files:
        try:
            content = pf.read_text(encoding="utf-8", errors="ignore")
            for line in content.splitlines():
                line = line.strip()
                if line.startswith("#"):
                    continue
                if "context-path" in line and "=" in line:
                    val = line.split("=", 1)[1].strip()
                    if val and val != "/":
                        return val if val.startswith("/") else f"/{val}"
        except OSError:
            pass
    return f"/{module_path.name}"


def read_service_doc(service_name: str) -> dict:
    doc_file = DOC_DIR / f"{service_name}.md"
    result = {"purpose": "", "title": service_name.replace("-", " ").title()}
    if not doc_file.exists():
        return result
    try:
        content = doc_file.read_text(encoding="utf-8", errors="ignore")
        purpose_match = re.search(r"## Purpose\s*\n\s*\n(.+?)(?:\n\n|\n##)", content, re.DOTALL)
        if purpose_match:
            result["purpose"] = purpose_match.group(1).strip()
    except OSError:
        pass
    return result


def parse_controllers(module_path: Path) -> list:
    java_root = module_path / "src/main/java"
    if not java_root.exists():
        return []

    endpoints = []
    controller_files = list(java_root.rglob("*Controller*.java"))

    for cf in controller_files:
        try:
            content = cf.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue

        class_mapping = ""
        cm = re.search(r'@RequestMapping\s*\(\s*(?:value\s*=\s*)?["\']([^"\']+)["\']', content)
        if cm:
            class_mapping = cm.group(1).rstrip("/")

        # @PostMapping, @GetMapping, etc.
        mapping_pattern = re.compile(
            r'@(Get|Post|Put|Delete|Patch)Mapping\s*\(\s*(?:value\s*=\s*)?(?:\{?\s*)?["\']([^"\']+)["\']',
            re.MULTILINE,
        )
        for match in mapping_pattern.finditer(content):
            http_method = match.group(1).upper()
            path_suffix = match.group(2)
            _add_endpoint(endpoints, class_mapping, path_suffix, http_method, content, match.end(), cf.name)

        # @RequestMapping(value = "...", method = RequestMethod.POST)
        rm_pattern = re.compile(
            r'@RequestMapping\s*\(\s*value\s*=\s*["\']([^"\']+)["\']\s*,\s*method\s*=\s*RequestMethod\.(\w+)',
            re.MULTILINE,
        )
        for match in rm_pattern.finditer(content):
            path_suffix = match.group(1)
            http_method = match.group(2).upper()
            _add_endpoint(endpoints, class_mapping, path_suffix, http_method, content, match.end(), cf.name)

        # @RequestMapping("/_create") without explicit method (DIGIT convention: POST)
        rm_simple = re.compile(
            r'@RequestMapping\s*\(\s*(?:value\s*=\s*)?["\']([^"\']+)["\']\s*\)',
            re.MULTILINE,
        )
        for match in rm_simple.finditer(content):
            path_suffix = match.group(1)
            # Skip if already captured by method-specific RequestMapping
            after_ann = content[match.end():match.end()+80]
            if "method" in after_ann[:40]:
                continue
            _add_endpoint(endpoints, class_mapping, path_suffix, "POST", content, match.end(), cf.name)

    return endpoints


def _add_endpoint(endpoints, class_mapping, path_suffix, http_method, content, pos, controller_name):
    full_path = f"{class_mapping}{path_suffix}" if class_mapping else path_suffix
    if not full_path.startswith("/"):
        full_path = "/" + full_path

    after = content[pos:pos + 500]
    ret_match = re.search(r'ResponseEntity<(\w+)>', after)
    return_type = ret_match.group(1) if ret_match else "Object"

    method_match = re.search(r'public\s+\S+\s+(\w+)\s*\(', after)
    method_name = method_match.group(1) if method_match else path_suffix.strip("/").replace("/", "_")

    endpoints.append({
        "method": http_method,
        "path": full_path,
        "controller": controller_name,
        "return_type": return_type,
        "operation": method_name,
    })


def infer_tag(path: str) -> str:
    parts = [p for p in path.split("/") if p and not p.startswith("_")]
    if parts:
        return parts[0].replace("-", " ").title()
    return "Default"


def infer_summary(path: str, operation: str) -> str:
    action_map = {
        "_create": "Create",
        "_search": "Search",
        "_update": "Update",
        "_delete": "Delete",
        "_count": "Count",
        "_plainsearch": "Plain search",
        "_download": "Download",
        "_transition": "Transition workflow",
        "_generate": "Generate",
    }
    for suffix, label in action_map.items():
        if path.endswith(suffix):
            resource = path.replace(suffix, "").strip("/").replace("/", " ").replace("_", " ")
            return f"{label} {resource}".strip()
    return operation.replace("_", " ").title()


def to_pascal_case(name: str) -> str:
    return "".join(word.capitalize() for word in re.split(r"[-_]", name) if word)


def build_swagger_spec(service_name: str, base_path: str, endpoints: list, doc_info: dict) -> dict:
    title = to_pascal_case(service_name) + " Service"
    description = doc_info.get("purpose") or f"This service provides APIs for the {service_name} module in the UPYOG platform."

    spec = {
        "swagger": "2.0",
        "info": {
            "version": "1.0.0",
            "title": title,
            "description": description + "\n",
            "contact": {
                "name": "UPYOG NUDM Team",
                "email": "cdg-contact@niua.org",
            },
        },
        "schemes": ["https"],
        "basePath": base_path,
        "x-api-id": f"org.egov.{service_name.replace('-', '.')}",
        "x-common-path": COMMON_PATH,
        "paths": {},
        "definitions": {},
    }

    tags_used = set()
    return_types = set()

    for ep in sorted(endpoints, key=lambda x: x["path"]):
        path = ep["path"]
        method = ep["method"].lower()
        tag = infer_tag(path)
        tags_used.add(tag)
        summary = infer_summary(path, ep["operation"])
        return_type = ep["return_type"]
        return_types.add(return_type)

        is_search = "_search" in path or "_plainsearch" in path or "_count" in path
        is_create = "_create" in path
        is_update = "_update" in path
        success_code = "201" if (is_create or is_update) else "200"

        operation = {
            "summary": summary,
            "description": f"{summary} for {service_name}. Follows DIGIT API conventions with RequestInfo in body and ResponseInfo in response.",
            "responses": {
                success_code: {
                    "description": f"{summary} completed successfully",
                    "schema": {"$ref": f"#/definitions/{return_type}"},
                },
                "400": {
                    "description": "Invalid input",
                    "schema": {
                        "$ref": f"{COMMON_PATH}#/definitions/ErrorRes"
                    },
                },
            },
            "tags": [tag],
        }

        if method == "post":
            if is_search:
                operation["parameters"] = [
                    {"$ref": f"{COMMON_PATH}#/parameters/requestInfo"},
                    {"$ref": f"{COMMON_PATH}#/parameters/tenantId"},
                    {
                        "name": "tenantId",
                        "in": "query",
                        "description": "Unique identifier of ULB",
                        "type": "string",
                        "required": False,
                    },
                    {
                        "name": "ids",
                        "in": "query",
                        "type": "array",
                        "items": {"type": "string"},
                        "maxItems": 50,
                        "description": "Unique identifiers to search",
                        "required": False,
                    },
                ]
                operation["parameters"].append({
                    "name": "RequestInfoWrapper",
                    "in": "body",
                    "description": "RequestInfo metadata and search criteria",
                    "required": True,
                    "schema": {"$ref": f"#/definitions/{return_type}"},
                })
            else:
                operation["parameters"] = [{
                    "name": return_type,
                    "in": "body",
                    "description": f"Request payload with RequestInfo and domain data for {summary.lower()}",
                    "required": True,
                    "schema": {"$ref": f"#/definitions/{return_type}"},
                }]
        elif method == "get":
            operation["parameters"] = [
                {"$ref": f"{COMMON_PATH}#/parameters/tenantId"},
            ]

        if path not in spec["paths"]:
            spec["paths"][path] = {}
        spec["paths"][path][method] = operation

    # Build generic definitions for return types
    domain_name = to_pascal_case(service_name.rstrip("-services").rstrip("-service"))
    for rt in return_types:
        spec["definitions"][rt] = {
            "type": "object",
            "description": f"Request/Response contract for {rt}",
            "properties": {
                "RequestInfo": {
                    "$ref": f"{COMMON_PATH}#/definitions/RequestInfo"
                },
                "ResponseInfo": {
                    "$ref": f"{COMMON_PATH}#/definitions/ResponseInfo"
                },
                f"{domain_name}Applications": {
                    "type": "array",
                    "description": "Domain application objects",
                    "items": {"$ref": f"#/definitions/{domain_name}Application"},
                },
            },
        }

    spec["definitions"][f"{domain_name}Application"] = {
        "type": "object",
        "description": f"A domain application object for {service_name}",
        "properties": {
            "id": {
                "type": "string",
                "description": "Unique identifier (UUID)",
                "readOnly": True,
            },
            "tenantId": {
                "type": "string",
                "description": "Unique identifier of ULB",
                "maxLength": 128,
                "minLength": 2,
            },
            "applicationNumber": {
                "type": "string",
                "description": "Unique application number",
                "readOnly": True,
            },
            "status": {
                "type": "string",
                "description": "Application status",
            },
            "auditDetails": {
                "$ref": f"{COMMON_PATH}#/definitions/AuditDetails"
            },
        },
        "required": ["tenantId"],
    }

    spec["definitions"]["Workflow"] = {
        "type": "object",
        "description": "Fields related to workflow service",
        "properties": {
            "action": {"type": "string", "description": "Action on the application"},
            "status": {"type": "string", "description": "Status of the application"},
            "comments": {"type": "string", "description": "Comments for the action"},
        },
    }

    return spec


def copy_existing_contract(service_name: str, src: Path, dest: Path) -> bool:
    """Copy existing contract to resources if not already swagger 2.0 in dest."""
    if not src.exists():
        return False
    try:
        content = src.read_text(encoding="utf-8", errors="ignore")
        dest.parent.mkdir(parents=True, exist_ok=True)
        dest.write_text(content, encoding="utf-8")
        return True
    except OSError:
        return False


def yaml_quote(s: str) -> str:
    if not isinstance(s, str):
        return json.dumps(s)
    if re.search(r"[:#\[\]{}&*!|>'\"\\@`]", s) or s.startswith((" ", "-", "?")) or "\n" in s:
        return "|+\n" + "\n".join("  " + line for line in s.splitlines()) if "\n" in s else json.dumps(s)
    return s


def to_yaml(obj, indent=0) -> str:
    sp = "  " * indent
    if isinstance(obj, dict):
        lines = []
        for k, v in obj.items():
            if isinstance(v, (dict, list)):
                lines.append(f"{sp}{k}:")
                lines.append(to_yaml(v, indent + 1))
            elif isinstance(v, bool):
                lines.append(f"{sp}{k}: {'true' if v else 'false'}")
            elif v is None:
                lines.append(f"{sp}{k}: null")
            elif isinstance(v, str) and "\n" in v:
                lines.append(f"{sp}{k}: |")
                for line in v.splitlines():
                    lines.append(f"{sp}  {line}")
            else:
                lines.append(f"{sp}{k}: {yaml_quote(v) if isinstance(v, str) else v}")
        return "\n".join(lines)
    if isinstance(obj, list):
        lines = []
        for item in obj:
            if isinstance(item, dict):
                first = True
                for k, v in item.items():
                    if first:
                        if isinstance(v, (dict, list)):
                            lines.append(f"{sp}- {k}:")
                            lines.append(to_yaml(v, indent + 2))
                        else:
                            lines.append(f"{sp}- {k}: {yaml_quote(v) if isinstance(v, str) else v}")
                        first = False
                    elif isinstance(v, (dict, list)):
                        lines.append(f"{sp}  {k}:")
                        lines.append(to_yaml(v, indent + 2))
                    else:
                        lines.append(f"{sp}  {k}: {yaml_quote(v) if isinstance(v, str) else v}")
            else:
                lines.append(f"{sp}- {yaml_quote(item) if isinstance(item, str) else item}")
        return "\n".join(lines)
    return f"{sp}{obj}"


def yaml_dump(spec: dict) -> str:
    return to_yaml(spec) + "\n"


def main():
    modules = find_service_modules()
    generated = []
    copied = []
    skipped = []

    for module in modules:
        service_name = module.name
        resources_dir = module / "src/main/resources"
        if not resources_dir.exists() and not (module / "main.go").exists():
            # Go services may use configs/
            resources_dir = module / "configs"
        if not resources_dir.exists():
            resources_dir = module / "src/main/resources"
        resources_dir.mkdir(parents=True, exist_ok=True)

        contract_name = f"{service_name}-contract.yml"
        dest = resources_dir / contract_name

        # Skip if already has a substantial contract in resources
        if dest.exists():
            existing = dest.read_text(encoding="utf-8", errors="ignore")
            if ("swagger:" in existing or "openapi:" in existing) and len(existing) > 2000:
                if service_name not in ("advertisement-service", "community-hall-booking", "pet-services"):
                    skipped.append(service_name)
                    continue

        # Prefer existing docs contract
        if service_name in EXISTING_CONTRACTS:
            src = EXISTING_CONTRACTS[service_name]
            if copy_existing_contract(service_name, src, dest):
                copied.append(service_name)
                continue

        endpoints = parse_controllers(module)
        if not endpoints:
            skipped.append(service_name)
            continue

        base_path = read_context_path(module)
        doc_info = read_service_doc(service_name)
        spec = build_swagger_spec(service_name, base_path, endpoints, doc_info)

        dest.write_text(yaml_dump(spec), encoding="utf-8")
        generated.append(f"{service_name} ({len(endpoints)} endpoints)")

    print(f"\n=== API Spec Generation Complete ===")
    print(f"Generated: {len(generated)}")
    for g in generated:
        print(f"  + {g}")
    print(f"\nCopied from existing docs: {len(copied)}")
    for c in copied:
        print(f"  -> {c}")
    print(f"\nSkipped (no controllers or existing spec): {len(skipped)}")
    print(f"  {', '.join(skipped[:20])}{'...' if len(skipped) > 20 else ''}")


if __name__ == "__main__":
    main()
