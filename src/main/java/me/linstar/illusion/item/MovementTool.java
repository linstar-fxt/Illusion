package me.linstar.illusion.item;

import me.linstar.illusion.IllusionSavedData;
import me.linstar.illusion.netwrok.BlockEntityIllusionS2CPacket;
import me.linstar.illusion.netwrok.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class MovementTool extends Item {

    public MovementTool(Properties properties) {
        super(properties);
    }
    public void change(ItemStack stack){
        int state = stack.getOrCreateTag().getInt("state");
        stack.getOrCreateTag().putInt("state", changeState(state));
        stack.setHoverName(Component.translatable("item.illusion.movement_tool.state" + changeState(state)));
    }

    public void onUse(UseOnContext context){
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        if (player == null){
            return;
        }
        ServerLevel level = player.serverLevel();
        BlockPos pos = context.getClickedPos();
        ChunkPos chunkPos = level.getChunkAt(pos).getPos();
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null){
            return;
        }

        CompoundTag blockTag = IllusionSavedData.get(level).getBlock(chunkPos, pos);
        if (blockTag.isEmpty()){
            return;
        }

        switch (context.getItemInHand().getOrCreateTag().getInt("state")) {
            case 0 ->
                    blockTag.putDouble("OffsetX", blockTag.getDouble("OffsetX") + (player.isShiftKeyDown() ? -0.1 : 0.1));
            case 1 ->
                    blockTag.putDouble("OffsetY", blockTag.getDouble("OffsetY") + (player.isShiftKeyDown() ? -0.1 : 0.1));
            case 2 ->
                    blockTag.putDouble("OffsetZ", blockTag.getDouble("OffsetZ") + (player.isShiftKeyDown() ? -0.1 : 0.1));
        }

        IllusionSavedData.get(level).updateBlockTag(chunkPos, pos, blockTag);

        player.playNotifySound(SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 1, 1);

        Network.CHANNEL.send(
                PacketDistributor.PLAYER.with(()-> player),
                new BlockEntityIllusionS2CPacket(pos, blockTag)
        );
    }

    private int changeState(int state){
        if (state == 2){
            state = 0;
            return state;
        }

        state ++;

        return state;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level p_41422_, List<Component> components, @NotNull TooltipFlag flag) {
        components.add(Component.translatable("text.illusion.movement_tool"));
    }
}
