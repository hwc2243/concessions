package com.concessions.core;

public class ClientContext {
	private static final ThreadLocal<Long> currentClient = new ThreadLocal<>();

    public static void setCurrentClient(long clientId) {
        currentClient.set(clientId);
    }

    public static long getCurrentClient() {
        return currentClient.get();
    }

    public static void clearCurrentClient() {
        currentClient.remove();
    }
}
