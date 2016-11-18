package seia.vanillamagic.tileentity.machine.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public enum Fertilizer 
{
	/**
	 * Not a fertilizer. Using this handler class any item can be "used" as a
	 * fertilizer. Meaning, fertilizing will always fail.
	 */
	NONE((ItemStack) null) 
	{
		public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) 
		{
			return false;
		}
	},

	BONEMEAL(new ItemStack(Items.DYE, 1, 15))
	{
		public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) 
		{
			EnumActionResult res = stack.getItem().onItemUse(player, world, bc, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
			return res != null && res != EnumActionResult.PASS; 
		}
	},

	FORESTRY_FERTILIZER_COMPOUND(Item.REGISTRY.getObject(new ResourceLocation("Forestry", "fertilizerCompound"))) 
	{
		public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) 
		{
			return BONEMEAL.apply(stack, player, world, bc);
		}
	},

	BOTANIA_FLORAL_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("Botania", "fertilizer"))) 
	{
		public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) 
		{
			BlockPos below = bc.offset(EnumFacing.DOWN);
			Block belowBlock = world.getBlockState(below).getBlock();
			if(belowBlock == Blocks.DIRT || belowBlock == Blocks.GRASS) 
			{
				EnumActionResult res = stack.getItem().onItemUse(player, world, below, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
				return res != null && res != EnumActionResult.PASS; 
			}
			return false;
		}
		
		public boolean applyOnAir() 
		{
			return true;
		}
		
		public boolean applyOnPlant() 
		{
			return false;
		}
	},

	METALLURGY_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("Metallurgy", "fertilizer"))) 
	{
		public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) 
		{
			return BONEMEAL.apply(stack, player, world, bc);
		}
	},

	GARDEN_CORE_COMPOST(Item.REGISTRY.getObject(new ResourceLocation("GardenCore", "compost_pile"))) 
	{
		public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) 
		{
			return BONEMEAL.apply(stack, player, world, bc);
		}
	},

	MAGICALCROPS_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("magicalcrops", "magicalcrops_MagicalCropFertilizer"))) 
	{
		public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) 
		{
			return BONEMEAL.apply(stack, player, world, bc);
		}
	};

	private ItemStack stack;
	
	private Fertilizer(Item item) 
	{
		this(new ItemStack(item));
	}

	private Fertilizer(Block block) 
	{
		this(new ItemStack(block));
	}

	private Fertilizer(ItemStack stack) 
	{
		this.stack = stack == null || stack.getItem() == null ? null : stack;
//		if(this.stack != null) 
//		{
//			FarmStationContainer.slotItemsFertilizer.add(this.stack);
//		}
	}

	private static final List<Fertilizer> validFertilizers = new ArrayList();
	
	static 
	{
		for(Fertilizer f : values()) 
		{
			if(f.stack != null) 
			{
				validFertilizers.add(f);
			}
		}
	}

	/**
	 * Returns the singleton instance for the fertilizer that was given as
	 * parameter. If the given item is no fertilizer, it will return an instance
	 * of Fertilizer.None.
	 */
	public static Fertilizer getInstance(ItemStack stack) 
	{
		for(Fertilizer fertilizer : validFertilizers) 
		{
			if(fertilizer.matches(stack)) 
			{
				return fertilizer;
			}
		}
		return NONE;
	}

	/**
	 * Returns true if the given item can be used as fertilizer.
	 */
	public static boolean isFertilizer(ItemStack stack) 
	{
		return getInstance(stack) != NONE;
	}

	protected boolean matches(ItemStack stack) 
	{
		return OreDictionary.itemMatches(this.stack, stack, false);
	}

	/**
	 * Tries to apply the given item on the given block using the type-specific
	 * method. SFX is played on success.
	 * 
	 * If the item was successfully applied, the stackSize will be decreased if
	 * appropriate. The caller will need to check for stackSize 0 and null the
	 * inventory slot if needed.
	 * 
	 * @return true if the fertilizer was applied
	 */
	public abstract boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc);

	public boolean applyOnAir() 
	{
		return false;
	}

	public boolean applyOnPlant() 
	{
		return true;
	}
}