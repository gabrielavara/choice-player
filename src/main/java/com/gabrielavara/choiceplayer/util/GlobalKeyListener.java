package com.gabrielavara.choiceplayer.util;

import com.gabrielavara.choiceplayer.controllers.PlayerController;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;

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
        boolean isCtrlPressed = (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0;

        if (e.getKeyCode() == NativeKeyEvent.VC_M && isAltPressed) {
            playerController.moveFileToGoodFolder();
        } else if (e.getKeyCode() == NativeKeyEvent.VC_D && isAltPressed) {
            playerController.moveFileToRecycleBin();
        } else if (e.getKeyCode() == NativeKeyEvent.VC_PAGE_UP && isAltPressed && isCtrlPressed) {
            playerController.goToPreviousTrack();
        } else if (e.getKeyCode() == NativeKeyEvent.VC_PAGE_DOWN && isAltPressed && isCtrlPressed) {
            playerController.goToNextTrack();
        } else if (e.getKeyCode() == NativeKeyEvent.VC_LEFT && isAltPressed && isCtrlPressed) {
            playerController.rewind();
        } else if (e.getKeyCode() == NativeKeyEvent.VC_RIGHT && isAltPressed && isCtrlPressed) {
            playerController.fastForward();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Nothing to do
    }
}
