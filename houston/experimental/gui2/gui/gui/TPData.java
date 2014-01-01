package gui;

public class TPData {
	public double temperature, pressure;
	public TPData() {
		
	}
	public TPData(double t, double p) {
		temperature = t;
		pressure = p;
	}
	public String toString() {
		return String.format("%.1fC, %.0fPa",temperature,pressure);
	}
}
