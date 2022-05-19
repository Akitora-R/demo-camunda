package me.aki.demo.camunda.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SomeService {
    public void sayHi() {
        log.info("hi from service");
    }
}
