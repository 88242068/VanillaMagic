package seia.vanillamagic.quest;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.utils.AltairChecker;
import seia.vanillamagic.utils.BlockPosHelper;
import seia.vanillamagic.utils.spell.EnumWand;

public class QuestCraftOnAltair extends Quest
{
	protected ItemStack[] ingredients; // each ItemStack is a different Item
	protected ItemStack result;
	protected int requiredAltairTier;
	protected EnumWand requiredMinimalWand;
	
	public QuestCraftOnAltair(Achievement required, int posX, int posY, String questName, String uniqueName, 
			ItemStack[] ingredients, ItemStack result, int requiredAltairTier, EnumWand requiredMinimalWand)
	{
		this(required, posX, posY, result.getItem(), questName, uniqueName, 
				ingredients, result, requiredAltairTier, requiredMinimalWand);
	}
	
	public QuestCraftOnAltair(Achievement required, int posX, int posY, Item itemIcon, String questName, String uniqueName, 
			ItemStack[] ingredients, ItemStack result, int requiredAltairTier, EnumWand requiredMinimalWand) 
	{
		super(required, posX, posY, itemIcon, questName, uniqueName);
		this.ingredients = ingredients;
		this.result = result;
		this.requiredAltairTier = requiredAltairTier;
		this.requiredMinimalWand = requiredMinimalWand;
	}
	
	public ItemStack[] getIngredients()
	{
		return ingredients;
	}
	
	public ItemStack getResult()
	{
		return result;
	}
	
	public int getRequiredAltairTier()
	{
		return requiredAltairTier;
	}
	
	public int getIngredientsStackSize()
	{
		int stackSize = 0;
		for(int i = 0; i < ingredients.length; i++)
		{
			stackSize += ingredients[i].stackSize;
		}
		return stackSize;
	}
	
	public int getIngredientsInCauldronStackSize(List<EntityItem> entitiesInCauldron)
	{
		int stackSize = 0;
		for(int i = 0; i < entitiesInCauldron.size(); i++)
		{
			stackSize += entitiesInCauldron.get(i).getEntityItem().stackSize;
		}
		return stackSize;
	}
	
	@SubscribeEvent
	public void craftOnAltair(RightClickBlock event)
	{
		try
		{
			EntityPlayer player = event.getEntityPlayer();
			BlockPos cauldronPos = event.getPos();
			
			// player has got stick in hand
			if(EnumWand.isWandInMainHandRight(player, requiredMinimalWand.wandTier))
			{
				World world = player.worldObj;
				// is right-clicking on Cauldron
				if(world.getBlockState(cauldronPos).getBlock() instanceof BlockCauldron)
				{
					// is altair build correct
					if(AltairChecker.checkAltairTier(world, cauldronPos, requiredAltairTier))
					{
						// all entities on World
						List<Entity> loadedEntities = world.loadedEntityList;
						// all items in cauldron
						List<EntityItem> entitiesInCauldron = new ArrayList<EntityItem>();
						// filtering all items in cauldron to check if the recipe is correct
						for(int i = 0; i < loadedEntities.size(); i++)
						{
							if(loadedEntities.get(i) instanceof EntityItem)
							{
								EntityItem entityItemInWorld = (EntityItem) loadedEntities.get(i);
								BlockPos entityItemInWorldPos = new BlockPos(entityItemInWorld.posX, entityItemInWorld.posY, entityItemInWorld.posZ);
								if(BlockPosHelper.isSameBlockPos(cauldronPos, entityItemInWorldPos))
								{
									entitiesInCauldron.add(entityItemInWorld);
									// lag stopper -> if there is more items in Cauldron then the recipe needs, return
									if(entitiesInCauldron.size() > ingredients.length)
									{
										return;
									}
								}
							}
						}
						// there is a right amount of items in Cauldron but are they the right items ?
						int ingredientsStackSize = getIngredientsStackSize();
						int ingredientsInCauldronStackSize = getIngredientsInCauldronStackSize(entitiesInCauldron);
						if(ingredientsStackSize == ingredientsInCauldronStackSize)
						{
							List<EntityItem> alreadyCheckedEntityItems = new ArrayList<EntityItem>(); // items to be deleted later
							for(int i = 0; i < ingredients.length; i++)
							{
								ItemStack currentlyCheckedIngredient = ingredients[i];
								for(int j = 0; j < entitiesInCauldron.size(); j++)
								{
									EntityItem currentlyCheckedEntityItem = entitiesInCauldron.get(j);
									if(ItemStack.areItemStacksEqual(currentlyCheckedIngredient, currentlyCheckedEntityItem.getEntityItem()))
									{
										alreadyCheckedEntityItems.add(currentlyCheckedEntityItem);
										break;
									}
								}
							}
							// the amount of items in Cauldron was right and the items themselfs were right
							if(ingredients.length == alreadyCheckedEntityItems.size())
							{
								if(!player.hasAchievement(achievement))
								{
									player.addStat(achievement, 1);
									return;
								}
								else if(player.hasAchievement(achievement))
								{
									for(int i = 0; i < alreadyCheckedEntityItems.size(); i++)
									{
										world.removeEntity(alreadyCheckedEntityItems.get(i));
									}
									BlockPos newItemPos = new BlockPos(cauldronPos.getX(), cauldronPos.getY() + 1, cauldronPos.getZ());
									Block.spawnAsEntity(world, newItemPos, new ItemStack(result.getItem()));
									world.updateEntities();
									return;
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
		}
	}
}