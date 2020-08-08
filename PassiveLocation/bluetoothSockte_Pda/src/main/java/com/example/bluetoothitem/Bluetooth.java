package com.example.bluetoothitem;

public class Bluetooth {
	private String btName;
	private String btAddress;
	private String btRSSI;
	public String getBtName() {
		return btName;
	}
	public void setBtName(String btName) {
		this.btName = btName;
	}
	public String getBtAddress() {
		return btAddress;
	}
	public void setBtAddress(String btAddress) {
		this.btAddress = btAddress;
	}
	public String getBtRSSI() {
		return btRSSI;
	}
	public void setBtRSSI(String btRSSI) {
		this.btRSSI = btRSSI;
	}
	public Bluetooth(String btName, String btAddress, String btRSSI) {
		super();
		this.btName = btName;
		this.btAddress = btAddress;
		this.btRSSI = btRSSI;
	}
}
