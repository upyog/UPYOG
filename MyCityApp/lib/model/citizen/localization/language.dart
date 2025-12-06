// To parse this JSON data, do
//
//     final languageList = languageListFromJson(jsonString);

import 'dart:convert';

LanguageList languageListFromJson(String str) =>
    LanguageList.fromJson(json.decode(str));

String languageListToJson(LanguageList data) => json.encode(data.toJson());

class LanguageList {
  dynamic responseInfo;
  MdmsRes? mdmsRes;

  LanguageList({
    this.responseInfo,
    this.mdmsRes,
  });

  factory LanguageList.fromJson(Map<String, dynamic> json) => LanguageList(
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
  MdmsResTenant? tenant;

  MdmsRes({
    this.commonMasters,
    this.tenant,
  });

  factory MdmsRes.fromJson(Map<String, dynamic> json) => MdmsRes(
        commonMasters: json["common-masters"] == null
            ? null
            : CommonMasters.fromJson(json["common-masters"]),
        tenant: json["tenant"] == null
            ? null
            : MdmsResTenant.fromJson(json["tenant"]),
      );

  Map<String, dynamic> toJson() => {
        "common-masters": commonMasters?.toJson(),
        "tenant": tenant?.toJson(),
      };
}

class CommonMasters {
  List<Department>? designation;
  List<Department>? department;
  List<StateInfo>? stateInfo;
  List<WfSlaConfig>? wfSlaConfig;
  List<UiHomePage>? uiHomePage;
  List<CitizenConsentForm>? citizenConsentForm;
  List<StaticDatum>? staticData;

  CommonMasters({
    this.designation,
    this.department,
    this.stateInfo,
    this.wfSlaConfig,
    this.uiHomePage,
    this.citizenConsentForm,
    this.staticData,
  });

  factory CommonMasters.fromJson(Map<String, dynamic> json) => CommonMasters(
        designation: json["Designation"] == null
            ? []
            : List<Department>.from(
                json["Designation"]!.map((x) => Department.fromJson(x)),
              ),
        department: json["Department"] == null
            ? []
            : List<Department>.from(
                json["Department"]!.map((x) => Department.fromJson(x)),
              ),
        stateInfo: json["StateInfo"] == null
            ? []
            : List<StateInfo>.from(
                json["StateInfo"]!.map((x) => StateInfo.fromJson(x)),
              ),
        wfSlaConfig: json["wfSlaConfig"] == null
            ? []
            : List<WfSlaConfig>.from(
                json["wfSlaConfig"]!.map((x) => WfSlaConfig.fromJson(x)),
              ),
        uiHomePage: json["uiHomePage"] == null
            ? []
            : List<UiHomePage>.from(
                json["uiHomePage"]!.map((x) => UiHomePage.fromJson(x)),
              ),
        citizenConsentForm: json["CitizenConsentForm"] == null
            ? []
            : List<CitizenConsentForm>.from(
                json["CitizenConsentForm"]!
                    .map((x) => CitizenConsentForm.fromJson(x)),
              ),
        staticData: json["StaticDatum"] == null
            ? []
            : List<StaticDatum>.from(
                json["StaticDatum"]!.map((x) => StaticDatum.fromJson(x)),
              ),
      );

  Map<String, dynamic> toJson() => {
        "Designation": designation == null
            ? []
            : List<dynamic>.from(designation!.map((x) => x.toJson())),
        "Department": department == null
            ? []
            : List<dynamic>.from(department!.map((x) => x.toJson())),
        "StateInfo": stateInfo == null
            ? []
            : List<dynamic>.from(stateInfo!.map((x) => x.toJson())),
        "wfSlaConfig": wfSlaConfig == null
            ? []
            : List<dynamic>.from(wfSlaConfig!.map((x) => x.toJson())),
        "uiHomePage": uiHomePage == null
            ? []
            : List<dynamic>.from(uiHomePage!.map((x) => x.toJson())),
        "CitizenConsentForm": citizenConsentForm == null
            ? []
            : List<dynamic>.from(citizenConsentForm!.map((x) => x.toJson())),
        "StaticDatum": staticData == null
            ? []
            : List<dynamic>.from(staticData!.map((x) => x.toJson())),
      };
}

class CitizenConsentForm {
  bool? isCitizenConsentFormEnabled;
  List<CheckBoxLabel>? checkBoxLabels;

  CitizenConsentForm({
    this.isCitizenConsentFormEnabled,
    this.checkBoxLabels,
  });

