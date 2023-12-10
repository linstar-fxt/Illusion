package me.linstar.illusion.mixin;

import link.infra.indium.renderer.render.BlockRenderInfo;
import me.linstar.illusion.until.Until;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockRenderInfo.class, remap = false)
public class IndiumBlockRenderInfoMixin {
    @Shadow public BlockPos blockPos;

    @Inject(method = "shouldDrawFace", at = @At("HEAD"), cancellable = true)
    public void shouldDrawFace(Direction face, CallbackInfoReturnable<Boolean> info){
        BlockPos pos = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (Until.isIllusionBlock(pos.offset(new BlockPos(face.getNormal()))) || Until.isIllusionBlock(blockPos)){
            info.setReturnValue(true);
        }
    }
}
