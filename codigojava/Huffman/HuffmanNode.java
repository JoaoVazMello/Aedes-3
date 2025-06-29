package codigojava.Huffman;

import java.io.Serializable;

public class HuffmanNode implements Comparable<HuffmanNode>, Serializable {
    private static final long serialVersionUID = 1L;

    char caractere;
    int frequencia;
    HuffmanNode esquerda;
    HuffmanNode direita;

    public HuffmanNode(char caractere, int frequencia) {
        this.caractere = caractere;
        this.frequencia = frequencia;
        this.esquerda = null;
        this.direita = null;
    }

    public boolean ehFolha() {
        return esquerda == null && direita == null;
    }

    @Override
    public int compareTo(HuffmanNode outro) {
        return Integer.compare(this.frequencia, outro.frequencia);
    }
}
