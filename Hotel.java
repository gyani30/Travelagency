public class Hotel {
    public int id;
    public int destinationId;
    public String name;
    public double pricePerNight;
    public double rating;
    public String imagePath;

    public Hotel(int id, int destId, String name, double price, double rating, String img) {
        this.id = id;
        this.destinationId = destId;
        this.name = name;
        this.pricePerNight = price;
        this.rating = rating;
        this.imagePath = img;
    }
}
