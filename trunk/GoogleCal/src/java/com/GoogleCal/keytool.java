package com.GoogleCal;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.*;
import java.io.File;
import java.io.FileInputStream;
//import sun.misc.BASE64Encoder;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author loope
 */
public class keytool {

    public static void main(String args[]) {
        //keytool myep = new keytool();
        //myep.doit();
    }

    public PrivateKey getPrivateKeyAsString() {

        KeyStore ks = null;
        String fileName = "/home/liferay/.keystore";

        char[] passPhrase = "CHANGEME".toCharArray();
        //BASE64Encoder myB64 = new BASE64Encoder();


        File certificateFile = new File(fileName);
        try {
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(certificateFile), passPhrase);
        } catch (IOException ex) {
            Logger.getLogger(keytool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(keytool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(keytool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(keytool.class.getName()).log(Level.SEVERE, null, ex);
        }

        KeyPair kp = getPrivateKey(ks, "tomcat", passPhrase);

        PrivateKey privKey = kp.getPrivate();


        //String b64 = myB64.encode(privKey.getEncoded());

        //System.out.println("-----BEGIN PRIVATE KEY-----");
        //System.out.println(b64);
        //System.out.println("-----END PRIVATE KEY-----");
        return privKey;

    }
// From http://javaalmanac.com/egs/java.security/GetKeyFromKs.html
    public KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
        try {
            // Get private key
            Key key = keystore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                // Get certificate of public key
                Certificate cert = keystore.getCertificate(alias);

                // Get public key
                PublicKey publicKey = cert.getPublicKey();

                // Return a key pair
                return new KeyPair(publicKey, (PrivateKey) key);
            }
        } catch (UnrecoverableKeyException e) {
            Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, e);
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, e);
        } catch (KeyStoreException e) {
            Logger.getLogger(GoogleCal.class.getName()).log(Level.WARNING, null, e);
        }
        return null;
    }
}
