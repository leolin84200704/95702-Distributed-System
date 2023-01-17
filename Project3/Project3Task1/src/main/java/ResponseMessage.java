import com.google.gson.Gson;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
// Refer to https://www.javatpoint.com/java-json-example
// Name: Leo Lin
// AndrewID: hungfanl
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ResponseMessage {
    static Socket clientSocket = null;
    static int serverPort = 7777;
    static JSONObject listenJson = new JSONObject();
    static JSONObject returnJson = new JSONObject();
    static BlockChain bc;

    public static void main(String[] args) {
        bc = new BlockChain();
        // Create a new genesis block with the difficulty of 2
        Block b = new Block(bc.getChainSize(), bc.getTime(), "Genesis", 2);
        bc.addBlock(b);
        bc.computeHashesPerSecond();
        System.out.println("Blockchain server running");
        try{
            ServerSocket listenSocket = new ServerSocket(serverPort);
            clientSocket = listenSocket.accept();
            Scanner in = new Scanner(clientSocket.getInputStream());
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            System.out.println("We have a visitor");
            // The server will run permanently
            while (true) {
                // If the user remains connected. Listen to the user.
                if (in.hasNextLine()) {
                    String info = in.nextLine();
                    listenJson = (JSONObject) JSONValue.parse(info);
                    Long l = (Long) listenJson.get("selection");
                    int option = l.intValue();
                    process(option);
                    out.println(returnJson.toJSONString());
                    out.flush();
                }
                // If there is no user, wait for another user.
                else {
                    clientSocket = listenSocket.accept();
                    in = new Scanner(clientSocket.getInputStream());
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if(clientSocket != null) clientSocket.close();
            } catch (IOException e) {
            }
        }


    }

    // According to different option from the client, perform different operations.
    public static void process(int option) {
        Timestamp start;
        returnJson.clear();
        Timestamp end;
        String response;
        int time;
        switch (option) {
            // Check the information of the chain
            case 0:
                returnJson.put("selection", 0);
                returnJson.put("size", bc.getChainSize());
                returnJson.put("chainHash", bc.getChainHash());
                returnJson.put("totalHashes", bc.getTotalExpectedHashes());
                returnJson.put("totalDiff", bc.getTotalDifficulty());
                returnJson.put("recentNonce", bc.getBlock(bc.getChainSize()-1).getNonce());
                returnJson.put("diff", bc.getBlock(bc.getChainSize()-1).getDifficulty());
                returnJson.put("hps", bc.getHashesPerSecond());
                System.out.println("Response : " + returnJson.toJSONString());
                break;
            // Allows the user to add a block to the chain.
            case 1:
                System.out.println("Adding a block");
                start = bc.getTime();
                bc.addBlock(new Block(bc.getChainSize(), bc.getTime(), (String) listenJson.get("data"), ((Long) listenJson.get("difficulty")).intValue()));
                end = bc.getTime();
                time = (int) (end.getTime() - start.getTime());
                response = "Total execution time to add this block was " + time +" milliseconds";
                System.out.println("Setting response to " + response);
                returnJson.put("selection", 1);
                returnJson.put("response", response);
                System.out.println("..." + returnJson.toJSONString());
                break;
            // Check if the chain is valid.
            case 2:
                System.out.println("Verifying entire chain");
                start = bc.getTime();
                String validation = bc.isChainValid();
                end = bc.getTime();
                time = (int) (end.getTime() - start.getTime());
                System.out.print("Chain verification: ");

                if (!validation.equals("True")) {
                    returnJson.put("verification", "False");
                    returnJson.put("errorMessage", validation);
                    System.out.println("False");
                }
                else returnJson.put("verification", "True");
                System.out.println(validation);
                response = "Total execution time required to verify the chain was " + time +" milliseconds";
                System.out.println(response);
                System.out.println("Setting response to " + response);
                returnJson.put("response", response);
                break;
            // View the block (json style)
            case 3:
                System.out.println("View the Blockchain");
                System.out.println("Setting response to " + bc.toString());
                returnJson.put("response", bc.toString());
                break;
            // Corrupt the chain by changing the information in the block.
            case 4:
                System.out.println("Corrupt the Blockchain");
                int index = ((Long) listenJson.get("index")).intValue();
                String corrupt_message = (String) listenJson.get("data");
                bc.getBlock(index).setData(corrupt_message);
                response = "Block " + index + " now holds " + corrupt_message;
                System.out.println(response);
                returnJson.put("response",response);
                break;
            // Fix the block by recomputing the nonce to meet the difficulty requirement.
            case 5:
                System.out.println("Repairing the entire chain");
                start = bc.getTime();
                if(!bc.isChainValid().equals("True")) bc.repairChain();
                end = bc.getTime();
                time = (int) (end.getTime() - start.getTime());
                response = "Total execution time required to repair the chain was " + time +" milliseconds";
                System.out.println("Setting response to " + response);
                returnJson.put("response",response);
                break;

        }

    }
    // An inner class same as task 0.
    public static class BlockChain extends java.lang.Object{
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
}
