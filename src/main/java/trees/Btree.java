package trees;

/**
 * Created on 4/2/2018.
 * <p>
 * trees.Btree structure implementation with the following properties:
 * - keeps kvpairs in simple array structure
 * - keeps children in array structure mapped to the key array structure
 * BTree visualization: http://www.cs.usfca.edu/~galles/visualization/BTree.html
 */
public class Btree {
    public static int MIN_DEGREE = 3; // min number of children

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
        private boolean root;
        private int numKeys;
        public Entry[] entries;
        public Node[] children;

        public Node(boolean leaf, boolean root) {
            this.leaf = leaf;
            this.root = root;
            entries = new Entry[2 * MIN_DEGREE - 1];
            children = new Node[2 * MIN_DEGREE];
        }

        /**
         * Inserts new entry into node. does not handle root node.
         *
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
                    splitChild(index + 1, children[index + 1]).insert(newEntry);
                } else {
                    return children[index + 1].insert(newEntry);
                }
            }
            return this;
        }

        public boolean delete(int key) {
            int keyIndex = getKeyIndex(key);

            if (keyIndex > 0) {
                if (isLeaf()) {
                    return deleteLeafKey(key);
                } else {
                    return deleteInnerNodeKey(key);
                }
            } else {
                // if this is a leaf node then the key is not in the tree
                if (isLeaf()) {
                    return false;
                }

                // if the child where the key is supposed to be has less than
                // MIN_NUM_KEYS keys then merge it with a sibling
                if (children[keyIndex].numKeys < MIN_DEGREE) {
                    Node deleteRoot = balanceNode(keyIndex);

                    // 1. if the current node is the right-most node then the
                    // last child of this node was merged with it's previous node
                    // start recursing on that previous node as the last one was
                    // removed during the merge.
                    // 2. if this node's children were merged then
                    // the current node's number of keys has been decreased.
                    int rootIndex = keyIndex == numKeys && keyIndex > numKeys? keyIndex - 1 : keyIndex;

                    deleteRoot.children[rootIndex].delete(key);
                }
            }
//            if (isLeaf()) {
//
//                // case 1: key in leaf node with enough keys aside from deleted key
//                // make sure that this node still fulfills the BTree properties after deleting the key
//                // min numKeys must be MIN_DEGREE - 1 at all times
//                if (isRoot() || numKeys > MIN_DEGREE) {
//                    return deleteLeafKey(key);
//                } else {
//                    // TODO implement other btree delete cases here
//                    return false;
//                }
//            }

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
            return getKeyIndex(key) != -1;
        }

        /**
         * Returns index of key in the key array
         *
         * @param key
         * @return
         */
        private int getKeyIndex(int key) {
            for (int i = 0; i < numKeys; i++) {
                if (entries[i].key == key) {
                    return i;
                }
            }

            return -1;
        }

        public boolean isFull() {
            return numKeys == 2 * MIN_DEGREE - 1;
        }

        public boolean isLeaf() {
            return leaf;
        }

        public boolean isRoot() {
            return this.root;
        }

        private Node splitChild(int childIndex, Node child) {
            Node newChild = new Node(child.isLeaf(), false);

            // copy over upper half of the kvpairs
            for (int i = 0; i < MIN_DEGREE - 1; i++) {
                newChild.entries[i] = child.entries[MIN_DEGREE + i];
                newChild.numKeys++;
            }

            // copy over children if not leaf
            if (!isLeaf()) {
                for (int i = 0; i < MIN_DEGREE; i++) {
                    newChild.children[i] = child.children[MIN_DEGREE + i];
                }
            }

            // reduce kvpairs and children in old node
            child.numKeys = MIN_DEGREE - 1;

            // link new child to the correct index position
            // shift all tailing children in the array to make room
            for (int i = numKeys; i >= childIndex + 1; i--) {
                this.children[i + 1] = this.children[i];
            }

            this.children[childIndex + 1] = newChild;

            // move middle key of child to current node
            addNewEntry(child.entries[MIN_DEGREE - 1]);

            // return current node to further use in search/insert
            // current node will be updated and will contain one extra key and one extra child
            return this;
        }

