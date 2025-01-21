import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class PropertyTaxScreen extends StatefulWidget {
  const PropertyTaxScreen({super.key});

  @override
  State<PropertyTaxScreen> createState() => _PropertyTaxScreenState();
}

class _PropertyTaxScreenState extends State<PropertyTaxScreen> {
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
      //isMyApplication: false,
    );
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return Scaffold(
          appBar: HeaderTop(
            titleWidget: Wrap(
              children: [
                Text(
                  getLocalizedString(i18.common.PROPERTY_TAX),
                ),
                Obx(() => Text(' (${_propertiesTaxController.length.value})')),
              ],
            ),
            onPressed: () => Navigator.of(context).pop(),
            orientation: o,
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
                    return Center(child: Text('Error: ${snapshot.error}'));
                  } else if (snapshot.hasData) {
                    if (snapshot.data is String || snapshot.data == null) {
                      return const NoApplicationFoundWidget();
                    }
                    final PtMyProperties myProperties = snapshot.data!;
                    final propertyList = myProperties.properties
                        ?.where(
                          (e) =>
                              e.status == ApplicationStatus.ACTIVE.value ||
                              e.status == ApplicationStatus.INACTIVE.value,
                        )
                        .toList();
                    if (propertyList!.isNotEmpty) {
                      return ListView.builder(
                        itemCount: propertyList.length >= 10
                            ? propertyList.length + 1
                            : propertyList.length,
                        itemBuilder: (context, index) {
                          if (index == propertyList.length &&
                              propertyList.length >= 10) {
                            return Obx(() {
                              if (_propertiesTaxController.isLoading.value) {
                                return showCircularIndicator();
                              } else {
                                return IconButton(
                                  onPressed: () async {
                                    await _propertiesTaxController.loadMore(
                                      token:
                                          _authController.token!.accessToken!,
                                      //isMyApplication: false,
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
                              title: property.owners?.first.name?.capitalize ??
                                  "N/A",
                              id: '${getLocalizedString(i18.propertyTax.PROPERTY_ID)}: ${property.propertyId}',
                              date:
                                  'Date: ${property.auditDetails!.createdTime.toCustomDateFormat()}',
                              onTap: () {
                                Get.toNamed(
                                  AppRoutes.MY_PROPERTIES_DETAILS,
                                  arguments: property,
                                );
                              },
                              status: getLocalizedString(
                                '${i18.propertyTax.PT_COMMON}${property.status}'
                                    .toUpperCase(),
                                module: Modules.PT,
                              ),
                              statusColor: getStatusColor('${property.status}'),
                              statusBackColor:
                                  getStatusBackColor('${property.status}'),
                            ).paddingOnly(bottom: 12);
                          }
                        },
                      );
                    } else {
                      return const NoApplicationFoundWidget();
                    }
                  } else {
                    return const NoApplicationFoundWidget();
                  }
                },
              ),
            ),
          ),
        );
      },
    );
  }
}
