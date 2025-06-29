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


import java.util.*;

public class LZW {

    // Compressão LZW para array de bytes -> lista de códigos (inteiros)
    public static List<Integer> compress(byte[] input) {
        Map<List<Byte>, Integer> dictionary = new HashMap<>();

        // Inicializa o dicionário com todos os bytes possíveis (0-255)
        for (int i = 0; i < 256; i++) {
            dictionary.put(Arrays.asList((byte) i), i);
        }

        List<Integer> result = new ArrayList<>();
        List<Byte> w = new ArrayList<>();
        int dictSize = 256;

        for (byte b : input) {
            List<Byte> wb = new ArrayList<>(w);
            wb.add(b);
            if (dictionary.containsKey(wb)) {
                w = wb;
            } else {
                result.add(dictionary.get(w));
                dictionary.put(wb, dictSize++);
                w = new ArrayList<>();
                w.add(b);
            }
        }

        if (!w.isEmpty()) {
            result.add(dictionary.get(w));
        }

        return result;
    }

    // Descompressão LZW: lista de códigos -> array de bytes original
    public static byte[] decompress(List<Integer> compressed) {
        Map<Integer, List<Byte>> dictionary = new HashMap<>();

        // Inicializa dicionário com bytes individuais
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, Arrays.asList((byte) i));
        }

        int dictSize = 256;

        Iterator<Integer> iter = compressed.iterator();
        List<Byte> w = new ArrayList<>(dictionary.get(iter.next()));
        List<Byte> result = new ArrayList<>(w);

        while (iter.hasNext()) {
            int k = iter.next();
            List<Byte> entry;

            if (dictionary.containsKey(k)) {
                entry = dictionary.get(k);
            } else if (k == dictSize) {
                // Caso especial
                List<Byte> copy = new ArrayList<>(w);
                copy.add(w.get(0));
                entry = copy;
            } else {
                throw new IllegalArgumentException("Código inválido: " + k);
            }

            result.addAll(entry);

            // Adiciona nova sequência ao dicionário
            List<Byte> newEntry = new ArrayList<>(w);
            newEntry.add(entry.get(0));
            dictionary.put(dictSize++, newEntry);

            w = entry;
        }

        // Converte List<Byte> para byte[]
        byte[] output = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            output[i] = result.get(i);
        }
        return output;
    }
}
