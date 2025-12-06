import 'dart:io';

import 'package:image_picker/image_picker.dart';

final ImagePicker picker = ImagePicker();

class FileService {
  static Future<File?> selectImage({
    ImageSource imageSource = ImageSource.gallery,
  }) async {
    var pickedFile = await picker.pickImage(source: imageSource);
    if (pickedFile != null) {
      return File(pickedFile.path);
    }
    return null;
  }
}
