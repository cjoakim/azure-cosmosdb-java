package org.cjoakim.cosmos.spring.dao;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.cjoakim.cosmos.spring.AppConfiguration;
import org.cjoakim.cosmos.spring.model.TelemetryEvent;
import org.cjoakim.cosmos.spring.model.TelemetryQueryResults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * This is a Data Access Object (DAO) which uses the CosmosDB SDK for Java
 * rather than Spring Data.
 *
 * Chris Joakim, Microsoft, September 2022
 */

@Slf4j
public class CosmosAsynchDao {

    private CosmosAsyncClient client;
    private CosmosAsyncDatabase database;
    private CosmosAsyncContainer container;

    private String uri;
    private String key;
    private String currentDbName;
    private String currentContainerName = "";

    boolean verbose;

    public CosmosAsynchDao() {

        super();
    }

    public CosmosAsyncClient initialize(
            String uri, String key, String dbName, boolean verbose) {

        this.uri = uri;
        this.key = key;
        this.currentDbName = dbName;

        if (verbose) {
            log.warn("uri:     " + uri);
            log.warn("key:     " + key);
            log.warn("dbName:  " + dbName);
            log.warn("regions: " + AppConfiguration.getInstance().getPreferredRegions());
        }

        if (client == null) {
            client = new CosmosClientBuilder()
                    .endpoint(uri)
                    .key(key)
                    .preferredRegions(AppConfiguration.getInstance().getPreferredRegions())
                    .consistencyLevel(ConsistencyLevel.SESSION)
                    .contentResponseOnWriteEnabled(true)
                    .buildAsyncClient();
            log.warn("client: " + client);

            database = client.getDatabase(this.currentDbName);
            log.warn("client connected to database Id: " + database.getId());
        }
        return client;
    }

    public void close() {

        if (client != null) {
            log.warn("closing...");
            client.close();
            log.warn("closed");
        }
    }

    public void setCurrentContainer(String c) {

        if (this.currentContainerName.equalsIgnoreCase(c)) {
            return;
        }
        else {
            container = database.getContainer(c);
            this.currentContainerName = c;
        }
    }

    public TelemetryQueryResults getAllTelemetry() {

        String sql = "select * from c offset 0 limit 10000";
        int    pageSize = 100;
        String continuationToken = null;
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        TelemetryQueryResults resultsStruct = new TelemetryQueryResults(sql);
        resultsStruct.start();

        // Execute the SQL query and iterate the paginated result set,
        // collecting the documents and total RU charge.
        do {
            Iterable<FeedResponse<TelemetryEvent>> feedResponseIterator =
                    container.queryItems(sql, queryOptions, TelemetryEvent.class).byPage(
                            continuationToken, pageSize).toIterable();  // Asynch Flux to Iterable

            for (FeedResponse<TelemetryEvent> page : feedResponseIterator) {
                for (TelemetryEvent doc : page.getResults()) {
                    resultsStruct.addDocument(doc);
                }
                resultsStruct.addRequestCharge(page.getRequestCharge());
                resultsStruct.incrementPageCount();
                continuationToken = page.getContinuationToken();
            }
        }
        while (continuationToken != null);
        resultsStruct.stop();
        return resultsStruct;
    }

    public TelemetryQueryResults countAllTelemetry() {

        String sql = "select count(1) as count from c";
        int    pageSize = 100;
        String continuationToken = null;
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        TelemetryQueryResults resultsStruct = new TelemetryQueryResults(sql);
        resultsStruct.start();

        Iterable<FeedResponse<JsonNode>> feedResponseIterator =
                container.queryItems(sql, queryOptions, JsonNode.class).byPage(
                        continuationToken, pageSize).toIterable();  // Asynch Flux to Iterable

        // only one result in one page is expected here
        for (FeedResponse<JsonNode> page : feedResponseIterator) {
            for (JsonNode node : page.getResults()) {
                log.warn("Count: " + node.toString());  // Count: {"count":1000} or Count: {"$1":1000}
                long count = Long.parseLong(node.get("count").asText());
                resultsStruct.setDocumentCount(count);
            }
            resultsStruct.addRequestCharge(page.getRequestCharge());
            resultsStruct.incrementPageCount();
        }
        resultsStruct.stop();
        return resultsStruct;
    }
}
