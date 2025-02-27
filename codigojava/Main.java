package codigojava;
import java.util.ArrayList;
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

          //Chamar o objeto Crud para realizar a operaçao de escrever o binario
          crud.EscreverBinario(ListaDeGames);
          break;
        
        // Opçao 1 = Criar novo Registro
        case 1:
          System.out.println("create");
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
    //fecha o leitor antes de terminar o programa
    leitor.close();
  }


}