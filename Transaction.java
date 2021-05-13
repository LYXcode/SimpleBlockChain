import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import org.graalvm.compiler.lir.framemap.FrameMap;
import org.graalvm.compiler.nodes.java.ArrayLengthNode;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey reciepient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash(){
        sequence++;

        return StringUtils.applySha256(StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(reciepient)
        + Float.toString(value) + Integer.toString(sequence));
    }

    public void generateSignature(PrivateKey privateKey){
        String data =StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(reciepient) + Float.toString(value);
        this.signature = StringUtils.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature(){
        String data =StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(reciepient) + Float.toString(value);
        return StringUtils.verifyECDSASig(sender, data, this.signature);
    }

    public boolean processTransaction(){
        if(verifySignature() == false){
            System.out.println("verify signature failed");
            return  false;
        }

        for(TransactionInput i : inputs){
            i.UTXO = MyChain.UTXOs.get(i.transactionOutputId);
        }

        if(getInputsValue() < MyChain.minimumTranction){
            System.out.println("#Transaction Inputs too small: " + getInputsValue());
			return false;
        }

        float leftOver = getInputsValue() - value;

        transactionId = calculateHash();

        outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        for(TransactionOutput o : outputs) {
			MyChain.UTXOs.put(o.id , o);
		}
		
		//remove transaction inputs from UTXO lists as spent:
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it 
			MyChain.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
    }

    public float getInputsValue(){
        float total = 0;
        for(TransactionInput i : inputs){
            if(i.UTXO == null){
                continue;
            }
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue(){
        float total = 0;
        for(TransactionOutput o:outputs){
            total += o.value;
        }
        return total;
    }
}
