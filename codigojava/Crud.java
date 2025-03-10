package codigojava;

import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Crud {

  private int ultimoID;

  public Crud() {
    ultimoID = 0;
  }

  // Funçao que vai escrever no Binario
  public void EscreverBinario(ArrayList<Game> objGame) {
    try {
      // Abrir arquivo para escrita
      RandomAccessFile arquivo = new RandomAccessFile("Dados.bd", "rw");

      // inicia o arquivo com o ultimo id
      arquivo.writeInt(0);

      // Posicionar o ponteiro no final do arquivo
      arquivo.seek(arquivo.length());

      for (Game game : objGame) {
        // Incrementa o último ID e atribui ao jogo
        ultimoID++;
        game.setAppID(ultimoID);

        // Escreve o ID do game
        arquivo.writeInt(game.getAppID());

        // Lápide, boolean pois ele é um bit
        arquivo.writeBoolean(true);

        // Calcular tamanho do registro
        int tamanhoRegistro = game.getByte(game);
        arquivo.writeInt(tamanhoRegistro);

        // Escreve os dados do jogo
        arquivo.writeUTF(game.getName());
        long data = ChronoUnit.DAYS.between(LocalDate.of(1970, 1, 1), game.getRelease());
        arquivo.writeLong(data);
        arquivo.writeInt(game.getRequired());
        arquivo.writeDouble(game.getPrice());

        // Escreve a descrição (limite de 250 caracteres)
        String descricao = game.getDescription().length() > 250
            ? game.getDescription().substring(0, 250)
            : game.getDescription();
        arquivo.writeUTF(descricao);

        // Escreve os gêneros
        for (String genero : game.getGenres()) {
          arquivo.writeUTF(genero);
        }
      }

      // Atualizar o ultimoID na posição 0
      arquivo.seek(0);
      arquivo.writeInt(ultimoID);

      // Fechar o arquivo
      arquivo.close();

      System.out.println("Banco atualizado. Último ID inserido: " + ultimoID);
    } catch (Exception e) {
      System.out.println("Não foi possível criar o banco.");
      e.printStackTrace();
    }
  }

  public void EscreverNovoGame(Game game) {
    try {
      // definindo o ultimo id
      ultimoID =  game.getAppID();

      // Abrir arquivo para escrita
      RandomAccessFile arquivo = new RandomAccessFile("Dados.bd", "rw");

      // Posicionar o ponteiro no final do arquivo
      arquivo.seek(arquivo.length());

      // Escreve o ID do game
      arquivo.writeInt(game.getAppID());

      // Lápide, boolean pois ele é um bit
      arquivo.writeBoolean(true);

      // Calcular tamanho do registro
      int tamanhoRegistro = game.getByte(game);
      arquivo.writeInt(tamanhoRegistro);

      // Escreve os dados do jogo
      arquivo.writeUTF(game.getName());
      long data = ChronoUnit.DAYS.between(LocalDate.of(1970, 1, 1), game.getRelease());
      arquivo.writeLong(data);
      arquivo.writeInt(game.getRequired());
      arquivo.writeDouble(game.getPrice());

      // Escreve a descrição (limite de 250 caracteres)
      String descricao = game.getDescription().length() > 250
          ? game.getDescription().substring(0, 250)
          : game.getDescription();
      arquivo.writeUTF(descricao);

      // Escreve os gêneros
      for (String genero : game.getGenres()) {
        arquivo.writeUTF(genero);
      }

      // Atualizar o ultimoID na posição 0
      arquivo.seek(0);
      arquivo.writeInt(ultimoID);

      // Fechar o arquivo
      arquivo.close();

      System.out.println("Banco atualizado. Último ID inserido: " + ultimoID);
    } catch (Exception e) {
      System.out.println("Não foi possível criar o banco.");
      e.printStackTrace();
    }
  }

  public int PegarUltimoId() {
    int id = 0;
    try {
      RandomAccessFile Acesso = new RandomAccessFile("Dados.bd", "rw");
      Acesso.seek(0);
      id = Acesso.readInt();
      Acesso.close();
    } catch (Exception e) {
      System.out.println("Error ao pegar ultimo id");
      e.printStackTrace();
    }

    return id;
  }

  public Game LerGame(int appID) {
    try {
      RandomAccessFile Acesso = new RandomAccessFile("Dados.bd", "r");
      Acesso.seek(4); // Pula o primeiro inteiro (ultimoID)

      while (Acesso.getFilePointer() < Acesso.length()) {
        int id = Acesso.readInt(); // Lê o ID do game
        boolean valido = Acesso.readBoolean(); // Lê a lápide
        int tamanhoRegistro = Acesso.readInt(); // Lê o tamanho do registro

        if (id == appID && valido) {
          // Se for o ID buscado e o registro for válido, lê os dados do game
          String name = Acesso.readUTF();
          long releaseDays = Acesso.readLong();
          LocalDate releaseDate = LocalDate.of(1970, 1, 1).plusDays(releaseDays);
          int requiredAge = Acesso.readInt();
          double price = Acesso.readDouble();
          String description = Acesso.readUTF();

          // Lê os gêneros
          ArrayList<String> genres = new ArrayList<>();
          int bytesLidos = 4 + 1 + 4; // ID (4) + lápide (1) + tamanhoRegistro (4)

          bytesLidos += name.getBytes("UTF-8").length + 2; // Nome (UTF)
          bytesLidos += 8; // Data de lançamento (long)
          bytesLidos += 4; // Required age (int)
          bytesLidos += 8; // Preço (double)
          bytesLidos += description.getBytes("UTF-8").length + 2; // Descrição (UTF)

          while (bytesLidos < tamanhoRegistro + 4) { // +4 por causa do próprio campo tamanhoRegistro
            String genre = Acesso.readUTF();
            genres.add(genre);
            bytesLidos += genre.getBytes("UTF-8").length + 2; // Cada string + 2 bytes extras do UTF
          }

          Acesso.close();
          return new Game(id, name, releaseDate, requiredAge, price, description, genres);
        } else {
          Acesso.skipBytes(tamanhoRegistro); // Pula o registro inteiro
        }
      }

      Acesso.close();
    } catch (Exception e) {
      System.out.println("Erro ao ler o game do arquivo.");
      e.printStackTrace();
    }
    return null;
  }

  public boolean ApagarGame(int appID) {
    try {
      RandomAccessFile Acesso = new RandomAccessFile("Dados.bd", "rw");
      Acesso.seek(4); // Pula o primeiro inteiro (ultimoID)

      while (Acesso.getFilePointer() < Acesso.length()) {
        int id = Acesso.readInt(); // Lê o ID do game
        boolean valido = Acesso.readBoolean(); // Lê a lápide

        if (id == appID && valido) {
          Acesso.seek(Acesso.getFilePointer() - 1); // Volta para a lápide
          Acesso.writeBoolean(false); // O game não é mais valido, lápide se torna false
          Acesso.close();
          return true;
        } else {
          int tamanhoRegistro = Acesso.readInt(); // Lê o tamanho do registro
          Acesso.skipBytes(tamanhoRegistro); // Pula o registro inteiro
        }
      }

      Acesso.close(); // Fecha o arquivo ao final
    } catch (Exception e) {
      System.out.println("Erro ao ler o game do arquivo.");
      e.printStackTrace();
    }
    return false;
  }


}
