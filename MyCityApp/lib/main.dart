import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/bpa_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/download_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/inbox_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/controller/notification_controller.dart';
import 'package:mobile_app/controller/obps_dynamic_form_controller.dart';
import 'package:mobile_app/controller/payment_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/controller/property_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/controller/water_controller.dart';
import 'package:mobile_app/env/app_config.dart';
import 'package:mobile_app/env/ntt_payment_config.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/screens/splash/splash_screen.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/services/notification_service.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/firebase_configurations/firebase_options.dart';
import 'package:mobile_app/utils/utils.dart';

// Listen background changes notification
Future _firebaseBackgroundMessage(RemoteMessage message) async {
  if (message.notification != null) {
    NotificationModel notification = NotificationModel(
      title: message.notification!.title!,
      text: message.notification!.body!,
    );

    notificationBadgeCount(notification);

    dPrint('''
      Notification received in background...
      title: ${message.notification!.title}
      body: ${message.notification!.body}
      ''');
  }
}

notificationBadgeCount(NotificationModel notification) {
  final notificationController = Get.find<NotificationController>();
  notificationController.addNotification(notification);
}

Future main() async {
  await Future.delayed(const Duration(seconds: 1));

  HttpOverrides.global = MyHttpOverrides();

  // Global environment setup
  setEnvironment(Environment.prod);

  //Ntt Payment Environment Setup
  // if (kReleaseMode) {
  //   setNttEnvironment(NttEnvironment.release);
  // } else {
  setNttEnvironment(NttEnvironment.development);
  // }

  runZonedGuarded(
    () async {
      FlutterError.onError = (FlutterErrorDetails details) {
        FlutterError.dumpErrorToConsole(details);
        ErrorHandler.logError(details.exception.toString(), details.stack);
      };

      WidgetsFlutterBinding.ensureInitialized();

      await Firebase.initializeApp(
        name: 'upyog',
        options: DefaultFirebaseOptions.currentPlatform,
      );

      //Initialize firebase messaging
      await NotificationService.init();

      //Initialize local notification
      await NotificationService.localNotificationInit();

      //Init Hive
      await initHive();

      //Controller
      Get.put(CommonController());
      Get.put(LanguageController());
      Get.put(AuthController());
      Get.put(CityController());
      Get.put(NotificationController());
      Get.put(EditProfileController());
      Get.put(FileController());
      Get.put(TradeLicenseController());
      Get.put(PropertyController());
      Get.put(PaymentController());
      Get.put(PropertiesTaxController());
      Get.put(TimelineController());
      Get.put(WaterController());
      Get.put(BpaController());
      Get.put(DownloadController());
      Get.put(InboxController());
      Get.put(ObpsDynamicFormController());

      //Get all localization from api or local
      final languageCtrl = Get.find<LanguageController>();
      await languageCtrl.getLocalizationData();

      //Convert all labels to language based map for translations
      final commonCtrl = Get.find<CommonController>();
      await commonCtrl.fetchLabels();

      //Handle terminate state
      await FirebaseMessaging.instance.getInitialMessage().then((message) {
        if (message?.notification != null) {
          dPrint('Launched from terminated state');
          Future.delayed(const Duration(seconds: 1), () {
            // Get.offAllNamed(AppRoutes.NOTIFICATION_DETAILS);
          });
        }
      });

      //On background notification tapped
      FirebaseMessaging.onMessageOpenedApp
          .listen((RemoteMessage message) async {
        dPrint('Notification tapped!: ${message.notification.toString()}');
        final authController = Get.find<AuthController>();
        if (message.notification != null) {
          dPrint('Background notification tapped!');
          if (authController.isValidUser) {
            Get.offAllNamed(AppRoutes.BOTTOM_NAV);
          } else {
            // Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
            Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
          }
        }
      });

      //Listen background notification
      FirebaseMessaging.onBackgroundMessage(_firebaseBackgroundMessage);

      //Handle foreground notification
      FirebaseMessaging.onMessage.listen((RemoteMessage message) {
        String payloadData = jsonEncode(message.data);
        dPrint('Foreground message received: $payloadData');
        if (message.notification != null) {
          NotificationModel notification = NotificationModel(
            title: message.notification!.title!,
            text: message.notification!.body!,
          );
          notificationBadgeCount(notification);

          NotificationService.showNotification(
            title: message.notification!.title!,
            body: message.notification!.body!,
            payload: payloadData,
          );
        }
      });

      runApp(
        const MyApp(),
      );
    },
    (error, stack) {
      ErrorHandler.logError(error.toString(), stack);
    },
  );
}

