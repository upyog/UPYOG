import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/model/citizen/water_sewerage/sewerage.dart' as sw;
import 'package:mobile_app/model/citizen/water_sewerage/water.dart' as ws;
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/build_card.dart';
import 'package:mobile_app/widgets/colum_header_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/icon_text_fill_button.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class EmpWaterEditApplicationScreen extends StatefulWidget {
  const EmpWaterEditApplicationScreen({super.key});

  @override
  State<EmpWaterEditApplicationScreen> createState() =>
      _EmpWaterEditApplicationScreenState();
}

class _EmpWaterEditApplicationScreenState
    extends State<EmpWaterEditApplicationScreen> {
  final _waterController = Get.find<WaterController>();

  final List<_RoadCuttingWidget> _roadCuttingWidgets = [];
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  ws.WaterConnection? _waterConnection;
  sw.SewerageConnection? _sewerageConnection;

  @override
  initState() {
    super.initState();
    init();
  }

  init() {
    final data = Get.arguments["data"];
    if (data is ws.WaterConnection) {
      _waterConnection = data;
    } else if (data is sw.SewerageConnection) {
      _sewerageConnection = data;
    }
    _waterController.clearEditAppDataEmp();
    _addRoadCuttingWidget();
  }

  void _addRoadCuttingWidget() {
    setState(() {
      _roadCuttingWidgets.add(
        _RoadCuttingWidget(
          index: 0,
          widgetLength: _roadCuttingWidgets.length,
          onRoadTypeChanged: (value) => _onRoadTypeChanged(0, value),
          onAreaPlotChanged: (value) => _onAreaPlotChanged(0, value),
          onRemove: () =>
              _removeRoadCuttingWidget(_roadCuttingWidgets.length - 1),
        ),
      );
    });
  }

  void _removeRoadCuttingWidget(int index) {
    setState(() {
      if (_roadCuttingWidgets.length > 1) {
        _roadCuttingWidgets.removeAt(index);
      }
    });
  }

  void _onRoadTypeChanged(int index, String value) {
    setState(() {
      _roadCuttingWidgets[index].roadTypeBy = value;
    });
  }

  void _onAreaPlotChanged(int index, String value) {
    setState(() {
      _roadCuttingWidgets[index].areaPlot = value;
    });
  }

  _submitForm() {
    if (_formKey.currentState!.validate()) {
      _formKey.currentState!.save();

      final isPlumberDetailsComplete =
          _waterController.isPlumberDetailsCompleted();

      if (!isPlumberDetailsComplete) {
        return snackBar(
          'Incomplete',
          'Plumber details incomplete!',
          BaseConfig.redColor,
        );
      }
      saveToLocal();
    } else {
      dPrint('Form is not valid!');
    }
  }

  Future<void> saveToLocal() async {
    final waterSubSource =
        _waterController.waterSubSourceSplit.value.replaceAll('_', '.');

    dPrint('''
            Nature Connection: ${_waterController.natureConnection.value}\n
            Water Source: ${_waterController.waterSourceSplit.value}\n
            Water Sub Source: $waterSubSource\n
            Pipe size: ${_waterController.pipeSize.value}\n
            No of Taps: ${_waterController.noOfTaps.value}\n
            Plumber provided by: ${_waterController.plumberProvidedBy.value}\n
            Plumber license no: ${_waterController.plumberLicenseNo.value}\n
            Plumber name: ${_waterController.plumberName.value}\n
            Plumber mobile no: ${_waterController.plumberMobileNo.value}\n
    ''');

    if (isNotNullOrEmpty(_waterConnection)) {
      _waterConnection!.connectionType =
          _waterController.natureConnection.value;
      _waterConnection!.waterSource = waterSubSource;
      _waterConnection!.pipeSize = _waterController.pipeSize.value;
      _waterConnection!.noOfTaps = int.parse(_waterController.noOfTaps.value);
      _waterConnection!.additionalDetails!.detailsProvidedBy =
          _waterController.plumberProvidedBy.value;

      final plumberInfo = ws.PlumberInfo()
        ..licenseNo = _waterController.plumberLicenseNo.value
        ..name = _waterController.plumberName.value
        ..mobileNumber = _waterController.plumberMobileNo.value;

      _waterConnection!.plumberInfo = [plumberInfo];

      List<ws.RoadCuttingInfo> roadCuttingData = [];

      for (var widget in _roadCuttingWidgets) {
        final roadCuttingInfo = ws.RoadCuttingInfo()
          ..roadType = widget.roadTypeBy
          ..roadCuttingArea = int.parse(widget.areaPlot);

        roadCuttingData.add(roadCuttingInfo);

        dPrint('Road Type: ${widget.roadTypeBy}');
        dPrint('Area Plot: ${widget.areaPlot}');
      }

      _waterConnection!.roadCuttingInfo = roadCuttingData;

      final formDataEncode = jsonEncode(_waterConnection!.toJson());

      await HiveService.setData(
        HiveConstants.WS_SESSION_APPLICATION_DETAILS,
        formDataEncode,
      );

      print(formDataEncode);
      _waterController.updateEditApplicationFormData();
    }

    if (isNotNullOrEmpty(_sewerageConnection)) {
      //  _sewerageConnection!.connectionType = _waterController.natureConnection.value;
      _sewerageConnection!.additionalDetails!.detailsProvidedBy =
          _waterController.plumberProvidedBy.value;

      final plumberInfo = sw.PlumberInfo()
        ..licenseNo = _waterController.plumberLicenseNo.value
        ..name = _waterController.plumberName.value
        ..mobileNumber = _waterController.plumberMobileNo.value;

      _sewerageConnection!.plumberInfo = [plumberInfo];

      List<sw.RoadCuttingInfoElement> roadCuttingData = [];

      for (var widget in _roadCuttingWidgets) {
        final roadCuttingInfo = sw.RoadCuttingInfoElement()
          ..roadType = widget.roadTypeBy
          ..roadCuttingArea = int.parse(widget.areaPlot);

        roadCuttingData.add(roadCuttingInfo);

        dPrint('Road Type: ${widget.roadTypeBy}');
        dPrint('Area Plot: ${widget.areaPlot}');
      }

      _sewerageConnection!.roadCuttingInfo = roadCuttingData;

      final formDataEncode = jsonEncode(_sewerageConnection!.toJson());

      await HiveService.setData(
        HiveConstants.WS_SESSION_APPLICATION_DETAILS,
        formDataEncode,
      );

      print(formDataEncode);
      _waterController.updateEditApplicationFormData(module: Modules.SW);
    }

    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Navigator.of(context).pop();
        },
        title: getLocalizedString(
          i18.waterSewerage.EMP_EDIT_APPLICATION,
          module: Modules.WS,
        ),
      ),
      bottomNavigationBar: Container(
        height: 44.h,
        width: Get.width,
        margin: EdgeInsets.all(o == Orientation.portrait ? 16.w : 12.w),
        child: FilledButtonApp(
          text: getLocalizedString(i18.common.SUBMIT),
          onPressed: () {
            final isConnectionDetailsComplete =
                _waterController.isConnectionDetailsCompleted();

            _submitForm();

            dPrint('isConnectionDetailsComplete: $isConnectionDetailsComplete');
          },
        ),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: Padding(
          padding: EdgeInsets.all(16.w),
          child: SingleChildScrollView(
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  BuildCard(child: _connectionDetails()),
                  SizedBox(height: 10.h),
                  BuildCard(child: _plumberDetails()),
                  SizedBox(height: 10.h),
                  BuildCard(child: _roadCuttingDetails()),
                  SizedBox(height: 10.h),
                  // BuildCard(child: _evidenceDetails()),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _connectionDetails() {
    final waterSources = _waterController
        .empMdmsResModel.mdmsResEmp?.wsServicesMasters?.waterSource
        ?.map(
          (e) => (e.active ?? false)
              ? '${i18.waterSewerage.EMP_WATERSOURCE_TYPE}${e.code!.split('.').first}'
              : '',
        )
        .toSet();

    List<String> waterSubSources = [];

    final pipeSize = _waterController
        .empMdmsResModel.mdmsResEmp?.wsServicesCalculation?.pipeSize
        ?.map(
          (e) => (e.isActive ?? false) ? e.size!.toString() : '',
        )
        .toList();

    return isNotNullOrEmpty(
      _waterController.empMdmsResModel.mdmsResEmp?.wsServicesMasters,
    )
        ? Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              BigTextNotoSans(
                text: getLocalizedString(
                  i18.waterSewerage.CONNECTION_DETAIL,
                  module: Modules.WS,
                ),
                fontWeight: FontWeight.w600,
                size: 16.sp,
              ),
              SizedBox(
                height: 10.h,
              ),
              if (isNotNullOrEmpty(_waterConnection)) ...[
                if (isNotNullOrEmpty(
                  _waterController.empMdmsResModel.mdmsResEmp?.wsServicesMasters
                      ?.connectionType,
                ))
                  Obx(
                    () => ColumnHeaderDropdownSearch(
                      label: getLocalizedString(
                        i18.waterSewerage.EMP_NATURE_CONNECTION,
                        module: Modules.WS,
                      ),
                      options: _waterController.empMdmsResModel.mdmsResEmp!
                          .wsServicesMasters!.connectionType!
                          .map(
                            (e) => (e.active ?? false)
                                ? getLocalizedString(
                                    '${i18.waterSewerage.EMP_CONNECTION_TYPE}${e.code?.replaceAll(' ', '_')}'
                                        .toUpperCase(),
                                    module: Modules.WS,
                                  )
                                : '',
                          )
                          .toList(),
                      selectedValue: _waterController.natureConnection.value,
                      onChanged: (value) {
                        dPrint('value: $value');

                        _waterController.natureConnection.value = value ?? '';
                      },
                      textSize: 14.sp,
                      isRequired: true,
                      enableLocal: true,
                      validator: (val) {
                        if (!isNotNullOrEmpty(val)) {
                          return 'Required Field';
                        }
                        return null;
                      },
                    ),
                  ),
                SizedBox(
                  height: 10.h,
                ),
                Obx(
                  () => ColumnHeaderDropdownSearch(
                    module: Modules.WS,
                    enableLocal: true,
                    label: getLocalizedString(
                      i18.waterSewerage.DETAIL_WATER_SOURCE,
                      module: Modules.WS,
                    ),
                    options: waterSources!
                        .map(
                          (e) => e.toUpperCase(),
                        )
                        .toList(),
                    selectedValue: _waterController.waterSource.value,
                    onChanged: (value) {
                      _waterController.waterSource.value = value ?? '';
                      _waterController.waterSourceSplit.value =
                          value?.split('_').last ?? '';
                      dPrint(
                        'value: ${_waterController.waterSourceSplit.value}',
                      );

                      waterSubSources = _waterController
                          .getSubWaterSourceTypes()
                          .map(
                            (e) => '${i18.waterSewerage.EMP_WATERSOURCE_TYPE}$e'
                                .toUpperCase(),
                          )
                          .toList();
                    },
                    textSize: 14.sp,
                    isRequired: true,
                    validator: (val) {
                      if (!isNotNullOrEmpty(val)) {
                        return 'Required Field';
                      }
                      return null;
                    },
                  ),
                ),
                SizedBox(
                  height: 10.h,
                ),
                Obx(
                  () =>
                      isNotNullOrEmpty(_waterController.waterSourceSplit.value)
                          ? ColumnHeaderDropdownSearch(
                              label: getLocalizedString(
                                i18.waterSewerage.EMP_WATER_SUB_SOURCE,
                                module: Modules.WS,
                              ),
                              options: waterSubSources.map((e) => e).toList(),
                              selectedValue:
                                  _waterController.waterSubSource.value,
                              onChanged: (value) {
                                _waterController.waterSubSource.value =
                                    value ?? '';
                                final lastSubPart = getLastTwoPart(value ?? '');
                                _waterController.waterSubSourceSplit.value =
                                    lastSubPart;
                                dPrint('value: $lastSubPart');
                              },
                              textSize: 14.sp,
                              isRequired: true,
                              enableLocal: true,
                              validator: (val) {
                                if (!isNotNullOrEmpty(val)) {
                                  return 'Required Field';
                                }
                                return null;
                              },
                            )
                          : const SizedBox.shrink(),
                ),
                SizedBox(
                  height: 10.h,
                ),
                if (isNotNullOrEmpty(pipeSize))
                  Obx(
                    () => ColumnHeaderDropdownSearch(
                      label: getLocalizedString(
                        i18.waterSewerage.EMP_PIPE_SIZE,
                        module: Modules.ABG,
                      ),
                      options: pipeSize!
                          .map(
                            (e) => e,
                          )
                          .toList(),
                      selectedValue: _waterController.pipeSize.value.toString(),
                      onChanged: (value) {
                        dPrint('value: $value');

                        _waterController.pipeSize.value =
                            double.parse(value ?? '0.0');
                      },
                      textSize: 14.sp,
                      isRequired: true,
                      module: Modules.WS,
                      enableLocal: true,
                      validator: (val) {
                        if (!isNotNullOrEmpty(val) || val == '0.0') {
                          return 'Required Field';
                        }
                        return null;
                      },
                    ),
                  ),
                SizedBox(
                  height: 10.h,
                ),
                ColumnHeaderTextField(
                  label: getLocalizedString(
                    i18.waterSewerage.SERV_DETAIL_NO_OF_TAPS,
                    module: Modules.WS,
                  ),
                  hintText: '',
                  onChange: (p0) => _waterController.noOfTaps.value = p0,
                  onSave: (p0) => _waterController.noOfTaps.value = p0 ?? '',
                  keyboardType: AppPlatforms.platformKeyboardType(),
                  textInputAction: TextInputAction.done,
                  inputFormatters: [
                    FilteringTextInputFormatter.digitsOnly,
                  ],
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
              ] else if (isNotNullOrEmpty(_sewerageConnection)) ...[
                ColumnHeaderTextField(
                  label: getLocalizedString(
                    i18.waterSewerage.WS_NUMBER_WATER_CLOSETS_LABEL,
                    module: Modules.WS,
                  ),
                  hintText: '',
                  onChange: (p0) =>
                      _waterController.noOfWaterClosets.value = p0,
                  onSave: (p0) =>
                      _waterController.noOfWaterClosets.value = p0 ?? '',
                  keyboardType: AppPlatforms.platformKeyboardType(),
                  inputFormatters: [
                    FilteringTextInputFormatter.digitsOnly,
                  ],
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
                    i18.waterSewerage.SERV_DETAIL_NO_OF_TOILETS,
                    module: Modules.WS,
                  ),
                  hintText: '',
                  onChange: (p0) =>
                      _waterController.numberOfToiletSeats.value = p0,
                  onSave: (p0) =>
                      _waterController.numberOfToiletSeats.value = p0 ?? '',
                  keyboardType: AppPlatforms.platformKeyboardType(),
                  inputFormatters: [
                    FilteringTextInputFormatter.digitsOnly,
                  ],
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
              ],
            ],
          )
        : const SizedBox.shrink();
  }

  Widget _plumberDetails() {
    final plumberProvideByList = ['SELF', 'ULB'];
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        BigTextNotoSans(
          text: getLocalizedString(
            i18.waterSewerage.PLUMBER_DETAILS,
            module: Modules.WS,
          ),
          fontWeight: FontWeight.w600,
          size: 16.sp,
        ),
        SizedBox(
          height: 10.h,
        ),
        Obx(
          () => ColumnHeaderRadioButton(
            label: getLocalizedString(
              i18.waterSewerage.PLUMBER_PROVIDED_BY,
              module: Modules.WS,
            ),
            options: plumberProvideByList
                .map(
                  (e) => getLocalizedString(
                    '${i18.waterSewerage.EMP_PLUMBER_PROVIDED_BY}$e'
                        .toUpperCase(),
                    module: Modules.WS,
                  ),
                )
                .toList(),
            selectedValue: _waterController.plumberProvidedBy.value,
            onChanged: (value) {
              dPrint('value: $value');
              _waterController.plumberProvidedBy.value =
                  value?.split('_').last ?? '';
            },
            isRequired: true,
          ),
        ),
        SizedBox(
          height: 10.h,
        ),
        ColumnHeaderTextField(
          label: getLocalizedString(
            i18.waterSewerage.EMP_PLUMBER_LICENSE_NO,
            module: Modules.WS,
          ),
          hintText: '',
          onChange: (p0) => _waterController.plumberLicenseNo.value = p0,
          onSave: (p0) => _waterController.plumberLicenseNo.value = p0 ?? '',
          isRequired: true,
          validator: (val) {
            if (!isNotNullOrEmpty(val)) {
              return 'Required Field';
            } else if (val!.trim().contains('.')) {
              return 'Invalid Input';
            }
            return null;
          },
          keyboardType: AppPlatforms.platformKeyboardType(),
          inputFormatters: [
            FilteringTextInputFormatter.digitsOnly,
          ],
          textInputAction: TextInputAction.done,
        ),
        SizedBox(
          height: 10.h,
        ),
        ColumnHeaderTextField(
          label: getLocalizedString(
            i18.waterSewerage.EMP_PLUMBER_NAME,
            module: Modules.WS,
          ),
          hintText: '',
          onChange: (p0) => _waterController.plumberName.value = p0,
          onSave: (p0) => _waterController.plumberName.value = p0 ?? '',
          isRequired: true,
          validator: (val) {
            if (!isNotNullOrEmpty(val)) {
              return 'Required Field';
            }
            return null;
          },
          keyboardType: TextInputType.text,
        ),
        SizedBox(
          height: 10.h,
        ),
        ColumnHeaderTextField(
          label: getLocalizedString(
            i18.waterSewerage.EMP_PLUMBER_MOBILE_NO,
            module: Modules.WS,
          ),
          hintText: '',
          prefixIcon: Padding(
            padding: const EdgeInsets.fromLTRB(20, 15, 20, 15),
            child: MediumTextNotoSans(
              text: '+91',
              fontWeight: FontWeight.w600,
              size: 14.sp,
            ),
          ),
          onChange: (p0) => _waterController.plumberMobileNo.value = p0,
          onSave: (p0) => _waterController.plumberMobileNo.value = p0 ?? '',
          isRequired: true,
          keyboardType: AppPlatforms.platformKeyboardType(),
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
      ],
    );
  }

  Widget _roadCuttingDetails() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        BigTextNotoSans(
          text: getLocalizedString(
            i18.waterSewerage.EMP_ROAD_CUTTING_DETAILS,
            module: Modules.WS,
          ),
          fontWeight: FontWeight.w600,
          size: 16.sp,
        ),
        for (int i = 0; i < _roadCuttingWidgets.length; i++)
          _RoadCuttingWidget(
            index: i,
            roadTypeBy: _roadCuttingWidgets[i].roadTypeBy,
            areaPlot: _roadCuttingWidgets[i].areaPlot,
            onRemove: () => _removeRoadCuttingWidget(i),
            widgetLength: _roadCuttingWidgets.length,
            onRoadTypeChanged: (value) => _onRoadTypeChanged(i, value),
            onAreaPlotChanged: (value) => _onAreaPlotChanged(i, value),
          ),
        iconTextButton(
          iconAlignment: IconAlignment.end,
          label: getLocalizedString(
            i18.waterSewerage.EMP_ADD_ROAD_TYPE,
            module: Modules.WS,
          ),
          labelColor: BaseConfig.appThemeColor1,
          icon: Icon(
            Icons.add,
            color: BaseConfig.appThemeColor1,
            size: 20.sp,
          ),
          onPressed: _addRoadCuttingWidget,
        ),
      ],
    );
  }
}

