import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:intl/intl.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/model/citizen/token/token.dart';
import 'package:mobile_app/utils/utils.dart';

extension OnPressed on Widget {
  Widget ripple(
    Function onPressed, {
    ShapeBorder? customBorder,
  }) =>
      InkWell(
        onTap: () {
          onPressed();
        },
        customBorder: customBorder,
        child: this,
      );

  Widget rippleColor(
    Function onPressed,
    double radiusSiz, {
    double borderRadius = 0.0,
    Color rippleColor = Colors.grey, // Added parameter for ripple color
  }) =>
      Material(
        color: Colors.transparent,
        child: InkWell(
          radius: radiusSiz,
          splashColor: rippleColor.withValues(alpha: 0.5), // Ripple color
          highlightColor: Colors.transparent,
          splashFactory: InkRipple.splashFactory,
          borderRadius: BorderRadius.circular(borderRadius),
          customBorder: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(borderRadius),
          ),
          onTap: () {
            onPressed();
          },
          child: this,
        ),
      );
}

extension PhoneValidator on String {
  bool isValidPhone() {
    return RegExp(
      r'^[0-9]+$',
    ).hasMatch(this);
  }
}

extension EmailValidator on String? {
  bool isValidEmail() {
    if (this == null) return false;
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    return emailRegex.hasMatch(this!);
  }
}

extension DobValidator on String {
  bool isValidDob() {
    return RegExp(r'^\d{4}-\d{2}-\d{2}$').hasMatch(this);
  }
}

extension UserProfileExtensions on FileStore? {
  String getUserPhoto() {
    if (isNotNullOrEmpty(this) &&
        this?.fileStoreIds != null &&
        this!.fileStoreIds!.isNotEmpty) {
      return this!.fileStoreIds!.first.url!.split(',').first;
    }
    return '';
  }
}

extension DateFormatExtension on int? {
  String? toCustomDateFormat({String pattern = 'd-MMM-yyyy'}) {
    if (this == null) return null;
    final dateTime = DateTime.fromMillisecondsSinceEpoch(this!);
    final formatter = DateFormat(pattern);
    return formatter.format(dateTime);
  }
}

extension DateTimeFormatExtension on DateTime? {
  String? toCustomDateFormat({String pattern = 'd-MMM-yyyy'}) {
    if (this == null) return null;
    final formatter = DateFormat(pattern);
    return formatter.format(this!);
  }
}

extension ConvertDoubleToString on double? {
  String? doubleToString({String pattern = '0.00'}) {
    if (this == null) return null;
    final formatter = NumberFormat(pattern);
    return formatter.format(this!);
  }
}

// Remove duplicates based on tenantId
extension RemoveDuplicates on List<Role> {
  List<Role> removeDuplicates() {
    final uniqueRoles = <String, Role>{};

    for (var role in this) {
      uniqueRoles[role.tenantId!] = role;
    }

    return uniqueRoles.values.toList();
  }
}

extension PreviousYearCheck on int {
  bool isPreviousYearFromMilliseconds() {
    DateTime date = DateTime.fromMillisecondsSinceEpoch(this);
    DateTime now = DateTime.now();
    if (date.year > now.year) {
      return false;
    }
    return date.year < now.year;
  }
}

extension DateTimeExtension on int {
  String timeAgo({bool numericDates = true}) {
    final date1 = DateTime.fromMillisecondsSinceEpoch(this);
    final date2 = DateTime.now();
    final difference = date2.difference(date1);

    if ((difference.inDays / 7).floor() >= 1) {
      return (numericDates) ? '1 week ago' : 'Last week';
    } else if (difference.inDays >= 2) {
      return '${difference.inDays} days ago';
    } else if (difference.inDays >= 1) {
      return (numericDates) ? '1 day ago' : 'Yesterday';
    } else if (difference.inHours >= 2) {
      return '${difference.inHours} hours ago';
    } else if (difference.inHours >= 1) {
      return (numericDates) ? '1 hour ago' : 'An hour ago';
    } else if (difference.inMinutes >= 2) {
      return '${difference.inMinutes} minutes ago';
    } else if (difference.inMinutes >= 1) {
      return (numericDates) ? '1 minute ago' : 'A minute ago';
    } else if (difference.inSeconds >= 3) {
      return '${difference.inSeconds} seconds ago';
    } else {
      return 'Just now';
    }
  }
}

extension AlphanumericInputFormatterExtension on List<TextInputFormatter> {
  static final TextInputFormatter _alphanumericSpaceFormatter =
      FilteringTextInputFormatter.allow(RegExp(r'[a-zA-Z0-9 ]'));

  List<TextInputFormatter> addAlphanumericSpaceFormatter() {
    return [
      ...this,
      _alphanumericSpaceFormatter,
    ];
  }
}

extension StringExtension on String {
  String addLastPart() => '$this.${split('.').last}';

  String capitalizeWordsWithUnderscore() {
    return split('_').map((word) {
      return word[0].toUpperCase() + word.substring(1);
    }).join('_');
  }
}
