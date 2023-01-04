"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.listenConsumer = undefined;

var _regenerator = require("babel-runtime/regenerator");

var _regenerator2 = _interopRequireDefault(_regenerator);

var _asyncToGenerator2 = require("babel-runtime/helpers/asyncToGenerator");

var _asyncToGenerator3 = _interopRequireDefault(_asyncToGenerator2);

var _EnvironmentVariables = require("../EnvironmentVariables");

var _EnvironmentVariables2 = _interopRequireDefault(_EnvironmentVariables);

var _logger = require("../config/logger");

var _logger2 = _interopRequireDefault(_logger);

var _index = require("../index");

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var kafka = require("kafka-node");
var listenConsumer = exports.listenConsumer = function () {
  var _ref = (0, _asyncToGenerator3.default)( /*#__PURE__*/_regenerator2.default.mark(function _callee(topic) {
    var receiveJob, Consumer, client, topicList, i, consumer;
    return _regenerator2.default.wrap(function _callee$(_context) {
      while (1) {
        switch (_context.prev = _context.next) {
          case 0:
            //let receiveJob = envVariables.KAFKA_RECEIVE_CREATE_JOB_TOPIC;
            receiveJob = topic;
            Consumer = kafka.Consumer;
            client = new kafka.KafkaClient({
              kafkaHost: _EnvironmentVariables2.default.KAFKA_BROKER_HOST
            });
            topicList = [];

            for (i in receiveJob) {
              topicList.push({ topic: receiveJob[i] });
            }

            consumer = new Consumer(client, topicList, {

              autoCommit: false
            });


            consumer.on("ready", function () {
              _logger2.default.info("consumer is ready");
            });

            consumer.on("message", function (message) {
              _logger2.default.info("record received on consumer for create");
              try {
                var data = JSON.parse(message.value);
                data.topic = message.topic;
                (0, _index.createAndSave)(data, null, function () {}, function () {}).then(function () {
                  _logger2.default.info("record created for consumer request");
                }).catch(function (error) {
                  _logger2.default.error(error.stack || error);
                });
              } catch (error) {
                _logger2.default.error("error in create request by consumer " + error.message);
                _logger2.default.error(error.stack || error);
              }
            });

            consumer.on("error", function (err) {
              _logger2.default.error("error in consumer " + err.message);
              _logger2.default.error(err.stack || err);
            });

            consumer.on("offsetOutOfRange", function (err) {
              _logger2.default.error("offsetOutOfRange");
              _logger2.default.error(err.stack || err);
            });

          case 10:
          case "end":
            return _context.stop();
        }
      }
    }, _callee, undefined);
  }));

  return function listenConsumer(_x) {
    return _ref.apply(this, arguments);
  };
}();
//# sourceMappingURL=consumer.js.map