package com.apus.salehub.adapter.out.agent;

public class RateLimiter {

    private final int requestsPerMinute;
    private double tokens;
    private long lastRefillNanos;

    public RateLimiter(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
        this.tokens = requestsPerMinute;
        this.lastRefillNanos = System.nanoTime();
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsed = (now - lastRefillNanos) / 1_000_000_000.0;
        tokens = Math.min(requestsPerMinute, tokens + elapsed * (requestsPerMinute / 60.0));
        lastRefillNanos = now;
    }

    public synchronized void acquire() throws InterruptedException {
        while (true) {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return;
            }
            double waitTime = (1.0 - tokens) / (requestsPerMinute / 60.0);
            Thread.sleep((long) (waitTime * 1000));
        }
    }
}
