package me.linstar.illusion.item;

import com.google.common.collect.ImmutableList;
import me.linstar.illusion.IllusionSavedData;
import me.linstar.illusion.netwrok.BlockEntityIllusionS2CPacket;
import me.linstar.illusion.netwrok.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockStateTool extends Item {
    public BlockStateTool(Properties properties) {
        super(properties);
    }

    public void onUse(UseOnContext context){
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ChunkPos chunkPos = level.getChunkAt(pos).getPos();
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null){
            return;
        }

        IllusionSavedData savedData = IllusionSavedData.get((ServerLevel) level);

        CompoundTag blockTag = savedData.getBlock(chunkPos, pos);
        if (blockTag.isEmpty()){
            return;
        }

        Block targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockTag.getString("id")));
        if (targetBlock == null){
            return;
        }
        ImmutableList<BlockState> blockStates = targetBlock.getStateDefinition().getPossibleStates();
        int lastState = blockTag.getInt("State");
        if (lastState + 1 == blockStates.size()){
            lastState = 0;
        }else {
            lastState ++;
        }

        blockTag.putInt("State", lastState);

        savedData.updateBlockTag(chunkPos, pos, blockTag);

        ServerPlayer serverPlayer = (ServerPlayer) context.getPlayer();

        if (serverPlayer == null){
            return;
        }

        serverPlayer.playNotifySound(SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 1, 1);
        serverPlayer.sendSystemMessage(Component.translatable("text.illusion.block_state_tool", lastState), true);

        Network.CHANNEL.send(
                PacketDistributor.PLAYER.with(()-> serverPlayer),
                new BlockEntityIllusionS2CPacket(pos, blockTag)
        );
    }
}
