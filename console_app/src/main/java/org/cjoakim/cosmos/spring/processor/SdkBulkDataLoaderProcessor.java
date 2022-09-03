package org.cjoakim.cosmos.spring.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cjoakim.cosmos.spring.AppConfiguration;
import org.cjoakim.cosmos.spring.AppConstants;
import org.cjoakim.cosmos.spring.dao.CosmosAsynchDao;
import org.cjoakim.cosmos.spring.model.TelemetryEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Execute CosmosDB bulk loading with the SDK and Asynch methods.
 *
 * Chris Joakim, Microsoft, September 2022
 */

@Component
@Data
@NoArgsConstructor
@Slf4j
public class SdkBulkDataLoaderProcessor extends ConsoleAppProcessor implements AppConstants {

    private String container;
    private long   skipCount  = 0;

    private long   outputDocCount = 0;
    private long   maxRecords = Long.MAX_VALUE;
    private String infile;
    private String loadType;

    @Value("${spring.cloud.azure.cosmos.endpoint}")
    public String uri;
    @Value("${spring.cloud.azure.cosmos.key}")
    public String key;
    @Value("${spring.cloud.azure.cosmos.database}")
    private String dbName;

    private CosmosAsynchDao dao = new CosmosAsynchDao();

    private boolean doWrites = false;
    private boolean verbose  = false;

    public void process() throws Exception {

        try {
            doWrites = AppConfiguration.booleanArg(DO_WRITES_FLAG);
            verbose  = AppConfiguration.booleanArg(VERBOSE_FLAG);

            log.warn("process, skipCount:  " + skipCount);
            log.warn("process, maxRecords: " + maxRecords);
            log.warn("process, loadType:   " + loadType);
            log.warn("process, infile:     " + infile);
            log.warn("process, doWrites:   " + doWrites);

            verbose  = AppConfiguration.booleanArg(VERBOSE_FLAG);
            dao.initialize(uri, key, dbName, verbose);
            dao.setCurrentContainer(container);

            if (loadType.equalsIgnoreCase("epa_ozone_telemetry")) {
                loadEpaOzoneTelemetry();
            }
        }
        finally {
            dao.close();
        }
    }

    private void loadEpaOzoneTelemetry() throws Exception {
        log.warn("loadEpaOzoneTelemetry...");

        ArrayList<TelemetryEvent> events = readTelemetry();
        log.warn("events read from disk: " + events.size());

        long startMs = System.currentTimeMillis();

        dao.bulkCreateTelemetryEvents(Flux.fromIterable(events));

        long elapsedMs = System.currentTimeMillis() - startMs;
        log.warn("bulk create complete in: " + elapsedMs + "ms");
    }

    private ArrayList<TelemetryEvent> readTelemetry() {

        ArrayList<TelemetryEvent> events = new  ArrayList<TelemetryEvent>();
        try {
            BufferedReader reader = null;
            long lineNumber = 0;
            long linesProcessed = 0;
            ObjectMapper mapper = new ObjectMapper();

            Path path = Paths.get(infile);
            reader = Files.newBufferedReader(path);
            String line = null;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber >= skipCount) {
                    if (linesProcessed < maxRecords) {
                        linesProcessed++;
                        TelemetryEvent event = null;
                        try {
                            event = mapper.readValue(line.trim(), TelemetryEvent.class);
                            event.setId(UUID.randomUUID().toString());
                            events.add(event);
                        }
                        catch (JsonProcessingException e) {
                            log.warn("bad line " + lineNumber + ": " + line);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return events;
    }
}
