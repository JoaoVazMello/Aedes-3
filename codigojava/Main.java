package codigojava;

import codigojava.HASH.CrudHash;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static codigojava.Game.MostraAtributos;

public class Main {

  public static void main(String[] args) throws Exception {

    Scanner leitor = new Scanner(System.in);
    CrudSequencial crud = new CrudSequencial();
    CrudHash<Game> hash = new CrudHash<>("games", Game.class.getConstructor());

    String nomeBase;
    int opcao = 0;

    // Laço de repetiçao para o menu
    while (opcao != 6) {

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

          ListaDeGames.forEach((obj) -> {
              try {
                  hash.create(obj);
              } catch (Exception e) {
                  throw new RuntimeException(e);
              }
          } );
          break;

        // Opçao 1 = Criar novo Registro
        case 1:
          System.out.println("Criar novo jogo");

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
          Game game = new Game(0, name, release, required, price, description, generes);

          // Chama o método para escrever o novo game no banco de dados
          crud.EscreverNovoGame(game);
          System.out.println("Jogo criado com sucesso");
          break;

        // Opçao 2 = Ler um registro
        case 2:

          System.out.println("Digite o id do jogo procurado");
          int idGame = leitor.nextInt();
          leitor.nextLine();

//          var gameResult = crud.LerGame(idGame);
          var gameResult = hash.read(idGame);
          if (gameResult != null)
            MostraAtributos(gameResult);
          else
            System.out.println("Game com ID " + idGame + " não encontrado.");
          break;

        // Opçao 3 = Atualizar um registro
        case 3:
          System.out.println("Digite o Id do Jogo que deseja atualizar");
          var resultGame = crud.LerGame(leitor.nextInt());

          if (resultGame != null) {
            leitor.nextLine();

            System.out.println("Jogo a ser atualizado: ");
            MostraAtributos(resultGame);

            System.out.println("Digite os seguintes dados jogo para atualizar");

            System.out.print("Digite o nome do jogo: ");
            String nameGame = leitor.nextLine();

            System.out.print("Digite o preço do jogo: ");
            double priceGame = leitor.nextDouble();

            leitor.nextLine();

            System.out.print("Digite a descrição do jogo: ");
            String descriptionGame = leitor.nextLine();

            System.out.print("Digite a idade mínima do jogo: ");
            int requiredGame = leitor.nextInt();
            leitor.nextLine();

            System.out.print("Digite o dia do lançamento (dd): ");
            int dayGame = leitor.nextInt();
            leitor.nextLine();

            System.out.print("Digite o mês do lançamento (mm): ");
            int monthGame = leitor.nextInt();
            leitor.nextLine();

            System.out.print("Digite o ano do lançamento (yyyy): ");
            int yearGame = leitor.nextInt();
            leitor.nextLine();

            // Cria a data de lançamento
            LocalDate releaseGame = LocalDate.of(yearGame, monthGame, dayGame);

            ArrayList<String> generesGame = new ArrayList<>();
            System.out.print("Digite os gêneros do jogo separados por vírgula (ex: Ação, Aventura): ");
            String generesInputGame = leitor.nextLine();
            String[] generesArrayGame = generesInputGame.split(",");
            for (String genre : generesArrayGame) {
              generesGame.add(genre.trim());
            }

            // Cria o objeto Game com os dados recebidos
            Game gameUpdate = new Game(resultGame.getId(), nameGame, releaseGame,
                requiredGame, priceGame,
                descriptionGame, generesGame);

            crud.AtualizarGame(gameUpdate);
          } else {
            System.out.println("Jogo não encontrado");
          }
          break;

        // Opçao 4 = Apagar um registro
        case 4:
          System.out.println("Digite o id do jogo a ser deletado");
          int idDelete = leitor.nextInt();
          leitor.nextLine();
          var resultado = crud.ApagarGame(idDelete);
          if (resultado != null) {
              System.out.println("Game com ID " + idDelete + " deletado com sucesso.");
              MostraAtributos(resultado);
          }

          else
            System.out.println("Game com ID " + idDelete + " não encontrado.");
          break;

        case 5:
          System.out.println("Ordenação");

          break;
        // Opçao 5 = Sair do sistema
        case 6:
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
          break;
      }
    }

    leitor.close();
  }

}