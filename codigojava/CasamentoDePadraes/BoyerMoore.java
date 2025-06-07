package codigojava.CasamentoDePadraes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoyerMoore {
    /**
     * Cria a tabela de "caracteres ruins" (bad character table) para o algoritmo
     * Boyer-Moore.
     * Esta tabela ajuda a determinar o quão longe podemos deslocar o padrão
     * quando ocorre uma incompatibilidade.
     *
     * @param pattern O array de bytes do padrão.
     * @return Um mapa onde a chave é um byte do padrão e o valor é o deslocamento.
     */
    private static Map<Byte, Integer> makeBadCharTable(byte[] pattern) {
        Map<Byte, Integer> table = new HashMap<>();
        int patternLength = pattern.length;
        for (int i = 0; i < patternLength - 1; i++) {
            // O deslocamento é o comprimento do padrão menos 1 menos o índice do caractere.
            // Isso garante que o caractere desalinhado no texto seja alinhado
            // com sua última ocorrência no padrão.
            table.put(pattern[i], patternLength - 1 - i);
        }
        return table;
    }

    /**
     * Procura todas as ocorrências de um padrão de bytes dentro de um texto de
     * bytes
     * usando o algoritmo Boyer-Moore.
     *
     * @param text    O array de bytes onde a busca será realizada.
     * @param pattern O array de bytes a ser procurado.
     * @return Uma lista de índices onde o padrão foi encontrado no texto.
     *         Retorna uma lista vazia se o padrão não for encontrado ou se os
     *         inputs forem inválidos.
     */
    public static List<Integer> search(byte[] text, byte[] pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (text == null || pattern == null || pattern.length == 0 || text.length == 0
                || pattern.length > text.length) {
            return occurrences;
        }

        int textLength = text.length;
        int patternLength = pattern.length;
        Map<Byte, Integer> badCharTable = makeBadCharTable(pattern);

        int textIndex = 0;

        while (textIndex <= textLength - patternLength) {
            int patternIndex = patternLength - 1;

            while (patternIndex >= 0 && pattern[patternIndex] == text[textIndex + patternIndex]) {
                patternIndex--;
            }

            if (patternIndex < 0) {
                occurrences.add(textIndex);
                textIndex += patternLength; // pula o comprimento do padrão
            } else {
                int shift = badCharTable.getOrDefault(text[textIndex + patternIndex], patternLength);
                textIndex += Math.max(1, shift);
            }
        }

        return occurrences;
    }
}
