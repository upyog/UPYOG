import 'package:flutter/material.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class Loaders {
  static Future<void> showLoadingDialog(
    BuildContext context, {
    String? label,
  }) async {
    return showDialog<void>(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return PopScope(
          child: SizedBox(
            height: MediaQuery.of(context).size.height,
            width: MediaQuery.of(context).size.width,
            child: SimpleDialog(
              elevation: 0.0,
              backgroundColor: Colors.transparent,
              children: <Widget>[
                Center(
                  child: Column(
                    children: [
                      const CircularProgressIndicator(
                        color: BaseConfig.redColor1,
                      ),
                      const SizedBox(height: 10),
                      MediumText(
                        text: label ?? 'Loading...',
                        color: BaseConfig.mainBackgroundColor,
                        size: 16,
                        fontWeight: FontWeight.w700,
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
