package mg.util;
public class StringUtil {

    // 1. Les constantes K (racines cubiques des 64 premiers nombres premiers)
    private static final int[] K = {
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    // 2. Fonctions logiques de base (Opérations binaires)
    private static int rotateRight(int x, int n) {
        return (x >>> n) | (x << (32 - n));
    }

    public static String applySha256(String input) {
        byte[] data = input.getBytes();
        int oldLen = data.length;
        int newLen = ((oldLen + 8) / 64 + 1) * 64;
        byte[] padded = new byte[newLen];
        System.arraycopy(data, 0, padded, 0, oldLen);
        System.out.println(java.util.Arrays.toString(padded));
        
        // Padding : un bit à 1 (0x80) puis des 0
        padded[oldLen] = (byte) 0x80;
        
        // Ajout de la taille du message à la fin (en bits)
        //Calcul de la longueur en bits (1 octet = 8 bits)
        long bits = (long) oldLen * 8;
        
        for (int i = 0; i < 8; i++) {
            padded[newLen - 1 - i] = (byte) (bits >>> (i * 8));
        }

        // Valeurs de hachage initiales (H0-H7)
        int h0 = 0x6a09e667, h1 = 0xbb67ae85, h2 = 0x3c6ef372, h3 = 0xa54ff53a;
        int h4 = 0x510e527f, h5 = 0x9b05688c, h6 = 0x1f83d9ab, h7 = 0x5be0cd19;

        // Traitement par blocs de 512 bits
        for (int off = 0; off < newLen; off += 64) {
            int[] w = new int[64];
            for (int i = 0; i < 16; i++) {
                w[i] = ((padded[off + i*4] & 0xff) << 24) | ((padded[off + i*4 + 1] & 0xff) << 16) |
                       ((padded[off + i*4 + 2] & 0xff) << 8) | (padded[off + i*4 + 3] & 0xff);
            }

            for (int i = 16; i < 64; i++) {
                int s0 = rotateRight(w[i-15], 7) ^ rotateRight(w[i-15], 18) ^ (w[i-15] >>> 3);
                int s1 = rotateRight(w[i-2], 17) ^ rotateRight(w[i-2], 19) ^ (w[i-2] >>> 10);
                w[i] = w[i-16] + s0 + w[i-7] + s1;
            }

            int a = h0, b = h1, c = h2, d = h3, e = h4, f = h5, g = h6, h = h7;

            // La boucle de compression (64 tours)
            for (int i = 0; i < 64; i++) {
                int S1 = rotateRight(e, 6) ^ rotateRight(e, 11) ^ rotateRight(e, 25);
                int ch = (e & f) ^ ((~e) & g);
                int temp1 = h + S1 + ch + K[i] + w[i];
                int S0 = rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = S0 + maj;

                h = g; g = f; f = e;
                e = d + temp1;
                d = c; c = b; b = a;
                a = temp1 + temp2;
            }

            h0 += a; h1 += b; h2 += c; h3 += d;
            h4 += e; h5 += f; h6 += g; h7 += h;
        }

        return String.format("%08x%08x%08x%08x%08x%08x%08x%08x", h0, h1, h2, h3, h4, h5, h6, h7);
    }
}