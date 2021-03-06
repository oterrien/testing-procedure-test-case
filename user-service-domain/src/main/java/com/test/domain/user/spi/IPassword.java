package com.test.domain.user.spi;

import com.test.domain.user.api.EncodedException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public interface IPassword extends Comparable<IPassword>{

    String getValue();

    void setValue(String value);

    boolean isEncoded();

    void setEncoded(boolean encoded);

    default IPassword encoded() {
        if (!isEncoded()) {
            setEncoded(true);
            setValue(hash(getValue()));
        }
        return this;
    }

    default String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new EncodedException(e);
        }
    }

    @Override
    default int compareTo(IPassword o) {

        String thisPassword = this.isEncoded() ? getValue() : hash(getValue());
        String otherPassword = o.isEncoded() ? o.getValue() : hash(o.getValue());

        return thisPassword.compareTo(otherPassword);
    }
}
