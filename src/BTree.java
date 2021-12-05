import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Stack;

public class BTree {
    int maxKeyCount;        //the man number of key, the order of B+ Tree
    BPlusTreeNode root;     //root
    BPlusTreeNode first;    //the first child node

    /**
     * constructor of BPlusTree
     *
     * @param path: the data file path
     */
    public BTree(String path) {
        this.maxKeyCount = 4;  // the max number of key index per node
        this.root = null;  //initialize root
        this.first = null;

        File data = new File(path);
        try {
            BufferedReader br = new BufferedReader(new FileReader(data));
            String line;
            while ((line = br.readLine()) != null) {
                this.insert(Integer.parseInt(line), 0); //insert value as 0
            }
            System.out.println("Building an initial B+-Tree... \nLaunching B+-Tree test program...\nWaiting for your commands: ");
        } catch (Exception e) {
            System.out.println("Cannot find the data file. Please check the name.");
            System.exit(1);  //terminate the progress with error
        }
    }

    /**
     * insert the value into the B+ Tree
     *
     * @param key   search key
     * @param value value is stored related to key (entry<key,value> pair) which is 0 in this supplement
     */
    public void insert(int key, int value) {
        BPlusTreeNode node = this.root;
        int i;
        Stack<BPlusTreeNode> nodeStack = new Stack<BPlusTreeNode>();
        Stack<Integer> indexStack = new Stack<Integer>();

        nodeStack.push(null);
        indexStack.push(0);
        if (node != null) {
            i = node.num;
            if (key < node.key[i - 1]) {
                while (true) {
                    for (i = 0; node.key[i] < key; i++) ; // find the value it stored in.
                    nodeStack.push(node);
                    indexStack.push(i);
                    if (!node.isLeaf) {
                        node = node.children[i];  //if it's not a leaf, the tree grows
                    } else {
                        break; //if it's a leaf, then finish insert.
                    }
                }
            } else {
                while (true) {
                    nodeStack.push(node);
                    if (!node.isLeaf) {
                        node.key[i - 1] = key; // set the max index key as key.
                        indexStack.push(i - 1);

                        node = node.children[i - 1];
                        i = node.num;
                    } else {
                        indexStack.push(i);
                        break;
                    }
                }
            }
        } else {
            node = new BPlusTreeNode(this.maxKeyCount);
            this.first = this.root = node;
            indexStack.push(0);
            nodeStack.push(node);
            node.num = 0;
            node.isLeaf = true;
            node.next = null;
        }
        //recursion insert the key,value pair
        this.insert(key, value, null, nodeStack, indexStack);
    }

    /**
     * recursion insert the key,value pair
     *
     * @param key        index key
     * @param value      value related to key
     * @param child      point to child node
     * @param nodeStack  stack of node
     * @param indexStack stack of index
     */
    private void insert(int key, int value, BPlusTreeNode child,
                        Stack<BPlusTreeNode> nodeStack, Stack<Integer> indexStack) {
        BPlusTreeNode node = nodeStack.peek(), parent, sibling;
        int j = indexStack.peek();  //return the first one in the index stack
        int i, m, k;
        if (node.num < this.maxKeyCount) { // this node has free space
            for (i = node.num; i > j; i--) {
                node.children[i] = node.children[i - 1];
                node.key[i] = node.key[i - 1];
                node.value[i] = node.value[i - 1];
            }
            node.children[j] = child;
            node.key[j] = key;
            node.value[j] = value;
            node.num++;
            return;
        }

        // split the node
        sibling = new BPlusTreeNode(this.maxKeyCount);
        m = (this.maxKeyCount + 1) / 2; // M=(KEY_COUNT+1)/2;
        sibling.next = node.next;  //linked them together
        sibling.isLeaf = node.isLeaf;
        sibling.num = this.maxKeyCount + 1 - m;
        node.next = sibling;
        node.num = m;
        if (j < m) {
            for (i = m - 1, k = 0; i < this.maxKeyCount; i++, k++) {
                sibling.key[k] = node.key[i];
                sibling.value[k] = node.value[i];
                sibling.children[k] = node.children[i];
            }
            for (i = m - 2; i >= j; i--) {
                node.key[i + 1] = node.key[i];
                node.value[i + 1] = node.value[i];
                node.children[i + 1] = node.children[i];
            }
            node.key[j] = key;
            node.value[j] = value;
            node.children[j] = child;
        } else {
            for (i = m, k = 0; i < j; i++, k++) {
                sibling.key[k] = node.key[i];
                sibling.value[k] = node.value[i];
                sibling.children[k] = node.children[i];
            }
            sibling.key[k] = key;
            sibling.value[k] = value;
            sibling.children[k] = child;
            k++;
            for (; i < this.maxKeyCount; i++, k++) {
                sibling.key[k] = node.key[i];
                sibling.value[k] = node.value[i];
                sibling.children[k] = node.children[i];
            }
        }

        // reset the parent key index
        nodeStack.pop();
        indexStack.pop();
        parent = nodeStack.peek();
        j = indexStack.peek();
        if (parent != null) {
            parent.children[j] = sibling;
            key = node.key[m - 1];
            this.insert(key, value, node, nodeStack, indexStack);
        } else {
            this.root = parent = new BPlusTreeNode(this.maxKeyCount);
            parent.num = 2;
            parent.next = null;
            parent.isLeaf = false;
            parent.key[0] = node.key[m - 1];
            parent.children[0] = node;
            parent.key[1] = sibling.key[this.maxKeyCount - m];
            parent.children[1] = sibling;
        }
    }