  factory CitizenConsentForm.fromJson(Map<String, dynamic> json) =>
      CitizenConsentForm(
        isCitizenConsentFormEnabled: json["isCitizenConsentFormEnabled"],
        checkBoxLabels: json["checkBoxLabels"] == null
            ? []
            : List<CheckBoxLabel>.from(
                json["checkBoxLabels"]!.map((x) => CheckBoxLabel.fromJson(x)),
              ),
      );

  Map<String, dynamic> toJson() => {
        "isCitizenConsentFormEnabled": isCitizenConsentFormEnabled,
        "checkBoxLabels": checkBoxLabels == null
            ? []
            : List<dynamic>.from(checkBoxLabels!.map((x) => x.toJson())),
      };
}

class CheckBoxLabel {
  String? linkPrefix;
  String? link;
  String? linkId;
  String? linkPostfix;
  String? enIn;

  CheckBoxLabel({
    this.linkPrefix,
    this.link,
    this.linkId,
    this.linkPostfix,
    this.enIn,
  });

  factory CheckBoxLabel.fromJson(Map<String, dynamic> json) => CheckBoxLabel(
        linkPrefix: json["linkPrefix"],
        link: json["link"],
        linkId: json["linkId"],
        linkPostfix: json["linkPostfix"],
        enIn: json["en_IN"],
      );

  Map<String, dynamic> toJson() => {
        "linkPrefix": linkPrefix,
        "link": link,
        "linkId": linkId,
        "linkPostfix": linkPostfix,
        "en_IN": enIn,
      };
}

class Department {
  String? name;
  String? code;
  bool? active;
  String? description;

  Department({
    this.name,
    this.code,
    this.active,
    this.description,
  });

  factory Department.fromJson(Map<String, dynamic> json) => Department(
        name: json["name"],
        code: json["code"],
        active: json["active"],
        description: json["description"],
      );

  Map<String, dynamic> toJson() => {
        "name": name,
        "code": code,
        "active": active,
        "description": description,
      };
}

class StateInfo {
  String? name;
  String? code;
  String? qrCodeUrl;
  String? bannerUrl;
  String? logoUrl;
  String? logoUrlWhite;
  String? statelogo;
  bool? hasLocalisation;
  DefaultUrl? defaultUrl;
  List<Languages>? languages;
  List<Languages>? localizationModules;

  StateInfo({
    this.name,
    this.code,
    this.qrCodeUrl,
    this.bannerUrl,
    this.logoUrl,
    this.logoUrlWhite,
    this.statelogo,
    this.hasLocalisation,
    this.defaultUrl,
    this.languages,
    this.localizationModules,
  });

  factory StateInfo.fromJson(Map<String, dynamic> json) => StateInfo(
        name: json["name"],
        code: json["code"],
        qrCodeUrl: json["qrCodeURL"],
        bannerUrl: json["bannerUrl"],
        logoUrl: json["logoUrl"],
        logoUrlWhite: json["logoUrlWhite"],
        statelogo: json["statelogo"],
        hasLocalisation: json["hasLocalisation"],
        defaultUrl: json["defaultUrl"] == null
            ? null
            : DefaultUrl.fromJson(json["defaultUrl"]),
        languages: json["languages"] == null
            ? []
            : List<Languages>.from(
                json["languages"]!.map((x) => Languages.fromJson(x)),
              ),
        localizationModules: json["localizationModules"] == null
            ? []
            : List<Languages>.from(
                json["localizationModules"]!.map((x) => Languages.fromJson(x)),
              ),
      );

  Map<String, dynamic> toJson() => {
        "name": name,
        "code": code,
        "qrCodeURL": qrCodeUrl,
        "bannerUrl": bannerUrl,
        "logoUrl": logoUrl,
        "logoUrlWhite": logoUrlWhite,
        "statelogo": statelogo,
        "hasLocalisation": hasLocalisation,
        "defaultUrl": defaultUrl?.toJson(),
        "languages": languages == null
            ? []
            : List<dynamic>.from(languages!.map((x) => x.toJson())),
        "localizationModules": localizationModules == null
            ? []
            : List<dynamic>.from(localizationModules!.map((x) => x.toJson())),
      };
}

class DefaultUrl {
  String? citizen;
  String? employee;

  DefaultUrl({
    this.citizen,
    this.employee,
  });

  factory DefaultUrl.fromJson(Map<String, dynamic> json) => DefaultUrl(
        citizen: json["citizen"],
        employee: json["employee"],
      );

  Map<String, dynamic> toJson() => {
        "citizen": citizen,
        "employee": employee,
      };
}

class Languages {
  String? label;
  String? value;
  bool isSelected = false;

