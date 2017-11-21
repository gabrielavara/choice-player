package com.gabrielavara.choiceplayer.util;

import java.util.logging.Level;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.gabrielavara.choiceplayer.controllers.PlayerController;

public class GlobalKeyListener implements NativeKeyListener {
    private final PlayerController playerController;

    public GlobalKeyListener(PlayerController playerController) {
        this.playerController = playerController;
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Nothing to do
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        boolean isAltPressed = (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0;
        if (e.getKeyCode() == NativeKeyEvent.VC_M && isAltPressed) {
            playerController.moveFileToGoodFolder();
        }
        if (e.getKeyCode() == NativeKeyEvent.VC_D && isAltPressed) {
            playerController.moveFileToRecycleBin();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Nothing to do
    }
}
