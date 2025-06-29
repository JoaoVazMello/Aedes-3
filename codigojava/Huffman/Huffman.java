package codigojava.Huffman;


import java.io.*;
import java.util.*;

public class Huffman {

    private HuffmanNode raiz;
    private Map<Character, String> codigos;
    private Map<Character, Integer> frequencias;

    public Huffman() {
        codigos = new HashMap<>();
        frequencias = new HashMap<>();
    }

    public void construirMapaDeFrequencias(String texto) {
        for (char c : texto.toCharArray()) {
            frequencias.put(c, frequencias.getOrDefault(c, 0) + 1);
        }
    }

    public void construirArvore() {
        PriorityQueue<HuffmanNode> fila = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencias.entrySet()) {
            fila.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (fila.size() > 1) {
            HuffmanNode n1 = fila.poll();
            HuffmanNode n2 = fila.poll();
            HuffmanNode novo = new HuffmanNode('\0', n1.frequencia + n2.frequencia);
            novo.esquerda = n1;
            novo.direita = n2;
            fila.add(novo);
        }

        raiz = fila.poll();
        gerarCodigos(raiz, "");
    }

    private void gerarCodigos(HuffmanNode no, String codigo) {
        if (no == null) return;
        if (no.ehFolha()) {
            codigos.put(no.caractere, codigo);
        }
        gerarCodigos(no.esquerda, codigo + "0");
        gerarCodigos(no.direita, codigo + "1");
    }

    public String codificar(String texto) {
        StringBuilder sb = new StringBuilder();
        for (char c : texto.toCharArray()) {
            sb.append(codigos.get(c));
        }
        return sb.toString();
    }

    public String decodificar(String binario) {
        StringBuilder sb = new StringBuilder();
        HuffmanNode atual = raiz;
        for (int i = 0; i < binario.length(); i++) {
            atual = (binario.charAt(i) == '0') ? atual.esquerda : atual.direita;
            if (atual.ehFolha()) {
                sb.append(atual.caractere);
                atual = raiz;
            }
        }
        return sb.toString();
    }

    public Map<Character, String> getCodigos() {
        return codigos;
    }

    public void salvarMapaDeFrequencias(String caminho) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(frequencias);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<Character, Integer> carregarMapaDeFrequencias(String caminho) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            return (Map<Character, Integer>) ois.readObject();
        }
    }

    public void salvarArvoreHuffman(String caminho) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(raiz);
        }
    }

    public void carregarArvoreHuffman(String caminho) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            raiz = (HuffmanNode) ois.readObject();
        }
    }

    public void salvarTextoBinario(String textoCodificado, String caminho) throws IOException {
        BitSet bits = new BitSet(textoCodificado.length());
        for (int i = 0; i < textoCodificado.length(); i++) {
            if (textoCodificado.charAt(i) == '1') bits.set(i);
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(bits);
            oos.writeInt(textoCodificado.length());
        }
    }

    public String carregarTextoBinario(String caminho) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminho))) {
            BitSet bits = (BitSet) ois.readObject();
            int length = ois.readInt();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(bits.get(i) ? '1' : '0');
            }
            return sb.toString();
        }
    }

    public void descomprimir(String caminhoEntradaBinario, String caminhoArvore, String caminhoSaidaTexto) throws IOException, ClassNotFoundException {
        carregarArvoreHuffman(caminhoArvore);
        String binario = carregarTextoBinario(caminhoEntradaBinario);
        String original = decodificar(binario);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoSaidaTexto))) {
            writer.write(original);
        }
    }
}