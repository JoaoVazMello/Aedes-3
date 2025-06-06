package codigojava.ListaInvertida;

import codigojava.Game;
import codigojava.HASH.CrudHash;
import codigojava.HASH.Registro;

import java.io.*;
import java.util.*;

class Stopwords{
    public static final Set<String> stopWords = Set.of(
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
            "about", "above", "after", "again", "against", "all", "am", "an", "and",
            "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being",
            "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't",
            "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during",
            "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't",
            "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here",
            "here's", "hers", "herself", "him", "himself", "his", "how", "how's",
            "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's",
            "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself",
            "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought",
            "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she",
            "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such",
            "than", "that", "that's", "the", "their", "theirs", "them", "themselves",
            "then", "there", "there's", "these", "they", "they'd", "they'll", "they're",
            "they've", "this", "those", "through", "to", "too", "under", "until", "up",
            "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were",
            "weren't", "what", "what's", "when", "when's", "where", "where's", "which",
            "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would",
            "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours",
            "yourself", "yourselves"
    );
}

public class ListaArquivo {

    //primeiramente ler linha a linha do arquivo cada registro
    public void LerBaseComHash() throws Exception {
        CrudHash<Game> hash = new CrudHash<>("games", Game.class.getConstructor(), 5);
    
        String pathCompleto = "C:\\Users\\joaoe\\OneDrive\\Desktop\\TpAedes\\codigojava\\.dadosgamesgames.bd";
        RandomAccessFile Acesso = new RandomAccessFile(pathCompleto, "r");
        int ultimoID = Acesso.readInt();
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ListaInvertida.txt", false))) {
            for (int contador = 1; contador <= 100; contador++) {
                var gameResult = hash.readArvore(contador);
    
                String descricao = gameResult.getDescription();
                int id = gameResult.getId();
    
                String descricaoLimpa = descricao.replace("-", " ").replaceAll("[^a-zA-Z ]", "").toLowerCase();
                String[] palavras = descricaoLimpa.split(" ");
    
                for (String palavra : palavras) {
                    if (!palavra.isEmpty() && !Stopwords.stopWords.contains(palavra)) {
                        writer.write(palavra + ":" + id);
                        writer.newLine();
                    }
                }
            }

            writer.close();
        }
    
        Acesso.close();
        ConcatenarArquivoDeLista();
    }
    

    public static void ConcatenarArquivoDeLista() throws Exception {
        Map<String, List<Integer>> indiceInvertido = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("ListaInvertida.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(":");
                if (partes.length == 2) {
                    String palavra = partes[0];
                    int id = Integer.parseInt(partes[1]);

                    indiceInvertido.computeIfAbsent(palavra, k -> new ArrayList<>()).add(id);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ListaInvertidaAgrupada.txt"))) {
            for (Map.Entry<String, List<Integer>> entry : indiceInvertido.entrySet()) {
                String palavra = entry.getKey();
                List<Integer> ids = entry.getValue();

                // Ordena os IDs
                Collections.sort(ids);


                String linha = palavra + ":" + ids.toString()
                        .replace("[", "").replace("]", "").replace(" ", "");

                writer.write(linha);
                writer.newLine();
            }
            System.out.println("Arquivo agrupado criado com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao escrever arquivo: " + e.getMessage());
        }
    }

    public void BuscarArquivoDeLista(String listaBusca) throws Exception {
        Map<String, Set<Integer>> mapaArquivo = new HashMap<>();
        CrudHash<Game> hash = new CrudHash<>("games", Game.class.getConstructor(), 5);

        // Lê o arquivo e monta o HashMap
        try (BufferedReader reader = new BufferedReader(new FileReader("ListaInvertidaAgrupada.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(":");

                if (partes.length >= 2) {
                    String palavra = partes[0].trim();
                    String[] idsString = partes[1].split(",");
                    Set<Integer> idsSet = new HashSet<>();

                    for (String id : idsString) {
                        idsSet.add(Integer.parseInt(id.trim()));
                    }

                    mapaArquivo.put(palavra, idsSet);
                }
            }
        }

        // Divide a entrada do usuário
        String[] palavrasListabusca = listaBusca.split(" ");

        Set<Integer> interseccao = null;
        Map<String, Set<Integer>> idsBuscados = new LinkedHashMap<>(); // Usei LinkedHashMap pra manter a ordem digitada

        for (String palavraBusca : palavrasListabusca) {
            palavraBusca = palavraBusca.trim();

            if (mapaArquivo.containsKey(palavraBusca)) {
                Set<Integer> idsPalavra = mapaArquivo.get(palavraBusca);
                idsBuscados.put(palavraBusca, idsPalavra); // salva os IDs da palavra

                if (interseccao == null) {
                    interseccao = new HashSet<>(idsPalavra);
                } else {
                    interseccao.retainAll(idsPalavra);
                }

            } else {
                System.out.println("Palavra não encontrada: " + palavraBusca);
                idsBuscados.put(palavraBusca, Collections.emptySet()); // palavra não encontrada, guarda vazio
            }
        }

        System.out.println(); // espaço

        // Primeiro mostra a interseção
        if (interseccao == null || interseccao.isEmpty()) {
            System.out.println("Nenhuma combinaçao de registro foi encontrada!");
        } else {
            List<Integer> ids = new ArrayList<>(interseccao);
            for (Integer id : ids) {
                var gameResult = hash.readHash(id);
                Game.MostraAtributos(gameResult);
            }
        }

        System.out.println(); // espaço

        for (Map.Entry<String, Set<Integer>> entry : idsBuscados.entrySet()) {
            if (entry.getValue().isEmpty()) {
                System.out.println("Nenhum Registro foi encontrado." );
            } else {
                List<Integer> ids2 = new ArrayList<>(entry.getValue());
                for (Integer id : ids2) {
                    var gameResult2 = hash.readHash(id);
                    Game.MostraAtributos(gameResult2);
                }
            }
        }
    }
}
