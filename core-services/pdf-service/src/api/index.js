// this file is not used any where in the service
import { version } from '../../package.json' assert { type: 'json' };
import { Router } from 'express';
import facets from './facets.js';

export default ({ config, db }) => {
	let api = Router();

	// mount the facets resource
	api.use('/facets', facets({ config, db }));

	// perhaps expose some API metadata at the root
	api.get('/', (req, res) => {
		res.json({ version });
	});

	return api;
}