//Hive Initialization
Future<void> initHive() async {
  await Hive.initFlutter();
  await Hive.openBox(HiveConstants.appBox);
  await Hive.openBox<List<String>>('searchHistoryBox');
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light,
        statusBarBrightness: Brightness.light,
        systemNavigationBarColor: Colors.white,
        systemNavigationBarIconBrightness: Brightness.dark,
      ),
    );

    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
      // DeviceOrientation.landscapeLeft,
      // DeviceOrientation.landscapeRight,
    ]);

    return ScreenUtilInit(
      designSize: const Size(360, 800), //690
      minTextAdapt: true,
      splitScreenMode: true,
      builder: (_, child) {
        return GetBuilder<LanguageController>(
          builder: (langController) {
            return OrientationBuilder(
              builder: (context, o) {
                return GetMaterialApp(
                  title: BaseConfig.APP_NAME,
                  theme: ThemeData(
                    textTheme: GoogleFonts.notoSansTextTheme(),
                    colorScheme:
                        ColorScheme.fromSeed(seedColor: Colors.deepPurple),
                    useMaterial3: true,
                    scaffoldBackgroundColor: BaseConfig.mainBackgroundColor,
                    appBarTheme: AppBarTheme(
                      elevation: 4,
                      backgroundColor: BaseConfig.mainBackgroundColor,
                      surfaceTintColor: BaseConfig.mainBackgroundColor,
                      shadowColor: Colors.black.withValues(alpha: 0.5),
                      titleTextStyle: GoogleFonts.notoSans.call().copyWith(
                            fontSize: o == Orientation.portrait ? 16.sp : 9.sp,
                            color: BaseConfig.textColor2,
                            fontWeight: FontWeight.w800,
                          ),
                    ),
                    dividerTheme: const DividerThemeData(
                      color: BaseConfig.borderColor,
                      thickness: 1,
                    ),
                    cardTheme: const CardTheme(
                      color: BaseConfig.mainBackgroundColor,
                      elevation: 4,
                    ),
                    textSelectionTheme: const TextSelectionThemeData(
                      selectionHandleColor: BaseConfig.appThemeColor1,
                    ),
                  ),
                  debugShowCheckedModeBanner: false,
                  supportedLocales: const [
                    Locale('en', 'IN'),
                    Locale('hi', 'IN'),
                    Locale.fromSubtags(languageCode: 'pn'),
                  ],
                  locale: langController.locale,
                  fallbackLocale: const Locale('en', 'IN'),
                  localizationsDelegates: const [
                    GlobalMaterialLocalizations.delegate,
                    GlobalWidgetsLocalizations.delegate,
                  ],
                  localeResolutionCallback: (locale, supportedLocales) {
                    for (var supportedLocaleLanguage in supportedLocales) {
                      if (supportedLocaleLanguage.languageCode ==
                              locale?.languageCode &&
                          supportedLocaleLanguage.countryCode ==
                              locale?.countryCode) {
                        return supportedLocaleLanguage;
                      }
                    }
                    return supportedLocales.first;
                  },
                  getPages: AppRoutes.getPages,
                  defaultTransition: Transition.fade,
                  home: child,
                );
              },
            );
          },
        );
      },
      child: const SplashScreen(),
    );
  }
}

class MyHttpOverrides extends HttpOverrides {
  @override
  HttpClient createHttpClient(SecurityContext? context) {
    return super.createHttpClient(context)
      ..badCertificateCallback =
          (X509Certificate cert, String host, int port) => true;
  }
}
