package me.linstar.illusion.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.linstar.illusion.until.Until;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherMixin {
    @Shadow @Final private ModelBlockRenderer modelRenderer;

    @Inject(method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderBatched(BlockState state, BlockPos pos, BlockAndTintGetter region, PoseStack poseStack, VertexConsumer builder, boolean culling, RandomSource randomSource, ModelData modelData, RenderType renderType, CallbackInfo info){
        BlockPos[] blocksAround = new BlockPos[]{
                pos.offset(0, 1, 0),
                pos.offset(0, -1, 0),
                pos.offset(-1, 0, 0),
                pos.offset(1, 0, 0),
                pos.offset(0, 0, 1),
                pos.offset(0, 0, -1)
        };

        //检测周围是否有幻化方块 如果有则取消面数剔除
        for (BlockPos blockPos: blocksAround){
            if (Until.isIllusionBlock(blockPos)){
                culling = false;
                break;
            }
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        if (Until.isIllusionBlock(pos)){
            BlockEntity blockEntity = region.getBlockEntity(pos);
            ClientLevel level = Minecraft.getInstance().level;

            if (blockEntity != null && level != null){

                ((BaseBlockStateAccessor) state).setCanOcclude(true);
                level.getChunk(pos).setBlockState(pos, state, true);

                CompoundTag tag = blockEntity.getPersistentData();
                String id = tag.getString("Illusion");

                if (!id.equals("NONE")){

                    Block targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
                    if (targetBlock != null) {
                        BlockState targetState = targetBlock.getStateDefinition().getPossibleStates().get(tag.getInt("State"));
                        BakedModel targetModel = dispatcher.getBlockModel(targetState);

                        poseStack.translate(tag.getDouble("OffsetX"), tag.getDouble("OffsetY"), tag.getDouble("OffsetZ"));

                        this.modelRenderer.tesselateBlock(region, targetModel, targetState, pos, poseStack, builder, culling, randomSource, targetState.getSeed(pos), OverlayTexture.NO_OVERLAY, modelData, renderType);
                        info.cancel();
                        return;
                    }
                }
            }
        }

        //原版渲染方法
        try {
            RenderShape rendershape = state.getRenderShape();
            if (rendershape == RenderShape.MODEL) {
                this.modelRenderer.tesselateBlock(region, dispatcher.getBlockModel(state), state, pos, poseStack, builder, culling, randomSource, state.getSeed(pos), OverlayTexture.NO_OVERLAY, modelData, renderType);
            }

        } catch (Throwable ignored) {

        }
    }
}
