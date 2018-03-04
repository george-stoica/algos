package trees.btree.interfaces;

import trees.btree.BTNode;

/**
 * Created on 4/3/2018.
 */
public interface SearchableTree<K extends Comparable, V> {
    BTNode find(BTNode<K, V> node, K key);
}
