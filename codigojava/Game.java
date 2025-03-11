package codigojava;
import java.time.LocalDate;
import java.util.ArrayList;

public class Game {
  private int AppID;
  private String Name;
  private LocalDate Release;
  private int Required;
  private double Price;
  private String Description;
  private ArrayList<String> Genres;

  public Game(int AppID, String Name, LocalDate Release, int Required, double Price, String Description, ArrayList<String> Genres){
    this.AppID = AppID;
    this.Name = Name;
    this.Release = Release;
    this.Price = Price;
    this.Description = Description;
    this.Genres = Genres;
    this.Required = Required;
  }

  public void MostraAtributos(Game objetoGame){
    System.out.println("\n");
    System.out.println("================================== Atributos no Objeto ======================== ");
    System.out.println("AppID : " + objetoGame.AppID);
    System.out.println("Name : " + objetoGame.Name);
    System.out.println("Release : " + objetoGame.Release);
    System.out.println("Required : " + objetoGame.Required);
    System.out.println("Price : " + objetoGame.Price);
    System.out.println("Description : " + objetoGame.Description);
    System.out.print("Generos: ");
    for(String generos : objetoGame.Genres){
      System.out.print(generos+ ",");
    }
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
}
