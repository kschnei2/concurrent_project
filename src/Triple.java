import java.sql.Timestamp;

public class Triple{
  String str1;
  String str2;
  Timestamp ts;
  
  @Override
  public String toString() {
      return str1.toString()+ ' ' + str2.toString()+ ' ' + ts.toString();
  }
}