        /**
         * Merge child at childIndex with child at childIndex + 1
         * NOTE: this method covers cases 2c, 3b
         * @return
         */
        private Node mergeChildren(int childIndex) {
            Node firstChild = children[childIndex];
            Node sibling = children[childIndex + 1];

            // TODO verifica
            // pull a key from parent into first merge node
            firstChild.entries[numKeys - 1] = entries[childIndex];

            // copy over other node's keys and children
            for (int i = 0; i < firstChild.numKeys; i++) {
                firstChild.entries[i + numKeys] = sibling.entries[i];
            }

            if (!isLeaf()) {
                for (int i = 0; i < firstChild.numKeys; i++) {
                    firstChild.children[i + numKeys] = sibling.children[i];
                }
            }

            // compact keys in current node after moving one key to child
            for (int i = childIndex + 1; i < numKeys; i++) {
                entries[i - 1] = entries[i];
            }

            // compact child mappings after merging in child[firstMergeChildIndex + 1]
            // this removes the link to the sibling child
            for (int i = childIndex + 2; i <= numKeys; i++) {
                children[i - 1] = children[i];
            }

            // update child key count: 1 key from current node + sibling keys
            firstChild.numKeys += sibling.numKeys + 1;

            // update current node key count
            numKeys -= 1;

            return this;
        }

        private Node getPredecessor(int key) {
            int keyIndex = getKeyIndex(key);
            int predIndex = keyIndex == 0 ? 0 : keyIndex - 1;
            return children[predIndex];
        }

        private int getPredecessorKeyIndex(int key) {
            Node predecessorNode = getPredecessor(key);
            int predecessorIndex = predecessorNode.numKeys - 1;

            while (key < predecessorNode.entries[predecessorIndex].key) {
                predecessorIndex--;
            }

            return predecessorIndex;
        }

        private int getSuccessorKeyIndex(int key) {
            Node successorNode = getPredecessor(key);
            int successorIndex = 0;

            for (int i = 0; i < successorNode.numKeys; i++) {
                if (successorNode.entries[i].key >= key) {
                    return i;
                }
            }

            return successorIndex;
        }

        private Node getSuccessor(int key) {
            int keyIndex = getKeyIndex(key);
            int succIndex = keyIndex + 1;
            return children[succIndex];
        }

        // case 1: key in leaf node with enough keys aside from deleted key
        // make sure that this node still fulfills the BTree properties after deleting the key
        // min numKeys must be MIN_DEGREE - 1 at all times
        private boolean deleteLeafKey(int key) {
            if (!hasKey(key)) {
                return false;
            }

            // remove key
            int index = numKeys - 1;
            while (key != entries[index].key) {
                index--;
            }

            entries[index] = null;

            // compact keys
            for (int i = index; i < numKeys - 1; i++) {
                entries[i] = entries[i + 1];
            }

            // decrement current number of keys
            numKeys--;

            return true;
        }

        // case 2: delete inner node key when there is no need
        // to resize the tree height
        private boolean deleteInnerNodeKey(int key) {
            Node predecessor = getPredecessor(key);
            Node successor = getSuccessor(key);

            // if predecessor node has more than MIN_DEGREE - 1 keys then
            // pull up the predecessor key to this node
            if (predecessor.numKeys >= MIN_DEGREE) {
                int keyIndex = getKeyIndex(key);
                int predKeyIndex = getPredecessorKeyIndex(key);

                entries[keyIndex] = predecessor.entries[predKeyIndex];

                // null predecessor key reference
                predecessor.entries[predKeyIndex] = null;

                // reset predecessor key set size
                predecessor.numKeys--;

                return true;
            }
            // if successor node has more than MIN_DEGREE - 1 keys then
            // pull up the successor key to this node
            else if (getSuccessor(key).numKeys >= MIN_DEGREE) {
                int keyIndex = getKeyIndex(key);
                int successorKeyIndex = getSuccessorKeyIndex(key);

                entries[keyIndex] = successor.entries[successorKeyIndex];

                successor.entries[successorKeyIndex] = null;
                successor.numKeys--;

                return true;
            }
            // merge successor and predecessor nodes
            else {
                int keyIndex = getKeyIndex(key);
                int predKeyIndex = getPredecessorKeyIndex(key);

                // pulls key from current node into predecessor then
                // merges predecessor with successor
                mergeChildren(predKeyIndex);

                // delete key from newly merged node
                return children[predKeyIndex].delete(key);
            }
        }

