import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/documents_not_found/documents_not_found.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/build_card.dart';
import 'package:mobile_app/widgets/build_expansion.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/file_dialogue/file_dilaogue.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/timeline_widget.dart/timeline_wdget.dart';

class MyPropertyApplicationDetails extends StatefulWidget {
  const MyPropertyApplicationDetails({super.key});

  @override
  State<MyPropertyApplicationDetails> createState() =>
      _MyPropertyApplicationDetailsState();
}

class _MyPropertyApplicationDetailsState
    extends State<MyPropertyApplicationDetails> {
  final _fileController = Get.find<FileController>();
  final _authController = Get.find<AuthController>();
  final _timelineHistoryController = Get.find<TimelineController>();
  final _propertiesController = Get.find<PropertiesTaxController>();
  final _paymentController = Get.find<PaymentController>();

  final properties = Get.arguments as Property;

  bool _isTimelineFetch = false;
  bool _isFileFetch = false;

  @override
  void initState() {
    super.initState();
    _init();
  }

  void _init() async {
    _propertiesController.getMyPropertiesMutation(
      tenantId: properties.tenantId!,
      token: _authController.token!.accessToken!,
      propertyIds: properties.propertyId!,
    );
  }

  String getFileStoreIds() {
    if (!isNotNullOrEmpty(properties.documents)) return '';

    List fileIds = [];
    for (var element in properties.documents!) {
      fileIds.add(element.fileStoreId);
    }
    return fileIds.join(', ');
  }

  Future<void> _getTimeline() async {
    await _timelineHistoryController
        .getTimelineHistory(
      token: _authController.token!.accessToken!,
      tenantId: properties.tenantId!,
      businessIds: properties.acknowledgementNumber!,
    )
        .then((value) {
      setState(() {
        _isTimelineFetch = true;
      });
    });
  }

  Widget _makePayment() {
    return FilledButtonApp(
      radius: 0,
      width: Get.width,
      text: getLocalizedString(
        i18.common.MAKE_PAYMENT,
      ),
      onPressed: () => goPayment(),
      circularColor: BaseConfig.fillAppBtnCircularColor,
      backgroundColor: BaseConfig.appThemeColor1,
    );
  }

  Future<BillInfo?> _fetchMyBills() async {
    return _paymentController.searchBillById(
      tenantId: properties.tenantId!,
      token: _authController.token!.accessToken!,
      consumerCode: properties.acknowledgementNumber!,
      service: (properties.workflow?.businessService ==
                  BusinessService.PT_MUTATION.name &&
              properties.creationReason == CreationReason.MUTATION.name)
          ? BusinessService.PT_MUTATION.name
          : BusinessService.PT.name,
    );
  }

  void goPayment() async {
    final bill = await _fetchMyBills();

    if (!isNotNullOrEmpty(bill?.bill)) return;

    Get.toNamed(
      AppRoutes.BILL_DETAIL_SCREEN,
      arguments: {
        'billData': bill?.bill?.first,
        'module': Modules.PT,
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(
          i18.propertyTax.APPLICATION_DETAILS,
          module: Modules.PT,
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      bottomNavigationBar:
          (properties.creationReason == CreationReason.MUTATION.name &&
                  properties.additionalDetails?.applicationStatus ==
                      'FIELDVERIFIED')
              ? _makePayment()
              : null,
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: StreamBuilder(
          stream: _propertiesController.mutationPropertyCtrl.stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              if (snapshot.data is String || snapshot.data == null) {
                return Center(
                  child: Text(getLocalizedString(i18.inbox.NO_APPLICATION)),
                );
              }

              final property = snapshot.data as Property;
              properties.workflow = property.workflow;

              return SingleChildScrollView(
                physics: AppPlatforms.platformPhysics(),
                child: Padding(
                  padding: EdgeInsets.all(16.w),
                  child: Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          TextButton(
                            onPressed: () async {
                              if (!_isTimelineFetch) {
                                await _getTimeline();
                              }
                              //TODO: Show timeline history
                              TimelineHistoryApp.buildTimelineDialogue(
                                context,
                                tenantId: properties.tenantId!,
                              );
                            },
                            child: MediumTextNotoSans(
                              text: getLocalizedString(
                                i18.common.TIMELINE,
                                module: Modules.PT,
                              ),
                              color: BaseConfig.redColor1,
                            ),
                          ),
                          const SizedBox(width: 10),
                        ],
                      ),
                      _buildDetails(property),
                      const SizedBox(height: 10),
                    ],
                  ),
                ),
              );
            } else if (snapshot.hasError) {
              return networkErrorPage(
                context,
                () => _propertiesController.getMyPropertiesMutation(
                  tenantId: properties.tenantId!,
                  token: _authController.token!.accessToken!,
                  propertyIds: properties.propertyId!,
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
      ),
    );
  }

  Widget _buildDetails(Property property) => BuildCard(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.APPLICATION_NUMBER_LABEL,
                module: Modules.PT,
              ),
              text: property.acknowledgementNumber ?? 'N/A',
            ).paddingOnly(left: 7),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.PROPERTY_UID,
                module: Modules.PT,
              ),
              text: property.propertyId ?? 'N/A',
            ).paddingOnly(left: 7),
            const SizedBox(height: 10),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.propertyTax.APPLICATION_CHANNEL_LABEL,
                module: Modules.PT,
              ),
              text: getLocalizedString(
                '${i18.propertyTax.APPLICATION_CHANNEL_RES}${property.channel}',
                module: Modules.PT,
              ),
              // text: getLocalizedString(property.channel,
              // module: Modules.PT) ?? 'N/A',
            ).paddingOnly(left: 7),
            const SizedBox(height: 10),
            if (property.creationReason == CreationReason.MUTATION.name) ...[
              _buildMutationPrice(property),
            ],
            BigTextNotoSans(
              text: getLocalizedString(
                i18.tlProperty.ADDRESS_HEADER,
                module: Modules.PT,
              ),
              size: 16.sp,
              fontWeight: FontWeight.w600,
            ).paddingOnly(left: 7),
            const SizedBox(height: 10),
            _buildAddress(address: property.address).paddingOnly(left: 7),
            const SizedBox(height: 10),
            BuildExpansion(
              title: getLocalizedString(
                i18.propertyTax.ASSESSMENT_DETAILS_HEADER,
                module: Modules.PT,
              ),
              children: [
                _buildPropertyAssessment(property).paddingOnly(left: 7),
              ],
            ),
            if (isNotNullOrEmpty(property.units)) ...[
              BuildExpansion(
                title: getLocalizedString(
                  i18.propertyTax.GROUND_FLOOR,
                  module: Modules.PT,
                ),
                children: [
                  ListView.builder(
                    itemCount: property.units?.length,
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemBuilder: (context, index) {
                      final unit = property.units?[index];
                      return _buildGroundFloorCard(unit!, index + 1)
                          .paddingOnly(left: 7, right: 7, bottom: 10);
                    },
                  ),
                ],
              ),
            ],
            if (isNotNullOrEmpty(property.owners)) ...[
              BuildExpansion(
                title: getLocalizedString(
                  i18.propertyTax.OWNERSHIP_DETAILS_HEADER,
                  module: Modules.PT,
                ),
                children: [
                  _buildOwnerDetails(property, property.owners!.first),
                ],
              ),
            ],
            BuildExpansion(
              title: getLocalizedString(i18.common.DOCUMENTS),
              onExpansionChanged: (p0) {
                if (p0 && !_isFileFetch) {
                  _fileController
                      .getFiles(
                    tenantId: property.tenantId!,
                    token: _authController.token!.accessToken!,
                    fileStoreIds: getFileStoreIds(),
                  )
                      .then((value) {
                    setState(() {
                      _isFileFetch = true;
                    });
                  });
                }
              },
              children: [
                StreamBuilder(
                  stream: _fileController.fileStoreStreamCtrl.stream,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      final fileData = snapshot.data!;

                      return _buildDocuments(fileData);
                    } else if (snapshot.hasError) {
                      return networkErrorPage(
                        context,
                        () => _fileController.getFiles(
                          tenantId: property.tenantId!,
                          token: _authController.token!.accessToken!,
                          fileStoreIds: getFileStoreIds(),
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
              ],
            ),
          ],
        ),
      );

  Widget _buildMutationPrice(Property property) {
    return FutureBuilder(
      future: _paymentController.searchBillById(
        tenantId: property.tenantId!,
        token: _authController.token!.accessToken!,
        service: property.workflow!.businessService!,
        consumerCode: property.acknowledgementNumber!,
      ),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final billData = snapshot.data!;

          final bill = billData.bill?.firstOrNull;

          return isNotNullOrEmpty(bill)
              ? Column(
                  children: [
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.PT_FEE_AMOUNT,
                        module: Modules.PT,
                      ),
                      text: isNotNullOrEmpty(bill?.totalAmount)
                          ? '₹ ${bill!.totalAmount}'
                          : 'N/A',
                      fontWeight: FontWeight.w600,
                    ).paddingOnly(left: 7),
                    const SizedBox(height: 10),
                    ColumnHeaderText(
                      label: getLocalizedString(
                        i18.propertyTax.PT_PAYMENT_STATUS,
                        module: Modules.PT,
                      ),
                      text: getLocalizedString(
                        '${i18.propertyTax.PT_MUT_BILL}${bill?.status}',
                        module: Modules.PT,
                      ),
                    ).paddingOnly(left: 7),
                    const SizedBox(height: 10),
                  ],
                )
              : const SizedBox.shrink();
        } else if (snapshot.hasError) {
          return networkErrorPage(
            context,
            () => _fileController.getFiles(
              tenantId: property.tenantId!,
              token: _authController.token!.accessToken!,
              fileStoreIds: getFileStoreIds(),
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
    );
  }

  Widget _buildOwnerDetails(Property property, Owner owner) => Column(
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.OWNER_NAME,
              module: Modules.PT,
            ),
            text: owner.name ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.GUARDIAN_NAME,
              module: Modules.PT,
            ),
            text: owner.fatherOrHusbandName ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.OWNER_GENDER,
              module: Modules.PT,
            ),
            text: owner.gender ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tlProperty.OWNERSHIP_TYPE,
              module: Modules.PT,
            ),
            text: getLocalizedString(
              '${i18.propertyTax.PT_OWNERSHIP}${property.ownershipCategory}',
              module: Modules.PT,
            ),
            //text: property.ownershipCategory ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.MOBILE_NUMBER,
              module: Modules.PT,
            ),
            text: owner.mobileNumber ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.OWNER_EMAIL,
              module: Modules.PT,
            ),
            text: owner.emailId ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.TRANSFEROR_SPECIAL_CATEGORY,
              module: Modules.PT,
            ),
            text: owner.ownerType ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tlProperty.OWNERSHIP_ADDRESS,
              module: Modules.PT,
            ),
            text: owner.permanentAddress ?? 'N/A',
          ),
          const SizedBox(height: 10),
        ],
      );

  Widget _buildPropertyAssessment(Property property) => Column(
        children: [
          ColumnHeaderText(
            label: getLocalizedString(i18.tlProperty.USE, module: Modules.PT),
            text: property.usageCategory ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PROPERTY_TYPE,
              module: Modules.PT,
            ),
            text: getLocalizedString(
              '${i18.propertyTax.SUB_TYPE_RES}${property.propertyType!.replaceAll('.', '_')}',
              module: Modules.PT,
            ),
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(i18.propertyTax.AREA, module: Modules.PT),
            text: '${property.landArea?.toInt()} sq.ft',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tlProperty.NO_OF_FLOOR,
              module: Modules.PT,
            ),
            text: parseNoOfFloors(property.noOfFloors),
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.ASSESMENT1_ELECTRICITY_NUMBER,
              module: Modules.PT,
            ),
            text: property.additionalDetails?.electricity ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.ASSESMENT1_ELECTRICITY_UID,
              module: Modules.PT,
            ),
            text: property.additionalDetails?.uid ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.STRUCTURE_TYPE_LABEL,
            ),
            text: 'undefined',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.AGE_OF_PROPERTY_LABEL,
            ),
            text: 'undefined',
          ),
          const SizedBox(height: 10),
        ],
      );

  Widget _buildAddress({Address? address}) => Column(
        children: [
          ColumnHeaderText(
            label:
                getLocalizedString(i18.tlProperty.PINCODE, module: Modules.PT),
            text: address?.pincode ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(i18.propertyTax.CITY, module: Modules.PT),
            text: address?.city ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.LOCALITY_OR_MOHALLA,
              module: Modules.PT,
            ),
            text: address?.locality?.area ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.tlProperty.STREET_NAME,
              module: Modules.PT,
            ),
            text: address?.street ?? 'N/A',
          ),
          const SizedBox(height: 10),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.propertyTax.PROPERTY_ADDRESS_COLONY_NAME,
              module: Modules.PT,
            ),
            text: address?.buildingName ?? 'N/A',
          ),
          const SizedBox(height: 10),
        ],
      );

  Widget _buildGroundFloorCard(Unit unit, int index) => Container(
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey),
          borderRadius: BorderRadius.circular(10),
        ),
        child: Padding(
          padding: const EdgeInsets.all(10.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Wrap(
                children: [
                  BigTextNotoSans(
                    text: getLocalizedString(
                      i18.propertyTax.UNIT,
                      module: Modules.PT,
                    ),
                    size: 16.sp,
                    fontWeight: FontWeight.w600,
                    color: const Color.fromRGBO(80, 90, 95, 1.0),
                  ),
                  BigTextNotoSans(
                    text: ' $index',
                    size: 16.sp,
                    fontWeight: FontWeight.w600,
                    color: const Color.fromRGBO(80, 90, 95, 1.0),
                  ),
                ],
              ),
              const SizedBox(height: 10),
              ColumnHeaderText(
                label: getLocalizedString(
                  i18.propertyTax.UNIT_USAGE_TYPE,
                  module: Modules.PT,
                ),
                text: getLocalizedString(
                  '${i18.propertyTax.SUB_TYPE_RES}${unit.usageCategory!.replaceAll('.', '_')}',
                  //module: Modules.PT,
                ),
              ),
              const SizedBox(height: 10),
              ColumnHeaderText(
                label: getLocalizedString(
                  i18.propertyTax.UNIT_OCCUPANY_TYPE,
                  module: Modules.PT,
                ),
                text: unit.occupancyType ?? 'N/A',
              ),
              const SizedBox(height: 10),
              ColumnHeaderText(
                label: getLocalizedString(
                  i18.propertyTax.UNIT_BUILTUP_AREA,
                  module: Modules.PT,
                ),
                text: '${unit.constructionDetail?.builtUpArea} sq.ft',
              ),
              if (unit.arv != null) ...[
                const SizedBox(height: 10),
                ColumnHeaderText(
                  label: getLocalizedString(
                    i18.propertyTax.FORM2_TOTAL_ANNUAL_RENT,
                    module: Modules.PT,
                  ),
                  text: '₹${unit.arv}',
                ),
                const SizedBox(height: 10),
              ],
            ],
          ),
        ),
      );

  Widget _buildDocuments(FileStore fileStore) {
    return (!isNotNullOrEmpty(fileStore.fileStoreIds))
        ? const DocumentsNotFound(module: Modules.PT)
        : Padding(
            padding: const EdgeInsets.all(10.0),
            child: GridView.builder(
              itemCount: fileStore.fileStoreIds!.length,
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3,
                crossAxisSpacing: 10.0,
                mainAxisSpacing: 10.0,
                mainAxisExtent: 110.0, //110
              ),
              shrinkWrap: true,
              itemBuilder: (context, index) {
                final fileUrl =
                    fileStore.fileStoreIds?[index].url?.split(',').firstOrNull;
                final docType = properties.documents?.firstWhereOrNull(
                  (element) =>
                      element.fileStoreId == fileStore.fileStoreIds?[index].id,
                );
                return isNotNullOrEmpty(docType)
                    ? Column(
                        children: [
                          Container(
                            width: Get.width,
                            decoration: BoxDecoration(
                              color: BaseConfig.greyColor2,
                              // border: Border.all(color: Colors.grey),
                              borderRadius: BorderRadius.circular(10.r),
                            ),
                            child: Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Icon(
                                _fileController.getFileType(fileUrl!).$1,
                                size: 40.sp,
                                color: Colors.grey.shade600,
                              ),
                            ),
                          ),
                          SizedBox(height: 10.h),
                          Tooltip(
                            message: getLocalizedString(
                              docType!.documentType,
                              module: Modules.PT,
                            ),
                            child: MediumText(
                              text: getLocalizedString(
                                docType.documentType,
                                module: Modules.PT,
                              ),
                              color: Colors.grey.shade600,
                              maxLine: 2,
                              textOverflow: TextOverflow.ellipsis,
                            ),
                          ),
                        ],
                      ).ripple(() {
                        final fileType =
                            _fileController.getFileType(fileUrl).$2;
                        dPrint('FileType: ${fileType.name}');
                        if (fileType.name == FileExtType.pdf.name) {
                          showTypeDialogue(
                            context,
                            url: fileUrl,
                            isPdf: true,
                            title: getLocalizedString(
                              docType.documentType,
                              module: Modules.PT,
                            ),
                          );
                        } else {
                          showTypeDialogue(
                            context,
                            url: fileUrl,
                            title: getLocalizedString(
                              docType.documentType,
                              module: Modules.PT,
                            ),
                          );
                        }
                      })
                    : const SizedBox.shrink();
              },
            ),
          );
  }
}
