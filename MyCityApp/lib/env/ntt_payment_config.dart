import 'package:mobile_app/config/base_config.dart';

const _baseUrlPayment = 'baseUrlPayment';
const _baseUrlPaymentReturn = 'baseUrlPaymentReturn';
const _baseMode = 'nttPaymentMode';

enum NttEnvironment { release, development }

late Map<String, dynamic> _config;
late Map<String, dynamic> _configReturn;
late Map<String, dynamic> _mode;

void setNttEnvironment(NttEnvironment env) {
  switch (env) {
    case NttEnvironment.release:
      _config = _releaseConstants;
      _configReturn = _releaseConstantsReturn;
      _mode = _releaseMode;
      break;
    case NttEnvironment.development:
      _config = _debugConstants;
      _configReturn = _debugConstantsReturn;
      _mode = _debugMode;
      break;
  }
}

//Ntt payment base url
dynamic get nttAuthBaseUrl {
  return _config[_baseUrlPayment];
}

Map<String, dynamic> _debugConstants = {
  _baseUrlPayment: BaseConfig.NTT_DEV_URL,
};

Map<String, dynamic> _releaseConstants = {
  _baseUrlPayment: BaseConfig.NTT_RELEASE_URL,
};

//Ntt payment return url
dynamic get paymentReturnUrl {
  return _configReturn[_baseUrlPaymentReturn];
}

Map<String, dynamic> _debugConstantsReturn = {
  _baseUrlPaymentReturn: BaseConfig.NTT_DEV_RETURN_URL,
};

Map<String, dynamic> _releaseConstantsReturn = {
  _baseUrlPaymentReturn: BaseConfig.NTT_RELEASE_RETURN_URL,
};

//Ntt Payment Mode
dynamic get paymentMode {
  return _mode[_baseMode];
}

Map<String, dynamic> _debugMode = {
  _baseMode: 'uat',
};

Map<String, dynamic> _releaseMode = {
  _baseMode: 'live',
};
