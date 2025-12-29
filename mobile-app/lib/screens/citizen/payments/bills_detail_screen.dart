import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/model/citizen/bill/bill_info.dart';
import 'package:mobile_app/model/citizen/bill/demands.dart';
import 'package:mobile_app/screens/citizen/payments/payment_screen.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/build_expansion.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class FinancialYear {
  final int fromPeriod;
  final int toPeriod;
  final double totalAmount;
  final String billId, dueDate;
  final List<TaxDetail> taxDetails;

  FinancialYear({
    required this.fromPeriod,
    required this.toPeriod,
    required this.totalAmount,
    required this.billId,
    required this.dueDate,
    required this.taxDetails,
  });
}

// TaxDetail Model
class TaxDetail {
  final String taxHeadCode;
  final double amount;

  TaxDetail({
    required this.taxHeadCode,
    required this.amount,
  });
}

class BillsDetailScreen extends StatefulWidget {
  const BillsDetailScreen({super.key});

  @override
  State<BillsDetailScreen> createState() => _BillsDetailScreenState();
}

class _BillsDetailScreenState extends State<BillsDetailScreen> {
  final _authController = Get.find<AuthController>();

  List<FinancialYear> financialYearList = [];
  Set<String> taxHeadCodes = {};

  late Bill billData;
  late Demand demandData;

  final bill = Get.arguments['billData'];
  final Modules module = Get.arguments['module'];
  final advanceAmount =
      Get.arguments['advanceAmount'] as int?; // Advance Amount for fsm

  double areasAmount = 0.0;

  // TenantTenant? tenant;

  @override
  void initState() {
    super.initState();
    parseJsonData();
  }

  void parseJsonData() {
    if (bill is Bill) {
      billData = bill;

      _initBillInfo();
    } else if (bill is Demand) {
      demandData = bill;
    }
  }

  _initBillInfo() {
    billData.billDetails!.first.billAccountDetails!
        .sort((a, b) => a.order!.compareTo(b.order!));

    final List<FinancialYear> parsedList = billData.billDetails!.map((data) {
      final int fromPeriod = data.fromPeriod!;
      final int toPeriod = data.toPeriod!;

      final List<BillAccountDetail>? billAccountDetails =
          data.billAccountDetails;

      double totalAmount = billAccountDetails!.fold(0.0, (sum, item) {
        return sum + item.amount!.toDouble();
      });

      List<TaxDetail> taxDetails = billAccountDetails.map((item) {
        taxHeadCodes.add(item.taxHeadCode!);

        return TaxDetail(
          taxHeadCode: item.taxHeadCode!,
          amount: item.amount!,
        );
      }).toList();

      return FinancialYear(
        fromPeriod: fromPeriod,
        toPeriod: toPeriod,
        totalAmount: totalAmount,
        taxDetails: taxDetails,
        billId: 'N/A',
        dueDate: 'N/A',
      );
    }).toList();

    setState(() {
      financialYearList = parsedList;
    });
  }

  List<TableRow> _buildTableHeader() {
    List<Widget> headers = [
      SmallSelectableTextNotoSans(
        text: getLocalizedString(
          i18.propertyTax.CS_BILL_PERIOD,
        ),
      ),
      SmallSelectableTextNotoSans(
        text: getLocalizedString(
          i18.common.BILL_NO,
        ),
      ),
      SmallSelectableTextNotoSans(
        text: getLocalizedString(
          i18.common.BILL_DUE_DATE,
        ),
      ),
    ];

    for (String taxHeadCode in taxHeadCodes) {
      var isSw = taxHeadCode.contains('SW') || taxHeadCode.contains('WS');

      headers.add(
        SmallSelectableTextNotoSans(
          text: getLocalizedString(
            taxHeadCode,
            module: isSw
                ? Modules.WS
                : module == Modules.UC
                    ? Modules.UC
                    : Modules.PT,
          ),
        ),
      );
    }

    headers.add(
      SmallSelectableTextNotoSans(
        text: getLocalizedString(
          i18.propertyTax.TL_COMMON_TOTAL_AMT,
        ),
      ),
    );

    return [
      TableRow(
        children: headers.map((header) {
          return Padding(
            padding: EdgeInsets.all(8.w),
            child: header,
          );
        }).toList(),
      ),
    ];
  }

