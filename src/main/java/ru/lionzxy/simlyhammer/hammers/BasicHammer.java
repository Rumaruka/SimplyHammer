package ru.lionzxy.simlyhammer.hammers;

import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;
import ru.lionzxy.simlyhammer.SimplyHammer;

import java.util.List;

/**
 * Created by nikit on 30.08.2015.
 */
public class BasicHammer extends Item {
    int breakRadius = 1, breakDepth = 0, oreDictId = 0;
    private Item repairMaterial;
    ToolMaterial toolMaterial;

    public BasicHammer(String name, int breakRadius, int harvestLevel, float speed, int damage, int Enchant, Item repairMaterial) {
        toolMaterial = EnumHelper.addToolMaterial(name, harvestLevel, damage, speed, speed * harvestLevel, Enchant);
        this.setTextureName("simplyhammer:" + name);
        this.setUnlocalizedName(name);
        this.breakRadius = breakRadius;
        this.setCreativeTab(SimplyHammer.tabGeneral);
        this.setMaxDamage(toolMaterial.getMaxUses());
        this.repairMaterial = repairMaterial;
        this.setMaxStackSize(1);
    }

    public BasicHammer(String name, int breakRadius, int harvestLevel, float speed, int damage, int Enchant, int oreDictId) {
        toolMaterial = EnumHelper.addToolMaterial(name, harvestLevel, damage, speed, speed * harvestLevel, Enchant);
        this.setTextureName("simplyhammer:" + name);
        this.setUnlocalizedName(name);
        this.breakRadius = breakRadius;
        this.setCreativeTab(SimplyHammer.tabGeneral);
        this.setMaxDamage(toolMaterial.getMaxUses());
        this.oreDictId = oreDictId;
        this.setMaxStackSize(1);
    }

    public BasicHammer(String name, int breakRadius, int harvestLevel, float speed, int damage, int Enchant) {
        toolMaterial = EnumHelper.addToolMaterial(name, harvestLevel, damage, speed, speed * harvestLevel, Enchant);
        this.setTextureName("simplyhammer:" + name);
        this.setUnlocalizedName(name);
        this.breakRadius = breakRadius;
        this.setCreativeTab(SimplyHammer.tabGeneral);
        this.setMaxDamage(toolMaterial.getMaxUses());
        this.setMaxStackSize(1);
    }

    public boolean checkMaterial(ItemStack itemStack) {
        if (repairMaterial != null)
            return itemStack.getItem() == repairMaterial;
        if (oreDictId != 0) {
            int[] oreIds = OreDictionary.getOreIDs(itemStack);

            for (int i = 0; i < oreIds.length; i++)
                if (oreDictId == oreIds[i])
                    return true;
        }
        return false;
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (isEffective(block, meta))
            return toolMaterial.getEfficiencyOnProperMaterial();

        return 0.3F;
    }

    @Override
    public boolean func_150897_b(Block block) {
        return isEffective(block.getMaterial());
    }

