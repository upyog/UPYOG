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

class MyPropertyApplications extends StatefulWidget {
  const MyPropertyApplications({super.key});

  @override
  State<MyPropertyApplications> createState() => _MyPropertyApplicationsState();
}

class _MyPropertyApplicationsState extends State<MyPropertyApplications> {
  final _authController = Get.find<AuthController>();
  final _propertiesTaxController = Get.find<PropertiesTaxController>();

  @override
  void initState() {
    super.initState();
    _propertiesTaxController.setDefaultLimit();
    getMyProperties();
  }

  void getMyProperties() {
    if (!_authController.isValidUser) return;
    _propertiesTaxController.getMyPropertiesStream(
      token: _authController.token!.accessToken!,
      isMyApplication: true,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Obx(
          () => Wrap(
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
        child: SingleChildScrollView(
          child: Padding(
            padding: EdgeInsets.all(16.w),
            child: GetBuilder<PropertiesTaxController>(
              builder: (ptController) {
                return StreamBuilder(
                  stream: ptController.streamCtrl.stream,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      if (snapshot.data is String || snapshot.data == null) {
                        return const NoApplicationFoundWidget();
                      }

                      PtMyProperties myProperties = snapshot.data;

                      final propertyList = myProperties.properties;
                      propertyList?.sort(
                        (a, b) => DateTime.fromMillisecondsSinceEpoch(
                          b.auditDetails!.createdTime!,
                        ).compareTo(
                          DateTime.fromMillisecondsSinceEpoch(
                            a.auditDetails!.createdTime!,
                          ),
                        ),
                      );
                      if (propertyList!.isNotEmpty) {
                        return ListView.builder(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemCount: propertyList.length >= 10
                              ? propertyList.length + 1
                              : propertyList.length,
                          itemBuilder: (context, index) {
                            if (index == propertyList.length &&
                                propertyList.length >= 10) {
                              return Obx(() {
                                if (ptController.isLoading.value) {
                                  return showCircularIndicator();
                                } else {
                                  return IconButton(
                                    onPressed: () async {
                                      await ptController.loadMore(
                                        token:
                                            _authController.token!.accessToken!,
                                        isMyApplication: true,
                                      );
                                    },
                                    icon: const Icon(
                                      Icons.expand_circle_down_outlined,
                                      size: 30,
                                      color: BaseConfig.appThemeColor1,
                                    ),
                                  );
                                }
                              });
                            } else {
                              var property = propertyList[index];
                              return ComplainCard(
                                title: isNotNullOrEmpty(property.creationReason)
                                    ? getLocalizedString(
                                        '${i18.propertyTax.PT}${property.creationReason}'
                                            .toUpperCase(),
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
                                date:
                                    'Date: ${property.auditDetails!.createdTime.toCustomDateFormat()}',
                                sla:
                                    '${getLocalizedString(i18.propertyTax.PTUID, module: Modules.PT)}: ${property.propertyId ?? 'N/A'}',
                                onTap: () {
                                  Get.toNamed(
                                    AppRoutes.PROPERTY_APPLICATIONS_DETAILS,
                                    arguments: property,
                                  );
                                },
                                status: getLocalizedString(
                                  '${i18.propertyTax.PT_COMMON}${property.status}'
                                      .toUpperCase(),
                                  module: Modules.PT,
                                ),
                                statusColor:
                                    getStatusColor('${property.status}'),
                                statusBackColor:
                                    getStatusBackColor('${property.status}'),
                              ).paddingOnly(bottom: 12);
                            }
                          },
                        );
                      } else {
                        return const NoApplicationFoundWidget();
                      }
                    } else if (snapshot.hasError) {
                      return networkErrorPage(
                        context,
                        () => getMyProperties(),
                      );
                    } else {
                      if (snapshot.connectionState == ConnectionState.waiting ||
                          snapshot.connectionState == ConnectionState.active) {
                        return SizedBox(
                          height: Get.height / 1.5,
                          width: Get.width,
                          child: showCircularIndicator(),
                        );
                      } else {
                        return const SizedBox.shrink();
                      }
                    }
                  },
                );
              },
            ),
          ),
        ),
      ),
    );
  }
}
