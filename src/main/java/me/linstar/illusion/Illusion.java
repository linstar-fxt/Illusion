package me.linstar.illusion;

import com.mojang.logging.LogUtils;
import me.linstar.illusion.item.BlockStateTool;
import me.linstar.illusion.item.IllusionCrystal;
import me.linstar.illusion.item.ItemRegister;
import me.linstar.illusion.item.MovementTool;
import me.linstar.illusion.netwrok.Network;
import me.linstar.illusion.until.Until;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(Until.MOD_ID)
public class Illusion {
    private static final Logger LOGGER = LogUtils.getLogger();
    public Illusion() {
        ItemRegister.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event){
        IllusionSavedData.get((ServerLevel) event.getWorld()).deleteBlock(event.getWorld().getChunk(event.getPos()).getPos(), event.getPos());
    }

    //为了取消掉副手的行为
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent.RightClickBlock event){
        ItemStack stack = event.getItemStack();
        if (event.getWorld().isClientSide){
            return;
        }
        if (stack.getItem() == ItemRegister.ILLUSION_CRYSTAL.get()){
            event.setCanceled(true);
            ((IllusionCrystal) stack.getItem()).onUse(new UseOnContext(event.getWorld() ,event.getPlayer(), event.getHand(), event.getItemStack(), event.getHitVec()));
        }else if (stack.getItem() == ItemRegister.MOVEMENT_TOOL.get()){
            event.setCanceled(true);
            ((MovementTool) stack.getItem()).onUse(new UseOnContext(event.getWorld() ,event.getPlayer(), event.getHand(), event.getItemStack(), event.getHitVec()));
        }else if (stack.getItem() == ItemRegister.BLOCK_STATE_TOOL.get()){
            event.setCanceled(true);
            ((BlockStateTool) stack.getItem()).onUse(new UseOnContext(event.getWorld() ,event.getPlayer(), event.getHand(), event.getItemStack(), event.getHitVec()));

        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent.RightClickItem event){
        ItemStack stack = event.getItemStack();
        if (event.getPlayer().isShiftKeyDown()){
            return;
        }
        if (event.getWorld().isClientSide){
            return;
        }
        if (stack.getItem() == ItemRegister.MOVEMENT_TOOL.get()){
            event.setCanceled(true);
            ((MovementTool) stack.getItem()).change(stack);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            event.enqueueWork(()->{
                Network.register();
                DistExecutor.safeRunWhenOn(Dist.CLIENT,
                        (DistExecutor.SafeSupplier<DistExecutor.SafeRunnable>) () -> Network::buildClient);
                DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER,
                        (DistExecutor.SafeSupplier<DistExecutor.SafeRunnable>) () -> Network::buildServer);
            });
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
            event.enqueueWork(()->{
                ItemProperties.register(ItemRegister.MOVEMENT_TOOL.get(), new ResourceLocation(Until.MOD_ID, "state"), (itemstack, world, entity, idk) -> itemstack.getOrCreateTag().getInt("state"));
            });
        }
    }
}
