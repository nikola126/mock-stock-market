package com.stock.backend.controllers;

import java.util.List;

import com.stock.backend.dtos.AssetDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.models.Asset;
import com.stock.backend.services.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/assets", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssetController {
    @Autowired
    private AssetService assetService;

    @PostMapping(path = "/get")
    public List<AssetDTO> getAllForUser(@RequestBody UserDTO userDTO) {
        return assetService.getAllForUser(userDTO).stream().map(Asset::mapToDTO).toList();
    }

}