    public boolean isEffective(Block block, int meta) {
        if (this.getHarvestType().equals(block.getHarvestTool(meta)))
            return true;

        else return isEffective(block.getMaterial());
    }

    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
        MovingObjectPosition mop = raytraceFromEntity(player.worldObj, player, false, 4.5d);
        if (mop == null)
            return false;
        int sideHit = mop.sideHit;
        World world = player.worldObj;
        int xRange = breakRadius;
        int yRange = breakRadius;
        int zRange = breakDepth;
        switch (sideHit) {
            case 0:
            case 1:
                yRange = breakDepth;
                zRange = breakRadius;
                break;
            case 2:
            case 3:
                xRange = breakRadius;
                zRange = breakDepth;
                break;
            case 4:
            case 5:
                xRange = breakDepth;
                zRange = breakRadius;
                break;
        }
        for (int xPos = X - xRange; xPos <= X + xRange; xPos++)
            for (int yPos = Y - yRange; yPos <= Y + yRange; yPos++)
                for (int zPos = Z - zRange; zPos <= Z + zRange; zPos++) {
                    // don't break the originally already broken block, duh
                    if (xPos == X && yPos == Y && zPos == Z)
                        continue;

                    if (!super.onBlockStartBreak(itemstack, xPos, yPos, zPos, player))
                        breakExtraBlock(player.worldObj, xPos, yPos, zPos, sideHit, player, X, Y, Z);
                }
        return super.onBlockStartBreak(itemstack, X, Y, Z, player);
    }

    //Right-click
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ) {
        if (world.isRemote)
            return true;

        boolean used = false;
        int hotbarSlot = player.inventory.currentItem;
        int itemSlot = hotbarSlot == 0 ? 8 : hotbarSlot + 1;
        ItemStack nearbyStack = null;

        if (hotbarSlot < 8) {
            nearbyStack = player.inventory.getStackInSlot(itemSlot);
            if (nearbyStack != null) {
                Item item = nearbyStack.getItem();

                if (item instanceof ItemBlock) {
                    int posX = x;
                    int posY = y;
                    int posZ = z;

                    switch (side) {
                        case 0:
                            --posY;
                            break;
                        case 1:
                            ++posY;
                            break;
                        case 2:
                            --posZ;
                            break;
                        case 3:
                            ++posZ;
                            break;
                        case 4:
                            --posX;
                            break;
                        case 5:
                            ++posX;
                            break;
                    }

                    AxisAlignedBB blockBounds = AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX + 1, posY + 1, posZ + 1);
                    AxisAlignedBB playerBounds = player.boundingBox;

                    if (item instanceof ItemBlock) {
                        Block blockToPlace = ((ItemBlock) item).field_150939_a;
                        if (blockToPlace.getMaterial().blocksMovement()) {
                            if (playerBounds.intersectsWith(blockBounds))
                                return false;
                        }
                    }

                    int dmg = nearbyStack.getItemDamage();
                    int count = nearbyStack.stackSize;
                    /*if (item == TinkerTools.openBlocksDevNull)
                    {
                        //Openblocks uses current inventory slot, so we have to do this...
                        player.inventory.currentItem=itemSlot;
                        item.onItemUse(nearbyStack, player, world, x, y, z, side, clickX, clickY, clickZ);
                        player.inventory.currentItem=hotbarSlot;
                        player.swingItem();
                    }
                    else*/
                    used = item.onItemUse(nearbyStack, player, world, x, y, z, side, clickX, clickY, clickZ);

                    // handle creative mode
                    if (player.capabilities.isCreativeMode) {
                        // fun fact: vanilla minecraft does it exactly the same way
                        nearbyStack.setItemDamage(dmg);
                        nearbyStack.stackSize = count;
                    }
                    if (nearbyStack.stackSize < 1) {
                        nearbyStack = null;
                        player.inventory.setInventorySlotContents(itemSlot, null);
                    }
                }
            }
        }
        return used;
    }

    //����� ������� � Tinkers Construct
    protected void breakExtraBlock(World world, int x, int y, int z, int sidehit, EntityPlayer playerEntity, int refX, int refY, int refZ) {
        // prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
        if (world.isAirBlock(x, y, z))
            return;


        // what?
        if (!(playerEntity instanceof EntityPlayerMP))
            return;
        EntityPlayerMP player = (EntityPlayerMP) playerEntity;

        // check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
        // or precious ores you can't harvest while mining stone
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        // only effective materials
        if (!isEffective(block, meta))
            return;

        Block refBlock = world.getBlock(refX, refY, refZ);
        float refStrength = ForgeHooks.blockStrength(refBlock, player, world, refX, refY, refZ);
        float strength = ForgeHooks.blockStrength(block, player, world, x, y, z);

        // only harvestable blocks that aren't impossibly slow to harvest
        if (!ForgeHooks.canHarvestBlock(block, player, meta) || refStrength / strength > 10f)
            return;

        // send the blockbreak event
        BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.getGameType(), player, x, y, z);
        if (event.isCanceled())
            return;
        if (player.capabilities.isCreativeMode) {
            block.onBlockHarvested(world, x, y, z, meta, player);
            if (block.removedByPlayer(world, player, x, y, z, false))
                block.onBlockDestroyedByPlayer(world, x, y, z, meta);

            // send update to client
            if (!world.isRemote) {
                player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
            }
            return;
        }

        // callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
        player.getCurrentEquippedItem().damageItem(1, player);

        // server sided handling
        if (!world.isRemote) {
            // serverside we reproduce ItemInWorldManager.tryHarvestBlock

            // ItemInWorldManager.removeBlock
            block.onBlockHarvested(world, x, y, z, meta, player);

            if (block.removedByPlayer(world, player, x, y, z, true)) // boolean is if block can be harvested, checked above
            {
                block.onBlockDestroyedByPlayer(world, x, y, z, meta);
                block.harvestBlock(world, player, x, y, z, meta);
                block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop());
            }

            // always send block update to client
            player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
        }
        // client sided handling
        else {
            //PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
            // clientside we do a "this clock has been clicked on long enough to be broken" call. This should not send any new packets
            // the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.

            // following code can be found in PlayerControllerMP.onPlayerDestroyBlock
            world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
            if (block.removedByPlayer(world, player, x, y, z, true)) {
                block.onBlockDestroyedByPlayer(world, x, y, z, meta);
            }
            // callback to the tool
            ItemStack itemstack = player.getCurrentEquippedItem();
            if (itemstack != null) {
                itemstack.func_150999_a(world, block, x, y, z, player);

                if (itemstack.stackSize == 0) {
                    player.destroyCurrentEquippedItem();
                }
            }

            // send an update to the server, so we get an update back
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
        }
    }


    public static MovingObjectPosition raytraceFromEntity(World world, Entity player, boolean par3, double range) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f;
        if (!world.isRemote && player instanceof EntityPlayer)
            d1 += 1.62D;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = range;
        if (player instanceof EntityPlayerMP) {
            d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return world.func_147447_a(vec3, vec31, par3, !par3, par3);
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap multimap = super.getAttributeModifiers(stack);
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double) toolMaterial.getDamageVsEntity(), 0));
        return multimap;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        list.add("RightClick for place torch");
        list.add("=================================");
        list.add("Uses left: " + (toolMaterial.getMaxUses() - itemStack.getItemDamage()) + " Blocks");
        list.add("Harvest Level: " + toolMaterial.getHarvestLevel());
        list.add("Repair material: " + getRepairMaterial().getDisplayName());
        list.add("Efficiency: " + toolMaterial.getEfficiencyOnProperMaterial());
    }


    public int getItemEnchantability() {
        return toolMaterial.getEnchantability();
    }

    public ItemStack getRepairMaterial() {
        if (repairMaterial != null)
            return new ItemStack(repairMaterial);
        if (oreDictId != 0)
            return OreDictionary.getOres(OreDictionary.getOreName(oreDictId)).get(0);
        return new ItemStack(Items.stick);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        // invalid query or wrong toolclass
        if (toolClass == null || !this.getHarvestType().equals(toolClass))
            return -1;

        // tadaaaa
        return toolMaterial.getHarvestLevel();
    }

    protected String getHarvestType() {
        return "pickaxe";
    }

    public boolean isEffective(Material material) {
        for (Material m : getEffectiveMaterials())
            if (m == material)
                return true;

        return false;
    }

    protected Material[] getEffectiveMaterials() {
        return materials;
    }

    static Material[] materials = new Material[]{Material.rock, Material.iron, Material.ice, Material.glass, Material.piston, Material.anvil};

}
