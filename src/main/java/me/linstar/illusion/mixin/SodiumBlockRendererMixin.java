package me.linstar.illusion.mixin;

import me.jellysquid.mods.sodium.client.model.light.LightMode;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import me.linstar.illusion.until.Until;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockRenderer.class)
public abstract class SodiumBlockRendererMixin {

    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 getOffset(BlockState state, BlockGetter blockGetter, BlockPos pos){
        if (!Until.isIllusionBlock(pos)){
            return state.getOffset(blockGetter, pos);
        }

        CompoundTag data  = Minecraft.getInstance().level.getBlockEntity(pos).getTileData();
        if (data.contains("OffsetX")){
            return new Vec3(
                    data.getDouble("OffsetX"),
                    data.getDouble("OffsetY"),
                    data.getDouble("OffsetZ"));
        }

        return state.getOffset(blockGetter, pos);
    }
}
