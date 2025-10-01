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
    public interface UdpDataListener {
        void onDataReceived(String message);
    }

    @SuppressLint("SdCardPath")
    public static final String DEFAULT_PATH = "/data/data/com.winlator.cmod/files/imagefs/tmp/xr";
    @SuppressLint("SdCardPath")
    public static final String DEFAULT_DEBUG_PATH = "/sdcard/Download/udp_debug";
    public static final int DEFAULT_PORT = 7872;
    public static final String FLAG_SBS = "sbs";
    public static final String FLAG_VR = "vr";
    private static final String MSG_CLIENT = "client";
    private static final String SYSTEM_FILE = "system";
    private static final String VERSION_FILE = "version";
    private static final String VERSION_VALUE = "0.1.5";

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

        //Write system info
        String info = "";
        info += Build.MANUFACTURER.toUpperCase() + "\n";
        info += Build.PRODUCT.toUpperCase() + "\n";
        info += Build.VERSION.RELEASE.toUpperCase() + "\n";
        info += Build.VERSION.SECURITY_PATCH.toUpperCase() + "\n";
        fos = new FileOutputStream(new File(dir, SYSTEM_FILE));
        fos.write(info.getBytes(StandardCharsets.US_ASCII));
        fos.close();
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

    public static class UdpThread implements Runnable {
        private static final int PORT = 7278;
        private static final int BUFFER_SIZE = 1024;

        private final UdpDataListener listener;
        private boolean running = false;

        public UdpThread(UdpDataListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[BUFFER_SIZE];
            try (DatagramSocket socket = new DatagramSocket(PORT)) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                running = true;

                while (running) {
                    socket.receive(packet);
                    String message = new String(buffer, 0, packet.getLength());

                    listener.onDataReceived(message);
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                System.err.println("Error listening for UDP packets: " + e.getMessage());
            }
        }

        public void stop() {
            running = false;
        }
    }
}
