package org.egov.batchtelemetry.connector;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.batchtelemetry.config.AppProperties;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ElasticsearchConnector {

    private AppProperties appProperties;

    public ElasticsearchConnector() {
        appProperties = new AppProperties();
    }

    /** Spark 3 / elasticsearch-spark-30: configure ES-Hadoop + query window (used with shared {@link JavaSparkContext}). */
    public SparkConf buildSparkConf(long startTime, long endTime) {
        SparkConf sparkConf = new SparkConf().setAppName("batch-telemetry").setMaster("local[*]");
        sparkConf.set("es.index.auto.create", "true");
        sparkConf.set("es.nodes.wan.only", appProperties.getEsNodesWANOnly());
        sparkConf.set("es.nodes", appProperties.getEsHost());
        sparkConf.set("es.port", appProperties.getEsPort());

        sparkConf.set("es.query", "{\"query\":{\"bool\":{\"must\":[{\"match_all\":{}},{\"match_phrase\":{\"edata" +
                ".type\":{\"query\":\"page\"}}},{\"range\":{\"ets\":{\"gte\":" + startTime + ",\"lte\":" + endTime + "," +
                "\"format\":\"epoch_millis\"}}}],\"must_not\":[]}}}");
        return sparkConf;
    }

    /** RDD read uses caller's context — required for Spark 3 lifecycle (see {@link org.egov.batchtelemetry.application.BatchApplication}). */
    public JavaPairRDD<String, Map<String, Object>> getTelemetryRecords(JavaSparkContext jsc) {
        return JavaEsSpark.esRDD(jsc, appProperties.getInputTelemetryIndex());
    }

    public List<String> getExistingUserIds()  {
        List<String> userIds = new ArrayList<>();

        Integer numberOfUsers = getNumberOfExistingUsers();
        String distinctUserQuery = "{\"size\":0,\"aggs\":{\"distinct_uid\":{\"terms\":{\"field\":\"userId.keyword\"," +
                "\"size\":" + numberOfUsers + "}}}}";

        log.info("Number of Existing users : " + numberOfUsers);

        try {
            URL esURL = new URL(appProperties.getEsURL() + appProperties.getOutputTelemetrySessionsIndex() +
                    "_search/");
            String response = executeQuery(esURL, distinctUserQuery);
            userIds = JsonPath.read(response, "$.aggregations.distinct_uid.buckets.[*].key");

        } catch (Exception e) {
            log.error("Error while fetching userIds: " + e.getMessage());
        }

        return userIds;
    }

    public Integer getNumberOfExistingUsers() {
        String distinctUserQuery = "{\"size\":0,\"aggs\":{\"numberOfUsers\":{\"cardinality\":{\"field\":\"userId" +
                ".keyword\"}}}}";

        try {
            URL esURL = new URL(appProperties.getEsURL() + appProperties.getOutputTelemetrySessionsIndex() +
                    "_search/");
            String response = executeQuery(esURL, distinctUserQuery);
            return JsonPath.read(response, "$.aggregations.numberOfUsers.value");
        } catch (Exception e) {
            log.error("Error while fetching number of existing users: " + e.getMessage());
        }
        return 10000;
    }


    /** UTF-8 explicit I/O (clear on multi-JDK / Java 17 stacks). */
    private String executeQuery(URL url, String queryContent) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream out = connection.getOutputStream()) {
                out.write(queryContent.getBytes(StandardCharsets.UTF_8));
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();
                }
            }
            log.info("Error in Elasticsearch Query");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
