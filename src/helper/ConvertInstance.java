package helper;

import java.io.IOException;

import common.InstanceManager;

public class ConvertInstance {
    public static void main(String[] args) throws IOException {
        InstanceManager manager = new InstanceManager();
        manager.convertInstance("TRP-S100-R2");
    }
}
