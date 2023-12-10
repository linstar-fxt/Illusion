package me.linstar.illusion.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockState.class)
public abstract class BaseBlockStateMixin extends BlockBehaviour.BlockStateBase{
    @Shadow protected abstract @NotNull BlockState asState();

    protected BaseBlockStateMixin(Block p_60608_, ImmutableMap<Property<?>, Comparable<?>> p_60609_, MapCodec<BlockState> p_60610_) {
        super(p_60608_, p_60609_, p_60610_);
    }

    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockGetter world, @NotNull BlockPos pos){
        if (this.getBlock().defaultBlockState().hasBlockEntity()){
            return Block.box(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        return this.getBlock().getOcclusionShape(this.asState(), world, pos);
    }
}
