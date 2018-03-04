package trees.btree.interfaces;

import trees.btree.BTNode;
import trees.btree.KVPair;

/**
 * Created on 18/2/2018.
 */
public interface BTInsertionManagerInterface<K extends Comparable, V> {
    BTNode insert(BTNode<K, V> node, KVPair<K, V> kvpair);
    BTNode splitChild(BTNode<Integer, String> parent, int childIndex);
}
