package com.chatautoscroll;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("chatautoscroll")
public interface ChatAutoScrollConfig extends Config
{
	@Range(min = 1, max = 600)
	@Units(Units.SECONDS)
	@ConfigItem(
			keyName = "delaySeconds",
			name = "Scroll back after",
			description = "How long the chatbox can stay scrolled up before it jumps back to the newest message"
	)
	default int delaySeconds()
	{
		return 10;
	}
}