import http from "http";
import express from "express";
import cors from "cors";
import morgan from "morgan";
import bodyParser from "body-parser";
import config from "./config.json";
import tracer from "./middleware/tracer";
import envVariables from "./envVariables";
import api from "./controller";
const { Pool } = require("pg");
var swaggerUi = require("swagger-ui-express"),
  swaggerDocument = require("./swagger.json");

var ssl = envVariables.DB_SSL;
if (typeof ssl == "string")
  ssl = ssl.toLowerCase() == "true";

/* Create a new pool instance to manage database connections inseatd of creating a new client for each request. 
 This allows for better performance and resource management. */
const pool = new Pool({
  user: envVariables.DB_USERNAME,
  host: envVariables.DB_HOST,
  database: envVariables.DB_NAME,
  password: envVariables.DB_PASSWORD,
  ssl: ssl,
  port: envVariables.DB_PORT,
  max: envVariables.DB_MAX_POOL_SIZE,
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 2000
});

let app = express();
app.server = http.createServer(app);

app.use(morgan("dev"));

app.use(
  cors({
    exposedHeaders: config.corsHeaders
  })
);

app.use(bodyParser.json({ limit: config.bodyLimit }));

app.use(tracer());

app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerDocument));

app.use("/", api(pool));

//error handler middleware
app.use((err, req, res, next) => {
  console.log(err);
  if (!err.errorType) {
    res.status(err.status).json(err.data);
  } else if (err.errorType == "custom") {
    res.status(400).json(err.errorReponse);
  } else {
    res.status(500);
    res.send("Oops, something went wrong.");
  }
});

console.log(envVariables.SERVER_PORT);

app.server.listen(envVariables.SERVER_PORT, () => {
  console.log(`Started on port ${app.server.address().port}`);
});

export default app;
