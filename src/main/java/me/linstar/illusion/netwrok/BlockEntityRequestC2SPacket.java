package me.linstar.illusion.netwrok;

import me.linstar.illusion.IllusionSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class BlockEntityRequestC2SPacket {
    private final ChunkPos pos;

    public BlockEntityRequestC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readChunkPos();
    }

    public BlockEntityRequestC2SPacket(ChunkPos pos) {
        this.pos = pos;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeChunkPos(this.pos);
    }
    public void handler(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer player = ctx.get().getSender();
            if (player == null){
                return;
            }
            IllusionSavedData.get(player.getLevel()).sendPackets(pos, player);
        });
        ctx.get().setPacketHandled(true);
    }
}
