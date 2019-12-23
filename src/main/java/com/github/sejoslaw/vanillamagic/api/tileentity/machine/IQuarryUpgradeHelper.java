package com.github.sejoslaw.vanillamagic.api.tileentity.machine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * This is the QuarryUpgradeHelper which holds data about all QuarryUpgrades
 * connected with one Quarry. <br>
 * Each Quarry has it's own IQuarryUpgradeHelper.
 * 
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public interface IQuarryUpgradeHelper {
	/**
	 * @return Returns Quarry to which this UpgradeHelper is connected.
	 */
	IQuarry getQuarry();

	/**
	 * @return Returns all upgrades of this Quarry in form of List.
	 */
	List<IQuarryUpgrade> getUpgradesList();

	/**
	 * @return Returns all upgrades of this Quarry in form of Map. <br>
	 *         Key is a position of Block connected with IQuarryUpgrade. <br>
	 *         World.getBlockState(key).getBlock() == value.getBlock() <br>
	 *         If these are not equal than something went horribly wrong.
	 */
	Map<BlockPos, IQuarryUpgrade> getUpgradesMap();

	/**
	 * Add the {@link IQuarryUpgrade} to the list of upgrades.
	 */
	void addUpgradeFromBlock(Block block, BlockPos upgradePos);

	/**
	 * @param upgrade checking QuarryUpgrade
	 * 
	 * @return Returns position of the given QuarryUpgrade from the list of all
	 *         registered upgrades for this Quarry.
	 */
	@Nullable
	BlockPos getUpgradePos(IQuarryUpgrade upgrade);

	/**
	 * This method will perform upgrades once a tick, for each QuarryUpgrade from
	 * list.
	 */
	void modifyQuarry(IQuarry quarry);

	/**
	 * @return Returns the list of drops from all upgrades for a given block.
	 */
	List<ItemStack> getDrops(Block blockToDig, IWorld world, BlockPos workingPos, BlockState workingPosState);

	/**
	 * Clears the upgrade lists.
	 */
	void clearUpgrades();

	/**
	 * @return This will add all upgrades to the given list.
	 */
	List<String> addAdditionalInfo(List<String> baseInfo);
}