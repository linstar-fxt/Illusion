package me.linstar.illusion.item;

import me.linstar.illusion.IllusionSavedData;
import me.linstar.illusion.netwrok.BlockEntityIllusionS2CPacket;
import me.linstar.illusion.netwrok.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class IllusionCrystal extends Item {
    public IllusionCrystal(Properties properties) {
        super(properties);
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

        ItemStack stack = player.getOffhandItem();
        String id;

        if (stack.isEmpty()){
            id = "NONE";
            IllusionSavedData.get(level).deleteBlock(chunkPos, pos);
        }else if (stack.getItem() instanceof BlockItem){
            Block block = ((BlockItem) stack.getItem()).getBlock();
            ResourceLocation resourceLocation = ForgeRegistries.BLOCKS.getKey(block);
            if (resourceLocation == null){
                return;
            }
            id = resourceLocation.toString();
        }else {
            return;
        }

        if (!id.equals("NONE")){
            IllusionSavedData.get(level).uploadBlock(chunkPos, pos, id, Vec3.ZERO);
        }

        player.playNotifySound(SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

        Network.CHANNEL.send(
                PacketDistributor.PLAYER.with(()-> player),
                new BlockEntityIllusionS2CPacket(pos, id)
        );

        player.getMainHandItem().setCount(player.getMainHandItem().getCount() - 1);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level p_41422_, List<Component> components, @NotNull TooltipFlag flag) {
        components.add(Component.translatable("text.illusion.illusion_crystal"));
    }
}
