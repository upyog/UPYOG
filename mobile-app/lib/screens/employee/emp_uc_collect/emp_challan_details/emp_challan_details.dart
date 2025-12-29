import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/employee/emp_challans/emp_challans_model.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/build_card.dart';
import 'package:mobile_app/widgets/build_expansion.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class EmpChallanDetailsScreen extends StatefulWidget {
  const EmpChallanDetailsScreen({super.key});

  @override
  State<EmpChallanDetailsScreen> createState() =>
      _EmpChallanDetailsScreenState();
}

class _EmpChallanDetailsScreenState extends State<EmpChallanDetailsScreen> {
  final _authController = Get.find<AuthController>();
  final _paymentController = Get.find<PaymentController>();
  final _fileController = Get.find<FileController>();
  final _downloadController = Get.find<DownloadController>();

  // final _inboxController = Get.find<InboxController>();

  Challan? challan;
  TenantTenant? tenant;

  var isLoading = false.obs;

  @override
  void initState() {
    challan = Get.arguments['challan'];
    tenant = Get.arguments['tenant'];
    super.initState();
    _init();
  }

  _init() async {
    isLoading.value = true;
    // await _getWorkflow();
    isLoading.value = false;
  }

  // String getFileStoreIds() {
  //   if (!isNotNullOrEmpty(
  //     _ptController.myProperties?.properties?.firstOrNull?.documents,
  //   )) {
  //     return '';
  //   }

  //   List fileIds = [];
  //   for (var element
  //       in _ptController.myProperties!.properties!.first.documents!) {
  //     fileIds.add(element.fileStoreId);
  //   }
  //   return fileIds.join(', ');
  // }

  // Future<void> _getWorkflow() async {
  //   try {
  //     await _timelineController.getWorkFlow(
  //       token: _authController.token!.accessToken!,
  //       tenantId: '',
  //       workFlow: statusMap.businessService!,
  //     );
  //   } catch (e) {
  //     dPrint('Error in getting workflow: $e');
  //   }
  // }

  @override
  Widget build(BuildContext context) {
    // final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        title: 'Challan Details',
        onPressed: () => Navigator.of(context).pop(),
      ),
      // bottomNavigationBar: Obx(
      //   () => isLoading.value
      //       ? const SizedBox.shrink()
      //       : (_item!.businessObject!.status! == InboxStatus.IN_WORKFLOW.name
      //           ? Container(
      //               height: 44.h,
      //               width: Get.width,
      //               margin:
      //                   EdgeInsets.all(o == Orientation.portrait ? 16.w : 12.w),
      //               child: PopupMenuButton(
      //                 style: FilledButton.styleFrom(
      //                   backgroundColor: BaseConfig.appThemeColor1,
      //                   shape: RoundedRectangleBorder(
      //                     borderRadius: BorderRadius.circular(
      //                       o == Orientation.portrait ? 12.w : 6.w,
      //                     ),
      //                   ),
      //                 ),
      //                 icon: MediumSelectableTextNotoSans(text: text)(
      //                   text: getLocalizedString(i18.common.TAKE_ACTION),
      //                   size: o == Orientation.portrait ? 14.sp : 8.sp,
      //                   color: BaseConfig.mainBackgroundColor,
      //                   fontWeight: FontWeight.w600,
      //                 ),
      //                 itemBuilder: (context) => _timelineController
      //                     .workflowBusinessServices
      //                     .businessServices!
      //                     .first
      //                     .states!
      //                     .where((s) => s.uuid == statusMap.statusId)
      //                     .first
      //                     .actions!
      //                     .map(
      //                       (action) => PopupMenuItem<String>(
      //                         value: action.action,
      //                         child: SmallTextNotoSans(
      //                           text: LocalizeUtils.getTakeActionLocal(
      //                             action.action,
      //                             workflowCode: statusMap.businessService!,
      //                             module: Modules.PT,
      //                             isCommon: true,
      //                           ),
      //                           color: BaseConfig.textColor,
      //                           fontWeight: FontWeight.w600,
      //                           size: o == Orientation.portrait ? 14.sp : 8.sp,
      //                         ),
      //                       ),
      //                     )
      //                     .toList(),
      //                 onSelected: (value) async {
      //                   dPrint("Action $value");
      //                   dPrint("Work Flow Id ${statusMap.businessService!}");

