'use strict';

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.toRes = toRes;

/**	Creates a callback that proxies node callback style arguments to an Express Response object.
 *	@param {express.Response} res	Express HTTP Response
 *	@param {number} [status=200]	Status code to send on success
 *
 *	@example
 *		list(req, res) {
 *			collection.find({}, toRes(res));
 *		}
 */
function toRes(res) {
  var status = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 200;

  return function (err, thing) {
    if (err) return res.status(400).send(err);

    if (thing && typeof thing.toObject === 'function') {
      thing = thing.toObject();
    }
    res.status(status).json(thing);
  };
}
//# sourceMappingURL=util.js.map