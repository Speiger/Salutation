package speiger.src.salutation.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import speiger.src.salutation.Salutation;
import speiger.src.salutation.client.gui.chat.ChatScreen;
import speiger.src.salutation.client.gui.chat.ISaluationChat;
import speiger.src.salutation.client.gui.chat.MPChatScreen;
import speiger.src.salutation.client.gui.chat.MultilineChatScreen;

@SideOnly(Side.CLIENT)
public class ClientHandler {
	public static final ClientHandler INSTANCE = new ClientHandler();
	boolean replacedChat = false;
	
	public void init() {
		//TODO figure out which one is needed (Chunk Pregen needs both but Salutation doesn't)
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		GuiScreen screen = event.gui;
		Minecraft mc = Minecraft.getMinecraft();
		//TODO Decide if chunk pregen gets a dependency on this.
		//if not then one of the two need to yield. At the moment it will be salutation.
		if(Loader.isModLoaded("chunkpregen")) return; 
		boolean disable = Salutation.DISABLE_OVERRIDE.get();
		if(screen instanceof GuiMainMenu) {
			if(!replacedChat && !disable) {
				ReflectionHelper.setPrivateValue(GuiIngame.class, mc.ingameGUI, new MultilineChatScreen(), "persistantChatGUI", "field_73840_e");
				replacedChat = true;
			}
			else if(replacedChat && disable) {
				ReflectionHelper.setPrivateValue(GuiIngame.class, mc.ingameGUI, new GuiNewChat(mc), "persistantChatGUI", "field_73840_e");
				replacedChat = false;
			}
		}
		else if(!disable && screen instanceof GuiChat && !(screen instanceof ISaluationChat)) {
			if(screen instanceof GuiSleepMP) {
				event.setCanceled(true);
				mc.displayGuiScreen(new MPChatScreen());
			}
			else {
				event.setCanceled(true);
				mc.displayGuiScreen(new ChatScreen((GuiChat)screen));
			}
		}
	}
}
