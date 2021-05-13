import java.util.ArrayList;

import com.google.gson.*;

public class MyChain {

    public static ArrayList<Block> myChain = new ArrayList<Block>();
    public static final int difficulty = 5;

    public static void main(String[] args) {

        myChain.add(new Block("gensis block", "0"));
        System.out.println("Trying to mining block......");
        myChain.get(myChain.size()-1).mineBlock(difficulty);
        myChain.add(new Block("second block", myChain.get(myChain.size() - 1).hash));
        System.out.println("Trying to mining block......");
        myChain.get(myChain.size()-1).mineBlock(difficulty);

        myChain.add(new Block("third block", myChain.get(myChain.size() - 1).hash));
        System.out.println("Trying to mining block......");
        myChain.get(myChain.size()-1).mineBlock(difficulty);
        boolean isValid =  isChainValid();
        System.out.println("The chain validation is: " + Boolean.toString(isValid));

        String blockChain = new GsonBuilder().setPrettyPrinting().create().toJson(myChain);
        System.out.println(blockChain);
    }

    public static boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        Block gensisBlock = myChain.get(0);
        if(!gensisBlock.hash.equals(gensisBlock.calculateHash())){
            System.out.println("gensin block changed!");
            return false;
        }

        for(int i = 1; i < myChain.size(); i++){
            currentBlock =myChain.get(i);
            previousBlock = myChain.get(i-1);
            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("current block changed!");
                return false;
            }
            if(!previousBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("previous block changed!");
                return false;
            }
        }

        return true;
    }
}
