import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card_billing.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class PropertyMyBillsScreen extends StatefulWidget {
  const PropertyMyBillsScreen({super.key});

  @override
  State<PropertyMyBillsScreen> createState() => _PropertyMyBillsScreenState();
}

class _PropertyMyBillsScreenState extends State<PropertyMyBillsScreen> {
  final _propertiesTaxController = Get.find<PropertiesTaxController>();
  final _authController = Get.find<AuthController>();
  final _profileController = Get.find<EditProfileController>();

  late Future<BillInfo?> billFuture;

  @override
  void initState() {
    super.initState();
    init();
  }

  init() {
    _propertiesTaxController.length.value = 0;
    billFuture = _fetchMyBills();
  }

  Future<BillInfo?> _fetchMyBills() {
    return _propertiesTaxController.getPtMyBills(
      tenantId: BaseConfig.STATE_TENANT_ID,
      token: _authController.token!.accessToken!,
      mobileNumber: _profileController.userProfile.user!.first.mobileNumber!,
      businessService: BusinessService.PT,
    );
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, orientation) {
        return Scaffold(
          appBar: HeaderTop(
            titleWidget: Obx(
              () => Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    getLocalizedString(
                      i18.propertyTax.MY_BILLS,
                      module: Modules.PT,
                    ),
                  ),
                  Text(' (${_propertiesTaxController.length.value})'),
                ],
              ),
            ),
            onPressed: () => Navigator.of(context).pop(),
            orientation: orientation,
          ),
          body: Padding(
            padding: EdgeInsets.all(16.w),
            child: FutureBuilder<BillInfo?>(
              future: billFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return showCircularIndicator();
                } else if (snapshot.hasError) {
                  return networkErrorPage(context, () => init());
                } else if (snapshot.data is String ||
                    snapshot.data == null ||
                    snapshot.hasData == false) {
                  return const NoApplicationFoundWidget();
                }

                final billInfo = snapshot.data;
                final billList = _sortBills(billInfo?.bill);

                if (billList.isEmpty) {
                  return const NoApplicationFoundWidget();
                }

                return _BillList(
                  bills: billList,
                  isLoading: _propertiesTaxController.isLoading,
                  onLoadMore: _fetchMyBills,
                );
              },
            ),
          ),
        );
      },
    );
  }

  List<Bill> _sortBills(List<Bill>? bills) {
    bills?.sort((a, b) {
      return b.auditDetails?.lastModifiedTime?.compareTo(
            a.auditDetails?.lastModifiedTime ?? 0,
          ) ??
          0;
    });
    return bills ?? [];
  }
}

class _BillList extends StatelessWidget {
  final List<Bill> bills;
  final RxBool isLoading;
  final Future<BillInfo?> Function() onLoadMore;

  const _BillList({
    required this.bills,
    required this.isLoading,
    required this.onLoadMore,
  });

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: bills.length + 1,
      itemBuilder: (context, index) {
        if (index == bills.length) {
          return Obx(
            () => FilledButtonApp(
              text: getLocalizedString(
                i18.propertyTax.LOAD_MORE,
                module: Modules.PT,
              ),
              isLoading: isLoading.value,
              circularColor: BaseConfig.fillAppBtnCircularColor,
              onPressed: () async => await onLoadMore(),
            ),
          );
        } else {
          final bill = bills[index];
          return _BillCard(bill: bill);
        }
      },
    );
  }
}

class _BillCard extends StatelessWidget {
  final Bill bill;

  const _BillCard({required this.bill});

  @override
  Widget build(BuildContext context) {
    return ComplainCardBilling(
      title: '₹${bill.totalAmount}',
      overDueDate: '882', // Placeholder, update as needed.
      id: '${getLocalizedString(i18.propertyTax.PROPERTY_ID, module: Modules.PT)}: ${bill.consumerCode}',
      ownerName:
          '${getLocalizedString(i18.propertyTax.CS_OWNER_NAME, module: Modules.PT)}: ${bill.payerName}',
      address:
          '${getLocalizedString(i18.propertyTax.PROPERTY_ADDRESS, module: Modules.PT)}: ${getLocalizedString(bill.payerAddress, module: Modules.PT)}',
      billingCycle:
          'Billing Cycle: FY ${bill.billDetails?.first.fromPeriod.toCustomDateFormat(pattern: 'yyyy')}'
          '- ${bill.billDetails?.first.toPeriod.toCustomDateFormat(pattern: 'yyyy')}',
      dueDate:
          '${getLocalizedString(i18.propertyTax.DUE_DATE, module: Modules.PT)}: ${bill.billDetails?.first.toPeriod.toCustomDateFormat()}',
      onTap: () {
        Get.toNamed(
          AppRoutes.BILL_DETAIL_SCREEN,
          arguments: {
            'billData': bill,
            'module': Modules.PT,
          },
        );
      },
      status: bill.status ?? 'N/A',
      statusColor: getStatusColor(bill.status ?? ''),
      statusBackColor: getStatusBackColor(bill.status ?? ''),
    );
  }
}
