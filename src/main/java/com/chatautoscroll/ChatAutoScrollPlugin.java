package com.chatautoscroll;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
		name = "Chat Autoscroll",
		description = "Scrolls the chatbox back to the newest message after a configurable delay",
		tags = {"chat", "chatbox", "scroll", "autoscroll", "qol"}
)
public class ChatAutoScrollPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ChatAutoScrollConfig config;

	// When we first noticed the chat was scrolled up (-1 = not scrolled up)
	private long scrolledUpSince = -1;
	private int lastScrollY = -1;
	private int lastScrollHeight = -1;

	@Provides
	ChatAutoScrollConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ChatAutoScrollConfig.class);
	}

	@Override
	protected void shutDown()
	{
		reset();
	}

	// GameTick runs on the client thread every ~0.6s, which is plenty precise for this
	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget lines = client.getWidget(InterfaceID.Chatbox.SCROLLAREA);
		if (lines == null || lines.isHidden())
		{
			reset();
			return;
		}

		int scrollY = lines.getScrollY();
		int scrollHeight = lines.getScrollHeight();
		int bottom = Math.max(0, scrollHeight - lines.getHeight());

		if (scrollY >= bottom)
		{
			// Already at the newest message
			reset();
			return;
		}

		long now = System.currentTimeMillis();

		// Start the timer the first time we see it scrolled up, and restart it
		// if the user scrolls again (scrollY moved but no new messages arrived)
		boolean userScrolled = scrollY != lastScrollY && scrollHeight == lastScrollHeight;
		if (scrolledUpSince < 0 || userScrolled)
		{
			scrolledUpSince = now;
		}

		lastScrollY = scrollY;
		lastScrollHeight = scrollHeight;

		if (now - scrolledUpSince >= config.delaySeconds() * 1000L)
		{
			scrollToBottom(lines, bottom);
			reset();
		}
	}

	private void scrollToBottom(Widget lines, int bottom)
	{
		lines.setScrollY(bottom);
		// Moves the content and keeps the scrollbar thumb in sync
		client.runScript(ScriptID.UPDATE_SCROLLBAR,
				InterfaceID.Chatbox.CHATSCROLLBAR,
				InterfaceID.Chatbox.SCROLLAREA,
				bottom);
	}

	private void reset()
	{
		scrolledUpSince = -1;
		lastScrollY = -1;
		lastScrollHeight = -1;
	}
}