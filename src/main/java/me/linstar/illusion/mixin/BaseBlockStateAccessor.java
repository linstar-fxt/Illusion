package me.linstar.illusion.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.BlockStateBase.class)
public interface BaseBlockStateAccessor {
    @Mutable
    @Accessor("canOcclude")
    void setCanOcclude(boolean canOcclude);
}
