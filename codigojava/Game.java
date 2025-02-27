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
    for(String generos : objetoGame.Genres){
      System.out.println("Generos: " + generos);
    }
    System.out.println("==============================================================================");
    System.out.println("\n");
  }

  // Funçao para pegar o tamanho em Bytes do Registro
  public int getByte(Game objGame){

    int tamanho_Bytes = 0;

    try {
                      // AppID        // Name                                          // Data                                                        // Required     // PRICE   
      tamanho_Bytes = Integer.BYTES + getName().getBytes("UTF-8").length + getRelease().toString().getBytes("UTF-8").length + Integer.BYTES + Double.BYTES + 
      // Description
      getDescription().getBytes("UTF-8").length;

      // Percorre a lista para somar a quantidade de bytes
      for(String generes : getGenres()){
        tamanho_Bytes = tamanho_Bytes + generes.getBytes("UTF-8").length;
      }
      
    } catch (Exception e) {
      // TODO: handle exception
      System.out.println("Nao foi possivel calcular o tamanho");
      e.printStackTrace();
    }

    return tamanho_Bytes;
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
}
