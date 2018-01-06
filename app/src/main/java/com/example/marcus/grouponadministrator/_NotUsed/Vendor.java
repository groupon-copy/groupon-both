package com.example.marcus.grouponadministrator._NotUsed;

/**
 * Created by Bubba on 3/25/2016.
 */
/*
public class Vendor implements Serializable
{
    private int vid;
    private String name;
    private String addr1;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String priceRange;
    private String website;
    private double rating;
    private String img_ref;
    private String description;
    private VendorHours hours;
    private boolean isOpen;

    //private Drawable image; images can't be serializable

    public Vendor(JSONObject jo) throws JSONException
    {
        vid = jo.getInt("vid");
        name = jo.getString("vname");
        addr1 = jo.getString("addr1");
        city = jo.getString("city");
        state = jo.getString("state");
        zip = jo.getString("zip");
        phone = jo.getString("phone1");
        priceRange = jo.getString("price_range");
        website = jo.getString("website");
        rating = Double.parseDouble(jo.getString("rating"));
        img_ref = jo.getString("img_ref");
        //image = null;
        description = jo.getString("description");
        hours = new VendorHours(jo.getString("hours"));
    }

    public Vendor(Business b)
    {
        name = b.name();

        if(b.location() != null)
        {
            if(b.location().address().size() > 0)
                addr1 = b.location().address().get(0);

            city = b.location().city();
            state = b.location().stateCode();
            zip = b.location().postalCode();
        }

        img_ref = b.imageUrl();
        phone = b.displayPhone();
        priceRange = "";
        website = b.url();
        rating = b.rating().doubleValue();
        hours = null;
        description = b.snippetText();
        isOpen = !b.isClosed(); //this is not the right function. Yelp does not tell us whether it is open or not
    }

    public Vendor()
    {
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating)
    {
        this.rating = rating;
    }

    public int getVid() {
        return vid;
    }

    public String getVname() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String address)
    {
        addr1 = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getFullAddress()
    {
        List<String> parts= new ArrayList<>();
        if(addr1 != null && addr1 != "")
            parts.add(addr1);
        if(city != null && city != "")
            parts.add(city);
        if(state != null && state != "")
            parts.add(state);

        String fullAddress = "";
        if(parts.size() >= 1)
        {
            fullAddress += parts.get(0);
            for(int i = 1; i < parts.size(); i++)
            {
                fullAddress += ", " + parts.get(i);
            }
        }

        if(zip != null && zip != "")
            fullAddress += " " + zip;

        return fullAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPrice_range() {
        return priceRange;
    }

    public void setPriceRange(String priceRange)
    {
        this.priceRange = priceRange;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public VendorHours getHours()
    {
        return hours;
    }

    public void setHours(VendorHours hours)
    {
        this.hours = hours;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getImg_ref()
    {
        return img_ref;
    }

    //public Drawable getImage(){return image;}

//    public void setImage(Drawable d){
//        image = d;
//    }

    public void setIsOpen(boolean bool) { isOpen = bool; }

    public boolean getIsOpen()
    {
        return isOpen;
    }

    public int getIsOpenDetails()
    {
        if(hours != null)
            return VendorHours.isVendorOpenNow(hours);

        //used for business yelp API
        else if(isOpen)
            return VendorHours.OPEN_TODAY;
        else
            return VendorHours.CLOSED_TODAY;
    }

    public void setImageURL(String imageURL)
    {
        img_ref = imageURL;
    }
}*/