  Languages({
    this.label,
    this.value,
    this.isSelected = false,
  });

  factory Languages.fromJson(Map<String, dynamic> json) => Languages(
        label: json["label"],
        value: json["value"],
      );

  Map<String, dynamic> toJson() => {
        "label": label,
        "value": value,
      };
}

class UiHomePage {
  AppBannerDesktop? appBannerDesktop;
  AppBannerDesktop? appBannerMobile;
  CitizenServicesCard? citizenServicesCard;
  CitizenServicesCard? informationAndUpdatesCard;
  CitizenServicesCard? whatsNewSection;
  AppBannerDesktop? whatsAppBannerDesktop;
  AppBannerDesktop? whatsAppBannerMobile;

  UiHomePage({
    this.appBannerDesktop,
    this.appBannerMobile,
    this.citizenServicesCard,
    this.informationAndUpdatesCard,
    this.whatsNewSection,
    this.whatsAppBannerDesktop,
    this.whatsAppBannerMobile,
  });

  factory UiHomePage.fromJson(Map<String, dynamic> json) => UiHomePage(
        appBannerDesktop: json["appBannerDesktop"] == null
            ? null
            : AppBannerDesktop.fromJson(json["appBannerDesktop"]),
        appBannerMobile: json["appBannerMobile"] == null
            ? null
            : AppBannerDesktop.fromJson(json["appBannerMobile"]),
        citizenServicesCard: json["citizenServicesCard"] == null
            ? null
            : CitizenServicesCard.fromJson(json["citizenServicesCard"]),
        informationAndUpdatesCard: json["informationAndUpdatesCard"] == null
            ? null
            : CitizenServicesCard.fromJson(json["informationAndUpdatesCard"]),
        whatsNewSection: json["whatsNewSection"] == null
            ? null
            : CitizenServicesCard.fromJson(json["whatsNewSection"]),
        whatsAppBannerDesktop: json["whatsAppBannerDesktop"] == null
            ? null
            : AppBannerDesktop.fromJson(json["whatsAppBannerDesktop"]),
        whatsAppBannerMobile: json["whatsAppBannerMobile"] == null
            ? null
            : AppBannerDesktop.fromJson(json["whatsAppBannerMobile"]),
      );

  Map<String, dynamic> toJson() => {
        "appBannerDesktop": appBannerDesktop?.toJson(),
        "appBannerMobile": appBannerMobile?.toJson(),
        "citizenServicesCard": citizenServicesCard?.toJson(),
        "informationAndUpdatesCard": informationAndUpdatesCard?.toJson(),
        "whatsNewSection": whatsNewSection?.toJson(),
        "whatsAppBannerDesktop": whatsAppBannerDesktop?.toJson(),
        "whatsAppBannerMobile": whatsAppBannerMobile?.toJson(),
      };
}

class AppBannerDesktop {
  String? code;
  String? name;
  String? bannerUrl;
  bool? enabled;
  String? navigationUrl;

  AppBannerDesktop({
    this.code,
    this.name,
    this.bannerUrl,
    this.enabled,
    this.navigationUrl,
  });

  factory AppBannerDesktop.fromJson(Map<String, dynamic> json) =>
      AppBannerDesktop(
        code: json["code"],
        name: json["name"],
        bannerUrl: json["bannerUrl"],
        enabled: json["enabled"],
        navigationUrl: json["navigationUrl"],
      );

  Map<String, dynamic> toJson() => {
        "code": code,
        "name": name,
        "bannerUrl": bannerUrl,
        "enabled": enabled,
        "navigationUrl": navigationUrl,
      };
}

class CitizenServicesCard {
  String? code;
  String? name;
  bool? enabled;
  String? headerLabel;
  SideOption? sideOption;
  List<Prop>? props;

  CitizenServicesCard({
    this.code,
    this.name,
    this.enabled,
    this.headerLabel,
    this.sideOption,
    this.props,
  });

  factory CitizenServicesCard.fromJson(Map<String, dynamic> json) =>
      CitizenServicesCard(
        code: json["code"],
        name: json["name"],
        enabled: json["enabled"],
        headerLabel: json["headerLabel"],
        sideOption: json["sideOption"] == null
            ? null
            : SideOption.fromJson(json["sideOption"]),
        props: json["props"] == null
            ? []
            : List<Prop>.from(json["props"]!.map((x) => Prop.fromJson(x))),
      );

