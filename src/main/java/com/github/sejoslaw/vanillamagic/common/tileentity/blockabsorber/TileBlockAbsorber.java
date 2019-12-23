package com.github.sejoslaw.vanillamagic.tileentity.blockabsorber;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import com.github.sejoslaw.vanillamagic.api.event.EventBlockAbsorber;
import com.github.sejoslaw.vanillamagic.api.tileentity.blockabsorber.IBlockAbsorber;
import com.github.sejoslaw.vanillamagic.inventory.InventoryHelper;
import com.github.sejoslaw.vanillamagic.tileentity.CustomTileEntity;
import com.github.sejoslaw.vanillamagic.util.ItemStackUtil;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class TileBlockAbsorber extends CustomTileEntity implements IBlockAbsorber {
	public static final String REGISTRY_NAME = TileBlockAbsorber.class.getName();

	public List<String> getAdditionalInfo() {
		List<String> list = super.getAdditionalInfo();
		TileEntityHopper connectedHopper = getConnectedHopper();
		BlockPos connectedHopperPos = connectedHopper.getPos();
		list.add("Saved Hopper position: X=" + connectedHopperPos.getX() + ", Y=" + connectedHopperPos.getY() + ", Z="
				+ connectedHopperPos.getZ());
		list.add("Has connected Hopper: " + (connectedHopper != null));
		return list;
	}

	/**
	 * On each update this {@link TileEntity} will check for block which is on this
	 * position and try to place it in bottom {@link IHopper}
	 * 
	 * @see com.github.sejoslaw.vanillamagic.tileentity.CustomTileEntity#update()
	 */
	public void update() {
		TileEntityHopper connectedHopper = getConnectedHopper();
		IBlockState thisState = world.getBlockState(pos);

		if (BlockUtil.areEqual(thisState.getBlock(), Blocks.AIR)) {
			return;
		}

		TileEntity tileAtThisPos = world.getTileEntity(pos);
		if ((tileAtThisPos != null) && (tileAtThisPos instanceof IInventory)) {
			IInventory inv = (IInventory) tileAtThisPos;

			try {
				if (!InventoryHelper.isInventoryEmpty(inv, Direction.DOWN)) {
					for (int i = 0; i < inv.getSizeInventory(); ++i) {
						ItemStack stackInSlot = inv.getStackInSlot(i);
						ItemStack leftItems = InventoryHelper.putStackInInventoryAllSlots(connectedHopper, stackInSlot,
								getInputFacing());
						inv.setInventorySlotContents(i, leftItems);
					}
				}
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
				return;
			}

			try {
				if (!InventoryHelper.isInventoryEmpty(inv, Direction.DOWN)) {
					return;
				}
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}

		ItemStack thisBlock = new ItemStack(thisState.getBlock());

		if (thisBlock.getItem() == null) {
			return;
		}

		ItemStack leftItems = InventoryHelper.putStackInInventoryAllSlots(connectedHopper, thisBlock, getInputFacing());

		if (ItemStackUtil.isNullStack(leftItems)) {
			MinecraftForge.EVENT_BUS.post(new EventBlockAbsorber((IBlockAbsorber) this, world, pos, connectedHopper));
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	public Direction getInputFacing() {
		return Direction.UP;
	}

	public TileEntityHopper getConnectedHopper() {
		return (TileEntityHopper) world.getTileEntity(pos.offset(Direction.DOWN));
	}

	/**
	 * Returns NULL if there is no inventory for Hopper to transfer into.
	 */
	@Nullable
	public IInventory getInventoryForHopperTransfer() {
		try {
			Method thisMethod = getConnectedHopper().getClass().getMethod("getInventoryForHopperTransfer");
			thisMethod.setAccessible(true);
			return (IInventory) thisMethod.invoke(getConnectedHopper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}