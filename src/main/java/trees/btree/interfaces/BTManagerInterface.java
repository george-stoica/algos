package trees.btree.interfaces;

import trees.btree.BTNode;
import trees.btree.KVPair;

/**
 * Created on 18/2/2018.
 */
public interface BTManagerInterface<K extends Comparable, V> extends SearchableTree<K, V> {
    BTNode insert(BTNode<K, V> node, KVPair value);
    boolean delete(BTNode<K, V> node, int key);
    BTNode splitRoot(BTNode<K, V> root);
}
