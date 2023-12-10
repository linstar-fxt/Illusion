package me.linstar.illusion;

import me.linstar.illusion.netwrok.BlockEntityIllusionS2CPacket;
import me.linstar.illusion.netwrok.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class IllusionSavedData extends SavedData {
    private static final String NAME = "IllusionData";
    private CompoundTag tag = new CompoundTag();

    public IllusionSavedData(){

    }

    public IllusionSavedData(CompoundTag compoundTag){
        tag = compoundTag.getCompound(NAME);
    }


    public void uploadBlock(ChunkPos chunkPos, BlockPos blockPos, String id, Vec3 offset){
        updateBlockTag(chunkPos, blockPos, new CompoundTag(){{
            putString("id", id);
            putDouble("OffsetX", offset.x);
            putDouble("OffsetY", offset.y);
            putDouble("OffsetZ", offset.z);
            putInt("State", 0);
        }});
    }

    public void updateBlockTag(ChunkPos chunkPos, BlockPos blockPos, CompoundTag compoundTag){
        CompoundTag blocks = tag.getCompound(chunkPos.toString());
        blocks.put(blockPos.toShortString(), compoundTag);
        tag.put(chunkPos.toString(), blocks);

        setDirty();
    }

    public CompoundTag getBlock(ChunkPos chunkPos, BlockPos pos){
        return tag.getCompound(chunkPos.toString()).getCompound(pos.toShortString());
    }

    public void deleteBlock(ChunkPos chunkPos, BlockPos pos){
        if (!tag.contains(chunkPos.toString())){
            return;
        }

        if (!tag.getCompound(chunkPos.toString()).contains(pos.toShortString())){
            return;
        }

        tag.getCompound(chunkPos.toString()).remove(pos.toShortString());
        setDirty();
    }

    public void sendPackets(ChunkPos chunkPos, ServerPlayer player){
        for(String key: tag.getCompound(chunkPos.toString()).getAllKeys()){
            CompoundTag blockTag = tag.getCompound(chunkPos.toString()).getCompound(key);
            String id = blockTag.getString("id");

            String[] strings = key.split(", ");
            int x = Integer.parseInt(strings[0]);
            int y = Integer.parseInt(strings[1]);
            int z = Integer.parseInt(strings[2]);

            double ox = blockTag.getDouble("OffsetX");
            double oy = blockTag.getDouble("OffsetY");
            double oz = blockTag.getDouble("OffsetZ");

            int state = blockTag.getInt("State");

            Network.CHANNEL.send(
                    PacketDistributor.PLAYER.with(()-> player),
                    new BlockEntityIllusionS2CPacket(new BlockPos(x, y, z), id, new Vec3(ox, oy, oz), state)
            );
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put(NAME, tag);
        return compoundTag;
    }

    public static IllusionSavedData get(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(IllusionSavedData::new, IllusionSavedData::new, NAME);
    }
}
