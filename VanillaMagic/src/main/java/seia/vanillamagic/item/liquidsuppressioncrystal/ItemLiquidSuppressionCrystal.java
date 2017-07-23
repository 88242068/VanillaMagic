package seia.vanillamagic.item.liquidsuppressioncrystal;

import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import seia.vanillamagic.item.CustomItemCrystal;

public class ItemLiquidSuppressionCrystal extends CustomItemCrystal
{
	public void registerRecipe() 
	{
		GameRegistry.addShapedRecipe(
				new ResourceLocation(""),
				null,
				getItem(), 
				new Object[]{
				"BBB",
				"BNB",
				"BBB",
				'B', Items.BUCKET,
				'N', Items.NETHER_STAR
		});
	}
	
	public String getItemName() 
	{
		return "Liquid Suppression Crystal";
	}
}