package ru.lionzxy.simlyhammer.commons.hammers;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import ru.lionzxy.simlyhammer.commons.items.AddItems;
import ru.lionzxy.simlyhammer.libs.HammerSettings;

/**
 * Created by nikit on 08.09.2015.
 */
public class Aronil98Hammer extends BasicHammer {
    public Aronil98Hammer() {
        this(new HammerSettings("aronil98Hammer", 20, 10, 50));
    }

    public Aronil98Hammer(HammerSettings hammerSettings) {
        super(hammerSettings);
        GameRegistry.registerItem(this, this.hammerSettings.getUnlocalizeName());
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this),
                "zzz", "xyx", " p ",
                'x', "blockDiamond",
                'p', new ItemStack(AddItems.stick, 1, 0),
                'y', new ItemStack(AddItems.stick, 1, 1),
                'z', new ItemStack(Blocks.obsidian)));
    }

    static public boolean isPlayerAutor(EntityPlayer player) {
        if(player.getDisplayName().equalsIgnoreCase("LionZXY") ||
                player.getDisplayName().equalsIgnoreCase("Aronil98"))
            return true;
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
        if(isPlayerAutor(player))
            super.onBlockStartBreak(itemstack, X, Y, Z, player);
        return false;
    }



}
