package codigojava.ARVORE;

import java.io.*;

public class ParIDEnderecoArvore implements RegistroArvoreBMais<ParIDEnderecoArvore> {

    private int id;         // chave de busca (ID)
    private long endereco;  // posição no arquivo de dados
    private static final short TAMANHO = Integer.BYTES + Long.BYTES;; // tamanho fixo em bytes (int + long)

    public ParIDEnderecoArvore() {
        this(-1, -1); // Construtor padrão com valores inválidos
    }

    public ParIDEnderecoArvore(byte[] b) throws IOException {
        fromByteArray(b);
    }

     public ParIDEnderecoArvore(int id, long endereco) {
        this.id = id;
        this.endereco = endereco;
    }

    // Getters
    public int getId() {
        return id;
    }

    public long getEndereco() {
        return endereco;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEndereco(long endereco) {
        this.endereco = endereco;
    }

    @Override
    public short size() {
        return TAMANHO; // 4 bytes (int) + 8 bytes (long) = 12 bytes
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeLong(endereco);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        endereco = dis.readLong();
    }

    @Override
    public int compareTo(ParIDEnderecoArvore outro) {
        return this.id - outro.id;
    }

    public int compareTo(int id) {
        return this.id - id;
    }

    @Override
    public ParIDEnderecoArvore clone() {
        return new ParIDEnderecoArvore(this.id, this.endereco);
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Endereço: " + endereco;
    }

    public static int TAMANHO() {
        return Integer.BYTES + Long.BYTES;
    }

}