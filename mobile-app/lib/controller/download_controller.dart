import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:file_saver/file_saver.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:http/http.dart' as http;
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/env/app_config.dart';
import 'package:mobile_app/model/request/request_info_model.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:path_provider/path_provider.dart';
import 'package:syncfusion_flutter_pdfviewer/pdfviewer.dart';

class DownloadController extends GetxController {
  File? file;
  RxString progressStr = '0'.obs;
  RxDouble progress = 0.0.obs;

  Future<void> starFileDownload({
    required String url,
    required String title,
  }) async {
    try {
      String dir;

      if (Platform.isAndroid) {
        Directory primaryDir = Directory('/storage/emulated/0/Download');
        if (await primaryDir.exists()) {
          dir = primaryDir.path;
        } else {
          Directory alternativeDir = Directory('/storage/emulated/0/Downloads');
          if (await alternativeDir.exists()) {
            dir = alternativeDir.path;
          } else {
            await primaryDir.create(recursive: true);
            dir = primaryDir.path;
            dPrint('Created directory: $dir');
          }
        }
      } else {
        dir = (await getApplicationDocumentsDirectory()).path;
      }

      var urlSplit = url.split('/').last;
      var name = urlSplit.split('.').first;
      var last = urlSplit.split('.').last;
      var ext = last.split('?').first;
      dPrint('File Type: $ext');

      final fileName = '$dir/upyog-$name.$ext';
      dPrint('Saving file to: $fileName');

      openDialogue(title);

      final dio = Dio();
      await _fileDownload(dio, url, fileName, ext.capitalizeFirst!);
    } catch (e, s) {
      dPrint('starFileDownload setup error - $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
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

      file = await writeToFile(Uint8List.fromList(response.data), savePath);
      dPrint('Download file: ${file!.path}');
      progressStr.value = (progress * 100).toInt().toString();
      if (progressStr.value == '100') {
        dio.close();
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

  Future<File> writeToFile(Uint8List data, String path) async {
    final file = File(path);
    await file.writeAsBytes(data, flush: true);
    return file;
  }

  /// EMP - UC Challan PDF Download
  Future<void> directFileDownloadHttp({
    required String title,
    required String urlString,
    required String fileName,
    required String authToken,
  }) async {
    try {
      progress.value = 0;
      progressStr.value = '0';

      final local = await getLocal();
      final body = {
        "RequestInfo": RequestInfo(local: local, authToken: authToken).toJson(),
      };

      openDialogue(title);
      final url = apiBaseUrl + urlString;

      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(body),
      );

      if (response.statusCode == 200) {
        final Uint8List bytes = response.bodyBytes;

        await FileSaver.instance.saveFile(
          name: fileName.split('.').first,
          bytes: bytes,
          ext: fileName.split('.').last,
          mimeType: MimeType.pdf,
        );

        progressStr.value = '100';
        snackBar(
          'Success',
          'PDF downloaded successfully.',
          Colors.green,
          seconds: 5,
        );
      } else {
        throw Exception('Failed to download file: ${response.statusCode}');
      }
    } catch (e, s) {
      dPrint('directFileDownload error - $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
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
                    : showCircularIndicator(),
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
                                  try {
                                    // Check if file exists before opening
                                    if (file!.existsSync()) {
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
                                                  color:
                                                      BaseConfig.appThemeColor1,
                                                ),
                                                onPressed: () => Get.back(),
                                              ),
                                            ],
                                          ),
                                          content: SizedBox(
                                            width: Get.width,
                                            height: Get.height * 0.6,
                                            child: SfPdfViewer.file(file!),
                                          ),
                                        ),
                                      );
                                    } else {
                                      Get.back();
                                      snackBar(
                                        'Error',
                                        'PDF file not found at location: ${file!.path}',
                                        Colors.red,
                                        seconds: 5,
                                      );
                                    }
                                  } catch (e) {
                                    Get.back();
                                    snackBar(
                                      'Error',
                                      'Cannot open PDF: ${e.toString()}',
                                      Colors.red,
                                      seconds: 5,
                                    );
                                    dPrint('Error opening PDF: $e');
                                  }
                                }
                              },
                            ),
                          ),
                        ],
                      )
                    : FilledButtonApp(
                        text: 'Close',
                        onPressed: () {
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
