package codigojava.Criptografia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import codigojava.Game;
import codigojava.HASH.CrudHash;

public class RSA {

    private final BigInteger n;
    private final BigInteger e;
    private final BigInteger d;

    public RSA() {
        // Valores fixos conforme calculado:
        BigInteger p = BigInteger.valueOf(11);
        BigInteger q = BigInteger.valueOf(17);
        n = p.multiply(q);          // 187
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // 160

        e = BigInteger.valueOf(7);  // e coprimo com phi=160
        d = e.modInverse(phi);      // d = 23
    }

    public BigInteger[] encrypt(String message) {
        List<BigInteger> encrypted = new ArrayList<>();
        for (char c : message.toCharArray()) {
            BigInteger m = BigInteger.valueOf((int) c);
            encrypted.add(m.modPow(e, n));
        }
        return encrypted.toArray(new BigInteger[0]);
    }

    public String decrypt(BigInteger[] encrypted) {
        StringBuilder sb = new StringBuilder();
        for (BigInteger c : encrypted) {
            BigInteger m = c.modPow(d, n);
            sb.append((char) m.intValue());
        }
        return sb.toString();
    }

    // Métodos para salvar as chaves fixas (útil para seu trabalho)
    public void salvarChavesTexto(String prefixo) throws IOException {
        try (FileWriter pub = new FileWriter(prefixo + "_chave_publica.txt")) {
            pub.write(n.toString() + "\n");
            pub.write(e.toString() + "\n");
        }
        try (FileWriter priv = new FileWriter(prefixo + "_chave_privada.txt")) {
            priv.write(n.toString() + "\n");
            priv.write(d.toString() + "\n");
        }
    }


    // Exemplo simples para criptografar a base (atributos do Game)
    public void encryptBase() throws Exception {
        CrudHash<Game> hash = new CrudHash<>("games", Game.class.getConstructor(), 5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (FileWriter writer = new FileWriter("Base_criptografada.txt", false)) {

            for (int contador = 1; contador <= 100; contador++) {
                Game gameResult = hash.readArvore(contador);
                if (gameResult == null) break;

                writer.write("\n=== Jogo " + contador + " ===\n");

                BigInteger[] appid = encrypt(Integer.toString(gameResult.getAppID()));
                BigInteger[] name = encrypt(gameResult.getName());
                BigInteger[] release = encrypt(gameResult.getRelease().format(formatter));
                BigInteger[] required = encrypt(Integer.toString(gameResult.getRequired()));
                BigInteger[] price = encrypt(Double.toString(gameResult.getPrice()));
                BigInteger[] descricao = encrypt(gameResult.getDescription());

                writer.write("AppID: (criptografado):\n");
                for (BigInteger c : appid) writer.write(c.toString() + "\n");

                writer.write("Name (criptografado):\n");
                for (BigInteger c : name) writer.write(c.toString() + "\n");

                writer.write("Release (criptografado):\n");
                for (BigInteger c : release) writer.write(c.toString() + "\n");

                writer.write("Required (criptografado):\n");
                for (BigInteger c : required) writer.write(c.toString() + "\n");

                writer.write("Price (criptografado):\n");
                for (BigInteger c : price) writer.write(c.toString() + "\n");

                writer.write("Descricao (criptografado):\n");
                for (BigInteger c : descricao) writer.write(c.toString() + "\n");

                writer.write("Generos (criptografado):\n");
                for (String genres : gameResult.getGenres()) {
                    BigInteger[] generos = encrypt(genres);
                    for (BigInteger c : generos) writer.write(c.toString() + "\n");
                }
            }
        }
    }

    // Descriptografar a base
    public void descriptografarBase() throws IOException {
        try (
            BufferedReader br = new BufferedReader(new FileReader("Base_criptografada.txt"));
            FileWriter writer = new FileWriter("Base_Descriptografada.txt", false)
        ) {
            String linha;
            List<BigInteger> criptografado = new ArrayList<>();
            String campoAtual = null;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.contains("(criptografado):")) {
                    if (campoAtual != null && !criptografado.isEmpty()) {
                        descriptografarCampo(campoAtual, criptografado, writer);
                        criptografado.clear();
                    }
                    campoAtual = linha.split(":")[0];
                    writer.write(campoAtual + ": (descriptografado):\n");

                } else if (linha.startsWith("Jogo")) {
                    if (campoAtual != null && !criptografado.isEmpty()) {
                        descriptografarCampo(campoAtual, criptografado, writer);
                        criptografado.clear();
                    }
                    campoAtual = null;
                    writer.write(linha + "\n");

                } else if (linha.matches("\\d+")) {
                    criptografado.add(new BigInteger(linha));
                }
            }

            if (campoAtual != null && !criptografado.isEmpty()) {
                descriptografarCampo(campoAtual, criptografado, writer);
            }
        }
    }

    private void descriptografarCampo(String campo, List<BigInteger> criptografado, FileWriter writer) throws IOException {
        try {
            BigInteger[] vetor = criptografado.toArray(new BigInteger[0]);
            String resultado = decrypt(vetor).trim();

            switch (campo) {
                case "AppID":
                case "Required":
                    int inteiro = Integer.parseInt(resultado);
                    writer.write(inteiro + "\n");
                    break;

                case "Price":
                    double preco = Double.parseDouble(resultado);
                    writer.write(preco + "\n");
                    break;

                case "Release":
                    writer.write(resultado + "\n");
                    break;

                default:
                    writer.write(resultado + " " + "\n");
                    break;
            }
        } catch (Exception e) {
            writer.write("[Erro na descriptografia de " + campo + "]\n");
        }
    }
}
