import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class PropertyTaxScreen extends StatefulWidget {
  const PropertyTaxScreen({super.key});

  @override
  State<PropertyTaxScreen> createState() => _PropertyTaxScreenState();
}

class _PropertyTaxScreenState extends State<PropertyTaxScreen> {
  final _authController = Get.find<AuthController>();
  final _propertiesTaxController = Get.find<PropertiesTaxController>();

  @override
  void initState() {
    super.initState();
    _propertiesTaxController.setDefaultLimit();
    getMyProperties();
  }

  void getMyProperties() {
    if (!_authController.isValidUser) return;
    _propertiesTaxController.getMyPropertiesStream(
      token: _authController.token!.accessToken!,
    );
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, orientation) {
        return Scaffold(
          appBar: _buildAppBar(orientation),
          body: Padding(
            padding: EdgeInsets.all(16.w),
            child: PropertyListStreamBuilder(
              stream: _propertiesTaxController.streamCtrl.stream,
              authController: _authController,
              propertiesTaxController: _propertiesTaxController,
            ),
          ),
        );
      },
    );
  }

  HeaderTop _buildAppBar(Orientation orientation) {
    return HeaderTop(
      titleWidget: Wrap(
        children: [
          Text(getLocalizedString(i18.common.PROPERTY_TAX)),
          Obx(() => Text(' (${_propertiesTaxController.length.value})')),
        ],
      ),
      onPressed: () => Navigator.of(context).pop(),
      orientation: orientation,
    );
  }
}

class PropertyListStreamBuilder extends StatelessWidget {
  final Stream stream;
  final AuthController authController;
  final PropertiesTaxController propertiesTaxController;

  const PropertyListStreamBuilder({
    super.key,
    required this.stream,
    required this.authController,
    required this.propertiesTaxController,
  });

  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
      stream: stream,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return showCircularIndicator();
        } else if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        } else if (snapshot.hasData) {
          return _buildPropertyList(snapshot.data as PtMyProperties);
        } else {
          return const NoApplicationFoundWidget();
        }
      },
    );
  }

  Widget _buildPropertyList(PtMyProperties myProperties) {
    final propertyList = myProperties.properties
        ?.where(
          (e) =>
              e.status == ApplicationStatus.ACTIVE.value ||
              e.status == ApplicationStatus.INACTIVE.value,
        )
        .toList();

    if (!isNotNullOrEmpty(propertyList)) {
      return const NoApplicationFoundWidget();
    }

    return ListView.builder(
      itemCount: propertyList!.length >= 10
          ? propertyList.length + 1
          : propertyList.length,
      itemBuilder: (context, index) {
        if (index == propertyList.length && propertyList.length >= 10) {
          return Obx(() {
            return propertiesTaxController.isLoading.value
                ? showCircularIndicator()
                : IconButton(
                    onPressed: () => propertiesTaxController.loadMore(
                      token: authController.token!.accessToken!,
                    ),
                    icon: const Icon(
                      Icons.expand_circle_down_outlined,
                      size: 30,
                      color: BaseConfig.appThemeColor1,
                    ),
                  );
          });
        } else {
          var property = propertyList[index];
          return PropertyCard(property: property).paddingOnly(bottom: 12);
        }
      },
    );
  }
}

class PropertyCard extends StatelessWidget {
  final Property property;

  const PropertyCard({super.key, required this.property});

  @override
  Widget build(BuildContext context) {
    return ComplainCard(
      title: property.owners?.first.name?.capitalize ?? "N/A",
      id: '${getLocalizedString(i18.propertyTax.PROPERTY_ID)}: ${property.propertyId}',
      date: 'Date: ${property.auditDetails!.createdTime.toCustomDateFormat()}',
      onTap: () {
        Get.toNamed(AppRoutes.MY_PROPERTIES_DETAILS, arguments: property);
      },
      status: getLocalizedString(
        '${i18.propertyTax.PT_COMMON}${property.status}'.toUpperCase(),
        module: Modules.PT,
      ),
      statusColor: getStatusColor('${property.status}'),
      statusBackColor: getStatusBackColor('${property.status}'),
    );
  }
}
