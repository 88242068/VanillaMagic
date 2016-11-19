package seia.vanillamagic.util;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import seia.vanillamagic.core.VanillaMagic;

public class ItemStackHelper 
{
	/**
	 * new ItemStack((Item)null);
	 */
	public static final ItemStack NULL_STACK = ItemStack.field_190927_a;
	
	private ItemStackHelper()
	{
	}
	
	public static ItemStack getLapis(int amount)
	{
		return new ItemStack(Items.DYE, amount, 4);
	}
	
	public static ItemStack getBonemeal(int amount)
	{
		return new ItemStack(Items.DYE, amount, 0);
	}
	
	public static ItemStack getSugarCane(int amount)
	{
		return new ItemStack(Items.REEDS, amount);
	}

	/**
	 * 0 - Skeleton
	 * 1 - Wither Skeleton
	 * 2 - Zombie
	 * 3 - Steve
	 * 4 - Creeper
	 * 5 - Ender Dragon
	 */
	@Nullable
	public static ItemStack getHead(int amount, int meta)
	{
		return new ItemStack(Items.SKULL, amount, meta);
	}
	
	public static boolean checkItemsInHands(EntityPlayer player, 
			ItemStack shouldHaveInOffHand, ItemStack shouldHaveInMainHand)
	{
		ItemStack offHand = player.getHeldItemOffhand();
		ItemStack mainHand = player.getHeldItemMainhand();
		if(ItemStack.areItemStacksEqual(offHand, shouldHaveInOffHand))
		{
			if(ItemStack.areItemStacksEqual(mainHand, shouldHaveInMainHand))
			{
				return true;
			}
		}
		return false;
	}
	
	@Nullable
	public static ItemStack getItemStackWithNextMetadata(ItemStack stack)
	{
		ItemStack stackWithNextMeta = null;
		Item item = stack.getItem();
		int amount = getStackSize(stack); // stackSize
		int meta = stack.getItemDamage();
		try
		{
			stackWithNextMeta = new ItemStack(item, amount, meta + 1);
		}
		catch(Exception e)
		{
			stackWithNextMeta = new ItemStack(item, amount, 0);
		}
		return stackWithNextMeta;
	}
	
	@Nullable
	public static ItemStack getItemStackFromJSON(JsonObject jo)
	{
		try
		{
			int id = jo.get("id").getAsInt();
			int stackSize = (jo.get("stackSize") != null ? jo.get("stackSize").getAsInt() : 1);
			int meta = (jo.get("meta") != null ? jo.get("meta").getAsInt() : 0);
			Item item = null;
			Block block = null;
			try
			{
				item = Item.getItemById(id);
			}
			catch(Exception e)
			{
				block = Block.getBlockById(id);
			}
			
			if(item == null)
			{
				return new ItemStack(block, stackSize, meta);
			}
			else if(block == null)
			{
				return new ItemStack(item, stackSize, meta);
			}
		}
		catch(Exception e)
		{
		}
		return null;
	}
	
	public static ItemStack[] getItemStackArrayFromJSON(JsonObject jo, String key)
	{
		JsonArray ja = jo.get(key).getAsJsonArray();
		ItemStack[] tab = new ItemStack[ja.size()];
		for(int i = 0; i < tab.length; ++i)
		{
			JsonElement je = ja.get(i);
			ItemStack stack = getItemStackFromJSON(je.getAsJsonObject());
			tab[i] = stack;
		}
		return tab;
	}

	public static boolean isIInventory(ItemStack stack) 
	{
		if(ItemStackHelper.isNullStack(stack))
		{
			return false;
		}
		Item itemFromStack = stack.getItem();
		Block blockFromStack = Block.getBlockFromItem(itemFromStack);
		if(blockFromStack == null)
		{
			return false;
		}
		if(blockFromStack instanceof ITileEntityProvider)
		{
			IBlockState blockFromStackState = blockFromStack.getDefaultState();
			TileEntity tileFromStack = blockFromStack.createTileEntity(null, blockFromStackState);
			if((tileFromStack instanceof IInventory) ||
					(tileFromStack instanceof IItemHandler))
			{
				return true;
			}
		}
		return false;
	}
	
	//================================== StackSize Operations ====================================
	
	/**
	 * Will return {@link ItemStackHelper#NULL_STACK} 
	 * if the {@link ItemStack} should be understand as Empty.
	 */
	public static ItemStack loadItemStackFromNBT(NBTTagCompound tag)
	{
		ItemStack stack = new ItemStack(tag);
		if(stack.func_190926_b())
		{
			return NULL_STACK;
		}
		return stack;
	}
	
	/**
	 * This method will returns the old ItemStack.stackSize
	 */
	public static int getStackSize(ItemStack stack)
	{
		return stack.func_190916_E();
	}
	
	/**
	 * This method will sets the ItemStack.stackSize to the given value.<br>
	 * Returns the given stack.
	 */
	public static void setStackSize(ItemStack stack, int value)
	{
		stack.func_190920_e(value);
	}
	
	/**
	 * This method will increase the ItemStack.stackSize of the given stack.<br>
	 * Returns the given stack.
	 */
	public static void increaseStackSize(ItemStack stack, int value)
	{
		stack.func_190917_f(value);
	}
	
	/**
	 * This method will decrease the ItemStack.stackSize of the given stack.<br>
	 * Returns the given stack.
	 */
	public static void decreaseStackSize(ItemStack stack, int value)
	{
		stack.func_190918_g(value);
	}
	
	/**
	 * Returns true if the given ItemStack is {@link #NULL_STACK}
	 */
	public static boolean isNullStack(ItemStack stack)
	{
		return stack.func_190926_b();
	}
	
	public static void printStack(ItemStack stack) 
	{
		VanillaMagic.LOGGER.log(Level.INFO, "Printing ItemStack data...");
		VanillaMagic.LOGGER.log(Level.INFO, "Item: " + stack.getItem());
		VanillaMagic.LOGGER.log(Level.INFO, "StackSize: " + getStackSize(stack));
		VanillaMagic.LOGGER.log(Level.INFO, "Meta: " + stack.toString());
	}
}