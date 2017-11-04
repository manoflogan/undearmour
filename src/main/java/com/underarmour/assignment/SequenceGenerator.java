package com.underarmour.assignment;

public class SequenceGenerator {
  

    public static final int NODE_SHIFT = 10;
    public static final int SEQ_SHIFT = 12;

    public static final short MAX_NODE = 1024;
    public static final short MAX_SEQUENCE = 4096;

    private short sequence;
    private long referenceTime;

    private int node;
    
    private static final SequenceGenerator GENERATOR = new SequenceGenerator();
    
    public static SequenceGenerator getInstance() {
      return GENERATOR;
    }

    /**
     * Sequence constructor
     *
     * @param node This is an id you use to differentiate different nodes.
     */
    private SequenceGenerator() {
      this.node = 1;
    }

    /**
     * Generates a k-ordered unique 64-bit integer. Subsequent invocations of this method 
     * will produce increasing integer values.
     *
     * @return The next 64-bit integer.
     */
    public long next() {

      long currentTime = System.currentTimeMillis();
      long counter;

      synchronized(this) {

        if (currentTime < referenceTime) {
          throw new RuntimeException(
              String.format("Last referenceTime %s is after reference time %s", 
                  referenceTime, currentTime));
        } else if (currentTime > referenceTime) {
          this.sequence = 0;
        } else {
          if (this.sequence < MAX_SEQUENCE) {
            this.sequence++;
          } else {
            throw new RuntimeException("Sequence exhausted at " + this.sequence);
          }
        }
        counter = this.sequence;
        referenceTime = currentTime;
      }
      return currentTime << NODE_SHIFT << SEQ_SHIFT | node << SEQ_SHIFT | counter;
    }

}
