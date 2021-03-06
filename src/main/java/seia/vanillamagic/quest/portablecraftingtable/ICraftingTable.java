package seia.vanillamagic.quest.portablecraftingtable;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface to tell the QuestPortableCraftingTable that the held item is an
 * kind of Crafting Table.
 * 
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public interface ICraftingTable {
	/**
	 * Returns true if a Player can open a Crafting Table GUI.
	 */
	boolean canOpenGui(EntityPlayer player);
}