    /**
     * inserts the certain number of numbers into the tree.
     * links with UI
     *
     * @param low:  lower bound
     * @param high: upper bound
     * @param num:  the number of <key,value> pair need to insert in.
     */
    public void insert(int low, int high, int num) {
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            insert(random.nextInt(high - low) + low, 0);
        }
    }

    /**
     * deletes the key
     *
     * @param key the key need to delete
     * @return true(delete successful), false(fail)
     */
    public boolean delete(long key) {
        //root is null or key is lager than the biggest key in the root
        if (this.root == null || key > this.root.key[this.root.num - 1]) {
            return false;
        }
        BPlusTreeNode node = this.root;
        int i, new_key;
        boolean key_found = false;
        Stack<BPlusTreeNode> nodeStack = new Stack<BPlusTreeNode>();
        Stack<Integer> indexStack = new Stack<Integer>();

        nodeStack.push(null);
        indexStack.push(0);

        while (true) {
            for (i = 0; node.key[i] < key; i++) ; //uses to find the position as same as insert
            nodeStack.push(node);
            indexStack.push(i);

            if (!node.isLeaf) { // mid node
                node = node.children[i];
            } else {
                if (node.key[i] == key) { //check if it finds the key
                    key_found = true;
                    node.num--;
                    for (; i < node.num; i++) {// delete k[i]
                        node.key[i] = node.key[i + 1];
                        node.value[i] = node.value[i + 1];
                        node.children[i] = node.children[i + 1];
                    }
                    new_key = node.key[node.num - 1];
                    // reset the key of parent node
                    i = nodeStack.size() - 2;
                    BPlusTreeNode node_i = nodeStack.elementAt(i);
                    BPlusTreeNode node_i1 = nodeStack.peek();
                    int index_i = indexStack.elementAt(i);
                    int index_i1 = indexStack.peek();
                    if (i > 0 && node_i.key[index_i] == key
                            && node_i1.num == index_i1) {

                        node_i.key[index_i] = new_key;
                        for (i--; i > 0; i--) {
                            node_i = nodeStack.elementAt(i);
                            node_i1 = nodeStack.elementAt(i + 1);
                            index_i = indexStack.elementAt(i);
                            index_i1 = indexStack.elementAt(i + 1);
                            if (node_i.key[index_i] == key
                                    && node_i1.num - 1 == index_i1) {
                                node_i.key[index_i] = new_key;
                            } else {
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }

        if (key_found) {
            this.check(nodeStack, indexStack);
            return true;
        } else {
            return false;
        }
    }

    /**
     * deletes the number between low and high
     *
     * @param low  lower bound of number
     * @param high upper bound of number
     */
    public void delete(int low, int high) {
        for (int i = low; i < high; i++) {
            delete(i);
        }
    }

    /**
     * check if it needs to merge two nodes
     *
     * @param nodeStack
     * @param indexStack
     */
    private void check(Stack<BPlusTreeNode> nodeStack, Stack<Integer> indexStack) {
        BPlusTreeNode node = nodeStack.peek();
        BPlusTreeNode parent, lchild, rchild;
        int pos;

        //too small number of elements in the node
        while (node.num < (this.maxKeyCount + 1) / 2) {
            nodeStack.pop();
            indexStack.pop();
            pos = indexStack.peek();
            parent = nodeStack.peek();
            if (parent == null) { //root which doesn't have parent node
                if (node.num <= 1) {
                    // reset root
                    if (node.children[0] != null) {
                        this.root = node.children[0];
                        // delete root and set the children as root
                        node = null;
                    } else {
                        // delete the last number in the tree
                        if (node.num == 0) {
                            this.root = null;
                            // delete the root
                            node = null;
                        }
                    }
                }
                break;
            }

            if (pos == 0) {// the Leftmost position
                lchild = node;
                rchild = parent.children[pos + 1];
            } else {
                pos--;
                rchild = node;
                lchild = parent.children[pos];
            }
            this.merge(lchild, pos, rchild, parent);
            node = parent;
        }
    }

    /**
     * merge nodes
     *
     * @param lchild left child
     * @param index  index is the position of lchild in the parent node
     * @param rchild right child
     * @param parent parent node
     */
    private boolean merge(BPlusTreeNode lchild, int index,
                          BPlusTreeNode rchild, BPlusTreeNode parent) {
        int i, j, m;
        int k = lchild.num + rchild.num;
        if (k <= this.maxKeyCount) {
            // merge together, elements in the right child will be merged into left child
            for (i = lchild.num, j = 0; j < rchild.num; j++, i++) {
                lchild.key[i] = rchild.key[j];
                lchild.value[i] = rchild.value[j];
                lchild.children[i] = rchild.children[j];
            }
            lchild.next = rchild.next;
            lchild.num = k;

            // reset the index in the parent node
            parent.num--;
            parent.key[index] = lchild.key[k - 1];
            for (i = index + 1; i < parent.num; i++) {
                parent.children[i] = parent.children[i + 1];
                parent.key[i] = parent.key[i + 1];
            }

            // delete the node after merging
            rchild = null;
            return true;
        } else {
            // elements are divided equally into two nodes
            BPlusTreeNode[] children = new BPlusTreeNode[this.maxKeyCount * 2];
            int[] key = new int[this.maxKeyCount * 2];
            int[] value = new int[this.maxKeyCount * 2];

            // get all elements
            for (i = 0; i < lchild.num; i++) {
                children[i] = lchild.children[i];
                key[i] = lchild.key[i];
                value[i] = lchild.value[i];
            }
            for (j = 0; j < rchild.num; i++, j++) {
                children[i] = rchild.children[j];
                key[i] = rchild.key[j];
                value[i] = rchild.value[j];
            }

            // divide elements
            // m = (lchild->n + rchild->n) / 2
            // number of elements should be added in the left/right child
            m = k >> 1;
            for (i = 0; i < m; i++) {
                lchild.children[i] = children[i];
                lchild.key[i] = key[i];
                lchild.value[i] = value[i];
            }
            lchild.num = m;

            for (j = 0; i < k; i++, j++) {
                rchild.children[j] = children[i];
                rchild.key[j] = key[i];
                rchild.value[j] = value[i];
            }
            rchild.num = k - m;

            // reset the index in the parent node
            parent.key[index] = key[m - 1];
            return false;
        }
    }

    /**
     * search the key in range of [x,y]
     *
     * @param low high
     * @return string if the key being found return all the value, if not return "can not found"
     */
    public String search(int low, int high) {
        String res = "";
        for (int i = low; i <= high; i++) {
            int count = search(i);
            while (count != -1) {
                for (int j = 0; j < count; j++) {
                    res = res + "[" + i + "]";
                }
                break;
            }
        }
        if (res.equals("")) {
            res = "";
        }
        return res;
    }


    /**
     * search key
     *
     * @param key searches by key
     * @return the value of key, if not find, return -1
     */
    public int search(int key) {
        //root is null or key is larger than the biggest key of root
        if (this.root == null || key > this.root.key[this.root.num - 1]) {
            return -1;
        }
        BPlusTreeNode node = this.root;
        int count = 0;
        int i;

        while (node != null) {
            for (i = 0; node.key[i] < key; i++) ; //find the position
            if (!node.isLeaf) {// middle node
                node = node.children[i];
            } else {
                if (node.key[i] == key) {// find as same as delete
                    count++;
                    for (int j = i + 1; j < node.key.length; j++) {
                        if (node.key[j] == key) {
                            count++;
                        }
                    }
                    while (node.next != null) {
                        for (int k = 0; k < node.next.key.length; k++) {
                            if (node.next.key[k] == key) {
                                count++;
                            }
                        }
                        node = node.next;
                    }
                    return count;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * print the tree
     */
    public void print() {
        if (this.root != null)
            BPlusTreeNode.print_2(this.root, 0);
    }

    /**
     * get height of the tree
     *
     * @return height
     */
    public int getHeight() {
        int h = 0;
        BPlusTreeNode node = this.root;
        while (node != null) {
            node = node.children[0];
            h++;
        }
        return h;
    }

    /**
     * get number of nodes
     *
     * @return number of nodes
     */
    public int getNodeTotalCount() {
        return getNodeCount(this.root);
    }

    /**
     * get the number of nodes in the subtree followed by node
     *
     * @param node node
     * @return number of nodes
     */
    private int getNodeCount(BPlusTreeNode node) {
        int count = 0;
        if (node == null) {
            return 0;
        } else if (node.isLeaf) {
            return 1;
        } else {
            for (int i = 0; i < node.num; i++) {
                count += getNodeCount(node.children[i]);
            }
            count++;
        }
        return count;
    }

    /**
     * get number of leaf nodes
     *
     * @return number of leaf nodes
     */
    public int getLeafNodeCount() {
        int leafCount = 0;
        BPlusTreeNode node = this.first;

        while (node != null) {
            leafCount++;
            node = node.next;
        }
        return leafCount;
    }

    /**
     * get number of keys
     * sum the number od keys on the leaf nodes
     *
     * @return number of keys
     */
    public int getKeyTotalCount() {
        int keyCount = 0;
        BPlusTreeNode node = this.first;

        while (node != null) {
            keyCount += node.num;
            node = node.next;
        }
        return keyCount;
    }

    /**
     * get the Average fill factor (used space/total space) of the nodes
     *
     * @return
     */
    public String getAveFillFactor() {
        int totalSpace = this.getLeafNodeCount() * 4;
        int usedSpace = this.getKeyTotalCount();
        float aveFillFactor_f = (float) usedSpace / totalSpace;
        return new DecimalFormat("#.##%").format(aveFillFactor_f);
    }

    public void printStats() {
        System.out.println("Statistics of the B+-tree: ");
        System.out.println("    Total number of nodes:" + getNodeTotalCount());
        System.out.println("    Total number of data entries:" + getLeafNodeCount());
        System.out.println("    Total number of index entries:" + getKeyTotalCount());
        System.out.println("    Average fill factor:" + getAveFillFactor() + "%");
        System.out.println("    Height of tree:" + getHeight());
    }
}

/**
 * define the Node of B+ Tree
 */
class BPlusTreeNode {
    private static final long serialVersionUID = 1L;

    static int index = 0;
    int position; // Record the serial number of the index where the node is located

    int max; // max number of the keys in the index node or values in the leaf node
    boolean isLeaf;    // figure out if it's leaf node
    int num;    // number of keys
    int[] key;    // key.length = num
    int[] value;    // value corresponding to key
    BPlusTreeNode[] children;    // children node
    BPlusTreeNode next;            // at the same depth of tree

    /**
     * constructor
     *
     * @param m oder of the B+ Tree
     */
    public BPlusTreeNode(int m) {
        position = index++;
        max = m;

        isLeaf = true;
        num = 0;

        key = new int[max];
        value = new int[max];
        children = new BPlusTreeNode[max];
        for (int i = 0; i < max; i++) {
            key[i] = -1;
            value[i] = -1;
            children[i] = null;
        }
        next = null;
    }

    /**
     * print horizontally
     *
     * @param node
     * @param level
     */
    public static void print_2(BPlusTreeNode node, int level) {
        int i, j;
        if (node.isLeaf) {
            for (j = 0; j < level; j++) {
                System.out.print(" ");
            }
            System.out.print("{");
            for (i = 0; i < node.num - 1; i++) {
                System.out.print("(" + node.key[i] + "," + node.value[i] + "),");
            }
            System.out.println("(" + node.key[i] + "," + node.value[i] + ")}");
        } else {
            for (j = 0; j < level; j++) {
                System.out.print(" ");
            }
            System.out.print("{");
            for (i = 0; i < node.num - 1; i++) {
                System.out.print(node.key[i] + ",");
            }
            System.out.println(node.key[i] + "}");
            for (i = 0; i < node.num; i++) {
                print_2(node.children[i], level + 4);
            }
        }
    }
}
