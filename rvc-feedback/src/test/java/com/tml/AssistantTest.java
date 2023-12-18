package com.tml;

import io.github.id.IdGeneratorException;
import io.github.id.snowflake.SnowflakeGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AssistantTest {

    @Resource
    SnowflakeGenerator snowflakeGenerator;

    @Test
    public void testSnowflakeGenerator() throws IdGeneratorException {
        System.out.println(snowflakeGenerator.generate());
    }
}