  List<TableRow> _buildTableRows() {
    List<TableRow> rows = [];

    for (int i = 0; i < financialYearList.length; i++) {
      final year = financialYearList[i];

      dPrint('ffe fromPeriod: ${year.fromPeriod} - toPeriod: ${year.toPeriod}');

      var isSw = billData.businessService!.contains('SW') ||
          billData.businessService!.contains('WS');

      List<Widget> cells = [
        Padding(
          padding: EdgeInsets.all(8.w),
          child: SmallSelectableTextNotoSans(
            text: isSw
                ? '${year.fromPeriod.toCustomDateFormat(pattern: 'M/d/yyyy')}-\n${year.toPeriod.toCustomDateFormat(pattern: 'M/d/yyyy')}'
                : 'FY${year.fromPeriod.toCustomDateFormat(pattern: 'yyyy')}-${year.toPeriod.toCustomDateFormat(pattern: 'yy')}',
          ),
        ),
        Padding(
          padding: EdgeInsets.all(8.w),
          child: SmallSelectableTextNotoSans(
            text: year.billId,
          ),
        ),
        Padding(
          padding: EdgeInsets.all(8.w),
          child: SmallSelectableTextNotoSans(
            text: year.dueDate,
          ),
        ),
      ];

      for (String taxHeadCode in taxHeadCodes) {
        final taxDetail = year.taxDetails.firstWhere(
          (detail) => detail.taxHeadCode == taxHeadCode,
          orElse: () => TaxDetail(taxHeadCode: taxHeadCode, amount: 0.0),
        );
        cells.add(
          Padding(
            padding: EdgeInsets.all(8.w),
            child: SmallSelectableTextNotoSans(text: '₹ ${taxDetail.amount}'),
          ),
        );
      }

      cells.add(
        Padding(
          padding: EdgeInsets.all(8.w),
          child: SmallSelectableTextNotoSans(text: '₹ ${year.totalAmount}'),
        ),
      );

      rows.add(
        TableRow(
          decoration: BoxDecoration(
            color: getRowColor(i),
          ),
          children: cells,
        ),
      );
    }

    return rows;
  }

  void goPayment(Bill bill) async {
    var isLoading = Get.find<PaymentController>().isLoading;
    if (isLoading.isTrue) {
      Get.find<PaymentController>().isLoading.value = false;
    }

    // final String? userType =
    //     await HiveService.getData(Constants.USER_TYPE) as String?;

    // var user = userType ?? UserType.CITIZEN.name;

    // if (user == UserType.CITIZEN.name) {
    //   tenant = await getCityTenant();
    // } else {
    //   tenant = await getCityTenantEmployee();
    // }

    dPrint(module.name);

    Get.find<PaymentController>().hidePaymentLoader();

    Get.to(
      () => PaymentScreen(
        token: _authController.token!.accessToken!,
        consumerCode: bill.consumerCode!,
        businessService: getBusinessServiceByStatus(bill.businessService!),
        cityTenantId: bill.tenantId!,
        module: module.name,
        billId: bill.id!,
        totalAmount:
            module == Modules.FSM ? '$advanceAmount' : '${bill.totalAmount}',
      ),
    );
  }

  Widget _makePayment() {
    final checkBtn = billData.billDetails?.last.amount != 0.0;
    return FilledButtonApp(
      radius: 0,
      width: Get.width,
      text: getLocalizedString(
        i18.common.MAKE_PAYMENT,
      ),
      onPressed: () => checkBtn ? goPayment(billData) : null,
      circularColor: BaseConfig.fillAppBtnCircularColor,
      backgroundColor:
          checkBtn ? BaseConfig.appThemeColor1 : BaseConfig.shadeAmber,
    );
  }