      //                   // Get the next state of the action
      //                   String uuid = _timelineController
      //                           .workflowBusinessServices
      //                           .businessServices
      //                           ?.first
      //                           .states
      //                           ?.where((s) => s.uuid == statusMap.statusId)
      //                           .first
      //                           .actions
      //                           ?.where((a) => a.action == value)
      //                           .first
      //                           .nextState ??
      //                       '';

      //                   dPrint('UUID: $uuid');

      //                   if (uuid.isEmpty) {
      //                     snackBar(
      //                       'InComplete',
      //                       'Next State is Empty',
      //                       Colors.green,
      //                     );
      //                     return;
      //                   }

      //                   if (value != BaseAction.reject.name &&
      //                       value != BaseAction.sendBackToCitizen.name) {
      //                     await _timelineController.getEmployees(
      //                       token: _authController.token!.accessToken!,
      //                       tenantId: _item!.businessObject!.tenantId!,
      //                       uuid: uuid,
      //                     );
      //                   }

      //                   if (!context.mounted) return;

      //                   _inboxController.actionDialogue(
      //                     context,
      //                     workFlowId: statusMap.businessService!,
      //                     action: value,
      //                     module: Modules.PT,
      //                     sectionType: ModulesEmp.PT_SERVICES,
      //                     tenantId: _item!.businessObject!.tenantId!,
      //                     businessService: BusinessService.PT,
      //                   );
      //                 },
      //               ),
      //             )
      //           : const SizedBox.shrink()),
      // ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => isLoading.value
              ? showCircularIndicator()
              : SingleChildScrollView(
                  physics: AppPlatforms.platformPhysics(),
                  child: Padding(
                    padding: EdgeInsets.all(16.w),
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
                                  MediumText(
                                    text: getLocalizedString(
                                      i18.common.DOWNLOAD,
                                    ),
                                    color: BaseConfig.redColor1,
                                    size: 14.0,
                                  ),
                                ],
                              ),
                              itemBuilder: (context) => <PopupMenuItem<String>>[
                                //UC_Challan download
                                PopupMenuItem<String>(
                                  value: getLocalizedString(
                                    i18.challans.UC_CHALLAN,
                                  ),
                                  child: MediumSelectableTextNotoSans(
                                    text: getLocalizedString(
                                      i18.challans.UC_CHALLAN,
                                    ),
                                  ),
                                  onTap: () async {
                                    //TODO: Raw pdf download
                                    final urlQuery =
                                        '${Url.UC_CHALLAN_DOWNLOAD_URL}?challanNo=${challan!.challanNo}&tenantId=${tenant!.code}';

                                    await _downloadController
                                        .directFileDownloadHttp(
                                      urlString: urlQuery,
                                      title: getLocalizedString(
                                        i18.challans.UC_CHALLAN,
                                        module: Modules.UC,
                                      ),
                                      fileName:
                                          'CHALLAN_${challan!.challanNo}.pdf',
                                      authToken:
                                          _authController.token!.accessToken!,
                                    );
                                  },
                                ),

                                if (challan?.applicationStatus == 'PAID')
                                  PopupMenuItem<String>(
                                    value: 'Receipt',
                                    child: const MediumSelectableTextNotoSans(
                                      text: 'Receipt',
                                    ),
                                    onTap: () async {
                                      final payment = await _paymentController
                                          .verifyPayment(
                                        tenantId: tenant!.code!,
                                        token:
                                            _authController.token!.accessToken!,
                                        businessService:
                                            challan!.businessService!,
                                        consumerCodes: challan!.challanNo!,
                                      );

                                      const PdfKey key =
                                          PdfKey.consolidated_receipt;
                                      final fileStoreId = await _fileController
                                          .getPdfServiceFile(
                                        tenantId: tenant!.code!,
                                        token:
                                            _authController.token!.accessToken!,
                                        key: key,
                                        payment: payment,
                                      );

                                      final fileStore =
                                          await _fileController.getFiles(
                                        tenantId: tenant!.code!,
                                        token:
                                            _authController.token!.accessToken!,
                                        fileStoreIds: fileStoreId,
                                      );

                                      if (fileStore?.fileStoreIds != null) {
                                        final url = fileStore!
                                            .fileStoreIds!.first.url!
                                            .split(',')
                                            .first;

                                        await _downloadController
                                            .starFileDownload(
                                          url: url,
                                          title: 'Download receipt',
                                        );
                                      }
                                    },
                                  ),
                              ],
                            ),
                          ],
                        ),
                        _buildDetails(),
                      ],
                    ),
                  ),
                ),
        ),
      ),
    );
  }

  Widget _buildDetails() => BuildCard(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          spacing: 10.h,
          children: [
            ColumnHeaderText(
              label: getLocalizedString(
                i18.challans.CHALLAN_NO,
                module: Modules.UC,
              ),
              text: challan?.challanNo ?? 'N/A',
            ).paddingOnly(left: 7),
            _buildPrice(),
            BuildExpansion(
              title: getLocalizedString(
                i18.challans.UC_SERVICE_DETAILS_LABEL,
                module: Modules.UC,
              ),
              tilePadding: EdgeInsets.symmetric(horizontal: 7.w),
              children: [
                _buildServiceDetails(),
              ],
            ),
            BuildExpansion(
              title: getLocalizedString(
                i18.challans.UC_CONSUMER_DETAILS_LABEL,
                module: Modules.UC,
              ),
              tilePadding: EdgeInsets.symmetric(horizontal: 7.w),
              children: [
                _buildConsumerDetails(),
              ],
            ),
          ],
        ),
      );

  Widget _buildPrice() {
    final paymentController = Get.find<PaymentController>();
    return FutureBuilder(
      future: paymentController.searchBillById(
        tenantId: tenant!.code!,
        token: _authController.token!.accessToken!,
        service: challan!.businessService!,
        consumerCode: challan!.challanNo!,
      ),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final billData = snapshot.data!;

          final bill = billData.bill?.firstOrNull;

          final billDetails =
              bill?.billDetails?.firstOrNull?.billAccountDetails;

          if (isNotNullOrEmpty(billDetails)) {
            billDetails!.sort((a, b) => a.order!.compareTo(b.order!));
          }

          // final ucLocalList = [
          //   'ADVT.HOARDINGS_FIELD_FEE',
          //   'ADVT.HOARDINGS_TAX',
          //   'RT.PARKING_FEE_TAX',
          //   'RT.PARKING_FEE_FIELD_FEE',
          //   'CH.Burning_of_Waste_Challan_fee_TAX',
          //   'CH.Burning_of_Waste_Challan_fee_SGST',
          //   'CH.Burning_of_Waste_Challan_fee_CGST',
          //   'CH.Burning_of_Waste_Challan_fee_FIELD_FEE',
          //   'ADVT.LIGHT_WALA_BOARD_FIELD_FEE',
          //   'ADVT.LIGHT_WALA_BOARD_TAX',
          //   'CH.Santitation_dumping_garbage_TAX',
          //   'CH.Santitation_dumping_garbage_CGST',
          //   'CH.Santitation_dumping_garbage_SGST',
          //   'CH.Santitation_dumping_garbage_FIELD_FEE',
          //   'ADVT.WALL_PAINT_ADVERTISEMENT_FIELD_FEE',
          //   'ADVT.WALL_PAINT_ADVERTISEMENT_TAX',
          // ];

          return isNotNullOrEmpty(bill)
              ? Column(
                  children: [
                    if (isNotNullOrEmpty(billDetails)) ...[
                      const Divider(
                        color: BaseConfig.greyColor2,
                      ),
                      ListView.builder(
                        itemCount: billDetails!.length,
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemBuilder: (context, index) {
                          final billAccount = billDetails[index];

                          final text = getLocalizedString(
                            billAccount.taxHeadCode!
                                .replaceAll('.', '_')
                                .toUpperCase(),
                          );

                          final isUcModule = text.contains('_');

                          return Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 2,
                                child: MediumTextNotoSans(
                                  text: getLocalizedString(
                                    billAccount.taxHeadCode!
                                        .replaceAll('.', '_')
                                        .toUpperCase(),
                                    module: isUcModule == true
                                        ? Modules.UC
                                        : Modules.COMMON,
                                  ),
                                  maxLine: 2,
                                  textOverflow: TextOverflow.ellipsis,
                                ),
                              ),
                              MediumSelectableTextNotoSans(
                                text: '₹ ${billAccount.amount!.toInt()}',
                              ),
                            ],
                          ).paddingAll(7);
                        },
                      ),
                      const Divider(
                        color: BaseConfig.greyColor2,
                      ),
                    ],
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        MediumSelectableTextNotoSans(
                          text: getLocalizedString(
                            i18.challans.UC_TOTAL_DUE_AMOUT_LABEL,
                            module: Modules.UC,
                          ),
                        ),
                        MediumSelectableTextNotoSans(
                          text: isNotNullOrEmpty(bill?.totalAmount)
                              ? '₹ ${bill!.totalAmount!.toInt()}'
                              : 'N/A',
                          fontWeight: FontWeight.w600,
                        ),
                      ],
                    ).paddingAll(7),
                  ],
                )
              : const SizedBox.shrink();
        } else if (snapshot.hasError) {
          return networkErrorPage(context, () => build(context));
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

  Widget _buildServiceDetails() => Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        spacing: 10.h,
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.challans.SERVICE_CATEGORY,
              module: Modules.UC,
            ),
            text: isNotNullOrEmpty(challan?.businessService)
                ? getLocalizedString(
                    '${i18.challans.UC_BUSINESS_SERVICE}${challan!.businessService!.replaceAll('.', '_').toUpperCase()}',
                  )
                : 'N/A',
          ).paddingOnly(left: 7),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.challans.FROM_DATE,
              module: Modules.UC,
            ),
            text: challan?.taxPeriodFrom
                    ?.toCustomDateFormat(pattern: 'd/MMM/yyyy') ??
                'N/A',
          ).paddingOnly(left: 7),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.challans.TO_DATE,
              module: Modules.UC,
            ),
            text: challan?.taxPeriodTo
                    ?.toCustomDateFormat(pattern: 'd/MMM/yyyy') ??
                'N/A',
          ).paddingOnly(left: 7),

          //TODO: UC Comment - Pending from API
          ColumnHeaderText(
            label: getLocalizedString(
              i18.challans.UC_COMMENT_LABEL,
              module: Modules.UC,
            ),
            text: 'N/A',
          ).paddingOnly(left: 7),

          ColumnHeaderText(
            label: getLocalizedString(i18.common.EMP_INBOX_STATUS),
            text: isNotNullOrEmpty(challan?.applicationStatus)
                ? getLocalizedString(
                    'UC_${challan!.applicationStatus}',
                  )
                : 'N/A',
          ).paddingOnly(left: 7, bottom: 10),
        ],
      );

  Widget _buildConsumerDetails() => Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        spacing: 10.h,
        children: [
          ColumnHeaderText(
            label: getLocalizedString(
              i18.challans.CONSUMER_NAME,
              module: Modules.UC,
            ),
            text: challan?.citizen?.name ?? 'N/A',
          ).paddingOnly(left: 7),
          ColumnHeaderText(
            label: getLocalizedString(i18.challans.MOBILE_NUMBER),
            text: challan?.citizen?.mobileNumber ?? 'N/A',
          ).paddingOnly(left: 7),
          ColumnHeaderText(
            label: getLocalizedString(
              i18.challans.EMAIL_ID,
              module: Modules.UC,
            ),
            text: challan?.citizen?.emailId ?? 'N/A',
          ).paddingOnly(left: 7),
          ColumnHeaderText(
            label: getLocalizedString(i18.challans.DOOR_NO),
            text: challan?.address?.doorNo ?? 'N/A',
          ).paddingOnly(left: 7),
          ColumnHeaderText(
            label: getLocalizedString(i18.challans.UC_BUILDING_NAME),
            text: challan?.address?.buildingName ?? 'N/A',
          ).paddingOnly(left: 7),
          ColumnHeaderText(
            label: getLocalizedString(i18.challans.UC_STREET_NAME),
            text: challan?.address?.street ?? 'N/A',
          ).paddingOnly(left: 7),
          ColumnHeaderText(
            label: getLocalizedString(i18.challans.MOHALLA),
            text: isNotNullOrEmpty(
              challan?.address?.locality,
            )
                ? getLocalizedString(
                    getLocality(
                      tenant!,
                      challan?.address?.locality?.code ?? '',
                    ),
                  )
                : 'N/A',
          ).paddingOnly(left: 7, bottom: 10),
        ],
      );
}
