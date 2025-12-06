import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/water_sewerage/consumption.dart';
import 'package:mobile_app/model/citizen/water_sewerage/water.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class WsViewConsumptionDetail extends StatelessWidget {
  WsViewConsumptionDetail({super.key});

  final WaterConnection waterConnection = Get.arguments['waterConnection'];
  final _authController = Get.find<AuthController>();
  final _waterController = Get.find<WaterController>();
  final _editProfileController = Get.find<EditProfileController>();

  final _isLoading = false.obs;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: getLocalizedString(
          i18.waterSewerage.VIEW_CONSUMPTION,
          module: Modules.WS,
        ),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => _isLoading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.all(20.0),
                    child: FutureBuilder(
                      future: _waterController.getWaterConsumptionDetailFuture(
                        tenantId: waterConnection.tenantId!,
                        mobileNumber: _editProfileController
                            .userProfile.user!.first.mobileNumber!,
                        token: _authController.token!.accessToken!,
                        connectionNos: waterConnection.connectionNo.toString(),
                      ),
                      builder: (context, snapshots) {
                        if (snapshots.hasData) {
                          if (snapshots.data is String ||
                              snapshots.data == null) {
                            return Center(
                              child: Text(
                                getLocalizedString(i18.inbox.NO_APPLICATION),
                              ),
                            );
                          }

                          final Consumption consumption = snapshots.data!;

                          return ListView.builder(
                            itemCount: consumption.meterReadings!.length,
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemBuilder: (context, index) {
                              final consump = consumption.meterReadings![index];
                              return Card(
                                child: Padding(
                                  padding: const EdgeInsets.all(10.0),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.waterSewerage
                                              .WS_MYCONNECTIONS_CONSUMER_NO,
                                          module: Modules.WS,
                                        ),
                                        text: consump.connectionNo ?? "N/A",
                                      ),
                                      const SizedBox(height: 16),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.waterSewerage
                                              .BILL_BILLING_PERIOD_LABEL,
                                          module: Modules.WS,
                                        ),
                                        text: consump.billingPeriod ?? 'N/A',
                                      ),
                                      const SizedBox(height: 16),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.waterSewerage
                                              .CONSUMPTION_DETAILS_METER_STATUS_LABEL,
                                          module: Modules.WS,
                                        ),
                                        text: consump.meterStatus ?? 'N/A',
                                      ),
                                      const SizedBox(height: 16),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.waterSewerage
                                              .CONSUMPTION_DETAILS_LAST_READING_LABEL,
                                          module: Modules.WS,
                                        ),
                                        text: isNotNullOrEmpty(
                                          consump.lastReading,
                                        )
                                            ? consump.lastReading.toString()
                                            : 'N/A',
                                      ),
                                      const SizedBox(height: 16),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.waterSewerage
                                              .CONSUMPTION_DETAILS_LAST_READING_DATE_LABEL,
                                          module: Modules.WS,
                                        ),
                                        text: consump.lastReadingDate
                                                .toCustomDateFormat(
                                              pattern: 'dd/MM/yyy',
                                            ) ??
                                            'N/A',
                                      ),
                                      const SizedBox(height: 16),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.waterSewerage
                                              .CONSUMPTION_DETAILS_CURRENT_READING_LABEL,
                                          module: Modules.WS,
                                        ),
                                        text: isNotNullOrEmpty(
                                          consump.currentReading,
                                        )
                                            ? consump.currentReading.toString()
                                            : 'N/A',
                                      ),
                                      const SizedBox(height: 16),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.waterSewerage
                                              .CONSUMPTION_DETAILS_CURRENT_READING_DATE_LABEL,
                                          module: Modules.WS,
                                        ),
                                        text: consump.currentReadingDate
                                                .toCustomDateFormat(
                                              pattern: 'dd/MM/yyyy',
                                            ) ??
                                            'N/A',
                                      ),
                                      const SizedBox(height: 16),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.waterSewerage
                                              .CONSUMPTION_DETAILS_CONSUMPTION_LABEL,
                                          module: Modules.WS,
                                        ),
                                        text: consump.consumption ?? '0',
                                      ),
                                      const SizedBox(height: 16),
                                    ],
                                  ),
                                ),
                              );
                            },
                          );
                        } else {
                          if (snapshots.connectionState ==
                                  ConnectionState.waiting ||
                              snapshots.connectionState ==
                                  ConnectionState.active) {
                            return SizedBox(
                              height: Get.height / 2,
                              width: Get.width,
                              child: showCircularIndicator(),
                            );
                          } else if (snapshots.hasError) {
                            return networkErrorPage(
                              context,
                              () => Get.offAndToNamed(
                                AppRoutes.WATER_CONSUMPTION_DETAILS,
                              ),
                            );
                          } else {
                            return const SizedBox.shrink();
                          }
                        }
                      },
                    ),
                  ),
                ),
        ),
      ),
    );
  }
}
