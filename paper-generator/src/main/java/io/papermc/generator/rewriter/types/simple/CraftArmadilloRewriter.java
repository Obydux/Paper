package io.papermc.generator.rewriter.types.simple;

import io.papermc.generator.rewriter.types.Types;
import io.papermc.typewriter.preset.SwitchRewriter;
import io.papermc.typewriter.preset.model.CodeBlock;
import io.papermc.typewriter.preset.model.SwitchBody;
import io.papermc.typewriter.preset.model.SwitchCases;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import java.util.ArrayList;
import java.util.List;

public class CraftArmadilloRewriter extends SwitchRewriter {

    @Override
    protected SwitchBody getBody() {
        Armadillo.ArmadilloState[] values = Armadillo.ArmadilloState.values();
        List<SwitchCases> cases = new ArrayList<>(values.length);
        for (Armadillo.ArmadilloState state : values) {
            cases.add(SwitchCases.inlined(state.name(), CodeBlock.of("%s.%s;".formatted(Types.ARMADILLO_STATE.simpleName(), state.name())), true));
        }
        return SwitchBody.of(cases);
    }
}
