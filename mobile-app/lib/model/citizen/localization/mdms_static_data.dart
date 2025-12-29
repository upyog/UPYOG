// To parse this JSON data, do
//
//     final mdmsStaticData = mdmsStaticDataFromJson(jsonString);

import 'dart:convert';

MdmsStaticData mdmsStaticDataFromJson(String str) {
  final jsonData = json.decode(str);
  return MdmsStaticData.fromJson(jsonData);
}

String mdmsStaticDataToJson(MdmsStaticData data) {
  final dyn = data.toJson();
  return json.encode(dyn);
}

class MdmsStaticData {
  dynamic responseInfo;
  MdmsRes? mdmsRes;

  MdmsStaticData({
    this.responseInfo,
    this.mdmsRes,
  });

  factory MdmsStaticData.fromJson(Map<String, dynamic> json) => MdmsStaticData(
        responseInfo: json["ResponseInfo"],
        mdmsRes:
            json["MdmsRes"] == null ? null : MdmsRes.fromJson(json["MdmsRes"]),
      );

  Map<String, dynamic> toJson() => {
        "ResponseInfo": responseInfo,
        "MdmsRes": mdmsRes?.toJson(),
      };
}

class MdmsRes {
  CommonMasters? commonMasters;
  BillingService? billingService;
  RainmakerPgr? rainmakerPgr;

  MdmsRes({
    this.commonMasters,
    this.billingService,
    this.rainmakerPgr,
  });

  factory MdmsRes.fromJson(Map<String, dynamic> json) => MdmsRes(
        commonMasters: json["common-masters"] == null
            ? null
            : CommonMasters.fromJson(json["common-masters"]),
        billingService: json["BillingService"] == null
            ? null
            : BillingService.fromJson(json["BillingService"]),
        rainmakerPgr: json["RAINMAKER-PGR"] == null
            ? null
            : RainmakerPgr.fromJson(json["RAINMAKER-PGR"]),
      );

  Map<String, dynamic> toJson() => {
        "common-masters": commonMasters?.toJson(),
        "BillingService": billingService?.toJson(),
        "RAINMAKER-PGR": rainmakerPgr?.toJson(),
      };
}

class CommonMasters {
  List<StaticDatum>? staticData;

  CommonMasters({
    this.staticData,
  });

  factory CommonMasters.fromJson(Map<String, dynamic> json) => CommonMasters(
        staticData: json["StaticData"] != null
            ? List<StaticDatum>.from(
                json["StaticData"].map((x) => StaticDatum.fromJson(x)),
              )
            : null,
      );

  Map<String, dynamic> toJson() => {
        "StaticData": staticData != null
            ? List<dynamic>.from(staticData!.map((x) => x.toJson()))
            : [],
      };
}

class StaticDatum {
  Pt? pt;
  Mcollect? tl;
  Mcollect? mcollect;
  Mcollect? pgr;
  Mcollect? obps;
  Pt? ws;

  StaticDatum({
    this.pt,
    this.tl,
    this.mcollect,
    this.pgr,
    this.obps,
    this.ws,
  });

  factory StaticDatum.fromJson(Map<String, dynamic> json) => StaticDatum(
        pt: Pt.fromJson(json["PT"]),
        tl: Mcollect.fromJson(json["TL"]),
        mcollect: Mcollect.fromJson(json["MCOLLECT"]),
        pgr: Mcollect.fromJson(json["PGR"]),
        obps: Mcollect.fromJson(json["OBPS"]),
        ws: Pt.fromJson(json["WS"]),
      );

  Map<String, dynamic> toJson() => {
        "PT": pt!.toJson(),
        "TL": tl!.toJson(),
        "MCOLLECT": mcollect!.toJson(),
        "PGR": pgr!.toJson(),
        "OBPS": obps!.toJson(),
        "WS": ws!.toJson(),
      };
}

class Mcollect {
  Helpline? helpline;
  String? serviceCenter;
  String? viewMapLocation;
  String? validity;

  Mcollect({
    this.helpline,
    this.serviceCenter,
    this.viewMapLocation,
    this.validity,
  });

  factory Mcollect.fromJson(Map<String, dynamic> json) => Mcollect(
        helpline: Helpline.fromJson(json["helpline"]),
        serviceCenter: json["serviceCenter"],
        viewMapLocation: json["viewMapLocation"],
        validity: json["validity"],
      );

  Map<String, dynamic> toJson() => {
        "helpline": helpline!.toJson(),
        "serviceCenter": serviceCenter,
        "viewMapLocation": viewMapLocation,
        "validity": validity,
      };
}

