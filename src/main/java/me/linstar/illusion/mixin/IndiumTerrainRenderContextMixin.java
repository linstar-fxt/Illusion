package me.linstar.illusion.mixin;

import link.infra.indium.renderer.render.TerrainRenderContext;
import me.linstar.illusion.until.Until;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TerrainRenderContext.class)
public class IndiumTerrainRenderContextMixin {
    @Redirect(method = "tessellateBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 getOffset(BlockState instance, BlockGetter blockGetter, BlockPos pos){
        if (Until.isIllusionBlock(pos)){
            CompoundTag tag = Until.getIllusionData(pos);
            return new Vec3(tag.getDouble("OffsetX"), tag.getDouble("OffsetY"), tag.getDouble("OffsetZ"));
        }
        return instance.getOffset(blockGetter, pos);
    }
}
