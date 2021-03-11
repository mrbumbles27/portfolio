// ben mygatt (c) 2021

import java.util.Arrays;
import java.util.ArrayList;

/*
* class for breaking up components of an ASCII diagram (see example in main())
*/
public class BreakPieces {

  private static String[][] matrix; //shape in matrix representation, first entry is vertical, second entry is horizontal
  private static ArrayList<ArrayList<Integer>> ULCorners; // collection of locations of upper-left corners in matrix

  /*
  * pre: input shape must satisfy certain properties like being ASCII diagram and being closed
  * post: shape is broken up into irreducible components; collection of components is returned as an array
  */
  public static String[] process(String shape) {
    stringToMatrix(shape);
    ArrayList<String> result = new ArrayList<String>();
    while (!ULCorners.isEmpty()) { //iterate through the ULCorners, since they are unique to each component
      ArrayList<Integer> corner = ULCorners.remove(0);
      String c = getComponent(corner);
      if (!c.isEmpty()) {result.add(c);}
    }
    return result.toArray(new String[] {});
  }

  /*
  * pre: s only contains predetermined ASCII characters, "+","-","|" and " "
  * post: s is transferred into matrix for easier navigability
  */
  private static void stringToMatrix(String s) {
    String[] sArr = s.split("\n");
    matrix = new String[sArr.length][sArr[0].length() + sArr.length]; //initialize matrix with max possible lengths
    ULCorners = new ArrayList<ArrayList<Integer>>();
    for (int i = 0; i < sArr.length; i++) {
      for (int j = 0; j < sArr[i].length(); j++) {
        String add = "" + sArr[i].charAt(j);
        matrix[i][j] = add;
        if ((add.contentEquals(" ")) && (i != 0) && (i != matrix.length - 1) && (j != 0)) {
          ArrayList<Integer> loc = new ArrayList<Integer>(Arrays.asList(i,j));
          if (!equal(up(loc), " ") && !equal(left(loc)," ")) { // if character is an upper-left corner, adds to ULCorners
            ULCorners.add(loc);
          }
        }
      }
    }
  }

  /*
  * pre: corner is an UL corner
  * post: returns trimmed component in String form
  */
  public static String getComponent(ArrayList<Integer> corner) {
    ArrayList<Integer> current = corner;
    String[][] component = new String[matrix.length][matrix[0].length + matrix.length]; //empty String[][] to add component to
    do {
      int i = current.get(0);
      int j = current.get(1);
      component[i][j] = matrix[i][j];
      if (i == matrix.length - 1) {return "";} //shape is closed, so not possible to be at the bottom and inside the shape
      if (!equal(up(current)," ") && (cEmpty(component,up(current)))) {
        if (!equal(left(current)," ")) {
          component[i-1][j] = matrix[i-1][j];
          component[i-1][j-1] = "+";
          component[i][j-1] = matrix[i][j-1];
          ULCorners.remove(current);
          if (!equal(right(current)," ")) {
            component[i-1][j+1] = "+";
            component[i][j+1] = matrix[i][j+1];
            if (!equal(down(current)," ")) { //case: 1x1 square
              component[i+1][j+1] = "+";
              component[i+1][j] = "-";
              component[i+1][j-1] = "+";
            }
            else { //case: width 1 corrdior ending at the top
              current = down(current);
              if (cEmpty(component, left(current)) && equal(left(current)," ")) {current = left(current);}
              else if (cEmpty(component, right(current)) && equal(right(current)," ")) {current = right(current);}
            }
          }
          else if (!equal(down(current)," ")) { //case: width 1 corridor ending at the left
            component[i+1][j] = matrix[i+1][j];
            component[i+1][j-1] = matrix[i+1][j-1];
            current = right(current);
            if (cEmpty(component, up(current)) && equal(up(current)," ")) {current = up(current);}
            else if (cEmpty(component, down(current)) && equal(down(current)," ")) {current = down(current);}
          }
          else { //case: UL corner
            current = right(current);
            if (equal(up(current)," ")) {current = up(current);}
          }
        }
        else if (!equal(right(current)," ")) {
          component[i-1][j] = matrix[i-1][j];
          component[i-1][j+1] = "+";
          component[i][j+1] = matrix[i][j+1];
          if (!equal(down(current)," ")) { //case: width 1 corridor ending at the right
            component[i+1][j+1] = "+";
            component[i+1][j] = matrix[i+1][j];
            current = left(current);
            if (cEmpty(component, up(current)) && equal(up(current)," ")) {current = up(current);}
            else if (cEmpty(component, down(current)) && equal(down(current)," ")) {current = down(current);}
          }
          else { //case: UR corner
            current = down(current);
            if (equal(right(current)," ")) {current = right(current);}
          }
        }
        else { //case: upper edge
          if (equal(up(left(current))," ") || equal(up(right(current))," ")) {component[i-1][j] = "+";}
          else {component[i-1][j] = "-";}
          current = right(current);
          if (equal(up(current)," ")) {
            current = up(current);
            component[i-1][j] = null;
          }
        }
      }
      else if (!equal(down(current)," ")) {
        if (!equal(left(current)," ")) {
          component[i+1][j] = matrix[i+1][j];
          component[i+1][j-1] = "+";
          component[i][j-1] = matrix[i][j-1];
          if (!equal(right(current)," ")) { //case: width 1 corrdior ending at the bottom
            component[i-1][j+1] = "+";
            component[i][j+1] = matrix[i][j+1];
            current = up(current);
            if (cEmpty(component, left(current)) && equal(left(current)," ")) {current = left(current);}
            else if (cEmpty(component, right(current)) && equal(right(current)," ")) {current = right(current);}
          }
          else { //case: DL corner
            current = up(current);
            if (equal(left(current)," ")) {current = left(current);}
          }
        }
        else if (!equal(right(current)," ")) { //case: DR corner
          component[i+1][j] = matrix[i+1][j];
          component[i+1][j+1] = "+";
          component[i][j+1] = matrix[i][j+1];
          current = left(current);
          if (equal(down(current)," ")) {current = down(current);}
        }
        else { //case: lower edge
          if (equal(down(left(current))," ") || equal(down(right(current))," ")) {component[i+1][j] = "+";}
          else {component[i+1][j] = "-";}
          current = left(current);
          if (equal(down(current)," ")) {
            current = down(current);
            component[i+1][j] = null;
          }
        }
      }
      else if (!equal(right(current)," ")) { //case: right edge
        if (equal(down(right(current))," ") || equal(up(right(current))," ")) {component[i][j+1] = "+";}
        else {component[i][j+1] = "|";}
        current = down(current);
        if (equal(right(current)," ")) {
          current = right(current);
          component[i][j+1] = null;
        }
      }
      else if (!equal(left(current)," ")) { //case: left edge
        if (equal(down(left(current))," ") || equal(up(left(current))," ")) {component[i][j-1] = "+";}
        else {component[i][j-1] = "|";}
        current = up(current);
        if (equal(left(current)," ")) {
          current = left(current);
          component[i][j-1] = null;
        }
      }
    } while(!current.equals(corner));
    trim(component);
    return matrixToString(component);
  }

