import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/model/citizen/water_sewerage/sewerage.dart';
import 'package:mobile_app/model/citizen/water_sewerage/water.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class WaterAdditionalDetailsScreen extends StatelessWidget {
  const WaterAdditionalDetailsScreen({
    super.key,
    this.module = Modules.WS,
    this.sewerageConnection,
    this.waterConnection,
    mod,
  });

  final Modules module;
  final SewerageConnection? sewerageConnection;
  final WaterConnection? waterConnection;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: getLocalizedString(
          i18.waterSewerage.ADDN_DETAILS,
          module: module,
        ),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                isNotNullOrEmpty(sewerageConnection)
                    ? _buildConnectionDetailsSW(sewerageConnection!)
                    : _buildConnectionDetailsWS(waterConnection!),
                const SizedBox(height: 10),
                _buildPlumberDetails(sewerageConnection, waterConnection),
                const SizedBox(height: 10),
                _buildRoadCuttingDetails(sewerageConnection, waterConnection),
                const SizedBox(height: 10),
                _buildActivationDetails(sewerageConnection, waterConnection),
                const SizedBox(height: 10),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildConnectionDetailsWS(WaterConnection waterConnection) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.CONNECTION_DETAIL,
                module: module,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.CONNECTION_TYPE,
                module: module,
              )}:',
              text: isNotNullOrEmpty(waterConnection.connectionType)
                  ? waterConnection.connectionType!
                  : getLocalizedString(i18.common.NA),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.SERV_DETAIL_NO_OF_TAPS,
                module: module,
              )}:',
              text: isNotNullOrEmpty(waterConnection.noOfTaps)
                  ? waterConnection.noOfTaps!.toString()
                  : getLocalizedString(i18.common.NA),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.PIPE_SIZE_IN_INCHES_LABE,
                module: module,
              )}:',
              text: isNotNullOrEmpty(waterConnection.pipeSize)
                  ? waterConnection.pipeSize.toString()
                  : getLocalizedString(i18.common.NA),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.DETAIL_WATER_SOURCE,
                module: module,
              )}:',
              text: isNotNullOrEmpty(waterConnection.waterSource)
                  ? waterConnection.waterSource!.split('.').first.capitalize!
                  : getLocalizedString(i18.common.NA),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.SERV_DETAIL_WATER_SUB_SOURCE,
                module: module,
              )}:',
              text: isNotNullOrEmpty(waterConnection.waterSource)
                  ? waterConnection.waterSource!.split('.').last.capitalize!
                  : getLocalizedString(i18.common.NA),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildConnectionDetailsSW(
    SewerageConnection sewerageConnection,
  ) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.CONNECTION_DETAIL,
                module: module,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.CONNECTION_TYPE,
                module: module,
              )}:',
              text: sewerageConnection.connectionType ??
                  getLocalizedString(i18.common.NA),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.SERV_DETAIL_WATER_CLOSETS,
                module: module,
              )}:',
              text: isNotNullOrEmpty(sewerageConnection.proposedWaterClosets)
                  ? sewerageConnection.proposedWaterClosets!.toString()
                  : getLocalizedString(i18.common.NA),
            ),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.SERV_DETAIL_NO_OF_TOILETS,
                module: module,
              )}:',
              text: isNotNullOrEmpty(sewerageConnection.proposedToilets)
                  ? sewerageConnection.proposedToilets!.toString()
                  : getLocalizedString(i18.common.NA),
            ),
            const SizedBox(height: 10),
          ],
        ),
      ),
    );
  }

  Widget _buildPlumberDetails(
    SewerageConnection? sewerageConnection,
    WaterConnection? waterConnection,
  ) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.PLUMBER_DETAILS,
                module: module,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.PLUMBER_PROVIDED_BY,
                module: module,
              )}:',
              text: isNotNullOrEmpty(
                sewerageConnection?.additionalDetails?.detailsProvidedBy,
              )
                  ? (sewerageConnection!.additionalDetails?.detailsProvidedBy ==
                          "ULB")
                      ? getLocalizedString(
                          i18.waterSewerage.PLUMBER_ULB,
                          module: module,
                        )
                      : (sewerageConnection
                                  .additionalDetails?.detailsProvidedBy ==
                              "ULB")
                          ? getLocalizedString(
                              i18.waterSewerage.PLUMBER_ULB,
                              module: module,
                            )
                          : getLocalizedString(
                              i18.waterSewerage.PLUMBER_SELF,
                              module: module,
                            )
                  : isNotNullOrEmpty(
                      waterConnection?.additionalDetails?.detailsProvidedBy,
                    )
                      ? (waterConnection!
                                  .additionalDetails?.detailsProvidedBy ==
                              "ULB")
                          ? getLocalizedString(
                              i18.waterSewerage.PLUMBER_ULB,
                              module: module,
                            )
                          : (waterConnection
                                      .additionalDetails?.detailsProvidedBy ==
                                  "ULB")
                              ? getLocalizedString(
                                  i18.waterSewerage.PLUMBER_ULB,
                                  module: module,
                                )
                              : getLocalizedString(
                                  i18.waterSewerage.PLUMBER_SELF,
                                  module: module,
                                )
                      : getLocalizedString(i18.common.NA),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRoadCuttingDetails(
    SewerageConnection? sewerageConnection,
    WaterConnection? waterConnection,
  ) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.ROAD_CUTTING_DETAILS,
                module: module,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.ADDN_DETAIL_ROAD_TYPE,
                module: module,
              )}:',
              text: isNotNullOrEmpty(
                sewerageConnection?.roadCuttingInfo?.first.roadType,
              )
                  ? getLocalizedString(
                      '${i18.waterSewerage.WS_ROADTYPE}${sewerageConnection!.roadCuttingInfo!.first.roadType!}',
                      module: module,
                    )
                  : isNotNullOrEmpty(
                      waterConnection?.roadCuttingInfo?.first.roadType,
                    )
                      ? getLocalizedString(
                          '${i18.waterSewerage.WS_ROADTYPE}${waterConnection!.roadCuttingInfo!.first.roadType!}',
                          module: module,
                        )
                      : getLocalizedString(i18.common.NA),
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.ADDN_DETAILS_AREA_LABEL,
                module: module,
              )}:',
              text: isNotNullOrEmpty(
                sewerageConnection
                    ?.roadCuttingInfo?.firstOrNull?.roadCuttingArea,
              )
                  ? sewerageConnection!.roadCuttingInfo!.first.roadCuttingArea
                      .toString()
                  : isNotNullOrEmpty(
                      waterConnection
                          ?.roadCuttingInfo?.firstOrNull?.roadCuttingArea,
                    )
                      ? waterConnection!.roadCuttingInfo!.first.roadCuttingArea
                          .toString()
                      : getLocalizedString(i18.common.NA),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActivationDetails(
    SewerageConnection? sewerageConnection,
    WaterConnection? waterConnection,
  ) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.ACTIVATION_DETAILS,
                module: module,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: '${getLocalizedString(
                i18.waterSewerage.DETAIL_CONN_EXECUTION_DATE,
                module: module,
              )}:',
              text: isNotNullOrEmpty(
                sewerageConnection?.connectionExecutionDate,
              )
                  ? sewerageConnection!.connectionExecutionDate!
                      .toCustomDateFormat(pattern: 'd/MM/yyyy')!
                  : isNotNullOrEmpty(waterConnection?.connectionExecutionDate)
                      ? waterConnection!.connectionExecutionDate!
                          .toCustomDateFormat(pattern: 'd/MM/yyyy')!
                      : getLocalizedString(i18.common.NA),
            ),
            if (isNotNullOrEmpty(waterConnection))
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 10),
                  ColumnHeaderText(
                    label: '${getLocalizedString(
                      i18.waterSewerage.METER_ID,
                      module: module,
                    )}:',
                    text: isNotNullOrEmpty(waterConnection?.meterId)
                        ? '${waterConnection!.meterId}'
                        : getLocalizedString(i18.common.NA),
                  ),
                  const SizedBox(height: 10),
                  ColumnHeaderText(
                    label: '${getLocalizedString(
                      i18.waterSewerage.METER_INSTALL_DATE,
                      module: module,
                    )}:',
                    text: isNotNullOrEmpty(
                      waterConnection?.meterInstallationDate,
                    )
                        ? '${waterConnection!.meterInstallationDate.toCustomDateFormat()}'
                        : getLocalizedString(i18.common.NA),
                  ),
                  const SizedBox(height: 10),
                  ColumnHeaderText(
                    label: '${getLocalizedString(
                      i18.waterSewerage.INITIAL_METER_READING,
                      module: module,
                    )}:',
                    text: isNotNullOrEmpty(
                      waterConnection?.additionalDetails?.initialMeterReading,
                    )
                        ? '${waterConnection?.additionalDetails!.initialMeterReading}'
                        : getLocalizedString(i18.common.NA),
                  ),
                ],
              ),
          ],
        ),
      ),
    );
  }
}
