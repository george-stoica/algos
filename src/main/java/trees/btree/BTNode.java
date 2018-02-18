package trees.btree;

/**
 * Created on 18/2/2018.
 */
public class BTNode<K, V> {
    public static int MIN_DEGREE = 3; // min number of children

    public final boolean leaf;
    public final boolean root;
    public int numKeys;
    public final KVPair<K, V>[] kvpairs;
    public final BTNode[] children;

    public BTNode(boolean leaf, boolean root, int minDegree) {
        this.leaf = leaf;
        this.root = root;
        kvpairs = new KVPair[2 * minDegree - 1];
        children = new BTNode[2 * minDegree];
    }

    public boolean hasKey(K key) {
        return getKeyIndex(key) != -1;
    }

    /**
     * Returns index of key in the key array
     *
     * @param key
     * @return
     */
    public int getKeyIndex(K key) {
        for (int i = 0; i < numKeys; i++) {
            if (kvpairs[i].key == key) {
                return i;
            }
        }

        return -1;
    }

    public boolean isFull() {
        return numKeys == 2 * MIN_DEGREE - 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Leaf: " + leaf + ", ");

        for (int i = 0; i < numKeys; i++) {
            sb.append(kvpairs[i] + ", ");
        }

        sb.append("\n");

        if (!leaf) {
            for (int i = 0; i < numKeys; i++) {
                sb.append(children[i].toString());
            }

            if (children[numKeys] != null) {
                sb.append(children[numKeys].toString());
            }
        }

        return sb.toString();
    }
}
