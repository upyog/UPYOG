import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:path_provider/path_provider.dart';
import 'package:syncfusion_flutter_pdfviewer/pdfviewer.dart';

class DownloadController extends GetxController {
  File? file;
  final dio = Dio();
  RxString progressStr = '0'.obs;
  RxDouble progress = 0.0.obs;

  Future<void> starFileDownload({
    required String url,
    required String title,
  }) async {
    var dir = Platform.isAndroid
        ? '/storage/emulated/0/Download'
        : (await getApplicationSupportDirectory()).path;

    var urlSplit = url.split('/').last;
    var name = urlSplit.split('.').first;
    var last = urlSplit.split('.').last;
    var ext = last.split('?').first;
    dPrint('File Type: $ext');

    final fileName = '$dir/upyog-$name.$ext';

    openDialogue(title);

    _fileDownload(dio, url, fileName, ext.capitalizeFirst!);
  }

  Future<void> _fileDownload(
    Dio dio,
    String url,
    String savePath,
    String ext,
  ) async {
    try {
      progress.value = 0;
      progressStr.value = '0';
      var response = await dio.get(
        url,
        onReceiveProgress: showDownloadProgress,
        options: Options(
          responseType: ResponseType.bytes,
          followRedirects: false,
          validateStatus: (status) {
            return status! < 500;
          },
        ),
      );

      file = await writeToFile(response.data, savePath);
      dPrint('Download file: ${file!.path}');
      progressStr.value = (progress * 100).toInt().toString();
      if (progressStr.value == '100') {
        snackBar(
          'Success',
          Platform.isAndroid
              ? '$ext download successfully to download folder'
              : '$ext download success',
          Colors.green,
          seconds: 5,
        );
      }
    } catch (e, s) {
      dPrint('File download error - $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
  }

  void showDownloadProgress(received, total) {
    progress.value = received / total;
    var pr = (received / total * 100).toStringAsFixed(0);
    if (total != -1) {
      progressStr.value = pr;
      dPrint(pr + "%");
    }
  }

  Future<File> writeToFile(data, String path) {
    return File(path).writeAsBytes(data);
  }

  openDialogue(String title) {
    Get.dialog(
      PopScope(
        // onPopInvokedWithResult: (value, d) => Future.value(value),
        canPop: true,
        child: AlertDialog(
          backgroundColor: BaseConfig.mainBackgroundColor,
          surfaceTintColor: BaseConfig.mainBackgroundColor,
          title: Center(child: BigTextNotoSans(text: title)),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Obx(
                () => progressStr.value == '100'
                    ? const SizedBox.shrink()
                    : const CircularProgressIndicator.adaptive(),
              ),
              const SizedBox(
                height: 20,
              ),
              Obx(
                () => Text(
                  progressStr.value == '100'
                      ? "Downloaded: ${progressStr.value}%"
                      : "Downloading: ${progressStr.value}%",
                  style: const TextStyle(
                    color: Colors.black87,
                    fontSize: 17,
                  ),
                ),
              ),
              const SizedBox(height: 30),
              Obx(
                () => progressStr.value == '100'
                    ? Row(
                        children: [
                          Expanded(
                            child: FilledButtonApp(
                              text: 'Close',
                              fontSize: 12.sp,
                              onPressed: () => Get.back(),
                              backgroundColor: Colors.red.shade700,
                              radius: 30.r,
                              horizontalPadding: 10.w,
                              verticalPadding: 5.h,
                            ),
                          ),
                          const SizedBox(width: 10),
                          Expanded(
                            child: FilledButtonApp(
                              text: 'Open',
                              radius: 30.r,
                              fontSize: 12.sp,
                              horizontalPadding: 10.w,
                              verticalPadding: 5.h,
                              onPressed: () {
                                if (file != null) {
                                  Get.dialog(
                                    AlertDialog(
                                      backgroundColor:
                                          BaseConfig.mainBackgroundColor,
                                      surfaceTintColor:
                                          BaseConfig.mainBackgroundColor,
                                      scrollable: false,
                                      title: Row(
                                        children: [
                                          Expanded(
                                            child: MediumTextNotoSans(
                                              text: title,
                                              size: 16.sp,
                                              fontWeight: FontWeight.w600,
                                            ),
                                          ),
                                          IconButton(
                                            icon: const Icon(
                                              Icons.close,
                                              color: BaseConfig.appThemeColor1,
                                            ),
                                            onPressed: () => Get.back(),
                                          ),
                                        ],
                                      ),
                                      content: SizedBox(
                                        width: Get.width,
                                        height: Get.height * 0.6,
                                        child: SfPdfViewer.file(
                                          File(file!.path),
                                        ),
                                      ),
                                    ),
                                  );
                                }
                              },
                            ),
                          ),
                        ],
                      )
                    : FilledButtonApp(
                        text: 'Close',
                        onPressed: () {
                          dio.close();
                          Get.back();
                        },
                        radius: 30,
                        backgroundColor: Colors.red.shade700,
                      ),
              ),
            ],
          ),
        ),
      ),
      barrierDismissible: false,
    );
  }
}
