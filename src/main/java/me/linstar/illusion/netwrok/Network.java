package me.linstar.illusion.netwrok;

import me.linstar.illusion.until.Until;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    public static SimpleChannel CHANNEL;
    public static final String VERSION = "1.0";
    public static void register(){
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Until.MOD_ID, "bec"),
                ()-> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );
    }

    public static void buildServer(){
        CHANNEL.messageBuilder(BlockEntityRequestC2SPacket.class, 0)
                .encoder(BlockEntityRequestC2SPacket::toBytes)
                .decoder(BlockEntityRequestC2SPacket::new)
                .consumerNetworkThread(BlockEntityRequestC2SPacket::handler).add();
        CHANNEL.messageBuilder(BlockEntityIllusionS2CPacket.class, 1)
                .encoder(BlockEntityIllusionS2CPacket::toBytes)
                .decoder(BlockEntityIllusionS2CPacket::new)
                .consumerNetworkThread((paket, ctx) -> {
                    ctx.get().setPacketHandled(true);
                }).add();
    }

    public static void buildClient(){
        CHANNEL.messageBuilder(BlockEntityIllusionS2CPacket.class, 1)
                .encoder(BlockEntityIllusionS2CPacket::toBytes)
                .decoder(BlockEntityIllusionS2CPacket::new)
                .consumerNetworkThread(BlockEntityIllusionS2CPacket::handler).add();

        CHANNEL.messageBuilder(BlockEntityRequestC2SPacket.class, 0)
                .encoder(BlockEntityRequestC2SPacket::toBytes)
                .decoder(BlockEntityRequestC2SPacket::new)
                .consumerNetworkThread(BlockEntityRequestC2SPacket::handler).add();
    }
}
