package net.kyrptonaught.lceui;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.kyrptonaught.lceui.config.LCEConfigOptions;
import net.kyrptonaught.lceui.creativeinv.CustomItemGroup;
import net.kyrptonaught.lceui.creativeinv.LCECreativeInventoryScreen;
import net.kyrptonaught.lceui.resourceloaders.TagResourceLoader;
import net.kyrptonaught.lceui.survivalinv.LCESurvivalInventoryScreen;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

// Hello.
// Before you look through rendering code,
// be warned that you may find some things weird.
// That's because Legacy Console Edition is
// inconsistent. To match its inconsistency, I
// have to use weirdly-sized textures and
// random divisions.
public class LCEUIMod implements ClientModInitializer {
    public static final String MOD_ID = "lceui";

    public static ConfigManager configManager = new ConfigManager.MultiConfigManager(MOD_ID);

    @Override
    public void onInitializeClient() {
        MixinExtrasBootstrap.init();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new TagResourceLoader());
        configManager.registerFile("config.json5", new LCEConfigOptions());
        configManager.load();
        LCEKeyBindings.init();
        CustomItemGroup.init();
        WhatsThisInit.init();
        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            while (LCEKeyBindings.openSecondaryInventory.wasPressed() && client.world != null && client.player != null && client.interactionManager != null && client.currentScreen == null) {
                client.getTutorialManager().onInventoryOpened();
                client.setScreen(client.interactionManager.hasCreativeInventory() && getConfig().creativeInventory ? new LCECreativeInventoryScreen(client.player) : (getConfig().survivalInventory ? new LCESurvivalInventoryScreen(client.player) : new InventoryScreen(client.player)));
            }
        });
    }

    public static void syncConfig() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(getConfig().closerTextShadows);
        buf.writeBoolean(getConfig().creativeInventory);
        buf.writeBoolean(getConfig().survivalInventory);
        buf.writeBoolean(getConfig().chatWidth);
        buf.writeBoolean(getConfig().rescaleChatText);
        buf.writeInt(getConfig().hotbarScale);
        ClientPlayNetworking.send(new Identifier(LCEUIMod.MOD_ID, "sync_config_packet"), buf);
    }

    public static LCEConfigOptions getConfig() {
        return (LCEConfigOptions) configManager.getConfig("config.json5");
    }
}