class Helpline {
  String? contactOne;
  String? contactTwo;

  Helpline({
    this.contactOne,
    this.contactTwo,
  });

  factory Helpline.fromJson(Map<String, dynamic> json) => Helpline(
        contactOne: json["contactOne"],
        contactTwo: json["contactTwo"],
      );

  Map<String, dynamic> toJson() => {
        "contactOne": contactOne,
        "contactTwo": contactTwo,
      };
}

class Pt {
  String? payViaWhatsApp;
  Helpline? helpline;
  String? serviceCenter;
  String? staticDataOne;
  String? staticDataTwo;
  String? viewMapLocation;

  Pt({
    this.payViaWhatsApp,
    this.helpline,
    this.serviceCenter,
    this.staticDataOne,
    this.staticDataTwo,
    this.viewMapLocation,
  });

  factory Pt.fromJson(Map<String, dynamic> json) => Pt(
        payViaWhatsApp: json["payViaWhatsApp"],
        helpline: Helpline.fromJson(json["helpline"]),
        serviceCenter: json["serviceCenter"],
        staticDataOne: json["staticDataOne"],
        staticDataTwo: json["staticDataTwo"],
        viewMapLocation: json["viewMapLocation"],
      );

  Map<String, dynamic> toJson() => {
        "payViaWhatsApp": payViaWhatsApp,
        "helpline": helpline!.toJson(),
        "serviceCenter": serviceCenter,
        "staticDataOne": staticDataOne,
        "staticDataTwo": staticDataTwo,
        "viewMapLocation": viewMapLocation,
      };
}

//UC collection
class BillingService {
  List<TaxHeadMaster>? taxHeadMaster;
  List<BusinessService>? businessService;
  List<TaxPeriod>? taxPeriod;

  BillingService({
    this.taxHeadMaster,
    this.businessService,
    this.taxPeriod,
  });

  factory BillingService.fromJson(Map<String, dynamic> json) => BillingService(
        taxHeadMaster: json["TaxHeadMaster"] == null
            ? []
            : List<TaxHeadMaster>.from(
                json["TaxHeadMaster"]!.map((x) => TaxHeadMaster.fromJson(x)),
              ),
        businessService: json["BusinessService"] == null
            ? []
            : List<BusinessService>.from(
                json["BusinessService"]!
                    .map((x) => BusinessService.fromJson(x)),
              ),
        taxPeriod: json["TaxPeriod"] == null
            ? []
            : List<TaxPeriod>.from(
                json["TaxPeriod"]!.map((x) => TaxPeriod.fromJson(x)),
              ),
      );

  Map<String, dynamic> toJson() => {
        "TaxHeadMaster": taxHeadMaster == null
            ? []
            : List<dynamic>.from(taxHeadMaster!.map((x) => x.toJson())),
        "BusinessService": businessService == null
            ? []
            : List<dynamic>.from(businessService!.map((x) => x.toJson())),
        "TaxPeriod": taxPeriod == null
            ? []
            : List<dynamic>.from(taxPeriod!.map((x) => x.toJson())),
      };
}

class BusinessService {
  String? businessService;
  String? code;
  List<String>? collectionModesNotAllowed;
  bool? partPaymentAllowed;
  bool? isAdvanceAllowed;
  int? demandUpdateTime;
  bool? isVoucherCreationEnabled;
  bool? isActive;
  String? type;
  String? billGineiUrl;

  BusinessService({
    this.businessService,
    this.code,
    this.collectionModesNotAllowed,
    this.partPaymentAllowed,
    this.isAdvanceAllowed,
    this.demandUpdateTime,
    this.isVoucherCreationEnabled,
    this.isActive,
    this.type,
    this.billGineiUrl,
  });

  factory BusinessService.fromJson(Map<String, dynamic> json) =>
      BusinessService(
        businessService: json["businessService"],
        code: json["code"],
        collectionModesNotAllowed: json["collectionModesNotAllowed"] == null
            ? []
            : List<String>.from(
                json["collectionModesNotAllowed"]!.map((x) => x),
              ),
        partPaymentAllowed: json["partPaymentAllowed"],
        isAdvanceAllowed: json["isAdvanceAllowed"],
        demandUpdateTime: json["demandUpdateTime"],
        isVoucherCreationEnabled: json["isVoucherCreationEnabled"],
        isActive: json["isActive"],
        type: json["type"],
        billGineiUrl: json["billGineiURL"],
      );

