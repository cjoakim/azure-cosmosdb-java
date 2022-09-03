package org.cjoakim.cosmos.spring.process;

import java.text.DecimalFormat;

/**
 * Abstract superclass for processing that is delegated from the main entry-point; class App.
 *
 * Chris Joakim, Microsoft, September 2022
 */

public abstract class AbstractProcess {

    public abstract void process() throws Exception;

    protected String formattedCount(long value) {
        DecimalFormat df = new DecimalFormat("###,###,###,###");
        return df.format(value);
    }
}
