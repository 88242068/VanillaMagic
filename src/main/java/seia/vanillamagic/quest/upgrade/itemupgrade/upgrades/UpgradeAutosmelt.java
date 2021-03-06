package seia.vanillamagic.quest.upgrade.itemupgrade.upgrades;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.api.upgrade.itemupgrade.ItemUpgradeBase;
import seia.vanillamagic.util.ItemStackUtil;
import seia.vanillamagic.util.SmeltingUtil;
import seia.vanillamagic.util.TextUtil;

/**
 * Class which is a definition of Autosmelt ItemUpgrade.
 * 
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class UpgradeAutosmelt extends ItemUpgradeBase {
	public ItemStack getIngredient() {
		return new ItemStack(Items.MAGMA_CREAM);
	}

	public String getUniqueNBTTag() {
		return "NBT_UPGRADE_AUTOSMELT";
	}

	public String getUpgradeName() {
		return "Autosmelt Upgrade";
	}

	public String getTextColor() {
		return TextUtil.COLOR_RED;
	}

	/**
	 * Smelt digged blocks.
	 */
	@SubscribeEvent
	public void onDig(HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();

		if ((player == null) || !containsTag(player.getHeldItemMainhand())) {
			return;
		}

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		List<ItemStack> drops = event.getDrops();

		for (int i = 0; i < drops.size(); ++i) {
			ItemStack dropStack = drops.get(i);
			ItemStack afterSmelt = SmeltingUtil.getSmeltingResultAsNewStack(dropStack);

			if (!ItemStackUtil.isNullStack(afterSmelt)) {
				EntityItem afterSmeltEntity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), afterSmelt);
				world.spawnEntity(afterSmeltEntity);
				drops.remove(i);
			}
		}
	}
}