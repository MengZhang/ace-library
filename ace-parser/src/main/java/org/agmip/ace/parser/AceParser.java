package org.agmip.ace.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.agmip.ace.parser.core.AceComponentType;
import org.agmip.ace.parser.core.JsonFactoryImpl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;


public class AceParser {
    private AceParser(){}

    public static AceDataset parse(byte[] source) throws IOException {
       return run(JsonFactoryImpl.INSTANCE.getParser(source));
    }

    public static AceDataset parse(InputStream source) throws IOException {
        return run(JsonFactoryImpl.INSTANCE.getParser(source));
    }

    private static AceDataset run(JsonParser p) throws IOException {
        JsonToken t;
        AceDataset dataset = new AceDataset();
        JsonGenerator component = null;
        ByteArrayOutputStream componentOut = new ByteArrayOutputStream();
        boolean inComponent = false;
        AceComponentType componentType = null;

        t = p.nextToken();
        while (t != null) {
            String currentName = p.getCurrentName();
            if (currentName != null && t == JsonToken.FIELD_NAME &&
                (currentName.equals("weathers") ||
                 currentName.equals("soils") ||
                 currentName.equals("experiments"))) {
                inComponent = true;
                if (currentName.equals("weathers")) {
                    componentType = AceComponentType.ACE_WEATHER;
                } else if (currentName.equals("soils")) {
                    componentType = AceComponentType.ACE_SOIL;
                } else if (currentName.equals("experiments")) {
                    componentType = AceComponentType.ACE_EXPERIMENT;
                }
            }

            if (inComponent) {
                if (t == JsonToken.START_OBJECT) {
                    // We are starting a new component
                    component = JsonFactoryImpl.INSTANCE.getGenerator(componentOut);
                    component.copyCurrentStructure(p);
                    component.close();
                    switch(componentType) {
                        case ACE_WEATHER:
                            dataset.addWeather(componentOut.toByteArray());
                            break;
                        case ACE_SOIL:
                            dataset.addSoil(componentOut.toByteArray());
                            break;
                        case ACE_EXPERIMENT:
                            dataset.addExperiment(componentOut.toByteArray());
                            break;
                        default:
                            break;
                    }
                    componentOut.reset();
                }
            }
            t = p.nextToken();
        }
        p.close();
        return dataset;
    }
}
