package com.farrellf.TelemetryGUI;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * A simple place for data storage and retrieval.
 * A Map of ArrayLists is currently used as the storage medium.
 * 
 * @author Farrell Farahbod
 * @version 1.0
 */
public class Database {
	
	// names of items being tracked by the database
	private List<String> names;
	private Map<String, List<Integer>> db;
	
	public Database() {
		names = new ArrayList<String>();
		db = new HashMap<String, List<Integer>>();
	}
	
	/** Insert a new value for the specified item
	 * 
	 * @param key		Name of tracked item
	 * @param value		New value
	 */
	public void addValue(String key, int value) {
		if(!names.contains(key)) {
			// add new list to the map if it doesn't already exist
			db.put(key, Collections.synchronizedList(new ArrayList<Integer>()));
			names.add(key);
		}
		
		db.get(key).add(value);
	}
	
	/** Get the most recent value from the database
	 * 
	 * @param key		Name of tracked item
	 * @return			Most recent value
	 */
	public int getLastValue(String key) {
		if(!names.contains(key)) {
			//System.err.println("Item \"" + key + "\" does not yet exist in the database.");
			return -1;
		}
		
		List<Integer> al = db.get(key);
		int lastIndex = al.size() - 1;
		if(lastIndex == -1) {
			return -1; // no values exist in the database
		} else {
			return al.get(lastIndex);
		}
	}
	
	/** Get the sample count for an item
	 * 
	 * @param key		Name of tracked item
	 * @return			Count of values stored in the db
	 */
	public int getListSize(String key) {
		if(!names.contains(key)) {
			//System.err.println("Item \"" + key + "\" does not yet exist in the database.");
			return -1;
		} else {
			return db.get(key).size();
		}
	}
	
	/** Get a list containing the history of values for an item
	 * 
	 * @param key		Name of tracked item
	 * @return			List<Integer> of values
	 */
	public List<Integer> getList(String key) {
		if(!names.contains(key)) {
			//System.err.println("Item \"" + key + "\" does not yet exist in the database.");
			return new ArrayList<Integer>();
		} else {
			return db.get(key);
		}
	}

}
