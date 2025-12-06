import 'dart:async';
import 'dart:io';

import 'package:dio/dio.dart' as dio;
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:http_parser/http_parser.dart';
import 'package:image_picker/image_picker.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:mime/mime.dart';
import 'package:mobile_app/env/app_config.dart';
import 'package:mobile_app/model/citizen/bpa_model/bpa_model.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/payments/payment.dart';
import 'package:mobile_app/model/citizen/properties_tax/my_properties.dart';
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart';
import 'package:mobile_app/model/citizen/water_sewerage/sewerage.dart';
import 'package:mobile_app/model/citizen/water_sewerage/water.dart';
import 'package:mobile_app/repository/file_repository.dart';
import 'package:mobile_app/services/file_services.dart';
import 'package:mobile_app/utils/constants/api_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/errors/error_handler.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:rxdart/rxdart.dart';

class FileController extends GetxController {
  //Stream
  final BehaviorSubject<FileStore?> fileStoreStreamCtrl =
      BehaviorSubject<FileStore?>();

  //Models
  FileStore? fileStore;

  // For files
  File? imageFile;
  RxBool isSelectedFile = false.obs;
  String fileName = '';
  RxBool isLoading = false.obs, isLoading2 = false.obs, isLoading3 = false.obs;

  dio.Dio dioInit = dio.Dio();

  @override
  void onClose() {
    super.onClose();
    fileStoreStreamCtrl.close();
  }

  /// Image selector
  Future<void> selectAndPickImage({
    ImageSource imageSource = ImageSource.gallery,
  }) async {
    imageFile = null;
    var pickedFile = await FileService.selectImage(imageSource: imageSource);
    if (pickedFile != null) {
      imageFile = pickedFile;
      fileName = imageFile!.path.split('/').last;
      isSelectedFile.value = true;
      dPrint('ImageFilePath: ${imageFile!.path}');
      Get.back();
    } else {
      removeSelectedImage();
    }
    update();
  }

  /// Any file selector
  Future<(File?, String)> selectAndPickFile() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      type: FileType.any,
    );

    if (result != null && result.files.single.path != null) {
      File pickedFile = File(result.files.single.path!);

      imageFile = pickedFile;
      fileName = pickedFile.path.split('/').last;

      return (pickedFile, fileName);
    }

    return (null, '');
  }

  /// Remove selected image
  void removeSelectedImage() {
    imageFile = null;
    isSelectedFile.value = false;
    update();
  }

  /// Get the file store object by `fileStoreId`
  Future<FileStore?> getFiles({
    required String tenantId,
    required String token,
    required String fileStoreIds,
  }) async {
    try {
      final query = {
        'tenantId': tenantId,
        'fileStoreIds': fileStoreIds,
      };
      final fileRes = await FileRepository.getFiles(
        body: {},
        token: token,
        query: query,
      );
      if (fileRes != null) {
        fileStore = FileStore.fromJson(fileRes);
        dPrint(fileStoreIds.toString());
        fileStoreStreamCtrl.sink.add(fileStore);
        return fileStore;
      }
    } catch (e, s) {
      fileStoreStreamCtrl.sink.addError(e);
      dPrint('getFiles Error: ${e.toString()}');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return null;
  }

  /// Upload file and return `fileStoreId`
  Future<String?> postFile({
    required String token,
    required String tenantId,
    required String module,
  }) async {
    try {
      final url = apiBaseUrl + Url.FILES;
      final mimeType = lookupMimeType(imageFile!.path);
      dio.FormData formData = dio.FormData.fromMap({
        "file": await dio.MultipartFile.fromFile(
          imageFile!.path,
          filename: fileName,
          contentType: MediaType.parse(mimeType!),
        ),
        "tenantId": tenantId,
        "module": module,
      });
      var response = await dioInit.post(
        url,
        data: formData,
        options: dio.Options(
          headers: {
            'Auth-Token': token,
            HttpHeaders.contentTypeHeader: 'multipart/form-data',
          },
        ),
      );

      var data = response.data['files'] as List<dynamic>;
      var fileId = data.firstOrNull['fileStoreId'];

      return fileId;
    } catch (e, s) {
      dPrint('postFileError - $e');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return null;
  }

  /// Return icon by filetype
  (IconData, FileExtType) getFileType(String url) {
    String fileType = url.split('?').first.split('/').last.split('.').last;
    switch (fileType.toLowerCase()) {
      case 'jpeg':
        return (Icons.image, FileExtType.jpeg);
      case 'jpg':
        return (Icons.image, FileExtType.jpg);
      case 'png':
        return (Icons.image, FileExtType.png);
      case 'pdf':
        return (Icons.picture_as_pdf, FileExtType.pdf);
      case 'dxf':
        return (Icons.area_chart, FileExtType.dxf);
      case 'docs':
        return (Symbols.dock, FileExtType.docs);
      default:
        return (Icons.picture_as_pdf, FileExtType.none);
    }
  }

  /// Return icon by filetype
  (IconData, FileExtType) getFileTypeString(String imageFile) {
    final fileType = imageFile.split('.').last;
    switch (fileType.toLowerCase()) {
      case 'jpeg':
        return (Icons.image, FileExtType.jpeg);
      case 'jpg':
        return (Icons.image, FileExtType.jpg);
      case 'png':
        return (Icons.image, FileExtType.png);
      case 'pdf':
        return (Icons.picture_as_pdf, FileExtType.pdf);
      case 'dxf':
        return (Icons.area_chart, FileExtType.dxf);
      default:
        return (Icons.picture_as_pdf, FileExtType.none);
    }
  }

  /// Get the getPdfService`
  Future<dynamic> getPdfServiceFile({
    required String tenantId,
    required String token,
    required PdfKey key,
    License? license,
    Payment? payment,
    List<Payment?>? payments,
    SewerageConnection? sewerageConnection,
    WaterConnection? waterConnection,
    BpaElement? bpaElement,
    Property? property,
  }) async {
    Map<String, dynamic> body = {};
    try {
      final query = {
        'tenantId': tenantId,
        'key': key.name,
      };

      if (property != null) {
        body = {
          'Properties': [property.toJson()],
        };
      } else if (license != null) {
        body = {
          'Licenses': [license.toJson()],
        };
      } else if (payment != null) {
        body = {
          'Payments': [payment.toJson()],
        };
      } else if (payments != null) {
        body = {
          'Payments': payments.map((e) => e!.toJson()).toList(),
        };
      } else if (sewerageConnection != null) {
        body = {
          'SewerageConnection': [sewerageConnection.toJson()],
        };
      } else if (waterConnection != null) {
        body = {
          'WaterConnection': [waterConnection.toJson()],
        };
      } else if (bpaElement != null) {
        body = {
          'Bpa': [bpaElement.toJson()],
        };
      }

      final fileRes = await FileRepository.getPdfService(
        body: body,
        token: token,
        query: query,
      );
      final newFileStoreId = await fileRes['filestoreIds'][0];
      return newFileStoreId;
    } catch (e, s) {
      dPrint('getPdf Error: ${e.toString()}');
      ErrorHandler.allExceptionsHandler(e, s);
    }
    return null;
  }
}
