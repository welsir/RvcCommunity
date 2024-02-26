package com.tml;

import com.tml.utils.LogsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class RvcGradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RvcGradeApplication.class, args);
        LogsUtil.fozu();
    }

}
