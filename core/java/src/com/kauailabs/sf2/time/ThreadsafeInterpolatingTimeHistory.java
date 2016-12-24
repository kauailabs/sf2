package com.kauailabs.sf2.time;

import java.util.ArrayList;

import com.kauailabs.sf2.interpolation.IValueInterpolator;

/**
 * The ThreadsafeInterpolatingTimeHistory class implements an array of
 * timestamped objects which can be inserted from one thread and accessed
 * by another thread.  The accessing thread can lookup objects within the
 * ThreadsafeInterpolatingTimeHistory based upon a timesatamp, and in
 * cases where an exact timestamp match is not found, and object with
 * interpolated values is returned.
 * <p>
 * This class is a template class, meaning that it can be used to contain
 * any type of object which implements the ITimestampedValue and
 * IValueInterpolator interfaces.
 * <p>
 * The implementation of this class is such that the contained objects
 * are statically allocated to avoid memory allocation when objects are
 * added.
 * @author Scott
 */

public class ThreadsafeInterpolatingTimeHistory<T extends ICopy<T> & ITimestampedValue & IValueInterpolator<T>> {
    ArrayList<T> history;
    int history_size;
    int curr_index;
    int num_valid_samples;
    T default_obj;
    TimestampInfo ts_info;

    /**
     * Constructs a ThreadsafeInterpolatingTimeHihstory to hold up to a specified number of 
     * objects of the specified class.
     * @param _class - the Java class of the objects to be contained.
     * @param num_samples - the maximum number of objects to be contained.
     */
    
    public ThreadsafeInterpolatingTimeHistory (T default_obj, int num_samples, TimestampInfo ts_info) {
    	history_size = num_samples;
    	history = new ArrayList<T>(num_samples);
    	
    	this.default_obj = default_obj;

    	for ( int i = 0; i < history_size; i++ ) {
    		T new_t = default_obj.instantiate_copy();
    		if ( new_t != null ) {    	
    			history.add(i,new_t);
    		}
    	}
    	curr_index = 0;
    	num_valid_samples = 0;
    	this.ts_info = ts_info;
    }
    
    /**
     * Clears all contents of the ThreadsafeInterpolatingTimeHistory by marking all
     * contained objects as invalid.
     */
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
    
    /**
     * Returns the current count of valid objects in this ThreadsafeInterpolatingTimeHistory.
     * @return
     */
    public int getValidSampleCount() {
    	return num_valid_samples;
    }
    
    /**
     * Adds the provided object to the ThreadsafeInterpolatingTimeHistory.
     * @param t - the object to add
     */
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
    
    /**
     * Retrieves the object in the ThreadsafeInterpolatingTimeHistory which matches
     * the provided timestamp.  If an exact match is not found, a new object will be
     * created using interpolated values, based upon the nearest objects preceding and
     * following the requested timestamp.
     * @param requested_timestamp - the timeatamp for which to return an object
     * @return - returns the object (either actual or interpolated) matching the requested
     * timestamp.  If no object could be located or interpolated, null is returned.
     */
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
	    		T new_t = default_obj.instantiate_copy();
	    		if ( new_t != null ) {
	    			new_t.copy(match);
	    			match = new_t;
	    		}
	    	}    	
    	}
    	    	
    	return match;
    }
    
    /**
     * Retrieves the most recently-added object in the ThreadsafeInterpolatingTimeHistory.
     * @return - the most recently-added object, or null if no valid objects exist
     */
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
		    		T new_t = default_obj.instantiate_copy();
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
