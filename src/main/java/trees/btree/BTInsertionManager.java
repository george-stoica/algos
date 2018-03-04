package trees.btree;

import trees.btree.interfaces.BTInsertionManagerInterface;

/**
 * Created on 4/3/2018.
 */
public class BTInsertionManager implements BTInsertionManagerInterface<Integer, String> {
    @Override
    public BTNode insert(BTNode<Integer, String> node, KVPair<Integer, String> kvpair) {
        // if this is a leaf node insert the key
        if (node.isLeaf()) {
            if (!node.isFull()) {
                addNewEntry(node, kvpair);
                return node;
            }
        }

        // find next search branch
        int nextChildIndex = node.numKeys - 1;
        while (kvpair.compareTo(node.kvpairs[nextChildIndex]) < 0) {
            nextChildIndex--;
        }

        // check if child needs to be split
        if (node.children[nextChildIndex + 1].isFull()) {
            // split then proceed with the insert
            splitChild(node, nextChildIndex + 1);
            // get the value that has been promoted to the current node from the split child
            KVPair<Integer, String> splitValue = node.kvpairs[nextChildIndex + 1];

            if (kvpair.compareTo(splitValue) > 0) {
                nextChildIndex += 2; // increment 1 over the initial 1 increment
            }

            return insert(node.children[nextChildIndex], kvpair);
        }

        return insert(node.children[nextChildIndex + 1], kvpair);
    }

    @Override
    public BTNode splitChild(BTNode<Integer, String> parent, int childIndex) {

        BTNode<Integer, String> toSplit = parent.children[childIndex];

        // current number of keys is 2 * MAX_DEGREE - 1
        int splitIndex = BTNode.MIN_DEGREE;

        // create new node with the right half of the keys
        BTNode<Integer, String> newNode = new BTNode<>(BTNode.MIN_DEGREE);

        // copy over keys
        for (int i = 0; i < splitIndex - 1; i++) {
            newNode.kvpairs[i] = toSplit.kvpairs[i + splitIndex];
            newNode.numKeys++;
        }

        // copy over children if any
        if (!toSplit.isLeaf()) {
            for (int i = 0; i < splitIndex - 1; i++) {
                newNode.children[i] = toSplit.children[i + splitIndex];
            }
        }

        // reset split child number of keys
        toSplit.numKeys = BTNode.MIN_DEGREE - 1;

        // link new child to the correct index position
        // shift all tailing children in the array to make room
        for (int i = parent.numKeys; i >= childIndex + 1; i--) {
            parent.children[i + 1] = parent.children[i];
        }

        parent.children[childIndex + 1] = newNode;

        // copy middle key into parent
        addNewEntry(parent, toSplit.kvpairs[BTNode.MIN_DEGREE - 1]);

        // return updated parent node
        return parent;
    }

    private boolean addNewEntry(BTNode<Integer, String> node, KVPair<Integer, String> entry) {
        if (node.numKeys == 0) {
            node.kvpairs[0] = entry;
        } else {
            int index = node.numKeys - 1;

            while (node.kvpairs[index].compareTo(entry) > 0) {
                node.kvpairs[index + 1] = node.kvpairs[index];
                index--;
            }

            node.kvpairs[index + 1] = entry;
        }

        // increment number of children
        node.numKeys++;

        return true;
    }
}
