package me.linstar.illusion.item;

import me.linstar.illusion.until.Until;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Until.MOD_ID);
    public static final CreativeModeTab ILLUSION_TAB = new IllusionItemGroup();
    public static final RegistryObject<Item> ILLUSION_CRYSTAL = ITEMS.register("illusion_crystal", ()-> new IllusionCrystal(new Item.Properties().tab(ILLUSION_TAB).stacksTo(64)));;
    public static final RegistryObject<Item> MOVEMENT_TOOL = ITEMS.register("movement_tool", ()-> new MovementTool(new Item.Properties().tab(ILLUSION_TAB).stacksTo(1)));
    public static final RegistryObject<Item> BLOCK_STATE_TOOL = ITEMS.register("block_state_tool", ()-> new BlockStateTool(new Item.Properties().tab(ILLUSION_TAB).stacksTo(1)));
    public static void register(){
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
