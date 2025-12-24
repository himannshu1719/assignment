// idle_detector.cpp  (Windows DLL)
#include <windows.h>

extern "C" __declspec(dllexport)
int waitForIdle(int seconds) {
    if (seconds <= 0) return 0;

    const ULONGLONG thresholdMs = (ULONGLONG)seconds * 1000ULL;
    const ULONGLONG watchStart  = GetTickCount64();

    while (GetTickCount64() - watchStart < thresholdMs) {
        LASTINPUTINFO lii;
        lii.cbSize = sizeof(LASTINPUTINFO);

        // Returns non-zero on success
        if (!GetLastInputInfo(&lii)) {
            return -1; // error
        }

        ULONGLONG now = GetTickCount64();
        ULONGLONG lastInputTick = (ULONGLONG)lii.dwTime; // tick count when last input received
        ULONGLONG idleMs = now - lastInputTick;

        if (idleMs >= thresholdMs) {
            return 1; // idle detected
        }

        Sleep(200); // pause thread a bit (avoid busy loop)
    }

    return 0; // not idle within given time window
}
