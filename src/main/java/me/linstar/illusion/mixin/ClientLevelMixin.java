package me.linstar.illusion.mixin;

import me.linstar.illusion.netwrok.BlockEntityRequestC2SPacket;
import me.linstar.illusion.netwrok.Network;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "onChunkLoaded", at = @At("HEAD"))
    public void onChunkLoaded(ChunkPos chunkPos, CallbackInfo info){
        Network.CHANNEL.send(
                PacketDistributor.SERVER.noArg(),
                new BlockEntityRequestC2SPacket(chunkPos)
        );
    }
}
