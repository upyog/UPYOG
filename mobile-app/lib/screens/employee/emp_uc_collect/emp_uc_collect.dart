import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/acknowlegement/emp_acknowledge_screen.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/challan_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/common/locality/locality_model.dart';
import 'package:mobile_app/model/request/emp_challan_request/challan_request_model.dart';
import 'package:mobile_app/model/request/emp_challan_request/taxt_head_model.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/build_card.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class EmpUcCollectScreen extends StatefulWidget {
  const EmpUcCollectScreen({super.key});

  @override
  State<EmpUcCollectScreen> createState() => _EmpUcCollectScreenState();
}

class _EmpUcCollectScreenState extends State<EmpUcCollectScreen> {
  final _authController = Get.find<AuthController>();
  final _commonController = Get.find<CommonController>();
  final _languageController = Get.find<LanguageController>();
  final _localityController = Get.put(LocalityController());
  final _cityController = Get.find<CityController>();
  final _challanController = Get.find<ChallanController>();
  final _key = GlobalKey<FormState>();

  late TenantTenant tenant;

  var email = ''.obs,
      name = ''.obs,
      mobile = ''.obs,
      doorNo = ''.obs,
      buildingName = ''.obs,
      street = ''.obs,
      pincode = ''.obs,
      localityCode = ''.obs,
      amount = ''.obs,
      isLoading = false.obs;

  DateTime? fromDate, toDate;

  ChallanRequestModel? challanRequestModel;

  @override
  void initState() {
    super.initState();
    _clear();
    _init();
  }

  void _init() async {
    isLoading.value = true;
    tenant = await getCityTenantEmployee();

    await Future.wait([
      _localityController.fetchLocality(hierarchyTypeCode: "ADMIN"),
      _languageController.getMdmsStaticData(isUc: true, tenantId: tenant.code!),
      _commonController.fetchLabels(modules: Modules.UC),
    ]);

    isLoading.value = false;
  }

  void setEmail(val) {
    email.value = val;
  }

  CitizenRequestModel _buildCitizen() {
    return CitizenRequestModel(
      name: name.value,
      mobileNumber: mobile.value,
      emailId: email.value,
    );
  }

  AddressRequestModel _buildAddress() {
    return AddressRequestModel(
      buildingName: buildingName.value,
      doorNo: doorNo.value,
      street: street.value,
      locality: LocalityRequestModel(code: localityCode.value),
      pincode: pincode.value,
    );
  }

  List<AmountRequestModel> _buildAmountList() {
    return _cityController.taxHeadAmounts
        .map(
          (taxHead) => AmountRequestModel(
            taxHeadCode: taxHead.taxHead,
            amount: taxHead.amount,
          ),
        )
        .toList();
  }

  void validate() async {
    if (!_key.currentState!.validate()) return;

    final citizen = _buildCitizen();
    final address = _buildAddress();
    final amountList = _buildAmountList();

    ChallanRequestModel challan = ChallanRequestModel(
      citizen: citizen,
      businessService: _cityController.filteredTaxHead.first.service!,
      consumerType:
          _cityController.selectedServiceCategory.value.split('_').last,
      taxPeriodFrom: fromDate!.millisecondsSinceEpoch,
      taxPeriodTo: toDate!.millisecondsSinceEpoch,
      tenantId: tenant.code!,
      address: address,
      amount: amountList,
    );

    final challanId = await _challanController.createChallan(
      tenantId: challan.tenantId,
      token: _authController.token!.accessToken!,
      body: challan.toJson(),
    );

    if (isNotNullOrEmpty(challanId)) {
      Get.off(
        () => EmpAcknowledgeScreen(
          mainTitle: getLocalizedString(
            i18.challans.BILL_GENERATED_SUCCESS,
            module: Modules.UC,
          ),
          appIdName:
              getLocalizedString(i18.challans.CHALLAN_NO, module: Modules.UC),
          applicationNo: challanId!,
          message: getLocalizedString(
            i18.challans.BILL_GENERATION_MESSAGE,
            module: Modules.UC,
          ),
        ),
      );
    }
  }

