package ws.prager;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ws.prager.camel.consul.ConsulRegistry;


@SpringBootApplication
public class Application {

	private static final Logger logger = Logger.getLogger(Application.class);

	public static void main(String[] args) {

		ConsulRegistry registry = new ConsulRegistry("192.168.99.100", 8500);
		CamelContext camelContext = new DefaultCamelContext(registry);
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("timer://simpleTimer?period=1000").setBody(simple("Hello from timer at ${header.firedTime}"))
							.to("stream:out");
				}
			});
			camelContext.start();
			Thread.sleep(3000);
		} catch (Exception e) {
			logger.error("start failed with " + e.getMessage());
		} finally {
			try {
				camelContext.stop();
			} catch (Exception e) {
				logger.error("stop failed with " + e.getMessage());
			}
		}
	}
}