// ignore: must_be_immutable
class _RoadCuttingWidget extends StatefulWidget {
  String roadTypeBy;
  String areaPlot;
  final VoidCallback onRemove;
  final int index, widgetLength;
  final Function(String) onRoadTypeChanged;
  final Function(String) onAreaPlotChanged;

  _RoadCuttingWidget({
    this.roadTypeBy = '',
    this.areaPlot = '',
    required this.onRemove,
    required this.index,
    required this.widgetLength,
    required this.onRoadTypeChanged,
    required this.onAreaPlotChanged,
  });

  @override
  State<_RoadCuttingWidget> createState() => __RoadCuttingWidgetState();
}

class __RoadCuttingWidgetState extends State<_RoadCuttingWidget> {
  final _waterController = Get.find<WaterController>();

  late String roadTypeBy;
  late String areaPlot;

  @override
  void initState() {
    super.initState();
    roadTypeBy = widget.roadTypeBy;
    areaPlot = widget.areaPlot;
  }

  setRoadTypeBy(String? value) {
    roadTypeBy = value ?? '';
    var roadTypeCode = roadTypeBy.split('_').last;
    dPrint('Updated roadTypeBy: $roadTypeBy - Code: $roadTypeCode');
    widget.onRoadTypeChanged(roadTypeCode);
    setState(() {});
  }

