import 'dart:io';

import 'package:flutter/material.dart';
import 'package:photo_view/photo_view.dart';

class ImageViewerPage extends StatelessWidget {
  final String url;
  final bool isFileImage;
  const ImageViewerPage({
    super.key,
    required this.url,
    this.isFileImage = false,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {
            Navigator.pop(context);
          },
          icon: const Icon(Icons.arrow_back_ios),
        ),
      ),
      body: isFileImage == true
          ? PhotoView(
              imageProvider: FileImage(File(url)),
            )
          : PhotoView(
              imageProvider: NetworkImage(url),
            ),
    );
  }
}
