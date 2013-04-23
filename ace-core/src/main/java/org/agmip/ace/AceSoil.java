package org.agmip.ace;

import java.io.IOException;

public class AceSoil extends AceComponent implements IAceBaseComponent {
    private String sid;
    private AceRecordCollection soilLayers = null;

    public AceSoil(byte[] source) throws IOException {
        super(source);
        this.sid = this.getValue("sid");
    }

    public String getId() {
        return this.sid;
    }
    
    public AceComponentType getType() {
        return AceComponentType.ACE_SOIL;
    }

    public AceRecordCollection getSoilLayers() throws IOException {
        if (this.soilLayers == null) {
            this.soilLayers = this.getRecords("soilLayers");
        }
        return this.soilLayers;
    }
}
