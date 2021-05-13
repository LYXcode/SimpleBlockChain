import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    public Wallet() {
        generateKeyPair();
    }

    public float getBalance() {
        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : MyChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
            }
        }

        return total;
    }

    public Transaction sendFunds(PublicKey _recipient, float value) {
        if (getBalance() < value) {
            System.out.println("not enough funds to send");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);

        }
        return newTransaction;
    }
    

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGenarator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");
            keyGenarator.initialize(ecGenParameterSpec, random);
            KeyPair keyPair = keyGenarator.generateKeyPair();

            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }
}
