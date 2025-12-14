package com.institute.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {


    private static class OtpEntry {
        String otp;
        long expiresAtMillis;
        OtpEntry(String otp, long expiresAtMillis) {
            this.otp = otp;
            this.expiresAtMillis = expiresAtMillis;
        }
    }

    // thread-safe store
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    // expiry in milliseconds (1 minute)
    private static final long OTP_TTL_MILLIS = 60_000L; // <-- 1 minute

    // Generate a 6-digit OTP, store with expiry, return OTP
    public String generateOtp(String rawKey) {
        if (rawKey == null) throw new IllegalArgumentException("key null");

        String key = normalizeKey(rawKey);
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        long expiresAt = Instant.now().toEpochMilli() + OTP_TTL_MILLIS;
        otpStore.put(key, new OtpEntry(otp, expiresAt));

        // debug log (prints on server console)
        System.out.println("OTP STORED → key=" + key + " | otp=" + otp + " | expiresAt=" + expiresAt);
        return otp;
    }

    // Validate OTP: check presence, match and expiry. Remove on success or expiry.
    public boolean validateOtp(String rawKey, String otpInput) {
        if (rawKey == null || otpInput == null) return false;

        String key = normalizeKey(rawKey);
        OtpEntry entry = otpStore.get(key);

        System.out.println("VALIDATE OTP → key=" + key + ", inputOtp=" + otpInput + ", entry=" + (entry==null ? "null" : entry.otp + " expiresAt=" + entry.expiresAtMillis));

        if (entry == null) {
            System.out.println("KEY NOT FOUND!");
            return false;
        }

        long now = Instant.now().toEpochMilli();

        // expired
        if (now > entry.expiresAtMillis) {
            otpStore.remove(key);
            System.out.println("OTP EXPIRED for key=" + key);
            return false;
        }

        // match
        boolean isValid = entry.otp.equals(otpInput);
        if (isValid) {
            // remove after successful verification to avoid reuse
            otpStore.remove(key);
            System.out.println("OTP VALID and removed for key=" + key);
            return true;
        }

        // wrong OTP (do not remove, allow retry until expiry)
        System.out.println("OTP MISMATCH for key=" + key);
        return false;
    }

    // Optional: allow checking remaining TTL in seconds (useful for UI)
    public long getRemainingTtlSeconds(String rawKey) {
        String key = normalizeKey(rawKey);
        OtpEntry entry = otpStore.get(key);
        if (entry == null) return 0;
        long remaining = entry.expiresAtMillis - Instant.now().toEpochMilli();
        return remaining > 0 ? (remaining + 999) / 1000 : 0;
    }

    // Normalize key consistently (trim + lowercase)
    private String normalizeKey(String raw) {
        return raw.trim().toLowerCase();
    }

    // Optional: cleanup method you can call periodically if needed (not required)
    public void cleanupExpiredOtps() {
        long now = Instant.now().toEpochMilli();
        otpStore.entrySet().removeIf(e -> e.getValue().expiresAtMillis <= now);
    }
}
