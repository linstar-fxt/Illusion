package me.linstar.illusion.until;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

        return !entity.getPersistentData().getString("Illusion").isEmpty();
    }

    public static boolean isIllusionBlock(BlockEntity entity){
        if (entity == null){
            return false;
        }

        return !entity.getPersistentData().getString("Illusion").isEmpty();
    }

    public static CompoundTag getIllusionData(BlockPos blockPos){
        if (isIllusionBlock(blockPos)){
            return Minecraft.getInstance().level.getBlockEntity(blockPos).getPersistentData();
        }

        return new CompoundTag();
    }


    public static boolean isSodiumLoaded(){
        for (ModInfo modInfo: FMLLoader.getLoadingModList().getMods()){
            if (modInfo.getModId().equals("rubidium") || modInfo.getModId().equals("embeddium")){
                return true;
            }
        }

        return false;
    }

    public static boolean isIndiumLoaded(){
        for (ModInfo modInfo: FMLLoader.getLoadingModList().getMods()){
            if (modInfo.getModId().equals("lazurite")){
                return true;
            }
        }

        return false;
    }
}
