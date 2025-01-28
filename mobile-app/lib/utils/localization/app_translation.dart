import 'package:get/get.dart';

class AppTranslations extends Translations {
  Map<String, Map<String, String>>? languages;
  AppTranslations({
    this.languages,
  });

  @override
  Map<String, Map<String, String>> get keys {
    return languages!;
  }

  // void update(Map<String, Map<String, String>> newLanguages) {
  //   languages = newLanguages;
  // }
}
