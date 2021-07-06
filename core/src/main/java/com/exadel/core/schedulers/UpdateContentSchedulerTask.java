package com.exadel.core.schedulers;

import com.exadel.core.services.ShopUpdateService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


@Designate(ocd = UpdateContentSchedulerTask.Config.class)
@Component(service = Runnable.class)
public class UpdateContentSchedulerTask implements Runnable {

    @ObjectClassDefinition(name="A UpdateContent task",
            description = "BeautyShop - Scheduler Configuration")
    public static @interface Config {

        @AttributeDefinition(name = "Cron-job expression")
        String scheduler_expression() default "*/30 * * * * ?";

        @AttributeDefinition(name = "Concurrent task",
                description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default false;

        @AttributeDefinition(name = "Scheduler Name")
        String scheduler_name() default "Exadel Update Content Scheduler";

        @AttributeDefinition(name = "A parameter",
                description = "Can be configured in /system/console/configMgr")
        String myParameter() default "";
    }

    @Reference
    private Scheduler scheduler;

    @Reference
    private ShopUpdateService beautyShopService;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Activate
    private void activate(Config config) {
        LOGGER.info("UpdateContentSchedulerTask scheduler activated");
    }

    @Deactivate
    private void deactivate(Config config) {
        LOGGER.info("UpdateContentSchedulerTask scheduler unactivated");
    }

    @Override
    public void run() {
        try {
            LOGGER.info("=== UpdateContentSchedulerTask is now running");
            Set<String> brands = beautyShopService.updateBrands();
            beautyShopService.updateProducts("maybelline");
            LOGGER.info("=== UpdateContentSchedulerTask is finished");
        } catch (Exception e) {
            LOGGER.error("-- UpdateContentSchedulerTask: " + e.getMessage());
        }
    }

}