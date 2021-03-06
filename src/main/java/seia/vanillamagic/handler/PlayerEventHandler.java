package seia.vanillamagic.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import seia.vanillamagic.config.VMConfig;
import seia.vanillamagic.item.book.BookRegistry;
import seia.vanillamagic.item.book.IBook;
import seia.vanillamagic.util.NBTUtil;

/**
 * Class which contains Events connected with Player.
 * 
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class PlayerEventHandler {
	public static final String NBT_PLAYER_HAS_BOOK = "NBT_PLAYER_HAS_BOOK";

	/**
	 * Event which will give Player books at first login to new World.
	 */
	@SubscribeEvent
	public void onPlayerLoggedInGiveBooks(PlayerLoggedInEvent event) {
		if (VMConfig.GIVE_PLAYER_CUSTOM_BOOKS_ON_LOGIN) {
			EntityPlayer player = event.player;
			NBTTagCompound playerData = player.getEntityData();
			NBTTagCompound data = NBTUtil.getTagSafe(playerData, EntityPlayer.PERSISTED_NBT_TAG);

			if (!data.getBoolean(NBT_PLAYER_HAS_BOOK)) {
				for (IBook book : BookRegistry.getBooks()) {
					ItemHandlerHelper.giveItemToPlayer(player, book.getItem());
				}

				data.setBoolean(NBT_PLAYER_HAS_BOOK, true);
				playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
			}
		}
	}
}