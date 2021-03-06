package seia.vanillamagic.quest;

import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import seia.vanillamagic.api.quest.IQuest;
import seia.vanillamagic.api.quest.QuestData;
import seia.vanillamagic.api.quest.QuestList;
import seia.vanillamagic.api.util.Point;
import seia.vanillamagic.util.ItemStackUtil;
import seia.vanillamagic.util.QuestUtil;
import seia.vanillamagic.util.TextUtil;

/**
 * Base class for all the quests.<br>
 * Each Quest MUST overrides {@link readData} if it adds additional data.
 * 
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public abstract class Quest implements IQuest {
	/**
	 * Quest that is required for this Quest to be completed.
	 */
	protected IQuest requiredQuest;
	/**
	 * Position of this Quest on Quest Page.
	 */
	protected int posX, posY;
	/**
	 * Icon of this Quest on Quest Page.
	 */
	protected ItemStack icon;
	/**
	 * 1. Name of the Quest - to be displayed. 2. Unique name of this Quest. 3.
	 * Quest Title 4. Quest Description
	 */
	protected String questName, uniqueName, questTitle, questDescription;
	/**
	 * Quest data for Minecraft statistics.
	 */
	protected QuestData questData;

	/**
	 * Array of additional Quests which should be completed to get this Quest.
	 */
	@Nullable
	IQuest[] additionalRequiredQuests;

	public void readData(JsonObject jo) {
		this.requiredQuest = QuestList.get(jo.get("requiredQuest").getAsString());

		if (jo.has("questName")) {
			this.questName = jo.get("questName").getAsString();
		}

		if (jo.has("uniqueName")) {
			this.uniqueName = jo.get("uniqueName").getAsString();
		}

		// Quest position on the screen
		int tmpX = jo.get("posX").getAsInt();
		this.posX = (this.requiredQuest != null ? (this.requiredQuest.getPosition().getX() + tmpX) : tmpX);
		int tmpY = jo.get("posY").getAsInt();
		this.posY = (this.requiredQuest != null ? (this.requiredQuest.getPosition().getY() + tmpY) : tmpY);

		if (jo.has("icon")) {
			this.icon = ItemStackUtil.getItemStackFromJSON(jo.get("icon").getAsJsonObject());
		}

		// Additional Quests
		if (jo.has("additionalRequiredQuests")) {
			JsonObject additionalRequiredQuests = jo.get("additionalRequiredQuests").getAsJsonObject();
			Set<Entry<String, JsonElement>> set = additionalRequiredQuests.entrySet();
			IQuest[] requiredQuestsTable = new IQuest[set.size()];
			int index = 0;

			for (Entry<String, JsonElement> q : set) {
				requiredQuestsTable[index] = QuestList.get(q.getValue().getAsString());
				index++;
			}

			this.additionalRequiredQuests = requiredQuestsTable;
		}

		this.questTitle = "quest." + this.uniqueName;
		this.questDescription = "quest." + this.uniqueName + ".desc";
		// Build QuestData
		this.questData = buildQuestData();

		if (this.requiredQuest == null) {
			this.questData.isIndependent = true;
		}

		this.questData.registerStat();
		// Registering Quest - this method should ONLY be called here
		QuestList.addQuest(this);
	}

	/**
	 * @return Returns QuestData build for this Quest
	 */
	public QuestData buildQuestData() {
		return new QuestData("vanillamagic:" + this.uniqueName,
				new TextComponentTranslation("quest." + this.uniqueName, new Object[0]), this);
	}

	/**
	 * @return Returns TRUE if checking Player can get this Quest.
	 */
	public boolean canPlayerGetQuest(EntityPlayer player) {
		if (QuestUtil.hasQuestUnlocked(player, this.requiredQuest)) {
			if (hasAdditionalQuests()) {
				if (finishedAdditionalQuests(player)) {
					return true;
				}
			} else {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return Returns TRUE if this Quest has any additional Quests that need to be
	 *         achieved before this.
	 */
	public boolean hasAdditionalQuests() {
		return (additionalRequiredQuests != null) && (additionalRequiredQuests.length > 0);
	}

	public boolean finishedAdditionalQuests(EntityPlayer player) {
		if (hasAdditionalQuests()) {
			for (IQuest quest : additionalRequiredQuests) {
				if (!QuestUtil.hasQuestUnlocked(player, quest)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Add THIS Quest to given Player.
	 */
	public void addStat(EntityPlayer player) {
		QuestUtil.addStat(player, this);
	}

	/**
	 * @return Returns TRUE if the given Player already owns THIS Quest.
	 */
	public boolean hasQuest(EntityPlayer player) {
		return QuestUtil.hasQuestUnlocked(player, this);
	}

	/**
	 * Check Player progress on this Quest
	 */
	public void checkQuestProgress(EntityPlayer player) {
		if (this.canAddStat(player)) {
			addStat(player);
		}
	}

	/**
	 * @return Returns TRUE if this Quest can be added to given Player.
	 */
	public boolean canAddStat(EntityPlayer player) {
		return canPlayerGetQuest(player) && !hasQuest(player);
	}

	/*
	 * =============================================================== GETTERS
	 * ================================================================
	 */

	public IQuest getParent() {
		return this.requiredQuest;
	}

	public Point getPosition() {
		return new Point(this.posX, this.posY);
	}

	public ItemStack getIcon() {
		return this.icon;
	}

	public String getQuestName() {
		return this.questName;
	}

	public String getQuestDescription() {
		return TextUtil.translateToLocal(this.questDescription);
	}

	public String getUniqueName() {
		return this.uniqueName;
	}

	public IQuest[] getAdditionalRequiredQuests() {
		return this.additionalRequiredQuests;
	}

	public QuestData getQuestData() {
		return this.questData;
	}
}
