import java.util.*;
import java.io.*;
public class Listing {
  
  private LinkedList<Variation> variations = new LinkedList<Variation>();
  private LinkedList<String> images = new LinkedList<String>();
  private String sku;
  private String picURL;
  private String title;
  private LinkedList<String> metals = new LinkedList<String>();
  private LinkedList<String> sizes = new LinkedList<String>();
  private String details;
  private String row;
  private String category;
  private String base;
  private String longSku;
  private LinkedList<String> tempWeights = new LinkedList<String>();
  
  public Listing(String skud, String titled, String based, String longsku) {
    sku = skud;
    title = titled;
    base = based;
    longSku = longsku;
  }
  
  public String getRow() {
    StringBuilder s = new StringBuilder();
    if (variations.size() <= 1) {
      String[] data = base.split(",");
      StringBuilder sb = new StringBuilder();
      Variation var = variations.get(0);
      data[1] = this.getLSKU();
      data[2] = "Does Not Apply";
      data[11] = var.getShort();
      data[13] = var.getSize();
      data[17] = var.getCountry();
      data[20] = var.getSize() + "=" + var.getImage();
      data[25] = var.getPrice();
      data[27] = "99";
      data[20] = this.getURL();
      data[22] = this.createDescription();
      data[5] = this.getTitle();
      data[8] = this.relationshipDetails();
      s.append("\n" + EbayConverter.arrayToString(data));
    }
    else {
      
      String[] data = base.split(",");
      data[20] = this.getURL();
      data[5] = this.getTitle();
      data[8] = this.relationshipDetails();
      data[1] = this.getLSKU();
      data[22] = this.createDescription();
      s.append("\n" + EbayConverter.arrayToString(data));
      Iterator it = variations.iterator();
      Variation var;
      while(it.hasNext()) {
       var = (Variation) it.next();
       
       s.append("\n" + var.toString());
      }
    }
    return s.toString();
  }
  
  public class Variation {
    private String custLabel;
    private String metalFull;
    private String metalShort;
    private String size;
    private String image;
    private String price;
    private String desc;
    private String color;
    private String countr;
    private Listing parent;
    private String weigh;
    private String titl;
    private boolean check = false;
    private String karat;
    
    private Variation(String custLabela, String desca, String metalFulla, String metalShorta, 
                      String sizea, String imagea, String pricea, String country, String weight, String title) {
      custLabel = custLabela;
      metalFull = metalFulla;
      metalShort = metalShorta;
      size = sizea;
      image = imagea;
      price = pricea;
      desc = desca;
      weigh = weight;
      titl = title;
      if (country.equals("US"))
        countr = "United States";
      else if (country.equals("IT"))
        countr = "Italy";
      else if (country.equals("JP"))
        countr = "Japan";
      else if (country.equals("HK"))
        countr = "Hong Kong";
      else countr = country;
      StringBuilder s = new StringBuilder();
      boolean pastSpace = false;
      if (metalFulla.charAt(0) > 47 && metalFulla.charAt(0) < 58) {
        for(int i = 0; i < metalFulla.length(); i++) {
          if (pastSpace == false)
            s.append(metalFull.charAt(i));
          if (metalFulla.charAt(i) == 'K')
            pastSpace = true;
        }
        karat = s.toString();
      }
      else
        karat = null;
    }
    
    public String getKarat() {
      return karat;
    }
    
    public String getSKU() {
      return custLabel;
    }
    
    private void setChecked(boolean checks) {
      check = checks;
    }
    
    private boolean getChecked() {
      return check;
    }
    
    private String getDesc() {
      return desc;
    }
    
    private String getTitl() {
     return titl; 
    }
    
    private String getFull() {
      return metalFull;
    }
    
    private String getShort() {
      return metalShort;
    }
    
    private String getSize() {
      return size;
    }
    
    private String getImage() {
      return image;
    }
    
    private String getPrice() {
      return price;
    }
    
    private String getCountry() {
      return countr;
    }
    
    public void setParent(Listing l) {
      parent = l;
    }
    
    public Listing getParent() {
      return parent;
    }
    
    private String getWeight() {
     return weigh; 
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      String[] data = new String[50];
      data[1] = this.getSKU();
      data[2] = "Does Not Apply";
      data[7] = "Variation";
      
      if (getParent().getMetalsLength() > 1 && getParent().getSizesLength() <= 1) {
        data[8] = "Metal=" + this.getFull();
        data[20] = this.getFull() + "=" + this.getImage();
      }
      else if (getParent().getMetalsLength() <= 1 && getParent().getSizesLength() > 1) {
        data[8] = "Size=" + this.getSize();
        data[20] = this.getSize() + "=" + this.getImage();
      }
      else if (getParent().getMetalsLength() > 1 && getParent().getSizesLength() > 1) {
        data[8] = "Metal=" + this.getFull() + "|Size=" + this.getSize();
        data[20] = this.getFull() + "=" + this.getImage();
      }
      data[11] = this.getShort();
      data[13] = this.getSize();
      data[17] = this.getCountry();
      data[25] = this.getPrice();
      data[27] = "99";
      return EbayConverter.arrayToString(data);
    }
  }
  
