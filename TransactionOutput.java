import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey reciepient;
    public float value;
    public String parentTransactionId;

    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId){
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtils.applySha256(StringUtils.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey){
        return (publicKey == reciepient);
    }
}
