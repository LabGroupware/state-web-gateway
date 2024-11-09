package org.cresplanex.api.state.webgateway.utils;

import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.IntStream;

public class CustomIdGenerator {
    private static final long MAX_COUNTER = 1 << 16; // 最大値は65536
    private static final Random RANDOM = new Random();

    private boolean fixedFirst = false;
    private final long initialId;
    private long currentPeriod = timeNow();
    private long counter = 0;

    public CustomIdGenerator() {
        initialId = getMappedMacAddressPart();
    }

    // マッピング用のバイト配置を生成
    private Map<Integer, Integer> generateByteMapping(byte[] hash) {
        try {
            // long のビット位置 0〜48のリストを作成し、ランダムな順序にシャッフル
            ArrayList<Integer> bitPositions = new ArrayList<>();
            IntStream.range(0, 48).forEach(bitPositions::add);
            Collections.shuffle(bitPositions, new Random(hash[0])); // ハッシュ値に基づいてシャッフル

            // 48個の MACアドレスのビットを、シャッフルされた long のビット位置に割り当て
            Map<Integer, Integer> bitMapping = new HashMap<>();
            for (int i = 0; i < 48; i++) {
                bitMapping.put(i, bitPositions.get(i));
            }
            return bitMapping;
        }catch (Exception e){
            throw new RuntimeException("Error generating byte mapping", e);
        }
    }

    // MACアドレスのバイトを取得し、マッピングに基づいて配置
    private Long getMappedMacAddressPart() {
        try {
            String ma = System.getenv("CUSTOM_MAC_ADDRESS");
            if (ma != null) {
                return Long.parseLong(ma);
            }
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                if (iface.getHardwareAddress() != null) {
                    byte[] macAddress = iface.getHardwareAddress();
                    if (macAddress == null || macAddress.length < 6) {
                        continue;
                    }
                    // SHA-256でハッシュを生成
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(getSecret().getBytes());

                    // Random でfixedFirstBitをtrueに変更
                    if (new Random(hash[0]).nextBoolean()) {
                        fixedFirst = true;
                    }
                    Map<Integer, Integer> bitMapping = generateByteMapping(hash);

                    long macPart = 0L;
                    for (int byteIndex = 0; byteIndex < 6; byteIndex++) {
                        for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                            int macBitPosition = byteIndex * 6 + bitIndex;  // MACアドレスのビット位置
                            int targetBitPosition = bitMapping.get(macBitPosition);  // マッピングされたビット位置
                            long bitValue = (macAddress[byteIndex] >> bitIndex) & 1L;  // MACアドレスの該当ビットの値
                            macPart |= bitValue << targetBitPosition;  // 指定されたビット位置に値を配置
                        }
                    }

                    return macPart;
                }
            }
            throw new RuntimeException("MAC address not available or incomplete.");
        } catch (Exception e) {
            throw new RuntimeException("Error obtaining MAC address", e);
        }
    }

    private String getSecret(){
        String sec = System.getenv("CUSTOM_SECRET");
        return sec != null ? sec : "secret";
    }

    private long timeNow() {
        return System.currentTimeMillis();
    }

    private Int128 makeId() {
        return new Int128((initialId << 16) + counter, currentPeriod);
    }

    private Int128 genIdInternal() {
        long now = timeNow();
        if (currentPeriod != now || counter == MAX_COUNTER) {
            long oldPeriod = this.currentPeriod;
            while ((this.currentPeriod = timeNow()) <= oldPeriod) {
                // Just do nothing
            }
            counter = 0;
        }
        Int128 id = makeId();
        counter = counter + 1;
        return id;
    }

    private String interleaveBits(long first, long second) {
        if (fixedFirst) {
            long temp = first;
            first = second;
            second = temp;
        }

        long high = 0;
        long low = 0;

        for (int i = 0; i < 64; i++) {
            if (i % 2 == 0) {
                if ((first & (1L << (i / 2))) != 0) {
                    low |= (1L << i);
                }
                if ((first & (1L << (i / 2 + 32))) != 0) {
                    high |= (1L << i);
                }
            } else {
                if ((second & (1L << (i / 2))) != 0) {
                    low |= (1L << i);
                }
                if ((second & (1L << (i / 2 + 32))) != 0) {
                    high |= (1L << i);
                }
            }
        }

        // high と low を16進数文字列に変換して、ゼロ埋めで16桁にする
        return String.format("%016x%016x", high, low);
    }

    public String generate() {
        var gen = genIdInternal();
        return interleaveBits(gen.getHi(), gen.getLo());
    }
}


