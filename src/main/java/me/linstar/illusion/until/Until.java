package me.linstar.illusion.until;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Until {
    public static final String MOD_ID = "illusion";

    public static boolean isIllusionBlock(BlockPos pos){
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null){
            return false;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null){
            return false;
        }

        return !entity.getTileData().getString("Illusion").isEmpty();
    }
}
