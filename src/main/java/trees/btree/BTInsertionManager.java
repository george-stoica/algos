package trees.btree;

/**
 * Created on 18/2/2018.
 */
public interface BTInsertionManager {
    <K, V> BTNode insert(KVPair<K, V> kvpair);
    BTNode splitChild(BTNode parent, int childIndex);
}
