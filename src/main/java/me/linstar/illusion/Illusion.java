package me.linstar.illusion;

import me.linstar.illusion.item.BlockStateTool;
import me.linstar.illusion.item.IllusionCrystal;
import me.linstar.illusion.item.ItemRegister;
import me.linstar.illusion.item.MovementTool;
import me.linstar.illusion.netwrok.Network;
import me.linstar.illusion.until.Until;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Until.MOD_ID)
public class Illusion {
    public static boolean SODIUM_ENVIRONMENT = ModList.get().isLoaded("embeddium") || ModList.get().isLoaded("rubidium");
    public Illusion() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegister.ITEMS.register(bus);
        ItemRegister.TABS.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event){
        IllusionSavedData.get((ServerLevel) event.getLevel()).deleteBlock(event.getLevel().getChunk(event.getPos()).getPos(), event.getPos());
    }

    //为了取消掉副手的行为
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent.RightClickBlock event){
        Item currentItem = event.getItemStack().getItem();
        if (event.getLevel().isClientSide){
            return;
        }
        if (currentItem == ItemRegister.ILLUSION_CRYSTAL.get()){
            event.setCanceled(true);
            ((IllusionCrystal) currentItem).onUse(new UseOnContext(event.getLevel() ,event.getEntity(), event.getHand(), event.getItemStack(), event.getHitVec()));
        }else if (currentItem == ItemRegister.MOVEMENT_TOOL.get()){
            event.setCanceled(true);
            ((MovementTool) currentItem).onUse(new UseOnContext(event.getLevel() ,event.getEntity(), event.getHand(), event.getItemStack(), event.getHitVec()));
        }else if (currentItem == ItemRegister.BLOCK_STATE_TOOL.get()){
            event.setCanceled(true);
            ((BlockStateTool) currentItem).onUse(new UseOnContext(event.getLevel() ,event.getEntity(), event.getHand(), event.getItemStack(), event.getHitVec()));

        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent.RightClickItem event){
        ItemStack stack = event.getItemStack();
        Item currentItem = stack.getItem();
        if (event.getEntity().isShiftKeyDown()){
            return;
        }
        if (event.getLevel().isClientSide){
            return;
        }
        if (currentItem == ItemRegister.MOVEMENT_TOOL.get()){
            event.setCanceled(true);
            ((MovementTool) currentItem).change(stack);
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
            event.enqueueWork(()-> ItemProperties.register(ItemRegister.MOVEMENT_TOOL.get(), new ResourceLocation(Until.MOD_ID, "state"), (itemstack, world, entity, idk) -> itemstack.getOrCreateTag().getInt("state")));
        }
    }
}
