package com.obstinate_3.obstifirstmod.setup;

import com.obstinate_3.obstifirstmod.container.ModContainer;
import com.obstinate_3.obstifirstmod.block.FirstBlockScreen;
import net.minecraft.client.gui.ScreenManager;

public class ClientInit {

    public static void registerScreens() {

        ScreenManager.registerFactory(ModContainer.FIRST_BLOCK, FirstBlockScreen::new);
    }
}
