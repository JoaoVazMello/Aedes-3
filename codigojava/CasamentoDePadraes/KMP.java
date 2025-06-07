package codigojava.CasamentoDePadraes;

import java.util.ArrayList;
import java.util.List;

public class KMP {
    /**
            * Calcula a tabela de prefixo (lps) para o padrão em bytes.
            *
            * @param pattern O padrão em bytes.
     * @return A tabela lps.
     */
    private static int[] computeLPSArray(byte[] pattern) {
        int m = pattern.length;
        int[] lps = new int[m];
        int len = 0;

        lps[0] = 0; // sempre 0

        int i = 1;
        while (i < m) {
            if (pattern[i] == pattern[len]) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Busca todas as ocorrências do padrão em um texto usando KMP, ambos em bytes.
     *
     * @param text    Texto em bytes.
     * @param pattern Padrão em bytes.
     * @return Lista de índices onde o padrão ocorre no texto.
     */
    public static List<Integer> search(byte[] text, byte[] pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (text == null || pattern == null || pattern.length == 0 || text.length == 0
                || pattern.length > text.length) {
            return occurrences;
        }

        int n = text.length;
        int m = pattern.length;

        int[] lps = computeLPSArray(pattern);

        int i = 0; // índice para text
        int j = 0; // índice para pattern

        while (i < n) {
            if (text[i] == pattern[j]) {
                i++;
                j++;
            }

            if (j == m) {
                occurrences.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text[i] != pattern[j]) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return occurrences;
    }
}
