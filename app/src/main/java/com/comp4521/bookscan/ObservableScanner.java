package com.comp4521.bookscan;

import java.util.Observable;

public class ObservableScanner extends Observable {
	 private long bookInfo_totalInput = 0;
	 private long bookInfo_found = 0;
	 
	 public ObservableScanner(long m, long n) {
		 this.bookInfo_totalInput = m;
		 this.bookInfo_found = n;
	 }
	 
	 public void setTotalInputValue(long n) {
	     this.bookInfo_totalInput = n;
	     setChanged();
	     notifyObservers("bookInfo_totalInput");
	 }
	 
	 public long getTotalInputValue() {
	     return bookInfo_totalInput;
	 }
	 
	 public void setFoundValue(long n) {
	     this.bookInfo_found = n;
	     setChanged();
	     notifyObservers("bookInfo_found");
	 }
	 
	 public long getFoundValue() {
	     return bookInfo_found;
	 }
}
