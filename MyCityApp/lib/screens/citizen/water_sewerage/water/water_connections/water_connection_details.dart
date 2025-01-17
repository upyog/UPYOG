import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/property_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/property/property.dart';
import 'package:mobile_app/model/citizen/water_sewerage/water.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class WaterConnectionDetails extends StatefulWidget {
  const WaterConnectionDetails({super.key});

  @override
  State<WaterConnectionDetails> createState() => _WaterConnectionDetailsState();
}

class _WaterConnectionDetailsState extends State<WaterConnectionDetails> {
  final WaterConnection waterConnection = Get.arguments['water'];
  final _fileController = Get.find<FileController>();
  final _downloadController = Get.find<DownloadController>();
  final _authController = Get.find<AuthController>();
  final _propertyController = Get.find<PropertyController>();

  Future<Properties?> _getProperties() async {
    TenantTenant tenantCity = await getCityTenant();
    return await _propertyController.getPropertiesByID(
      token: _authController.token!.accessToken!,
      propertyId: waterConnection.propertyId!,
      tenantCity: tenantCity.code!,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: getLocalizedString(
          i18.waterSewerage.CONNECTION_DETAIL,
          module: Modules.WS,
        ),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          physics: AppPlatforms.platformPhysics(),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    PopupMenuButton(
                      icon: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          const Icon(
                            Icons.download,
                            color: BaseConfig.redColor1,
                          ),
                          const SizedBox(width: 5),
                          MediumTextNotoSans(
                            text: getLocalizedString(i18.common.DOWNLOAD),
                            color: BaseConfig.redColor1,
                            size: 14.0,
                          ),
                        ],
                      ),
                      itemBuilder: (context) => <PopupMenuItem<String>>[
                        if (waterConnection
                                .additionalDetails?.estimationFileStoreId !=
                            null)
                          PopupMenuItem<String>(
                            value: getLocalizedString(
                              i18.waterSewerage.CONNECTION_DETAIL_PDF,
                              module: Modules.WS,
                            ),
                            child: MediumText(
                              text: getLocalizedString(
                                i18.waterSewerage.CONNECTION_DETAIL_PDF,
                                module: Modules.WS,
                              ),
                            ),
                            onTap: () async {
                              final ids = await _fileController.getFiles(
                                tenantId: BaseConfig.STATE_TENANT_ID,
                                token: _authController.token!.accessToken!,
                                fileStoreIds: waterConnection
                                    .additionalDetails!.estimationFileStoreId!,
                              );
                              var fileId = ids?.fileStoreIds?.first.url;
                              if (fileId != null && context.mounted) {
                                _downloadController.starFileDownload(
                                  url: fileId,
                                  title: getLocalizedString(
                                    i18.waterSewerage.CONNECTION_DETAIL_PDF,
                                    module: Modules.WS,
                                  ),
                                );
                              }
                            },
                          ),
                      ],
                      onSelected: (value) {
                        //TODO: Implement download function
                      },
                    ),
                  ],
                ),
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(10.0),
                    child: Column(
                      children: [
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.MYCONNECTIONS_CONSUMER_NO,
                            module: Modules.WS,
                          ),
                          text: waterConnection.connectionNo ?? 'N/A',
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.SERVICE_NAME_LABEL,
                            module: Modules.WS,
                          ),
                          text: getLocalizedString(
                            '${i18.waterSewerage.WS_APPLICATION_TYPE_}${waterConnection.applicationType}',
                            module: Modules.WS,
                          ),
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.WS_STATUS,
                            module: Modules.WS,
                          ),
                          text: waterConnection.status ?? 'N/A',
                        ),
                        const SizedBox(height: 10),
                        MediumText(
                          text: getLocalizedString(
                            i18.waterSewerage.CONNECTION_DETAIL,
                            module: Modules.WS,
                          ),
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.CONNECTION_TYPE,
                            module: Modules.WS,
                          ),
                          text: waterConnection.connectionType ?? 'N/A',
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.SERV_DETAIL_NO_OF_TAPS,
                            module: Modules.WS,
                          ),
                          text: waterConnection.noOfTaps.toString(),
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.PIPE_SIZE_IN_INCHES_LABE,
                            module: Modules.WS,
                          ),
                          text: isNotNullOrEmpty(waterConnection.pipeSize)
                              ? waterConnection.pipeSize.toString()
                              : getLocalizedString(i18.common.NA),
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.DETAIL_WATER_SOURCE,
                            module: Modules.WS,
                          ),
                          text: isNotNullOrEmpty(waterConnection.waterSource)
                              ? waterConnection.waterSource!
                                  .split('.')
                                  .first
                                  .capitalize!
                              : getLocalizedString(i18.common.NA),
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.SERV_DETAIL_WATER_SUB_SOURCE,
                            module: Modules.WS,
                          ),
                          text: isNotNullOrEmpty(waterConnection.waterSource)
                              ? waterConnection.waterSource!
                                  .split('.')
                                  .last
                                  .capitalize!
                              : getLocalizedString(i18.common.NA),
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.DETAIL_CONN_EXECUTION_DATE,
                            module: Modules.WS,
                          ),
                          text: waterConnection.connectionExecutionDate
                                  .toCustomDateFormat(pattern: 'dd/MM/yyyy') ??
                              'N/A',
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.METER_ID,
                            module: Modules.WS,
                          ),
                          text: waterConnection.meterId ?? 'N/A',
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.METER_INSTALL_DATE,
                            module: Modules.WS,
                          ),
                          text: waterConnection.meterInstallationDate
                                  .toCustomDateFormat(pattern: 'dd/MM/yyyy') ??
                              'N/A',
                        ),
                        const SizedBox(height: 10),
                        ColumnHeaderText(
                          label: getLocalizedString(
                            i18.waterSewerage.INITIAL_METER_READING,
                            module: Modules.WS,
                          ),
                          text: waterConnection
                                  .additionalDetails?.initialMeterReading
                                  .toString() ??
                              'N/A',
                        ),
                        const SizedBox(height: 10),
                        Align(
                          alignment: Alignment.centerLeft,
                          child: TextButton(
                            onPressed: () async {
                              Get.toNamed(
                                AppRoutes.WATER_CONSUMPTION_DETAILS,
                                arguments: {
                                  'waterConnection': waterConnection,
                                },
                              );
                            },
                            child: Row(
                              children: [
                                MediumText(
                                  text: getLocalizedString(
                                    i18.waterSewerage.VIEW_CONSUMPTION_LABEL,
                                    module: Modules.WS,
                                  ),
                                  color: Colors.red,
                                ),
                                const SizedBox(width: 10),
                                const Icon(
                                  Icons.arrow_right_alt_outlined,
                                  color: Colors.red,
                                ),
                              ],
                            ),
                          ),
                        ).paddingOnly(left: 10.0),
                        FutureBuilder(
                          future: _getProperties(),
                          builder: (context, snapshot) {
                            if (!snapshot.hasError &&
                                snapshot.hasData &&
                                snapshot.data!.properties!.isNotEmpty) {
                              Properties property = snapshot.data!;
                              return _buildPropertyDetails(property);
                            } else if (snapshot.hasError) {
                              return Obx(
                                () => _propertyController.isLoading.value
                                    ? showCircularIndicator()
                                    : networkErrorPage(
                                        context,
                                        errorText:
                                            'Unable to get property details',
                                        () => _getProperties(),
                                      ),
                              );
                            } else {
                              switch (snapshot.connectionState) {
                                case ConnectionState.waiting:
                                  return showCircularIndicator();
                                case ConnectionState.active:
                                  return showCircularIndicator();
                                default:
                                  return const SizedBox.shrink();
                              }
                            }
                          },
                        ),
                        MediumText(
                          text: getLocalizedString(
                            i18.waterSewerage.CONNECTION_HOLDER_DETAILS_HEADER,
                            module: Modules.WS,
                          ),
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        const SizedBox(height: 10),
                        Center(
                          child: waterConnection.connectionHolders == null
                              ? MediumText(
                                  text: getLocalizedString(
                                    i18.waterSewerage
                                        .PROPERTY_OWNER_SAME_AS_CONN_HOLDERS,
                                    module: Modules.WS,
                                  ),
                                  fontWeight: FontWeight.bold,
                                  color: BaseConfig.greyColor3,
                                )
                              : Column(
                                  children: [
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        i18.waterSewerage.CONSUMER_NAME_LABEL,
                                        module: Modules.WS,
                                      ),
                                      text: waterConnection
                                              .connectionHolders?.first.name ??
                                          'N/A',
                                    ),
                                    const SizedBox(height: 10),
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        i18.waterSewerage.CONSUMER_GENDER_LABEL,
                                        module: Modules.WS,
                                      ),
                                      text: waterConnection.connectionHolders
                                              ?.first.gender ??
                                          'N/A',
                                    ),
                                    const SizedBox(height: 10),
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        i18.waterSewerage.CONSUMER_MOBILE_LABEL,
                                        module: Modules.WS,
                                      ),
                                      text: waterConnection.connectionHolders
                                              ?.first.mobileNumber ??
                                          'N/A',
                                    ),
                                    const SizedBox(height: 10),
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        i18.waterSewerage
                                            .CONSUMER_FATHER_OR_HUSBAND_LABEL,
                                        module: Modules.WS,
                                      ),
                                      text: waterConnection.connectionHolders
                                              ?.first.fatherOrHusbandName ??
                                          'N/A',
                                    ),
                                    const SizedBox(height: 10),
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        i18.waterSewerage
                                            .CONSUMER_RELATION_LABEL,
                                        module: Modules.WS,
                                      ),
                                      text: waterConnection.connectionHolders
                                              ?.first.relationship ??
                                          'N/A',
                                    ),
                                    const SizedBox(height: 10),
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        i18.waterSewerage
                                            .CONSUMER_ADDRESS_LABEL,
                                        module: Modules.WS,
                                      ),
                                      text: waterConnection.connectionHolders
                                              ?.first.correspondenceAddress ??
                                          'N/A',
                                    ),
                                    const SizedBox(height: 10),
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        i18.waterSewerage.CONSUMER_OWNER_LABEL,
                                        module: Modules.WS,
                                      ),
                                      text:
                                          '${getLocalizedString(i18.waterSewerage.COMMON_MASTERS_OWNERTYPE_, module: Modules.WS)}${waterConnection.connectionHolders?.first.ownerType}',
                                    ),
                                    //COMMON_MASTERS_OWNERTYPE_NONE
                                    const SizedBox(height: 10),
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        i18.waterSewerage.CONSUMER_EMAIL_LABEL,
                                        module: Modules.WS,
                                      ),
                                      text: waterConnection.connectionHolders
                                              ?.first.emailId ??
                                          'N/A',
                                    ),
                                    const SizedBox(height: 10),
                                  ],
                                ),
                        ),
                        const SizedBox(height: 10),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 10),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildPropertyDetails(Properties property) {
    final ownerList = property.properties?.first.owners?.reversed.toList();
    final addressParts = [
      property.properties?.first.address?.doorNo,
      property.properties?.first.address?.street,
      property.properties?.first.address?.locality?.name,
      property.properties?.first.address?.city,
      property.properties?.first.address?.pinCode,
    ];

    final filteredAddressParts =
        addressParts.where((part) => part != null && part.isNotEmpty).toList();
    final finalAddress = filteredAddressParts.join(', ');

    return Column(
      //crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Column(
          //crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            BigTextNotoSans(
              text: getLocalizedString(
                i18.waterSewerage.PROPERTY_DETAILS,
                module: Modules.WS,
              ),
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(i18.tlProperty.ID),
              text: waterConnection.propertyId ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.CONSUMER_NAME_LABEL,
                module: Modules.WS,
              ),
              text: ownerList?.first.name ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.waterSewerage.PROPERTY_ADDRESS,
                module: Modules.WS,
              ),
              text: finalAddress,
            ),
            const SizedBox(height: 10),
          ],
        ).paddingOnly(top: 10.0, left: 10.0, right: 10.0, bottom: 0),
        Align(
          alignment: Alignment.centerLeft,
          child: TextButton(
            onPressed: () async {
              final TenantTenant tenant = await getCityTenant();
              Get.toNamed(
                AppRoutes.WATER_PROPERTY_INFO,
                arguments: {
                  'property': property,
                  'tenant': tenant,
                },
              );
            },
            child: Row(
              children: [
                MediumText(
                  text: getLocalizedString(
                    i18.waterSewerage.VIEW_PROPERTY,
                    module: Modules.WS,
                  ),
                  color: Colors.red,
                ),
                const SizedBox(width: 10),
                const Icon(
                  Icons.arrow_right_alt_outlined,
                  color: Colors.red,
                ),
              ],
            ),
          ),
        ).paddingOnly(left: 10.0),
      ],
    );
  }
}
