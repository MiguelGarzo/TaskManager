package com.taskmanager.TaskManager.upgrade;

import com.stripe.exception.StripeException;
import com.taskmanager.TaskManager.users.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping
public class UpgradeController {

    @PostMapping("/upgrade")
    public ResponseEntity<UserResponseDTO> upgrade(@RequestParam String priceId, Principal principal) throws StripeException {



    }

}
