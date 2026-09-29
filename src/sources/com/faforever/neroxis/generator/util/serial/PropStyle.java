package com.faforever.neroxis.generator.util.serial;

import com.faforever.neroxis.generator.ParameterConstraints;
import com.faforever.neroxis.generator.prop.BasicPropGenerator;
import com.faforever.neroxis.generator.prop.BoulderFieldPropGenerator;
import com.faforever.neroxis.generator.prop.EnemyCivPropGenerator;
import com.faforever.neroxis.generator.prop.ForrestSomethingPropGenerator;
import com.faforever.neroxis.generator.prop.HighReclaimPropGenerator;
import com.faforever.neroxis.generator.prop.LargeBattlePropGenerator;
import com.faforever.neroxis.generator.prop.NavyWrecksPropGenerator;
import com.faforever.neroxis.generator.prop.NeutralCivPropGenerator;
import com.faforever.neroxis.generator.prop.PropGenerator;
import com.faforever.neroxis.generator.prop.RockFieldPropGenerator;
import com.faforever.neroxis.generator.prop.SmallBattlePropGenerator;
import com.faforever.neroxis.generator.util.HasParameterConstraints;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.function.Supplier;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/PropStyle.class */
public enum PropStyle implements HasParameterConstraints {
    BASIC(BasicPropGenerator::new, ParameterConstraints.ANY),
    BOULDER_FIELD(BoulderFieldPropGenerator::new, ParameterConstraints.ANY),
    ENEMY_CIV(EnemyCivPropGenerator::new, ParameterConstraints.ANY),
    HIGH_RECLAIM(HighReclaimPropGenerator::new, ParameterConstraints.ANY),
    LARGE_BATTLE(LargeBattlePropGenerator::new, ParameterConstraints.ANY),
    NAVY_WRECKS(NavyWrecksPropGenerator::new, ParameterConstraints.ANY),
    NEUTRAL_CIV(NeutralCivPropGenerator::new, ParameterConstraints.ANY),
    ROCK_FIELD(RockFieldPropGenerator::new, ParameterConstraints.ANY),
    SMALL_BATTLE(SmallBattlePropGenerator::new, ParameterConstraints.ANY),
    FORREST_SOMETHING(ForrestSomethingPropGenerator::new, ParameterConstraints.ANY);

    private final Supplier<PropGenerator> generatorSupplier;
    private final ParameterConstraints parameterConstraints;

    @Generated
    PropStyle(final Supplier generatorSupplier, final ParameterConstraints parameterConstraints) {
        this.generatorSupplier = generatorSupplier;
        this.parameterConstraints = parameterConstraints;
    }

    @Generated
    public Supplier<PropGenerator> getGeneratorSupplier() {
        return this.generatorSupplier;
    }

    @Generated
    public ParameterConstraints getParameterConstraints() {
        return this.parameterConstraints;
    }

    @Override // com.faforever.neroxis.generator.util.HasParameterConstraints
    @JsonIgnore
    public ParameterConstraints parameterConstraints() {
        return this.parameterConstraints;
    }
}