  double billBallanceDue() {
    return billData.totalAmount! - (advanceAmount ?? 0);
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;

    if (bill is Bill) {
      financialYearList.sort(
        (a, b) => b.fromPeriod.compareTo(a.fromPeriod),
      );
      dPrint(billData.businessService.toString());
    }

    final isSwOrWs = bill is Bill
        ? (billData.businessService?.contains('SW') ?? false) ||
            (billData.businessService?.contains('WS') ?? false)
        : false;

    return bill is Bill
        ? Scaffold(
            appBar: HeaderTop(
              titleWidget: Text(
                getLocalizedString(
                  i18.propertyTax.PAYMENT_BILL_DETAILS,
                  module: billData.businessService == Modules.PT.name
                      ? Modules.PT
                      : Modules.COMMON,
                ),
              ),
              onPressed: () => Navigator.of(context).pop(),
              orientation: o,
            ),
            bottomNavigationBar: _makePayment(),
            body: SizedBox(
              height: Get.height,
              width: Get.width,
              child: SingleChildScrollView(
                child: Padding(
                  padding: EdgeInsets.all(16.w),
                  child: module == Modules.FSM
                      ? TaxBillInfoCard(
                          o: o,
                          consumerCode: billData.consumerCode!,
                          totalAmount: '₹ ${billData.totalAmount}',
                          advanceAmount: '₹ ${advanceAmount ?? 0}',
                          dueBalance: '₹ ${billBallanceDue()}',
                          paymentAmount: '₹ ${advanceAmount ?? 0}',
                        )
                      : Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Card(
                              child: Padding(
                                padding: EdgeInsets.all(10.w),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    ColumnHeaderText(
                                      label: getLocalizedString(
                                        billData.businessService ==
                                                BusinessService.PT_MUTATION.name
                                            ? i18.common.MUTATION_NUMBER_LABEL
                                            : isSwOrWs
                                                ? i18.waterSewerage
                                                    .WS_MYCONNECTIONS_CONSUMER_NO
                                                : module == Modules.UC
                                                    ? i18.challans.CHALLAN_NO
                                                    : module == Modules.NOC
                                                        ? i18.noc
                                                            .PAYMENT_CONSUMER_CODE
                                                        : i18.propertyTax
                                                            .PT_UNIQUE_PROPERTY_ID,
                                        module: billData.businessService ==
                                                BusinessService.PT_MUTATION.name
                                            ? Modules.COMMON
                                            : isSwOrWs
                                                ? Modules.WS
                                                : module == Modules.UC
                                                    ? Modules.UC
                                                    : module == Modules.NOC
                                                        ? Modules.COMMON
                                                        : Modules.PT,
                                      ),
                                      text: '${billData.consumerCode}',
                                      textSize: o == Orientation.portrait
                                          ? 12.sp
                                          : 8.sp,
                                    ),
                                    SizedBox(height: 10.h),
                                    if (billData.businessService !=
                                            BusinessServicesEmp
                                                .PT_MUTATION.name &&
                                        !isSwOrWs) ...[
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.propertyTax
                                              .PAYMENT_BILLING_PERIOD,
                                          module: module == Modules.UC
                                              ? Modules.COMMON
                                              : module == Modules.NOC
                                                  ? Modules.COMMON
                                                  : Modules.PT,
                                        ),
                                        text: module == Modules.UC
                                            ? '${billData.billDetails?.first.fromPeriod.toCustomDateFormat()} - ${billData.billDetails?.first.toPeriod.toCustomDateFormat()}'
                                            : 'FY ${billData.billDetails?.first.fromPeriod.toCustomDateFormat(pattern: 'yyyy')} - ${billData.billDetails?.first.toPeriod.toCustomDateFormat(pattern: 'yyyy')}',
                                        textSize: o == Orientation.portrait
                                            ? 12.sp
                                            : 8.sp,
                                      ),
                                      SizedBox(height: 10.h),
                                    ],

                                    //SW&WS
                                    if (isSwOrWs) ...[
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.propertyTax
                                              .PAYMENT_BILLING_PERIOD,
                                        ),
                                        text:
                                            '${billData.billDetails?.first.fromPeriod.toCustomDateFormat(pattern: 'd MMM yyyy')}'
                                            '- ${billData.billDetails?.first.toPeriod.toCustomDateFormat(pattern: 'd MMM yyyy')}',
                                        textSize: o == Orientation.portrait
                                            ? 12.sp
                                            : 8.sp,
                                      ),
                                      SizedBox(height: 10.h),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.common.BILL_NO,
                                        ),
                                        text: billData.billNumber ?? 'N/A',
                                        textSize: o == Orientation.portrait
                                            ? 12.sp
                                            : 8.sp,
                                      ),
                                      SizedBox(height: 10.h),
                                      ColumnHeaderText(
                                        label: getLocalizedString(
                                          i18.common.BILL_DUE_DATE,
                                        ),
                                        text: billData
                                                .billDetails?.first.expiryDate
                                                .toCustomDateFormat(
                                              pattern: 'M/d/yyyy',
                                            ) ??
                                            'N/A',
                                        textSize: o == Orientation.portrait
                                            ? 12.sp
                                            : 8.sp,
                                      ),
                                      SizedBox(height: 10.h),
                                    ],

                                    Container(
                                      decoration: BoxDecoration(
                                        borderRadius:
                                            BorderRadius.circular(10.r),
                                        color: BaseConfig.greyColor2
                                            .withValues(alpha: 0.3),
                                      ),
                                      child: ListView.builder(
                                        itemCount: billData.billDetails!.first
                                                .billAccountDetails!.length +
                                            1,
                                        shrinkWrap: true,
                                        physics:
                                            const NeverScrollableScrollPhysics(),
                                        itemBuilder: (context, index) {
                                          if (index ==
                                              billData.billDetails!.first
                                                  .billAccountDetails!.length) {
                                            return Padding(
                                              padding: EdgeInsets.all(10.w),
                                              child: Column(
                                                children: [
                                                  if (module != Modules.NOC)
                                                    ColumnHeaderText(
                                                      label: getLocalizedString(
                                                        i18.common
                                                            .COMMON_ARREARS,
                                                      ),
                                                      text: isNotNullOrEmpty(
                                                        areasAmount,
                                                      )
                                                          ? '₹ $areasAmount'
                                                          : '₹ 0.0',
                                                      textSize: o ==
                                                              Orientation
                                                                  .portrait
                                                          ? 12.sp
                                                          : 8.sp,
                                                    ),
                                                  const Divider(
                                                    color: BaseConfig
                                                        .mainBackgroundColor,
                                                    thickness: 2,
                                                  ),
                                                  ColumnHeaderText(
                                                    label: getLocalizedString(
                                                      i18.propertyTax
                                                          .PAYMENT_TOTAL_AMOUNT,
                                                      module: isSwOrWs ||
                                                              module ==
                                                                  Modules.UC
                                                          ? Modules.COMMON
                                                          : Modules.PT,
                                                    ),
                                                    text:
                                                        '₹ ${billData.totalAmount}',
                                                    fontWeight: FontWeight.bold,
                                                    textSize: o ==
                                                            Orientation.portrait
                                                        ? 14.sp
                                                        : 10.sp,
                                                  ),
                                                ],
                                              ),
                                            );
                                          } else {
                                            final billAccount = billData
                                                .billDetails!
                                                .first
                                                .billAccountDetails![index];

                                            return Padding(
                                              padding: EdgeInsets.all(10.w),
                                              child: ColumnHeaderText(
                                                label: getLocalizedString(
                                                  billAccount.taxHeadCode,
                                                  module: isSwOrWs
                                                      ? Modules.WS
                                                      : module == Modules.UC
                                                          ? Modules.UC
                                                          : module ==
                                                                  Modules.NOC
                                                              ? Modules.NOC
                                                              : Modules.PT,
                                                ),
                                                text: '₹ ${billAccount.amount}',
                                                textSize:
                                                    o == Orientation.portrait
                                                        ? 12.sp
                                                        : 8.sp,
                                              ),
                                            );
                                          }
                                        },
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                            SizedBox(height: 10.h),
                            if (isNotNullOrEmpty(
                                  billData.billDetails?.last.amount,
                                ) &&
                                !isSwOrWs &&
                                module != Modules.UC &&
                                module != Modules.NOC)
                              Card(
                                child: Padding(
                                  padding: EdgeInsets.all(16.w),
                                  child: Column(
                                    children: [
                                      BuildExpansion(
                                        title: getLocalizedString(
                                          i18.common.ARREARS_DETAILS,
                                        ),
                                        children: [
                                          SingleChildScrollView(
                                            scrollDirection: Axis.horizontal,
                                            child: Table(
                                              defaultColumnWidth:
                                                  const IntrinsicColumnWidth(),
                                              border: TableBorder.all(
                                                color: Colors.grey.shade300,
                                              ),
                                              children: [
                                                ..._buildTableHeader(),
                                                ..._buildTableRows(),
                                              ],
                                            ),
                                          ),
                                          SizedBox(height: 10.h),
                                          Row(
                                            children: [
                                              MediumSelectableTextNotoSans(
                                                text: getLocalizedString(
                                                  i18.common
                                                      .COMMON_ARREARS_TOTAL,
                                                ),
                                              ),
                                              MediumSelectableTextNotoSans(
                                                text:
                                                    ': ₹ ${billData.totalAmount}',
                                              ),
                                            ],
                                          ),
                                          SizedBox(height: 10.h),
                                        ],
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                          ],
                        ),
                ),
              ),
            ),
          )
        : _DemandDetailsScreen(
            demand: demandData,
            module: module,
            advanceAmount: advanceAmount ?? 0,
          );
  }
}

class _DemandDetailsScreen extends StatefulWidget {
  final Demand demand;
  final Modules module;
  final int advanceAmount;
  const _DemandDetailsScreen({
    required this.demand,
    required this.module,
    required this.advanceAmount,
  });

