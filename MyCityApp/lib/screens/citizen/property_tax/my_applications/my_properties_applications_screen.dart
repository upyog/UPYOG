import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class MyPropertyApplications extends StatelessWidget {
  MyPropertyApplications({super.key});

  final AuthController _authController = Get.find<AuthController>();
  final _propertiesTaxController = Get.find<PropertiesTaxController>();

  void getMyProperties() {
    if (!_authController.isValidUser) return;
    _propertiesTaxController.getMyPropertiesStream(
      token: _authController.token!.accessToken!,
      isMyApplication: true,
    );
  }

  @override
  Widget build(BuildContext context) {
    _propertiesTaxController.setDefaultLimit();
    getMyProperties();

    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Obx(
          () => Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                getLocalizedString(
                  i18.propertyTax.MY_APPLICATION,
                  module: Modules.PT,
                ),
              ),
              Text(' (${_propertiesTaxController.length.value})'),
            ],
          ),
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Padding(
          padding: EdgeInsets.all(16.w),
          child: StreamBuilder(
            stream: _propertiesTaxController.streamCtrl.stream,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return showCircularIndicator();
              } else if (snapshot.hasError) {
                return networkErrorPage(context, () => getMyProperties());
              } else if (snapshot.data is String ||
                  snapshot.data == null ||
                  snapshot.hasData == false) {
                return const NoApplicationFoundWidget();
              }

              final myProperties = snapshot.data as PtMyProperties;
              final propertyList = _sortProperties(myProperties.properties);

              if (propertyList.isEmpty) {
                return const NoApplicationFoundWidget();
              }

              return _PropertyList(
                properties: propertyList,
                onLoadMore: () async {
                  await _propertiesTaxController.loadMore(
                    token: _authController.token!.accessToken!,
                    isMyApplication: true,
                  );
                },
                isLoading: _propertiesTaxController.isLoading,
              );
            },
          ),
        ),
      ),
    );
  }

  List<Property> _sortProperties(List<Property>? properties) {
    properties?.sort((a, b) {
      return DateTime.fromMillisecondsSinceEpoch(b.auditDetails!.createdTime!)
          .compareTo(
        DateTime.fromMillisecondsSinceEpoch(
          a.auditDetails!.createdTime!,
        ),
      );
    });
    return properties ?? [];
  }
}

class _PropertyList extends StatelessWidget {
  final List<Property> properties;
  final VoidCallback onLoadMore;
  final RxBool isLoading;

  const _PropertyList({
    required this.properties,
    required this.onLoadMore,
    required this.isLoading,
  });

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      shrinkWrap: true,
      itemCount:
          properties.length >= 10 ? properties.length + 1 : properties.length,
      itemBuilder: (context, index) {
        if (index == properties.length && properties.length >= 10) {
          return Obx(
            () => isLoading.value
                ? showCircularIndicator()
                : IconButton(
                    onPressed: onLoadMore,
                    icon: const Icon(
                      Icons.expand_circle_down_outlined,
                      size: 30,
                      color: BaseConfig.appThemeColor1,
                    ),
                  ),
          );
        } else {
          final property = properties[index];
          return _PropertyCard(property: property);
        }
      },
    );
  }
}

class _PropertyCard extends StatelessWidget {
  final Property property;

  const _PropertyCard({required this.property});

  @override
  Widget build(BuildContext context) {
    return ComplainCard(
      title: isNotNullOrEmpty(property.creationReason)
          ? getLocalizedString(
              '${i18.propertyTax.PT}${property.creationReason}'.toUpperCase(),
              module: Modules.PT,
            )
          : 'N/A',
      id: '${getLocalizedString(i18.propertyTax.APPLICATION_NO, module: Modules.PT)}: ${property.acknowledgementNumber}',
      address: '${getLocalizedString(
        i18.propertyTax.APPLICATION_CATEGORY,
        module: Modules.PT,
      )}: ${getLocalizedString(
        i18.propertyTax.PT_TAX,
        module: Modules.PT,
      )}',
      date: 'Date: ${property.auditDetails!.createdTime.toCustomDateFormat()}',
      sla:
          '${getLocalizedString(i18.propertyTax.PTUID, module: Modules.PT)}: ${property.propertyId ?? 'N/A'}',
      onTap: () {
        Get.toNamed(
          AppRoutes.PROPERTY_APPLICATIONS_DETAILS,
          arguments: property,
        );
      },
      status: getLocalizedString(
        '${i18.propertyTax.PT_COMMON}${property.status}'.toUpperCase(),
        module: Modules.PT,
      ),
      statusColor: getStatusColor('${property.status}'),
      statusBackColor: getStatusBackColor('${property.status}'),
    ).paddingOnly(bottom: 12);
  }
}
