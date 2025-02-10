import 'package:mobile_app/config/base_config.dart';

const _baseUrl = "baseUrl";

enum Environment {
  /// Test development environment
  dev,

  /// Sandbox environment
  stage,

  /// Production environment
  prod,
}

late Map<String, dynamic> _config;

void setEnvironment(Environment env) {
  switch (env) {
    case Environment.dev:
      _config = devConstants;
      break;
    case Environment.stage:
      _config = stageConstants;
      break;
    case Environment.prod:
      _config = prodConstants;
      break;
  }
}

dynamic get apiBaseUrl {
  return _config[_baseUrl];
}

Map<String, dynamic> devConstants = {
  _baseUrl: BaseConfig.DEV_URL,
};

Map<String, dynamic> stageConstants = {
  _baseUrl: BaseConfig.STAGE_URL,
};

Map<String, dynamic> prodConstants = {
  _baseUrl: BaseConfig.PROD_URL,
};
