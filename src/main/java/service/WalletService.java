package service;

import entities.User;
import entities.Wallet;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import repositories.UserRepository;
import repositories.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet createWallet(User user){
        var wallet = Wallet.builder()
                .balance(BigDecimal.ZERO).user(user)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        walletRepository.saveWallet(wallet);
        return wallet;
    }

    public BigDecimal getBalance(long userId){
        var wallet = getWalletByUserId(userId);
        return wallet.getBalance();
    }

    public void deposit(Long userId, BigDecimal amount){
        validateAmount(amount);

        var wallet = getWalletByUserId(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.update(wallet);
    }

    public void withdraw(Long userId, BigDecimal amount){
        validateAmount(amount);

        var wallet = getWalletByUserId(userId);

        if(!hasSufficientFunds(userId, amount))
            throw new IllegalArgumentException("Insufficient balance");

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.update(wallet);
    }


    // Helpers:
    public Wallet getWalletByUserId(Long id){
        var wallet = walletRepository.findByUserId(id);
        if(wallet == null)
            throw new IllegalArgumentException("Wallet not found: "+ id);

        return wallet;

    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    public boolean hasSufficientFunds(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        return wallet.getBalance().compareTo(amount) >= 0;
    }

}