  Future<void> selectFromDate() async {
    showDatePickerModal(
      context: context,
      selectedDate: fromDate,
      onDateSelected: (DateTime newDate) {
        setState(() {
          fromDate = newDate;
        });
      },
      onClear: () {
        setState(() {
          fromDate = null;
        });
      },
      onToday: () {
        setState(() {
          fromDate = DateTime.now();
        });
      },
    );
  }

  // To Date
  Future<void> selectToDate() async {
    showDatePickerModal(
      context: context,
      selectedDate: toDate,
      onDateSelected: (DateTime newDate) {
        setState(() {
          toDate = newDate;
        });
      },
      onClear: () {
        setState(() {
          toDate = null;
        });
      },
      onToday: () {
        setState(() {
          toDate = DateTime.now();
        });
      },
    );
  }

  void _clear() {
    fromDate = null;
    toDate = null;
    name.value = '';
    mobile.value = '';
    email.value = '';
    buildingName.value = '';
    doorNo.value = '';
    street.value = '';
    localityCode.value = '';
    pincode.value = '';
    amount.value = '';
    _cityController.clearUc();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(i18.challans.NEW_CHALLAN, module: Modules.UC),
        onPressed: () => Get.back(),
      ),
      bottomNavigationBar: Obx(
        () => isLoading.value
            ? const SizedBox.shrink()
            : Padding(
                padding: EdgeInsets.all(16.w),
                child: FilledButtonApp(
                  isLoading: _challanController.isLoading.value,
                  circularColor: BaseConfig.mainBackgroundColor,
                  onPressed: () => validate(),
                  text: getLocalizedString(i18.common.UC_SUBMIT),
                ),
              ),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Obx(
          () => isLoading.value
              ? showCircularIndicator()
              : Padding(
                  padding: EdgeInsets.all(16.w),
                  child: SingleChildScrollView(
                    child: BuildCard(
                      child: Form(
                        key: _key,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            BigTextNotoSans(
                              text: getLocalizedString(
                                i18.challans.CONSUMER_DETAILS,
                                module: Modules.UC,
                              ),
                              fontWeight: FontWeight.w600,
                              size: 16.sp,
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            ColumnHeaderTextField(
                              label: getLocalizedString(
                                i18.challans.CONSUMER_NAME,
                                module: Modules.UC,
                              ),
                              hintText: '',
                              onChange: (p0) {
                                name.value = p0;
                              },
                              onSave: (p0) {
                                name.value = p0 ?? '';
                              },
                              keyboardType: TextInputType.text,
                              textInputAction: TextInputAction.done,
                              isRequired: true,
                              validator: (val) {
                                if (!isNotNullOrEmpty(val)) {
                                  return 'Required Field';
                                } else if (val!.trim().contains('.')) {
                                  return 'Invalid Input';
                                }
                                return null;
                              },
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            ColumnHeaderTextField(
                              label: getLocalizedString(
                                i18.challans.MOBILE_NUMBER,
                                module: Modules.UC,
                              ),
                              hintText: '',
                              prefixIcon: Padding(
                                padding:
                                    const EdgeInsets.fromLTRB(20, 15, 20, 15),
                                child: MediumTextNotoSans(
                                  text: '+91',
                                  fontWeight: FontWeight.w600,
                                  size: 14.sp,
                                ),
                              ),
                              onChange: (p0) {
                                mobile.value = p0;
                              },
                              onSave: (p0) {
                                mobile.value = p0 ?? '';
                              },
                              isRequired: true,
                              keyboardType: AppPlatforms.platformKeyboardType(),
                              textInputAction: TextInputAction.done,
                              inputFormatters: [
                                LengthLimitingTextInputFormatter(10),
                                FilteringTextInputFormatter.digitsOnly,
                              ],
                              validator: (val) {
                                if (!isNotNullOrEmpty(val)) {
                                  return 'Required Field';
                                }
                                return null;
                              },
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            ColumnHeaderTextField(
                              label: getLocalizedString(
                                i18.challans.EMAIL_ID,
                                module: Modules.UC,
                              ),
                              hintText: '',
                              onChange: setEmail,
                              onSave: setEmail,
                              keyboardType: TextInputType.emailAddress,
                              textInputAction: TextInputAction.done,
                              validator: (val) {
                                if (isNotNullOrEmpty(val) &&
                                    !val.isValidEmail()) {
                                  return getLocalizedString(
                                    i18.common.UC_EMAIL_ERROR,
                                  );
                                }
                                return null;
                              },
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            ColumnHeaderTextField(
                              label: getLocalizedString(
                                i18.challans.DOOR_NO,
                                module: Modules.UC,
                              ),
                              hintText: '',
                              onChange: (p0) {
                                doorNo.value = p0;
                              },
                              onSave: (p0) {
                                doorNo.value = p0 ?? '';
                              },
                              keyboardType: TextInputType.text,
                              textInputAction: TextInputAction.done,
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            ColumnHeaderTextField(
                              label: getLocalizedString(
                                i18.challans.BUILDING_NAME,
                                module: Modules.UC,
                              ),
                              hintText: '',
                              onChange: (p0) {
                                buildingName.value = p0;
                              },
                              onSave: (p0) {
                                buildingName.value = p0 ?? '';
                              },
                              keyboardType: TextInputType.text,
                              textInputAction: TextInputAction.done,
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            ColumnHeaderTextField(
                              label: getLocalizedString(
                                i18.challans.STREET_NAME,
                                module: Modules.UC,
                              ),
                              hintText: '',
                              onChange: (p0) {
                                street.value = p0;
                              },
                              onSave: (p0) {
                                street.value = p0 ?? '';
                              },
                              keyboardType: TextInputType.text,
                              textInputAction: TextInputAction.done,
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            ColumnHeaderTextField(
                              label: getLocalizedString(
                                i18.challans.PIN_CODE,
                                module: Modules.UC,
                              ),
                              hintText: '',
                              onChange: (p0) {
                                pincode.value = p0;
                              },
                              onSave: (p0) {
                                pincode.value = p0 ?? '';
                              },
                              keyboardType: AppPlatforms.platformKeyboardType(),
                              textInputAction: TextInputAction.done,
                              inputFormatters: [
                                FilteringTextInputFormatter.digitsOnly,
                              ],
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            ColumnHeaderDropdownSearch(
                              label: getLocalizedString(
                                i18.challans.MOHALLA,
                                module: Modules.UC,
                              ),
                              options: _localityController.locality
                                      ?.tenantBoundary?.firstOrNull?.boundary
                                      ?.map((e) => e.name ?? '')
                                      .toList() ??
                                  [],
                              onChanged: (value) {
                                final locality = _localityController.locality
                                    ?.tenantBoundary?.firstOrNull?.boundary
                                    ?.firstWhereOrNull(
                                  (element) => element.name == value,
                                );
                                _localityController.selectedLocalityList.value =
                                    [
                                  locality ?? Boundary(),
                                ];

                                localityCode.value = locality?.code ?? '';

                                dPrint('Locality Code: ${locality?.code}');
                              },
                              textSize: 14.sp,
                              isRequired: true,
                              // enableLocal: true,
                              validator: (val) {
                                if (!isNotNullOrEmpty(val)) {
                                  return 'Required Field';
                                }
                                return null;
                              },
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            const Divider(),
                            SizedBox(
                              height: 10.h,
                            ),
                            _ServiceDetails(
                              fromDate: fromDate?.toCustomDateFormat() ??
                                  'dd-mm-yyyy',
                              toDate:
                                  toDate?.toCustomDateFormat() ?? 'dd-mm-yyyy',
                              onFromDatePressed: selectFromDate,
                              onToDatePressed: selectToDate,
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
        ),
      ),
    );
  }
}

class _ServiceDetails extends StatelessWidget {
  final String fromDate;
  final String toDate;
  final Function()? onFromDatePressed;
  final Function()? onToDatePressed;
  const _ServiceDetails({
    this.fromDate = 'dd-mm-yyyy',
    this.toDate = 'dd-mm-yyyy',
    this.onFromDatePressed,
    this.onToDatePressed,
  });

  @override
  Widget build(BuildContext context) {
    final cityController = Get.find<CityController>();
    final languageController = Get.find<LanguageController>();
    final businessServices = languageController
            .mdmsStaticData?.mdmsRes?.billingService?.businessService
            ?.where(
              (e) => e.isActive == true,
            )
            .toSet()
            .toList() ??
        [];

    final taxHeadMasters = languageController
            .mdmsStaticData?.mdmsRes?.billingService?.taxHeadMaster ??
        [];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        BigTextNotoSans(
          text: getLocalizedString(
            i18.challans.SERVICE_DETAILS,
            module: Modules.UC,
          ),
          fontWeight: FontWeight.w600,
          size: 16.sp,
        ),
        SizedBox(
          height: 10.h,
        ),
        ColumnHeaderTextField(
          label: getLocalizedString(
            i18.challans.CITY,
            module: Modules.UC,
          ),
          hintText: cityController.empSelectedCity.value == 'pg'
              ? getLocalizedString(i18.common.LOCATION_PREFIX)
              : getLocalizedString(
                  '${i18.common.LOCATION_PREFIX}${getTenantCode(cityController.empSelectedCity.value)}',
                ),
          isRequired: true,
          readOnly: true,
        ),
        SizedBox(
          height: 10.h,
        ),
        Obx(
          () => ColumnHeaderDropdownSearch(
            label: getLocalizedString(
              i18.challans.SERVICE_CATEGORY,
              module: Modules.UC,
            ),
            options: businessServices
                .map(
                  (e) =>
                      '${i18.challans.UC_BUSINESS_SERVICE}${e.code?.split('.').firstOrNull?.toUpperCase()}',
                )
                .toSet()
                .toList(),
            onChanged: (String? value) {
              dPrint('Service category: $value');
              cityController.selectedServiceType.value = '';
              cityController.filteredBusinessService.clear();
              cityController.filteredTaxHead.clear();
              cityController.taxHeadAmounts.clear();

              cityController.setSelectedServiceCategoryUC(value ?? '');
              final codes = businessServices
                  .where(
                    (service) =>
                        service.code?.split('.').firstOrNull ==
                        value?.split('_').lastOrNull,
                  )
                  .toList();

              cityController.filteredBusinessService.value = codes;
            },
            textSize: 14.sp,
            selectedValue: cityController.selectedServiceCategory.value,
            isRequired: true,
            enableLocal: true,
            validator: (val) {
              if (!isNotNullOrEmpty(val)) {
                return 'Required Field';
              }
              return null;
            },
          ).marginOnly(bottom: 10.h),
        ),
        Obx(
          () => cityController.filteredBusinessService.isNotEmpty
              ? ColumnHeaderDropdownSearch(
                  label: getLocalizedString(
                    i18.challans.SERVICE_TYPE,
                    module: Modules.UC,
                  ),
                  options: cityController.filteredBusinessService
                      .map(
                        (e) =>
                            '${i18.challans.UC_BUSINESS_SERVICE}${e.code?.replaceAll('.', '_').toUpperCase()}',
                      )
                      .toSet()
                      .toList(),
                  onChanged: (String? value) {
                    dPrint('Service category: $value');
                    cityController.selectedServiceType.value = value ?? '';
                    final type = value
                        ?.replaceFirst(
                          i18.challans.UC_BUSINESS_SERVICE,
                          '',
                        )
                        .replaceFirst('_', '.');

                    dPrint(type!);

                    final codes = taxHeadMasters
                        .where(
                          (head) => head.service?.toUpperCase() == type,
                        )
                        .toList();
                    cityController.filteredTaxHead.assignAll(codes);
                    cityController.taxHeadAmounts.clear();
                  },
                  textSize: 14.sp,
                  isRequired: true,
                  enableLocal: true,
                  selectedValue: cityController.selectedServiceType.value,
                  validator: (val) {
                    if (!isNotNullOrEmpty(val)) {
                      return 'Required Field';
                    }
                    return null;
                  },
                ).marginOnly(bottom: 10.h)
              : const SizedBox.shrink(),
        ),
        ColumnHeaderTextField(
          label: getLocalizedString(
            i18.challans.FROM_DATE,
            module: Modules.UC,
          ),
          hintText: fromDate,
          icon: Icons.calendar_today,
          textSize: 14.sp,
          isRequired: true,
          readOnly: true,
          textInputAction: TextInputAction.done,
          suffixIcon: IconButton(
            onPressed: onFromDatePressed,
            icon: const Icon(
              Icons.calendar_today,
              color: BaseConfig.appThemeColor1,
            ),
          ),
          validator: (val) {
            if (!isNotNullOrEmpty(fromDate) || fromDate == 'dd-mm-yyyy') {
              return 'Required Field';
            }
            return null;
          },
        ).marginOnly(bottom: 10.h),
        ColumnHeaderTextField(
          label: getLocalizedString(
            i18.challans.TO_DATE,
            module: Modules.UC,
          ),
          hintText: toDate,
          icon: Icons.calendar_today,
          textSize: 14.sp,
          isRequired: true,
          readOnly: true,
          textInputAction: TextInputAction.done,
          suffixIcon: IconButton(
            onPressed: onToDatePressed,
            icon: const Icon(
              Icons.calendar_today,
              color: BaseConfig.appThemeColor1,
            ),
          ),
          validator: (val) {
            if (!isNotNullOrEmpty(toDate) || toDate == 'dd-mm-yyyy') {
              return 'Required Field';
            }
            return null;
          },
        ).marginOnly(bottom: 10.h),
        Obx(() {
          cityController.filteredTaxHead
              .sort((a, b) => a.order?.compareTo(b.order));

          return ListView.builder(
            itemCount: cityController.filteredTaxHead.length,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              final taxHead = cityController.filteredTaxHead[index];
              final taxCode = taxHead.code?.replaceAll('.', '_') ?? '';
              return ColumnHeaderTextField(
                label: getLocalizedString(
                  taxCode,
                  module: taxCode == 'ADVT_GAS_BALLOON_ADVERTISEMENT_FIELD_FEE'
                      ? Modules.COMMON
                      : Modules.UC,
                ),
                hintText: '0',
                textSize: 14.sp,
                isRequired: true,
                prefixIcon: Icon(
                  Icons.currency_rupee,
                  size: 16.sp,
                ),
                keyboardType: AppPlatforms.platformKeyboardType(),
                textInputAction: TextInputAction.done,
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                ],
                onChange: (val) {
                  final value = val == '' ? '0' : val;
                  cityController.taxHeadAmounts.removeWhere(
                    (element) => element.taxHead == taxHead.code,
                  );

                  final taxCode = TaxHeadModel(
                    taxHead: taxHead.code!,
                    amount: int.parse(value),
                  );
                  cityController.taxHeadAmounts.add(taxCode);
                },
                validator: (val) {
                  if (!isNotNullOrEmpty(val)) {
                    return 'Required Field';
                  }
                  return null;
                },
              ).marginOnly(bottom: 10.h);
            },
          );
        }),
      ],
    );
  }
}
