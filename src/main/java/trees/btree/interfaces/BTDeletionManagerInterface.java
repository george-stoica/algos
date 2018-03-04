package trees.btree.interfaces;

import trees.btree.BTNode;

/**
 * Created on 18/2/2018.
 */
public interface BTDeletionManagerInterface {
    BTNode mergeChildren(BTNode node, int childIndex);
    boolean deleteLeafKey(BTNode node, int key);
    boolean deleteInnerNodeKey(BTNode node, int key);
}