  /*
  * pre: c must be output of getComponent()
  * post: removes all trailing space and any extra space above or below component by indicating those spaces with "!"
  */
  private static void trim(String[][] c) {
    boolean done = false;
    int k = 0;
    while (!done) { //removes any full vertical lines of leading space in component
      for (int i = 0; i < c.length; i++) {
        if (c[i][k] != null) {
          done = true;
          break;
        }
      }
      if (!done) { //marks the leading spaces with "!"
        for (int i = 0; i < c.length; i++) {c[i][k] = "!";}
      }
      k++;
    }
    for (int i = 0; i < c.length; i++) { //removes all trailing spaces
      for (int j = c[i].length - 1; j >= 0; j--) {
        if (c[i][j] == null) {c[i][j] = "!";}
        else {break;}
      }
    }
  }

  /*
  * pre: c has been trimmed
  * post: returns printable String form of c
  */
  private static String matrixToString(String[][] c) {
    String[] sArr = new String[c.length];
    String result = "";
    for (int i = 0; i < c.length; i++) {
      sArr[i] = "";
      for (int j = 0; j < c[i].length; j++) {
        if (c[i][j] == null) {c[i][j] = " ";}
        if (!c[i][j].contentEquals("!")) {sArr[i] += c[i][j];}
      }
    }
    int first = 0;
    for (int i = 0; i < sArr.length - 1; i++) { //finds the first nonempty row and adds that to result, top of shape
      if (!sArr[i].isEmpty()) {
        result += sArr[i];
        first = i;
        break;
      }
    }
    for (int i = first + 1; i < sArr.length; i++) { //adds remaining rows to result
      if (!sArr[i].isEmpty()) {
        result += "\n";
        result += sArr[i];
      }
    }
    return result;
  }

  /*
  * tests if shape contains certain character at location a
  */
  private static boolean equal(ArrayList<Integer> a, String s) {
    return matrix[a.get(0)][a.get(1)].contentEquals(s);
  }

  /*
  * tests if component c is empty at location a
  */
  private static boolean cEmpty(String[][] c, ArrayList<Integer> a) {
    return (c[a.get(0)][a.get(1)] == null);
  }

  /*
  * post: returns location as ArrayList
  */
  private static ArrayList<Integer> newLoc(int i, int j) {
    return new ArrayList<Integer>(Arrays.asList(i,j));
  }

  /*
  * post: returns location to the right of loc
  */
  private static ArrayList<Integer> right(ArrayList<Integer> loc) {
    return newLoc(loc.get(0), loc.get(1) + 1);
  }

  /*
  * post: returns location below loc
  */
  private static ArrayList<Integer> down(ArrayList<Integer> loc) {
    return newLoc(loc.get(0) + 1, loc.get(1));
  }

  /*
  * post: returns location to the left of loc
  */
  private static ArrayList<Integer> left(ArrayList<Integer> loc) {
    return newLoc(loc.get(0), loc.get(1) - 1);
  }

  /*
  * post: returns location above loc
  */
  private static ArrayList<Integer> up(ArrayList<Integer> loc) {
    return newLoc(loc.get(0) - 1, loc.get(1));
  }

  public static void main(String args[]) {
    String[] result = process(String.join("\n", new String[] {"+------------+",
                                                              "|            |",
                                                              "|            +",
                                                              "|            |",
                                                              "+------+-----+",
                                                              "|      |     |",
                                                              "|      |     |",
                                                              "+------+-----+"}));
    for (String s : result) {
      System.out.println(s);
    }
  }
}
