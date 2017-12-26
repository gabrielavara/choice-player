package com.gabrielavara.choiceplayer.util;

import static java.awt.event.ActionEvent.CTRL_MASK;
import static org.jnativehook.NativeInputEvent.ALT_MASK;
import static org.jnativehook.NativeInputEvent.SHIFT_MASK;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_D;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_LEFT;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_M;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_PAGE_DOWN;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_PAGE_UP;
import static org.jnativehook.keyboard.NativeKeyEvent.VC_RIGHT;

import java.util.logging.Level;

import com.gabrielavara.choiceplayer.controllers.PlayerController;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {
    private static final int LONG_I = 0;

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
        boolean isAltPressed = (e.getModifiers() & ALT_MASK) != 0;
        boolean isShiftPressed = (e.getModifiers() & SHIFT_MASK) != 0;
        boolean isCtrlPressed = (e.getModifiers() & CTRL_MASK) != 0;

        if (e.getKeyCode() == VC_M && isAltPressed && !isCtrlPressed && !isShiftPressed) {
            playerController.getLikedFolderFileMover().moveFile();
        } else if (e.getKeyCode() == VC_D && isAltPressed && !isCtrlPressed && !isShiftPressed) {
            playerController.getRecycleBinFileMover().moveFile();
        } else if (e.getKeyCode() == VC_PAGE_UP && isAltPressed && isCtrlPressed && !isShiftPressed) {
            playerController.getPlaylistUtil().goToPreviousTrack();
        } else if (e.getKeyCode() == VC_PAGE_DOWN && isAltPressed && isCtrlPressed && !isShiftPressed) {
            playerController.getPlaylistUtil().goToNextTrack();
        } else if (e.getKeyCode() == VC_LEFT && isAltPressed && isCtrlPressed && !isShiftPressed) {
            playerController.rewind();
        } else if (e.getKeyCode() == VC_RIGHT && isAltPressed && isCtrlPressed && !isShiftPressed) {
            playerController.fastForward();
        } else if (e.getKeyCode() == LONG_I && isAltPressed && !isCtrlPressed && !isShiftPressed) {
            playerController.playPause(true);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Nothing to do
    }
}
