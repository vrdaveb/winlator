package com.winlator.cmod.xr;

import androidx.annotation.NonNull;

public interface XrInterface {

    enum AppInput {
        L_HAPTICS, R_HAPTICS, MODE_VR, MODE_SBS, HMD_FOVX, HMD_FOVY
    }

    // Order of the enum has to be the as in xr/main.cpp
    enum ControllerAxis {
        L_PITCH, L_YAW, L_ROLL, L_QX, L_QY, L_QZ, L_QW, L_THUMBSTICK_X, L_THUMBSTICK_Y, L_X, L_Y, L_Z,
        R_PITCH, R_YAW, R_ROLL, R_QX, R_QY, R_QZ, R_QW, R_THUMBSTICK_X, R_THUMBSTICK_Y, R_X, R_Y, R_Z,
        HMD_PITCH, HMD_YAW, HMD_ROLL, HMD_QX, HMD_QY, HMD_QZ, HMD_QW, HMD_X, HMD_Y, HMD_Z,
        HMD_IPD, HMD_FOVX, HMD_FOVY, HMD_SYNC
    }

    // Order of the enum has to be the as in xr/main.cpp
    enum ControllerButton {
        L_GRIP,  L_MENU, L_THUMBSTICK_PRESS, L_THUMBSTICK_LEFT, L_THUMBSTICK_RIGHT, L_THUMBSTICK_UP, L_THUMBSTICK_DOWN, L_TRIGGER, L_X, L_Y,
        R_A, R_B, R_GRIP, R_THUMBSTICK_PRESS, R_THUMBSTICK_LEFT, R_THUMBSTICK_RIGHT, R_THUMBSTICK_UP, R_THUMBSTICK_DOWN, R_TRIGGER,
    }

    void dataReceived(@NonNull String message);
    byte[] encode(@NonNull float[] axes, @NonNull boolean[] buttons, int clientIndex);
    float getValue(@NonNull AppInput index);
    void setValue(@NonNull AppInput index, float value);
}
