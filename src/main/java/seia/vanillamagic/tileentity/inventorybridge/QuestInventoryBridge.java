package seia.vanillamagic.tileentity.inventorybridge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.api.exception.NotInventoryException;
import seia.vanillamagic.handler.CustomTileEntityHandler;
import seia.vanillamagic.magic.wand.WandRegistry;
import seia.vanillamagic.quest.Quest;
import seia.vanillamagic.util.EntityUtil;
import seia.vanillamagic.util.ItemStackUtil;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class QuestInventoryBridge extends Quest {
	/**
	 * ItemStack required in left hand to create {@link TileInventoryBridge}
	 */
	public final ItemStack requiredLeftHand = new ItemStack(Blocks.STAINED_GLASS);

	@SubscribeEvent
	public void onRightClick(RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack rightHand = player.getHeldItemMainhand();
		ItemStack leftHand = player.getHeldItemOffhand();

		if (ItemStackUtil.isNullStack(rightHand)
				|| !WandRegistry.areWandsEqual(WandRegistry.WAND_BLAZE_ROD.getWandStack(), rightHand)
				|| ItemStackUtil.isNullStack(leftHand) || !ItemStack.areItemsEqual(leftHand, requiredLeftHand)) {
			return;
		}

		if (!player.isSneaking()) {
			return;
		}

		World world = event.getWorld();
		BlockPos clickedPos = event.getPos();
		TileEntity clickedInventory = world.getTileEntity(clickedPos);

		if ((clickedInventory == null) || !(clickedInventory instanceof IInventory)) {
			return;
		}

		this.checkQuestProgress(player);

		if (!hasQuest(player)) {
			return;
		}

		TileInventoryBridge tile = new TileInventoryBridge();
		tile.init(player.world, clickedPos.offset(EnumFacing.UP));

		try {
			tile.setPositionFromSelector(player);
		} catch (NotInventoryException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println(e.position.toString());
			return;
		}

		try {
			tile.setOutputInventory(world, clickedPos);
		} catch (NotInventoryException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println(e.position.toString());
			return;
		}

		if (!CustomTileEntityHandler.addCustomTileEntity(tile, player.dimension)) {
			return;
		}

		EntityUtil.addChatComponentMessageNoSpam(player, tile.getClass().getSimpleName() + " added");
		ItemStackUtil.decreaseStackSize(leftHand, 1);

		if (ItemStackUtil.getStackSize(leftHand) != 0) {
			return;
		}

		player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
	}

	@SubscribeEvent
	public void onBridgeDestroyed(BreakEvent event) {
		BlockPos inventoryPos = event.getPos();
		World world = event.getWorld();
		TileEntity inventoryTile = world.getTileEntity(inventoryPos);

		if (!(inventoryTile instanceof IInventory)) {
			return;
		}

		BlockPos customTilePos = inventoryPos.offset(EnumFacing.UP);
		CustomTileEntityHandler.removeCustomTileEntityAndSendInfoToPlayer(world, customTilePos, event.getPlayer());
	}
}