package seia.vanillamagic.items.enchantedbucket;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import seia.vanillamagic.items.ICustomItem;

/**
 * Interface implemented to any class that should be read as Enchanted Bucket.
 */
public interface IEnchantedBucket extends ICustomItem
{
	public static final String NBT_ENCHANTED_BUCKET = "NBT_ENCHANTED_BUCKET";
	public static final String NBT_FLUID_NAME = "NBT_FLUID_NAME";
	
	/**
	 * Fluid which this bucket contains.
	 */
	Fluid getFluidInBucket();
	
	default public ItemStack getItem()
	{
		ItemStack stack = getBucket().copy();
		stack.setStackDisplayName("Enchanted Bucket: " + getFluidInBucket().getName());
		NBTTagCompound stackTag = stack.getTagCompound();
		stackTag.setString(NBT_UNIQUE_NAME, getUniqueNBTName());
		stackTag.setString(NBT_ENCHANTED_BUCKET, getUniqueNBTName()); // to let Quest know that we want EnchantedBucket
		stackTag.setString(NBT_FLUID_NAME, getFluidInBucket().getName());
		return stack;
	}
	
	/**
	 * Crafting ingredient bucket with fluid.
	 */
	default public ItemStack getBucket()
	{
		return EnchantedBucketHelper.getResult(getFluidInBucket());
	}
	
	default void registerRecipe()
	{
	}
}