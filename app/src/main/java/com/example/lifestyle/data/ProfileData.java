package com.example.lifestyle.data;

public class ProfileData {
    private String username;
    private String country;
    private String city;
    private int age;
    private int foot;
    private int inch;
    private double weight;
    private String mSex;

    public String getUsername(){return username;}
    public String getCountry(){return country;}
    public String getCity(){return city;}
    public int getAge(){return age;}
    public int getFoot(){return foot;}
    public int getInch(){return inch;}
    public double getWeight(){return weight;}
    public String getSex() {
        return mSex;
    }

    public void setUsername(String s){ username = s;}
    public void setCountry(String s){country = s;}
    public void setCity(String s){city = s;}
    public void setAge(int i){age = i;}
    public void setFoot(int i){foot = i;}
    public void setInch(int i){inch = i;}
    public void setWeight(double i){weight = i;}
    public void setSex (String sex) {
        mSex = sex;
    }

}

