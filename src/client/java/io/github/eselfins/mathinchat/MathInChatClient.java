package io.github.eselfins.mathinchat;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MathInChatClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mathinchat");

	@Override
	public void onInitializeClient() {
		LOGGER.info("MathInChat initialized");
	}
}
