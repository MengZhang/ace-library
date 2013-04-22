package org.agmip.ace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AceDataset {
    List<AceWeather> weathers;
    List<AceSoil> soils;
    List<AceExperiment> experiments;

    public AceDataset() {
        this.weathers = new ArrayList<AceWeather>();
        this.soils    = new ArrayList<AceSoil>();
        this.experiments = new ArrayList<AceExperiment>();
    }

    public void addWeather(byte[] source) throws IOException {
        AceWeather weather = new AceWeather(source);
        this.weathers.add(weather);
    }

    public void addSoil(byte[] source) throws IOException {
        AceSoil soil = new AceSoil(source);
        this.soils.add(soil);
    }

    public void addExperiment(byte[] source) throws IOException {
        AceExperiment experiment = new AceExperiment(source);
        this.experiments.add(experiment);
    }

    public List<AceWeather> getWeathers() {
        return this.weathers;
    }

    public List<AceSoil> getSoils() {
        return this.soils;
    }

    public List<AceExperiment> getExperiments() {
        return this.experiments;
    }
}
