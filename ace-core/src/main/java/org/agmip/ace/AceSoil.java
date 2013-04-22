package org.agmip.ace;

import java.io.IOException;

public class AceSoil extends AceComponent {
    private String sid;
    private AceRecordCollection soilLayers = null;

    public AceSoil(byte[] source) throws IOException {
        super(source);
        this.sid = this.getValue("sid");
    }

    public String getId() {
        return this.sid;
    }

    public AceRecordCollection getSoilLayers() throws IOException {
        if (this.soilLayers == null) {
            this.soilLayers = this.getRecords("soilLayers");
        }
        return this.soilLayers;
    }
}
