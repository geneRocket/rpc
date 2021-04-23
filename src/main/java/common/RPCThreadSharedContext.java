package common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RPCThreadSharedContext {
    private static final ConcurrentHashMap<String, CompletableFuture<RPCResponse>> RESPONSES = new ConcurrentHashMap<>();

    public static void registerResponseFuture(String requestId, CompletableFuture<RPCResponse> future) {
        RESPONSES.put(requestId, future);
    }

    public static CompletableFuture<RPCResponse> getAndRemoveResponseFuture(String requestId) {
        return RESPONSES.remove(requestId);
    }


}