package trees.btree;

import trees.btree.interfaces.BTManagerInterface;

/**
 * Created on 4/3/2018.
 */
public class BTManager implements BTManagerInterface<Integer, String>{
    private final BTInsertionManager insertionManager;

    public BTManager(BTInsertionManager insertionManager) {
        this.insertionManager = insertionManager;
    }

    @Override
    public BTNode insert(BTNode<Integer, String> node, KVPair value) {
        return insertionManager.insert(node, value);
    }

    @Override
    public boolean delete(BTNode<Integer, String> node, int key) {
        return false;
    }

    @Override
    public BTNode splitRoot(BTNode<Integer, String> root) {
        BTNode<Integer, String> newRoot = new BTNode<>(BTNode.MIN_DEGREE);
        newRoot.children[0] = root;

        insertionManager.splitChild(newRoot, 0);

        return newRoot;
    }

    @Override
    public BTNode find(BTNode<Integer, String> node, Integer key) {
        if (node.isLeaf() && node.hasKey(key)) {
            return node;
        }

        // identify branch to search in
        int searchChildIndex = 0;
        while (key < node.kvpairs[searchChildIndex].key) {
            searchChildIndex++;
        }

        return find(node.children[searchChildIndex], key);
    }
}
