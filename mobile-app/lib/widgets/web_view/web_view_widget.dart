import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:mobile_app/env/app_config.dart';

class WebViewWidget extends StatelessWidget {
  const WebViewWidget({
    super.key,
    required this.url,
    this.onUpdateVisitedHistory,
    this.onLoadStop,
    this.onLoadStart,
  });
  final String url;
  final Function(InAppWebViewController, WebUri?, bool?)?
      onUpdateVisitedHistory;
  final Function(InAppWebViewController, WebUri?)? onLoadStop;
  final Function(InAppWebViewController, WebUri?)? onLoadStart;

  @override
  Widget build(BuildContext context) {
    InAppWebViewSettings options = InAppWebViewSettings(
      useShouldOverrideUrlLoading: true,
      mediaPlaybackRequiresUserGesture: false,
      transparentBackground: true,
      userAgent: Platform.isIOS
          ? "Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/117.0.5938.108 Mobile/15E148 Safari/604.1"
          : "",
      useOnLoadResource: true,
      allowsInlineMediaPlayback: true,
    );

    final blockUrlRedirect = [
      '${apiBaseUrl}digit-ui/citizen/login/otp',
      '${apiBaseUrl}digit-ui/citizen/select-language',
    ];

    return InAppWebView(
      initialSettings: options,
      initialUrlRequest: URLRequest(url: WebUri.uri(Uri.parse(url))),
      onUpdateVisitedHistory: onUpdateVisitedHistory,
      onLoadStop: onLoadStop,
      onLoadStart: onLoadStart,
      shouldOverrideUrlLoading: (controller, navigationAction) async {
        final uri = navigationAction.request.url.toString().split('?')[0];

        if (blockUrlRedirect.contains(uri)) {
          return NavigationActionPolicy.CANCEL;
        }

        return NavigationActionPolicy.ALLOW;
      },
      onPermissionRequest: (controller, request) async {
        return PermissionResponse(
          resources: request.resources,
          action: PermissionResponseAction.GRANT,
        );
      },
    );
  }
}
