package me.linstar.illusion.mixin;

import com.mojang.logging.LogUtils;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.linstar.illusion.until.Until;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChunkBuilderMeshingTask.class, remap = false)
public class SodiumChunkBuilderMeshingTaskMixin {
    @Redirect(method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;update(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/resources/model/BakedModel;JLnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"))
    public void onUpdate(BlockRenderContext context, BlockPos pos, BlockPos origin, BlockState state, BakedModel model, long seed, ModelData modelData, RenderType renderLayer){
        try {
            WorldSlice world = context.world();

            if (state.hasBlockEntity() && Until.isIllusionBlock(pos)) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                BlockState oldState = blockEntity.getBlockState();

                ((BaseBlockStateAccessor) oldState).setCanOcclude(true);

                Minecraft.getInstance().level.getChunk(pos).setBlockState(pos, oldState, true);

                CompoundTag data = blockEntity.getPersistentData();
                String id = data.getString("Illusion");
                if (!id.isEmpty()) {
                    if (id.equals("NONE")){
                        throw new Exception("");
                    }

                    Block targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
                    if (targetBlock != null) {
                        BlockState targetState = targetBlock.getStateDefinition().getPossibleStates().get(data.getInt("State"));
                        BakedModel targetModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(targetState);

                        context.update(pos, origin, targetState, targetModel, seed, modelData, renderLayer);
                        return;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.getLogger().info(e.getMessage());
            for (StackTraceElement stackTraceElement: e.getStackTrace()){
                LogUtils.getLogger().info(stackTraceElement.toString());
            }
        }

        context.update(pos, origin, state, model, seed, modelData, renderLayer);
    }
}
