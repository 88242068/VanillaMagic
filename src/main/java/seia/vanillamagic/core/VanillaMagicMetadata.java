package seia.vanillamagic.core;

import java.util.Arrays;

import net.minecraftforge.fml.common.ModMetadata;

/**
 * Class which adds metadata to mod main class.
 * 
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public final class VanillaMagicMetadata {
	private VanillaMagicMetadata() {
	}

	/**
	 * Run in PreInitialization stage. Add mod metadata.
	 */
	public static ModMetadata preInit(ModMetadata modMetadata) {
		modMetadata.modId = VanillaMagic.MODID;
		modMetadata.name = VanillaMagic.NAME;
		modMetadata.description = "Magic in vanilla Minecraft.";
		modMetadata.url = "https://github.com/Sejoslaw/VanillaMagic";
		modMetadata.version = VanillaMagic.VERSION;
		modMetadata.authorList = Arrays.asList("Seia");
		modMetadata.credits = "";
		modMetadata.autogenerated = false;
		return modMetadata;
	}
}