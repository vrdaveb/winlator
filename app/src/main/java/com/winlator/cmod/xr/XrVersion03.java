package com.winlator.cmod.xr;

import com.winlator.cmod.XrActivity;

public class XrVersion03 extends XrVersion02 {

    public XrVersion03() {
        super();
    }

    public String getFlags() {
        StringBuilder binary = new StringBuilder();
        binary.append(" ");
        binary.append(XrActivity.getImmersive() ? "T" : "F");
        binary.append(XrActivity.getSBS() ? "T" : "F");
        return binary.toString();
    }
}
