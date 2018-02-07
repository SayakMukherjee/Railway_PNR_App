package com.sayak.railway_pnr;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by SAYAK on 8/1/2016.
 */
public class HMAC {
    public String getHMAC(String pnr){
        String test="'"+pnr+"'.'json'.'6a9c31798c24db78975edf5bb3d53a02'";
        String mykey = "3cd8281f9a623766e2d3474dc5c0d643";
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(mykey.getBytes(),
                    "HmacSHA1");
            mac.init(secret);
            byte[] digest = mac.doFinal(test.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x",b));
                //System.out.format("%02x", b);
            }
            //System.out.println();
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