  public String getSKU() {
    return sku;
  }
  
  public String getLSKU() {
    return longSku;
  }
  
  public Variation getVar(int i) {
   return variations.get(i); 
  }
  
  public void addVar(String custLabel, String desc, String metalFull, String metalShort, String size, String image, String price, String country, String weight, String title) {
    Variation var = new Variation(custLabel, desc, metalFull, metalShort, size, image, price, country, weight, title);
    StringBuilder pic = new StringBuilder();
    if (picURL == null)
      pic.append(image);
    else
      pic.append(picURL + "|" + image);
    picURL = pic.toString();
    variations.add(var);
    var.setParent(this);
    if (metals.contains(metalFull) == false)
      metals.add(metalFull);
    if (sizes.contains(size) == false)
      sizes.add(size);
  }
  
  public int getVarsLength() {
    return variations.size(); 
  }
  
  public String getURL() {
    return picURL;
  }
  
  public String getTitle() {
    //Adding karat abbreviations first
    LinkedList<String> karats = new LinkedList<String>();
    LinkedList<String> golds = new LinkedList<String>();
    LinkedList<String> mets = new LinkedList<String>();
    Iterator it = variations.iterator();
    StringBuilder titl = new StringBuilder();
    Variation var = variations.get(0);
    while(it.hasNext()) {
      var = (Variation) it.next();
      String[] sep = var.getFull().split(" ");
      if (karats.contains(sep[0]) == false && sep[0].charAt(0) > 47 && sep[0].charAt(0) < 58)
        karats.add(sep[0]);
      if (sep.length > 1 && golds.contains(sep[1]) == false && sep[0].charAt(0) > 47 && sep[0].charAt(0) < 58)
        golds.add(sep[1]);
      if (sep[0].charAt(0) <= 47 || sep[0].charAt(0) >= 58 && mets.contains(var.getFull()) == false)
        mets.add(var.getFull());
    }
    if (karats.size() > 0) 
      karats = sortedKarats(karats);
    Collections.sort(golds);
    Collections.sort(mets);
    it = karats.iterator();
    while(it.hasNext())
      titl.append(it.next() + " ");
    it = golds.iterator();
    while(it.hasNext()) {
      titl.append(it.next() + " ");
    }
    if (golds.size() > 0)
      titl.append("Gold ");
    it = mets.iterator();
    while(it.hasNext()) {
      titl.append(it.next() + " ");
    }
    titl.append(var.getTitl());
    return titl.toString();
  }
  
