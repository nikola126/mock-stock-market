package com.stock.backend.controllers;

import java.util.List;

import com.stock.backend.dtos.AssetDTO;
import com.stock.backend.dtos.NetWorthDTO;
import com.stock.backend.dtos.NetWorthRequestDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.models.Asset;
import com.stock.backend.models.HotListEntry;
import com.stock.backend.models.NetWorth;
import com.stock.backend.services.AssetService;
import com.stock.backend.services.NetWorthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/assets", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssetController {
    @Autowired
    private AssetService assetService;
    @Autowired
    private NetWorthService netWorthService;

    @PostMapping(path = "/get")
    public List<AssetDTO> getAllForUser(@RequestBody UserDTO userDTO) {
        return assetService.getAllForUser(userDTO).stream().map(Asset::mapToDTO).toList();
    }

    @PostMapping(path = "/hotlist")
    public Page<HotListEntry> getHotlist(@PageableDefault(size = 20) Pageable pageable) {
        return assetService.getHotlist(pageable);
    }

    @PostMapping(path = "/networth")
    public List<NetWorthDTO> getNetworthGraph(@RequestBody NetWorthRequestDTO netWorthRequestDTO) {
        return netWorthService.getNetWorthByUserId(netWorthRequestDTO.getUserId()).stream().map(NetWorth::mapToDTO).toList();
    }

}
