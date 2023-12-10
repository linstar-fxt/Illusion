package me.linstar.illusion.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.linstar.illusion.until.Until;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    @Inject(method = "setupAndRender", at = @At("HEAD"), cancellable = true)
    private static <T extends BlockEntity> void setupAndRender(BlockEntityRenderer<T> renderer, T blockEntity, float p_112287_, PoseStack p_112288_, MultiBufferSource p_112289_, CallbackInfo info){
        if (Until.isIllusionBlock(blockEntity) && !blockEntity.getPersistentData().getString("Illusion").equals("NONE")){
            info.cancel();
        }
    }
}
