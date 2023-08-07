package me.linstar.illusion.netwrok;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class BlockEntityIllusionS2CPacket {
    private final BlockPos pos;
    private final String id;
    private final Vec3 offset;
    private final int state;

    public BlockEntityIllusionS2CPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        id = buffer.readUtf();
        offset = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        state = buffer.readInt();
    }

    public BlockEntityIllusionS2CPacket(BlockPos pos, String id) {
        this.pos = pos;
        this.id = id;
        this.offset = Vec3.ZERO;
        this.state = 0;
    }

    public BlockEntityIllusionS2CPacket(BlockPos pos, String id, Vec3 offset) {
        this.pos = pos;
        this.id = id;
        this.offset = offset;
        this.state = 0;
    }

    public BlockEntityIllusionS2CPacket(BlockPos pos, String id, Vec3 offset, int state) {
        this.pos = pos;
        this.id = id;
        this.offset = offset;
        this.state = state;
    }

    public BlockEntityIllusionS2CPacket(BlockPos pos, CompoundTag tag){
        this.pos = pos;
        this.id = tag.getString("id");
        this.offset = new Vec3(tag.getDouble("OffsetX"), tag.getDouble("OffsetY"), tag.getDouble("OffsetZ"));
        this.state = tag.getInt("State");
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeUtf(id);
        buf.writeDouble(offset.x);
        buf.writeDouble(offset.y);
        buf.writeDouble(offset.z);
        buf.writeInt(state);
    }
    public void handler(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if (Minecraft.getInstance().level == null) {
                return;
            }

            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
            if (blockEntity == null){
                return;
            }

            blockEntity.getTileData().putString("Illusion", id);
            blockEntity.getTileData().putDouble("OffsetX", offset.x);
            blockEntity.getTileData().putDouble("OffsetY", offset.y);
            blockEntity.getTileData().putDouble("OffsetZ", offset.z);
            blockEntity.getTileData().putInt("State", state);

            ClientLevel level = Minecraft.getInstance().level;
            ChunkPos chunkPos = level.getChunkAt(pos).getPos();
            for(int y = level.getMinSection(); y < level.getMaxSection(); ++y){
                SodiumWorldRenderer.instance().scheduleRebuildForChunk(chunkPos.x, y, chunkPos.z, false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
