package codigojava;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LeitorCSV {

  private static String path;

  // Funçao para colocar os atributos em um lista
  private String[] Splitador(String linha) {

    // Criaçao da lista
    String[] listaAtributos = linha.split(";");

    // Retorna um vetor de bytes
    return listaAtributos;
  }

  public LeitorCSV(String nomeBase) {
//    path = "../BaseDeDados/" + nomeBase;

     path = "/Users/pedrofelix/Aedes-3/BaseDeDados/BaseDeDados.csv";
  }

  // Metodo para trabalhar a lista do CSV
  private ArrayList<String> ConverteLista(String linhaString) {
    // String que vai ser trabalhada => ['Action', 'Free to Play']

    // Objeto utilizado para altera a string
    StringBuilder sb = new StringBuilder(linhaString);

    // Loop que percorre a string
    for (int contador = 0; contador < sb.length(); contador++) {

      // Testando os caracteres ('[') (']') (' \' ') para serem apagados da string
      if (sb.charAt(contador) == '[' || sb.charAt(contador) == '\'' || sb.charAt(contador) == ']') {
        sb.deleteCharAt(contador);
        contador--;
      }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    // Agora com a string preparada basta separa e adicionar na lista

    // Objeto lista que vai ser retornado
    ArrayList<String> listaArrayList = new ArrayList<String>();

    // Categoria dos games separadas
    String[] CategoriasSplitadas = sb.toString().split(",");

    // Coloca cada String splitada dentro da lista
    for (int contador = 0; contador < CategoriasSplitadas.length; contador++) {
      listaArrayList.add(CategoriasSplitadas[contador].trim());
    }

    // Retorna a lista
    return listaArrayList;
  }

  public ArrayList<Game> LerCsv() {
    // Vetor de Objetos Game para conseguir retornar para o Crud.java
    // Devo implementar uma lista flexivel ???????? perguntar hayala
    ArrayList<Game> novGamelist = new ArrayList<Game>();

    try {
      // Objeto para ler cada linha do csv
      FileReader leitor = new FileReader(path);

      // Objeto bufferedReader para ler linha a linha do csv de maneira mais eficiente
      BufferedReader buffer = new BufferedReader(leitor);

      // Inicializa a linha com o buffer para pular a primeira
      String linha = buffer.readLine();

      // Contador para testa com volume menor de dados
      int contador = 0;

      // Loop para percorrer todas as linhas do arquivo , contador para diminuir a
      // quantidade de testes;
      while ((linha = buffer.readLine()) != null && contador < 1110) {

        // Funçao que vai splitar a linha
        String[] lista = Splitador(linha);

        // Instancia do objeto que vai ser construido
        Game objGame = new Game(
            // Id do jogo
            Integer.parseInt(lista[0]),

            // Nome do jogo
            lista[1],

            // Data do jogo(DateTimeFormatter.ISO_LOCAL_DATE formata a data no Formato ISO)
            LocalDate.parse(lista[2], DateTimeFormatter.ISO_LOCAL_DATE),

            // Idade para Jogar
            Integer.parseInt(lista[3]),

            // Preço do jogo
            Double.parseDouble(lista[4]),

            // Descriçao do jogo
            lista[5],

            // Categorias do jogo
            ConverteLista(lista[6]));

        novGamelist.add(objGame);

        // Mostrar Atributos carregados no objeto
        // objGame.MostraAtributos(objGame);

        // Contador para a quantidade de teste;
        contador++;
      }

      // Fecha o BufferReader
      buffer.close();
    } catch (Exception e) {
      System.out.print("Erro ao abrir o arquivo. Erro : ");
      e.printStackTrace();
    }

    // Retorna uma lista com todos os games;
    return novGamelist;
  }

}
