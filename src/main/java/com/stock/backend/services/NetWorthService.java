package com.stock.backend.services;

import java.util.List;

import com.stock.backend.models.NetWorth;
import com.stock.backend.models.User;
import com.stock.backend.repositories.NetWorthRepository;
import com.stock.backend.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NetWorthService {
    private final UserRepository userRepository;
    private final NetWorthRepository netWorthRepository;

    public NetWorthService(UserRepository userRepository,
                           NetWorthRepository netWorthRepository) {
        this.userRepository = userRepository;
        this.netWorthRepository = netWorthRepository;
    }

    public List<NetWorth> getNetWorthByUserId(Long userId) {
        return netWorthRepository.findByUserIdOrderByDateAsc(userId);
    }

    public Double getCurrentNetWorthByUserId(Long userId) {
        Double netWorth = 0.0;
        netWorth += userRepository.getById(userId).getCapital();
        netWorth += netWorthRepository.getNetworth(userId);
        return netWorth;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 4, initialDelay = 1000 * 60 * 2)
    public void scheduledNetworthUpdate() {
        List<User> userList = userRepository.findAll();

        for (User user : userList) {
            NetWorth netWorth = new NetWorth();
            netWorth.setUser(user);
            netWorth.setDate(System.currentTimeMillis());
            netWorth.setNetworth(netWorthRepository.getNetworth(user.getId()) + user.getCapital());

            netWorthRepository.save(netWorth);
        }
    }


}
