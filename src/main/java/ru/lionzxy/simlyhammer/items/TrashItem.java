package ru.lionzxy.simlyhammer.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import ru.lionzxy.simlyhammer.SimplyHammer;
import ru.lionzxy.simlyhammer.interfaces.ITrash;
import ru.lionzxy.simlyhammer.utils.HammerTab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikit on 12.09.2015.
 */
public class TrashItem extends Item implements ITrash {
    public TrashItem() {
        this.setCreativeTab(SimplyHammer.tabGeneral);
        this.setUnlocalizedName("trashitem");
        this.setTextureName("simplyhammer:trashitem");
        this.setMaxStackSize(1);
        GameRegistry.registerItem(this, "trashitem");
    }


    public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
        if (!world.isRemote)
            player.openGui(SimplyHammer.instance, 1, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        return is;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        if (itemStack.hasTagCompound()) {
            if (itemStack.getTagCompound().getBoolean("Invert"))
                list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("trash.Inverted"));
            list.add(StatCollector.translateToLocal("trash.IgnoreList"));
            for (int i = 0; i < itemStack.getTagCompound().getTagList("Items", Constants.NBT.TAG_COMPOUND).tagCount(); ++i) {
                NBTTagCompound item = /*(NBTTagCompound)*/ itemStack.getTagCompound().getTagList("Items", Constants.NBT.TAG_COMPOUND).getCompoundTagAt(i);
                list.add(ItemStack.loadItemStackFromNBT(item).getDisplayName());
            }
        }
    }


    public static boolean isTrash(ItemStack trash, ItemStack itemStack) {
        if (!itemStack.hasTagCompound() || !itemStack.getTagCompound().getBoolean("Trash"))
            return false;
        NBTTagList items = itemStack.getTagCompound().getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound item = /*(NBTTagCompound)*/ items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");

            // Just double-checking that the saved slot index is within our inventory array bounds
            if (slot >= 0 && slot < 9) {
                if (!itemStack.getTagCompound().getBoolean("Invert") && trash.getItem() == ItemStack.loadItemStackFromNBT(item).getItem())
                    return true;
                else if (itemStack.getTagCompound().getBoolean("Invert") && trash.getItem() != ItemStack.loadItemStackFromNBT(item).getItem())
                    return true;
            }
        }
        return false;
    }

    public static void removeTrash(ArrayList<ItemStack> itemStacks, ItemStack itemStack) {
        for (int i = 0; i < itemStacks.size(); i++)
            if (isTrash(itemStacks.get(i), itemStack))
                itemStacks.remove(i);
    }

}