  public LinkedList<String> sortedKarats(LinkedList<String> list) {
    LinkedList<String> numberForm = new LinkedList<String>(); 
    LinkedList<String> sorted = new LinkedList<String>();
    LinkedList<Integer> ints = new LinkedList<Integer>();
    Integer smallest = 1000;
    String str;
    String karat;
    Integer saveInt = 0;
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < list.size(); i++) {
      str = list.get(i);
      sb = new StringBuilder();
      for(int z = 0; z < str.length();z++) {
        if (str.charAt(z) > 47 && str.charAt(z) < 58)
          sb.append(str.charAt(z));
      }
      karat = sb.toString();
      numberForm.add(karat);
    }
    for(int f = 0; f < numberForm.size(); f++) {
      smallest = 1000;
      for(int k = 0; k < numberForm.size(); k++) {
        if (Integer.parseInt(numberForm.get(k)) < smallest && !ints.contains(k)) {
          smallest = Integer.parseInt(numberForm.get(k));
          saveInt = k;
        }
      }
      ints.add(saveInt);
      sorted.add(smallest.toString() + "K");
    }
    return sorted;
  }
  
  public int getMetalsLength() {
    return metals.size();
  }
  
  public int getSizesLength() {
    return sizes.size();
  }
  
  public String relationshipDetails() {
    StringBuilder s = new StringBuilder();
    if (metals.size() > 1 && sizes.size() > 1) {
      Iterator mit = metals.iterator();
      s.append("Metal=");
      while(mit.hasNext()) {
        s.append(mit.next());
        if (mit.hasNext() == true)
          s.append(";");
      }
      s.append("|Size=");
      Iterator sit = sizes.iterator();
      while(sit.hasNext()) {
        s.append(sit.next());
        if (sit.hasNext() == true)
          s.append(";");
      }
    }
    else if (metals.size() <= 1 && sizes.size() > 1) {
      s.append("Size=");
      Iterator sit = sizes.iterator();
      while(sit.hasNext()) {
        s.append(sit.next());
        if (sit.hasNext() == true)
          s.append(";");
      }
    }
    else if (metals.size() > 1 && sizes.size() <= 1) {
      Iterator mit = metals.iterator();
      s.append("Metal=");
      while(mit.hasNext()) {
        s.append(mit.next());
        if (mit.hasNext() == true)
          s.append(";");
      }
    }
    return s.toString();
  }
  
  public String createDescription() {
    /*<p>Jeweler Grade findings and components. High quality products manufactured for use by industry jewelry 
     * professionals and hobbyists. Diamonds graded and sorted by GIA graduate of diamonds. Why settle for less?</p><p>Le 
     * Suq offers an assortment of 10K, 14K, 18K Solid Gold and .925 Sterling Silver finished jewelry, findings and jewelry 
     * making products at prices that are extremely competitive with today's precious metals market.</p><p><u>14K 18K Gold 
     * Platinum Silver Oval Elongated Charm Bail Lobster Claw Clasp w/Ring</u></p>*/
    StringBuilder s = new StringBuilder();
    s.append("<p style = \"color:#848484;font-size:90%\"><i>Jeweler Grade findings and components. High quality products manufactured for use by industry jewelry professionals and hobbyists. Diamonds graded and sorted by GIA graduate of diamonds. Why settle for less?</p><p style = \"color:#848484;font-size:90%\">LeSuq offers an assortment of 10K/14K/18K Solid Gold and .925 Sterling Silver finished jewelry findings and jewelry making products at prices that are extremely competitive with today's precious metals market.</i></p>");
    s.append("<p style = \"font-size:130%;\"><b>" + getTitle() + "</p></b>");
    s.append("<p> Specifications: </p>");
    String t = null;
    for(int i = 0; i < variations.size(); i++) {
      t = getDescription(i);
      if (t.length() > 5) {
        s.append("<p>" + t + "</p>");
      }
    }
    return s.toString();
  }
  
  public String getDescription(int i) {
    String lists = createLists(variations.get(i));
    if (!lists.equals("")) {
      //07.00X02.75mm, 0.8mm, 14kt White or Yellow Gold, 0.18-0.21 grams
      StringBuilder s = new StringBuilder();
      Variation var = variations.get(i);
      s.append("Size: " + var.getSize() + "<br>");
      s.append("Metal Types: " + lists + "<br>");
      String min = findMin();
      String max = findMax();
      if (!min.equals(max))
        s.append("Weight: " + min + " - " + max + " grams");
      else
        s.append("Weight: " + min + " grams");
      tempWeights.clear();
      return s.toString();
    }
    else return "";
  }
  
  public String createLists(Variation var) {
    Iterator it = variations.iterator();
    int stop = 0;
    Variation next = null;
    if (var.getChecked() == false) {
      LinkedList<Variation> metalsToAdd = new LinkedList<Variation>();
      LinkedList<String> karats = new LinkedList<String>();
      while(it.hasNext()) {
        next = (Variation) it.next();
        if (next.getSize().equals(var.getSize())) {
          if (next.getChecked() == false) {
            metalsToAdd.add(next);
          }
          else {
            stop = 1;
          }
        }
      }
      StringBuilder s = new StringBuilder();
      if (stop == 0) {
        //creating list of karats
        for(int i = 0;i < metalsToAdd.size(); i++) {
          if (!karats.contains(metalsToAdd.get(i).getKarat()) && metalsToAdd.get(i).getKarat() != null)
            karats.add(metalsToAdd.get(i).getKarat()); 
        }
        if (karats.size() > 0)
          karats = sortedKarats(karats);
        Iterator iz = metalsToAdd.iterator();
        Iterator ik = karats.iterator();
        String kar = null;
        Variation ptr = null;
        while(ik.hasNext()) {
          kar = (String) ik.next();
          s.append(kar + " ");
          iz = metalsToAdd.iterator();
          while(iz.hasNext()) {
            ptr = (Variation) iz.next();
            if (ptr.getFull().contains(kar)) {
              s.append(getColor(ptr.getFull()) + " ");
          }
        }
        }
        if (karats.size() > 0)
          s.append("Gold ");
        iz = metalsToAdd.iterator();
        while(iz.hasNext()) {
          ptr = (Variation) iz.next();
          tempWeights.add(ptr.getWeight());
          if (ptr.getFull().charAt(0) <= 47 || ptr.getFull().charAt(0) >= 58)
            s.append(ptr.getFull() + " ");
        }
        var.setChecked(true);
        return s.toString();
      }
    }
    return "";
  }
  
  public String getColor(String f) {
    if (f.charAt(0) > 47 && f.charAt(0) < 58) {
      String[] split = f.split(" ");
      return split[1];
    }
    else
      return f;
  }
  
  public String findMin() {
    Double min = 1000000.0;
    for(int i = 0; i < tempWeights.size(); i++) {
      if (Double.parseDouble(tempWeights.get(i)) < min)
        min = Double.parseDouble(tempWeights.get(i));
    }
    return min.toString();
  }
  
  public String findMax() {
    Double max = 0.0;
    for(int i = 0; i < tempWeights.size(); i++) {
      if (Double.parseDouble(tempWeights.get(i)) > max)
        max = Double.parseDouble(tempWeights.get(i));
    }
    return max.toString();
  }
}
