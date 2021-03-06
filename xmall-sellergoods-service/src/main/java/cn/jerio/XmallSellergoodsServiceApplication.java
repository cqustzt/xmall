package cn.jerio;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.jerio.mapper")
public class XmallSellergoodsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(XmallSellergoodsServiceApplication.class, args);
	}
}
