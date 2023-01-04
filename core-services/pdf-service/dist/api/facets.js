'use strict';

Object.defineProperty(exports, "__esModule", {
	value: true
});

var _resourceRouterMiddleware = require('resource-router-middleware');

var _resourceRouterMiddleware2 = _interopRequireDefault(_resourceRouterMiddleware);

var _facets = require('../models/facets');

var _facets2 = _interopRequireDefault(_facets);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

exports.default = function (_ref) {
	var config = _ref.config,
	    db = _ref.db;
	return (0, _resourceRouterMiddleware2.default)({

		/** Property name to store preloaded entity on `request`. */
		id: 'facet',

		/** For requests with an `id`, you can auto-load the entity.
   *  Errors terminate the request, success sets `req[id] = data`.
   */
		load: function load(req, id, callback) {
			var facet = _facets2.default.find(function (facet) {
				return facet.id === id;
			}),
			    err = facet ? null : 'Not found';
			callback(err, facet);
		},


		/** GET / - List all entities */
		index: function index(_ref2, res) {
			var params = _ref2.params;

			res.json(_facets2.default);
		},


		/** POST / - Create a new entity */
		create: function create(_ref3, res) {
			var body = _ref3.body;

			body.id = _facets2.default.length.toString(36);
			_facets2.default.push(body);
			res.json(body);
		},


		/** GET /:id - Return a given entity */
		read: function read(_ref4, res) {
			var facet = _ref4.facet;

			res.json(facet);
		},


		/** PUT /:id - Update a given entity */
		update: function update(_ref5, res) {
			var facet = _ref5.facet,
			    body = _ref5.body;

			for (var key in body) {
				if (key !== 'id') {
					facet[key] = body[key];
				}
			}
			res.sendStatus(204);
		},


		/** DELETE /:id - Delete a given entity */
		delete: function _delete(_ref6, res) {
			var facet = _ref6.facet;

			_facets2.default.splice(_facets2.default.indexOf(facet), 1);
			res.sendStatus(204);
		}
	});
};
//# sourceMappingURL=facets.js.map