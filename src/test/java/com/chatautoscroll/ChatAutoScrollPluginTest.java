package com.chatautoscroll;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ChatAutoScrollPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ChatAutoScrollPlugin.class);
		RuneLite.main(args);
	}
}