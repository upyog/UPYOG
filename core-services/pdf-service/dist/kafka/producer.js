"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _logger = require("../config/logger");

var _logger2 = _interopRequireDefault(_logger);

var _EnvironmentVariables = require("../EnvironmentVariables");

var _EnvironmentVariables2 = _interopRequireDefault(_EnvironmentVariables);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var kafka = require("kafka-node");


var Producer = kafka.Producer;
var client = void 0;
// if (process.env.NODE_ENV === "development") {
// client = new kafka.Client();
client = new kafka.KafkaClient({ kafkaHost: _EnvironmentVariables2.default.KAFKA_BROKER_HOST, connectRetryOptions: { retries: 1 } });
//   console.log("local - ");
// } else {
//   client = new kafka.KafkaClient({ kafkaHost: envVariables.KAFKA_BROKER_HOST });
//   console.log("cloud - ");
// }

var producer = new Producer(client);

producer.on("ready", function () {
  _logger2.default.info("Producer is ready");
});

producer.on("error", function (err) {
  _logger2.default.error("Producer is in error state");
  _logger2.default.error(err.stack || err);
});

exports.default = producer;
//# sourceMappingURL=producer.js.map