  Map<String, dynamic> toJson() => {
        "code": code,
        "name": name,
        "enabled": enabled,
        "headerLabel": headerLabel,
        "sideOption": sideOption?.toJson(),
        "props": props == null
            ? []
            : List<dynamic>.from(props!.map((x) => x.toJson())),
      };
}

class Prop {
  String? code;
  String? name;
  String? label;
  bool? enabled;
  String? navigationUrl;

  Prop({
    this.code,
    this.name,
    this.label,
    this.enabled,
    this.navigationUrl,
  });

  factory Prop.fromJson(Map<String, dynamic> json) => Prop(
        code: json["code"],
        name: json["name"],
        label: json["label"],
        enabled: json["enabled"],
        navigationUrl: json["navigationUrl"],
      );

  Map<String, dynamic> toJson() => {
        "code": code,
        "name": name,
        "label": label,
        "enabled": enabled,
        "navigationUrl": navigationUrl,
      };
}

class SideOption {
  String? name;
  bool? enabled;
  String? navigationUrl;

  SideOption({
    this.name,
    this.enabled,
    this.navigationUrl,
  });

  factory SideOption.fromJson(Map<String, dynamic> json) => SideOption(
        name: json["name"],
        enabled: json["enabled"],
        navigationUrl: json["navigationUrl"],
      );

  Map<String, dynamic> toJson() => {
        "name": name,
        "enabled": enabled,
        "navigationUrl": navigationUrl,
      };
}

class WfSlaConfig {
  dynamic slotPercentage;
  String? positiveSlabColor;
  String? negativeSlabColor;
  String? middleSlabColor;

  WfSlaConfig({
    this.slotPercentage,
    this.positiveSlabColor,
    this.negativeSlabColor,
    this.middleSlabColor,
  });

  factory WfSlaConfig.fromJson(Map<String, dynamic> json) => WfSlaConfig(
        slotPercentage: json["slotPercentage"],
        positiveSlabColor: json["positiveSlabColor"],
        negativeSlabColor: json["negativeSlabColor"],
        middleSlabColor: json["middleSlabColor"],
      );

  Map<String, dynamic> toJson() => {
        "slotPercentage": slotPercentage,
        "positiveSlabColor": positiveSlabColor,
        "negativeSlabColor": negativeSlabColor,
        "middleSlabColor": middleSlabColor,
      };
}

class MdmsResTenant {
  List<TenantTenant>? tenants;
  List<Citymodule>? citymodule;

  MdmsResTenant({
    this.tenants,
    this.citymodule,
  });

  factory MdmsResTenant.fromJson(Map<String, dynamic> json) => MdmsResTenant(
        tenants: json["tenants"] == null
            ? []
            : List<TenantTenant>.from(
                json["tenants"]!.map((x) => TenantTenant.fromJson(x)),
              ),
        citymodule: json["citymodule"] == null
            ? []
            : List<Citymodule>.from(
                json["citymodule"]!.map((x) => Citymodule.fromJson(x)),
              ),
      );

  Map<String, dynamic> toJson() => {
        "tenants": tenants == null
            ? []
            : List<dynamic>.from(tenants!.map((x) => x.toJson())),
        "citymodule": citymodule == null
            ? []
            : List<dynamic>.from(citymodule!.map((x) => x.toJson())),
      };
}

class Citymodule {
  String? module;
  String? code;
  bool? active;
  int? order;
  List<CitymoduleTenant>? tenants;
  String? bannerImage;

  Citymodule({
    this.module,
    this.code,
    this.active,
    this.order,
    this.tenants,
    this.bannerImage,
  });

  factory Citymodule.fromJson(Map<String, dynamic> json) => Citymodule(
        module: json["module"],
        code: json["code"],
        active: json["active"],
        order: json["order"],
        tenants: json["tenants"] == null
            ? []
            : List<CitymoduleTenant>.from(
                json["tenants"]!.map((x) => CitymoduleTenant.fromJson(x)),
              ),
        bannerImage: json["bannerImage"],
      );

  Map<String, dynamic> toJson() => {
        "module": module,
        "code": code,
        "active": active,
        "order": order,
        "tenants": tenants == null
            ? []
            : List<dynamic>.from(tenants!.map((x) => x.toJson())),
        "bannerImage": bannerImage,
      };
}

class CitymoduleTenant {
  String? code;

  CitymoduleTenant({
    this.code,
  });

  factory CitymoduleTenant.fromJson(Map<String, dynamic> json) =>
      CitymoduleTenant(
        code: json["code"],
      );

