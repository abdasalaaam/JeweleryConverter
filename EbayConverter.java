import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.awt.Desktop;
import javafx.scene.layout.GridPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class EbayConverter extends Application{
  
  /* Converts any Stuller output listing in .csv format to Ebay input listing.
   * NOTE: All csv files input into program must follow these guidelines:
   * No commas in any cell
   * First row must be filled with the column titles
   * Second row must also be filled with the upload details (e.g. shipping service, paypal user, etc.)
   * Stuller pw
   * msalem
   * kharbana
   * 
   * Change variations so it doesnt list sizes that are the saem
   * Change word before url to match what variation u want
   * change single items to have no variations
   */
  
  private static LinkedList<Listing> listings = new LinkedList<Listing>();
  
  private class ButtonAction implements EventHandler<ActionEvent> {
    TextField input;
    TextField output;
    TextField sample;
    
    public ButtonAction(TextField inputd, TextField outputd, TextField sampleFile) {
      input = inputd;
      output = outputd;
      sample = sampleFile;
    }
    
    public void handle(ActionEvent e) {
      runApp(input.getText(),output.getText(),sample.getText());
    }
  }
  
  private class FileChoose implements EventHandler<ActionEvent> {
    
    File selectedFile;
    TextField tex;
    
    public FileChoose(TextField text) {
      tex = text;
    }
    
    public void handle(ActionEvent e) {
      JFrame frame = new JFrame();      
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
      int result = fileChooser.showOpenDialog(frame);
      if (result == JFileChooser.APPROVE_OPTION) {
        selectedFile = fileChooser.getSelectedFile();
        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        tex.setText(selectedFile.getAbsolutePath());
      }
    }
    
    public String getFile() {
     return selectedFile.getAbsolutePath(); 
    }
  }
  
  public void start(Stage primaryStage) {
    Text instructions = new Text("\nStuller conversion to Ebay file exchange program. All programs must be type .csv and MUST NOT CONTAIN ANY COMMAS. This will fudge up the entire thing, before using, replace all commas in excel file with a blank \"\". The input file is the file you want to convert, output file is the file you will upload to Ebay, and sample row file will be a two line excel file containing the column titles and the information that will be consistent with every listing. Hope it works to your liking my G 100. Eid Mubarak to all yalls down in the holy land, consider this my Eid gift to you <3. Love, Future Computer Scientist (Inshallah) and CEO of Big Nose Inc.,\nBoody\n");
    instructions.setWrappingWidth(300);
    instructions.setTextAlignment(TextAlignment.CENTER);
    instructions.setLineSpacing(1.2);
    TextField input = new TextField();
    input.setPromptText("Input File");
    TextField output = new TextField();
    output.setPromptText("Output File");
    TextField sampleFile = new TextField();
    sampleFile.setPromptText("Sample Row");
    Button buttonIn = new Button("Search");
    Button buttonOut = new Button("Search");
    Button buttonSam = new Button("Search");
    FileChoose inz = new FileChoose(input);
    FileChoose outz = new FileChoose(output);
    FileChoose samz = new FileChoose(sampleFile);
    buttonIn.setOnAction(inz);
    buttonOut.setOnAction(outz);
    buttonSam.setOnAction(samz);
    BorderPane pane = new BorderPane();
    Scene scene = new Scene(pane);
    Button button1 = new Button("Convert!");
    
    
    GridPane grid = new GridPane();
    grid.add(input,0,0);
    grid.add(buttonIn,1,0);
    grid.add(buttonOut,1,1);
    grid.add(buttonSam,1,2);
    grid.add(output,0,1);
    grid.add(sampleFile,0,2);
    input.setPrefColumnCount(20);
    output.setPrefColumnCount(20);
    sampleFile.setPrefColumnCount(20);
    pane.setBottom(button1);
    pane.setCenter(grid);
    pane.setTop(instructions);
    input.setEditable(true);
    output.setEditable(true);
    sampleFile.setEditable(true);
    button1.setOnAction(new ButtonAction(input ,output,sampleFile));
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ebay Converter");
    primaryStage.show();
  }
  
  public static void main(String[] args) {
    Application.launch(args);
  }
  
  public static void runApp(String in, String out, String samp) {
    try {
      BufferedReader csvReader = new BufferedReader(new FileReader(new File(in)));
      BufferedReader outReader = new BufferedReader(new FileReader(new File(samp)));
      String baseRow = "";
      String[] firstTwo = new String[2];
      int z = 0;
      while(z < 2) {  
        baseRow = outReader.readLine();
        firstTwo[z] = baseRow;
        z++;
      }
      String row;
      String sku;
      boolean check;
      double price;
      String image;
      String metalFull;
      String metalShort;
      int doneFirst = 0;
      int index;
      String sizz;
      String[] data;
      LinkedList<String> allMetals = new LinkedList<String>();
      allMetals.add("Platinum");
      allMetals.add("Sterling Silver");
      allMetals.add("Continuum Sterling Silver");
      allMetals.add("Stainless Steel");
      while ((row = csvReader.readLine()) != null) {
        data = row.split(",");
        if (doneFirst != 0) {
          //figure out sku
          StringBuilder s = new StringBuilder();
          check = false;
          for(int i = 0; check == false; i++) {
            if (data[0].charAt(i) != ':') {
              s.append(data[0].charAt(i));
            }
            else
              check = true;
          }
          sku = s.toString();
          sku = data[26];
          index = containsSKU(sku);
          if (index == -1) {
            Listing l = new Listing(sku, data[1], firstTwo[1], data[0]);
            listings.add(l);
            //figure out price
            price = Double.parseDouble(data[13]);
            if (price >= 500)
              price *= 1.5;
            if (price >= 200 && price < 500)
              price *= 1.8;
            if (price >= 100 && price < 200)
              price *= 2;
            if (price < 100 && price >= 20)
              price *= 2.2;
            if (price < 20)
              price *= 2.4;
            price = Math.ceil(price) - .01;
            
            //figure out images
            LinkedList<String> images = new LinkedList<String>();
            for(int i = 0; i < 8; i++)
              images.add(data[56 + i]);
            image = convertList(images);
            
            //figure out metalFull
            if (allMetals.contains(data[28])) {
              metalShort = data[28];
              metalFull = data[28];
            }
            else if (data[28].charAt(0) > 47 && data[28].charAt(0) < 58) {
              metalShort = data[28];
              metalFull = data[28] + " Gold";
            }
            else if (allMetals.contains(data[30])) {
              metalShort = data[30];
              metalFull = data[30];
            }
            else if (data[30].charAt(2) == 'K') {
              metalShort = data[30];
              metalFull = data[30] + " Gold";
            }
            else {
              metalFull = "COULD NOT FIND METAL";
              metalShort = "COULD NOT FIND METAL";
            }
              
            //figure out size
            
            sizz = findSize(data);
            
            l.addVar(data[0], data[1], metalFull, metalShort, sizz, image, Double.toString(price), data[65], data[18], data[3]);
          }
          else {
            //figure out price
            price = Double.parseDouble(data[13]);
            if (price >= 500)
              price *= 1.5;
            if (price >= 200 && price < 500)
              price *= 1.8;
            if (price >= 100 && price < 200)
              price *= 2;
            if (price < 100 && price >= 20)
              price *= 2.2;
            if (price < 20)
              price *= 2.4;
            price = Math.ceil(price) - .01;
            
            //figure out images
            LinkedList<String> images = new LinkedList<String>();
            for(int i = 0; i < 8; i++) 
              images.add(data[56 + i]);
            image = convertList(images);
            
            //figure out metalFull
            if (allMetals.contains(data[28])) {
              metalShort = data[28];
              metalFull = data[28];
            }
            else if (data[28].charAt(0) > 47 && data[28].charAt(0) < 58) {
              metalShort = data[28];
              metalFull = data[28] + " Gold";
            }
            else if (allMetals.contains(data[30])) {
              metalShort = data[30];
              metalFull = data[30];
            }
            else if (data[30].charAt(2) == 'K') {
              metalShort = data[30];
              metalFull = data[30] + " Gold";
            }
            else {
              metalFull = "COULD NOT FIND METAL";
              metalShort = "COULD NOT FIND METAL";
            }
            // SIZE
            sizz = findSize(data);
            
            listings.get(index).addVar(data[0], data[1], metalFull, metalShort, sizz, image, Double.toString(price), data[65], data[18], data[3]);
          }
        }
        doneFirst++;
      }
      
      csvReader.close();
      //Writing
      Iterator newIt = listings.iterator();
      Listing item;
      File f = new File(out);
      FileWriter writerz = new FileWriter(f, false);
      BufferedWriter writer = Files.newBufferedWriter(Paths.get(out), Charset.defaultCharset());
      writer.write(firstTwo[0]);
      for(int p = 0; p < listings.size();p++) {
        writer.write(listings.get(p).getRow());
      }
      writer.flush();
      writer.close();
      outReader.close();
      Desktop desktop = Desktop.getDesktop(); 
      System.out.print("Done");
      desktop.open(f);
    }
     catch (IOException e) {
       System.out.print("Error with reading files!");
      return;
    }
    
  }

  public static String convertList(LinkedList<String> images) {
    Iterator it = images.iterator();
    String s;
    int i = 0;
    StringBuilder f = new StringBuilder();
    while(it.hasNext()) {
      s = (String) it.next();
      s = replaceString(s, "$standard$");
      if (s.length() > 3 && i != 0) {
        f.append("|" + s);
      }
      if (s.length() > 3 && i == 0) {
        f.append(s);
        i++;
      }
      
    }
    return f.toString();
  }
  
  public static int containsSKU(String sku) {
    Iterator it = listings.iterator();
    Listing s;
    int i = 0;
    while(it.hasNext()) {
      s = (Listing) it.next();
      if (s != null && s.getSKU().equals(sku)) {
        return i;
      }
      i++;
    }
    return -1;
  }
  
  public static String arrayToString(String[] data) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < data.length; i++) {
      if (data[i] == null)
        data[i] = "";
      if (i < data.length - 1)
        sb.append(data[i] + ",");
      else
        sb.append(data[i]);
    }
    return sb.toString();
  }
  
  public static String replaceString(String primary, String remove) {
    StringBuilder finalS = new StringBuilder();
    //pri - used to store the location of the letter of primary
    int pri = 0;
    //checks if the word we are removing is the same as the word found in 'primary'
    int check = 0;
    //runs through the word we are removing from
    for(int f = 0;f < primary.length(); f = f + 1) {
      //rem - used to compare the corresponding letter from string remove with string primary
      int rem = 0;
      if (primary.charAt(f) == remove.charAt(rem)){
        pri = f;
        //while rem = pri (the letters are the same), add one to the checker
        while ((rem < remove.length()) && (pri < primary.length()) && (primary.charAt(pri) == remove.charAt(rem)) ) {
          pri += 1; 
          rem += 1;
          check += 1;
        }
        //if the checker is the word we are removing, jump the loop ahead
        if (check == remove.length()) {
          finalS.append("$xlarge$");
          f += remove.length() - 1;
          check = 0;
        }
        else {
          finalS.append(primary.charAt(f));
          check = 0;
        }
      }
      else
        finalS.append(primary.charAt(f));
    }
    return finalS.toString();
  }
  
  public static String findSize(String[] data) {
    String[] specs = data[2].split(" / ");
    String size = specs[2];
    if (isSize(size) != "")
      return isSize(size);
    if (isSize(data[3]) != "") {
      StringBuilder sb = new StringBuilder();
      int stopAdding = 0;
      for(int i = 0; i < data[3].length(); i++) {
        if (stopAdding == 0)
          sb.append(data[3].charAt(i));
        if (data[3].length() > i + 1 && Character.toLowerCase(data[3].charAt(i)) == 'm' && Character.toLowerCase(data[3].charAt(i + 1)) == 'm')
          stopAdding = 1;
      }
      return sb.toString();
    }
    return "COULD NOT FIND SIZE (maybe " + data[30] + ")";
  }
  
  public static String isSize(String s) {
    StringBuilder sb = new StringBuilder();
    int bool = 0;
    for(int i = 0; i < s.length(); i++) {
      sb.append(s.charAt(i));
      if (s.charAt(i) == 'x' || s.charAt(i) == 'X' || (s.length() > i + 1 && Character.toLowerCase(s.charAt(i)) == 'm' && Character.toLowerCase(s.charAt(i + 1)) == 'm'))
        bool = 1;
    }
    if ((sb.toString().charAt(1) == 'a' && sb.toString().charAt(0) == 'L') || (sb.toString().charAt(0) == 'M' && sb.toString().charAt(1) == 'e') || (sb.toString().charAt(0) == 'S' && sb.toString().charAt(1) == 'm'))
      bool = 2;
    if (bool == 2)
      return sb.toString();
    if (bool == 1)
      return sb.toString().toLowerCase();
    return "";
  }
}
