package com.winlator.cmod.xr;

import androidx.annotation.NonNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class XrVersion01 implements XrInterface {

    private static final String FLAG_SBS = "sbs";
    private static final String FLAG_VR = "vr";
    private static final String MSG_CLIENT = "client";

    private final File dir;
    protected final float[] input = new float[XrAPI.AppInput.values().length];

    public XrVersion01(File dir) {
        this.dir = dir;
    }

    @Override
    public void dataReceived(@NonNull String message) {
        try {
            String[] parts = message.split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                float value = Float.parseFloat(parts[i]);
                if (value > 0) {
                    input[i] = value;
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing float values: " + e.getMessage());
        }
    }

    @Override
    public byte[] encode(@NonNull float[] axes, @NonNull boolean[] buttons, int clientIndex) {
        StringBuilder binary = new StringBuilder();
        for (boolean button : buttons) {
            binary.append(button ? "T" : "F");
        }
        return (MSG_CLIENT + clientIndex +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.L_QX.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.L_QY.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.L_QZ.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.L_QW.ordinal()]) +
                " " + String.format(Locale.US, "%.1f", axes[XrAPI.ControllerAxis.L_THUMBSTICK_X.ordinal()]) +
                " " + String.format(Locale.US, "%.1f", axes[XrAPI.ControllerAxis.L_THUMBSTICK_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.L_X.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.L_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.L_Z.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.R_QX.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.R_QY.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.R_QZ.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.R_QW.ordinal()]) +
                " " + String.format(Locale.US, "%.1f", axes[XrAPI.ControllerAxis.R_THUMBSTICK_X.ordinal()]) +
                " " + String.format(Locale.US, "%.1f", axes[XrAPI.ControllerAxis.R_THUMBSTICK_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.R_X.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.R_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.R_Z.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.HMD_QX.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.HMD_QY.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.HMD_QZ.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.HMD_QW.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.HMD_X.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.HMD_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrAPI.ControllerAxis.HMD_Z.ordinal()]) +
                " " + String.format(Locale.US, "%.4f", axes[XrAPI.ControllerAxis.HMD_IPD.ordinal()]) +
                " " + String.format(Locale.US, "%.2f", axes[XrAPI.ControllerAxis.HMD_FOVX.ordinal()]) +
                " " + String.format(Locale.US, "%.2f", axes[XrAPI.ControllerAxis.HMD_FOVY.ordinal()]) +
                " " + String.format(Locale.US, "%d", (int)axes[XrAPI.ControllerAxis.HMD_SYNC.ordinal()]) +
                " " + binary).getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public float getValue(@NonNull XrAPI.AppInput index) {
        return switch (index) {
            case MODE_SBS -> new File(dir, FLAG_SBS).exists() ? 1 : 0;
            case MODE_VR -> new File(dir, FLAG_VR).exists() ? 1 : 0;
            default -> input[index.ordinal()];
        };
    }

    @Override
    public void setValue(@NonNull XrAPI.AppInput index, float value) {
        input[index.ordinal()] = value;
    }
}
