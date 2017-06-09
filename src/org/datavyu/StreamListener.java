package org.datavyu;


public interface StreamListener {
	public void streamOpened();
	public void streamData(byte[] data); // data is read-only
	public void streamClosed();
}
