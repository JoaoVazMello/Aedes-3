package codigojava.HASH;

import codigojava.ARVORE.ArvoreBMais;
import codigojava.ARVORE.ParIDEnderecoArvore;
import codigojava.Game;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CrudHash<T extends Registro> {
    final int TAM_CABECALHO = 12;
    RandomAccessFile arquivo;
    public String nomeArquivo;
    Constructor<T> construtor;
    HashExtensivel<ParIDEndereco> indiceDireto;
    ArvoreBMais<ParIDEnderecoArvore> arvoreBMais;
    String na;
    int ordem;

    public CrudHash(String na, Constructor<T> c, int ordem) throws Exception {
        this.na = na;
        this.ordem = ordem;
        File d = new File(".\\dados");
        if (!d.exists())
            d.mkdir();

        d = new File(".\\dados\\" + na);
        if (!d.exists())
            d.mkdir();

        this.nomeArquivo = ".\\dados\\" + na + "\\" + na + ".bd";
        this.construtor = c;
        arquivo = new RandomAccessFile(this.nomeArquivo, "rw");
        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0); // último ID
            arquivo.writeLong(-1); // lista de registros marcados para exclusão
        }

        indiceDireto = new HashExtensivel<>(
                ParIDEndereco.class.getConstructor(),
                4,
                ".\\dados\\" + na + "\\" + na + ".d.bd", // diretório
                ".\\dados\\" + na + "\\" + na + ".c.bd" // cestos
        );

        arvoreBMais = new ArvoreBMais<>(
                ParIDEnderecoArvore.class.getConstructor(),
                ordem,
                "C:\\Users\\joaoe\\OneDrive\\Desktop\\Faculdade\\TpAedes\\codigojava\\dados\\indices.db");
    }


    public int create(List<T> objs) throws Exception {
        arquivo.setLength(0);
        arquivo.writeInt(0); // último ID
        arquivo.writeLong(-1); // lista de registros marcados para exclusão

        indiceDireto.limpaArq();
        indiceDireto = new HashExtensivel<>(
                ParIDEndereco.class.getConstructor(),
                4,
                ".\\dados\\" + na + "\\" + na + ".d.bd", // diretório
                ".\\dados\\" + na + "\\" + na + ".c.bd" // cestos
        );

        arvoreBMais.limparArquivo();
        arvoreBMais = new ArvoreBMais<>(
                ParIDEnderecoArvore.class.getConstructor(),
                ordem,
                "C:\\Users\\joaoe\\OneDrive\\Desktop\\Faculdade\\TpAedes\\codigojava\\dados\\indices.db");

        int proximoID = 1;

        for (T obj : objs) {
            obj.setId(proximoID);
            byte[] b = obj.toByteArray();
            long endereco = arquivo.getFilePointer();
            arquivo.writeBoolean(true); // lápide
            arquivo.writeShort(b.length); // tamanho do vetor de bytes
            arquivo.write(b); // vetor de bytes

            indiceDireto.create(new ParIDEndereco(proximoID, endereco));
            arvoreBMais.create(new ParIDEnderecoArvore(proximoID, endereco));

            proximoID++;
            System.out.println(obj);
        }

        --proximoID;
        arquivo.seek(0);
        arquivo.writeInt(proximoID);

        return 1;
    }

    public int create(T obj) throws Exception {
        arquivo.seek(0);
        int proximoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(proximoID);
        obj.setId(proximoID);
        byte[] b = obj.toByteArray();

        long endereco = getDeleted(b.length); // tenta reusar algum espaço de registro excluído
        if (endereco == -1) { // nenhum espaço disponível; escreve o registro no fim do arquivo
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
            arquivo.writeBoolean(true); // lápide
            arquivo.writeShort(b.length); // tamanho do vetor de bytes
            arquivo.write(b); // vetor de bytes
        } else {
            arquivo.seek(endereco);
            arquivo.writeBoolean(true); // limpa o lápide
            arquivo.skipBytes(2); // pula o indicador de tamanho para preservá-lo
            arquivo.write(b); // vetor de bytes
        }


        indiceDireto.create(new ParIDEndereco(proximoID, endereco));
        arvoreBMais.create(new ParIDEnderecoArvore(proximoID, endereco));

        return obj.getId();
    }

    public Game read(int appID) {
        try {
            // Abrir o arquivo para leitura
            arquivo.readInt(); // para ler o ultimo id e pular
            arquivo.readLong(); // para ler espacos vazios
            // Percorrer os registros no arquivo
            while (arquivo.getFilePointer() < arquivo.length()) {

                // Ler a lápide (boolean)
                boolean valido = arquivo.readBoolean();

                // Ler o tamanho do registro
                int tamanhoRegistro = arquivo.readShort(); // não conta com o id, a lapide e tamnho(4,1,4)

                // Verificar se é o game desejado e se está válido
                if (valido) {
                    // Ler os bytes dos dados do jogo
                    byte[] gameBytes = new byte[tamanhoRegistro];
                    arquivo.readFully(gameBytes);
                    Game game = new Game();
                    game.fromByteArray(gameBytes);

                    if (game.getId() == appID) {
                        return game;
                    }
                } else {
                    // Pular o registro atual, se não for o game desejado
                    arquivo.skipBytes(tamanhoRegistro);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler o game do arquivo.");
            e.printStackTrace();
        }


        return null; // Caso o game não seja encontrado
    }

    public T readHash(int id) throws Exception {
        T obj;
        short tam;
        byte[] b;
        boolean lapide;

        ParIDEndereco pid = indiceDireto.read(id);
        if(pid!=null) {
            arquivo.seek(pid.getEndereco());
            obj = construtor.newInstance();
            lapide = arquivo.readBoolean();
            if(lapide) {
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                obj.fromByteArray(b);
                if(obj.getId()==id)
                    return obj;
            }
        }
        return null;
    }

    public T readArvore(int id) throws Exception {
        T obj;
        short tam;
        byte[] b;
        boolean lapide;

        ArrayList<ParIDEnderecoArvore> resultados = arvoreBMais.read(new ParIDEnderecoArvore(id, -1));

        if(!resultados.isEmpty()) {
            var pid = resultados.getFirst();

            arquivo.seek(pid.getEndereco());
            obj = construtor.newInstance();
            lapide = arquivo.readBoolean();
            if(lapide) {
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                obj.fromByteArray(b);
                if(obj.getId()==id)
                    return obj;
            }
        }
        return null;
    }

    public boolean delete(int id) throws Exception {
        T obj;
        short tam;
        byte[] b;
        boolean lapide;

        ParIDEndereco pie = indiceDireto.read(id);
        if(pie!=null) {
            arquivo.seek(pie.getEndereco());
            obj = construtor.newInstance();
            lapide = arquivo.readBoolean();
            if(lapide) {
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                obj.fromByteArray(b);
                if(obj.getId()==id) {
                    if(indiceDireto.delete(id) && arvoreBMais.delete(new ParIDEnderecoArvore(pie.getId(), pie.getEndereco()))) {
                        arquivo.seek(pie.getEndereco());
                        arquivo.writeBoolean(false);
                        addDeleted(tam, pie.getEndereco());
                        System.out.println(obj);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public boolean update(T novoObj) throws Exception {
        T obj;
        short tam;
        byte[] b;
        boolean lapide;
        ParIDEndereco pie = indiceDireto.read(novoObj.getId());
        if(pie!=null) {
            arquivo.seek(pie.getEndereco());
            obj = construtor.newInstance();
            lapide = arquivo.readBoolean();
            if(lapide) {
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                obj.fromByteArray(b);
                if(obj.getId()==novoObj.getId()) {

                    byte[] b2 = novoObj.toByteArray();
                    short tam2 = (short)b2.length;

                    // sobrescreve o registro
                    if(tam2 <= tam) {
                        arquivo.seek(pie.getEndereco()+3);
                        arquivo.write(b2);
                    }

                    // move o novo registro para o fim
                    else {
                        // exclui o registro anterior
                        arquivo.seek(pie.getEndereco());
                        arquivo.writeBoolean(false);
                        addDeleted(tam, pie.getEndereco());                        

                        // grava o novo registro
                        long novoEndereco = getDeleted(b.length);   // tenta reusar algum espaço de registro excluído
                        if(novoEndereco == -1) {   // nenhum espaço disponível; escreve o registro no fim do arquivo  
                            arquivo.seek(arquivo.length());
                            novoEndereco = arquivo.getFilePointer();
                            arquivo.writeBoolean(true);       // lápide
                            arquivo.writeShort(tam2);       // tamanho do vetor de bytes
                            arquivo.write(b2);              // vetor de bytes
                        } else {
                            arquivo.seek(novoEndereco);
                            arquivo.writeBoolean(true);    // limpa o lápide
                            arquivo.skipBytes(2);         // pula o indicador de tamanho para preservá-lo
                            arquivo.write(b2);              // vetor de bytes
                        }

                        // atualiza o índice direto
                        indiceDireto.update(new ParIDEndereco(novoObj.getId(), novoEndereco));
                        arvoreBMais.delete(new ParIDEnderecoArvore(pie.getId(), pie.getEndereco()));
                        arvoreBMais.create(new ParIDEnderecoArvore(novoObj.getId(), novoEndereco));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // adiciona um registro à lista de excluídos (espaços disponíveis para reuso)
    public void addDeleted(int tamanhoEspaco, long enderecoEspaco) throws Exception {
        long anterior = 4; // início da lista
        arquivo.seek(anterior);
        long endereco = arquivo.readLong(); // endereço do elemento que será testado
        long proximo = -1; // endereço do elemento seguinte da lista
        int tamanho;
        if(endereco==-1) {  // lista vazia
            arquivo.seek(4);
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco+3);
            arquivo.writeLong(-1);
        } else {
            do {
                arquivo.seek(endereco+1);
                tamanho = arquivo.readShort();
                proximo = arquivo.readLong();
                if(tamanho > tamanhoEspaco) {  // encontrou a posição de inserção (antes do elemento atual)
                    if(anterior == 4) // será o primeiro elemento da lista
                        arquivo.seek(anterior);
                    else
                        arquivo.seek(anterior+3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco+3);
                    arquivo.writeLong(endereco);
                    break;
                }
                if(proximo == -1) {  // fim da lista
                    arquivo.seek(endereco+3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco+3);
                    arquivo.writeLong(+1);
                    break;
                }
                anterior = endereco;
                endereco = proximo;
            } while (endereco!=-1);
        }
    }
    
    // retira um registro à lista de excluídos para reuso, mas com o risco de algum desperdício
    // se necessário, o código pode ser alterado para controlar um limite máximo de desperdício
    public long getDeleted(int tamanhoNecessario) throws Exception {

//        tamanhoNecessario = 0;
//        if(tamanhoNecessario == 0) return -1;
        long anterior = 4; // início da lista
        arquivo.seek(anterior);
        long endereco = arquivo.readLong(); // endereço do elemento que será testado
        long proximo = -1; // endereço do elemento seguinte da lista
        int tamanho;
        while(endereco != -1) {
            arquivo.seek(endereco+1);
            tamanho = arquivo.readShort();
            proximo = arquivo.readLong();
            if(tamanho > tamanhoNecessario) {  
                if(anterior == 4)  // o elemento é o primeiro da lista 
                    arquivo.seek(anterior);
                else
                    arquivo.seek(anterior+3);
                arquivo.writeLong(proximo);
                break;
            }
            anterior = endereco;
            endereco = proximo;
        }
        return endereco;
    }

    public void close() throws Exception {
        arquivo.close();
        indiceDireto.close();
    }

}