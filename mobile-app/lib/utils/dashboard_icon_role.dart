import 'package:flutter/material.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';

class DashboardIcon {
  final String title;
  final String icon;
  final List<String>? roles;
  final IconData? flutterIcon;
  DashboardIcon({
    required this.title,
    required this.icon,
    this.roles,
    this.flutterIcon,
  });
}

// Get dashboard card
final List<DashboardIcon> serviceNamesCitizen = [
  DashboardIcon(
    title: i18.common.HELP_GRIEVANCE,
    icon: BaseConfig.helpGrievanceSvgIcon,
    flutterIcon: Icons.feedback_outlined,
  ),
  DashboardIcon(
    title: i18.common.TRADE_LICENSE,
    icon: BaseConfig.tradeLicenseSvgIcon,
    flutterIcon: Icons.work_outline,
  ),
  DashboardIcon(
    title: i18.common.WATER_SEWERAGE,
    flutterIcon: Icons.water_drop_outlined,
    icon: BaseConfig.waterSvg,
  ),
  DashboardIcon(
    title: i18.common.PROPERTY_TAX,
    icon: BaseConfig.propertySvg,
    flutterIcon: Icons.business_outlined,
  ),
  DashboardIcon(
    title: i18.common.BUILDING_PLAN_APPROVAL,
    icon: BaseConfig.buildingApprovalSvgIcon,
    flutterIcon: Icons.fact_check_outlined,
  ),
  DashboardIcon(
    title: i18.common.DESULDGING_SERVICES,
    flutterIcon: Icons.local_shipping_outlined,
    icon: BaseConfig.deslaudgingSvgIcon,
  ),
  DashboardIcon(
    title: i18.common.FIRE_NOC,
    icon: BaseConfig.fireNocSvgIcon,
    flutterIcon: Icons.local_fire_department_outlined,
  ),
  DashboardIcon(
    title: i18.common.MCOLLECT,
    flutterIcon: Icons.account_balance_wallet_outlined,
    icon: '',
  ),
];

final List<DashboardIcon> serviceNames = [
  DashboardIcon(
    title: i18.common.HELP_GRIEVANCE,
    icon: BaseConfig.helpGrievanceSvgIcon,
    flutterIcon: Icons.feedback_outlined,
  ),
  DashboardIcon(
    title: i18.common.TRADE_LICENSE,
    icon: BaseConfig.tradeLicenseSvgIcon,
    flutterIcon: Icons.work_outline,
    roles: [
      InspectorType.TL_FIELD_INSPECTOR.name,
    ],
  ),
  DashboardIcon(
    title: i18.common.WATER,
    flutterIcon: Icons.water_drop_outlined,
    icon: BaseConfig.waterSvg,
    roles: [
      InspectorType.WS_FIELD_INSPECTOR.name,
      InspectorType.SW_FIELD_INSPECTOR.name,
    ],
  ),
  DashboardIcon(
    title: i18.common.PROPERTY_TAX,
    icon: BaseConfig.propertySvg,
    flutterIcon: Icons.business_outlined,
    roles: [
      InspectorType.PT_FIELD_INSPECTOR.name,
      InspectorType.PT_CEMP_INSPECTOR.name,
      InspectorType.PT_APPROVER_INSPECTOR.name,
    ],
  ),
  DashboardIcon(
    title: i18.common.BUILDING_PLAN_APPROVAL,
    icon: BaseConfig.buildingApprovalSvgIcon,
    flutterIcon: Icons.fact_check_outlined,
    roles: [
      InspectorType.BPA_FIELD_INSPECTOR.name,
    ],
  ),
  DashboardIcon(
    title: i18.common.DESULDGING_SERVICES,
    flutterIcon: Icons.local_shipping_outlined,
    icon: BaseConfig.deslaudgingSvgIcon,
  ),
  DashboardIcon(
    title: i18.common.EMP_FIRE_NOC,
    icon: BaseConfig.fireNocSvgIcon,
    flutterIcon: Icons.local_fire_department_outlined,
    roles: [
      InspectorType.NOC_FIELD_INSPECTOR.name,
    ],
  ),
  DashboardIcon(
    title: i18.common.SEWERAGE,
    icon: BaseConfig.waterSvg,
    flutterIcon: Icons.water_drop_outlined,
    roles: [
      InspectorType.WS_FIELD_INSPECTOR.name,
      InspectorType.SW_FIELD_INSPECTOR.name,
    ],
  ),
  DashboardIcon(
    title: i18.common.UC_COLLECT,
    flutterIcon: Icons.domain_outlined,
    icon: BaseConfig.ucCollectIconSvg,
    roles: [
      InspectorType.UC_EMP_INSPECTOR.name,
    ],
  ),
  DashboardIcon(
    title: i18.common.HELP_GRIEVANCE,
    flutterIcon: Icons.feedback,
    icon: BaseConfig.helpGrievanceSvgIcon,
    roles: [
      InspectorType.PGR_LME_INSPECTOR.name,
    ],
  ),
];
