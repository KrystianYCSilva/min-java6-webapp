package br.gov.inep.censo.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testes unitarios de hashing para senha.
 */
public class PasswordUtilTest {

    @Test
    public void deveGerarEValidarHashPbkdf2() {
        String hash = PasswordUtil.hashPassword("admin123");
        Assert.assertTrue(hash.startsWith("PBKDF2$"));
        Assert.assertTrue(PasswordUtil.verifyPassword("admin123", hash));
        Assert.assertFalse(PasswordUtil.verifyPassword("senha-incorreta", hash));
        Assert.assertFalse(PasswordUtil.needsRehash(hash));
    }

    @Test
    public void deveManterCompatibilidadeComHashLegadoSha256() {
        String legacyHash = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";
        Assert.assertTrue(PasswordUtil.verifyPassword("admin123", legacyHash));
        Assert.assertTrue(PasswordUtil.needsRehash(legacyHash));
    }
}
