package codigojava.Huffman;

import codigojava.Game;
import codigojava.HASH.CrudHash;

public class CompressaoGames {
    public void RodarCompressao() throws NoSuchMethodException, SecurityException, Exception{
        Huffman huffman = new Huffman();

        CrudHash<Game> crud = new CrudHash<>("games", Game.class.getConstructor(), 5);
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= 100; i++) {
            Game g = crud.readArvore(i);

            sb.append(g.getAppID());
            sb.append(g.getName());
            sb.append(g.getRelease());
            sb.append(g.getRequired());
            sb.append(g.getPrice());
            sb.append(g.getDescription());
            for (String genero : g.getGenres()) sb.append(genero);
        }

        String texto = sb.toString();

        // Compressão
        huffman.construirMapaDeFrequencias(texto);
        huffman.construirArvore();
        String codificado = huffman.codificar(texto);

        huffman.salvarArvoreHuffman("arvore_huffman.dat");
        huffman.salvarTextoBinario(codificado, "jogos_comprimido.bin");

        // Descompressão
        huffman.descomprimir("jogos_comprimido.bin", "arvore_huffman.dat", "jogos_descomprimido.txt");
    }
}
