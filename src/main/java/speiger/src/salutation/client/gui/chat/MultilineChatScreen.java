package speiger.src.salutation.client.gui.chat;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class MultilineChatScreen extends GuiNewChat {
	
	public MultilineChatScreen() {
		super(Minecraft.getMinecraft());
	}
	
	@Override
	public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
		if(chatLineId != 0) {
			super.printChatMessageWithOptionalDeletion(chatComponent, chatLineId);
			return;
		}
		split(chatComponent, T -> super.printChatMessageWithOptionalDeletion(T, chatLineId));
	}
	
	@SuppressWarnings("unchecked")
	public static void split(IChatComponent input, Consumer<IChatComponent> output) {
		IChatComponent currentOutput = new ChatComponentText("");
		for(IChatComponent component : (Iterable<IChatComponent>)input) {
			boolean hasSplit = false;
			String originalText = component.getUnformattedTextForChat();
			if("\\n".equals(originalText)) {
				output.accept(currentOutput);
				currentOutput = new ChatComponentText("");
			}
			else {
				for(String text : originalText.split("\\n")) {
					if(hasSplit) {
						output.accept(currentOutput);
						currentOutput = new ChatComponentText("");
						hasSplit = false;
					}
					currentOutput.appendSibling(new ChatComponentText(text).setChatStyle(component.getChatStyle()));
				}
			}
		}
		if(currentOutput.getSiblings().size() > 0) {
			output.accept(currentOutput);
		}
	}
}
