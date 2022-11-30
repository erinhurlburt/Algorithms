/**
 * Erin Hurlburt
 * 10 April 2022
 * Huffman.java
 */

package main.compression;

import java.util.*;
import java.io.ByteArrayOutputStream; // Optional

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    // TreeMap chosen here just to make debugging easier
    private TreeMap<Character, String> encodingMap;
    // Character that represents the end of a compressed transmission
    private static final char ETB_CHAR = 23;
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * 
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    public Huffman (String corpus) {
    	encodingMap = new TreeMap<>();
    	TreeMap<Character, Integer> characterCount = new TreeMap<>();
        for(int i = 0; i < corpus.length(); i++) {
        	char c = corpus.charAt(i);
        	if (characterCount.containsKey(c)) {
        		characterCount.put(c, characterCount.get(c) + 1);
        	} else {
        		characterCount.put(c, 1);
        	}
        }
        
        characterCount.put(ETB_CHAR, 1);
        
        PriorityQueue<HuffNode> q = new PriorityQueue<>();
        for(char c: characterCount.keySet()) {
        	HuffNode leaf = new HuffNode(c, characterCount.get(c));
        	q.add(leaf);
        }
        
        while (q.size() > 1) {
        	HuffNode first = q.poll();
        	HuffNode second = q.poll();
        	HuffNode parent = new HuffNode('\0', (first.count + second.count));
        	parent.zeroChild = first;
        	parent.oneChild = second;
        	q.add(parent);
        }
        
        trieRoot = q.poll();
        
        buildMap(trieRoot, "");
     
    }
    
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * 
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as:
     *         (1) the bitstring containing the message itself, (2) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
    	BitWriter writeBits = new BitWriter();
        for (int i = 0; i < message.length(); i++) {
        	writeBits.writeBits(encodingMap.get(message.charAt(i)));
        }
        
        writeBits.writeBits(encodingMap.get(ETB_CHAR));
        
        
        return writeBits.byteArray();
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * 
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as:
     *        (1) the bitstring containing the message itself, (2) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        BitReader bitReader = new BitReader(compressedMsg);
        String message = "";
        while(!bitReader.EOF()) {
        	HuffNode current = trieRoot;
        	while(!current.isLeaf()) {
        		if(bitReader.readBit() == 1) {
        			current = current.oneChild;
        		} else {
        			current = current.zeroChild;
        		}
        	}
        	
        	if(current.character == ETB_CHAR) {
        		break;
        	}
        	
        	message += current.character;
        }
        
        
        return message;
        
        
        
    }
    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left (0) and right (1) child), contains
     * a character field that it represents, and a count field that holds the 
     * number of times the node's character (or those in its subtrees) appear 
     * in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode zeroChild, oneChild;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return this.zeroChild == null && this.oneChild == null;
        }
        
        public int compareTo (HuffNode other) {
        	int countDiff = this.count - other.count;
            if (countDiff != 0) {
            	return countDiff;
            } else {
            	return this.character - other.character;
            }
        }
        
    }
    
    private class BitReader {
    	
    	private int currentBit;
    	private byte[] message;
    	
    	public BitReader(byte[] message) {
    		this.message = message;
    		currentBit = 0;
    	}
    	
    	public int readBit() {
    		byte b = message[(currentBit/8)];
    		int offSet = currentBit % 8;
    		currentBit++;
    		if ((b & (1 << (7-offSet))) != 0) {
    			return 1;
    		} else {
    			return 0;
    		}
    		
    	}
    	
    	public boolean EOF() {
    		return (currentBit/8) == message.length;
    		
    	}
    	
    	
    }
    
    
    private class BitWriter {
    	
    	ByteArrayOutputStream stream;
    	byte buffer;
    	int position;
    	
    	public BitWriter() {
    		stream = new ByteArrayOutputStream();
    		buffer = 0;
    		position = 0;
    	}
    	

    	public void writeBit(int bit) {
    		if (position == 8) {
    			stream.write(buffer);
    			position = 0;
    			buffer = 0;
    		}
    		if (bit == 1) {
    			buffer |= 1 << (7 - position) ;
    		}
    		position++;
    	}
    	
    	public void writeBits(String bits) {
    		for (int i = 0; i < bits.length(); i++) {
    			if (bits.charAt(i) == '0') {
    				writeBit(0);
    			} else {
    				writeBit(1);
    			}
    		}
    	}
    	
    	public byte[] byteArray() {
    		if (position > 0) {
    			stream.write(buffer);
    		}
    		return stream.toByteArray();
    	}
    }
    
    
    private void buildMap(HuffNode current, String path) {
    	if (current.zeroChild == null) {
    		encodingMap.put(current.character, path);
    	} else {
    		buildMap(current.zeroChild, path + "0");
    		buildMap(current.oneChild, path + "1");
    	}

    }

}




