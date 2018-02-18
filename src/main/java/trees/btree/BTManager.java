package trees.btree;

/**
 * Created on 18/2/2018.
 */
public interface BTManager {
    BTNode insert(KVPair value);
    boolean delete(int key);
    BTNode find(int key);
    BTNode splitRoot(BTNode root);
}
