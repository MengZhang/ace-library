package org.agmip.ace.parser.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceComponent {
    private static final Logger log = LoggerFactory.getLogger(AceComponent.class);
    protected byte[] component;

    public AceComponent(){}

    public AceComponent(byte[] component) {
        this.component = component;
    }

    public byte[] getRawComponent() {
        return this.component;
    }

    public JsonParser getParser() throws IOException {
        return JsonFactoryImpl.INSTANCE.getParser(this.component);
    }

    public JsonGenerator getGenerator() throws IOException {
        return JsonFactoryImpl.INSTANCE.getGenerator(new ByteArrayOutputStream());
    }

    public JsonGenerator getGenerator(OutputStream stream) throws IOException {
        return JsonFactoryImpl.INSTANCE.getGenerator(stream);
    }

    public String getValueOr(String key, String alternateValue) throws IOException {
        String value = this.getValue(key);
        if (value == null) {
            return alternateValue;
        } else {
            return value;
        }
    }

    public String getValue(String key) throws IOException {
        JsonParser p = this.getParser();
        JsonToken t;

        t = p.nextToken();

        while (t != null) {
            if (t == JsonToken.FIELD_NAME && p.getCurrentName().equals(key)) {
                String value = p.nextTextValue();
                p.close();
                return value;
            }
            t = p.nextToken();
        }
        p.close();
        return null;
    }

    public byte[] getRawRecords(String key) throws IOException {
        JsonParser p = this.getParser();
        JsonToken t;

        t = p.nextToken();

        while (t != null) {
            if (t == JsonToken.FIELD_NAME && p.getCurrentName().equals(key)) {
                t = p.nextToken();
                if (p.isExpectedStartArrayToken()) {
                    JsonGenerator g = this.getGenerator();
                    g.copyCurrentStructure(p);
                    g.flush();
                    byte[] subcomponent = ((ByteArrayOutputStream)g.getOutputTarget()).toByteArray();
                    g.close();
                    p.close();
                    return subcomponent;
                } else {
                    log.error("Key {} does not start an array.", key);
                    return new byte[0];
                }
            }
            t = p.nextToken();
        }
        log.error("Did not find key: {}", key);
        return new byte[0];
    }

    public AceRecordCollection getRecords(String key) throws IOException {
        return new AceRecordCollection(this.getRawRecords(key));
    }

    public byte[] getRawSubcomponent(String key) throws IOException {
        JsonParser p = this.getParser();
        JsonToken t = p.nextToken();

        while (t != null) {
            if (t == JsonToken.FIELD_NAME && p.getCurrentName().equals(key)) {
                t = p.nextToken();
                if (t == JsonToken.START_OBJECT) {
                    JsonGenerator g = this.getGenerator();
                    g.copyCurrentStructure(p);
                    g.flush();
                    byte[] subcomponent = ((ByteArrayOutputStream)g.getOutputTarget()).toByteArray();
                    g.close();
                    p.close();
                    return subcomponent;
                } else {
                    log.error("Key {} does not start an object.", key);
                    return new byte[0];
                }
            }
            t = p.nextToken();
        }
        log.error("Did not find key: {}", key);
        return new byte[0];
    }

    public AceComponent getSubcomponent(String key) throws IOException {
        return new AceComponent(this.getRawSubcomponent(key));
    }

    public String toString() {
        return new String(this.component);
    }

    public boolean checkProperty(String propertyKey) throws IOException {
        if (! propertyKey.startsWith("*") && ! propertyKey.endsWith("*")) {
            propertyKey = "*"+propertyKey+"*";
        }
        String propertyValue = this.getValue(propertyKey);
        if (propertyValue != null) {
            if (propertyValue.toUpperCase().startsWith("T") ||
                propertyValue.toUpperCase().startsWith("Y")) {
                return true;
            }
        }
        return false;
    }
}
