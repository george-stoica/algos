import trees.btree.BTInsertionManager;
import trees.btree.BTManager;
import trees.btree.BTNode;
import trees.btree.KVPair;

/**
 * Created on 4/3/2018.
 */
public class Start {
    public static void main(String[] args) {
        BTManager treeManager = new BTManager(new BTInsertionManager());

        BTNode<Integer, String> root = new BTNode<>(BTNode.MIN_DEGREE);

        treeManager.insert(root, new KVPair<Integer, String>(0, "root"));

        for (int i = 1; i < 10; i++) {
            if (root.isFull()) {
                root = treeManager.splitRoot(root);
            }

            treeManager.insert(root, new KVPair<Integer, String>(i, "node-" + i));
        }

        // print
        System.out.println(root);
    }
}
