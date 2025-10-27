package com.winlator.cmod.xr;

import androidx.annotation.NonNull;

public class XrVersion02 extends XrVersion01 {

    public XrVersion02() {
        super(null);
    }

    @Override
    public void dataReceived(@NonNull String message) {
        try {
            String[] parts = message.split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                float value = Float.parseFloat(parts[i]);
                if ((value > 0) || (i >= 2)) {
                    input[i] = value;
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing float values: " + e.getMessage());
        }
    }

    @Override
    public float getValue(@NonNull AppInput index) {
        return input[index.ordinal()];
    }
}
