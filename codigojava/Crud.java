package codigojava;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
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
      DataOutputStream dos = new DataOutputStream(new FileOutputStream("Dados.bd"));

      // Objeto para acessar arquivo
      RandomAccessFile Acesso = new RandomAccessFile("Dados.bd", "rw");

      // Ultimo Id inserid

      // Insere o ID do primeiro no cabeçalho (Posso fazer isso perguntar hayala)
      dos.writeInt(ultimoID);

      for (Game game : objGame) {
        ultimoID++;

        // Salve o ID que esta sendo inserido agora
        game.setAppID(ultimoID);

        // Escreve o ID do game
        dos.writeInt(game.getAppID());

        //lapide do dado
        // true indica que ele é válido/acessível
        dos.writeBoolean(true);

        // Escreve o tamanho do Registro em bytes
        int QuantidadeDeBytesRegistro = game.getByte(game);
        dos.writeInt(QuantidadeDeBytesRegistro);

        // Escreve o Nome do Game
        dos.writeUTF(game.getName());

        // Escreve a data de lançamento(Conferir com o hayala)
        long data = ChronoUnit.DAYS.between(LocalDate.of(1970, 1, 1), game.getRelease());
        dos.writeLong(data);

        // Escreve a idade para jogar o Game
        dos.writeInt(game.getRequired());

        // Escreve o Preço do game
        dos.writeDouble(game.getPrice());

        // Cria o objeto SB para utilizar dps
        StringBuilder sb = new StringBuilder(game.getDescription());

        // Teste se a string tem mais de 250 caracteres se possuir apaga todos para
        // frente
        if (sb.length() > 250) {
          sb.delete(251, sb.length());
        }

        // Escreve a descriçao
        dos.writeUTF(sb.toString());

        // Escreve a lista de Generos dos jogos
        for (String generes : game.getGenres()) {
          dos.writeUTF(generes);
        }

        // Acessa a posiçao 0 do Arquivo
        Acesso.seek(0);

        // Atualiza o UltimoId
        Acesso.writeInt(ultimoID);

        System.out.println("Último id inserido" + ultimoID);

        // Volta o ponteiro para o final do arquivo
        Acesso.seek(Acesso.length());
      }

      Acesso.close();
      dos.close();

    } catch (Exception e) {
      // TODO: handle exception
      System.out.println("Nao foi possivel Criar o Banco");
      e.printStackTrace();
    }
  }

  public void EscreverNovoGame(Game game) {
    try {
      RandomAccessFile Acesso = new RandomAccessFile("Dados.bd", "rw");
      Acesso.seek(Acesso.length());

      ultimoID = game.getAppID();

      // Escreve o ID do game
      Acesso.writeInt(game.getAppID());

      //lapide do dado
      // true indica que ele é válido/acessível
      Acesso.writeBoolean(true);

      // Escreve o tamanho do Registro em bytes
      int QuantidadeDeBytesRegistro = game.getByte(game);
      Acesso.writeInt(QuantidadeDeBytesRegistro);

      // Escreve o Nome do Game
      Acesso.writeUTF(game.getName());

      // Escreve a data de lançamento(Conferir com o hayala)
      long data = ChronoUnit.DAYS.between(LocalDate.of(1970, 1, 1), game.getRelease());
      Acesso.writeLong(data);

      // Escreve a idade para jogar o Game
      Acesso.writeInt(game.getRequired());

      // Escreve o Preço do game
      Acesso.writeDouble(game.getPrice());

      // Cria o objeto SB para utilizar dps
      StringBuilder sb = new StringBuilder(game.getDescription());

      // Teste se a string tem mais de 250 caracteres se possuir apaga todos para
      // frente
      if (sb.length() > 250) {
        sb.delete(251, sb.length());
      }

      // Escreve a descriçao
      Acesso.writeUTF(sb.toString());

      // Escreve a lista de Generos dos jogos
      for (String generes : game.getGenres()) {
        Acesso.writeUTF(generes);
      }

      // Acessa a posiçao 0 do Arquivo
      Acesso.seek(0);

      // Atualiza o UltimoId
      Acesso.writeInt(ultimoID);

      Acesso.close();

    } catch (Exception e) {
      // TODO: handle exception
      System.out.println("Nao foi adicionar novo Game " + game);
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
}
