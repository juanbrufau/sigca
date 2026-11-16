/*
 * SIGCA — Sistema de Gestão da Coleta de Alimentos
 * Projeto de Extensão Comunitária (Curricularização) — Fatec Rio Preto
 * Tecnologia em Informática para Negócios, 3º período
 */
package sigca.servico;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.Normalizer;

/**
 * Guarda de senha e de resposta de segurança.
 *
 * A senha NUNCA é gravada em texto. O que vai para o banco é o resumo
 * SHA-256 de (sal || senha), em 64 caracteres hexadecimais, e o sal
 * próprio de cada usuário, em 32 caracteres. Dois usuários com a mesma
 * senha têm hashes diferentes, porque os sais são diferentes.
 *
 * DECLARAÇÃO ACADÊMICA: MessageDigest e SecureRandom não foram dados em
 * aula. Eles entram porque gravar senha em texto é indefensável num
 * sistema que lida com dados de voluntários, e porque pertencem à
 * biblioteca padrão do Java — não exigem nenhuma dependência externa.
 * Na versão entregue ao cliente, o algoritmo será BCrypt ou equivalente,
 * que é próprio para senha e tem custo de cálculo ajustável.
 */
public class ServicoSeguranca {

    private static final String ALGORITMO = "SHA-256";
    private static final char[] HEXA = "0123456789abcdef".toCharArray();
    private static final SecureRandom SORTEIO = new SecureRandom();

    /**
     * Sorteia um sal de 16 bytes, devolvido em 32 caracteres hexadecimais.
     */
    public String gerarSal() {
        byte[] bytes = new byte[16];
        SORTEIO.nextBytes(bytes);
        return paraHexa(bytes);
    }

    /**
     * Resumo criptográfico de (sal || texto).
     */
    public String gerarHash(String sal, String texto) {
        try {
            MessageDigest resumo = MessageDigest.getInstance(ALGORITMO);
            byte[] bytes = resumo.digest((sal + texto).getBytes(StandardCharsets.UTF_8));
            return paraHexa(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo " + ALGORITMO + " indisponível.", e);
        }
    }

    /**
     * Confere a senha digitada contra o hash gravado.
     */
    public boolean conferir(String sal, String hashGravado, String textoDigitado) {
        if (sal == null || hashGravado == null || textoDigitado == null) {
            return false;
        }
        return hashGravado.trim().equalsIgnoreCase(gerarHash(sal.trim(), textoDigitado));
    }

    /**
     * Normaliza a resposta da pergunta de segurança antes de comparar:
     * sem espaços nas pontas, tudo em minúsculas e sem acentuação. Assim
     * "São José" e "sao jose" valem a mesma coisa.
     */
    public String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto.trim().toLowerCase(),
                Normalizer.Form.NFD);
        return semAcento.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private String paraHexa(byte[] bytes) {
        char[] saida = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int valor = bytes[i] & 0xFF;
            saida[i * 2] = HEXA[valor >>> 4];
            saida[i * 2 + 1] = HEXA[valor & 0x0F];
        }
        return new String(saida);
    }
}
