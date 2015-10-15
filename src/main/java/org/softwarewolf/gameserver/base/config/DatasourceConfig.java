package org.softwarewolf.gameserver.base.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages={"org.softwarewolf.gameserver.base"})
@ImportResource({
    "classpath*:/spring/db.xml"
})
public class DatasourceConfig {

}
