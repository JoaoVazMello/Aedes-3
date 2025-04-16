package codigojava.HASH;

import java.io.IOException;

/*
REGISTRO HASH EXTENSÍVEL

Esta interface apresenta os métodos que os objetos
a serem incluídos na tabela hash extensível devem
conter.

Implementado pelo Prof. Marcos Kutova
v1.1 - 2021
*/
public interface RegistroHashExtensivel<T> {

    public int hashCode(); // chave numérica para ser usada no diretório

    public short size(); // tamanho FIXO do registro

    public byte[] toByteArray() throws IOException; // representação do elemento num vetor de bytes

    public void fromByteArray(byte[] ba) throws IOException; // vetor de bytes a ser usado na construção do elemento
}

