package me.linstar.illusion.item;

import me.linstar.illusion.until.Until;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Until.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Until.MOD_ID);
    public static final RegistryObject<Item> ILLUSION_CRYSTAL = ITEMS.register("illusion_crystal", ()-> new IllusionCrystal(new Item.Properties().stacksTo(64)));;
    public static final RegistryObject<Item> MOVEMENT_TOOL = ITEMS.register("movement_tool", ()-> new MovementTool(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLOCK_STATE_TOOL = ITEMS.register("block_state_tool", ()-> new BlockStateTool(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> ILLUSION_TAB = TABS.register("illusion_group", ()-> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.illusion_group"))
            .icon(() -> new ItemStack(ILLUSION_CRYSTAL.get()))
            .displayItems(((p_270258_, output) -> {
                output.accept(ILLUSION_CRYSTAL.get());
                output.accept(MOVEMENT_TOOL.get());
                output.accept(BLOCK_STATE_TOOL.get());
            }))
            .build());
}
