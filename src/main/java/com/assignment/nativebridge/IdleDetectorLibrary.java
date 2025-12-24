package com.assignment.nativebridge;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface IdleDetectorLibrary extends Library {

    // JNA will search in java.library.path and in the classpath
    IdleDetectorLibrary INSTANCE =
            Native.load("IdleDetector", IdleDetectorLibrary.class);


    int waitForIdle(int seconds);
}
