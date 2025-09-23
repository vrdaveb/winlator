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

interface UdpDataListener {
    void onDataReceived(String message);
}

public class XrAPI implements UdpDataListener {
    @SuppressLint("SdCardPath")
    public static final String DEFAULT_PATH = "/data/data/com.winlator.cmod/files/imagefs/tmp/xr";
    @SuppressLint("SdCardPath")
    public static final String DEFAULT_DEBUG_PATH = "/sdcard/Download/udp_debug";
    public static final int DEFAULT_PORT = 7872;
    public static final String FLAG_SBS = "sbs";
    public static final String FLAG_VR = "vr";
    private static final String MSG_CLIENT = "client";
    private static final String VERSION_FILE = "version";
    private static final String VERSION_VALUE = "0.1.3";

    private final File dir;
    private final DatagramSocket socket = new DatagramSocket();

    private String debugIp = null;
    private boolean debugMode = false;

    public XrAPI(String path) throws Exception {
        //Ensure directory exists
        dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Exception("Filesystem issue");
            }
        }

        //Ensure there are no previous data
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.delete()) {
                throw new Exception("Filesystem issue");
            }
        }

        //Write version number
        FileOutputStream fos = new FileOutputStream(new File(dir, VERSION_FILE));
        fos.write(VERSION_VALUE.getBytes(StandardCharsets.US_ASCII));
        fos.close();

        //Create our haptic UDP listener background thread
        Thread udpRecThread = new Thread(new UdpThread(this));
        udpRecThread.setDaemon(true); // Run as daemon thread
        udpRecThread.start();
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

    public void send(@NonNull String data, int port) throws Exception {
        //Send data to localhost
        InetAddress address = InetAddress.getLocalHost();
        byte[] bytes = data.getBytes(StandardCharsets.US_ASCII);
        socket.send(new DatagramPacket(bytes, bytes.length, address, port));

        if (debugMode) {
            //Get requested IP from the filesystem
            if (debugIp == null) {
                debugIp = "";
                File debugDir = new File(DEFAULT_DEBUG_PATH);
                if (debugDir.exists()) {
                    for (File file : Objects.requireNonNull(debugDir.listFiles())) {
                        debugIp = file.getName();
                        break;
                    }
                }
            }

            //Send the data over the network
            if (!debugIp.isEmpty()) {
                InetAddress debugIPAdd = InetAddress.getByName(debugIp);
                socket.send(new DatagramPacket(bytes, bytes.length, debugIPAdd, port));
            }
        }
    }

    @Override
    public void onDataReceived(String message) {
        //System.out.println("Received: " + message);

        try {
            String[] parts = message.split("\\s+");
            float[] values = new float[parts.length];

            for (int i = 0; i < parts.length; i++) {
                values[i] = Float.parseFloat(parts[i]);
            }

            if (values.length >= 2) {
                if (values[0] > 0) XrActivity.lControllerVibration = values[0];
                if (values[1] > 0) XrActivity.rControllerVibration = values[1];
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing float values: " + e.getMessage());
        }
    }
}

class UdpThread implements Runnable {
    private static final int PORT = 7278;
    private static final int BUFFER_SIZE = 1024;

    private final UdpDataListener listener;

    public UdpThread(UdpDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);
                String message = new String(buffer, 0, packet.getLength());

                listener.onDataReceived(message);
            }
        } catch (Exception e) {
            System.err.println("Error listening for UDP packets: " + e.getMessage());
        }
    }
}
