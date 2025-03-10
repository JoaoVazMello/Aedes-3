package codigojava;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {

    // Objeto para fazer as leituras
    Scanner leitor = new Scanner(System.in);

    // Inicializar o CRUD;
    Crud crud = new Crud();

    String nomeBase;
    int opcao = 0;

    // Laço de repetiçao para o menu
    while (opcao < 5) {

      // Opçoes de menu para realizar as operaçoes do crud
      System.out.println("======================== Sistema de CRUD - Aedes3 ========================");
      System.out.println(" 0 - Carregar Base de Dados - Deixe seu CSV no diretorio: BaseDeDados ");
      System.out.println(" 1 - Create - Digite as informaçoes do game que deseja inserir ");
      System.out.println(" 2 - Read - Passe o atributo que deseja utilizar para ler ");
      System.out.println(" 3 - Update - Passe o ID do game que deseja atualizar ");
      System.out.println(" 4 - Delete - Passe algum atributo que deseja apagar ");
      System.out.println(" 5 - Sair");
      System.out.println("==========================================================================");

      // Leitura do valor
      System.out.print("Digite a opçao que deseja: ");
      opcao = leitor.nextInt();
      leitor.nextLine();
      // Processamento da opçao selecionada
      switch (opcao) {
        // Opçao 0 = Carregamento da base de dados;
        case 0:
          // Coleta o nome da base de dados
          System.out.print("Digite o nome da base de dados: ");
          nomeBase = leitor.next();

          // Inicializador do leitorCSV
          LeitorCSV leitorCSV = new LeitorCSV(nomeBase);
          ArrayList<Game> ListaDeGames = leitorCSV.LerCsv();

          // Chamar o objeto Crud para realizar a operaçao de escrever o binario
          crud.EscreverBinario(ListaDeGames);
          break;

        // Opçao 1 = Criar novo Registro
        case 1:
          System.out.println("Criar novo jogo");

          Crud crud1 = new Crud();
          int id = crud1.PegarUltimoId() + 1;

          System.out.print("Digite o nome do jogo: ");
          String name = leitor.nextLine();

          System.out.print("Digite o preço do jogo: ");
          double price = leitor.nextDouble();

          leitor.nextLine();

          System.out.print("Digite a descrição do jogo: ");
          String description = leitor.nextLine();

          System.out.print("Digite a idade mínima do jogo: ");
          int required = leitor.nextInt();
          leitor.nextLine();

          System.out.print("Digite o dia do lançamento (dd): ");
          int day = leitor.nextInt();
          leitor.nextLine();

          System.out.print("Digite o mês do lançamento (mm): ");
          int month = leitor.nextInt();
          leitor.nextLine();

          System.out.print("Digite o ano do lançamento (yyyy): ");
          int year = leitor.nextInt();
          leitor.nextLine();

          // Cria a data de lançamento
          LocalDate release = LocalDate.of(year, month, day);

          ArrayList<String> generes = new ArrayList<>();
          System.out.print("Digite os gêneros do jogo separados por vírgula (ex: Ação, Aventura): ");
          String generesInput = leitor.nextLine();
          String[] generesArray = generesInput.split(",");
          for (String genre : generesArray) {
            generes.add(genre.trim());
          }

          // Cria o objeto Game com os dados recebidos
          Game game = new Game(id, name, release, required, price, description, generes);

          // Chama o método para escrever o novo jogo no banco de dados (exemplo)
          crud1.EscreverNovoGame(game);
          System.out.println("Jogo criado com sucesso");
          break;

        // Opçao 2 = Ler um registro
        case 2:
          System.out.println("Read");
          break;

        // Opçao 3 = Atualizar um registro
        case 3:
          System.out.println("Update");
          break;

        // Opçao 4 = Apagar um registro
        case 4:
          System.err.println("Delete");
          break;

        // Opçao 5 = Apagar um registro
        case 5:
          System.out.println("\n");
          System.out.println("=====================================");
          System.out.println("=  Obrigado por utilizar o sistema  =");
          System.out.println("=====================================");
          System.out.println("\n");
          opcao = 6;
          break;

        // Opçao invalida
        default:
          System.out.println("Opçao Invalida");
          opcao = leitor.nextInt();
          break;
      }
    }
    // fecha o leitor antes de terminar o programa
    leitor.close();
  }

}