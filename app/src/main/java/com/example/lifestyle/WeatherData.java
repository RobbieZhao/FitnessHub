package com.example.lifestyle;

public class WeatherData {
    private CurrentCondition mCurrentCondition = new CurrentCondition();
    private Temperature mTemperature = new Temperature();
    private Wind mWind = new Wind();

    public  class CurrentCondition {
        private String mCondition;
        private double mPressure;
        private int mHumidity;

        public String getCondition() {
            return mCondition;
        }
        public void setCondition(String condition) {
            mCondition = condition;
        }
        public double getPressure() {
            return mPressure;
        }
        public void setPressure(double pressure) {
            mPressure = pressure;
        }
        public int getHumidity() {
            return mHumidity;
        }
        public void setHumidity(int humidity) {
            mHumidity = humidity;
        }
    }

    public class Temperature {
        private double mTemp;
        private double mMinTemp;
        private double mMaxTemp;
        private double mFeelTemp;

        public double getTemp() {
            return mTemp;
        }
        public void setTemp(double temp) {
            mTemp = temp;
        }
        public double getMinTemp() {
            return mMinTemp;
        }
        public void setMinTemp(double minTemp) {
            mMinTemp = minTemp;
        }
        public double getMaxTemp() {
            return mMaxTemp;
        }
        public void setMaxTemp(double maxTemp) {
            mMaxTemp = maxTemp;
        }
        public double getFeelTemp() {
            return mFeelTemp;
        }
        public void setFeelTemp(double feelTemp) {
            mFeelTemp = feelTemp;
        }
    }

    public class Wind {
        private double mSpeed;
        public double getSpeed() {
            return mSpeed;
        }
        public void setSpeed(double speed) {
            mSpeed = speed;
        }
    }


    public void setCurrentCondition(CurrentCondition currentCondition){
        mCurrentCondition = currentCondition;
    }
    public CurrentCondition getCurrentCondition(){
        return mCurrentCondition;
    }

    public void setTemperature(Temperature temperature){
        mTemperature = temperature;
    }
    public Temperature getTemperature(){
        return mTemperature;
    }

    public void setWind(Wind wind){
        mWind = wind;
    }
    public Wind getWind(){
        return mWind;
    }
}

