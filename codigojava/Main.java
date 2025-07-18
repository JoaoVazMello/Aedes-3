package codigojava;

import codigojava.CasamentoDePadraes.BoyerMoore;
import codigojava.CasamentoDePadraes.KMP;
import codigojava.Compressao.LZW.LZW;
import codigojava.Criptografia.RSA;
import codigojava.Criptografia.VigenereBytes;
import codigojava.HASH.CrudHash;
import codigojava.Huffman.CompressaoGames;
import codigojava.ListaInvertida.ListaArquivo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static codigojava.Game.MostraAtributos;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner leitor = new Scanner(System.in);
        CrudHash<Game> crud = new CrudHash<>("games", Game.class.getConstructor(), 5);
        ListaArquivo listainvertida = new ListaArquivo();
        String nomeArquivoCompactado = crud.nomeArquivo + "LZWCompressao" + ".bd";
        RSA criptografar = new RSA();
        String arquivoVigenere = crud.nomeArquivo + "Vigenere" + ".bd";
        criptografar.salvarChavesTexto("minhaChave");
        String chave = "criptografar";

        int opcao = 0;

        // Laço de repetiçao para o menu
        while (opcao != 16) {

            // Opçoes de menu para realizar as operaçoes do crud
            System.out.println("======================== Sistema de CRUD - Aedes3 ========================");
            System.out.println(" 0 - Carregar Base de Dados - Deixe seu CSV no diretorio: BaseDeDados ");
            System.out.println(" 1 - Create - Digite as informaçoes do game que deseja inserir ");
            System.out.println(" 2 - Read - Passe o atributo que deseja utilizar para ler ");
            System.out.println(" 3 - Update - Passe o ID do game que deseja atualizar ");
            System.out.println(" 4 - Delete - Passe algum atributo que deseja apagar ");
            System.out.println(" 5 - Text Read - Digite algum termo para ser buscado na base ");
            System.out.println(" 6 - LZW -  Para realizar a Compressão de Dados");
            System.out.println(" 7 - LZW -  Para realizar a Descompressão de Dados");
            System.out.println(" 8 - Huffman - Para realizar a Compressão de Dados ");
            System.out.println(" 9 - Busca por KMP");
            System.out.println(" 10 - Busca por BoyerMoore");
            System.out.println(" 11 - Busca por BoyerMoore");
            System.out.println(" 12 - Criptografia RSA");
            System.out.println(" 13 - Descriptografia RSA");
            System.out.println(" 14 - Criptografia Vigenere");
            System.out.println(" 15 - Descriptografia Vigenere");
            System.out.println(" 16 - Sair ");
            System.out.println("==========================================================================");

            // Leitura do valor
            System.out.print("Digite a opçao que deseja: ");
            opcao = leitor.nextInt();

            leitor.nextLine();

            // Processamento da opçao selecionada
            switch (opcao) {
                // Opçao 0 = Carregamento da base de dados;
                case 0:

                    // Inicializador do leitorCSV
                    LeitorCSV leitorCSV = new LeitorCSV();
                    List<Game> ListaDeGames = leitorCSV.LerCsv();

                    // Chamar o objeto Crud para realizar a operaçao de escrever o binario
                    crud.create(ListaDeGames);

                    // Criar o arquivo da lista
                    listainvertida.LerBaseComHash();

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
                    int id = crud.create(game);
                    System.out.println("Jogo criado com sucesso");
                    System.out.println("id: " + id);
                    break;

                // Opçao 2 = Ler um registro
                case 2:

                    System.out.println("Digite o id do jogo procurado");
                    int idGame = leitor.nextInt();
                    leitor.nextLine();

                    Game gameResult = null;

                    int opcaoProcurada = 0;

                    do {
                        System.out.println("Digite 1 para leitura sequencial, 2 para hash e 3 para arvore B+");
                        opcaoProcurada = leitor.nextInt();
                        leitor.nextLine();
                        switch (opcaoProcurada) {
                            case 1:
                                gameResult = crud.read(idGame);
                                break;
                            case 2:
                                gameResult = crud.readHash(idGame);
                                break;
                            case 3:
                                gameResult = crud.readArvore(idGame);
                                break;

                            default:
                                opcao = 4;
                                break;
                        }
                    } while (opcao == 4);

                    if (gameResult != null)
                        MostraAtributos(gameResult);
                    else
                        System.out.println("Game com ID " + idGame + " não encontrado.");
                    break;

                // Opçao 3 = Atualizar um registro
                case 3:
                    System.out.println("Digite o Id do Jogo que deseja atualizar");
                    var resultGame = crud.readHash(leitor.nextInt());

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
                        Game gameUpdate = new Game(resultGame.getId(), nameGame, releaseGame, requiredGame, priceGame,
                                descriptionGame, generesGame);

                        crud.update(gameUpdate);
                    } else {
                        System.out.println("Jogo não encontrado");
                    }
                    break;

                // Opçao 4 = Apagar um registro
                case 4:
                    System.out.println("Digite o id do jogo a ser deletado");
                    int idDelete = leitor.nextInt();
                    leitor.nextLine();

                    var resultado = crud.delete(idDelete);
                    if (resultado) {
                        System.out.println("Game com ID " + idDelete + " deletado com sucesso.");
                    } else
                        System.out.println("Game com ID " + idDelete + " não encontrado.");
                    break;

                // Opçao 5 = Busca por texto
                case 5:
                    String texto;

                    System.out.println("Digite o que deseja buscar: ");
                    texto = leitor.nextLine();

                    listainvertida.BuscarArquivoDeLista(texto);

                    break;

                case 6:
                    byte[] msgBytesLZW = Files.readAllBytes(Paths.get(crud.nomeArquivo));
                    nomeArquivoCompactado = crud.nomeArquivo + "LZWCompressao" + ".bd";
                    var compressed = LZW.compress(msgBytesLZW);

                    try (DataOutputStream out = new DataOutputStream(new FileOutputStream(nomeArquivoCompactado))) {
                        for (int code : compressed) {
                            out.writeShort(code);
                        }
                        System.out.println("Arquivo comprimido gerado: " + nomeArquivoCompactado);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Tamanho do arquivo comprimido em bytes (2 bytes por código)
                    int tamanhoComprimidoEmBytes = compressed.size() * 2;

                    // Tamanho original
                    int tamanhoOriginalEmBytes = msgBytesLZW.length;

                    float eficiencia = 100 * (1 - (float) tamanhoComprimidoEmBytes / tamanhoOriginalEmBytes);
                    System.out.println("Eficiência: " + eficiencia + "%");

                    System.out.println("codificado");
                    break;

                case 7:
                    List<Integer> compressedCodes = new ArrayList<>();
                    try (DataInputStream in = new DataInputStream(new FileInputStream(nomeArquivoCompactado))) {
                        while (in.available() >= 2) {
                            int code = in.readUnsignedShort();
                            compressedCodes.add(code);
                        }
                    }

                    byte[] decompressed = LZW.decompress(compressedCodes);
                    Files.write(Paths.get(crud.nomeArquivo), decompressed);
                    System.out.println("Descompressão finalizada!");

                    System.out.println("descodificado");
                    break;

                case 8:

                    CompressaoGames compressaoGames = new CompressaoGames();

                    compressaoGames.RodarCompressao();

                    break;

                case 10:
                    System.out.println("Digite o texto que você deseja buscar");
                    String palavra = leitor.nextLine();
                    List<Integer> resultadoKMP;

                    byte[] msgBytes = Files.readAllBytes(Paths.get(crud.nomeArquivo));
                    resultadoKMP = KMP.search(msgBytes, palavra.getBytes());

                    if (resultadoKMP.isEmpty()) {
                        System.out.println("Padrão não encontrado.");
                    } else {
                        System.out.println("Padrão encontrado nos índices: " + resultadoKMP);
                        for (int index : resultadoKMP) {
                            System.out.println("Ocorrência em " + index + ": "
                                    + new String(msgBytes, index, palavra.getBytes().length));
                        }
                    }

                    break;

                case 11:
                    System.out.println("Digite o texto que você deseja buscar");
                    String pattern = leitor.nextLine();
                    List<Integer> resultadoBoyer;

                    byte[] msgBytesBoyer = Files.readAllBytes(Paths.get(crud.nomeArquivo));
                    resultadoBoyer = BoyerMoore.search(msgBytesBoyer, pattern.getBytes());

                    if (resultadoBoyer.isEmpty()) {
                        System.out.println("Padrão não encontrado.");
                    } else {
                        System.out.println("Padrão encontrado nos índices: " + resultadoBoyer);
                        for (int index : resultadoBoyer) {
                            System.out.println("Ocorrência em " + index + ": "
                                    + new String(msgBytesBoyer, index, pattern.getBytes().length));
                        }
                    }
                    break;

                case 12:

                    System.out.println("Criptografando...");
                    criptografar.encryptBase();

                    break;
                case 13:

                    System.out.println("Descriptografando... ");

                    criptografar.descriptografarBase();

                    break;

                case 14:
                    try {
                        byte[] bytesArquivo = Files.readAllBytes(Paths.get(crud.nomeArquivo));
                        byte[] cripto = VigenereBytes.encrypt(bytesArquivo, chave);

                        try (OutputStream out = new FileOutputStream(arquivoVigenere)) {
                            out.write(cripto);
                        }

                        System.out.println("Arquivo criptografado com sucesso!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 15:
                    byte[] conteudoCriptografado = Files.readAllBytes(Paths.get(arquivoVigenere));
                    byte[] originalBytes = VigenereBytes.decrypt(conteudoCriptografado, chave);
                    Files.write(Paths.get(crud.nomeArquivo), originalBytes);
                    break;

                case 16:

                    System.out.println("\n");
                    System.out.println("=====================================");
                    System.out.println("=  Obrigado por utilizar o sistema  =");
                    System.out.println("=====================================");
                    System.out.println("\n");
                    opcao = 16;

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