  setAreaPlot(String? value) {
    areaPlot = value ?? '';
    dPrint('Updated areaPlot: $areaPlot');
    widget.onAreaPlotChanged(areaPlot);
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    final rodTypes = _waterController
        .empMdmsResModel.mdmsResEmp?.wsServicesCalculation?.roadType
        ?.map((e) => '${i18.waterSewerage.WS_ROADTYPE}${e.code}')
        .toList();

    return isNotNullOrEmpty(rodTypes)
        ? Container(
            margin: EdgeInsets.only(top: 4.h, bottom: 8.h),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(10.r),
              border: Border.all(
                color: BaseConfig.borderColor,
                width: 1,
              ),
            ),
            child: Padding(
              padding: EdgeInsets.all(4.w),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      SmallTextNotoSans(
                        text: widget.index >= 1
                            ? '${getLocalizedString(
                                i18.waterSewerage.EMP_ROAD_CUTTING_DETAILS,
                                module: Modules.WS,
                              )} - ${widget.index + 1}'
                            : widget.widgetLength != 1
                                ? '${getLocalizedString(
                                    i18.waterSewerage.EMP_ROAD_CUTTING_DETAILS,
                                    module: Modules.WS,
                                  )} - ${widget.index + 1}'
                                : getLocalizedString(
                                    i18.waterSewerage.EMP_ROAD_CUTTING_DETAILS,
                                    module: Modules.WS,
                                  ),
                        size: 14.sp,
                      ),
                      const Spacer(),
                      if (widget.index >= 1 || widget.widgetLength != 1)
                        IconButton(
                          onPressed: widget.onRemove,
                          icon: const Icon(
                            Icons.delete,
                            color: BaseConfig.redColor,
                          ),
                        ),
                    ],
                  ),
                  SizedBox(
                    height: 10.h,
                  ),
                  ColumnHeaderDropdownSearch(
                    module: Modules.WS,
                    enableLocal: true,
                    label: getLocalizedString(
                      i18.waterSewerage.EMP_ROAD_TYPE,
                      module: Modules.WS,
                    ),
                    options: rodTypes!.toList(),
                    selectedValue: roadTypeBy,
                    onChanged: setRoadTypeBy,
                    isRequired: true,
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
                      i18.waterSewerage.EMP_AREA_SQ_FT,
                      module: Modules.WS,
                    ),
                    hintText: '',
                    onChange: setAreaPlot,
                    onSave: setAreaPlot,
                    isRequired: true,
                    keyboardType: AppPlatforms.platformKeyboardType(),
                    inputFormatters: [
                      FilteringTextInputFormatter.digitsOnly,
                    ],
                    textInputAction: TextInputAction.done,
                    validator: (String? value) {
                      if (!isNotNullOrEmpty(value)) {
                        return 'Required Field';
                      } else if (value!.trim().contains('.')) {
                        return 'Invalid Input';
                      }
                      return null;
                    },
                  ),
                  SizedBox(
                    height: 10.h,
                  ),
                ],
              ),
            ),
          )
        : const SizedBox.shrink();
  }
}
