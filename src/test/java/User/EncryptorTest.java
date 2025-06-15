package User;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptorTest {
    @Test
    public void testSalt1() {
        //check whether same salt and password lead to the same encrypted password
        Encryptor encryptor = new Encryptor();
        byte[] salt = encryptor.generateSalt();
        String pass = "123456";
        assertEquals(encryptor.encrypt(pass, salt), encryptor.encrypt(pass, salt));
    }

    @Test
    public void testSalt2() {
        //check whether diffrent salt and same password lead to the different encrypted passwords
        Encryptor encryptor = new Encryptor();
        byte[] salt1 = encryptor.generateSalt();
        byte[] salt2 = encryptor.generateSalt();
        String pass = "123456";
        assertFalse(encryptor.encrypt(pass, salt1).equals(encryptor.encrypt(pass, salt2)));
    }

    @Test
    public void testHash() {
        //tests whether the encryptor hashes correctly. salt isn't randomly generated for test purposes
        Encryptor encryptor = new Encryptor();
        assertEquals("34800e15707fae815d7c90d49de44aca97e2d759", encryptor.encrypt("!", "a".getBytes()));
        assertEquals("66b27417d37e024c46526c2f6d358a754fc552f3", encryptor.encrypt("yz", "x".getBytes()));
        assertEquals("05bbf636b93640c8b0346297214cf199e9631db7", encryptor.encrypt("Cad)1", "a".getBytes()));
        assertEquals("86f7e437faa5a7fce15d1ddcb9eaeaea377667b8", encryptor.encrypt("", "a".getBytes()));
        assertEquals("4181eecbd7a755d19fdf73887c54837cbecf63fd", encryptor.encrypt("olly","m".getBytes()));
    }

}