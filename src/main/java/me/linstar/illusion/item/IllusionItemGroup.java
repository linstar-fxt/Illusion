package me.linstar.illusion.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IllusionItemGroup extends CreativeModeTab {
    public IllusionItemGroup() {
        super("illusion_group");
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(ItemRegister.ILLUSION_CRYSTAL.get());
    }
}
