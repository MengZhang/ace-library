package org.agmip.ace.parser.core;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AceExperiment extends AceComponent {
    private String eid;
    private AceWeather weather;
    private AceSoil    soil;
    private AceInitialConditions ic;
    private AceObservedData observed;
    private AceEventCollection events;

    public AceExperiment(byte[] source) throws IOException {
        super(source);
        this.eid = this.getValue("eid");
        this.extractSubcomponents();
    }

    public String getId() throws IOException {
        return this.eid;
    }

    public AceWeather getWeather() {
        return this.weather;
    }

    public AceSoil getSoil() {
        return this.soil;
    }

    public void setWeather(AceWeather weather) {
        this.weather = weather;
    }

    public void setSoil(AceSoil soil) {
        this.soil = soil;
    }

    public AceInitialConditions getInitialConditions() throws IOException {
        return this.ic;
    }

    public AceObservedData getOberservedData() {
        return this.observed;
    }

    public AceEventCollection getEvents() {
        return this.events;
    }

    private void extractSubcomponents() throws IOException {
        ByteArrayOutputStream finalOut = new ByteArrayOutputStream();
        JsonParser p = this.getParser();
        JsonGenerator g = this.getGenerator(finalOut);
        JsonToken t;

        t = p.nextToken();

        while (t != null) {
            String currentName = p.getCurrentName();
            if(currentName != null && t == JsonToken.FIELD_NAME &&
                    (currentName.equals("initial_conditions") ||
                     currentName.equals("observed") ||
                     currentName.equals("events"))) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                t = p.nextToken();
                JsonGenerator subcomponent = this.getGenerator(outStream);
                subcomponent.copyCurrentStructure(p);
                subcomponent.flush();
                byte[] out = outStream.toByteArray();
                subcomponent.close();
                if (currentName.equals("initial_conditions")) {
                    this.ic = new AceInitialConditions(out);
                } else if (currentName.equals("observed")) {
                    this.observed = new AceObservedData(out);
                } else if (currentName.equals("events")) {
                    this.events = new AceEventCollection(out);
                }
            } else {
                g.copyCurrentEvent(p);
            }
            t = p.nextToken();
        }
        g.flush();
        byte[] experiment = finalOut.toByteArray();
        this.component = experiment;
        p.close();
    }
}
