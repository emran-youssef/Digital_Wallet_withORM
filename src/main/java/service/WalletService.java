package service;

import entities.User;
import entities.Wallet;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import repositories.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet createWallet(EntityManager em, User user) {
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        walletRepository.save(em, wallet);
        return wallet;
    }

    public Wallet findWalletByUserId(EntityManager em, Long userId) {

        Wallet wallet = walletRepository.findByUserId(em, userId);

        if (wallet == null)
            throw new IllegalArgumentException("Wallet not found for user: " + userId);

        return wallet;
    }

    public void deposit(Wallet wallet, BigDecimal amount) {

        validateAmount(amount);

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
    }

    public void withdraw(Wallet wallet, BigDecimal amount) {

        validateAmount(amount);
        if (wallet.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient funds");

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
    }

    public void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}



