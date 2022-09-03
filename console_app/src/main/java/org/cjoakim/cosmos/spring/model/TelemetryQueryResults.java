package org.cjoakim.cosmos.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * Instances of this class are used to contain a set of Telemetry query result documents
 * along with the SQL query and CosmosDB metadata such as request charge.
 *
 * Chris Joakim, Microsoft, September 2022
 */

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelemetryQueryResults {

    private ArrayList<TelemetryEvent> documents;
    private long documentCount;
    private String sql;
    private double requestCharge;
    private int pageCount;

    public TelemetryQueryResults() {

        this(null);
    }

    public TelemetryQueryResults(String sql) {

        super();
        documents = new ArrayList<TelemetryEvent>();
        this.sql = sql;
    }

    public void addDocument(TelemetryEvent doc) {

        if (doc != null) {
            documents.add(doc);
            documentCount++;
        }
    }

    public void incrementPageCount() {

        pageCount++;
    }

    public double addRequestCharge(double incrementalCharge) {

        requestCharge = requestCharge + incrementalCharge;
        return requestCharge;
    }

}
