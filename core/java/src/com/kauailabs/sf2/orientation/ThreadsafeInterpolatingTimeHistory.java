package com.kauailabs.sf2.orientation;

import java.util.ArrayList;

/* T is a class which implements the interfaces Cloneable and Timestamp */

public class ThreadsafeInterpolatingTimeHistory<T extends ITimestampedValue & IValueInterpolator<T>> {
    ArrayList<T> history;
    int history_size;
    int curr_index;
    int num_valid_samples;
    Class<T> _class;

    private T create() {
    	T new_t = null;
		try {
			new_t = _class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return new_t;
    }
    
    public ThreadsafeInterpolatingTimeHistory (Class<T> _class, int num_samples) {
    	this._class = _class;
    	history_size = num_samples;
    	history = new ArrayList<T>(num_samples);

    	for ( int i = 0; i < history_size; i++ ) {
    		T new_t = create();
    		if ( new_t != null ) {    	
    			history.add(i,new_t);
    		}
    	}
    	curr_index = 0;
    	num_valid_samples = 0;
    }
    
    public void reset() {
    	synchronized(this) {
	    	for ( int i = 0; i < history_size; i++ ) {
	    		T t = history.get(i);
	    		t.setValid(false);
	    	}
	    	curr_index = 0;
	    	num_valid_samples = 0;
    	}
    }
    
    public int getValidSampleCount() {
    	return num_valid_samples;
    }
    
    public void add(T t) {
    	synchronized(this) {
    		T existing = history.get(curr_index);
    		existing.copy(t);
    		curr_index++;    		
	    	if ( curr_index >= history_size) {
	    		curr_index = 0;
	    	}
	    	if ( num_valid_samples < history_size ) {
	    		num_valid_samples++;
	    	}			
    	}
    }
    
    public T get( long requested_timestamp ) {
    	T match = null;
    	long nearest_preceding_timestamp = Long.MIN_VALUE;
    	long nearest_preceding_timestamp_delta = Long.MIN_VALUE;
    	T nearest_preceding_obj = null;
    	long nearest_following_timestamp_delta = Long.MAX_VALUE;
    	T nearest_following_obj = null;
    	boolean copy_object = true;
    	synchronized(this) {
        	int entry_index = curr_index;
	    	for ( int i = 0; i < num_valid_samples; i++ ) {
	    		T obj = history.get(entry_index);
	    		long entry_timestamp = obj.getTimestamp();
	    		long delta = entry_timestamp - requested_timestamp;
	    		if ( delta < 0 ) {
		    		if ( delta > nearest_preceding_timestamp_delta ) {
		    			nearest_preceding_timestamp_delta = delta;
		    			nearest_preceding_timestamp = entry_timestamp;
		    			nearest_preceding_obj = obj;
		    			/* To optimize, break out once both nearest preceding
		    			 * and following entries are found.  This optimization
		    			 * relies on entries being in descending timestamp
		    			 * order, beginning with the current entry.
		    			 */
		    			if ( nearest_following_obj != null ) break;
		    		}
	    		} else if ( delta > 0 ) {
		    		if ( delta < nearest_following_timestamp_delta ) {
		    			nearest_following_timestamp_delta = delta;
		    			nearest_following_obj = obj;
		    		}
	    		} else { /* entry_timestamp == requested_timestamp */
	    			match = obj;
	    			break;
	    		}
	    		entry_index--;
	    		if ( entry_index < 0 ) {
	    			entry_index = history_size - 1;
	    		}
	    	}
	    	
	    	/* If a match was not found, and the requested timestamp falls
	    	 * within two entries in the history, interpolate an intermediate
	    	 * value.
	    	 */
	    	if ( ( match == null ) &&
	    		 ( nearest_preceding_obj != null ) &&
	    		 ( nearest_following_obj != null ) ) {
		    	double timestamp_delta = nearest_following_timestamp_delta - nearest_preceding_timestamp_delta;
		    	double requested_timestamp_offset = requested_timestamp - nearest_preceding_timestamp;
		    	double requested_timestamp_ratio = requested_timestamp_offset / timestamp_delta;
		    	
		    	match = nearest_preceding_obj.interpolate(nearest_following_obj,
							requested_timestamp_ratio);    	    	
		    	match.setInterpolated(true);
		    	copy_object = false;
	    	}
	    	
	    	if ( ( match != null ) && copy_object ) {
	    		/* Make a copy of the object, so that caller does not directly reference
	    		 * an object within the volatile (threadsafe) history.
	    		 */
	    		T new_t = create();
	    		if ( new_t != null ) {
	    			new_t.copy(match);
	    			match = new_t;
	    		}
	    	}    	
    	}
    	    	
    	return match;
    }
    
    public T getMostRecent() {
    	T most_recent_t = null;
    	synchronized(this){
	    	if ( num_valid_samples > 0 ) {
	    		int curr_idx = this.curr_index;
	    		curr_idx--;
	    		if ( curr_idx < 0 ) {
	    			curr_idx = (history_size - 1);
	    		}
				most_recent_t = history.get(curr_idx);
				if ( !most_recent_t.getValid()) {
					most_recent_t = null;
				} else {
					/* Make a copy of the object, so that caller does not directly
					 * reference an object within the volatile (threadsafe) history. */
		    		T new_t = create();
		    		if ( new_t != null ) {
		    			new_t.copy(most_recent_t);
		    		}
	    			most_recent_t = new_t;		    		
				}
	    	}
    	}
    	return most_recent_t;    	
    }
}
