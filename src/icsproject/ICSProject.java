package icsproject;

import java.math.BigInteger;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICSProject {

    int primeSize;
    BigInteger p, q;
    BigInteger N;
    BigInteger r;
    BigInteger E, D;
    String fileName;
    String filePath;
    BigInteger[] encrypted;

    public ICSProject(int primeSize, String filePath, String fileName) {
        this.primeSize = primeSize;
        this.filePath = filePath;
        this.fileName = fileName;

        // Generate two distinct large prime numbers p and q.
        generatePrimeNumbers();

        // Generate Public Key {E, N} and Private Key {D, N}.
        generatePublicPrivateKeys();
        
        // Encrypt the File Data
        encrypt();
        
        // Decrypt File the Data
        decrypt();
    }

    public final void generatePrimeNumbers() {
        p = new BigInteger(primeSize, 10, new Random());
        do {
            q = new BigInteger(primeSize, 10, new Random());
        } while (q.compareTo(p) == 0);
    }

    public final long getCurrentTime() {
        java.util.Date date = Calendar.getInstance().getTime();
        Timestamp a = new Timestamp(date.getTime());
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return currentTime;
    }

    public final void generatePublicPrivateKeys() {
        N = p.multiply(q);
        r = p.subtract(BigInteger.valueOf(1));
        r = r.multiply(q.subtract(BigInteger.valueOf(1)));
        do {
            E = new BigInteger(2 * primeSize, new Random());
        } while ((E.compareTo(r) != -1) || (E.gcd(r).compareTo(BigInteger.valueOf(1)) != 0));

        D = E.modInverse(r);
    }

    public final void encrypt() {
        String message = "";
        try {
            message = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException ex) {
            Logger.getLogger(ICSProject.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] temp = new byte[1];

        byte[] digits = message.getBytes();

        BigInteger [] bigdigits = new BigInteger[digits.length];

        for (int i = 0; i < bigdigits.length; i++) {
            temp[0] = digits[i];
            bigdigits[i] = new BigInteger(temp);
        }

        encrypted = new BigInteger[bigdigits.length];

        long start = getCurrentTime();
        for (int i = 0; i < bigdigits.length; i++) {
            encrypted[i] = bigdigits[i].modPow(E, N);
        }
        long end = getCurrentTime();
        System.out.println("Encryption done in : " + new Timestamp(end - start).getTime()/1000.0 + " seconds");
        
        File f = new File(filePath.substring(0, filePath.lastIndexOf("\\\\") + 2) + "ciphertext.txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            for (BigInteger encrypt : encrypted) {
                pw.println(encrypt.toString(16).toUpperCase());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ICSProject.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public final void decrypt() {
        BigInteger[] decrypted = new BigInteger[encrypted.length];
        long start = getCurrentTime();
        for (int i = 0; i < decrypted.length; i++) {
            decrypted[i] = encrypted[i].modPow(D, N);
        }
        long end = getCurrentTime();
        System.out.println("Decryption done in : " + new Timestamp(end - start).getTime()/1000.0 + " seconds");

        char[] charArray = new char[decrypted.length];

        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = (char) (decrypted[i].intValue());
        }
        
        File f = new File(filePath.substring(0, filePath.lastIndexOf("\\\\") + 2) + "plaintext.txt");
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println(new String(charArray));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ICSProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BigInteger getp() {
        return (p);
    }

    public BigInteger getq() {
        return (q);
    }

    public BigInteger getr() {
        return (r);
    }

    public BigInteger getN() {
        return (N);
    }

    public BigInteger getE() {
        return (E);
    }

    public BigInteger getD() {
        return (D);
    }
}
