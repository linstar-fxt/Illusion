package me.linstar.illusion.mixin;

import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.linstar.illusion.until.Until;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockRenderer.class, remap = false)
public class SodiumBlockRenderMixin {
    @Shadow @Final private BlockOcclusionCache occlusionCache;

    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasOffsetFunction()Z"), remap = true)
    public boolean hasOffsetFunction(BlockState instance){
        return true;
    }

    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"), remap = true)
    public Vec3 getOffset(BlockState instance, BlockGetter blockGetter, BlockPos pos){
        if (Until.isIllusionBlock(pos)){
            CompoundTag tag = Until.getIllusionData(pos);
            return new Vec3(tag.getDouble("OffsetX"), tag.getDouble("OffsetY"), tag.getDouble("OffsetZ"));
        }
        return instance.getOffset(blockGetter, pos);
    }

    //确保幻形方块附近不进行面剔除
    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;isFaceVisible(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lnet/minecraft/core/Direction;)Z"))
    public boolean isFaceVisible(BlockRenderer instance, BlockRenderContext ctx, Direction face){
        BlockPos pos = ctx.pos();
        if ((face != null && Until.isIllusionBlock(pos.offset(new BlockPos(face.getNormal())))) || Until.isIllusionBlock(pos)){
            return true;
        }

        return this.occlusionCache.shouldDrawSide(ctx.state(), ctx.localSlice(), ctx.pos(), face);
    }
}