  Map<String, dynamic> toJson() => {
        "code": code,
      };
}

class TenantTenant {
  String? code;
  String? name;
  String? description;
  List<int>? pincode;
  String? logoId;
  dynamic imageId;
  String? domainUrl;
  String? type;
  dynamic twitterUrl;
  dynamic facebookUrl;
  String? emailId;
  OfficeTimings? officeTimings;
  bool? isPopular;
  City? city;
  String? address;
  String? contactNumber;
  String? helpLineNumber;

  TenantTenant({
    this.code,
    this.name,
    this.description,
    this.pincode,
    this.logoId,
    this.imageId,
    this.domainUrl,
    this.type,
    this.twitterUrl,
    this.facebookUrl,
    this.emailId,
    this.officeTimings,
    this.isPopular,
    this.city,
    this.address,
    this.contactNumber,
    this.helpLineNumber,
  });

  factory TenantTenant.fromJson(Map<String, dynamic> json) => TenantTenant(
        code: json["code"] ?? "",
        name: json["name"] ?? "",
        description: json["description"] ?? "",
        pincode: json["pincode"] == null
            ? []
            : List<int>.from(json["pincode"]!.map((x) => x)),
        logoId: json["logoId"] ?? "",
        imageId: json["imageId"] ?? "",
        domainUrl: json["domainUrl"] ?? "",
        type: json["type"] ?? "",
        twitterUrl: json["twitterUrl"] ?? "",
        facebookUrl: json["facebookUrl"] ?? "",
        emailId: json["emailId"] ?? "",
        officeTimings: json["OfficeTimings"] == null
            ? null
            : OfficeTimings.fromJson(json["OfficeTimings"]),
        isPopular: json["isPopular"] ?? false,
        city: json["city"] == null ? null : City.fromJson(json["city"]),
        address: json["address"] ?? "",
        contactNumber: json["contactNumber"] ?? '',
        helpLineNumber: json["helpLineNumber"] ?? '',
      );

  Map<String, dynamic> toJson() => {
        "code": code,
        "name": name,
        "description": description,
        "pincode":
            pincode == null ? [] : List<dynamic>.from(pincode!.map((x) => x)),
        "logoId": logoId,
        "imageId": imageId,
        "domainUrl": domainUrl,
        "type": type,
        "twitterUrl": twitterUrl,
        "facebookUrl": facebookUrl,
        "emailId": emailId,
        "OfficeTimings": officeTimings?.toJson(),
        "isPopular": isPopular,
        "city": city?.toJson(),
        "address": address,
        "contactNumber": contactNumber,
        "helpLineNumber": helpLineNumber,
      };
}

class City {
  String? name;
  String? localName;
  String? districtCode;
  String? districtName;
  String? districtTenantCode;
  String? regionName;
  String? ulbGrade;
  double? longitude;
  double? latitude;
  dynamic shapeFileLocation;
  dynamic captcha;
  String? code;
  String? ddrName;

  City({
    this.name,
    this.localName,
    this.districtCode,
    this.districtName,
    this.districtTenantCode,
    this.regionName,
    this.ulbGrade,
    this.longitude,
    this.latitude,
    this.shapeFileLocation,
    this.captcha,
    this.code,
    this.ddrName,
  });

  factory City.fromJson(Map<String, dynamic> json) => City(
        name: json["name"],
        localName: json["localName"],
        districtCode: json["districtCode"],
        districtName: json["districtName"],
        districtTenantCode: json["districtTenantCode"],
        regionName: json["regionName"],
        ulbGrade: json["ulbGrade"],
        longitude: json["longitude"]?.toDouble(),
        latitude: json["latitude"]?.toDouble(),
        shapeFileLocation: json["shapeFileLocation"],
        captcha: json["captcha"],
        code: json["code"],
        ddrName: json["ddrName"],
      );

  Map<String, dynamic> toJson() => {
        "name": name,
        "localName": localName,
        "districtCode": districtCode,
        "districtName": districtName,
        "districtTenantCode": districtTenantCode,
        "regionName": regionName,
        "ulbGrade": ulbGrade,
        "longitude": longitude,
        "latitude": latitude,
        "shapeFileLocation": shapeFileLocation,
        "captcha": captcha,
        "code": code,
        "ddrName": ddrName,
      };
}

class OfficeTimings {
  String? monFri;
  String? sat;

  OfficeTimings({
    this.monFri,
    this.sat,
  });

  factory OfficeTimings.fromJson(Map<String, dynamic> json) => OfficeTimings(
        monFri: json["Mon - Fri"],
        sat: json["Sat"],
      );

  Map<String, dynamic> toJson() => {
        "Mon - Fri": monFri,
        "Sat": sat,
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
