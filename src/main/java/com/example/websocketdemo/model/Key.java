package com.example.websocketdemo.model;

import org.apache.commons.codec.binary.Base64;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Date;


@Entity
@Table(name = "key_info")
public class Key {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private int id;

    @Column(name = "key_value", length = 3071)
    private byte[] key;

    @Column(name = "asym")
    private boolean isAsym;

    @Column(name = "created_time")
    private Date creationTime;

    @Column(name = "expired_time")
    private Date expiredTime;

    @Column(name = "is_public")
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User owner;


    public Key(byte[] key, boolean isAsym, Date creationTime, Date expiredTime, boolean isPublic, User owner) {
        this.key = key;
        this.isAsym = isAsym;
        this.creationTime = creationTime;
        this.expiredTime = expiredTime;
        this.isPublic = isPublic;
        this.owner = owner;
    }

    public Key(byte[] key, boolean isAsym, boolean isPublic, User owner) {
        this.key = key;
        this.isAsym = isAsym;
        this.isPublic = isPublic;
        this.owner = owner;
    }

    public Key() {
        this(new byte[0], true, true, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public boolean isAsym() {
        return isAsym;
    }

    public void setAsym(boolean asym) {
        isAsym = asym;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public PublicKey toPublic() {
        String keyContent = new String(getKey());
        keyContent = keyContent.replace("-----BEGIN RSA PUBLIC KEY-----", "");
        keyContent = keyContent.replace("-----END RSA PUBLIC KEY-----", "");
        System.out.println(keyContent);
        byte[] decoded = Base64.decodeBase64(keyContent);
        org.bouncycastle.asn1.pkcs.RSAPublicKey pkcs1PublicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(decoded);

        BigInteger modulus = pkcs1PublicKey.getModulus();
        BigInteger publicExponent = pkcs1PublicKey.getPublicExponent();
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            return null;
        }

//
//        PublicKey generatedPublic = kf.generatePublic(keySpec);
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(keyContent));
//        try {
//            System.out.println(new String(getKey()));
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            return keyFactory.generatePublic(keySpec);
//        }
//        catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
//            ex.printStackTrace();
//            return null;
//        }
    }

    public PrivateKey toPrivate() {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(getKey()));
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
