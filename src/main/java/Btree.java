/**
 * Created on 4/2/2018.
 *
 * Btree structure implementation with the following properties:
 * - keeps entries in simple array structure
 * - keeps children in array structure mapped to the key array structure
 */
public class Btree {
    public static int MIN_NUM_CHILDREN = 3;

    public static class Entry {
        public int key;
        public String value;

        public Entry(int key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%d - %s", key, value);
        }
    }

    public static class Node {
        private boolean leaf;
        private int numKeys;
        public Entry[] entries;
        public Node[] children;

        public Node(boolean leaf) {
            this.leaf = leaf;
            entries = new Entry[2 * MIN_NUM_CHILDREN - 1];
            children = new Node[2 * MIN_NUM_CHILDREN];
        }

        /**
         * Inserts new entry into node. does not handle root node.
         * @param newEntry
         * @return
         */
        public Node insert(Entry newEntry) {
            if (isLeaf()) {
                addNewEntry(newEntry);
            } else {
                int index = numKeys - 1;
                while (entries[index].key > newEntry.key) {
                    index--;
                }

                // found index position
                if (children[index + 1].isFull()) {
                    // split child then reattempt to insert
                    splitChild(index + 1, children[index+1]).insert(newEntry);
                } else {
                    return children[index + 1].insert(newEntry);
                }
            }
            return this;
        }

        public boolean delete(int key) {
            Node containerNode = find(key);

            if (containerNode == null) {
                return false;
            }

            return deleteFromChild(containerNode, key);
        }

        private boolean deleteFromChild(Node child, int key) {
            if (child.isLeaf()) {

                // case 1: key in leaf node with enough keys aside from deleted key
                // make sure that this node still fulfills the BTree properties after deleting the key
                // min numKeys must be MIN_NUM_CHILDREN - 1 at all times
                if (child.numKeys >= MIN_NUM_CHILDREN) {
                    if (!child.hasKey(key)) {
                        return false;
                    }

                    // remove key
                    int index = child.numKeys - 1;
                    while (key != child.entries[index].key) {
                        index--;
                    }

                    child.entries[index] = null;

                    // compact keys
                    for (int i = index; i < child.numKeys - 1; i++) {
                        child.entries[i] = child.entries[i + 1];
                    }

                    // decrement current number of keys
                    child.numKeys--;
                } else {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Leaf: " + leaf + ", ");

            for (int i = 0; i < numKeys; i++) {
                sb.append(entries[i] + ", ");
            }

            sb.append("\n");

            if (!isLeaf()) {
                for (int i = 0; i < numKeys; i++) {
                    sb.append(children[i].toString());
                }

                if (children[numKeys] != null) {
                    sb.append(children[numKeys].toString());
                }
            }

            return sb.toString();
        }

        // todo implement better search algo
        public Node find(int key) {
            // lookup key in current node
            if (hasKey(key)) {
                return this;
            }

            // reached leaf without finding key
            if (isLeaf()) {
                return null;
            }

            // lookup key in children
            // find lookup branch
            int index = 0;
            while (index < numKeys && key > entries[index].key) {
                index++;
            }

            return children[index].find(key);
        }

        private boolean hasKey(int key) {
            for (int i = 0; i < numKeys; i++) {
                if (entries[i].key == key) {
                    return true;
                }
            }

            return false;
        }

        public boolean isFull() {
            return numKeys == 2 * MIN_NUM_CHILDREN - 1;
        }

        public boolean isLeaf() {
            return leaf;
        }

        private Node splitChild(int childIndex, Node child) {
            Node newChild = new Node(child.isLeaf());

            // copy over entries
            for (int i = 0; i < MIN_NUM_CHILDREN - 1; i++) {
                newChild.entries[i] = child.entries[MIN_NUM_CHILDREN + i];
                newChild.numKeys++;
            }

            // copy over children if not leaf
            if (!isLeaf()) {
                for (int i = 0; i < MIN_NUM_CHILDREN; i++) {
                    newChild.children[i] = child.children[MIN_NUM_CHILDREN + i];
                }
            }

            // reduce entries and children in old node
            child.numKeys = MIN_NUM_CHILDREN - 1;

            // link new child to the correct index position
            // shift all tailing children in the array to make room
            for (int i = numKeys; i >= childIndex + 1; i--) {
                this.children[i + 1] = this.children[i];
            }

            this.children[childIndex + 1] = newChild;

            // move middle key of child to current node
            addNewEntry(child.entries[MIN_NUM_CHILDREN - 1]);

            // return current node to further use in search/insert
            // current node will be updated and will contain one extra key and one extra child
            return this;
        }

        // todo implement better search/insert algo
        private boolean addNewEntry(Entry entry) {
            if (numKeys == 0) {
                entries[0] = entry;
            } else {
                int index = numKeys - 1;

                while (entries[index].key > entry.key) {
                    entries[index + 1] = entries[index];
                    index --;
                }

                entries[index + 1] = entry;
            }

            // increment number of children
            numKeys++;

            return true;
        }
    }

    public static class BTreeManager {
        public static Node splitRoot(Node root) {
            Node newRoot = new Node(false);
            newRoot.children[0] = root;
            newRoot.splitChild(0, root);

            return newRoot;
        }

        public static Node initTree() {
            return new Node(true);
        }
    }

    public static void main(String[] args) {
        Node root = BTreeManager.initTree();

        for (int i = 0; i < 10; i++) {
            if (root.isFull()) {
                root = BTreeManager.splitRoot(root);
            }

            root.insert(new Entry(i, "valoarea " + i));
        }

        // print
        System.out.println(root);

        System.out.printf("Find key %d: %s", 7, root.find(7));
        System.out.printf("Find key %d: %s", 3, root.find(3));

        System.out.printf("Deleted key %d: %b", 3, root.delete(3));
        System.out.println();
        System.out.println("After delete");
        System.out.println(root);

        System.out.printf("Deleted key %d: %b", 7, root.delete(7));
        System.out.println();
        System.out.println("After delete");
        System.out.println(root);
    }
}
