package codigojava.Compressao.LZW;

/**
 * A classe {@code LZW} codifica e decodifica uma string usando uma sequência
 * de índices. Esses índices são armazenados na forma de uma sequência de bits,
 * com o apoio da classe VetorDeBits.
 * <p>
 * A codificação não é exatamente de caracteres (Unicode), mas dos bytes que
 * representam esses caracteres.
 *
 * @author Marcos Kutova
 * modificações feitas para se adequar à compressão de arquivo
 * <p>
 * PUC Minas
 */

import codigojava.Compressao.VetorDeBits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class LZW {

    public static final int BITS_POR_INDICE = 24;

    public static void codificaArquivo(String arquivoOriginal, String arquivoCompactado) throws Exception {
        File file = new File(arquivoOriginal);
        byte[] msgBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(msgBytes);
        }

        byte[] bytesCompactados = codifica(msgBytes);

        try (FileOutputStream fos = new FileOutputStream(arquivoCompactado)) {
            fos.write(bytesCompactados);
        }

        System.out.println("Arquivo original (" + msgBytes.length + " bytes): " + arquivoOriginal);
        System.out.println("Arquivo compactado (" + bytesCompactados.length + " bytes): " + arquivoCompactado);
        System.out.println("Eficiência: " + (100 * (1 - (float) bytesCompactados.length / (float) msgBytes.length)) + "%");
    }

    public static void decodificaArquivo(String arquivoCompactado, String arquivoDescompactado) throws Exception {
        File file = new File(arquivoCompactado);
        byte[] bytesCompactados = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytesCompactados);
        }

        byte[] msgBytes = decodifica(bytesCompactados);

        try (FileOutputStream fos = new FileOutputStream(arquivoDescompactado)) {
            fos.write(msgBytes);
        }

        System.out.println("Arquivo descompactado criado: " + arquivoDescompactado + " (" + msgBytes.length + " bytes)");
    }

    public static byte[] codifica(byte[] msgBytes) throws Exception {
        ArrayList<ArrayList<Byte>> dicionario = new ArrayList<>();
        ArrayList<Byte> vetorBytes;
        int i, j;
        byte b;
        for (j = -128; j < 128; j++) {
            b = (byte) j;
            vetorBytes = new ArrayList<>();
            vetorBytes.add(b);
            dicionario.add(vetorBytes);
        }

        ArrayList<Integer> saida = new ArrayList<>();
        i = 0;
        int indice, ultimoIndice;

        while (i < msgBytes.length) {
            vetorBytes = new ArrayList<>();
            b = msgBytes[i];
            vetorBytes.add(b);
            indice = dicionario.indexOf(vetorBytes);
            ultimoIndice = indice;

            while (indice != -1 && i < msgBytes.length - 1) {
                i++;
                b = msgBytes[i];
                vetorBytes.add(b);
                indice = dicionario.indexOf(vetorBytes);
                if (indice != -1)
                    ultimoIndice = indice;
            }

            if (indice == -1)
                i--;

            // Adiciona o último índice encontrado à saída!
            saida.add(ultimoIndice);

            if (dicionario.size() < (Math.pow(2, BITS_POR_INDICE) - 1) && indice == -1)
                dicionario.add(vetorBytes);
        }

        VetorDeBits bits = new VetorDeBits(saida.size() * BITS_POR_INDICE);
        int l = saida.size() * BITS_POR_INDICE - 1;
        for (i = saida.size() - 1; i >= 0; i--) {
            int n = saida.get(i);
            for (int m = 0; m < BITS_POR_INDICE; m++) {
                if (n % 2 == 0)
                    bits.clear(l);
                else
                    bits.set(l);
                l--;
                n /= 2;
            }
        }

        System.out.println("Índices: ");
        System.out.println(saida);
        System.out.println("Vetor de bits: ");
        System.out.println(bits);

        return bits.toByteArray();
    }


    public static byte[] decodifica(byte[] msgCodificada) throws Exception {
        VetorDeBits bits = new VetorDeBits(msgCodificada);
        int i, j, k;
        ArrayList<Integer> indices = new ArrayList<>();
        k = 0;
        for (i = 0; i < bits.length() / BITS_POR_INDICE; i++) {
            int n = 0;
            for (j = 0; j < BITS_POR_INDICE; j++) {
                n = n * 2 + (bits.get(k++) ? 1 : 0);
            }
            indices.add(n);
        }

        ArrayList<Byte> vetorBytes;
        ArrayList<Byte> msgBytes = new ArrayList<>();
        ArrayList<ArrayList<Byte>> dicionario = new ArrayList<>();
        byte b;
        for (j = -128, i = 0; j < 128; j++, i++) {
            b = (byte) j;
            vetorBytes = new ArrayList<>();
            vetorBytes.add(b);
            dicionario.add(vetorBytes);
        }

        ArrayList<Byte> proximoVetorBytes;
        i = 0;
        while (i < indices.size()) {
            vetorBytes = (ArrayList<Byte>) (dicionario.get(indices.get(i))).clone();

            for (j = 0; j < vetorBytes.size(); j++)
                msgBytes.add(vetorBytes.get(j));

            if (dicionario.size() < (Math.pow(2, BITS_POR_INDICE) - 1))
                dicionario.add(vetorBytes);

            i++;
            if (i < indices.size()) {
                proximoVetorBytes = (ArrayList<Byte>) dicionario.get(indices.get(i));
                vetorBytes.add(proximoVetorBytes.get(0));
            }
        }

        byte[] msgVetorBytes = new byte[msgBytes.size()];
        for (i = 0; i < msgBytes.size(); i++)
            msgVetorBytes[i] = msgBytes.get(i);

        return msgVetorBytes;
    }
}
