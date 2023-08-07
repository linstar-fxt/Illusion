package me.linstar.illusion.mixin;

import com.mojang.logging.LogUtils;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.common.util.DirectionUtil;
import me.linstar.illusion.until.Until;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@Mixin(value = ChunkRenderRebuildTask.class, remap = false)
public class ChunkRenderRebuildTaskMixin {

    @Redirect(method = "performBuild",
              at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/pipeline/BlockRenderer;renderModel(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/resources/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;ZJLnet/minecraftforge/client/model/data/IModelData;)Z")
    )
    public boolean renderModel(BlockRenderer renderer, BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed, IModelData modelData){
        try {
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
                    cull = false;
                    break;
                }
            }

            if (state.hasBlockEntity() && Until.isIllusionBlock(pos)) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                BlockState oldState = blockEntity.getBlockState();

                ((BaseBlockStateAccessor) oldState).setCanOcclude(true);

                Minecraft.getInstance().level.getChunk(pos).setBlockState(pos, oldState, true);

                CompoundTag data = blockEntity.getTileData();
                String id = data.getString("Illusion");
                if (!id.isEmpty()) {
                    if (id.equals("NONE")){
                        throw new Exception("");
                    }

                    Block targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
                    if (targetBlock != null) {
                        BlockState targetState = targetBlock.getStateDefinition().getPossibleStates().get(data.getInt("State"));
                        BakedModel targetModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(targetState);

                        return renderer.renderModel(world, targetState, pos, origin, targetModel, buffers, false, seed, modelData);
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
        return renderer.renderModel(world, state, pos, origin, model, buffers, cull, seed, modelData);
    }

}