  @override
  State<_DemandDetailsScreen> createState() => __DemandDetailsScreenState();
}

class __DemandDetailsScreenState extends State<_DemandDetailsScreen> {
  final _authController = Get.find<AuthController>();

  Demand? demand;

  @override
  void initState() {
    super.initState();
    demand = widget.demand;
  }

  void goPayment() async {
    var isLoading = Get.find<PaymentController>().isLoading;
    if (isLoading.isTrue) {
      Get.find<PaymentController>().isLoading.value = false;
    }

    Get.to(
      () => PaymentScreen(
        token: _authController.token!.accessToken!,
        consumerCode: demand!.consumerCode!,
        businessService: getBusinessServiceByStatus(demand!.businessService!),
        cityTenantId: demand!.tenantId!,
        module: widget.module.name,
        billId: demand!.id!,
        totalAmount: '${dueBalance()}',
      ),
    );
  }

  Widget _makePayment() {
    return FilledButtonApp(
      radius: 0,
      width: Get.width,
      text: getLocalizedString(
        i18.common.MAKE_PAYMENT,
      ),
      onPressed: demand?.isPaymentCompleted == false ? () => goPayment() : null,
      circularColor: BaseConfig.fillAppBtnCircularColor,
      backgroundColor: demand?.isPaymentCompleted == false
          ? BaseConfig.appThemeColor1
          : BaseConfig.shadeAmber,
    );
  }

