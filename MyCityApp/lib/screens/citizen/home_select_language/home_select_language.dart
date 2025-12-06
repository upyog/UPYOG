import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class HomeSelectLanguage extends StatefulWidget {
  const HomeSelectLanguage({super.key});

  @override
  State<HomeSelectLanguage> createState() => _HomeSelectLanguageState();
}

class _HomeSelectLanguageState extends State<HomeSelectLanguage> {
  final _languageController = Get.find<LanguageController>();

  @override
  void initState() {
    super.initState();
    _languageController.getLocalizationData();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Get.offAndToNamed(AppRoutes.BOTTOM_NAV);
        },
        title: "Change Language",
      ),
      body: OrientationBuilder(
        builder: (context, orientation) {
          return Container(
            height: Get.height,
            width: Get.width,
            padding: const EdgeInsets.all(20),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  width: Get.width,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(20.0),
                    child: StreamBuilder(
                      stream: _languageController.streamController.stream,
                      builder: (context, AsyncSnapshot snapshot) {
                        if (snapshot.hasData && snapshot.data != 'error') {
                          List<StateInfo> stateInfo = snapshot.data;
                          return _buildView(
                              stateInfo.first, _languageController,);
                        } else if (snapshot.hasError) {
                          return networkErrorPage(
                            context,
                            () => _languageController.getLocalizationData(),
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
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildView(StateInfo stateInfo, LanguageController langCtrl) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        for (int i = 0; i < stateInfo.languages!.length; i++)
          Obx(
            () => SizedBox(
              height: 50,
              child: RadioListTile<String>(
                title: Text(
                  '${stateInfo.languages![i].label}',
                  style:
                      TextStyle(fontSize: 14.sp, fontWeight: FontWeight.w400),
                ),
                contentPadding: EdgeInsetsDirectional.zero,
                value: stateInfo.languages![i].label.toString(),
                groupValue: langCtrl.selectedAppLanguage.value,
                activeColor: BaseConfig.appThemeColor1,
                onChanged: (value) async {
                  if (value != null) {
                    langCtrl.selectedAppLanguage.value = value;
                    await HiveService.setData(
                      Constants.LANG_SELECTION_INDEX,
                      i,
                    );
                    await HiveService.setData(
                      Constants.TENANT_ID,
                      stateInfo.code,
                    );
                    langCtrl.onSelectionOfLanguage(
                      stateInfo.languages![i],
                      stateInfo.languages!,
                      i,
                    );
                  }
                },
              ),
            ),
          ),
        const SizedBox(height: 20),
      ],
    );
  }
}
