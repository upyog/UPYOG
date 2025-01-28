import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';

Widget networkErrorPage(
  context,
  Function()? callBack, {
  bool isCard = false,
  String errorText = "Unable to connect to the server",
}) {
  return isCard
      ? Container(
          decoration: BoxDecoration(
            border: Border.all(color: Colors.red),
            borderRadius: BorderRadius.circular(10),
          ),
          child: _getErrorText(errorText, callBack).paddingAll(8.0),
        )
      : _getErrorText(errorText, callBack);
}

Widget _getErrorText(errorText, callBack) => Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          Text(
            errorText,
            style: const TextStyle(color: Colors.black, fontSize: 18),
          ),
          Padding(
            padding: const EdgeInsets.all(12.0),
            child: ElevatedButton(
              onPressed: callBack,
              child: const Text(
                'Retry',
                style: TextStyle(color: BaseConfig.textColor),
              ),
            ),
          ),
        ],
      ),
    );
