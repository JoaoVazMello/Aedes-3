package codigojava;

import codigojava.HASH.Registro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Game implements Registro {
  private int AppID;
  private String Name;
  private LocalDate Release;
  private int Required;
  private double Price;
  private String Description;
  private ArrayList<String> Genres;

  public Game(int AppID, String Name, LocalDate Release, int Required, double Price, String Description,
      ArrayList<String> Genres) {
    this.AppID = AppID;
    this.Name = Name;
    this.Release = Release;
    this.Price = Price;
    this.Description = Description;
    this.Genres = Genres;
    this.Required = Required;
  }

  public Game() {
    this(-1, "", LocalDate.of(2000, 1, 1), 0, 0.0, "", new ArrayList<String>());
  }

  public Game(String name, LocalDate release, int required, double price, String description, ArrayList<String> genres) {
    this(-1, name, release, required, price, description, genres);
  }


  public static void MostraAtributos(Game objetoGame) {
    System.out.println("\n");
    System.out.println("================================== Atributos no Objeto ======================== ");
    System.out.println("AppID : " + objetoGame.AppID);
    System.out.println("Name : " + objetoGame.Name);
    System.out.println("Release : " + objetoGame.Release);
    System.out.println("Required : " + objetoGame.Required);
    System.out.println("Price : " + objetoGame.Price);
    System.out.println("Description : " + objetoGame.Description);
    System.out.print("Generos: ");
    for (String generos : objetoGame.Genres) {
      System.out.print(generos + ",");
    }
    System.err.println("\n");
    System.out.println("==============================================================================");
    System.out.println("\n");
  }

  // Funçao para pegar o tamanho em Bytes do Registro
  public int getByte(Game objGame) {
    int tamanho_Bytes = 0;

    try {
      // Nome (String + 2 bytes extras do writeUTF)
      tamanho_Bytes += objGame.getName().getBytes("UTF-8").length + 2;

      // Data de lançamento (long = 8 bytes)
      tamanho_Bytes += Long.BYTES;

      // Required age (int = 4 bytes)
      tamanho_Bytes += Integer.BYTES;

      // Preço (double = 8 bytes)
      tamanho_Bytes += Double.BYTES;

      // Descrição (máximo de 250 caracteres + 2 bytes extras)
      String descricao = objGame.getDescription();
      int tamanhoDescricao = Math.min(descricao.length(), 250); // Limita a 250 chars
      tamanho_Bytes += descricao.substring(0, tamanhoDescricao).getBytes("UTF-8").length + 2;

      // Lista de gêneros (cada String + 2 bytes extras)
      for (String genero : objGame.getGenres()) {
        tamanho_Bytes += genero.getBytes("UTF-8").length + 2;
      }

    } catch (Exception e) {
      System.out.println("Não foi possível calcular o tamanho");
      e.printStackTrace();
    }

    return tamanho_Bytes; // Este valor será salvo no campo "tamanhoRegistro"
  }

  @Override
  public void setId(int i) {
    this.AppID = i;
  }

  @Override
  public int getId() {
    return this.AppID;
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    // Escrever o ID do game
    dos.writeInt(AppID);

    // Escrever o nome do jogo (UTF-8)
    dos.writeUTF(Name != null ? Name : "");

    // Converter a data de lançamento para long (dias desde 1970)
    long data = Release != null ? ChronoUnit.DAYS.between(LocalDate.of(1970, 1, 1), Release) : -1;
    dos.writeLong(data);

    // Escrever os requisitos (int)
    dos.writeInt(Required);

    // Escrever o preço (double)
    dos.writeDouble(Price);

    // Escrever a descrição (limite de 250 caracteres)
    String descricao = Description != null ? Description : "";
    dos.writeUTF(descricao.length() > 250 ? descricao.substring(0, 250) : descricao);

    // Escrever os gêneros (lista de strings)
    dos.writeInt(Genres != null ? Genres.size() : 0);
    if (Genres != null) {
      for (String genero : Genres) {
        dos.writeUTF(genero != null ? genero : "");
      }
    }

    return baos.toByteArray();
  }

  public static Game recontruir(int id, byte[] gameBytes) {
    DataInputStream dis;
    try {
      dis = new DataInputStream(new ByteArrayInputStream(gameBytes));
      String name = dis.readUTF();

      long releaseDays = dis.readLong();

      LocalDate releaseDate = LocalDate
          .of(1970, 1, 1)
          .plusDays(releaseDays);

      int requiredAge = dis.readInt();

      double price = dis.readDouble();

      String description = dis.readUTF();

      ArrayList<String> genres = new ArrayList<>();
      int qtdGenres = dis.readInt();
      while (qtdGenres > 0) {
        genres.add(dis.readUTF());
        qtdGenres--;
      }

      dis.close();

      return new Game(id, name, releaseDate, requiredAge, price, description, genres);
    } catch (IOException ex) {
      System.out.println("Erro ao realizar o parse");
    }

    return null;
  }

  public void fromByteArray(byte[] b) throws IOException {
    DataInputStream dis;
    try {
      dis = new DataInputStream(new ByteArrayInputStream(b));
      int id = dis.readInt();
      String name = dis.readUTF();

      long releaseDays = dis.readLong();

      LocalDate releaseDate = LocalDate
              .of(1970, 1, 1)
              .plusDays(releaseDays);

      int requiredAge = dis.readInt();

      double price = dis.readDouble();

      String description = dis.readUTF();

      ArrayList<String> genres = new ArrayList<>();
      int qtdGenres = dis.readInt();
      while (qtdGenres > 0) {
        genres.add(dis.readUTF());
        qtdGenres--;
      }

      dis.close();

      this.AppID = id;
      this.Name = name;
      this.Release = releaseDate;
      this.Price = price;
      this.Description = description;
      this.Genres = genres;
      this.Required = requiredAge;


    } catch (IOException ex) {
      System.out.println("Erro ao realizar o parse");
      throw  new IOException();
    }

  }

  public int getAppID() {
    return AppID;
  }

  public void setAppID(int appID) {
    AppID = appID;
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public LocalDate getRelease() {
    return Release;
  }

  public void setRelease(LocalDate release) {
    Release = release;
  }

  public int getRequired() {
    return Required;
  }

  public void setRequired(int required) {
    Required = required;
  }

  public double getPrice() {
    return Price;
  }

  public void setPrice(double price) {
    Price = price;
  }

  public String getDescription() {
    return Description;
  }

  public void setDescription(String description) {
    Description = description;
  }

  public ArrayList<String> getGenres() {
    return Genres;
  }

  public void setGenres(ArrayList<String> genres) {
    Genres = genres;
  }

  @Override
  public String toString() {
    return "Game{ " +
        "AppID=" + AppID +
        ", Name='" + Name + '\'' +
        ", Release=" + Release +
        ", Required=" + Required +
        ", Price=" + Price +
        ", Description='" + Description + '\'' +
        ", Genres=" + Genres +
        '}';
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + AppID;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Game other = (Game) obj;
    if (AppID != other.AppID)
      return false;
    return true;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return super.clone();
  }

}
