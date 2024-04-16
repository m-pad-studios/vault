package com.vault.techvault.routes;


import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.vault.techvault.controller.TechVaultController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class TechVaultRoutes {

    @Autowired
    TechVaultController techVaultController;


    @GetMapping("/")
    public String sample() throws IOException, GeneralSecurityException{
        String files;
        return  files = techVaultController.getFiles();

    }



}
