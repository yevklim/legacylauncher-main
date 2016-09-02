package ru.turikhay.util.async;

public abstract class LoopedThread extends ExtendedThread {
    protected static final String LOOPED_BLOCK = "iteration";

    public LoopedThread(String name) {
        super(name);
    }

    public LoopedThread() {
        this("LoopedThread");
    }

    protected final void lockThread(String reason) {
        if (reason == null) {
            throw new NullPointerException();
        } else if (!reason.equals("iteration")) {
            throw new IllegalArgumentException("Illegal block reason. Expected: iteration, got: " + reason);
        } else {
            super.lockThread(reason);
        }
    }

    public final boolean isIterating() {
        return !isThreadLocked();
    }

    public void iterate() {
        if (!isIterating()) {
            unlockThread("iteration");
        }
    }

    public final void run() {
        while (true) {
            lockThread("iteration");
            iterateOnce();
        }
    }

    protected abstract void iterateOnce();
}
