package org.cresplanex.api.state.webgateway.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController("/members")
@RequiredArgsConstructor
public class MemberController {

    // private final CustomerRepository customerRepository;

    // @RequestMapping("/", method = RequestMethod.GET)
    // public Customer getUserDetailsAfterLogin(Authentication authentication) {
    //     Optional<Customer> optionalCustomer = customerRepository.findByEmail(authentication.getName());
    //     return optionalCustomer.orElse(null);
    // }

}
