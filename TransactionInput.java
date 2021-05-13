import java.security.PublicKey;

public class TransactionInput {
    public String transactionOutputId;
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOutput){
        this.transactionOutputId = transactionOutput;
    }
}
