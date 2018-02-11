/**
 * Created on 4/2/2018.
 */
public class Btree {
    public static int MIN_DEGREE = 3;

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
        public Entry[] keys;
        public Node[] children;

        public Node(boolean leaf) {
            this.leaf = leaf;
            keys = new Entry[2 * MIN_DEGREE - 1];
            children = new Node[2 * MIN_DEGREE];
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
                while (keys[index].key > newEntry.key) {
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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < numKeys; i++) {
                sb.append(keys[i] + ", ");
            }

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

        public boolean isFull() {
            return numKeys == 2 * MIN_DEGREE - 1;
        }

        public boolean isLeaf() {
            return leaf;
        }

        private Node splitChild(int childIndex, Node child) {
            Node newChild = new Node(child.isLeaf());

            // copy over keys
            for (int i = 0; i < MIN_DEGREE - 1; i++) {
                newChild.keys[i] = child.keys[MIN_DEGREE + i];
                newChild.numKeys++;
            }

            // copy over children if not leaf
            if (!isLeaf()) {
                for (int i = 0; i < MIN_DEGREE; i++) {
                    newChild.children[i] = child.children[MIN_DEGREE + i];
                }
            }

            // reduce keys and children in old node
            child.numKeys = MIN_DEGREE - 1;

            // link new child to the correct index position
            // shift all tailing children in the array to make room
            for (int i = numKeys; i >= childIndex + 1; i--) {
                this.children[i + 1] = this.children[i];
            }

            this.children[childIndex + 1] = newChild;

            // move middle key of child to current node
            addNewEntry(child.keys[MIN_DEGREE - 1]);

            // return current node to further use in search/insert
            // current node will be updated and will contain one extra key and one extra child
            return this;
        }

        // todo implement better search/insert algo
        private boolean addNewEntry(Entry entry) {
            if (numKeys == 0) {
                keys[0] = entry;
            } else {
                int index = numKeys - 1;

                while (keys[index].key > entry.key) {
                    keys[index + 1] = keys[index];
                    index --;
                }

                keys[index + 1] = entry;
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
    }
}
