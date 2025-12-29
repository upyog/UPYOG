class RequestInfo {
  final String? apiId;
  final String? msgId;
  final String? authToken;
  final String local;
  final PlainAccessRequest? plainAccessRequest;
  final bool isComparison;
  final bool isEmpFireNoc;
  final bool isPgCreate;
  final bool isPgUpdate;

  RequestInfo({
    this.apiId,
    this.msgId,
    this.plainAccessRequest,
    this.authToken,
    required this.local,
    this.isComparison = false,
    this.isEmpFireNoc = false,
    this.isPgCreate = false,
    this.isPgUpdate = false,
  });

  Map<String, dynamic> toJson() => {
        "apiId": apiId ?? 'Rainmaker',
        "msgId": msgId ?? '${DateTime.now().millisecondsSinceEpoch}|$local',
        if (isPgCreate || isPgUpdate) "ver": ".01",
        if (isPgCreate || isPgUpdate) "did": '1',
        if (isPgCreate || isPgUpdate) "key": "",
        if (isPgCreate || isPgUpdate) "requesterId": "",
        if (isPgCreate) "action": "_create",
        if (isPgUpdate) "action": "_update",
        if (isEmpFireNoc) "action": '_search',
        if (isEmpFireNoc) "did": '1',
        if (isEmpFireNoc) "ver": ".01",
        "authToken": authToken,
        if (!isComparison)
          "plainAccessRequest": plainAccessRequest?.toJson() ?? {},
      };

  Map<String, dynamic> toJsonWithoutPAR() => {
        "apiId": apiId ?? 'Rainmaker',
        "msgId": msgId ?? '${DateTime.now().millisecondsSinceEpoch}|$local',
        "authToken": authToken,
      };
}

class PlainAccessRequest {
  PlainAccessRequest();

  factory PlainAccessRequest.fromJson(Map<String, dynamic> json) =>
      PlainAccessRequest();

  Map<String, dynamic> toJson() => {};
}

// EMP - FIRE NOC
class RequestInfoNoc {
  String? apiId;
  String? ver;
  String? ts;
  String? action;
  String? did;
  String? key;
  String? msgId;
  String? authToken;

  RequestInfoNoc({
    this.apiId,
    this.ver,
    this.ts,
    this.action,
    this.did,
    this.key,
    this.msgId,
    this.authToken,
  });

  factory RequestInfoNoc.fromJson(Map<String, dynamic> json) => RequestInfoNoc(
        apiId: json["apiId"],
        ver: json["ver"],
        ts: json["ts"],
        action: json["action"],
        did: json["did"],
        key: json["key"],
        msgId: json["msgId"],
        authToken: json["authToken"],
      );

  Map<String, dynamic> toJson() => {
        "apiId": apiId,
        "ver": ver,
        "ts": ts,
        "action": action,
        "did": did,
        "key": key,
        "msgId": msgId,
        "authToken": authToken,
      };
}
