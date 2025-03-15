package codigojava;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Crud {
  public Crud() {
  }

  public void EscreverBinario(ArrayList<Game> objGame) {
    RandomAccessFile arquivo = null;
    int ultimoID = 0;
    try {
      // Abrir arquivo para escrita
      arquivo = new RandomAccessFile("Dados.bd", "rw");

      // Zera o arquivo e inicia com o último ID
      arquivo.setLength(0);
      arquivo.writeInt(0); // Último ID no início

      // Posicionar o ponteiro no final do arquivo
      arquivo.seek(arquivo.length());

      // Incrementa o último ID e escreve os dados de cada jogo
      for (Game game : objGame) {
        // Incrementa o último ID e atribui ao jogo
        ultimoID++;
        game.setAppID(ultimoID);

        // Obter os bytes do jogo usando o método arrayDeBytes()
        byte[] gameBytes = game.arrayDeBytes();

        // Calcular o tamanho do registro (ID + lápide + tamanho + dados do jogo)
        int tamanhoRegistro = 4 + 1 + 4 + gameBytes.length; // 4 bytes para ID, 1 byte para lápide, 4 bytes para tamanho

        // Escrever o ID do game
        arquivo.writeInt(game.getAppID());

        // Escrever a lápide (boolean)
        arquivo.writeBoolean(true); // 1 byte

        // Escrever o tamanho do registro
        arquivo.writeInt(tamanhoRegistro);

        // Escrever os dados do jogo
        arquivo.write(gameBytes);

        System.out.println(game);
      }

      // Atualizar o ultimoID na posição 0
      arquivo.seek(0);
      arquivo.writeInt(ultimoID);

      System.out.println("Banco atualizado. Último ID inserido: " + ultimoID);
    } catch (Exception e) {
      System.out.println("Não foi possível criar o banco.");
      e.printStackTrace();
    } finally {
      try {
        // Fechar o arquivo após o processamento
        if (arquivo != null) {
          arquivo.close();
        }
      } catch (IOException e) {
        System.out.println("Erro ao fechar o arquivo.");
        e.printStackTrace();
      }
    }
  }

  public boolean EscreverNovoGame(Game game) {
    RandomAccessFile arquivo = null;
    try {
      int id = PegarUltimoId() + 1;

      // Abrir arquivo para escrita
      arquivo = new RandomAccessFile("Dados.bd", "rw");

      // Posicionar o ponteiro no final do arquivo
      arquivo.seek(arquivo.length());

      // Obter os bytes do jogo usando o método arrayDeBytes()
      byte[] gameBytes = game.arrayDeBytes();

      // Calcular o tamanho do registro (ID + lápide + tamanho + dados do jogo)
      int tamanhoRegistro = 4 + 1 + 4 + gameBytes.length; // 4 bytes para ID, 1 byte para lápide, 4 bytes para tamanho

      // Escrever o ID do game
      arquivo.writeInt(id);

      // Escrever a lápide (boolean)
      arquivo.writeBoolean(true); // 1 byte

      // Escrever o tamanho do registro
      arquivo.writeInt(tamanhoRegistro);

      // Escrever os dados do jogo
      arquivo.write(gameBytes);

      System.out.println(game);

      // Atualizar o ultimoID na posição 0
      arquivo.seek(0);
      arquivo.writeInt(id);
      System.out.println("Banco atualizado. Último ID inserido: " + id);

      return true;

    } catch (

    Exception e) {
      System.out.println("Não foi possível criar o banco.");
      e.printStackTrace();
    } finally {
      try {
        // Fechar o arquivo após o processamento
        if (arquivo != null) {
          arquivo.close();
        }
      } catch (IOException e) {
        System.out.println("Erro ao fechar o arquivo.");
        e.printStackTrace();
      }
    }

    return false;
  }

  public boolean AtualizarGame(Game game, Game original) {
    try {
      RandomAccessFile arquivo = new RandomAccessFile("Dados.bd", "rw");

      // Ler o último ID no arquivo (não será alterado, pois estamos apenas marcando a
      // lápide e adicionando no final)
      arquivo.readInt();

      // Percorrer os registros no arquivo
      while (arquivo.getFilePointer() < arquivo.length()) {
        // Ler ID, lápide e tamanho do registro
        int id = arquivo.readInt();
        boolean valido = arquivo.readBoolean();
        int tamanhoRegistro = arquivo.readInt();

        // Se o ID do game original for encontrado e a lápide for válida
        if (id == original.getAppID() && valido) {
          // Se os tamanhos dos jogos forem iguais
          if (game.arrayDeBytes().length == original.arrayDeBytes().length) {
            // Voltar à posição onde os dados do jogo começam
            long posicaoDados = arquivo.getFilePointer();

            // Calcular o número de bytes para os dados do jogo
            byte[] gameBytes = game.arrayDeBytes();
            arquivo.seek(posicaoDados);

            // Escrever os novos dados (sem alterar ID, lápide ou tamanho)
            arquivo.write(gameBytes); // Sobrescrever os dados do jogo

            System.out.println("Jogo atualizado com sucesso!");
            break; // Terminar a execução após a atualização
          } else {

            long posicao = arquivo.getFilePointer() - 1 - 4; // Voltar à posição da lápide
            arquivo.seek(posicao);
            arquivo.writeBoolean(false); // Apaga o jogo (marcando a lápide como falsa)
            System.out.println("Jogo original marcado como inválido.");

            // Adicionar o novo jogo no final
            game.setAppID(original.getAppID()); // Atualiza o ID do novo jogo
            byte[] gameBytes = game.arrayDeBytes(); // Obter os bytes do jogo

            // Calcular o tamanho do registro
            int tamanhoRegistroNovo = 4 + 1 + 4 + gameBytes.length; // 4 bytes para ID, 1 byte para lápide, 4 bytes para
                                                                    // tamanho, e os dados

            // Posicionar o ponteiro no final do arquivo
            arquivo.seek(arquivo.length());

            // Escrever o novo jogo no final
            arquivo.writeInt(game.getAppID()); // Escrever o ID do jogo
            arquivo.writeBoolean(true); // "true" para indicar que o objeto está ativo
            arquivo.writeInt(tamanhoRegistroNovo); // Escrever o tamanho do registro
            arquivo.write(gameBytes); // Escrever os dados do jogo

            System.out.println("Jogo atualizado e adicionado no final com sucesso!");
            break; // Terminar a execução após a atualização
          }
        } else {
          // Pular o registro se não for o jogo desejado
          arquivo.skipBytes(tamanhoRegistro - 4 - 1 - 4); // Pular os bytes do registro
        }
      }

      arquivo.close();
      return true;
    } catch (Exception e) {
      System.out.println("Erro ao atualizar o game.");
      e.printStackTrace();
      return false;
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
    RandomAccessFile Acesso = null;
    try {
      // Abrir o arquivo para leitura
      Acesso = new RandomAccessFile("Dados.bd", "r");
      Acesso.readInt(); // para ler o ultimo id e pular
      // Percorrer os registros no arquivo
      while (Acesso.getFilePointer() < Acesso.length()) {

        // Ler o ID do game
        int id = Acesso.readInt();

        // Ler a lápide (boolean)
        boolean valido = Acesso.readBoolean();

        // Ler o tamanho do registro
        int tamanhoRegistro = Acesso.readInt() - 9; // não conta com o id, a lapide e tamnho(4,1,4)

        // Verificar se é o game desejado e se está válido
        if (id == appID && valido) {
          // Ler os bytes dos dados do jogo
          byte[] gameBytes = new byte[tamanhoRegistro];
          Acesso.readFully(gameBytes);

          return Game.recontruir(id, gameBytes);
        } else {
          // Pular o registro atual, se não for o game desejado
          Acesso.skipBytes(tamanhoRegistro);
        }
      }
    } catch (Exception e) {
      System.out.println("Erro ao ler o game do arquivo.");
      e.printStackTrace();
    } finally {
      try {
        // Fechar o arquivo
        if (Acesso != null) {
          Acesso.close();
        }
      } catch (IOException e) {
        System.out.println("Erro ao fechar o arquivo.");
        e.printStackTrace();
      }
    }

    return null; // Caso o game não seja encontrado
  }

  public boolean ApagarGame(int appID) {
    try {
      RandomAccessFile Acesso = new RandomAccessFile("Dados.bd", "rw");
      Acesso.seek(4); // Pula o ultimoID

      while (Acesso.getFilePointer() < Acesso.length()) {
        int id = Acesso.readInt(); // Lê o ID do game
        boolean valido = Acesso.readBoolean(); // Lê a lápide

        if (id == appID && valido) {
          Acesso.seek(Acesso.getFilePointer() - 1); // Volta para a lápide
          Acesso.writeBoolean(false); // O game não é mais valido, lápide se torna false
          Acesso.close();
          return true;
        } else {
          int tamanhoRegistro = Acesso.readInt() - 9; // Lê o tamanho do registro
          Acesso.skipBytes(tamanhoRegistro); // Pula o registro inteiro
        }
      }

      Acesso.close();
    } catch (Exception e) {
      System.out.println("Erro ao ler o game do arquivo.");
      e.printStackTrace();
    }
    return false;
  }

}
