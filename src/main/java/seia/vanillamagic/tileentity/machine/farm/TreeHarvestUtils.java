package seia.vanillamagic.tileentity.machine.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import seia.vanillamagic.tileentity.machine.farm.farmer.FarmerTree;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class TreeHarvestUtils {
	private final static List<Block> LEAVES = new ArrayList<Block>();

	private int horizontalRange;
	private int verticalRange;
	private BlockPos origin;

	public void harvest(TileFarm farm, FarmerTree farmer, BlockPos pos, HarvestResult res) {
		this.horizontalRange = farm.getWorkRadius() + 7;
		this.verticalRange = 30;

		harvest(farm.getWorld(), farm.getMachinePos(), pos, res, farmer.getIgnoreMeta());
	}

	public void harvest(World world, BlockPos pos, HarvestResult res) {
		this.horizontalRange = 12;
		this.verticalRange = 30;
		this.origin = new BlockPos(pos);

		IBlockState wood = world.getBlockState(pos);

		harvestUp(world, pos, res, new HarvestTarget(wood));
	}

	private void harvest(World world, BlockPos origin, BlockPos bc, HarvestResult res, boolean ignoreMeta) {
		this.origin = new BlockPos(origin);
		IBlockState wood = world.getBlockState(bc);

		if (ignoreMeta) {
			harvestUp(world, bc, res, new BaseHarvestTarget(wood.getBlock()));
		} else {
			harvestUp(world, bc, res, new HarvestTarget(wood));
		}
	}

	protected void harvestUp(World world, BlockPos bc, HarvestResult res, BaseHarvestTarget target) {
		if (!isInHarvestBounds(bc) || res.harvestedBlocks.contains(bc)) {
			return;
		}

		IBlockState bs = world.getBlockState(bc);
		boolean isLeaves = isLeaves(bs);

		if (target.isTarget(bs) || isLeaves) {
			res.harvestedBlocks.add(bc);

			for (EnumFacing dir : EnumFacing.VALUES) {
				if (dir != EnumFacing.DOWN) {
					harvestUp(world, bc.offset(dir), res, target);
				}
			}
		} else {
			harvestAdjacentWood(world, bc, res, target);

			for (EnumFacing dir : EnumFacing.HORIZONTALS) {
				BlockPos loc = bc.offset(dir);
				IBlockState locBS = world.getBlockState(loc);

				if (isLeaves(locBS)) {
					harvestAdjacentWood(world, loc, res, target);
				}
			}
		}
	}

	static boolean isLeaves(IBlockState bs) {
		return bs.getMaterial() == Material.LEAVES || LEAVES.contains(bs.getBlock());
	}

	private void harvestAdjacentWood(World world, BlockPos bc, HarvestResult res, BaseHarvestTarget target) {
		for (EnumFacing dir : EnumFacing.HORIZONTALS) {
			BlockPos targ = bc.offset(dir);

			if (target.isTarget(world.getBlockState(targ))) {
				harvestUp(world, targ, res, target);
			}
		}
	}

	private boolean isInHarvestBounds(BlockPos bc) {
		int dist = Math.abs(this.origin.getX() - bc.getX());

		if (dist > this.horizontalRange) {
			return false;
		}

		dist = Math.abs(this.origin.getZ() - bc.getZ());

		if (dist > this.horizontalRange) {
			return false;
		}

		dist = Math.abs(this.origin.getY() - bc.getY());

		if (dist > this.verticalRange) {
			return false;
		}

		return true;
	}

	private static final class HarvestTarget extends BaseHarvestTarget {
		IBlockState bs;
		EnumType variant;

		HarvestTarget(IBlockState bs) {
			super(bs.getBlock());

			this.bs = bs;
			variant = getVariant(bs);
		}

		public static EnumType getVariant(IBlockState bs) {
			EnumType v = null;

			try {
				v = bs.getValue(BlockNewLog.VARIANT);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (v == null) {
				try {
					v = bs.getValue(BlockOldLog.VARIANT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return v;
		}

		boolean isTarget(IBlockState bs) {
			if (variant == null) {
				return super.isTarget(bs);
			}

			return super.isTarget(bs) && variant == getVariant(bs);
		}
	}

	private static class BaseHarvestTarget {
		private final Block wood;

		BaseHarvestTarget(Block wood) {
			this.wood = wood;
		}

		boolean isTarget(IBlockState bs) {
			return bs.getBlock() == wood;
		}
	}
}