  int dueBalance() {
    final amountSum = totalAmount();
    return (amountSum - widget.advanceAmount);
  }

  int totalAmount() {
    return demand?.demandDetails!.fold(0, (sum, item) {
          return sum! + (item.taxAmount ?? 0);
        }) ??
        0;
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Text(
          getLocalizedString(
            i18.propertyTax.PAYMENT_BILL_DETAILS,
            module: Modules.COMMON,
          ),
        ),
        onPressed: () => Navigator.of(context).pop(),
        orientation: o,
      ),
      bottomNavigationBar: _makePayment(),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: EdgeInsets.all(16.w),
            child: TaxBillInfoCard(
              o: o,
              consumerCode: demand!.consumerCode!,
              totalAmount: '₹ ${totalAmount()}',
              advanceAmount: '₹ ${widget.advanceAmount}',
              dueBalance: '₹ ${dueBalance()}',
              paymentAmount: '₹ ${dueBalance()}',
            ),
          ),
        ),
      ),
    );
  }
}

class TaxBillInfoCard extends StatelessWidget {
  const TaxBillInfoCard({
    super.key,
    required this.o,
    required this.consumerCode,
    required this.totalAmount,
    required this.advanceAmount,
    required this.dueBalance,
    required this.paymentAmount,
  });

  final Orientation o;
  final String consumerCode;
  final String totalAmount;
  final String advanceAmount;
  final String dueBalance;
  final String paymentAmount;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: EdgeInsets.all(10.w),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ColumnHeaderText(
              label: getLocalizedString(
                i18.fsmLocal.FSM_REQUEST_NO,
                module: Modules.FSM,
              ),
              text: consumerCode,
              textSize: o == Orientation.portrait ? 12.sp : 8.sp,
            ),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.fsmLocal.FSM_TOTAL_AMOUNT,
                module: Modules.FSM,
              ),
              text: totalAmount,
              textSize: o == Orientation.portrait ? 12.sp : 8.sp,
            ),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.fsmLocal.FSM_ADV_AMOUNT,
                module: Modules.FSM,
              ),
              text: advanceAmount,
              textSize: o == Orientation.portrait ? 12.sp : 8.sp,
            ),
            SizedBox(height: 10.h),
            ColumnHeaderText(
              label: getLocalizedString(
                i18.fsmLocal.FSM_DUE_AMOUNT,
                module: Modules.FSM,
              ),
              text: dueBalance,
              textSize: o == Orientation.portrait ? 12.sp : 8.sp,
            ),
            SizedBox(height: 30.h),
            const Divider(),
            ColumnHeaderText(
              fontWeight: FontWeight.bold,
              textColor: BaseConfig.appThemeColor1,
              textSize: 14.sp,
              label: getLocalizedString(
                i18.fsmLocal.FSM_PAYMENT_AMOUNT,
              ),
              text: paymentAmount,
            ),
          ],
        ),
      ),
    );
  }
}
