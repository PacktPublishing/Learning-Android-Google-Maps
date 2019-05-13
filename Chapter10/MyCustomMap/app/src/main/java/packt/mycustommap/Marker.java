package packt.mycustommap;


public class Marker {

    private int _id;
    private String _name;
    private String _latitude;
    private String _longitude;
    private String _address;
    private String _color;

    public Marker(){

    }

    public Marker(int id,String name,String latitude, String longitude, String address, String color) {

        this._id = id;
        this._name = name;
        this._latitude = latitude;
        this._longitude = longitude;
        this._address = address;
        this._color = color;
    }
    public Marker(String name,String latitude, String longitude, String address, String color) {

        this._name = name;
        this._latitude = latitude;
        this._longitude = longitude;
        this._address = address;
        this._color = color;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public String getLatitude(){
        return this._latitude;
    }

    public void setLatitude(String latitude){
        this._latitude= latitude;
    }

    public String getLongitude(){
        return this._longitude;
    }
    public void setLongitude(String longitude){
        this._longitude = longitude;
    }
    public String getAddress(){
        return this._address;
    }
    public void setAddress(String address){
        this._address = address;
    }

    public String getColor(){
        return this._color;
    }

    public void setColor(String color){
        this._color = color;
    }

}