        /**
         * case 3
         * @param childIndex
         * @return
         */
        private Node balanceNode(int childIndex) {
            if (childIndex > 0 && children[childIndex - 1].numKeys >= MIN_DEGREE) {
                return borrowFromPrev(childIndex);
            } else if (childIndex <= numKeys && children[childIndex + 1].numKeys >= MIN_DEGREE) {
                return borrowFromNext(childIndex);
            } else {
                // both neighbouring siblings have the minimum number of keys
                // need to merge them.
                // merge child with right sibling
                if (childIndex == numKeys) {
                    return mergeChildren(childIndex - 1);
                } else {
                    return mergeChildren(childIndex);
                }
            }
        }

        private Node borrowFromPrev(int childIndex) {
            Node child = children[childIndex];
            Node sibling = children[childIndex - 1];

            // shift keys in child one step to the right to make room for borrowed key
            for (int i = child.numKeys - 1; i >= 0; i--) {
                child.entries[i + 1] = child.entries[i];
            }

            // shift children also
            if (!child.isLeaf()) {
                for (int i = child.numKeys; i >=0; i--) {
                    child.children[i + 1] = child.children[i];
                }
            }

            // add key from left sibling to first position
            if (!isLeaf()) {
                // pull one key from the current node into the child
                child.entries[0] = entries[childIndex - 1];
            }

            // move rightmost key from left sibling to parent
            entries[childIndex - 1] = sibling.entries[sibling.numKeys - 1];

            // update key counters
            child.numKeys += 1;
            sibling.numKeys -= 1;

            return this;
        }

        private Node borrowFromNext(int childIndex) {
            Node child = children[childIndex];
            Node sibling = children[childIndex + 1];

            // add left-most key from right sibling to child
            child.entries[child.numKeys] = entries[childIndex];

            // copy over children also
            child.children[child.numKeys + 1] = sibling.children[0];

            // move sibling's first key to parent
            entries[childIndex] = sibling.entries[0];

            // shift keys left in sibling node
            for (int i = 0; i < sibling.numKeys - 1; i++) {
                sibling.entries[i] = sibling.entries[i + 1];
            }

            // shift children in sibling
            if (!isLeaf()) {
                for (int i = 0; i < sibling.numKeys + 1; i++) {
                    sibling.children[i] = sibling.children[i + 1];
                }
            }

            // reset key counters
            child.numKeys += 1;
            sibling.numKeys -= 1;

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
                    index--;
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
            Node newRoot = new Node(false, true);
            newRoot.children[0] = root;
            newRoot.splitChild(0, root);

            return newRoot;
        }

        public static boolean deleteKey(Node root, int key) {
            Node containerNode = root.find(key);

            if (containerNode == null) {
                return false;
            }

            return containerNode.delete(key);
        }

        public static Node initTree() {
            return new Node(true, true);
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

        System.out.printf("Deleted key %d: %b", 3, BTreeManager.deleteKey(root, 3));
        System.out.println();
        System.out.println("After delete");
        System.out.println(root);

        System.out.printf("Deleted key %d: %b", 7, BTreeManager.deleteKey(root, 7));
        System.out.println();
        System.out.println("After delete");
        System.out.println(root);
    }
}
