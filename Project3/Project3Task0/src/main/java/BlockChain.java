import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Name: Leo Lin
// AndrewID: hungfanl

public class BlockChain extends java.lang.Object{
    public List<Block> blockchain;
    public String chain_hash;
    public int hashes_per_second;
    public static final int HASH_ROUND = 2000000;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static final String LEADING_ZERO = "000000000000000000000000000000000000000000000000000";

    public BlockChain(){
        blockchain = new ArrayList<>();
        chain_hash = "";
        hashes_per_second = 0;
    }

    public static void main(String[] args) {
        // This list contains all the selection that the user is allowed to choose.
        String[] message = {"View basic blockchain status.", "Add a transaction to the blockchain.",
        "Verify the blockchain.", "View the blockchain.", "Corrupt the chain.",
        "Hide the corruption by repairing the chain.", "Exit."};
        BlockChain bc = new BlockChain();
        // Create a new genesis block with the difficulty of 2
        Block b = new Block(bc.getChainSize(), bc.getTime(), "Genesis", 2);
        bc.addBlock(b);
        bc.computeHashesPerSecond();
        Scanner readInput = new Scanner(System.in);
        Timestamp start;
        Timestamp end;
        int time;
        // Set a boolean finish that only if the user insert 6 will be true.
        boolean finish = false;
        while(!finish) {
            for(int i = 0; i < message.length; i++) {
                System.out.println(i + ". " + message[i]);
            }
            int option = Integer.parseInt(readInput.nextLine());
            switch (option) {
                // Check the information of the chain
                case 0:
                    System.out.println("Current size of chain: " + bc.getChainSize());
                    System.out.println("Difficulty of most recent block: " + bc.getBlock(bc.getChainSize()-1).getDifficulty());
                    System.out.println("Total difficulty for all blocks: " + bc.getTotalDifficulty());
                    System.out.println("Approximate hashes per second on this machine: " + bc.getHashesPerSecond());
                    System.out.println("Expected total hashes required for the whole chain: " + bc.getTotalExpectedHashes());
                    System.out.println("Nonce for most recent block: " + bc.getBlock(bc.getChainSize()-1).getNonce());
                    System.out.println("Chain hash: " + bc.getChainHash());
                    break;
                // Allows the user to add a block to the chain.
                case 1:
                    System.out.println("Enter difficulty > 0");
                    int difficulty = Integer.parseInt(readInput.nextLine());
                    System.out.println("Enter transaction");
                    String data = readInput.nextLine();
                    start = bc.getTime();
                    // Generate a new block with the information get from the user.
                    bc.addBlock(new Block(bc.getChainSize(), bc.getTime(), data, difficulty));
                    // record the time for adding the block
                    end = bc.getTime();
                    time = (int) (end.getTime() - start.getTime());
                    System.out.println("Total execution time to add this block was " + time + " milliseconds");
                    break;
                // Check if the chain is valid.
                case 2:
                    start = bc.getTime();
                    String validation = bc.isChainValid();
                    end = bc.getTime();
                    time = (int) (end.getTime() - start.getTime());
                    System.out.print("Chain verification: ");
                    if (!validation.equals("True")) System.out.println("False");
                    System.out.println(validation);
                    System.out.println("Total execution time to verify the chain was " + time + " milliseconds");
                    break;
                // View the block (json style)
                case 3:
                    System.out.println("View the Blockchain");
                    System.out.println(bc.toString());
                    break;
                // Corrupt the chain by changing the information in the block.
                case 4:
                    System.out.println("corrupt the Blockchain");
                    System.out.println("Enter block ID of block to corrupt");
                    int index = Integer.parseInt(readInput.nextLine());
                    System.out.println("Enter new data for block " + index);
                    String corrupt_message = readInput.nextLine();
                    bc.getBlock(index).setData(corrupt_message);
                    System.out.println("Block " + index + " now holds " + corrupt_message);
                    break;
                // Fix the block by recomputing the nonce to meet the difficulty requirement.
                case 5:
                    start = bc.getTime();
                    if(!bc.isChainValid().equals("True")) bc.repairChain();
                    end = bc.getTime();
                    time = (int) (end.getTime() - start.getTime());
                    System.out.println("Total execution time required to repair the chain was " + time +" milliseconds");
                    break;
                case 6:
                    finish = true;
                    break;
                default:
                    break;
            }

        }
        readInput.close();
    }

