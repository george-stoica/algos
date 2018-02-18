package trees.btree;

/**
 * Created on 18/2/2018.
 */
public interface BTDeletionManager {
    BTNode mergeChildren(BTNode node, int childIndex);
    boolean deleteLeafKey(BTNode node, int key);
    boolean deleteInnerNodeKey(BTNode node, int key);
}
