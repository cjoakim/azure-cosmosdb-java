package org.cjoakim.cosmos.spring.processor;

import java.text.DecimalFormat;

/**
 * Abstract superclass for processing delegated from the main entry-point class.
 *
 * Chris Joakim, Microsoft, September 2022
 */

public abstract class ConsoleAppProcessor {

    public abstract void process() throws Exception;

    protected String formattedCount(long value) {
        DecimalFormat df = new DecimalFormat("###,###,###,###");
        return df.format(value);
    }
}
