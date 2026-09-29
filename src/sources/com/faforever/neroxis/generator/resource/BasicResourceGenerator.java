package com.faforever.neroxis.generator.resource;

import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.DebugUtil;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/resource/BasicResourceGenerator.class */
public class BasicResourceGenerator extends ResourceGenerator {
    @Override // com.faforever.neroxis.generator.resource.ResourceGenerator
    public void placeResources() {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "generateResources", () -> {
            this.mexPlacer.placeMexes(getMexCount(), ((BooleanMask) this.resourceMask.getFinalMask()).subtract(this.mexDeadZone), (BooleanMask) this.waterResourceMask.getFinalMask());
            placeHydros();
        });
    }

    @Override // com.faforever.neroxis.generator.resource.ResourceGenerator
    protected int getMexCount() {
        int mexCount;
        int mapSize = this.generatorParameters.mapSize();
        int spawnCount = this.generatorParameters.spawnCount();
        float mexMultiplier = 1.0f;
        if (spawnCount <= 2) {
            mexCount = (int) (10.0f + (20.0f * this.resourceDensity));
        } else if (spawnCount <= 4) {
            mexCount = (int) (12.0f + (6.0f * this.resourceDensity));
        } else if (spawnCount <= 10) {
            mexCount = (int) (8.0f + (7.0f * this.resourceDensity));
        } else {
            mexCount = (int) (6.0f + (7.0f * this.resourceDensity));
        }
        if (mapSize < 384) {
            mexMultiplier = 0.5f;
        } else if (mapSize >= 768) {
            if (spawnCount <= 4) {
                mexMultiplier = 1.75f;
            } else if (spawnCount <= 6) {
                mexMultiplier = 1.5f;
            } else if (spawnCount <= 10) {
                mexMultiplier = 1.35f;
            } else {
                mexMultiplier = 1.25f;
            }
        }
        return StrictMath.max(StrictMath.round(mexCount * mexMultiplier), 9) * spawnCount;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.resource.ResourceGenerator
    public void setupPipeline() {
        this.resourceMask.init(this.passableLand);
        ((BooleanMask) this.waterResourceMask.init(this.passableLand)).invert();
        this.resourceMask.subtract(this.unbuildable).deflate(4);
        ((BooleanMask) this.resourceMask.fillEdge(16, false)).fillCenter(24, false);
        ((BooleanMask) this.waterResourceMask.subtract(this.unbuildable).deflate(8).fillEdge(16, false)).fillCenter(24, false);
    }
}
