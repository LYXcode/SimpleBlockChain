import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.*;

public class MyChain {

    public static ArrayList<Block> myChain = new ArrayList<Block>();

    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    public static float minimumTranction = 0.0f;
    public static final int difficulty = 5;

    public static Wallet walletA;
    public static Wallet walletB;

    public static Transaction genesisTransaction;

    public static void main(String[] args) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();

        Wallet coinbase = new Wallet();

        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);

        System.out.println("Private and public keys:");
        System.out.println(StringUtils.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtils.getStringFromKey(walletA.publicKey));


genesisTransaction.generateSignature(coinbase.privateKey);
genesisTransaction.transactionId ="0";
genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value,genesisTransaction.transactionId));
UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
System.out.println("Creating and Mining Genesis block... "); 
Block genesis = new Block("0");
genesis.addTransaction(genesisTransaction);
addBlock(genesis);
Block block1 = new Block(genesis.hash);
System.out.println("\nWalletA's balance is: " + walletA.getBalance());
System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");

block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
addBlock(block1);
System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		isChainValid();

}

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        myChain.add(newBlock);
    }

    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        Block gensisBlock = myChain.get(0);
        if (!gensisBlock.hash.equals(gensisBlock.calculateHash())) {
            System.out.println("gensin block changed!");
            return false;
        }

        for (int i = 1; i < myChain.size(); i++) {
            currentBlock = myChain.get(i);
            previousBlock = myChain.get(i - 1);
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("current block changed!");
                return false;
            }
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("previous block changed!");
                return false;
            }
        }

        return true;
    }
}
