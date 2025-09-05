package com.winlator.cmod;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

public class XrAPI {
    public static final String CURRENT_VERSION = "0.1.2";
    @SuppressLint("SdCardPath")
    public static final String DEFAULT_PATH = "/data/data/com.winlator.cmod/files/imagefs/tmp/xr";
    @SuppressLint("SdCardPath")
    public static final String DEFAULT_DEBUG_PATH = "/sdcard/Download/udp_debug";
    public static final int DEFAULT_PORT = 7872;
    public static final String FLAG_SBS = "sbs";
    public static final String FLAG_VERSION = "version";
    public static final String FLAG_VR = "vr";
    public static final String MSG_CLIENT = "client";
    public static final int SLOTS_LIMIT = 4096;

    private final File dir;
    private final File[] lastFiles = new File[SLOTS_LIMIT];
    private final DatagramSocket socket = new DatagramSocket();

    private String debugIp = null;
    private boolean debugMode = false;

    public XrAPI(String path) throws Exception {
        //Ensure directory exists
        dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new Exception("Filesystem issue");
            }
        }

        //Ensure there are no previous data
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.delete()) {
                throw new Exception("Filesystem issue");
            }
        }
    }

    public String encode(@NonNull float[] axes, @NonNull boolean[] buttons, int clientIndex) {
        StringBuilder binary = new StringBuilder();
        for (boolean button : buttons) {
            binary.append(button ? "T" : "F");
        }
        return MSG_CLIENT + clientIndex +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.L_QX.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.L_QY.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.L_QZ.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.L_QW.ordinal()]) +
                " " + String.format(Locale.US, "%.1f", axes[XrActivity.ControllerAxis.L_THUMBSTICK_X.ordinal()]) +
                " " + String.format(Locale.US, "%.1f", axes[XrActivity.ControllerAxis.L_THUMBSTICK_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.L_X.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.L_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.L_Z.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.R_QX.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.R_QY.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.R_QZ.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.R_QW.ordinal()]) +
                " " + String.format(Locale.US, "%.1f", axes[XrActivity.ControllerAxis.R_THUMBSTICK_X.ordinal()]) +
                " " + String.format(Locale.US, "%.1f", axes[XrActivity.ControllerAxis.R_THUMBSTICK_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.R_X.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.R_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.R_Z.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.HMD_QX.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.HMD_QY.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.HMD_QZ.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.HMD_QW.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.HMD_X.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.HMD_Y.ordinal()]) +
                " " + String.format(Locale.US, "%.3f", axes[XrActivity.ControllerAxis.HMD_Z.ordinal()]) +
                " " + String.format(Locale.US, "%.4f", axes[XrActivity.ControllerAxis.HMD_IPD.ordinal()]) +
                " " + String.format(Locale.US, "%.2f", axes[XrActivity.ControllerAxis.HMD_FOVX.ordinal()]) +
                " " + String.format(Locale.US, "%.2f", axes[XrActivity.ControllerAxis.HMD_FOVY.ordinal()]) +
                " " + String.format(Locale.US, "%d", (int)axes[XrActivity.ControllerAxis.HMD_SYNC.ordinal()]) +
                " " + binary + " " + Build.MANUFACTURER.toUpperCase().replaceAll("\\s+", "");
    }

    public boolean hasFlag(String flag) {
        return new File(dir, flag).exists();
    }

    public void setDebugMode(boolean enabled) {
        debugMode = enabled;
    }

    @Deprecated
    public void sendFile(String data, int slot) throws Exception {
        File name = new File(dir, data);
        if (lastFiles[slot] == null) {
            if (!name.createNewFile()) {
                throw new Exception("Filesystem issue");
            }
        } else if (!lastFiles[slot].renameTo(name)) {
            throw new Exception("Filesystem issue");
        }
        lastFiles[slot] = name;
    }

    public void sendUDP(@NonNull String data, int port) throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        byte[] bytes = data.getBytes(StandardCharsets.US_ASCII);
        socket.send(new DatagramPacket(bytes, bytes.length, address, port));

        if (debugMode) {
            if (debugIp == null) {
                debugIp = getDebugModeIP();
            }
            if (!Objects.equals(debugIp, "0.0.0.0")) {
                InetAddress debugIPAdd = InetAddress.getByName(debugIp);
                socket.send(new DatagramPacket(bytes, bytes.length, debugIPAdd, port));
            }
        }
    }

    @NonNull
    private String getDebugModeIP() {
        File debugDir = new File(DEFAULT_DEBUG_PATH);
        if (debugDir.exists()) {
            for (File file : Objects.requireNonNull(debugDir.listFiles())) {
                return file.getName();
            }
        }
        return "0.0.0.0";
    }

    public void writeFile(String flag, @NonNull String data) throws Exception {
        FileOutputStream fos = new FileOutputStream(new File(dir, flag));
        fos.write(data.getBytes(StandardCharsets.US_ASCII));
        fos.close();
    }
}
