/*
 *  Microassignment: Probing Hash Table addElement and removeElement
 *
 *  LinearHashTable: Yet another Hash Table Implementation
 * 
 *  Contributors:
 *    Bolong Zeng <bzeng@wsu.edu>, 2018
 *    Aaron S. Crandall <acrandal@wsu.edu>, 2019
 * 
 *  Copyright:
 *   For academic use only under the Creative Commons
 *   Attribution-NonCommercial-NoDerivatives 4.0 International License
 *   http://creativecommons.org/licenses/by-nc-nd/4.0
 */

/* 
 * Chase Conaway
 * CptS 233: MA3: LinearHashTable.java
 * Date November 24, 2020
 * git repo url: https://github.com/Chase1242/MA3.git
 * Implemented a function hash table.	
 */

class LinearHashTable<K, V> extends HashTableBase<K, V>
{
	// Linear and Quadratic probing should rehash at a load factor of 0.5 or higher
    private static final double REHASH_LOAD_FACTOR = 0.5;

    // Constructors
    public LinearHashTable()
    {
        super();
    }

    public LinearHashTable(HasherBase<K> hasher)
    {
        super(hasher);
    }

    public LinearHashTable(HasherBase<K> hasher, int number_of_elements)
    {
        super(hasher, number_of_elements);
    }

    // Copy constructor
    public LinearHashTable(LinearHashTable<K, V> other)
    {
        super(other);
	}
    
   
    // ***** MA Section Start ************************************************ //

    // Concrete implementation for parent's addElement method
    public void addElement(K key, V value)
    {
        // Check for size restrictions
        resizeCheck();
 
        // Calculate hash based on key
        int hash = super.getHash(key);
        // find the current slot
        HashItem<K, V> slot = _items.elementAt(hash);
        // make a new HashItem to add to the table
        HashItem<K, V> newItem = new HashItem<K, V>(key, value, false);
        // make sure the current slot is empty
        while (!slot.isEmpty()) {
        	// if not, linear probe
        	hash = (hash + 1) % _items.size();
        	slot = _items.elementAt(hash);
        }
        // wehn we find empty slot, put new item there
        _items.setElementAt(newItem, hash);

        
        // Remember how many things we are presently storing (size N)
    	//  Hint: do we always increase the size whenever this function is called?
        _number_of_elements++;

    }

    // Removes supplied key from hash table
    public void removeElement(K key)
    {
        // Calculate hash from key
        int hash = super.getHash(key);
        // finds the element at that hash
        HashItem<K, V> slot = _items.elementAt(hash);
        // makes sure this item is actually in array
        if (containsElement(key)) {
        	// loops through whole array to ensure whole array is looped through
        	for (int i = 0; i < _items.size() - 1; i++) {
        		// as long as the slot is not empty AND the given key equals the current slot key
        		// then we have found our key and we lazy delete it
        		if (!slot.isEmpty()) {
        			if (slot.getKey().equals(key)) {
        				
        				slot.setIsEmpty(true);
        		        _items.setElementAt(slot, hash);
        			}
        		}
        		hash = (hash + 1) % _items.size();
    			slot = _items.elementAt(hash);
        	}
	        
	        _number_of_elements--;
        }
        
    }
    
    // ***** MA Section End ************************************************ //
    

    // Public API to get current number of elements in Hash Table
	public int size() {
		return this._number_of_elements;
	}

    // Public API to test whether the Hash Table is empty (N == 0)
	public boolean isEmpty() {
		return this._number_of_elements == 0;
	}
    
    // Returns true if the key is contained in the hash table
    public boolean containsElement(K key)
    {
    	int size = _items.size();
        int hash = super.getHash(key);
        HashItem<K, V> slot = _items.elementAt(0);
        for (int i = 0; i < size - 1; i++){
        	if (!slot.isEmpty()) {
        		if (slot.equals(_items.elementAt(hash))) 
        			return true;
        	}
			hash = (hash + 1) % size;
			slot = _items.elementAt(hash);
        }
        // Left incomplete to avoid hints in the MA :)
        return false;
    }
    
    // Returns the item pointed to by key
    public V getElement(K key)
    {
    	// gets the hash code for us
        int hash = super.getHash(key);
        HashItem<K, V> slot = _items.elementAt(hash);
        
        while (!slot.isEmpty()) {
        	if (slot.getKey().equals(key)) {
				return slot.getValue();
        	}
			hash = (hash + 1) % _items.size();
			slot = _items.elementAt(hash);
        }
        // Left incomplete to avoid hints in the MA :)
        return null;	
    }

    // Determines whether or not we need to resize
    //  to turn off resize, just always return false
    protected boolean needsResize()
    {
        // Linear probing seems to get worse after a load factor of about 50%
        if (_number_of_elements > (REHASH_LOAD_FACTOR * _primes[_local_prime_index]))
        {
            return true;
        }
        return false;
    }
    
    // Called to do a resize as needed
    protected void resizeCheck()
    {
        // Right now, resize when load factor > 0.5; it might be worth it to experiment with 
        //  this value for different kinds of hashtables
        if (needsResize())
        {
            _local_prime_index++;

            HasherBase<K> hasher = _hasher;
            LinearHashTable<K, V> new_hash = new LinearHashTable<K, V>(hasher, _primes[_local_prime_index]);

            for (HashItem<K, V> item: _items)
            {
                if (item.isEmpty() == false)
                {
                    // Add element to new hash table
                    new_hash.addElement(item.getKey(), item.getValue());
                }
            }

            // Steal temp hash object's internal vector for ourselves
            _items = new_hash._items;
        }
    }

    // Debugging tool to print out the entire contents of the hash table
	public void printOut() {
		System.out.println(" Dumping hash with " + _number_of_elements + " items in " + _items.size() + " buckets");
		System.out.println("[X] Key	| Value	| Deleted");
		for( int i = 0; i < _items.size(); i++ ) {
			HashItem<K, V> curr_slot = _items.get(i);
			System.out.print("[" + i + "] ");
			System.out.println(curr_slot.getKey() + " | " + curr_slot.getValue() + " | " + curr_slot.isEmpty());
		}
	}
}