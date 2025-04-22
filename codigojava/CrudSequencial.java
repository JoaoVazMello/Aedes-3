package codigojava;

import codigojava.HASH.Registro;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class CrudSequencial {
  public CrudSequencial() {
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

      for (Game game : objGame) {
        // Incrementa o último ID e atribui ao jogo
        ultimoID++;
        game.setAppID(ultimoID);

        // Obter os bytes do jogo usando o método arrayDeBytes()
        byte[] gameBytes = game.toByteArray();

        // Escrever a lápide (boolean)
        arquivo.writeBoolean(true); // 1 byte

        // Escrever o tamanho do registro
        arquivo.writeInt(gameBytes.length);

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
      // Abrir arquivo para escrita
      arquivo = new RandomAccessFile("Dados.bd", "rw");

      int id = PegarUltimoId() + 1;
      game.setAppID(id);

      // Obter os bytes do jogo usando o método arrayDeBytes()
      byte[] gameBytes = game.toByteArray();

      // Atualizar o ultimoID na posição 0
      arquivo.seek(0);
      arquivo.writeInt(id);

      // Posicionar o ponteiro no final do arquivo
      arquivo.seek(arquivo.length());

      // Escrever a lápide (boolean)
      arquivo.writeBoolean(true); // 1 byte


      // Escrever o tamanho do registro
      arquivo.writeInt(gameBytes.length);

      // Escrever os dados do jogo
      arquivo.write(gameBytes);

      System.out.println(game);

      System.out.println("Banco atualizado. Último ID inserido: " + id);

      return true;

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

    return false;
  }


  public Game LerGame(int appID) {
    RandomAccessFile Acesso = null;
    try {
      // Abrir o arquivo para leitura
      Acesso = new RandomAccessFile("Dados.bd", "r");
      Acesso.readInt(); // para ler o ultimo id e pular
      // Percorrer os registros no arquivo
      while (Acesso.getFilePointer() < Acesso.length()) {

        // Ler a lápide (boolean)
        boolean valido = Acesso.readBoolean();

        // Ler o tamanho do registro
        int tamanhoRegistro = Acesso.readInt(); // não conta com o id, a lapide e tamnho(4,1,4)

        // Verificar se é o game desejado e se está válido
        if (valido) {
          // Ler os bytes dos dados do jogo
          byte[] gameBytes = new byte[tamanhoRegistro];
          Acesso.readFully(gameBytes);
          Game game = new Game();
          game.fromByteArray(gameBytes);

          if(game.getId() == appID) {
            return game;
          }
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

  public boolean AtualizarGame(Game game) {
    try {
      RandomAccessFile arquivo = new RandomAccessFile("Dados.bd", "rw");
      arquivo.readInt();

      // Percorrer os registros no arquivo
      while (arquivo.getFilePointer() < arquivo.length()) {
        // Ler ID, lápide e tamanho do registro
        boolean valido = arquivo.readBoolean();
        int tamanhoRegistro = arquivo.readInt();

        if(valido) {
          byte[] gameBytes = new byte[tamanhoRegistro];
          arquivo.readFully(gameBytes);
          Game original = new Game();
          original.fromByteArray(gameBytes);
          // Se o ID do game original for encontrado e a lápide for válida
          if (game.getId() == original.getAppID()) {
            // Se os tamanhos dos jogos forem iguais
            if (game.toByteArray().length == original.toByteArray().length) {

              // Voltar à posição onde os dados do jogo começam
              long posicaoDados = arquivo.getFilePointer() - tamanhoRegistro;
              arquivo.seek(posicaoDados);

              // Calcular o número de bytes para os dados do jogo
              byte[] gameByteArray = game.toByteArray();

              // Escrever os novos dados (sem alterar lápide ou tamanho)
              arquivo.write(gameByteArray); // Sobrescrever os dados do jogo

              System.out.println("Jogo atualizado com sucesso!");
              break; // Terminar a execução após a atualização
            } else {

              long posicao = arquivo.getFilePointer() - (tamanhoRegistro + 5); // Voltar à posição da lápide
              arquivo.seek(posicao);
              arquivo.writeBoolean(false); // Apaga o jogo (marcando a lápide como falsa)
              System.out.println("Jogo original marcado como inválido.");

              // Adicionar o novo jogo no final
              game.setAppID(original.getAppID()); // Atualiza o ID do novo jogo
              byte[] gameBytesArray = game.toByteArray(); // Obter os bytes do jogo

              // Posicionar o ponteiro no final do arquivo
              arquivo.seek(arquivo.length());

              // Escrever o novo jogo no final
              arquivo.writeBoolean(true); // "true" para indicar que o objeto está ativo
              arquivo.writeInt(gameBytesArray.length); // Escrever o tamanho do registro
              arquivo.write(gameBytesArray); // Escrever os dados do jogo

              System.out.println("Jogo atualizado e adicionado no final com sucesso!");
              break; // Terminar a execução após a atualização
            }
        }

        } else {
          // Pular o registro se não for o jogo desejado
          arquivo.skipBytes(tamanhoRegistro); // Pular os bytes do registro
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

  public Game ApagarGame(int appID) {
    RandomAccessFile Acesso = null;
    try {
      // Abrir o arquivo para leitura
      Acesso = new RandomAccessFile("Dados.bd", "rw");
      Acesso.readInt(); // para ler o ultimo id e pular
      // Percorrer os registros no arquivo
      while (Acesso.getFilePointer() < Acesso.length()) {

        // Ler a lápide (boolean)
        boolean valido = Acesso.readBoolean();

        // Ler o tamanho do registro
        int tamanhoRegistro = Acesso.readInt(); // não conta com o id, a lapide e tamnho(4,1,4)

        // Verificar se é o game desejado e se está válido
        if (valido) {
          // Ler os bytes dos dados do jogo
          byte[] gameBytes = new byte[tamanhoRegistro];
          Acesso.readFully(gameBytes);
          Game game = new Game();
          game.fromByteArray(gameBytes);

          if(game.getId() == appID) {
            long posicao = Acesso.getFilePointer() - (tamanhoRegistro + 5); // Voltar à posição da lápide
            Acesso.seek(posicao);
            Acesso.writeBoolean(false); // Apaga o jogo (marcando a lápide como falsa)
            return game;
          }
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
