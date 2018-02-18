package trees.btree;

/**
 * Created on 18/2/2018.
 */
public class KVPair<K, V> {
    public K key;
    public V value;

    public KVPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%d - %s", key, value);
    }
}
