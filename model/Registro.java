package model;

public interface Registro 
{
  public int getId();
  public void setId(int id);

  byte[] toByteArray() throws Exception;

  void fromByteArray(byte[] ba) throws Exception;
}