  Map<String, dynamic> toJson() => {
        "businessService": businessService,
        "code": code,
        "collectionModesNotAllowed": collectionModesNotAllowed == null
            ? []
            : List<dynamic>.from(collectionModesNotAllowed!.map((x) => x)),
        "partPaymentAllowed": partPaymentAllowed,
        "isAdvanceAllowed": isAdvanceAllowed,
        "demandUpdateTime": demandUpdateTime,
        "isVoucherCreationEnabled": isVoucherCreationEnabled,
        "isActive": isActive,
        "type": type,
        "billGineiURL": billGineiUrl,
      };
}

class TaxHeadMaster {
  String? category;
  String? service;
  String? name;
  String? code;
  bool? isDebit;
  bool? isActualDemand;
  dynamic order;
  bool? isRequired;
  bool? isBillamend;
  bool? isDiscountApplicable;
  String? roadType;

  TaxHeadMaster({
    this.category,
    this.service,
    this.name,
    this.code,
    this.isDebit,
    this.isActualDemand,
    this.order,
    this.isRequired,
    this.isBillamend,
    this.isDiscountApplicable,
    this.roadType,
  });

  factory TaxHeadMaster.fromJson(Map<String, dynamic> json) => TaxHeadMaster(
        category: json["category"],
        service: json["service"],
        name: json["name"],
        code: json["code"],
        isDebit: json["isDebit"],
        isActualDemand: json["isActualDemand"],
        order: json["order"],
        isRequired: json["isRequired"],
        isBillamend: json["IsBillamend"],
        isDiscountApplicable: json["isDiscountApplicable"],
        roadType: json["roadType"],
      );

  Map<String, dynamic> toJson() => {
        "category": category,
        "service": service,
        "name": name,
        "code": code,
        "isDebit": isDebit,
        "isActualDemand": isActualDemand,
        "order": order,
        "isRequired": isRequired,
        "IsBillamend": isBillamend,
        "isDiscountApplicable": isDiscountApplicable,
        "roadType": roadType,
      };
}

class TaxPeriod {
  int? fromDate;
  int? toDate;
  String? periodCycle;
  String? service;
  String? code;
  String? financialYear;

  TaxPeriod({
    this.fromDate,
    this.toDate,
    this.periodCycle,
    this.service,
    this.code,
    this.financialYear,
  });

  factory TaxPeriod.fromJson(Map<String, dynamic> json) => TaxPeriod(
        fromDate: json["fromDate"],
        toDate: json["toDate"],
        periodCycle: json["periodCycle"],
        service: json["service"],
        code: json["code"],
        financialYear: json["financialYear"],
      );

  Map<String, dynamic> toJson() => {
        "fromDate": fromDate,
        "toDate": toDate,
        "periodCycle": periodCycle,
        "service": service,
        "code": code,
        "financialYear": financialYear,
      };
}

//Rainmaker-pgr
class RainmakerPgr {
  List<ServiceDef>? serviceDefs;

  RainmakerPgr({
    this.serviceDefs,
  });

  factory RainmakerPgr.fromJson(Map<String, dynamic> json) => RainmakerPgr(
        serviceDefs: json["ServiceDefs"] == null
            ? []
            : List<ServiceDef>.from(
                json["ServiceDefs"]!.map((x) => ServiceDef.fromJson(x))),
      );

  Map<String, dynamic> toJson() => {
        "ServiceDefs": serviceDefs == null
            ? []
            : List<dynamic>.from(serviceDefs!.map((x) => x.toJson())),
      };
}

class ServiceDef {
  String? name;
  bool? active;
  String? keywords;
  String? menuPath;
  int? slaHours;
  String? department;
  String? serviceCode;
  int? order;

  ServiceDef({
    this.name,
    this.active,
    this.keywords,
    this.menuPath,
    this.slaHours,
    this.department,
    this.serviceCode,
    this.order,
  });

  factory ServiceDef.fromJson(Map<String, dynamic> json) => ServiceDef(
        name: json["name"],
        active: json["active"],
        keywords: json["keywords"],
        menuPath: json["menuPath"],
        slaHours: json["slaHours"],
        department: json["department"],
        serviceCode: json["serviceCode"],
        order: json["order"],
      );

  Map<String, dynamic> toJson() => {
        "name": name,
        "active": active,
        "keywords": keywords,
        "menuPath": menuPath,
        "slaHours": slaHours,
        "department": department,
        "serviceCode": serviceCode,
        "order": order,
      };
}