    public String getChainHash() {
        return chain_hash;
    }

    public Timestamp getTime(){
        return new Timestamp(System.currentTimeMillis());
    }

    public Block getLatestBlock(){
        return blockchain.get(this.getChainSize()-1);
    }

    public int getChainSize(){
        return blockchain.size();
    }

    // Get the information of a particular block in the chain.
    public Block getBlock(int i) {
        if(i >= getChainSize()) {
            System.out.println("Insert number exceed block size");
            return null;
        }
        return blockchain.get(i);
    }

    // Compute the expected time of calculating hash by doing the calculation for 20000 times and get the average time.
    public void computeHashesPerSecond() {
        String s = "00000000";
        Timestamp start = getTime();
        for(int i = 0; i < HASH_ROUND; i++) {
            calculateHash(s);
        }
        Timestamp end = getTime();
        hashes_per_second = (int) ( (double)HASH_ROUND / (end.getTime() - start.getTime()) * 1000);
    }

    public int getHashesPerSecond() {
        return  hashes_per_second;
    }
    // Add a block in the chain and revise the chain information.
    public void addBlock(Block block){
        if(getChainSize() == 0) {
            block.setPreviousHash("");
        }
        else block.setPreviousHash(chain_hash);
        blockchain.add(block);
        chain_hash = block.proofOfWork();
    }
    // Transfer the chain in the form of json.
    public String toString() {
        BlockChain bc = new BlockChain();
        for(int i = 0; i < getChainSize(); i++) {
            bc.blockchain.add(getBlock(i));
        }
        bc.hashes_per_second = getHashesPerSecond();
        bc.chain_hash = getChainHash();
        Gson gson = new Gson();
        String messageToSend = gson.toJson(bc);
        return messageToSend;
    }

    // Adding the difficulty of all blocks and come up with a total difficulty of the chain.
    public int getTotalDifficulty() {
        int totalDifficulty = 0;
        for(Block b: blockchain) {
            totalDifficulty += b.getDifficulty();
        }
        return totalDifficulty;
    }

    // Get the expected number of hashes the chain requires to compute.
    public double getTotalExpectedHashes() {
        double totalExpectedHashes = 0;
        for(Block b: blockchain) {
            totalExpectedHashes += Math.pow(16, b.getDifficulty());
        }
        return  totalExpectedHashes;
    }

    // Return true if the function founds no error, the type of error if the function finds any.
    public String isChainValid() {
        for(int i = 0; i < getChainSize(); i++) {
            Block b = getBlock(i);
            String s = b.calculateHash();
            for(int j = 0; j < b.getDifficulty(); j++) {
                //Improper hash on node 1 Does not begin with 00
                if(s.charAt(j) != '0')
                    return "Improper hash on node " + i + " does not begin with " +
                            LEADING_ZERO.substring(0, b.getDifficulty());
            }
            if(i != 0 && !getBlock(i-1).calculateHash().equals(b.getPreviousHash()))
                return "Block " + i + " does not have a matching hash.";
        }
        if(!getBlock(getChainSize()-1).calculateHash().equals(chain_hash))
            return "The chain hash is different from the hash of the last block.";
        return "True";
    }

    // Fix the chain by changing the nonce and get the right hash number.
    public void repairChain() {
        for(int i = 0; i < getChainSize(); i++) {
            Block b = getBlock(i);
            if(i != getChainSize()-1) getBlock(i+1).previousHash = b.proofOfWork();
            else chain_hash = b.proofOfWork();
        }
    }

    // Calculate the hash number.
    public String calculateHash(String s) {
        String hash_value = null;
        try {
            MessageDigest md;
            // Compute SHA-265 code for the input
            md = MessageDigest.getInstance("SHA-256");
            md.update(s.getBytes(StandardCharsets.UTF_8));
            hash_value = bytesToHex(md.digest());
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("No Hash available" + e);
        }
        return String.valueOf(hash_value);
    }

    // Transfer the byte representation of a string to a hex value.
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
