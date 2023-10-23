package SumoAutAv;

import java.util.ArrayList;

import javax.crypto.SecretKey;

import SumoAutAv.JSONUtil.TransferData;

public class AlphaBank {
    ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();
    SecretKey key;

    public AlphaBank(SecretKey key) {
        this.key = key;
    }

    public void addAccount(BankAccount account) {
        this.accounts.add(account);
    }

    public void transferMoney(BankAccount from, BankAccount to, double amount) { // transferencia bancaria
        if (from.getBalance() >= amount) {
            from.removeBalance(amount);
            to.addBalance(amount);
            ReportGenerator rg = new ReportGenerator();
            long timestampInNanoSeconds = System.nanoTime();
            String transaction = "Timestamp: " + timestampInNanoSeconds + " | Transaction type: "
                    + " | From: " + from.getUser() + " | To: " + to.getUser()
                    + " | Amount: " + amount;
            rg.transactionReport(transaction);
        }
    }

    public void transferMoneyJsonEncrypted(String message) { // transferencia bancaria com json criptografado
        message = EncryptionUtil.decryptedMessage(message, key); // descriptografa a mensagem
        TransferData data = JSONUtil.parseTransferJson(message); // converte a mensagem em um objeto
        BankAccount from = getAccount(data.getFrom()); // pega a conta de quem vai transferir
        BankAccount to = getAccount(data.getTo()); // pega a conta de quem vai receber
        transferMoney(from, to, data.getAmount()); // transfere o dinheiro
    }

    private BankAccount getAccount(String from) { // descobre a conta do cliente do banco
        for (BankAccount account : accounts) {
            String user = account.getUser();
            if (user.compareTo(from) == 0) {
                return account;
            }
        }
        return null;
    }

}
