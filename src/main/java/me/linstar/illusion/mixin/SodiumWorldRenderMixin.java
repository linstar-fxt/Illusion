package me.linstar.illusion.mixin;

import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.linstar.illusion.netwrok.BlockEntityRequestC2SPacket;
import me.linstar.illusion.netwrok.Network;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSectionManager.class, remap = false)
public class SodiumWorldRenderMixin {
    @Inject(method = "onChunkAdded", at = @At("HEAD"))
    public void onChunkAdded(int x, int z, CallbackInfo ci){
                Network.CHANNEL.send(
                PacketDistributor.SERVER.noArg(),
                new BlockEntityRequestC2SPacket(new ChunkPos(x, z))
        );
    }
}
